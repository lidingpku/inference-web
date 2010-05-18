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
public class PMLJ{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inference-web.org/2.0/pml-justification.owl#";

    public static final String getURI(){ return NS; }

	// Class (8)
	 public final static String AbstractionRule_lname = "AbstractionRule";
	 public final static Resource  AbstractionRule = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#AbstractionRule");

	 public final static String JustificationElement_lname = "JustificationElement";
	 public final static Resource  JustificationElement = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#JustificationElement");

	 public final static String Query_lname = "Query";
	 public final static Resource  Query = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#Query");

	 public final static String Question_lname = "Question";
	 public final static Resource  Question = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#Question");

	 public final static String NodeSetList_lname = "NodeSetList";
	 public final static Resource  NodeSetList = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#NodeSetList");

	 public final static String InferenceStep_lname = "InferenceStep";
	 public final static Resource  InferenceStep = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#InferenceStep");

	 public final static String Mapping_lname = "Mapping";
	 public final static Resource  Mapping = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#Mapping");

	 public final static String NodeSet_lname = "NodeSet";
	 public final static Resource  NodeSet = _model.createResource("http://inference-web.org/2.0/pml-justification.owl#NodeSet");

	// Property (21)
	 public final static String isConsequentOf_lname = "isConsequentOf";
	 public final static Property  isConsequentOf = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#isConsequentOf");

	 public final static String hasAnswerPattern_lname = "hasAnswerPattern";
	 public final static Property  hasAnswerPattern = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasAnswerPattern");

	 public final static String hasVariableMapping_lname = "hasVariableMapping";
	 public final static Property  hasVariableMapping = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasVariableMapping");

	 public final static String isQueryFor_lname = "isQueryFor";
	 public final static Property  isQueryFor = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#isQueryFor");

	 public final static String hasInferenceRule_lname = "hasInferenceRule";
	 public final static Property  hasInferenceRule = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasInferenceRule");

	 public final static String hasMetaBinding_lname = "hasMetaBinding";
	 public final static Property  hasMetaBinding = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasMetaBinding");

	 public final static String isFromEngine_lname = "isFromEngine";
	 public final static Property  isFromEngine = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#isFromEngine");

	 public final static String hasConclusion_lname = "hasConclusion";
	 public final static Property  hasConclusion = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasConclusion");

	 public final static String fromAnswerOrQuery_lname = "fromAnswerOrQuery";
	 public final static Property  fromAnswerOrQuery = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#fromAnswerOrQuery");

	 public final static String fromAnswer_lname = "fromAnswer";
	 public final static Property  fromAnswer = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#fromAnswer");

	 public final static String hasAntecedentList_lname = "hasAntecedentList";
	 public final static Property  hasAntecedentList = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasAntecedentList");

	 public final static String hasInferenceEngine_lname = "hasInferenceEngine";
	 public final static Property  hasInferenceEngine = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasInferenceEngine");

	 public final static String mapTo_lname = "mapTo";
	 public final static Property  mapTo = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#mapTo");

	 public final static String hasDischarge_lname = "hasDischarge";
	 public final static Property  hasDischarge = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasDischarge");

	 public final static String hasPatternNodeSet_lname = "hasPatternNodeSet";
	 public final static Property  hasPatternNodeSet = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasPatternNodeSet");

	 public final static String hasIndex_lname = "hasIndex";
	 public final static Property  hasIndex = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasIndex");

	 public final static String hasSourceUsage_lname = "hasSourceUsage";
	 public final static Property  hasSourceUsage = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasSourceUsage");

	 public final static String isExplanationOf_lname = "isExplanationOf";
	 public final static Property  isExplanationOf = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#isExplanationOf");

	 public final static String hasAnswer_lname = "hasAnswer";
	 public final static Property  hasAnswer = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#hasAnswer");

	 public final static String mapFrom_lname = "mapFrom";
	 public final static Property  mapFrom = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#mapFrom");

	 public final static String fromQuery_lname = "fromQuery";
	 public final static Property  fromQuery = _model.createProperty("http://inference-web.org/2.0/pml-justification.owl#fromQuery");

	// Instance (0)


}


 
