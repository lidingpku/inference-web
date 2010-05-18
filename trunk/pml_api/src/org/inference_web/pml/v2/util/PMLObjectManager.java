/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.util;

import java.lang.reflect.*;

import java.util.*;

import org.inference_web.pml.shared.util.*;
import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.context.DataObjectIdentifier;
import org.inference_web.pml.context.DataObjectImpl;
import org.inference_web.pml.context.accessor.*;
import org.inference_web.pml.shared.Config;
import org.inference_web.pml.shared.ObjectCache;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlj.impl.*;
import org.inference_web.pml.v2.vocabulary.*;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.*;



public class PMLObjectManager extends DataObjectManager {
	
  protected static int expInterval = Config.defaultCacheExpirationIntevalInMinute;
  protected static int cacheSize = Config.defaultCacheSize;
	public static ObjectCache nodeSetCache = new ObjectCache(cacheSize);
	public static Map<String, IWNodeSet>partialNodeSetCache = new HashMap<String, IWNodeSet>();
	public static ObjectCache queryCache = new ObjectCache(cacheSize);

  protected PMLObjectManager() {
  }
  
  /**
   * Returns an instance of NodeSet identified by an URI with specified magnitude.
   * If the NodeSet is already in cache, no new instance is created. All root and<br>
   * antecedent nodesets within the depth of magnitude are loaded.
   * @param nsURI URI of the NodeSet
   * @param magnitude depth of the NodeSet
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI, int magnitude) {
  	IWNodeSet result = null;
  	IWNodeSetImpl tempNs = null;
  	if (nsURI != null && !nsURI.equals("") && magnitude >= Config.PARTIAL_LOADING) {
  		Object temp = nodeSetCache.get(nsURI);
  		if (temp != null) {
  			tempNs = (IWNodeSetImpl)temp;
  			if (tempNs.getDepth() >= magnitude) {
  				result = tempNs;
//System.out.println("getNodeSet: "+nodeSetCache.size()+" - from cache "+nsURI);
  			} else {
  				tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude);
  				if (tempNs != null) {
  					result = tempNs;
  				}
  			}
  		} else {
  			tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude);
  			if (tempNs != null) {
  				result = tempNs;
  			}
  		}
  	}
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by an URI with specified magnitude.<br>
   * The NodeSet is created from the source file if it is not already in cache.<br>
   * All root and antecedent nodesets within the depth of magnitude are loaded.
   * @param nsURI URI of the NodeSet
   * @param magnitude depth of the NodeSet
   * @param sourceFileName name of the source file
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI, int magnitude, String sourceFileName) {
  	IWNodeSet result = null;
  	IWNodeSetImpl tempNs = null;
  	if (nsURI != null && !nsURI.equals("") && magnitude >= Config.PARTIAL_LOADING) {
  		
  		Object temp = nodeSetCache.get(nsURI);
  		if (temp != null) {
  			tempNs = (IWNodeSetImpl)temp;
  			if (tempNs.getDepth() >= magnitude) {
  				result = tempNs;
  			} else {
  				tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude, sourceFileName);
  				if (tempNs != null) {
  					result = tempNs;
  				}
  			}
  		} else {
  			tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude, sourceFileName);
  			if (tempNs != null) {
  				result = tempNs;
  			}
  		}
  	}
  	return result;  	
  }
  
  /**
   * Returns an instance of NodeSet identified by an URI with specified magnitude.<br>
   * The NodeSet is created from the Jena model if it is not already in cache.<br>
   * All root and antecedent nodesets within the depth of magnitude are loaded.
   * @param nsURI URI of the NodeSet
   * @param magnitude depth of the NodeSet
   * @param model Jena model
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI, int magnitude, OntModel model) {
  	IWNodeSet result = null;
  	IWNodeSetImpl tempNs = null;
  	if (nsURI != null && !nsURI.equals("") && magnitude >= Config.PARTIAL_LOADING) {
  		Object temp = nodeSetCache.get(nsURI);
  		if (temp != null) {
  			tempNs = (IWNodeSetImpl)temp;
  			if (tempNs.getDepth() >= magnitude) {
  				result = tempNs;
  			} else {
  				tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude, model);
  				if (tempNs != null) {
  					result = tempNs;
  				}
  			}
  		} else {
  			tempNs = (IWNodeSetImpl)loadNodeSet(nsURI, magnitude, model);
  			if (tempNs != null) {
  				result = tempNs;
  			}
  		}
  	}
  	return result;  	
  }
  /**
   * Returns an instance of NodeSet identified by the URI. The magnitude of the <br>
   * NodeSet is assumed 1.
   * @param nsURI URI of the NodeSet
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI) {
  	IWNodeSet result = getNodeSet(nsURI,Config.PARTIAL_LOADING);
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by the URI, and is read from the specified file if it 
   * not already in cache. The magnitude of the NodeSet is assumed 1.
   * @param nsURI URI of the NodeSet
   * @param sourceFileName name of the source file
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI, String sourceFileName) {
  	IWNodeSet result = getNodeSet(nsURI,Config.PARTIAL_LOADING, sourceFileName);
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by the URI, and read from a Jena Model if it is not
   * already in cache. The magnitude of the NodeSet is assumed 1.
   * @param nsURI URI of the NodeSet
   * @param model Jena model
   * @return NodeSet instance
   */
  public static IWNodeSet getNodeSet (String nsURI, OntModel model) {
  	IWNodeSet result = getNodeSet(nsURI,Config.PARTIAL_LOADING, model);
  	return result;
  }
  
   
  /**
   * Returns an instance of NodeSet identified by URI with specified magnitude. The content of the<br>
   * nodeset and all antecedents within the depth of magnitude are read <br>
   * directly from source bypassing the cache.
   * @param nsURI URI of NodeSet
   * @param magnitude the depth of NodeSet to be loaded
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI, int magnitude) {
  	int idx = -1;
  	IWNodeSet result = null;
  	DataObject nsBasic = null;
  	IWNodeSet ns = null;
//System.out.println("PMLObjectManager.loadNodeSet: pmlmodel loading...");
  	nsBasic = DataObjectManager.loadDataObject(nsURI, Config.pmlOntologySet);
  	if (nsBasic != null) {
  		ns = new IWNodeSetImpl();
//System.out.println("PMLObjectManager.loadNodeSet:"+nodeSetCache.size()+" - " +nsURI+" loaded.");
  	DataObjectManager.copyDataObject(nsBasic, (DataObject)ns);
//System.out.println("PMLObjectManager.loadNodeSet: iwnodeset converted.");
  	partialNodeSetCache.put(nsURI, ns);
  	List infSteps = ns.getInferenceSteps();
  	if (infSteps != null && infSteps.size()>0) {
  		// order inf steps
  		int infStepSize = infSteps.size();
  		IWInferenceStep[] tempSteps = new IWInferenceStepImpl[infStepSize];
  		Stack tempIS = new Stack(); 
  		for (int i=0; i<infStepSize; i++) {
  			IWInferenceStep infStep = new IWInferenceStepImpl();
  			DataObjectManager.copyDataObject((DataObject)infSteps.get(i),(DataObject)infStep);
  			idx = -1;
  			if (infStep.getPropertyByLocalName(PMLJ.hasIndex_lname) != null){
  				idx = Integer.parseInt((String)infStep.getPropertyByLocalName(PMLJ.hasIndex_lname));
  			}
  			if (idx>=0 && idx<infStepSize && tempSteps[idx]==null) {
  				tempSteps[idx]= infStep; 
  			} else {
  				tempIS.push(infStep); 
  			}
  		}
  		if (tempIS.size()>0) {
  			for (int i = 0; i< infStepSize; i++) {
  				if (tempSteps[i]==null) {
  					tempSteps[i]=(IWInferenceStep)tempIS.pop();
  					tempSteps[i].setPropertyByLocalName(PMLJ.hasIndex_lname, Integer.toString(i));
  				}
  			}
  		}
  		ArrayList newSteps = new ArrayList(infStepSize);
			for (int i = 0; i< infStepSize; i++) {
					newSteps.add(tempSteps[i]);
			}  		
  		ns.setPropertyByLocalName(PMLJ.isConsequentOf_lname, newSteps);
    	if (magnitude > 1) {
    		infSteps = newSteps;
    		for (int i=0; i<newSteps.size(); i++) {
    			IWInferenceStep infStep = (IWInferenceStep)newSteps.get(i);
    			String antePropName = infStep.getAntecedentPropertyName();
    			Object anteTest = infStep.getProperty(antePropName);
    			if (anteTest != null) {
    				List newAntes = new ArrayList();
    				if (antePropName.indexOf(PMLJ.hasAntecedentList_lname)>=0) { // v2
//  					begin ante list
  						DataObject list = null;
  						if (anteTest instanceof String) {
  							list = getDataObject((String)anteTest, Config.pmlOntologySet);
  						} else {
  							list = (DataObject) anteTest;
  						}
    					while (list!=null){

    						Object list_first = list.getProperty( PMLDS.first.getURI());
    						if (null!= list_first){
    							IWNodeSet newAnte= null;
    							if (list_first instanceof String) {  
    								String tempAnteUri = (String)list_first;
    								Object tempAnte = nodeSetCache.get(tempAnteUri);
    								if ( tempAnte != null) {
    									newAnte = (IWNodeSet)tempAnte;
    								} else {
    									tempAnte = partialNodeSetCache.get(tempAnteUri);
    									if (tempAnte != null) {
    										newAnte = (IWNodeSet)tempAnte;
    									} else {
        								newAnte = loadNodeSet( (String)list_first, magnitude-1);    										
    									}
    								}
    							}else if (list_first instanceof DataObject) {    					
    								newAnte = buildNodeSet((DataObject)list_first, magnitude-1);
    							}
    							if (null!= newAnte)
    								newAntes.add(newAnte);
    						}    					
    						Object list_rest= list.getProperty(PMLDS.rest.getURI());
    						if (null!= list_rest){
    							if (list_rest instanceof String) {    					
    								list = (DataObject) getDataObject((String)list_rest, Config.pmlOntologySet);
    							}else if (list_rest instanceof DataObject) {    					
    								list = (DataObject) list_rest;
    							}
    						}else
    							list = null;
    					}
//    				end ante list

    				} else { // v1
      				List	antes = (List)anteTest;
      				if (antes.size()>0 ) {
      					int size = antes.size();
      					for (int j=0; j<size; j++) {
      						Object anteObj = antes.get(j);
      						if (anteObj != null) {
      							IWNodeSet anteNode = null;    					
      							if (anteObj instanceof String) {    
      								String tempAnteUri = (String)anteObj;
      								Object tempAnte = nodeSetCache.get(tempAnteUri);
      								if ( tempAnte != null) {
      									anteNode = (IWNodeSet)tempAnte;
      								} else {
      									tempAnte = partialNodeSetCache.get(tempAnteUri);
      									if (tempAnte != null) {
      										anteNode = (IWNodeSet)tempAnte;
      									} else {
          								anteNode = loadNodeSet((String)anteObj, magnitude-1);
      									}
      								}
      							}
      							if (anteObj instanceof DataObject) {    					
      								anteNode = buildNodeSet((DataObject)anteObj, magnitude-1);
      							}
      							if (anteNode != null) {
      								newAntes.add(anteNode);
      							}
      						}
      					}
      				}
      			}
      			if (newAntes.size()>0) infStep.setProperty(antePropName, newAntes);
    			}
    		}
    	}
  	}
  	ns.setDepth(magnitude);
  	partialNodeSetCache.remove(nsURI);
		cacheNodeSet(ns);
  	result = ns;
  	}
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by the URI with specified magnitude. From the root<br>
   * nodeset to all antecedents within the depth of magnitude are loaded from the <br>
   * specified file. This operation bypasses the<br>
   * cache.
   * @param nsURI URI of NodeSet
   * @param magnitude depth of NodeSet
   * @param sourceFileURL URL of the source file
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI, int magnitude, String sourceFileURL) {
  	IWNodeSet result = null;
  	if ( sourceFileURL != null) {
  		OntModel model = DataObjectManager.getJenaModelFromFile(sourceFileURL);
  		if (model != null) {
  			result = loadNodeSet (nsURI, magnitude, model);
  		}
  	}
  	return result;
  }
  /**
   * Returns an instance of NodeSet identified by the URI with specified magnitude. From the root<br>
   * nodeset to all antecedents within the depth of magnitude are read from the Jena model<br> 
   * bypassing cache.
   * @param nsURI URI of NodeSet
   * @param magnitude depth of the NodeSet
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI, int magnitude, OntModel model) {
  	int idx = -1;
  	IWNodeSet result = null;
  	DataObject nsBasic = null;
  	IWNodeSet ns = null;
//System.out.println("PMLObjectManager.loadNodeSet: pmlmodel loading...");
//  	nsBasic = ObjectDescriptionManager.loadObjectDescriptionFromURI(nsURI);
  	nsBasic = DataObjectManager.getDataObject(nsURI, model, Config.pmlOntologySet);
  	if (nsBasic != null) {
  		ns = new IWNodeSetImpl();
//System.out.println("PMLObjectManager.loadNodeSet: ns "+nsURI+" loaded.");
  	DataObjectManager.copyDataObject(nsBasic, (DataObject)ns);
  	ns = (IWNodeSet)getPMLObject(ns);
  	partialNodeSetCache.put(nsURI, ns);
//System.out.println("PMLObjectManager.loadNodeSet: iwnodeset converted.");
  	List infSteps = ns.getInferenceSteps();
  	if (infSteps != null && infSteps.size()>0) {
  		// order inf steps
  		int infStepSize = infSteps.size();
  		IWInferenceStep[] tempSteps = new IWInferenceStepImpl[infStepSize];
  		Stack tempIS = new Stack(); 
  		for (int i=0; i<infStepSize; i++) {
  			IWInferenceStep infStep = new IWInferenceStepImpl();
  			DataObjectManager.copyDataObject((DataObject)infSteps.get(i),(DataObject)infStep);
  			idx = -1;
  			if (infStep.getPropertyByLocalName(PMLJ.hasIndex_lname) != null){
  				idx = Integer.parseInt((String)infStep.getPropertyByLocalName(PMLJ.hasIndex_lname));
  			}
  			if (idx>=0 && idx<infStepSize && tempSteps[idx]==null) {
  				tempSteps[idx]= infStep; 
  			} else {
  				tempIS.push(infStep); 
  			}
  		}
  		if (tempIS.size()>0) {
  			for (int i = 0; i< infStepSize; i++) {
  				if (tempSteps[i]==null) {
  					tempSteps[i]=(IWInferenceStep)tempIS.pop();
  					tempSteps[i].setPropertyByLocalName(PMLJ.hasIndex_lname, Integer.toString(i));
  				}
  			}
  		}
  		ArrayList newSteps = new ArrayList(infStepSize);
			for (int i = 0; i< infStepSize; i++) {
					newSteps.add(tempSteps[i]);
			}  		
  		ns.setPropertyByLocalName(PMLJ.isConsequentOf_lname, newSteps);
    	if (magnitude > 1) {
    		infSteps = newSteps;
    		for (int i=0; i<infStepSize; i++) {
    			IWInferenceStep infStep = (IWInferenceStep)newSteps.get(i);
    			String antePropName = infStep.getAntecedentPropertyName();
    			Object anteTest = infStep.getProperty(antePropName);

    			if (anteTest != null) {
  					List newAntes = new ArrayList();
      			if (antePropName.indexOf(PMLJ.hasAntecedentList_lname)>=0) { // v2

//					begin ante list
  						DataObject list = null;
  						if (anteTest instanceof String) {
  							list = getDataObject((String)anteTest, Config.pmlOntologySet);
  						} else {
  							list = (DataObject) anteTest;
  						}
    				while (list!=null){

    					Object list_first = list.getProperty( PMLDS.first.getURI());
    					if (null!= list_first){
    						IWNodeSet newAnte= null;
    						if (list_first instanceof String) {    					
  								String tempAnteUri = (String)list_first;
  								Object tempAnte = nodeSetCache.get(tempAnteUri);
  								if ( tempAnte != null) {
  									newAnte = (IWNodeSet)tempAnte;
  								} else {
  									tempAnte = partialNodeSetCache.get(tempAnteUri);
  									if (tempAnte != null) {
  										newAnte = (IWNodeSet)tempAnte;
  									} else {
  	    							newAnte = loadNodeSet( (String)list_first, magnitude-1, model);
  									}
  								}
    						}else if (list_first instanceof DataObject) {    					
    							newAnte = buildNodeSet((DataObject)list_first, magnitude-1, model);
    						}
    						if (null!= newAnte)
    							newAntes.add(newAnte);
    					}    					

    					Object list_rest= list.getProperty(PMLDS.rest.getURI());
    					if (null!= list_rest){
    						if (list_rest instanceof String) {    					
    							list = (DataObject) getDataObject((String)list_rest, model, Config.pmlOntologySet);
    						}else if (list_rest instanceof DataObject) {    					
    							list = (DataObject) list_rest;
    						}
    					}else
    						list = null;
    				}

//    			end ante list
      			} else {
      				List antes = (List)anteTest;
      				if (antes != null && antes.size()>0 ) {
      					int size = antes.size();
      					for (int j=0; j<size; j++) {
      						Object anteUri = antes.get(j);
      						if (anteUri != null) {
      							IWNodeSet anteNode = null;    					
      							if (anteUri instanceof String) {    					
      								String tempAnteUri = (String)anteUri;
      								Object tempAnte = nodeSetCache.get(tempAnteUri);
      								if ( tempAnte != null) {
      									anteNode = (IWNodeSet)tempAnte;
      								} else {
      									tempAnte = partialNodeSetCache.get(tempAnteUri);
      									if (tempAnte != null) {
      										anteNode = (IWNodeSet)tempAnte;
      									} else {
          								anteNode = loadNodeSet( (String)anteUri, magnitude-1, model);
      									}
      								}

      							}
      							if (anteUri instanceof DataObject) {    					
      								anteNode = buildNodeSet((DataObject)anteUri, magnitude-1, model);
      							}
      							if (anteNode != null) {
      								newAntes.add(anteNode);
      							}
      						}
      					}
      				}
      			}
      			if (newAntes.size()>0) infStep.setProperty(antePropName, newAntes);      			
    			}
    		}
    	}
    	ns.setDepth(magnitude);
    	partialNodeSetCache.remove(nsURI);
			cacheNodeSet( ns);
    	result = ns;
  	}
  }
  	return result;
  }
  
  
  private static IWNodeSet buildNodeSet (DataObject rawDesc, int magnitude) {
  	int idx = -1;
  	IWNodeSet result = null;
  	DataObject nsBasic = null;
  	IWNodeSet ns = null;
  	if (rawDesc != null) { 		
  		ns = new IWNodeSetImpl();
//System.out.println("PMLObjectManager.buildNodeSet: "+rawDesc.getIdentifier().getURIString()+" from dataobject");
  	nsBasic = rawDesc;
//System.out.println("PMLObjectManager.loadNodeSet: pmlmodel loaded.");
  	DataObjectManager.copyDataObject(nsBasic, (DataObject)ns);
  	partialNodeSetCache.put(rawDesc.getIdentifier().getURIString(), ns);
//System.out.println("PMLObjectManager.loadNodeSet: iwnodeset converted.");
  	List infSteps = ns.getInferenceSteps();
  	if (infSteps != null && infSteps.size()>0) {
  		// order inf steps
  		int infStepSize = infSteps.size();
  		IWInferenceStep[] tempSteps = new IWInferenceStepImpl[infStepSize];
  		Stack tempIS = new Stack(); 
  		for (int i=0; i<infStepSize; i++) {
  			IWInferenceStep infStep = new IWInferenceStepImpl();
  			DataObjectManager.copyDataObject((DataObject)infSteps.get(i),(DataObject)infStep);
  			idx = -1;
  			if (infStep.getPropertyByLocalName(PMLJ.hasIndex_lname) != null){
  				idx = Integer.parseInt((String)infStep.getPropertyByLocalName(PMLJ.hasIndex_lname));
  			}
  			if (idx>=0 && idx<infStepSize && tempSteps[idx]==null) {
  				tempSteps[idx]= infStep; 
  			} else {
  				tempIS.push(infStep); 
  			}
  		}
  		if (tempIS.size()>0) {
  			for (int i = 0; i< infStepSize; i++) {
  				if (tempSteps[i]==null) {
  					tempSteps[i]=(IWInferenceStep)tempIS.pop();
  					tempSteps[i].setPropertyByLocalName(PMLJ.hasIndex_lname, Integer.toString(i));
  				}
  			}
  		}
  		ArrayList newSteps = new ArrayList(infStepSize);
			for (int i = 0; i< infStepSize; i++) {
					newSteps.add(tempSteps[i]);
			}  		

  		ns.setPropertyByLocalName(PMLJ.isConsequentOf_lname, newSteps);
    	if (magnitude > 1) {
    		infSteps = newSteps;
    		for (int i=0; i<newSteps.size(); i++) {
    			IWInferenceStep infStep = (IWInferenceStep)newSteps.get(i);
    			String antePropName = infStep.getAntecedentPropertyName();
    			Object anteTest = infStep.getProperty(antePropName);
    			List antes = null;
    			if (anteTest != null) {
    				antes = (List)anteTest;
    			}
    			if (antes != null && antes.size()>0 ) {
    				int size = antes.size();
    				List newAntes = new ArrayList();
    				for (int j=0; j<size; j++) {
    					Object anteObj = antes.get(j);
    					if (anteObj != null) {
    						IWNodeSet anteNode = null;    					
    						if (anteObj instanceof String) {    					
  								String tempAnteUri = (String)anteObj;
  								Object tempAnte = nodeSetCache.get(tempAnteUri);
  								if ( tempAnte != null) {
  									anteNode = (IWNodeSet)tempAnte;
  								} else {
  									tempAnte = partialNodeSetCache.get(tempAnteUri);
  									if (tempAnte != null) {
  										anteNode = (IWNodeSet)tempAnte;
  									} else {
  	    							anteNode = loadNodeSet((String)anteObj, magnitude-1);
  									}
  								}
    						}
    						if (anteObj instanceof DataObject) {    					
    							anteNode = buildNodeSet((DataObject)anteObj, magnitude-1);
    						}
    						if (anteNode != null) {
    						 newAntes.add(anteNode);
    						}
    					}
    				}
    				infStep.setProperty(antePropName, newAntes);
    			}
    		}
    	}
    	ns.setDepth(magnitude);
    	partialNodeSetCache.remove(rawDesc.getIdentifier().getURIString());
    	cacheNodeSet(ns);
    	result = ns;
  	}
  	}
  	return result;
  }
  
  
  private static IWNodeSet buildNodeSet (DataObject rawModel, int magnitude, OntModel model ) {
  	int idx = -1;
  	IWNodeSet result = null;
  	DataObject nsBasic = null;
  	IWNodeSet ns = null;
  	if (rawModel != null) {
  		ns = new IWNodeSetImpl();
  	
//System.out.println("PMLObjectManager.loadNodeSet: pmlmodel loading...");
  	nsBasic = rawModel;
//System.out.println("PMLObjectManager.loadNodeSet: pmlmodel loaded.");
  	DataObjectManager.copyDataObject(nsBasic, (DataObject)ns);
  	partialNodeSetCache.put(nsBasic.getIdentifier().getURIString(), ns);
//System.out.println("PMLObjectManager.loadNodeSet: iwnodeset converted.");
  	List infSteps = ns.getInferenceSteps();
  	if (infSteps != null && infSteps.size()>0) {
  		// order inf steps
  		int infStepSize = infSteps.size();
  		IWInferenceStep[] tempSteps = new IWInferenceStepImpl[infStepSize];
  		Stack tempIS = new Stack(); 
  		for (int i=0; i<infStepSize; i++) {
  			IWInferenceStep infStep = new IWInferenceStepImpl();
  			DataObjectManager.copyDataObject((DataObject)infSteps.get(i),(DataObject)infStep);
  			idx = -1;
  			if (infStep.getPropertyByLocalName(PMLJ.hasIndex_lname) != null){
  				idx = Integer.parseInt((String)infStep.getPropertyByLocalName(PMLJ.hasIndex_lname));
  			}
  			if (idx>=0 && idx<infStepSize && tempSteps[idx]==null) {
  				tempSteps[idx]= infStep; 
  			} else {
  				tempIS.push(infStep); 
  			}
  		}
  		if (tempIS.size()>0) {
  			for (int i = 0; i< infStepSize; i++) {
  				if (tempSteps[i]==null) {
  					tempSteps[i]=(IWInferenceStep)tempIS.pop();
  					tempSteps[i].setPropertyByLocalName(PMLJ.hasIndex_lname, Integer.toString(i));
  				}
  			}
  		}
  		ArrayList newSteps = new ArrayList(infStepSize);
			for (int i = 0; i< infStepSize; i++) {
					newSteps.add(tempSteps[i]);
			}  		
  		ns.setPropertyByLocalName(PMLJ.isConsequentOf_lname, newSteps);
    	if (magnitude > 1) {
    		infSteps = newSteps;
    		for (int i=0; i<newSteps.size(); i++) {
    			IWInferenceStep infStep = (IWInferenceStep)newSteps.get(i);
    			String antePropName = infStep.getAntecedentPropertyName();
    			Object anteTest = infStep.getProperty(antePropName);
    			List antes = null;
    			if (anteTest != null) {
    				antes = (List)anteTest;
    			}
    			if (antes != null && antes.size()>0 ) {
    				int size = antes.size();
    				List newAntes = new ArrayList();
    				for (int j=0; j<size; j++) {
    					Object anteObj = antes.get(j);
    					if (anteObj != null) {
    						IWNodeSet anteNode = null;    					
    						if (anteObj instanceof String) {    					
  								String tempAnteUri = (String)anteObj;
  								Object tempAnte = nodeSetCache.get(tempAnteUri);
  								if ( tempAnte != null) {
  									anteNode = (IWNodeSet)tempAnte;
  								} else {
  									tempAnte = partialNodeSetCache.get(tempAnteUri);
  									if (tempAnte != null) {
  										anteNode = (IWNodeSet)tempAnte;
  									} else {
  	    							anteNode = loadNodeSet((String)anteObj, magnitude-1,model);
  									}
  								}
    						}
    						if (anteObj instanceof DataObject) {    					
    							anteNode = buildNodeSet((DataObject)anteObj, magnitude-1,model);
    						}
    						if (anteNode != null) {
    						 newAntes.add(anteNode);
    						}
    					}
    				}
    				infStep.setProperty(antePropName, newAntes);
    			}
    		}
    	}
    	ns.setDepth(magnitude);
    	partialNodeSetCache.remove(nsBasic.getIdentifier().getURIString());
    	cacheNodeSet(ns);
    	result = ns;
  	}
  	}
  	return result;
  }  

  /**
   * Returns an instance of NodeSet identified by the URI with magnitude of 1.<br>
   * Its content is read directly from source, bypassing the cache.<br>
   * Its antecedents, if any, are not loaded (remain in URI form).
   * @param nsURI URI of NodeSet
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI) {
  	IWNodeSet result = loadNodeSet(nsURI,Config.PARTIAL_LOADING);
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by the URI with magnitude of 1.<br>
   * Its content is read directly from the Jena model, bypassing the cache. <br>
   * Its antecedents, if any, are not loaded (remain in URI form).
   * @param nsURI URI of NodeSet
   * @param model Jena model
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI, OntModel model) {
  	IWNodeSet result = loadNodeSet(nsURI,Config.PARTIAL_LOADING, model);
  	return result;
  }
  
  /**
   * Returns an instance of NodeSet identified by the URI with magnitude of 1.<br>
   * Its content is read directly from the source file, bypassing the cache.
   * Its antecedents, if any, are not loaded (remain in URI form).
   * @param nsURI URI of NodeSet
   * @param sourceFileURL the URL of the source file
   * @return NodeSet instance
   */
  public static IWNodeSet loadNodeSet (String nsURI, String sourceFileURL) {
  	IWNodeSet result = loadNodeSet(nsURI,Config.PARTIAL_LOADING, sourceFileURL);
  	return result;
  }
    
  /**
   * Returns an instance of Query identified by an URI. It is loaded from its source if it is
   * not already in cache.
   * @param qrURI Query's URI
   * @return Query instance
   */
  public static IWQuery getQuery (String qrURI) {
  	IWQuery result = null;
  	if (qrURI != null && !qrURI.equals("")) {
  		Object temp = nodeSetCache.get(qrURI);
  		if (temp != null) {
  			result = (IWQueryImpl)temp;
  		} else {
  			result = (IWQueryImpl)loadQuery(qrURI);
  		}
  		
  	}
  	return result;
  }
  
  /**
   * Returns an instance of Query idenfied by an URI. The Query is loaded from a
   * specific file if it is not already in cache.
   * @param qrURI query's URI
   * @param sourceFileURL URL of the file
   * @return Query instance
   */
  public static IWQuery getQuery (String qrURI, String sourceFileURL) {
  	IWQuery result = null;
  	IWQueryImpl tempQr = null;
  	if (qrURI != null && !qrURI.equals("")) {
  		Object temp = nodeSetCache.get(qrURI);
  		if (temp != null) {
  			result = (IWQueryImpl)temp;
  		} else {
  			result = (IWQueryImpl)loadQuery(qrURI, sourceFileURL);
  		}
  		
  	}
  	return result;
  }
  
  /**
   * Returns an instance of Query identified by an URI. It is loaded from a Jena OntModel
   * if it is not already in cache.
   * @param qrURI query's URI
   * @param model Jena model
   * @return Query instance
   */
  public static IWQuery getQuery (String qrURI, OntModel model) {
  	IWQuery result = null;
  	IWQueryImpl tempQr = null;
  	if (qrURI != null && !qrURI.equals("")) {
  		Object temp = nodeSetCache.get(qrURI);
  		if (temp != null) {
  			result = (IWQueryImpl)temp;
  		} else {
  			result = (IWQueryImpl)loadQuery(qrURI, model);
  		}
  	}
  	return result;
  }
  
  /**
   * Returns an instance of Query identified by an URI and read directly from source.
   * @param qrURI Query's URI
   * @return Query instance
   */
  public static IWQuery loadQuery (String qrURI) {
  	IWQuery result = null;
  	DataObject qrBasic = null;
  	IWQuery query = new IWQueryImpl();
  	qrBasic = DataObjectManager.getDataObject(qrURI, Config.pmlOntologySet);
  	if (qrBasic != null) {
  		DataObjectManager.copyDataObject(qrBasic,(DataObject)query );
  		queryCache.put(qrURI, query);
  		result = query;
  	}
  	return result;
  }
  
  /**
   * Returns an instance of Query identified by an URI and loaded from the specified file.
   * @param qrURI
   * @param sourceFileURL URL of the file
   * @return Query instance
   */
  public static IWQuery loadQuery (String qrURI, String sourceFileURL) {
  	IWQuery result = null;
  	DataObject qrBasic = null;
  	IWQuery query = new IWQueryImpl();
  	qrBasic = DataObjectManager.getDataObjectFromFile(qrURI, sourceFileURL, Config.pmlOntologySet);
  	if (qrBasic != null) {
  		DataObjectManager.copyDataObject(qrBasic,(DataObject)query );
  		queryCache.put(qrURI, query);
  		result = query;
  	}
  	return result;
  }
  
  /**
   * Returns an instance of Query identified by an URI and loaded from a Jena model.
   * @param qrURI URI of the Query
   * @param model Jena model
   * @return Query instance
   */
  public static IWQuery loadQuery (String qrURI, OntModel model) {
  	IWQuery result = null;
  	DataObject qrBasic = null;
  	IWQuery query = new IWQueryImpl();
  	qrBasic = DataObjectManager.getDataObject(qrURI, model, Config.pmlOntologySet);
  	if (qrBasic != null) {
  		DataObjectManager.copyDataObject(qrBasic,(DataObject)query );
  		queryCache.put(qrURI, query);
  		result = query;
  	}
  	return result;
  }

  
  
  
  /**
   * Returns a list of PML objects as the value of a property whose range is
   * a PML List.
   * @param value value as DataObject
   * @return List of PML objects
   */
  public static List getPMLListObjectPropertyValue (DataObject value) {
  	List result = null;
  	if (value != null) {
  		OntClass valCls = value.getOntologyClass();
  		if (valCls.equals(Config.PMLListCls) || value.getOntology().isSubClassOf(valCls, Config.PMLListCls)) {
  			List values = new ArrayList();
    		while (value!=null){
    			Object list_first = value.getProperty( PMLDS.first.getURI());
    			if (null!= list_first){
    				Object firstObj = PMLObjectManager.getPMLObject(list_first);
    				if (firstObj != null) {
    					values.add(firstObj);
    				}
    			}    					

    			Object list_rest= value.getProperty(PMLDS.rest.getURI());
    			if (null!= list_rest) {
    				if (list_rest instanceof String) {    					
    					value = (DataObject) getDataObject((String)list_rest, Config.pmlOntologySet);
    				} else if (list_rest instanceof DataObject) {    					
    					value = (DataObject) list_rest;
    				}
    			} else {						
    				value = null;
    			}
    		}
    		if (values.size()>0) {
    			result = values;
    		}
  		}
  	}
  	return result;
  }
  
  /**
   * Returns a PML object identified by URI.
   * @param URIStr URI of PML object
   * @return PML object
   */
  public static Object getPMLObjectFromURI(String URIStr) {
  	Object result = null;
  	DataObject od = getDataObject(URIStr, Config.pmlOntologySet);
  	if (od != null) {
  		result = getPMLObjectFromDataObject(od);
  	}
  	return result;
  }
  
  /**
   * Returns a PML object identified by its URI and is loaded from a
   * specific file.
   * @param URIStr URI of PML object
   * @param sourceFileURL URL of the file from which the object is to be created
   * @return PML object
   */
  public static Object getPMLObjectFromFile(String URIStr, String sourceFileURL) {
  	Object result = null;
  	DataObject od = getDataObjectFromFile(URIStr, sourceFileURL, Config.pmlOntologySet);
  	if (od != null) {
  		result = getPMLObjectFromDataObject(od);
  	}
  	return result;
  }
  /**
   * Returns a PML object identified by its URI and is loaded from a
   * string.
   * @param URIStr URI of PML object
   * @param sourceString String from which the object is to be created
   * @return PML object
   */
  public static Object getPMLObjectFromString(String URIStr, String sourceString) {
  	Object result = null;
  	DataObject od = getDataObjectFromString(URIStr, sourceString, Config.pmlOntologySet);
  	if (od != null) {
  		result = getPMLObjectFromDataObject(od);
  	}
  	return result;
  }
  
  /**
   * Returns a PML object identified by its URI and is loaded from the
   * specified Jena OntModel.
   * @param URIStr URI of the PML object
   * @param model Jena model
   * @return PML object
   */
  public static Object getPMLObjectFromModel(String URIStr, OntModel model) {
  	Object result = null;
  	DataObject od = getDataObject(URIStr, model, Config.pmlOntologySet);
  	if (od != null) {
  		result = getPMLObjectFromDataObject(od);
  	}
  	return result;
  }
  
  /**
   * Returns a PML data object base on an instance of DataObject. The underline implementation
   * of the PML object is determined by its ontology class name, such as InferenceRule or
   * NodeSet.
   * @param dObj the DataObject
   * @return PML object
   */
  public static Object getPMLObjectFromDataObject(DataObject dObj) {
  	Object result = dObj;
  	/*
  	List knownPackages = new ArrayList();
  	knownPackages.add(Config.PMLJ_PACKAGE_ROOT);
  	knownPackages.add(Config.PMLP_PACKAGE_ROOT);
  	knownPackages.add(Config.PMLOWL_PACKAGE_ROOT);
  	 */  	
  	if (dObj != null && dObj.getOntologyClassName() != null && !dObj.getOntologyClassName().equals("")) {
  		OntClass objCls = dObj.getOntologyClass();   		

  		if (objCls.equals(Config.PMLListCls) || dObj.getOntology().isSubClassOf(objCls, Config.PMLListCls)) {
  			result = getPMLListObjectPropertyValue(dObj);
  		} else {  	
  			String className = dObj.getOntologyClassName();
  			String classOnt = dObj.getOntologyURI();
//System.out.println("PMLObjectManager.getPMLObjectFromDataObject: entering with object type "+className);
  			// this is not what i like. just want to get things going for now. old Team is now Organization
  			if (className != null && className.equalsIgnoreCase(IW200407.Team_lname)) className = PMLP.Organization_lname;
  			if (className != null && className.equalsIgnoreCase(IW200407.DerivedRule_lname)) className = PMLJ.AbstractionRule_lname;
  			if (!dObj.getClass().getName().startsWith("IW") || !dObj.getClass().getName().endsWith(className+"Impl")) {
  				List<OntClass> superClss = org.inference_web.pml.context.accessor.OntologyManager.getOrderedSuperClasses(objCls) ;
  				List<OntClass> allClasses = new ArrayList<OntClass>();
  				allClasses.add(objCls);
  				allClasses.addAll((List<OntClass>)superClss);

  				Class iwCls = null;
  				boolean done = false;
  				ListIterator cIt = allClasses.listIterator();
  				while (cIt.hasNext() && !done) {
  					OntClass tempCls = (OntClass)cIt.next();
  					if (tempCls != null) {
  						String tempClassName = tempCls.getLocalName();
  						String tempClassOnt = tempCls.getNameSpace();
  						if (tempClassOnt != null && Config.ontologyPackageMap.containsValue(tempClassOnt)) {
  							Iterator packages = Config.ontologyPackageMap.keySet().iterator();
  							while (packages.hasNext() && iwCls==null) {
  								String iwCName = (String)packages.next()+".impl.IW" + tempClassName + "Impl";
  								try {
  									iwCls = Class.forName(iwCName);
  								} catch (ClassNotFoundException e) {

  								}
  							}
  							done = true;
  						}
  					}
  				}
  				if (iwCls != null) {
  					try { 
  						Class [] constArgs = {DataObject.class};
  						Constructor cons = iwCls.getConstructor(constArgs); 
  						Object[] argValues = {dObj};
  						result = cons.newInstance(argValues);
  					} catch (Exception e) {
  						System.out.println("PMLObjectManager.getPMLObjectFromDataObject: unable to create PML object type "+iwCls +
  								"\n"+e.getMessage());
  					}
  				} else {
  					System.out.println("PMLObjectManager.getPMLObjectFromDataObject: non-PML data object with type "+classOnt+className ); 			
  				}
  			}

  		}
  	}
  	return result;
  }

  public static Object createPMLObject(String className) {
  	Object result = null;
  	String packageName = null;
  	String ontURI = null;

  	if (className != null ) {
  		Class iwCls = null;
  		Iterator packages = Config.ontologyPackageMap.keySet().iterator();
  		while (packages.hasNext() && iwCls==null) {
  			packageName = (String)packages.next();
  			ontURI = (String)Config.ontologyPackageMap.get(packageName);
  			String iwCName = packageName+".impl.IW" + className + "Impl";
  			try {
  				iwCls = Class.forName(iwCName);
  			} catch (ClassNotFoundException e) {

  			}
  		}
  		if (iwCls != null) {
  			try { 
  				Class [] constArgs = {String.class, String.class};
  				Constructor cons = iwCls.getConstructor(constArgs); 
  				Object[] argValues = {ontURI, className};
  				result = cons.newInstance(argValues);
//				System.out.println("PMLObjectManager.getPMLObjectFromDataObject: dataobject converted to "+iwCls.getName());
  			} catch (Exception e) {
  				System.out.println("PMLObjectManager.createPMLObject: unable to create PML object type "+iwCls +
  						"\n"+e.getMessage());
  			}
  		} else {
  			System.out.println("PMLObjectManager.createPMLObject: unable to determine the class name in order to create PML object type "+className ); 			
  		}
  	}
  	return result;
  }

  public static Object getPMLObjectPropertyValue(Object inObj) {
  	Object result = null;
  	if (inObj != null) {
  		if (inObj instanceof List) {
  			ArrayList valueList = new ArrayList();
  			Iterator elems = ((List)inObj).listIterator();
  			while (elems.hasNext()) {
  				Object element = (elems.next());
  				valueList.add(getPMLObject(element));
  			}
  			result = valueList;
  		} else {
  			result = getPMLObject(inObj);
  		}
  	}
  	return result;
  }
  /**
   * Returns a PML data object based on the reference object, as an argument. 
   * The argument object could be the URI of a PML object, or an instance of DataObject, or 
   * already a PML object. If the argument object is an URI, then the PML object is built
   * from a document specified by the URI.
   * @param inObj reference object
   * @return PML object
   */
  public static Object getPMLObject (Object inObj) {
  	Object result = null;
  	if (inObj != null) {
  		if (isPMLObject(inObj) ) {
  			result = inObj;
  		} else {
  			if (inObj instanceof String) {
  				result = getPMLObjectFromURI((String) inObj); 
  			} else if (inObj instanceof DataObject) { 
  				result = getPMLObjectFromDataObject((DataObject) inObj);
  			} else {
  				result = inObj;
  			}		

  		}
  	}
  	return result;
  }
  public static boolean isPMLObject (Object inObj) {
  	boolean result = false;
  	String pmlObjClsName = "org.inference_web.pml.v2.PMLObjectImpl";
  	try {
    	Class PMLObjClass = Class.forName(pmlObjClsName);
  		if (PMLObjClass.cast(inObj) != null) {
  			result = true;
  		}
  	} catch (ClassNotFoundException e) {
  		System.out.println("PMLObjectManager.isPMLObject: Could not create root PML Object class from incorrect class name of "+pmlObjClsName);
  		
  	} catch (ClassCastException e) {
  		result = false;
  	}
  	return result;
  }
  public static boolean isJustificationURI(String URI) {
  	boolean result = false;
  	Resource rsc = ResourceFactory.createResource(URI);
  	if (rsc != null) {
  		if (ToolURI.equalURI(rsc.getNameSpace(), PMLJ.getURI())) {
  			result =true;
  		}
  	}  	
  	return result;
  }
  public static boolean isProvenanceURI(String URI) {
  	boolean result = false;
  	Resource rsc = ResourceFactory.createResource(URI);
  	if (rsc != null) {
  		if (ToolURI.equalURI(rsc.getNameSpace(), PMLP.getURI())) {
  			result =true;
  		}
  	}  	
  	return result;
  }
  /**
   * Returns a PML data object based on the reference object, as an argument. 
   * The argument object could be the URI of a PML object, or an instance of DataObject, or 
   * already a PML object. If the argument object is an URI, then the PML object is built
   * from the source file.
   * @param inObj reference object
   * @param sourceFileURL URL of the file
   * @return PML object
   */
  public static Object getPMLObject (Object inObj, String sourceFileURL) {
  	Object result = null;
  	if (inObj instanceof String) {
  		result = getPMLObjectFromFile((String) inObj, sourceFileURL);
  	} else if (inObj.getClass().getName().startsWith(Config.PMLJ_PACKAGE_ROOT) ||
				 inObj.getClass().getName().startsWith(Config.PMLP_PACKAGE_ROOT) ) {
  		result = inObj;
  	} else if (inObj instanceof DataObject) { 
  		result = getPMLObjectFromDataObject((DataObject) inObj);
  	} else {
  		result = inObj;
  	}
  	return result;
  }
  
  /**
   * Returns a PML data object based on the reference object, as an argument. 
   * The argument object could be the URI of a PML object, or an instance of DataObject, or 
   * already a PML object. If the argument object is an URI, then the PML object is built
   * from the Jena model.
   * @param inObj reference object
   * @param model Jena model to be used to build PML object
   * @return PML object
   */
  public static Object getPMLObject (Object inObj, OntModel model) {
  	Object result = null;
  	if (inObj instanceof String) {
  		result = getPMLObjectFromModel((String) inObj, model);
  	} else if (inObj.getClass().getName().startsWith(Config.PMLJ_PACKAGE_ROOT) ||
				 inObj.getClass().getName().startsWith(Config.PMLP_PACKAGE_ROOT) ) {
  		result = inObj;
  	} else if (inObj instanceof DataObject) { 
  		result = getPMLObjectFromDataObject((DataObject) inObj);
  	} else {
  		result = inObj;
  	}
  	return result;
  }
  
  /**
   * Saves the PML data object to a local file.
   * @param pmlObj PML data object
   * @param fileName name of the file
   * @return if save is successful
   */
  public static boolean savePMLObject (PMLObject pmlObj, String fileName) {
    boolean result = false;
    if (pmlObj != null) {
      OntModel modelPE = PMLObjectManager.getOntModel(pmlObj);
      result = Writer.toFile(modelPE, fileName);
    }    
    return result;    
  }
  
  public static boolean savePMLObjectCollection(Collection pmlObjs, String fileName) {
  	boolean result = false;
  	if (pmlObjs != null) {
   	  OntModel model = ModelFactory.createOntologyModel();
  		Iterator pmls = pmlObjs.iterator();
  		while (pmls.hasNext()) {
  			PMLObject obj = (PMLObject)pmls.next();
  			addPMLObjectToJenaModel(model, obj);
  		}
      result = Writer.toFile(model, fileName);
  	}
  	
  	return result;
  }
  /**
   * Adds a PML data object as Individual into a Jena model.
   * @param model Jena model
   * @param pmlObj PML data object
   */
	public static void addPMLObjectToJenaModel (OntModel model, PMLObject pmlObj){
//   	OntModel result = null;
   	String className; 
   	String classUri;
   	String ontUri;
   	String poNameSpace;
   	OntClass ontCls;
   	
   	if (model != null){
   		if (pmlObj != null && pmlObj.getIdentifier() != null && pmlObj.getNameSpace()!= null) {
   			className = pmlObj.getOntologyClassName();
   			ontUri = pmlObj.getOntologyURI();
   			classUri = pmlObj.getClassURI();
   			poNameSpace = pmlObj.getNameSpace();

   			org.inference_web.pml.context.accessor.Ontology classOntology = OntologyManager.getOntology(pmlObj.getOntologyURI());
   			ontCls = classOntology.getOntClass(classUri);
   			org.inference_web.pml.context.accessor.Ontology propOntology = null;

   			model.setNsPrefixes(classOntology.getModel().getNsPrefixMap());

   			Individual ind = null;
   			if (pmlObj.getIdentifier().getURIString() != null) {
   				ind = model.createIndividual(pmlObj.getIdentifier().getURIString(), ontCls);
   			} else {
   				ind = model.createIndividual(ontCls);
   			}
   			Iterator propertyURIs = classOntology.listPropertyURIs(classUri, false).iterator();
   			int maxCard;
   			while (propertyURIs.hasNext()) {
   				String propName = (String)propertyURIs.next();
   				Object propValue = pmlObj.getProperty(propName);
   				if (propValue != null ) {
   					propOntology = OntologyManager.getOntology(ResourceFactory.createResource(propName).getNameSpace());
   					OntProperty prop = propOntology.getProperty(propName);   
   					maxCard = classOntology.getMaxCardinality(ontCls, prop);
   					addProperty(poNameSpace, model, ind, ontCls, prop, propOntology, maxCard, propValue);
   				}
   			}
   		}   	
   	}
	}
  
  /**
   * Returns a Jena model represents the PML data object.
   * @param pmlObj PML data object
   * @return Jena model
   */
	public static OntModel getOntModel (PMLObject pmlObj){
   	OntModel result = null;
   	if (pmlObj != null && pmlObj.getIdentifier() != null && pmlObj.getNameSpace()!= null) {
   	  OntModel model = ModelFactory.createOntologyModel();
   	  addPMLObjectToJenaModel(model, pmlObj);
   	  result = model;
   	}
    return result;    
  }
/*
	public static OntModel getOntModel (PMLObject pmlObj){
   	OntModel result = null;
   	String className; 
   	String classUri;
   	String ontUri;
   	String poNameSpace;
   	OntClass ontCls;
   	
   	
   	if (pmlObj != null && pmlObj.getIdentifier() != null && pmlObj.getNameSpace()!= null) {
   	  className = pmlObj.getOntologyClassName();
   	  ontUri = pmlObj.getOntologyURI();
   	  classUri = pmlObj.getClassURI();
   	  poNameSpace = pmlObj.getNameSpace();
   	  
   	  OntModel model = ModelFactory.createOntologyModel();
   	  org.inference_web.pml.context.accessor.Ontology classOntology = OntologyManager.getOntology(pmlObj.getOntologyURI());
   	  ontCls = classOntology.getOntClass(classUri);
   	  org.inference_web.pml.context.accessor.Ontology propOntology = null;
   	  
   	  model.setNsPrefixes(classOntology.getModel().getNsPrefixMap());
   	  
   	  Individual ind = null;
   	  if (pmlObj.getIdentifier().getURIString() != null) {
   	  	ind = model.createIndividual(pmlObj.getIdentifier().getURIString(), ontCls);
   	  } else {
   	  	ind = model.createIndividual(ontCls);
   	  }
   	  Iterator propertyURIs = classOntology.listPropertyURIs(classUri, false).iterator();
   	  int maxCard;
   	  while (propertyURIs.hasNext()) {
   	    String propName = (String)propertyURIs.next();
   	    Object propValue = pmlObj.getProperty(propName);
   	    if (propValue != null ) {
   	    	propOntology = OntologyManager.getOntology(ResourceFactory.createResource(propName).getNameSpace());
   	      OntProperty prop = propOntology.getProperty(propName);   
   	      maxCard = classOntology.getMaxCardinality(ontCls, prop);
    	    addProperty(poNameSpace, model, ind, ontCls, prop, propOntology, maxCard, propValue);
   	    }
   	  }
   	  result = model;
   	}   	
    return result;    
  }
*/
	private static void addProperty (String nameSpace, OntModel model, Individual ind, OntClass cls, OntProperty property, org.inference_web.pml.context.accessor.Ontology propOnt, int maxCard, Object value) {
		boolean isObjProp = false;
		boolean isDataProp = false;
	
		if (model != null && ind != null && property != null && value != null) {
			if (property.isDatatypeProperty()) isDataProp = true;
			if (property.isObjectProperty()) isObjProp = true;
			if (isObjProp) {
				Object objPropValue = prepareObjectPropertyValue(nameSpace, model, ind, value);
				if (objPropValue instanceof Individual) {
//System.out.println("addProperty: for "+property.getURI()+" value type individual ");
					ind.addProperty(property, (Individual)objPropValue);
				} else if (objPropValue instanceof Resource) {
//System.out.println("addProperty: for "+property.getURI()+" value type resource ");
					ind.addProperty(property, (Resource)objPropValue);
				} else if (objPropValue instanceof List) {
					OntResource range = propOnt.getPropertyRange(cls, property);
//   	      OntClass range = (OntClass)Ontology.getPropertyRange(property);
   	      org.inference_web.pml.context.accessor.Ontology rangeOntology = null;
   	      if (range !=null) {
   	      	rangeOntology= OntologyManager.getOntology(((Resource)range).getNameSpace());
   	      }
   	      if (range != null && (range.equals(Config.PMLListCls) || rangeOntology.isSubClassOf(rangeOntology.getOntClass(range.getURI()), Config.PMLListCls))) {
   	      	OntClass rangeCls = rangeOntology.getOntClass(range.getURI());
   	      	if (maxCard < 0 || maxCard > 1) {
   	      		ListIterator vlt = ((List)objPropValue).listIterator();
   	      		while (vlt.hasNext()) {
   	      			addPMLListPropertyValue (model, ind, property, rangeCls, vlt.next());
   	      		}
   	      	} else {
 	      			addPMLListPropertyValue (model, ind, property, rangeCls, objPropValue);   	      		
   	      	}

   	      } else {
//System.out.println("addProperty: for "+property.getURI()+" value type list for multiples ");
   	      	ListIterator values = ((ArrayList)objPropValue).listIterator();
   	      	while (values.hasNext()) {
   	      		Object currentValue = values.next();
   	      		if (currentValue instanceof Individual) {
   	      			ind.addProperty(property, (Individual)currentValue);
   	      		} else if (currentValue instanceof Resource) {
   	      			ind.addProperty(property, (Resource)currentValue);
   	      		} else {
   	      			System.out.println("PMLObjectManager.addProperty: unable to determine property value type for "+property.getURI());
   	      		}
   	      	}
   	      }					
				}				
			} else if (isDataProp) {
				if (property.getRange() != null && property.getRange().getURI() != null) {
					if (value instanceof List) {
						Iterator lIt = ((List)value).listIterator();
						if (maxCard < 0 || maxCard >1) {
							while (lIt.hasNext()) {
								ind.addProperty(property, model.createTypedLiteral(lIt.next(), property.getRange().getURI()));								
							}
						} else {
							ind.addProperty(property, model.createTypedLiteral(lIt.next(), property.getRange().getURI()));															
						}
					} else {
						ind.addProperty(property, model.createTypedLiteral(value, property.getRange().getURI()));
					}
				} else {
					ind.addProperty(property, value.toString());
				}
			}      
		}
	}
	
	private static void addPMLListPropertyValue(OntModel model, Individual ind, OntProperty property, OntClass propRange, Object value) {
		ListIterator values = null;
		Individual rootlist = null;
		Individual prevlist = null;
		Individual curlist = null;
		OntClass list_type_uri = propRange;
		Individual nextlist = null;

		if (value instanceof Individual) {
			Individual valueInd = (Individual) value; 
			org.inference_web.pml.context.accessor.Ontology vOntology = OntologyManager.getOntology(valueInd.getRDFType().getNameSpace());
			OntClass vOntCls = vOntology.getOntClass(valueInd.getRDFType().getURI());
//System.out.println("**add PMLList property value: individual uri "+valueInd.getRDFType().getURI());
			if (vOntCls != null && (vOntCls.equals(Config.PMLListCls) || vOntology.isSubClassOf(propRange, Config.PMLListCls))) {
				ind.addProperty(property, valueInd);   	      			
			}
		} else if (value instanceof List) {
			values = ((ArrayList)value).listIterator();

			while (values.hasNext()) {
				Object currentValue = values.next();
				if (!( currentValue instanceof Resource)) {
					System.out.println("PMLObjectManager.addProperty: unable to determine property value type for "+property.getURI());
					continue;
				}
				curlist = model.createIndividual( list_type_uri);
				curlist.addProperty(PMLDS.first, (Resource) currentValue);
				if (prevlist != null) {
					prevlist.addProperty(PMLDS.rest, curlist);  	      								
				} //else {
					prevlist = curlist;
//				}
				
				if (rootlist == null) {
					rootlist=curlist;
				}
/*				
				if (nextlist != null) {
					curlist = nextlist;
					prevlist.addProperty(PMLDS.rest, curlist);  	      			
				}

				if (curlist == null) {
					curlist = model.createIndividual( list_type_uri);
				}

				if (rootlist == null) {
					rootlist = curlist;
				}
				curlist.addProperty(PMLDS.first, (Resource) currentValue);
				prevlist = curlist;
				nextlist = model.createIndividual( list_type_uri);
*/
			}   	      	

			if (null!=rootlist) {
				ind.addProperty(property, rootlist);
			}
		}
	}
	
	private static Object prepareObjectPropertyValue (String nameSpace, OntModel model, Individual ind,  Object value) {
		Object result = null;
	
		if (model != null && ind != null && value != null) {
	
				if (value instanceof List) {
					ArrayList newValues = new ArrayList();
					Iterator values = ((List)value).iterator();
					while (values.hasNext()) {
						Object currentValue = values.next();
//System.out.println("add new value "+ind.getURI());
						newValues.add(prepareObjectPropertyValue(nameSpace, model, ind, currentValue));
//						addProperty(nameSpace, model, ind, property, currentValue);
					}
					result = newValues;
				}
				if (value instanceof DataObjectImpl) {
					DataObjectImpl valueObject = (DataObjectImpl)value;
					if (Config.isUnnamedConcept(valueObject.getOntologyClassName())) {
						valueObject.setIdentifier(null);
						Individual propInd = getPMLIndividual(nameSpace,model,valueObject);
						result = propInd;						
					} else {
					if (valueObject.getIdentifier() != null && 
							valueObject.getIdentifier().getURIString() != null && 
							!valueObject.getIdentifier().getURIString().equals("")) { // has uri
						
						// look inside of model to see if an individual by this uri is already in
						Individual tempInd = model.getIndividual(valueObject.getIdentifier().getURIString());
						
						if (tempInd != null) { // already in ontModel
							result = tempInd;
//							result = model.createResource(valueObject.getIdentifier().getURIString());
//							ind.addProperty(property, model.createResource(valueObject.getIdentifier().getURIString()));  						
						} else { // not found in ontModel
							boolean sameNS = false;
							try {
								if (ToolURI.equalURI(valueObject.getNameSpace(),nameSpace)) { // has uri and in same namespace
									sameNS = true;
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (sameNS) { // has uri and in same namespace								
								// create an individual in same model because having the same namespace
								Individual propInd = getPMLIndividual(nameSpace,model,(DataObject)value);
//System.out.println("individual in same namespace "+((DataObject)value).getIdentifier().getURIString());
//								ind.addProperty(property,propInd);
								result = propInd;
//								result = model.createResource(propInd.getURI());
							} else { // has uri, but in different namespace
									result = model.createResource(valueObject.getIdentifier().getURIString()); 
							}
						}						
					} else { // has no uri
						Individual propInd = getPMLIndividual(nameSpace,model,(DataObject)value);
//						ind.addProperty(property,propInd);
						result = propInd;
					}
				}
				}
				if (value instanceof String) { // object property, value type string => uri
//					ind.addProperty(property, model.createResource((String)value));
					result = model.getResource((String)value);
					if (result == null) {
						result = model.createResource((String)value);
					}
				}
		}
		return result;
	}
	
	private static Individual getPMLIndividual (String nameSpace, OntModel model, DataObject dObj){
   	Individual result = null;
   	String type ;
   	
   	if (model != null && dObj != null && dObj.getClassURI() != null) {

   	  org.inference_web.pml.context.accessor.Ontology clsOnt = OntologyManager.getOntology(dObj.getOntologyURI());
   	  type = dObj.getClassURI();
   	  org.inference_web.pml.context.accessor.Ontology propOntology = null;

   	  Individual ind = null;
   	  OntClass indClass = clsOnt.getOntClass(type);
   	  if (dObj.getIdentifier() != null && 
   	  		dObj.getIdentifier().getURIString() != null &&
   	  		!dObj.getIdentifier().getURIString().equals("")) {
   	  	ind = model.createIndividual(dObj.getIdentifier().getURIString(),indClass);
   	  } else {
   	  	ind = model.createIndividual(indClass);
   	  }
   	     	  
   	  Iterator propertyURIs = clsOnt.listPropertyURIs(type, false).iterator();
   	  int maxCard;
   	  while (propertyURIs.hasNext()) {
   	    String propName = (String)propertyURIs.next();
   	    Object propValue = dObj.getProperty(propName);
   	    if (propValue != null ) {
   	    	propOntology = OntologyManager.getOntology(
   	    			ResourceFactory.createResource(propName).getNameSpace());
   	      OntProperty prop = propOntology.getProperty(propName);
   	      maxCard = clsOnt.getMaxCardinality(indClass, prop);
   	      addProperty(nameSpace, model, ind, indClass, prop, propOntology, maxCard, propValue);
   	    }
   	  }
   	  result = ind;
   	} else {
   		if (model == null )System.out.println("PMLObjectManager.getIndividual: jena model is null");
   		if (dObj == null )System.out.println("PMLObjectManager.getIndividual: data object is null");
   		if (dObj.getClassURI() == null )System.out.println("PMLObjectManager.getIndividual: data object class uri is null");
   	}
   	
    return result;    
  }
  
  public static boolean isPMLListType (OntClass objCls) {
  	boolean result = false;
  	if (objCls != null) {
  		org.inference_web.pml.context.accessor.Ontology ontology = OntologyManager.getOntology(objCls.getNameSpace());
  		if (objCls.equals(Config.PMLListCls) || ontology.isSubClassOf(objCls, Config.PMLListCls)) {
  			result = true;
  		}
  	}
  	return result;
  }
  
  public static Resource getPMLListElementType (OntClass objCls) {
  	Resource result = null;
  	if (isPMLListType(objCls)) {
  		org.inference_web.pml.context.accessor.Ontology clsOnt = OntologyManager.getOntology(objCls.getNameSpace());
  		Iterator properties = clsOnt.listPropertyURIs(objCls.getURI()).listIterator();
  		boolean found = false;
  		while (properties.hasNext() && !found) {
  			String propertyName = (String)properties.next();
  			Resource propertyResource = ResourceFactory.createResource(propertyName);
  			if (propertyResource.getLocalName().equalsIgnoreCase(PMLDS.first_lname)) {
  				Resource rangeResource = clsOnt.getPropertyRange(objCls, propertyName);
  				if (rangeResource != null) {
  					result = rangeResource;
  					found = true;
  				}  	else {
  					System.out.println("rangeResource can not be found by ontology ");
  				}
  			}
  		}  		
  	}
  	return result;
  }
  
  public static String printPMLObjectToString (PMLObject pObj) {
  	String result = null;
  	if (pObj != null) {
  		OntModel model = PMLObjectManager.getOntModel(pObj);
  		if (model != null) {
  			result = Writer.printToString(model);
  		}
  	}
  	return result;
  }

  public static String printPMLObjectCollectionToString(Collection pmlObjs) {
  	String result = null;
  	if (pmlObjs != null) {
   	  OntModel model = ModelFactory.createOntologyModel();
  		Iterator pmls = pmlObjs.iterator();
  		while (pmls.hasNext()) {
  			PMLObject obj = (PMLObject)pmls.next();
  			addPMLObjectToJenaModel(model, obj);
  		}
      result = Writer.printToString(model);
  	}
  	
  	return result;
  }

  /**
   * Saves a NodeSet and its antecedents into a file with specified namespace. <br>
   * If the namespace is null, it assumes the identity (URI) of the NodeSet and its
   * antecedents are already defined.
   * @param ns NodeSet to be saved
   * @param deployNamespace namespace for deploying NodeSet
   * @param fileName file name
   * @return URI of the NodeSet
   */
  public static String saveNodeSetToFile(IWNodeSet ns, String deployNamespace, String fileName){
  	String result = null;
  	if (ns != null && fileName != null) {
  		if ( deployNamespace != null ) {
  			List count = new ArrayList();
  			count.add(new Integer(0));
  			IWNodeSet newNS = assignNodeSetIdentity(ns, deployNamespace, count);
  			if (newNS != null) {
  				OntModel model = PMLObjectManager.getOntModel(newNS);
  				if (model != null) {
  					Writer.toFile(getOntModel(newNS), fileName);
  					result = newNS.getIdentifier().getURIString();
  				}
  			}
  		} else {
  			OntModel model = PMLObjectManager.getOntModel(ns);
  			if (model != null) {
  				Writer.toFile(getOntModel(ns), fileName);
  				result = ns.getIdentifier().getURIString();
  			}

  		}
  	}
    return result;
  }

  
  private static IWNodeSet assignNodeSetIdentity(IWNodeSet ns, String nameSpace, List count) {
    String origURIStr = null;
    String newURIStr = null;
  	String objectName = null;
  	String origNameSpace = null;
  	
  	if (!ns.isAxiom()) {
  		ListIterator iss = ns.getInferenceSteps().listIterator();
  		while (iss.hasNext()) {
  			IWInferenceStep currentIS = (IWInferenceStep) iss.next();
  			if (currentIS.getHasAntecedentList() != null) {
  				ArrayList newAntes = new ArrayList();
  				ListIterator antecedents = currentIS.getHasAntecedentList().listIterator();
  				while (antecedents.hasNext()) { 
  					Object ante = antecedents.next();
  					if (ante instanceof String) {
  						newAntes.add(ante);
  					} else {
  						IWNodeSet currentAntecedent =  (IWNodeSet)ante;
  						newAntes.add(assignNodeSetIdentity(currentAntecedent,nameSpace, count));
  					}
  				}
  				currentIS.setHasAntecedentList(newAntes);
  			}
  		}
  	}
    if (ns.getIdentifier() != null) {
    	origURIStr = ns.getIdentifier().getURIString();
    	objectName = ns.getObjectName();
  		origNameSpace = ns.getNameSpace();
    }
    if (origNameSpace !=null && !origNameSpace.trim().equals("#") && origNameSpace.length()>1) {
    	// do nothing
    } else {
    	if (objectName != null && objectName.length()>0) {
    		newURIStr = nameSpace+objectName;
    		ns.setIdentifier(getObjectID(newURIStr));
    	} else {
      	Integer countVal = (Integer)count.get(0);
      	String currentCount = countVal.toString();
      	newURIStr = nameSpace+"ns"+currentCount;
      	count.set(0, new Integer(countVal.intValue()+1));
      	ns.setIdentifier(getObjectID(newURIStr));
    	}   
    }
//System.out.println("assignNameSpace_prepareOneNodeSetURI: return uri="+newNodeSet.getIdentifier().getURIString());
    return ns;
  }

  private static void cacheNodeSet (IWNodeSet ns) {
  	DataObjectIdentifier nsID = ns.getIdentifier();
  	String nsURI = null;
  	IWNodeSet tempNs = null;
  	int magnitude = ns.getDepth();
  	if (nsID != null) {
  		nsURI = nsID.getURIString();
  		if (nsURI != null) {
  			nodeSetCache.put(nsURI, ns);
  		}  		
  	}
  }
  
  public static DataObject loadDataObject(String dObjURI) {
  	DataObject result = null;
  	result = DataObjectManager.loadDataObject(dObjURI, Config.pmlOntologySet);  
  	return result;
  }
  
  public static DataObject getDataObject(String dObjURI) {
  	return DataObjectManager.getDataObject(dObjURI, Config.pmlOntologySet);
  }


  public static void initPMLObjectCache(int size) {  	
  	if (size>0) {
  		cacheSize= size;
  		nodeSetCache = new ObjectCache(cacheSize);
  		queryCache = new ObjectCache(cacheSize);
  	}  	
  }
  public static int getCacheSize() {
  	return cacheSize;
  }
  
  public static boolean setExpirationIntervalInMinutes (int newExpInter) {
  	boolean success = false;
  		if (newExpInter >= 0) { 
  			expInterval = newExpInter;
  			nodeSetCache.setExpirationIntervalInMinutes(expInterval);
  			queryCache.setExpirationIntervalInMinutes(expInterval);
  			DataObjectManager.setExpirationIntervalInMinutes(newExpInter);
  			success = true;
  		}
  	return success;
  }
  
  public static int getExpirationInterval() {
  	return expInterval;
  }
 
  /**
   * Resets NodeSet and Query caches, as well as 
   * chaches managed by DataObjectManager and OntologyManger including
   * DataObject, JenaModel, PropertyTemplate and Ontology caches.
   * 
   */
  public static void invalidateCache() {
  	nodeSetCache.invalidateCache();
  	queryCache.invalidateCache();
		DataObjectManager.invalidateCache();
  }

}
/* END OF PMLObjectManager */
