/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.context.accessor;

import java.util.*;
import java.net.*;

import org.inference_web.pml.shared.ObjectCache;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.shared.util.*;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyManager {

	public static ObjectCache ontologyCache = new ObjectCache();
  
  public static Ontology getOntology(String ontURIString) {
  	
  	org.inference_web.pml.context.accessor.Ontology result = null;
  	try {
  		URI testUri = new URI(ontURIString);
  		result =	(org.inference_web.pml.context.accessor.Ontology)ontologyCache.get(ontURIString);
  		if (result == null) {
  			result = new Ontology(ontURIString);
  			if (result != null) {
  				try {
  				ontologyCache.put(ontURIString, result);
  				} catch (Exception e) {  					
  				}
  			}
  		}  		
  	} catch (Exception e) {
  		
  	}
  	return result;
  }

  static boolean isIncludedInOntologyList(String ontUri, Set<String> ontUris){
//boolean debug = false;
//if (ontUri.indexOf("justification")>0) debug = true;
  	boolean result = false;
  	if (ontUri != null && ontUris != null) {
//if (debug)System.out.println("compare "+ontUri);
  		ontUri = ontUri.trim();
  		if (ontUri.endsWith("#")) {
  			ontUri = ontUri.substring(0, ontUri.length()-1);
  		}
  		Iterator it = ontUris.iterator();
  		while (it.hasNext() && !result) {
  			String tempUri = ((String)it.next()).trim();
    		if (tempUri.endsWith("#")) {
    			tempUri = tempUri.substring(0, tempUri.length()-1);
    		}
//if (debug)System.out.println("  with "+tempUri);
  			if (ToolURI.equalURI(ontUri, tempUri)) {
//if (debug)System.out.println("  == ");
  				result = true;
  			}
  		}
  	}
  	return result;
  }

  static Set<String> cleanUpOntologyURIs (Set<String> inUris) {
  	Set<String> result = null;
  	if (inUris != null) {
  		result = new HashSet<String>();
  		Iterator it = inUris.iterator();
  		while (it.hasNext()) {
  			String tempUri = ((String)it.next()).trim();
    		if (tempUri.endsWith("#")) {
    			tempUri = tempUri.substring(0, tempUri.length()-1);
    			result.add(tempUri);
    		} else {
    			result.add(tempUri);   			
    		}
  		} 		
  	}
  	return result;
  }
  
  public static OntClass pickRDFType (Individual indv, Set<String> preferredOntologyURIs) {
  	OntClass result = null; 
  	if (indv != null) {
  		List<OntClass> allTypes = indv.listOntClasses(false).toList(); // super classes included
  		List<OntClass> directTypes = indv.listOntClasses(true).toList(); // direct classes only
  		List<OntClass> preferredTypes = new ArrayList<OntClass>(); // all types in preferred ontologies
  		List<OntClass> directpreferredTypes = new ArrayList<OntClass>(); // direct types in preferred ontologyies
  		List<OntClass> preferredNonSuperClassTypes = new ArrayList<OntClass>(); // all types in preferred ontoloties, but not a super class type of others in preferredTypes list
  		List<OntClass> directNonSuperClasspreferredTypes = new ArrayList<OntClass>();// direct types in preferred ontoloties, but not a super class type of others in directpreferredTypes list
  		if (directTypes.size() == 1) {
  			result = directTypes.get(0);
  		} else {
  			if (preferredOntologyURIs != null && preferredOntologyURIs.size()>0) {
  				preferredOntologyURIs = OntologyManager.cleanUpOntologyURIs(preferredOntologyURIs);
  				if (directTypes.size()>0) {
  					OntClass tempCls = null;
  					int size = directTypes.size();
  					for (int i=0; i<size; i++) {
  						tempCls = directTypes.get(i);
  						String ontNamespace = tempCls.getNameSpace();
  						if (isIncludedInOntologyList(ontNamespace, preferredOntologyURIs)) {
  							directpreferredTypes.add(tempCls);
  						}
  					}
  				}

  				if (allTypes.size()>0) {
  					OntClass tempCls = null;
  					int size = allTypes.size();
  					for (int i=0; i<size; i++) {
  						tempCls = allTypes.get(i);
  						String ontNamespace = tempCls.getNameSpace();
  						if (isIncludedInOntologyList(ontNamespace, preferredOntologyURIs)) {
  							preferredTypes.add(tempCls);
  						}
  					}
  				}
  			} else {
  				directpreferredTypes = directTypes;
  				preferredTypes = allTypes;
  			}
/*
System.out.println("sizes: allTypes="+allTypes.size()+", directTypes="+directTypes.size()
  					+", prefferdTypes="+preferredTypes.size()+", directpreferredTypes="+directpreferredTypes.size()
  					+", preferredNonSuperClassTypes="+preferredNonSuperClassTypes.size()+", directNonSuperClassTypes="+directNonSuperClasspreferredTypes.size());
if (preferredTypes.size()>1) {
	int s = preferredTypes.size();
	for (int i=0; i<s; i++ ){
		System.out.println("  preferred: "+preferredTypes.get(i));
	}
}
*/
  			if (directpreferredTypes.size() == 1) {
  				result = directpreferredTypes.get(0);
  			} else {
  				if (preferredTypes.size() == 1) {
  					result = preferredTypes.get(0);
  				} else { // can not be determined by directPrefferd or allpreferred
  					directNonSuperClasspreferredTypes = getNonSuperClasses(directpreferredTypes);
  					preferredNonSuperClassTypes = getNonSuperClasses (preferredTypes);
  					if (directNonSuperClasspreferredTypes != null && directNonSuperClasspreferredTypes.size() == 1) {
  						result = directNonSuperClasspreferredTypes.get(0);
  					} else {
  						if (preferredNonSuperClassTypes != null && preferredNonSuperClassTypes.size() == 1) {
  							result = preferredNonSuperClassTypes.get(0);
  						} else { // can not find a unique class.
  							if (directNonSuperClasspreferredTypes != null && directNonSuperClasspreferredTypes.size() > 1) {
  								result = directNonSuperClasspreferredTypes.get(0);
  							} else if (directpreferredTypes != null && directpreferredTypes.size() > 1) {
  								result = directpreferredTypes.get(0);
  							} else if (preferredNonSuperClassTypes != null && preferredNonSuperClassTypes.size() > 1) {
  								result = preferredNonSuperClassTypes.get(0);
  							} else if (preferredTypes != null && preferredTypes.size() > 1) {
  								result = preferredTypes.get(0);
  							} else if (directTypes != null && directTypes.size() > 1) {
  								result = directTypes.get(0);
  							} else if (allTypes != null && allTypes.size() > 1) {
  								result = allTypes.get(0);
  							} else {
  								System.out.println("OntologyManager: could not determine class for "+indv.getURI());
  							}

  						}
  					}
  				}
  			}
  		}
  	}
//System.out.println("pickRDFType:"+result.getURI());
  	return result;
  }
  /**
   * Gets a list of OntClass instances that are not super class of any other <br>
   * classes from the original list.
   * 
   */
  static List<OntClass> getNonSuperClasses (List<OntClass> inClasses) {
  	List<OntClass> result = null;
  	if (inClasses != null) {
  		result = new ArrayList<OntClass> ();
  		int size = inClasses.size();
  		for (int i = 0; i<size; i++) {
  			OntClass clsI = inClasses.get(i);
  			boolean isSuperClass = false;
  			for (int j=0; j<size; j++) {
  				if (i != j) {
  					OntClass clsJ = inClasses.get(j);
  					if (clsJ.hasSuperClass(clsI)) {
  						isSuperClass = true;
  						break;
  					}
  				}
  			}
  			if (!isSuperClass) {
  				result.add(clsI);
  			}
  		}
  	}
  	return result;
  }
  
  public static List<OntClass> getOrderedSuperClasses (OntClass cls) {
  	List<OntClass> result = null;
  	if (cls != null) {
  		result = new ArrayList<OntClass>();
  		List<OntClass> levelClss = new ArrayList<OntClass>();
  		levelClss.add(cls);
  		getSuperClasses(levelClss, result);
  	}
  	return result;
  }
  private static void getSuperClasses(List<OntClass>  lvClss, List<OntClass> classes) {
  	if (lvClss != null && lvClss.size()>0) {
  		ListIterator it = lvClss.listIterator();
  		while (it.hasNext()) {
  			classes.add((OntClass)it.next());
  		}
  		List<OntClass> nextLvClss = new ArrayList<OntClass>();
  		it = lvClss.listIterator();
  		while (it.hasNext()) {
  			OntClass cls = (OntClass)it.next();
  	  	ExtendedIterator supers = cls.listSuperClasses(true);
  	  	while (supers.hasNext()) {
  	  		nextLvClss.add((OntClass)supers.next());
  	  	}
  		}
  		getSuperClasses(nextLvClss, classes);
  	}
  	
  }

  public static void initOntologyCache(int size) {
  	if (size >0) ontologyCache = new ObjectCache(size);
  }

  public static int getCacheSize() {
  	return ontologyCache.getCapacity();
  }
  
  public static boolean setExpirationIntervalinMinutes (int newExpInter) {
  	return ontologyCache.setExpirationIntervalInMinutes(newExpInter);
  }
  
  public static int getExpirationInterval() {
  	return ontologyCache.getExpirationInterval();
  }
  
  public static void invalidateCache() {
  	ontologyCache.invalidateCache();
  }
  

}