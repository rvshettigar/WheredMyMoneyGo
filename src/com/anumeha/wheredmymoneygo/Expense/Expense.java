package com.anumeha.wheredmymoneygo.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.anumeha.wheredmymoneygo.CashFlow;

public class Expense extends CashFlow{
     
   
    String _category1;
    
     
    // Empty constructor
    public Expense(){
         
    }
    // constructor
    public Expense(int id, String name, String desc, String date, String currency, float amount,  String category1){
       
    	super(id,name,desc,date,currency,amount);
        this._category1 = category1;
        
    }
     
   
	// constructor
    public Expense(String name, String desc, String date, String currency, float amount, String category1){
       
    	super(name,desc,date,currency,amount);
        this._category1 = category1;
         
    }
     
    
    // getting category1
    public String getCategory1(){
        return this._category1;
    }
     
    // setting category1
    public void setCategory1(String category1){
        this._category1 = category1;
    }
    
}