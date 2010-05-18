/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

public interface IWDocumentFragmentByRowCol extends IWDocumentFragment {
	
	public String getHasFromCol();
	public void setHasFromCol(String col);
	public String getHasToCol();
	public void setHasToCol(String col);
    
	public String getHasFromRow();
	public void setHasFromRow(String row);
	public String getHasToRow();
	public void setHasToRow(String row);
    
} /* END OF IWDocumentFragmentByRowCol */
