/* Copyright 2007 Inference Web Group. All Rights Reserved. */

package org.inference_web.pml.context;

import java.util.*;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Top level PML version nutual data object. It has the following internal fields:<br>
 * <ol>
 *  <li> Ontology URI: URI of the ontology by which this object is defined.</li>
 *  <li> Ontology Class Name: Class name of the object in ontology.</li>
 *  <li> Object Class URI: Type of this object instance defined by the class URI which is the 
 *       combination of ontology and class name.</li>
 *  <li> Identifier: Identifier of the object description.</li>
 *  <li> Proerties: object's properties in Java Map.
 * </ol>
 *  
 *
 */
public interface DataObject{
    
  /**
   * Returns the URI of the ontology by which the PML object is defined.
   * @return the URI of the ontology
   */  
  public String getOntologyURI();
  
  /**
   * Sets the URI of ontology by which the object is define.
   * @param newOntURI the new URI of the ontology
   */
  public void setOntologyURI(String newOntURI);
  
  /**
   * Sets ontology class name to a new value.
   * @param newClassName new ontology class name
   */
  public void setOntologyClassName (String newClassName);
  
  /**
   * Returns  object's ontology class name.
   * @return ontology class name
   */
  public String getOntologyClassName();
  
  /**
   * Sets objects class URI to a new value.
   * @param newURI new class URI
   */
  public void setClassURI (String newURI);
  
  /**
   * Returns object's class URI.
   * @return class URI
   */
  public String getClassURI();

  /**
   * Returns object's ontology object
   * @return ontology
   */
  public org.inference_web.pml.context.accessor.Ontology getOntology() ;
  /**
   * Returns object's ontology class.
   * @return ontology class
   */
  public OntClass getOntologyClass();

  
  /**
   * Sets data object's identifier to a new value.
   * @param newID new ID
   */
  public void setIdentifier (DataObjectIdentifier newID);
  
  /**
   * Returns the identifier of the data object
   * @return identifier
   */
  public DataObjectIdentifier getIdentifier();


  /**
   * Returns a list of property names of the data object.
   * @return all property names
   */
  public List listPropertyNames();

  /**
   * Returns all properties of the data object in a Map. The keys of the map are
   * property names and values are property values.
   * @return properties of the Description
   */
  public Map getProperties();

  /**
   * Sets the properties of the data object.
   * @param newProperties new properties
   */
  public void setProperties(Map newProperties);
  
  /**
   * Answers if the data object has a specific property by its local name.
   * @param propName property's local name
   * @return true if the data object has the property
   */
  public boolean hasPropertyByLocalName(String propName);
  
  /**
   * Returns the value of a property.
   * @param propName property full name
   * @return value of property
   */
  public Object getProperty(String propName);
  
  /**
   * Sets a property to a new value.
   * @param propName property full name
   * @param propValue property value
   * @return true if setting new value succeeds
   */
  public boolean setProperty(String propName, Object propValue);

  /**
   * Returns the value of a property by its local name.The type of the value may vary, <br>
   * depending on currently stored value in property list.
   * @param propLocalName property's local name
   * @return value of the property
   */
  public Object getPropertyByLocalName (String propLocalName);

  /**
   * Returns the value of a property by its local name. The type of the value is determined by ontology.<br>
   * If the property is a object property, only data object instances can be returned.
   * @param propLocalName property's local name
   * @return value of the property 
   */
  public Object getPropertyObjectByLocalName (String propLocalName);
  
  /**
   * Sets a property by its local name to a new value.
   * @param propLocalName property local name
   * @param propValue property new value
   * @return true if setting new value succeeds
   */
  public boolean setPropertyByLocalName(String propLocalName, Object propValue);
  
  /**
   * Adds a new value to a property. If the property allows multiple values, the<br>
   * existing values are not altered.
   * @param propName property full name
   * @param propValue new property value
   */
  public void addPropertyValue(String propName, Object propValue);
  
  /**
   * Adds a new value to a property by property's local name.<br>
   * If the property allows multiple values, the<br>
   * existing values are not altered.
   * @param propLocalName property local name
   * @param propValue new property value
   */
  public void addPropertyValueByLocalName(String propLocalName, Object propValue);

  /**
   * Returns the range of the property in URI format.
   * @param propName property full name
   * @return range of the property
   */
  public String getPropertyRange (String propName);
  
  /**
   * Returns the Jena OntPropety of a property by its local name.
   * @param propLocalName property local name
   * @return OntProperty of the property
   */  
  public OntProperty getOntPropertyByLocalName (String propLocalName) ;
  
  /**
   * Answers if the property's range matches the given type.
   * @param propLocalName property local name
   * @param typeLocalName range's local name
   * @return true if property's range local name matches the type's local name
   */
  public boolean isPropertyRangeByLocalName (String propLocalName, String typeLocalName) ;
  
  /**
   * Returns the name of the data object.
   * @return Description's name
   */
  public String getObjectName();
  
  
  /**
   * Returns the namespace of the data object
   * @return namespace
   */
  public String getNameSpace();

  /**
   * Answers if data object is equivalent to another data object.
   * @param other data object
   * @return true if two data objects are equivalent
   */
  public boolean equals(DataObject other);


} /* END OF DataObject */
