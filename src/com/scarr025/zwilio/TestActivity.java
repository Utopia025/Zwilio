package com.scarr025.zwilio;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Intent pastIntent = getIntent();
		String[] intentValsArr = pastIntent.getStringArrayExtra(AdventureSetUp.EXTRA_ARRAY);
		
		// Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(25);
	    textView.setText("Adventure Title: " + intentValsArr[0] + " | Start Date: " + intentValsArr[1] + "-" + intentValsArr[2] + 
	    		"-" + intentValsArr[3] + " | Start Time: " + intentValsArr[4] + ":" + intentValsArr[5] + " | End Date: "
	    		+ intentValsArr[6] + "-" + intentValsArr[7] + "-" + intentValsArr[8] + " | End Time: " + intentValsArr[9] + 
	    		":" + intentValsArr[10]);

	    // Set the text view as the activity layout
	    setContentView(textView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
