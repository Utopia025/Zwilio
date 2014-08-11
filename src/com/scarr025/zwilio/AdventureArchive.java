package com.scarr025.zwilio;

import com.scarr025.zwilio.R;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class AdventureArchive extends ListActivity
		implements LoaderCallbacks<Cursor>{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mCursor = getCursorData();
		setupLoadManagers();
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	private void setupLoadManagers() {
	    /* Initialize the adapter. Note that we pass a 'null' Cursor as the third argument. We will pass the 
	     * adapter a Cursor only when the data has finished loading for the first time (i.e. when the
	     * LoaderManager delivers the data to onLoadFinished). 
	     * Also note that we have passed the '0' flag as the last argument. This prevents the adapter from 
	     * registering a ContentObserver for the Cursor (the CursorLoader will do this for us!). */
	    mAdapter = new SimpleCursorAdapter(this, 
	    		android.R.layout.two_line_list_item,				// Specify used row template
	    		null, 												// Pass in the cursor to bind to.
	    		dataColumns, 										// Array of cursor columns to bind to.
	    		viewIDs, 											// Which layout objects to bind to those columns
	    		0);
	    		
	    // Associate the (now empty) adapter with the ListView.
	    setListAdapter(mAdapter);

	    /* The Activity (which implements the LoaderCallbacks<Cursor> interface) is the callbacks object 
	     * through which we will interact with the LoaderManager. The LoaderManager uses this object to
	     * instantiate the Loader and to notify the client when data is made available/unavailable */
	    mCallbacks = this;

	    /* Initialize the Loader with id '1' and callbacks 'mCallbacks'. If the loader doesn't already exist, 
	     * one is created. Otherwise, the already created Loader is reused. In either case, the LoaderManager will 
	     * manage the Loader across the Activity/Fragment lifecycle, will receive any new loads once they have completed,
	     * and will report this new data back to the 'mCallbacks' object. */
	    LoaderManager lm = getLoaderManager();
	    lm.initLoader(LOADER_ID, null, mCallbacks);		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		// Create a new CursorLoader with the following query parameters.
	    return new AdventureArchiveLoader(AdventureArchive.this, CONTENT_URI,
	        PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> mLoader, Cursor mCursor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> mLoader) {
		// TODO Auto-generated method stub
		
	}
	

	private Cursor getCursorData() {
		// Instantiate  subclass of SQLiteOpenHelper; must instantiate class (DBAdventureHelper)
		//   then subclass instance of SQLiteOpenHelper, that is within above class (AdventureDBHelper)
		DBAdventureHelper enclosingInstance = new DBAdventureHelper();
		DBAdventureHelper.AdventureDBHelper mDbHelper = enclosingInstance.new AdventureDBHelper(this);
		// Get the data repository in read mode
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Projection defines which DB cols you will be pulled from the query
		// Cursor queries the database with the provided parameters; pulling record where ID col == Adventure ID
		Cursor c = db.query(
				AdventureContract.AdventuresTable.ADVENTURES_TABLE_NAME,  									// The table to query
			    projection,                               													// The columns to return
			    null,																						// The columns for the WHERE clause
			    null,      	 								                     							// The values for the WHERE clause
			    null,                                     													// don't group the rows
			    null,                                     													// don't filter by row groups
			    "DESC"             		                   													// The sort order
		);
		c.moveToFirst();
	    return c;
	  }
	
	private Cursor mCursor;
	String[] projection = {
			AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE,
			AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_DATE,
			AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_START_TIME,
	};
	private static final int LOADER_ID = 1;								// Loader id-specific to the Activity/Fragment in which they reside
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;			// Callbacks through which we will interact with the LoaderManager
	private SimpleCursorAdapter mAdapter;								// The adapter that binds our data to the ListView
	String[] dataColumns = new String[] 								// Array of cursor columns to bind to
			{AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_ADVENTURE_TITLE,
    		AdventureContract.AdventuresTable.ADVENTURES_COLUMN_NAME_CREATE_DATE };				
	int[] viewIDs = { R.id.firstLine, R.id.secondLine };				// Parallel array of which layout objects to bind dataColumns to

	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adventure_archive, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
