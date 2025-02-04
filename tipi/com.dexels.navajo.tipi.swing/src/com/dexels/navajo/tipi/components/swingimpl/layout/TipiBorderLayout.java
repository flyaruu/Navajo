package com.dexels.navajo.tipi.components.swingimpl.layout;

import java.awt.BorderLayout;

import com.dexels.navajo.tipi.TipiValue;
import com.dexels.navajo.tipi.components.core.TipiLayoutImpl;

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
public class TipiBorderLayout extends TipiLayoutImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8754706361402823467L;
	BorderLayout layout = new BorderLayout();

	public TipiBorderLayout() {
	}

	public void createLayout() {
		setLayout(new BorderLayout());
	}

	public Object createDefaultConstraint(int index) {
		switch (index) {
		case 0:
			return BorderLayout.CENTER;
		case 1:
			return BorderLayout.NORTH;
		case 2:
			return BorderLayout.SOUTH;
		case 3:
			return BorderLayout.EAST;
		case 4:
			return BorderLayout.WEST;
		default:
			return null;
		}
	}

	protected void setValue(String name, TipiValue tv) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Object parseConstraint(String text, int index) {

		if (text == null) {
			return null;
		}
		int ind = text.indexOf(":");
		if (ind != -1) {
			text = text.substring(0, ind);
		}
		if (text.equals("center") || text.equals(BorderLayout.CENTER)) {
			return BorderLayout.CENTER;
		}
		if (text.equals("north") || text.equals(BorderLayout.NORTH)) {
			return BorderLayout.NORTH;
		}
		if (text.equals("south") || text.equals(BorderLayout.SOUTH)) {
			return BorderLayout.SOUTH;
		}
		if (text.equals("east") || text.equals(BorderLayout.EAST)) {
			return BorderLayout.EAST;
		}
		if (text.equals("west") || text.equals(BorderLayout.WEST)) {
			return BorderLayout.WEST;
		}
		return BorderLayout.CENTER;
	}
}