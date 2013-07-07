package com.shejiaomao.core.impl;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.shejiaomao.core.LibConstants;
import com.shejiaomao.core.LibException;
import com.shejiaomao.core.LibResultCode;
import com.shejiaomao.core.LibRuntimeException;
import com.shejiaomao.core.ServiceProvider;
import com.shejiaomao.core.api.SocialService;
import com.shejiaomao.core.entity.Gender;
import com.shejiaomao.core.entity.User;
import com.shejiaomao.core.http.HttpMethod;
import com.shejiaomao.core.http.HttpRequestHelper;
import com.shejiaomao.core.http.HttpRequestWrapper;
import com.shejiaomao.core.http.auth.Authorization;
import com.shejiaomao.core.util.GsonUtil;
import com.shejiaomao.core.util.StringUtil;

public class SoicalServiceImpl extends BaseService implements SocialService {
	public static final String API_SERVER_SINA = "https://api.weibo.com/2";
	public static final String API_SERVER_QQ = "https://graph.qq.com";
	
	protected ResponseHandler<String> responseHandler;
	public SoicalServiceImpl(Authorization auth) {
		super(auth);
	}
	
	@Override
	public User showUser() throws LibException {
		ServiceProvider sp = auth.getServiceProvider();
		User user = null;
		
		if (sp == ServiceProvider.Sina) {
			HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
					HttpMethod.GET, API_SERVER_SINA + "/users/get_uid.json" , auth);
			String response = HttpRequestHelper.execute(httpRequestWrapper, new SinaResponseHandler());
			String userId = null;
			
			JsonObject json = jsonParser.parse(response).getAsJsonObject();
			userId = json.get("uid").getAsString();
			
	        if (StringUtil.isEmpty(userId)) {
	        	return user;
	        }
	        
	        httpRequestWrapper = new HttpRequestWrapper(
	    			HttpMethod.GET, API_SERVER_SINA + "/users/show.json", auth);
	    	httpRequestWrapper.addParameter("uid", userId);
	    	response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
	    	user = createSinaUser(response);
		} else if (sp == ServiceProvider.Qzone) {
			HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
					HttpMethod.POST, API_SERVER_QQ + "/user/get_user_info", auth);
			//附加参数
			httpRequestWrapper.addParameter("oauth_consumer_key",
					auth.getoAuthConfig().getConsumerKey());
			httpRequestWrapper.addParameter("openid", getQQOpenId());
			httpRequestWrapper.addParameter("format", "json");

			String response = HttpRequestHelper.execute(httpRequestWrapper,	new QzoneResponseHandler());
			
			user = createQQUser(response);
		}
		
		return user;
	}

	private User createSinaUser(String jsonString) throws LibException {
		User user = null;
		try {
			JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
			user = new User();
			user.setUserId(GsonUtil.getRawString("id", json));
			user.setName(GsonUtil.getRawString("domain", json)); //新浪不支持name属性，用个性网址填充
			user.setScreenName(GsonUtil.getRawString("screen_name", json));
			user.setLocation(GsonUtil.getRawString("location", json));
			user.setDescription(GsonUtil.getRawString("description", json));
			user.setProfileImageUrl(GsonUtil.getRawString("profile_image_url", json));
			user.setBigProfileImageUrl(GsonUtil.getRawString("avatar_large", json));
			user.setVerified(GsonUtil.getBoolean("verified", json));
			user.setVerifyInfo(GsonUtil.getRawString("verified_reason", json));
			user.setFollowersCount(GsonUtil.getInt("followers_count", json));
			user.setFriendsCount(GsonUtil.getInt("friends_count", json));
			user.setCreatedAt(GsonUtil.getDate("created_at", json, "EEE MMM dd HH:mm:ss z yyyy"));
			user.setFavouritesCount(GsonUtil.getInt("favourites_count", json));
			user.setStatusesCount(GsonUtil.getInt("statuses_count", json));
			
			user.setGender(Gender.Unkown);
			if (json.get("gender") != null) {
				String gender = GsonUtil.getRawString("gender", json);
				if ("m".equals(gender)) {
					user.setGender(Gender.Male);
				} else if ("f".equals(gender)) {
					user.setGender(Gender.Female);
				} else if ("n".equals(gender)) {
					user.setGender(Gender.Unkown);
				}
			}
			
			user.setServiceProvider(ServiceProvider.Sina);
		} catch (JsonSyntaxException jsone) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
		
		return user;
	}
	
	private String getQQOpenId() throws LibException {	
		String userId = null;
		
		String url = API_SERVER_QQ + "/oauth2.0/me";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper,
				responseHandler);
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject json = jsonParser.parse(response).getAsJsonObject();
			userId = GsonUtil.getRawString("openid", json);
		} catch (JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
		
		return userId;
	}
	
	private User createQQUser(String jsonString) throws LibException {
		User user = null;
		try {
			JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
			user = new User();
			user.setScreenName(GsonUtil.getRawString("nickname", json));
			user.setName(user.getScreenName());
			user.setProfileImageUrl((GsonUtil.getRawString("figureurl_1", json)));
	
			user.setServiceProvider(ServiceProvider.Qzone);
		} catch (JsonSyntaxException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		return user;
	}
	
	class SinaResponseHandler implements ResponseHandler<String> {

		public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			String responseString = (entity == null ? null : EntityUtils.toString(entity));

			if (LibConstants.isDebug()) {
			    logger.debug("SinaResponseHandler : {}", responseString);
			}			
			
			if (statusLine.getStatusCode() >= 300) {
				LibRuntimeException apiException = null;
				try {
					JsonObject json = jsonParser.parse(responseString).getAsJsonObject();
					int errorCode = json.get("error_code").getAsInt();
					String errorDesc = json.get("error").getAsString();
					String[] errorDetails = errorDesc.split(":");
					if (errorDetails.length == 3) {
						errorCode = Integer.valueOf(errorDetails[0]);
						errorDesc = errorDetails[2].trim();
					} else if (errorDetails.length == 2) {
						if (StringUtil.isNumeric(errorDetails[0])) {
							errorCode = Integer.valueOf(errorDetails[0]);
						}				
						errorDesc = errorDetails[1].trim();
					}
					
					String requestPath = json.get("request").getAsString();
					apiException = new LibRuntimeException(
						errorCode, requestPath,	errorDesc, ServiceProvider.Sina);
					
				} catch (JsonSyntaxException e) {
					apiException = new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Sina);
				}
				
				throw apiException;
			}
			
			return responseString;
		}
	}
	
	class QzoneResponseHandler implements ResponseHandler<String> {

		private static final String CALLBACK_RESPONSE_REGEX = "callback\\( (.*) \\);";
        
		public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			String responseString = (entity == null ? null : EntityUtils.toString(entity, "UTF-8"));

			if (LibConstants.isDebug()) {
			    logger.debug("QQZoneResponseHandler : {}", responseString);
			}
			
			if (responseString != null) {
				responseString = responseString.trim();
				if (responseString.matches(CALLBACK_RESPONSE_REGEX)) {
					responseString = responseString.replaceAll(CALLBACK_RESPONSE_REGEX, "$1");
				}
				if (responseString.contains("error_code")
					&& responseString.startsWith("{")) {
					try {
						JsonObject json = jsonParser.parse(responseString).getAsJsonObject();
						if (json.has("error_code")) {
							// 明确是异常响应，而不是包含了error_code的文本
							int errorCode = json.get("error_code").getAsInt();
							String errorDesc = json.get("error").getAsString();
							String requestPath = json.get("request").getAsString();
							throw new LibRuntimeException(
								errorCode, requestPath,	errorDesc, ServiceProvider.Qzone);
						}
					} catch (JsonSyntaxException e) {
						throw new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Qzone);
					}
				}
			}

			int statusCode = statusLine.getStatusCode();
			if (statusCode >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			}

			return responseString;
		}
	}
}
