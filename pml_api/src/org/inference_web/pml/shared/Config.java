/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.shared;

import java.util.*;

import org.inference_web.pml.context.accessor.OntologyManager;
import org.inference_web.pml.v2.vocabulary.*;

import com.hp.hpl.jena.ontology.OntClass;



public class Config {
	
	/**
	 * the magnitude (depth) when pml api conduct full-load,
	 *  i.e. loading all nodeSet starting from root.  
	 *  
	 */
	public static final int FULL_LOADING = 999;
	/**
	 *  just load the current nodeset.
	 */
	public static final int PARTIAL_LOADING = 1;
	
	
	/**
	 * the list of PML concepts whose instances are always anonymous
	 */
	public static Set unnamedConcepts = initUnnamedConcepts();

	/**
	 * The default file extension used by PML
	 */
	public static final String DEFAULT_EXT                = ".owl";

	/*
	 * Ontology and source code package map
	 */

	public static final String PMLP_PACKAGE_ROOT = "org.inference_web.pml.v2.pmlp";
	public static final String PMLJ_PACKAGE_ROOT = "org.inference_web.pml.v2.pmlj";
	public static final String PMLOWL_PACKAGE_ROOT = "org.inference_web.pml.v2.pmlowl";
	public static final String PML2P_ONTOLOGY_URI = "http://inference-web.org/2.0/pml-provenance.owl#";
	public static final String PML2J_ONTOLOGY_URI = "http://inference-web.org/2.0/pml-justification.owl#";
	public static final String PML2OWL_ONTOLOGY_URI = "http://inference-web.org/2.0/pml-owl.owl#";
	public static final String PML2DS_ONTOLOGY_URI = "http://inference-web.org/2.0/ds.owl#";
	
	/*
	 * Ontology and source code package map
	 */

	public static Map ontologyPackageMap = initOntologyPackageMap();
	public static Set pmlOntologySet = initOntologySet();
	public static final org.inference_web.pml.context.accessor.Ontology PMLP_ONTOLOGY = OntologyManager.getOntology(Config.PML2P_ONTOLOGY_URI);
	public static final org.inference_web.pml.context.accessor.Ontology PMLJ_ONTOLOGY = OntologyManager.getOntology(Config.PML2J_ONTOLOGY_URI);
	public static final org.inference_web.pml.context.accessor.Ontology PMLOWL_ONTOLOGY = OntologyManager.getOntology(Config.PML2OWL_ONTOLOGY_URI);
	public static final OntClass PMLListCls = PMLP_ONTOLOGY.getOntClass(Config.PML2DS_ONTOLOGY_URI+"List");

	public static final int defaultCacheSize = 200;
	public static final int defaultCacheExpirationIntevalInMinute = 60;
	public static String[] iwSpecURIStrs = {
		"http://inferenceweb.stanford.edu/2004/03/iw.owl#",
		"http://inferenceweb.stanford.edu/2004/07/iw.owl#",
		"http://inference-web.org/2.0/pml-provenance.owl#",
		"http://inference-web.org/2.0/pml-justification.owl#", 
		"http://inference-web.org/2.0/ds.owl#", 
		"http://inference-web.org/2.0/pml-owl.owl#"}; 



	// Justification document types
	public static final String NodeSet              = PMLJ.NodeSet_lname;
	public static final String Query                = PMLJ.Query_lname;
	public static final String Question             = PMLJ.Question_lname;
	public static final String Other                = "Other";


	private static Set initUnnamedConcepts () {
		Set sc = new HashSet();
//		sc.add(PMLP.Information_lname);
		sc.add(PMLP.SourceUsage_lname);
		sc.add(PMLP.LearnedSourceUsage_lname);
		sc.add(PMLP.DocumentFragment_lname);
		sc.add(PMLP.DocumentFragmentByOffset_lname);
		sc.add(PMLP.DocumentFragmentByRowCol_lname);
		sc.add(PMLP.PrettyNameMapping_lname);
		sc.add(PMLJ.Mapping_lname);
		sc.add(PMLJ.InferenceStep_lname);
		sc.add(PMLP.AgentList_lname);
		sc.add(PMLJ.NodeSetList_lname);
		return sc;

	}

	public static boolean isUnnamedConcept(String concept) {
		boolean result = false;
		if (unnamedConcepts.contains(concept)) {
			result = true;
		}
		return result;
	}
	
	private static Map initOntologyPackageMap() {
		Map result = new HashMap();
		result.put(PMLP_PACKAGE_ROOT, PML2P_ONTOLOGY_URI);
		result.put(PMLJ_PACKAGE_ROOT, PML2J_ONTOLOGY_URI);
		result.put(PMLOWL_PACKAGE_ROOT, PML2OWL_ONTOLOGY_URI);
			
		return result;
	}
	private static Set initOntologySet() {
		Set result = new HashSet();
		result.add(PML2P_ONTOLOGY_URI);
		result.add(PML2J_ONTOLOGY_URI);
		result.add(PML2OWL_ONTOLOGY_URI);
		result.add(PML2DS_ONTOLOGY_URI);
		return result;
	}
} /* END OF Config */
