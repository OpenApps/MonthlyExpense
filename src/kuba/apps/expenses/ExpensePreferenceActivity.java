package kuba.apps.expenses;

import kuba.apps.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ExpensePreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		int i = 0;

		menu.add(0, MonthlyExpense.MENU_MAIN, i, "Back to Main Menu");

		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = new Intent()
		.setClass(this, kuba.apps.expenses.MonthlyExpense.class);
		this.startActivityForResult(intent, 0);
		return true;
	}
	
	
}