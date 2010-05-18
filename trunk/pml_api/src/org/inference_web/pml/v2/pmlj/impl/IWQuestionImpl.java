/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import java.util.List;
import java.io.*;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.pmlp.impl.IWInformationImpl;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.v2.vocabulary.*;

public class IWQuestionImpl extends PMLObjectImpl implements IWQuestion, Serializable {

	private String questionContentV1PropName = "quetionContent";

    /* ===== CONSTRUCTORS ======= */

    public IWQuestionImpl() {
    }
    public IWQuestionImpl(String ontologyURI, String ontologyClassName) {
    	super(ontologyURI, ontologyClassName);
    }
    
    public IWQuestionImpl(String resourceTypeURI) {
    	super(resourceTypeURI);
    }
  	public IWQuestionImpl(DataObject dObj) {
  		super(dObj);	
  	}

    /* ======== METHODS ========= */

  	public String getContentRawString () {
  		String result = null;
  		if (this.hasPropertyByLocalName(questionContentV1PropName)) { // v1
  			result = (String)getPropertyByLocalName(questionContentV1PropName);
  		} else {
  			IWInformation contInfo = null;
  			contInfo = this.getHasContent();
  			if (contInfo != null) result = contInfo.getHasRawString();
  		}
  		return result;
  	}
  	
  	public void setContentRawString (String newContent) { 		
  		if (this.hasPropertyByLocalName(questionContentV1PropName)) { // v1
  			setPropertyByLocalName(questionContentV1PropName, newContent);
  		} else {// v2  			
  			IWInformation contInfo = null;
  			contInfo = getHasContent();
  			if (contInfo != null) {
  				contInfo.setHasRawString(newContent);
  			} else {
  				contInfo = new IWInformationImpl(getOntologyURI(), "Information");
  				contInfo.setHasRawString(newContent);
  				this.setHasContent(contInfo);
  			}
  		}
  	}

  	public String getContentString () {
  		String result = null;
  		IWInformation contInfo = this.getHasContent();
			if (contInfo != null) {
				result = contInfo.getHasPrettyString();
				if (result == null || result.trim().length()==0) {
					result = getContentRawString();
				}
			}
			if (result == null) result = "";
  		return result;
  	}
  	
 	public List getHasAnswerPattern() { 
  		return getMultipleValueDataPropertyValue(PMLJ.hasAnswerPattern_lname);
  	}
  	public void setHasAnswerPattern(List newPatterns) {
  		setMultipleValueDataPropertyValue(PMLJ.hasAnswerPattern_lname, newPatterns);
  	}
  	public void addHasAnswerPattern(String newPattern){
  		addMultipleValueDataPropertyValue(PMLJ.hasAnswerPattern_lname, newPattern);
  	}
    
/*    public String getAnswerPattern() { return _answerPattern; }
    
    public void setAnswerPattern(String _newPattern) {
	_answerPattern = _newPattern;
    }
*/
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

} // IWQuestionImpl
