package com.dexels.navajo.adapter;

/**
 * @author  Arjen Schoneveld
 * @version $Id$
 * 
 * This class is used to call a Navajo webservice like the NavajoMap.
 * Difference is that it compares the current Navajo with the Navajo
 * result of the called Navajo webservice. If the two differ a special
 * message "DataHasChanged" is created containing a property "Overwrite".
 * The current Navajo is included in the result, overwriting the Navajo
 * of the called webservice if message names coincide.
 * 
 * The purpose of this adapter is to use it for checking data changes between
 * an original query and the subsequent update.
 */
import java.util.ArrayList;
import java.util.List;

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Method;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.NavajoException;
import com.dexels.navajo.document.NavajoFactory;
import com.dexels.navajo.document.Property;
import com.dexels.navajo.server.AuthorizationException;
import com.dexels.navajo.server.ConditionErrorException;
import com.dexels.navajo.server.SystemException;
import com.dexels.navajo.server.UserException;

public class VersionedNavajoMap extends NavajoMap {

 public void setDoSend(String method) throws UserException, ConditionErrorException, SystemException, AuthorizationException {

     super.setDoSend(method);

     System.err.println("in VersionedNavajoMap store()");
     if (!this.isEqual) {

       System.err.println("ABOUT TO CONSTRUCT MESSAGES!");
       try {
         Navajo currentOutDoc = super.access.getOutputDoc();

         Message confirm = NavajoFactory.getInstance().createMessage(
             currentOutDoc, "DataHasChanged");
         currentOutDoc.addMessage(confirm);
         Property b = NavajoFactory.getInstance().createProperty(currentOutDoc,
             "Overwrite", Property.BOOLEAN_PROPERTY, "false", 1, "",
             Property.DIR_IN);
         confirm.addProperty(b);

         List<Method> allMethods = inMessage.getAllMethods();
         for (int i = 0; i < allMethods.size(); i++) {
           Method m = allMethods.get(i);
           System.err.println("Adding method: " + m.getName());
           Method a = NavajoFactory.getInstance().createMethod(currentOutDoc,
               m.getName(), "");
           List<String> required = m.getRequiredMessages();
           for (int j = 0; j < required.size(); j++) {
             String name = required.get(j);
              System.err.println("Adding required message: " + name);
             a.addRequired(name);
           }
           currentOutDoc.addMethod(a);
         }

         List<Message> allMessages = inMessage.getAllMessages();
         for (int i = 0; i < allMessages.size(); i++) {

           Message m = (Message) allMessages.get(i);
            System.err.println("Adding message: " + m.getName());
           Message a = inMessage.copyMessage(m, currentOutDoc);
           currentOutDoc.addMessage(a);
         }
       }
       catch (NavajoException ne) {
         throw new UserException( -1, ne.getMessage(), ne);
       }
     }
  }

  public static void main(String [] args) throws Exception {
     Navajo n = NavajoFactory.getInstance().createNavajo(new java.io.FileInputStream("/home/arjen/@@.tml"));
     n.write(System.err);
     ArrayList l = n.getAllMethods();
     System.err.println("l = " + l.size());
     System.exit(1);
  }
}