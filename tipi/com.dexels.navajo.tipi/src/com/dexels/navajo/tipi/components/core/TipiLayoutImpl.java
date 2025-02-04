package com.dexels.navajo.tipi.components.core;

import java.util.List;

import com.dexels.navajo.tipi.TipiComponent;
import com.dexels.navajo.tipi.internal.TipiLayout;
import com.dexels.navajo.tipi.tipixml.XMLElement;

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
public abstract class TipiLayoutImpl extends TipiLayout {

	private static final long serialVersionUID = 4380200289873893303L;
	protected XMLElement myInstanceElement;

	protected Object parseConstraint(String text, int index) {
		return null;
	}

	public void loadLayout(XMLElement def, TipiComponent t)
			throws com.dexels.navajo.tipi.TipiException {
		myInstanceElement = def;
		myComponent = t;
		List<XMLElement> v = myInstanceElement.getChildren();
		for (int i = 0; i < v.size(); i++) {
			XMLElement child = v.get(i);
			String constraintString = child.getStringAttribute("constraint");
			if (!child.getName().equals("event")
					&& !child.getName().startsWith("on")) {
				Object constraint = parseConstraint(constraintString, i);

				t.addComponentInstance(myContext, child, constraint);
			} else {
				System.err.println("Event found within layout. Line: "
						+ def.getLineNr() + "\nNot right, but should work");
			}
		}
	}

	public Object getDefaultConstraint(TipiComponent tc, int index) {
		return null;
	}
}
