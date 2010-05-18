/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

public interface IWSourceUsage extends IWIdentifiedThing {
  
  public IWSource getHasSource();
  
  public void setHasSource(IWSource newSource);
  public void setHasSource(String sourceURI);
  
  public String getHasUsageDateTime();
  
  public void setHasUsageDateTime(String newUsageTime);
  
  public IWInformation getHasUsageQueryContent();
  
  public void setHasUsageQueryContent(IWInformation newUsageQueryContent);
  
  public String getSourceInfoStringFormat ();
  
  public boolean isSourceDocumentFragment();

  public boolean isV1SourceUsage();
  
  public String getSourceUsageStringForDocumentFragmentSource() ;
  
  public String getV1SourceUsageString ();

  
} /* END OF IWSourceUsage */
