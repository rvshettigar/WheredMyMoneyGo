package com.anumeha.wheredmymoneygo.Services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.anumeha.wheredmymoneygo.DBhelpers.CurrencyDbHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class CurrencyConverter {
	
	public static interface ResultListener<T> {
		void OnSuccess(T data);
		void OnFaiure(int errCode);
	}
	
	private CurrencyDbHelper db;
	private SharedPreferences prefs;
	private String baseCurrency;
	private Context context;
	public CurrencyConverter(CurrencyDbHelper db, SharedPreferences prefs, Context context) {
		this.db = db;
		this.prefs = prefs;	
		this.baseCurrency = prefs.getString("base_currency", "USD");
		this.context = context;
	}
	
	public void getConvertedRate(ResultListener<Float> lstnr, String from, String to) {
		
		ConvertCurrencyTask task = new ConvertCurrencyTask(lstnr,db,context);
		task.execute(from,to,baseCurrency); //to is the default currency
		
	}
	
	static class ConvertCurrencyTask extends AsyncTask<String, Void, Float> {
		
		ResultListener<Float> lstnr; 
		CurrencyDbHelper db;
		static Context context;
		
		ConvertCurrencyTask(ResultListener<Float> lstnr, CurrencyDbHelper db, Context context) {
			this.lstnr = lstnr;
			this.db = db;
			this.context = context;
		}
		
		@Override
		protected Float doInBackground(String... params) {
			float rateDefToBase;
			float rateCurToBase;
			float rate = -1;
			
			rateDefToBase = getRate(params[1], params[2], db);
			if(rateDefToBase == -1)
				return rate;
			rateCurToBase = getRate(params[0], params[2], db);
			if(rateCurToBase == -1)
				return rate;
						
			return (rateCurToBase/rateDefToBase);
			
		}
		
		@Override
		protected void onPostExecute(Float rate) {
			
			if(rate.isNaN() || rate == -1) {
				lstnr.OnFaiure(0);
			} else {
				lstnr.OnSuccess(rate); 
			}
			
		}
		
		private float getRate(String curCode, String base,CurrencyDbHelper db) {
		
			Cursor c= db.getCurrencyById(curCode);
			c.moveToFirst();
			String timeStamp = c.getString(4);
			
			float rate = -1;
			
			if(isTSValid(timeStamp)) {
				rate = Float.parseFloat(c.getString(1));
				return rate;
			}
			
			if(timeStamp.equals("") && !networkPresent()) {
				
				return rate;
			}
			else if(networkPresent()) {
				
				StringBuffer sb = new StringBuffer("http://rate-exchange.appspot.com/currency?from=");
				sb.append(curCode);
				sb.append("&to=");
				sb.append(base);
				
				JSONObject json = getJSONFromURL(sb.toString());
				
				if(json.has("rate")) {
					try {
						rate =Float.parseFloat(json.getString("rate"));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				db.updateCurrency(rate, curCode);
				return rate;
				
			}
			else {
				return rate;
			}
	
		}
		
		private JSONObject getJSONFromURL (String url) {
			
			BufferedReader br;
			JSONObject obj = null;
			
			URL u;
			try {
				u = new URL(url);
			
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			
			br = new BufferedReader(new InputStreamReader(in));
			
			StringBuffer sb = new StringBuffer();
			String s;
			while((s = br.readLine())!= null){
				sb.append(s);
			}
			System.out.println(sb.toString());
			obj = new JSONObject(sb.toString());
			
			return obj;
			
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
	
		}
		
		//method returns true of the timeStamp is less than 12 hours old compared to the current time
		private boolean isTSValid(String timeStamp) {
			
			if(timeStamp.equals("")) { 
				return false;
			}
			
			Calendar cal = Calendar.getInstance();
			Date curDate = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date tsDate = curDate;
			try {
				tsDate = sdf.parse(timeStamp);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//in mili sec
			long diff = curDate.getTime() - tsDate.getTime();
			//convert to hours
			if(diff/(60*60*1000) > 12) {
				return false;
			}

			return true; 
			
		}
		
		public boolean networkPresent() {
		    ConnectivityManager cm =
		        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}
	
	}

}
