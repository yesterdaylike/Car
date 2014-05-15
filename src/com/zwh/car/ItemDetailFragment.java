package com.zwh.car;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zwh.car.dummy.DiskLruCache;
import com.zwh.car.dummy.DummyContent;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	public LinearLayout mDetailLayout;
	private AssetManager mAssetManager;
	private SqliteHelper mSqliteHelper;

	private LruCache<String, Bitmap> mMemoryCache;

	private DiskLruCache mDiskLruCache;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	//private static final String DISK_CACHE_SUBDIR = "thumbnails";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
		//Log.i("mItem.id", "id:"+mItem.id);
		mAssetManager = this.getResources().getAssets();
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
		//File cacheDir = getDiskCacheDir(this.getActivity(), DISK_CACHE_SUBDIR);
		//new InitDiskCacheTask().execute(cacheDir);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);
		onCreateData();
		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			mDetailLayout = (LinearLayout) rootView.findViewById(R.id.detail_layout);
			//String[] mStringArray = DummyContent.OpenFile(this.getActivity());
			if( null == mSqliteHelper ){
				mSqliteHelper = new SqliteHelper(this.getActivity());
			}
			String[][] mStringArray = mSqliteHelper.queryItem(mItem.id);
			//Log.i("getImageFromAssetsFile","length:" + mStringArray.length);
			for (int i = 0; i < mStringArray.length; i++ ) {//mStringArray.length
				String[] strings = mStringArray[i];
				View item = inflater.inflate(R.layout.multiple_choice, null, false);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.title = (TextView)item.findViewById(R.id.title);
				viewHolder.image = (ImageView)item.findViewById(R.id.image);
				viewHolder.radioGroup = (RadioGroup)item.findViewById(R.id.radio_group);
				viewHolder.aRadioButton = (RadioButton)item.findViewById(R.id.choice_a);
				viewHolder.bRadioButton = (RadioButton)item.findViewById(R.id.choice_b);
				viewHolder.cRadioButton = (RadioButton)item.findViewById(R.id.choice_c);
				viewHolder.dRadioButton = (RadioButton)item.findViewById(R.id.choice_d);
				viewHolder.answer = (TextView)item.findViewById(R.id.answer);
				viewHolder.explanation = (TextView)item.findViewById(R.id.explanation);
				viewHolder.star = (CheckBox)item.findViewById(R.id.checkBox);
				viewHolder.star.setOnCheckedChangeListener(listener);
				item.setTag(viewHolder);

				viewHolder.radioGroup.setTag(viewHolder);
				viewHolder.radioGroup.setOnCheckedChangeListener(radioGroupListener);

				viewHolder.title.setText(strings[1]);
				//viewHolder.image.setImageResource(image[j]);
				viewHolder.image.setImageBitmap(getImageFromAssetsFile(strings[2]));
				viewHolder.aRadioButton.setText(strings[3]);
				viewHolder.bRadioButton.setText(strings[4]);
				if(strings[5].equals(strings[6])){
					viewHolder.cRadioButton.setVisibility(View.GONE);
					viewHolder.dRadioButton.setVisibility(View.GONE);
				}
				viewHolder.cRadioButton.setText(strings[5]);
				viewHolder.dRadioButton.setText(strings[6]);

				viewHolder.answer.setText(strings[7]);
				viewHolder.explanation.setText(strings[8]);

				if( null!=strings[9] && strings[9].equals("1")){
					viewHolder.star.setChecked(true);
				}
				viewHolder.star.setTag(strings[0]);
				item.setTag(viewHolder);
				mDetailLayout.addView(item);
			}
		}
		return rootView;
	}

	//由文件构建数据库
	public void onCreateData() {

		if( null == mSqliteHelper ){
			mSqliteHelper = new SqliteHelper(this.getActivity());
		}

		int count = mSqliteHelper.query();
		if(count>0){
			return;
		}

		String[][] qs = new String[973][9];
		String[] illustration = null;
		try {
			illustration = mAssetManager.list("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> illustrationList = Arrays.asList(illustration);

		String[] mStringArray = DummyContent.OpenFile(this.getActivity());
		String[] q;
		for (int i = 0, j = 0; i < mStringArray.length && j < 973; j++ ) {
			q = qs[j];
			q[0] = mStringArray[i++];  //question

			String img = String.valueOf(j+1);
			String png = img+".png";
			String jpg = img+".jpg";

			if(illustrationList.contains(png)){
				q[1] = png;
			}
			else if(illustrationList.contains(jpg)){
				q[1] = jpg;
			}

			q[3] = mStringArray[i++];  //a
			q[4] = mStringArray[i++];  //b
			if(mStringArray[i].equals(mStringArray[i+1])){
				q[2] = "TrueOrFalse";//option_type
			}
			else{
				q[2] = "MultipleChoice";//option_type
			}
			q[5] = mStringArray[i++];  //c
			q[6] = mStringArray[i++];  //d

			q[7] = mStringArray[i++];  //answer
			q[8] = mStringArray[i++];  //explanation
		}
		mSqliteHelper.buildQuestionLibrary(qs);
	}

	private Bitmap getImageFromAssetsFile(String fileName) {

		if(null == fileName){
			return null;
		}
		//Log.i("getImageFromAssetsFile","fileName:" + fileName);
		Bitmap image = null;
		try {
			InputStream is = mAssetManager.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public class ViewHolder{
		TextView title;
		ImageView image;
		RadioGroup radioGroup;
		RadioButton aRadioButton;
		RadioButton bRadioButton;
		RadioButton cRadioButton;
		RadioButton dRadioButton;
		TextView answer;
		TextView explanation;
		CheckBox star;
	}

	OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if( null == mSqliteHelper ){
				mSqliteHelper = new SqliteHelper(ItemDetailFragment.this.getActivity());
			}
			mSqliteHelper.star(String.valueOf(buttonView.getTag()), isChecked);

			View temp = (View) buttonView.getParent();
			int resId = isChecked ? R.drawable.card_goldborder: R.drawable.card_blueborder;
			temp.setBackgroundResource(resId);
		}
	};

	RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			ViewHolder temp = (ViewHolder) group.getTag();
			temp.answer.setVisibility(View.VISIBLE);
			temp.explanation.setVisibility(View.VISIBLE);

			String answer = String.valueOf(temp.answer.getText());
			//Log.i("OnCheckedChangeListener", "checkedId:"+checkedId+", answer:"+str);

			String choiceMul = null;
			String choiceyes = null;
			switch (checkedId) {
			case R.id.choice_a:
				choiceMul = "A";
				choiceyes = "正确";
				break;
			case R.id.choice_b:
				choiceMul = "B";
				choiceyes = "错误";
				break;
			case R.id.choice_c:
				choiceMul = "C";
				break;
			case R.id.choice_d:
				choiceMul = "D";
				break;

			default:
				break;
			}

			if( ( choiceMul != null && answer.endsWith(choiceMul) ) || ( choiceyes != null && answer.endsWith(choiceyes) ) ){
				//回答正确
			}
			else{
				temp.star.setChecked(true);
				View view = (View) group.getParent();
				view.setBackgroundResource(R.drawable.card_goldborder);
			}
		}
	};

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		// Add to memory cache as before
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}

		// Also add to disk cache
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
				mDiskLruCache.put(key, bitmap);
			}
		}
	}


	public Bitmap getBitmapFromDiskCache(String key) {
		synchronized (mDiskCacheLock) {
			// Wait while disk cache is started from background thread
			while (mDiskCacheStarting) {
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {}
			}
			if (mDiskLruCache != null) {
				return mDiskLruCache.get(key);
			}
		}
		return null;
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void loadBitmap(String image, ImageView imageView) {
		final Bitmap bitmap = getBitmapFromMemCache(image);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.ic_launcher);
			BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			task.execute(image);
		}
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		ImageView mImageView;
		public BitmapWorkerTask(ImageView imageView){
			mImageView = imageView;
		}
		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			// Check disk cache in background thread
			Bitmap bitmap = getBitmapFromDiskCache(params[0]);

			if (bitmap == null) { // Not found in disk cache
				try {
					InputStream is = mAssetManager.open(params[0]);
					bitmap = BitmapFactory.decodeStream(is);
					addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
					mImageView.setImageBitmap(bitmap);
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return bitmap;
		}
	}

	class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
		@Override
		protected Void doInBackground(File... params) {
			synchronized (mDiskCacheLock) {
				File cacheDir = params[0];
				mDiskLruCache = DiskLruCache.openCache(cacheDir, DISK_CACHE_SIZE);
				mDiskCacheStarting = false; // Finished initialization
				mDiskCacheLock.notifyAll(); // Wake any waiting threads
			}
			return null;
		}
	}

	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use external cache dir
		// otherwise use internal cache dir
		final String cachePath =
				Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
				!Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
					context.getCacheDir().getPath();

				return new File(cachePath + File.separator + uniqueName);
	}
}
