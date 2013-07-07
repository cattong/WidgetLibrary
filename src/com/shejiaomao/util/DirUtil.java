package com.shejiaomao.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.shejiaomao.common.WidgetConstants;

public class DirUtil {
	public static final String TAG = DirUtil.class.getSimpleName();
	
	public static File getCacheDir(Context context) {
		File cacheFile = null;
		if (context == null) {
			return cacheFile;
		}
		
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			String packageName = context.getPackageName();
			int pos = packageName.lastIndexOf(".");
			String cachePath = Environment.getExternalStorageDirectory().getPath();
			cachePath += "/" + packageName.substring(pos + 1);
			cacheFile = new File(cachePath);
		} else {
			cacheFile = context.getCacheDir();
		}
		
		if (WidgetConstants.isDebug) {
			Log.d(TAG, "DownloadCacheDirectory: " + Environment.getDownloadCacheDirectory().getPath());
			Log.d(TAG, "DownloadCacheDirectory: " + Environment.getDownloadCacheDirectory().getPath());
			Log.d(TAG, "ExternalStorageDirectory: " + Environment.getExternalStorageDirectory().getPath());
			Log.d(TAG, "RootDirectory: " + Environment.getRootDirectory().getPath());
		}
		
		return cacheFile;
	}
}
