package com.anumeha.wheredmymoneygo.DBhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anumeha.wheredmymoneygo.DBHandler;

public class ColorDbHelper {
	
    /** Colors Table**/
    private static final String TABLE_COLORS = "Colors"; 
    
    private static final String KEY_Co_COLOR = "color_code";
    private static final String KEY_Co_TAKEN = "taken";  

	 
	 private DBHandler dbh;
	
	 public ColorDbHelper(Context context){
			
		dbh = new DBHandler(context);			
	 }
	 
	 // add a color 
	 public void addColor(int color_code, String taken) {
		 
		 SQLiteDatabase   db = dbh.getWritableDatabase();
		 
		 ContentValues values = new ContentValues();
		 values.put(KEY_Co_COLOR, color_code);
		 values.put(KEY_Co_TAKEN, taken); 
		 
		 // Inserting Row
		 db.insert(TABLE_COLORS, null, values);
		 
	 }	 
	 
	 public void updateColor(int color_code, String taken) {
    	SQLiteDatabase db = dbh.getWritableDatabase();
    	 
  	    ContentValues values = new ContentValues();
  	    values.put(KEY_Co_COLOR, color_code); // Category Name
  	    values.put(KEY_Co_TAKEN, taken); // Category  budget
  	   
  	    // Updating Row
  	    db.update(TABLE_COLORS, values, KEY_Co_COLOR+"=\""+ color_code+"\"", null);
  	    db.close(); // Closing database connection
 
   }
	 
	 //Check if color exists
	 public Boolean colorExists(int color_code) {
        // Select All Query
        String selectQuery = "SELECT "+KEY_Co_COLOR+" FROM " + TABLE_COLORS + " WHERE "+KEY_Co_COLOR+"=\"" + color_code + "\"";
     
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.getCount() == 0)
        {
        	return false;
        }
      
        return true;
    }
	 
	 //Get first non taken color
	 public int getFirstAvailableColor() {
        // Select All Query
        String selectQuery = "SELECT "+KEY_Co_COLOR+" FROM " + TABLE_COLORS + " WHERE "+KEY_Co_TAKEN+"=\"false\"";
     
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.getCount() == 0)
        {
        	return -1;
        }
        cursor.moveToFirst();
      
        return cursor.getInt(0);
     }
}
