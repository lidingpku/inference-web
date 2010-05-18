/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;

import java.util.*;

import org.inference_web.pml.v2.pmlp.*;


/**
 * Interface <code>IWNodeSet</code> is the PML API specification of the
 * functionality of PML Nodesets. A PML Nodeset represents a node in a proof
 * whose conclusion is justified by any of a set of inference steps associated
 * with the Nodeset. PML adopts the term "node set" since each Nodeset can
 * be viewed as a set of nodes gathered from one or more proof trees having
 * the same conclusion.
 */
public interface IWNodeSet extends IWJustificationElement {

	/**
	 * Returns <code>true</code> if and only if the Nodeset is an axiom. A Nodeset
	 * is an axiom if an only if either it has no inference steps or all inference
	 * steps have empty antecedent lists.
	 * @return <code>true</code> if and only if the Nodeset is an axiom.
	 */
	public boolean isAxiom();

	/**
	 * Returns the conclusion of Nodeset as a <code>String</code> expressed in the
	 * language of the Nodeset. The conclusion of the nodeset represents the expression
	 * concluded by the proof step. Every Nodeset has exactly one conclusion.
	 * @return the conclusion of the Nodeset
	 */
	public String getConclusionRawString();

	/**
	 * Sets the conclusion of the Nodeset to <code>newConclusion</code>. The conclusion
	 * of the nodeset represents the expression concluded by the proof step. Every Nodeset
	 * has exactly one conclusion
	 * @param newConclusion the new conclusion of the nodeset
	 */
	public void setConclusionRawString(String newConclusion);

	/**
	 * Returns a <code>String</code> containing the URI of the language of the Nodeset.
	 * The language of a Nodeset is the language in which the conclusion of the Nodeset
	 * is expressed.
	 * @return The URI of the language of the Nodeset
	 */
	public IWLanguage getLanguage();

	/**
	 * Sets the language URI of the nodeset to the specified string. The language of
	 * a Nodeset is the language in which the conclusion of the Nodeset is expressed.
	 * @param newLanguage the URI of the new language of the Nodeset
	 */
	public void setLanguage(Object newLanguage);

	/**
	 * Returns a list consisting of the inference steps of the Nodeset. Each element
	 * of the list is an object of type IWInferenceStep. Each inference step of a
	 * nodeset represents the application of an inference rule that justifies the
	 * conclusion of the nodeset.
	 * @return list of inference steps
	 */
	public List getInferenceSteps();

	/**
	 * Sets the list of inference steps in a Nodeset to the list provided. Each element
	 * of the new list is expected to be of type IWInferenceStep.
	 * @param _newInferenceSteps the new list of inference steps for the Nodeset
	 */
	public void setInferenceSteps(List _newInferenceSteps);

	/**
	 * Adds the inference step provided to the list of inference steps for the nodeset.
	 * The inference step is expected to represent an application of an inference rule
	 * that justifies the conclusion of the Nodeset.
	 * @param newInferenceStep the new inference step to be added to the list
	 */
	public void addInferenceStep(IWInferenceStep newInferenceStep);

	/**
	 * Adds the inference step provided to the top of the list of inference steps for the nodeset.
	 * The inference step is expected to represent an application of an inference rule
	 * that justifies the conclusion of the Nodeset.
	 * @param newInferenceStep the new inference step to be added to the list
	 */
	public void addInferenceStepAtTop(IWInferenceStep newInferenceStep);

	/**
	 * Returns a list of antecedent NodeSets that match the specified URI.
	 * @param antecedentURI antecedent URI
	 * @return list of antecedent NodeSets
	 */
	public List getAntecedent(String antecedentURI);

	/**
	 * Returns a list of antecedent URIs
	 * @return antecedent URIs
	 */
	public List getAntecedentURIsInGraph();
	/**
	 * Returns NodeSet and conclusion pairs in the PML graph of the NodeSet
	 * @return NodeSet and conclusion pairs in NodeSet
	 */    
	public Map getConclusionsInGraph();

	/**
	 * Returns a Map of NodeSet and conclusion pair of leaf NodeSets in the PML graph of this NodeSet
	 * @return leaf NodeSet and conclusion pairs
	 */    
	public Map getLeafConclusions();

	/**
	 * Returns the depth of NodeSet in a proof.
	 * @return depth
	 */
	public int getDepth ();
	
	/**
	 * Sets the depth of NodeSet in a proof
	 * @param depth depth 
	 */
	public void setDepth(int depth);

	// n3 uses
	/**
	 * Returns NodeSet's English string (n3 only).
	 */
	public String getEnglishString();
	
	/**
	 * Sets NodeSet's English string (n3 only)
	 * @param newString new value
	 */
	public void setEnglishString(String newString);

	/**
	 * Returns the answer NodeSet of selected inference step.
	 * @return answer NodeSet
	 */
	public IWNodeSet getAnswer ();
	
	/**
	 * Returns the query of selected inference step.
	 * @return query
	 */
	public IWQuery getQuery();

	/**
	 * Returns isExplanationOf NodeSet.
	 * @return isExplanationOf NodeSet
	 */
	public IWNodeSet getIsExplanationOf ();
	
	/**
	 * Sets isExplanationOf to a new NodeSet.
	 * @param explanation new value
	 */
	public void setIsExplanationOf (IWNodeSet explanation);
	
	/**
	 * Sets isExplanationOf to a new NodeSet's URI.
	 * @param explanationURI new value
	 */
	public void setIsExplanationOf (String explanationURI);

	/**
	 * Returns the value of hasConclusion.
	 * @return conclusion Information
	 */
	public IWInformation getHasConclusion();
	
	/**
	 * Sets hasConclusion to a new value.
	 * @param conclusion new value
	 */
	public void setHasConclusion(IWInformation conclusion);

	/**
	 * Returns a list of inference steps.
	 * @return inference steps
	 */
	public List getIsConsequentOf ();
	
	/**
	 * Set isConsequentOf to a new list of inference steps.
	 * @param infSteps new inference steps
	 */
	public void setIsConsequentOf (List infSteps);
	
	/**
	 * Adds an inference step at the end of list.
	 * @param infStep inference step
	 */
	public void addIsConsequentOf (IWInferenceStep infStep);
	
	/**
	 * Returns the position of currently selected inference step in the list.
	 * @return index currently selected inference step's position
	 */
	public int getSelectedInferenceStepIndex () ;
	
	/**
	 * Makes the inference step at the given position in the list to be the
	 * selected one.
	 * @param index of inference step as selected
	 */
	public void setSelectedInferenceStepIndex(int index);
	
	/**
	 * Returns the currently selected inference step.
	 * @return inference step currently selected
	 */
	public IWInferenceStep getSelectedInferenceStep () ;


	/**
	 * Returns the creation date and time of nodeset.
	 * @return date time
	 */
	public String getHasCreationDateTime () ;
	
	/**
	 * Sets nodeset's creation date and time.
	 * @param dateTime creation date time
	 */
	public void setHasCreationDateTime (String dateTime) ;

  	  	
}   /* END OF IWNodeSet */
