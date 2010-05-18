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
public class IW200403{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inferenceweb.stanford.edu/2004/03/iw.owl#";

    public static final String getURI(){ return NS; }

	// Class (24)
	 public final static String DeclarativeRule_lname = "DeclarativeRule";
	 public final static Resource  DeclarativeRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#DeclarativeRule");

	 public final static String MethodRule_lname = "MethodRule";
	 public final static Resource  MethodRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#MethodRule");

	 public final static String ProofElement_lname = "ProofElement";
	 public final static Resource  ProofElement = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#ProofElement");

	 public final static String Ontology_lname = "Ontology";
	 public final static Resource  Ontology = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Ontology");

	 public final static String InferenceRule_lname = "InferenceRule";
	 public final static Resource  InferenceRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#InferenceRule");

	 public final static String Language_lname = "Language";
	 public final static Resource  Language = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Language");

	 public final static String TranslationRule_lname = "TranslationRule";
	 public final static Resource  TranslationRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#TranslationRule");

	 public final static String Axiom_lname = "Axiom";
	 public final static Resource  Axiom = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Axiom");

	 public final static String Question_lname = "Question";
	 public final static Resource  Question = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Question");

	 public final static String Person_lname = "Person";
	 public final static Resource  Person = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Person");

	 public final static String NodeSet_lname = "NodeSet";
	 public final static Resource  NodeSet = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#NodeSet");

	 public final static String Organization_lname = "Organization";
	 public final static Resource  Organization = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Organization");

	 public final static String VariableMapping_lname = "VariableMapping";
	 public final static Resource  VariableMapping = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#VariableMapping");

	 public final static String Publication_lname = "Publication";
	 public final static Resource  Publication = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Publication");

	 public final static String ProvenanceElement_lname = "ProvenanceElement";
	 public final static Resource  ProvenanceElement = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#ProvenanceElement");

	 public final static String LanguageAxiomSet_lname = "LanguageAxiomSet";
	 public final static Resource  LanguageAxiomSet = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#LanguageAxiomSet");

	 public final static String Query_lname = "Query";
	 public final static Resource  Query = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Query");

	 public final static String ModelElement_lname = "ModelElement";
	 public final static Resource  ModelElement = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#ModelElement");

	 public final static String InferenceStep_lname = "InferenceStep";
	 public final static Resource  InferenceStep = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#InferenceStep");

	 public final static String PrimitiveRule_lname = "PrimitiveRule";
	 public final static Resource  PrimitiveRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#PrimitiveRule");

	 public final static String Team_lname = "Team";
	 public final static Resource  Team = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Team");

	 public final static String InferenceEngine_lname = "InferenceEngine";
	 public final static Resource  InferenceEngine = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#InferenceEngine");

	 public final static String Source_lname = "Source";
	 public final static Resource  Source = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#Source");

	 public final static String DerivedRule_lname = "DerivedRule";
	 public final static Resource  DerivedRule = _model.createResource("http://inferenceweb.stanford.edu/2004/03/iw.owl#DerivedRule");

	// Property (50)
	 public final static String date_lname = "date";
	 public final static Property  date = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#date");

	 public final static String month_lname = "month";
	 public final static Property  month = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#month");

	 public final static String hasAuthor_lname = "hasAuthor";
	 public final static Property  hasAuthor = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasAuthor");

	 public final static String isConsequentOf_lname = "isConsequentOf";
	 public final static Property  isConsequentOf = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#isConsequentOf");

	 public final static String isExplanationOf_lname = "isExplanationOf";
	 public final static Property  isExplanationOf = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#isExplanationOf");

	 public final static String questionContent_lname = "questionContent";
	 public final static Property  questionContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#questionContent");

	 public final static String queryContent_lname = "queryContent";
	 public final static Property  queryContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#queryContent");

	 public final static String fromAnswer_lname = "fromAnswer";
	 public final static Property  fromAnswer = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#fromAnswer");

	 public final static String conclusion_lname = "conclusion";
	 public final static Property  conclusion = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#conclusion");

	 public final static String url_lname = "url";
	 public final static Property  url = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#url");

	 public final static String hasOntology_lname = "hasOntology";
	 public final static Property  hasOntology = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasOntology");

	 public final static String submitter_lname = "submitter";
	 public final static Property  submitter = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#submitter");

	 public final static String hasSource_lname = "hasSource";
	 public final static Property  hasSource = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasSource");

	 public final static String name_lname = "name";
	 public final static Property  name = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#name");

	 public final static String forLanguage_lname = "forLanguage";
	 public final static Property  forLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#forLanguage");

	 public final static String ruleSpec_lname = "ruleSpec";
	 public final static Property  ruleSpec = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#ruleSpec");

	 public final static String firstSubmissionDate_lname = "firstSubmissionDate";
	 public final static Property  firstSubmissionDate = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#firstSubmissionDate");

	 public final static String uri_lname = "uri";
	 public final static Property  uri = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#uri");

	 public final static String inferenceEngineRule_lname = "inferenceEngineRule";
	 public final static Property  inferenceEngineRule = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#inferenceEngineRule");

	 public final static String fromQuery_lname = "fromQuery";
	 public final static Property  fromQuery = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#fromQuery");

	 public final static String term_lname = "term";
	 public final static Property  term = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#term");

	 public final static String toLanguage_lname = "toLanguage";
	 public final static Property  toLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#toLanguage");

	 public final static String version_lname = "version";
	 public final static Property  version = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#version");

	 public final static String englishExample_lname = "englishExample";
	 public final static Property  englishExample = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#englishExample");

	 public final static String escapeCharacter_lname = "escapeCharacter";
	 public final static Property  escapeCharacter = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#escapeCharacter");

	 public final static String hasMember_lname = "hasMember";
	 public final static Property  hasMember = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasMember");

	 public final static String hasAntecedent_lname = "hasAntecedent";
	 public final static Property  hasAntecedent = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasAntecedent");

	 public final static String lastSubmissionDate_lname = "lastSubmissionDate";
	 public final static Property  lastSubmissionDate = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#lastSubmissionDate");

	 public final static String tacticOfAxiom_lname = "tacticOfAxiom";
	 public final static Property  tacticOfAxiom = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#tacticOfAxiom");

	 public final static String describedFrom_lname = "describedFrom";
	 public final static Property  describedFrom = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#describedFrom");

	 public final static String fromLanguage_lname = "fromLanguage";
	 public final static Property  fromLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#fromLanguage");

	 public final static String englishDescriptionTemplate_lname = "englishDescriptionTemplate";
	 public final static Property  englishDescriptionTemplate = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#englishDescriptionTemplate");

	 public final static String hasLanguage_lname = "hasLanguage";
	 public final static Property  hasLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasLanguage");

	 public final static String year_lname = "year";
	 public final static Property  year = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#year");

	 public final static String hasVariableMapping_lname = "hasVariableMapping";
	 public final static Property  hasVariableMapping = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasVariableMapping");

	 public final static String toExpression_lname = "toExpression";
	 public final static Property  toExpression = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#toExpression");

	 public final static String isQueryFor_lname = "isQueryFor";
	 public final static Property  isQueryFor = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#isQueryFor");

	 public final static String hasDischarge_lname = "hasDischarge";
	 public final static Property  hasDischarge = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasDischarge");

	 public final static String fromExpresssion_lname = "fromExpresssion";
	 public final static Property  fromExpresssion = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#fromExpresssion");

	 public final static String isFromEngine_lname = "isFromEngine";
	 public final static Property  isFromEngine = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#isFromEngine");

	 public final static String hasInferenceEngine_lname = "hasInferenceEngine";
	 public final static Property  hasInferenceEngine = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasInferenceEngine");

	 public final static String hasAxiom_lname = "hasAxiom";
	 public final static Property  hasAxiom = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasAxiom");

	 public final static String day_lname = "day";
	 public final static Property  day = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#day");

	 public final static String variable_lname = "variable";
	 public final static Property  variable = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#variable");

	 public final static String axiomContent_lname = "axiomContent";
	 public final static Property  axiomContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#axiomContent");

	 public final static String hasRule_lname = "hasRule";
	 public final static Property  hasRule = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasRule");

	 public final static String englishDescription_lname = "englishDescription";
	 public final static Property  englishDescription = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#englishDescription");

	 public final static String hasAnswerPattern_lname = "hasAnswerPattern";
	 public final static Property  hasAnswerPattern = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasAnswerPattern");

	 public final static String verificationMethod_lname = "verificationMethod";
	 public final static Property  verificationMethod = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#verificationMethod");

	 public final static String hasAnswer_lname = "hasAnswer";
	 public final static Property  hasAnswer = _model.createProperty("http://inferenceweb.stanford.edu/2004/03/iw.owl#hasAnswer");

	// Instance (0)


}


 
