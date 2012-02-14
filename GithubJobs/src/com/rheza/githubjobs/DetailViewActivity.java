package com.rheza.githubjobs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailViewActivity extends FragmentActivity {
		private String company_logo_private;
		
	 	@Override
	    public void onCreate(Bundle savedInstanceState) {
	 		super.onCreate(savedInstanceState);
	 		
	 		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	 		
	 	   getSupportActionBar().setTitle("jobbe : Job Detail");
	 		
	 	  Bundle bundle = this.getIntent().getExtras();
	 		String company = bundle.getString("company");
	 		String location = bundle.getString("location");
	 		String created_At = bundle.getString("created_at");
	 		final String company_url = bundle.getString("company_url");
	 		String title = bundle.getString("title");
	 		String url = bundle.getString("url");
	 		String id = bundle.getString("id");
	 		String company_logo = bundle.getString("company_logo");
	 		String type = bundle.getString("type");
	 		String howtoapply = bundle.getString("how_to_apply");
	 		String description = bundle.getString("description");
	 		
	 		company_logo_private = bundle.getString("company_logo");
	 		
	 		System.out.println("company logo:"+company_logo);
	 		 
	         setContentView(R.layout.detail);
	         
	         TextView titleView = (TextView)findViewById(R.id.titledetail);
	         TextView companyView = (TextView)findViewById(R.id.companydetail);
             
             TextView locationView = (TextView)findViewById(R.id.locationdetail);
             TextView typeView = (TextView)findViewById(R.id.typedetail);
             WebView descriptionWeb = (WebView)findViewById(R.id.descriptionwebdetail);
             WebView howtoapplyWeb = (WebView)findViewById(R.id.howtowebdetail);
             Button companyWeb = (Button)findViewById(R.id.buttonWeb);
             
             companyView.setText(company);
             titleView.setText(title);
             locationView.setText(location);
             typeView.setText(type);
             companyWeb.setText(company);
             descriptionWeb.loadData(description, "text/html", "UTF-8");
             howtoapplyWeb.loadData( howtoapply, "text/html", "UTF-8");
            
           
          
            	
            	  companyWeb.setOnClickListener(new View.OnClickListener() {
                      public void onClick(View v) {
                          openWebURL(company_url);
                      }
                  });
            	  
            	  //disable load image
	 	//	LoadImage(company_logo_private);
	 }
	 	public void openWebURL( String inURL ) {
	 	    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
	 	    startActivity( browse );
	 	}
	 	
	 	public void LoadImage( String inURL ) {
	 		
	 		 try {
	 			setProgressBarVisibility(true);
           	  ImageView i = (ImageView)findViewById(R.id.logodetail);
           	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(inURL).getContent());
           	  i.setImageBitmap(bitmap); 
           	setProgressBarVisibility(false);
           	} catch (MalformedURLException e) {
           	  e.printStackTrace();
           	} catch (IOException e) {
           	  e.printStackTrace();
           	}
	 	}
	 	@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.detailactionbar, menu);
	       
	        
	       
	        return true;
	    }
	 	
	 	
	 	@Override
	 	public boolean onOptionsItemSelected(MenuItem item) {
	 	    switch (item.getItemId()) {
	 	        case android.R.id.home:
	 	            // app icon in action bar clicked; go home
	 	        	this.finish();
	 	            return true;
	 	            
	 	       case R.id.menu_share:
	 	        {
	 	        	share();
	 	        }
	 	       return true;
	 	        default:
	 	            return super.onOptionsItemSelected(item);
	 	    }
	 	}
	 	
	 	 public void share() {
	 		Bundle bundle = this.getIntent().getExtras();
	 		 String title = bundle.getString("title") + " Available";
	 		
	 		String contents = "Apply for " + bundle.getString("title") +" in "+ bundle.getString("location") +" on "+ bundle.getString("company") +". Check this out, " + bundle.getString("url") + " via Jobbe App";
	 		
	 		Intent i = new Intent(Intent.ACTION_SEND);
	 		i.setType("text/plain");
	 		i.putExtra(Intent.EXTRA_SUBJECT, title);
	 		i.putExtra(Intent.EXTRA_TEXT, contents);
	 		startActivity(Intent.createChooser(i, "Share this Github job"));
	 
	    }
	    
}
