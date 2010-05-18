/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj.impl;

import java.util.*;
import java.net.*;
import java.io.*;

import org.inference_web.pml.context.DataObject;
import org.inference_web.pml.context.accessor.*;
import org.inference_web.pml.v2.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.*;
import org.inference_web.pml.v2.vocabulary.*;



public class IWNodeSetImpl extends PMLObjectImpl implements IWNodeSet, Serializable {
	
	protected final String infStepPropName = PMLJ.isConsequentOf_lname;
	protected final String hasIndex = PMLJ.hasIndex_lname;

  protected int selectedInferenceStepIndex = -1;

  /* ==== CLASS VARIABLES ===== */

//  protected static HashMap _loadedNodeSet = new HashMap();
//  protected static HashMap _loadedNodeSetOccurrences = new HashMap();
//  protected static HashMap _loadedModels = new HashMap();

  /* ====== ATRIBUTES ========= */

//  protected String _conclusion;
//  protected String _language;
  protected String _englishString=""; // n3 uses
//  protected IWFormat _format = null;
//  protected Individual _obj;

//  protected List _inferenceSteps; // of IWInferenceStep
//  protected boolean _fullLoaded;
//  protected String _proof;
  public int _depth;

//  protected List conclusionFillers_;
  
//  private List n3Formulas = new ArrayList();


//	private IWNodeSet m_root = null;
//	private List m_oLocalPrettyNameMappings = new ArrayList();
//	private List m_oPropPrettyNameMappings = new ArrayList();

  /* ===== CONSTRUCTORS ======= */

  public IWNodeSetImpl() {
    super();
  }
  public IWNodeSetImpl(String ontologyURI, String ontologyClassName) {
  	super(ontologyURI, ontologyClassName);
  }
  
  public IWNodeSetImpl(String resourceTypeURI) {
  	super(resourceTypeURI);
  }
	
	public IWNodeSetImpl(DataObject desc) {
		super(desc);	
	}
  

	public int getDepth() {
	  return _depth;
	}
	
	public void setDepth (int depth) {
	  _depth = depth;
	}

	public boolean isAxiom() {
		boolean result = true;
		IWNodeSet tempNS = PMLObjectManager.getNodeSet(this.getIdentifier().getURIString(), 2);
		IWInferenceStep infStep = null;
		List infSteps = tempNS.getInferenceSteps();
		if (infSteps != null && infSteps.size()>0) {
			Iterator iit = infSteps.listIterator();
			boolean found = false;
			while (iit.hasNext() && !found) {
				infStep = (IWInferenceStep)iit.next();
				if (infStep.hasAntecedents()) {
					found = true;
					result = false;
				}
			}
		}
		return result;
	}

  public Map getConclusionsInGraph() {
    Map result = null;
    HashMap conclusions = new HashMap();
    getConclusions(this, conclusions);
    result = (Map)conclusions;
    return result;
  }

  private void getConclusions(IWNodeSet ns, Map result) {
  	String currentConclusion = ns.getConclusionRawString();
  	if (currentConclusion != null) {
  		result.put(ns, currentConclusion);
  	}

  	ListIterator tempIt = null; // inference step iterator
  	ListIterator tempIt2 = null; // antecedent iterator

  	IWInferenceStep currentInfStep = null;
  	List antecedentsList = null;
  	IWNodeSet antNodeSet = null;
  	IWNodeSet currentNodeSet = ns;
  	List infSteps = getInferenceSteps();
  	if (infSteps != null) {
  		tempIt = infSteps.listIterator();
  		while (tempIt.hasNext()) {
  			currentInfStep = (IWInferenceStep) tempIt.next();
  			antecedentsList = (List)currentInfStep.getAntecedents();
  			if (antecedentsList != null && antecedentsList.size() >0) {
  				tempIt2 = antecedentsList.listIterator();
  				while (tempIt2.hasNext()) {
  					try {
  					antNodeSet = (IWNodeSet)tempIt2.next();
  					if (antNodeSet != null) {
  						getConclusions(antNodeSet, result);
  					}
  					} catch (Exception e) {
  					}
  				}
  			}
  		}
  	}
  }
  
  public List getAntecedentURIsInGraph() {
    List result = null;
    List antecedents = new ArrayList();
    findAntecedentsInGraph(this, antecedents);
    result = antecedents;
    return result;
  }

  private void findAntecedentsInGraph(IWNodeSet ns, List result) {
  	if (ns.getIdentifier() != null) {
  		result.add(ns.getIdentifier());
  	}

  	ListIterator tempIt = null; // inference step iterator
  	ListIterator tempIt2 = null; // antecedent iterator

  	IWInferenceStep currentInfStep = null;
  	List antecedentsList = null;
  	IWNodeSet antNodeSet = null;
  	IWNodeSet currentNodeSet = ns;
  	List infSteps = getInferenceSteps();
  	if (infSteps != null) {
  		tempIt = infSteps.listIterator();
  		while (tempIt.hasNext()) {
  			currentInfStep = (IWInferenceStep) tempIt.next();
  			antecedentsList = (List)currentInfStep.getAntecedents();
  			if (antecedentsList != null && antecedentsList.size() >0) {
  				tempIt2 = antecedentsList.listIterator();
  				while (tempIt2.hasNext()) {
  					antNodeSet = (IWNodeSet)tempIt2.next();
  					if (antNodeSet != null) {
  						findAntecedentsInGraph(antNodeSet, result);
  					}
  				}
  			}
  		}
  	}
  }
  
  public Map getLeafConclusions() {
    Map result = null;
    HashMap conclusions = new HashMap();
    leafConclusions(this, conclusions);
    result = (Map)conclusions;
    return result;
  }

  private void leafConclusions(IWNodeSet ns, Map result) {
    String currentConclusion;

    ListIterator tempIt = null; // inference step iterator
    ListIterator tempIt2 = null; // antecedent iterator

    IWInferenceStep currentInfStep = null;
    List antecedentsList = null;
    IWNodeSet currentNodeSet = ns;
    List infSteps = (List)currentNodeSet.getPropertyByLocalName(infStepPropName);
    if (infSteps != null) {
    	tempIt = infSteps.listIterator();
    	while (tempIt.hasNext()) {
    		currentInfStep = (IWInferenceStep) tempIt.next();      
    		antecedentsList = (List)currentInfStep.getAntecedents();
    		if (antecedentsList != null && antecedentsList.size() >0 ) {
    			tempIt2 = antecedentsList.listIterator();
    			while (tempIt2.hasNext()) {
    				IWNodeSet antecedent = (IWNodeSet)tempIt2.next();
    				if (antecedent != null ) {
    					currentConclusion = antecedent.getConclusionRawString();
    					if (currentConclusion != null) {
    						result.put(ns, currentConclusion);
    					} 
    					leafConclusions(antecedent, result);          
    				}
    			}
    		}
    	}
    }
  }
  
  
  /**
   * Returns a list of inference steps that have antecedents match the
   * specified uri. The list should contain no more than one inference
   * step.
   *
   * @param antUri uri of the antecedent
   * @return list of inference step
   */
  private List getInferenceStepsWithAntecedent(String antUri) {

    ArrayList result = new ArrayList();
    findInferenceStepsWithAntecedent(antUri, result);
    return (List) result;
  }

  private void findInferenceStepsWithAntecedent(String antUri, ArrayList result) {
    
    ListIterator tempIt = null;
    ListIterator tempIt2 = null;
    
    IWInferenceStep currentInfStep = null;
    List antecedentNodeList = null;
    IWNodeSetImpl antNodeSet = null;
    try {
    	URL antURL = new URL (antUri);
    	IWNodeSet currentNodeSet = (IWNodeSet)this;
    	List infSteps = currentNodeSet.getInferenceSteps();
    	if (infSteps != null && infSteps.size() > 0 ) {
    		tempIt = infSteps.listIterator();
    		while (tempIt.hasNext()) {
    			currentInfStep = (IWInferenceStep) tempIt.next();
    			antecedentNodeList = currentInfStep.getAntecedents();
    			tempIt2 = antecedentNodeList.listIterator();
    			while (tempIt2.hasNext()) {
    				antNodeSet = (IWNodeSetImpl) tempIt2.next();
    				URL currentAntURL = new URL (antNodeSet.getIdentifier().getURIString());
    				if (currentAntURL.equals(antURL)) {
    					result.add(currentInfStep);
    					return;
    				} else {
    					antNodeSet.findInferenceStepsWithAntecedent(antUri, result);
    				}
    			}
    		}
    	}
    } catch (MalformedURLException mue) {
      System.out.println("Malformed URI: " + mue.getMessage());
    }
  }
  
  private List getNodeSetsWithAntecedent(String antUri) {

    ArrayList result = new ArrayList();
    findNodeSetsWithAntecedent(antUri, result);
    return (List) result;
  }

  private void findNodeSetsWithAntecedent(String antUri, ArrayList result) {
    
    ListIterator tempIt = null;
    ListIterator tempIt2 = null;
    
    IWInferenceStep currentInfStep = null;
    List antecedentNodeList = null;
    IWNodeSetImpl antNodeSet = null;
    try {
      URL antURL = new URL (antUri);
      IWNodeSet currentNodeSet = (IWNodeSet)this;
      List infSteps = currentNodeSet.getInferenceSteps();
      if (infSteps != null) {
      	tempIt = infSteps.listIterator();
      	while (tempIt.hasNext()) {
      		currentInfStep = (IWInferenceStep) tempIt.next();
      		antecedentNodeList = currentInfStep.getAntecedents();
      		tempIt2 = antecedentNodeList.listIterator();
      		while (tempIt2.hasNext()) {
      			antNodeSet = (IWNodeSetImpl) tempIt2.next();
      			URL currentAntURL = new URL (antNodeSet.getIdentifier().getURIString());
      			if (currentAntURL.equals(antURL)) {
      				result.add(this);
      				return;
      			} else {
      				antNodeSet.findNodeSetsWithAntecedent(antUri, result);
      			}
      		}
      	}
      }
    } catch (MalformedURLException mue) {
      System.out.println("Malformed URI: " + mue.getMessage());
    }
  }
  public List getAntecedent(String antUri) {

    ArrayList result = new ArrayList();
    findAntecedent(antUri, result);
    return (List) result;
  }

  private void findAntecedent(String antUri, ArrayList result) {
    
    ListIterator tempIt = null;
    ListIterator tempIt2 = null;
    
    IWInferenceStep currentInfStep = null;
    List antecedentNodeList = null;
    IWNodeSetImpl antNodeSet = null;
    try {
      IWNodeSet currentNodeSet = (IWNodeSet)this;
      URL thisURL = new URL (currentNodeSet.getIdentifier().getURIString());
      URL antURL = new URL (antUri);
      if (thisURL.equals(antURL)) {
        result.add(currentNodeSet);
      } else {
        List infSteps = currentNodeSet.getInferenceSteps();
        if (infSteps != null){
        	tempIt = infSteps.listIterator();
        	while (tempIt.hasNext()) {
        		currentInfStep = (IWInferenceStep) tempIt.next();
        		antecedentNodeList = currentInfStep.getHasAntecedentList();
        		if (antecedentNodeList != null && antecedentNodeList.size()>0) {
        		tempIt2 = antecedentNodeList.listIterator();
        		while (tempIt2.hasNext()) {
        			antNodeSet = (IWNodeSetImpl) tempIt2.next();
        			antNodeSet.findAntecedent(antUri,result);
        		}
        		}
        	}
        }
      }
    } catch (MalformedURLException mue) {
      System.out.println("Malformed URI: " + mue.getMessage());
    }
  }
  public void combine (IWNodeSet fromNS, IWNodeSet intoNS) {
    List infSteps = fromNS.getInferenceSteps();
    int size = infSteps.size();
    if (size>0) {
      int i ;
      for (i=size-1; i>=0; i--) {
        intoNS.addInferenceStepAtTop((IWInferenceStep)infSteps.get(i));
      }
    }
  }
  
  public void replaceNodeSet (String oldNodeSetUri, String newNodeSetUri) {
    List infSteps = getInferenceStepsWithAntecedent(oldNodeSetUri);
    if (infSteps.size() >0 ) {
      IWInferenceStep infStep = (IWInferenceStep)infSteps.get(0);
      infStep.replaceAntecedent(oldNodeSetUri,newNodeSetUri);
//      infStep.replaceAntecedentNode(oldNodeSetUri, newNodeSetUri);
    }
  }
  
  private int getNumberOfInferenceSteps() {
  	int result = 0;
  	List infSteps = getIsConsequentOf();
  	if (infSteps != null) {
  		result = infSteps.size();
  	}
  	return result;
  }
  public int getSelectedInferenceStepIndex ()  {
    int idx = selectedInferenceStepIndex;
    if ( idx < 0 && getNumberOfInferenceSteps()>0) {
      selectedInferenceStepIndex = 0;
      idx = 0;
    }
    return idx;
  }
  
  public void setSelectedInferenceStepIndex(int newIndex) {
    if (getNumberOfInferenceSteps()>0 && newIndex >= 0 && newIndex < getNumberOfInferenceSteps()) {
      selectedInferenceStepIndex = newIndex;
    }
  }

  public IWInferenceStep getSelectedInferenceStep () {
  	IWInferenceStep infStep = null;
  	List infSteps = getInferenceSteps();
  	if (infSteps != null && infSteps.size()>0) {
  		ListIterator iIt = (ListIterator)infSteps.listIterator();
  		if (infSteps.size() == 1) {
  			try {
  			infStep = (IWInferenceStepImpl) iIt.next();
  			} catch (Exception e) {}
  		} else {
  			boolean found = false;
  			while (iIt.hasNext() && !found) {
  				infStep = (IWInferenceStepImpl) iIt.next();
  				int idx = Integer.parseInt((String)infStep.getPropertyByLocalName(hasIndex));
  				if (idx == getSelectedInferenceStepIndex()) {
  					found = true;
  				}
  			}
  		}
  	}
  	return infStep;
  }

/*  
	public List getUsedFormulas () {
	  return n3Formulas;
	}
	
	public void addFormulaUsed(IWUsesFormula formula) {
	  n3Formulas.add(formula);
	}
	
	public void setUsedFormulas(List formulas) {
	  n3Formulas = formulas;
	}
*/	
	public List getInferenceSteps () {
		return getIsConsequentOf();
	}
	public void setInferenceSteps (List newSteps) {
		setIsConsequentOf(newSteps);
	}
	
	public String getConclusionRawString () {
		String result = null;
		Object conclusion = this.getPropertyByLocalName(PMLJ.hasConclusion_lname);
		IWInformation concInfo = null;
		if (conclusion != null) {
			if (conclusion instanceof String  && this.isPropertyRangeByLocalName("hasConclusion", "String") ){
				result = (String)conclusion;
			} else if (isPropertyRangeByLocalName("hasConclusion", "Information") ){
				try {
				concInfo = (IWInformation)PMLObjectManager.getPMLObject(conclusion);
				if (concInfo != null) {
					setPropertyByLocalName("hasConclusion", concInfo);
					result = (String) concInfo.getPropertyByLocalName("hasRawString");
				}
				} catch (Exception e) {
					
				}
			}
		} else {
			conclusion = this.getPropertyByLocalName("conclusion");
			if (conclusion != null) {
				if (conclusion instanceof String  && this.isPropertyRangeByLocalName("conclusion", "String") ){
					result = (String)conclusion;
				} 
			}			
		}
		return result;
	}
	
	public void setConclusionRawString (String newConclusion) {
		Object conclusion = this.getPropertyByLocalName("hasConclusion");
		IWInformation concInfo = null;
		if (conclusion != null) {
			if (conclusion instanceof String  && this.isPropertyRangeByLocalName("hasConclusion", "String") ){
				this.setPropertyByLocalName("hasConclusion", (String)conclusion);
			} else if (isPropertyRangeByLocalName("hasConclusion", "Information") ){
				concInfo = (IWInformation)PMLObjectManager.getPMLObject(conclusion);
				if (concInfo != null) {
					setPropertyByLocalName("hasConclusion", concInfo);
					concInfo.setPropertyByLocalName("hasRawString", newConclusion);
				}
			}
		}
	}
  
	public IWLanguage getLanguage () {
		IWLanguage result = null;
		Object conclusion = null;
		String languageName = PMLP.hasLanguage_lname;
		Object language = getPropertyByLocalName(languageName);
		if (language != null) { // v1 property
			result = (IWLanguage)PMLObjectManager.getPMLObject(language);
			if (result != null) {
				setPropertyByLocalName(languageName, result);
			}
		} else { // v2. check hasLanguage of hasConclusion
			conclusion = 	getPropertyByLocalName(PMLJ.hasConclusion_lname);
			IWInformation concInfo = null;
			if (conclusion != null) {
				if (isPropertyRangeByLocalName(PMLJ.hasConclusion_lname, "Information")) { // check in v1, conclusion in string
					try {
					concInfo = (IWInformation)PMLObjectManager.getPMLObject(conclusion);
					if (concInfo != null) {						
						setPropertyByLocalName(PMLJ.hasConclusion_lname,concInfo);
						language = concInfo.getPropertyByLocalName(languageName);
						if (language != null) {
							result = (IWLanguage)PMLObjectManager.getPMLObject(language);
							if (result != null) {
								concInfo.setPropertyByLocalName(languageName,result);							
							}
						}
					}
					} catch (Exception e) {
					}
				}
			} else {
			}
		}
		return result;
	}
	
	public void setLanguage (Object newLanguage) {
		Object conclusion = null;
		String languagePropName = PMLP.hasLanguage_lname;

		if (hasPropertyByLocalName(languagePropName)) {//v1
			setPropertyByLocalName(languagePropName,newLanguage);
		} else {//v2
			conclusion = this.getPropertyByLocalName("hasConclusion");
			IWInformation concInfo = null;
			if (conclusion != null) {
				if (isPropertyRangeByLocalName("hasConclusion", "Information")) { // check in v2, conclusion in string
					concInfo = (IWInformation)PMLObjectManager.getPMLObject(conclusion);
					if (concInfo != null) {
						setPropertyByLocalName("hasConclusion", concInfo);
						concInfo.setPropertyByLocalName(languagePropName,newLanguage);
					}
				}
			}
		}
	}
	
	public IWNodeSet getAnswer() {
		IWNodeSet _answer = null; 
		IWInferenceStep infStep = null;
		try {
			infStep = (IWInferenceStep) this.getSelectedInferenceStep();
			if (infStep != null) {
				_answer = infStep.getFromAnswer();
			}
		} catch (Exception e) {
		}
		return _answer;
	}
	
  public IWQuery getQuery() {
 		IWQuery query = null; 
 		IWNodeSet answer = null;
  	IWInferenceStep infStep = null;

  		infStep = (IWInferenceStep) this.getSelectedInferenceStep();
  		if (infStep != null) {
  			query = infStep.getFromQuery();
  			if (query == null) {
  				answer = infStep.getFromAnswer();
  				if (answer != null) {
  					query = answer.getQuery();
  				}
  			}
  		}
  		return query;
  	}
  
	public String getEnglishString () {
	  return _englishString;
	}
	
	public void setEnglishString (String newString) {
	  _englishString = newString;
	}

	public IWNodeSet getIsExplanationOf () {
		IWNodeSet result = null;
		Object value = getObjectPropertyValue(PMLJ.isExplanationOf_lname);
		if (value != null) result = (IWNodeSet)value;
		return result;
	}
	public void setIsExplanationOf (IWNodeSet explanation) {
		setObjectPropertyValue(PMLJ.isExplanationOf_lname, explanation);
	}
	public void setIsExplanationOf (String explanationURI) {
		setObjectPropertyValue(PMLJ.isExplanationOf_lname, explanationURI);
	}
	
	public IWInformation getHasConclusion(){
		IWInformation result = null;
		Object value = getObjectPropertyValue(PMLJ.hasConclusion_lname);
		if (value != null) {
			try {
			result = (IWInformation)value;
			} catch (ClassCastException cce) {
				System.out.println("IWNodeSetImpl.getHasConclusion: value object can not be casted to IWInformation.");
			}
		}
		return result;
	}
	
	public void setHasConclusion(IWInformation conclusion){
		setObjectPropertyValue(PMLJ.hasConclusion_lname, conclusion);
	}
	
	public List getIsConsequentOf () {
		List result = null;
		// not using getMultiplevalueObjectPropertyValue to prevent property value reload
		// when mix use of inference step and its occurrence
		return this.getMultipleValueObjectPropertyValue(PMLJ.isConsequentOf_lname);
	}
	public void setIsConsequentOf (List infSteps) {
		setMultipleValueObjectPropertyValue(PMLJ.isConsequentOf_lname, infSteps);
	}
	public void addIsConsequentOf (IWInferenceStep newInferenceStep) {  	
  	List infSteps = getInferenceSteps();
  	if (infSteps != null && infSteps.size() > 0 &&  newInferenceStep!= null) {
  		newInferenceStep.setHasIndex(infSteps.size());
  	} else {
  		newInferenceStep.setHasIndex(0);
  	}
		addMultipleValueObjectPropertyValue(PMLJ.isConsequentOf_lname, newInferenceStep);
	}
  public void addInferenceStep(IWInferenceStep newInferenceStep) {
  	addIsConsequentOf(newInferenceStep);
  }

  public void addInferenceStepAtTop(IWInferenceStep newInferenceStep) {
  	List infSteps = getInferenceSteps();
  	List newList = null;
  	if (infSteps != null && infSteps.size() > 0) {
  		newList = new ArrayList(infSteps.size() + 1);
  		newInferenceStep.setHasIndex(0);
  		newList.set(0, newInferenceStep);
//		System.out.println("addInferenceStepAtTop: new inferenceStep added at "+newInferenceStep.getIndex());
  		for (int i=0; i<infSteps.size(); i++) {      
  			IWInferenceStep currentInfStep =  (IWInferenceStep) infSteps.get(i);
  			int currentIdx = currentInfStep.getHasIndex();
  			currentInfStep.setHasIndex(currentIdx+1);
  			newList.set(currentIdx +1, currentInfStep);
//			System.out.println("addInferenceStepAtTop: currentInfStep added at "+currentInfStep.getIndex());
  		}  
  	} else {
  		newList = new ArrayList();
  		newInferenceStep.setHasIndex(0);
  		newList.set(0, newInferenceStep);  		
  	}
    setIsConsequentOf(newList);
    // keep selected inference step index
    int idx = getSelectedInferenceStepIndex();
    if (idx >= 0) {
      setSelectedInferenceStepIndex(idx+1);
    }
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

}
/* END OF IWNodeSetImpl */
