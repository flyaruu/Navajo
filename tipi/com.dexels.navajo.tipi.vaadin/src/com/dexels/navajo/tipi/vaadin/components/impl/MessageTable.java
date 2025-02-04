package com.dexels.navajo.tipi.vaadin.components.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dexels.navajo.document.types.ClockTime;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class MessageTable extends Table {

	private static final long serialVersionUID = 9005864145330315869L;
	private static final SimpleDateFormat dateFormat3 = new SimpleDateFormat( com.dexels.navajo.document.Property.DATE_FORMAT3 );
	
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory
			.getLogger(MessageTable.class);
	//
//	public MessageTable() {
//	}

//	public MessageTable(String caption) {
//		super(caption);
//		setTableFieldFactory(new TipiFieldFactory());
//		setEditable(true);
//	}

//	public MessageTable(String caption, Container dataSource) {
//		super(caption, dataSource);
//		setTableFieldFactory(new TipiFieldFactory());
//		setEditable(true);
//	}
	
	@Override
	  protected String formatPropertyValue(Object rowId, Object colId,
              Property property) {
          Object v = property.getValue();
//          logger.warn("Formatting: "+rowId+" col: "+colId+" prop: "+property+" value: "+v);
          if (v instanceof Date) {
              Date dateValue = (Date) v;
              return dateFormat3.format(dateValue);
          }
          if(v instanceof ClockTime) {
        	  ClockTime ct = (ClockTime)v;
        	  return ct.toShortString();
          }
          if(v instanceof Number) {
        	  return ""+v.toString();
          }
          if(v instanceof Boolean) {
        	  boolean b = (Boolean)v;
        	  return b?"ja":"nee";
          }
          return super.formatPropertyValue(rowId, colId, property);
      }

	
}
