package com.scarr025.zwilio;

import java.util.Calendar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class AdventureService extends Service implements
				GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener,
				LocationListener,
				ConnectionCallbacks, 
				OnConnectionFailedListener {
	
	// Called when the Service object is instantiated (ie: when the service is created). Use this method for 
	//  things you only need to do once (ie: initialize some variables). Will only ever be called once per 
	//  instantiated object. Only need to implement if need to initialize something only once.
	@Override
	public void onCreate() {
	    Log.d(TAG, "In onCreate of Adventure Service" );
	    
	}
	
	// Called every time a client starts the service using startService(Intent intent). It can get called multiple 
	//  times. Do the things in this method that are needed each time a client requests something from the service. 
	//  If not implemented, won't be able to get Intent info that is passed
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    long dbID = intent.getLongExtra(EXTRA_DB_ID, 0);
	    Log.d(TAG, "In onStartCommand of Adventure Service; Intent Extra = " + dbID);
	    initAdventureServices(dbID); 
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	private void initAdventureServices(long dbID) {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext()) == ConnectionResult.SUCCESS
				&& dbID != 0) {
		    createLocationClients();
		    initializeAdventureData(dbID);
		    setEndingAlarm();
		    initLocationServices();
		    registerReceiver(br, getIntentFilter());
		    initMessagingObservers();
		} else {
		    Toast.makeText(getBaseContext(), GPS_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
			shutDownService();
		}
		
	}

	// Initialize the Locator, check if it is connected to Google Play, if so, connect
	private void initLocationServices() {
		activityRecognitionActive = false;
		connectLocTrackers();
	}

	
	// Connect the location tracking and activity recognition client
	private void connectLocTrackers() {
        Log.d(TAG2, "Inits, connecting location clients.");
        // Connect the client.
        mLocClient.connect(); 
//**Activity Check**//
        // If a request is not already underway
		if (!activityRecognitionActive) {
			activityRecognitionActive = true;		// Indicate that a request is in progress
			mActivityRecognitionClient.connect(); 	// Request a connection to Location Services
	        Log.d(TAG2, "Activity Recognition Client connection requested");
		} else {
            // A request is already underway. You can handle this situation by disconnecting the client,
            // re-setting the flag, and then re-trying the request.
	        Log.d(TAG2, "Activity Recognition request in progress, disconnecting then re-requesting");   
        }
	}

	// Create the location tracking and activity recognition client
	private void createLocationClients() {
		mLocClient = new LocationClient(this, this, this);
		/*  Create the LocationRequest object; receive updates at a specified interval, and receive at a 
		 *  faster when available (via other app's calls, still low power impact). Set interval responsible
		 *  for power consumption grade, pulls data at faster interval when present via other apps */
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the typical update interval
        mLocationRequest.setInterval(UPDATE_INTERVAL_MILLISECONDS);
        // Set the fastest update interval (e.g. if nav is open, will pull location at this interval)
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

//**Activity Check**//
        activityRecognitionActive = false;
        // Instantiate a new activity recognition client. Since the parent Activity implements the 
        // connection listener and connection failure listener, the constructor uses "this"
        // to specify the values of those parameters.
        mActivityRecognitionClient = new ActivityRecognitionClient(getBaseContext(), this, this);

        // Create the PendingIntent that Location Services uses to send activity recognition 
        // updates back to this app. Return a PendingIntent that starts the IntentService.
        mActivityRecognitionPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, 
        		new Intent(FILTER_ACTIVITY_TRACKER),
                PendingIntent.FLAG_UPDATE_CURRENT);		
	}
		
	private void initMessagingObservers() {
//		startOutgoingSMS();
		
	}

	private void startOutgoingSMS() {
		/*Handler mhandler = new Handler();
		Message msgObj = mhandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("type", sentSMS);
        msgObj.setData(b);*/        
        
		//this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new ObserverSMSOut(new Handler()));
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		contentResolver.registerContentObserver(Uri.parse("content://sms"), true, new MessageObserver(new Handler(), getBaseContext()));
	}

	// Sets the alarm for the service's end; instantiates the Cal object's end date/time; Uncomment below code~~~
	private void setEndingAlarm() {
		Calendar servEnd = Calendar.getInstance();
		String[] hrOfDayMin 		= advEndTime.split("[^\\d+]");
		String[] yrMoDay 			= advEndDate.split("[^\\d+]");
		servEnd.set(Integer.parseInt(yrMoDay[0]), Integer.parseInt(yrMoDay[1]), Integer.parseInt(yrMoDay[2]), 
				Integer.parseInt(hrOfDayMin[0]), Integer.parseInt(hrOfDayMin[1]), 0);		
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, 
				new Intent("com.scarr025.zwilio.quitService"), 0);
		AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, servEnd.getTimeInMillis(), pi);	
	    Log.d(TAG, "In setEndAlarm of Adventure Service; end time = " + servEnd.toString());
	}
	
	private void shutDownService() {
		Log.d(TAG2, "In service shutdown.");
		unregisterReceiver(br);
		stopSelf();
		if (mLocClient.isConnected()) {
			// Kill the location client; Remove location updates for a listener. Current Activity 
			// is the listener, so the argument is "this".
			mLocClient.removeLocationUpdates(this);
			// After disconnect() is called, the client is considered "dead". 
			mLocClient.disconnect();
			Log.d(TAG2, "Location listener killed.");
		}
		if (mActivityRecognitionClient.isConnected()) {
			mActivityRecognitionClient.removeActivityUpdates(mActivityRecognitionPendingIntent);
			Log.d(TAG2, "Activity listener killed.");
			activityRecognitionActive = false;
		}
	}
	
	
	// Actions the services BR will filter for; add the actions here, and the action to take in the BR's onReceive method
	private IntentFilter getIntentFilter() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		iFilter.addAction(FILTER_ACTION_QUIT_SERVICE);
		iFilter.addAction(FILTER_ACTION_LOCATION_TRACKER);
		iFilter.addAction(FILTER_ACTION_RECEIVE_SMS);
		iFilter.addAction(FILTER_ACTIVITY_TRACKER);
		return iFilter;
	}
	
	private final BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(FILTER_ACTION_QUIT_SERVICE)){
				//action for service ending, unregister broadcast receiver and stop service
			    Log.d(TAG, "In onRece of Quit Service--BR; BR has fired for service stoppage");
				shutDownService();
			    Log.d(TAG, "BR unregistered, LocAlarm canceled, service stopped");
			} else if(action.equals(FILTER_ACTION_RECEIVE_SMS)){
	            //action for receiving SMS
			    Log.d(TAG, "In onRece of SMS Rece--BR; SMS has been received and is being logged to Messages Table");
			    Toast.makeText(getBaseContext(), "$$AdvServ$$ - In BR of SMS Rece", Toast.LENGTH_LONG).show();
			    long returnedMessageID = logMessageData(intent);
			    Log.d(TAG, "SMS data inserted, entry ID = " + returnedMessageID);
			} else if (action.equals(FILTER_ACTIVITY_TRACKER)) {
				if (ActivityRecognitionResult.hasResult(intent)) {
		            saveActivityData(intent);	        
		        } else {
		            // This implementation ignores intents that don't contain an activity update. If you
		            // wish, you can report them as errors.
			        Log.d(TAG2, "ActivityRecognitionResult has no result.");
		        }
			}
		}	
	};
	
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	//----------------------------------------SMS Methods-----------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////////

	// Grab message data and insert it into DB; ***Need to flesh out sent SMS logic
	protected long logMessageData(Intent intent) {
		String[] messageData = getMessageData(intent);
		if (!(messageData == null)) {
			return insertMessageData(messageData);
		} else {
			return 0;
		}
	}

	protected long insertMessageData(String[] messageData) {
		// Get location data for insert
		Integer isIncomming = (messageData[3].equals("true"))?1:0;
		Integer locID = Integer.parseInt(messageData[4]);
		// Write data to database table: messages
		// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
		// then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
		DBAdventureHelper enclosingInstance = new DBAdventureHelper();
		DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(getBaseContext());
		// Get the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// Create new contentValue and write columns to the object
		ContentValues advData = new ContentValues();
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_ADVENTURE_ID, advID);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CONTACT_NUMBER, messageData[1]);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_MESSAGE, messageData[2]);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_IS_INCOMMING, isIncomming);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_LOCATION, locID);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CREATE_DATE, messageData[5]);
		advData.put(AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CREATE_TIME, messageData[6]);
		// Insert the new row, returning the primary key value of the new row
		// first argument for insert() is the table name, second argument provides the name of a column in 
		//  which the framework can insert NULL in the event that the ContentValues is empty (if you instead 
		//  set this to "null", then the framework will not insert a row when there are no values).
		long newRowId;
		newRowId = db.insert(
		         AdventureContract.MessagesTable.MESSAGES_TABLE_NAME,
		         null,
		         advData);
        Log.d(TAG_INSERT, "Inserted SMS row ID " + newRowId + ": ADV_ID='" + advID + "' || CONTACT='" + messageData[1] + 
        		"' || MESSAGE='" + messageData[2] + "' || IS_INCOMING='" + isIncomming + "' || LOC_ID='" + locID + "'");
		return newRowId;
	}

	protected String[] getMessageData(Intent intent) {
		// Parse the SMS.
		Bundle extras = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageString = "";
        if (extras != null) {
            // Retrieve the SMS; protocol description unit (PDU- the industry format for an SMS message);
        	//	A large message might be broken into many, which is why it is an array of objects
            Object[] pdus = (Object[]) extras.get("pdus");
            msgs = new SmsMessage[pdus.length];
		    Log.d(TAG, "In getMessageData of SMS Rece Workflow (BR); pdu/msgs #: " + pdus.length);
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                messageString += msgs[i].getMessageBody().toString();
            }
            // Build SMS array and insert it into DB
            long locID = 1;//~~insertLocationData(mLocClient.getLastLocation());
            String[] dateTime = getDBDateTime();
            String isIncomming = (intent.getAction().equals(FILTER_ACTION_RECEIVE_SMS))?"true":"false";
            String[] arr = {String.valueOf(advID), msgs[0].getOriginatingAddress(), messageString, isIncomming, 
            		Long.toString(locID), dateTime[0], dateTime[1]};
    		return arr;   
        }
        return null;
	}
	
	
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	//---------------------------------------Location Methods-------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////////

	// The callback method that receives location updates
	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG2, "Location client triggered. Current Location: Longitude: " + location.getLongitude() +
				"Latitude: " + location.getLatitude());
		insertLocationData(location);
	}
	
	// Inserts location data into table, returns the ID of the inserted location data
	protected long insertLocationData(Location mloc) {
		// Get location data for insert
		String mLongitude = Double.toString(mloc.getLongitude());
		String mLatitude = Double.toString(mloc.getLatitude());
		String[] dateTime = getDBDateTime();
		String createDate = dateTime[0];
		String createTime = dateTime[1];
		// Write data to database table: locations
		// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
		// then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
		DBAdventureHelper enclosingInstance = new DBAdventureHelper();
		DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(getBaseContext());
		// Get the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// Create new contentValue and write columns to the object
		ContentValues advData = new ContentValues();
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ADVENTURE_ID, advID);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ORDER, 		locOrderCounter);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_LONGITUDE, 	mLongitude);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_LATITUDE, 	mLatitude);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CREATE_DATE, createDate);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CREATE_TIME, createTime);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ACTIVITY, 	activityType);
		advData.put(AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CONFIDENCE, 	confidence);
		
		// Insert the new row, returning the primary key value of the new row
		// first argument for insert() is the table name, second argument provides the name of a column in 
		//  which the framework can insert NULL in the event that the ContentValues is empty (if you instead 
		//  set this to "null", then the framework will not insert a row when there are no values).
		long newRowId;
		newRowId = db.insert(
		         AdventureContract.LocationsTable.LOCATIONS_TABLE_NAME,
		         null,
		         advData);
        Log.d(TAG_INSERT, "Inserted location row ID " + newRowId + ": ADV_ID='" + advID + "' || LOC_ORDER='" + locOrderCounter + 
        		"' || LONG='" + mLongitude + "' || LAT='" + mLatitude + "' || ACTIVITY_TYPE='" + activityType + "' || CONF='" 
        		+ confidence + "'");

		locOrderCounter++;
		return newRowId;
	}
	
	protected String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
	
	private void saveActivityData(Intent intent) {
		// Get the update
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        // Get the most probable activity
        DetectedActivity mostProbableActivity = result.getMostProbableActivity();
        // Get the probability that this activity is the the user's actual activity
        confidence = mostProbableActivity.getConfidence();
        // Get the type of activity
        activityType = mostProbableActivity.getType();
        activityName = getNameFromType(activityType);
        Log.d(TAG2, "User's is currently '" + activityName + "' with a '" + confidence + "' confidence." );
	}
	// Queries the adventures data from the DB and instantiates the global variables
	private void initializeAdventureData(long dbID) {
		// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
		//   then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
		DBAdventureHelper enclosingInstance = new DBAdventureHelper();
		DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(this);
		// Get the data repository in read mode
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		// Projection defines which DB cols you will be pulled from the query
		String[] projection = {
				AdventureContract.AdventuresTable._ID,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_DATE,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_TIME,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_DATE,
				AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_TIME	
		};
		
		// Cursor queries the database with the provided parameters; pulling record where ID col == Adventure ID
		Cursor c = db.query(
				AdventureContract.AdventuresTable.ADVENTURES_TABLE_NAME,  									// The table to query
			    projection,                               													// The columns to return
			    AdventureContract.AdventuresTable._ID + " =  ?",											// The columns for the WHERE clause
			    new String[] {String.valueOf(dbID)},                            							// The values for the WHERE clause
			    null,                                     													// don't group the rows
			    null,                                     													// don't filter by row groups
			    null              		                   													// The sort order
		);
		c.moveToFirst();
		
		// Grabs the DB data from the cursor, and saves the values to the global variables
		advID 			= c.getInt(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable._ID));
		advTitle		= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE));
		advStartDate	= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE));
		advStartTime	= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME));
		advEndDate		= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_DATE));
		advEndTime		= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_TIME));
		advCreateDate	= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_DATE));
		advCreateTime	= c.getString(c.getColumnIndexOrThrow(AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_TIME));
	}
		
		
	protected String[] getDBDateTime() {
		Calendar curCal = Calendar.getInstance();
		String createDate = Integer.toString(curCal.get(Calendar.YEAR)) + "-" + Integer.toString(curCal.get(Calendar.MONTH)) + "-" + Integer.toString(curCal.get(Calendar.DAY_OF_MONTH));
		String createTime = Integer.toString(curCal.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(curCal.get(Calendar.MINUTE));
		return new String[] {createDate, createTime};
	}

    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int sentSMS = 1;
	public Location lastLoc;
	public int advID;
	public int locOrderCounter = 0;
	public String advTitle;
	public String advStartDate; 
	public String advStartTime;
	public String advEndDate;
	public String advEndTime;
	public String advCreateDate;
	public String advCreateTime;
	public PendingIntent recurringLocPI;
	public AdventureLocator mAdventureLoc;
	public final static String EXTRA_DB_ID 						= "com.scarr025.zwilio.LONG";
	public final static int RECURRING_LOC_TIME_INTERVAL		= 1 * 60 * 1000; // To minutes
	public final static String FILTER_ACTION_QUIT_SERVICE 		= "com.scarr025.zwilio.quitService";
	public final static String FILTER_ACTION_LOCATION_TRACKER 	= "com.scarr025.zwilio.locTracker";
	public final static String FILTER_ACTION_RECEIVE_SMS		= "android.provider.Telephony.SMS_RECEIVED";
	public final static String TAG								= "$$AdvServ$$";
	public final static String TAG_INSERT						= "$$DBInsert$$";
	
//**Location Tracking Variables**// 	
    private LocationClient mLocClient;
    private LocationRequest mLocationRequest;
    private static final int UPDATE_INTERVAL_SECONDS = 30;				// Update frequency in seconds
    private static final long UPDATE_INTERVAL_MILLISECONDS =			// Update frequency in milliseconds
    		UPDATE_INTERVAL_SECONDS * 1000;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;			// The fastest update frequency, in seconds
    private static final long FASTEST_INTERVAL =						// A fast frequency ceiling in milliseconds
            FASTEST_INTERVAL_IN_SECONDS * 1000;
    private final static String TAG2 = "$$AdvServ-LocTracker$$";	
	private static final String GPS_ERROR_MESSAGE = "Could not connect to Google Play Services, or DB entry error, please resolve issue.";

//**Activity Tracking Variables**// 	
    // Constants that define the activity detection interval
    private static final int DETECTION_INTERVAL_SECONDS = 10;
    private static final int DETECTION_INTERVAL_MILLISECONDS = 1000 * DETECTION_INTERVAL_SECONDS;
    // Store the PendingIntent used to send activity recognition events back to the app
	private PendingIntent mActivityRecognitionPendingIntent;
	private ActivityRecognitionClient mActivityRecognitionClient;			// Store the current activity recognition client
	private final static String FILTER_ACTIVITY_TRACKER 	= "com.example.android_tester.activityTracker";
	private boolean activityRecognitionActive;
	private String activityName;
	private int activityType;
	private int confidence;


	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	// Called by Location Services if the attempt to Location Services fails.
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Kill service and display message that location services can't connect
		
	}
	// Called by Location Services when the request to connect the client finishes successfully. At this point, you can
    //  request the current location or start periodic updates
	@Override
	public void onConnected(Bundle arg0) {
        Log.d(TAG2, "In onConnected.");
		if (mLocClient.isConnected()) {
	        mLocClient.requestLocationUpdates(mLocationRequest, this);
	        Log.d(TAG2, "Location listener started.");
		} else if (mLocClient.isConnecting()) {
	        Log.d(TAG2, "Location client is attempting to connect, listener not started.");
		} else {
	        Log.d(TAG2, "Location client not connected, listener not started.");
		}
//**Activity Check**// 
		if (mActivityRecognitionClient.isConnected()) {
			// Request activity recognition updates using the preset detection interval and
	        // PendingIntent. This call is synchronous.
	        mActivityRecognitionClient.requestActivityUpdates(
	                DETECTION_INTERVAL_MILLISECONDS,
	                mActivityRecognitionPendingIntent);
	        // Since the preceding call is synchronous, turn off the in progress flag and 
	        //  disconnect the client
	        mActivityRecognitionClient.disconnect();
	        activityRecognitionActive = false;
		} else if (mActivityRecognitionClient.isConnecting()) {
	        Log.d(TAG2, "Activity Recognition client is attempting to connect, listener not started.");
		} else {
	        Log.d(TAG2, "Activity Recognition client not connected, listener not started.");
		}			  
	}

	// Called by Location Services if the connection to the location client drops because of an error.
   @Override
   public void onDisconnected() {
	   // TODO Reconnect the client
       mActivityRecognitionClient = null;					// Delete the client
       activityRecognitionActive = false;
   }
   
   public boolean servicesConnected() {
       // Check that Google Play services is available
       int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
       
       // If Google Play services is available
       if (ConnectionResult.SUCCESS == resultCode) {
           Log.d(TAG2, "Google Play services is available.");
           return true;																	// Continue
       // Google Play services was not available for some reason
       } else {
           Log.d(TAG2, "You do not have access to Google Play Services..." +
       			"\nError Code: " + GooglePlayServicesUtil.getErrorString(resultCode));
           return false;
       }
   }
   
}