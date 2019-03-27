package com.yondev.diary.sqlite.ds;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yondev.diary.entity.Setting;
import com.yondev.diary.sqlite.DbSchema;

public class SettingDataSource {
	
	private SQLiteDatabase db;
	public SettingDataSource(SQLiteDatabase db)
	{
		this.db = db;
	}
	
	public long truncate()
	{
		return db.delete(DbSchema.TBL_SETTING,null,null);
	}
	
	public Setting get(String code) {
		 
		Setting item = new Setting();
		 
		 String selectQuery = " SELECT  * FROM " + DbSchema.TBL_SETTING  + 
						      " Where " +DbSchema.COL_SETTING_CODE + " = '"+code+"'";
		 
		Cursor c = db.rawQuery(selectQuery, null);
	
		if (c.moveToFirst()) {
			do {
				item.setCode(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_CODE)));
				item.setName(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_NAME)));
				item.setValue(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_VALUE)));
				item.setValueBlob(c.getBlob(c.getColumnIndex(DbSchema.COL_SETTING_VALUEBLOB)));
			} while (c.moveToNext());
		}
	
		return item;
	}

	public ArrayList<Setting> getAll() {
		 
		ArrayList<Setting> items = new ArrayList<Setting>();
		 
		 String selectQuery = " SELECT  * FROM " + DbSchema.TBL_SETTING + " order by " + DbSchema.COL_SETTING_CODE + " asc ";
		 
		Cursor c = db.rawQuery(selectQuery, null);
	
		if (c.moveToFirst()) {
			do {
				Setting item = new Setting();
				item.setCode(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_CODE)));
				item.setName(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_NAME)));
				item.setValue(c.getString(c.getColumnIndex(DbSchema.COL_SETTING_VALUE)));
				item.setValueBlob(c.getBlob(c.getColumnIndex(DbSchema.COL_SETTING_VALUEBLOB)));
				items.add(item);
			} while (c.moveToNext());
		}
	
		return items;
	}
	
	public long insert(Setting item)
	{
		ContentValues values = new ContentValues();
		values.put(DbSchema.COL_SETTING_CODE, item.getCode());
		values.put(DbSchema.COL_SETTING_NAME, item.getName());
		values.put(DbSchema.COL_SETTING_VALUE, item.getValue());
		values.put(DbSchema.COL_SETTING_VALUEBLOB, item.getValueBlob());
		return db.insert(DbSchema.TBL_SETTING, null, values);
	}
	
	public long update(Setting item)
	{
		ContentValues values = new ContentValues();
		values.put(DbSchema.COL_SETTING_CODE, item.getCode());
		values.put(DbSchema.COL_SETTING_NAME, item.getName());
		values.put(DbSchema.COL_SETTING_VALUE, item.getValue());
		values.put(DbSchema.COL_SETTING_VALUEBLOB, item.getValueBlob());
		return db.replace(DbSchema.TBL_SETTING, null, values);
	}
	
	public int delete(String code)
	{
		return db.delete(DbSchema.TBL_SETTING, DbSchema.COL_SETTING_CODE + "=" + code, null);
	}
}
