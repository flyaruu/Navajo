package com.dexels.navajo.tipi.impl;

import java.awt.Component;
import com.dexels.navajo.tipi.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DefaultEventMapper {
  private TipiComponent myComponent = null;
  public DefaultEventMapper(TipiComponent tc) {
    myComponent = tc;
  }

  public void deregisterEvent(TipiEvent e) {
    System.err.println("BEWARE..EVENT IS STILL CONNECTED TO THE COMPONENT!!");
  }

  public void registerEvent(TipiEvent te) {
    Component c = myComponent.getContainer();
    if (c==null) {
      System.err.println("Cannot register swing event: Container is null!");
      return;
    }

    if (te.isTrigger("onActionPerformed", null)) {
      try {
        java.lang.reflect.Method m = c.getClass().getMethod("addActionListener", new Class[] {ActionListener.class});
        ActionListener bert = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              myComponent.performTipiEvent("onActionPerformed", e);
            }
            catch (TipiException ex) {
              ex.printStackTrace();
            }
          }
        };
        m.invoke(c, new Object[] {bert});
      }
      catch (Exception exe) {
        exe.printStackTrace();
      }
    }
    if (te.isTrigger("onWindowClosed", null)) {
      if (JInternalFrame.class.isInstance(c)) {
        JInternalFrame jj = (JInternalFrame) c;
        jj.addInternalFrameListener(new InternalFrameAdapter() {
          public void internalFrameClosing(InternalFrameEvent e) {
            try {
              myComponent.performTipiEvent("onWindowClosed", e);
            }
            catch (TipiException ex) {
              ex.printStackTrace();
            }
          }
        });
      }
      else if (JFrame.class.isInstance(c)) {
        JFrame jj = (JFrame) c;
        jj.addWindowListener(new WindowAdapter() {
          public void windowClosed(WindowEvent e) {
            try {
              myComponent.performTipiEvent("onWindowClosed", e);
            }
            catch (TipiException ex) {
              ex.printStackTrace();
            }
          }
        });
      }
      else {
        throw new RuntimeException("Can not fire onWindowClosed event from class: " + c.getClass());
      }
    }
    if (te.isTrigger("onMouseEntered", null)) {
      c.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          try {
            myComponent.performTipiEvent("onMouseEntered", e);
          }
          catch (TipiException ex) {
            ex.printStackTrace();
          }
        }
      });
    }
    if (te.isTrigger("onActionExited", null)) {
      c.addMouseListener(new MouseAdapter() {
        public void mouseExited(MouseEvent e) {
          try {
            myComponent.performTipiEvent("onMouseExited", e);
          }
          catch (TipiException ex) {
            ex.printStackTrace();
          }
        }
      });
    }
  }
}