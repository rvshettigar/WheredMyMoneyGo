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
import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.Income.Income;

import android.app.ProgressDialog;
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
	private Context context;
	public CurrencyConverter(CurrencyDbHelper db, SharedPreferences prefs, Context context) {
		this.db = db;
		this.prefs = prefs;	
		this.context = context;
	}
	
	public void getConvertedRate(ResultListener<Float> lstnr, String from, Expense e) {
		
		String to = prefs.getString("def_currency","USD" );
		ConvertCurrencyTask task = new ConvertCurrencyTask(lstnr,db,context, e);
		task.execute(from,to); //to is the default currency
		
	}
	
	public void getConvertedRate(ResultListener<Float> lstnr, String from, Income i) {
		
		String to = prefs.getString("def_currency","USD" );
		ConvertCurrencyTask task = new ConvertCurrencyTask(lstnr,db,context, i);
		task.execute(from,to); //to is the default currency
		
	}
	
	static class ConvertCurrencyTask extends AsyncTask<String, Void, Float> {
		
		ResultListener<Float> lstnr; 
		CurrencyDbHelper db;
		ExpenseDbHelper expDb ;
		static Context context;
		Expense e;
		Income i;
		Boolean isExpense = true;
		ProgressDialog pd;
		
		ConvertCurrencyTask(ResultListener<Float> lstnr, CurrencyDbHelper db, Context context, Expense e) {
			this.lstnr = lstnr;
			this.db = db;
			this.context = context;
			this.e = e;
			expDb = new ExpenseDbHelper(context);
		}
		
		ConvertCurrencyTask(ResultListener<Float> lstnr, CurrencyDbHelper db, Context context, Income i) {
			this.lstnr = lstnr;
			this.db = db;
			this.context = context;
			this.i = i;
		}
		
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.show();
		}
		
		@Override
		protected Float doInBackground(String... params) {
			
			Float rate = getRate(params[0],params[1],db);	
			
			if(rate.isNaN() || rate == -1) {
				e.set_convToDef(0);	
			} else {
				e.set_convToDef(rate);
			}
			
			expDb.addExpense(e);
			return rate;
			
		}
		
		@Override
		protected void onPostExecute(Float rate) {
			
			if(rate.isNaN() || rate == -1) {
				pd.dismiss();
				lstnr.OnFaiure(0);
			} else {
				pd.dismiss();
				lstnr.OnSuccess(rate); 
			}
			
		}
		
		private float getRate(String curCode, String base,CurrencyDbHelper db) {
		
			if(curCode.equals(base)) {
				return 1;
			}
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
				
				StringBuffer sb = new StringBuffer("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22");
				sb.append(curCode);
				sb.append(base);
				sb.append("%22)&format=json&env=store://datatables.org/alltableswithkeys");
				
				JSONObject json = getJSONFromURL(sb.toString());
				
				
				if(json!=null) {
					try {
						JSONObject jsonObj =  ((json.getJSONObject("query")).getJSONObject("results")).getJSONObject("rate");
						if(jsonObj != null && jsonObj.has("Rate")) {
							
							rate =Float.parseFloat(jsonObj.getString("Rate")); 
							System.out.println("rate is"+rate);
						}
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
