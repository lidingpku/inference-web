/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;
import java.util.List;

public interface IWAgent extends IWSource {
  public List getIsMemberOf ();
  public void setIsMemberOf(List newList);
  public void addIsMemberOf(IWOrganization newOrg);
  public void addIsMemberOf(String orgURI);
  
  public List getUsesInferenceEngine();
  public void setUsesInferenceEngine(List newList);
  public void addUsesInferenceEngine(IWInferenceEngine newEngine);
  public void addUsesInferenceEngine(String engineURI);
} /* END OF IWAgent */
