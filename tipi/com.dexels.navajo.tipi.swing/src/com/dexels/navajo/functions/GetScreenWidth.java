package com.dexels.navajo.functions;

import java.awt.Toolkit;

import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;

/**
 * @author frank
 * 
 */
public class GetScreenWidth extends FunctionInterface {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dexels.navajo.parser.FunctionInterface#remarks()
	 */
	public String remarks() {
		return "Returns the width of the screen (using Toolkit)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dexels.navajo.parser.FunctionInterface#usage()
	 */
	public String usage() {
		return "GetScreenWidth()";
	}

	// GetComponent({component://init/desktop},{event:/from})
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dexels.navajo.parser.FunctionInterface#evaluate()
	 */
	public Object evaluate() throws TMLExpressionException {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

}
