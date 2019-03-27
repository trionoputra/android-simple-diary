package com.yondev.diary.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.text.Line;
import com.yondev.diary.R;
import com.yondev.diary.entity.Speech;
import com.yondev.diary.utils.Shared;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by ThinkPad on 4/22/2017.
 */

public class SpeechLayout extends RelativeLayout {
    private boolean flip;
    private int color;
    private String emoji;
    private String text;
    private View view;

    private EmojiconTextView v_text;
    private ImageView v_image;
    private RelativeLayout v_imageContent;
    private EmojiconTextView v_emo;

    public SpeechLayout(Context context) {
        super(context);
        this.initVar();
        this.initLayout();
    }
    public SpeechLayout(Context context,boolean flip) {
        super(context);
        this.initVar();

        this.flip = flip;
        this.initLayout();
    }

    public SpeechLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initVar();
        this.initLayout();
    }

    public SpeechLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initVar();
        this.initLayout();
    }

    private void initVar()
    {
        flip = false;
        color = Color.parseColor("#e1675a");
        emoji = "\uD83D\uDE03";
        text = "input your text";
    }

    private void initLayout()
    {
        this.setClickable(true);
        this.setFocusable(true);
        this.setLongClickable(true);

        if(view != null)
            this.removeView(view);

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        if(!flip)
        {
            view = inflater.inflate(R.layout.speech_left,null);
            params.gravity  = Gravity.LEFT;
        }
        else
        {
            view = inflater.inflate(R.layout.speech_right,null);
            params.gravity  = Gravity.RIGHT;
        }

        this.setLayoutParams(params);


        v_text = (EmojiconTextView)view.findViewById(R.id.emojiTextView);
        v_image = (ImageView)view.findViewById(R.id.imageView3);
        v_imageContent = (RelativeLayout)view.findViewById(R.id.speechWrapper);
        v_emo = (EmojiconTextView)view.findViewById(R.id.emojiTextView2);

        v_text.setText(this.text);
        v_image.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        v_emo.setText(this.emoji);

        v_text.setTypeface(Shared.appfont);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(this.color);
        gd.setCornerRadius(10);

        v_imageContent.setBackgroundDrawable(gd);
        v_imageContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechLayout.this.callOnClick();
            }
        });
        v_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechLayout.this.callOnClick();
            }
        });

        this.addView(view);
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
        this.initLayout();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        v_image.setColorFilter(this.color, PorterDuff.Mode.MULTIPLY);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(this.color);
        gd.setCornerRadius(10);

        v_imageContent.setBackgroundDrawable(gd);
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
        v_emo.setText(this.emoji);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        v_text.setText(this.text);
    }
}
