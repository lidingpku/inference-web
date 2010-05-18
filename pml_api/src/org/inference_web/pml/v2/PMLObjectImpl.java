/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.v2;

import java.util.ArrayList;
import java.util.List;

import org.inference_web.pml.context.*;
import org.inference_web.pml.v2.pmlp.IWAgent;
import org.inference_web.pml.v2.pmlp.IWInformation;
import org.inference_web.pml.v2.util.PMLObjectManager;


public class PMLObjectImpl extends DataObjectImpl  {


	public PMLObjectImpl() {
	}
	
  public PMLObjectImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public PMLObjectImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }

	public PMLObjectImpl(DataObject desc) {
		super(desc);	
	}

	public List getMultipleValueObjectPropertyValue (String propLocalName) {
		List result = null;
		Object propValue = getPropertyByLocalName(propLocalName);
		if (propValue != null && propValue instanceof List) {
			result = (List)PMLObjectManager.getPMLObjectPropertyValue(propValue);
		}
		setPropertyByLocalName(propLocalName,result);
		return result;		
	}

	public void setMultipleValueObjectPropertyValue (String propLocalName, List newList) {
		setPropertyByLocalName(propLocalName,newList);
	}

	public void addMultipleValueObjectPropertyValue (String propLocalName, Object newValue) {
		List lst = getMultipleValueObjectPropertyValue (propLocalName);
		if (lst == null) {
			lst = new ArrayList();
		}
		lst.add(newValue);
		setMultipleValueObjectPropertyValue (propLocalName, lst);
	}
	public List getMultipleValueDataPropertyValue (String propLocalName) {
		List result = null;
		Object propValue = getPropertyByLocalName(propLocalName);
		if (propValue != null && propValue instanceof List) 
			result = (List)propValue;
		return result;		
	}

	public void setMultipleValueDataPropertyValue (String propLocalName, List newList) {
		setPropertyByLocalName(propLocalName,newList);
	}

	public void addMultipleValueDataPropertyValue (String propLocalName, String newValue) {
		List lst = getMultipleValueDataPropertyValue (propLocalName);
		if (lst == null) {
			lst = new ArrayList();
		}
		lst.add(newValue);
		setMultipleValueDataPropertyValue (propLocalName, lst);
	}
	
	public List getListObjectPropertyValue (String propLocalName) {
		List result = null;
		Object propValue = getPropertyByLocalName(propLocalName);
		if (propValue != null && propValue instanceof DataObject) {
			result = (List)PMLObjectManager.getPMLObjectPropertyValue((DataObject)propValue);
		}
		setPropertyByLocalName(propLocalName, result);
		return result;		
	}
	
	public void setListObjectPropertyValue (String propLocalName, List newList) {
		setPropertyByLocalName(propLocalName, newList);
	}

	public Object getObjectPropertyValue (String propLocalName) {
		Object result = null;
		Object propValue = getPropertyByLocalName(propLocalName);
		if (propValue != null) {
			result = PMLObjectManager.getPMLObjectPropertyValue(propValue);
		}
		setPropertyByLocalName(propLocalName, result);
		return result;		
	}
	
	public void setObjectPropertyValue (String propLocalName, Object newValue) {
		setPropertyByLocalName(propLocalName, newValue);
	}
	
	public String getDataPropertyValue(String propLocalName) {
		String result = null;
		Object value = getPropertyByLocalName(propLocalName);
		if (value != null && value instanceof String) result =(String)value;
		return result;
	}
	
	public void setDataPropertyValue(String propLocalName, String newValue) {
		setPropertyByLocalName(propLocalName, newValue);
	}
}
