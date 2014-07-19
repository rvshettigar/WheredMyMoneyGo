package com.anumeha.wheredmymoneygo.Income;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.anumeha.wheredmymoneygo.Category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.Currency.CurrencyCursorLoader;
import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.Income.Income;
import com.anumeha.wheredmymoneygo.Income.IncomeCursorLoader;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.anumeha.wheredmymoneygo.Source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class IncomeEditActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
	
	private static Button add, cancel;
	private static TextView incomeDate;
	private static Spinner source,currency;
	private String i_name;
	private static String i_date, i_date_edit;
	private String i_desc;
	private String i_source;
	private String i_currency;
    private static float i_amount;	
    private static ArrayAdapter<String> dataAdapter1, dataAdapter2;
    static String dateFormat;
	boolean loadFinished1 =false;
	boolean loadFinished2 =false;
	boolean loadFinished3 =false;
	
	IncomeDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true, noChanges =true;
	int incId;
	CurrencyConverter conv;
	
	final static int DATE_DIALOG_ID = 999;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.income_edit_activity);
        
	        source = (Spinner)findViewById(R.id.incSourceEdit);
	        currency = (Spinner)findViewById(R.id.inputIncomeCurrencyEdit);
	        incomeDate = (TextView)findViewById(R.id.incomeDateEdit);

	        add = (Button)findViewById(R.id.incSaveEdit);
			add.setOnClickListener(this);
			cancel = (Button)findViewById(R.id.incCancelEdit);
			cancel.setOnClickListener(this);
			dbh = new IncomeDbHelper(this);
			conv = new CurrencyConverter(this);
			
			incId = getIntent().getIntExtra("id",0);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			dateFormat = prefs.getString("def_dateformat", "MM-dd-yyyy");
			getLoaderManager().initLoader(1,null, this ); //load sources
			getLoaderManager().initLoader(3,null, this ); //get income with that id.
			getLoaderManager().initLoader(5,null, this ); //get currencies
			 
	 
	    }
	 
	 public void endActivity(String res) {	
		 
		 Intent data = new Intent();
		 data.putExtra("result",res);
		  // Activity finished ok, return the data
		  setResult(RESULT_OK, data);	 		 
			this.finish();			
		}
	 
	 @Override
	 public void onClick(View arg0) {
		 
		 StringBuffer sb = new StringBuffer("Please check the following :\n");
			valid = true;
			noChanges = true;
			
			if(arg0.getId() == R.id.incCancelEdit) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.incSaveEdit) {
				
				//name
				String i_name_edit = ((EditText)findViewById(R.id.inputIncomeNameEdit)).getText().toString();
				if(i_name_edit.trim().equals("")){
					sb.append("- Income Name cannot be blank. \n");
					valid = false;
				}
				if(noChanges && !i_name_edit.trim().equals(i_name.trim())){
					noChanges = false;
					
				}
				
				if(!i_date.equals(i_date_edit.trim())){
					noChanges = false;
					
				}
				
				//amount
				float amount = 0;
				String i_amount_edit = ((EditText)findViewById(R.id.inputIncomeAmountEdit)).getText().toString();
				if(i_amount_edit.trim().equals("")){
					sb.append("- Income Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(i_amount_edit);
						
						if(noChanges && amount != i_amount){
							noChanges = false;
							
						}
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				String i_currency_edit =((Spinner) findViewById(R.id.inputIncomeCurrencyEdit)).getSelectedItem().toString(); 
				if(noChanges && !i_currency_edit.trim().equals(i_currency)){
					noChanges = false;
				
				}
				
				//source
				String i_source_edit = ((Spinner) findViewById(R.id.incSourceEdit)).getSelectedItem().toString(); 
				if(noChanges && !i_source_edit.trim().equals(i_source)){
					noChanges = false;
				
				}
				
				String i_desc_edit = ((EditText)findViewById(R.id.inputIncomeDescEdit)).getText().toString();
				if(i_desc.trim().equals("")) {
					i_desc =" ";
				}
				if(noChanges && !i_desc_edit.trim().equals(i_desc.trim())){
					noChanges = false;
					
				}
				
				
				if(valid && !noChanges) {	
					if(!i_currency_edit.equals(i_currency)) {
					conv.getConvertedRate(new CurrencyConverter.ResultListener<Float>() {	
	 					@Override
	 					public void OnSuccess(Float rate) {
	 						endActivity("edited");
	 					}	
	 					@Override
	 					public void OnFaiure(int errCode) {
	 						endActivity("edited");
	 					}  },new Income(incId,i_name_edit,i_desc_edit,i_date_edit,i_currency_edit,amount,i_source),true); 
					} else {
						dbh.updateIncome(new Income(i_name_edit,i_desc_edit,i_date_edit,i_currency_edit,amount,i_source),incId);
						endActivity("edited");
					}
				}
				
				else
				{
					String title = "Invalid Entries";
					if(noChanges){
						title = "No Changes";
						sb = new StringBuffer("No changes were made");
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setTitle(title)
			        .setMessage(sb.toString())
			        .setCancelable(false)
			        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();
					
				}				
			}	 
	 }

	 public void showDatePickerDialog(View view) {
			DialogFragment newFragment = new SelectDateFragment();
			newFragment.show(getFragmentManager(), "DatePicker");
	 }
	
	 public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}
				 
		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
					
			Date myDate;
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.MONTH, mm);
	        cal.set(Calendar.DATE, dd);
	        cal.set(Calendar.YEAR, yy);
	        myDate = cal.getTime();
	        //set Date into SQLIte date format
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	        i_date_edit = dateFormat.format(myDate);
	        dateFormat = new SimpleDateFormat("MMMM dd, yyyy",Locale.ENGLISH);     
			incomeDate.setText(dateFormat.format(myDate));
				}
			}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
	    if(id == 1)
			return new SourceCursorLoader(this);
	    else if (id == 5) 
	    	return new CurrencyCursorLoader(this);
	    else
			return new IncomeCursorLoader(this,incId);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		
		if(arg0.getId() == 1) {
		//cursor has category data
		loadSpinners(c);
		loadFinished2 =true;
		
		} else if(arg0.getId() == 5) {
			loadCurrencies(c);
			loadFinished3 = true;
		}
		else {
			
		c.moveToFirst(); 
				
				i_name = c.getString(1);
				i_desc =  c.getString(2);
				i_date = c.getString(3);
				i_date_edit = i_date;
			
				String tempdate="";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat);
				try {
					tempdate = sdf1.format((sdf.parse(i_date))); //show the date in user specified format
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				i_currency =  c.getString(4);
				i_amount = c.getFloat(5);
				i_source = c.getString(6);
				
				((EditText)findViewById(R.id.inputIncomeNameEdit)).setText(i_name);
				((EditText)findViewById(R.id.inputIncomeAmountEdit)).setText(Float.toString(i_amount));
				((EditText)findViewById(R.id.inputIncomeDescEdit)).setText(i_desc);
				//((EditText)findViewById(R.id.inputIncomeCurrencyEdit)).setText(i_currency);
				((TextView)findViewById(R.id.incomeDateEdit)).setText(tempdate);
				
				
				loadFinished1= true;

		}
		
		if(loadFinished1 && loadFinished2&& loadFinished3){
			
			int spinnerPosition = dataAdapter1.getPosition(i_source);
			((Spinner) findViewById(R.id.incSourceEdit)).setSelection(spinnerPosition);	
			spinnerPosition = dataAdapter2.getPosition(i_currency);
			((Spinner) findViewById(R.id.inputIncomeCurrencyEdit)).setSelection(spinnerPosition);	
			loadFinished1 =false;
			loadFinished2 = false;	
			loadFinished3 = false;
			
		}
			
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadSpinners(Cursor c)
	{	
		List<String> names = new ArrayList<String>();

		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(1));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        source.setAdapter(dataAdapter1);  // attaching data adapter to category spinner
        
	}
	private void loadCurrencies(Cursor c)
	{	
		List<String> names = new ArrayList<String>();

		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(0));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        currency.setAdapter(dataAdapter2);  // attaching data adapter to category spinner
        
	}

}
