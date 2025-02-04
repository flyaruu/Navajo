package com.dexels.navajo.dnd;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

public class TestDragTable extends JTable implements Runnable {

	private static final long serialVersionUID = -8822849623440273745L;
	JTable jt;

	public void run() {
		JTextField jtf = new JTextField("Drag This!");
		jtf.setDragEnabled(true);
		String[][] data = new String[10][10];
		String[] names = new String[10];
		for (int i = 0; i < 10; i++) {
			names[i] = String.valueOf(i);
			for (int j = 0; j < 10; j++)
				data[i][j] = String.valueOf(j);
		}
		DefaultTableModel dtm = new DefaultTableModel(data, names);
		jt = this;
		jt.setModel(dtm);
		jt.setDragEnabled(true);
		System.out.println(jt.getDropMode());
		jt.setDropMode(DropMode.ON_OR_INSERT);
		jt.setTransferHandler(new TransferHandler2());
		JFrame jf = new JFrame();
		jf.add(jt);
		jf.add(jtf, BorderLayout.NORTH);
		jf.pack();
		jf.setVisible(true);

	}

	public static class TransferHandler2 extends TransferHandler {
		private static final long serialVersionUID = 867827589145563565L;

		@Override
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			super.exportAsDrag(comp, e, action);
		}

		TransferHandler2() {
			super();
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			return true;
		}

		@Override
		public boolean importData(TransferSupport support) {
			System.err.println("Importing");
			return super.importData(support);
		}

	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new TestDragTable());
	}

}