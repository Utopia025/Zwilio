package com.scarr025.zwilio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class BasePage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base_page, menu);
		return true;
	}
	
	public void newAdventure (View view) {
		startActivity(new Intent(this, AdventureSetUp.class));
	}
	
	public void viewArchive (View view) {
		startActivity(new Intent(this, AdventureArchive.class));
	}

	public void viewMap (View view) {
		startActivity(new Intent(this, GMap.class));
	}
}
