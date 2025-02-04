package com.dexels.navajo.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.adapter.JDBCMap;
import com.dexels.navajo.adapter.SQLMap;

public class JDBCFactory {
	
	public final static Logger logger = LoggerFactory.getLogger(JDBCFactory.class);
	private static volatile int useOSGi = -1;
	
	public final static boolean useOSGi() {
		if ( useOSGi != -1) {
			return ( useOSGi == 1);
		}
		try {
			boolean result = navajoadapters.Version.getDefaultBundleContext()!=null;
			if ( result ) {
				useOSGi = 1; 
			} else {
				useOSGi = 0;
			}
			return result;
		} catch (Throwable t) {
			logger.warn("No OSGi libraries found.");
			useOSGi = 0;
			return false;
		}
	}
	
	public static JDBCMappable getJDBCMap() {
		if(useOSGi()) {
			return new JDBCMap();
		} else {
			return new SQLMap();
		}
	}
}
