/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import java.util.*;
import java.io.*;

import org.inference_web.pml.context.*;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.pmlp.impl.*;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.v2.vocabulary.*;


public class IWQueryImpl extends PMLObjectImpl implements IWQuery, Serializable {
	
	private String queryContentV1PropName = "queryContent";

    /* ===== CONSTRUCTORS ======= */

    public IWQueryImpl() {
    }
    public IWQueryImpl(String ontologyURI, String ontologyClassName) {
    	super(ontologyURI, ontologyClassName);
    }
    
    public IWQueryImpl(String resourceTypeURI) {
    	super(resourceTypeURI);
    }
  	
  	public IWQueryImpl(DataObject desc) {
  		super(desc);	
  	}

    /* ======== METHODS ========= */

    
  	public IWLanguage getLanguage () {
  		IWLanguage result = null;
  		IWInformation contInfo = null;
  		Object language = getPropertyByLocalName(PMLP.hasLanguage_lname);
  		if (language != null) { // v1 property, language is direct property
  			result = (IWLanguage) PMLObjectManager.getPMLObject(language);
  			if (result != null) {
  				setPropertyByLocalName(PMLP.hasLanguage_lname,result);  				
  			}
  		} else { // v2. check hasLanguage of hasContent
  			contInfo = this.getHasContent();
  			if (contInfo != null) {
  				result = contInfo.getHasLanguage();  
  			}
  		}
  		return result;
  	}
  	
  	public void setLanguage (IWLanguage newLanguage) {
  		if (hasPropertyByLocalName(PMLP.hasLanguage_lname)) {// v1
  			this.setPropertyByLocalName(PMLP.hasLanguage_lname, newLanguage);
  		} else { // v2
  			IWInformation contInfo = getHasContent();
  			if (contInfo != null) {
  				contInfo.setHasLanguage(newLanguage);
  			} else {
  				contInfo = new IWInformationImpl(getOntologyURI(), PMLP.Information_lname);
  				contInfo.setHasLanguage(newLanguage);
  				this.setHasContent(contInfo);
  			}
  		}
  	}
  	
  	public String getContentRawString () {
  		String result = null;
  		if (this.hasPropertyByLocalName(queryContentV1PropName)) { // v1
  			result = (String)getPropertyByLocalName(queryContentV1PropName);
  		} else {
  			IWInformation contInfo = null;
  			contInfo = this.getHasContent();
  			if (contInfo != null) result = contInfo.getHasRawString();
  		}
  		if (result == null) result = "";
  		return result;
  	}
  	
  	public void setContentRawString (String newContent) {
  		if (this.hasPropertyByLocalName(queryContentV1PropName)) { // v1
  			setPropertyByLocalName(queryContentV1PropName, newContent);
  		} else {// v2  			
  			IWInformation contInfo = null;
  			contInfo = getHasContent();
  			if (contInfo != null) {
  				contInfo.setHasRawString(newContent);
  			} else {
  				contInfo = new IWInformationImpl(getOntologyURI(), PMLP.Information_lname);
  				contInfo.setHasRawString(newContent);
  				this.setHasContent(contInfo);
  			}
  		}
  	}

    public List getHasAnswer() { 
    	return getMultipleValueObjectPropertyValue(PMLJ.hasAnswer_lname);
    }

    public void setHasAnswer(List _newAnswers) {
    	setMultipleValueObjectPropertyValue(PMLJ.hasAnswer_lname,_newAnswers);
    }

    public void addHasAnswer(IWNodeSet newAnswer){
    	this.addMultipleValueObjectPropertyValue(PMLJ.hasAnswer_lname, newAnswer);
    }
    public void addHasAnswer(String answerURI){
    	this.addMultipleValueObjectPropertyValue(PMLJ.hasAnswer_lname, answerURI);
    }
    
  	public List getIsQueryFor () {
  		return getMultipleValueObjectPropertyValue(PMLJ.isQueryFor_lname);
  	}
  	
  	public void setIsQueryFor (List newQuestions) {
  	setMultipleValueObjectPropertyValue(PMLJ.isQueryFor_lname, newQuestions);
  	}
    public void addIsQueryFor (IWQuestion question){
    	this.addMultipleValueObjectPropertyValue(PMLJ.isQueryFor_lname, question);
    }
    public void addIsQueryFor (String questionURI){
    	this.addMultipleValueObjectPropertyValue(PMLJ.isQueryFor_lname, questionURI);
    }
    
    public IWInferenceEngine getIsFromEngine() {
    	IWInferenceEngine result = null;
    	Object value = getObjectPropertyValue(PMLJ.isFromEngine_lname);
    	if (value != null) {
    		try{
    			result = (IWInferenceEngine)PMLObjectManager.getPMLObject(value);
				} catch (Exception e) {
				}
    	}
    	return result;
    }
    public void setIsFromEngine(IWInferenceEngine newEngine) {
    	setObjectPropertyValue(PMLJ.isFromEngine_lname, newEngine);
    }
    public void setIsFromEngine(String engineURI) {
    	setObjectPropertyValue(PMLJ.isFromEngine_lname, engineURI);
    }
    
    public IWInformation getHasContent(){
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

  	public String getHasCreationDateTime () {
  		String result = null;
  		Object value = getDataPropertyValue(PMLP.hasCreationDateTime_lname);
  		if (value != null) result = (String)value;
  		return result;
  	}
  	public void setHasCreationDateTime (String newDateTime) {
  		setDataPropertyValue(PMLP.hasCreationDateTime_lname, newDateTime);
  	}

}   /* END OF IWQueryImpl */
