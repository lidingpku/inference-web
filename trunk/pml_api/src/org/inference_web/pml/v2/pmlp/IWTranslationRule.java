/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.io.Serializable;

public interface IWTranslationRule extends IWMethodRule, Serializable  { 
	
	public IWLanguage getHasFromLanguage();
	public void setHasFromLanguage(IWLanguage newLanguage);
	public void setHasFromLanguage(String languageURI);
	public IWLanguage getHasToLanguage();
	public void setHasToLanguage(IWLanguage newLanguage);
	public void setHasToLanguage(String languageURI);
	


} /* END OF IWTranslationRule */
