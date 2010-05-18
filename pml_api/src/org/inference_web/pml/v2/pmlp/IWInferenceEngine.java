/* Copyright 2007 Inference Web Group.  All Rights Reserved. */


package org.inference_web.pml.v2.pmlp;

import java.util.List;

public interface IWInferenceEngine extends IWSoftware {
	
	public List getHasInferenceEngineRule();
	public void setHasInferenceEngineRule(List newRules);
	public void addHasInferenceEngineRule(IWInferenceRule newRule);
	public void addHasInferenceEngineRule(String ruleURI);
    
} /* END OF IWInferenceEngine */
