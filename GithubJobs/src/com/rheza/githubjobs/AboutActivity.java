package com.rheza.githubjobs;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;

public class AboutActivity extends FragmentActivity  {
		
	 	@Override
	    public void onCreate(Bundle savedInstanceState) {
	 		super.onCreate(savedInstanceState);
	
	 		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	 		
	 		getSupportActionBar().setTitle("About Jobbe App");
	 		setContentView(R.layout.about);
	 		
	 	}
		@Override
	 	public boolean onOptionsItemSelected(MenuItem item) {
	 	    switch (item.getItemId()) {
	 	        case android.R.id.home:
	 	            // app icon in action bar clicked; go home
	 	        	this.finish();
	 	            return true;
	 	        default:
	 	            return super.onOptionsItemSelected(item);
	 	    }
	 	}
}