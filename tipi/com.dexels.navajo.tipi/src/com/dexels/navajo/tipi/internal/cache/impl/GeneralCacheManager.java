package com.dexels.navajo.tipi.internal.cache.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.dexels.navajo.tipi.internal.cache.CacheManager;
import com.dexels.navajo.tipi.internal.cache.LocalStorage;
import com.dexels.navajo.tipi.internal.cache.RemoteStorage;

public class GeneralCacheManager implements CacheManager {

	private final LocalStorage local;
	private final RemoteStorage remote;

	public GeneralCacheManager(LocalStorage l, RemoteStorage r) {
		this.local = l;
		this.remote = r;
	}

	public InputStream getContents(String location) throws IOException {
		if (isUpToDate(location)) {
			return local.getLocalData(location);
		}
		Map<String, Object> metadata = new HashMap<String, Object>();
		InputStream is = remote.getContents(location, metadata);
		if (is == null) {
			return null;
		}
		local.storeData(location, is, metadata);
		return local.getLocalData(location);
	}

	public boolean hasLocal(String location) throws IOException {
		return local.hasLocal(location);
	}

	public boolean isUpToDate(String location) throws IOException {
		if (!hasLocal(location)) {
			System.err.println("Reporting not local");
			return false;
		}
		System.err.println("");
		long localMod = local.getLocalModificationDate(location);
		long remoteMod = remote.getRemoteModificationDate(location);
		if (localMod >= remoteMod) {
			System.err.println("CACHE HIT!");
			return true;
		}
		System.err.println("Local is ");
		System.err.println("CACHE MISSSS!");
		return false;
	}

	public URL getLocalURL(String location) throws IOException {
		if (isUpToDate(location)) {
			return local.getURL(location);
		}
		Map<String, Object> metadata = new HashMap<String, Object>();
		InputStream is = remote.getContents(location, metadata);
		System.err.println("Loading data to local storage: " + metadata);
		local.storeData(location, is, metadata);
		return local.getURL(location);
	}

	public URL getRemoteURL(String location) throws IOException {
		return remote.getURL(location);
	}

	public void flushCache() {
		try {
			local.flushAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
