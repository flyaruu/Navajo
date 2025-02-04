package com.dexels.navajo.tipi.vaadin.components;

import java.util.Iterator;

import com.dexels.navajo.tipi.TipiBreakException;
import com.dexels.navajo.tipi.TipiException;
import com.dexels.navajo.tipi.vaadin.components.base.TipiVaadinComponentImpl;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class TipiAccordion extends TipiVaadinComponentImpl {

	private static final long serialVersionUID = 7459432694853380651L;
	private Accordion accordion;

	@Override
	public Object createContainer() {
		Panel myPanel = new Panel();
		accordion = new Accordion();
//		myPanel.setWidth("300px");
//		myPanel.setHeight("300px");
		myPanel.addComponent(accordion);
		
//		Label l1 = new Label("There are no previously saved actions.");
//		Label l2 = new Label("There are no saved notes.");
//		Label l3 = new Label("There are currently no issues.");
//
//		// Add the components as tabs in the Accordion.
//		accordion.addTab(l1, "Saved actions", null);
//		accordion.addTab(l2, "Notes", null);
//		accordion.addTab(l3, "Issues", null);
		
		accordion.addListener(new SelectedTabChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				try {
					performTipiEvent("onTabChanged", null, true);
				} catch (TipiBreakException e) {
					e.printStackTrace();
				} catch (TipiException e) {
					e.printStackTrace();
				}
				
			}
		});
		return myPanel;
	}

	@Override
	public Object getActualComponent() {
		return accordion;
	}

	@Override
	public void removeFromContainer(Object c) {
		accordion.removeComponent((Component) c);
	}

	
	
	@Override
	protected void setComponentValue(String name, Object object) {
		super.setComponentValue(name, object);
		if(name.equals("selectedindex")) {
			Integer toSelect = (Integer)object;
			Iterator<Component> cc = accordion.getComponentIterator();
			int i = 0;
			while (cc.hasNext()) {
				Component component = cc.next();
				if(i==toSelect.intValue()) {
					accordion.setSelectedTab(component);
				}
				i++;
			}
		}
		if(name.equals("width")) {
			int i = (Integer)object;
			getVaadinContainer().setWidth(""+i+"px");
		}
		if(name.equals("height")) {
			int i = (Integer)object;
			getVaadinContainer().setHeight(""+i+"px");
		}

	}

	@Override
	protected Object getComponentValue(String name) {
		if(name.equals("selectedindex")) {
			Component c =  accordion.getSelectedTab();
			Iterator<Component> cc = accordion.getComponentIterator();
			int i = 0;
			while (cc.hasNext()) {
				Component component = cc.next();
				if(c==component) {
					// found
					return i;
				}
				i++;
			}
			return -1;
		}
		return super.getComponentValue(name);
	}

	@Override
	protected void addToVaadinContainer(ComponentContainer currentContainer, Component component, Object constraints) {
//		super.addToVaadinContainer(currentContainer, component, constraints);
		accordion.addComponent(component);
		accordion.addTab(component, ""+constraints, null);
//		component.setCaption(""+constraints);
	}

	
}
