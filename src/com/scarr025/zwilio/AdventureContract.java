package com.scarr025.zwilio;

import android.provider.BaseColumns;

public class AdventureContract {
	/* Container for constants that define names for URIs, tables, and columns. The contract class allows you to use 
	 * the same constants across all the other classes in the same package. This lets you change a column name in one 
	 * place and have it propagate throughout your code.
	 * 
	 * A good way to organize a contract class is to put definitions that are global to your whole database in the 
	 * root level of the class. Then create an inner class for each table that enumerates its columns.
	 * 
	 * Note: By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID 
	 * that some Android classes such as cursor adaptors will expect it to have. It's not required, but this can help 
	 * your database work harmoniously with the Android framework.
	 */
	
	// Database Variables
	public static final String ADVENTURES_DATABASE_NAME						= "adventures-db";
	
	public static abstract class AdventuresTable implements BaseColumns {
	    // Base cols for scheduled adventures
		public static final String ADVENTURES_TABLE_NAME 					= "adventures";
	    public static final String ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE 	= "adventure_title";
	    public static final String ADVENTURES_COLUMN_NAME_START_DATE 		= "start_date";
	    public static final String ADVENTURES_COLUMN_NAME_START_TIME 		= "start_time";
	    public static final String ADVENTURES_COLUMN_NAME_END_DATE 			= "end_date";
	    public static final String ADVENTURES_COLUMN_NAME_END_TIME 			= "end_time";
	    public static final String ADVENTURES_COLUMN_NAME_CREATE_DATE		= "create_date";
	    public static final String ADVENTURES_COLUMN_NAME_CREATE_TIME		= "create_time";
	    
	}
	
	
	public static abstract class LocationsTable implements BaseColumns {   
	    // Base cols for location table
		public static final String LOCATIONS_TABLE_NAME 					= "locations";
	    public static final String LOCATIONS_COLUMN_NAME_ADVENTURE_ID 		= "adventure_id";
	    public static final String LOCATIONS_COLUMN_NAME_ORDER		 		= "location_order";
	    public static final String LOCATIONS_COLUMN_NAME_LONGITUDE	 		= "longitude";
	    public static final String LOCATIONS_COLUMN_NAME_LATITUDE	 		= "latitude";	    
	    public static final String LOCATIONS_COLUMN_NAME_CREATE_DATE 		= "create_date";
	    public static final String LOCATIONS_COLUMN_NAME_CREATE_TIME		= "create_time";
	    public static final String LOCATIONS_COLUMN_NAME_ACTIVITY			= "activity";
	    public static final String LOCATIONS_COLUMN_NAME_CONFIDENCE			= "confidence";
	}
	
	
	public static abstract class MessagesTable implements BaseColumns {   
	    // Base cols for messages table
		public static final String MESSAGES_TABLE_NAME 					= "messages";
	    public static final String MESSAGES_COLUMN_NAME_ADVENTURE_ID 	= "adventure_id";
	    public static final String MESSAGES_COLUMN_NAME_CONTACT_NUMBER	= "contact_number";
	    public static final String MESSAGES_COLUMN_NAME_MESSAGE	 		= "message";
	    public static final String MESSAGES_COLUMN_NAME_IS_INCOMMING 	= "is_incomming";	    
	    public static final String MESSAGES_COLUMN_NAME_LOCATION	 	= "location_id";
	    public static final String MESSAGES_COLUMN_NAME_CREATE_DATE 	= "create_date";
	    public static final String MESSAGES_COLUMN_NAME_CREATE_TIME	 	= "create_time";
	}
	
	
	public static abstract class CallsTable implements BaseColumns {   
	    // Base cols for calls table
		public static final String CALLS_TABLE_NAME 					= "calls";
	    public static final String CALLS_COLUMN_NAME_ADVENTURE_ID 		= "adventure_id";
	    public static final String CALLS_COLUMN_NAME_CONTACT_NUMBER		= "contact_number";
	    public static final String CALLS_COLUMN_NAME_IS_INCOMMING 		= "is_incomming";	    
	    public static final String CALLS_COLUMN_NAME_LOCATION	 		= "location_id";
	    public static final String CALLS_COLUMN_NAME_CREATE_DATE 		= "create_date";
	    public static final String CALLS_COLUMN_NAME_CREATE_TIME	 	= "create_time";
	}

	
	public static abstract class ContactsTable implements BaseColumns {   
	    // Base cols for calls table
		public static final String CONTACTS_TABLE_NAME 					= "contacts";
	    public static final String CONTACTS_COLUMN_FIRST_NAME	 		= "first_name";
	    public static final String CONTACTS_COLUMN_LAST_NAME			= "last_name";
	    public static final String CONTACTS_COLUMN_PHONE_NUMBER 		= "contact_number";	    
	    public static final String CONTACTS_COLUMN_NAME_CREATE_DATE 	= "create_date";
	    public static final String CONTACTS_COLUMN_NAME_CREATE_TIME	 	= "create_time";
	}
	
	// Prevents the FeedReaderContract class from being instantiated.
	private AdventureContract() {}
}
