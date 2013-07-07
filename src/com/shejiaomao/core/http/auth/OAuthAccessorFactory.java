package com.shejiaomao.core.http.auth;

import java.util.HashMap;
import java.util.Map;

import com.shejiaomao.core.LibException;
import com.shejiaomao.core.LibResultCode;
import com.shejiaomao.core.http.oauth.OAuthAccessor;
import com.shejiaomao.core.http.oauth.OAuthConsumer;
import com.shejiaomao.core.http.oauth.OAuthParameterStyle;
import com.shejiaomao.core.http.oauth.config.OAuthConfig;
import com.shejiaomao.core.util.StringUtil;


public class OAuthAccessorFactory {

	private static Map<Authorization, OAuthAccessor> accessorMap;
	private static Map<String, OAuthConsumer> consumerMap;

	static {
		accessorMap = new HashMap<Authorization, OAuthAccessor>();
		consumerMap = new HashMap<String, OAuthConsumer>();		
	}

	public synchronized static OAuthAccessor getOAuthAccessorInstance(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		OAuthAccessor accessor = accessorMap.get(auth);
		if (accessor == null) {
			OAuthConsumer consumer = consumerMap.get(auth.getoAuthConfig().getConsumerKey());
			if (consumer == null) {
				OAuthConfig oauthConfig = auth.getoAuthConfig();				
				
				consumer = new OAuthConsumer(oauthConfig.getCallbackUrl(), 
					oauthConfig.getConsumerKey(), 
					oauthConfig.getConsumerSecret());
				if (StringUtil.isNotEmpty(oauthConfig.getOAuthParameterStyle())) {
					OAuthParameterStyle paramStyle = OAuthParameterStyle.valueOf(oauthConfig.getOAuthParameterStyle());
					consumer.setParameterStyle(paramStyle);
				}
				
				consumerMap.put(oauthConfig.getConsumerKey(), consumer);				
			}

			accessor = new OAuthAccessor(consumer);
			accessor.setAuthorization(auth);
			accessorMap.put(auth, accessor);
		}

		return accessor;
	}
}
