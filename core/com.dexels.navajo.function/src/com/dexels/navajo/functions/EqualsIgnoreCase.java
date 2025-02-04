package com.dexels.navajo.functions;


/**
 * Title:        Navajo
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Dexels
 * @author Arjen Schoneveld en Martin Bergman
 * @version $Id$
 */

import java.util.List;

import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;


public final class EqualsIgnoreCase  extends FunctionInterface {

    public String remarks() {
        return "";
    }

    public String usage() {
        return "";
    }

    public final Object evaluate() throws TMLExpressionException {

        List<?> operands = this.getOperands();

        if (operands.size() != 2)
            throw new TMLExpressionException("Invalid number of arguments for EqualsIgnoreCase()");
        String a = (String) operands.get(0);
        String b = (String) operands.get(1);

        return new Boolean(a.equalsIgnoreCase(b));
    }
}
