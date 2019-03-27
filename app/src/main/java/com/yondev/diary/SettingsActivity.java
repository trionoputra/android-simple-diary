package com.yondev.diary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yondev.diary.adapter.SettingListAdapter;
import com.yondev.diary.utils.Shared;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SettingListAdapter adapter;
    private ListView lv;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_settings);

        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        adapter = new SettingListAdapter(this);

        lv = (ListView)findViewById(R.id.listView1);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position)
        {
            case 0:
                intent = new Intent(SettingsActivity.this,PasswordLockActivity.class);
                break;
            case 1:
                intent = new Intent(SettingsActivity.this,BackupRestoreActivity.class);
                break;
            case 2:
                intent = new Intent(SettingsActivity.this,AboutActivity.class);
                break;
        }

        if(intent != null)
        {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, android.R.anim.fade_out);
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
