package com.anumeha.wheredmymoneygo;

import com.anumeha.wheredmymoneygo.Category.Category;
import com.anumeha.wheredmymoneygo.Currency.Currency;
import com.anumeha.wheredmymoneygo.Source.Source;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper{
	/** class variables  - database**/
	private static final int DATABASE_VERSION = 1;	  
	private static final String DATABASE_NAME = "WMMGDatabase";
	
	/** Expense Table **/
    private static final String TABLE_EXPENSES	 = "Expenses";

    private static final String KEY_E_ID = "_id";
    private static final String KEY_E_NAME = "e_name";
    private static final String KEY_E_DESC = "e_description";
    private static final String KEY_E_CURRENCY = "e_currency";
    private static final String KEY_E_DATE = "e_date";
    private static final String KEY_E_AMOUNT = "e_amount";
    private static final String KEY_E_CATEGORY1 = "e_category1";
    
    /** Income Table **/
    private static final String TABLE_INCOME	 = "Income";

    private static final String KEY_I_ID = "_id";
    private static final String KEY_I_NAME = "i_name";
    private static final String KEY_I_DESC = "i_description";
    private static final String KEY_I_CURRENCY = "i_currency";
    private static final String KEY_I_DATE = "i_date";
    private static final String KEY_I_AMOUNT = "i_amount";
    private static final String KEY_I_SOURCE = "i_source";
    
    /** Category Table **/
    private static final String TABLE_CATEGORY	 = "Category";
 
    private static final String KEY_C_ID = "_id";
    private static final String KEY_C_NAME = "c_name";
    private static final String KEY_C_BUDGET = "c_budget";
    private static final String KEY_C_FREQUENCY = "c_frequency";
        
    /** Sources Table **/
    private static final String TABLE_SOURCE	 = "Source";
 
    private static final String KEY_S_ID = "_id";
    private static final String KEY_S_NAME = "s_name";
    
    /** Recurrences Table**/
    private static final String TABLE_RECURRENCE = "Recurrences";
    
    private static final String KEY_R_ID = "_id";
    private static final String KEY_R_IS_EI = "expense_or_income";
    private static final String KEY_R_EI_ID = "ei_id"; //  income or expense ID
    private static final String KEY_R_FREQUENCY = "frequency"; //one time - weekly -monthly or annual
    private static final String KEY_R_DATE = "recurrence_date";
    
    /** Currency Table**/
    private static final String TABLE_CURRENCY = "Currency";
    
    private static final String KEY_Cu_ID = "_id";
    private static final String KEY_Cu_CONVRATE = "conversion_rate"; //  rate of conversion to base currency
    private static final String KEY_Cu_COUNTRY = "Country"; //
    private static final String KEY_Cu_SYMBOL = "symbol";
    private static final String KEY_Cu_TS = "timestamp";
    
    /** Alerts Table**/
    private static final String TABLE_ALERTS = "Alerts"; // basically a reminder to pay bills etc
    
    private static final String KEY_A_ID = "_id";
    private static final String KEY_A_TEXT = "alert_text"; 
    private static final String KEY_A_FREQUENCY = "frequency"; //one time - weekly -monthly or annual
    private static final String KEY_A_DATE = "recurrence_date";
 

	 public DBHandler(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	 
	 /** Creating tables **/
	 
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	
	    	//Expense Table
	        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
	                + KEY_E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_E_NAME + " TEXT, "
	                + KEY_E_DESC + " TEXT, " 
	        		+ KEY_E_DATE + " datetime, " 
	        		+ KEY_E_CURRENCY + " TEXT, " 
	                + KEY_E_AMOUNT + " REAL, "
	                + KEY_E_CATEGORY1 + " TEXT"+")";
	        db.execSQL(CREATE_EXPENSES_TABLE);
	        
	      //Income Table
	        String CREATE_INCOME_TABLE = "CREATE TABLE " + TABLE_INCOME + "("
	                + KEY_I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_I_NAME + " TEXT, "
	                + KEY_I_DESC + " TEXT, " 
	        		+ KEY_I_DATE + " datetime, " 
	        		+ KEY_I_CURRENCY + " TEXT, " 
	                + KEY_I_AMOUNT + " REAL, "
	                + KEY_I_SOURCE + " TEXT"+")";
	        db.execSQL(CREATE_INCOME_TABLE);
	        	        
	        //Category Table     
	        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
	                + KEY_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_C_NAME + " TEXT," 
	                + KEY_C_BUDGET + " REAL," 
	        		+ KEY_C_FREQUENCY + " TEXT"
	                + ")";
	        db.execSQL(CREATE_CATEGORY_TABLE);
	        
	      //Sources Table     
	        String CREATE_SOURCE_TABLE = "CREATE TABLE " + TABLE_SOURCE + "("
	                + KEY_S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_S_NAME + " TEXT" + ")";
	        db.execSQL(CREATE_SOURCE_TABLE);
	        
	      //Recurrence Table    
	        String CREATE_RECURRENCE_TABLE = "CREATE TABLE " + TABLE_RECURRENCE + "("
	                + KEY_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_R_IS_EI + " TEXT," 
	                + KEY_R_EI_ID + " INTEGER," 
	        		+ KEY_R_FREQUENCY + " TEXT,"
	                + KEY_R_DATE + " datetime" + ")";
	        db.execSQL(CREATE_RECURRENCE_TABLE);
	        
	        //Currency Table    
	        String CREATE_CURRENCY_TABLE = "CREATE TABLE " + TABLE_CURRENCY + "("
	                + KEY_Cu_ID + " TEXT PRIMARY KEY," 
	        		+ KEY_Cu_CONVRATE + " REAL," 
	                + KEY_Cu_COUNTRY + " TEXT," 
	        		+ KEY_Cu_SYMBOL + " TEXT,"
	                + KEY_Cu_TS + " TEXT" + ")";
	        db.execSQL(CREATE_CURRENCY_TABLE);
	        
	        //Alerts Table     -------- next release
/**	        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_ALERTS + "("
	                + KEY_A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_A_TEXT + " TEXT," 
	        		+ KEY_A_FREQUENCY + " TEXT,"
	                + KEY_A_DATE + " datetime" + ")";
	        db.execSQL(CREATE_ALERTS_TABLE);
**/
	        
	        
	      populateCategories(db); //pre populate the categories
	      populateSources(db); //pre populate the sources
	      populateCurrency(db); //pre populate the currency
	    }
	    
	    // Upgrading database
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRENCE);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTS);
	 
	        // Create tables again
	        onCreate(db);
	    }
	    
	    private void populateCategories(SQLiteDatabase db) {
	    	Category category;
	    	
	    	
	    	category = new Category ("Food",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Entertainment",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Utilities",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Rent",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Electricity",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Other",-1f,"");
	    	addCategory(category,db);
	    	
	    	System.out.println("populating categories");
	    	
	    }
	    
	    private void populateCurrency(SQLiteDatabase db) {
	    	Currency cur;
	    	
	    	
	    	cur = new Currency ("USD",1,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("EUR",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("JPY",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("AUD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CAD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("SGD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CHF",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("INR",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CNY",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("HKD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("AED",0,"","","");
	    	addCurrency(cur,db);
	    	
	    	System.out.println("populating categories");
	    	
	    }
	    
	    private void populateSources(SQLiteDatabase db) {
	    	Source source;
 	
	    	source = new Source("Other");
	    	addSource(source,db);
	    	
	    	System.out.println("populating source");
	    	
	    }
	    
	    public void addCategory(Category category, SQLiteDatabase db) {
			 
		 
			 ContentValues values = new ContentValues();
			 values.put(KEY_C_NAME, category.getName()); // Category Name
			 values.put(KEY_C_BUDGET, category.getBudget()); // Category budget
			 values.put(KEY_C_FREQUENCY, category.getFrequency()); // Category frequency
			 
			 // Inserting Row
			 db.insert(TABLE_CATEGORY, null, values);
			
			 
		 }
	    
	    public void addCurrency(Currency cur, SQLiteDatabase db) {
			 
			
			 ContentValues values = new ContentValues();
			 values.put(KEY_Cu_ID , cur.get_id()); // Currency code eg USD - mandatory
			 values.put(KEY_Cu_CONVRATE, cur.getConversionRate()); // Currency conversion rate
			 values.put(KEY_Cu_COUNTRY, cur.getCountry()); // Currency country
			 values.put(KEY_Cu_SYMBOL, cur.getConversionRate()); // Currency symbol - $
			 values.put(KEY_Cu_TS, cur.getCountry()); // Currency country 
			 
			 // Inserting Row
			 db.insert(TABLE_CURRENCY, null, values);
			
			 
		 }
	    
	    
	    public void addSource(Source source, SQLiteDatabase db) {
	    	
	    	ContentValues values = new ContentValues();
			 values.put(KEY_S_NAME, source.getName()); //Source Name
			 
			 // Inserting Row
			 db.insert(TABLE_SOURCE, null, values);
			
			 
		 }
	    
	    

}
