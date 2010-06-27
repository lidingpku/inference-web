/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sw4j.util.ToolSafe;
import sw4j.vocabulary.pml.*;



import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


public class PmlConst{
	
	/*
	 * Ontology and source code package map
	 */

	public static final String PML_200403_ONTOLOGY_URI = IW200403.getURI();
	public static final String PML_200407_ONTOLOGY_URI = IW200407.getURI();
	public static final String PML2P_ONTOLOGY_URI = PMLP.getURI();
	public static final String PML2J_ONTOLOGY_URI = PMLJ.getURI();
	public static final String PML2OWL_ONTOLOGY_URI = PMLOWL.getURI();
	public static final String PML2DS_ONTOLOGY_URI =  PMLDS.getURI();
	public static final String PML2R_ONTOLOGY_URI =  PMLR.getURI();

	public static final String OBSOLETE_PML2P_ONTOLOGY_URI = "http://inferencew.standford.edu/2006/06/pml-provenance.owl#";
	public static final String OBSOLETE_PML2J_ONTOLOGY_URI = "http://inferenceweb.stanford.edu/2006/06/pml-justification.owl#";
	public static final String OBSOLETE_PML2OWL_ONTOLOGY_URI = "http://inferenceweb.stanford.edu/2006/06/pml-owl.owl#";
	public static final String OBSOLETE_PML2DS_ONTOLOGY_URI = "http://inferenceweb.stanford.edu/2006/06/ds.owl#";
	
	private static Set<Resource> g_pml_classes = null;

	/**
	 * return a set of PML classes whose instances MUST be RDF URI resource
	 * @return
	 */
	public static Set<Resource> getPmlClassMustHaveUri(){
		if (null!= g_pml_classes)
			return g_pml_classes;
		
		g_pml_classes = new HashSet<Resource>();
		g_pml_classes.add(IW200403.NodeSet);
		g_pml_classes.add(IW200407.NodeSet);
		g_pml_classes.add(PMLJ.NodeSet);
		
		g_pml_classes.add(IW200403.Query);
		g_pml_classes.add(IW200407.Query);
		g_pml_classes.add(PMLJ.Query);
		
		g_pml_classes.add(IW200403.Question);
		g_pml_classes.add(IW200407.Question);
		g_pml_classes.add(PMLJ.Question);
		
		g_pml_classes.add(IW200403.Source);
		g_pml_classes.add(IW200407.Source);
		g_pml_classes.add(PMLP.Source);
	
		g_pml_classes.add(IW200403.InferenceRule);
		g_pml_classes.add(IW200407.InferenceRule);
		g_pml_classes.add(PMLP.InferenceRule);
		
		g_pml_classes.add(IW200403.Language);
		g_pml_classes.add(IW200407.Language);
		g_pml_classes.add(PMLP.Language);
		
		g_pml_classes.add(IW200403.Axiom);
		g_pml_classes.add(IW200407.Axiom);
		
		g_pml_classes.add(PMLP.Format);
		
		return g_pml_classes;
	}

	
	public static String[][] PML_NAMESPACES_PREFIX = {
		{"iw200403", PML_200403_ONTOLOGY_URI},
		{"iw200407", PML_200407_ONTOLOGY_URI},
		{"pmlp", PML2P_ONTOLOGY_URI},
		{"pmlj", PML2J_ONTOLOGY_URI},
		{"pmlowl", PML2OWL_ONTOLOGY_URI},
		{"pmlr", PML2R_ONTOLOGY_URI},
		{"ds", PML2DS_ONTOLOGY_URI}
	}; 
	
	public static Map<String,String> getNsPrefixMapping(){
		HashMap<String,String> map = new HashMap<String,String>();
		for (int i=0; i<PML_NAMESPACES_PREFIX.length; i++)
			map.put(PML_NAMESPACES_PREFIX[i][0], PML_NAMESPACES_PREFIX[i][1]);
		return map;
	}
	
	public static String[][] OBSOLETE_PML_NAMESPACES = {
		{OBSOLETE_PML2P_ONTOLOGY_URI,PML2P_ONTOLOGY_URI},
		{OBSOLETE_PML2J_ONTOLOGY_URI,PML2J_ONTOLOGY_URI},
		{OBSOLETE_PML2OWL_ONTOLOGY_URI,PML2OWL_ONTOLOGY_URI},
		{OBSOLETE_PML2DS_ONTOLOGY_URI,PML2DS_ONTOLOGY_URI},
	};
	
	/**
	 * Test if a URI is using PML namespaces (including previous PML versions).
	 * @param uri
	 * @return
	 */
	public static boolean testPmlNamespace(String uri){
		if (ToolSafe.isEmpty(uri))
			return false;
		
		for (int i=0; i<PML_NAMESPACES_PREFIX.length; i++)
			if (uri.startsWith(PML_NAMESPACES_PREFIX[i][1]))
				return true;
						
		return false;
	}

	public static List<String> listSupportedPmlNamespace(){
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i=0; i<PML_NAMESPACES_PREFIX.length; i++)
			list.add(PML_NAMESPACES_PREFIX[i][1]);
		return list;
	}

	/**
	 * Test if a resource  belong to PML vocabulary
	 * @param res
	 * @return
	 */
	public static boolean testPmlNamespace(RDFNode node){
		if (!node.isURIResource())
			return false;
		
		return testPmlNamespace(((Resource)node).getURI());
	} 


} /* END OF Config */
