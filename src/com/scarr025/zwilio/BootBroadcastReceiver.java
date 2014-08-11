package com.scarr025.zwilio;

import java.sql.Date;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class BootBroadcastReceiver extends BroadcastReceiver {
	
	//**** Once testing on phone un-comment 26, 34, 35 to run boot tests
	
    // Run below code every time a reboot occurs, and re-set all scheduled adventure alarms
	// Open a DB instance, pull each adventure scheduled, if start time is after 
	//	current date/time, create alarm;
	public void onReceive(Context context, Intent intent)
    {
		mContext = context;		
		//SQLiteDatabase db = getDB();
		// Projection defines which DB cols you will be pulled from the query
		String[] projection = {
				AdventureContract.AdventuresTable._ID,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME
		};
		// Cursor queries the scheduled adventures table, and returns projections that are scheduled >= today's date
		//Cursor c = getCursor(db, projection);
		//checkScheduledAdventures(c);
    }

	// Takes a cursor, iterates through the query results, generates a timeInMillis	Cal object, and sets alarm on that
	//	date (adding advID)
	private void checkScheduledAdventures(Cursor c) {
		c.moveToFirst();
		Calendar todaysCal 				= Calendar.getInstance();
		while(true) {
			Long advID 					= c.getLong(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable._ID));
			String advTime 				= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME));
			String advDate 				= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE));
			String[] hrOfDayMin 		= advTime.split("[^\\d+]");
			String[] yrMoDay 			= advTime.split("[^\\d+]");
			Calendar entryCal 			= Calendar.getInstance();			
			entryCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrOfDayMin[0]));
			entryCal.set(Calendar.MINUTE, Integer.parseInt(hrOfDayMin[1]));
			entryCal.set(Calendar.YEAR, Integer.parseInt(yrMoDay[0]));
			entryCal.set(Calendar.MONTH, Integer.parseInt(yrMoDay[1]));
			entryCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(yrMoDay[2]));
			
			if (entryCal.getTimeInMillis() > todaysCal.getTimeInMillis()) {
				// Date is in the future need to set alarm
				setAlarm(entryCal, advID, AdventureService.class);
				
				Toast.makeText(mContext, "Adventure rescheduled for ", Toast.LENGTH_LONG).show(); 
			} else if (entryCal.getTimeInMillis() <= todaysCal.getTimeInMillis()) {
				// Date in the past, do nothing
			}
			
			if(c.moveToNext() == false) {
				break;
			}
		}
	}

	
	// Sets an alarm for the given Cal object, adds an long extra, and sets the intent as the supplied class
	private void setAlarm(Calendar entryCal, Long advID, Class mClass) {
		/*A PendingIntent is a token that you give to a foreign application (e.g. NotificationManager, AlarmManager, 
		 * Home Screen AppWidgetManager, or other 3rd party applications), which allows the foreign application to 
		 * use your application's permissions to execute a predefined piece of code.
		 * If you give the foreign application an Intent, and that application sends/broadcasts the Intent you gave, 
		 * they will execute the Intent with their own permissions. But if you instead give the foreign application 
		 * a PendingIntent you created using your own permission, that application will execute the contained Intent 
		 * using your application's permission.
		*/
		PendingIntent pi 		= PendingIntent.getService(mContext, 0, 
				new Intent(mContext, mClass).putExtra(EXTRA_DB_ID, advID), 0);
		AlarmManager am 		= (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, entryCal.getTimeInMillis(), pi);
	}
	
	
	// takes a projection and db and returns a query 
	private Cursor getCursor(SQLiteDatabase db, String[] projection) {
		String sortOrder = AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE + " DESC";
		Calendar cal = Calendar.getInstance();
		Integer yr = cal.get(Calendar.YEAR); Integer mon = cal.get(Calendar.MONTH); Integer day = cal.get(Calendar.DAY_OF_MONTH);
		String todayDate = yr.toString() + "-" + mon.toString() + "-" + day.toString();
		Cursor c = db.query(
				AdventureContract.AdventuresTable.ADVENTURES_TABLE_NAME,  									// The table to query
			    projection,                               													// The columns to return
			    AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE + " >= ",				// The columns for the WHERE clause
			    new String[] {todayDate},                            										// The values for the WHERE clause
			    null,                                     													// don't group the rows
			    null,                                     													// don't filter by row groups
			    sortOrder                                 													// The sort order
			    );
		return c;
	}
	
	
	// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
	//   then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
	private SQLiteDatabase getDB() {
		DBAdventureHelper enclosingInstance = new DBAdventureHelper();
		DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(mContext);
		// Get the data repository in read mode
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		return db;
	}
	
	
	
	
	
	public final static String EXTRA_DB_ID = "com.scarr025.zwilio.LONG";
	private Context mContext;

}
