package com.dexels.navajo.tipi.components.swingimpl;

import java.awt.FlowLayout;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dexels.navajo.tipi.components.core.ThreadActivityListener;
import com.dexels.navajo.tipi.components.core.TipiThread;

public class TipiThreadActivityMonitor extends TipiSwingComponentImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5821858713666617528L;
	private JPanel tja;
	private JLabel queueCountElement;
	private Map<TipiThread, JLabel> threadMap = null;

	@Override
	public Object createContainer() {
		tja = new JPanel();

		tja.setLayout(new FlowLayout());
		queueCountElement = new JLabel();
		tja.add(queueCountElement);
		myContext.addThreadStateListener(new ThreadActivityListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5062528423592250484L;

			public void threadActivity(
					final Map<TipiThread, String> threadStateMap,
					TipiThread tt, String state, final int queueSize) {
				runSyncInEventThread(new Runnable() {
					public void run() {
						if (threadMap == null) {
							initialize(threadStateMap);
						} else {
							update(threadStateMap);
						}
						queueCountElement.setText("queue: " + queueSize);
					}
				});
			}
		});
		return tja;
	}

	protected void update(Map<TipiThread, String> threadStateMap) {
		for (TipiThread t : threadMap.keySet()) {
			JLabel jl = threadMap.get(t);
			tja.add(jl);
			String state = threadStateMap.get(t);
			jl.setText(state);
		}

	}

	protected void initialize(Map<TipiThread, String> threadStateMap) {
		threadMap = new TreeMap<TipiThread, JLabel>();
		// threadStateMap.keySet()
		for (TipiThread t : threadStateMap.keySet()) {
			JLabel jl = new JLabel(t.getName());
			threadMap.put(t, jl);
		}
	}

}
