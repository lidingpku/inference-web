/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.util.*;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.PMLP;

public class IWPrettyNameMappingImpl  extends PMLObjectImpl implements IWPrettyNameMapping, Serializable {
	
	public IWPrettyNameMappingImpl() {
	}
	
  public IWPrettyNameMappingImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWPrettyNameMappingImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	
	public IWPrettyNameMappingImpl(DataObject desc) {
		super(desc);	
	}

	public String getHasReplacee () {
		String result = null;
		Object value = getDataPropertyValue(PMLP.hasReplacee_lname);
		if (value != null) result = (String)value;
		return result;
	}
	public void setHasReplacee (String newReplacee) {
		setDataPropertyValue(PMLP.hasReplacee_lname, newReplacee);
	}

	public String getHasShortPrettyName () {
		String result = null;
		Object value = getDataPropertyValue(PMLP.hasShortPrettyName_lname);
		if (value != null) result = (String)value;
		return result;
	}
	public void setHasShortPrettyName (String newShortPrettyName) {
		setDataPropertyValue(PMLP.hasShortPrettyName_lname, newShortPrettyName);
	}

	public List getHasLongPrettyName() {
		return getMultipleValueDataPropertyValue(PMLP.hasLongPrettyName_lname);
	}
	public void setHasLongPrettyName(List newList) {
		setMultipleValueDataPropertyValue(PMLP.hasLongPrettyName_lname, newList);
	}
	public void addHasLongPrettyName(String newLongPrettyName){
		addMultipleValueDataPropertyValue(PMLP.hasLongPrettyName_lname, newLongPrettyName);
	}

} /* END OF IWPrettyNameMappingImpl */
