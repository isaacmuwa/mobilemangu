package dev.app.mangu;

import dev.app.mangu.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class JSONParser extends AsyncTask<String, Void,  JSONObject> {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	Utils util = null;
	
	// constructor
	public JSONParser(Activity a) {
	//	Awesomeness.loading(a);	
		//Awesomeness.println("post execute");
	}


	public JSONObject getJSONFromUrl(String url) {

		doInBackground(url);	
		// return JSON String
		return jObj;

	}

	@Override
	protected  JSONObject  doInBackground(String... urls) {
		//Log.e('My Url',url);
		
		 for (String url : urls) {
				url +="&api_key=750746d4acdf37825f2b6fba85312e26e70ab485";
				// Making HTTP request
				try {
					// defaultHttpClient
					
					HttpPost httpPost = new HttpPost(url);
					HttpParams httpParameters = new BasicHttpParams();
					// The default value is zero, that means the timeout is not used. 
					int timeoutConnection = 0;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					// Set the default socket timeout (SO_TIMEOUT) 
					// in milliseconds which is the timeout for waiting for data.
					int timeoutSocket = 10000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					
					httpPost.setHeader("Binarylogic","750746d4acdf37825f2b6fba85312e26e70ab485");
					
					DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
					
					httpClient.setParams(httpParameters);
					
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();			

					
				
					
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
				} catch (Exception e) {
					Log.e("Buffer Error", "Error converting result " + e.toString());
				}

				// try parse the string to a JSON object
				try {
					jObj = new JSONObject(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}
		 }		
		return jObj;
	}
	

	 	protected void onProgressUpdate(Integer... progress) {
	     util.println("pre execute");
	     
	    }
	    protected void onPostExecute(Void result)    {
	    	util.println("post execute");
	    }
}
