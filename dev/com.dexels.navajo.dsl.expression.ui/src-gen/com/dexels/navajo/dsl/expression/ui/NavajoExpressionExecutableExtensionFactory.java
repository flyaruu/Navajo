/*
 * generated by Xtext
 */
package com.dexels.navajo.dsl.expression.ui;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class NavajoExpressionExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return com.dexels.navajo.dsl.expression.ui.internal.NavajoExpressionActivator.getInstance().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return com.dexels.navajo.dsl.expression.ui.internal.NavajoExpressionActivator.getInstance().getInjector("com.dexels.navajo.dsl.expression.NavajoExpression");
	}
	
}
