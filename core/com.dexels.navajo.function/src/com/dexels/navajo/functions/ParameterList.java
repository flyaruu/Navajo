package com.dexels.navajo.functions;


import com.dexels.navajo.parser.FunctionInterface;


/**
 * Title:        Navajo
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      Dexels
 * @author Arjen Schoneveld en Martin Bergman
 * @version $Id$
 */

public final class ParameterList extends FunctionInterface {

    public ParameterList() {}

    public final Object evaluate() throws com.dexels.navajo.parser.TMLExpressionException {
        Integer count = (Integer) this.getOperands().get(0);
        StringBuffer result = new StringBuffer(count.intValue() * 2);

        for (int i = 0; i < (count.intValue() - 1); i++) {
            result.append("?,");
        }
        result.append("?");
        return result.toString();
    }

    public String usage() {
        return "ParameterList(count)";
    }

    public String remarks() {
        return "Create a list of comma separate ? values for use in SQL queries";
    }
}
