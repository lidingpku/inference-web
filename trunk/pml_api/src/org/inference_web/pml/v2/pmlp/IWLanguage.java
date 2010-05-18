/* Copyright 2007 Inference Web Group.  All Rights Reserved. */


package org.inference_web.pml.v2.pmlp;

import java.util.List;

public interface IWLanguage extends IWIdentifiedThing{
	
	public List getHasEscapeCharacterSequence () ;	
	public void setHasEscapeCharacterSequence (List newEscapeCharacterSequences) ;
	public void addHasEscapeCharacterSequence(String newSequence);

    
} /* END OF IWLanguage */
