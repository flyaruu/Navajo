package com.dexels.navajo.tipi.projectbuilder.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.dexels.navajo.tipi.projectbuilder.ClientActions;
import com.dexels.navajo.tipi.projectbuilder.TipiProjectBuilder;
import com.dexels.navajo.tipi.util.XMLElement;

public class TipiRemoteJnlpProjectBuilder extends TipiProjectBuilder {

	public void downloadExtensionJars(String extensionName, String version, URL remoteExtensionUrl, XMLElement extensionElement,
			File baseDir, boolean clean, boolean localSign) throws MalformedURLException, IOException {
		System.err.println("Project dir: " + remoteExtensionUrl);
		URL signed = new URL(remoteExtensionUrl, "lib/");

		File f = new File(baseDir, "lib");
		if (f.exists()) {
			f.delete();
		}
		f.mkdirs();
		XMLElement main = extensionElement.getElementByTagName("main");
		if (main != null) {
			String path = main.getStringAttribute("proxyjar");
			URL jar = new URL(signed, path);
			if(useJnlpVersioning()) {
				path = path.substring(0,path.length()-4)+"__V"+version+".jar";
			}

//			URL jar = new URL(unsigned, path);
				ClientActions.downloadFile(jar, path, f,clean,false);
		}
//		downloadProjectInclude(extensionName, remoteExtensionUrl, extensionElement, baseDir, clean);

	}

}
