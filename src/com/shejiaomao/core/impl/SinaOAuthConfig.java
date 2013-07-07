package com.shejiaomao.core.impl;

import com.shejiaomao.core.http.auth.Authorization;
import com.shejiaomao.core.http.oauth.config.OAuthConfigBase;

public class SinaOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -4059369499822415321L;

	public SinaOAuthConfig() {		
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);

		//社交猫微博客户端
		this.setConsumerKey("517864430");
		this.setConsumerSecret("3c45d5fb3a65a9a57436db52f5b72190");
		this.setCallbackUrl("http://www.shejiaomao.com/getAccessToken.do");
		
		this.setRequestTokenUrl("https://api.weibo.com/oauth2/authorize");
		this.setAuthorizeUrl("https://api.weibo.com/oauth2/authorize");
		this.setAccessTokenUrl("https://api.weibo.com/oauth2/access_token");
		
		this.setOAuthScope("ollow_app_official_microblog,friendships_groups_read,friendships_groups_write");
		this.setOfficialUserId("3198102734");
	}

}
