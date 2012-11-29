package dev.app.mangu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import dev.app.mangu.R;



public class Utils {
	
	Activity caller_activity = new Activity();
	Activity master_activity = null;
	View master_view = null;
	Intent in = new Intent();
	//location stuff
	static LocationManager locManager; 
	static LocationListener locationListener;
	
	public Utils(Activity calling_activity) {
		caller_activity = calling_activity;
	}
	
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
	/*
	 * Method to Open an HTTP Connection and return an input stream
	 */
	public InputStream OpenHttpConnection(String urlString,Activity a) throws IOException  {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
        
        if (!(conn instanceof HttpURLConnection))
        	throw new IOException("Not an HTTP connection");
        
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode(); 
            if (response == HttpURLConnection.HTTP_OK) {
            	in = httpConn.getInputStream();
            }
        	
        }catch(Exception ex) {
        	//throw new IOException("Error connectiong");
        	//fallback(a);
        	
        }
        return in;	
        
	}
	
	/*
	 * Method below downloads an image from a url and returns the bitmap.
	 * It makes use of the method above
	 */
	public Bitmap DownloadImage(String URL,Activity a) {
        Bitmap bitmap = null;
        InputStream in = null;
        try
		{
		in = OpenHttpConnection(URL,a);
		bitmap = BitmapFactory.decodeStream(in);
		in.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
        return bitmap;
	}
	
	/*
	 * Alert Dialog method
	 */
	public static void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();


		
		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);
		
		// Setting alert dialog icon
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail );

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	
	
	/*
	 * Action bar method
	 */
	public static void titleProgressDialog(Activity a,Boolean show)
	{
	if(show)
	{
		// then, you can do this to show the icon
		a.setProgressBarIndeterminateVisibility(true);
		
	}else
	{
		//or to hide it
		a.setProgressBarIndeterminateVisibility(false);
	}
	
	}
	
	/*
	 *loading bar method
	 */
	public static void loadingBar(Activity l,Boolean show)
	{
		//show or hide loading bar
		try
		{
		LinearLayout mainLayout=(LinearLayout)l.findViewById(R.id.loading_bar);
		
		if(show)
		{
			mainLayout.setVisibility(LinearLayout.VISIBLE);
		}else
		{
			mainLayout.setVisibility(LinearLayout.GONE);
			
		}	
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Add A sha1 to a url();
	 */
	public static String toSHA1(byte[] convertme) throws Exception {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return  byteArrayToHexString(md.digest(convertme));
	}
	
	/*
	 * Used by method above
	 */
	public static  String byteArrayToHexString(byte[] b) throws Exception {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	
	/*
	 * returns a string formated as a currency with a particular format
	 * example:
	 * String price = 200000;
	 * String formated_price = null;
	 * formated_price = Utils.currency_format(price,"UGX");
	 * This will return "UGX 200,000"
	 */
	public String currency_format(String currency,String format) {
		
		//
		Double original = Double.parseDouble(currency);
        //BigDecimal payment = new BigDecimal("1115.37");
		
		BigDecimal payment = new BigDecimal(original);
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US); 
        double doublePayment = payment.doubleValue();
        String s = n.format(doublePayment);
       // System.out.println("And the final result is: "+s);
        String final_value = s.substring(1);
        //final_value.replace(".00","");
        final_value = format+" "+final_value;
        final_value= final_value.substring(0,final_value.length()-3);
        return final_value;
	}
	
	/*
	 * return a json array.Supply this method with the url from which to retrieve the json.
	 * You need to include the Activity calling the json,the json root and a boolean for cashe
	 */
	public static JSONArray  fetchJson(Activity a,String url,String name,String TAG,Boolean cache) throws JSONException
	{

		JSONArray myjson = new JSONArray();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
		
        String saved_data = preferences.getString(url,"");
        
        	//if we are caching this request
            if(!("").equals(saved_data) && cache && !checkInternetStatus(a))
            {
            	 JSONObject jsonObj = new JSONObject(saved_data);
            	// Getting Array of Categories
    		myjson = jsonObj.getJSONArray(name);
    			
    		//	System.out.println("Local");
            }else
            {
            	
            	if(checkInternetStatus(a))
            	{
            	// Getting Array of Categories
            		try
            		{
            			
            			
            			JSONParser jParser = new JSONParser(a);
            			JSONObject json =	jParser.execute(new String[] {url}).get();
            		//	JSONObject json = jParser.getJSONFromUrl(url);
            			myjson = json.getJSONArray(TAG);	
            			preferences.edit().putString(url,json.toString()).commit();
            		}catch(Exception e)
            		{
            		e.printStackTrace();	
            		}
    			
    			
    		
            	}
            }
            return myjson;
	}
	
	/*
	 * Check that there is an active Internet connection
	 */
	public static Boolean checkInternetStatus(Activity a)
	{
		 a.getApplicationContext();
		 ConnectivityManager cm = (ConnectivityManager)a.getSystemService(Context.CONNECTIVITY_SERVICE);
		 return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	/*
	 * System.out.println(); made easy
	 */
	public static void println(String r)
	{
		System.out.println(r);
	}
	
	/*
	 *Loading..... 
	 */
	public static void loading(Activity a ,ProgressDialog dialog)
	{
		dialog.setMessage("Loading . Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
	}
	
	/*
	 * Load an intent
	 */
	private static Intent loadIntent(Activity a,Class<?> b)
    {
		Intent intent = new Intent(a.getApplicationContext(),b);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
    }
	
	/*
	 *Get GPS 
	 */
	private static double[] getGPS(Activity a) {
    	LocationManager lm = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);  
    	List<String> providers = lm.getProviders(true);

    	/* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
    	Location l = null;
    	
    	for (int i=providers.size()-1; i>=0; i--) {
    		l = lm.getLastKnownLocation(providers.get(i));
    		if (l != null) break;
    	}
    	
    	double[] gps = new double[2];
    	if (l != null) {
    		gps[0] = l.getLatitude();
    		gps[1] = l.getLongitude();
    	}
    	return gps;
    	
    }
	
	/*
	 * Store user Location
	 */
	public static void getAndStoreUserLocation(Activity b,Boolean reset)
	{
		
		if(getUserLocationString(b).length() < 5 || reset)
		{
		   double[] myloc = new double[2];
		   myloc = getGPS(b);
		   String coords = ""+myloc[0]+","+myloc[1];
		   String mylat = ""+myloc[0]+"";
		   String mylng = ""+myloc[1]+"";
		   	  
		      //store the coords for later
		   if(mylat.length() >0 && mylng.length() > 0)
		   {
			  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(b);
			  preferences.edit().putString("lat",mylat).commit();
			  preferences.edit().putString("lng",mylng).commit();
			  preferences.edit().putString("coords",coords).commit();
			  
		   }
		}
		  //turn off GPS
		  //Awesomeness.turnGPSOff(b);  
	}
	
	/*
	 * Get user location
	 */
	public static String getUserLocationString(Activity a)
	{
    String coords = "";
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
    String lat = preferences.getString("lat","");
    String lng = preferences.getString("lng","");
    coords = ""+lat+","+lng;
   // coords = preferences.getString("coords","");
    if(coords.length() > 0)
    {
    	// Awesomeness.turnGPSOff(a);	
    }
    println(coords);
    return coords;
	}
	
	/*
	 * Check that user GPS is enabled
	 */
	public static void CheckEnableGPS(Activity a){
	    String provider = Settings.Secure.getString(a.getContentResolver(),
	      Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	       if(provider.equals("")){
	    	showToast(a,"Your Gps is not Enabled,Please Enable your GPS");
	        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
	        a.startActivity(intent);
	       }

	   }
	
	/*
	 * Turn off user GPS
	 */
	public static void turnGPSOff(Activity a ){
	    String provider = Settings.Secure.getString(a.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		println(" GPS Provider"+provider);
	    
	    if(provider.contains("gps")){ //if gps is enabled
	  
	    	
	    	//locManager.removeUpdates( locationListener);
	    	println("Turning off GPS");
	    	
	    	/*
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        a.sendBroadcast(poke);
	        */
	    	 final Intent poke = new Intent();
	         poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	         poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	         poke.setData(Uri.parse("3")); 
	         a.sendBroadcast(poke);
	        
	    }
	}
	
	/*
	 * Turn user GPS on
	 */
	 public static void  turnGPSOn(final Activity a){   
		 
		 
		 AlertDialog builder = new AlertDialog.Builder(a).create();
		    builder.setMessage("Your GPS is disabled! Would you like to enable it?");

		    builder.setButton("Enable GPS", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		          
		        	
		        	    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
				        a.startActivity(intent);
		        	//load the intent
		        	//Intent intent = Awesomeness.loadIntent(a,b);
		        	//a.startActivity(intent);
		        }
		       
		    });
		    builder.setButton2("Do nothing", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	
		        	 dashboard(a,false,true);
		        	//a.finish();
		            return;
		        }
		    });

		    builder.show();
		 
		  
		    }
	 
	 /*
	  * Check GPS
	  */
		public static Boolean checkGPS(Activity a)
		{
		Boolean gps = false;
	    String provider = Settings.Secure.getString(a.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	   	if(provider.contains("gps")){ 	
	   	gps = true;
	   	}
    	return gps;
		}
		
		/*
		 *Show Toast within calling Activity 
		 */
		public static void showToast(Activity a,String msg)
		{
		if(msg.length() < 1)
		{
		msg = "There was an Error";	
		}
			
		Toast toast= Toast.makeText(a.getApplicationContext(), 
				msg, Toast.LENGTH_LONG);  
				toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();

		}
		
		/*
		 * String limit
		 */
		 public static String limit(String value, int length)
		  {
		    StringBuilder buf = new StringBuilder(value);
		    if (buf.length() > length)
		    {
		      buf.setLength(length);
		      buf.append("...");
		    }

		    return buf.toString();
		  }
		 
		 /*
		  * 
		  */
			public static void dashboard(final Activity a,Activity b,Boolean Links,Boolean title,final ProgressDialog dialog)
			{
			
				
				  //Below goes the action bar code...should probably be given a class...this code is repeated in all activities across
				
				/*
				
		            try	
		            {
					final ActionBar actionBar = (ActionBar) a.findViewById(R.id.actionbar);
					
					//clear all previous actions so we don't have dupes
					actionBar.removeAllActions();	
					actionBar.setHomeAction(new IntentAction(a.getApplicationContext(),loadIntent(a,MekaActivity.class), R.drawable.meka_logo)); //R.drawable.icon
					if(title)
					{
					actionBar.setTitle("Meka Price Search");
					}
					
					
					if(Links)
					{
					//Awesomeness.println(a.getLocalClassName());
						
						
						//Get the classname and make sure not to display the same link
					
					
					if(!a.getLocalClassName().equals("findMapActivity"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,findMapActivity.class), R.drawable.location));
					}
					
					if(!a.getLocalClassName().equals("Stores"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,Stores.class), R.drawable.basket));
					}
				    
					if(!a.getLocalClassName().equals("Featured"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,FeaturedAll.class), R.drawable.featured));
					}
				    
					if(!a.getLocalClassName().equals("Categories"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,Categories.class), R.drawable.category_icon));
					}
					
				
				}
			
					
		            }catch(Exception e)
		            {
		            e.printStackTrace();	
		            }
				
			
		          */
			} 
			
			/*
			 * 
			 */
			public static void dashboard(final Activity a,Boolean Links,Boolean title)
			{
			
				
				  //Below goes the action bar code...should probably be given a class...this code is repeated in all activities across 
			
				/*
		            try	
		            {
					final ActionBar actionBar = (ActionBar) a.findViewById(R.id.actionbar);
					
					//clear all previous actions so we don't have dupes
					actionBar.removeAllActions();	
					actionBar.setHomeAction(new IntentAction(a.getApplicationContext(),loadIntent(a,MekaActivity.class), R.drawable.meka_logo));
					if(title)
					{
					actionBar.setTitle("Meka Price Search");
					}
					
					if(Links)
					{
					//Awesomeness.println(a.getLocalClassName());
						
						
						//Get the classname and make sure not to display the same link
					
					if(!a.getLocalClassName().equals("findMapActivity"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,findMapActivity.class), R.drawable.location));
					}
					
					if(!a.getLocalClassName().equals("Stores"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,Stores.class), R.drawable.basket));
					}
				    
					if(!a.getLocalClassName().equals("Featured"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,FeaturedAll.class), R.drawable.featured));
					}
				    
					if(!a.getLocalClassName().equals("Categories"))
					{
				    actionBar.addAction(new IntentAction(a.getApplicationContext(), loadIntent(a,Categories.class), R.drawable.category_icon));
					}
				
				}

		            }catch(Exception e)
		            {
		            e.printStackTrace();	
		            }
				
				
		            
		            try
		            {
		            	ImageButton back = (ImageButton)a.findViewById(R.id.home_search_button);
		    			back.setClickable(true);	
		            }catch(Exception e)
		            {
		            	e.printStackTrace();
		            }
		            */
		            
			} 
		 
	

	
	
    

}
