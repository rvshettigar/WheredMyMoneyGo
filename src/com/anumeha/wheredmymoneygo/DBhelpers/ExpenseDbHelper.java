package com.anumeha.wheredmymoneygo.DBhelpers;

import java.util.ArrayList;

import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.DBHandler;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class ExpenseDbHelper {
	
	/** Expense Table **/
    private static final String TABLE_EXPENSES	 = "Expenses";

    private static final String KEY_E_ID = "_id";
    private static final String KEY_E_NAME = "e_name";
    private static final String KEY_E_DESC = "e_description";
    private static final String KEY_E_CURRENCY = "e_currency";
    private static final String KEY_E_DATE = "e_date";
    private static final String KEY_E_AMOUNT = "e_amount";
    private static final String KEY_E_CATEGORY1 = "e_category1";
    private static final String KEY_E_CONVRATE = "e_convrate";


	private SQLiteDatabase database;
	private DBHandler dbh;
	private SharedPreferences prefs;
	private float convRate;
	
	
	public ExpenseDbHelper(Context context){
		
		dbh = new DBHandler(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);	
	}
	
	// Adding new expense
    public void addExpense(Expense expense) {
	    database = dbh.getWritableDatabase();

	    System.out.println("Conv Rate gotten as: "+convRate);
	    ContentValues values = new ContentValues();
	    values.put(KEY_E_NAME, expense.getName()); // Expense Name
	    values.put(KEY_E_DESC, expense.getDesc()); // Expense description
	    values.put(KEY_E_DATE, expense.getDate()); // Expense date
	    values.put(KEY_E_CURRENCY, expense.getCurrency()); // Expense currency
	    values.put(KEY_E_AMOUNT, expense.getAmount()); // Expense amount  
	    values.put(KEY_E_CATEGORY1, expense.getCategory1()); // Expense Category1
	    values.put(KEY_E_CONVRATE, expense.get_convToDef()); // Expense conversion to default rate
	    // Inserting Row
	    database.insert(TABLE_EXPENSES, null, values);
	    database.close(); // Closing database connection
    }
    

	//getting all expenses
    public Cursor getAllExpenses() {
    	
    	StringBuffer temp = new StringBuffer();
    	StringBuffer temp2 = new StringBuffer();
    	ArrayList<String> temp1 = new ArrayList<String>();
    	String orderBy = null, selection = null;
    	String [] selectionArgs = null;
    	
    	 SQLiteDatabase db = dbh.getWritableDatabase();
       //  Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_EXPENSES, null);
    	 
    	 //if view as pie, no ordering required
    	 if(prefs.getString("exp_cur_viewAs","").equals("pie") || (prefs.getString("exp_cur_viewAs","").equals("")&&prefs.getString("exp_def_viewAs","").equals("pie"))){
    	 
    		 orderBy = null;
    	 }
    	 else {    		 
    		 temp2.append(prefs.getString("exp_cur_orderBy", "").equals("")? prefs.getString("exp_def_orderBy", ""):prefs.getString("exp_cur_orderBy", ""));   
    		 temp2.append(" ");
    		 temp2.append(prefs.getString("exp_cur_sortOrder", "").equals("")? prefs.getString("exp_def_sortOrder", ""):prefs.getString("exp_cur_sortOrder", ""));
    	 }
    	 
    	 // add selection args if "inrange" is selected
    	 if(prefs.getString("exp_cur_viewBy","").equals("inRange")){
        	 
    		temp.append("date(");
    		temp.append(KEY_E_DATE);
    		temp.append(") >= ? AND date(");
    		temp.append(KEY_E_DATE);
    		temp.append(") <= ?");
    		
    		temp1.add(prefs.getString("exp_startDate",""));
    		temp1.add(prefs.getString("exp_endDate",""));
    		
    		 
    	 }
    	 
    	 if(!prefs.getString("exp_filter","").equals(""))
    	 {
    		 if(temp.length() > 0)
    		 {
    			 temp.append(" AND ");
    		 }
    		 
    		 temp.append(KEY_E_CATEGORY1);
    		 temp.append(" = ?");
    		 temp1.add(prefs.getString("exp_filter","")); // add selection args
    	 }
    	
    	 if(temp.length() > 0) {
    	 
	    	 selection = temp.toString();	    	 
	    	 selectionArgs = (String[]) temp1.toArray();    	 
    	 } 
    	 
    	 if(temp2.length() > 0){
    		 orderBy = temp2.toString();
    	 }
         
         Cursor cursor = db.query(TABLE_EXPENSES, null, selection, selectionArgs, null , null, orderBy);
         // Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_EXPENSES+ " ORDER BY date("+ KEY_E_DATE +") DESC", null);
         
         return cursor;
    	
    }
    
    public Cursor getExpenseById(int id) {
    	
   	 SQLiteDatabase db = dbh.getWritableDatabase();   	 
   	 Cursor c = db.query(TABLE_EXPENSES, null ,KEY_E_ID +" = "+id,null,null,null,null);
    	
    	return c;
    }
    
    public Cursor getExpenseByCategory(String c_name) {
    	
      	 SQLiteDatabase db = dbh.getWritableDatabase();   	 
      	 Cursor c = db.query(TABLE_EXPENSES, null ,KEY_E_CATEGORY1 +" = "+c_name,null,null,null,null);
       	
       	return c;
       }
	

    //update expense
    public void updateExpense(Expense expense, int id) {
      SQLiteDatabase db = dbh.getWritableDatabase();
    		 
  	    ContentValues values = new ContentValues();
  	    values.put(KEY_E_NAME, expense.getName()); // Expense Name
	    values.put(KEY_E_DESC, expense.getDesc()); // Expense description
	    values.put(KEY_E_DATE, expense.getDate()); // Expense date
	    values.put(KEY_E_CURRENCY, expense.getCurrency()); // Expense currency
	    values.put(KEY_E_AMOUNT, expense.getAmount()); // Expense amount  
	    values.put(KEY_E_CATEGORY1, expense.getCategory1()); // Expense Category1
	    values.put(KEY_E_CONVRATE, expense.get_convToDef()); // Expense conversion to default rate
  	 
  	    // Updating Row
  	    db.update(TABLE_EXPENSES, values, KEY_E_ID+"="+ id , null);
  	    db.close(); // Closing database connection
  
    }
    
    //delete expense 
    
    public void deleteExpense(int expId) {
      SQLiteDatabase db = dbh.getWritableDatabase();
 
  	    // Deleting Row
  	    db.delete(TABLE_EXPENSES, KEY_E_ID + "=" + expId, null);
  	    db.close(); // Closing database connection
  
    }

	public Cursor getCategoriesAndExpenses() {
		
		 SQLiteDatabase db = dbh.getWritableDatabase();   	
		 String [] columns = {KEY_E_CATEGORY1,"SUM("+KEY_E_AMOUNT+")"};
		 
	   	 Cursor c = db.query(TABLE_EXPENSES, columns ,null,null,KEY_E_CATEGORY1,null,null);
	    	
	    	return c;
	}
 
}
