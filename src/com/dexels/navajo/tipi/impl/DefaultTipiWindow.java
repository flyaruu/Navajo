package com.dexels.navajo.tipi.impl;

import com.dexels.navajo.tipi.*;
import com.dexels.navajo.tipi.components.*;
import nanoxml.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import tipi.*;
import java.net.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DefaultTipiWindow
    extends DefaultTipi {

//  private int x, y, w, h;

  public DefaultTipiWindow() {
    initContainer();
   }

  public Container createContainer() {
    JInternalFrame f = new JInternalFrame();
    f.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
    return f;
  }

  public void addToContainer(Component c, Object constraints) {
    ((JInternalFrame)getContainer()).getContentPane().add(c,constraints);
  }
  public void setContainerLayout(LayoutManager layout){
    ((JInternalFrame)getContainer()).getContentPane().setLayout(layout);
  }
  public void setComponentValue(String name, Object object) {
    super.setComponentValue(name,object);
    JInternalFrame jj = (JInternalFrame)getContainer();
    Rectangle r = jj.getBounds();
    if (name.equals("x")) {
      r.x = Integer.parseInt( (String) object);
    }
    if (name.equals("y")) {
     r.y = Integer.parseInt( (String) object);
   }
   if (name.equals("w")) {
     r.width = Integer.parseInt( (String) object);
   }
   if (name.equals("h")) {
     r.height = Integer.parseInt( (String) object);
   }
   if (name.equals("iconifiable")) {
     boolean b = object.equals("true");
     jj.setIconifiable(b);
   }
   if (name.equals("maximizable")) {
     boolean b = object.equals("true");
     jj.setMaximizable(b);
   }
   if (name.equals("closable")) {
     boolean b = object.equals("true");
     jj.setClosable(b);
   }
   if (name.equals("resizable")) {
     boolean b = object.equals("true");
     jj.setResizable(b);
   }
   if (name.equals("title")) {
    jj.setTitle((String)object);
  }
  if (name.equals("icon")) {
    String icon = (String)object;
          try{
            URL i = new URL(icon);
//            JFrame f = new JFrame();
            ImageIcon ic = new ImageIcon(i);
            jj.setFrameIcon(ic);
          }catch(Exception e){
             URL t = MainApplication.class.getResource(icon);
             if(t!=null){
               ImageIcon ii = new ImageIcon(t);
               jj.setFrameIcon(ii);
             }
          }
//        }

  }

   jj.setBounds(r);
  }
}
