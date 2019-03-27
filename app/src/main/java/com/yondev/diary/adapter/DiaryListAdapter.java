package com.yondev.diary.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterInputStream;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yondev.diary.R;
import com.yondev.diary.entity.DetailDiary;
import com.yondev.diary.entity.Diary;
import com.yondev.diary.utils.Shared;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class DiaryListAdapter extends BaseAdapter {
 
    private List<Diary> dtList = new ArrayList<Diary>();
    private Activity context;
    private LayoutInflater inflater;
    public DiaryListAdapter(Activity context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public DiaryListAdapter(Activity context, List<Diary> data) {
     
        this.context = context;
        this.dtList = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    private class ViewHolder {
        TextView title;
        TextView content;
        TextView date;
        EmojiconTextView feel;
    }
 
    public int getCount() {
        return dtList.size();
    }
    
    public List<Diary> getData() {
        return dtList;
    }
    
  
    public void set(List<Diary> list) {
    	dtList = list;
        notifyDataSetChanged();
    }
    
    public void addOrReplace(Diary Diary) {
    	boolean hasData = false;
    	for (int i = 0; i < dtList.size(); i++) 
    	{
    		if (dtList.get(i).getCode().equals(Diary.getCode()))
    		{
    			dtList.set(i, Diary);
    			hasData = true;
    		}
    	}
    	
    	if(!hasData)
    		dtList.add(Diary);
    	
        notifyDataSetChanged();
    }
    
    public void remove(Diary user) {
    	dtList.remove(user);
        notifyDataSetChanged();
    }
    public void removeAll() {
    	dtList = new ArrayList<Diary>();
        notifyDataSetChanged();
    }
    
    public void removeByID(String code) {
    	for (int i = 0; i < dtList.size(); i++) {
			if(dtList.get(i).getCode().equals(code))
				dtList.remove(i);
		};
        notifyDataSetChanged();
    }

    public void addMany(List<Diary>  dt) {
        for (int i = 0; i < dt.size(); i++)
        {
            dtList.add(dt.get(i));
        }

        notifyDataSetChanged();
    }

    public void add(Diary user) {
    	dtList.add(user);
        notifyDataSetChanged();
    }
    
    public void insert(Diary user,int index) {
    	dtList.add(index, user);
        notifyDataSetChanged();
    }
 
    public Object getItem(int position) {
        return dtList.get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
    
 
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        
        if (convertView == null) {
        	vi = inflater.inflate(R.layout.diary_item, null);
            holder = new ViewHolder();
 
            holder.date = (TextView) vi.findViewById(R.id.textView3);
            holder.title = (TextView) vi.findViewById(R.id.textView1);
            holder.content = (TextView) vi.findViewById(R.id.textView2);
            holder.feel = (EmojiconTextView) vi.findViewById(R.id.emojiTextView);

            holder.date.setTypeface(Shared.appfontLight);
            holder.title.setTypeface(Shared.appfont);
            holder.content.setTypeface(Shared.appfont);

            vi.setTag(holder);
        } else {
        	 holder=(ViewHolder)vi.getTag();
        }


        
        final Diary Diary = (Diary) getItem(position);

        if(Diary.getTitle().length() >= 30)
            holder.title.setText(Diary.getTitle().substring(0,29)+"...");
        else
            holder.title.setText(Diary.getTitle());

        holder.date.setText(Shared.dateformatFeed.format(Diary.getDate()));
        holder.feel.setText(Diary.getFeel());

        String content = "";
        for(int i = 0; i < Diary.getDetail().size();i++)
        {
            DetailDiary dt = Diary.getDetail().get(i);
            if(i == 0)
                content += dt.getContent();
            else
                content += "\n" +  dt.getContent();

            if(i == 2)
                break;;
        }
        if(content.length() >= 90)
            holder.content.setText(content.substring(0,89)+"...");
        else
            holder.content.setText(content);

        return vi;
    }
}