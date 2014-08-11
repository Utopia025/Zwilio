package com.scarr025.zwilio;

import java.util.Calendar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

//** Month is 0-11

public class AdventureSetUp extends FragmentActivity implements
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_adventure_set_up);	
		
		// Find scheduler button, set clickListener, and save adventure + send stats to new activity
		Button btnScheduleAdventure 	= (Button) findViewById(R.id.btn_schedule_adventure);
		btnScheduleAdventure.setOnClickListener(new View.OnClickListener() {
			// Perform your action here
			public void onClick(View v) {
				// Get adventure schedule data
				EditText btnScheduleAdventure = (EditText) findViewById(R.id.adventure_title);
				String adventureTitle 	= btnScheduleAdventure.getText().toString();
				String startDate	= (startYear + "-" + startMonth + "-" + startDay);
				String startTime 	= (startHour + ":" + startMinute);
				String endDate 		= (endYear + "-" + endMonth + "-" + endDay);
				String endTime	 	= (endHour + ":" + endMinute);
				
				// Get created at time/date
				Calendar curCal		= Calendar.getInstance();
				Integer mon	= curCal.get(Calendar.MONTH); Integer day = curCal.get(Calendar.DAY_OF_MONTH); Integer yr = curCal.get(Calendar.YEAR);
				String createDate	= yr.toString() + "-" + mon.toString() + "-" + day.toString();
				Integer hr	= curCal.get(Calendar.HOUR_OF_DAY); Integer min = curCal.get(Calendar.MINUTE); 
				String createTime	= hr.toString() + ":" + min.toString();
				
				// Print out adventure data
				Log.d(TAG, "Clicked Schedule Adventure, for Adventure: " + adventureTitle);
				Log.d(TAG, "Start Date: " + startMonth + "-" + startDay + "-" + startYear);
				Log.d(TAG, "Start Time: " + startHour + ":" + startMinute);
				Log.d(TAG, "End Date: " + endMonth + "-" + endDay + "-" + endYear);
				Log.d(TAG, "End Time: " + endHour + ":" + endMinute);
				Log.d(TAG, "Create Date: " + createDate + " " + "	Create Time: " + createTime);

				
				// Write data to database table: adventures
				// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
					// then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
				DBAdventureHelper enclosingInstance = new DBAdventureHelper();
				DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(getBaseContext());
				// Get the data repository in write mode
				SQLiteDatabase db = mDbHelper.getWritableDatabase();
				// Create new contentValue and write columns to the object
				ContentValues advData = new ContentValues();
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE, adventureTitle);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE, startDate);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME, startTime);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_DATE, endDate);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_TIME, endTime);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_DATE, createDate);
				advData.put(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_TIME, createTime);				
				
			
				
				
				// Set an alarmed intent to begin a service (to record adventure activity)				
					// sets an alarm to run at startTime (putExtra newRowId), sets onReceive to run adventure service
				Calendar startCal = Calendar.getInstance();
				startCal.set(startYear, startMonth, startDay, startHour, startMinute);
				
				/*A PendingIntent is a token that you give to a foreign application (e.g. NotificationManager, AlarmManager, 
				 * Home Screen AppWidgetManager, or other 3rd party applications), which allows the foreign application to 
				 * use your application's permissions to execute a predefined piece of code.
				 * If you give the foreign application an Intent, and that application sends/broadcasts the Intent you gave, 
				 * they will execute the Intent with their own permissions. But if you instead give the foreign application 
				 * a PendingIntent you created using your own permission, that application will execute the contained Intent 
				 * using your application's permission.
				*/
				
				if (servicesConnected()) {
					// Insert the new row, returning the primary key value of the new row
					// first argument for insert() is the table name, second argument provides the name of a column in 
					//  which the framework can insert NULL in the event that the ContentValues is empty (if you instead 
					//  set this to "null", then the framework will not insert a row when there are no values).
					long newRowId;
					newRowId = db.insert(
					         AdventureContract.AdventuresTable.ADVENTURES_TABLE_NAME,
					         null,
					         advData);
					// Setting an intent with the specified filter will fire up the broadcast receiver registered in the
					//  Android Manifest under the same intent filter; set alarm to start adventure service on scheduled start time
					PendingIntent pi 		= PendingIntent.getService(getBaseContext(), 0, 
												new Intent(getBaseContext(), AdventureService.class).putExtra(EXTRA_DB_ID, newRowId), 0);
					
					AlarmManager am 		= (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, startCal.getTimeInMillis(), pi);
					Log.d(TAG, "GPlay Services connected and adventure scheduled, set alarm w/ pendingIntent extra LONG = " + newRowId);
					// Open new page after alarm set
					newIntent = new Intent(getBaseContext(), NEXT_ACTIVITY);
					startActivity(newIntent);
				}
				
				/*
				// Sample test: puting values into an intent and sending them to a new activity (TestActivity.java)
				newIntent = new Intent(getBaseContext(), TestActivity.class);
				String[] intentArray = {adventureTitle, startMonth.toString(), startDay.toString(), startYear.toString(),
						startHour.toString(), startMinute.toString(),
						endMonth.toString(), endDay.toString(), endYear.toString(), endHour.toString(), endMinute.toString()};
				newIntent.putExtra(EXTRA_ARRAY, intentArray);
				startActivity(newIntent);
				*/
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adventure_set_up, menu);
		return true;
	}
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	//-------------------------------------Date/Time Picker Methods-------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Date Pickers
	// Start Date Picker for start date vals (separate from End Date Fragment)
	public static class startDatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			startDay 	= day;
			startMonth 	= month;
			startYear	= year;
			Log.d(TAG, "Start Date Picked--Year:" + startYear + " Month: " + startMonth + " Day: " + startDay);
		}
	}
	
	// End Date Picker for end date vals (separate from Start Date Fragment)
	public static class endDatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			endDay 		= day;
			endMonth 	= month;
			endYear		= year;
			Log.d(TAG, "End Date Picked--Year:" + endYear + " Month: " + endMonth + " Day: " + endDay);
		}
	}
	
	// Date Picker sorter, directs the clicked date fragment to the correct class (to save the vals)
	public void showDatePickerDialog(View v) {
		switch (v.getId()) {
		case (R.id.picker_start_date):
	    	DialogFragment startDateFrag = new startDatePickerFragment();
	    	startDateFrag.show(getSupportFragmentManager(), "datePicker");
			break;
		case (R.id.picker_end_date):
		    DialogFragment endDateFrag = new endDatePickerFragment();
		    endDateFrag.show(getSupportFragmentManager(), "datePicker");
			break;			
		}
	}

	// Time Picker
	
	// Start Time Picker for start time vals (separate from End Time Fragment)
	public static class startTimePickerFragment extends DialogFragment
    	implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			startHour 		= hourOfDay;
			startMinute 	= minute;
			Log.d(TAG, "Start Time Picked--Time:" + startHour + ":" + startMinute);

		}
	}

	// End Time Picker for end time vals (separate from Start Time Fragment)
	public static class endTimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			endHour 		= hourOfDay;
			endMinute 		= minute;
			Log.d(TAG, "End Time Picked--Time:" + endHour + ":" + endMinute);

		}
	}
	
	// Time Picker sorter, directs the clicked time fragment to the correct class (to save the vals)
	public void showTimePickerDialog(View v) {
		switch (v.getId()) {
		case (R.id.picker_start_time):
	    	DialogFragment startTimeFragment = new startTimePickerFragment();
	    	startTimeFragment.show(getSupportFragmentManager(), "timePicker");	
	    	break;
		case (R.id.picker_end_time):
		    DialogFragment endTimeFragment = new endTimePickerFragment();
		    endTimeFragment.show(getSupportFragmentManager(), "timePicker");	
		    break;
		}
	}
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	//-----------------------------------Google Play Service Methods------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "Google Play services is connected (in onConnected).");
	}
	
	
	@Override
	public void onDisconnected() {
		// Display connection status.
		Log.d(TAG, "Google Play services is disconnected. Please re-connect (in onDisconnect).");
	}
	
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Google Play services can resolve some errors it detects. If the error has a resolution, 
		// try sending an Intent to start a Google Play services activity that can resolve error.
		Log.d(TAG, "Connection has failed, in OnConnFail, trying to resolve.");
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				// Thrown if Google Play services canceled the original PendingIntent
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			// If no resolution is available, display a dialog to the user with the error.
			// showErrorDialog(connectionResult.getErrorCode());
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), 
					this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(), "Location Updates"); 
		}		
	}


	public boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	
		if (ConnectionResult.SUCCESS == resultCode) {
			// If Google Play services is available
			Log.d(TAG, "Google Play services is available.");
			return true;																	// Continue
		} else {
			// Google Play services was not available for some reason
			Log.d(TAG, "You do not have access to Google Play Services..." +
					"\nError Code: " + GooglePlayServicesUtil.getErrorString(resultCode));
	
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				// Get the error dialog from Google Play services
				Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
						resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				Log.d(TAG, "Created error dialog.");
				// If Google Play services can provide an error dialog
				if (errorDialog != null) {
					// Create a new DialogFragment for the error dialog
					ErrorDialogFragment errorFragment = new ErrorDialogFragment();
					// Set the dialog in the DialogFragment
					errorFragment.setDialog(errorDialog);
					// Show the error dialog in the DialogFragment
					errorFragment.show(getSupportFragmentManager(), "Location Updates");
				}                            	
			} else {
				Toast.makeText(this, "This device is not supported.", 
						Toast.LENGTH_LONG).show();
			}
			return false;
		}
	}
	
	
	////////////////////////////////////Dialogs //////////////////////////////////// 	
		
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
	
		// Global field to contain the error dialog
		private Dialog mDialog;
		
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	
	// Handle results returned to the FragmentActivity by Google Play services 
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// Decide what to do based on the original request code
			switch (requestCode) {
			case CONNECTION_FAILURE_RESOLUTION_REQUEST :
				// If the result code is Activity.RESULT_OK, try to connect again
				switch (resultCode) {
				case Activity.RESULT_OK :
					Log.d(TAG, "Positive result returned, try connecting to GPS again.");	
					break;
				}
		
			}
			Log.d(TAG, "onActivityResult - requestCode=" + requestCode + " resultCode=" + resultCode);
		}

	// Global Variables
	public static final String EXTRA_ARRAY 		= "com.scarr025.zwilio.ARRAY";
	public final static String EXTRA_DB_ID 		= "com.scarr025.zwilio.LONG";
	private static final Class NEXT_ACTIVITY 	= BasePage.class;
	public final static String TAG				= "$$AdvSetUp$$";
	private static Intent newIntent;
	private static Integer startMonth;
	private static Integer startDay;
	private static Integer startYear;
	private static Integer startHour;
	private static Integer startMinute;
	private static Integer endMonth;
	private static Integer endDay;
	private static Integer endYear;
	private static Integer endHour;
	private static Integer endMinute;
	
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1999;

}
