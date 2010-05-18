/* Copyright 2007 Inference Web Group.  All Rights Reserved. */

package org.inference_web.pml.v2.examples;

import java.util.*;

import org.inference_web.pml.shared.Config;
import org.inference_web.pml.v2.pmlj.*;
import org.inference_web.pml.v2.pmlj.impl.*;
import org.inference_web.pml.v2.pmlp.*;
import org.inference_web.pml.v2.pmlp.impl.*;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.*;

public class MDSAProofGenerator {	
	
	public static void genType1Proof () {
		// URI of English language provenance already in registry
		String languageURI = "http://inference-web.org/registry/LG/English.owl#English";
		
		// define single deployment namespace and file
		String namespace = "http://inference-web.org/proofs/mdsa/test1.owl";
		String fileName = "/home/csc/proofs/mdsa/test1.owl";

		// create agent provenance
		String agentURI = namespace+"#GiovanniUser";
		IWAgent agent = (IWAgent)PMLObjectManager.createPMLObject(PMLP.Agent_lname);
		agent.setHasName("Giovanni User");
		IWInformation agentDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		agentDescription.setHasURL("http://www.nasa.gov/audience/foreducators/9-12/features/giovanni-an-easier-way.html");
		agent.addHasDescription(agentDescription);
		agent.setIdentifier(PMLObjectManager.getObjectID(agentURI));
		
		// create inference rule provenance
		String ruleURI = namespace+"#MDSARule";
		IWInferenceRule rule= (IWInferenceRule)PMLObjectManager.createPMLObject(PMLP.DeclarativeRule_lname);
		rule.setHasName("MDSA Rule");
		IWInformation ruleDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		ruleDescription.setHasURL("http://tw.rpi.edu/wiki/MDSA");
		rule.addHasDescription(ruleDescription);
		rule.setIdentifier(PMLObjectManager.getObjectID(ruleURI));

		// create inference engine provenance
		String engineURI = namespace+"#MDSAEngine";
		IWInferenceEngine engine = (IWInferenceEngine)PMLObjectManager.createPMLObject(PMLP.InferenceEngine_lname);
		engine.setHasName("MDSA Engine");
		IWInformation engineDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		engineDescription.setHasURL("http://tw.rpi.edu/wiki/MDSA");
		engine.addHasDescription(engineDescription);
		engine.addHasInferenceEngineRule(ruleURI);
		engine.setIdentifier(PMLObjectManager.getObjectID(engineURI));
		
		// create leaf nodesets with source
		String timeConstraintsURI = namespace+"#timeConstraints";
		IWNodeSet timeConstraints = createSimpleNodeSetType1(timeConstraintsURI, "Time Constraints", languageURI, rule, engine, agent, null);
		String spatialConstraintsURI = namespace+"#spatialConstraints";
		IWNodeSet spatialConstraints = createSimpleNodeSetType1(spatialConstraintsURI, "Spatial Constraints", languageURI, rule, engine, agent, null);
		String parametersURI = namespace+"#parameters";
		IWNodeSet parameters = createSimpleNodeSetType1(parametersURI, "Parameters", languageURI, rule, engine, agent, null);
		String datasetURI = namespace+"#dataset";
		IWNodeSet dataset = createSimpleNodeSetType1(datasetURI, "dataset", languageURI, rule, engine, agent, null);
		String visualizationTypeURI = namespace+"#visualizationType";
		IWNodeSet visualizationType = createSimpleNodeSetType1(visualizationTypeURI, "Visualization Type", languageURI, rule, engine, agent, null);

		// create non-leaf nodesets
		String dataFetchURI = namespace+"#dataFetch";
		ArrayList<IWNodeSet> dataFetchAntecedents = new ArrayList();
		dataFetchAntecedents.add(timeConstraints);
		dataFetchAntecedents.add(parameters);		
		dataFetchAntecedents.add(dataset);
		IWNodeSet dataFetch = createSimpleNodeSetType1(dataFetchURI, "Data Fetch", languageURI, rule, engine, null, dataFetchAntecedents);

		String gridSubsetterURI = namespace+"#gridSubsetter";
		ArrayList<IWNodeSet> gridSubsetterAntecedents = new ArrayList();
		gridSubsetterAntecedents.add(dataFetch);
		gridSubsetterAntecedents.add(spatialConstraints);		
		IWNodeSet gridSubsetter = createSimpleNodeSetType1(gridSubsetterURI, "Grid Subsetter", languageURI, rule, engine, null, gridSubsetterAntecedents);

	
		String timeAveragingURI = namespace+"#timeAveraging";
		ArrayList<IWNodeSet> timeAveragingAntecedents = new ArrayList();
		timeAveragingAntecedents.add(gridSubsetter);
		IWNodeSet timeAveraging = createSimpleNodeSetType1(timeAveragingURI, "Time Averaging", languageURI, rule, engine, null, timeAveragingAntecedents);

		
		String dimensionAverageTypeURI = namespace+"#dimensionAverageType";
		ArrayList<IWNodeSet> dimensionAverageTypeAntecedents = new ArrayList();
		dimensionAverageTypeAntecedents.add(visualizationType);
		IWNodeSet dimensionAverageType = createSimpleNodeSetType1(dimensionAverageTypeURI, "Dimension Average Type", languageURI, rule, engine, null, dimensionAverageTypeAntecedents);

		String plotTypeURI = namespace+"#plotType";
		ArrayList<IWNodeSet> plotTypeAntecedents = new ArrayList();
		plotTypeAntecedents.add(visualizationType);
		IWNodeSet plotType = createSimpleNodeSetType1(plotTypeURI, "Plot Type", languageURI, rule, engine, null, plotTypeAntecedents);

		
		String dimensionAveragingURI = namespace+"#dimensionAveraging";
		ArrayList<IWNodeSet> dimensionAveragingAntecedents = new ArrayList();
		dimensionAveragingAntecedents.add(timeAveraging);
		dimensionAveragingAntecedents.add(dimensionAverageType);		
		IWNodeSet dimensionAveraging = createSimpleNodeSetType1(dimensionAveragingURI, "Dimension Averaging", languageURI, rule, engine, null, dimensionAveragingAntecedents);

		// create root nodeset
		String rendererURI = namespace+"#renderer";
		ArrayList<IWNodeSet> rendererAntecedents = new ArrayList();
		rendererAntecedents.add(dimensionAveraging);
		rendererAntecedents.add(plotType);		
		IWNodeSet renderer = createSimpleNodeSetType1(rendererURI, "Renderer", languageURI, rule, engine, null, rendererAntecedents);
System.out.println("save type1 proof");	

//		System.out.println(PMLObjectManager.printPMLObjectToString(renderer));
		if(PMLObjectManager.savePMLObject(renderer, fileName)) {
			System.out.println("Proof "+rendererURI+" saved to "+fileName);
		}
	}

	
	public static void genType2Provenance () {
		// URI of English language provenance already in registry
		String languageURI = "http://inference-web.org/registry/LG/English.owl#English";
		
		// define deployment namespaces and files. each provenence has its own namespace/file
		String ruleNamespace = "http://inference-web.org/proofs/mdsa/test2Rule.owl";
		String ruleFileName = "/home/csc/proofs/mdsa/test2Rule.owl";
		String engineNamespace = "http://inference-web.org/proofs/mdsa/test2Engine.owl";
		String engineFileName = "/home/csc/proofs/mdsa/test2Engine.owl";
		String agentNamespace = "http://inference-web.org/proofs/mdsa/test2Agent.owl";
		String agentFileName = "/home/csc/proofs/mdsa/test2Agent.owl";
		
		// create agent provenance
		String agentURI = agentNamespace+"#GiovanniUser";
		IWAgent agent = (IWAgent)PMLObjectManager.createPMLObject(PMLP.Agent_lname);
		agent.setHasName("Giovanni User");
		IWInformation agentDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		agentDescription.setHasURL("http://www.nasa.gov/audience/foreducators/9-12/features/giovanni-an-easier-way.html");
		agent.addHasDescription(agentDescription);
		agent.setIdentifier(PMLObjectManager.getObjectID(agentURI));
		
		// create inference rule provenance
		String ruleURI = ruleNamespace+"#MDSARule";
		IWInferenceRule rule= (IWInferenceRule)PMLObjectManager.createPMLObject(PMLP.DeclarativeRule_lname);
		rule.setHasName("MDSA Rule");
		IWInformation ruleDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		ruleDescription.setHasURL("http://tw.rpi.edu/wiki/MDSA");
		rule.addHasDescription(ruleDescription);
		rule.setIdentifier(PMLObjectManager.getObjectID(ruleURI));

		// create inference engine provenance
		String engineURI = engineNamespace+"#MDSAEngine";
		IWInferenceEngine engine = (IWInferenceEngine)PMLObjectManager.createPMLObject(PMLP.InferenceEngine_lname);
		engine.setHasName("MDSA Engine");
		IWInformation engineDescription = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
		engineDescription.setHasURL("http://tw.rpi.edu/wiki/MDSA");
		engine.addHasDescription(engineDescription);
		engine.addHasInferenceEngineRule(ruleURI);
		engine.setIdentifier(PMLObjectManager.getObjectID(engineURI));

		// save provenances
		PMLObjectManager.savePMLObject(agent, agentFileName);
		PMLObjectManager.savePMLObject(rule, ruleFileName);
		PMLObjectManager.savePMLObject(engine, engineFileName);
	}
	
	public static void genType2Proof () {
		// URI of English language provenance already in registry
		String languageURI = "http://inference-web.org/registry/LG/English.owl#English";
		
		// URI of provenances
		String ruleNamespace = "http://inference-web.org/proofs/mdsa/test2Rule.owl";
		String ruleURI = ruleNamespace+"#MDSARule";
		String engineNamespace = "http://inference-web.org/proofs/mdsa/test2Engine.owl";
		String engineURI = engineNamespace+"#MDSAEngine";
		String agentNamespace = "http://inference-web.org/proofs/mdsa/test2Agent.owl";
		String agentURI = agentNamespace+"#GiovanniUser";

		// define deployment namespace and file
		String proofNamespace = "http://inference-web.org/proofs/mdsa/test2Proof.owl";
		String proofFileName = "/home/csc/proofs/mdsa/test2Proof.owl";

		// create leaf nodesets with source
		String timeConstraintsURI = proofNamespace+"#timeConstraints";
		IWNodeSet timeConstraints = createSimpleNodeSetType2(timeConstraintsURI, "Time Constraints", languageURI, ruleURI, engineURI, agentURI, null);
		String spatialConstraintsURI = proofNamespace+"#spatialConstraints";
		IWNodeSet spatialConstraints = createSimpleNodeSetType2(spatialConstraintsURI, "Spatial Constraints", languageURI, ruleURI, engineURI, agentURI, null);
		String parametersURI = proofNamespace+"#parameters";
		IWNodeSet parameters = createSimpleNodeSetType2(parametersURI, "Parameters", languageURI, ruleURI, engineURI, agentURI, null);
		String datasetURI = proofNamespace+"#dataset";
		IWNodeSet dataset = createSimpleNodeSetType2(datasetURI, "dataset", languageURI, ruleURI, engineURI, agentURI, null);
		String visualizationTypeURI = proofNamespace+"#visualizationType";
		IWNodeSet visualizationType = createSimpleNodeSetType2(visualizationTypeURI, "Visualization Type", languageURI, ruleURI, engineURI, agentURI, null);

		// create non-leaf nodesets
		String dataFetchURI = proofNamespace+"#dataFetch";
		ArrayList<IWNodeSet> dataFetchAntecedents = new ArrayList();
		dataFetchAntecedents.add(timeConstraints);
		dataFetchAntecedents.add(parameters);		
		dataFetchAntecedents.add(dataset);
		IWNodeSet dataFetch = createSimpleNodeSetType2(dataFetchURI, "Data Fetch", languageURI, ruleURI, engineURI, null, dataFetchAntecedents);

		String gridSubsetterURI = proofNamespace+"#gridSubsetter";
		ArrayList<IWNodeSet> gridSubsetterAntecedents = new ArrayList();
		gridSubsetterAntecedents.add(dataFetch);
		gridSubsetterAntecedents.add(spatialConstraints);		
		IWNodeSet gridSubsetter = createSimpleNodeSetType2(gridSubsetterURI, "Grid Subsetter", languageURI, ruleURI, engineURI, null, gridSubsetterAntecedents);

	
		String timeAveragingURI = proofNamespace+"#timeAveraging";
		ArrayList<IWNodeSet> timeAveragingAntecedents = new ArrayList();
		timeAveragingAntecedents.add(gridSubsetter);
		IWNodeSet timeAveraging = createSimpleNodeSetType2(timeAveragingURI, "Time Averaging", languageURI,ruleURI, engineURI, null, timeAveragingAntecedents);

		
		String dimensionAverageTypeURI = proofNamespace+"#dimensionAverageType";
		ArrayList<IWNodeSet> dimensionAverageTypeAntecedents = new ArrayList();
		dimensionAverageTypeAntecedents.add(visualizationType);
		IWNodeSet dimensionAverageType = createSimpleNodeSetType2(dimensionAverageTypeURI, "Dimension Average Type", languageURI, ruleURI, engineURI, null, dimensionAverageTypeAntecedents);

		String plotTypeURI = proofNamespace+"#plotType";
		ArrayList<IWNodeSet> plotTypeAntecedents = new ArrayList();
		plotTypeAntecedents.add(visualizationType);
		IWNodeSet plotType = createSimpleNodeSetType2(plotTypeURI, "Plot Type", languageURI, ruleURI, engineURI, null, plotTypeAntecedents);

		
		String dimensionAveragingURI = proofNamespace+"#dimensionAveraging";
		ArrayList<IWNodeSet> dimensionAveragingAntecedents = new ArrayList();
		dimensionAveragingAntecedents.add(timeAveraging);
		dimensionAveragingAntecedents.add(dimensionAverageType);		
		IWNodeSet dimensionAveraging = createSimpleNodeSetType2(dimensionAveragingURI, "Dimension Averaging", languageURI, ruleURI, engineURI, null, dimensionAveragingAntecedents);

		// create root nodeset
		String rendererURI = proofNamespace+"#renderer";
		ArrayList<IWNodeSet> rendererAntecedents = new ArrayList();
		rendererAntecedents.add(dimensionAveraging);
		rendererAntecedents.add(plotType);		
		IWNodeSet renderer = createSimpleNodeSetType2(rendererURI, "Renderer", languageURI, ruleURI, engineURI, null, rendererAntecedents);
		
		// save proof
//		System.out.println(PMLObjectManager.printPMLObjectToString(renderer));
		if(PMLObjectManager.savePMLObject(renderer, proofFileName)) {
			System.out.println("Proof "+rendererURI+" saved to "+proofFileName);
		}
	}


  public static IWNodeSet createSimpleNodeSetType1(String nsURI, String conclusionRawString, String languageURI, IWInferenceRule rule, IWInferenceEngine engine, IWSource source, List antecedents) { 
  	// create Information instance and assign property values
System.out.println("create pml object information");	
  	IWInformation conclusion = null;
  	if (conclusionRawString != null){
  		conclusion = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
  		if (conclusionRawString != null ) {
  			conclusion.setHasRawString(conclusionRawString);
  		}
  		if (languageURI != null) {
  			conclusion.setHasLanguage(languageURI);
  		}
  	}

System.out.println("create pml object nodeset");	
  	// create NodeSet with specified ontology and class
  	IWNodeSet ns = (IWNodeSet)PMLObjectManager.createPMLObject(PMLJ.NodeSet_lname);
  	// assign NodeSet name
  	ns.setIdentifier(PMLObjectManager.getObjectID(nsURI));
  	// assign NodeSet conclusion
  	ns.setHasConclusion(conclusion);  	
  	
  	// create SourceUsage with source URI
  	IWSourceUsage srcUsg = null;
  	if (source!=null) {
  		srcUsg = (IWSourceUsage)PMLObjectManager.createPMLObject(PMLP.SourceUsage_lname);
  		srcUsg.setHasSource(source);
  	}
  	
System.out.println("create pml object inf step");	
  	// create InferenceStep instance and assign property values
  	IWInferenceStep infStep = (IWInferenceStep)PMLObjectManager.createPMLObject( PMLJ.InferenceStep_lname);
System.out.println(" done create pml object inf step");	
  	infStep.setHasIndex(0);
System.out.println("inf step 2");	
	infStep.setHasInferenceEngine(engine);
System.out.println("inf step 3");	
infStep.setHasInferenceRule(rule); 
System.out.println("inf step 4");	
 	infStep.setHasAntecedentList(antecedents);
System.out.println("inf step 5");	
  	infStep.setHasSourceUsage(srcUsg);

System.out.println("inf step 6");	
  	ArrayList infSteps = new ArrayList();
System.out.println("inf step 7");	
  	infSteps.add(infStep); 	
System.out.println("inf step 8");	
  	ns.setIsConsequentOf(infSteps);
System.out.println("done inf step");	

  	return ns;
  }

  public static IWNodeSet createSimpleNodeSetType2(String nsURI, String conclusionRawString, String languageURI, String ruleURI, String engineURI, String sourceURI, List antecedents) { 
  	// create Information instance and assign property values
  	IWInformation conclusion = null;
  	if (conclusionRawString != null){
  		conclusion = (IWInformation)PMLObjectManager.createPMLObject(PMLP.Information_lname);
  		if (conclusionRawString != null ) {
  			conclusion.setHasRawString(conclusionRawString);
  		}
  		if (languageURI != null) {
  			conclusion.setHasLanguage(languageURI);
  		}
  	}
  	
  	// create NodeSet with specified ontology and class
  	IWNodeSet ns = (IWNodeSet)PMLObjectManager.createPMLObject(PMLJ.NodeSet_lname);
  	// assign NodeSet name
  	ns.setIdentifier(PMLObjectManager.getObjectID(nsURI));
  	// assign NodeSet conclusion
  	ns.setHasConclusion(conclusion);  	
  	
  	// create SourceUsage with source URI
  	IWSourceUsage srcUsg = null;
  	if (sourceURI!=null) {
  		srcUsg = (IWSourceUsage)PMLObjectManager.createPMLObject(PMLP.SourceUsage_lname);
  		srcUsg.setHasSource(sourceURI);
  	}
  	
  	// create InferenceStep instance and assign property values
  	IWInferenceStep infStep = (IWInferenceStep)PMLObjectManager.createPMLObject( PMLJ.InferenceStep_lname);
  	infStep.setHasIndex(0);
  	infStep.setHasInferenceRule(ruleURI); 
 		infStep.setHasInferenceEngine(engineURI);
  	infStep.setHasAntecedentList(antecedents);
  	infStep.setHasSourceUsage(srcUsg);

  	ArrayList infSteps = new ArrayList();
  	infSteps.add(infStep); 	
  	ns.setIsConsequentOf(infSteps);

  	return ns;
  }


  /**
	 * @param args
	 */
	public static void main(String[] args) {

		// generate a proof and provenances
		genType1Proof();
		
		// generate provenances and then proof
//		genType2Provenance();
//		genType2Proof();
	}
}
/* END OF MDSAProofGenerator */
