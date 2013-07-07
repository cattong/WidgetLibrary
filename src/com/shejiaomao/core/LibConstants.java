package com.shejiaomao.core;

import java.util.regex.Pattern;


public class LibConstants {

	public final static int VERBOSE  = 0;
	public final static int DEBUG    = 1;
	public final static int INFO     = 2;
	public final static int WARN     = 3;
	public final static int ERROR    = 4;
	
	public static int level = DEBUG;
	
	public static boolean isDalvikVM;
	static {
		// detecting dalvik (Android platform)
		isDalvikVM = false;
		try {
			// dalvik.system.VMRuntime class should be existing on Android platform.
			// @see http://developer.android.com/reference/dalvik/system/VMRuntime.html
			Class.forName("dalvik.system.VMRuntime");
			isDalvikVM = true;
		} catch (ClassNotFoundException e) {
		}
	}
	
	//网络连接配置
	public static final int CONNECTION_POOL_SIZE = 128; // HTTP连接连接池大小
	public static final int CONNECTION_TIME_OUT = 30000; // 连接池连接超时时间，以毫秒为单位
	
	// 通行证正则表达式
    public static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]{4,32}"); 
    public static final Pattern PATTERN_PASSWORD = Pattern.compile("[a-zA-Z0-9_]{6,16}");
    public static final Pattern PATTERN_EMAIL = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    );
	
	public static boolean isDebug() {
		return level <= DEBUG;
	}
}
