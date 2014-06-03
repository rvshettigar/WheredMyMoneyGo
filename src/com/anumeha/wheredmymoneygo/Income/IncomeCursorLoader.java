package com.anumeha.wheredmymoneygo.Income;


import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class IncomeCursorLoader extends CursorLoader {

	IncomeDbHelper db; 
	int incId = -1;
	public IncomeCursorLoader(Context context) {
		super(context);
		
		db = new IncomeDbHelper(context);
		
	}
	
	public IncomeCursorLoader(Context context, int incId) {
		super(context);
		this.incId= incId;
		db = new IncomeDbHelper(context);	
	}

	@ Override
	public Cursor loadInBackground() {

		if(incId == -1){
		return db.getAllIncome();
		}
		
		Cursor c = db.getIncomeById(incId); // if income id is being retrieved
		incId=-1;
		return c;
		
	}
}
