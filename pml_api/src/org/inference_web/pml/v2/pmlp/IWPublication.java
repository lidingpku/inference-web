/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

public interface IWPublication extends IWDocument {
	
	public String getHasISBN();
	public void setHasISBN(String newISBN);
	
	public String getHasPublicationDateTime();
	public void setHasPublicationDateTime(String dateTime);
    
} /* END OF IWPublication */
