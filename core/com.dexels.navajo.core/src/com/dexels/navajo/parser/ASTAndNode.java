package com.dexels.navajo.parser;


public final class ASTAndNode extends SimpleNode {
    public ASTAndNode(int id) {
        super(id);
    }

    public final Object interpret() throws TMLExpressionException {

        Boolean a = (Boolean) jjtGetChild(0).interpret();

        if(a==null) {
        	return Boolean.FALSE;
        }
        if (a.booleanValue() == false) {
			return a;
		}
        Boolean b = (Boolean) jjtGetChild(1).interpret();
        if(b==null) {
        	return Boolean.FALSE;
        }

        return Boolean.valueOf(a.booleanValue() && b.booleanValue());
    }
}
