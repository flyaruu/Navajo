package com.dexels.navajo.rich.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class RichTableCellRenderer implements TableCellRenderer {

	public JLabel component = new JLabel();
	private Font bold = new Font("Dialog", Font.BOLD, 12);
	private Font plain = new Font("Dialog", Font.PLAIN, 12);

	public RichTableCellRenderer() {
		component.setOpaque(false);
		component.setForeground(Color.white);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			component.setFont(bold);
		} else {
			component.setFont(plain);
		}
		component.setText(value.toString());
		return component;
	}

}
