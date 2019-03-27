package com.yondev.diary.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ThinkPad on 5/1/2017.
 */

public class BackupObject {
    private Date created_on;
    private ArrayList<Diary> diary;

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public ArrayList<Diary> getDiary() {
        return diary;
    }

    public void setDiary(ArrayList<Diary> diary) {
        this.diary = diary;
    }
}
