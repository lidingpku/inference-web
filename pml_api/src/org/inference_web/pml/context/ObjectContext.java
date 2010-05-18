/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.context;

import java.util.*;

public class ObjectContext extends HashMap {
	/**
	 * Puts an object to the context.
	 * @param name object name
	 * @param dataObj data object
	 */
	public void putObject (String name, DataObject dataObj) {
		if (name != null && !name.trim().equals("")) {
			put(name,dataObj);
		}
	}
	
	/**
	 * Returns a data object in the context by name
	 * @param name object name
	 * @return data object
	 */
	public DataObject getObject (String name) {
		DataObject result = null;
		if (containsKey(name)) {
			result = (DataObject)this.get(name); 
		}
		return result;
	}
	/**
	 * Returns an iterator of the context
	 * @return context iterator
	 */
	public Iterator listObject () {
		Iterator result = null;
		if (this.entrySet() != null) {
			result = entrySet().iterator();
		}
		return result;
	}
}
