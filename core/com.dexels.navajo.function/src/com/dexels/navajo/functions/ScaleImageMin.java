/*
 * Created on May 23, 2005
 *
 */
package com.dexels.navajo.functions;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.document.types.Binary;
import com.dexels.navajo.functions.scale.ImageScaler;
import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;

/**
 * @author arjen
 *
 */
public class ScaleImageMin extends FunctionInterface {
    private final static double DEFAULT_COMPRESSION = 0.8;
    
	private final static Logger logger = LoggerFactory
			.getLogger(ScaleImageMin.class);
	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#remarks()
	 */
	public String remarks() {
		return "Scales an image to the specified dimensions. Keeps aspect ratio, and uses the smallest value of the dimensions";
	}

	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#usage()
	 */
	public String usage() {
		return "ScaleImageMin(Binary,int width,int height)";
	}

	/* (non-Javadoc)
	 * @see com.dexels.navajo.parser.FunctionInterface#evaluate()
	 */
	public Object evaluate() throws TMLExpressionException {
	    if (getOperands().size()!=3) {
            throw new TMLExpressionException(this, "Three operands expected. ");
        }
        Binary b = (Binary)getOperand(0);
        Integer width = (Integer)getOperand(1);
        Integer height = (Integer)getOperand(2);

        try {
            Binary res =ImageScaler.scaleToMin(b, width.intValue(), height.intValue(), DEFAULT_COMPRESSION);
            return res;
        } catch (IOException e) {
        	logger.error("Scaling problem: ",e);
        	return null;
        }
      
	}

}
