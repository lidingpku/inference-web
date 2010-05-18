/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.util.*;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.PMLP;

public class IWIdentifiedThingImpl  extends PMLObjectImpl implements IWIdentifiedThing, Serializable {

	public IWIdentifiedThingImpl() {
	}
	
  public IWIdentifiedThingImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWIdentifiedThingImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	
	public IWIdentifiedThingImpl(DataObject desc) {
		super(desc);	
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
  		System.out.println("IWIdentifiedThing: properties map null");
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
			try{
				result = (IWAgent) propValue;				
			} catch (Exception e) {
		}
		return result;		
	}
	
	public void setHasOwner (IWAgent newOwner) {
		setObjectPropertyValue(PMLP.hasOwner_lname, newOwner);
	}
	public void setHasOwner (String ownerURI) {
		setObjectPropertyValue(PMLP.hasOwner_lname, ownerURI);
	}
	
} /* END OF IWIdentifiedThingImpl */
