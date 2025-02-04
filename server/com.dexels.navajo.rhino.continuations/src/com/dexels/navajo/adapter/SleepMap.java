package com.dexels.navajo.adapter;

import com.dexels.navajo.mapping.Mappable;
import com.dexels.navajo.mapping.MappableException;
import com.dexels.navajo.rhino.ContinuationRunnable;
import com.dexels.navajo.server.Access;
import com.dexels.navajo.server.UserException;

// A map that will do a 'scheduled' sleep (i.e. sleep without blocking a thread.)
public class SleepMap implements Mappable {

	private Access access;

	public void load(Access access) throws MappableException, UserException {
		this.access = access;
	}

	public void setSleep(final String method) {
		ContinuationRunnable cr = ContinuationMapUtils.getContinuation(access);
		ContinuationMapUtils.scheduleAndContinue(cr);
		cr.releaseCurrentThread();

	}

	@Override
	public void store() throws MappableException, UserException {

	}

	@Override
	public void kill() {

	}

}
