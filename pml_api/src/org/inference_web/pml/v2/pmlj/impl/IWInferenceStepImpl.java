/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import java.util.*;
import java.io.*;

import org.inference_web.pml.shared.util.ToolURI;
import org.inference_web.pml.context.*;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.v2.vocabulary.*;



public class IWInferenceStepImpl extends PMLObjectImpl implements IWInferenceStep, Serializable {

	protected String antecedentPropName =null;
	private String inferenceRuleV1PropName = "hasRule";
	
  /* ===== CONSTRUCTORS ======= */

  public IWInferenceStepImpl() {
	
  }
  public IWInferenceStepImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  	setAntecedentPropertyName();
  }
  
  public IWInferenceStepImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  	setAntecedentPropertyName();
  }

  public IWInferenceStepImpl (DataObject desc) {
  	super(desc);
  	setAntecedentPropertyName();
  }

  /* ======== METHODS ========= */

  private void setAntecedentPropertyName() {
  	if (properties != null) {
  		boolean found = false;
  		Iterator propNames = properties.keySet().iterator();
  		while (propNames.hasNext() && !found) {
  			String propName = (String)propNames.next();
  			if (propName.indexOf("hasAntecedent")>=0) {
  				antecedentPropName = propName;
  				found = true;
  			}
  		}
  	} else {
  		System.out.println("InferenceStep.setAntecedentPropertyName: properties map null");
  	}
  }

  public String getAntecedentPropertyName () {
  	if (antecedentPropName == null) {
  		setAntecedentPropertyName();
  	}
  	return antecedentPropName;
  }
  
  public IWInferenceRule getHasInferenceRule() {
  	IWInferenceRule result = null;
  	Object rule = getObjectPropertyValue(PMLJ.hasInferenceRule_lname);
  	if (rule == null) { // check v1 property name
  		rule = getObjectPropertyValue(inferenceRuleV1PropName);
  	}
  	if (rule != null) {
  		try {
  			result = (IWInferenceRule) PMLObjectManager.getPMLObject(rule);
  		}catch (Exception e){}
  	}
  	return result;
  }

  public void setHasInferenceRule(IWInferenceRule newInferenceRule) {
  	String rulePropName = PMLJ.hasInferenceRule_lname;
  	Iterator props = this.getProperties().keySet().iterator();
  	while (props.hasNext()) {
  		String propName = (String)props.next();
  		String propLocalName = PMLObjectManager.getObjectID(propName).getName();
  		if (propLocalName.equalsIgnoreCase(inferenceRuleV1PropName)) {
  			rulePropName = inferenceRuleV1PropName;
  			break;
  		}
  	}
		this.setPropertyByLocalName(rulePropName, newInferenceRule);
  }
  
  public void setHasInferenceRule(String inferenceRuleURI) {
  	String rulePropName = PMLJ.hasInferenceRule_lname;
  	Iterator props = this.getProperties().keySet().iterator();
  	while (props.hasNext()) {
  		String propName = (String)props.next();
  		String propLocalName = PMLObjectManager.getObjectID(propName).getName();
  		if (propLocalName.equalsIgnoreCase(inferenceRuleV1PropName)) {
  			rulePropName = inferenceRuleV1PropName;
  			break;
  		}
  	}
		this.setPropertyByLocalName(rulePropName, inferenceRuleURI);
  }
  
  public IWInferenceEngine getHasInferenceEngine() {
  	IWInferenceEngine result = null;
  	Object eng = getObjectPropertyValue(PMLJ.hasInferenceEngine_lname);
  	if (eng != null) {
  		try {
  			result = (IWInferenceEngine)PMLObjectManager.getPMLObject(eng);
  		} catch (Exception e) {}
  	}
  	return result;
  }

  public void setHasInferenceEngine(IWInferenceEngine newEngine) {
		this.setObjectPropertyValue(PMLJ.hasInferenceEngine_lname, newEngine);
  }
  public void setHasInferenceEngine(String engineURI) {
		this.setObjectPropertyValue(PMLJ.hasInferenceEngine_lname, engineURI);
  }
  
  public IWNodeSet getFromAnswer() {
  	IWNodeSet result = null;
//  	Object prop = getMultipleValueObjectPropertyValue(PMLJ.fromAnswer_lname);
  	Object prop = getObjectPropertyValue(PMLJ.fromAnswer_lname);
  	if (prop == null) {
  		prop = this.getObjectPropertyValue(PMLJ.fromAnswerOrQuery_lname);
  	}
  	if (prop != null) {
  		if (prop instanceof List) { // this is because subproperty fromAnswer of fromAnswerOrQuery does not have cardinality on
  			prop = ((List)prop).get(0);
  		}
  		if (prop != null) {
  			try{result = (IWNodeSet)prop;} catch(Exception e){}
  		}
  	}
  	return result;
  }

  public void setFromAnswer(IWNodeSet newAnswer) {
  	if (newAnswer != null) {
    	ArrayList answer = new ArrayList();
    	answer.add(newAnswer);
    	this.setMultipleValueObjectPropertyValue(PMLJ.fromAnswer_lname, answer);
  	}
  }
  public void setFromAnswer(String answerURI) {
  	if (answerURI != null) {
    	ArrayList answer = new ArrayList();
    	answer.add(answerURI);
    	this.setMultipleValueObjectPropertyValue(PMLJ.fromAnswer_lname, answer);
  	}
  }
  
  public IWQuery getFromQuery() {
  	IWQuery result = null;
//  	Object prop = getMultipleValueObjectPropertyValue(PMLJ.fromQuery_lname);
  	Object prop = getObjectPropertyValue(PMLJ.fromQuery_lname);
  	if (prop == null) {
  		prop = this.getObjectPropertyValue(PMLJ.fromAnswerOrQuery_lname);
  	}
  	if (prop != null) {
  		if (prop instanceof List) { // this is because subproperty fromQuery of fromAnswerOrQuery does not have cardinality on
  			prop = ((List)prop).get(0);
  		}
  		if (prop != null) {
  			try {result = (IWQuery)PMLObjectManager.getPMLObject(prop);}catch(Exception e){}
  		}
  	}
  	return result;
  }

  public void setFromQuery(IWQuery newQuery) {
  	if (newQuery != null) {
  	ArrayList query = new ArrayList();
  	query.add(newQuery);
  	this.setMultipleValueObjectPropertyValue(PMLJ.fromQuery_lname, query);
  	}
  }
  public void setFromQuery(String queryURI) {
  	if (queryURI != null) {
  	ArrayList query = new ArrayList();
  	query.add(queryURI);
  	this.setMultipleValueObjectPropertyValue(PMLJ.fromQuery_lname, query);
  	}
  }
  
  public void setHasAntecedentList(List newAntecedents) {
  	if (antecedentPropName == null) {
  		setAntecedentPropertyName();
  	}
  	setProperty(antecedentPropName, newAntecedents);
  }
  
  public List getHasAntecedentList() {
  	List result = null;
  	List antes = null;
  	Object anteNode = null;
  	if (antecedentPropName == null) {
  		setAntecedentPropertyName();
  	}
  	Object antecedents = getProperty(antecedentPropName);
  	if (antecedents != null && antecedents instanceof List) {
  		antes = new ArrayList();
  		Iterator anteIt = ((List)antecedents).listIterator();
  		while (anteIt.hasNext()) {
  			anteNode = anteIt.next();
  			if (anteNode instanceof String) {
  				IWNodeSet ante = PMLObjectManager.getNodeSet((String) anteNode);
  				if (ante != null) {
  					antes.add(ante);
  				} else {
  					antes.add(anteNode);
  				}
  			} else if (PMLObjectManager.isPMLObject(anteNode)) { 
  				antes.add(anteNode); 
  			} else if (anteNode instanceof DataObject) { 
  				antes.add(PMLObjectManager.getPMLObjectFromDataObject((DataObject) anteNode));
  			}		
  		}
  		setHasAntecedentList(antes);
  		result = antes;
  	}	
    return result;
  }
  

  public List getHasVariableMapping() {
  	return getMultipleValueObjectPropertyValue(PMLJ.hasVariableMapping_lname);
  }

  public void setHasVariableMapping(List newMappings) {
  	setMultipleValueObjectPropertyValue(PMLJ.hasVariableMapping_lname, newMappings);
  }
  
  public void addHasVariableMapping(IWMapping aMapping) {
  	addMultipleValueObjectPropertyValue(PMLJ.hasVariableMapping_lname, aMapping);
  }

  public List getHasMetaBinding() {
  	return getMultipleValueObjectPropertyValue(PMLJ.hasMetaBinding_lname);
  }

  public void setHasMetaBinding(List newMetaBindings) {
  	setMultipleValueObjectPropertyValue(PMLJ.hasMetaBinding_lname, newMetaBindings);
  }
  public void addHasMetaBinding(IWMapping aMapping) {
  	addMultipleValueObjectPropertyValue(PMLJ.hasMetaBinding_lname, aMapping);
  }
  
  public IWSourceUsage getHasSourceUsage() {
  	IWSourceUsage result = null;
  	Object srcUsg = this.getObjectPropertyValue(PMLJ.hasSourceUsage_lname);
  	if (srcUsg != null) {
  		try {result = (IWSourceUsage)srcUsg;} catch (Exception e){}
  	}
    return result;
  }

  public void setHasSourceUsage(IWSourceUsage newSourceUsage) {
  	this.setObjectPropertyValue(PMLJ.hasSourceUsage_lname, newSourceUsage);
  }

  public int getHasIndex() {
  	int result = -1;  	
  	Object idx = this.getDataPropertyValue(PMLJ.hasIndex_lname);
  	if (idx != null && idx instanceof String) {
  		result = Integer.parseInt((String)idx);
  	}
  	return result;
  }
  
  public void setHasIndex(int newIndex) {
  	if (newIndex >=0) {
  		setDataPropertyValue(PMLJ.hasIndex_lname,Integer.toString(newIndex));
  	}
  }
  
  public List getHasDischarge() {
  	return this.getMultipleValueObjectPropertyValue(PMLJ.hasDischarge_lname);
  }
  public void setHasDischarge(List discharges) {
  	this.setMultipleValueObjectPropertyValue(PMLJ.hasDischarge_lname, discharges);
  }
  public void addHasDischarge(IWNodeSet newDischarge) {
  	this.addMultipleValueObjectPropertyValue(PMLJ.hasDischarge_lname, newDischarge);
  }
  public void addHasDischarge(String dischargeURI) {
  	this.addMultipleValueObjectPropertyValue(PMLJ.hasDischarge_lname, dischargeURI);
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
  public void setAntecedents(List newAntecedents) {
   	setHasAntecedentList(newAntecedents);
  }
   
   public List getAntecedents() {
  	 List result = null;
  	 List temp = getHasAntecedentList();
  	 if (temp != null && temp.size()>0) {
  		 result = new ArrayList();
  		 for (int i=0; i<temp.size(); i++) {
  			 Object ante = temp.get(i);
  			 if (ante instanceof String) {
  				 result.add((String)ante); 
  			 } else if (ante instanceof DataObject) { 
  				 DataObjectIdentifier id = ((DataObject)ante).getIdentifier();
  				 if (id!=null) {
  					 String uri = id.getURIString();
  					 if (uri!=null) {
  						 result.add(uri);
  					 } else {
  						 result.add(ante);
  					 }
  				 } else {
  					 result.add(ante);
  				 }
  			 }	else {
  				 result.add(ante);
  			 }
  		 }
  	 }
  	 return result;
   }
   public void addAntecedentAtBottom(Object newAntecedent) {
   	addAntecedent(newAntecedent) ;
   }
   
   public void addAntecedent(Object newAntecedent) {
   	if (newAntecedent != null) {
   		List antes = getHasAntecedentList();
   		if (antes == null || antes.size()==0) {
   			antes = new ArrayList();
   			antes.add(newAntecedent);
   			this.setAntecedents(antes);
   		} else {
   			antes.add(antes.size(), newAntecedent);
   		}
   	}
   }
   public void addAntecedentAtTop(Object newAntecedent) {

   	if (newAntecedent != null) {
   		List antes = getHasAntecedentList();
   		if (antes.size()>0) {
   			antes.add(0, newAntecedent);
   		} else {
   			antes = new ArrayList();
   			antes.add(newAntecedent);
   		}
   		this.setAntecedents(antes);
   	}

   }
   
   public void replaceAntecedent(String oldAntecedent, String newAntecedent) {

   	if (oldAntecedent != null && newAntecedent != null) {
   		List antes = getHasAntecedentList();
   		if (antes != null && antes.size()>0) {
   			for (int i=0; i<antes.size(); i++) {
   				String current = ((IWNodeSet)antes.get(i)).getIdentifier().getURIString();
   				if (current.equalsIgnoreCase(oldAntecedent)) {
   					antes.set(i, PMLObjectManager.getNodeSet(newAntecedent));
   				}
   			}  	
   		}
   	}
   }
   
   public boolean hasAntecedents () {
  	 boolean result = false;
  	 List antecedents = this.getHasAntecedentList();
  	 if (antecedents != null && antecedents.size()>0 )
  		 result = true;
  	 return result;
   }
   
   public double getHasConfidenceValue() {
   	double result = 0.0;  	
   	Object cv = this.getDataPropertyValue(PMLP.hasConfidenceValue_lname);
   	if (cv != null && cv instanceof String) {
   		result = Double.parseDouble((String)cv);
   	}
   	return result;
   }
   
   public void setHasConfidenceValue(double confidence) {  	
  	 setDataPropertyValue(PMLP.hasConfidenceValue_lname,Double.toString(confidence));  
   }

}
/* END OF IWInferenceStepImpl */
