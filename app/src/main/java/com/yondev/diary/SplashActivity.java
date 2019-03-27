package com.yondev.diary;

import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

public class SplashActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				YoYo.with(Techniques.ZoomOut).duration(700).withListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onAnimationEnd(Animator arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SplashActivity.this, TimeLineActivity.class);
						startActivity(intent);
				        overridePendingTransition(anim.fade_in, 0);
				        finish();
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
				}).playOn(findViewById(R.id.logo1));
			}
		}, 2000);
		
	}
}
