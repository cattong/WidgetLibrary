package com.shejiaomao.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.shejiaomao.core.PagingSupport;
import com.shejiaomao.core.http.auth.Authorization;
import com.shejiaomao.core.util.GMTDateGsonAdapter;

public abstract class BaseService extends PagingSupport {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
	protected Authorization auth;
	
	protected Gson gson;
	protected JsonParser jsonParser;
	public BaseService(Authorization auth) {
		this.auth = auth;
		this.gson = getGson();
		this.jsonParser = new JsonParser();
	}
	
	public Authorization getAuth() {
		return auth;
	}
	public void setAuth(Authorization auth) {
		this.auth = auth;
	}
	
	private Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.serializeNulls();
		
		//GSON对date的序列化和反序列化处理
		GMTDateGsonAdapter dateGsonAdapter = new GMTDateGsonAdapter();
		gsonBuilder.registerTypeAdapter(java.util.Date.class, dateGsonAdapter);
		gsonBuilder.registerTypeAdapter(java.sql.Timestamp.class, dateGsonAdapter);
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, dateGsonAdapter);
		
		return gsonBuilder.create();
	}
}
