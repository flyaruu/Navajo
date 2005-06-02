/* Generated By:JJTree: Do not edit this line. ASTListNode.java */

package com.dexels.navajo.parser;


import java.util.ArrayList;


public final class ASTListNode extends SimpleNode {

    int args = 0;

    public ASTListNode(int id) {
        super(id);
    }

    public final Object interpret() throws TMLExpressionException {

        ArrayList list = new ArrayList();

        for (int i = 0; i < args; i++) {
            Object a = (Object) jjtGetChild(i).interpret();

            list.add(a);
        }
        return list;
    }
}
