/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.rdf.arp.impl;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ARPErrorNumbers;
import com.hp.hpl.jena.rdf.arp.states.Frame;

abstract public class QNameLexer implements Names, ARPErrorNumbers {
    final int bad;
    final int select;
    final Frame frame;
    
    
    public QNameLexer(Frame f, int good, int bad) {
        bad &= ~good;
        this.bad = bad;
        this.select = good|bad;
        this.frame = f;
    }
    
    private int xml(String wanted, int fl) {
        return
             (fl &select)== fl
          && wanted.equals(getLocalName())
          && getUri().equals(xmlns)
          ? fl : 0;
    }
    abstract boolean isInRdfns(Taint taintMe) throws SAXParseException;
    abstract void error(Taint taintMe,int rslt) throws SAXParseException;
    abstract void deprecatedAttribute(Taint me,int rslt) throws SAXParseException;
    

    abstract String getLocalName();
    abstract String getUri();
    abstract String getQName();
    
    private int rdf(Taint taintMe,String wanted, int fl) throws SAXParseException {
        if ((fl &select)== fl
          && wanted.equals(getLocalName())) {
            if (isInRdfns(taintMe))
                return fl;
            if (getQName().toLowerCase().startsWith("rdf:"))
            frame.warning(taintMe,WARN_NOT_RDF_NAMESPACE,getQName() + " is not special. " +
                    "The namespace binding of the RDF namespace is incorrect. It should be <"+rdfns+"> not <"+getUri()+">");
        }
        return 0;
    }
    int lookup(Taint taintMe) throws SAXParseException {
        int rslt = lookupNoMsg(taintMe);
        if ((rslt&bad)!=0) {
            switch (rslt){
            case A_DEPRECATED:
                deprecatedAttribute(taintMe,rslt);
                break;
            case A_BAGID:
                bagIDAttribute(taintMe,rslt);
                break;
                default:
                    error(taintMe,rslt);
            }
        }
        return rslt;
    }
    abstract void bagIDAttribute(Taint taintMe, int rslt) throws SAXParseException;

    private int lookupNoMsg(Taint taintMe) throws SAXParseException {
        char firstChar;
        try {
          firstChar = getLocalName().charAt(0);
        }
        catch (StringIndexOutOfBoundsException e) {
            // Yes this really happens with the DOM one.
            // How disgusting.
            // When xmlns="eg:a"
            // xmlns is the prefix ...
//            System.err.println(getUri());
            if (this.getUri().equals(xmlnsns)) 
                return A_XMLNS;
            throw e;
        }
        switch (firstChar) {
        case 'b': /* base bagID */
            switch (getLocalName().length()) {
            case 4:
                return xml("base",A_XMLBASE);
            case 5:
                return rdf(taintMe,"bagID",A_BAGID);
            }
            break;
        case 'l': /* lang  li */
            switch (getLocalName().length()) {
            case 2:
                return rdf(taintMe,"li",E_LI);
            case 4:
                return xml("lang",A_XMLLANG);
            }
            break;
        case 's': /* space */
            return xml("space",A_XML_OTHER);
        case 'i': /* space */
            return xml("id",A_XML_OTHER);
        case 'I': /* ID */
            return rdf(taintMe,"ID",A_ID);
        case 'n': /* nodeID */
            return rdf(taintMe,"nodeID",A_NODEID);
        case 'a': /* about aboutEach aboutEachPrefix */
            switch (getLocalName().length()) {
            case 5:
                return rdf(taintMe,"about",A_ABOUT);
            case 9:
                return rdf(taintMe,"aboutEach",A_DEPRECATED);
            case 15:
                return rdf(taintMe,"aboutEachPrefix",A_DEPRECATED);
            }
            break;
        case 'r': /* resource */
            return rdf(taintMe,"resource",A_RESOURCE);
        case 'R': /* resource */
            return rdf(taintMe,"RDF",E_RDF);
        case 'd': /* datatype */
            return rdf(taintMe,"datatype",A_DATATYPE);
        case 't': /* type */
            return rdf(taintMe,"type",A_TYPE);
        case 'p': /* parseType */
            return rdf(taintMe,"parseType",A_PARSETYPE);
        case 'D': /* Description */
            return rdf(taintMe,"Description",E_DESCRIPTION);
        }
        return 0;
    }

    
//    static final Set rdfnames = new HashSet();
//    static {
//        rdfnames.add("Description");
//        rdfnames.add("RDF");
//        rdfnames.add("li");
//    }

    static final Set knownRDFProperties = new HashSet();

    static final Set knownRDFTypes = knownRDFProperties;
    static {
        knownRDFTypes.add("Bag");
        knownRDFTypes.add("Seq");
        knownRDFTypes.add("Alt");
        knownRDFTypes.add("List");
        knownRDFTypes.add("XMLLiteral");
        knownRDFTypes.add("Property");
        knownRDFProperties.add("type");
        knownRDFTypes.add("Statement");
        knownRDFProperties.add("subject");
        knownRDFProperties.add("predicate");
        knownRDFProperties.add("object");
        knownRDFProperties.add("value");
        knownRDFProperties.add("first");
        knownRDFProperties.add("rest");
        // not strictly true.
        knownRDFProperties.add("nil");
    }

//    static final Set knownBadRDFNames = new HashSet();
//    static {
//        knownBadRDFNames.add("ID");
//        knownBadRDFNames.add("about");
//        knownBadRDFNames.add("aboutEach");
//        knownBadRDFNames.add("aboutEachPrefix");
//        knownBadRDFNames.add("resource");
//        knownBadRDFNames.add("bagID");
//        knownBadRDFNames.add("parseType");
//        knownBadRDFNames.add("datatype");
//        knownBadRDFNames.add("li");
//        knownBadRDFNames.add("type");
//        knownBadRDFNames.add("Description");
//        knownBadRDFNames.add("nodeID");
//    }


    protected static boolean isMemberProperty(String name) {
        if (name.startsWith("_")) {
            String number = name.substring(1);
            if (number.startsWith("-") || number.startsWith("0"))
                return false;
            try {
                Integer.parseInt(number);
                return true;
            } catch (NumberFormatException e) {
                try {
                    // It might be > Integer.MAX_VALUE
                    new java.math.BigInteger(number);
                    return true;
                } catch (NumberFormatException ee) {
                    return false;
                }
            }
        }
        return false;
    }

    static public boolean isKnownRDFProperty(String name) {
        return knownRDFProperties.contains(name)
          || isMemberProperty(name);
    }
    static public boolean isKnownNonMemberRDFProperty(String name) {
        return knownRDFProperties.contains(name);
    }

    

}


/*
 *  (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
