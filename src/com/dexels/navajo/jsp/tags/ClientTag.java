package com.dexels.navajo.jsp.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.dexels.navajo.client.context.NavajoContext;

public class ClientTag extends BaseNavajoTag {

	private String server;
	private String username;
	private String password;
	
	public void setServer(String server) {
		this.server = server;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public int doStartTag() throws JspException {
		NavajoContext nc = (NavajoContext) getPageContext().findAttribute("navajoContext");
//		getPageContext().findAttribute("NavajoContext");
		if(server==null) {
			HttpServletRequest rr =  (HttpServletRequest) getPageContext().getRequest();
			StringBuffer sb = new StringBuffer();
			sb.append(rr.getServerName()+":");
			sb.append(rr.getServerPort());
			sb.append(rr.getContextPath());
			server = sb.toString()+"/Postman";
		}
		if(nc==null) {
			throw new JspException("No navajo context found!");
		}
		HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();
		String requestServerName = request.getServerName();
		int requestServerPort = request.getServerPort();
		String requestContextPath = request.getContextPath();

		nc.setupClient(server,username,password,requestServerName,requestServerPort,requestContextPath,"/Postman");
		//		getPageContext().setAttribute("Aap", "Noot");
		return EVAL_BODY_INCLUDE;
	}
}
