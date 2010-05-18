/* Copyright 2009 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.context.accessor;

import java.util.*;
import java.net.URI;
import java.io.*;

import org.inference_web.pml.shared.util.*;
import org.inference_web.pml.shared.*;
import org.inference_web.pml.context.*;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;


public class DataObjectManager  {
  private static int cacheSize = ObjectCache.defaultCacheSize;
  private static int expInterval = Config.defaultCacheExpirationIntevalInMinute;

	public static ObjectCache JenaModelCache = new ObjectCache(cacheSize);
	public static ObjectCache DataObjectCache = new ObjectCache(cacheSize);
	public static ObjectCache propertyTemplateCache = new ObjectCache(cacheSize);
	
	/**
	 * Returns a Jena OntModel identified by a URI
	 * @param URIStr URI
	 * @return Jena OntModel
	 */
  public static OntModel getJenaModel(String URIStr) {
//System.out.println("\nentering getJenaModel with "+URIStr);
  	OntModel result = null;
  	try {
  	String _docURL = URIStr.replaceAll(" ","%20");
    
    if (_docURL.indexOf('#') > -1) {
      _docURL = _docURL.substring(0,_docURL.indexOf('#'));
    }
/*    
      expireCache();
      if (loadedJenaModels.containsKey(_docURL)) {
      	result = (OntModel)loadedJenaModels.get(_docURL);
      } else {      	
      	OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
      	try {
//String s1="--loading jene model "+_docURL+" started at "+ParameterHelper.getCurrentTimeStr2();
      		// dl 2007.02.12
      		tempModel.read(_docURL);
//      		ToolOntologyCache.loadModel(tempModel, _docURL);
      		loadedJenaModels.put(_docURL, tempModel);
//System.out.println(s1+" and ended "+ParameterHelper.getCurrentTimeStr2());
      		result = tempModel;
      	} catch (Exception _e) {
      		System.out.println("DataObjectManager.getJenaModel: Could not read document at " +
            _docURL + "\n" + _e.getMessage());
      		tempModel.close();
      	}
  
      }
*/
     
      Object temp = JenaModelCache.get(_docURL);
      if (temp != null) {
        result = (OntModel)temp;
      } else {
      	OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
//    		OntModel tempModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

      	try {
//String s1="--loading jene model "+_docURL+" started at "+ParameterHelper.getCurrentTimeStr2();
      		// dl 2007.02.12
//System.out.println("\nDataObjectManager: reading ontology "+_docURL);
      		tempModel.read(_docURL);
      		JenaModelCache.put(_docURL, tempModel);
      		Iterator impOnts = tempModel.listImportedOntologyURIs().iterator(); // only the direct ones       		
      		while (impOnts.hasNext()) {
      			String oneOnt = ((String)impOnts.next()).trim();
//System.out.println("check ont "+oneOnt);
						oneOnt = oneOnt.replaceAll(" ","%20");      	    
      	    if (oneOnt.indexOf('#') > -1) {
      	    	oneOnt = oneOnt.substring(0,oneOnt.indexOf('#'));      	      
      	    }
//System.out.println("about to put model in cache not overwrite for "+oneOnt);
        		JenaModelCache.put(oneOnt, tempModel, false); // register, but not overwrite
      		}
//System.out.println(s1+" and ended "+ParameterHelper.getCurrentTimeStr2());
      		result = tempModel;
      	} catch (Exception _e) {
      		System.out.println("DataObjectManager.getJenaModel: Could not read document at " +
            _docURL + "\n");  _e.printStackTrace();
      		tempModel.close();
      	}
      	
      }
  	} catch (Exception e) {
  		e.printStackTrace();
  	}
    return result;
  }

	/**
	 * Returns a Jena OntModel identified by a URI
	 * @param URIStr URI
	 * @return Jena OntModel
	 */

  public static OntModel getJenaModelWithPellet(String URIStr) {
  	OntModel result = null;
  	try {
  	String _docURL = URIStr.replaceAll(" ","%20");
    
    if (_docURL.indexOf('#') > -1) {
      _docURL = _docURL.substring(0,_docURL.indexOf('#'));
      Object temp = JenaModelCache.get(_docURL);
      if (temp != null) {
        result = (OntModel)temp;
      } else {
//      	OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
    		OntModel tempModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

      	try {
      		tempModel.read(_docURL);
      		JenaModelCache.put(_docURL, tempModel);
      		result = tempModel;
      	} catch (Exception _e) {
      		System.out.println("DataObjectManager.getJenaModel: Could not read document at " +
            _docURL + "\n");  _e.printStackTrace();
      		tempModel.close();
      	}
      	
      }
    } else {
      System.out.println("DataObjectManager.getJenaModel: "+ _docURL + " does not have the # sign.");
    }
  	} catch (Exception e) {
  		e.printStackTrace();
  	}
    return result;
  }

	/**
	 * Answers if a document identified by a URI exist
	 * @param URIStr URI
	 * @return Jena OntModel
	 */
  public static boolean documentExists(String URIStr) {
  	boolean result = false;
  	try {
  		String _docURL = URIStr.replaceAll(" ","%20");

  		if (_docURL.indexOf('#') > -1) {
  			_docURL = _docURL.substring(0,_docURL.indexOf('#'));
  			OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
  			try {
  				tempModel.read(_docURL);
  				if (tempModel != null)
  					result = true;
  			} catch (Exception _e) {
  				tempModel.close();
  			}

  		} else {
  			System.out.println("DataObjectManager.getJenaModel: "+ _docURL + " does not have the # sign.");
  		}
  	} catch (Exception e) {
  		e.printStackTrace();
  	}
  	return result;
  }

  /**
   * Returns a Jena OntModel identified by a file URI
   * @param fileURI file URI
   * @return Jena model
   */
  public static OntModel getJenaModelFromFile(String fileURI) {
  	OntModel result = null;
  	String _docURL = fileURI.replaceAll(" ","%20");
/*
    expireCache();
  	if (loadedJenaModels.containsKey(_docURL)) {
  		result = (OntModel)loadedJenaModels.get(_docURL);
  	} else {
  		OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
  		try {        
//  			tempModel.read(_docURL);
    		ToolOntologyCache.loadModel(tempModel, _docURL);
  			loadedJenaModels.put(_docURL, tempModel);
  			result = tempModel;
  		} catch (Exception _e) {
  			System.out.println("DataObjectManager.getJenaModelFromFile: Could not read document at " +
  					_docURL + "\n" + _e.getMessage());
  			tempModel.close();
  		}

  	}
*/
  	Object temp = JenaModelCache.get(_docURL);
  	if (temp != null) {
  		result = (OntModel)temp;		
  	} else {
  		OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
  		try {        
//			tempModel.read(_docURL);
  			ToolOntologyCache.loadModel(tempModel, _docURL);
  			JenaModelCache.put(_docURL, tempModel);
  			result = tempModel;
  		} catch (Exception _e) {
  			System.out.println("DataObjectManager.getJenaModelFromFile: Could not read document at " +
  					_docURL + "\n" + _e.getMessage());
  			tempModel.close();
  		}
  	}
  	return result;
  }
  
  /**
   * Returns a Jena OntModel from a string
   * @param sourceString source string
   * @return Jena model
   */
  public static OntModel getJenaModelFromString(String sourceString) {
  	OntModel result = null;
		OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
  	if (sourceString !=null && sourceString.length()>0) {
  		try { 
  			ByteArrayInputStream inStrm = new ByteArrayInputStream(sourceString.getBytes("UTF-8"));
  			tempModel.read(inStrm, null);
  			result = tempModel;
  		} catch (Exception _e) {
  			System.out.println("DataObjectManager.getJenaModelFromString: Could not read document at " +
  					sourceString + "\n" + _e.getMessage());
  			tempModel.close();
  		}
  	}
  	return result;
  }
  
  /**
   * Returns a Jena OntModel from a Data Object
   * @param dObj Data Object
   * @return Jena model
   */
  public static OntModel getJenaModel(DataObject dObj) {
  	OntModel result = getOntModel(dObj);
  	return result;
  }
  
  /**
   * Returns a Jena OntModel from a Data Object context
   * @param objects Data Object context
   * @return Jena model
   */
  public static OntModel getJenaModel (ObjectContext objects) {
  	OntModel result = null;
  	OntModel outModel  = ModelFactory.createOntologyModel();

  	if (objects != null) {
  		Iterator objIt = objects.values().iterator();
  		while (objIt.hasNext()) {
  			DataObject od = (DataObject)objIt.next();
  			DataObjectManager.addDataObjectToJenaModel(outModel, od);
  		}
  		result = outModel;
  	}    
  	return result;
  }

  /**
   * Returns a Jena Individual identified by a URI from source.
   * @param uri URI
   * @return Jena Individual
   */
  public static Individual getIndividual (String uri) {
  	Individual result = null;
  	OntModel model = getJenaModel(uri);
  	if (model != null) {
      RDFNode _rdf = null;
      
      _rdf = model.getIndividual(uri);
      if (_rdf != null && _rdf.canAs(Individual.class)) {
        result = (Individual) _rdf.as(Individual.class);
//System.out.println("DataObjectManager.getIndividual:getting "+uri+", got "+result.getURI());
      } else { 
        System.out.println("*****DataObjectManager.getIndividual: Could not read individual with URI " + uri);
      }
    }
    return result;
  }
  
  /**
   * Returns a Jena Individual identified by a URI and loaded from a source file.
   * @param uri URI
   * @param sourceFileURL file URL of source
   * @return Jena Individual
   */
  public static Individual getIndividualFromFile (String uri, String sourceFileURL) {
  	Individual result = null;
  	OntModel model = getJenaModelFromFile(sourceFileURL);
  	if (model != null) {
      RDFNode _rdf = null;
      
      _rdf = model.getIndividual(uri);
      if (_rdf != null && _rdf.canAs(Individual.class)) {
        result = (Individual) _rdf.as(Individual.class);
      } else { // take care of relative uri
      	try {
      		_rdf = model.getIndividual((new URI(uri)).getFragment());
      		if (_rdf != null && _rdf.canAs(Individual.class)) {
      			result = (Individual) _rdf.as(Individual.class);
      		} else {      
      			System.out.println("DataObjectManager.getIndividual: Could not read individual with URI " + uri + " from "+sourceFileURL);
      		}
      	} catch (Exception se) {
      		System.out.println("DataObjectManager.getIndividual: Could not read individual with URI " + uri + " from "+sourceFileURL);      		
      	}
      }
    }
    return result;
  }
  
  /**
   * Returns a Jena Individual identified by a URI and loaded from a string
   * @param uri URI
   * @param sourceString source string
   * @return Jena Individual
   */
  public static Individual getIndividualFromString (String uri, String sourceString) {
  	Individual result = null;
  	OntModel model = getJenaModelFromString(sourceString);
  	if (model != null) {
      RDFNode _rdf = null;
      
      _rdf = model.getIndividual(uri);
      if (_rdf != null && _rdf.canAs(Individual.class)) {
        result = (Individual) _rdf.as(Individual.class);
      } else { // take care of relative uri
      	try {
      		_rdf = model.getIndividual((new URI(uri)).getFragment());
      		if (_rdf != null && _rdf.canAs(Individual.class)) {
      			result = (Individual) _rdf.as(Individual.class);
      		} else {      
      			System.out.println("DataObjectManager.getIndividual: Could not read individual with URI " + uri + " from "+sourceString);
      		}
      	} catch (Exception se) {
      		System.out.println("DataObjectManager.getIndividual: Could not read individual with URI " + uri + " from "+sourceString);      		
      	}
      }
    }
    return result;
  }
  
  /**
   * Returns a Jena Individual identified by a URI and loaded from a Jena OntModel
   * @param uri URI
   * @param model source OntModel
   * @return Jena Individual
   */
  public static Individual getIndividual (String uri, OntModel model) {
  	Individual result = null;
  	if (model != null) {
      RDFNode _rdf = null;
      _rdf = model.getIndividual(uri);
      if (_rdf != null && _rdf.canAs(Individual.class)) {
        result = (Individual) _rdf.as(Individual.class);
//System.out.println("DataObjectManager.getIndividual:getting "+uri+", got "+result.getURI());
      } else { 
        System.out.println("DataObjectManager.getIndividual: Could not read individual with URI " + uri);
      }
    }
    return result;
  }

  /**
   * Returns a DataObject identified by a URI. If the object can be found in the object cache,<br>
   * the cached object is returned. Otherwise, the object is loaded from its source and cached.
   * @param dObjURI URI of Data Object
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject getDataObject(String dObjURI,Set<String> preferredOntologyURIs) {
//System.out.println("entering getDataObject:"+dObjURI);
  	DataObject result = null;
  	Object temp = DataObjectCache.get(dObjURI);
  	if (temp != null) {
  		result = (DataObject)temp;
  	} else {
  		result = Reader.loadDataObject(dObjURI,preferredOntologyURIs);
  		if (result != null) {
  			DataObjectCache.put(dObjURI, result);
  		} else {
  			System.out.println("Could not get data object "+dObjURI);
  		}
  	}
  	return result;
  }

  /**
   * Returns a Data Object identified by a URI and loaded from a file if not found from the
   * object cache.
   * @param dObjURI URI of Data Object
   * @param sourceFileURL URL of source file
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject getDataObjectFromFile(String dObjURI, String sourceFileURL,Set<String> preferredOntologyURIs) {
  	DataObject result = null;

  	Object temp = DataObjectCache.get(dObjURI);
  	if (temp != null) {
  		result = (DataObject)temp;
  	} else {
			result = Reader.loadDataObjectFromFile(dObjURI, sourceFileURL, preferredOntologyURIs);
  		if (result != null) {
  			DataObjectCache.put(dObjURI, result);
  		}
  	}  	
  	return result;
  }

  /**
   * Returns a Data Object identified by a URI and loaded from a string if not found from the
   * object cache.
   * @param dObjURI URI of Data Object
   * @param sourceString source string
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject getDataObjectFromString(String dObjURI, String sourceString, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	Object temp = DataObjectCache.get(dObjURI);
  	if (temp != null) {
  		result = (DataObject)temp;
  	} else {
			result = Reader.loadDataObjectFromString(dObjURI, sourceString, preferredOntologyURIs);
  		if (result != null) {
  			DataObjectCache.put(dObjURI, result);
  		}
  	}
  	
  	return result;
  }
  
  /**
   * Returns a Data Object identified by a URI and loaded from a Jena OntModel if not found from the
   * object cache.
   * @param dObjURI URI of Data Object
   * @param model Jena OntModel
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject getDataObject(String dObjURI, OntModel model, Set<String> preferredOntologyURIs) {
  	DataObject result = null;

  	Object temp = DataObjectCache.get(dObjURI);
  	if (temp != null) {
  		result = (DataObject)temp;
  	} else {
  		result = Reader.loadDataObject(dObjURI, model, preferredOntologyURIs);
  		if (result != null) {
  			DataObjectCache.put(dObjURI, result);
  		}
  	}
  	return result;
  }
  
  /**
   * Returns a Data Object identified by a URI and loaded from source directly.
   * @param dObjURI URI of Data Object
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject loadDataObject(String dObjURI, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	result = Reader.loadDataObject(dObjURI, preferredOntologyURIs);  
  	return result;
  }
  
  /**
   * Returns a Data Object identified by a URI and loaded from a file.
   * @param dObjURI URI of Data Object
   * @param sourceFileURL URL of source file
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject loadDataObjectFromFile(String dObjURI, String sourceFileURL, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	result = Reader.loadDataObjectFromFile(dObjURI, sourceFileURL, preferredOntologyURIs);
  	
  	return result;
  }
  
  /**
   * Returns a Data Object identified by a URI and loaded from a string.
   * @param dObjURI URI of Data Object
   * @param sourceString source string
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return Data Object
   */
  public static DataObject loadDataObjectFromString(String dObjURI, String sourceString, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	result = Reader.loadDataObjectFromString(dObjURI, sourceString, preferredOntologyURIs);
  	
  	return result;
  }
  
  /**
   * Returns a Data Object identified by a URI and loaded from a Jena OntModel.
   * @param dObjURI URI of Data Object
   * @param model Jena OntModel
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
  * @return Data Object
   */
  public static DataObject loadDataObject(String dObjURI, OntModel model, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	result = Reader.loadDataObject(dObjURI, model, preferredOntologyURIs);
	
  	return result;
  }
  
  /**
   * Saves a Data Object to a file.
   * @param dObj Data Object
   * @param fileName file name
   * @return true if successful
   */
  public static boolean storeDataObject (DataObject dObj, String fileName) {
    boolean result = false;
    if (dObj != null) {
      OntModel modelPE = getOntModel(dObj);
      result = Writer.toFile(modelPE, fileName);
    }    
    return result;    
  }

 /**
  * Saves all Data Objects in a object context to a file.
  * @param objects Data Objects
  * @param fileName file name
  * @return true if successful
  */
  public static boolean storeObjectCollection (ObjectContext objects, String fileName) {
  	boolean result = false;
  	OntModel outModel  = ModelFactory.createOntologyModel();

  	if (objects != null) {
  		Iterator objIt = objects.values().iterator();
  		while (objIt.hasNext()) {
  			DataObject od = (DataObject)objIt.next();
  			DataObjectManager.addDataObjectToJenaModel(outModel, od);
  		}
  		result = Writer.toFile(outModel, fileName);
  	}    
  	return result;    
  }
  
  /**
   * Copies the content of one Data Object to another.
   * @param fromDObj source Data Object
   * @param toDObj destination Data Object
   */
  public static void copyDataObject (DataObject fromDObj, DataObject toDObj) {
  	if (fromDObj != null && toDObj != null) {
    	toDObj.setClassURI(fromDObj.getClassURI());
    	toDObj.setIdentifier(fromDObj.getIdentifier());
    	HashMap newProperties = new HashMap();
    	newProperties.putAll(fromDObj.getProperties());
    	toDObj.setProperties(newProperties);      
  	}
  }
  
	/**
   * Creates a duplicate Data Object.
   * @param origDObj original Data Object
   * @return a copy of the original Data Object
   */
  public static DataObject getNewInstance (DataObject origDObj) {
    DataObject newDObj = null;    
    if (origDObj != null) {
    	newDObj = new DataObjectImpl();
    	newDObj.setClassURI(origDObj.getClassURI());
    	newDObj.setIdentifier(origDObj.getIdentifier());
    	HashMap newProperties = new HashMap();
    	newProperties.putAll(origDObj.getProperties());

    	newDObj.setProperties(newProperties);      
    }
    return newDObj;
    
  }

  /**
   * Initializes the Data Object cache.
   * @param newCacheSize cache size
   */
  public static void initCaches(int newCacheSize) {
  	if (newCacheSize >0) {
  		cacheSize = newCacheSize;
  		JenaModelCache = new ObjectCache(cacheSize);
  		DataObjectCache = new ObjectCache(cacheSize);
  		propertyTemplateCache = new ObjectCache(cacheSize);  		  		
  	}
  }
  
  /**
   * Returns Data Object cache size.
   * @return cache size
   */
  public static int getCacheSize() {
  	return cacheSize;
  }
  
  /**
   * Sets the Data Object cache expiration interval in minutes.
   * @param newExpInter expiration interval
   * @return true if seccessful
   */
  public static boolean setExpirationIntervalInMinutes (int newExpInter) {
  	boolean success = false;
  		if (newExpInter >= 0) { 
  			expInterval = newExpInter;
  			JenaModelCache.setExpirationIntervalInMinutes(expInterval);
    		DataObjectCache.setExpirationIntervalInMinutes(expInterval);
    		propertyTemplateCache.setExpirationIntervalInMinutes(expInterval);
  			OntologyManager.setExpirationIntervalinMinutes(newExpInter);
  			success = true;
  		}
  	return success;
  }
  
  /**
   * Returns Data Object cache expiration interval in minutes.
   * @return expiration interval
   */
  public static int getExpirationInterval() {
  	return expInterval;
  }

  /**
   * Invalidates the current Data Object cache.
   *
   */
  public static void invalidateCache() {
  	JenaModelCache.invalidateCache();
  	DataObjectCache.invalidateCache();
  	propertyTemplateCache.invalidateCache();
  	OntologyManager.invalidateCache();
  }

  /**
   * Adds a Data Object into a Jena OntModel as an Individual.
   * @param model Jena OntModel
   * @param dObj Data Object
   */
  public static void addDataObjectToJenaModel (OntModel model, DataObject dObj) {
  	String className; 
  	String ontUri;
  	String descNameSpace;

  	if (model != null && dObj != null && dObj.getIdentifier() != null && dObj.getNameSpace()!= null) {
  		className = dObj.getOntologyClassName();
  		ontUri = dObj.getOntologyURI();
  		descNameSpace = dObj.getNameSpace();

  		org.inference_web.pml.context.accessor.Ontology classOntology = OntologyManager.getOntology(dObj.getOntologyURI());
  		org.inference_web.pml.context.accessor.Ontology propOntology = null;


  		Map odlNamespaces = classOntology.getModel().getNsPrefixMap();
  		Iterator pfs = odlNamespaces.keySet().iterator();
  		while (pfs.hasNext()) {
  			String pf = (String)pfs.next();
  			String nsp = (String)odlNamespaces.get(pf);
  			if (!model.getNsPrefixMap().containsKey(pf)) {
  				model.setNsPrefix(pf, nsp);
  			}
  		}
  		Individual ind = model.createIndividual(dObj.getIdentifier().getURIString(), classOntology.getOntClass(ontUri+className));

  		Iterator propertyURIs = classOntology.listPropertyURIs(ontUri+className, false).iterator();

  		while (propertyURIs.hasNext()) {
  			String propName = (String)propertyURIs.next();
  			Object propValue = dObj.getProperty(propName);
  			if (propValue != null ) {
  				propOntology = OntologyManager.getOntology(
  						ResourceFactory.createResource(propName).getNameSpace());
  				OntProperty prop = propOntology.getProperty(propName);
  				if (propValue instanceof List) {
  					Iterator values = ((List)propValue).iterator();
  					while (values.hasNext()) {
  						Object value = values.next();
  						DataObjectManager.addProperty(descNameSpace, model, ind, prop, value);
  					}
  				}	else {
  					DataObjectManager.addProperty(descNameSpace, model,ind,prop,propValue);
  				}
  			}
  		}
  	}   	
  }

	private static void addProperty (String nameSpace, OntModel model, Individual ind, OntProperty property, Object value) {
		boolean isObjProp = false;
		boolean isDataProp = false;
	
		if (model != null && ind != null && property != null && value != null) {
			if (property.isDatatypeProperty()) isDataProp = true;
			if (property.isObjectProperty()) isObjProp = true;
			if (isObjProp) {
	
				if (value instanceof List) {
					Iterator values = ((List)value).iterator();
					while (values.hasNext()) {
						Object currentValue = values.next();
						addProperty(nameSpace, model, ind, property, currentValue);
					}
				}
				if (value instanceof DataObjectImpl) {
					DataObjectImpl valueObject = (DataObjectImpl)value;
					if (valueObject.getIdentifier() != null &&
							valueObject.getIdentifier().getURIString() != null &&
							!valueObject.getIdentifier().getURIString().equals("")) {
						Individual tempInd = model.getIndividual(valueObject.getIdentifier().getURIString());
						if (tempInd != null) {
		  				ind.addProperty(property, model.createResource(valueObject.getIdentifier().getURIString()));  						
						} else {
							if (ToolURI.equalURI(valueObject.getNameSpace(),nameSpace)){
  							Individual propInd = DataObjectManager.getIndividual(nameSpace,model,(DataObject)value);
  							ind.addProperty(property,propInd);							
  						}

  						ind.addProperty(property, model.createResource(valueObject.getIdentifier().getURIString()));  
						}
					} else {
						Individual propInd = DataObjectManager.getIndividual(nameSpace,model,(DataObject)value);
						ind.addProperty(property,propInd);
					}
				}
				if (value instanceof String) {
					ind.addProperty(property, model.createResource((String)value));
				}
			} else if (isDataProp) {
				if (property.getRange() != null && property.getRange().getURI() != null) {
					ind.addProperty(property, model.createTypedLiteral(value, property.getRange().getURI()));
				} else {
					ind.addProperty(property, value.toString());
				}
			}       
		}
	}

	/**
	 * Returns a Jena Individual of a Data Object.
	 * @param nameSpace namespace of the Data Object
	 * @param model Jena OntModel from which the Individual is
	 * @param od Data Object
	 * @return Jena Individual
	 */
	public static Individual getIndividual (String nameSpace, OntModel model, DataObject od){
	   	Individual result = null;
	   	String type ;
	   	
	   	if (model != null && od != null && od.getClassURI() != null) {
	
	   	  org.inference_web.pml.context.accessor.Ontology ontSpec = OntologyManager.getOntology(od.getOntologyURI());
	   	  type = od.getClassURI();
	   	  org.inference_web.pml.context.accessor.Ontology propOntology = null;
	
	   	  Individual ind = null;
	   	  OntClass indClass = ontSpec.getOntClass(type);
	   	  if (od.getIdentifier() != null && 
	   	  		od.getIdentifier().getURIString() != null &&
	   	  		!od.getIdentifier().getURIString().equals("")) {
	// 	  	ind = model.createIndividual(od.getURI(),model.createClass(type));
	   	  	ind = model.createIndividual(od.getIdentifier().getURIString(),indClass);
	   	  } else {
	//   	  	ind = model.createIndividual(model.createClass(type));
	   	  	ind = model.createIndividual(indClass);
	   	  }
	   	  
	   	  Iterator propertyURIs = ontSpec.listPropertyURIs(type, false).iterator();
	   	  
	   	  while (propertyURIs.hasNext()) {
	   	    String propName = (String)propertyURIs.next();
	   	    Object propValue = od.getProperty(propName);
	   	    if (propValue != null ) {
	   	    	propOntology = OntologyManager.getOntology(
	   	    			ResourceFactory.createResource(propName).getNameSpace());
	   	      OntProperty prop = propOntology.getProperty(propName);
	   	      addProperty(nameSpace, model,ind,prop,propValue);
	   	    }
	   	  }
	   	  result = ind;
	   	} else {
	   		if (model == null )System.out.println("DataObjectManager.getIndividual: jena model is null");
	   		if (od == null )System.out.println("DataObjectManager.getIndividual: odl model is null");
	   		if (od.getClassURI() == null )System.out.println("DataObjectManager.getIndividual: odl model type is null");
	   	}
	   	
	    return result;    
	  }

	/**
	 * Returns a Jena OntModel that contains a Data Object's individual.
	 * @param dObj Data Object
	 * @return Jena OntModel
	 */
	public static OntModel getOntModel (DataObject dObj){
	   	OntModel result = null;
	   	String className; 
	   	String ontUri;
	   	String modelNameSpace;
	   	
	   	if (dObj != null && dObj.getIdentifier() != null && dObj.getNameSpace()!= null) {
	   	  className = dObj.getOntologyClassName();
	   	  ontUri = dObj.getOntologyURI();
	   	  modelNameSpace = dObj.getNameSpace();
	   	  
	   	  OntModel model = ModelFactory.createOntologyModel();
	   	  org.inference_web.pml.context.accessor.Ontology classOntology = OntologyManager.getOntology(dObj.getOntologyURI());
	   	  org.inference_web.pml.context.accessor.Ontology propOntology = null;
	   	  
	   	  model.setNsPrefixes(classOntology.getModel().getNsPrefixMap());
	   	  
	   	  Individual ind = model.createIndividual(dObj.getIdentifier().getURIString(), classOntology.getOntClass(ontUri+className));
	//   	  model.createClass(ontUri + className));
	   	  
	   	  Iterator propertyURIs = classOntology.listPropertyURIs(ontUri+className, false).iterator();
	   	  
	   	  while (propertyURIs.hasNext()) {
	   	    String propName = (String)propertyURIs.next();
	   	    Object propValue = dObj.getProperty(propName);
	   	    if (propValue != null ) {
	   	    	propOntology = OntologyManager.getOntology(
	   	    			ResourceFactory.createResource(propName).getNameSpace());
	   	      OntProperty prop = propOntology.getProperty(propName);
	   	      
	  	      if (propValue instanceof List) {
	   	        Iterator values = ((List)propValue).iterator();
	   	        while (values.hasNext()) {
	   	          Object value = values.next();
	   	          addProperty(modelNameSpace, model, ind, prop, value);
	   	        }
	   	      }	else {
	   	      	addProperty(modelNameSpace, model,ind,prop,propValue);
	   	      }
	   	    }
	   	  }
	   	  result = model;
	   	}   	
	    return result;    
	  }
	
	/**
	 * Returns a Data Object's property object. All property values are unset.
	 * @param objectClassURI class URI of the Data Object
	 * @return property Map
	 */
	public static Map getInitProperties (String objectClassURI) {
		Map result = null;
		Map properties = null;

		String ontUri = null;
  	Object temp = propertyTemplateCache.get(objectClassURI);
  	if (temp != null) {
			properties = (Map)temp;
			Iterator propNames = properties.keySet().iterator();
			result = new HashMap();
			while (propNames.hasNext()) {
				String prop = (String)propNames.next();
				result.put(prop, null);
			} 
  	} else {
			Resource resource = ResourceFactory.createResource(objectClassURI);
			ontUri = resource.getNameSpace();
			org.inference_web.pml.context.accessor.Ontology ontology = OntologyManager.getOntology(ontUri);
			ListIterator propNames = ontology.listPropertyURIs(objectClassURI, false).listIterator();
			properties = new HashMap();
			result = new HashMap();
			while (propNames.hasNext()) {
				String prop = (String)propNames.next();
				result.put(prop, null);
				properties.put(prop, null);
			} 
			propertyTemplateCache.put(objectClassURI, properties);
  	}

		return result;
	}
  public static DataObjectIdentifier getObjectID (String objectURI) {
  	DataObjectIdentifier result = null;
  		result = new DataObjectIdentifier(objectURI);
  	return result;
  }


}
/* END OF DataObjectManager */