package com.anumeha.wheredmymoneygo;

import java.util.Locale;

import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.Expense.ExpenseListFragment;
import com.anumeha.wheredmymoneygo.Expense.ExpenseOptionsFragment;
import com.anumeha.wheredmymoneygo.Expense.ExpensePieFragment;
import com.anumeha.wheredmymoneygo.Income.IncomeListFragment;
import com.example.wheredmymoneygo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener{


	private static String currentTab ;
	private int INCOME_ADDED = 00;
	private int EXPENSE_ADDED = 10;
	private static int EDIT_INCOME = 01; //0 FOR INCOME 1 FOR EDIT	
	private Button convert,listPie, sortByDate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setDefaults();
		
		convert = (Button)findViewById(R.id.convertCur);
		convert.setOnClickListener(this);
		
		//sortByDate = (Button)findViewById(R.id.sortByDate);
		//sortByDate.setOnClickListener(this);
		
		
		listPie = (Button)findViewById(R.id.listPie);
		listPie.setOnClickListener(this);
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// add the income, expense and income vs expense tabs
		actionBar.addTab(actionBar.newTab().setText(R.string.title_expensetab).setTabListener(new TwoFragTabListener(this, "tag1", "expense", ExpenseOptionsFragment.class, ExpenseListFragment.class )));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_incometab).setTabListener(new TwoFragTabListener(this, "tag3", "income", ExpenseOptionsFragment.class, IncomeListFragment.class )));
	//	actionBar.addTab(actionBar.newTab().setText(R.string.title_expvsinctab).setTabListener(new TwoFragTabListener(this, "tag5", "evi", ExpenseOptionsFragment.class, ExpenseListFragment.class )));
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
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 10:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
					exp.restartLoader();
				  }
			  break;
			  case 01: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) {	
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 11:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
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

	
	

	public static class TwoFragTabListener implements
	ActionBar.TabListener {
		
		private Fragment options;
		private Fragment list;
		private Activity activity;
		private String tag1;
		private String tag2;
		private Class optionsClass;
		private Class listClass;
		
		
		public TwoFragTabListener(Activity activity, String tag1, String tag2, Class optionsClass, Class listClass)
		{
			this.tag1 = tag1;
			this.tag2 = tag2;
			this.optionsClass = optionsClass;
			this.listClass = listClass;
			this.activity = activity;			
		}
		
		@Override
		public void onTabSelected(ActionBar.Tab tab,
				FragmentTransaction ft) {
			
			 // Check if the fragment is already initialized
	        if (options == null) {
	            // If not, instantiate and add it to the activity
	            options = Fragment.instantiate(activity, optionsClass.getName());
	            ft.add(R.id.fragment_e_options1, options, tag1);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(options);
	        }

	        // Check if the fragment is already initialized
	        if (list == null) {

	            // If not, instantiate and add it to the activity
	            list = Fragment.instantiate(activity, listClass.getName());
	            ft.add(R.id.fragment_expense_list1, list, tag2);
	           
	        } else{
	            // If it exists, simply attach it in order to show it
	            ft.attach(list);
	        }
	        
	        currentTab = list.getTag();
		
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab,
				FragmentTransaction ft) {
			if(list!=null)
			{
				ft.detach(list);
			}
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab,
				FragmentTransaction fragmentTransaction) {
		}
		
	}




	@Override
	public void onClick(View v) {
		
		 SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
	      Editor editor = prefs.edit();
	      Button b = (Button)v;
	      if(v.getId() == R.id.sortByDate) { 
	    	  
	    	  if(currentTab.equals("expense")) {
	    		  
	    		  String currentOrder = prefs.getString("exp_cur_sortOrder","DESC");
	    		  if(currentOrder.equals("DESC")) {
	    			  editor.putString("exp_cur_sortOrder","ASC");
	    		      b.setText("View in Descending"); }
	    		  else {
	    			  editor.putString("exp_cur_sortOrder","DESC");
	    		      b.setText("View in Ascending"); }
	    		  
	    		  ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
					exp.restartLoader();
	    		  
	    		  editor.commit();
	    		 
	    		  
	    	  } else {
	    		  
	    		  String currentOrder = prefs.getString("inc_cur_sortOrder","DESC");
	    		  if(currentOrder.equals("DESC")) {
	    			  editor.putString("inc_cur_sortOrder","ASC");
	    		      b.setText("View in Descending"); }
	    		  else {
	    			  editor.putString("inc_cur_sortOrder","DESC");
	    		       b.setText("View in Ascending"); }
	    		  
	    		  IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
					inc.restartLoader();
	    		      
	    		  editor.commit();
	    		  
	    	  }
	    	  
	    	  
	    	  
	      }
	      
	      
	      
		if(v.getId() == R.id.convertCur) {
		
			
			if(currentTab.equals("expense")) {
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
			
			if(currentTab.equals("expense")) {
				if(prefs.getString("exp_cur_viewAs", "list").equals("list")) {
					editor.putString("exp_cur_viewAs", "pie");
					b.setText("List");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpensePieFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_expense_list1, f, "expense_pie");
					ft.commit();
				}
				else {
					editor.putString("exp_cur_viewAs", "list");
					b.setText("Pie");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpenseListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_expense_list1, f, "expense");
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
}