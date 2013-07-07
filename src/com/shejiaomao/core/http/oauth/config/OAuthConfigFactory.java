package com.shejiaomao.core.http.oauth.config;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shejiaomao.core.LibConstants;
import com.shejiaomao.core.ServiceProvider;


public class OAuthConfigFactory {
	private static final String CLASS_NAME_FORMAT = "com.shejiaomao.core.impl.%1$sOAuthConfig";
	private static final Logger logger = LoggerFactory.getLogger(OAuthConfigFactory.class);
	
	public static synchronized OAuthConfig getOAuthConfig(ServiceProvider sp) {
		if (sp == ServiceProvider.None) {
			return null;
		}

		String packageName = String.format(CLASS_NAME_FORMAT, sp.toString());
		
		OAuthConfig oauthConfig = null;
		try {
			//Class<?> oauthConfigInstanceClass = ScanPackageUtil.getAbstractExtendClass(packageName, OAuthConfigBase.class);
			Class<?> oauthConfigInstanceClass = Class.forName(packageName);
			Constructor<?> constructor = oauthConfigInstanceClass.getConstructor();
			oauthConfig = (OAuthConfig)constructor.newInstance();
		} catch (Exception e) {
			if (LibConstants.isDebug())	logger.debug("OAuthConfigFactory: {}", sp, e);
		}
		
		return oauthConfig;
	}
}
