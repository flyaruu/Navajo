/**
 * Title:        Navajo<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Arjen Schoneveld<p>
 * Company:      Dexels<p>
 * @author Arjen Schoneveld
 * @version $Id$
 */
package com.dexels.navajo.client;

import java.io.StringWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;

import com.dexels.navajo.document.*;
import com.dexels.navajo.document.jaxpimpl.xml.XMLDocumentUtils;
import com.dexels.navajo.client.html.HTMLutils;

public class NavajoHTMLClient extends NavajoClient {

    // private Vector nodes;
    private Message n;

    public NavajoHTMLClient(String dtdFile) {
        super(dtdFile);
    }

    public NavajoHTMLClient(int protocol) {
        super(protocol);
    }

    // NOTE: readHTMLForm() ASSUMES THAT ALL PROPERTY NAMES MAP UNIQUELY TO FORM FIELD NAMES!!!!!
    public String readHTMLForm(Navajo tbMessage, HttpServletRequest request) throws NavajoException {
        return HTMLutils.readHTMLForm(tbMessage, request);
    }

    private StringBuffer generateErrorMessage(Message msg) {
        StringBuffer result = new StringBuffer();
        Property prop = null;

        prop = msg.getProperty("message");
        result.append("<H1> ERROR </H1><H2>" + prop.getValue() + "</H2>");
        prop = msg.getProperty("code");
        result.append("Code: " + prop.getValue() + "<BR>");
        prop = msg.getProperty("level");
        result.append("Severity level: " + prop.getValue());

        return result;
    }

    public String generateHTMLFromMessage(Navajo tbMessage,
            ArrayList messages,
            ArrayList actions,
            String clientName,
            boolean setter,
            String xslFile) throws NavajoException {

        String result = "";
        StringWriter text = new java.io.StringWriter();

        tbMessage.write(text);

        Message errMsg = tbMessage.getMessage("error");

        if (errMsg != null) {               // Format error message for HTML usage. Replace \n with </BR>
            Property prop = errMsg.getProperty("message");
            String value = prop.getValue();
            StringBuffer newValue = new StringBuffer(value.length());

            for (int i = 0; i < value.length(); i++) {
                if (value.charAt(i) == '\n')
                    newValue.append("</BR>");
                else
                    newValue.append(value.charAt(i));
            }
            prop.setValue(newValue.toString());
        }
        java.io.File xsl = new java.io.File(xslFile);

        try {

            // if aanvraag is filled, then only process the new TML Message, else
            // process all retrieved TML Messages
            if (setter)
                result = XMLDocumentUtils.transform((Document) getDocIn().getMessageBuffer(), xsl);
            else
                result = XMLDocumentUtils.transform((Document) tbMessage.getMessageBuffer(), xsl);

        } catch (TransformerConfigurationException e) {
            System.out.println(e);
            System.out.println("A TransformerConfigurationException occured: " + e);
        } catch (TransformerException e) {
            System.out.println(e);
            System.out.println("A TransformerException occured: " + e);
        } catch (ParserConfigurationException e) {
            System.out.println(e);
            System.out.println("A ParserConfigurationException occured: " + e);
        } catch (java.io.IOException e) {
            System.out.println(e);
            System.out.println("An IOException occured: " + e);
        }
        return result;
    }
}

