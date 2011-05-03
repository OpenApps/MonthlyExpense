package kuba.apps.expenses;

import kuba.apps.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MonthlyExpense extends Activity implements OnTouchListener {

	EditText comment;
	EditText other;
	EditText fuel;
	EditText food;
	EditText phoneBill;
	EditText clothing;
	EditText gift;
	EditText entertaiment;
	EditText neuchtenka;
	EditText car;
	EditText purchase;
	EditText health;
	EditText education;
	EditText workIncome;
	EditText otherIncome;
	EditText creditIn;
	EditText creditOut;
	EditText transferToBank;
	SQLiteDatabase db;

	public static final String LOG_TAG = "MonthlyExpenseApp";

	public static final int MENU_SEND = 0;
	public static final int MENU_RESET = 1;
	public static final int MENU_LAST5 = 2;
	public static final int MENU_MAIN = 3;
	public static final int MENU_PREFERENCES = 4;

	Button expenseButton;

	Button debitButton;

	Button miscButton;
	
	float downXValue;

	/**
	 * Active layout
	 */
	private int activeLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// MonthlyExpense.this.deleteDatabase("test");

		mainMenu();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (activeLayout != R.layout.main) {
				mainMenu();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				mainMenu();
		}
		
		return true;
	}	

	 
	protected static String describeEvent(View view, MotionEvent event) {
		StringBuilder result = new StringBuilder(300);
		result.append("Action: ").append(event.getAction()).append("\n");
		result.append("Location: ").append(event.getX()).append(" x ").append(
				event.getY()).append("\n");
		if (event.getX() < 0 || event.getX() > view.getWidth()
				|| event.getY() < 0 || event.getY() > view.getHeight()) {
			result.append(">>> Touch has left the view <<<\n");
		}
		result.append("Edge flags: ").append(event.getEdgeFlags()).append("\n");
		result.append("Pressure: ").append(event.getPressure()).append(" ");
		result.append("Size: ").append(event.getSize()).append("\n");
		result.append("Down time: ").append(event.getDownTime()).append("ms\n");
		result.append("Event time: ").append(event.getEventTime()).append("ms");
		result.append(" Elapsed: ").append(
				event.getEventTime() - event.getDownTime());
		result.append(" ms\n");
		return result.toString();
	}

	public void switchToWriteExpenseLayout(int layout) {
		setContentView(layout);

		Button send = (Button) findViewById(R.id.sendButton);

		Button sendSecondary = (Button) findViewById(R.id.send2Button);

		OnClickListener clickListener = new Button.OnClickListener() {

			public void onClick(View v) {
				send();
			}
		};

		send.setOnClickListener(clickListener);
		sendSecondary.setOnClickListener(clickListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		int i = 0;

		menu.add(0, MENU_SEND, ++i, "Send");
		menu.add(0, MENU_RESET, ++i, "Clear");
		menu.add(0, MENU_LAST5, ++i, "Recent records");
		menu.add(0, MENU_PREFERENCES, ++i, "Preferences");
		menu.add(0, MENU_MAIN, ++i, "Back to Main Menu");

		return true;
	}

	/**
	 * Main menu
	 */
	public void mainMenu() {
		setContentView(R.layout.main);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.main_menu);
		layout.setOnTouchListener((OnTouchListener) this);

		expenseButton = (Button) findViewById(R.id.expenseButton);

		expenseButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				switchToWriteExpenseLayout(R.layout.expense);
			}
		});

		debitButton = (Button) findViewById(R.id.debitButton);

		debitButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				switchToWriteExpenseLayout(R.layout.income);
			}
		});

		miscButton = (Button) findViewById(R.id.miscButton);

		miscButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				switchToWriteExpenseLayout(R.layout.misc);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_SEND:
			/**
			 * Send
			 */
			try {
				send();
			} catch (Exception e) {
				Log.v(LOG_TAG, e.toString());
			}
			return true;
		case MENU_RESET:
			/**
			 * Clear
			 */
			clear();
			return true;
		case MENU_LAST5:
			/**
			 * Clear
			 */
			last5entries();
			return true;
		case MENU_MAIN:
			mainMenu();
			return true;
		case MENU_PREFERENCES:
			try {
				Intent intent = new Intent().setClass(this,
						ExpensePreferenceActivity.class);
				this.startActivityForResult(intent, 0);
			} catch (Exception e) {
				Log.v(LOG_TAG, e.getMessage());
			}
			return true;

		}
		return true;
	}

	class SendToGDocs extends AsyncTask<Void, Integer, Long> {

		ProgressDialog loader;

		byte[] responseBody;

		protected void onPreExecute() {

			loader = new ProgressDialog(MonthlyExpense.this);
			loader.setMessage("Sending");
			loader.setIndeterminate(true);
			loader.setCancelable(true);
			loader.show();
		}

		protected Long doInBackground(Void... params) {
			/**
			 * Fields
			 */
			comment = (EditText) findViewById(R.id.comment);
			other = (EditText) findViewById(R.id.other);
			fuel = (EditText) findViewById(R.id.fuel);
			food = (EditText) findViewById(R.id.food);
			phoneBill = (EditText) findViewById(R.id.phoneBill);
			clothing = (EditText) findViewById(R.id.clothing);
			gift = (EditText) findViewById(R.id.gift);
			entertaiment = (EditText) findViewById(R.id.entertaiment);
			neuchtenka = (EditText) findViewById(R.id.neuchtenka);
			car = (EditText) findViewById(R.id.car);
			purchase = (EditText) findViewById(R.id.purchase);
			health = (EditText) findViewById(R.id.health);
			education = (EditText) findViewById(R.id.education);
			workIncome = (EditText) findViewById(R.id.work_income);
			otherIncome = (EditText) findViewById(R.id.other_income);
			creditIn = (EditText) findViewById(R.id.credit_in);
			creditOut = (EditText) findViewById(R.id.credit_out);
			transferToBank = (EditText) findViewById(R.id.transfer_to_bank);

			try {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(getString(R.string.url));

				// Make data (post data)
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(
						2);
				if (comment != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.comment, comment.getText()
									.toString()));

				if (other != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.other, other.getText().toString()));

				if (fuel != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.fuel, fuel.getText().toString()));
				if (food != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.food, food.getText().toString()));
				if (phoneBill != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.phoneBill, phoneBill.getText()
									.toString()));
				if (clothing != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.clothing, clothing.getText()
									.toString()));
				if (gift != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.gift, gift.getText().toString()));

				if (entertaiment != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.entertaiment, entertaiment
									.getText().toString()));

				if (neuchtenka != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.neuchtenka, neuchtenka.getText()
									.toString()));

				if (car != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.car, car.getText().toString()));

				if (purchase != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.purchase, purchase.getText()
									.toString()));

				if (health != null)
					nameValuePairs
							.add(new BasicNameValuePair(GDocsPostValues.health,
									health.getText().toString()));

				if (education != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.education, education.getText()
									.toString()));

				if (workIncome != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.workIncome, workIncome.getText()
									.toString()));

				if (otherIncome != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.otherIncome, otherIncome.getText()
									.toString()));
				if (creditIn != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.creditIn, creditIn.getText()
									.toString()));
				if (creditOut != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.creditOut, creditOut.getText()
									.toString()));

				if (transferToBank != null)
					nameValuePairs.add(new BasicNameValuePair(
							GDocsPostValues.transferToBank, transferToBank
									.getText().toString()));

				nameValuePairs.add(new BasicNameValuePair("submit", "Submit"));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				response.getEntity().writeTo(outstream);
				responseBody = outstream.toByteArray();

			} catch (ClientProtocolException e) {
				Log.v(LOG_TAG, "Failed (protocol)");
			} catch (IOException e) {
				/**
				 * Saving to database
				 */
				try {
					SQLiteDatabase db = dbConnection();
					ContentValues cv = new ContentValues();
					// comment
					if (comment.getText().toString().length() > 0) {
						/*
						 * cv.clear(); cv.put(DbOpenHelper.FIELD_NAME,
						 * "comment"); cv.put(DbOpenHelper.FIELD,
						 * GDocsPostValues.comment); cv .put(DbOpenHelper.VALUE,
						 * comment.getText() .toString());
						 * cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						 * db.insert(DbOpenHelper.TABLE_NAME, null, cv);
						 */
					}

					// other
					if (other.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "other");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.other);
						cv.put(DbOpenHelper.VALUE, other.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// fuel
					if (fuel.getText().toString() != "") {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "fuel");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.fuel);
						cv.put(DbOpenHelper.VALUE, fuel.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// food
					if (food.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "food");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.food);
						cv.put(DbOpenHelper.VALUE, food.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// phoneBill
					if (phoneBill.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "phone bill");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.phoneBill);
						cv.put(DbOpenHelper.VALUE, phoneBill.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// clothing
					if (clothing.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "clothing");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.clothing);
						cv.put(DbOpenHelper.VALUE, clothing.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// gift
					if (gift.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "gift");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.gift);
						cv.put(DbOpenHelper.VALUE, gift.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// Entertainment
					if (entertaiment.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "entertainment");
						cv
								.put(DbOpenHelper.FIELD,
										GDocsPostValues.entertaiment);
						cv.put(DbOpenHelper.VALUE, entertaiment.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// neuchtenka
					if (neuchtenka.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "neuchtenka");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.neuchtenka);
						cv.put(DbOpenHelper.VALUE, neuchtenka.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// car
					if (car.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "car");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.car);
						cv.put(DbOpenHelper.VALUE, car.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// purchase
					if (purchase.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "purchase");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.purchase);
						cv.put(DbOpenHelper.VALUE, purchase.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// health
					if (health.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "health");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.health);
						cv.put(DbOpenHelper.VALUE, health.getText().toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// education
					if (education.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "education");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.education);
						cv.put(DbOpenHelper.VALUE, education.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// work in come
					if (workIncome.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "work income");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.workIncome);
						cv.put(DbOpenHelper.VALUE, workIncome.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// other in come
					if (otherIncome.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "work income");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.otherIncome);
						cv.put(DbOpenHelper.VALUE, otherIncome.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// credit in
					if (creditIn.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "credit in");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.creditIn);
						cv.put(DbOpenHelper.VALUE, creditIn.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// credit out
					if (creditOut.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "credit out");
						cv.put(DbOpenHelper.FIELD, GDocsPostValues.creditOut);
						cv.put(DbOpenHelper.VALUE, creditOut.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					// transfer to bank
					if (transferToBank.getText().toString().length() > 0) {
						cv.clear();
						cv.put(DbOpenHelper.FIELD_NAME, "transfer to bank");
						cv.put(DbOpenHelper.FIELD,
								GDocsPostValues.transferToBank);
						cv.put(DbOpenHelper.VALUE, transferToBank.getText()
								.toString());
						cv.put(DbOpenHelper.IS_TRANSFERED, 0);
						db.insert(DbOpenHelper.TABLE_NAME, null, cv);
					}

					db.close();

				} catch (Exception e1) {

					Log.v(LOG_TAG, e1.toString());
				}
			} catch (Exception e) {
				Log.v(LOG_TAG, e.toString());
			}
			return null;
		}

		protected void onPostExecute(Long unused) {
			loader.dismiss();

			/**
			 * Alert dialog
			 */
			AlertDialog alertDialog = new AlertDialog.Builder(
					MonthlyExpense.this).create();
			alertDialog.setTitle("Response");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					clear();
				}
			});
			alertDialog.setIcon(R.drawable.icon);

			if (responseBody != null) {
				if (new String(responseBody).contains("has been recorded")) {
					/**
					 * Synchronize docs
					 */
					new SynchroniseWithGDocs().execute();

					alertDialog.setMessage("Your record has been recorded");
				} else {
					alertDialog.setMessage("Error");
				}
			} else {
				alertDialog
						.setMessage("Your record save to internal database. It will be synchronised later.");
			}

			alertDialog.show();
		}
	}

	class SynchroniseWithGDocs extends AsyncTask<Void, Integer, Long> {

		protected void onPreExecute() {
		}

		protected Long doInBackground(Void... params) {

			SQLiteDatabase db = dbConnection();

			Cursor c = db.query(DbOpenHelper.TABLE_NAME, new String[] {
					DbOpenHelper.FIELD_NAME, DbOpenHelper.FIELD,
					"SUM(" + DbOpenHelper.VALUE + ") as val",
					DbOpenHelper.IS_TRANSFERED, DbOpenHelper.DATE },
					DbOpenHelper.IS_TRANSFERED + " = 0", null,
					DbOpenHelper.FIELD, null, null);
			int numRows = c.getCount();
			c.moveToFirst();

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(getString(R.string.url));
			// Make data (post data)
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(
					2);

			if (numRows == 0) {
				Log.v(LOG_TAG, "there is no synchronized data");
				return null;
			}

			for (int i = 0; i < numRows; ++i) {

				if (c.getString(2).equals(new String("0")) == false
						&& c.getString(2).length() > 0) {
					Log.v(LOG_TAG, c.getString(2));
					nameValuePairs.add(new BasicNameValuePair(c.getString(1), c
							.getString(2)));
				}

				c.moveToNext();

			}

			nameValuePairs.add(new BasicNameValuePair("submit", "Submit"));

			try {

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				response.getEntity().writeTo(outstream);
				byte[] responseBody = outstream.toByteArray();

				if (responseBody != null) {
					if (new String(responseBody).contains("has been recorded")) {
						/**
						 * successful
						 */
						db
								.execSQL("UPDATE " + DbOpenHelper.TABLE_NAME
										+ " SET " + DbOpenHelper.IS_TRANSFERED
										+ " = 1");

						Log.v(LOG_TAG, "synchronized successfully");
					} else {
						Log
								.v(LOG_TAG,
										"synchronized failed: not found success message");
					}
				} else {
					Log.v(LOG_TAG, "synchronized failed: no response");
				}

			} catch (Exception e) {
				Log.v(LOG_TAG, e.toString());
			}

			db.close();
			return null;
		}

		protected void onPostExecute(Long unused) {

		}
	}

	public void clear() {

		/**
		 * Fields
		 */
		comment = (EditText) findViewById(R.id.comment);
		other = (EditText) findViewById(R.id.other);
		fuel = (EditText) findViewById(R.id.fuel);
		food = (EditText) findViewById(R.id.food);
		phoneBill = (EditText) findViewById(R.id.phoneBill);
		clothing = (EditText) findViewById(R.id.clothing);
		gift = (EditText) findViewById(R.id.gift);
		entertaiment = (EditText) findViewById(R.id.entertaiment);
		neuchtenka = (EditText) findViewById(R.id.neuchtenka);
		car = (EditText) findViewById(R.id.car);
		purchase = (EditText) findViewById(R.id.purchase);
		health = (EditText) findViewById(R.id.health);
		education = (EditText) findViewById(R.id.education);
		workIncome = (EditText) findViewById(R.id.work_income);
		otherIncome = (EditText) findViewById(R.id.other_income);
		creditIn = (EditText) findViewById(R.id.credit_in);
		creditOut = (EditText) findViewById(R.id.credit_out);
		transferToBank = (EditText) findViewById(R.id.transfer_to_bank);

		if (comment != null)
			comment.getText().clear();
		if (other != null)
			other.getText().clear();
		if (fuel != null)
			fuel.getText().clear();
		if (food != null)
			food.getText().clear();
		if (phoneBill != null)
			phoneBill.getText().clear();
		if (clothing != null)
			clothing.getText().clear();
		if (gift != null)
			gift.getText().clear();
		if (entertaiment != null)
			entertaiment.getText().clear();
		if (neuchtenka != null)
			neuchtenka.getText().clear();
		if (car != null)
			car.getText().clear();
		if (purchase != null)
			purchase.getText().clear();
		if (health != null)
			health.getText().clear();
		if (education != null)
			education.getText().clear();
		if (workIncome != null)
			workIncome.getText().clear();
		if (otherIncome != null)
			otherIncome.getText().clear();
		if (creditIn != null)
			creditIn.getText().clear();
		if (creditOut != null)
			creditOut.getText().clear();
		if (transferToBank != null)
			transferToBank.getText().clear();
	}

	/**
	 * Gets db conneciton
	 * 
	 * @return
	 */
	public SQLiteDatabase dbConnection() {
		DbOpenHelper dbOpenHelper = new DbOpenHelper(MonthlyExpense.this);

		db = dbOpenHelper.getWritableDatabase();

		return db;
	}

	/**
	 * Last 5 entries
	 */
	public void last5entries() {
		setContentView(R.layout.last5);
		try {

			ListView lv1 = (ListView) findViewById(R.id.ListView01);

			ArrayList<String> rowList = new ArrayList<String>();

			SQLiteDatabase db = dbConnection();

			Cursor c = db.query(DbOpenHelper.TABLE_NAME, new String[] {
					DbOpenHelper.FIELD_NAME, DbOpenHelper.FIELD,
					DbOpenHelper.VALUE, DbOpenHelper.DATE }, null, null, null,
					null, DbOpenHelper.DATE + " DESC");

			c.moveToFirst();

			if (c.getCount() > 0) {
				for (int i = 0; i < c.getCount(); i++) {
					rowList.add(c.getString(3) + ": " + c.getString(0) + " - "
							+ c.getString(2));
					c.moveToNext();
					if (i == Integer.parseInt(getNumEntries()) - 1)
						break;
				}
			} else {
				rowList.add("No records");
			}

			String lv_arr[] = (String[]) rowList.toArray(new String[rowList
					.size()]);

			lv1.setAdapter((ListAdapter) new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, lv_arr));
		} catch (Exception e) {
			Log.v(LOG_TAG, e.getMessage());
		}

	}

	@Override
	public void setContentView(int layoutResID) {
		activeLayout = layoutResID;
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
	}

	/**
	 * Sends
	 */
	public void send() {
		new SendToGDocs().execute();
	}

	public void onActivityResult(int reqCode, int resCode, Intent data) {
		super.onActivityResult(reqCode, resCode, data);
		setOptionText();
	}

	private void setOptionText() {

	}

	/**
	 * Gets num of entries
	 */
	protected String getNumEntries() {
		SharedPreferences prefs = getSharedPreferences("kuba.apps_preferences",
				0);
		String option = prefs.getString(this.getResources().getString(
				R.string.num_entries), this.getResources().getString(
				R.string.num_entries_default));
		String[] optionText = this.getResources().getStringArray(
				R.array.num_entries);
		return optionText[Integer.parseInt(option)];
	}
}
