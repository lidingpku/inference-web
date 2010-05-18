/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.util.List;

public interface IWDocument extends IWSource {
	
	public String getHasAbstract();
	public void setHasAbstract(String newAbstract);
	
	public IWInformation getHasContent();
	public void setHasContent(IWInformation newContent);
	
	public List getHasPublisher();
	public void setHasPublisher(List newList);
	public void addHasPublisher(IWAgent newPublisher);
	public void addHasPublisher(String publisherURI);
	
	public String getHasVersion();
	public void setHasVersion(String newVersion);
	
} /* END OF IWDocument */
