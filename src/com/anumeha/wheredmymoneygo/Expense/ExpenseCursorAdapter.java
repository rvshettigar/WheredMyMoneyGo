package com.anumeha.wheredmymoneygo.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.anumeha.wheredmymoneygo.DBhelpers.CurrencyDbHelper;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.example.wheredmymoneygo.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class ExpenseCursorAdapter extends ResourceCursorAdapter{

	SharedPreferences prefs;
	CurrencyConverter conv; 
	CurrencyDbHelper db;
	String defaultCurrency, baseCurrency;
	
	public ExpenseCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		defaultCurrency = prefs.getString("def_currency", "USD");
		baseCurrency = prefs.getString("base_currency", "USD");
		db = new CurrencyDbHelper(context);
		conv = new CurrencyConverter(db,prefs,context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		final String dateFormat, currency;
		Date tempDate = null;
		SimpleDateFormat sdf;		
		
		//set expense id -- invisible but will be used for editing
		TextView e_Id = (TextView)view.findViewById(R.id.expenseId);
		e_Id.setText(cursor.getString(0));
		String id = cursor.getString(0);
		
		
		//set expense name
		TextView e_Name = (TextView)view.findViewById(R.id.expenseName);
		e_Name.setText(cursor.getString(1));
		
		//set expense date--after adapting it to the user's prefered format
		TextView e_date = (TextView)view.findViewById(R.id.expenseDate);		
		//adapt date to user's preference 
		dateFormat = prefs.getString("def_dateformat", "MMMM dd, yyyy");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			tempDate = sdf.parse(cursor.getString(3));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		sdf = new SimpleDateFormat(dateFormat);
		e_date.setText(sdf.format(tempDate)); 
		
		//show amount after appending currency symbol
		currency = cursor.getString(4);
	    final TextView e_Amount = (TextView)view.findViewById(R.id.expenseAmount);
		if(prefs.getString("exp_conv", "off").equals("off") || currency.equals(defaultCurrency)) { //conversion not required
			
			e_Amount.setText(currency + " "+cursor.getString(5));
		} else { //conversion to default
			
			final float amount = Float.parseFloat(cursor.getString(5));
			
			conv.getConvertedRate(new CurrencyConverter.ResultListener<Float>() {

				@Override
				public void OnSuccess(Float rate) {
					
					e_Amount.setText(defaultCurrency + " " + amount*rate);
					System.out.println("Success called" );
				}

				@Override
				public void OnFaiure(int errCode) {
					
					e_Amount.setText(currency + " " + amount);
					e_Amount.setTextColor(Color.RED);
					
				}  },currency,defaultCurrency);
			
		}
		
		//set the category
		TextView e_Category = (TextView)view.findViewById(R.id.expenseCategory);
		e_Category.setText(cursor.getString(6));
		
		
	
	}

}
