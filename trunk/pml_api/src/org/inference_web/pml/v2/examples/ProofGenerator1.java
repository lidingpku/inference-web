/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.examples;

import java.util.*;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.*;

public class ProofGenerator1 {	

	public static IWNodeSet createSimpleNodeSetType1(String nsURI, String conclusionRawString, String languageURI, String ruleURI, String engineURI, String sourceURI, List antecedents) { 
		// create Information instance and assign property values
		IWInformation conclusion = null;
		if (conclusionRawString != null){
			conclusion = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
			if (conclusionRawString != null ) {
				conclusion.setHasRawString(conclusionRawString);
			}
			if (languageURI != null) {
				conclusion.setHasLanguage(languageURI);
			}
		}

		// create NodeSet with specified ontology and class
		IWNodeSet ns = (IWNodeSet)PMLObjectManager.createPMLObject(PMLJ.NodeSet_lname);
		// assign NodeSet name
		ns.setIdentifier(PMLObjectManager.getObjectID(nsURI));
		// assign NodeSet conclusion
		ns.setHasConclusion(conclusion);  	

		// create SourceUsage with source URI
		IWSourceUsage srcUsg = null;
		if (sourceURI!=null) {
			srcUsg = (IWSourceUsage)PMLObjectManager.createPMLObject(PMLP.SourceUsage_lname);
			srcUsg.setHasSource(sourceURI);
		}

		// create InferenceStep instance and assign property values
		IWInferenceStep infStep = (IWInferenceStep)PMLObjectManager.createPMLObject( PMLJ.InferenceStep_lname);
		infStep.setHasIndex(0);
		infStep.setHasInferenceRule(ruleURI); 
		infStep.setHasInferenceEngine(engineURI);
		infStep.setHasAntecedentList(antecedents);
		infStep.setHasSourceUsage(srcUsg);

		ArrayList infSteps = new ArrayList();
		infSteps.add(infStep); 	
		ns.setIsConsequentOf(infSteps);

		return ns;
	}
}
/* END OF ProofGenerator1 */
