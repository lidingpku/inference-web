/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/
package org.inference_web.pml;

import org.apache.log4j.Logger;

import sw4j.app.oie.AgentModelLoaderOie;
import sw4j.app.oie.DataEvaluationConfig;
import sw4j.app.oie.DefaultOwlInstanceDataChecker;
import sw4j.task.common.DataTaskResult;


/**
 * Validate pml instance data.  
 * 
 * @author Li Ding
 *
PmlValidator helps users to validate whether an RDF graph (either addressed as a URI or
simply supplied as a jena Model) contains valid PML instance data. A
well-formed PML document is a document that successfully passes PML
validation. Besides validating just one RDF graph.
<ul>
  <li>inspect(String szFileOrUrl) // validate only the present URL</li>
  <li>inspect(String szText, String szXmlBase, String String szTextRdfSyntax) // parse RDF graph from szText; validate it and its dependent files max_depth hops away  </li>
</ul>
 *
 */
public class AgentPmlValidator extends DefaultOwlInstanceDataChecker{

	private Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	/**
	 * validate PML instance data  
	 * 
	 * 
	 * @param model_load		the RDF model to be validated
	 * @return
	 */
	public DataTaskResult inspect(AgentModelLoaderOie model_load, DataTaskResult der, DataEvaluationConfig vc){
		
		// inspect PML related issues and filter PML data
		InspectFilterPmlData inspector = InspectFilterPmlData.inspect(model_load.getModelData(),true);
		getLogger().info(inspector.getReport().getConclusionMessage());
		der.reports.add(inspector.getReport());

		//inspect regular issues  on filter data (only PML part)
		super.inspect(model_load, der, vc);
		
		return der;
	}
	
	
	
/*	

	
	public static String PML_ERROR_MSG_NO_RDF_DATA =
		"[PMLValidator][msg 1]{ERROR: The supplied RDF document does not contain any RDF data}";
	
	public static String PML_ERROR_MSG_NO_PML_DATA = 
		"[PMLValidator][msg 2]{ERROR: Cannot find PML data in specific document or its referred ontologies. " +
		"The existence of PML data can be decided by checking the usage of classes and properties defined by PML ontologies. " +
		"Currently, PMLValidator supports the following PML ontologies: " + PMLConst.listSupportedPmlNamespace()+"."+
		"The PML ontology published at Stanford are old versions, and the current PML ontologies are published under http://inference-web.org/2.0/ .}"; 

	public static final String PML_VALIDATION_ERROR_ANONYMOUS_CLASS = "[PMLValidator][msg 3] a PML instance document should not have any instace of an unnamed class.";
	public static final String PML_VALIDATION_ERROR_ANONYMOUS_UNEXPECTED_DEFINITION = " [PMLValidator][msg 4] PML instance document should not redefine PML class or property. see : ";
	
	public static final String PML_VALIDATION_ERROR_CLASS_UNDEFINED = " [PMLValidator][msg 5]  an RDF class is used in this PML document, but it is not defined in either the instance data and the corresponding ontologies: ";
	public static final String PML_VALIDATION_ERROR_CLASS_UNDEFINED_IN_ONTO = " [PMLValidator][msg 6]  an RDF class is used and defined in this PML document, but it is not defined in the corresponding ontology: ";
	public static final String PML_VALIDATION_ERROR_PROPERTY_UNDEFINED = " [PMLValidator][msg 7] an RDF property is used in this PML document, but it is not defined in either the instance data and the corresponding ontologies: ";
	public static final String PML_VALIDATION_ERROR_PROPERTY_UNDEFINED_IN_ONTO = " [PMLValidator][msg 8]  an RDF property is used and defined in this PML document, but it is not defined in the corresponding ontology: ";

	public static final String PML_VALIDATION_ERROR_OWL_DL_FAILED = " [PMLValidator][msg 9] OWL DL validation failed. [Details] ";
	public static final String PML_VALIDATION_ERROR_DOMAIN_RANGE_UNDEFINED_PROPERTY = " [PMLValidator][msg 10] property not defined in corresponding ontology, unable to check its domain/range. [Details] ";
	public static final String PML_VALIDATION_ERROR_POTENTIAL_INCOMPATIABLE_DOMAIN = " [PMLValidator][msg 11] instance types not matching domains of property. [Details] ";
	public static final String PML_VALIDATION_ERROR_POTENTIAL_INCOMPATIABLE_RANGE = " [PMLValidator][msg 12] instance types not matching ranges of property. [Details] ";
	
	public static final String PML_VALIDATION_ERROR_UNDEFINED_CLASS = " [PMLValidator][msg 13] class undefined due to either (i) unable to load ontology or (ii) the concept does not exist in the ontology. [Details] ";
	public static final String PML_VALIDATION_ERROR_ANONYMOUS_INSTANCE = " [PMLValidator][msg 14] In PML instance data, instances of the following classes must have URI and cannot be anonymous: ";
	
	public static final String PML_VALIDATION_ERROR_CARD = " [PMLValidator][msg 15] mismatching owl:cardinality. [Details] ";
	public static final String PML_VALIDATION_ERROR_CARD_WARN = " [PMLValidator][msg 16] potential mismatching owl:cardinality. [Details] ";
	public static final String PML_VALIDATION_ERROR_MAX_CARD = " [PMLValidator][msg 17] mismatching owl:maxCardinality. [Details] ";
	public static final String PML_VALIDATION_ERROR_MIN_CARD_WARN = " [PMLValidator][msg 18] potential mismatching owl:minCardinality. [Details] ";

	public static final String PML_VALIDATION_ONTO_USAGE = " [PMLValidator][msg 19] the following ontologies are used in the supplied PML data: ";
	
	public static final String PML_VALIDATION_MSG_VALIDATION_LOAD =
		"[check point] loading ontologies used by supplied document...";

	public static final String PML_VALIDATION_MSG_VALIDATION_OWL_DL =
		"[check point] validating OWL DL semantics  ...";

	public static final String PML_VALIDATION_MSG_VALIDATION_PML_DATA =
		"[check point] PML validation - checking existence of PML data ...";
	public static final String PML_VALIDATION_MSG_VALIDATION_CLASS_PROPERTY_DEFINITION=
		"[check point] PML validation - checking class/property definition in PML data ...";
	
	public static final String PML_VALIDATION_MSG_VALIDATION_PML_DOMAIN_RANGE =
		"[check point] PML validation - checking domain/range compatibility in PML data...";
	public static final String PML_VALIDATION_MSG_VALIDATION_PML_CARDINALITY =
		"[check point] PML validation - checking cardinality restrictions in PML data...";

	public static final String PML_VALIDATION_MSG_VALIDATION_PML_UNNAMED_INSTANCE =
		"[check point] PML validation - checking URI of instance in PML data  ...";
	public static final String PML_VALIDATION_MSG_VALIDATION_PML_OTHER=
		"[check point] PML validation - checking usage of PML ontology...";
	
	public static final String PML_VALIDATION_STATUS_START=
		"[Validation Status] one-step validation started.";
	public static final String PML_VALIDATION_STATUS_FAILED =
		"[Validation Status] one-step validation failed, so the supplied document is not a valid PML document.";
	public static final String PML_VALIDATION_STATUS_SUCCEED=
		"[Validation Status] one-step validation succeed.";

	public static final String PML_VALIDATION_RECURSIVE_STATUS_START=
		"[Validation Status] recursive validation started";
	public static final String PML_VALIDATION_RECURSIVE_STATUS_STOP=
		"[Validation Status] recursive validation stopped. Depth limit reached";
	public static final String PML_VALIDATION_RECURSIVE_STATUS_STOP_FAILED=
		"[Validation Status] recursive validation stopped due to failure in one-step validation";
	public static final String PML_VALIDATION_RECURSIVE_STATUS_STEP=
		"[Validation Status] now validating:";
	public static final String PML_VALIDATION_RECURSIVESTATUS_FAILED =
		"[Validation Status] recursive validation failed. ";
	public static final String PML_VALIDATION_RECURSIVESTATUS_SUCCEED=
		"[Validation Status] recursive validation succeed.";

*/	
}
