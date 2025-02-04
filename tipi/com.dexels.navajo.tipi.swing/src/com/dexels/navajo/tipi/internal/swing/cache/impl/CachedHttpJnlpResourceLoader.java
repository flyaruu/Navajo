package com.dexels.navajo.tipi.internal.swing.cache.impl;

import java.net.URL;

import javax.jnlp.UnavailableServiceException;

import com.dexels.navajo.tipi.internal.CachedResourceLoader;
import com.dexels.navajo.tipi.internal.cache.CacheManager;
import com.dexels.navajo.tipi.internal.cache.impl.GeneralCacheManager;
import com.dexels.navajo.tipi.internal.cache.impl.HttpRemoteStorage;
import com.dexels.navajo.tipi.internal.cookie.CookieManager;

public class CachedHttpJnlpResourceLoader extends CachedResourceLoader {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7140275852274687914L;
	protected final CacheManager cache;

	public CachedHttpJnlpResourceLoader(String relativePath, URL baseUrl,
			CookieManager cm) throws UnavailableServiceException {
		System.err.println("JNLP Local storage instantiated");
		cache = new GeneralCacheManager(new JnlpLocalStorage(relativePath, cm),
				new HttpRemoteStorage(baseUrl));
	}

	public CacheManager getCacheManager() {
		return cache;
	}

}
