package com.shejiaomao.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.shejiaomao.core.entity.AppInfo;
import com.shejiaomao.core.entity.Os;
import com.shejiaomao.core.util.ListUtil;
import com.shejiaomao.core.util.StringUtil;

public class PackageUtil {
	private static final String TAG = PackageUtil.class.getSimpleName();
	
	/** 
	 * 查询手机内所有应用 
	 * @param context 
	 * @return 
	 */  
	public static List<PackageInfo> getAllPackageInfos(Context context) {	    
	    PackageManager pManager = context.getPackageManager();  
	    //获取手机内所有应用  
	    List<PackageInfo> appList = pManager.getInstalledPackages(0);
	    
	    return appList;
	}
	
	public static List<AppInfo> getAllAppInfos(Context context) {
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		
	    List<PackageInfo> packageInfoList = getAllPackageInfos(context);	    
	    if (ListUtil.isEmpty(packageInfoList)) {
	    	return appInfoList;
	    }
	    
	    PackageManager pManager = context.getPackageManager();
	    for (PackageInfo packageInfo : packageInfoList) {
	    	AppInfo appInfo = new AppInfo();
	    	appInfo.setAppId(packageInfo.applicationInfo.packageName);
	    	String appName = pManager.getApplicationLabel(packageInfo.applicationInfo).toString();
	    	appInfo.setAppName(appName);
	    	appInfo.setPackageName(packageInfo.applicationInfo.packageName);
	    	appInfo.setOs(Os.Android);
	    	appInfo.setVersionName(packageInfo.versionName == null ? "unknow" : packageInfo.versionName);
	    	appInfo.setVersionCode("" + packageInfo.versionCode);
	    	
	    	appInfoList.add(appInfo);
	    }
	    
	    return appInfoList;
	}
	
	/** 
	 * 查询手机内用户安装的应用 
	 * @param context 
	 * @return 
	 */ 
	public static List<PackageInfo> getUserPackageInfos(Context context) {
	    List<PackageInfo> appList = new ArrayList<PackageInfo>();
	    
	    PackageManager pManager = context.getPackageManager();
	    //获取手机内所有应用  
	    List<PackageInfo> packageInfoList = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < packageInfoList.size(); i++) {  
	        PackageInfo packageInfo = (PackageInfo) packageInfoList.get(i);
	        
	        //判断是否为非系统预装的应用程序  
	        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
	            // customs applications  
	        	appList.add(packageInfo);  
	        }
	    } 
	    
	    return appList;  
	}
	
	/** 
	 * 查询手机内用户安装的应用 
	 * @param context 
	 * @return 
	 */ 
	public static List<AppInfo> getUserAppInfos(Context context) {
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		
	    List<PackageInfo> packageInfoList = getUserPackageInfos(context);	    
	    if (ListUtil.isEmpty(packageInfoList)) {
	    	return appInfoList;
	    }
	    
	    PackageManager pManager = context.getPackageManager();
	    for (PackageInfo packageInfo : packageInfoList) {
	    	AppInfo appInfo = new AppInfo();
	    	appInfo.setAppId(packageInfo.applicationInfo.packageName);
	    	String appName = pManager.getApplicationLabel(packageInfo.applicationInfo).toString();
	    	appInfo.setAppName(appName);
	    	appInfo.setPackageName(packageInfo.applicationInfo.packageName);
	    	appInfo.setOs(Os.Android);
	    	appInfo.setVersionName(packageInfo.versionName == null ? "unknow" : packageInfo.versionName);
	    	appInfo.setVersionCode("" + packageInfo.versionCode);
	    	
	    	appInfoList.add(appInfo);
	    }
	    
	    return appInfoList;
	}
	
	/** 
	 * 查询手机内的系统应用 
	 * @param context 
	 * @return 
	 */ 
	public static List<PackageInfo> getSystemPackageInfos(Context context) {
	    List<PackageInfo> appList = new ArrayList<PackageInfo>(); 
	    
	    PackageManager pManager = context.getPackageManager();
	    //获取手机内所有应用  
	    List<PackageInfo> packageInfoList = pManager.getInstalledPackages(0);
	    for (int i = 0; i < packageInfoList.size(); i++) {
	        PackageInfo packageInfo = (PackageInfo) packageInfoList.get(i);
	        //判断是否为系统预装的应用程序  
	        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
	        	appList.add(packageInfo);
	        }
	    }  
	    
	    return appList;  
	}
	
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageInfo packageInfo = null;
		if (context == null || StringUtil.isEmpty(packageName)) {
			return packageInfo;
		}
		
		PackageManager packageManager = context.getPackageManager();		
		try {
			packageInfo = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		
		return packageInfo;
	}
	
	public static AppInfo getAppInfo(Context context, String packageName) {
		AppInfo appInfo = null;
		if (context == null || StringUtil.isEmpty(packageName)) {
			return appInfo;
		}
		
		PackageManager packageManager = context.getPackageManager();		
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			
			appInfo = new AppInfo();
	    	appInfo.setAppId(packageInfo.applicationInfo.packageName);
	    	String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
	    	appInfo.setAppName(appName);
	    	appInfo.setPackageName(packageInfo.applicationInfo.packageName);
	    	appInfo.setOs(Os.Android);
	    	appInfo.setVersionName(packageInfo.versionName == null ? "unknow" : packageInfo.versionName);
	    	appInfo.setVersionCode("" + packageInfo.versionCode);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return appInfo;
	}
	
	public static Drawable getPackageInfoDrawable(Context context, String packgeName) {
		PackageInfo packageInfo = getPackageInfo(context, packgeName);
		Drawable drawable = null;
		if (packageInfo != null) {
			drawable = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
		}
		
		return drawable;
	}
	
	public static void startApp(Context context, String packageName) {
		if (context == null || StringUtil.isEmpty(packageName)) {
			return;
		}
		
		PackageManager packageManager = context.getPackageManager();		
		
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}
}
