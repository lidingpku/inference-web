/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.PMLP;


public class IWDocumentFragmentByRowColImpl  extends PMLObjectImpl implements IWDocumentFragmentByRowCol, Serializable {

	public IWDocumentFragmentByRowColImpl() {
	}
	
  public IWDocumentFragmentByRowColImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWDocumentFragmentByRowColImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }
	
	public IWDocumentFragmentByRowColImpl(DataObject desc) {
		super(desc);
	}
	public String getHasFromCol(){
		return getDataPropertyValue(PMLP.hasFromCol_lname);
	}
	public void setHasFromCol(String col) {
		setDataPropertyValue(PMLP.hasFromCol_lname, col);
	}
	public String getHasToCol(){
		return getDataPropertyValue(PMLP.hasToCol_lname);
	}
	public void setHasToCol(String col) {
		setDataPropertyValue(PMLP.hasToCol_lname, col);
	}
    
	public String getHasFromRow(){
		return getDataPropertyValue(PMLP.hasFromRow_lname);
	}
	public void setHasFromRow(String row) {
		setDataPropertyValue(PMLP.hasFromRow_lname, row);
	}
	public String getHasToRow(){
		return getDataPropertyValue(PMLP.hasToRow_lname);
	}
	public void setHasToRow(String row) {
		setDataPropertyValue(PMLP.hasToRow_lname, row);
	}
	
	public IWDocument getHasDocument() {
		IWDocument result = null;
		Object value = getObjectPropertyValue(PMLP.hasDocument_lname);
		if (value != null) result = (IWDocument)PMLObjectManager.getPMLObject(value);
		return result;
	}
	public void setHasDocument (IWDocument newDoc) {
		setObjectPropertyValue(PMLP.hasDocument_lname, newDoc);
	}
	public void setHasDocument (String docURI) {
		setObjectPropertyValue(PMLP.hasDocument_lname, docURI);
	}

	private String namePropName = null; // v1 v2 differ
  private void setNamePropertyName() {
  	if (properties != null) {
  		boolean found = false;
  		Iterator propNames = properties.keySet().iterator();
  		while (propNames.hasNext() && !found) {
  			String propName = (String)propNames.next();
  			if (propName.indexOf("#name")>=0 || propName.indexOf("#hasName")>=0 ) {
  				namePropName = propName;
  				found = true;
  			}
  		}
  	} else {
  		System.out.println("IWDocumentFragmentByRowCol: properties map null");
  	}
  }

	public String getNamePropertyName () {
		if (namePropName == null) {
			setNamePropertyName();
		}
		return namePropName;
	}
	
	public String getHasName () {
		String result = null;
		Object value = getProperty(getNamePropertyName());
		if (value != null) result = (String)value;
		return result;
	}
	
	public void setHasName (String newName) {
		setProperty(getNamePropertyName(),newName);
	}


	public String getHasCreationDateTime () {
		String result = null;
		Object value = getDataPropertyValue(PMLP.hasCreationDateTime_lname);
		if (value != null) result = (String)value;
		return result;
	}
	public void setHasCreationDateTime (String newDateTime) {
		setDataPropertyValue(PMLP.hasCreationDateTime_lname, newDateTime);
	}
	
	public List getHasAuthorList () {
		return getListObjectPropertyValue(PMLP.hasAuthorList_lname);
	}
	
	public void setHasAuthorList (List newAuthors) {
		setListObjectPropertyValue(PMLP.hasAuthorList_lname, newAuthors);
	}
	
  public void addHasAuthor(IWAgent author){
  	addMultipleValueObjectPropertyValue(PMLP.hasAuthorList_lname, author);
  }
  public void addHasAuthor(String authorURI){
  	addMultipleValueObjectPropertyValue(PMLP.hasAuthorList_lname, authorURI);
  }

	public List getHasDescription () {
		return getMultipleValueObjectPropertyValue(PMLP.hasDescription_lname);
	}
	
	public void setHasDescription (List newDescriptions) {
		setMultipleValueObjectPropertyValue(PMLP.hasDescription_lname, newDescriptions);
	}
	
	public void addHasDescription(IWInformation newDescription) {
		addMultipleValueObjectPropertyValue(PMLP.hasDescription_lname, newDescription);
	}
	
	public IWAgent getHasOwner () {
		IWAgent result = null;
		Object propValue = getObjectPropertyValue(PMLP.hasOwner_lname);
		if (propValue != null) 
			result = (IWAgent) propValue;
		return result;		
	}
	
	public void setHasOwner (IWAgent newOwner) {
		setObjectPropertyValue(PMLP.hasOwner_lname, newOwner);
	}
	public void setHasOwner (String ownerURI) {
		setObjectPropertyValue(PMLP.hasOwner_lname, ownerURI);
	}

} /* END OF IWDocumentFragmentByRowColImpl */
