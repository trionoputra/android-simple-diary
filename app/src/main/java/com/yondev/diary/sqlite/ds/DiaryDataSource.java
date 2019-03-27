package com.yondev.diary.sqlite.ds;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yondev.diary.entity.DetailDiary;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.sqlite.DbSchema;
import com.yondev.diary.utils.Shared;

public class DiaryDataSource {
	private SQLiteDatabase db;
	public DiaryDataSource(SQLiteDatabase db)
	{
		this.db = db;
	}
	
	public long truncate()
	{
		db.delete(DbSchema.TBL_DIARY_DETAIL,null,null);
		return db.delete(DbSchema.TBL_DIARY,null,null);
	}
	
	public Diary get(int code) {

		Diary item = new Diary();
		 
		String selectQuery = 	" SELECT  * FROM " + DbSchema.TBL_DIARY   +
								" Where " +DbSchema.COL_DIARY_CODE + " = '"+code+"' ";
		
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
			
				item.setCode(c.getInt(c.getColumnIndex(DbSchema.COL_DIARY_CODE)));
				item.setFeel(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_FEEL)));
				item.setTitle(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_TITLE)));
				
				try { item.setDate(Shared.dateformat.parse(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_DATE)))); } catch (Exception e) {}

				String selectQueryDetail =  " SELECT  *  FROM " + DbSchema.TBL_DIARY_DETAIL  +
											" WHERE " +DbSchema.COL_DIARY_DETAIL_CODE_DIARY + " = '"+code+"'";

				Cursor cDetail = db.rawQuery(selectQueryDetail, null);
				
				ArrayList<DetailDiary> details = new ArrayList<DetailDiary>();
				if (cDetail.moveToFirst()) {
					do {

						DetailDiary  dt = new DetailDiary();
						dt.setCode(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CODE)));
						dt.setDiaryCode(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CODE_DIARY)));
						dt.setFlip(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_FLIP)) == 1 ? true : false);
						dt.setColor(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_COLOR)));
						dt.setContent(cDetail.getString(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CONTENT)));
						dt.setEmoji(cDetail.getString(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_EMOJI)));
                        dt.setType(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_TYPE)));
						details.add(dt);
					} while (cDetail.moveToNext());
				}
				
				 item.setDetail(details);
			
			} while (c.moveToNext());
		}
		return item;
	}
	
	
	public ArrayList<Diary> getAll() {
		return getAll(null,null,0,0);
	}
	
	public ArrayList<Diary> getAll(ArrayList<HashMap<String, String>> filter,String orderby,int limit,int offset) {
		 
		ArrayList<Diary> items = new ArrayList<Diary>();
		
		String selectQuery = " SELECT  *  FROM " + DbSchema.TBL_DIARY;


		String _orderBy =  " ORDER BY " + DbSchema.COL_DIARY_DATE + " DESC ";
		if(orderby != null)
			_orderBy =  " ORDER BY " + orderby;

		String _limitOffset =  " ";
		if(limit != 0 || offset != 0)
		{
			_limitOffset = " LIMIT " + offset + "," + limit;
		}

		selectQuery += _orderBy;
		selectQuery += _limitOffset;

		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				Diary item = new Diary();
				item.setCode(c.getInt(c.getColumnIndex(DbSchema.COL_DIARY_CODE)));
				item.setFeel(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_FEEL)));
				item.setTitle(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_TITLE)));

				try { item.setDate(Shared.dateformat.parse(c.getString(c.getColumnIndex(DbSchema.COL_DIARY_DATE)))); } catch (Exception e) {}

				String selectQueryDetail =  " SELECT  *  FROM " + DbSchema.TBL_DIARY_DETAIL  +
						" WHERE " +DbSchema.COL_DIARY_DETAIL_CODE_DIARY + " = '"+item.getCode()+"'";

				Cursor cDetail = db.rawQuery(selectQueryDetail, null);

				ArrayList<DetailDiary> details = new ArrayList<DetailDiary>();
				if (cDetail.moveToFirst()) {
					do {

						DetailDiary  dt = new DetailDiary();
						dt.setCode(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CODE)));
						dt.setDiaryCode(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CODE_DIARY)));
						dt.setFlip(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_FLIP)) == 1 ? true : false);
						dt.setColor(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_COLOR)));
						dt.setContent(cDetail.getString(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_CONTENT)));
						dt.setEmoji(cDetail.getString(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_EMOJI)));
                        dt.setType(cDetail.getInt(cDetail.getColumnIndex(DbSchema.COL_DIARY_DETAIL_TYPE)));

						details.add(dt);
					} while (cDetail.moveToNext());
				}

				 item.setDetail(details);
				
				items.add(item);
			} while (c.moveToNext());
		}
	
		return items;
	}
	
	public long insert(Diary item)
	{
		ContentValues values = new ContentValues();
		values.put(DbSchema.COL_DIARY_TITLE, item.getTitle());
		values.put(DbSchema.COL_DIARY_FEEL, item.getFeel());
		values.put(DbSchema.COL_DIARY_DATE,  Shared.dateformat.format(item.getDate()));
		db.insert(DbSchema.TBL_DIARY, null, values);

		String selectQuery = 	" SELECT  * FROM " + DbSchema.TBL_DIARY   +
				" order by "+ DbSchema.COL_DIARY_CODE  +" DESC LIMIT 1";

		int id = 0;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				id = c.getInt(c.getColumnIndex(DbSchema.COL_DIARY_CODE));
			} while (c.moveToNext());
		}

		if(id != 0)
		{
			for (DetailDiary detail : item.getDetail()) {
				ContentValues valuesDetails = new ContentValues();
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_CODE_DIARY, id);
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_COLOR, detail.getColor());
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_CONTENT, detail.getContent());
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_EMOJI, detail.getEmoji());
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_FLIP,detail.isFlip() ? 1 : 0);
				valuesDetails.put(DbSchema.COL_DIARY_DETAIL_TYPE, detail.getType());
				db.insert(DbSchema.TBL_DIARY_DETAIL, null, valuesDetails);
			}
		}

		return 1;
	}

	public int delete(int code)
	{
		db.delete(DbSchema.TBL_DIARY_DETAIL, DbSchema.COL_DIARY_DETAIL_CODE_DIARY + "= '" + code + "'", null);
		db.delete(DbSchema.TBL_DIARY, DbSchema.COL_DIARY_CODE + "= '" + code + "'", null);
		return 1;
	}

	public long update(Diary item,int lastCode)
	{
		ContentValues values = new ContentValues();
		if(item.getCode() != null)
			values.put(DbSchema.COL_DIARY_TITLE, item.getTitle());

		if(item.getFeel() != null)
			values.put(DbSchema.COL_DIARY_FEEL, item.getFeel());

		if(item.getDate() != null)
			values.put(DbSchema.COL_DIARY_DATE,  Shared.dateformat.format(item.getDate()));

		db.delete(DbSchema.TBL_DIARY_DETAIL, DbSchema.COL_DIARY_DETAIL_CODE_DIARY + "= '" + lastCode + "'", null);
		for(int i = 0; i < item.getDetail().size();i++)
		{
			DetailDiary detail = item.getDetail().get(i);
			ContentValues valuesDetails = new ContentValues();
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_CODE_DIARY, lastCode);
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_COLOR, detail.getColor());
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_CONTENT, detail.getContent());
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_EMOJI, detail.getEmoji());
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_FLIP,detail.isFlip() ? 1 : 0);
			valuesDetails.put(DbSchema.COL_DIARY_DETAIL_TYPE, detail.getType());

			db.insert(DbSchema.TBL_DIARY_DETAIL,null, valuesDetails);
		}

		return db.update(DbSchema.TBL_DIARY, values, DbSchema.COL_DIARY_CODE+"= '"+lastCode+"' ", null);
	}
}
