/*
 * SelectorListImpl.java
 *
 * Steady State CSS2 Parser
 *
 * Copyright (C) 1999, 2002 Steady State Software Ltd.  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * To contact the authors of the library, write to Steady State Software Ltd.,
 * 49 Littleworth, Wing, Buckinghamshire, LU7 0JX, England
 *
 * http://www.steadystate.com/css/
 * mailto:css@steadystate.co.uk
 *
 * $Id$
 */

package com.steadystate.css.parser;

import java.io.Serializable;
import java.util.*;
import org.w3c.css.sac.*;

public class SelectorListImpl implements SelectorList, Serializable {

    private Vector _selectors = new Vector(10, 10);

    public int getLength() {
        return _selectors.size();
    }

    public Selector item(int index) {
        return (Selector) _selectors.elementAt(index);
    }

    public void add(Selector sel) {
        _selectors.addElement(sel);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int len = getLength();
        for (int i = 0; i < len; i++) {
            sb.append(item(i).toString());
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
