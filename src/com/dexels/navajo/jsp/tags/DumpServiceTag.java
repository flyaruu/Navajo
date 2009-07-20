package com.dexels.navajo.jsp.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.dexels.navajo.client.ClientException;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.NavajoException;

public class DumpServiceTag extends BaseNavajoTag  {

	private String myService;

	public void setService(String service) {
		myService = service;
	}

	public int doStartTag() throws JspException {
		Navajo n = null;
		if(myService!=null) {
				n = getNavajoContext().getNavajo(myService);
		} else {
			n = getNavajoContext().getNavajo();
			myService = n.getHeader().getRPCName();
		}

		getNavajoContext().getNavajo();
		try {
			if (myService == null) {
				getNavajoContext().getNavajo().write(getPageContext().getOut());
			} else {
				getNavajoContext().getNavajo(myService).write(
						getPageContext().getOut());
			}
			getPageContext().getOut().write("Dumped: " + myService);
		} catch (NavajoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}

}
