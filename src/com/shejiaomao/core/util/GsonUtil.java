package com.shejiaomao.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GsonUtil {
	private GsonUtil() {
	}

	private static ThreadLocal<Map<String, SimpleDateFormat>> formatMap = 
		new ThreadLocal<Map<String, SimpleDateFormat>>() {
		@Override
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<String, SimpleDateFormat>();
		}
	};

	public static String getUnescapedString(String str, JsonObject json) {
		return HTMLUtil.unescape(getRawString(str, json));
	}

	public static String getRawString(String name, JsonObject json) {
		JsonElement jsonElement = json.get(name);
		return jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
	}

	public static String getURLDecodedString(String name, JsonObject json) {
		String returnValue = getRawString(name, json);
		if (null != returnValue) {
			try {
				returnValue = URLDecoder.decode(returnValue, "UTF-8");
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		return returnValue;
	}

	public static Date getDate(String name, JsonObject json) throws ParseException {
		return getDate(name, json, "EEE MMM d HH:mm:ss z yyyy");
	}

	public static Date getDate(String name, JsonObject json, String format) throws ParseException {
		String dateStr = getUnescapedString(name, json);
		if (null == dateStr || dateStr.trim().length() == 0 || "null".equals(dateStr)) {
			return null;
		} else {
			return getDate(dateStr, format);
		}
	}

	public static Date getDate(String dateString, String format) throws ParseException {
		SimpleDateFormat sdf = formatMap.get().get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.get().put(format, sdf);
		}
		return sdf.parse(dateString);
	}

	public static int getInt(String name, JsonObject json) {
		JsonElement jsonElement = json.get(name);
		return jsonElement != null ? jsonElement.getAsInt() : -1;
	}

	public static long getLong(String name, JsonObject json) {
		JsonElement jsonElement = json.get(name);
		return jsonElement != null ? jsonElement.getAsLong() : -1L;
	}

	public static double getDouble(String name, JsonObject json) {
		JsonElement jsonElement = json.get(name);
		return jsonElement != null ? jsonElement.getAsDouble() : -1d;
	}

	public static boolean getBoolean(String name, JsonObject json) {
		JsonElement jsonElement = json.get(name);
		return jsonElement != null ? jsonElement.getAsBoolean() : false;
	}

	public static String escapeAngleBrackets(String text){
    	if (text == null) {
    		return text;
    	}

    	return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}