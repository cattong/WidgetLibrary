package com.shejiaomao.core;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shejiaomao.core.api.SocialService;
import com.shejiaomao.core.http.auth.Authorization;

public class ApiFactory {
    private final static Logger logger = LoggerFactory.getLogger(ApiFactory.class);
    
	private static <T> T getServiceInstance(Authorization auth, Class<T> clazz) {
		if (auth == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
		
		String packageName = ApiFactory.class.getPackage().getName();
		ServiceProvider sp = auth.getServiceProvider();
		packageName += ".impl";
		packageName += "." + clazz.getSimpleName() + "Impl";
		
		T serviceInstance = null;
		Class<?>[] constructorParams = {Authorization.class};
		try {
			Class<?> instanceClass = Class.forName(packageName);
			Constructor<?> constructor = instanceClass.getConstructor(constructorParams);
			serviceInstance = (T)constructor.newInstance(auth);			
		} catch (Exception e) {
			if (LibConstants.isDebug()) logger.error("ApiFactory: {}", sp, e);
		}
		
		return serviceInstance;
	}

	public static SocialService getSocialService(Authorization auth) {
		if (auth == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
		
		return (SocialService)getServiceInstance(auth, SocialService.class);
	}
}
