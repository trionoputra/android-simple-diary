package com.yondev.diary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.yondev.diary.adapter.DiaryListAdapter;
import com.yondev.diary.entity.BackupObject;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.entity.Setting;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.DiaryDataSource;
import com.yondev.diary.sqlite.ds.SettingDataSource;
import com.yondev.diary.utils.Constant;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.ConfirmDialog;
import com.yondev.diary.widget.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class BackupRestoreActivity extends AppCompatActivity  implements View.OnClickListener {
    private LoadingDialog loading;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_backup_restore);

        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.btnBack).setOnClickListener(this);

        TextView t5 = (TextView)findViewById(R.id.textView5);
        t5.setTypeface(Shared.appfontBold);

        Button btnBackup = (Button)findViewById(R.id.button1);
        Button btnRestore = (Button)findViewById(R.id.button2);

        btnBackup.setTypeface(Shared.appfontBold);
        btnRestore.setTypeface(Shared.appfontBold);

        btnBackup.setOnClickListener(this);
        btnRestore.setOnClickListener(this);

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
                backup();
                break;
            case R.id.button2:
                restore();
                break;
        }
    }

    private void backup()
    {
        final String filename = Shared.getAppDir() + File.separator   + "DiaryKu_" + Shared.dateformatBackup.format(new Date()) + ".dku";
        final ConfirmDialog dialog = new ConfirmDialog(this,"Backup file will be saved to " + filename,"Save","Cancel");
        dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOK() {
                dialog.dismiss();
                createBackupFile(filename);

            }
            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createBackupFile(String name)
    {
        backUpAsync bak = new backUpAsync(name);
        bak.execute();
    }

    public class backUpAsync extends AsyncTask<String, String, String>
    {
        private String name;
        public backUpAsync(String name)
        {
            this.name = name;
        }

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

            ArrayList<Diary> dt = new ArrayList<>();
            SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
            DiaryDataSource DS = new DiaryDataSource(db);
            dt = DS.getAll();
            DatabaseManager.getInstance().closeDatabase();

            if(dt.size() == 0 )
                result = "2";
            else
            {
                BackupObject obj = new BackupObject();
                obj.setCreated_on(new Date());
                obj.setDiary(dt);

                Gson gson = new Gson();
                String back = gson.toJson(obj);
                String back64 = Base64.encodeToString(back.getBytes(),Base64.DEFAULT);
                FileOutputStream fos = null;

                try {

                    fos = new FileOutputStream(this.name);
                    OutputStreamWriter OutDataWriter  = new OutputStreamWriter(fos);
                    OutDataWriter.write(back64);
                    OutDataWriter.close();
                    fos.flush();
                    fos.close();

                    result = "1";

                } catch (FileNotFoundException e) {
                    result = "0";
                    e.printStackTrace();

                } catch (IOException e) {
                    result = "0";
                    e.printStackTrace();
                }


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
                Toast.makeText(BackupRestoreActivity.this,"Backup file created",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("2"))
            {
                Toast.makeText(BackupRestoreActivity.this,"No diary can backup",Toast.LENGTH_SHORT).show();
            }
            else
            {

                Toast.makeText(BackupRestoreActivity.this,"Failed create backup file",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void restore()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("file/*");
        startActivityForResult(Intent.createChooser(intent, "SELECT '.dku' FILE"),10001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == 10001)
            {
                final ConfirmDialog dialog = new ConfirmDialog(this,"your diary will be replaced with backup file","Ok","Cancel");
                dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
                    @Override
                    public void onOK() {
                        dialog.dismiss();
                        RestoreAsync restore = new RestoreAsync(data.getData());
                        restore.execute();

                    }
                    @Override
                    public void onCancel() {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }
        }

    }


    public class RestoreAsync extends AsyncTask<String, String, String>
    {
        private Uri URI;
        public RestoreAsync(Uri URI)
        {
            this.URI = URI;
        }
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

            try {

                InputStream in = getContentResolver().openInputStream(URI);
                byte b[] = Shared.getStringFromInputStream(in);
                byte ba[] = Base64.decode(b,Base64.DEFAULT);

                String back = new String(ba);

                Gson gson = new Gson();
                BackupObject bo = gson.fromJson(back,BackupObject.class);
                SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
                DiaryDataSource DS = new DiaryDataSource(db);
                DS.truncate();
                for(Diary diary : bo.getDiary())
                {
                    DS.insert(diary);
                }
                DatabaseManager.getInstance().closeDatabase();
                Shared.write("isRefresh","true");
                result = "1";
                Thread.sleep(1000);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
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
                Toast.makeText(BackupRestoreActivity.this,"Restore file succeed",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(BackupRestoreActivity.this,"Failed restore diary",Toast.LENGTH_SHORT).show();
            }
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
