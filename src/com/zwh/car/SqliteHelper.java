package com.zwh.car;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "c1_question_library.db";
	private static final int DATABASE_VERSION = 1;

	public SqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS question_library" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, illustration TEXT, option_type TEXT, option_a TEXT, option_b TEXT, option_c TEXT, option_d TEXT, correct_answer TEXT, explanation TEXT, star TEXT,wrong TEXT,answer TEXT,special_type TEXT,section_type TEXT)"); 
	}

	public void buildQuestionLibrary(String[][] questionArray) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (String[] question : questionArray) {  
                db.execSQL("INSERT INTO question_library VALUES( null,?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, null, null, null)", question);  
            }
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	}
	
	public void star(String id, boolean checked) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("star", checked);
		db.update("question_library", values, "_id = ?", new String[] { id });
		
		db.close();
	}
	
	public int query(){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("question_library", 
				new String[] { "_id"},
				null,
				null,
				null, 
				null, 
				null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	public String [][] queryItem(String id){
		if(id.equals("star")){
			return queryStar();
		}
		return queryAll(id);
		
	}
	
	public String [][] queryAll(String id){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("question_library", 
				new String[] { "_id", "question", "illustration", "option_a", "option_b", "option_c", "option_d", "correct_answer", "explanation", "star"},
				"_id >= ? AND _id < ?", 
				new String[] { id, String.valueOf(Integer.valueOf(id)+100)},
				null, 
				null, 
				null);
		
		String [][]str = new String[cursor.getCount()][cursor.getColumnCount()]; 
		
		for (String[] strings : str) {
			cursor.moveToNext();
			for (int i = 0; i < strings.length; i++) {
				strings[i] = cursor.getString(i);
			}
		}
		cursor.close();
		db.close();
		return str;
	}
	
	public String [][] queryStar(){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("question_library", 
				new String[] { "_id", "question", "illustration", "option_a", "option_b", "option_c", "option_d", "correct_answer", "explanation", "star"},
				"star = ?", 
				new String[] { "1" },
				null, 
				null, 
				null);
		
		String [][]str = new String[cursor.getCount()][cursor.getColumnCount()]; 
		
		for (String[] strings : str) {
			cursor.moveToNext();
			for (int i = 0; i < strings.length; i++) {
				strings[i] = cursor.getString(i);
			}
		}
		cursor.close();
		db.close();
		return str;
	}
	
	public String [][] queryTrueOrFalse(){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("question_library", 
				new String[] { "_id", "question", "illustration", "option_a", "option_b", "correct_answer", "explanation", "star"},
				"option_type=?", 
				new String[] { "TrueOrFalse" },
				null, 
				null, 
				null);
		
		String [][]str = new String[cursor.getCount()][cursor.getColumnCount()]; 
		
		for (String[] strings : str) {
			cursor.moveToNext();
			for (int i = 0; i < strings.length; i++) {
				strings[i] = cursor.getString(i);
			}
		}
		cursor.close();
		db.close();
		return str;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
}
