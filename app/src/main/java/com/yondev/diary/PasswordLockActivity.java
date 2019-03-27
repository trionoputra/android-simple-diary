package com.yondev.diary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yondev.diary.entity.Setting;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.DiaryDataSource;
import com.yondev.diary.sqlite.ds.SettingDataSource;
import com.yondev.diary.utils.Constant;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.ConfirmDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordLockActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtPassword;
    private EditText txtEmail;
    private EditText txtEmailConfirm;

    private Button btnDeactivate;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_password_lock);

        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        txtPassword = (EditText)findViewById(R.id.editText1) ;
        txtEmail = (EditText)findViewById(R.id.editText2) ;
        txtEmailConfirm = (EditText)findViewById(R.id.editText3) ;

        btnDeactivate = (Button)findViewById(R.id.btnDeactivate);
        Button btnSave = (Button)findViewById(R.id.btnSave);

        btnDeactivate.setVisibility(View.GONE);

        findViewById(R.id.btnBack).setOnClickListener(this);
        btnSave.setOnClickListener(this);

        btnDeactivate.setOnClickListener(this);

        btnDeactivate.setTypeface(Shared.appfontBold);
        btnSave.setTypeface(Shared.appfontBold);

        TextView t5 = (TextView)findViewById(R.id.textView5);
        t5.setTypeface(Shared.appfontBold);

        txtPassword.setTypeface(Shared.appfontLight);
        txtEmail.setTypeface(Shared.appfontLight);
        txtEmailConfirm.setTypeface(Shared.appfontLight);

        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        SettingDataSource DS = new SettingDataSource(db);
        Setting password = DS.get(Constant.SETTING_PASSWORD);
        Setting email = DS.get(Constant.SETTING_EMAIL);
        Setting isLock = DS.get(Constant.SETTING_IS_LOCK);

        if(isLock.getValue().equals("true"))
        {
            txtPassword.setText(password.getValue());
            txtEmail.setText(email.getValue());
            txtEmailConfirm.setText(email.getValue());

            btnDeactivate.setVisibility(View.VISIBLE);
        }

        DatabaseManager.getInstance().closeDatabase();
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
            case R.id.btnDeactivate:
                deactivate();
                break;
            case R.id.btnSave:
                save();
                break;
        }
    }

    private void save()
    {
        if(txtPassword.getText().toString().equals("") || txtEmail.getText().toString().equals("") || txtEmailConfirm.getText().toString().equals(""))
        {
            Toast.makeText(this,"Field can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(txtPassword.getText().toString().length() < 4)
        {
            Toast.makeText(this, "Password at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!txtEmail.getText().toString().equals(txtEmailConfirm.getText().toString()))
        {
            Toast.makeText(this,"Email confirmation doesn't match",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(txtEmail.getText().toString()))
        {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }



        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        SettingDataSource DS = new SettingDataSource(db);
        Setting email = DS.get(Constant.SETTING_EMAIL);
        Setting password = DS.get(Constant.SETTING_PASSWORD);
        Setting isLock = DS.get(Constant.SETTING_IS_LOCK);

        email.setValue(txtEmail.getText().toString());
        DS.update(email);

        password.setValue(txtPassword.getText().toString());
        DS.update(password);

        isLock.setValue("true");
        DS.update(isLock);

        DatabaseManager.getInstance().closeDatabase();

        Toast.makeText(this, "Password and Email recovery saved", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void deactivate()
    {
        final ConfirmDialog dialog = new ConfirmDialog(this,"Deactivate Password Lock?","Yes","No");
        dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOK() {
                SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
                SettingDataSource DS = new SettingDataSource(db);
                Setting email = DS.get(Constant.SETTING_EMAIL);
                Setting password = DS.get(Constant.SETTING_PASSWORD);
                Setting isLock = DS.get(Constant.SETTING_IS_LOCK);

                email.setValue("");
                DS.update(email);

                password.setValue("");
                DS.update(password);

                isLock.setValue("false");
                DS.update(isLock);

                DatabaseManager.getInstance().closeDatabase();

                Toast.makeText(PasswordLockActivity.this, "Password Lock Deactivated", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean isValidEmail(String email) {
        String PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
