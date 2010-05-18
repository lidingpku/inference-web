/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

public interface IWDocumentFragment extends IWSource{
	
	public IWDocument getHasDocument();
	public void setHasDocument (IWDocument newDoc);
	public void setHasDocument (String docURI);
    
} /* END OF IWDocumentFragment */
