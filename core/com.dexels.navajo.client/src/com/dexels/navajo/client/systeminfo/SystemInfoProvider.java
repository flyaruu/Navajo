package com.dexels.navajo.client.systeminfo;

public interface SystemInfoProvider {

	public abstract int getCpuCount();

	public abstract long getMaxMem();

	public abstract String getOs();

	public abstract String getOsVersion();

	public abstract String getJavaVersion();

	public abstract String getOsArch();

	/**
	 * Returns SystemInfo in compact string.
	 */
	public abstract String toString();

	public abstract void init();

}