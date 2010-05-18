/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import java.io.Serializable;

import org.inference_web.pml.context.*;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.vocabulary.*;



public class IWJustificationElementImpl  extends PMLObjectImpl implements IWJustificationElement, Serializable {


	public IWJustificationElementImpl() {
	}
	
  public IWJustificationElementImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWJustificationElementImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	public IWJustificationElementImpl(DataObject desc) {
		super(desc);	
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

} /* END OF IWJustificationElementImpl */
