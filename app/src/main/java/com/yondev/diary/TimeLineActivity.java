package com.yondev.diary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.yondev.diary.adapter.DiaryListAdapter;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.entity.Setting;
import com.yondev.diary.sqlite.DatabaseHelper;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.DiaryDataSource;
import com.yondev.diary.sqlite.ds.SettingDataSource;
import com.yondev.diary.utils.Constant;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.FloatingActionButton;

public class TimeLineActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemClickListener {
	private ImageView imgCover;
	private ImageView imgPP;
	private DiaryListAdapter adapter;
	private ListView lv;
	
	private final int CHANGE_PROFILE = 103;
	private final int CHANGE_COVER = 104;
	public final static int MAKE_DIARY = 105;

	private boolean doubleBackToExitPressedOnce;
	private boolean hasLogin = false;

	private InterstitialAd Interstitial;
	private AdRequest adRequest;
	private int pageLimit = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Shared.initialize(getBaseContext());

		adRequest = new AdRequest.Builder()
				.build();

		Interstitial = new InterstitialAd(TimeLineActivity.this);
		Interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

		DatabaseManager.initializeInstance(new DatabaseHelper(this));

		if (shouldAskPermissions()) {
			askPermissions();
		}

		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			hasLogin = extras.getBoolean("hasLogin");
		}

		SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
		SettingDataSource DSs = new SettingDataSource(db);
		Setting isLock = DSs.get(Constant.SETTING_IS_LOCK);

		if(isLock.getValue().equals("true") && !hasLogin)
		{
			Intent intent = new Intent(TimeLineActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}

		DatabaseManager.getInstance().closeDatabase();

		setContentView(R.layout.activity_time_line);
		
		imgCover = (ImageView)findViewById(R.id.imageCover);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,(int)(Shared.getDisplayHeight() * 0.3));
		imgCover.setLayoutParams(params);
		
		//ImageView shadow = (ImageView)findViewById(R.id.imageShadow);
		//LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT,(int)(params.height * 0.4));
		//shadow.setLayoutParams(params2);
		

		imgPP  = (ImageView)findViewById(R.id.imagePP);
		
		/*Glide.with(this).load(R.mipmap.profile_default).asBitmap().centerCrop()
		.transform(new CircleTransform(TimeLineActivity.this))
		.into(imgPP);
		*/
		
		FrameLayout lay = (FrameLayout)findViewById(R.id.rootview);
		FloatingActionButton addButton = new FloatingActionButton.Builder(this)
	        .withDrawable(getResources().getDrawable(R.mipmap.ic_edit_white_24dp))
	        .withButtonColor(Color.parseColor("#e1675a"))
	        .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
	        .withMargins(0, 0, 10, 10)
	        .create(lay);


        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeLineActivity.this, DiaryActivity.class);
                startActivityForResult(intent,MAKE_DIARY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

		findViewById(R.id.imageButton1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimeLineActivity.this, SettingsActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});

	/*	FloatingActionButton settingButton = new FloatingActionButton.Builder(this)
	        .withDrawable(getResources().getDrawable(R.drawable.ic_setting))
	        .withButtonColor(Color.parseColor("#363d45"))
	        .withGravity(Gravity.TOP | Gravity.RIGHT)
	        .withMargins(0, 5, 5, 0)
	        .withButtonSize(40)
	        .create(lay);
		*/


		ArrayList<Diary> dt = new ArrayList<Diary>();

		SQLiteDatabase db2 =  DatabaseManager.getInstance().openDatabase();
		DiaryDataSource DS = new DiaryDataSource(db2);
		SettingDataSource DSsetting = new SettingDataSource(db2);
		dt = DS.getAll(null,null,pageLimit,0);

		Setting settingProfile = DSsetting.get(Constant.SETTING_PROFILE);
		Setting settingCover = DSsetting.get(Constant.SETTING_COVER);

		DatabaseManager.getInstance().closeDatabase();

		adapter = new DiaryListAdapter(this, dt);
		
		lv = (ListView)findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnScrollListener(new  EndlessScrollListener());
		imgCover.setOnClickListener(this);
		imgPP.setOnClickListener(this);

		if(settingProfile.getValueBlob() != null)
		{
			imgPP.setImageBitmap(BitmapFactory.decodeByteArray(settingProfile.getValueBlob(), 0, settingProfile.getValueBlob().length));
		}

		if(settingCover.getValueBlob() != null)
		{
			imgCover.setImageBitmap(BitmapFactory.decodeByteArray(settingCover.getValueBlob(), 0, settingCover.getValueBlob().length));
		}

	}
	
	private void coverOnClick(int selected)
	{
		if(selected == 0)
		{
			final CharSequence[] items = { "CHANGE PROFILE", "CHANGE COVER"};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(TimeLineActivity.this);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					    int select = CHANGE_PROFILE;
						if (items[item].equals("CHANGE COVER"))
							select = CHANGE_COVER;

						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(
								Intent.createChooser(intent, "SELECT IMAGE"),select);
				}
			});
			builder.show();
		}
		else
		{
			Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, selected);
		}
		
	}

	private boolean shouldAskPermissions()
	{
		return(Build.VERSION.SDK_INT >= 23);
	}

	@TargetApi(23)
	protected void askPermissions() {
		String[] permissions = {
				"android.permission.ACCESS_FINE_LOCATION",
				"android.permission.ACCESS_COARSE_LOCATION"
		};
		int requestCode = 200;
		requestPermissions(permissions, requestCode);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageCover:
			coverOnClick(0);
			break;
		case R.id.imagePP:
			coverOnClick(CHANGE_PROFILE);
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK)
		{
			if(requestCode == MAKE_DIARY)
			{
				ArrayList<Diary> dt = new ArrayList<Diary>();

				SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
				DiaryDataSource DS = new DiaryDataSource(db);
				dt = DS.getAll(null,null,pageLimit,0);
				adapter.set(dt);
				lv.setOnScrollListener(new  EndlessScrollListener());
				DatabaseManager.getInstance().closeDatabase();

			}
			else if(requestCode == CHANGE_COVER || requestCode == CHANGE_PROFILE)
			{
				Uri URI = data.getData();
				try {

					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), URI);

					int mWidth = 1024;
					int mHeight = 1024;

					if(bitmap.getWidth() > bitmap.getHeight())
						mHeight = (int)(bitmap.getHeight() * (1024f / bitmap.getWidth()));
					else
						mWidth = (int)(bitmap.getWidth() * (1024f / bitmap.getHeight()));

					Bitmap bm = Bitmap.createScaledBitmap(bitmap, mWidth,mHeight, false);

					if(requestCode == CHANGE_COVER)
					{
						imgCover.setImageBitmap(bm);
						SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
						SettingDataSource DS = new SettingDataSource(db);
						Setting setting = DS.get(Constant.SETTING_COVER);
						if(setting.getCode() == null)
						{
							setting.setCode(Constant.SETTING_COVER);
							setting.setName("COVER");

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
							setting.setValueBlob(stream.toByteArray());
							DS.insert(setting);
						}
						else
						{
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
							setting.setValueBlob(stream.toByteArray());
							DS.update(setting);
						}
						DatabaseManager.getInstance().closeDatabase();
					}

					else
					{
						imgPP.setImageBitmap(bm);
						SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
						SettingDataSource DS = new SettingDataSource(db);
						Setting setting = DS.get(Constant.SETTING_PROFILE);
						if(setting.getCode() == null)
						{
							setting.setCode(Constant.SETTING_PROFILE);
							setting.setName("PROFILE");

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
							setting.setValueBlob(stream.toByteArray());
							DS.insert(setting);
						}
						else
						{
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
							setting.setValueBlob(stream.toByteArray());
							DS.update(setting);
						}
						DatabaseManager.getInstance().closeDatabase();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Toast toast;
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			if(toast != null)
				toast.cancel();
			finish();
			Interstitial.loadAd(adRequest);
		}
		else
		{
			doubleBackToExitPressedOnce = true;
			toast =  Toast.makeText(this, R.string.tap_again_to_exit, Toast.LENGTH_SHORT);
			toast.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					doubleBackToExitPressedOnce = false;
					if(toast != null)
						toast.cancel();
				}
			}, 2000);
		}
		//super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(TimeLineActivity.this, PreviewActivity.class);
		Diary d = (Diary) adapter.getItem(position);
		intent.putExtra("code",d.getCode());
		intent.putExtra("fromTimeline",true);
		startActivityForResult(intent,MAKE_DIARY);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try
		{
			if(Shared.read("isRefresh","false").equals("true"))
			{
				Shared.clear("isRefresh");
				SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
				DiaryDataSource DS = new DiaryDataSource(db);
				adapter.set(DS.getAll(null,null,pageLimit,0));
				lv.setOnScrollListener(new  EndlessScrollListener());
				DatabaseManager.getInstance().closeDatabase();

			}
		}catch (Exception ex){}

		Interstitial.loadAd(adRequest);

	}

	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (Interstitial.isLoaded()) {
			Interstitial.show();
		}
	}

	@Override
	protected void onDestroy() {
		displayInterstitial();
		super.onDestroy();
	}


	public class EndlessScrollListener implements AbsListView.OnScrollListener {

		private int visibleThreshold = pageLimit;
		private int currentPage = 0;
		private int previousTotal = 0;
		private boolean loading = true;

		public EndlessScrollListener() {
		}
		public EndlessScrollListener(int visibleThreshold) {
			this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					currentPage++;
				}
			}
			if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

				new getAsync(currentPage + 1,visibleThreshold).execute();
				loading = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}

	public class getAsync extends AsyncTask<String, String, ArrayList<Diary>>
	{
		int page;
		int limit;
		public getAsync(int page,int limit)
		{
			this.page = page;
			this.limit = limit;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Diary> doInBackground(String... params) {
			// TODO Auto-generated method stub
            ArrayList<Diary> d = new ArrayList<>();
			SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
			DiaryDataSource DS = new DiaryDataSource(db);
            d = DS.getAll(null,null,limit,(page-1)*limit);
			DatabaseManager.getInstance().closeDatabase();
			return d;
		}

		@Override
		protected void onPostExecute(ArrayList<Diary> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
            adapter.addMany(result);
		}
	}
}
