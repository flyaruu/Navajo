/*
 * Created on Jul 21, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.dexels.navajo.studio.script.plugin.actions;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.debug.core.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;

import com.dexels.navajo.studio.script.plugin.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StartSocketRunner extends BaseNavajoAction {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
//        try {
//            IProject p = getProject();
//            if (p==null) {
//                NavajoScriptPluginPlugin.getDefault().showError("Check preferences. There is no default Navajo project selected.");
//                return;
//              }
//            NavajoScriptPluginPlugin.getDefault().startSocketRunner(p);
//
//        } catch (Exception e1) {
//            NavajoScriptPluginPlugin.getDefault().log("starting socket runner did not work.",e1);
//        }
        BundleContext bc = NavajoScriptPluginPlugin.getDefault().getBundle().getBundleContext();
        Bundle serverBundle = null;

        bc.addBundleListener(new BundleListener(){

			public void bundleChanged(BundleEvent be) {
				System.err.println("Bundle event: "+be.getType()+" be: "+be.getBundle().getSymbolicName());
				
			}});
        //bc.installBundle("file:///Users/frank/Documents/workspace-osgi/com.dexels.navajo.server");
			Bundle[] b = bc.getBundles();
			for (Bundle bundle : b) {
				System.err.println("b: "+bundle.getSymbolicName()+" > "+bundle.getState());
				if(bundle.getSymbolicName().equals("com.dexels.navajo.server")) {
					serverBundle = bundle;
				}
			}
			navajoserver.Version v;
//			try {
//				v.start(bc);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			System.err.println(">>> "+serverBundle.getState());
			//		try {
////			serverBundle.start();
//		} catch (BundleException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

}
