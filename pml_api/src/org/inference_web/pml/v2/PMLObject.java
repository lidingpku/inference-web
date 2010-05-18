/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.v2;

import java.util.List;

import org.inference_web.pml.context.DataObject;


public interface PMLObject extends DataObject {

	abstract Object getObjectPropertyValue (String propLocalName) ;	
	abstract void setObjectPropertyValue (String propLocalName, Object newValue) ;
	
	abstract String getDataPropertyValue(String propLocalName) ;	
	abstract void setDataPropertyValue(String propLocalName, String newValue) ;


	abstract List getMultipleValueObjectPropertyValue (String propLocalName) ;
	abstract void setMultipleValueObjectPropertyValue (String propLocalName, List newList) ;
	abstract void addMultipleValueObjectPropertyValue (String propLocalName, Object newValue) ;
	
	abstract List getMultipleValueDataPropertyValue (String propLocalName) ;
	abstract void setMultipleValueDataPropertyValue (String propLocalName, List newList) ;
	abstract void addMultipleValueDataPropertyValue (String propLocalName, String newValue) ;
	
	abstract List getListObjectPropertyValue (String propLocalName) ;	
	abstract void setListObjectPropertyValue (String propLocalName, List newList) ;

}
