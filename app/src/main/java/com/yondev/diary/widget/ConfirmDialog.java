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

public class ConfirmDialog extends Dialog implements View.OnClickListener{

	private Context context;
	private Button btnOk;
	private Button btnCancel;
	private ConfirmListener listener;

	private String text = "";
	private String okText = "";
	private String cancelText = "";
	public ConfirmDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public ConfirmDialog(Context context,String text,String okText,String cancelText) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.okText = okText;
		this.cancelText = cancelText;
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.confirm_dialog);
		
		btnOk = (Button)findViewById(R.id.btnOk);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		
		TextView t1 =(TextView)findViewById(R.id.textView);
		
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		if(!text.equals(""))
			t1.setText(text);
		if(!okText.equals(""))
			btnOk.setText(okText);
		if(!cancelText.equals(""))
			btnCancel.setText(cancelText);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnCancel:
			if(listener != null)
				listener.onCancel();
			dismiss();
			break;
		case R.id.btnOk:
			if(listener != null)
				listener.onOK();
			dismiss();
			break;	
		default:
			break;
		}
	}

	public void setConfirmListener(ConfirmListener listener)
    {
    	this.listener = listener;
    }
    
    public interface ConfirmListener {
        public void onOK();
		public void onCancel();
    }

}
