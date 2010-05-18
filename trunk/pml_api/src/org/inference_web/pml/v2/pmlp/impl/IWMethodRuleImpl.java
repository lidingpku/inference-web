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

public class IWMethodRuleImpl extends PMLObjectImpl implements IWMethodRule, Serializable  { 
	

	public IWMethodRuleImpl() {
	}
  public IWMethodRuleImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWMethodRuleImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }


	public IWMethodRuleImpl(DataObject desc) {
		super(desc);	
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
	public void setHasContent (IWInformation newContent) {
  	setObjectPropertyValue(PMLP.hasContent_lname,newContent);
	}
	
	public String getRuleSpec() {
		String result = null;
		Object content1 = null;
		IWInformation content2 = getHasContent();;
		if (content2 != null) { // v2 property
			result = (String)content2.getPropertyByLocalName("hasRawString");
		} else { //v1 property
			content1 = getPropertyByLocalName(ruleSpecPropName);
			if (content1 != null && content1 instanceof String) {
				result =(String)content1;
			}
		}
		return result;
	}

  /**
   * Sets the rule specification
   * @param newSpec the rule specification
   */
  public void setRuleSpec(String newSpec) {
		IWInformation content2 = getHasContent();;
		if (content2 != null) { // v2
			content2.setPropertyByLocalName("hasRawString", newSpec);
		} else { // v1
			if (isPropertyRangeByLocalName(ruleSpecPropName, "String")) { // check if v1
				setPropertyByLocalName(ruleSpecPropName, newSpec);
			}
		}
	}
  
  public IWLanguage getLanguage () {
  	IWLanguage result = null;
  	IWInformation content = null;
  	String languagePropName = PMLP.hasLanguage_lname;

  	Object language = getPropertyByLocalName(languagePropName);
  	if (language != null) { // v1 property
  		result = (IWLanguage)PMLObjectManager.getPMLObject(language);
  		if (result != null) {
  			setPropertyByLocalName(languagePropName, result);
  		}
  	} else { // v2. check hasLanguage of hasConclusion
  		content = getHasContent();
  		if (content != null) {
  			language = content.getPropertyByLocalName(languagePropName);
    		result = (IWLanguage)PMLObjectManager.getPMLObject(language);
    		if (result != null) {
    			content.setPropertyByLocalName(languagePropName, result);
    		}
  		}
  	}
  	return result;
  }
  
  public void setLanguage (IWLanguage newLanguage) {

  	IWInformation content = null;
  	String languagePropName = PMLP.hasLanguage_lname;
  	
  	if (hasPropertyByLocalName(languagePropName)) { // v1
  		setPropertyByLocalName(languagePropName, newLanguage);
  	} else { //v2
  		content = getHasContent();
  		if (content != null) { 
  			content.setPropertyByLocalName(languagePropName, newLanguage);
  		}
  	}
  }
  
	public String getEnglishDescription() {
		String result = null;
		Object contentO = null;
		IWInformation content = (IWInformation)getHasDescription().get(0);
		if (content != null) { // v2
			result = (String)content.getPropertyByLocalName("hasPrettyString");
		} else { // v1
			contentO = getPropertyByLocalName(englishDescriptionPropName);
			if (contentO != null && contentO instanceof String) {
				result =(String)contentO;
			}
		}
		return result;
	}
  
	public void setEnglishDescription(String newEngDesc) {
		IWInformation content = (IWInformation)getHasDescription().get(0);
		if (content != null) { // v2
			content.setPropertyByLocalName("hasPrettyString", newEngDesc);
		} else if (hasPropertyByLocalName(englishDescriptionPropName)) {
			setPropertyByLocalName(englishDescriptionPropName, newEngDesc);
		}
	}
  public List getHasRuleExample() {
  	return getMultipleValueObjectPropertyValue(PMLP.hasRuleExample_lname);
  }

  public void setHasRuleExample(List newExamples) {
  	setMultipleValueObjectPropertyValue(PMLP.hasRuleExample_lname, newExamples);
  }

  public void addHasRuleExample(IWInformation newExample) {
  		addMultipleValueObjectPropertyValue(PMLP.hasRuleExample_lname, newExample);
  }
  
  public String getHasEnglishDescriptionTemplate () {
  	return getDataPropertyValue(PMLP.hasEnglishDescriptionTemplate_lname);
  }
  
  public void setHasEnglishDescriptionTemplate (String template) {
  		setDataPropertyValue(PMLP.hasEnglishDescriptionTemplate_lname, template);
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
  		System.out.println("IWMethodRule: properties map null");
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
} /* END OF IWMethodRuleImpl */
