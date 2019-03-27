package com.yondev.diary.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.yondev.diary.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public final class Shared
{
	private static ContextWrapper instance;
	private static SharedPreferences pref;
	public static Typeface appfont;
	public static Typeface appfontBold;
	public static Typeface appfontThin;
	public static Typeface appfontLight;

	public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dateformatFeed = new SimpleDateFormat("MMM dd, yyyy");
	public static String SERVER_URL = "http://104.131.24.96/api/diary/index.php/api/mobile/";
	public static SimpleDateFormat dateformatBackup = new SimpleDateFormat("yyyyMMdd");
	public static void initialize(Context base)
	{
		instance = new ContextWrapper(base);
		pref = instance.getSharedPreferences("com.yondev.diary", Context.MODE_PRIVATE);
		appfont = Typeface.createFromAsset(instance.getAssets(),"fonts/Roboto-Regular.ttf");
		appfontBold = Typeface.createFromAsset(instance.getAssets(),"fonts/Roboto-Bold.ttf");
		appfontThin = Typeface.createFromAsset(instance.getAssets(),"fonts/Roboto-Thin.ttf");
		appfontLight = Typeface.createFromAsset(instance.getAssets(),"fonts/Roboto-Light.ttf");
		File file =  new File(getAppDir());
		if (!file.exists()) {
			if (!file.mkdirs()) {
				return;
			}
		}
		
	}
	
	public static void write(String key, String value)
	{
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String read(String key)
	{
		return Shared.read(key, null);
	}
	
	public static String read(String key, String defValue)
	{
		return pref.getString(key, defValue);
	}
	
	public static void clear()
	{
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void clear(String key)
	{
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static Context getContext()
	{
		return instance.getBaseContext();
	}
	
	public static int DipToInt(int value)
	{
		return (int)(instance.getResources().getDisplayMetrics().density * value);
	}
	
	public static int getDisplayHeight()
	{
		
		 WindowManager wm = (WindowManager) instance.getSystemService(Context.WINDOW_SERVICE);
		 Display display = wm.getDefaultDisplay();
		 final int version = android.os.Build.VERSION.SDK_INT;
		 
		 if (version >= 13)
		 {
		     Point size = new Point();
		     display.getSize(size);
		    return size.y;
		 }
		 else
		 {
		     
		     return  display.getHeight();
		 }
	}
	
	public static int getDisplayWidth()
	{
		 WindowManager wm = (WindowManager) instance.getSystemService(Context.WINDOW_SERVICE);
		 Display display = wm.getDefaultDisplay();
		 final int version = android.os.Build.VERSION.SDK_INT;
		 
		 if (version >= 13)
		 {
		     Point size = new Point();
		     display.getSize(size);
		     return size.x;
		 }
		 else
		 {
		     
		     return  display.getWidth();
		 }
	}

	public static String getAppDir()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator  + instance.getResources().getString(R.string.app_name);
	}

	public static byte[] getStringFromInputStream(InputStream is)
	{

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		byte[] bReturn = new byte[0];

		String line;
		try
		{

			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			String sContent = sb.toString();
			bReturn = sContent.getBytes();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return bReturn;
	}

}

