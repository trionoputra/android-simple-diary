package com.yondev.diary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Setting setting;
    private LoadingDialog loading;
    private Button btnSend;
    private TextView t7;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_forgot_password);

        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.btnBack).setOnClickListener(this);
        btnSend = (Button) findViewById(R.id.button1);
        btnSend.setOnClickListener(this);

        TextView t5 = (TextView)findViewById(R.id.textView5);
        t5.setTypeface(Shared.appfontBold);

        t7 = (TextView)findViewById(R.id.textView7);
        t7.setTypeface(Shared.appfont);

        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        SettingDataSource DS = new SettingDataSource(db);
        setting = DS.get(Constant.SETTING_EMAIL);
        DatabaseManager.getInstance().closeDatabase();

        String email = "-";
        if(setting != null)
        {
            if(!setting.getValue().equals(""))
            {
                String split[] = setting.getValue().split("@",2);
                if(split.length >= 2)
                {
                    if(split[0].length() == 1)
                    {
                        email = "*@" + split[1];
                    }
                    else if(split[0].length() == 2)
                    {
                        String ss[] = split[0].split("");
                        email = ss[0]+"*@" + split[1];
                    }
                    else
                    {
                        String ss[] = split[0].trim().split("");
                        String e = "";
                        for (int i = 0; i < ss.length; i++)
                        {
                            if(!e.equals(""))
                                e += "*";
                            else
                                e += ss[i];

                        }

                        email = e+"@" + split[1];
                    }
                }
            }
        }

        t7.setText("We will send you an email to "+email+" with a new password to get back into your diary");
        loading = new LoadingDialog(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnBack:
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
                break;
            case R.id.button1:
               resetPassword();
                break;
        }
    }

    private void resetPassword()
    {
        ResetAsync reset = new ResetAsync();
        reset.execute();
    }

    public class ResetAsync extends AsyncTask<String, String, String>
    {
        public ResetAsync()
        {}

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "0";
            URL url;
            HttpURLConnection urlConnection = null;
            try {

                String newPassword = randomString(5);
                String data = setting.getValue() + "|" + newPassword;
                url = new URL(Shared.SERVER_URL + "resetpassword?data="+ Base64.encodeToString(data.getBytes(),Base64.URL_SAFE));
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    if(sb.toString().equals("true"))
                    {
                        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
                        SettingDataSource DS = new SettingDataSource(db);
                        setting = DS.get(Constant.SETTING_PASSWORD_NEW);
                        if(setting.getCode() == null)
                        {
                            setting.setCode(Constant.SETTING_PASSWORD_NEW);
                            setting.setName("New password");
                            setting.setValue(newPassword);
                            DS.insert(setting);
                        }
                        else
                        {
                            setting.setValue(newPassword);
                            DS.update(setting);
                        }

                        DatabaseManager.getInstance().closeDatabase();
                        result = "1";
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            loading.dismiss();
            if(result.equals("0"))
            {
                Toast.makeText(ForgotPasswordActivity.this,"Internal server error",Toast.LENGTH_SHORT).show();
            }
            else
            {
                t7.setText("We sent you new password to your email, please check your inbox or spam folder");
                btnSend.setVisibility(View.INVISIBLE);
                btnSend.setEnabled(false);
            }

        }
    }

    static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

}
