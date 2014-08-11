package com.scarr025.zwilio;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


public class GMap extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext()) == ConnectionResult.SUCCESS) {
        		
    		LatLng sydney = new LatLng(-33.867, 151.206);
	
	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
	
	        map.addMarker(new MarkerOptions()
	                .title("Sydney")
	                .snippet("The most populous city in Australia.")
	                .position(sydney));
        } else {
		    Toast.makeText(getBaseContext(), "No GPS Connection.", Toast.LENGTH_LONG).show();
        }
    }

}
