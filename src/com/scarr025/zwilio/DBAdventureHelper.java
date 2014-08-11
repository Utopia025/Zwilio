package com.scarr025.zwilio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdventureHelper {
	
	private static final String SQL_CREATE_ADVENTURES_TABLE =
			"CREATE TABLE " + AdventureContract.AdventuresTable.ADVENTURES_TABLE_NAME + "( " +
					AdventureContract.AdventuresTable._ID + " INTEGER PRIMARY KEY," +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE 	+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE			+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME			+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_DATE			+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_END_TIME			+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_DATE		+ " TEXT, " +
					AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_TIME		+ " TEXT" +
			" )";
	
	private static final String SQL_CREATE_LOCATIONS_TABLE =
			"CREATE TABLE " + AdventureContract.LocationsTable.LOCATIONS_TABLE_NAME + "( " +
					AdventureContract.LocationsTable._ID + " INTEGER PRIMARY KEY," +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ADVENTURE_ID 		+ " INTEGER, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ORDER 				+ " INTEGER, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_LATITUDE 			+ " TEXT, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_LONGITUDE		 	+ " TEXT, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_ACTIVITY			 	+ " INTEGER, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CONFIDENCE		 	+ " INTEGER, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CREATE_DATE			+ " TEXT, " +
					AdventureContract.LocationsTable.LOCATIONS_COLUMN_NAME_CREATE_TIME			+ " TEXT" +
			" )";				
	
	private static final String SQL_CREATE_MESSAGES_TABLE =
			"CREATE TABLE " + AdventureContract.MessagesTable.MESSAGES_TABLE_NAME + "( " +
					AdventureContract.MessagesTable._ID + " INTEGER PRIMARY KEY," +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_ADVENTURE_ID 			+ " INTEGER, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CONTACT_NUMBER			+ " TEXT, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_MESSAGE 				+ " TEXT, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_IS_INCOMMING	 		+ " INTEGER, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_LOCATION		 		+ " INTEGER, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CREATE_DATE			+ " TEXT, " +
					AdventureContract.MessagesTable.MESSAGES_COLUMN_NAME_CREATE_TIME			+ " TEXT" +
			" )";	
	
	
	private static final String SQL_CREATE_CALLS_TABLE =
			"CREATE TABLE " + AdventureContract.CallsTable.CALLS_TABLE_NAME + "( " +
					AdventureContract.CallsTable._ID + " INTEGER PRIMARY KEY," +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_ADVENTURE_ID 			+ " INTEGER, " +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_CONTACT_NUMBER			+ " TEXT, " +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_IS_INCOMMING 			+ " INTEGER, " +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_LOCATION		 			+ " INTEGER, " +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_CREATE_DATE				+ " TEXT, " +
					AdventureContract.CallsTable.CALLS_COLUMN_NAME_CREATE_TIME				+ " TEXT" +
			" )";	
	
	
	private static final String SQL_CREATE_CONTACTS_TABLE =
			"CREATE TABLE " + AdventureContract.ContactsTable.CONTACTS_TABLE_NAME + "( " +
					AdventureContract.ContactsTable._ID + " INTEGER PRIMARY KEY," +
					AdventureContract.ContactsTable.CONTACTS_COLUMN_FIRST_NAME	 			+ " TEXT, " +
					AdventureContract.ContactsTable.CONTACTS_COLUMN_LAST_NAME				+ " TEXT, " +
					AdventureContract.ContactsTable.CONTACTS_COLUMN_PHONE_NUMBER 			+ " TEXT, " +
					AdventureContract.ContactsTable.CONTACTS_COLUMN_NAME_CREATE_DATE		+ " TEXT, " +
					AdventureContract.ContactsTable.CONTACTS_COLUMN_NAME_CREATE_TIME		+ " TEXT" +
			" )";	
	
	
	public class AdventureDBHelper extends SQLiteOpenHelper {
	    // If you change the database schema, you must increment the database version.
	    public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "zwilio-db.db";

	    public AdventureDBHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(SQL_CREATE_ADVENTURES_TABLE);
	        db.execSQL(SQL_CREATE_LOCATIONS_TABLE);
	        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
	        db.execSQL(SQL_CREATE_CALLS_TABLE);
	        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
	    }
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // When adding new tables, create all tables that changed between the old and new DB versions
	    }
	}
	
	
}
