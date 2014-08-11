package com.scarr025.zwilio;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;


public class AdventureLocator extends FragmentActivity implements
				GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener {

	
	protected void onCreate(Bundle savedInstanceState) {
		mLocClient = new LocationClient(this, this, this);
		mContext = this;
	}

	public void startConnection() {
		mLocClient.connect();
	}
	
	public Location getLastLocation() {
		return mLocClient.getLastLocation();
	}
	
	// Method that encapsulates the check for Google Play services
	public boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("$$AdventureLocator$$", "In servicesConnected; Google Play services is available (returned true).");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
            return false;
        }
    }
	
	
	
     // Called by Location Services if the attempt to Location Services fails.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects. If the error has a resolution,  
        // try sending an Intent to start a Google Play services activity that can resolve error.
        Log.d("$$AdventureLocator$$", "In onConnectionFailed");
    	if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                Log.d("$$AdventureLocator$$", "In onConnectionFailed; Google has Resolution, resolving");
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
            Log.d("$$AdventureLocator$$", "In onConnectionFailed; Google has no resolution, showing error dialog");
            showErrorDialog(connectionResult.getErrorCode());
        }
    }


    void showErrorDialog(int code) {
    	  GooglePlayServicesUtil.getErrorDialog(code, this, 
    	      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }
    
    
    // Called by Location Services when the request to connect the client finishes successfully.
    //  At this point, you can request the current location or start periodic updates
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
	    Log.d("$$AdvLoc$$", "In the onConnected method of AdventureLocator");

    }

    // Called by Location Services if the connection to the location client drops because of an error.
    //	When called, all requests have been canceled and no outstanding listeners will be executed.
    @Override
    public void onDisconnected() {
        // Display the connection status
	    Log.d("$$AdvLoc$$", "In the onDisconnect method of AdventureLocator");

    }
    
    
    // If isGooglePlayServicesAvailable() returns error, call GooglePlayServicesUtil.getErrorDialog(); this will
    // retrieve localized dialog that prompts users to take the correct action, then display the dialog in a DialogFragment;
    // dialog may allow the user to correct the problem, in which case Google Play services may send a result back to your activity
    // To handle this result, override the method onActivityResult().
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
               	// If the result code is Activity.RESULT_OK, try to connect again
				switch (resultCode) {
                    case Activity.RESULT_OK :
                    // Try the request again
                    break;
                }
        }
     }
    
    
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//-------------------------------------Error Dialog Fragment----------------------------------------//
	//////////////////////////////////////////////////////////////////////////////////////////////////////

    
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

    
    
    
    // Define a request code to send to Google Play services This code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private LocationClient mLocClient;
    private Context mContext;
}
