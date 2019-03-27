package com.yondev.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.yondev.diary.utils.Shared;
import com.yondev.diary.widget.ConfirmDialog;
import com.yondev.diary.widget.ImojiDialog;
import com.yondev.diary.widget.SpeechLayout;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class SpeechActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener  {
    private EmojiconEditText txtContent;
    private ImageView btnImojiKeyBoard;
    private SpeechLayout speechLayout;
    private int indexEdit = -1;
    private ImageButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.initialize(getBaseContext());

        setContentView(R.layout.activity_speech);

        txtContent = (EmojiconEditText)findViewById(R.id.txtContent);
        btnImojiKeyBoard = (ImageView) findViewById(R.id.btnImoji);
        btnDelete = (ImageButton)findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(this);
        txtContent.addTextChangedListener(this);
        findViewById(R.id.btnImoji2).setOnClickListener(this);

        findViewById(R.id.btnFlip).setOnClickListener(this);
        findViewById(R.id.btnColor).setOnClickListener(this);

        Button btnDone = (Button)findViewById(R.id.btnSave);
        btnDone.setOnClickListener(this);

        btnDone.setTypeface(Shared.appfontBold);

        speechLayout = (SpeechLayout)findViewById(R.id.SpeechLayout);

        iniEmojiPopup();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            speechLayout.setColor(extras.getInt("color"));
            speechLayout.setText(extras.getString("text"));
            speechLayout.setEmoji(extras.getString("emoji"));
            speechLayout.setFlip(extras.getBoolean("flip"));
            indexEdit = extras.getInt("index");
            btnDelete.setVisibility(View.VISIBLE);
            txtContent.setText(extras.getString("text"));
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.speechLayout.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnImoji2:
                openImoji(1);
                break;
            case R.id.btnSave:
                Intent intent = getIntent();
                intent.putExtra("delete",false);
                intent.putExtra("color",this.speechLayout.getColor());
                intent.putExtra("emoji",this.speechLayout.getEmoji());
                intent.putExtra("flip",this.speechLayout.isFlip());
                intent.putExtra("text",this.speechLayout.getText());
                intent.putExtra("index",indexEdit);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
                break;
            case R.id.btnFlip:
                this.speechLayout.setFlip(!this.speechLayout.isFlip());
                break;
            case R.id.btnColor:
                openColorPicker();
                break;
            case R.id.btnDelete:
                deleteSpeech();
                break;
        }
    }
    private void openColorPicker()
    {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(speechLayout.getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        speechLayout.setColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .build()
                .show();
    }
    private void openImoji(final int type)
    {
        ImojiDialog d = new ImojiDialog(this);
        d.setImojiListener(new ImojiDialog.ImojiListener() {
            @Override
            public void onSelect(String result) {
                if(type == 1)
                {
                    speechLayout.setEmoji(result);
                }
            }
        });
        d.show();
        emojIcon.closeEmojIcon();
        hideSoftKeyboard();
    }

    private EmojIconActions emojIcon;
    private void iniEmojiPopup()
    {
        emojIcon = new EmojIconActions(this,findViewById(R.id.mainDialog),txtContent,btnImojiKeyBoard);
        emojIcon.setIconsIds(R.mipmap.ic_action_keyboard_w,R.mipmap.emoji_people_w);
        emojIcon.ShowEmojIcon();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void deleteSpeech()
    {
        Intent intent = getIntent();
        intent.putExtra("delete",true);
        intent.putExtra("index",indexEdit);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);

        /*ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setConfirmListener(new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOK() {
                Intent intent = getIntent();
                intent.putExtra("delete",true);
                intent.putExtra("index",indexEdit);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
            @Override
            public void onCancel() {

            }
        });
        dialog.show();
        */
    }
}
