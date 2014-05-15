package com.zwh.car.dummy;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "  1-100"));
		addItem(new DummyItem("101", "101-200"));
		addItem(new DummyItem("201", "201-300"));
		addItem(new DummyItem("301", "301-400"));
		addItem(new DummyItem("401", "401-500"));
		addItem(new DummyItem("501", "501-600"));
		addItem(new DummyItem("601", "601-700"));
		addItem(new DummyItem("701", "701-800"));
		addItem(new DummyItem("801", "801-900"));
		addItem(new DummyItem("901", "901-973"));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
	public static String[] OpenFile(Context context){
		ArrayList<String> mStringArrayList= new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(context.getAssets().open("result.txt"))); 
			String mLine = reader.readLine();
			while (mLine != null) {
				//process line
				//Log.i("zheng",mLine);
				mStringArrayList.add(mLine);
				mLine = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			//log the exception
		}
		String[] mStringArray = mStringArrayList.toArray(new String[mStringArrayList.size()]);
		mStringArrayList.clear();
		return mStringArray;
	}

	/*public static void buildFile(Context context){
		try {

			BufferedReader answerReader = new BufferedReader(
					new InputStreamReader(context.getAssets().open("all.txt")));


			FileOutputStream outputStream = context.openFileOutput("re.txt",Activity.MODE_APPEND);


			String mLine = "start";// = zhengReader.readLine();
			int i = 0;
			while (mLine != null) {
				if(i%7==2 && mLine.equals("")){
					i--;
				}
				else{
					outputStream.write(mLine.getBytes());
					outputStream.write("\n".getBytes());
					outputStream.flush();
				}
				i++;
				mLine = answerReader.readLine();
				
				//outputStream.write(mLine.getBytes());
				//outputStream.write("\n".getBytes());
				//outputStream.flush();
			}

			answerReader.close();
			outputStream.close();  
		} catch (IOException e) {
			e.printStackTrace();  
		}  
	}*/
	public static void buildFile(Context context){
		try {

			BufferedReader zhengReader = new BufferedReader(
					new InputStreamReader(context.getAssets().open("all.txt")));

			BufferedReader answerReader = new BufferedReader(
					new InputStreamReader(context.getAssets().open("answer.txt")));


			FileOutputStream outputStream = context.openFileOutput("result.txt",Activity.MODE_APPEND);


			String mLine = "start";// = zhengReader.readLine();
			int index = 1;
			while (mLine != null) {
				if(index>=6049){
					if( index % 7 == 0 ){
						mLine = zhengReader.readLine();
						mLine = answerReader.readLine();
						outputStream.write(mLine.getBytes());
						outputStream.write("\n".getBytes());
						outputStream.flush();
					}else{
						mLine = answerReader.readLine();
						mLine = zhengReader.readLine();
						outputStream.write(mLine.getBytes());
						outputStream.write("\n".getBytes());
						outputStream.flush();
					}
					
				}
				else{
					mLine = zhengReader.readLine();
					outputStream.write(mLine.getBytes());
					outputStream.write("\n".getBytes());
					outputStream.flush();
				}
				index++;
			}

			zhengReader.close();
			answerReader.close();
			outputStream.close();  
		} catch (IOException e) {
			e.printStackTrace();  
		}  
	}
}
