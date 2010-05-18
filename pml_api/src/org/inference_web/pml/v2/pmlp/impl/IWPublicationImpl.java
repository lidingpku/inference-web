/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.vocabulary.PMLP;

public class IWPublicationImpl  extends PMLObjectImpl implements IWPublication, Serializable {

	public IWPublicationImpl() {
	}
	
  public IWPublicationImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWPublicationImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }
	
	public IWPublicationImpl(DataObject desc) {
		super(desc);	
	}
	
	public String getHasISBN() {
		return getDataPropertyValue(PMLP.hasISBN_lname);
	}
	
	public void setHasISBN(String newISBN) {
		setDataPropertyValue(PMLP.hasISBN_lname, newISBN);
	}
	
	public String getHasPublicationDateTime() {
		return getDataPropertyValue(PMLP.hasPublicationDateTime_lname);
	}
	
	public void setHasPublicationDateTime(String dateTime) {
		setDataPropertyValue(PMLP.hasPublicationDateTime_lname, dateTime);
	}

	public String getHasAbstract() {
		return getDataPropertyValue(PMLP.hasAbstract_lname);
	}
	public void setHasAbstract(String newAbstract) {
		setDataPropertyValue(PMLP.hasAbstract_lname, newAbstract);
	}
	
	public IWInformation getHasContent() {
		IWInformation result = null;
		Object value = getObjectPropertyValue(PMLP.hasContent_lname);
		if (value != null) {
			try {
				result = (IWInformation)value;
			} catch (Exception e) {
			}
		}
		return result;
	}
	public void setHasContent(IWInformation newContent) {
		setObjectPropertyValue(PMLP.hasContent_lname, newContent);
	}
	
	public List getHasPublisher() {
		return this.getMultipleValueObjectPropertyValue(PMLP.hasPublisher_lname);
	}
	public void setHasPublisher(List newList) {
		this.setMultipleValueObjectPropertyValue(PMLP.hasPublisher_lname, newList);
	}
	public void addHasPublisher(IWAgent newPublisher) {
		this.addMultipleValueObjectPropertyValue(PMLP.hasPublisher_lname, newPublisher);
	}
	public void addHasPublisher(String publisherURI) {
		this.addMultipleValueObjectPropertyValue(PMLP.hasPublisher_lname, publisherURI);
	}
	
	public String getHasVersion(){
	return getDataPropertyValue(PMLP.hasVersion_lname);
}
	public void setHasVersion(String newVersion){
		setDataPropertyValue (PMLP.hasVersion_lname, newVersion);
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
  		System.out.println("IWPublication: properties map null");
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
} /* END OF IWPublicationImpl */
