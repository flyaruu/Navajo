package com.dexels.navajo.functions;

import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;
import com.dexels.navajo.tipi.vaadin.VaadinTipiContext;

public class CreateEchoUrl extends FunctionInterface {

	@Override
	public Object evaluate() throws TMLExpressionException {
		if (getOperands().size() != 2) {
			throw new TMLExpressionException("CreateEchoUrl needs two parameters");
		}
		Object oo = getOperand(0);
		if(!(oo instanceof VaadinTipiContext)) {
			throw new TMLExpressionException("CreateEchoUrl: param one should be an EchoTipiContext");
		}
		VaadinTipiContext ee = (VaadinTipiContext)getOperand(0);
		String expression = (String)getOperand(1);
		String result = ee.createExpressionUrl(expression);
		return result;
	}

	@Override
	public String remarks() {
		return null;
	}

	@Override
	public String usage() {
		return(" CreateEchoUrl(context, object) returns a string pointing to the object.\nContext will usually be {context:/} ");
	}

}
