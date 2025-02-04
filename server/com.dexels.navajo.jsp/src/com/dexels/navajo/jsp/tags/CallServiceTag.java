package com.dexels.navajo.jsp.tags;

import java.io.StringWriter;

import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.NavajoException;
import com.dexels.navajo.document.NavajoFactory;
import com.dexels.navajo.script.api.LocalClient;

public class CallServiceTag extends BaseNavajoTag {

	private String myService;
	private String myNavajo;
	private Navajo resultNavajo;
	
	private final static Logger logger = LoggerFactory
			.getLogger(CallServiceTag.class);
	
	@Override
	public int doEndTag() throws JspException {
		if(resultNavajo!=null) {
			getNavajoContext().popNavajo();			
		}
		return EVAL_PAGE;
	}
	
	public void setService(String service) {
		myService = service;
	}

	public void setNavajo(String navajo) {
		myNavajo = navajo;
	}

	
	public int doStartTag() throws JspException {
		assertTest();
		if(myService==null || "".equals(myService)) {
			throw new JspException("Error calling service: No service supplied!");
		}
		System.err.println("Calling service: "+myService);
		try {
			Navajo navajo = null;
			if (myNavajo==null) {
				navajo = NavajoFactory.getInstance().createNavajo();
				navajo.addHeader(NavajoFactory.getInstance().createHeader(navajo, myService, null,null, -1));
			} else {
				navajo = getNavajoContext().getNavajo(myNavajo);
				if(navajo==null) {
					navajo = NavajoFactory.getInstance().createNavajo();
					navajo.addHeader(NavajoFactory.getInstance().createHeader(navajo, myService, null, null, -1));
				}
				navajo.getHeader().setRPCName(myService);
			}
			LocalClient lc = (LocalClient) getPageContext().getServletContext().getAttribute("localClient");
			resultNavajo = lc.call(navajo);
			getNavajoContext().putNavajo(myService, resultNavajo);

			
		} catch (Throwable e) {
			logger.error("Error: ", e);
		}
		resultNavajo = getNavajoContext().getNavajo(myService);
		
		if(resultNavajo==null) {
			throw new JspException("Unknown Navajo service error while calling service: "+myService);
		}
		System.err.println("Service called: "+myService);
		Message error = resultNavajo.getMessage("error");
		if(error!=null) {
			StringWriter sw = new StringWriter();
			try {
				error.write(sw);
			} catch (NavajoException e) {
				logger.error("Error: ", e);
			}
		}
		error = resultNavajo.getMessage("ConditionErrors");
		if(error!=null) { 
			StringWriter sw = new StringWriter();
			try {
				error.write(sw);
			} catch (NavajoException e) {
				logger.error("Error: ", e);
			}
			resultNavajo = null;
		}
		
		if(resultNavajo!=null) {
			getNavajoContext().pushNavajo(resultNavajo);
		}
		return EVAL_BODY_INCLUDE ;
	}
	}
