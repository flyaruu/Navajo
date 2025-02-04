package com.dexels.navajo.tipi.actions;

import com.dexels.navajo.document.Operand;
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
public final class TipiSetCookie extends TipiAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5093937798517391321L;

	public final void execute(TipiEvent event)
			throws com.dexels.navajo.tipi.TipiException,
			com.dexels.navajo.tipi.TipiBreakException {

		Operand nameO = getEvaluatedParameter("name", event);
		Operand valueO = getEvaluatedParameter("value", event);
		if (nameO == null || valueO == null) {
			return;
		}
		String name = (String) nameO.value;
		String value = (String) valueO.value;

		myContext.setCookie(name, value);
	}

}