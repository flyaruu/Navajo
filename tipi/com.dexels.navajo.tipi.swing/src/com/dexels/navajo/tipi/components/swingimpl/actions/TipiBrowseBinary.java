/*
 * Created on Jun 29, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.dexels.navajo.tipi.components.swingimpl.actions;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.dexels.navajo.document.Operand;
import com.dexels.navajo.document.Property;
import com.dexels.navajo.document.types.Binary;
import com.dexels.navajo.tipi.TipiBreakException;
import com.dexels.navajo.tipi.TipiException;
import com.dexels.navajo.tipi.components.swingimpl.swing.DefaultBrowser;
import com.dexels.navajo.tipi.internal.TipiAction;
import com.dexels.navajo.tipi.internal.TipiEvent;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TipiBrowseBinary extends TipiAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8248569662575036320L;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dexels.navajo.tipi.internal.TipiAction#execute(com.dexels.navajo.
	 * tipi.internal.TipiEvent)
	 */
	private File f = null;
	private int result = 0;

	protected void execute(TipiEvent event) throws TipiBreakException,
			TipiException {
		Operand value = getEvaluatedParameter("property", event);
		if (value == null) {
			throw new TipiException("TipiBrowseBinary: no value supplied");
		}
		if (value.value == null) {
			throw new TipiException("TipiBrowseBinary: null value supplied");
		}
		if (!(value.value instanceof Property)) {
			throw new TipiException(
					"TipiOpenBinary: Type of value is not Property, but: "
							+ value.value.getClass());
		}
		final Property pp = (Property) value.value;
		if (!pp.getType().equals(Property.BINARY_PROPERTY)) {
			throw new TipiException(
					"TipiOpenBinary: Property is not type binary , but: "
							+ pp.getType());
		}
		getComponent().runSyncInEventThread(new Runnable() {

			public void run() {
				JFileChooser jf = new JFileChooser(System
						.getProperty("user.home"));
				if (pp.getSubType("description") != null) {
					File file = new File(pp.getSubType("description"));
					jf.setSelectedFile(file);
				}
				result = jf.showOpenDialog((Component) myContext.getTopLevel());
				f = jf.getSelectedFile();
			}
		});

		if (result != JFileChooser.APPROVE_OPTION) {
			throw new TipiBreakException(TipiBreakException.USER_BREAK);
		}

		try {
			Binary b = new Binary(f);
			pp.setAnyValue(b);
			String currentSubtype = pp.getSubType();
			System.err.println("Setting type to: " + f.getPath());
			if (currentSubtype != null && !"".equals(currentSubtype)) {
				// beware, maybe already present?
				pp.setSubType(currentSubtype + "," + "description="
						+ f.getName());
			} else {
				pp.setSubType("description=" + f.getName());
			}
			System.err.println("Sub: " + pp.getSubType());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		File f = File.createTempFile("tipi_", "" + ".pdf");
		// URL u = f.toURL();
		DefaultBrowser.displayURL(f.getAbsolutePath());

	}
}
