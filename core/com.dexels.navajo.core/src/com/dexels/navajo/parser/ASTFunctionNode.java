package com.dexels.navajo.parser;


/**
 * $Id$
 * $Log$
 * Revision 1.31  2012/04/05 14:59:57  frank
 * remove odd static NavajoBundleManager.getInstance call
 *
 * Revision 1.30  2011/04/15 14:17:32  frank
 * renamed 'Version' (the basic one) to 'AbstractVersion' it seems to confuse Tycho
 *
 * Revision 1.29  2011/04/05 15:48:28  frank
 * Added OSGi based function solution
 *
 * Revision 1.28  2009/02/04 15:36:21  arjen
 * Alway evaluate function with type checking enabled.
 *
 * Revision 1.27  2009/02/04 13:48:37  arjen
 * Several changes for extension mechanism support.
 *
 * Revision 1.26  2008/09/29 09:48:15  arjen
 * Major changes:
 * 1. Better testability.
 * 2. Support for auditlog events
 * 3. Support for exception events
 * 4. More JMX support
 * 5. Restructuring of NavajoConfig
 * 6. ...
 *
 * Revision 1.25  2008/02/08 14:57:13  arjen
 * Added empty header check.
 *
 * Revision 1.24  2008/01/11 16:55:36  arjen
 * Suppress unchecked type warnings
 *
 * Revision 1.23  2007/08/21 10:05:52  frank
 * added exception chaining
 *
 * Revision 1.22  2006/12/13 17:16:14  arjen
 * Beta functionality also works again for adapters.
 *
 * Revision 1.21  2006/06/27 16:00:26  frank
 * Did not run in a dispatcherless environment (e.g. tipi)
 *
 * Revision 1.20  2006/05/31 14:10:37  arjen
 * Refactored for new Dispatcher.getInstance() methodology
 *
 * Revision 1.19  2005/06/02 18:09:36  arjen
 * Removed redundant imports.
 *
 * Revision 1.18  2005/05/17 15:55:26  arjen
 * *** empty log message ***
 *
 * Revision 1.17  2004/08/20 12:01:06  arjen
 * Added support for ExecuteScript function.
 *
 * Revision 1.16  2004/04/01 09:23:42  arjen
 * Added support for setting the current Message context in FunctionInterface.
 *
 * Revision 1.15  2004/01/12 16:38:36  arjen
 * Added lot's of final qualifiers.
 *
 * Revision 1.14  2003/10/31 16:58:01  arjen
 * Added support for passing incomming Navajo document to FunctionInterface context.
 *
 * Revision 1.13  2003/10/21 13:15:25  arjen
 * Added support for hour,minute,second in Navajo date types.
 *
 * Revision 1.12  2003/06/02 16:19:42  arjen
 * *** empty log message ***
 *
 * Revision 1.11  2003/06/02 11:53:37  aphilip
 * Watch it!
 *
 * Revision 1.10  2003/05/16 08:28:31  arjen
 * *** empty log message ***
 *
 * Revision 1.9  2003/05/08 15:02:48  frank
 * <No Comment Entered>
 *
 * Revision 1.8  2002/11/26 17:03:28  arjen
 * <No Comment Entered>
 *
 * Revision 1.7  2002/11/09 13:26:41  arjen
 * Removed use of objectpool  for function: possible race-condition problem!
 *
 * Revision 1.6  2002/11/08 16:28:44  arjen
 * <No Comment Entered>
 *
 * Revision 1.5  2002/11/06 09:33:47  arjen
 * Used Jacobe code beautifier over all source files.
 * Added log4j support.
 *
 * Revision 1.4  2002/09/18 16:03:41  matthijs
 * <No Comment Entered>
 *
 * Revision 1.3  2002/06/11 15:16:03  arjen
 * *** empty log message ***
 *
 * Revision 1.2  2002/06/10 15:11:16  arjen
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2002/06/05 10:12:27  arjen
 * Navajo
 *
 * Revision 1.6  2002/05/17 15:15:12  arjen
 * *** empty log message ***
 *
 * Revision 1.5  2002/03/07 16:15:27  arjen
 * *** empty log message ***
 *
 * Revision 1.4  2002/03/01 09:48:28  arjen
 * <No Comment Entered>
 *
 */

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.Selection;
import com.dexels.navajo.functions.util.FunctionFactoryFactory;
import com.dexels.navajo.functions.util.FunctionFactoryInterface;
import com.dexels.navajo.server.DispatcherFactory;


public final class ASTFunctionNode extends SimpleNode {

	String functionName;
	int args = 0;
	Navajo doc;
	Message parentMsg;
	Message parentParamMsg;
	Selection parentSel;
	//Access access;

	public ASTFunctionNode(int id) {
		super(id);
	}

	public final Object interpret() throws TMLExpressionException {

		ClassLoader cl = null;
		if ( DispatcherFactory.getInstance() == null ) {
			cl = getClass().getClassLoader();
		} else if ( doc != null && doc.getHeader() != null && 
				doc.getHeader().getRPCUser() != null && 
				!doc.getHeader().getRPCUser().endsWith(DispatcherFactory.getInstance().getNavajoConfig().getBetaUser())) {
			cl = DispatcherFactory.getInstance().getNavajoConfig().getClassloader();
		} else {
			cl = DispatcherFactory.getInstance().getNavajoConfig().getBetaClassLoader();
		}

		
		FunctionFactoryInterface fff = FunctionFactoryFactory.getInstance();
		FunctionInterface  f = fff.getInstance(cl, functionName);
		f.inMessage = doc;
		f.currentMessage = parentMsg;
		f.reset();

		for (int i = 0; i < args; i++) {
			Object a = jjtGetChild(i).interpret();
			f.insertOperand(a);
		}

		Object result = f.evaluateWithTypeChecking();

		return result;

	}

}
