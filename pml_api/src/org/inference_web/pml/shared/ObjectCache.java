/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.shared;

import java.util.*;
import java.net.*;
import org.inference_web.pml.shared.util.*;
import org.apache.log4j.Logger;

public class ObjectCache {
	
	private static final float loadFactor = 0.75f;
	public static final int defaultCacheSize = Config.defaultCacheSize;
  private int expInterval = Config.defaultCacheExpirationIntevalInMinute;
  
  private Map loadedObjects;

  private int capacity = defaultCacheSize;
  private Calendar time = Calendar.getInstance();
  
  public ObjectCache(int cacheSize) {
  	if (cacheSize > 0) {
  		this.capacity = cacheSize;
  	}
  	int max = (int)Math.ceil(this.capacity / loadFactor) +1;
  	loadedObjects = Collections.synchronizedMap(new LinkedHashMap<URL, Object> (max, loadFactor, true) {
  		@Override protected boolean removeEldestEntry(Map.Entry eldest) {
  			return size() > ObjectCache.this.capacity;
  		}
  	});
  }
  
  public ObjectCache() {
  	this(defaultCacheSize);
  }

  public void put (String uriStr, Object obj) {
  	try {
  		URL uri = new URL (uriStr);
//System.out.println("Model register: add new "+uriStr + obj.getClass());
    	loadedObjects.put(uri, obj);
  	} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e.getMessage());  		
  	}
  }
  public void put (String uriStr, Object obj, boolean overwrite) {
  	try {
  		URL uri = new URL (uriStr);
  		if (overwrite) {
    	loadedObjects.put(uri, obj);
//System.out.println("Model register: overwrite "+uriStr);
  		} else {// not overwrite
  			if (loadedObjects.get(uri) == null) {
  				loadedObjects.put(uri,obj);
//System.out.println("Model register: add non-exist "+uriStr);
  			} else {
  				// found, not overwrite
  			}
  		}
  	} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e.getMessage());  		
  	}
  }
  public void putAll(Map other) {
  	try {
    	loadedObjects.putAll(other);
  	} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e.getMessage());  		
  	}
  }
  
  public Map getAll() {
  	return loadedObjects;
  }
  public Object get(String objectURIString) {
  	Object result = null;
  		try {
  			URL objURI = new URL(objectURIString);
  			expireCache();
  			result = loadedObjects.get(objURI);
  		} catch (Exception e) {
  			Logger.getLogger(this.getClass()).error(e.getMessage());
//  			System.out.println(this.getClass().getName()+": "+e.getMessage());
  			e.printStackTrace();
  		}
//  	} else {
//			Logger.getLogger(this.getClass()).error(" -- get: Invalid URI - "+objectURIString);
//  		System.out.println(this.getClass().getName()+": Invalid URI - "+objectURIString);
//  	}
  	return result;
  }
  
  public Object get(URI objectURI) {
  	
  	Object result = null;
  		expireCache();
  	try {
  		result = loadedObjects.get(objectURI.toURL());
  	} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(" -- get: Invalid URI - "+objectURI.toASCIIString());  		
  	}
  	return result;
  }
  
  public int getCapacity() {
  	return capacity;
  }
  
  public int size() {
  	return loadedObjects.size();
  }
  public boolean setExpirationIntervalInMinutes (int newExpInter) {
  	boolean result = false;
  		if (newExpInter >= 0) { 
  			expInterval = newExpInter;
  			result = true;
  		}
  	return result;
  }
  
  public int getExpirationInterval() {
  	return expInterval;
  }
  
  private void expireCache() {
  	if (expInterval > 0) {
  		Calendar now = Calendar.getInstance(); 
  		now.add(Calendar.MINUTE, -expInterval);
  		if (now.after(time)) {
  			loadedObjects.clear();
  			time = Calendar.getInstance();
  		}
  	} 

  	}
  public void invalidateCache() {
		loadedObjects.clear();
		time = Calendar.getInstance();
  }

}