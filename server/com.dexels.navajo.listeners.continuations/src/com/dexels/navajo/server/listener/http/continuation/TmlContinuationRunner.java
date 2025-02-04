package com.dexels.navajo.server.listener.http.continuation;

import java.io.IOException;

import javax.servlet.ServletException;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.NavajoException;
import com.dexels.navajo.script.api.AsyncRequest;
import com.dexels.navajo.script.api.ClientInfo;
import com.dexels.navajo.script.api.FatalException;
import com.dexels.navajo.script.api.LocalClient;
import com.dexels.navajo.script.api.NavajoDoneException;
import com.dexels.navajo.script.api.TmlScheduler;
import com.dexels.navajo.server.listener.http.standard.TmlStandardRunner;

public class TmlContinuationRunner extends TmlStandardRunner {

	private final Continuation continuation;
	private Navajo outDoc;
	

	private final static Logger logger = LoggerFactory
			.getLogger(TmlContinuationRunner.class);

	public TmlContinuationRunner(AsyncRequest request, LocalClient lc) {
		super(request,lc);
		continuation = ContinuationSupport.getContinuation(request.getHttpRequest());
		continuation.setTimeout(Long.MAX_VALUE);
	}
	
	@Override
	public void abort(String reason) {
		super.abort(reason);
		try {
			logger.warn("Abort: "+reason+" generating outdoc and resuming");
			outDoc = getLocalClient().generateAbortMessage(reason);
			resumeContinuation();
		} catch (FatalException e) {
			logger.error("Error: ", e);
		}
	}

	

	@Override
	public void endTransaction() throws IOException {
		try {
			// writeOutput moved from execute to here, as the scheduler thread shouldn't touch the response output stream
			writeOutput(getInputNavajo(), outDoc);
			TmlScheduler ts = getTmlScheduler();
			String schedulingStatus = null;
			if(ts!=null) {
				schedulingStatus = ts.getSchedulingStatus();
			}
			getRequest().writeOutput(getInputNavajo(), outDoc, scheduledAt, startedAt, schedulingStatus);
		} catch (NavajoException e) {
			e.printStackTrace();
		}
		if ( getRequestQueue() != null ) { // Check whether there is a request queue available.
			getRequestQueue().finished();
		}
		super.endTransaction();
	}

	
	  /**
	   * Handle a request.
	   *
	   * @param request
	   * @param response
	   * @throws IOException
	   * @throws ServletException
	   */
	  private final void execute() throws IOException, ServletException {
		  startedAt = System.currentTimeMillis();
		  setCommitted(true);
//		  BufferedReader r = null;
		  try {
			  Navajo in = getInputNavajo();
			  in.getHeader().setHeaderAttribute("useComet", "true");
			  

				  boolean continuationFound = false;
				  try {
					  
				      int queueSize = getRequestQueue().getQueueSize();
				      String queueId = getRequestQueue().getId();
				      
					  ClientInfo clientInfo = getRequest().createClientInfo(scheduledAt, startedAt, queueSize, queueId);
					  outDoc = getLocalClient().handleInternal(in, getRequest().getCert(), clientInfo);
//					  outDoc = DispatcherFactory.getInstance().removeInternalMessages(DispatcherFactory.getInstance().handle(in, this,getRequest().getCert(), clientInfo));
					  // Do do: Support async services in a more elegant way.
				  } catch (NavajoDoneException e) {
					  // temp catch, to be able to pre
					  continuationFound = true;
					  //.println("Navajo done in service runner. Thread disconnected...");
					  throw(e);
				  }
				  finally {
					  if(!continuationFound) {
						  resumeContinuation();
					  }
				  }
//			  }
		  }
		  catch (NavajoDoneException e) {
			  throw(e);
		  }
		  catch (Throwable e) {
			  //e.printStackTrace(System.err);
			  if ( e instanceof  FatalException ) {
				  FatalException fe = (FatalException) e;
				  if ( fe.getMessage().equals("500.13")) {
					  // Server too busy.
					  continuation.undispatch();
					  throw new ServletException("500.13");
				  }
			  }
			  throw new ServletException(e);
		  } 
	  }



	private void resumeContinuation() {
		continuation.resume();
		
		
	}



	public void suspendContinuation() {
		continuation.suspend();
	}
	


	@Override
	public void run() {
		try {
			execute();
		} catch(NavajoDoneException e) {
			System.err.println("NavajoDoneException caught. This thread fired a continuation. Another thread will finish it in the future.");
		} catch (Exception e) {
			getRequest().fail(e);
		}
	}

}
