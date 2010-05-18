/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package  org.inference_web.pml.v2.vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 *
 *  
 *  Ontology information  (TODO)
 *   	label:  __ONTOLOGY__LABEL__
 *   	comment:   __ONTOLOGY__COMMENT__
 *   	versionInfo:  __ONTOLOGY__VERSIONINFO__
 */
public class PMLDS{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inference-web.org/2.0/ds.owl#";

    public static final String getURI(){ return NS; }

	// Class (1)
	 public final static String List_lname = "List";
	 public final static Resource  List = _model.createResource("http://inference-web.org/2.0/ds.owl#List");

	// Property (2)
	 public final static String first_lname = "first";
	 public final static Property  first = _model.createProperty("http://inference-web.org/2.0/ds.owl#first");

	 public final static String rest_lname = "rest";
	 public final static Property  rest = _model.createProperty("http://inference-web.org/2.0/ds.owl#rest");

	// Instance (1)
	 public final static String nil_lname = "nil";
	 public final static Resource  nil = _model.createResource("http://inference-web.org/2.0/ds.owl#nil");



}


 
