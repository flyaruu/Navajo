package com.dexels.navajo.tipi.actions;

import com.dexels.navajo.tipi.internal.TipiAction;
import com.dexels.navajo.tipi.internal.TipiEvent;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class TipiDumpStack extends TipiAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3731646268988692805L;

	public void execute(TipiEvent event)
			throws com.dexels.navajo.tipi.TipiException,
			com.dexels.navajo.tipi.TipiBreakException {
		System.err.println("JAVA SAYS:");
		Thread.dumpStack();
		System.err.println("TIPI SAYS:");
		dumpStack("Dumpstack");

		System.err.println("Event params: ");
		for (String s : event.getEventKeySet()) {
			System.err.println("Param: " + s + " value: "
					+ event.getEventParameter(s));

		}
	}

}