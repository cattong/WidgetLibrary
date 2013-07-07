package com.shejiaomao.util;

import android.app.Activity;
import android.content.Context;

import com.shejiaomao.R;

public class CompatUtil {
	/*
	 * 解决某些版本和设置，在传递参数EXTRA_OUTPUT，不起作用（1.5不起作用,只能getData())，
	 * 返回只有一半大小图片的bug;
	 */
	public static boolean hasImageCaptureBug() {
	    boolean hasBug = false;
	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.DONUT) {
	    	hasBug = true;
	    }
	
        if ("me600".equals(getModel().toLowerCase())) {
        	hasBug = false;
        }
	    return hasBug;
	}

	/*
	 * 获得固件版本
	 */
    public static String getRelease() {
    	return android.os.Build.VERSION.RELEASE;
    }

	/*
	 * 获得手机型号
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	public static void overridePendingTransitionSlideInFromRight(Context context) {
		((Activity)context).overridePendingTransition(R.anim.slide_in_from_right, android.R.anim.fade_out);
	}

	public static void overridePendingTransitionSlideInFromLeft(Context context) {
		((Activity)context).overridePendingTransition(R.anim.slide_in_from_left, android.R.anim.fade_out);
	}
	
	public static void overridePendingTransitionSlideInFromBottom(Context context) {
		((Activity)context).overridePendingTransition(R.anim.slide_in_from_bottom, android.R.anim.fade_out);
	}
	
	public static void overridePendingTransitionSlideInFromTop(Context context) {
		((Activity)context).overridePendingTransition(R.anim.slide_in_from_top, android.R.anim.fade_out);
	}
	
	public static void overridePendingTransitionSlideOutToTop(Context context) {
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_top);
	}
	
	public static void overridePendingTransitionSlideOutToRight(Context context) {
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right);
	}
	
	public static void overridePendingTransitionSlideOutToLeft(Context context) {
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_left);
	}
	
	public static int dip2px(Context context, int dipValue) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * density + 0.5f);
	}

	public static int px2dip(Context context, int pxValue) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * density + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density + 0.5f);
	}
}
