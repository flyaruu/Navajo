package com.dexels.navajo.server.embedded.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;

import com.dexels.navajo.client.ClientException;
import com.dexels.navajo.client.context.NavajoContext;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.types.Binary;
import com.dexels.navajo.server.embedded.EmbeddedLogbackAppender;
import com.dexels.navajo.server.embedded.EmbeddedServerActivator;
import com.dexels.navajo.server.listener.NavajoContextListener;
import com.dexels.navajo.server.listener.http.TmlHttpServlet;
import com.dexels.navajo.server.listener.nql.NqlServlet;
import com.dexels.navajo.studio.script.plugin.ServerInstance;
import com.dexels.navajo.studio.script.plugin.views.TmlClientView;
import com.dexels.navajo.version.INavajoBundleManager;
import com.dexels.navajo.version.NavajoBundleManager;

public class ServerInstanceImpl implements ServerInstance {
//	private String projectName;
	private int port;
	
	private Server jettyServer;
	private NavajoContext localContext;

	private ServletContextHandler webappContextHandler;

	private final List<LifeCycle.Listener> lifeCycleListeners = new ArrayList<LifeCycle.Listener>();
	private final Appendable outputAppendable;

	public ServerInstanceImpl(Appendable a) {
		outputAppendable = a;
	}

	/* (non-Javadoc)
	 * @see com.dexels.navajo.server.embedded.impl.ServerInstance#getPort()
	 */
	@Override
	public int getPort() {
		return port;
	}
	
	/* (non-Javadoc)
	 * @see com.dexels.navajo.server.embedded.impl.ServerInstance#startServer(java.lang.String)
	 */
	@Override
	public void startServer(final String projectName) {
		try {
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

			
//					dumpBundleStates();
			LifeCycle.Listener lifecycleListener = new LifeCycle.Listener() {
				
				@Override
				public void lifeCycleStopping(LifeCycle l) {
					
				}
				
				@Override
				public void lifeCycleStopped(LifeCycle l) {
//					stopServerAction.setEnabled(false);
//					startServerAction.setEnabled(true);
//					
					NavajoBundleManager.getInstance().uninstallAdapterBundles();
//					NavajoBundleManager.getInstance().loadAdapterPackages(project.getRawLocation().toFile());
					EmbeddedServerActivator.getDefault().deregisterServerInstance(project);

					
				}
				
				@Override
				public void lifeCycleStarting(LifeCycle l) {
					
				}
				
				@Override
				public void lifeCycleStarted(LifeCycle l) {
					port = jettyServer.getConnectors()[0].getPort();
//					stopServerAction.setEnabled(true);
//					startServerAction.setEnabled(false);							

					String server = "localhost:"+port+"/Postman";
					setupClient(server, "plugin","plugin");
					try {
						callPluginServices(project);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
					

				}
				
				@Override
				public void lifeCycleFailure(LifeCycle l, Throwable e) {
					l.removeLifeCycleListener(this);
//					stopServerAction.setEnabled(false);
//					startServerAction.setEnabled(true);
				}
			};
			
			startServer(projectName,lifecycleListener);
//			Enumeration e =jettyServer.getAttributeNames();
//			while (e.hasMoreElements()) {
//				String object = (String) e.nextElement();
//				System.err.println("Attribute: "+object+" value: "+jettyServer.getAttribute(object));
//			}
			port = jettyServer.getConnectors()[0].getPort();
			IWorkbenchWindow window = EmbeddedServerActivator.getDefault().getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			TmlClientView tw;
			tw = (TmlClientView) page.showView("com.dexels.TmlClientView");
			tw.setServerInstance(this);
			//			tw.setServerPort(port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public NavajoContext getNavajoContext() {
		return localContext;
	}
	
	protected void setupClient(String server, String user, String pass) {
		localContext = new NavajoContext();
		localContext.setupClient(server,user, pass);
		EmbeddedServerActivator.getDefault().setCurrentContext(localContext);
		
	}

	protected void callPluginServices(IProject project) throws CoreException {
		try {
			localContext.callService("plugin/InitNavajoBundle");
			Navajo n = localContext.getNavajo("plugin/InitNavajoBundle");
			Binary b = (Binary) n.getProperty("NavajoBundle/FunctionDefinition").getTypedValue();
			Binary adap = (Binary) n.getProperty("NavajoBundle/AdapterDefinition").getTypedValue();
			IFolder iff = project.getFolder("navajoconfig");
			if(!iff.exists()) {
				iff.create(true, true, null);
			}
			IFile ifi = iff.getFile("functions.xml");
			if(!ifi.exists()) {
				ifi.create(b.getDataAsStream(), true, null);
			} else {
				ifi.setContents(b.getDataAsStream(), true, false,null);
				ifi.refreshLocal(1, null);
			}
			ifi = iff.getFile("adapters.xml");
			if(!ifi.exists()) {
				ifi.create(adap.getDataAsStream(), true, null);
			} else {
				ifi.setContents(adap.getDataAsStream(), true, false,null);
				ifi.refreshLocal(1, null);
			}

		} catch (ClientException e) {
			e.printStackTrace();
		} catch(Throwable t) {
			// safe catch
			t.printStackTrace();
			System.err.println("Unexpected behaviour. continuing");
		}
	}

	
	private void startServer(final String projectName, Listener lifecycleListener) throws Exception, InterruptedException {
		System.err.println("Project name: "+projectName);
		IProject navajoProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		INavajoBundleManager instance = NavajoBundleManager.getInstance();
		File file = navajoProject.getLocation().toFile();
		instance.loadAdapterPackages(file);
		
		initializeServer(navajoProject);
		jettyServer.addLifeCycleListener(lifecycleListener);
		
		EmbeddedServerActivator.getDefault().registerServerInstance(navajoProject,this);
		// ordering, allow for listeners to be added befor instantiating the jetty server
		for (LifeCycle.Listener l : lifeCycleListeners) {
			jettyServer.addLifeCycleListener(l);
		}
		startServer();
	}
	
	public void addLifeCycleListener(LifeCycle.Listener lifecycleListener) {
		lifeCycleListeners.add(lifecycleListener);
		if(jettyServer!=null) {
			jettyServer.addLifeCycleListener(lifecycleListener);
		}
	}
	
	public void removeLifeCycleListener(LifeCycle.Listener lifecycleListener) {
		lifeCycleListeners.remove(lifecycleListener);
		if(jettyServer!=null) {
			jettyServer.removeLifeCycleListener(lifecycleListener);
		}
	}
	
	private void startServer() {
		Thread t = new Thread() {

			@Override
			public void run() {
					ClassLoader currentContextLoader = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
					try {
						jettyServer.start();
						jettyServer.join();
//						EmbeddedServerActivator.this.serverStopped(jettyServer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.currentThread().setContextClassLoader(currentContextLoader );
		        }
			
		};
		t.start();
		
	}

	
	/* (non-Javadoc)
	 * @see com.dexels.navajo.server.embedded.impl.ServerInstance#stopServer()
	 */
	@Override
	public void stopServer() {
		System.err.println("Stopping server");
		try {
			if(this.jettyServer==null) {
				return;
			}
			System.err.println("Stopping context seems to hang? Disabled");
//			NavajoContextListener.destroyContext(webappContextHandler.getServletContext());
			System.err.println("Stopping context handler");
			webappContextHandler.stop();
			System.err.println("Context stopped");

			
			this.jettyServer.stop();
			System.err.println("Server stopped");
			this.jettyServer = null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void initializeServer(IProject folder) throws IOException {
		port = findFreePort();
		System.err.println("FREE PORT: "+port);
		String ss = folder.getLocation().toString();
		initializeServer(port, ss);
	}
	
	// .... and if it is not free? 
	private int findFreePort() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();
		server.close();
		return port;
	}

	private void initializeServer(int port, String navajoPath) throws FileNotFoundException {
//		   Logger logger = (Logger) LoggerFactory.getLogger("org.eclipse.jetty.util.log");
		   LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		   lc.reset();
//		   logger.setLevel(Level.INFO);
		   
		    PatternLayout patternLayout = new PatternLayout();
		      patternLayout.setContext(lc);
		      patternLayout.setPattern("%-5level %logger - %msg%n");
		      patternLayout.start();
		   
		   EmbeddedLogbackAppender embeddedLogbackAppender = new EmbeddedLogbackAppender(this.outputAppendable);
		   // Both of these should be set BEFORE setOutputStream is called.
		   // Otherwise: Nasty silent NPE
		   embeddedLogbackAppender.setContext(lc);
		   embeddedLogbackAppender.setLayout(patternLayout);
		   embeddedLogbackAppender.start();
		   final Logger LOG =(Logger)  LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		   System.err.println("Logger ");
		   LOG.addAppender(embeddedLogbackAppender);
		      
			
//	  
//			PatternLayoutEncoder encoder = new PatternLayoutEncoder(); 
//			encoder.setContext(lc);
//			encoder.setPattern("%marker %level %msg%n");
//			encoder.start();
//		   embeddedLogbackAppender.setContext(lc);
			
//		   FileOutputStream fo = new FileOutputStream("/Users/frank/Desktop/log.txt");
//		   fileAppender.setOutputStream(fo);
		jettyServer = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		jettyServer.addConnector(connector);
		webappContextHandler = new ServletContextHandler(ServletContextHandler.NO_SECURITY | ServletContextHandler.NO_SESSIONS);
		webappContextHandler.setContextPath("/");
		NavajoContextListener.initializeContext(webappContextHandler.getServletContext(),navajoPath);
		webappContextHandler.addServlet(new ServletHolder(new TmlHttpServlet()),"/Postman");
		webappContextHandler.addServlet(new ServletHolder(new NqlServlet()),"/Nql");
//		webappContextHandler.addServlet(new ServletHolder(new NqlServlet()),"/Nssql");
		jettyServer.setHandler(webappContextHandler);

		}

	@Override
	public Navajo callService(Navajo n, String service) throws ClientException {
		 localContext.callService(service, n);
		 return localContext.getNavajo(service);
	}

}
