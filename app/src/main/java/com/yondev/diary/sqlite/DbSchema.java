package com.yondev.diary.sqlite;

import com.yondev.diary.utils.Constant;

public interface DbSchema {
	
	String DB_NAME = "com_yondev_diary.db";
	int DB_VERSION = 1;
	
	String TBL_DIARY = "diary";	
	String COL_DIARY_CODE = "code";
	String COL_DIARY_TITLE = "title";
	String COL_DIARY_DATE = "post_date";
	String COL_DIARY_FEEL = "feel";
	
	String CREATE_TBL_DIARY = "CREATE TABLE "
								+ TBL_DIARY
								+ "(" 
									+ COL_DIARY_CODE  + " INTEGER PRIMARY KEY AUTOINCREMENT," 
									+ COL_DIARY_TITLE + " TEXT,"
									+ COL_DIARY_FEEL + " TEXT,"
									+ COL_DIARY_DATE + " DATETIME" 
								+ ");";

	String TBL_DIARY_DETAIL = "diary_detail";	
	String COL_DIARY_DETAIL_CODE = "code";
	String COL_DIARY_DETAIL_CODE_DIARY = "code_diary";
	String COL_DIARY_DETAIL_TYPE = "type";
	String COL_DIARY_DETAIL_COLOR = "color";
	String COL_DIARY_DETAIL_CONTENT = "content";
	String COL_DIARY_DETAIL_FLIP = "flip";
	String COL_DIARY_DETAIL_EMOJI = "emoji";
	
	String CREATE_TBL_DIARY_DETAIL = "CREATE TABLE "
								+ TBL_DIARY_DETAIL
								+ "(" 
									+ COL_DIARY_DETAIL_CODE  + " INTEGER PRIMARY KEY AUTOINCREMENT," 
									+ COL_DIARY_DETAIL_CODE_DIARY + " INTEGER,"
									+ COL_DIARY_DETAIL_TYPE + " TEXT,"
									+ COL_DIARY_DETAIL_COLOR + " INTEGER,"
									+ COL_DIARY_DETAIL_CONTENT + " TEXT,"
									+ COL_DIARY_DETAIL_FLIP + " INTEGER,"
									+ COL_DIARY_DETAIL_EMOJI + " TEXT"
								+ ");";
	
	
	
	String TBL_SETTING = "settings";	
	String COL_SETTING_CODE = "code";
	String COL_SETTING_NAME = "name";
	String COL_SETTING_VALUE = "value";
	String COL_SETTING_VALUEBLOB = "value_blob";
			
	String CREATE_TBL_SETTING = "CREATE TABLE "
								+ TBL_SETTING 
								+ "(" 
									+ COL_SETTING_CODE  + " TEXT PRIMARY KEY," 
									+ COL_SETTING_NAME + " TEXT," 
									+ COL_SETTING_VALUE + " TEXT,"
									+ COL_SETTING_VALUEBLOB + " BLOB"
								+ ");";


	String INSERT_TBL_SETTING = " INSERT INTO " + TBL_SETTING+ " ("+ COL_SETTING_CODE +","+ COL_SETTING_NAME +","+ COL_SETTING_VALUE +") VALUES ('"+ Constant.SETTING_IS_LOCK +"','is lock','false'),('"+ Constant.SETTING_PASSWORD +"','password',''),('"+ Constant.SETTING_EMAIL +"','email','');" ;


	String DROP_TBL_DIARY = "DROP TABLE IF EXISTS "+ TBL_DIARY;
	String DROP_TBL_DIARY_DETAIL = "DROP TABLE IF EXISTS "+ TBL_DIARY_DETAIL;
	String DROP_TBL_SETTING = "DROP TABLE IF EXISTS "+ TBL_SETTING;
}
