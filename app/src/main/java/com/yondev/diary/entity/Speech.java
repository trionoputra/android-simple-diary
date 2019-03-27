package com.yondev.diary.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThinkPad on 4/23/2017.
 */

public class Speech {
    private boolean flip;
    private int color;
    private String emoji;
    private String text;

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
