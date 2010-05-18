/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.context;

import java.util.*;
import java.net.*;
import java.io.Serializable;

import org.inference_web.pml.context.accessor.*;
import org.inference_web.pml.shared.*;
import org.inference_web.pml.shared.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;

public class DataObjectImpl implements DataObject, Serializable {

  protected Map properties = new HashMap();
  // internal vars
  protected Resource objectClassResource = null;  
  protected DataObjectIdentifier objectIdentifier = null;

  public DataObjectImpl() {
  	
  }

  public DataObjectImpl(String ontologyURI, String ontologyClassName) {
  	
  	objectClassResource = ResourceFactory.createResource(ontologyURI+ontologyClassName);
  	objectIdentifier = null; // uri is not assigned initially
  	
  	properties = DataObjectManager.getInitProperties(ontologyURI+ontologyClassName);
       
  }
  
  public DataObjectImpl(String objectClassURI) {
 
  	if (objectClassURI != null) {
  		objectClassResource = ResourceFactory.createResource(objectClassURI);
  		objectIdentifier = null; 

    	properties = DataObjectManager.getInitProperties(objectClassURI);

  	}
  }
  
  public DataObjectImpl(DataObject dObj) {
  	
  	DataObjectManager.copyDataObject(dObj, this);
  	 
  }
  
  /* ======== METHODS ========= */
  
  public DataObjectIdentifier getIdentifier() {
  	return objectIdentifier;
  }

  public void setIdentifier(DataObjectIdentifier newID) {
    objectIdentifier = newID;
  }
  
  public String getOntologyURI() {
  	String result = null;
  	if (objectClassResource != null) {
  		result = objectClassResource.getNameSpace();
  	}
  	return result;
  }

  public void setOntologyURI(String newOntURI) {
  	if (objectClassResource == null) {
  		objectClassResource = ResourceFactory.createResource(newOntURI);
  	}
  	objectClassResource = ResourceFactory.createResource(newOntURI+objectClassResource.getLocalName());
  }
  public String getOntologyClassName() {
  	String result = null;
  	if (objectClassResource != null) {
  		result = objectClassResource.getLocalName();
  	}
  	return result;
  }

  public void setOntologyClassName(String newClassName) {
  	if (objectClassResource == null) {
  		objectClassResource = ResourceFactory.createResource(newClassName);
  	}
  	objectClassResource = ResourceFactory.createResource(objectClassResource.getNameSpace()+newClassName);
  }

  public String getClassURI() {
  	String result = null;
		if (objectClassResource != null) {
			result = objectClassResource.getURI();
		}  	
    return result;
  }
  
  public void setClassURI(String newURI) {
  	objectClassResource = ResourceFactory.createResource(newURI);
  }

  public OntClass getOntologyClass() {
  	OntClass result = null;
  	org.inference_web.pml.context.accessor.Ontology  ontology = getOntology();
  	if (ontology != null) {
  		result = ontology.getOntClass(getClassURI());
  	}
  	return result;
  }
  
  public org.inference_web.pml.context.accessor.Ontology getOntology() {
  	org.inference_web.pml.context.accessor.Ontology  result = null;
  	if (getOntologyURI() != null) {
  		result = OntologyManager.getOntology(getOntologyURI());
  	}  	
  	return result;
  }
  public String getNameSpace() {
  	String result = null;
  	if (objectIdentifier != null) {
  		result = objectIdentifier.getNameSpace();
  	}
  	return result;
  }

  public String getObjectName() {
  	String result = null;
  	if (objectIdentifier != null) {
  		result = objectIdentifier.getName();
  	}
  	return result;
  }
    
  public List listPropertyNames() {
  	List result = null;
  	if (!properties.keySet().isEmpty()) {
  		result = new ArrayList();
  		Iterator names = properties.keySet().iterator();
  		while (names.hasNext()) {
  			result.add((String)names.next());
  		}
  	}
  	return result;
  }

  public Map getProperties() {
    return properties;
  }
  
  public void setProperties(Map newProperties) {
    if (newProperties != null) {
      properties = newProperties;
    }
  }
  
  public Object getProperty(String propName) {
    Object result = null;
    if (properties.containsKey(propName)) {
      result = properties.get(propName);
    }
    return  result;
  }

  public Object getPropertyByLocalName (String propLocalName) {
  	Object result = null;
  	OntProperty property = getOntPropertyByLocalName(propLocalName);
  	if (property != null) {
  		result = getProperty(property.getURI());
  	}
  	return result;
  }
  
  public Object getPropertyObjectByLocalName (String propLocalName) {
  	Object result = null;
  	OntProperty property = getOntPropertyByLocalName(propLocalName);
  	if (property != null) {
  		result = getPropertyObject(property.getURI());
  	}
  	return result;
  }
  
  public Object getPropertyObject (String propName) {
  	Object result = null;
  	Object propValue = getProperty (propName);
  	if (propValue != null) {
  		Resource propRsc = propRsc = ResourceFactory.createResource(propName);
  		org.inference_web.pml.context.accessor.Ontology propOntology = OntologyManager.getOntology(propRsc.getNameSpace());
  		OntProperty property = propOntology.getProperty(propName);
  		if (propValue instanceof List) { // multi value
  			ArrayList newValues = new ArrayList();
  			ListIterator vlit = ((List)propValue).listIterator();
  			while (vlit.hasNext()) {
  				Object value = vlit.next();
  				newValues.add(getPropertyValueObject(property, value));
  			}
  			result = newValues;
  		} else {
  			result = getPropertyValueObject(property, propValue);
  		}
  	}
  	return result;
  }

  private Object getPropertyValueObject (OntProperty property , Object value) {
  	Object result = value;
  	if	(property.isDatatypeProperty() && value instanceof String) {
  		result = (String)value;
  	} else if ( property.isObjectProperty()) {
  		if (value instanceof String) {
  			result = DataObjectManager.getDataObject((String)value, null);
  		} else if (value instanceof DataObject) {
  			result = value;
  		} 
  	}
  	return result;
  }

  public boolean setPropertyByLocalName (String propLocalName, Object value) {

  	boolean result = false;
  	OntProperty property = getOntPropertyByLocalName(propLocalName);
  	if (property != null) {
  		setProperty (property.getURI(), value);
  		result = true;
  	}
  	return result;
  }
  
  public boolean setProperty(String propName, Object propValue) {
    boolean result = false;
//  	if (properties.containsKey(propName)) {
  			properties.put(propName, propValue);
  		result = true;
//  	}
    return result;
  }
  
  public void addPropertyValue(String propName, Object propValue) {
  	org.inference_web.pml.context.accessor.Ontology clsOntology = OntologyManager.getOntology(objectClassResource.getNameSpace());
  	Resource propRsc  = ResourceFactory.createResource(propName);
  	org.inference_web.pml.context.accessor.Ontology propOntology = OntologyManager.getOntology(propRsc.getNameSpace());
  	
  	if (properties.containsKey(propName)) {
  		int maxCard = clsOntology.getMaxCardinality(clsOntology.getOntClass(objectClassResource.getLocalName()), propOntology.getProperty(propName));
  		if ( maxCard > 1 || maxCard < 0) { // multiple values
  			ArrayList values = (ArrayList)properties.get(propName);
  			if (values == null) {
  				values = new ArrayList();
    			properties.put(propName, values);
  			} 
  			values.add((String)propValue);
  		} else {
  			properties.put(propName, propValue);
   		}
  	}
  }
  
  public void addPropertyValueByLocalName (String propLocalName, Object value) {
  	OntProperty property = getOntPropertyByLocalName(propLocalName);
  	if (property != null) {
  		addPropertyValue(property.getURI(), value);
  	}
  }

  public String getPropertyRange(String propName) {
  	String result = null;
  	org.inference_web.pml.context.accessor.Ontology clsOnt = OntologyManager.getOntology(getOntologyURI());  	
    if (properties.containsKey(propName)) {
    	result = clsOnt.getPropertyRange(clsOnt.getOntClass(getClassURI()),propName).getURI();
    }
    return  result;
  }
  
  
  public boolean hasPropertyByLocalName (String propLocalName) {
  	boolean result = false;
  	OntProperty property = getOntPropertyByLocalName(propLocalName);
  	if (property != null) result = true;
  	return result;
  }
  
  public OntProperty getOntPropertyByLocalName (String propLocalName) {
  	OntProperty result = null;
  	org.inference_web.pml.context.accessor.Ontology ontology = null;
  	Iterator pnames = properties.keySet().iterator();
  	boolean found = false;
  	while (pnames.hasNext() && !found) {
  		String pname = (String)pnames.next();
  		Resource propRsc = ResourceFactory.createResource(pname);
  		ontology = OntologyManager.getOntology(propRsc.getNameSpace());
  		OntProperty property = ontology.getProperty(pname);
  		if (property != null && property.getLocalName().equalsIgnoreCase(propLocalName)) {
  			result = property;
  			found = true;
  		}  		
  	}
  	return result;
  	
  }
  
  public boolean isPropertyRangeByLocalName (String propLocalName, String typeLocalName) {
  	boolean result = false;
  	OntProperty prop = getOntPropertyByLocalName(propLocalName);
  	if (prop != null && typeLocalName != null && !typeLocalName.equals("")) {
    	org.inference_web.pml.context.accessor.Ontology ontology = OntologyManager.getOntology(getOntologyURI());
      OntResource range = ontology.getPropertyRange(ontology.getOntClass(getClassURI()),prop);
      if (range == null) {
      	System.out.println("DataObjectImpl.isPropertyRangeByLocalName: property "+propLocalName+" has no range.");
      } else {
      	String rangeLocalName = range.getLocalName();
      	if (rangeLocalName.equalsIgnoreCase(typeLocalName.trim())) {
      		result = true;
      	}
      }
  	}
  	return result;
  }

  public boolean equals (DataObject other) {
  	boolean result = false;
  	if (other != null) {
  		if (!Config.isUnnamedConcept(this.getOntologyClassName()) && !Config.isUnnamedConcept(other.getOntologyClassName()) && 
  				this.getIdentifier() != null && other.getIdentifier() != null) {  		
  			result = this.getIdentifier().equals(other.getIdentifier());
  		} else {
  			if (this.getClassURI() != null && other.getClassURI() != null) {
  				try {
  					URI thisURI = new URI(this.getClassURI());
  					URI otherURI = new URI(other.getClassURI());
  					if (thisURI.equals(otherURI)) {
  						if (this.getProperties().size() == other.getProperties().size()) {
//System.out.println("ObjectDescription.equals: 2 objects have same amount of properties.");
  							boolean match = true;
  							Set propNames = this.getProperties().keySet();
  							Iterator pIt = propNames.iterator();
  							while (pIt.hasNext() && match) {
  								String propName = (String)pIt.next();
  								if ((this.getProperty(propName) == null && other.getProperty(propName) != null) ||
  										(this.getProperty(propName) != null && other.getProperty(propName) == null) ||
  										!other.getProperties().containsKey(propName)) {
//System.out.println("ObjectDescription.equals: one of the objects does not have value for "+propName);
  									match = false;
  								}
  								if ((this.getProperty(propName) != null && other.getProperty(propName) != null)) {
  									Object thisPropValue = this.getProperty(propName);
  									Object otherPropValue = other.getProperty(propName);
  									if (!thisPropValue.getClass().getName().equals(otherPropValue.getClass().getName())) {
//System.out.println("ObjectDescription.equals: 2 property values are not the same type.");
  										match = false;
  										if (thisPropValue instanceof DataObject && otherPropValue instanceof String) {
//System.out.println("ObjectDescription.equals: for prop "+propName+", one value od, one string");
  											if (((DataObject)thisPropValue).getIdentifier() != null && ToolURI.equalURI(((DataObject)thisPropValue).getIdentifier().getNameSpace()+((DataObject)thisPropValue).getIdentifier().getName(),(String)otherPropValue)) {
//System.out.println("ObjectDescription.equals: id matches string.");
  												match = true;
  											}
  										}
  										if (otherPropValue instanceof DataObject && thisPropValue instanceof String) {
//System.out.println("ObjectDescription.equals: for prop "+propName+", one value string one od.");
  											if (((DataObject)otherPropValue).getIdentifier() != null && ToolURI.equalURI(((DataObject)otherPropValue).getIdentifier().getNameSpace()+((DataObject)otherPropValue).getIdentifier().getName(),(String)thisPropValue)) {
//System.out.println("ObjectDescription.equals: string matches id.");
  												match = true;
  											}
  										}
  									} else { // same class instance
//System.out.println("ObjectDescription.equals: 2 objects have same class.");
  										if (thisPropValue instanceof String) {
  											if (!thisPropValue.equals(otherPropValue)) {
//System.out.println("ObjectDescription.equals: string match");
  												match = false;
  											}
  										} else if (thisPropValue instanceof DataObject){
//System.out.println("ObjectDescription.equals: will call to check od match.");
  											match = ((DataObject)thisPropValue).equals((DataObject)otherPropValue);
//System.out.println("ObjectDescription.equals: od match.");
  										} else if (thisPropValue instanceof List) {
  											match = comparePropertyValue((List)thisPropValue, (List)otherPropValue);
  										}
  									}    									
  								}
  							}
  							result = match;
  						}
  					}
  				} catch (Exception e) {
  					System.out.println(this.getClass().getName()+": can not compare "+this.getClassURI()+" and "+other.getClassURI());
  				}    		
  			}
  		}
  	}
  	return result;
  }

  private boolean comparePropertyValue(List propertyA, List propertyB) {
  	boolean result = true;
  	if (propertyA.size() != propertyB.size()) {
  		result = false;
  	} else {
  		Iterator pIta = propertyA.listIterator();
  		while (pIta.hasNext() && result) {
  			boolean match = false;
  			Object pa = pIta.next();
  			Iterator pItb = propertyB.listIterator();
  			while (pItb.hasNext() && !match) {
  				Object pb = pItb.next();
  				if (pa instanceof String && pb instanceof String && ((String)pa).equalsIgnoreCase((String)pb)) {
  					match = true;
  				} else if (pa instanceof DataObject && pb instanceof DataObject) {
  					match = ((DataObject)pb).equals((DataObject)pb);
  				} else if (pa instanceof DataObject && pb instanceof String) {
  					if (((DataObject)pa).getIdentifier() != null) {
  						match = ToolURI.equalURI(((DataObject)pa).getIdentifier().getNameSpace()+((DataObject)pa).getIdentifier().getName(),(String)pb);
  					}
  				} else if (pa instanceof String && pb instanceof DataObject) {
  					if (((DataObject)pb).getIdentifier() != null) {
  						match = ToolURI.equalURI(((DataObject)pb).getIdentifier().getURIString(),(String)pa);
  					}
  				} 
  			}
  			result = match;
  		}
  	}
  	return result;
  }

} /* END OF DataObjectImpl */
