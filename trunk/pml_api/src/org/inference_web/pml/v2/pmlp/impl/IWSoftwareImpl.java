/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.vocabulary.PMLP;

public class IWSoftwareImpl  extends PMLObjectImpl implements IWSoftware, Serializable {

	public IWSoftwareImpl() {
	}
	
  public IWSoftwareImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWSoftwareImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	
	public IWSoftwareImpl(DataObject desc) {
		super(desc);	
	}
	
	public String getHasVersion() {
		return getDataPropertyValue(PMLP.hasVersion_lname);
	}
	public void setHasVersion(String newVersion) {
		setDataPropertyValue(PMLP.hasVersion_lname, newVersion);
	}


  public List getIsMemberOf () {
  	return getMultipleValueObjectPropertyValue(PMLP.isMemberOf_lname);
  }
  public void setIsMemberOf(List newList) {
  	setMultipleValueObjectPropertyValue(PMLP.isMemberOf_lname, newList);
  }
  public void addIsMemberOf(IWOrganization newOrg) {
  	addMultipleValueObjectPropertyValue(PMLP.isMemberOf_lname, newOrg);
  }
  public void addIsMemberOf(String orgURI) {
  	addMultipleValueObjectPropertyValue(PMLP.isMemberOf_lname, orgURI);
  }
  
  public List getUsesInferenceEngine() {
  	return getMultipleValueObjectPropertyValue(PMLP.usesInferenceEngine_lname);
  }
  public void setUsesInferenceEngine(List newList) {
  	setMultipleValueObjectPropertyValue(PMLP.usesInferenceEngine_lname, newList);
  }
  public void addUsesInferenceEngine(IWInferenceEngine newEngine){
  	addMultipleValueObjectPropertyValue(PMLP.usesInferenceEngine_lname, newEngine);
  }
  public void addUsesInferenceEngine(String engineURI){
  	addMultipleValueObjectPropertyValue(PMLP.usesInferenceEngine_lname, engineURI);
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
  		System.out.println("IWSoftware: properties map null");
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
} /* END OF IWSoftwareImpl */
