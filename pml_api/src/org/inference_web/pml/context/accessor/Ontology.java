/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.context.accessor;

import java.util.*;

import org.inference_web.pml.context.accessor.*;
import org.inference_web.pml.shared.util.*;


import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.util.iterator.*;

public class Ontology {

  OntModel model = null; // model of spec (iw.owl)
  String defaultNS = "";
  Set ontClassURIs = new HashSet(); // contains all class URIs defined for the ontology
  Set ontClasses = new HashSet ();
  Map ontClassSuperClasses = new HashMap();
  Map ontClassPropertyURIAndCardinality = new HashMap(); // contains all property names of the class and cardinalities
  Map	ontProperties = new HashMap();
  private Set allOntologyURIs = null;
  
  public Ontology(String ontURI) {
  	String _uriMod = ontURI.replaceAll(" ","%20");

    model = DataObjectManager.getJenaModel(ontURI);
    defaultNS = _uriMod;
  	allOntologyURIs = model.listImportedOntologyURIs(true); // all ontologies included transitively
  	allOntologyURIs.add(defaultNS);
  	allOntologyURIs = OntologyManager.cleanUpOntologyURIs(allOntologyURIs);

  	initClassList();
  	initPropertyList();

  }
  
  public Ontology(String ontURI, String sourceURI) {
  	String _uriMod = ontURI.replaceAll(" ","%20");

    model = DataObjectManager.getJenaModelFromFile(sourceURI);
    defaultNS = _uriMod;
    
  	allOntologyURIs = model.listImportedOntologyURIs(true); // all ontologies included transitively
  	allOntologyURIs.add(defaultNS);    
  	allOntologyURIs = OntologyManager.cleanUpOntologyURIs(allOntologyURIs);
    
  	initClassList();
  	initPropertyList();

  }
  
  private void initClassList() {

    ExtendedIterator cit;
    int totalNumClasses = 0;
    if (model != null) {
      String clsURI = null;
      try {
        cit = model.listNamedClasses();
        while (cit.hasNext()) {
          OntClass cls = (OntClass) cit.next();
          
          if (cls.getNameSpace() != null && cls.getLocalName() != null) {
          	// for each class
            totalNumClasses++;
            clsURI = cls.getURI();
            ontClasses.add(cls);
            ontClassURIs.add(clsURI);
            ontClassPropertyURIAndCardinality.put(clsURI, null);
          }
        }
                
      } catch (Exception e) {
        System.out.println("Ontology: error processing class "+clsURI+"\n"+e.getMessage());
      }
      
    }
    else {
      System.out.println("Ontology.constructor: model null");
    }
  }

  private void initPropertyList() {
    
    ExtendedIterator pit = model.listAllOntProperties(); // all properties including object properties and datatype properties
    while (pit.hasNext()) {
      OntProperty currentProperty = (OntProperty)pit.next();
      if (OntologyManager.isIncludedInOntologyList(currentProperty.getNameSpace(), allOntologyURIs)) {
        String propUri = currentProperty.getURI();
        ontProperties.put(propUri, new PropertyAttribute(currentProperty));
      }
    }
  }
  
  private Map initClassPropertyList(OntClass oClass) {
  	
   	// class's directly declared properties and with restrictions on properties
		Set allProps = getOwnProperties(oClass); 

    // super classes own properties
		Iterator cIt ;
		OntClass superCls;
		// all class's super classes, their direct declared properties and with restrictions on properties
  	cIt = oClass.listSuperClasses();
  	while (cIt.hasNext()) {
  		superCls = (OntClass)cIt.next();
  		if (superCls.getURI() != null && OntologyManager.isIncludedInOntologyList(superCls.getNameSpace(), allOntologyURIs)) {
  			allProps.addAll(getOwnProperties(superCls));
  	    allProps.addAll(getOptionalProperties(superCls));
  		}
  	}
  	
  	// get any domain unspecified properties
    allProps.addAll(getOptionalProperties(oClass));

  	Set propUris = new HashSet();
  	Iterator propit = allProps.iterator();
  	Map propertyWithCardinality = new HashMap();

  	while(propit.hasNext()) {
  		OntProperty prop = (OntProperty)propit.next();
  		String propURI = prop.getURI();
  		propUris.add(propURI);
  		if (propUris.contains(propURI)){
  			propertyWithCardinality.put(propURI, null); // init setting
  		}
  	}
  	return propertyWithCardinality;
  }
  
  public String getNameSpace(String prefix) {
    String ns = null;
    if (model != null) {
      Map nsPrefixes = model.getNsPrefixMap();
      if (nsPrefixes.get(prefix) != null) {
        ns = (String) nsPrefixes.get(prefix);
      }
    }
    return ns;
  }

  public boolean hasClass(String clsURI) {
    boolean has = false;
    if (ontClassURIs != null) {
      has = ontClassURIs.contains(clsURI);
    }
    return has;
  }

  public List listClasseURIs() {
  	// convert a hash set to array liat
    List<String> clss = new ArrayList<String>();
    if (ontClassURIs != null && !ontClassURIs.isEmpty()) {
      Iterator it = ontClassURIs.iterator();
      while (it.hasNext()) {
        clss.add( (String) it.next());
      }
    }
    return clss;
  }

  public boolean hasProperty(String clsURI, String propURI, boolean all) {
    boolean has = false;
    
    List properties = listPropertyURIs(clsURI, !all);
    if (properties != null && properties.size()>0) {
    	has = properties.contains(propURI);
    }
    return has;
  }

  
  public List listPropertyURIs(String clsURI, boolean direct) {
    ArrayList result = new ArrayList(); 
    OntClass cls = getOntClass(clsURI);
    if (cls != null) { 
    	if (ontClassPropertyURIAndCardinality.keySet().contains(clsURI)) {
    		Map properties = getOneClassPropertyCardinality(clsURI);
    		if (direct) {
    			Set props = getOwnProperties(cls);
    			if (props != null && props.size()>0) {
    				result.addAll(props);
    			}
    		} else {
    			result.addAll((Set)properties.keySet());
    		}
    	} else {
    		System.out.println("listPropertyURIs: could not find class "+clsURI+" in ontClassPropertyURIAndCardinality");
    	}

    } else {
    	System.out.println("listPropertyURIs: could not get class from "+clsURI);
    }    
    return result;
  }

  public List listPropertyURIs(String clsURI) {
  	return listPropertyURIs(clsURI, false); // false means not only directly declared properties, but all
  }

  public OntClass getOntClass (String clsURI) {
    OntClass cls = null;
    OntClass tmpCls ;
    Iterator clsit = ontClasses.iterator();
    while (clsit.hasNext()) {
      tmpCls = (OntClass)clsit.next();
      if (ToolURI.equalURI(tmpCls.getURI(),clsURI)) {
        cls = tmpCls;
        break;
      }
    }
    return cls;
  }
  
  public String getOntClassLabel(String clsURI) {
  	String result = null;
  	OntClass ontCls = getOntClass(clsURI);
  	if (ontCls != null) {
  		result = ontCls.getLabel("en");
  		if (result == null) {
  			result = ontCls.getLabel(null);   	  
  		}
  		if (result == null) {
  			result = ontCls.getLocalName();   	  
  		}
  	}
  	return result;
  }
  
  public OntProperty getProperty(String propName) {
    OntProperty result = null;
    PropertyAttribute pa = getPropertyAttribute(propName);
    if (pa != null) {
    	result = pa.getProperty();
    }
    return result;
  }
  
  public PropertyAttribute getPropertyAttribute(String propName) {
  	PropertyAttribute result = null;
    Set propNames;    
    propNames = ontProperties.keySet();
    if (propNames.contains(propName.trim())) {
      result = (PropertyAttribute)ontProperties.get(propName.trim());
   } else {
    }
    return result;  	
  }
  

  public OntModel getModel () {
    return model;
  }
 
  public boolean isSubClassOf (OntClass cls, OntClass superCls) {
    boolean result = false;
    if (cls != null && superCls != null) {
      ExtendedIterator supers = cls.listSuperClasses(false);
      while (supers.hasNext() && !result) {
        OntClass superC = (OntClass)supers.next();
        if (superC.equals(superCls)) {
          result = true;
        }
      }
    }    
    return result;
  }
  
  public List getSubClasses (OntClass cls, boolean direct) {
    List<OntClass> subs =  null;
    if (cls != null) {
      ExtendedIterator subIt = cls.listSubClasses(direct);
      OntClass sub;
      while (subIt.hasNext()) {
        if (subs == null) {
          subs = new ArrayList<OntClass>();
        }
        sub = (OntClass)subIt.next();
        if (ontClasses.contains(sub)) {
          subs.add(sub);
        }
      }
    }
    return subs;
  }
  
  public List getSubClassNames (String className, boolean direct) {
    List<String> subs =  null;
    OntClass cls = getOntClass(className);
    if (cls != null) {
      ExtendedIterator subIt = cls.listSubClasses(direct);
      OntClass sub;
      while (subIt.hasNext()) {
        if (subs == null) {
          subs = new ArrayList<String>();
        }
        sub = (OntClass)subIt.next();
        if (ontClasses.contains(sub)) {
          subs.add(sub.getLocalName());
        }
      }
    }
    return subs;
  }

  public boolean hasSubClasses (OntClass cls, boolean direct) {
    boolean result = false;
    
    List subs =  getSubClasses (cls,direct);
    if (subs.size()>0) {
      result = true;
    }
    
    return result;
  }

  public int getMaxCardinality (OntClass oc, OntProperty prop) {
  	int result = -1;
  	PropertyCardinality cardis = getOnePropertyCardinality(oc, prop);
  	if (cardis != null) {
  		result = cardis.getMaxCardinality();
  	}
  	return result;
  }
  
  private int initOneClassPropertyMaxCardinality (OntClass oc, OntProperty prop) {
    int card = -1; 
    if (oc != null && prop!=null) {
    	card = initOnePropertyMaxCardinality(oc, prop);
      ExtendedIterator supProps = prop.listSuperProperties(true);
      while (supProps.hasNext() && card < 0) {
      	card = initOneClassPropertyMaxCardinality(oc, (OntProperty)supProps.next());
      }
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.initMaxCardinality: class  null");
      	if (prop == null)
      	System.out.println("Ontology.initMaxCardinality: property null");
    }
    return card ;
  }

  private int initOnePropertyMaxCardinality (OntClass oc, OntProperty prop) {
    int card = -1; 
    if (oc != null && prop!=null) {
      ExtendedIterator supClasses = oc.listSuperClasses(true);
      while (supClasses.hasNext() && card < 0) {
        OntClass supc = (OntClass)supClasses.next();
        if (supc.isRestriction()) {
          Restriction res = supc.asRestriction();
          if (res.isMaxCardinalityRestriction()) {
            MaxCardinalityRestriction mc = res.asMaxCardinalityRestriction();
            if (mc.onProperty(prop)) {
              card = mc.getMaxCardinality();
            }
          }
        }
      }
      if (card == -1) {
        ExtendedIterator supers = oc.listSuperClasses(true);
        while (supers.hasNext() && card < 0) {
          OntClass sc = (OntClass)supers.next();
          if (!sc.isRestriction()) {
            card = initOneClassPropertyMaxCardinality (sc, prop);
          }
        }
      } 
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.getOneMaxCardinality: class  null");
      	if (prop == null)
      	System.out.println("Ontology.getOneMaxCardinality: property null");
    }
//    if (card < 0) {
//      card = initPropertyCardinality(oc, prop);
//    }
    return card ;
  }
  
  public int getMinCardinality (OntClass oc, OntProperty prop) {
  	int result = -1;
  	PropertyCardinality cardis = getOnePropertyCardinality(oc, prop);
  	if (cardis != null) {
  		result = cardis.getMinCardinality();
  	}
  	return result;
  }
  private int initOneClassPropertyMinCardinality (OntClass oc, OntProperty prop) {
    int card = -1;
    if (oc != null && prop !=null) {
    	card = initOnePropertyMinCardinality(oc, prop);
      ExtendedIterator supProps = prop.listSuperProperties(true);
      while (supProps.hasNext() && card < 0) {
      	card = initOneClassPropertyMinCardinality(oc, (OntProperty)supProps.next());
      }
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.getMinCardinality: class  null");
      	if (prop == null)
      	System.out.println("Ontology.getMinCardinality: property null");
    }
    return card ;
  }
  
  private int initOnePropertyMinCardinality (OntClass oc, OntProperty prop) {
    int card = -1;
    if (oc != null && prop !=null) {
      ExtendedIterator superClasses = oc.listSuperClasses(true);
      while (superClasses.hasNext() && card < 0) {
        OntClass superc = (OntClass)superClasses.next();
        if (superc.isRestriction()) {
          Restriction res = superc.asRestriction();
          if (res.isMinCardinalityRestriction()) {
            MinCardinalityRestriction mc = res.asMinCardinalityRestriction();
            if (mc.onProperty(prop)) {
              card = mc.getMinCardinality();
            }
          }
        }
      }
      if (card == -1) {
        ExtendedIterator supers = oc.listSuperClasses(true);
        while (supers.hasNext() && card < 0) {
          OntClass sc = (OntClass)supers.next();
          if (!sc.isRestriction()) {
            card = initOneClassPropertyMinCardinality (sc, prop);
          }
        }
      } 
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.getOneMinCardinality: class  null");
      	if (prop == null)
      	System.out.println("Ontology.getOneMinCardinality: property null");
    }
//    if (card < 0) {
//      card = initPropertyCardinality(oc,prop);
//    }
    return card ;
  }
  
  
  public int getCardinality (OntClass oc, OntProperty prop) {
  	int result = -1;
  	PropertyCardinality cardis = getOnePropertyCardinality(oc, prop);
  	if (cardis != null) {
  		result = cardis.getCardinality();
  	} else {
  		System.out.println("did not get cardinalty");
  	}
  	return result;
  }
  
  private int initOneClassPropertyCardinality (OntClass oc, OntProperty prop) {
    int card = -1;
    if (oc != null && prop !=null) {
    	card = initOnePropertyCardinality(oc, prop);
      ExtendedIterator supProps = prop.listSuperProperties(true);
      while (supProps.hasNext() && card < 0) {
      	card = initOneClassPropertyCardinality(oc, (OntProperty)supProps.next());
      }
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.getCardinality: class null");
      if (prop == null)
      	System.out.println("Ontology.getCardinality: property null");
    }
    return card ;
  }
  
  private int initOnePropertyCardinality (OntClass oc, OntProperty prop) {
    int card = -1;
    if (oc != null && prop !=null) {
      ExtendedIterator superClasses = oc.listSuperClasses(true);
      while (superClasses.hasNext() && card < 0) {
        OntClass subc = (OntClass)superClasses.next();
        if (subc.isRestriction()) {
          Restriction res = subc.asRestriction();
          if (res.isCardinalityRestriction()) {
            CardinalityRestriction mc = res.asCardinalityRestriction();
            if (mc.onProperty(prop)) {
              card = mc.getCardinality();
            }
          }
        }
      }
      if (card == -1) {
        ExtendedIterator supers = oc.listSuperClasses(true);
        while (supers.hasNext() && card < 0) {
          OntClass sc = (OntClass)supers.next();
          if (!sc.isRestriction()) {
            card = initOneClassPropertyCardinality (sc, prop);
          }
        }
      } 
    } else {
    	if (oc == null) 
      	System.out.println("Ontology.getOneCardinality: class null");
      if (prop == null)
      	System.out.println("Ontology.getOneCardinality: property null");
    }
    return card ;
  }
  
  private Map getOneClassPropertyCardinality (String classURI) {
  	Map properties = null;
		if (ontClassPropertyURIAndCardinality.get(classURI)==null) {
			properties = initClassPropertyList(this.getOntClass(classURI));
			ontClassPropertyURIAndCardinality.put(classURI,properties);
		} else {
			properties = (Map)ontClassPropertyURIAndCardinality.get(classURI);
		}
		return properties;
  }
  
  private PropertyCardinality getOnePropertyCardinality(OntClass oClass, OntProperty property) {
  	PropertyCardinality cards = null;
  	String classURI = oClass.getURI();
  	String propertyURI = property.getURI();
  	Map allCardis = getOneClassPropertyCardinality(classURI);
  	if (allCardis != null) {
  		Object onePropCardis = allCardis.get(propertyURI);
  		if (onePropCardis == null) {
  			allCardis.put(propertyURI, new PropertyCardinality(oClass, property));
  		}
  		cards = (PropertyCardinality)allCardis.get(propertyURI);
  	}
  	return cards;
  }
  
  private static Set getOwnProperties (OntClass ontCls) {
  	Set result = new HashSet();

  	//  first, all directly declared properties to this class
		Iterator pit = ontCls.listDeclaredProperties(true);
		while (pit.hasNext()) {	
			OntProperty prop = (OntProperty)pit.next(); 
			result.add(prop);
			Iterator subPit = prop.listSubProperties();
			while (subPit.hasNext()) {
				result.add(subPit.next());
			}
		}
  	
		// second, all restrictions on properties for this class
  	OntClass superCls = null;
  	
  	pit = ontCls.listSuperClasses(true);
  	while (pit.hasNext()) {
  		superCls = (OntClass)pit.next();
  		if (superCls.isRestriction()) {
  			Restriction res = superCls.asRestriction();
  			OntProperty prop = res.getOnProperty();
  			result.add(prop);
  		}
  	}
  	return result;
  }
  
  /**
   * Returns a list of properties of an ontology class. These properties are
   * usually declared with no domain specified.
   * @param oCls ontology class
   * @return List of properties
   */
  private List getOptionalProperties(OntClass oCls) {

  	ArrayList otherProps = new ArrayList();	
  	Iterator props = oCls.listDeclaredProperties();
  	OntProperty prop = null;
  	Resource domain = null;
  	OntModel model = null;
  	OntClass domainCls = null;
  	while (props.hasNext()) {
  		prop = (OntProperty)props.next();
  		domain = getPropertyDomain(prop);
  		if (domain == null) { // no domain specified
  			// commented because hasContent shows up for everyone
//  			otherProps.add(prop);
  		} else { // check if any super property has domain specified
  			model = DataObjectManager.getJenaModel(domain.getNameSpace());
  			domainCls = model.getOntClass(domain.getURI());
  			if (oCls.equals(domainCls) || isSubClassOf(oCls, domainCls)) {
  				// only if the domain is the ont class its self or its super class
  				otherProps.add(prop);
  			}
  		}

  	}
  	return otherProps;
  }            
 
  private class PropertyAttribute {
  	OntProperty property = null;
  	OntResource range = null;
  	OntResource domain = null;
  	String label = null;
  	
  	PropertyAttribute(OntProperty property) {
  		if (property != null){
  			this.property = property;
//  			range = Ontology.getPropertyRange(property);
//  			domain = Ontology.getPropertyDomain(property);
  		}
  	}
  	
  	public OntProperty getProperty() {
  		return property;
  	}
  	public void setProerty (OntProperty newProp) {
  		property = newProp;
  	}
  	public OntResource getRange () {
  		if (range == null) {
  			range = initPropertyRange(property);
  		}
  		return range;
  	}
  	
  	public void setRange (OntResource newRange) {
  		range = newRange;
  	}
  	
  	public OntResource getDomain () {
  		if (domain == null) {
  			domain = initPropertyDomain(property);
  		}
  		return domain;
  	}
  	
  	public void setDomain (OntResource newDomain) {
  		domain = newDomain;
  	}
  	
  	public String getLabel () {
  		if (label == null) {
  			label = initPropertyLabel(property);
  		}
  		return label;
  	}
  	
  	public void setLabel (String newLabel) {
  		label = newLabel;
  	}
  }
 

  private class PropertyCardinality {
  	int maxCardinality = -1;
  	int minCardinality = -1;
  	int cardinality = -1;
  	
  	public PropertyCardinality (OntClass oClass, OntProperty property) {
  		cardinality = initOneClassPropertyCardinality(oClass, property);  		
  		if (cardinality != -1) {
  			minCardinality = cardinality;
  			maxCardinality = cardinality;
  		} else {
    		minCardinality = initOneClassPropertyMinCardinality(oClass, property);
    		maxCardinality = initOneClassPropertyMaxCardinality(oClass, property);
  			
  		}
  	}
  	public PropertyCardinality (int minCard, int maxCard, int card) {  
  		minCardinality = minCard;
  		maxCardinality = maxCard;
  		cardinality = card;
  	}
  	
  	public int getMinCardinality() {
  		return minCardinality;
  	}
  	public void setMinCardinality(int newMinCard) {
  		minCardinality = newMinCard;
  	}
  	public int getMaxCardinality() {
  		return maxCardinality;
  	}
  	public void setMaxCardinality(int newMaxCard) {
  		maxCardinality = newMaxCard;
  	}
  	public int getCardinality() {
  		return cardinality;
  	}
  	public void setCardinality(int newCard) {
  		cardinality = newCard;
  	}
  }

	public OntResource getPropertyRange (OntClass cls, OntProperty property) {
		OntResource result = getPropertyRange(property);
		if (result == null) {
      ExtendedIterator superClasses = cls.listSuperClasses(true);
      while (superClasses.hasNext() && result == null) {
        OntClass supc = (OntClass)superClasses.next();
        if (supc.isRestriction()) {
          Restriction res = supc.asRestriction();
          if (res.isAllValuesFromRestriction()) {
            AllValuesFromRestriction vr = res.asAllValuesFromRestriction();
            if (vr.onProperty(property)) {
            	result = (OntResource)vr.getAllValuesFrom();
            }
          }          
        }
      }
			
		}
		return result;
	}
	public OntResource getPropertyRange (OntClass cls, String propName) {
		OntResource result = getPropertyRange(propName);
		if (result == null) {
			org.inference_web.pml.context.accessor.Ontology propOnt = OntologyManager.getOntology(ResourceFactory.createResource(propName).getNameSpace());
			result = getPropertyRange(cls, propOnt.getProperty(propName));
		}
		return result;
	}
	
	private OntResource getPropertyRange (OntProperty property) {
		OntResource result = null;
    PropertyAttribute pa = getPropertyAttribute(property.getURI());
    if (pa != null) {
    	result = pa.getRange();
    }
		return result;
	}
	
	private OntResource getPropertyRange (String propName) {
		OntResource result = null;
    PropertyAttribute pa = getPropertyAttribute(propName);
    if (pa != null) {
    	result = pa.getRange();
    }
		return result;
	}
	
	private static OntResource initPropertyRange (OntProperty property) {
		OntResource result = null;
		OntResource range = null;
		Iterator ranges = property.listRange();
		while (ranges.hasNext() && range == null) {
	    range = (OntResource)ranges.next();
		}
	
		if (range == null) {
			Iterator superProps = property.listSuperProperties(true);
			while (superProps.hasNext() && range == null) {
				OntProperty superProp = (OntProperty)superProps.next();
				if (superProp != null ) {
					range = initPropertyRange(superProp);
				}
			}
		}  
		result = range;
		return result;
	}
	
  public Resource getPropertyDomain (OntProperty property) {
		OntResource result = null;
    PropertyAttribute pa = getPropertyAttribute(property.getURI());
    if (pa != null) {
    	result = pa.getDomain();
    }
		return result;
  }
  public Resource getPropertyDomain (String propName) {
		OntResource result = null;
    PropertyAttribute pa = getPropertyAttribute(propName);
    if (pa != null) {
    	result = pa.getDomain();
    }
		return result;
  }
  
  private static OntResource initPropertyDomain (OntProperty property) {
  	OntResource result = null;
  		if (property != null) {
  			result = property.getDomain();
  			if (result == null) {
  	      ExtendedIterator supProps = property.listSuperProperties(true);
  	      while (supProps.hasNext() && result == null) {
  	      	result = initPropertyDomain((OntProperty)supProps.next());
  	      }

  			}
  		}
  	return result;
  }
  public String getPropertyLabel(OntProperty prop) {
    String result = null;
    PropertyAttribute pa = getPropertyAttribute(prop.getURI());
    if (pa != null) {
    	result = pa.getLabel();
    }
    return result;
  }
  
  public String getPropertyLabel(String propName) {
    String result = null;
    PropertyAttribute pa = getPropertyAttribute(propName);
    if (pa != null) {
    	result = pa.getLabel();
    }
    return result;
  }
  
  private String initPropertyLabel(OntProperty prop) {
    String result = null;
    if (prop != null) {
      result = prop.getLabel("en");
    	if (result == null) {
    		result = prop.getLabel(null);
    	}
    	if (result == null) {
    		result = prop.getLocalName().trim();
    		if (result.startsWith("has")) {
    			result = result.substring(3);
    		} else if (result.startsWith("uses")) {
    			result = result.substring(4);
    		} else if (result.startsWith("is")) {
    			result = result.substring(2);
    		}
    	}
    }
    return result;
  }
 

}