package com.yondev.diary.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.yondev.diary.R;
import com.yondev.diary.adapter.ImojiListAdapter;
import com.yondev.diary.utils.Shared;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;

/**
 * Created by ThinkPad on 4/20/2017.
 */

public class SpeechDialog extends Dialog implements TextWatcher, View.OnClickListener {
    private Context context;
    private SpeechListener listener;
    private EmojiconEditText txtContent;
    private ImageView btnImojiKeyBoard;
    private SpeechLayout speechLayout;
    public SpeechDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.view_speech);

        txtContent = (EmojiconEditText)findViewById(R.id.txtContent);
        btnImojiKeyBoard = (ImageView) findViewById(R.id.btnImoji);

        txtContent.addTextChangedListener(this);
        findViewById(R.id.btnImoji2).setOnClickListener(this);
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnFlip).setOnClickListener(this);
        findViewById(R.id.btnColor).setOnClickListener(this);

        speechLayout = (SpeechLayout)findViewById(R.id.SpeechLayout);
        txtContent.setTypeface(Shared.appfont);

        iniEmojiPopup();

    }

    public void setSpeechListener(SpeechListener listener)
    {
        this.listener = listener;
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
                if(listener != null)
                    listener.onFinish(this.speechLayout);

                dismiss();
                break;
            case R.id.btnFlip:
                this.speechLayout.setFlip(!this.speechLayout.isFlip());
                break;
            case R.id.btnColor:
                openColorPicker();
                break;

        }
    }

    public interface SpeechListener {
        public void onFinish(SpeechLayout result);
    }

    private void openColorPicker()
    {
        ColorPickerDialogBuilder
                .with(context)
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
        ImojiDialog d = new ImojiDialog(getContext());
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
        emojIcon = new EmojIconActions(this.context,findViewById(R.id.mainDialog),txtContent,btnImojiKeyBoard);
        emojIcon.setIconsIds(R.mipmap.ic_action_keyboard_w,R.mipmap.emoji_people_w);
        emojIcon.ShowEmojIcon();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}
