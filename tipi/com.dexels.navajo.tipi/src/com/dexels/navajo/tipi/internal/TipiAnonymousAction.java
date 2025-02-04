package com.dexels.navajo.tipi.internal;

import java.util.List;

import com.dexels.navajo.tipi.TipiBreakException;
import com.dexels.navajo.tipi.TipiComponent;
import com.dexels.navajo.tipi.TipiException;
import com.dexels.navajo.tipi.TipiExecutable;
import com.dexels.navajo.tipi.TipiSuspendException;
import com.dexels.navajo.tipi.tipixml.XMLElement;

public class TipiAnonymousAction implements TipiExecutable {

	private final Runnable myRunnable;

	public TipiAnonymousAction(Runnable r) {
		myRunnable = r;
	}

	public String getBlockParam(String key) {
		return null;
	}

	public TipiComponent getComponent() {
		return null;
	}

	public TipiEvent getEvent() {
		return null;
	}

	public TipiExecutable getExecutableChild(int index) {
		return null;
	}

	public int getExecutableChildCount() {
		return 0;
	}

	public void performAction(TipiEvent te, TipiExecutable parent, int index)
			throws TipiBreakException, TipiException {
		myRunnable.run();

	}

	public void setEvent(TipiEvent e) {

	}

	public XMLElement store() {
		return null;
	}

	public TipiStackElement getStackElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStackElement(TipiStackElement myStackElement) {
		// TODO Auto-generated method stub

	}

	public void dumpStack(String message) {
		// TODO Auto-generated method stub

	}

	public void setComponent(TipiComponent c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExecutionIndex(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getExecutionIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	@Override
	public void setParent(TipiExecutable tipiAbstractExecutable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TipiExecutable getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TipiExecutable> getExecutables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getExeIndex(TipiExecutable child) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void continueAction(TipiEvent original)
			throws TipiBreakException, TipiException, TipiSuspendException {
		
	}

}
