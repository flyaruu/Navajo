/*
 * Created on May 23, 2005
 *
 */
package com.dexels.navajo.functions;

import com.dexels.navajo.document.jaxpimpl.xml.XMLutils;
import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;

/**
 * @author arjen
 *
 */
public class XmlEscape extends FunctionInterface {

	  public final static String XML_ESCAPE_DELIMITERS = "&'<>\"";

	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#remarks()
	 */
	public String remarks() {
		return "Escapes the following characters in the characters: &'<>\"";
	}

	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#usage()
	 */
	public String usage() {
		return "XmlEscape(String)";
	}

	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#evaluate()
	 */
	public Object evaluate() throws TMLExpressionException {
		Object o = getOperand(0);
		if (o == null) {
			return null;
		}
		if (o instanceof String) {
			return XMLutils.XMLEscape((String) o);
		}  else {
			throw new TMLExpressionException(this, "Invalid operand: " + o.getClass().getName());
		}
	}


}
