/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlp.impl;

import java.io.Serializable;
import java.net.*;
import java.util.Iterator;
import java.util.List;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.v2.vocabulary.PMLP;

public class IWSourceUsageImpl extends PMLObjectImpl implements IWSourceUsage, Serializable {
  
	// currently only these properties are used
  String usageTimeV1PropName = "usageTime";
  String usageQueryContentV1PropName = "usageQuery";
  
  // special property names for document fragment source, needed for highlighting
  
  private String delimChar = "|";

  /* ===== CONSTRUCTORS ======= */
  
  public IWSourceUsageImpl() {
  }  
  public IWSourceUsageImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWSourceUsageImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }
  
  public IWSourceUsageImpl(DataObject desc) {
  	super(desc);	
  }
  public IWSource getHasSource() { 	
  	IWSource result = null;
  	Object src = getObjectPropertyValue(PMLP.hasSource_lname);
  	if (src == null) src = getObjectPropertyValue(usageTimeV1PropName);
  	if (src != null) {
  		result = (IWSource)PMLObjectManager.getPMLObject(src);
  	}
  	return result;
  }
  
  public void setHasSource(IWSource newSource) {
    setObjectPropertyValue(PMLP.hasSource_lname, newSource);
  }
  public void setHasSource(String sourceURI) {
    setObjectPropertyValue(PMLP.hasSource_lname, sourceURI);
  }
  
  public String getHasUsageDateTime() {
  	return getDataPropertyValue(PMLP.hasUsageDateTime_lname);
  }
  
  public void setHasUsageDateTime(String newUsageTime) {
  	if (this.hasPropertyByLocalName(PMLP.hasUsageDateTime_lname)) {
  		setDataPropertyValue(PMLP.hasUsageDateTime_lname, newUsageTime);
  	} else 	if (this.hasPropertyByLocalName(usageTimeV1PropName)) {
  		setDataPropertyValue(usageTimeV1PropName, newUsageTime);
  	} 	
  }
  
  public IWInformation getHasUsageQueryContent() {
  	IWInformation result = null;  	
    Object value = getObjectPropertyValue(PMLP.hasUsageQueryContent_lname);
    if (value == null) {
      value = getObjectPropertyValue(usageQueryContentV1PropName);    	
    }    
    if (value != null) result = (IWInformation)value;
    return result;
  }

  
  public void setHasUsageQueryContent(IWInformation newUsageQueryContent) {
  	if (this.hasPropertyByLocalName(PMLP.hasUsageQueryContent_lname)) {
  		setObjectPropertyValue(PMLP.hasUsageQueryContent_lname, newUsageQueryContent);
  	} else 	if (this.hasPropertyByLocalName(usageQueryContentV1PropName)) {
  		setObjectPropertyValue(usageQueryContentV1PropName, newUsageQueryContent);
  	}
  }
  
  private String getQueryString (){
  	String result = "";
  	IWInformation  query = getHasUsageQueryContent();
  	if (query != null) {
  		String rawString = query.getHasRawString();
  		if (rawString != null) result = rawString;
  	}
  	return result;
  }
  
  private String getUsageString () {
  	String result = "";
  	String usage = getHasUsageDateTime();
  	if (usage != null) result = usage;
  	return  result;
  }
  public String getSourceInfoStringFormat() {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: entering...");
  	String result = null;
  	IWSource source = getHasSource();
  	if (source != null && source.getIdentifier() != null && 
  			source.getIdentifier().getURIString() != null && source.getIdentifier().getURIString().length()>0) {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: found source uri "+source.getIdentifier().getURIString());

  		result = source.getIdentifier().getURIString() + delimChar + getQueryString() +
  		delimChar + getUsageString();
  	} else {
  		if (isSourceDocumentFragment()) {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: is doc frag source");
  			IWSource dfs = getHasSource();
  			if (dfs != null) {	
  				DataObject doc =  (DataObject)dfs.getPropertyObjectByLocalName(PMLP.hasDocument_lname);
  				if (doc != null) {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: source document not null ");
  					DataObject docContent = (DataObject)doc.getPropertyObjectByLocalName(PMLP.hasContent_lname);
  					if (docContent != null) {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: source document content not null ");
  						String docUrl = (String)docContent.getPropertyByLocalName(PMLP.hasURL_lname);
  						if (docUrl != null) {
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: source document contnet url not null ");
  							try {
  								result =  URLEncoder.encode(docUrl,"UTF-8") + delimChar + getQueryString() +
  								delimChar + getUsageString();

  							} catch (Exception e) {
  								System.out.println("IWSourceUsage.getSourceInfoStringFormat: failed to encode document URL "+docUrl);
  							}
  						}
  					}
  				} else {
  					System.out.println("source document null");
  				}
  			}

  		}

  	}
//System.out.println("IWSourceUsageImpl.getSourceInfoStringFormat: exiting with "+result);
  	return result;
  }


  public String getSourceUsageStringForDocumentFragmentSource() {
  	String result = null;
  	if (isSourceDocumentFragment()) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentFragmentSource: is doc frag source");
  		IWSource dfs = getHasSource();
  		if (dfs != null) {	
   			DataObject doc =  (DataObject)dfs.getPropertyObjectByLocalName(PMLP.hasDocument_lname);
  			if (doc != null) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentGragmentSource: source document not null ");
  				DataObject docContent = (DataObject)doc.getPropertyObjectByLocalName(PMLP.hasContent_lname);
  				if (docContent != null) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentGragmentSource: source document content not null ");
  					String docUrl = (String)docContent.getPropertyByLocalName(PMLP.hasURL_lname);
  					if (docUrl != null) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentGragmentSource: source document contnet url not null ");
  						try {
  							result =  URLEncoder.encode(docUrl,"UTF-8") + delimChar + getQueryString() +
  							delimChar + this.getUsageString();
  							
  							String srcDocType = dfs.getOntologyClassName();
  							if (srcDocType.endsWith("Offset")) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentGragmentSource: source offset type ");
  								String from = (String)dfs.getPropertyByLocalName(PMLP.hasFromOffset_lname);
  								String to = (String)dfs.getPropertyByLocalName(PMLP.hasToOffset_lname);
  								if (from != null && to != null ) {
  									result += delimChar +from+delimChar+to;
  								}
  							} else if (srcDocType.endsWith("RowCal")) {
//System.out.println("IWSourceUsage.getSourceUsageStringForDocumentGragmentSource: source rowcal type ");
  								String fromRow = (String)dfs.getPropertyByLocalName(PMLP.hasFromRow_lname);
  								String fromCol = (String)dfs.getPropertyByLocalName(PMLP.hasFromCol_lname);
  								String toRow = (String)dfs.getPropertyByLocalName(PMLP.hasToRow_lname);
  								String toCol = (String)dfs.getPropertyByLocalName(PMLP.hasToCol_lname);
  								if (fromRow != null && fromCol != null && toRow != null && toCol != null) {
  									result += delimChar + fromRow + delimChar + fromCol + 
  									delimChar + toRow + delimChar + toCol; 
  								}
  							} else {
  								System.out.println("IWSourceUsage.getSourceUsageStringForDocumentFragmentSource: source unrecongnized fragment type "+srcDocType+". ");
  							}
  						} catch (Exception e) {
  							System.out.println("IWSourceUsage.getSourceUsageStringForDocumentFragmentSource: failed to encode document URL "+docUrl);
  						}
  					}
  				}
  			} else {
  				System.out.println("source document null");
  			}
  		}
  		
  	}
  	return result;
  }

  public String getV1SourceUsageString() {
  	String result = null;
  	if (isV1SourceUsage()) {
  		IWSource src = getHasSource();
  		if (src != null) { 
  			String docUrl = (String)src.getPropertyByLocalName("URL");
  			if (docUrl == null) {
  				docUrl = (String)src.getPropertyByLocalName("url");
  			}
  			if (docUrl != null) {
  				String from = (String)getPropertyByLocalName("spanFromByte");
  				String to = (String)getPropertyByLocalName("spanToByte");
  				if (from != null && to != null ) {
  					try {
    				result =  URLEncoder.encode(docUrl,"UTF-8") + delimChar + getQueryString() +
  						delimChar + getUsageString() + delimChar +from+delimChar+to;
						} catch (Exception e) {
							System.out.println("IWSourceUsage.getV1SourceUsageString: failed to encode document URL "+docUrl);
  					} 
  				}
  			} else {
  				System.out.println("IWSourceUsage.getV1SourceUsageString: docurl null");
  			}
  		} else {
  			System.out.println("IWSourceUsage.getV1SourceUsageString:  source null");
  		}
  	}
  	return result;
  }

  public boolean isSourceDocumentFragment() {
  	boolean result = false;
  	IWSource source = getHasSource();
  	if (source != null && source.getOntologyClassName().startsWith("DocumentFragment")) {
  		result = true;
  	}
  	return result;
  	
  }
  
  public boolean isV1SourceUsage() {
  	boolean result = false;
  	String v1OntUriStr =  "http://inferenceweb.stanford.edu/2004/07/iw.owl#";
  	String thisOntUriStr = getOntologyURI();
  	
  	try {
  		URI v1Uri = new URI(v1OntUriStr);
  		URI thisOntUri = new URI (thisOntUriStr);
  		if (thisOntUri.equals(v1Uri)) {
  			result = true;
  		}
  	} catch (Exception e){
  		System.out.println("IWSourceUsage.isV1SourceUsage: failed compare two ontology uris: "+v1OntUriStr+" and "+thisOntUriStr); 
  		e.printStackTrace();
  	}
//System.out.println("isV1SourceUsage: "+result);
  	return result;
  }
  
	private String namePropName = null; // v1 v2 differ
  private void setNamePropertyName() {
  	if (properties != null) {
  		boolean found = false;
  		Iterator propNames = properties.keySet().iterator();
  		while (propNames.hasNext() && !found) {
  			String propName = (String)propNames.next();
  			if (propName.indexOf("#name")>=0 || propName.indexOf("#hasName")>=0 ) {
  				namePropName = propName;
  				found = true;
  			}
  		}
  	} else {
  		System.out.println("IWSourceUsage: properties map null");
  	}
  }

	public String getNamePropertyName () {
		if (namePropName == null) {
			setNamePropertyName();
		}
		return namePropName;
	}
	
	public String getHasName () {
		String result = null;
		Object value = getProperty(getNamePropertyName());
		if (value != null) result = (String)value;
		return result;
	}
	
	public void setHasName (String newName) {
		setProperty(getNamePropertyName(),newName);
	}

	public String getHasCreationDateTime () {
		String result = null;
		Object value = getDataPropertyValue(PMLP.hasCreationDateTime_lname);
		if (value != null) result = (String)value;
		return result;
	}
	public void setHasCreationDateTime (String newDateTime) {
		setDataPropertyValue(PMLP.hasCreationDateTime_lname, newDateTime);
	}
	
	public List getHasAuthorList () {
		return getListObjectPropertyValue(PMLP.hasAuthorList_lname);
	}
	
	public void setHasAuthorList (List newAuthors) {
		setListObjectPropertyValue(PMLP.hasAuthorList_lname, newAuthors);
	}
	
  public void addHasAuthor(IWAgent author){
  	addMultipleValueObjectPropertyValue(PMLP.hasAuthorList_lname, author);
  }
  public void addHasAuthor(String authorURI){
  	addMultipleValueObjectPropertyValue(PMLP.hasAuthorList_lname, authorURI);
  }

	public List getHasDescription () {
		return getMultipleValueObjectPropertyValue(PMLP.hasDescription_lname);
	}
	
	public void setHasDescription (List newDescriptions) {
		setMultipleValueObjectPropertyValue(PMLP.hasDescription_lname, newDescriptions);
	}
	
	public void addHasDescription(IWInformation newDescription) {
		addMultipleValueObjectPropertyValue(PMLP.hasDescription_lname, newDescription);
	}
	
	public IWAgent getHasOwner () {
		IWAgent result = null;
		Object propValue = getObjectPropertyValue(PMLP.hasOwner_lname);
		if (propValue != null) 
			try{
				result = (IWAgent) propValue;				
			} catch (Exception e) {
		}
		return result;		
	}
	
	public void setHasOwner (IWAgent newOwner) {
		setObjectPropertyValue(PMLP.hasOwner_lname, newOwner);
	}
	public void setHasOwner (String ownerURI) {
		setObjectPropertyValue(PMLP.hasOwner_lname, ownerURI);
	}
} /* END OF IWSourceUsageImpl */
