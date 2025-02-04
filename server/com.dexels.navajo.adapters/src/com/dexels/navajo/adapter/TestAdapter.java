package com.dexels.navajo.adapter;

import com.dexels.navajo.mapping.Mappable;
import com.dexels.navajo.mapping.MappableException;
import com.dexels.navajo.server.Access;
import com.dexels.navajo.server.UserException;

/**
 * Title:        Navajo Product Project
 * Description:  This is the official source for the Navajo server
 * Copyright:    Copyright (c) 2002
 * Company:      Dexels BV
 * @author Arjen Schoneveld
 * @version 1.0
 */

public class TestAdapter implements Mappable {

    public String empty = null;
    public TestAdapter [] testAdapters;
    public TestAdapter single;

    public TestAdapter() {}

    public void load(Access access) throws MappableException, UserException {
       testAdapters = new TestAdapter[5];
       for (int i = 0; i < 5; i++) {
        testAdapters[i] = new TestAdapter();
        testAdapters[i].empty = "adapter"+i;
       }
       single = new TestAdapter();
       single.empty = "I am single";
    }

    public void store() throws MappableException, UserException {
    	System.err.println("TestAdapter store() called!!");
    }

    public void kill() {
    	System.err.println("TestAdapter kill() called!!");
    }

    public TestAdapter getSingle() {
      return this.single;
    }

    public void setSingle(TestAdapter s) {
      this.single = s;
    }


    public void setTestAdapters(TestAdapter [] all) {
      this.testAdapters = all;
    }

    public TestAdapter [] getTestAdapters() {
      return this.testAdapters;
    }

    public void setEmpty(String s) {
      empty = s;
    }

    public String getEmpty() {
        return empty;
    }
}
