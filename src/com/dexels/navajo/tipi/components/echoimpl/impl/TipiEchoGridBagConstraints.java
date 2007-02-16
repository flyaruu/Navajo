package com.dexels.navajo.tipi.components.echoimpl.impl;

import java.util.*;

import echopointng.able.Sizeable;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.layout.GridLayoutData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TipiEchoGridBagConstraints
    extends GridLayoutData {
  private String[] myConstraints = new String[14];
  public TipiEchoGridBagConstraints() {
	  super();
  }

  public TipiEchoGridBagConstraints(int x, int y, int w, int h, double wx, double wy, int anchor, int fill, Insets insets, int padx, int pady) {
//    super(x, y, w, h, wx, wy, anchor, fill, insets, padx, pady);
  }

  public void parse(String cs, int index) {
//    String cs = elm.getStringAttribute("gridbag");
    StringTokenizer tok = new StringTokenizer(cs, ", ");
    int tokenCount = tok.countTokens();
    if (tokenCount != 14) {
      throw new RuntimeException("Gridbag for: " + cs + " is invalid!");
    }
    else {
      for (int i = 0; i < 14; i++) {
//        int con = new Integer(tok.nextToken()).intValue();
        myConstraints[i] = tok.nextToken();
      }
    
      int gridx = Integer.parseInt(myConstraints[0]);
      int gridy = Integer.parseInt(myConstraints[1]);
      int gridwidth = Integer.parseInt(myConstraints[2]);
      int gridheight = Integer.parseInt(myConstraints[3]);
      double weightx = Double.parseDouble(myConstraints[4]);
      double weighty = Double.parseDouble(myConstraints[5]);
      int anchor = Integer.parseInt(myConstraints[6]);
      int fill = Integer.parseInt(myConstraints[7]);
      Insets insets = new Insets(Integer.parseInt(myConstraints[8]), Integer.parseInt(myConstraints[9]), Integer.parseInt(myConstraints[10]), Integer.parseInt(myConstraints[11]));
      int ipadx = Integer.parseInt(myConstraints[12]);
      int ipady = Integer.parseInt(myConstraints[13]);
      
      
      setColumnSpan(gridwidth);
      setRowSpan(gridheight);
      System.err.println("ROW/COLUMNSPAN: "+gridwidth+" / "+gridheight);
//      if(weightx>0 && child instanceof Sizeable) {
//    	  Sizeable s = (Sizeable)child;
//    	  s.setWidth(new Extent(90,Extent.PERCENT));
//      }
//      
    }
  }

  public TipiEchoGridBagConstraints(String s, Component parent, Component child, int index) {
    parse(s,index);
  }

//  public static void main(String[] args) {
//    XMLElement bert = new CaseSensitiveXMLElement();
//    bert.setName("Bert");
//    bert.setAttribute("name", "bert_een");
//    bert.setAttribute("gridbag", "1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0");
//    TipiEchoGridBagConstraints bertje = new TipiEchoGridBagConstraints("1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0");
//  }

//  public String toString() {
//    return "" + gridx + "," + gridy + "," + gridwidth + "," + gridheight + "," + weightx + "," + weighty + "," + anchor + "," + fill + "," + insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right + "," + ipadx + "," + ipady;
//  }
}
