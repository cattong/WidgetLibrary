package com.shejiaomao.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.shejiaomao.app.SlideOutGestureListener.SlideDirection;
import com.shejiaomao.util.CompatUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity {
	protected GestureDetector detector; //触摸监听实例
	protected SlideOutGestureListener gestureListener;
	protected SlideDirection slideDirection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//activity 被回收时，把保存的bundle也合并到this.getIntent()时
		if (savedInstanceState != null) {
			if (this.getIntent() == null) {
				Intent newIntent = new Intent();
				this.setIntent(newIntent);
			}
			if (this.getIntent().getExtras() == null) {
			    this.getIntent().putExtras(savedInstanceState);	
			} else {
				this.getIntent().getExtras().putAll(savedInstanceState);
			}
		}
		
		//初始变量
		slideDirection = SlideDirection.Right;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	protected abstract void initParams();
	
	protected abstract void initComponents();
	
	protected abstract void bindEvent();
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev == null) {
			return true;
		}
		boolean isGesture = false;
		
		if (slideDirection == null) {
			slideDirection = SlideDirection.Right;
		}
		if (detector == null) {
		    gestureListener = new SlideOutGestureListener(this, slideDirection);
		    detector = new GestureDetector(this, gestureListener);
		}
		isGesture = detector.onTouchEvent(ev);
		
		if (isGesture) {
			return isGesture;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

    @TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
    	finish();
    }

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		MobclickAgent.onEvent(this, "on_low_memory");
	}
	
	@Override
	public void finish() {		
		super.finish();
		if (slideDirection == null) {
			return;
		}
		
		switch (slideDirection) {
		case Right:
			CompatUtil.overridePendingTransitionSlideOutToRight(this);
			break;
		case Left:
			CompatUtil.overridePendingTransitionSlideOutToLeft(this);
			break;
		case Top:
			CompatUtil.overridePendingTransitionSlideOutToTop(this);
	    default:
	    	break;
		}
	}

	public SlideDirection getSlideDirection() {
		return slideDirection;
	}

	public void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
		if (gestureListener != null) {
		    gestureListener.setSlideDirection(slideDirection);
		}
	}
}
