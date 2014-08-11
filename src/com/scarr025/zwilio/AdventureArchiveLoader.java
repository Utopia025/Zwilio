package com.scarr025.zwilio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public class AdventureArchiveLoader extends AsyncTaskLoader<Cursor> {

	public AdventureArchiveLoader(Context context) {
		/* Loaders may be used across multiple Activitys (assuming they aren't bound to the LoaderManager), so 
		   NEVER hold a reference to the context directly. Doing so will cause you to leak an entire Activity's 
		   context. The superclass constructor will store a reference to the Application Context instead, and 
		   can be retrieved with a call to getContext().*/
		super(context);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/****************************************************/
	/** (1) A task that performs the asynchronous load **/
	/****************************************************/

	@Override
	public Cursor loadInBackground() {
		// This method is called on a background thread and should generate a
		// new set of data to be delivered back to the client.
		Cursor loadDataCursor = null;
		// TODO: Perform the query here and add the results to 'data'.
		
		return loadDataCursor;
	}

	
	/********************************************************/
	/** (2) Deliver the results to the registered listener **/
	/********************************************************/
	
	@Override
	public void deliverResult(Cursor loadDataCursor) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			releaseResources(loadDataCursor);
			return;
	    }

	    // Hold a reference to the old data so it doesn't get garbage collected.
	    // We must protect it until the new data has been delivered.
	    Cursor oldData = mData;
	    mData = loadDataCursor;

	    if (isStarted()) {
	    	// If the Loader is in a started state, deliver the results to the
	    	// client. The superclass method does this for us.
	    	super.deliverResult(loadDataCursor);
	    }

	    // Invalidate the old data as we don't need it any more.
	    if (oldData != null && oldData != loadDataCursor) {
	    	releaseResources(oldData);
	    }
	}
	
	private void releaseResources(Cursor data) {
	    // For a simple List, there is nothing to do. For something like a Cursor, we 
	    // would close it in this method. All resources associated with the Loader
	    // should be released here.
	}
	
	
	private Cursor mData;											// We hold a reference to the Loader’s data here.

}
