package com.anumeha.wheredmymoneygo.Services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.Income.Income;
import com.example.wheredmymoneygo.R;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.TextView;

public class AlarmOps {

	private ExpenseDbHelper expDb;
	private IncomeDbHelper incDb;
	boolean isIncome;
	
	public static interface OnAlarmOpsCompleted {
		void OnSuccess();
		void OnFaiure();
	}
	
	public AlarmOps(Context ctx, boolean isIncome) {
		this.isIncome = isIncome;
		if(!isIncome) {
			expDb = new ExpenseDbHelper(ctx); 
		}
		else{
			incDb = new IncomeDbHelper(ctx);
		}
	}
	
	public void addRecToDb(OnAlarmOpsCompleted lstnr,long id) {
		
		if(isIncome) {
			AlarmOpsTask task = new AlarmOpsTask(lstnr,incDb);
			task.execute(id);
		} else {
			AlarmOpsTask task = new AlarmOpsTask(lstnr,expDb);
			task.execute(id);
		}
		
	}
	
	static class AlarmOpsTask extends AsyncTask<Long, Void, Long> { 
		private ExpenseDbHelper expDb;
		private IncomeDbHelper incDb;
		boolean isIncome;
		OnAlarmOpsCompleted lstnr;
		
		public AlarmOpsTask(OnAlarmOpsCompleted lstnr, ExpenseDbHelper expDb ){
			this.expDb = expDb;
			isIncome = false;
			this.lstnr = lstnr;
		}
		
		public AlarmOpsTask(OnAlarmOpsCompleted lstnr,IncomeDbHelper incDb ){
			this.incDb = incDb;
			isIncome = true;
			this.lstnr = lstnr;
		}

		@Override
		protected Long doInBackground(Long... id) {
			long oldId = id[0];
			String oldDate, newDate;
			long newId = -1;
			if(isIncome) {
				Cursor c = incDb.getIncomeById(oldId);
				c.moveToFirst();
				oldDate = c.getString(3);
				newDate = getCurrentDate();
				Income i = new Income(c.getString(1),c.getString(2),newDate,c.getString(4),c.getFloat(5),c.getString(6),c.getString(7),c.getString(8).equals("yes"));
				incDb.updateIncome(i,oldId);
				i.setDate(oldDate); 
				i.setFreq("Do not repeat");
				newId = incDb.addIncome(i);				
			} else {
				Cursor c = expDb.getExpenseById(oldId);
				c.moveToFirst();
				oldDate = c.getString(3);
				newDate = getCurrentDate();
			
				Expense e = new Expense(c.getString(1),c.getString(2),newDate,c.getString(4),c.getFloat(5),c.getString(6),c.getString(7),c.getString(8).equals("yes"));
				expDb.updateExpense(e,oldId);
				e.setDate(oldDate); 
				e.setFreq("Do not repeat");
				newId = expDb.addExpense(e);	
			}
			return newId;
		}
		public String getCurrentDate() {			 
		    Calendar cal = Calendar.getInstance();	    
		    Date  myDate = cal.getTime();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		    String date = sdf.format(myDate);
		    return date;
		 }
		@Override
		protected void onPostExecute(Long id) {
			
			if(id == -1) {
				lstnr.OnFaiure();
			} else {
				lstnr.OnSuccess(); 
			}
			
		}
	}
}
