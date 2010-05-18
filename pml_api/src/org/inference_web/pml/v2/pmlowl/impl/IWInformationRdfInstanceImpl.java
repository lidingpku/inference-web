/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlowl.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlowl.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.pmlp.impl.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.*;


public class IWInformationRdfInstanceImpl  extends PMLObjectImpl implements IWInformationRdfInstance, Serializable {

	public IWInformationRdfInstanceImpl() {
	}
	
  public IWInformationRdfInstanceImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWInformationRdfInstanceImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	public IWInformationRdfInstanceImpl(DataObject desc) {
		super(desc);	
	}

	public Object getHasInstanceReference() {
		Object result = getObjectPropertyValue(PMLOWL.hasInstanceReference_lname);
		return result;		
	}
	
	public void setHasInstanceReference(Object newInstanceReference) {
			setObjectPropertyValue(PMLOWL.hasInstanceReference_lname, newInstanceReference);
	}
	public IWLanguage getHasLanguage() {
		IWLanguage result = null;
		Object value = getObjectPropertyValue(PMLP.hasLanguage_lname);
		if (value != null) {
			result = (IWLanguage)PMLObjectManager.getPMLObject(value);
		}
		return result;		
	}
	
	public void setHasLanguage(IWLanguage newLanguage) {
		setObjectPropertyValue(PMLP.hasLanguage_lname,newLanguage);
	}
	public void setHasLanguage(String languageURI) {
		setObjectPropertyValue(PMLP.hasLanguage_lname, languageURI);
	}
	
	public IWFormat getHasFormat() {
		IWFormat result = null;
		Object value = getObjectPropertyValue(PMLP.hasFormat_lname);
		if (value != null) {
			result = (IWFormat)PMLObjectManager.getPMLObject(value);
		}
		return result;		
	}
	public void setHasFormat(IWFormat newFormat) {
		setObjectPropertyValue(PMLP.hasFormat_lname, newFormat);
	}
	public void setHasFormat(String formatURI) {
		setObjectPropertyValue(PMLP.hasFormat_lname, formatURI);
	}
	
	public IWSourceUsage getHasReferenceSourceUsage() {
		IWSourceUsage result = null;
		Object value = getObjectPropertyValue(PMLP.hasReferenceSourceUsage_lname);
		if (value != null) {
			result = (IWSourceUsage)value;
		}
		return result;		
	}
	
	public void setHasReferenceSourceUsage(IWSourceUsage newInfoSourceUsage) {
			setObjectPropertyValue(PMLP.hasReferenceSourceUsage_lname,newInfoSourceUsage);
	}
	
	public List getHasPrettyNameMappingList() {
		return getListObjectPropertyValue(PMLP.hasPrettyNameMappingList_lname);
	}
	public void setHasPrettyNameMappingList(List newList){
		this.setListObjectPropertyValue(PMLP.hasPrettyNameMappingList_lname, newList);
	}
	
	public String getHasPrettyString () {
		return getDataPropertyValue(PMLP.hasPrettyString_lname);
	}
	public void setHasPrettyString(String newString) {
		setDataPropertyValue(PMLP.hasPrettyString_lname, newString);
	}
	
	public String getHasRawString (){
		return getDataPropertyValue(PMLP.hasRawString_lname);

	}
	public void setHasRawString(String newString){
		setDataPropertyValue(PMLP.hasRawString_lname, newString);
	}
	
	public String getHasURL () {
		return getDataPropertyValue(PMLP.hasURL_lname);

	}
	public void setHasURL(String newURL){
		setDataPropertyValue(PMLP.hasURL_lname, newURL);
	}
	
	public List getHasEncoding() {
		return getMultipleValueDataPropertyValue(PMLP.hasEncoding_lname);
	}
	public void setHasEncoding(List newList) {
		setMultipleValueDataPropertyValue(PMLP.hasEncoding_lname, newList);
	}
	public void addHasEncoding(String newEncoding){
		addMultipleValueDataPropertyValue(PMLP.hasEncoding_lname, newEncoding);
	}

	public List getHasMimetype() {
		return getMultipleValueDataPropertyValue(PMLP.hasMimetype_lname);
	}
	public void setHasMimetype(List newList) {
		this.setMultipleValueDataPropertyValue(PMLP.hasMimetype_lname, newList);
	}
	public void addHasMimetype(String newMime){
		addMultipleValueDataPropertyValue(PMLP.hasMimetype_lname, newMime);		
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
  		System.out.println("IWInformation: properties map null");
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

} /* END OF IWInformationRDFInstanceImpl */
