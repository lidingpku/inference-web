/*
 *  (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
 
 * * $Id: SAX2RDF.java,v 1.20 2008/01/02 12:06:46 andy_seaborne Exp $
   
   AUTHOR:  Jeremy J. Carroll
*/
package com.hp.hpl.jena.rdf.arp;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.hp.hpl.jena.rdf.arp.impl.SAX2RDFImpl;
import com.hp.hpl.jena.rdf.arp.impl.XMLHandler;

/**
 * <p>
 * Allows connecting an arbitrary source of SAX events with ARP.
 * </p>
 * <p>For use with a DOM tree,
 * either use {@link DOM2Model} or
 * see <a href="http://javaalmanac.com/egs/javax.xml.transform.sax/Dom2Sax.html">
 * The Java Developer's Almanac</a> for a discussion of how to transform a DOM
 * into a source of SAX events.
 * </p>
 * 
 * <p>
 * The use pattern is to create and initialize one of these,
 * then set it as the content, lexical and error handler
 * for some source of SAX events (e.g. from a parser).
 * The parser must be configured to use namespaces, and namespace
 * prefixes. This initializing can be done for XMLReaders
 * using {@link #installHandlers}.
 * </p>
 * 
 * <p>
 * To build a Jena model it is better to use {@link SAX2Model}.
 * The documentation here, covers usage both using the subclass
 * {@link SAX2Model}, and not.
 * </p>
 * <p>
 * This class does not support multithreaded SAX sources, nor IO interruption.
 * </p>
 * <p>
 * There is further documentation:
 * <a href="../../../../../../../ARP/standalone.html#not-jena">here</a>
 * and
 * <a href="../../../../../../../ARP/sax.html#sax2rdf">here</a>.
 * </p>
 * @author Jeremy Carroll
 * */
public class SAX2RDF extends SAX2RDFImpl
implements ARPConfig {
	/**
	 * Factory method to create a new SAX2RDF.
	 * Use
     * {@link #getHandlers} or {@link #setHandlersWith} to provide
     * a {@link StatementHandler}, and usually an {@link org.xml.sax.ErrorHandler}
	 *
	 * @param base The retrieval URL, or the base URI to be 
     * used while parsing.
     * @deprecated Use {@link #create(String)}.
     *  @return A new SAX2RDF
	 * @throws MalformedURIException
	 */
	static public SAX2RDF newInstance(String base) throws MalformedURIException { 
        try {
            return  create(base);    
            }
            catch (SAXParseException e) {
                throw new MalformedURIException(e.getMessage());
            }
	}
	/**
	 * Factory method to create a new SAX2RDF.
	 * This is particularly
     * intended for when parsing a non-root element within
     * an XML document. In which case the application
     * needs to find this value in the outer context.
     * Optionally, namespace prefixes can be passed from the
     * outer context using {@link #startPrefixMapping}.
	 * @param base The retrieval URL, or the base URI to be 
     * used while parsing. Use
     * {@link #getHandlers} or {@link #setHandlersWith} to provide
     * a {@link StatementHandler}, and usually an {@link org.xml.sax.ErrorHandler}
	 * @param lang The current value of xml:lang when parsing starts, usually "".
	 * @return A new SAX2RDF
	 * @throws MalformedURIException If base is bad.
     * @deprecated Use {@link #create(String,String)}.
	 */
	static public SAX2RDF newInstance(String base, String lang) throws MalformedURIException { 
		try {
        return  create(base,lang);    
        }
        catch (SAXParseException e) {
            throw new MalformedURIException(e.getMessage());
        }
        
	}    
    /**
     * Factory method to create a new SAX2RDF.
     * Use
     * {@link #getHandlers} or {@link #setHandlersWith} to provide
     * a {@link StatementHandler}, and usually an {@link org.xml.sax.ErrorHandler}
     *
     * @param base The retrieval URL, or the base URI to be 
     * used while parsing.
     *  @return A new SAX2RDF
     * @throws ParseException
     */
    static public SAX2RDF create(String base) throws SAXParseException { 
        return new SAX2RDF(base,""); 
    }
    /**
     * Factory method to create a new SAX2RDF.
     * This is particularly
     * intended for when parsing a non-root element within
     * an XML document. In which case the application
     * needs to find this value in the outer context.
     * Optionally, namespace prefixes can be passed from the
     * outer context using {@link #startPrefixMapping}.
     * @param base The retrieval URL, or the base URI to be 
     * used while parsing. Use
     * {@link #getHandlers} or {@link #setHandlersWith} to provide
     * a {@link StatementHandler}, and usually an {@link org.xml.sax.ErrorHandler}
     * @param lang The current value of xml:lang when parsing starts, usually "".
     * @return A new SAX2RDF
     * @throws ParseException If base or lang is bad.
     */
    static public SAX2RDF create(String base, String lang) throws SAXParseException { 
        return new SAX2RDF(base,lang); 
    }    
    
    
    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     *
     *<p>This is passed to any {@link NamespaceHandler} associated
     *with this parser.
     *It can be called before the initial 
     *<code>startElement</code> event, or other events associated
     *with the elements being processed.
     *When building a Jena Model, with {@link SAX2Model} it is not required to match this
     *with corresponding <code>endPrefixMapping</code> events.
     *Other {@link NamespaceHandler}s may be fussier.
     *When building a Jena Model, the prefix bindings are
     *remembered with the Model, and may be used in some
     *output routines. It is permitted to not call this method
     *for prefixes declared in the outer context, in which case,
     *any output routine will need to use a gensym for such 
     *namespaces.
     *</p>
     * @param prefix The Namespace prefix being declared.
     * @param uri The Namespace URI the prefix is mapped to.
     * 
     */
    public void startPrefixMapping (String prefix, String uri) throws SAXParseException
	 { super.startPrefixMapping(prefix,uri);
    }

    SAX2RDF(String base,  String lang) throws SAXParseException {
    	super(base,lang);
        
    	initParse(base);
    }

    /**
     * @deprecated
     */
    void initParseX(String base, String lang) throws MalformedURIException {
        try {
            initParse(base,lang);
        } catch (SAXParseException e) {
            throw new MalformedURIException(e);
        }
    }
    /**
     * Must call initParse after this.
     * @deprecated
     */
    SAX2RDF(String base,  String lang, boolean dummy){
        super(base,lang);
        
    }
    /**The handlers used for processing ARP events. 
     * Do not use with a {@link SAX2Model}.

	 * @see com.hp.hpl.jena.rdf.arp.ARPConfig#getHandlers()
	 */
	public ARPHandlers getHandlers() {
		return super.getHandlers();
	}
    /**Copys handlers used for processing ARP events. 
     * Do not use with a {@link SAX2Model}.
	
	 * @see com.hp.hpl.jena.rdf.arp.ARPConfig#setHandlersWith(ARPHandlers)
	 */
	public void setHandlersWith(ARPHandlers handlers) {
		super.setHandlersWith(handlers);
	}
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.arp.ARPConfig#getOptions()
	 */
	public ARPOptions getOptions() {
		return super.getOptions();
	}
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.arp.ARPConfig#setOptions(com.hp.hpl.jena.rdf.arp.ARPOptions)
	 */
	public void setOptionsWith(ARPOptions opts) {
		super.setOptionsWith(opts);
		
	}
	/**
	 * Initializes an XMLReader to use the SAX2RDF object
	 * as its handler for all events, and to use namespaces
	 * and namespace prefixes.
	 * @param rdr The XMLReader to initialize.
	 * @param sax2rdf The SAX2RDF instance to use.
	 */
	static public void installHandlers(XMLReader rdr, XMLHandler sax2rdf) 
	throws SAXException 
	{
		rdr.setEntityResolver(sax2rdf);
		rdr.setDTDHandler(sax2rdf);
		rdr.setContentHandler(sax2rdf);
		rdr.setErrorHandler(sax2rdf);
		rdr.setFeature("http://xml.org/sax/features/namespaces", true);
		rdr.setFeature(
			"http://xml.org/sax/features/namespace-prefixes",
			false);
		rdr.setProperty(
			"http://xml.org/sax/properties/lexical-handler",
			sax2rdf);
		rdr.setFeature(
		        "http://apache.org/xml/features/allow-java-encodings",true);
	
	}
}
    
	
