package com.rheza.githubjobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MenuInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;


import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;


public class SearchJobsActivity extends FragmentActivity {
	
	
	private static final int EDIT_ACTION = 1;
	private ListView mainListView ;
	 private ArrayAdapter<String> listAdapter ;
	 
	 private JSONObject json_data;
	 private ProgressDialog loadingDialog = null;
	 TextView selection;

	 Handler toastHandler = new Handler();
	 Runnable toastRunnable = new Runnable() {public void run() {Toast.makeText(getApplicationContext(), "No jobs found",
             Toast.LENGTH_LONG).show();}};
	 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 	   Bundle extras = getIntent().getExtras();
  	 if(extras !=null) {
  		 
  	 String searchKeyword = extras.getString("searchKeywordText");
  	getSupportActionBar().setTitle("Search \""+searchKeyword+"\" " + "on Github Jobs");
  	 }
        new DownloadJsonTask().execute();
       
        
    }
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
    
   
    


    private class DownloadJsonTask extends AsyncTask<String, Void, ArrayList<Jobs>> {
     
    
     
        // can use UI thread here
        protected void onPreExecute() {
        	
        	
        	 System.out.println("onPreExecute Called");
        	 
        	 loadingDialog = ProgressDialog.show(SearchJobsActivity.this,
                     "", "Loading Data...", true);
             loadingDialog.show();
             
        }
   
        // automatically done on worker thread (separate from UI thread)
        protected ArrayList<Jobs> doInBackground(final String... args) {
        	 ArrayList<Jobs> jobs = new ArrayList<Jobs>();
        	 System.out.println("doInBackground Called");
        	 String URL = "http://jobs.github.com/positions.json";
      
        	 SharedPreferences pref = getSharedPreferences("MyPreference", MODE_PRIVATE);
        	 String locationFromPreference =  pref.getString("locationPreference", null);
        	
        	 System.out.println("Location From Preference in search view :" + locationFromPreference);
        	 
        	 Bundle extras = getIntent().getExtras();
        	 if(extras !=null) {
        		 
        	 String searchKeyword = extras.getString("searchKeywordText");
        	 searchKeyword = searchKeyword.replace(" ", "+");
        	 	URL = "http://jobs.github.com/positions.json?description="+ searchKeyword ;
        	 	if(locationFromPreference !=null) {
                	
               	 locationFromPreference = locationFromPreference.replace(" ", "+");
               	 System.out.println("Location From Preference = "+locationFromPreference);
               	 	URL = "http://jobs.github.com/positions.json?&description="+ searchKeyword +"&location=" + locationFromPreference;
               	 
               	 }
        	 }
        	 System.out.println(URL);
             try {
             	
            	 HttpClient hc = new DefaultHttpClient();  
                 HttpGet get = new  
                 HttpGet(URL);  
                 System.out.println(URL);
                 HttpResponse rp = hc.execute(get);  
                
                
                
                
               
                	 if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)  
                     {  
                    	 
                		 String result = EntityUtils.toString(rp.getEntity()); 
                    	JSONArray jArray = new JSONArray(result); 
                    	 System.out.println(result);
                    	
                    	 if (jArray.length() <= 0)
                    	 {
                    		 System.out.println("JARRAY LENGTH " + jArray.length());
                    	//	 Toast.makeText(getApplicationContext(), "No result found ",
                               //     Toast.LENGTH_SHORT).show();
                    		 loadingDialog.dismiss();
                    		 toastHandler.post(toastRunnable);
                    		 finish();
                    		 
                    	 }
                    	
                    		 
                    	 
                    	for (int i = 0; i < jArray.length(); i++) { 
                    	json_data = jArray.getJSONObject(i);
                    	Jobs job = new Jobs();
                        job.company = json_data.getString("company");
                        job.location = json_data.getString("location");
                        job.createdAt = json_data.getString("created_at");
                        job.companyURL = json_data.getString("company_url");
                        job.title = json_data.getString("title");
                        job.url = json_data.getString("url");
                        job.id = json_data.getString("id");
                        job.company_logo = json_data.getString("company_logo");
                        job.type = json_data.getString("type");
                        job.how_to_apply = json_data.getString("how_to_apply");
                        job.description = json_data.getString("description");
                       
                        jobs.add(job);
                        
                        
                    	}
                     
                    	
                    }
                	 
                 
                
                           
            } catch (Exception e) {
                    Log.e("Github Jobs Activity", "Error loading JSON", e);
            }
            loadingDialog.dismiss();
            return jobs;
        }
   
        // can use UI thread here
        protected void onPostExecute(ArrayList<Jobs> result) {
        	 System.out.println("onPostExecute Called");
             JobListAdaptor adaptor = new JobListAdaptor(SearchJobsActivity.this,R.layout.list_item, result);  
             
             
           
            
              mainListView = (ListView) findViewById( R.id.mainListView );
              mainListView.setAdapter(adaptor);
              
              mainListView.setOnItemClickListener(new OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
                   Object o = mainListView.getItemAtPosition(position);
                   Jobs fullObject = (Jobs)o;
                   Bundle bundle = new Bundle();
                   bundle.putString("company", fullObject.company);
                   bundle.putString("location", fullObject.location);
                   bundle.putString("created_at", fullObject.createdAt);
                   bundle.putString("company_url", fullObject.companyURL);
                   bundle.putString("title", fullObject.title);
                   bundle.putString("url", fullObject.url);
                   bundle.putString("id", fullObject.id);
                   bundle.putString("company_logo", fullObject.company_logo);
                   bundle.putString("type", fullObject.type);
                   bundle.putString("how_to_apply", fullObject.how_to_apply);
                   bundle.putString("description", fullObject.description);
                 
                   System.out.println(fullObject.title);
                   
                   Intent newIntent = new Intent(getApplicationContext(), DetailViewActivity.class);
                   newIntent.putExtras(bundle);
                   startActivityForResult(newIntent, 0);
                  
                   
                  }  
              	});
        	 
     }
       	
  
    
 
    private class JobListAdaptor extends ArrayAdapter<Jobs> {
        private ArrayList<Jobs> jobs;
        public JobListAdaptor(Context context,
                int textViewResourceId,
                ArrayList<Jobs> items) {
        	super(context, textViewResourceId, items);
        	this.jobs = items;
        }
   

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        		
        		
                View v = convertView;
                if (v == null) {
                        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(R.layout.list_item, null);
                }
                Jobs o = jobs.get(position);
              
                TextView companyView = (TextView) v.findViewById(R.id.companytext);
                TextView titleView = (TextView) v.findViewById(R.id.titletext);
                TextView locationView = (TextView) v.findViewById(R.id.locationtext);
                TextView typeView = (TextView) v.findViewById(R.id.typetext);
                TextView createdView = (TextView) v.findViewById(R.id.createdattext);
              
                companyView.setText(o.company);
                titleView.setText(o.title);
                locationView.setText(o.location);
                typeView.setText(o.type);
  SimpleDateFormat parser = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy");  
              
                
                try {
					Date lastUpdateDate = parser.parse(o.createdAt);
					 long longDate=lastUpdateDate.getTime();
					CharSequence timeago2 = DateUtils.getRelativeTimeSpanString(longDate);
					String timeagoString = timeago2.toString();
					createdView.setText(timeagoString);
                } catch (ParseException e) {
					// TODO Auto-generated catch block
                	
					e.printStackTrace();
				}  
                
                
                return v;
        }
    }
    }
}
    