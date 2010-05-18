/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp;

import java.util.List;

import org.inference_web.pml.v2.*;

public interface IWIdentifiedThing extends PMLObject {
	
	public String getHasName();
	public void setHasName(String newName);
	public String getNamePropertyName () ;

	public String getHasCreationDateTime();
	public void setHasCreationDateTime(String newCreationDateTime);
	
	public List getHasAuthorList();
	public void setHasAuthorList(List newAuthorList);
	public void addHasAuthor (String authorURI);
	public void addHasAuthor (IWAgent author);
    
	public List getHasDescription();
	public void setHasDescription(List newDescriptions);
	public void addHasDescription(IWInformation newDescription);
	
	public IWAgent getHasOwner();
	public void setHasOwner(IWAgent newOwner);
	public void setHasOwner(String ownerURI);
	
} /* END OF IWIdentifiedThing */
