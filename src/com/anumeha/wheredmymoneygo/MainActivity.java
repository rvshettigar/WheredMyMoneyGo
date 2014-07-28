package com.anumeha.wheredmymoneygo;

import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.Category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.Currency.CurrencyCursorLoader;
import com.anumeha.wheredmymoneygo.Expense.ExpenseListFragment;
import com.anumeha.wheredmymoneygo.Expense.ExpensePieFragment;
import com.anumeha.wheredmymoneygo.Income.IncomeListFragment;
import com.anumeha.wheredmymoneygo.Source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends FragmentActivity implements OnClickListener, OnItemSelectedListener, LoaderCallbacks<Cursor>{


	private static String currentTab ;
	private static Spinner sortOrder,filters;
	private int INCOME_ADDED = 00;
	private int EXPENSE_ADDED = 10;
	private static int EDIT_INCOME = 01; //0 FOR INCOME 1 FOR EDIT	
	private static String EXPENSE_TAG = "expense";
	private static String INCOME_TAG = "income";
	
	private Button convert,listPie;
	private MyTabListener expenseTab,incomeTab,IvETab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setDefaults();
		filters = (Spinner)findViewById(R.id.filter);
		filters.setOnItemSelectedListener(this);
		
		convert = (Button)findViewById(R.id.convertCur);
		convert.setOnClickListener(this);
	
		listPie = (Button)findViewById(R.id.listPie);
		listPie.setOnClickListener(this);
		
		sortOrder = (Spinner)findViewById(R.id.sortOrder);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.sort_spinner_items, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sortOrder.setAdapter(adapter);
		sortOrder.setOnItemSelectedListener(this);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// add the income, expense and income vs expense tabs
		expenseTab = new MyTabListener(this,EXPENSE_TAG,ExpenseListFragment.class);
		incomeTab = new MyTabListener(this,INCOME_TAG,IncomeListFragment.class);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_expensetab).setTabListener(expenseTab));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_incometab).setTabListener(incomeTab));
	//	actionBar.addTab(actionBar.newTab().setText(R.string.title_expvsinctab).setTabListener(new TwoFragTabListener(this, "tag5", "evi", ExpenseOptionsFragment.class, ExpenseListFragment.class )));
		
		getLoaderManager().initLoader(1,null, this ); // 1 for category
		getLoaderManager().initLoader(2,null, this ); // 2 for sources
	}
	
	@Override
	  public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Restore the previously serialized current tab position.
	    if (savedInstanceState.containsKey("Navigation_item_state")) {
	      getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("Navigation_item_state"));
	    }
	  }

	  @Override
	  public void onSaveInstanceState(Bundle outState) {
	    // Serialize the current tab position.
	    outState.putInt("Navigation_item_state", getActionBar()
	        .getSelectedNavigationIndex());
	  }

	

	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			Intent intent;
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.action_categories:
		        	//define a new Intent for the add expense form Activity
					 intent = new Intent(this,com.anumeha.wheredmymoneygo.Category.CategoryActivity.class);
			 
					//start the add expense form Activity
					this.startActivity(intent);
		            return true;
		            
		        case R.id.action_sources:
		        	//define a new Intent for the add expense form Activity
					 intent = new Intent(this,com.anumeha.wheredmymoneygo.Source.SourceActivity.class);
			 
					//start the add expense form Activity
					this.startActivity(intent);
		            return true;
		            
		        case R.id.action_add:
		        	//define a new Intent for the add expense form Activity
		        	if(currentTab.equals("income")) {
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Income.IncomeAddActivity.class);
		        		this.startActivityForResult(intent, INCOME_ADDED);
		        	}
		        	else if(currentTab.equals("expense")){
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Expense.ExpenseAddActivity.class);
		        		this.startActivityForResult(intent, EXPENSE_ADDED);
		        	}			 
					
		            return true;
		            
		        case android.R.id.home:
		        	  Intent intent1 = new Intent(this, MainActivity.class);
		        	  intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        	  startActivity(intent1);
		        	  return true;
		        	  
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK ) {
			  
			  switch(requestCode) {
			  case 00: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) {	
					  //add part about list/pie
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 10:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
					exp.restartLoader();
				  }
			  break;
			  case 01: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) {	
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 11:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
					exp.restartLoader();
				  }
			  break;

			  } 
		  }
	  }
	
	 private void setDefaults() {
		  
		  SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
	      Editor editor = prefs.edit();
		 
		  
	      if(!prefs.contains("base_currency"))
		  editor.putString("base_currency", "USD");
	      
	      if(!prefs.contains("def_currency"))
		  editor.putString("def_currency", "USD");
		  
	      if(!prefs.contains("def_dateformat"))
		  editor.putString("def_dateformat", "MMMM dd, yyyy");
		  //----------------------------------------------
		  
		  if(!prefs.contains("exp_def_orderBy"))
		  editor.putString("exp_def_orderBy", "date(e_date)");
		  if(!prefs.contains("exp_def_sortOrder"))
		  editor.putString("exp_def_sortOrder", "DESC"); 
		  if(!prefs.contains("exp_viewBy"))
		  editor.putString("exp_viewBy", "all");
		  if(!prefs.contains("exp_def_viewAs"))
		  editor.putString("exp_def_viewAs", "list");
		  if(!prefs.contains("exp_filter"))
		  editor.putString("exp_filter", "");
		  
		  //---------------------------------------------
		  if(!prefs.contains("inc_def_orderBy"))
		  editor.putString("inc_def_orderBy", "date(i_date)");
	      if(!prefs.contains("inc_def_sortOrder"))
		  editor.putString("inc_def_sortOrder", "DESC"); 
		  if(!prefs.contains("inc_viewBy"))
		  editor.putString("inc_viewBy", "all");
		  if(!prefs.contains("inc_def_viewAs"))
		  editor.putString("inc_def_viewAs", "list");
		  if(!prefs.contains("inc_filter"))
		  editor.putString("inc_filter", "");
		  
		  editor.commit();
		  
	}

	
	

	public static class MyTabListener implements
	ActionBar.TabListener {
		
		private Activity activity;
		private String tag;
		private Class fragClass;
		private Fragment currentFrag;
		
		
		public MyTabListener(Activity activity, String tag, Class fragClass){
			this.tag = tag;
			this.fragClass = fragClass;
			this.activity = activity;			
		}
		
		@Override
		public void onTabSelected(ActionBar.Tab tab,
				FragmentTransaction ft) {

	        // Check if the fragment is already initialized
	        if (currentFrag == null) {
	            // If not, instantiate and add it to the activity
	            currentFrag = Fragment.instantiate(activity, fragClass.getName());
	            ft.add(R.id.fragment_cashflow, currentFrag, tag);
	           
	        } else{
	            // If it exists, simply attach it in order to show it
	            ft.attach(currentFrag);
	        }	      
	        currentTab = tag;	
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab,
				FragmentTransaction ft) {
			if(currentFrag!=null){	
				ft.detach(currentFrag);
			}
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab,
				FragmentTransaction fragmentTransaction) {
		}
		
		public void setCurrentFrag(Fragment f){
				currentFrag = f;
		}
		
	}


	@Override
	public void onClick(View v) {
		
		 SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
	      Editor editor = prefs.edit();
	      Button b = (Button)v;
 
		if(v.getId() == R.id.convertCur) {
		
			
			if(currentTab.equals(EXPENSE_TAG)) {
				if(prefs.getString("exp_conv", "off").equals("off")) {
					editor.putString("exp_conv", "on");
					b.setText("convert to Original");					
				}
				else {
					editor.putString("exp_conv", "off");
					b.setText("convert to Default");
				}
				editor.commit();
				ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
				exp.restartLoader();
			} else {
				if(prefs.getString("inc_conv", "off").equals("off")) {
					editor.putString("inc_conv", "on");
					b.setText("convert to Original");
				}
				else {
					editor.putString("inc_conv", "off");
					b.setText("convert to Default");					
				}
				editor.commit();
					IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
					inc.restartLoader();
			}
			
		}
		
		if(v.getId() == R.id.listPie) {

			FragmentManager fm = getFragmentManager();
			Fragment f;
			FragmentTransaction ft;
			
			if(currentTab.equals(EXPENSE_TAG)) {
				if(prefs.getString("exp_cur_viewAs", "list").equals("list")) {
					editor.putString("exp_cur_viewAs", "pie");
					b.setText("List");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpensePieFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "expense_pie");
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				else {
					editor.putString("exp_cur_viewAs", "list");
					b.setText("Pie");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpenseListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "expense");
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				
			} else {
				if(prefs.getString("inc_conv", "off").equals("off")) {
					editor.putString("inc_conv", "on");
					b.setText("convert to Original");
				}
				else {
					editor.putString("inc_conv", "off");
					b.setText("convert to Default");					
				}
				editor.commit();
					IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
					inc.restartLoader();
			}
			
			
			
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos,
			long arg3) {
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
	    Editor editor = prefs.edit();
	    
	    if(v.getId() == R.id.sortOrder) {
			String item = (String)parent.getSelectedItem();
			String sortBy = item.split(" ")[0].trim();
			String order = item.split(" ")[2].trim();
			
			 if(currentTab.equals(EXPENSE_TAG)) {   		  
	   		  if(sortBy.equals("Date")) 			  
	   			  editor.putString("exp_cur_orderBy","date(e_date)");
	   		  else 
	   			  editor.putString("exp_cur_orderBy","e_amount * e_convrate");
	   		  if(order.equals("Newest")||order.equals("Highest"))
	   			  editor.putString("exp_cur_sortOrder","DESC"); 
	   		  else 
	   			  editor.putString("exp_cur_sortOrder","ASC");
	   		   
	   		  editor.commit();
	   		  ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
			  exp.restartLoader();
	  
	   	  } else {
	   		  
	   		  if(sortBy.equals("Date")) 			  
	   			  editor.putString("inc_cur_orderBy","date(i_date)");
	   		  else 
	   			  editor.putString("inc_cur_orderBy","i_amount * i_convrate");
	  
	   		  if(order.equals("Newest")||order.equals("Highest"))
	   			  editor.putString("inc_cur_sortOrder","DESC"); 
	   		  else 
	   			  editor.putString("inc_cur_sortOrder","ASC");
	   		  
	   		  editor.commit();
	   		  IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
					inc.restartLoader();
	   		      
	   		  
	   	  }
	   	  
	  } else {
		  if(currentTab.equals(EXPENSE_TAG)) {
			  if(((String)parent.getSelectedItem()).equals("All")) {
				  editor.putString("exp_filter","");
			  }
			  else {
				  editor.putString("exp_filter",(String)parent.getSelectedItem());
			  }
			  
	   		  ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
			  exp.restartLoader();
			  editor.commit();
		  } else {
			  if(((String)parent.getSelectedItem()).equals("All")) {
				  editor.putString("inc_filter","");
			  }
			  else
			  editor.putString("inc_filter",(String)parent.getSelectedItem());
			  editor.commit();
	   		  IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
			  inc.restartLoader();
		  }
	  }
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(currentTab.equals(EXPENSE_TAG)) {
			return new CategoryCursorLoader(this);
		} else {
			return new SourceCursorLoader(this);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		loadSpinners(c);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadSpinners(Cursor c){	
		List<String> names = new ArrayList<String>();
		names.add("All");
		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(1));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        filters.setAdapter(dataAdapter);  // attaching data adapter to category spinner
        
	}
}
