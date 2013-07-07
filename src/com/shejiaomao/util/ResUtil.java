package com.shejiaomao.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.shejiaomao.common.WidgetConstants;

public class ResUtil {
    private static final String TAG = ResUtil.class.getSimpleName();
    
	public static Drawable getDrawable(Context context, String resName) {
		Drawable drawable = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "drawable", context.getPackageName());
			drawable= res.getDrawable(resId);
		} catch (NotFoundException e) {
			if (WidgetConstants.isDebug) Log.e(TAG, e.getMessage(), e);
		}
		
		return drawable;
	}
	

	public static Drawable getDrawable(Context context, int resId) {
		return context.getResources().getDrawable(resId);
	}

	public static Drawable getDrawableByColor(Context context, String resName) {
		Drawable drawable = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", context.getPackageName());
			drawable= res.getDrawable(resId);
		} catch (NotFoundException e) {
			if (WidgetConstants.isDebug) Log.e(TAG, e.getMessage(), e);
		}
		
		return drawable;
	}
    

	public static int getColor(Context context, String resName) {
		int color = 0;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", context.getPackageName());
			color = res.getColor(resId);
		} catch (NotFoundException e) {
			if (WidgetConstants.isDebug) Log.e(TAG, e.getMessage(), e);
		}
		
		return color;
	}

	public static int getColor(Context context, int resId) {
		return context.getResources().getColor(resId);
	}
    
	public static ColorStateList getColorStateList(Context context, String resName) {
		ColorStateList colorStateList = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", context.getPackageName());
			colorStateList = res.getColorStateList(resId);
		} catch (NotFoundException e) {
			if (WidgetConstants.isDebug) Log.e(TAG, e.getMessage(), e);
		}
		
		return colorStateList;
	}
	
	public static ColorStateList getColorStateList(Context context, int resId) {
		return context.getResources().getColorStateList(resId);
	}
	
}
