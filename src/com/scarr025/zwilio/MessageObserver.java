package com.scarr025.zwilio;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class MessageObserver extends ContentObserver {

	public MessageObserver(Handler handler, Context c) {
		super(handler);
		// TODO Auto-generated constructor stub
		mContext = c;
	}

	@Override
    public void onChange(boolean selfChange) {
		this.onChange(selfChange);
	    Log.d(TAG, "In oChange, old");
		/*
		// save the message to the SD card here
		Uri uriSMSURI = Uri.parse("content://sms/out");
		Cursor cur = this.getContentResolver().query(uriSMSURI, null, null, null, null);
		 // this will make it point to the first record, which is the last SMS sent
		cur.moveToNext();
		String content = cur.getString(cur.getColumnIndex("body"));
		// use cur.getColumnNames() to get a list of all available columns...
		// each field that compounds a SMS is represented by a column (phone number, status, etc.)
		// then just save all data you want to the SDcard :)
		*/
    }		

	
	// Method is only available from API level 16 onwards. Code should not rely on a URI to work properly
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		// do s.th.
		// depending on the handler you might be on the UI
		// thread, so be cautious!
	    Log.d(TAG, "In onChange, new");
	    getLastSentMessage();
	}
	
	
	
	private void getLastSentMessage() {
		Uri uriSMSURI = Uri.parse("content://sms/conversations");
		Cursor cur = mContext.getContentResolver().query(uriSMSURI, null, null, null, null);
		 // this will make it point to the first record, which is the last SMS sent
		if (cur != null) {
			cur.moveToLast();
			String content = cur.getString(cur.getColumnIndex("body"));	
			String[] colNames = cur.getColumnNames();
			for (int i = 0; i < colNames.length; i++) {
//			    Log.d(TAG, "Col " + i + " " + colNames[i] + " = " + cur.getString(cur.getColumnIndex(colNames[i]));
			}
		    Log.d(TAG, "Sent Message body = " + content);
		}
	}

	public Context mContext;
	public final static String TAG								= "$$MessageObserver$$";
}
