/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;


import java.util.*;

import org.inference_web.pml.v2.pmlp.*;

/**
 * Models abstraction rules.
 */
public interface IWAbstractionRule extends IWMethodRule {
	
	public List getHasPatternNodeSet();
	public void setHasPatternNodeSet(List nodeSets);
	public void addHasPatternNodeSet(IWNodeSet nodeSet);
	public void addHasPatternNodeSet(String nodeSetURI);

} /* END OF IWAbstractionRule */
