/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.util.List;

import org.inference_web.pml.v2.*;

public interface IWPrettyNameMapping extends PMLObject{
	
	public String getHasReplacee () ;
	public void setHasReplacee (String newReplacee) ;
	
	public String getHasShortPrettyName ();
	public void setHasShortPrettyName (String newShortPrettyName);

	public List getHasLongPrettyName() ;
	public void setHasLongPrettyName(List newList) ;
	public void addHasLongPrettyName(String newLongPrettyName);

} /* END OF IWPrettyNameMapping*/
