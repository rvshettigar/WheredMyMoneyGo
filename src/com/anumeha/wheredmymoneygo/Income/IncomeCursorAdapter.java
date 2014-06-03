package com.anumeha.wheredmymoneygo.Income;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.wheredmymoneygo.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class IncomeCursorAdapter extends ResourceCursorAdapter {
	
SharedPreferences prefs;
	
	public IncomeCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		String dateFormat, currency;
		Date tempDate = null;
		SimpleDateFormat sdf;		
		
		//set income id -- invisible but will be used for editing
		TextView e_Id = (TextView)view.findViewById(R.id.incomeId);
		e_Id.setText(cursor.getString(0));
		
		//set income name
		TextView e_Name = (TextView)view.findViewById(R.id.incomeName);
		e_Name.setText(cursor.getString(1));
		
		//set income date--after adapting it to the user's prefered format
		TextView e_date = (TextView)view.findViewById(R.id.incomeDate);		
		//adapt date to user's preference 
		dateFormat = prefs.getString("def_dateformat", "MMMM dd, yyyy");
		sdf = new SimpleDateFormat("yyyy-MM-dd"); // the standard format for storage
		try {
			tempDate = sdf.parse(cursor.getString(3));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		sdf = new SimpleDateFormat(dateFormat);
		e_date.setText(sdf.format(tempDate)); 
		
		//show amount after appending currency symbol
		TextView e_Amount = (TextView)view.findViewById(R.id.incomeAmount);
		currency = cursor.getString(4);
		e_Amount.setText(currency + " "+cursor.getString(5));
		
		//set the source
		TextView e_Category = (TextView)view.findViewById(R.id.incomeSource);
		e_Category.setText(cursor.getString(6));
		
		
	
	}

}
