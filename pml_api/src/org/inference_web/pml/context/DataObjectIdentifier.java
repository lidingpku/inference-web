/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.context;

import org.inference_web.pml.shared.util.*;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class DataObjectIdentifier{
	
	String localName = null;
	String nameSpace = null;
    
  /**
   * Creates a data object identifier from a URI string.
   * @param idURI identifier's URI 
   */
	public DataObjectIdentifier(String idURI) {
		Resource resource = ResourceFactory.createResource(idURI);
		if (resource !=null) {
			this.nameSpace = resource.getNameSpace();
			this.localName = resource.getLocalName();
		}
	}
	
	/**
	 * Creates a data object identifier from a namespace and a local name.
	 * @param nameSpace namespace of the identifier
	 * @param localName local name of the identifier
	 */
	public DataObjectIdentifier(String nameSpace, String localName) {
		this.nameSpace = nameSpace;
		this.localName = localName;
	}
	
	/**
	 * Sets the local name to a new value.
	 * @param newName new local name
	 */
  public void setName (String newName) {
  	localName = newName;
  }
  
  /**
   * Returns identifier's local name
   * @return identifier local name
   */
  public String getName() {
  	return localName;
  }
   
  /**
   * Returns the namespace of the identifier
   * @return identifier's namespace 
   */
  public String getNameSpace() {
  	return nameSpace;
  }

  /**
   * Sets the namespace of the identifier to a new value.
   * @param newNameSpace new namespace
   */
  public void setNameSpace (String newNameSpace) {
  	nameSpace = newNameSpace;
  }

  /**
   * Returns the URI of the identifier.
   * @return identifier's URI
   */
  public String getURIString () {
  	String result = null;
  	if (nameSpace != null || localName != null) {
  		String thisNameSpace = this.nameSpace;
  		String thisName = this.localName;
  		if (thisNameSpace == null) {
  			thisNameSpace = "";
  		}
  		if (thisName == null) {
  			thisName = "";
  		}
  		while (thisNameSpace.endsWith("#")) {
  			int length = thisNameSpace.length();
  			thisNameSpace = thisNameSpace.substring(0, length-1);
  		}
  		this.nameSpace = thisNameSpace+"#";
  		result = thisNameSpace+"#"+thisName;  	
  	}
  	return result;
  }

  /**
   * Answers if identifier equals another identifier.
   * @param other the other identifier
   * @return true if identifiers are equal
   */
  public boolean equals (DataObjectIdentifier other) {
  	boolean result = false;
  	String thisNameSpace = this.nameSpace;
  	String thisName = this.localName;
  	String otherNameSpace = other.getNameSpace();
  	String otherName = other.getName();
  	if (thisNameSpace == null) {
  		thisNameSpace = "";
  	}
  	if (thisName == null) {
  		thisName = "";
  	}
  	if (otherNameSpace == null) {
  		otherNameSpace = "";
  	}
  	if (otherName == null) {
  		otherName = "";
  	}
  	try {	
  		String thisUriStr = thisNameSpace+thisName;
  		String otherUriStr = otherNameSpace+otherName;
  		if (thisUriStr.length()>0 && otherUriStr.length()>0); {
  			if (ToolURI.equalURI(thisUriStr, otherUriStr)) {
  				result = true;
  			}
  		}
  	} catch (Exception e) {
  		e.printStackTrace();
  	}
  	return result;
  }

} /* END OF DataObjectIdentifier */
