package com.yondev.diary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yondev.diary.entity.DetailDiary;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.DiaryDataSource;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.SpeechLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class

PreviewActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private Diary diary;
    private ImageButton btnEdit;
    private boolean isFromtimeline = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_preview);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        // Set up the user interaction to manually show or hide the system UI.


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            isFromtimeline = extras.getBoolean("fromTimeline");
            SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
            DiaryDataSource DS = new DiaryDataSource(db);
            diary = DS.get(extras.getInt("code"));
            DatabaseManager.getInstance().closeDatabase();
            btnEdit.setVisibility(View.VISIBLE);
        }
        else
        {
            btnEdit.setVisibility(View.GONE);
            diary = DiaryActivity.diaryPreview;
        }


        if(diary != null)
        {
            initEditView();
        }
        else
        {
            Toast.makeText(this,"Somethings wrong",Toast.LENGTH_SHORT).show();
            finish();
        }
            ;

        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    private LayoutInflater inflater;
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    private void initEditView()
    {
        EmojiconTextView txtTitle = (EmojiconTextView)findViewById(R.id.txtTitle);
        TextView diaryDate = (TextView)findViewById(R.id.txtDate);
        EmojiconTextView emojiTextView = (EmojiconTextView)findViewById(R.id.emojiTextView);
        LinearLayout diaryWrapper = (LinearLayout) findViewById(R.id.diaryWrapper);

        txtTitle.setText(diary.getTitle());
        emojiTextView.setText(diary.getFeel());
        diaryDate.setText(dateFormat.format(diary.getDate()));
        diaryDate.setTag(diary.getDate());

        diaryDate.setTypeface(Shared.appfontLight);
        txtTitle.setTypeface(Shared.appfont);

        for (int i =0; i < diary.getDetail().size();i++)
        {
            DetailDiary detail = diary.getDetail().get(i);
            if(detail.getType() == 1)
            {
                EmojiconTextView im = (EmojiconTextView)inflater.inflate(R.layout.textview_content,null);
                im.setText(detail.getContent());
                im.setTypeface(Shared.appfont);
                diaryWrapper.addView(im);
            }
            else if(detail.getType() == 2)
            {
                SpeechLayout s = new SpeechLayout(this,detail.isFlip());
                s.setText(detail.getContent());
                s.setEmoji(detail.getEmoji());
                s.setColor(detail.getColor());
                s.setTag("speech");
                diaryWrapper.addView(s);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnBack:
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
                break;
            case R.id.btnEdit:
                Intent intent = new Intent(PreviewActivity.this, DiaryActivity.class);
                intent.putExtra("code",diary.getCode());
                startActivityForResult(intent,TimeLineActivity.MAKE_DIARY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TimeLineActivity.MAKE_DIARY) {
            if(resultCode == Activity.RESULT_OK) {

                setResult(RESULT_OK);

            }

            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, android.R.anim.fade_out);
    }
}
