/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.util.List;

public interface IWOrganization extends IWAgent {
	
  public List getHasMember ();
  public void setHasMember(List newList);
  public void addHasMember(IWAgent newMember);
  public void addHasMember(String memberURI);
    
} /* END OF IWOrganization */
