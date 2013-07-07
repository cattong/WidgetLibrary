package com.shejiaomao.core.http.config;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shejiaomao.core.LibConstants;
import com.shejiaomao.core.ServiceProvider;

public class HttpConfigFactory {
    private static final String CLASS_NAME_FORMAT = "com.shejiaomao.core.impl.%1$s.%2$sHttpConfig";

    private static final Logger logger = LoggerFactory.getLogger(HttpConfigFactory.class);
    
	private static Hashtable<ServiceProvider, HttpConfig> spHttpConfigs =
		new Hashtable<ServiceProvider, HttpConfig>();

	public static synchronized HttpConfig getHttpConfiguration(ServiceProvider sp) {

		HttpConfig conf = spHttpConfigs.get(sp);
		if (conf != null) {
			return conf;
		}

		try {
			if (sp == ServiceProvider.None) {
				conf = new HttpConfigBase();
			} else {				
				String className =  String.format(CLASS_NAME_FORMAT,
	                 sp.toString().toLowerCase(),
	                 sp.toString()
	            );

		        conf = (HttpConfig) Class.forName(className).newInstance();
			}

        } catch (Exception e) {
        	if (LibConstants.level <= LibConstants.VERBOSE) logger.debug("Get HttpConfig instance unfound for {}", sp, e);
        	conf = new HttpConfigBase();
        }
		spHttpConfigs.put(sp, conf);

		return conf;
	}
}
