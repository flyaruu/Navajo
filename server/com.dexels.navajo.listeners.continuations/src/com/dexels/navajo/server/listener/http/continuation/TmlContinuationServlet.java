package com.dexels.navajo.server.listener.http.continuation;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.script.api.AsyncRequest;
import com.dexels.navajo.script.api.LocalClient;
import com.dexels.navajo.script.api.SchedulableServlet;
import com.dexels.navajo.script.api.TmlRunnable;
import com.dexels.navajo.script.api.TmlScheduler;
import com.dexels.navajo.server.listener.http.impl.BaseRequestImpl;



public class TmlContinuationServlet extends HttpServlet implements SchedulableServlet {

	private static final long serialVersionUID = -8645365233991777113L;

	

	private final static Logger logger = LoggerFactory
			.getLogger(TmlContinuationServlet.class);
	
	public static final String COMPRESS_GZIP = "gzip";
	public static final String COMPRESS_JZLIB = "jzlib";
	public static final String COMPRESS_NONE = "";

//	private boolean jmxRegistered = false;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		performInitialization();
		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.err.println("In ContinuationServlet init()");
		// TOO EARLY check performInitialization
		//		JMXHelper.registerMXBean(this, JMXHelper.NAVAJO_DOMAIN, "TmlCometServlet");
	}


	@SuppressWarnings("unused")
	@Override
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		performInitialization();
		TmlContinuationRunner tmlRunner = (TmlContinuationRunner) req.getAttribute("tmlRunner");
		if(tmlRunner!=null) {
//			tmlRunner.setResponse(resp);
			tmlRunner.endTransaction();
			return;
		}
		
		final LocalClient lc = (LocalClient) req.getServletContext().getAttribute("localClient");
		if(lc==null) {
			resp.sendError(500, "No local client registered in servlet context");
			return;
		}
		boolean precheck = getTmlScheduler().preCheckRequest(req);
		if (!precheck) {
			req.getInputStream().close();
			resp.getOutputStream().close();
			return;
		}
		
		Object certObject = req
				.getAttribute("javax.servlet.request.X509Certificate");
		String recvEncoding = req.getHeader("Content-Encoding");
		String sendEncoding = req.getHeader("Accept-Encoding");
		AsyncRequest request = new BaseRequestImpl(lc,req, resp, sendEncoding, recvEncoding, certObject){

			@Override
			public TmlRunnable instantiateRunnable() {
				String remoteAddress = req.getRemoteAddr();
				TmlContinuationRunner tr = new TmlContinuationRunner(this,lc);
				req.setAttribute("tmlRunner", tr);
			
				tr.suspendContinuation();
				return tr;
			}};

		boolean check = getTmlScheduler().checkNavajo(request.getInputDocument());
		if (!check) {
			resp.getOutputStream().close();
			return;
		}

		
		getTmlScheduler().submit(request.instantiateRunnable(),false);
	}

	private void performInitialization() {
		// Not nice. Not thread safe on hot startup. TODO Move it someplace else. 
		// I can not do this in the servlet init because it requires the Navajo context to be up, and
		// that is not really the case.
		logger.warn("WARNING: Omitting JMX registry of comet servlet!");
//		if(!jmxRegistered) {
//			JMXHelper.registerMXBean(this, JMXHelper.NAVAJO_DOMAIN, "TmlCometServlet");
//			jmxRegistered = true;
//		}
	}

//	
//	protected final Navajo parseInputNavajo(HttpServletRequest request) throws IOException, UnsupportedEncodingException {
//		InputStream is = request.getInputStream();
//		// TODO This is a bug: Accept encoding is what encoding the client will accept in the
//		// response. Here it is used as the Content Encoding of the request.
//		// Not a problem as long as it is the same, but needs to be fixed.
//		// http://spiritus.dexels.nl:6070/browse/NAVAJO-29
//		String sendEncoding = request.getHeader("Accept-Encoding");
//
//		BufferedReader r;
//		  Navajo in = null;
//		if (sendEncoding != null && sendEncoding.equals(COMPRESS_JZLIB)) {
//			r = new BufferedReader(new java.io.InputStreamReader(
//					new ZInputStream(is)));
//		} else if (sendEncoding != null && sendEncoding.equals(COMPRESS_GZIP)) {
//			r = new BufferedReader(new java.io.InputStreamReader(
//					new java.util.zip.GZIPInputStream(is), "UTF-8"));
//		} else {
//			r = new BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
//		}
//		in = NavajoFactory.getInstance().createNavajo(r);
//
//		  if (in == null) {
//			  throw new IOException("Invalid request.");
//		  }
//
//		  Header header = in.getHeader();
//		  if (header == null) {
//			  throw new IOException("Empty Navajo header.");
//		  }
//
//		  is.close();
//
//		return in;
//	}
//	
	@Override
	public TmlScheduler getTmlScheduler() {
		return (TmlScheduler) getServletContext().getAttribute("tmlScheduler");
	}

//	@Override
//	public void init() throws ServletException {
//		super.init();
//		setTmlScheduler(SchedulerTools.createScheduler(this));
//
//	}




	
}
