package com.yondev.diary.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yondev.diary.R;
import com.yondev.diary.utils.Shared;

public class LoadingDialog extends Dialog{

	private Context context;
	public LoadingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public LoadingDialog(Context context, String text, String okText, String cancelText) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.loading_dialog);

		TextView t1 =(TextView)findViewById(R.id.textView4);
		t1.setTypeface(Shared.appfontLight);

		
	}

}
