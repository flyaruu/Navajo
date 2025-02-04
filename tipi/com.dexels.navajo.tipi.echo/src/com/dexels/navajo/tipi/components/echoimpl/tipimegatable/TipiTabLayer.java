package com.dexels.navajo.tipi.components.echoimpl.tipimegatable;

import java.util.Stack;

import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.Operand;
import com.dexels.navajo.document.Property;
import com.dexels.navajo.tipi.tipixml.XMLElement;

import echopointng.TabbedPane;
import echopointng.tabbedpane.DefaultTabModel;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class TipiTabLayer extends TipiTableBaseLayer {

    private DefaultTabModel defaultTabModel;

    public TipiTabLayer(TipiMegaTable tmt) {
        super(tmt);

    }

    public void loadLayer(XMLElement elt) {
        super.loadLayer(elt);
        /**
         * @todo Implement this
         *       com.dexels.navajo.tipi.components.swingimpl.tipimegatable.TipiMegaTableLayer
         *       abstract method
         */
        messagePath = elt.getStringAttribute("messagePath");
        // String direc = elt.getStringAttribute("direction");
        // if ("top".equals(direc)) {
        // direction = JTabbedPane.TOP;
        // }
        // if ("left".equals(direc)) {
        // direction = JTabbedPane.LEFT;
        // }
        // if ("right".equals(direc)) {
        // direction = JTabbedPane.RIGHT;
        // }
        // if ("bottom".equals(direc)) {
        // direction = JTabbedPane.BOTTOM;
        // }
        // String layout = elt.getStringAttribute("layout");
        // if ("scroll".equals(layout)) {
        // tabLayout = JTabbedPane.SCROLL_TAB_LAYOUT;
        // }
        // if ("wrap".equals(layout)) {
        // tabLayout = JTabbedPane.WRAP_TAB_LAYOUT;
        // }

    }

    public void loadData(final Navajo n, Message current, Stack layerStack, final Component currentPanel) {
        Message nextMessage = null;
        if (current == null) {
            nextMessage = n.getMessage(messagePath);
        } else {
            nextMessage = current.getMessage(messagePath);
        }
        // System.err.println("Tab. Loading with nextMessage:
        // "+nextMessage.getName()+" type: "+nextMessage.getType());
        // System.err.println("My messagePatH: "+messagePath);
        if (layerStack.isEmpty()) {
            return;
        }
        final Message msg = nextMessage;
        final Stack newStack = (Stack) layerStack.clone();
        final TipiTableBaseLayer nextLayer = (TipiTableBaseLayer) newStack.pop();
        // System.err.println("Tab. My stack: "+layerStack);
        TabbedPane jt = new TabbedPane();
        defaultTabModel = new DefaultTabModel();
        jt.setTabSpacing(0);
        // myTabbedPane.setForeground(new Color(0,0,0));
        jt.setModel(defaultTabModel);

        // jt.setTabPlacement(direction);
        // jt.setTabLayoutPolicy(tabLayout);
        currentPanel.add(jt);
        if (msg != null) {
            for (int i = 0; i < msg.getArraySize(); i++) {
                Message cc = msg.getMessage(i);
                // System.err.println("Got message: ");
                // cc.write(System.err);
                // System.err.println("Looking for property: "+titleProperty);
                Property titleProp = cc.getProperty(titleProperty);
                // String title = titleProp.getValue();

                String title = null;
                if (titleProp != null) {
                    System.err
                            .println("*********\nDEPRECATED: You used only a propertyname as title in your scroll layer, in TipiMegaTabel\nYou should just use an expression..\n********");
                    title = titleProp.getValue();
                } else {
                    Operand titleOperand = myTable.getContext().evaluate(titleProperty, myTable, null, cc.getRootDoc(), cc);
                    if (titleOperand != null) {
                        title = "" + titleOperand.value;
                    }
                }

                Column newPanel = new Column();
                defaultTabModel.addTab(title, newPanel);
                // jt.addTab(title,newPanel);
                nextLayer.loadData(n, cc, newStack, newPanel);
            }
        }
    }

}
