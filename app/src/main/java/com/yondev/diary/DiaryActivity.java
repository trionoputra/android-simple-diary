package com.yondev.diary;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.yondev.diary.entity.DetailDiary;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.sqlite.DatabaseHelper;
import com.yondev.diary.sqlite.DatabaseManager;
import com.yondev.diary.sqlite.ds.DiaryDataSource;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.ConfirmDialog;
import com.yondev.diary.widget.ImojiDialog;
import com.yondev.diary.widget.LoadingDialog;
import com.yondev.diary.widget.SpeechLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;
import hani.momanii.supernova_emoji_library.emoji.People;

public class DiaryActivity extends AppCompatActivity implements OnClickListener, View.OnFocusChangeListener,TextWatcher {
	private EmojiconEditText lasteditext;
	private boolean isEditMode = false;
	private TextView diaryDate;
	private DatePickerDialog datePickerDialog;
	private SimpleDateFormat dateFormat;

	private EmojiconTextView emojiTextView;
	private ImageView btnImoji;
	private EmojiconEditText txtTitle;
	private EmojiconEditText txtContent;

    public LinearLayout diaryWrapper;

	private boolean isSpeechOpen = false;
	private LoadingDialog loading;

    private Diary diary;
    public static Diary diaryPreview = new Diary();
    private boolean hasEditChanges = false;

	private InterstitialAd Interstitial;
	private AdRequest adRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary);

		Shared.initialize(getBaseContext());

		adRequest = new AdRequest.Builder()
				.build();

		Interstitial = new InterstitialAd(DiaryActivity.this);
		Interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

		DatabaseManager.initializeInstance(new DatabaseHelper(DiaryActivity.this));


		txtTitle = (EmojiconEditText)findViewById(R.id.txtTitle);
		txtContent = (EmojiconEditText)findViewById(R.id.txtContent);
		emojiTextView = (EmojiconTextView)findViewById(R.id.emojiTextView);
		diaryDate =(TextView)findViewById(R.id.txtDate);
		btnImoji = (ImageView)findViewById(R.id.btnImoji);
        diaryWrapper = (LinearLayout) findViewById(R.id.diaryWrapper);

		diaryDate.setTypeface(Shared.appfontLight);
		txtTitle.setTypeface(Shared.appfont);
		txtContent.setTypeface(Shared.appfont);

		btnImoji.setOnClickListener(this);
		emojiTextView.setOnClickListener(this);
        diaryWrapper.setOnClickListener(this);

		Emojicon emoji1 = People.DATA[6];
		emojiTextView.setText(emoji1.getEmoji());

		Button btnSave =(Button) findViewById(R.id.btnSave);
		btnSave.setTypeface(Shared.appfontBold);

		btnSave.setOnClickListener(this);
		findViewById(R.id.scrollDiaryWrapper).setOnClickListener(this);
		findViewById(R.id.btnBack).setOnClickListener(this);
		findViewById(R.id.btnDelete).setOnClickListener(this);
		findViewById(R.id.dateWrapper).setOnClickListener(this);
		findViewById(R.id.btnSpeech).setOnClickListener(this);

        findViewById(R.id.btnPreview).setOnClickListener(this);

		lasteditext = txtContent;

		txtTitle.setOnFocusChangeListener(this);
		txtContent.setOnFocusChangeListener(this);

		dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		diaryDate.setText(dateFormat.format(new Date()));
		diaryDate.setTag(new Date());

		Calendar c = Calendar.getInstance();
		datePickerDialog = new DatePickerDialog(this, dateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

		iniEmojiPopup();

		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            isEditMode = true;
            SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
            DiaryDataSource DS = new DiaryDataSource(db);
            diary = DS.get(extras.getInt("code"));
            DatabaseManager.getInstance().closeDatabase();

            initEditView();
        }

        if(isEditMode)
            findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.btnDelete).setVisibility(View.GONE);

		loading = new LoadingDialog(this);
	}

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, monthOfYear, dayOfMonth);
			diaryDate.setText(dateFormat.format(calendar.getTime()));
			diaryDate.setTag(calendar.getTime());

            hasEditChanges = false;
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnBack:
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				break;
			case R.id.dateWrapper:
				showDatePicker();
				break;
			case R.id.btnDelete:
				delete();
				break;
			case R.id.diaryWrapper:
				//lasteditext.requestFocus();
				///showSoftKeyboard(lasteditext);
				break;
			case R.id.scrollDiaryWrapper:
				//lasteditext.requestFocus();
				//showSoftKeyboard(lasteditext);
				break;
			case R.id.emojiTextView:
				openImoji(1);
				break;
			case R.id.btnSpeech:
				openSpeech();
				break;
			case R.id.btnSave:
				save();
				break;
            case R.id.btnPreview:
                showPreview();
                break;

		}

		if(v.getTag() != null)
		{
			if(v.getTag().toString().equals("speech"))
			{
				if(!isSpeechOpen)
				{
					isSpeechOpen = true;
					SpeechLayout speech = (SpeechLayout)v;

					Intent intent = new Intent(DiaryActivity.this,SpeechActivity.class);
					intent.putExtra("color",speech.getColor());
					intent.putExtra("emoji",speech.getEmoji());
					intent.putExtra("flip",speech.isFlip());
					intent.putExtra("text",speech.getText());
					intent.putExtra("text",speech.getText());
					intent.putExtra("index",diaryWrapper.indexOfChild(v));
					startActivityForResult(intent,10004);
					overridePendingTransition(android.R.anim.fade_in, 0);
				}
			}
		}
	}

	private void delete()
	{
        final ConfirmDialog dialog = new ConfirmDialog(this,"Delete this from Diary?","Yes","No");
        dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOK() {
                SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
                DiaryDataSource DS = new DiaryDataSource(db);
                DS.delete(diary.getCode());
                DatabaseManager.getInstance().closeDatabase();
                dialog.dismiss();
				setResult(RESULT_OK);
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

	private void showDatePicker()
	{
		datePickerDialog.show();
	}

	private void openImoji(final int type)
	{
		ImojiDialog d = new ImojiDialog(this);
		d.setImojiListener(new ImojiDialog.ImojiListener() {
			@Override
			public void onSelect(String result) {
				if(type == 1)
				{
					emojiTextView.setText(result);
				}
			}
		});
		d.show();
		emojIcon.closeEmojIcon();
		hideSoftKeyboard();
	}

	private void openSpeech()
	{
		/*SpeechDialog d = new SpeechDialog(this);
		d.setSpeechListener(new SpeechDialog.SpeechListener() {
			@Override
			public void onFinish(SpeechLayout result) {
				reTransformLayout(result);
			}
		});
		d.show();
		hideSoftKeyboard();
		*/
		if(!isSpeechOpen)
		{
			isSpeechOpen = true;
			Intent intent = new Intent(DiaryActivity.this,SpeechActivity.class);
			startActivityForResult(intent,10004);
			overridePendingTransition(android.R.anim.fade_in, 0);
		}

	}

	private LayoutInflater inflater;
	private void reTransformLayout()
	{

        for (int i = 0; i < diaryWrapper.getChildCount();i++)
        {
            if(diaryWrapper.getChildAt(i) instanceof EmojiconEditText)
            {
                EmojiconEditText emo = (EmojiconEditText) diaryWrapper.getChildAt(i);
				emo.addTextChangedListener(this);
				emo.setHint("");
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)emo.getLayoutParams();
                if(i == diaryWrapper.getChildCount() - 1)
                {
                    params.weight = 1;
                }
                else
                {
                    params.weight = 0;
                }
                emo.setLayoutParams(params);
            }
            else if(diaryWrapper.getChildAt(i) instanceof SpeechLayout)
            {
                if(i == diaryWrapper.getChildCount() - 1)
                {
					EmojiconEditText im = (EmojiconEditText)inflater.inflate(R.layout.edittext_content,null);
                    diaryWrapper.addView(im);
					im.addTextChangedListener(this);
					emojIcon.addEmojiconEditTextList(im);
					im.setTypeface(Shared.appfont);
					lasteditext = im;
					im.requestFocus();

                }
            }
        }

		this.removeEmptyEditText();

        hasEditChanges = true;
	}

	public void hideSoftKeyboard() {
		if(getCurrentFocus()!=null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		view.requestFocus();
		//inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus)
		{
			if(v.getTag().toString().equals("content"))
				lasteditext = (EmojiconEditText) v;

			// showSoftKeyboard(v);
		}
	}

	private EmojIconActions emojIcon;
	private void iniEmojiPopup()
	{
		emojIcon = new EmojIconActions(this,findViewById(R.id.mainLayout),lasteditext,btnImoji);
		emojIcon.addEmojiconEditTextList(txtTitle,txtContent);
		emojIcon.setIconsIds(R.mipmap.ic_action_keyboard_w,R.mipmap.emoji_people_w);
		emojIcon.ShowEmojIcon();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			if(requestCode == 10004)
			{
				Bundle extras = data.getExtras();
				if(extras != null)
				{
					if(extras.getBoolean("delete"))
					{
						diaryWrapper.removeViewAt(extras.getInt("index"));
						this.removeEmptyEditText();
					}
					else
					{
						if(extras.getInt("index") == -1)
						{
							SpeechLayout s = new SpeechLayout(this,extras.getBoolean("flip"));
							s.setText(extras.getString("text"));
							s.setEmoji(extras.getString("emoji"));
							s.setColor(extras.getInt("color"));
							s.setTag("speech");
							diaryWrapper.addView(s);
							s.setOnClickListener(this);
							s.post(new Runnable() {
								@Override
								public void run() {
									reTransformLayout();
								}
							});
						}
						else
						{
							SpeechLayout s = (SpeechLayout)diaryWrapper.getChildAt(extras.getInt("index"));
							s.setColor(extras.getInt("color"));
							s.setText(extras.getString("text"));
							s.setEmoji(extras.getString("emoji"));
							s.setFlip(extras.getBoolean("flip"));
						}
					}
				}
			}
		}

		isSpeechOpen = false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		if(s.toString().equals(""))
		{
			this.removeEmptyEditText();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
        hasEditChanges = true;
	}

	private void removeEmptyEditText()
	{
		ArrayList<Integer> removeIndex = new ArrayList<Integer>();
		boolean isRemove = false;
		for (int i = 0; i < diaryWrapper.getChildCount();i++)
		{
			Object prev = diaryWrapper.getChildAt(i);
			if(diaryWrapper.getChildAt(i) instanceof EmojiconEditText)
			{
				if(i != diaryWrapper.getChildCount() - 1)
				{
					EmojiconEditText emo = (EmojiconEditText) diaryWrapper.getChildAt(i);
					if(emo.getText().toString().equals(""))
					{
						isRemove = true;
						removeIndex.add(i);
					}
				}
			}
		}

		if(isRemove)
		{
			for(int j = 0;j < removeIndex.size();j++)
			{
				diaryWrapper.removeViewAt(removeIndex.get(j));
			}
		}

		removeIndex = new ArrayList<Integer>();
		isRemove = false;
		Object prev = null;
		for (int i = 0; i < diaryWrapper.getChildCount();i++)
		{
			if(prev != null)
			{
				if(diaryWrapper.getChildAt(i) instanceof EmojiconEditText && prev instanceof EmojiconEditText )
				{
					((EmojiconEditText) prev).setText(((EmojiconEditText) prev).getText() + " " + ((EmojiconEditText) diaryWrapper.getChildAt(i)).getText());
					removeIndex.add(i);
					isRemove = true;
				}
			}

			prev = diaryWrapper.getChildAt(i);
		}

		if(isRemove)
		{
			for(int j = 0;j < removeIndex.size();j++)
			{
				diaryWrapper.removeViewAt(removeIndex.get(j));
			}
		}
	}

	@Override
	public void onBackPressed() {

        if(hasUpdate())
        {
			final ConfirmDialog dialog = new ConfirmDialog(this,"Save changes?","Yes","No");
            dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
                @Override
                public void onOK() {
					dialog.dismiss();
                    save();
                }
                @Override
                public void onCancel() {
                    finish();
                    overridePendingTransition(0, android.R.anim.fade_out);
                }
            });
            dialog.show();
        }
        else
            super.onBackPressed();
	}

	private boolean hasUpdate()
    {
        boolean hasUpdate = false;

        if(!txtTitle.getText().toString().trim().equals(""))
            hasUpdate = true;
        else if(diaryWrapper.getChildCount() > 1)
            hasUpdate = true;
        else if(diaryWrapper.getChildCount()  == 1)
            if(!((EmojiconEditText)diaryWrapper.getChildAt(0)).getText().toString().trim().equals(""))
                hasUpdate = true;

        if(isEditMode)
            hasUpdate = hasEditChanges;

        return hasUpdate;
    }

    private void save()
    {
		if(txtTitle.getText().toString().trim().equals(""))
		{
			Toast.makeText(this,"fill title",Toast.LENGTH_SHORT).show();
			return;
		}
        else if(diaryWrapper.getChildCount()  == 1)
            if(((EmojiconEditText)diaryWrapper.getChildAt(0)).getText().toString().trim().equals(""))
            {
                Toast.makeText(this,"fill content",Toast.LENGTH_SHORT).show();
                return;
            }


        Diary d = parseDiary();

		SaveAsync save = new SaveAsync(d);
		save.execute();
    }

    private Diary parseDiary()
    {
        Diary d = new Diary();

        if(isEditMode)
            d.setCode(diary.getCode());

        d.setTitle(txtTitle.getText().toString());
        d.setFeel(emojiTextView.getText().toString());
        d.setDate((Date) diaryDate.getTag());

        ArrayList<DetailDiary> details = new ArrayList<DetailDiary>();
        for (int i = 0; i < diaryWrapper.getChildCount();i++)
        {
            DetailDiary detail = new DetailDiary();
            if(diaryWrapper.getChildAt(i) instanceof EmojiconEditText)
            {
                EmojiconEditText emo = (EmojiconEditText) diaryWrapper.getChildAt(i);
                detail.setType(1);
                detail.setContent(emo.getText().toString());
            }
            else if(diaryWrapper.getChildAt(i) instanceof SpeechLayout)
            {
                SpeechLayout im = (SpeechLayout)diaryWrapper.getChildAt(i);;
                detail.setType(2);
                detail.setContent(im.getText());
                detail.setEmoji(im.getEmoji());
                detail.setFlip(im.isFlip());
                detail.setColor(im.getColor());
            }
            details.add(detail);
        }

        d.setDetail(details);

        return d;
    }

    private void showPreview()
    {
        diaryPreview = this.parseDiary();
        Intent intent = new Intent(DiaryActivity.this, PreviewActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

	public class SaveAsync extends AsyncTask<String, String, String>
	{
		private Diary diary;
		public SaveAsync(Diary diary)
		{
			this.diary = diary;
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

				SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
				DiaryDataSource DS = new DiaryDataSource(db);

                if(!isEditMode)
				    DS.insert(this.diary);
                else
                    DS.update(this.diary,this.diary.getCode());

				DatabaseManager.getInstance().closeDatabase();

				result = "1";

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
			if(!isEditMode)
				displayInterstitial();

			setResult(RESULT_OK);
			finish();
			overridePendingTransition(0, android.R.anim.fade_out);

		}
	}

	private void initEditView()
    {
        txtTitle.setText(diary.getTitle());
        emojiTextView.setText(diary.getFeel());
        diaryDate.setText(dateFormat.format(diary.getDate()));
        diaryDate.setTag(diary.getDate());

        for (int i =0; i < diary.getDetail().size();i++)
        {
           DetailDiary detail = diary.getDetail().get(i);
            if(detail.getType() == 1)
            {
                EmojiconEditText im = (EmojiconEditText)inflater.inflate(R.layout.edittext_content,null);
                im.setText(detail.getContent());
                diaryWrapper.addView(im);
                im.addTextChangedListener(this);
                emojIcon.addEmojiconEditTextList(im);
            }
            else if(detail.getType() == 2)
            {
                SpeechLayout s = new SpeechLayout(this,detail.isFlip());
                s.setText(detail.getContent());
                s.setEmoji(detail.getEmoji());
                s.setColor(detail.getColor());
                s.setTag("speech");
                diaryWrapper.addView(s);
                s.setOnClickListener(this);
            }
        }

		reTransformLayout();
    }

	@Override
	protected void onResume() {
		super.onResume();
		/*lasteditext.requestFocus();
		lasteditext.postDelayed(new Runnable() {
			@Override
			public void run() {
				showSoftKeyboard(lasteditext);
			}
		}, 100);
		*/
		Interstitial.loadAd(adRequest);
	}

	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (Interstitial.isLoaded()) {
			Interstitial.show();
		}
	}
}
