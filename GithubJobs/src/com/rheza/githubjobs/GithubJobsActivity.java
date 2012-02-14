package com.rheza.githubjobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class GithubJobsActivity extends FragmentActivity {
	
	
	
	private ListView mainListView ;

	 
	 private JSONObject json_data;
	 
	 TextView selection;


	 
	
	
	 
	 
	 private ProgressDialog loadingDialog = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
  
    	
    	 
       
        getSupportActionBar().setTitle(R.string.main_title);
        new DownloadJsonTask().execute();
        /*
        if(haveNetworkConnection() == false)
        {
        	AlertDialog.Builder internetDialog = new AlertDialog.Builder(this);

			 internetDialog.setMessage("No internet connection").setPositiveButton("Close Jobbe App", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 finish();
                     	
            }
             });

		
			internetDialog.show();
        }
      */
    }
    private boolean haveNetworkConnection() {
    	boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
		
	}
    
  
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactionbar, menu);
       
        
       
        return true;
    }
  

    public boolean onQueryTextChange(String newText) {
       // mStatusView.setText("Query = " + newText);
    //	 Toast.makeText(this, "Query = " + newText, Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
      //  mStatusView.setText("Query = " + query + " : submitted");
       // Toast.makeText(this, "Query = " + query + " : submitted", Toast.LENGTH_SHORT).show();
        Intent i= new Intent(getApplicationContext(), SearchJobsActivity.class);
        i.putExtra("searchKeywordText",query);
        startActivity(i);
        return false;
    }

    public boolean onClose() {
    	Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void onClick(View view) {
    
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_search:
        {
        	AlertDialog.Builder locationDialog = new AlertDialog.Builder(this);
        	final EditText input = new EditText(this); 
			 locationDialog.setView(input);
			 locationDialog.setMessage("Enter your keyword").setPositiveButton("Search", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 	
                     	String searchText = input.getText().toString().trim();
                     	
                     	 if(searchText.length() != 0) {
                         
                         Intent intent = new Intent(getBaseContext(), SearchJobsActivity.class);
                         intent.putExtra("searchKeywordText", searchText);
                         startActivity(intent);
                     	 }
                     	 else
                     	 {
                     	Toast.makeText(getApplicationContext(), "No keyword found",
                                    Toast.LENGTH_SHORT).show(); 
                     	 }
                         
            }
             });

			locationDialog.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                             dialog.cancel();
                     }
                     });
			locationDialog.show();
        }
            return true;
        case R.id.menu_setLocation:
        {
        	AlertDialog.Builder locationDialog = new AlertDialog.Builder(this);
        	final EditText input = new EditText(this); 
			 locationDialog.setView(input);
			 locationDialog.setMessage("Set Your Location").setPositiveButton("Save", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 	finish();
                     	String locationText = input.getText().toString().trim();
                     	SharedPreferences pref = getSharedPreferences("MyPreference", MODE_PRIVATE);
                     	 SharedPreferences.Editor editor = pref.edit();
                     	 editor.putString("locationPreference", locationText);
                     	 editor.commit();
                     	 if(locationText.length() != 0) {
                         Toast.makeText(getApplicationContext(), "Set Location to " + locationText,
                                 Toast.LENGTH_SHORT).show();
                     	 }
                     	 else
                     	 {
                     	Toast.makeText(getApplicationContext(), "Set Location to Anywhere" + locationText,
                                    Toast.LENGTH_SHORT).show(); 
                     	 }
                         Intent intent = new Intent(getBaseContext(), GithubJobsActivity.class);
                         intent.putExtra("locationFromIntent", locationText);
                         startActivity(intent);
            }
             });

			locationDialog.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                             dialog.cancel();
                     }
                     });
			locationDialog.show();
        }
            return true;
            
        case R.id.menu_refresh:
        {
        	refresh();
        }
        return true;
        case R.id.menu_about:
        {
        	Intent myIntent = new Intent(GithubJobsActivity.this, AboutActivity.class);
        	GithubJobsActivity.this.startActivity(myIntent);
        }
      
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    //refresh method
    public void refresh() {

    	 Intent intent = getIntent();
    	    finish();
    	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    	    startActivity(intent);
    }
    
  
  
    
   

    private class DownloadJsonTask extends AsyncTask<String, Void, ArrayList<Jobs>> {
     
    	
    
        // can use UI thread here
        protected void onPreExecute() {
        	
        	
        	 System.out.println("onPreExecute Called");
        	 
        	
             // ...
             loadingDialog = ProgressDialog.show(GithubJobsActivity.this,
                     "", "Loading Data...", true);
             
             loadingDialog.show();
             
             // ...
                     // Do some Fake-Work
             // ...
             
            
             
        }
        
        // automatically done on worker thread (separate from UI thread)
        protected ArrayList<Jobs> doInBackground(final String... args) {
        	 ArrayList<Jobs> jobs = new ArrayList<Jobs>();
        	 System.out.println("doInBackground Called");
        	 String URL = "http://jobs.github.com/positions.json";
        	 SharedPreferences pref = getSharedPreferences("MyPreference", MODE_PRIVATE);
        	 String locationFromPreference =  pref.getString("locationPreference", null);
        	 
        	 if(locationFromPreference !=null) {
        	
        	 locationFromPreference = locationFromPreference.replace(" ", "+");
        	 System.out.println("Location From Preference = "+locationFromPreference);
        	 	URL = "http://jobs.github.com/positions.json?&location=" + locationFromPreference;
        	 
        	 }
        	 else
        	 {
        		 System.out.println("Location From Preference = "+locationFromPreference);
         	 	URL = "http://jobs.github.com/positions.json";
        	 }
        	 
        
        	 
        	 
        	 System.out.println(URL);
             try {
             	
            	 HttpClient hc = new DefaultHttpClient();  
                 HttpGet get = new  
                 HttpGet(URL);  
                 System.out.println(URL);
                 HttpResponse rp = hc.execute(get);  
                 System.out.println(rp);
                 if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)  
                 {  
                	 
                	String result = EntityUtils.toString(rp.getEntity());  
                	JSONArray jArray = new JSONArray(result); 
                	 System.out.println(result);
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
            
            return jobs;
        }
        
        // can use UI thread here
        protected void onPostExecute(ArrayList<Jobs> result) {
        	 System.out.println("onPostExecute Called");
             JobListAdaptor adaptor = new JobListAdaptor(GithubJobsActivity.this,R.layout.list_item, result);  
             
             
           
            
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
             loadingDialog.dismiss();
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
    