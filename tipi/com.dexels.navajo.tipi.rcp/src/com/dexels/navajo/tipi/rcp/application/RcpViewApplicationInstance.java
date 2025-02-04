package com.dexels.navajo.tipi.rcp.application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;

import tipi.BaseTipiApplicationInstance;
import tipi.TipiApplicationInstance;
import tipi.TipiExtension;

import com.dexels.navajo.tipi.TipiContext;
import com.dexels.navajo.tipi.rcp.TipiRcpContext;

public class RcpViewApplicationInstance extends BaseTipiApplicationInstance implements TipiApplicationInstance {

	private final Composite compositeParent;
	public RcpViewApplicationInstance(Composite parent) {
		compositeParent = parent;
		try {
			setCurrentContext(createContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TipiContext createContext() throws IOException {
		return new TipiRcpContext(this,compositeParent, new ArrayList<TipiExtension>(), null);
	}

	@Override
	public void dispose(TipiContext t) {

	}

	@Override
	public String getDefinition() {
		return null;
	}

	@Override
	public void setEvalUrl(URL context, String relativeUri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContextUrl(URL contextUrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public URL getContextUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
