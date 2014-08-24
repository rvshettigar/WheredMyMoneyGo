package com.anumeha.wheredmymoneygo.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.anumeha.wheredmymoneygo.Category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.Currency.CurrencyCursorLoader;
import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.example.wheredmymoneygo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ExpenseEditActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
	
	private static Button add, cancel;
	private static TextView expenseDate;
	private static Spinner category1, currency, frequency;
	private String e_name;
	private static String e_date, e_date_edit;
	private String e_desc;
	private String e_category1;
	private String e_currency;
	private int e_freq;
	private boolean e_notify;
    private static float e_amount;	
    private static ArrayAdapter<String> dataAdapter1, dataAdapter2;
    private ArrayAdapter<CharSequence> freqadapter;
    static String dateFormat;
    private CheckBox ask;
	boolean loadFinished1 =false;
	boolean loadFinished2 =false;
	boolean loadFinished3 =false;
	
	ExpenseDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true, noChanges =true;
	int expId;
	
	CurrencyConverter convFrag;
	final static int DATE_DIALOG_ID = 999;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.expense_edit_activity);
	        
	        category1 = (Spinner)findViewById(R.id.expCategory1Edit);
	        currency = (Spinner)findViewById(R.id.inputExpenseCurrencyEdit);
	        frequency = (Spinner)findViewById(R.id.inputExpenseFreqEdit);
	        
	        freqadapter = ArrayAdapter.createFromResource(this,
			        R.array.frequency_spinner_items, android.R.layout.simple_spinner_item);
			freqadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			frequency.setAdapter(freqadapter);

	        ask = (CheckBox)findViewById(R.id.inputExpNotifyEdit); 
	        expenseDate = (TextView)findViewById(R.id.expenseDateEdit);
	        add = (Button)findViewById(R.id.expSaveEdit);
			add.setOnClickListener(this);
			cancel = (Button)findViewById(R.id.expCancelEdit);
			cancel.setOnClickListener(this);
			dbh = new ExpenseDbHelper(this);
			FragmentManager fragmentManager = getFragmentManager();
			convFrag = (CurrencyConverter) fragmentManager                
			                      .findFragmentByTag(CurrencyConverter.TAG);
			
			if (convFrag == null) {
	            convFrag = new CurrencyConverter();
	            fragmentManager.beginTransaction().add(convFrag,
	                    CurrencyConverter.TAG).commit();
	        }
			
			expId = getIntent().getIntExtra("id",0);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			dateFormat = prefs.getString("def_dateformat", "MM-dd-yyyy");
			getLoaderManager().initLoader(1,null, this ); //load categories
			getLoaderManager().initLoader(0,null, this ); //get expense with that id.
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
			
			if(arg0.getId() == R.id.expCancelEdit) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.expSaveEdit) {
				
				//name
				String e_name_edit = ((EditText)findViewById(R.id.inputExpenseNameEdit)).getText().toString();
				if(e_name_edit.trim().equals("")){
					sb.append("- Expense Name cannot be blank. \n");
					valid = false;
				}
				if(noChanges && !e_name_edit.trim().equals(e_name.trim())){
					noChanges = false;
					
				}
				
				if(!e_date.equals(e_date_edit.trim())){
					noChanges = false;
					
				}
				
				//amount
				float amount = 0;
				String e_amount_edit = ((EditText)findViewById(R.id.inputExpenseAmountEdit)).getText().toString();
				if(e_amount_edit.trim().equals("")){
					sb.append("- Expense Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(e_amount_edit);
						
						if(noChanges && amount != e_amount){
							noChanges = false;
							
						}
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				String e_currency_edit = ((Spinner) findViewById(R.id.inputExpenseCurrencyEdit)).getSelectedItem().toString(); 
				if(noChanges && !e_currency_edit.trim().equals(e_currency)){
					noChanges = false;
				
				}
				
				//category 1
				String e_category1_edit = ((Spinner) findViewById(R.id.expCategory1Edit)).getSelectedItem().toString(); 
				if(noChanges && !e_category1_edit.trim().equals(e_category1)){
					noChanges = false;
				
				}
				
				String e_desc_edit = ((EditText)findViewById(R.id.inputExpenseDescEdit)).getText().toString();
				if(e_desc.trim().equals("")) {
					e_desc =" ";
				}
				if(noChanges && !e_desc_edit.trim().equals(e_desc.trim())){
					noChanges = false;
					
				}
				String e_freq_edit = frequency.getSelectedItem().toString();
				if(frequency.getSelectedItemPosition()!= e_freq) noChanges = false;
				
				boolean e_notify_edit = ask.isChecked();
				if(e_notify_edit != e_notify) noChanges = false;
				
				
				if(valid && !noChanges) {	
					if(!e_currency_edit.equals(e_currency)) {
						convFrag.getConvertedRate(new CurrencyConverter.ResultListener<Long>() {	
		 					@Override
		 					public void OnSuccess(Long rate) {
		 						endActivity("edited");
		 					}	
		 					@Override
		 					public void OnFaiure(int errCode) {
		 						endActivity("edited");
		 					}  },new Expense(expId,e_name_edit,e_desc_edit,e_date_edit,e_currency_edit,amount,e_category1_edit,e_freq_edit,e_notify_edit),true); 
					} else {
						dbh.updateExpense(new Expense(e_name_edit,e_desc_edit,e_date_edit,e_currency_edit,amount,e_category1_edit,e_freq_edit,e_notify_edit),expId);
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
	        e_date_edit = dateFormat.format(myDate);
	        dateFormat = new SimpleDateFormat("MMMM dd, yyyy",Locale.ENGLISH);     
			expenseDate.setText(dateFormat.format(myDate));
				}
			}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
	    if(id == 1)
			return new CategoryCursorLoader(this);
	    else if (id == 5) 
	    	return new CurrencyCursorLoader(this);
	    else
			return new ExpenseCursorLoader(this,expId,2);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		
		if(arg0.getId() == 1) {
		//cursor has category data
		loadSpinners(c);
		loadFinished2 =true;
		
		}
		else if(arg0.getId() == 5) {
			loadCurrencies(c);
			loadFinished3 = true;
		}
		else {
			
		c.moveToFirst(); 
				
				e_name = c.getString(1);
				e_desc =  c.getString(2);
				e_date = c.getString(3);
				e_date_edit = e_date; //dates are in the format yyyy-MM-dd for storage
			
				String tempdate="";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat);
				try {
					tempdate = sdf1.format((sdf.parse(e_date))); //show the date in user specified format
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				e_currency =  c.getString(4);
				e_amount = c.getFloat(5);
				e_category1 = c.getString(6);
				String freq = c.getString(7);
				for(int i =0;i<freqadapter.getCount();i++) {
					if(freq.equals(freqadapter.getItem(i))) {
						frequency.setSelection(i);
						e_freq = i;
						break;
					}
				}
				String askTemp = c.getString(8);
				if(askTemp.equals("yes")) {
					ask.setChecked(true);
					e_notify = true;
				}
	
				
				((EditText)findViewById(R.id.inputExpenseNameEdit)).setText(e_name);
				((EditText)findViewById(R.id.inputExpenseAmountEdit)).setText(Float.toString(e_amount));
				((EditText)findViewById(R.id.inputExpenseDescEdit)).setText(e_desc);
				//((EditText)findViewById(R.id.inputExpenseCurrencyEdit)).setText(e_currency);
				((TextView)findViewById(R.id.expenseDateEdit)).setText(tempdate);
				
				
				loadFinished1= true;

		}
		
		if(loadFinished1 && loadFinished2 && loadFinished3){ 
			
			int spinnerPosition = dataAdapter1.getPosition(e_category1);
			((Spinner) findViewById(R.id.expCategory1Edit)).setSelection(spinnerPosition);	
			loadFinished1 =false;
			loadFinished2 = false;	
			
			
			spinnerPosition = dataAdapter2.getPosition(e_currency);
			((Spinner) findViewById(R.id.inputExpenseCurrencyEdit)).setSelection(spinnerPosition);	
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
        category1.setAdapter(dataAdapter1);  // attaching data adapter to category spinner
        
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
