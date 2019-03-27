package com.yondev.diary.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yondev.diary.R;
import com.yondev.diary.entity.Diary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hani.momanii.supernova_emoji_library.emoji.Cars;
import hani.momanii.supernova_emoji_library.emoji.Electr;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;
import hani.momanii.supernova_emoji_library.emoji.Food;
import hani.momanii.supernova_emoji_library.emoji.Nature;
import hani.momanii.supernova_emoji_library.emoji.People;
import hani.momanii.supernova_emoji_library.emoji.Sport;
import hani.momanii.supernova_emoji_library.emoji.Symbols;

public class ImojiListAdapter extends BaseAdapter {

    private List<Emojicon> dtList = new ArrayList<Emojicon>();
    private Context context;
    private LayoutInflater inflater;
    private int type = 1;
    public ImojiListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initData();
    }
    public ImojiListAdapter(Context context,int type ) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
        initData();
    }

    private void initData()
    {
        ArrayList<Emojicon> p = new ArrayList<Emojicon>(Arrays.asList(People.DATA));
        ArrayList<Emojicon> n = new ArrayList<Emojicon>(Arrays.asList(Nature.DATA));
        ArrayList<Emojicon> f = new ArrayList<Emojicon>(Arrays.asList(Food.DATA));
        ArrayList<Emojicon> s = new ArrayList<Emojicon>(Arrays.asList(Sport.DATA));
        ArrayList<Emojicon> c = new ArrayList<Emojicon>(Arrays.asList(Cars.DATA));
        ArrayList<Emojicon> e = new ArrayList<Emojicon>(Arrays.asList(Electr.DATA));
        ArrayList<Emojicon> m = new ArrayList<Emojicon>(Arrays.asList(Symbols.DATA));
        this.dtList.addAll(p);
        this.dtList.addAll(n);
        this.dtList.addAll(f);
        this.dtList.addAll(s);
        this.dtList.addAll(c);
        this.dtList.addAll(e);
        this.dtList.addAll(m);
    }

    public ImojiListAdapter(Activity context, List<Emojicon> data) {
     
        this.context = context;
        this.dtList = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    private class ViewHolder {
        TextView title;
    }
 
    public int getCount() {
        return dtList.size();
    }
    
    public List<Emojicon> getData() {
        return dtList;
    }
    
  
    public void set(List<Emojicon> list) {
    	dtList = list;
        notifyDataSetChanged();
    }
    
    public void addOrReplace(Emojicon imo) {
    	boolean hasData = false;
    	for (int i = 0; i < dtList.size(); i++) 
    	{
    		if (dtList.get(i).equals(imo))
    		{
    			dtList.set(i, imo);
    			hasData = true;
    		}
    	}
    	
    	if(!hasData)
    		dtList.add(imo);
    	
        notifyDataSetChanged();
    }
    
    public void remove(Emojicon dt) {
    	dtList.remove(dt);
        notifyDataSetChanged();
    }
    public void removeAll() {
    	dtList = new ArrayList<Emojicon>();
        notifyDataSetChanged();
    }
    
    public void removeByID(Emojicon code) {
    	for (int i = 0; i < dtList.size(); i++) {
			if(dtList.get(i).equals(code))
				dtList.remove(i);
		};
        notifyDataSetChanged();
    }
    
    public void add(Emojicon dt) {
    	dtList.add(dt);
        notifyDataSetChanged();
    }
    
    public void insert(Emojicon dt,int index) {
    	dtList.add(index, dt);
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
            if(type == 1)
        	    vi = inflater.inflate(R.layout.item_imoji, null);
            else if(type == 2)
                vi = inflater.inflate(R.layout.item_imoji_small, null);

            holder = new ViewHolder();

            holder.title = (TextView) vi.findViewById(R.id.emojiTextView);
            vi.setTag(holder);
        } else {
        	 holder=(ViewHolder)vi.getTag();
        }

        Emojicon emoji1 = (Emojicon)this.getItem(position);
        holder.title.setText(emoji1.getEmoji());

        
        return vi;
    }
}