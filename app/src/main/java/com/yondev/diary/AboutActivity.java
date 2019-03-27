package com.yondev.diary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yondev.diary.utils.Shared;

public class AboutActivity extends AppCompatActivity  implements View.OnClickListener {
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_about);

        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        findViewById(R.id.btnBack).setOnClickListener(this);

        TextView t5 = (TextView)findViewById(R.id.textView5);

        TextView t1 = (TextView)findViewById(R.id.textView1);
        TextView t2 = (TextView)findViewById(R.id.textView2);
        TextView t3 = (TextView)findViewById(R.id.textView3);
        TextView t4 = (TextView)findViewById(R.id.textView4);

        t5.setTypeface(Shared.appfontBold);
        t1.setTypeface(Shared.appfontBold);
        t2.setTypeface(Shared.appfontLight);
        t3.setTypeface(Shared.appfontBold);
        t4.setTypeface(Shared.appfontLight);

        try {
            t2.setText("v"+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

}
