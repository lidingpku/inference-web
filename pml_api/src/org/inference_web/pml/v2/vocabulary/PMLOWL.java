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
public class PMLOWL{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inference-web.org/2.0/pml-owl.owl#";

    public static final String getURI(){ return NS; }

	// Class (1)
	 public final static String InformationRdfInstance_lname = "InformationRdfInstance";
	 public final static Resource  InformationRdfInstance = _model.createResource("http://inference-web.org/2.0/pml-owl.owl#InformationRdfInstance");

	// Property (1)
	 public final static String hasInstanceReference_lname = "hasInstanceReference";
	 public final static Property  hasInstanceReference = _model.createProperty("http://inference-web.org/2.0/pml-owl.owl#hasInstanceReference");

	// Instance (0)


}


 
