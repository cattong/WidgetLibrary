package com.shejiaomao.widget;

import android.view.View;

public class PhotoViewCompat {
	
	private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
	
	public static void postOnAnimation(View view, Runnable runnable) {
//		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
//			SDK16.postOnAnimation(view, runnable);
//		} else {
			view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
		}
//	}

}
