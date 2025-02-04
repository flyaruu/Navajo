package com.dexels.navajo.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.Operand;
import com.dexels.navajo.parser.Condition;
import com.dexels.navajo.parser.Expression;
import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;
import com.dexels.navajo.server.SystemException;


/**
 * <p>Title: Navajo Product Project</p>
 * <p>Description: This is the official source for the Navajo server</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Dexels BV</p>
 * @author Arjen Schoneveld
 * @version 1.0
 */

public final class EvaluateExpression extends FunctionInterface {

	
	private final static Logger logger = LoggerFactory
			.getLogger(EvaluateExpression.class);
	
  public String remarks() {
    return "Evaluate a Navajo expression";
  }

  public final Object evaluate() throws com.dexels.navajo.parser.TMLExpressionException {

    if (getOperands().size() != 1 && getOperands().size() != 3) {
      throw new TMLExpressionException("Wrong number of arguments");
    }

    Navajo currentNavajo = this.getNavajo();
    Message currentMessage = this.getCurrentMessage();
    Operand result = null;

    boolean conditional = (this.getOperands().size() == 3);

    if (!conditional) {
      String expression = (String) getOperand(0);

      try {
        //System.err.println("Evaluating " + expression + " against Navajo " + currentNavajo + ", and message " + currentMessage);
        result = Expression.evaluate(expression, currentNavajo, null, currentMessage);
      }
      catch (SystemException ex) {
    	  logger.error("Error: ", ex);
      }
      catch (TMLExpressionException ex) {
    	  logger.error("Error: ",ex);
      }
    } else {
       String condition = (String) getOperand(0);
       String exp1 = (String) getOperand(1);
       String exp2 = (String) getOperand(2);

      try {
        if (Condition.evaluate(condition, currentNavajo, null, currentMessage)) {
          result =  Expression.evaluate(exp1, currentNavajo, null, currentMessage);
        } else {
          result =  Expression.evaluate(exp2, currentNavajo, null, currentMessage);
        }
      }
      catch (SystemException ex1) {
    	  logger.error("Error: ", ex1);
    	  throw new TMLExpressionException(this, ex1.getMessage());
      }
      catch (TMLExpressionException ex1) {
    	  logger.error("Error: ", ex1);
    	  throw new TMLExpressionException(this, ex1.getMessage());
      }
    }

    if (result != null) {
      return result.value;
    } else {
      return null;
    }
  }

  public String usage() {
    return "EvaluateExpression(expression);EvaluateExpression(condition, expression1, expression2)";
  }

  public static void main(String [] args) throws Exception {
    Operand o = Expression.evaluate("EvaluateExpression('5 < 4', '\\'Aap\\'', '\\'Noot\\'')", null);
    System.err.println("o.value = " + o.value);
  }
}
