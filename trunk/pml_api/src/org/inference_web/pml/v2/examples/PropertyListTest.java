package org.inference_web.pml.v2.examples;


import java.util.*;
import org.inference_web.pml.shared.Config;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlj.impl.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.pmlp.impl.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

/*
import org.mindswap.*;
import org.mindswap.pellet.*;
import org.mindswap.pellet.datatypes.*;
import org.mindswap.pellet.jena.*;
*/

public class PropertyListTest {
	
	public static void run() {
		IWInferenceStep infStep = (IWInferenceStep)PMLObjectManager.createPMLObject(PMLJ.InferenceStep_lname);
		IWNodeSet ns = (IWNodeSet)PMLObjectManager.createPMLObject(PMLJ.NodeSet_lname);
		IWInferenceRule rule = (IWInferenceRule)PMLObjectManager.createPMLObject(PMLP.DeclarativeRule_lname);
		infStep.setHasInferenceRule(rule);
		Map props = (Map)infStep.getProperties();
		Iterator pIt = props.keySet().iterator();
		while (pIt.hasNext()) {
			System.out.println(pIt.next());
		}
		
	}
/*	
	public static void test1() {
		String ontUri = "http://inference-web.org/2.0/pml-justification.owl";
		String infEngineClassUri = "http://inference-web.org/2.0/pml-provenance.owl#InferenceEngine";
		OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		OntClass ieCls = model.getOntClass(infEngineClassUri);
		ieCls.listDeclaredProperties();
		ieCls.listDeclaredProperties();
	}
*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		run();
	}

}
