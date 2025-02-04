package com.dexels.navajo.tipi.components.swingimpl;

import java.io.IOException;

import org.osgi.framework.BundleContext;

import tipi.MainApplication;
import tipi.SwingTipiApplicationInstance;

import com.dexels.navajo.tipi.TipiException;

public class TipiSwingWrapper {
	public static SwingTipiApplicationInstance runApp(BundleContext bundle, String appInstance) {
		try {
			SwingTipiApplicationInstance applicationInstance = MainApplication.runApp(bundle, appInstance);
			applicationInstance.setBundleContext(bundle);
			applicationInstance.setCurrentContext(applicationInstance.createContext());
			return applicationInstance;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TipiException e) {
			e.printStackTrace();
		}
		return null;
	}
}
