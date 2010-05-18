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
public class PMLP{

    private static Model _model = ModelFactory.createDefaultModel();

    protected static final String NS = "http://inference-web.org/2.0/pml-provenance.owl#";

    public static final String getURI(){ return NS; }

	// Class (29)
	 public final static String Website_lname = "Website";
	 public final static Resource  Website = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Website");

	 public final static String InferenceEngine_lname = "InferenceEngine";
	 public final static Resource  InferenceEngine = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#InferenceEngine");

	 public final static String Dataset_lname = "Dataset";
	 public final static Resource  Dataset = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Dataset");

	 public final static String DocumentFragment_lname = "DocumentFragment";
	 public final static Resource  DocumentFragment = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#DocumentFragment");

	 public final static String DocumentFragmentByOffset_lname = "DocumentFragmentByOffset";
	 public final static Resource  DocumentFragmentByOffset = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#DocumentFragmentByOffset");

	 public final static String Format_lname = "Format";
	 public final static Resource  Format = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Format");

	 public final static String TranslationRule_lname = "TranslationRule";
	 public final static Resource  TranslationRule = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#TranslationRule");

	 public final static String Information_lname = "Information";
	 public final static Resource  Information = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Information");

	 public final static String DeclarativeRule_lname = "DeclarativeRule";
	 public final static Resource  DeclarativeRule = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#DeclarativeRule");

	 public final static String WebService_lname = "WebService";
	 public final static Resource  WebService = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#WebService");

	 public final static String Language_lname = "Language";
	 public final static Resource  Language = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Language");

	 public final static String IdentifiedThing_lname = "IdentifiedThing";
	 public final static Resource  IdentifiedThing = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#IdentifiedThing");

	 public final static String LearnedSourceUsage_lname = "LearnedSourceUsage";
	 public final static Resource  LearnedSourceUsage = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#LearnedSourceUsage");

	 public final static String Organization_lname = "Organization";
	 public final static Resource  Organization = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Organization");

	 public final static String EmptyInformation_lname = "EmptyInformation";
	 public final static Resource  EmptyInformation = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#EmptyInformation");

	 public final static String Publication_lname = "Publication";
	 public final static Resource  Publication = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Publication");

	 public final static String Ontology_lname = "Ontology";
	 public final static Resource  Ontology = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Ontology");

	 public final static String Software_lname = "Software";
	 public final static Resource  Software = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Software");

	 public final static String AgentList_lname = "AgentList";
	 public final static Resource  AgentList = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#AgentList");

	 public final static String Source_lname = "Source";
	 public final static Resource  Source = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Source");

	 public final static String MethodRule_lname = "MethodRule";
	 public final static Resource  MethodRule = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#MethodRule");

	 public final static String Sensor_lname = "Sensor";
	 public final static Resource  Sensor = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Sensor");

	 public final static String PrettyNameMapping_lname = "PrettyNameMapping";
	 public final static Resource  PrettyNameMapping = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#PrettyNameMapping");

	 public final static String Person_lname = "Person";
	 public final static Resource  Person = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Person");

	 public final static String SourceUsage_lname = "SourceUsage";
	 public final static Resource  SourceUsage = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#SourceUsage");

	 public final static String Agent_lname = "Agent";
	 public final static Resource  Agent = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Agent");

	 public final static String DocumentFragmentByRowCol_lname = "DocumentFragmentByRowCol";
	 public final static Resource  DocumentFragmentByRowCol = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#DocumentFragmentByRowCol");

	 public final static String InferenceRule_lname = "InferenceRule";
	 public final static Resource  InferenceRule = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#InferenceRule");

	 public final static String PrettyNameMappingList_lname = "PrettyNameMappingList";
	 public final static Resource  PrettyNameMappingList = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#PrettyNameMappingList");

	 public final static String Document_lname = "Document";
	 public final static Resource  Document = _model.createResource("http://inference-web.org/2.0/pml-provenance.owl#Document");

	// Property (46)
	 public final static String hasDescription_lname = "hasDescription";
	 public final static Property  hasDescription = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasDescription");

	 public final static String hasFromOffset_lname = "hasFromOffset";
	 public final static Property  hasFromOffset = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasFromOffset");

	 public final static String hasRawString_lname = "hasRawString";
	 public final static Property  hasRawString = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasRawString");

	 public final static String hasToOffset_lname = "hasToOffset";
	 public final static Property  hasToOffset = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasToOffset");

	 public final static String hasDataCollectionStartDateTime_lname = "hasDataCollectionStartDateTime";
	 public final static Property  hasDataCollectionStartDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasDataCollectionStartDateTime");

	 public final static String usesInferenceEngine_lname = "usesInferenceEngine";
	 public final static Property  usesInferenceEngine = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#usesInferenceEngine");

	 public final static String hasReferenceSourceUsage_lname = "hasReferenceSourceUsage";
	 public final static Property  hasReferenceSourceUsage = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasReferenceSourceUsage");

	 public final static String hasReplacee_lname = "hasReplacee";
	 public final static Property  hasReplacee = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasReplacee");

	 public final static String hasToRow_lname = "hasToRow";
	 public final static Property  hasToRow = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasToRow");

	 public final static String hasContent_lname = "hasContent";
	 public final static Property  hasContent = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasContent");

	 public final static String hasName_lname = "hasName";
	 public final static Property  hasName = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasName");

	 public final static String hasEncoding_lname = "hasEncoding";
	 public final static Property  hasEncoding = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasEncoding");

	 public final static String hasFromCol_lname = "hasFromCol";
	 public final static Property  hasFromCol = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasFromCol");

	 public final static String hasUsageDateTime_lname = "hasUsageDateTime";
	 public final static Property  hasUsageDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasUsageDateTime");

	 public final static String hasMember_lname = "hasMember";
	 public final static Property  hasMember = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasMember");

	 public final static String hasPrettyNameMappingList_lname = "hasPrettyNameMappingList";
	 public final static Property  hasPrettyNameMappingList = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasPrettyNameMappingList");

	 public final static String hasPrettyName_lname = "hasPrettyName";
	 public final static Property  hasPrettyName = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasPrettyName");

	 public final static String hasEscapeCharacterSequence_lname = "hasEscapeCharacterSequence";
	 public final static Property  hasEscapeCharacterSequence = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasEscapeCharacterSequence");

	 public final static String hasToLanguage_lname = "hasToLanguage";
	 public final static Property  hasToLanguage = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasToLanguage");

	 public final static String hasAbstract_lname = "hasAbstract";
	 public final static Property  hasAbstract = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasAbstract");

	 public final static String hasISBN_lname = "hasISBN";
	 public final static Property  hasISBN = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasISBN");

	 public final static String hasPrettyString_lname = "hasPrettyString";
	 public final static Property  hasPrettyString = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasPrettyString");

	 public final static String hasLanguage_lname = "hasLanguage";
	 public final static Property  hasLanguage = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasLanguage");

	 public final static String hasRuleExample_lname = "hasRuleExample";
	 public final static Property  hasRuleExample = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasRuleExample");

	 public final static String hasURL_lname = "hasURL";
	 public final static Property  hasURL = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasURL");

	 public final static String hasPublisher_lname = "hasPublisher";
	 public final static Property  hasPublisher = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasPublisher");

	 public final static String hasEnglishDescriptionTemplate_lname = "hasEnglishDescriptionTemplate";
	 public final static Property  hasEnglishDescriptionTemplate = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasEnglishDescriptionTemplate");

	 public final static String hasSource_lname = "hasSource";
	 public final static Property  hasSource = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasSource");

	 public final static String hasDataCollectionEndDateTime_lname = "hasDataCollectionEndDateTime";
	 public final static Property  hasDataCollectionEndDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasDataCollectionEndDateTime");

	 public final static String hasShortPrettyName_lname = "hasShortPrettyName";
	 public final static Property  hasShortPrettyName = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasShortPrettyName");

	 public final static String hasModificationDateTime_lname = "hasModificationDateTime";
	 public final static Property  hasModificationDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasModificationDateTime");

	 public final static String hasMimetype_lname = "hasMimetype";
	 public final static Property  hasMimetype = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasMimetype");

	 public final static String hasFromRow_lname = "hasFromRow";
	 public final static Property  hasFromRow = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasFromRow");

	 public final static String hasLongPrettyName_lname = "hasLongPrettyName";
	 public final static Property  hasLongPrettyName = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasLongPrettyName");

	 public final static String hasPublicationDateTime_lname = "hasPublicationDateTime";
	 public final static Property  hasPublicationDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasPublicationDateTime");

	 public final static String hasFromLanguage_lname = "hasFromLanguage";
	 public final static Property  hasFromLanguage = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasFromLanguage");

	 public final static String hasVersion_lname = "hasVersion";
	 public final static Property  hasVersion = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasVersion");

	 public final static String hasInferenceEngineRule_lname = "hasInferenceEngineRule";
	 public final static Property  hasInferenceEngineRule = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasInferenceEngineRule");

	 public final static String hasFormat_lname = "hasFormat";
	 public final static Property  hasFormat = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasFormat");

	 public final static String hasCreationDateTime_lname = "hasCreationDateTime";
	 public final static Property  hasCreationDateTime = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasCreationDateTime");

	 public final static String hasOwner_lname = "hasOwner";
	 public final static Property  hasOwner = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasOwner");

	 public final static String hasAuthorList_lname = "hasAuthorList";
	 public final static Property  hasAuthorList = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasAuthorList");

	 public final static String hasToCol_lname = "hasToCol";
	 public final static Property  hasToCol = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasToCol");

	 public final static String hasDocument_lname = "hasDocument";
	 public final static Property  hasDocument = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasDocument");

	 public final static String hasUsageQueryContent_lname = "hasUsageQueryContent";
	 public final static Property  hasUsageQueryContent = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasUsageQueryContent");

	 public final static String isMemberOf_lname = "isMemberOf";
	 public final static Property  isMemberOf = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#isMemberOf");

	 public final static String hasConfidenceValue_lname = "hasConfidenceValue";
	 public final static Property  hasConfidenceValue = _model.createProperty("http://inference-web.org/2.0/pml-provenance.owl#hasConfidenceValue");

	// Instance (0)


}


 
