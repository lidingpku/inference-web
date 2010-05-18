/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.util.List;

public interface IWInformation extends IWIdentifiedThing {
	
	public IWLanguage getHasLanguage();
	public void setHasLanguage(IWLanguage newLanguage);
	public void setHasLanguage(String languageURI);
	
	public IWFormat getHasFormat();
	public void setHasFormat(IWFormat newFormat);
	public void setHasFormat(String formatURI);
	
	public IWSourceUsage getHasReferenceSourceUsage();
	public void setHasReferenceSourceUsage(IWSourceUsage newReferenceoSourceUsage);
	
	public List getHasPrettyNameMappingList();
	public void setHasPrettyNameMappingList(List newList);
	
	public String getHasPrettyString ();
	public void setHasPrettyString(String newString);
	
	public String getHasRawString ();
	public void setHasRawString(String newString);
	
	public String getHasURL ();
	public void setHasURL(String newURL);
	
	public List getHasEncoding();
	public void setHasEncoding(List newList);
	public void addHasEncoding(String newEncoding);

	public List getHasMimetype();
	public void setHasMimetype(List newList);
	public void addHasMimetype(String newMime);
	
    
} /* END OF IWInformation */
