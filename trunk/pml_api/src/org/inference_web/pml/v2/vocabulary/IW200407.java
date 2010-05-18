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
public class IW200407{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inferenceweb.stanford.edu/2004/07/iw.owl#";

    public static final String getURI(){ return NS; }

	// Class (33)
	 public final static String Person_lname = "Person";
	 public final static Resource  Person = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Person");

	 public final static String NodeSet_lname = "NodeSet";
	 public final static Resource  NodeSet = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#NodeSet");

	 public final static String InferenceRule_lname = "InferenceRule";
	 public final static Resource  InferenceRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#InferenceRule");

	 public final static String TrustRelation_lname = "TrustRelation";
	 public final static Resource  TrustRelation = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#TrustRelation");

	 public final static String Document_lname = "Document";
	 public final static Resource  Document = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Document");

	 public final static String Source_lname = "Source";
	 public final static Resource  Source = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Source");

	 public final static String MethodRule_lname = "MethodRule";
	 public final static Resource  MethodRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#MethodRule");

	 public final static String Publication_lname = "Publication";
	 public final static Resource  Publication = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Publication");

	 public final static String Ontology_lname = "Ontology";
	 public final static Resource  Ontology = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Ontology");

	 public final static String AggregatedTrustRelation_lname = "AggregatedTrustRelation";
	 public final static Resource  AggregatedTrustRelation = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#AggregatedTrustRelation");

	 public final static String Axiom_lname = "Axiom";
	 public final static Resource  Axiom = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Axiom");

	 public final static String ProofElement_lname = "ProofElement";
	 public final static Resource  ProofElement = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#ProofElement");

	 public final static String LearnedSourceUsage_lname = "LearnedSourceUsage";
	 public final static Resource  LearnedSourceUsage = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#LearnedSourceUsage");

	 public final static String PrimitiveRule_lname = "PrimitiveRule";
	 public final static Resource  PrimitiveRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#PrimitiveRule");

	 public final static String DeclarativeRule_lname = "DeclarativeRule";
	 public final static Resource  DeclarativeRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#DeclarativeRule");

	 public final static String SourceUsage_lname = "SourceUsage";
	 public final static Resource  SourceUsage = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#SourceUsage");

	 public final static String ProvenanceElement_lname = "ProvenanceElement";
	 public final static Resource  ProvenanceElement = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#ProvenanceElement");

	 public final static String InferenceStep_lname = "InferenceStep";
	 public final static Resource  InferenceStep = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#InferenceStep");

	 public final static String DerivedRule_lname = "DerivedRule";
	 public final static Resource  DerivedRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#DerivedRule");

	 public final static String Format_lname = "Format";
	 public final static Resource  Format = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Format");

	 public final static String PrettyNameMapping_lname = "PrettyNameMapping";
	 public final static Resource  PrettyNameMapping = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#PrettyNameMapping");

	 public final static String LanguageAxiomSet_lname = "LanguageAxiomSet";
	 public final static Resource  LanguageAxiomSet = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#LanguageAxiomSet");

	 public final static String ModelElement_lname = "ModelElement";
	 public final static Resource  ModelElement = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#ModelElement");

	 public final static String InferenceEngine_lname = "InferenceEngine";
	 public final static Resource  InferenceEngine = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#InferenceEngine");

	 public final static String Language_lname = "Language";
	 public final static Resource  Language = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Language");

	 public final static String Website_lname = "Website";
	 public final static Resource  Website = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Website");

	 public final static String Agent_lname = "Agent";
	 public final static Resource  Agent = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Agent");

	 public final static String Query_lname = "Query";
	 public final static Resource  Query = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Query");

	 public final static String Question_lname = "Question";
	 public final static Resource  Question = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Question");

	 public final static String TranslationRule_lname = "TranslationRule";
	 public final static Resource  TranslationRule = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#TranslationRule");

	 public final static String Mapping_lname = "Mapping";
	 public final static Resource  Mapping = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Mapping");

	 public final static String Organization_lname = "Organization";
	 public final static Resource  Organization = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Organization");

	 public final static String Team_lname = "Team";
	 public final static Resource  Team = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#Team");

	// Property (76)
	 public final static String ruleSpec_lname = "ruleSpec";
	 public final static Property  ruleSpec = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#ruleSpec");

	 public final static String queryContent_lname = "queryContent";
	 public final static Property  queryContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#queryContent");

	 public final static String lastSubmissionDate_lname = "lastSubmissionDate";
	 public final static Property  lastSubmissionDate = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#lastSubmissionDate");

	 public final static String hasDischarge_lname = "hasDischarge";
	 public final static Property  hasDischarge = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasDischarge");

	 public final static String hasShortPrettyName_lname = "hasShortPrettyName";
	 public final static Property  hasShortPrettyName = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasShortPrettyName");

	 public final static String hasAuthor_lname = "hasAuthor";
	 public final static Property  hasAuthor = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAuthor");

	 public final static String hasEnglishString_lname = "hasEnglishString";
	 public final static Property  hasEnglishString = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasEnglishString");

	 public final static String hasAxiom_lname = "hasAxiom";
	 public final static Property  hasAxiom = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAxiom");

	 public final static String hasTrustedParty_lname = "hasTrustedParty";
	 public final static Property  hasTrustedParty = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasTrustedParty");

	 public final static String spanToByte_lname = "spanToByte";
	 public final static Property  spanToByte = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#spanToByte");

	 public final static String usageQuery_lname = "usageQuery";
	 public final static Property  usageQuery = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#usageQuery");

	 public final static String hasPrettyName_lname = "hasPrettyName";
	 public final static Property  hasPrettyName = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasPrettyName");

	 public final static String hasTopic_lname = "hasTopic";
	 public final static Property  hasTopic = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasTopic");

	 public final static String hasImage_lname = "hasImage";
	 public final static Property  hasImage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasImage");

	 public final static String hasEndTime_lname = "hasEndTime";
	 public final static Property  hasEndTime = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasEndTime");

	 public final static String hasSourceUsage_lname = "hasSourceUsage";
	 public final static Property  hasSourceUsage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasSourceUsage");

	 public final static String isExplanationOf_lname = "isExplanationOf";
	 public final static Property  isExplanationOf = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#isExplanationOf");

	 public final static String usageTime_lname = "usageTime";
	 public final static Property  usageTime = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#usageTime");

	 public final static String axiomContent_lname = "axiomContent";
	 public final static Property  axiomContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#axiomContent");

	 public final static String name_lname = "name";
	 public final static Property  name = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#name");

	 public final static String hasAudio_lname = "hasAudio";
	 public final static Property  hasAudio = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAudio");

	 public final static String hasFormat_lname = "hasFormat";
	 public final static Property  hasFormat = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasFormat");

	 public final static String version_lname = "version";
	 public final static Property  version = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#version");

	 public final static String fromQuery_lname = "fromQuery";
	 public final static Property  fromQuery = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#fromQuery");

	 public final static String hasLanguage_lname = "hasLanguage";
	 public final static Property  hasLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasLanguage");

	 public final static String usesInferenceEngine_lname = "usesInferenceEngine";
	 public final static Property  usesInferenceEngine = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#usesInferenceEngine");

	 public final static String dataCollectionEnd_lname = "dataCollectionEnd";
	 public final static Property  dataCollectionEnd = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#dataCollectionEnd");

	 public final static String hasPrettyNameMapping_lname = "hasPrettyNameMapping";
	 public final static Property  hasPrettyNameMapping = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasPrettyNameMapping");

	 public final static String consequent_lname = "consequent";
	 public final static Property  consequent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#consequent");

	 public final static String hasStartTime_lname = "hasStartTime";
	 public final static Property  hasStartTime = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasStartTime");

	 public final static String isQueryFor_lname = "isQueryFor";
	 public final static Property  isQueryFor = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#isQueryFor");

	 public final static String isConsequentOf_lname = "isConsequentOf";
	 public final static Property  isConsequentOf = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#isConsequentOf");

	 public final static String URL_lname = "URL";
	 public final static Property  URL = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#URL");

	 public final static String hasSource_lname = "hasSource";
	 public final static Property  hasSource = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasSource");

	 public final static String hasMember_lname = "hasMember";
	 public final static Property  hasMember = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasMember");

	 public final static String isFromEngine_lname = "isFromEngine";
	 public final static Property  isFromEngine = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#isFromEngine");

	 public final static String englishDescriptionTemplate_lname = "englishDescriptionTemplate";
	 public final static Property  englishDescriptionTemplate = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#englishDescriptionTemplate");

	 public final static String questionContent_lname = "questionContent";
	 public final static Property  questionContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#questionContent");

	 public final static String hasMetaBinding_lname = "hasMetaBinding";
	 public final static Property  hasMetaBinding = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasMetaBinding");

	 public final static String explanation_lname = "explanation";
	 public final static Property  explanation = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#explanation");

	 public final static String fromAnswer_lname = "fromAnswer";
	 public final static Property  fromAnswer = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#fromAnswer");

	 public final static String ruleNodeSet_lname = "ruleNodeSet";
	 public final static Property  ruleNodeSet = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#ruleNodeSet");

	 public final static String hasLongPrettyName_lname = "hasLongPrettyName";
	 public final static Property  hasLongPrettyName = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasLongPrettyName");

	 public final static String fromExpression_lname = "fromExpression";
	 public final static Property  fromExpression = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#fromExpression");

	 public final static String verificationMethod_lname = "verificationMethod";
	 public final static Property  verificationMethod = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#verificationMethod");

	 public final static String hasVariableMapping_lname = "hasVariableMapping";
	 public final static Property  hasVariableMapping = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasVariableMapping");

	 public final static String submitter_lname = "submitter";
	 public final static Property  submitter = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#submitter");

	 public final static String hasInferenceEngine_lname = "hasInferenceEngine";
	 public final static Property  hasInferenceEngine = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasInferenceEngine");

	 public final static String year_lname = "year";
	 public final static Property  year = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#year");

	 public final static String mapFrom_lname = "mapFrom";
	 public final static Property  mapFrom = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#mapFrom");

	 public final static String hasAnswer_lname = "hasAnswer";
	 public final static Property  hasAnswer = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAnswer");

	 public final static String firstSubmissionDate_lname = "firstSubmissionDate";
	 public final static Property  firstSubmissionDate = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#firstSubmissionDate");

	 public final static String toExpression_lname = "toExpression";
	 public final static Property  toExpression = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#toExpression");

	 public final static String hasConclusion_lname = "hasConclusion";
	 public final static Property  hasConclusion = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasConclusion");

	 public final static String hasTrustValue_lname = "hasTrustValue";
	 public final static Property  hasTrustValue = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasTrustValue");

	 public final static String forLanguage_lname = "forLanguage";
	 public final static Property  forLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#forLanguage");

	 public final static String englishExample_lname = "englishExample";
	 public final static Property  englishExample = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#englishExample");

	 public final static String hasProvenanceElement_lname = "hasProvenanceElement";
	 public final static Property  hasProvenanceElement = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasProvenanceElement");

	 public final static String dataCollectionStart_lname = "dataCollectionStart";
	 public final static Property  dataCollectionStart = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#dataCollectionStart");

	 public final static String mapTo_lname = "mapTo";
	 public final static Property  mapTo = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#mapTo");

	 public final static String hasOriginalContent_lname = "hasOriginalContent";
	 public final static Property  hasOriginalContent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasOriginalContent");

	 public final static String hasAntecedent_lname = "hasAntecedent";
	 public final static Property  hasAntecedent = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAntecedent");

	 public final static String fromAnswerOrQuery_lname = "fromAnswerOrQuery";
	 public final static Property  fromAnswerOrQuery = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#fromAnswerOrQuery");

	 public final static String month_lname = "month";
	 public final static Property  month = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#month");

	 public final static String day_lname = "day";
	 public final static Property  day = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#day");

	 public final static String spanFromByte_lname = "spanFromByte";
	 public final static Property  spanFromByte = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#spanFromByte");

	 public final static String hasRule_lname = "hasRule";
	 public final static Property  hasRule = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasRule");

	 public final static String hasReplacee_lname = "hasReplacee";
	 public final static Property  hasReplacee = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasReplacee");

	 public final static String inferenceEngineRule_lname = "inferenceEngineRule";
	 public final static Property  inferenceEngineRule = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#inferenceEngineRule");

	 public final static String escapeCharacter_lname = "escapeCharacter";
	 public final static Property  escapeCharacter = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#escapeCharacter");

	 public final static String toLanguage_lname = "toLanguage";
	 public final static Property  toLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#toLanguage");

	 public final static String hasIndex_lname = "hasIndex";
	 public final static Property  hasIndex = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasIndex");

	 public final static String hasAnswerPattern_lname = "hasAnswerPattern";
	 public final static Property  hasAnswerPattern = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasAnswerPattern");

	 public final static String englishDescription_lname = "englishDescription";
	 public final static Property  englishDescription = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#englishDescription");

	 public final static String hasTrustingParty_lname = "hasTrustingParty";
	 public final static Property  hasTrustingParty = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#hasTrustingParty");

	 public final static String fromLanguage_lname = "fromLanguage";
	 public final static Property  fromLanguage = _model.createProperty("http://inferenceweb.stanford.edu/2004/07/iw.owl#fromLanguage");

	// Instance (2)
	 public final static String KSL_lname = "KSL";
	 public final static Resource  KSL = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#KSL");

	 public final static String DirectAssertion_lname = "DirectAssertion";
	 public final static Resource  DirectAssertion = _model.createResource("http://inferenceweb.stanford.edu/2004/07/iw.owl#DirectAssertion");



}


 
