package kuba.apps.expenses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper{

	  private static final int DB_VERSION = 1;
	  private static final String DB_NAME = "test";

	  public static final String TABLE_NAME = "records";	  
	  public static final String FIELD_NAME = "field_name";
	  public static final String FIELD = "field";
	  public static final String VALUE = "val";
	  public static final String DATE = "date";
	  public static final String IS_TRANSFERED = "is_transfered";
	  
	  
	  private static final String CREATE_TABLE = "create table " + TABLE_NAME + " ( id integer primary key autoincrement, "
	      + FIELD_NAME + " CHAR(255), " + FIELD + " CHAR(255), " + VALUE + " CHAR(255)," + DATE + " DATETIME default CURRENT_DATE, " + IS_TRANSFERED + " tinyint(1));";

	  public DbOpenHelper(Context context) {
	    super(context, DB_NAME, null,DB_VERSION);	    
	  }

	  @Override
	  public void onCreate(SQLiteDatabase sqLiteDatabase) {
	    sqLiteDatabase.execSQL(CREATE_TABLE);	    
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		  	    
	  }
	}