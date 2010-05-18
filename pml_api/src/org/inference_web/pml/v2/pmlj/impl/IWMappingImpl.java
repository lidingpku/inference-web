/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.vocabulary.*;

public class IWMappingImpl extends PMLObjectImpl implements IWMapping { 
	
	protected final String toPropName = PMLJ.mapTo_lname;
	protected final String fromPropName = PMLJ.mapFrom_lname;

	/* ====== ATRIBUTES ========= */

	protected String mapFrom = null;
	protected String mapTo = null;

	/* ===== CONSTRUCTORS ======= */

	public IWMappingImpl() {
		super();
		mapFrom = "";
		mapTo = "";
	}

	public IWMappingImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
	}
	
	public IWMappingImpl(DataObject desc) {
		super(desc);	
		if (desc != null) {
			mapFrom = (String)desc.getPropertyByLocalName(fromPropName);
			mapTo = (String)desc.getPropertyByLocalName(toPropName);
		}
	}

	/* ======== METHODS ========= */

	public String getMapFrom() { 
		return mapFrom; 
	}

	public void setMapFrom(String newFrom) { 
		mapFrom = newFrom; 
		setPropertyByLocalName(fromPropName,newFrom);
	}

	public String getMapTo() { 
		return mapTo; 
	}

	public void setMapTo(String newTo) { 
		mapTo = newTo; 
		setPropertyByLocalName(toPropName, newTo);
	}


	public boolean equals (IWMapping mapping) {
		boolean result = true;
		if (mapFrom != null && mapping.getMapFrom()!=null) {
			if (!mapFrom.equalsIgnoreCase(mapping.getMapFrom())) {
				return false;
			}
		}	else {
			if (!(mapFrom == null && mapping.getMapFrom() == null ) ) {
				return false;
			}

		}
		if (mapTo != null && mapping.getMapTo()!=null) {
			if (!mapTo.equalsIgnoreCase(mapping.getMapTo())) {
				return false;
			}
		}	else {
			if (!(mapTo == null && mapping.getMapTo() == null ) ) {
				return false;
			}        
		}

		return result;
	}

} /* END OF IWMappingImpl */
