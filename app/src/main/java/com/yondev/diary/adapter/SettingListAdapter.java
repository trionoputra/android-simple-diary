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
import com.yondev.diary.entity.Setting;
import com.yondev.diary.utils.Shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingListAdapter extends BaseAdapter {

    private List<HashMap<String,Object>> dtList = new ArrayList<HashMap<String,Object>>();
    private Context context;
    private LayoutInflater inflater;
    private int type = 1;
    public SettingListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initData();
    }
    public SettingListAdapter(Context context, int type ) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
        initData();
    }

    private void initData()
    {
        HashMap<String,Object> dt1 = new HashMap<String,Object>();
        HashMap<String,Object> dt2 = new HashMap<String,Object>();
        HashMap<String,Object> dt3 = new HashMap<String,Object>();

        dt1.put("title","PASSWORD LOCK");
        dt1.put("desc","Protect your diary with password");
        dt1.put("icon",R.mipmap.ic_key_w);

        dt2.put("title","BACKUP & RESTORE");
        dt2.put("desc","Create backup and restore");
        dt2.put("icon",R.mipmap.ic_restore);

        dt3.put("title","ABOUT");
        dt3.put("desc","General info of this app");
        dt3.put("icon",R.mipmap.ic_about);

        dtList.add(dt1);
        dtList.add(dt2);
        dtList.add(dt3);
    }

    public SettingListAdapter(Activity context, List<HashMap<String,Object>> data) {
     
        this.context = context;
        this.dtList = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    private class ViewHolder {
        TextView title;
        TextView desc;
        ImageView img;
    }
 
    public int getCount() {
        return dtList.size();
    }
    
    public List<HashMap<String,Object>> getData() {
        return dtList;
    }
    
  
    public void set(List<HashMap<String,Object>> list) {
    	dtList = list;
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

            vi = inflater.inflate(R.layout.setting_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) vi.findViewById(R.id.textView1);
            holder.desc = (TextView) vi.findViewById(R.id.textView2);
            holder.img = (ImageView)vi.findViewById(R.id.imageView2);

            holder.title.setTypeface(Shared.appfontBold);
            holder.desc.setTypeface(Shared.appfont);

            vi.setTag(holder);
        } else {
        	 holder=(ViewHolder)vi.getTag();
        }

        HashMap<String,Object> data = (HashMap<String,Object>)this.getItem(position);
        holder.title.setText((String)data.get("title"));
        holder.img.setImageResource((int)data.get("icon"));
        if(!data.get("desc").equals(""))
            holder.desc.setText((String)data.get("desc"));
        else
            holder.desc.setVisibility(View.GONE);
        
        return vi;
    }
}