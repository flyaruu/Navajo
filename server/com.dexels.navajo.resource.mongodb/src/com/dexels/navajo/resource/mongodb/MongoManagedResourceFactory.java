package com.dexels.navajo.resource.mongodb;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class MongoManagedResourceFactory implements ManagedServiceFactory {

	
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String DATABASE = "database";
	
	private final String name;
	private final String pid;
	private final BundleContext bundleContext;
	
	
	private final Map<String,DB> contextMap = new HashMap<String, DB>();
	private final Map<String,ServiceRegistration<DB>> registryMap = new HashMap<String,ServiceRegistration<DB>>();


	
	private final static Logger logger = LoggerFactory
			.getLogger(MongoManagedResourceFactory.class);
	private ServiceRegistration factoryRegistration;
	
	public MongoManagedResourceFactory(BundleContext bc, String pid, String name) {
		this.bundleContext = bc;
		this.pid = pid;
		this.name = name;
		
        Dictionary<String, Object> managedProperties = new Hashtable<String, Object>();
        managedProperties.put(Constants.SERVICE_PID, this.pid);
        factoryRegistration = bundleContext.registerService(ManagedServiceFactory.class.getName(), this, managedProperties);

	}
	
	@Override
	public void deleted(String pid) {
		logger.info("Shutting down instance: "+pid);
		DB nc = contextMap.get(pid);
		if(nc==null) {
			logger.warn("Strange: Deleting, but already gone.");
			return;
		}
		nc.getMongo().close();
		contextMap.remove(pid);
		ServiceRegistration<DB> reg = registryMap.get(pid);
		reg.unregister();

	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updated(String pid, Dictionary settings)
			throws ConfigurationException {
		logger.info("Configuration received, pid: "+pid);
		try {
			Object source = instantiate(bundleContext, pid,settings);
			ServiceRegistration reg =  bundleContext.registerService(DB.class.getName(),(DB)source, settings);
			registryMap.put(pid, reg);
			contextMap.put(pid, (DB) source);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes" })
	public Object instantiate(BundleContext bc, String pid, Dictionary settings) throws Exception {
		Properties prop = new Properties(); 
		Enumeration en = settings.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			prop.put(key, settings.get(key));
		}
		MongoOptions o = new MongoOptions();
		
		Mongo m = new Mongo((String) settings.get(HOST));
		DB db = m.getDB((String) settings.get(DATABASE));
		return db;
	}

	public void close() {
		for (Entry<String,ServiceRegistration<DB>> s: registryMap.entrySet()) {
			s.getValue().unregister();
		}

		for (Entry<String, DB> s: contextMap.entrySet()) {
			// close individual datasources?
			s.getValue().getMongo().close();
		}

		registryMap.clear();
		contextMap.clear();
		
		factoryRegistration.unregister();
		
	}
}
