package com.dexels.navajo.document.saximpl.qdxml;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quick and Dirty xml parser. This parser is, like the SAX parser, an event
 * based parser, but with much less functionality.
 */
public class QDParser {
    
    public static final int PUSHBACK_SIZE = 2000;
    
	private final static Logger logger = LoggerFactory
			.getLogger(QDParser.class);
	
    private static int popMode(Stack<Integer> st) {
        if (!st.empty())
            return st.pop().intValue();
        else
            return PRE;
    }

    private final static int TEXT = 1, ENTITY = 2, OPEN_TAG = 3, CLOSE_TAG = 4, START_TAG = 5, ATTRIBUTE_LVALUE = 6, ATTRIBUTE_EQUAL = 9,
            ATTRIBUTE_RVALUE = 10, QUOTE = 7, IN_TAG = 8, SINGLE_TAG = 12, COMMENT = 13, DONE = 11, DOCTYPE = 14, PRE = 15, CDATA = 16;

    public static void parse(DocHandler doc, Reader re) throws Exception {
        PushbackReader r = new PushbackReader(new BufferedReader(re), PUSHBACK_SIZE);
        StringBuffer attributeBuffer = new StringBuffer(50);
        //Stack st = new Stack();
        int depth = 0;
        int mode = PRE;
        int c = 0;
        int quotec = '"';
        depth = 0;
        StringBuffer sb = new StringBuffer();
        StringBuffer etag = new StringBuffer();
        String tagName = null;
        String lvalue = null;
        String rvalue = null;
        Hashtable<String,String> attrs = null;
        Stack<Integer> st = new Stack<Integer>();
        doc.startDocument();
        int line = 1, col = 0;
        boolean eol = false;
        while ((c = r.read()) != -1) {

            // We need to map \r, \r\n, and \n to \n
            // See XML spec section 2.11
            if (c == '\n' && eol) {
                eol = false;
                continue;
            } else if (eol) {
                eol = false;
            } else if (c == '\n') {
                line++;
                col = 0;
            } else if (c == '\r') {
                eol = true;
                c = '\n';
                line++;
                col = 0;
            } else {
                col++;
            }

            if (mode == DONE) {
                doc.endDocument();
                return;

                // We are between tags collecting text.
            } else if (mode == TEXT) {
                if (c == '<') {
                    st.push(Integer.valueOf(mode));
                    mode = START_TAG;
                    // if (sb.length() > 0) {
                    // doc.text(sb.toString());
                    // sb.setLength(0);
                    // }
                } else if (c == '&') {
                    st.push(Integer.valueOf(mode));
                    mode = ENTITY;
                    etag.setLength(0);
                } else
                    sb.append((char) c);

                // we are processing a closing tag: e.g. </foo>
            } else if (mode == CLOSE_TAG) {
                if (c == '>') {
                    mode = popMode(st);
                    tagName = sb.toString();
                    sb.setLength(0);
                    depth--;
                    if (depth == 0)
                        mode = DONE;
                    doc.endElement(tagName);
                } else {
                    sb.append((char) c);
                }

                // we are processing CDATA
            } else if (mode == CDATA) {
                if (c == '>' && sb.toString().endsWith("]]")) {
                    sb.setLength(sb.length() - 2);
                    logger.info("Warning: ignoring cdata!");
                    // doc.text(sb.toString());
                    sb.setLength(0);
                    mode = popMode(st);
                } else
                    sb.append((char) c);

                // we are processing a comment. We are inside
                // the <!-- .... --> looking for the -->.
            } else if (mode == COMMENT) {
                if (c == '>' && sb.toString().endsWith("--")) {
                    sb.setLength(0);
                    mode = popMode(st);
                } else
                    sb.append((char) c);

                // We are outside the root tag element
            } else if (mode == PRE) {
                if (c == '<') {
                    // mode = TEXT;
                    // st.push(new Integer(mode));
                    mode = START_TAG;
                }

                // We are inside one of these <? ... ?>
                // or one of these <!DOCTYPE ... >
            } else if (mode == DOCTYPE) {
                if (c == '>') {
                    mode = popMode(st);
                    if (mode == TEXT)
                        mode = PRE;
                }

                // we have just seen a < and
                // are wondering what we are looking at
                // <foo>, </foo>, <!-- ... --->, etc.
            } else if (mode == START_TAG) {
                mode = popMode(st);
                if (c == '/') {
                    st.push(Integer.valueOf(mode));
                    mode = CLOSE_TAG;
                } else if (c == '?') {
                    mode = DOCTYPE;
                } else {
                    st.push(Integer.valueOf(mode));
                    mode = OPEN_TAG;
                    tagName = null;
                    attrs = new Hashtable<String,String>();
                    sb.append((char) c);
                }

                // we are processing an entity, e.g. &lt;, &#187;, etc.
            } else if (mode == ENTITY) {
                if (c == ';') {
                    mode = popMode(st);
                    String cent = etag.toString();
                    etag.setLength(0);
                    if (cent.equals("lt"))
                        sb.append('<');
                    else if (cent.equals("gt"))
                        sb.append('>');
                    else if (cent.equals("amp"))
                        sb.append('&');
                    else if (cent.equals("quot"))
                        sb.append('"');
                    else if (cent.equals("apos"))
                        sb.append('\'');
                    // Could parse hex entities if we wanted to
                    // else if(cent.startsWith("#x"))
                    // sb.append((char)Integer.parseInt(cent.substring(2),16));
                    else if (cent.startsWith("#"))
                        sb.append((char) Integer.parseInt(cent.substring(1)));
                    // Insert custom entity definitions here
                    else
                        exc("Unknown entity: &" + cent + ";", line, col);
                } else {
                    etag.append((char) c);
                }

                // we have just seen something like this:
                // <foo a="b"/
                // and are looking for the final >.
            } else if (mode == SINGLE_TAG) {
                if (tagName == null)
                    tagName = sb.toString();
                if (c != '>') {
                    exc("Expected > for tag: <" + tagName + "/>. Got c='" + c + "'", line, col);
                }
                doc.startElement(tagName, attrs);
                doc.endElement(tagName);
                if (depth == 0) {
                    doc.endDocument();
                    return;
                }
                sb.setLength(0);
                attrs = new Hashtable<String,String>();
                tagName = null;
                mode = popMode(st);

                // we are processing something
                // like this <foo ... >. It could
                // still be a <!-- ... --> or something.
            } else if (mode == OPEN_TAG) {
                if (c == '>') {
                    if (tagName == null)
                        tagName = sb.toString();
                    sb.setLength(0);
                    depth++;
                    doc.startElement(tagName, attrs);
                    tagName = null;
                    attrs = new Hashtable<String,String>();
                    mode = popMode(st);
                    skipWhitespace(r);
                    char ccc = nextChar(r);
                    if (ccc != '<') {
                        doc.text(r);
                        r.read();
                        popMode(st);
                        // / mode = CLOSE_TAG;
                    }
                } else if (c == '/') {
                    mode = SINGLE_TAG;
                } else if (c == '-' && sb.toString().equals("!-")) {
                    mode = COMMENT;
                } else if (c == '[' && sb.toString().equals("![CDATA")) {
                    mode = CDATA;
                    sb.setLength(0);
                } else if (c == 'E' && sb.toString().equals("!DOCTYP")) {
                    sb.setLength(0);
                    mode = DOCTYPE;
                } else if (Character.isWhitespace((char) c)) {
                    tagName = sb.toString();
                    sb.setLength(0);
                    mode = IN_TAG;
                } else {
                    sb.append((char) c);
                }

                // We are processing the quoted right-hand side
                // of an element's attribute.
            } else if (mode == QUOTE) {
                if (c == quotec) {
                    rvalue = sb.toString();
                    sb.setLength(0);
                    if (attrs==null) {
						throw new NullPointerException("The XML Compiler is in deep st#@$t");
					} else {
	                    attrs.put(lvalue, rvalue);
					}
                    mode = IN_TAG;
                    // See section the XML spec, section 3.3.3
                    // on normalization processing.

                    // HMMMMMMMMM DOES THIS BREAK BASE64?
                    // } else if (" \r\n\u0009".indexOf(c) >= 0) {
                    // sb.append(' ');
                } else if (c == '&') {
                    st.push(Integer.valueOf(mode));
                    mode = ENTITY;
                    etag.setLength(0);
                } else {
                    sb.append((char) c);
                }

            } else if (mode == ATTRIBUTE_RVALUE) {
                if (c == '"' || c == '\'') {
                    quotec = c;
                    rvalue = doc.quoteStarted(quotec, r, lvalue, tagName,attributeBuffer);
                    if (rvalue != null) {
                    	if (attrs==null) {
							throw new NullPointerException("Serious parsing problem!");
						} else {
	                        attrs.put(lvalue, rvalue);
						}
                    }
                    mode = IN_TAG;

                    // mode = QUOTE;
                } else if (Character.isWhitespace((char) c)) {
                    ;
                } else {
                    exc("Error in attribute processing", line, col);
                }

            } else if (mode == ATTRIBUTE_LVALUE) {
                if (Character.isWhitespace((char) c)) {
                    lvalue = sb.toString();
                    sb.setLength(0);
                    mode = ATTRIBUTE_EQUAL;
                } else if (c == '=') {
                    lvalue = sb.toString();
                    sb.setLength(0);
                    mode = ATTRIBUTE_RVALUE;
                } else {
                    sb.append((char) c);
                }

            } else if (mode == ATTRIBUTE_EQUAL) {
                if (c == '=') {
                    mode = ATTRIBUTE_RVALUE;
                } else if (Character.isWhitespace((char) c)) {
                    ;
                } else {
                    exc("Error in attribute processing, got c='" + c + "'", line, col);
                }

            } else if (mode == IN_TAG) {
                if (c == '>') {
                    mode = popMode(st);
                    doc.startElement(tagName, attrs);
                    depth++;
                    tagName = null;
                    attrs = new Hashtable<String,String>();
                    skipWhitespace(r);
//                    logger.info("Char: " + (char) nextChar(r));
                    if (nextChar(r) != '<') {
                        doc.text(r);
//                        r.unread('<');
                    } 
                    // char cc = (char)r.read();
                    // /Lookahead(r);
                    // mode = TEXT;

                } else if (c == '/') {
                    mode = SINGLE_TAG;
                } else if (Character.isWhitespace((char) c)) {
                    ;
                } else {
                    mode = ATTRIBUTE_LVALUE;
                    sb.append((char) c);
                }
            }
        }
        if (mode == DONE)
            doc.endDocument();
        else
            exc("missing end tag", line, col);
    }

//    private static String displayMode(int mode) {
//        switch (mode) {
//        case 1:
//            return "TEXT";
//        case 2:
//            return "ENTITY";
//        case 3:
//            return "OPEN_TAG";
//        case 4:
//            return "CLOSE_TAG";
//        case 5:
//            return "START_TAG";
//        case 6:
//            return "ATTRIBUTE_LVALUE";
//        case 7:
//            return "QUOTE";
//        case 8:
//            return "IN_TAG";
//        case 9:
//            return "ATTRIBUTE_EQUAL";
//        case 10:
//            return "ATTRIBUTE_RVALUE";
//        case 11:
//            return "DONE";
//        case 12:
//            return "SINGLE_TAG";
//        case 13:
//            return "COMMENT";
//        case 14:
//            return "DOCTYPE";
//        case 15:
//            return "PRE";
//        case 16:
//            return "CDATA";
//
//        default:
//            break;
//        }
//        return null;
//    }
//
//    private static void showLookahead(PushbackReader r) throws IOException {
//        char[] c = new char[10];
//        r.read(c);
//        System.err.print("LOOKAHEAD: >");
//        for (int i = 0; i < c.length; i++) {
//            System.err.print(c[i]);
//        }
//        logger.info("<\n");
//        r.unread(c);
//    }

    private static char nextChar(PushbackReader r) throws IOException {
        int c = r.read();
        r.unread(c);
        return (char) c;
    }

    private static void skipWhitespace(PushbackReader r) throws IOException {
        while (Character.isWhitespace(nextChar(r))) {
            r.read();
        }
    }

    private static void exc(String s, int line, int col) throws Exception {
        throw new Exception(s + " near line " + line + ", column " + col);
    }
 
}
