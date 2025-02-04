package com.dexels.navajo.script.api;


import java.io.IOException;

import com.dexels.navajo.document.Navajo;

public interface Scheduler {

	
	/**
	 * Return the timeout you want to give to this specifice request.
	 * This only works when you are using the NIO connector (and not the APR connector)
	 * Return 0 if you want to leave the timeout setting on its default value.
	 * @param request
	 * @return
	 */
	public int getTimeout();
	
	/**
	 * Called when the request navajo is complete.
	 * Here you get another opportunity to reject a navajo call by returning false.
	 * You have access to all headers and other parts of the navajo object.
	 * @param input
	 * @param tr 
	 * @return
	 */
	public boolean checkNavajo(Navajo input);

	/**
	 * The runner object is complete, so we can schedule it now.
	 * TODO Huh? Why an IO exception?
	 * @param myRunner
	 * @throws IOException 
	 */
	public void submit(TmlRunnable myRunner, boolean priority) throws IOException;
	

	/**
	 * Run the Runnable synchronously
	 * @param myRunner
	 */
	public void run(TmlRunnable myRunner);
	
	/**
	 * Returns a string describing the status of this scheduling implementation
	 * @return
	 */
	public String getSchedulingStatus();

	
	public void shutdownScheduler();
	
	/**
	 * To schedule 'other' runnable that need to be executed, you can use this pool.
	 * Right now, I use it for the non-blocking HTTP client.
	 * @return
	 */
	public RequestQueue getDefaultQueue();
}
