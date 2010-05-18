/* Copyright 2008 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.pmlj;

import java.util.*;

import org.inference_web.pml.v2.pmlp.*;

/**
 * Models inference steps within the inference web. An inference step
 * represent a justification of the conclusion of a node set by the application
 * of an inference rule.
 */
public interface IWInferenceStep extends IWJustificationElement {

    /**
     * Returns the inference rule applied in the inference step
     * @return the inference rule applied in the inference step
     */
    public IWInferenceRule getHasInferenceRule();

	/**
     * Sets the inference rule applied in the inference step
     * @param newInferenceRule the new inference rule applied in the inference step
     */
    public void setHasInferenceRule(IWInferenceRule newInferenceRule);
    
    /**
     * Sets the URI of inference rule applied in the inference step
     * @param inferenceRuleURI the URI of inference rule
     */
    public void setHasInferenceRule(String inferenceRuleURI);

	/**
     * Returns the inference engine that produced the inference step
     * @return the inference engine that produced the inference step
     */
    public IWInferenceEngine getHasInferenceEngine();

	/**
     * Sets the inference engine that produced the inference step
     * @param newInferenceEngine the new inference engine that produced the inference step
     */
    public void setHasInferenceEngine(IWInferenceEngine newInferenceEngine);

    /**
     * Sets the URI of inference engine that produced the inference step
     * @param inferenceEngineURI the URI of inference engine
     */
    public void setHasInferenceEngine(String inferenceEngineURI);
    
    /**
     * Returns a list of antecedents. Each entry in the list is an instance of IWNodeSet.
     * @return list of antecedents
     */
    public List getHasAntecedentList();
    
    /**
     * Sets the list of antecedents for the inference step.
     * @param newAntecedents list of antecedents
     */
    public void setHasAntecedentList(List newAntecedents);


  	/**
     * Returns the list of variable mappings for the inference step. Each binding of an inference step is
     * a mapping from a variable to a term specifying substitutions performed on the premises before the
     * application of the step's rule. For instance, substitutions may be required to unify the terms in
     * premises in order to perform resolution. Variable mappings are used to model these bindings.
     * @return the list of variable mappings for the inference step
     */
    public List getHasVariableMapping();

	/**
     * Sets the list of variable mappings for the inference step. Each binding of an inference step is
     * a mapping from a variable to a term specifying substitutions performed on the premises before the
     * application of the step's rule. For instance, substitutions may be required to unify the terms in
     * premises in order to perform resolution. Variable mappings are used to model these bindings.
     * @param newVariableMappings the new list of variable mappinfs for the inference step
     */
    public void setHasVariableMapping(List newVariableMappings);
  	/**
     * Returns the list of meta bindings for the inference step. Each binding of an inference step is
     * a mapping from a variable to a term specifying substitutions performed on the premises before the
     * application of the step's rule. For instance, substitutions may be required to unify the terms in
     * premises in order to perform resolution. Meta bindings are used to model these bindings.
     * @return the list of meta bindings for the inference step
     */
    public List getHasMetaBinding();

	/**
     * Sets the list of meta bindings for the inference step. Each binding of an inference step is
     * a mapping from a variable to a term specifying substitutions performed on the premises before the
     * application of the step's rule. For instance, substitutions may be required to unify the terms in
     * premises in order to perform resolution. Meta bindings are used to model these bindings.
     * @param newMetaBindings the new list of variable mappinfs for the inference step
     */
    public void setHasMetaBinding(List newMetaBindings);
  	/**
     * Adds the specified meta binding to the list of meta bindings for the inference step.
     * @param newMetaBinding the new meta binding for the inference step
     */
    public void addHasMetaBinding(IWMapping newMetaBinding);

	/**
     * Adds the specified variable mapping to the list of variable mappings for the inference step.
     * Each binding of an inference step is
     * a mapping from a variable to a term specifying substitutions performed on the premises before the
     * application of the step's rule. For instance, substitutions may be required to unify the terms in
     * premises in order to perform resolution. Variable mappings are used to model these bindings.
     * @param variableMapping the new variabl mapping for the inference step
     */
    public void addHasVariableMapping(IWMapping variableMapping);

    /**
     * Returns the position of this inference step in nodeset's infererence step list.
     * @return position of inference step in the list
     */
    public int getHasIndex();
    
    /**
     * Sets the inference step to in a specific position in nodeset's inference step list.
     * @param newIndex the position in the list
     */
    public void setHasIndex(int newIndex);

    /**
     * Returns the source usage of the inference step.
     * @return source usage of inference step.
     */
    public IWSourceUsage getHasSourceUsage();

    /**
     * Sets the source usage of the inference step.
     * @param sourceUsage source usage of inference step
     */
    public void setHasSourceUsage(IWSourceUsage sourceUsage);

    /**
     * Returns the query answered by the nodeset this inference step concludes.<br>
     * Only the root nodeset in a proof answers the query.
     * @return query the root nodeset answers
     */
    public IWQuery getFromQuery();

    /**
     * Sets the query answered by the nodeset this inference conclude.<br>
     * Only the root nodeset in a proof answers the query.
     * @param query the query the root nodeset answers
     */
    public void setFromQuery(IWQuery query);
    /**
     * Sets the URI of the query answered by the nodeset this inference step concludes.<br>
     * Only the root nodeset in a proof answers the query.
     * @param queryURI the URI of the query
     */
    public void setFromQuery(String queryURI);
    
    /**
     * Returns the answer nodeset (root) of the proof this inference step concludes.
     * @return answer root nodeset of the proof
     */
    public IWNodeSet getFromAnswer();
    
    /**
     * Sets the answer nodeset (root) of the proof this inference step concludes.
     * @param answer root nodeset of the proof
     */
    public void setFromAnswer(IWNodeSet answer);
    
    /**
     * Sets the URI of the answer nodeset (root) of the proof this inference step concludes.
     * @param answerURI root nodeset's URI
     */
    public void setFromAnswer(String answerURI);

    
  	/**
     * Returns a list of assumptions that are discharged by the inference step. Each discharged
     * assumption of an inference step is an expression that is discharged as an assumption by the
     * application of the step's rule.
     * @return a list of assumptions that are discharged by the inference step
     */
    public List getHasDischarge();

	/**
     * Sets the list of assumptions that are discharged by the inference step. Each discharged
     * assumption of an inference step is an expression that is discharged as an assumption by the
     * application of the step's rule.
     * @param newDischarges the new list of assumptions that are discharged by the inference step
     */
    public void setHasDischarge(List newDischarges);

	/**
     * Adds the specified assumption to the list of discharged assumptions. Each discharged
     * assumption of an inference step is an expression that is discharged as an assumption by the
     * application of the step's rule.
     * @param newDischarge the new assumption (NodeSet) which is to be added to the list of discharged assumption
     */
    public void addHasDischarge(IWNodeSet newDischarge);
  	/**
     * Adds the specified assumption to the list of discharged assumptions. Each discharged
     * assumption of an inference step is an expression that is discharged as an assumption by the
     * application of the step's rule.
     * @param dischargeURI the URI of the new assumption which is to be added to the list of discharged assumption
     */
    public void addHasDischarge(String dischargeURI);
    
    /**
     * Returns the property name for antecedents. PML1 and PML2 have different property names for<br>
     * antecedents. PML1 has property name "antecedent" and PML2 has property name "hasAntecedentList".<br>
     * This method is only used for PML1 documents. 
     * @return property name for antecedents
     */
    public String getAntecedentPropertyName();
    
    /**
     * Returns the list of antecedent nodeset URIs of the inference step
     * @return the list of antecedent nodeset URIs  of the inference step
     */
    public List getAntecedents();

	/**
     * Sets the list of antecedent nodeset URIs of the inference step. The antecedennts of
     * an inference step is a sequence of node sets, each of whose conclusions is a premise
     * to the application of the step's inference rule.
     * @param newAntecedents the new list of antecedent node set URIs of the inference step
     */
    public void setAntecedents(List newAntecedents);

  	/**
     * Adds the specified antecedent to the top of list of antecedents of the inference step. The antecedennts of
     * an inference step is a sequence of node sets, each of whose conclusions is a premise
     * to the application of the step's inference rule.
     * @param newAntecedent the new antecedent for the inference step
     */
    public void addAntecedentAtTop(Object newAntecedent);
  	/**
     * Adds the specified antecedent to the bottom of list of antecedents of the inference step. The antecedennts of
     * an inference step is a sequence of node sets, each of whose conclusions is a premise
     * to the application of the step's inference rule.
     * @param newAntecedent the new antecedent for the inference step, either the URI or the object of the antecedent
     */
    public void addAntecedentAtBottom(Object newAntecedent);
  	/**
     * Adds the specified antecedent to the bottom of list of antecedents of the inference step. The antecedennts of
     * an inference step is a sequence of node sets, each of whose conclusions is a premise
     * to the application of the step's inference rule.
     * @param newAntecedent the new antecedent for the inference step, either the URI or the object of the antecedent
     */
    public void addAntecedent(Object newAntecedent);
  	/**
     * Replaces the specified antecedent identified by URI with a new antecedent.
     * @param oldAntecedent the URI of the antecedent to be replaced
     * @param newAntecedent the URI of the antecedent to replace another antecedent
     */
    public void replaceAntecedent(String oldAntecedent, String newAntecedent);

    /**
     * Answers if inference step has any antecedent
     * @return true if there is at least one antecedent
     */
    public boolean hasAntecedents ();
    
    /**
     * Returns the confidence value of the inference step.
     * @return confidence value
     */
    public double getHasConfidenceValue () ;
    
    /**
     * Sets the confidence value of the inference step.
     * @param confidence value
     */
    public void setHasConfidenceValue(double confidence);

} /* END OF IWInferenceStep */
