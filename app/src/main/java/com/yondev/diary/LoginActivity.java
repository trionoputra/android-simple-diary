package com.yondev.diary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yondev.diary.entity.Setting;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.SettingDataSource;
import com.yondev.diary.utils.Constant;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.LoadingDialog;


public class LoginActivity extends AppCompatActivity implements OnClickListener {
	private EditText txtpin;
	private LoadingDialog loading;

	private AdView mAdView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Shared.initialize(getBaseContext());

		mAdView = (AdView)findViewById(R.id.ad_view);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		
		Button btnOK = (Button)findViewById(R.id.button1);
		btnOK.setOnClickListener(this);
		
		txtpin = (EditText)findViewById(R.id.editText1);
		loading = new LoadingDialog(this);

        TextView t1 = (TextView)findViewById(R.id.textView1);
        TextView t6 = (TextView)findViewById(R.id.textView6);

        txtpin.setTypeface(Shared.appfontLight);
        btnOK.setTypeface(Shared.appfontBold);
        t1.setTypeface(Shared.appfontBold);
        t6.setTypeface(Shared.appfont);

		t6.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
			case R.id.button1:
				if(txtpin.getText().toString().equals(""))
				{
					Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
					return;
				}

				SaveAsync save = new SaveAsync(txtpin.getText().toString());
				save.execute();
				break;
			case R.id.textView6:
				Intent  intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				break;
		}

	}

	public class SaveAsync extends AsyncTask<String, String, String>
	{
        private String password;
        public SaveAsync(String password)
        {
            this.password = password;
        }

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(loading != null)
				loading.show();
			else
				loading = new LoadingDialog(LoginActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String result = "0";

			try {

                SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
                SettingDataSource DS = new SettingDataSource(db);
                Setting password = DS.get(Constant.SETTING_PASSWORD);
                if(password.getValue().equals(this.password))
                {
                    result = "1";
                }
                else
				{
					Setting passwordnew = DS.get(Constant.SETTING_PASSWORD_NEW);
					if(passwordnew != null)
					{
						if(!passwordnew.getValue().equals(""))
						{
							if(passwordnew.getValue().equals(this.password))
							{
								password.setValue(passwordnew.getValue());
								DS.update(password);

								passwordnew.setValue("");
								DS.update(passwordnew);
								result = "1";
							}
						}
					}
				}

                DatabaseManager.getInstance().closeDatabase();

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loading.dismiss();
            if(result.equals("1"))
            {
                Intent intent = new Intent(LoginActivity.this, TimeLineActivity.class);
                intent.putExtra("hasLogin",true);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
            }
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (loading != null) {
			loading.dismiss();
			loading = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mAdView != null) {
			mAdView.destroy();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdView != null) {
			mAdView.pause();
		}

		if (loading != null) {
			loading.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}
}
