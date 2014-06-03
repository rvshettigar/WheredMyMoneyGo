package com.anumeha.wheredmymoneygo.Expense;

import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.PieChart;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpensePieFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	View view;
	List<String> categories;
	ImageView imgView;
	Activity activity;
	PieChart pie;
	TextView noExp;
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.expense_chart_fragment, container, false);   
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		categories = new ArrayList<String>();		
		imgView = (ImageView) view.findViewById(R.id.expPieChart);
		noExp = (TextView) view.findViewById(R.id.expNotPresent);
		getLoaderManager().initLoader(0, null,this); //exp cursor loader
		
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;      
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    return new ExpenseCursorLoader(activity, 3); //to get expenses and categories
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		if(cursor.getCount()!= 0) {
			noExp.setVisibility(0);
			pie = new PieChart(activity,imgView,cursor);
			imgView.setImageDrawable(pie);
		}
		else {
			noExp.setText("No expenses present!");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
