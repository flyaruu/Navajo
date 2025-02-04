package tipi;

import com.dexels.navajo.tipi.TipiBreakException;
import com.dexels.navajo.tipi.TipiException;
import com.dexels.navajo.tipi.actions.adapters.BaseActions;
import com.dexels.navajo.tipi.components.core.adapter.BaseAdapter;
import com.dexels.navajo.parser.FunctionInterface;
import com.dexels.navajo.parser.TMLExpressionException;

public class TipiRichFunctions extends BaseActions {

private FunctionInterface instantiateFunction(String name) throws TMLExpressionException {
	Class cc;
	try {
		cc = Class.forName(name);

		FunctionInterface fi = (FunctionInterface) cc.newInstance();
		fi.reset();
		return fi;
	} catch (Exception e) {
		throw new TMLExpressionException("Error instantiating function object: "+name, e);
	}
}



}
