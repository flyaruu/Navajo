/* Generated By:JJTree: Do not edit this line. ASTForAllNode.java */

package com.dexels.navajo.parser;


import java.util.*;
import com.dexels.navajo.document.*;
import com.dexels.navajo.mapping.MappableTreeNode;

public final class ASTForAllNode extends SimpleNode {

    String functionName;
    Navajo doc;
    Message parentMsg;
    MappableTreeNode mapObject;

    public ASTForAllNode(int id) {
        super(id);
    }

   
    /**
     * FORALL(<EXPRESSION>, `[$x] <EXPRESSION>`)
     * E.G. FORALL([/ClubMembership/ClubMemberships/ClubIdentifier], `CheckRelatieCode([$x])`)
     * @return
     * @throws TMLExpressionException
     */
    public final Object interpret() throws TMLExpressionException {

        boolean matchAll = true;

        if (functionName.equals("FORALL"))
            matchAll = true;
        else
            matchAll = false;


        Object a = jjtGetChild(0).interpret();

        String msgList = (String) a;

        Object b = jjtGetChild(1).interpret();

        try {
                ArrayList<Message> list = null;

                if (parentMsg == null) {
                  list = doc.getMessages(msgList);
                }
                else {
                  list = parentMsg.getMessages(msgList);
                }

                for (int i = 0; i < list.size(); i++) {
                    Object o = list.get(i);

                    parentMsg = (Message) o;

                    // ignore definition messages in the evaluation
                    if (parentMsg.getType().equals(Message.MSG_TYPE_DEFINITION))
                      continue;

                    String expr = (String) b;

                    boolean result = Condition.evaluate(expr, doc, mapObject, parentMsg);

                    if ((result == false) && matchAll)
                        return Boolean.FALSE;
                    if ((result == true) && !matchAll)
                        return Boolean.TRUE;
                }

        } catch (com.dexels.navajo.server.SystemException se) {
            se.printStackTrace();
            throw new TMLExpressionException("Invalid expression in FORALL construct: \n" + se.getMessage());
        } catch (NavajoException ne) {
            ne.printStackTrace();
            throw new TMLExpressionException("Invalid expression in FORALL construct: \n" + ne.getMessage());
        }

        if (matchAll)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
}
