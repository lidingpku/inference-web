  /* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.context.accessor;

import java.util.*;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.context.DataObjectIdentifier;
import org.inference_web.pml.context.DataObjectImpl;
import org.inference_web.pml.shared.util.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.*;

public class Reader  {
	
	/**
	 * Creates a data object from  URI.
	 * @param URIStr URI of data object
	 * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
	 * @return data object
	 */
  public static DataObject loadDataObject (String URIStr, Set<String> preferredOntologyURIs) {
//System.out.println("Reader:loadDataObject: entering with "+URIStr);
    DataObject result = null;
    String logMessage = null;
    Individual pmInd = null;
    try {
      pmInd = DataObjectManager.getIndividual(URIStr);
      if (pmInd != null) {
        result = getDataObject(pmInd,preferredOntologyURIs);
      } else {
      	System.out.println("DataObjectManager could not get individual "+URIStr);
      }
    } catch (Exception e) {
      logMessage = "Reader.loadDataObject: Failed create individual from "+URIStr+".\n"+e.getMessage();
    }
    if (logMessage != null) {
      System.out.println(logMessage);
    }
    return result;    
  }
  
  /**
   * Create a data object from source file
   * @param URIStr the URI of data object
   * @param sourceFileURL the URI of the source file
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return data object
   */
  public static DataObject loadDataObjectFromFile(String URIStr, String sourceFileURL, Set<String> preferredOntologyURIs) {
    DataObject result = null;
    String logMessage = null;
    Individual pmInd = null;
    
    try {
      pmInd = DataObjectManager.getIndividualFromFile(URIStr, sourceFileURL);
      if (pmInd != null) {
        result = getDataObject(pmInd, preferredOntologyURIs);
      }
    } catch (Exception e) {
      logMessage = "Reader.loadDataObject: Failed create individual from "+URIStr+".\n"+e.getMessage();
    }
    if (logMessage != null) {
      System.out.println(logMessage);
    }
    return result;    
  }
  
  /**
   * Create a data object from string object
   * @param URIStr the URI of data object
   * @param sourceString the string object
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return data object
   */
  public static DataObject loadDataObjectFromString(String URIStr, String sourceString,Set<String> preferredOntologyURIs) {
    DataObject result = null;
    String logMessage = null;
    Individual pmInd = null;
    
    try {
      pmInd = DataObjectManager.getIndividualFromString(URIStr, sourceString);
      if (pmInd != null) {
        result = getDataObject(pmInd, preferredOntologyURIs);
      }
    } catch (Exception e) {
      logMessage = "Reader.loadDataObject: Failed create individual from "+URIStr+".\n"+e.getMessage();
    }
    if (logMessage != null) {
      System.out.println(logMessage);
    }
    return result;    
  }
  
  /**
   * Create a data object from Jena OntModel instance
   * @param URIStr the URI of the data object
   * @param model the Jena model
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return data object
   */
  public static DataObject loadDataObject (String URIStr, OntModel model, Set<String> preferredOntologyURIs) {
    DataObject result = null;
    String logMessage = null;
    Individual pmInd = null;
    
    try {
      pmInd = DataObjectManager.getIndividual(URIStr, model);
      if (pmInd != null) {
        result = getDataObject(pmInd, preferredOntologyURIs);
      }
    } catch (Exception e) {
      logMessage = "Reader.loadDataObject: Failed create individual from "+URIStr+".\n"+e.getMessage();
    }
    if (logMessage != null) {
      System.out.println(logMessage);
    }
    return result;    
  }
  
  /**
   * Creates a data object from a Jena Individual.
   * @param pmInd Individual
   * @param preferredOntologyURIs a set of ontology URIs that is used in favor when an individual with mutilple<br>
	 * type definitions
   * @return object description
   */
  private static DataObject getDataObject (Individual pmInd, Set<String> preferredOntologyURIs) {
  	DataObject result = null;
  	String logMessage = null;
  	org.inference_web.pml.context.accessor.Ontology ont = null;
  	DataObject dObj = null;

  	if (pmInd != null) {
  		Resource doOntType = OntologyManager.pickRDFType(pmInd, preferredOntologyURIs);
  		String ontUri = doOntType.getNameSpace();
  		String ontClassName = doOntType.getLocalName();
  		String classURI = doOntType.getURI();
  		// check if this is not property individual
  		if (!ToolURI.equalURI(classURI, "http://www.w3.org/2002/07/owl#ObjectProperty") &&
  				!ToolURI.equalURI(classURI, "http://www.w3.org/2002/07/owl#DatatypeProperty")) {
  			dObj = new DataObjectImpl(classURI);

  		dObj.setIdentifier(new DataObjectIdentifier(pmInd.getURI()));
  		ont = OntologyManager.getOntology(ontUri);
  		try {
  			Class resourceClass = Class.forName("com.hp.hpl.jena.rdf.model.Resource");
  			Class rdfListClass = Class.forName("com.hp.hpl.jena.rdf.model.RDFList");
  			Class individualClass = Class.forName("com.hp.hpl.jena.ontology.Individual");
  			
  			String propName ;
  			Object propValue;
  			RDFNode valueNode;
  			RDFList valueList;


//  			List propValues;
//  			int maxCar, minCar;
  			OntClass cls = ont.getOntClass(classURI);
  			OntProperty property;
 
 				// load properties defined only by the class
//			Iterator propNames = ont.listPropertyURIs(dObj.getClassURI(),false).iterator();
// 			  while (propNames.hasNext()) {
//  				propName = (String)propNames.next();

  			// load properties defined by document author (everything in document)
   			StmtIterator propsInInd = pmInd.listProperties();  			
  			while (propsInInd.hasNext()) {
  				propName = propsInInd.nextStatement().getPredicate().getURI();
  				if (!ToolURI.equalURI(propName, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
//System.out.println("     start prop "+propName+": "+ParameterHelper.getCurrentTimeStr2());
//System.out.println("** Reader: property "+propName);				
  				DataObjectIdentifier propID = DataObjectManager.getObjectID(propName);
  				org.inference_web.pml.context.accessor.Ontology propOnt = OntologyManager.getOntology(propID.getNameSpace());
  				property = propOnt.getProperty(propName);
  				if (property != null) {
	  				valueNode = pmInd.getPropertyValue(property);
	  				if (valueNode != null) {
						int maxCard = ont.getMaxCardinality(cls,property); 
//System.out.println("Reader: max car="+maxCard);
  					if (maxCard == 1) {
//System.out.println("Reader: for property " + property.getLocalName()+" maxCar = 1");
  							if (valueNode.canAs(rdfListClass)) {
  								propValue = getListPropertyValue((RDFList) valueNode.as(rdfListClass),preferredOntologyURIs);
  							} else {
  								propValue = getPropertyValue(valueNode,preferredOntologyURIs);
  							}
  							if (propValue != null) {
//System.out.println("set property value for "+propName);
  								dObj.setProperty(propName, propValue);
  							} else {
  								System.out.println("Reader.getDataObject: Improper property value found for "+propName+" in "+pmInd.getURI());
  							}
  					}	else if ( maxCard < 0 || maxCard > 1) { // <0: no max set = multiple
  							// for multiple value property defined by list, can not get property type from ontology (which returns list type)
//System.out.println("Reader: for property " + property.getLocalName()+" maxCar < 0");
  							if (valueNode.canAs(rdfListClass)) {
//System.out.println("Reader: for property " + property.getLocalName()+" value is list.");
  								valueList = (RDFList) valueNode.as(rdfListClass);
  								List propertyValues = getListPropertyValue(valueList,preferredOntologyURIs);
  								if (propertyValues.size() > 0) {
  									dObj.setProperty(propName, propertyValues);
  								}
  							} else {
//System.out.println("Reader: for property " + property.getLocalName()+" value is iterator.");
  								ArrayList propertyValues = new ArrayList();
  								NodeIterator propIter = pmInd.listPropertyValues(property);
  								while (propIter.hasNext()){
  									valueNode = (RDFNode) propIter.next();
  									if (property.isDatatypeProperty()) {
  										if (valueNode instanceof com.hp.hpl.jena.rdf.model.Literal) {
  											propertyValues.add(((com.hp.hpl.jena.rdf.model.Literal) valueNode).getString());
  										} else {
  											propertyValues.add(valueNode.toString());
  										}
  									} else if (property.isObjectProperty() ) {
  										if (valueNode instanceof com.hp.hpl.jena.rdf.model.Resource) {
  											String uriStr = ((Resource)valueNode.as(resourceClass)).getURI();
  											if (uriStr != null && !uriStr.equals("")) {
  												propertyValues.add(uriStr);
  											} else {
  												Object oneValue = Reader.getDataObject((Individual)valueNode.as(individualClass), preferredOntologyURIs);
  												propertyValues.add(oneValue);
  											}
  										} else if ((valueNode instanceof com.hp.hpl.jena.rdf.model.Literal)) {
  											propertyValues.add(((com.hp.hpl.jena.rdf.model.Literal) valueNode).getString());
  										} else {
  											propertyValues.add(valueNode.toString());  											
  										}
  									} else {
  										if (valueNode instanceof com.hp.hpl.jena.rdf.model.Literal) {
  											propertyValues.add(((com.hp.hpl.jena.rdf.model.Literal) valueNode).getString());
  										} else {
  											propertyValues.add(valueNode.toString());
  										}
  									}
  								}
  								if (propertyValues.size() > 0 ) {
  									dObj.setProperty(propName,propertyValues);
  								}
  							}  
  					}
  				}
  				} else {
  					// these properties are still in 2004/07 ontology, ignore
  					if (!propName.endsWith("explanation") && !propName.endsWith("consequent"))
  					System.out.println("Reader: could not get property "+propName+" from ontology for "+propID.getNameSpace());
  				}
  			}
  			}
  			result = dObj;

  		} catch (Exception e) {
  			if (logMessage == null) {
  				logMessage = "";
  			} else {
  				logMessage +="\n";
  			}
  			logMessage += "Reader.getObjectObject: Unable to create DataObject from individual uri "+pmInd.getURI()+"\n"+e.getMessage();
  		}
  		if (logMessage != null) {
  			System.out.println(logMessage);
  		}
  	}
  	} else {
  		System.out.println("Reader: could not get data object from null individual.");
  	}
//System.out.println("Reader: end of getDataObject for "+pmInd.getURI());
  	return result;    
  }

  private static List getListPropertyValue (RDFList valueList, Set<String> preferredOntologyURIs) {
  	RDFNode valueNode = null;
  	ArrayList propertyValues = null;
  	if (valueList != null) {
  		try {
  			Class resourceClass = Class.forName("com.hp.hpl.jena.rdf.model.Resource");
  			Class rdfListClass = Class.forName("com.hp.hpl.jena.rdf.model.RDFList");
  			Class individualClass = Class.forName("com.hp.hpl.jena.ontology.Individual");

  			propertyValues = new ArrayList();

  			Iterator vIt = valueList.iterator();
  			while (vIt.hasNext()) {
  				valueNode = (RDFNode) vIt.next();
  				if (valueNode != null && valueNode instanceof com.hp.hpl.jena.rdf.model.Literal) { 	
  					propertyValues.add(((com.hp.hpl.jena.rdf.model.Literal) valueNode).getString());
  				} else if (valueNode !=null && (valueNode instanceof com.hp.hpl.jena.rdf.model.Resource)) {
  					String uriStr = ((Resource)valueNode.as(resourceClass)).getURI();
  					if (uriStr != null && !uriStr.equals("")) {
  						propertyValues.add(uriStr);
  					} else {
  						propertyValues.add(Reader.getDataObject((Individual)valueNode.as(individualClass), preferredOntologyURIs));
  					}
  				}
  			}
  		} catch (Exception e) {
  			System.out.println("Reader: unable to get property value.\n"+e.getMessage());
  		}

  	}
  	return propertyValues;
  }
  
  private static Object getPropertyValue (RDFNode valueNode, Set<String> preferredOntologyURIs) {

  	Object result = null;
  	Object tempValue = null;
  	
		RDFList valueList;
		try {
			Class resourceClass = Class.forName("com.hp.hpl.jena.rdf.model.Resource");
			Class rdfListClass = Class.forName("com.hp.hpl.jena.rdf.model.RDFList");
			Class individualClass = Class.forName("com.hp.hpl.jena.ontology.Individual");

			if (valueNode != null) {
				if (valueNode instanceof com.hp.hpl.jena.rdf.model.Literal) { 	              
					result =  ((com.hp.hpl.jena.rdf.model.Literal) valueNode).getString();
				} else if ((valueNode instanceof com.hp.hpl.jena.rdf.model.Resource)) {
					String uriStr = ((Resource)valueNode.as(resourceClass)).getURI();
					if (uriStr != null && !uriStr.equals("")) {
//						result = ((Resource) valueNode.as(resourceClass)).getURI();
						result = uriStr;
					} else {
						result = Reader.getDataObject((Individual)valueNode.as(individualClass), preferredOntologyURIs);
					}
				} else if ((valueNode.canAs(rdfListClass))) {
					valueList = (RDFList) valueNode.as(rdfListClass);
					ArrayList propertyValues = new ArrayList();
					Iterator vIt = valueList.iterator();
					while (vIt.hasNext()) {
						valueNode = (RDFNode) vIt.next();
						tempValue = getPropertyValue(valueNode, preferredOntologyURIs);
						if (tempValue != null) {
							propertyValues.add(tempValue);
						}
					}
					if (propertyValues.size()>0) {
						result = propertyValues;
					}
				} else {
					result = valueNode.toString();
				}				
			}
		} catch (Exception e) {
			System.out.println("Reader: unable to get property value.\n"+e.getMessage());
		}
		return result;    
  }

}
/* END OF Reader */