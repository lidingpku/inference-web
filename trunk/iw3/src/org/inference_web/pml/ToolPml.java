package org.inference_web.pml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.app.pml.PMLR;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class ToolPml {
	
	public static Set<Resource> list_roots(Model m){
		Set<Resource> set_ns = m.listSubjectsWithProperty(PMLJ.isConsequentOf).toSet();
		set_ns.removeAll(m.listObjectsOfProperty(PMLDS.first).toSet());
		Set<Resource> set_info = new HashSet<Resource>();
		for(Resource res_ns: set_ns){
			Resource res_info = ToolJena.getValueOfProperty(m, res_ns, PMLJ.hasConclusion, (Resource)null);
			set_info.add(res_info);
		}
		return set_info;
	}
	
	public static Set<Resource> list_info(Model m, Set<Resource> set_res_step){
		Set<Resource> ret = new HashSet<Resource>();
		for(Resource res_step: set_res_step){
			ret.addAll(list_info(m,res_step));
		}
		return ret;
	}
	
	public static Set<Resource> list_info(Model m, Resource res_step){
		Set<Resource> ret = new HashSet<Resource>();
		for (RDFNode node: m.listObjectsOfProperty(res_step,PMLR.hasInput).toSet()){
			ret.add((Resource)node);
		}
		for (RDFNode node: m.listObjectsOfProperty(res_step,PMLR.hasOutput).toSet()){
			ret.add((Resource)node);
		}
		return ret;
	}
	 
	public static Set<Resource> list_depending_steps(Model m, Resource res_info_root){
		Set<Resource> set_step = m.listSubjectsWithProperty(RDF.type,PMLR.Step).toSet();
		Set<RDFNode> set_step_depends = null;
		for(Resource res_step_root: m.listSubjectsWithProperty(PMLR.hasOutput, res_info_root).toSet()){
			if (null==set_step_depends)
				set_step_depends = m.listObjectsOfProperty(res_step_root,PMLR.dependsOn).toSet();
			else
				set_step_depends.addAll( m.listObjectsOfProperty(res_step_root,PMLR.dependsOn).toSet());
		}
		set_step.retainAll(set_step_depends);
		return set_step;
	}
	
	/**
	 * sign pml model, return something new
	 * 
	 * @param m
	 * @param sz_namespace_bnode
	 * @return
	 */
	public static Model pml_sign(Model m, String sz_namespace_bnode){
		return ToolJena.model_signBlankNode(m, sz_namespace_bnode);
	}

	/**
	 * normalize a PML model
	 * * decouple recursive list
	 * * add index data: including dependsOn relation, and input/output links 
	 * * make dependsOn relation transitive
	 * 
	 * @param model_norm
	 * @return
	 */
	public static void pml_index(Model model_norm){	
		getLogger().info(model_norm.size());
		//increment model with decoupled list
    	ToolJena.model_update_List2Map(model_norm, RDF.first, RDF.rest, PMLR.hasMember, false);
    	ToolJena.model_update_List2Map(model_norm, PMLDS.first, PMLDS.rest, PMLR.hasMember, false);
    	
    	//add PMLR namespace declaration
    	model_norm.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(), PMLR.getURI());

    	////////////////////////////////
    	// add index data
    	// * transitive dependency relation among inferencestep and nodeset
    	// * pmlr step connecting information as input and output
    	try {
    		String filename = "pml_index.sparql";
        	InputStream in = ToolPml.class.getResourceAsStream(filename);
			String sz_sparql_query = ToolIO.pipeInputStreamToString(in);
			
			AgentSparql o_sparql = new  AgentSparql();
			Dataset dataset = DatasetFactory.create(model_norm);
			Object ret = o_sparql.exec(sz_sparql_query, dataset, RDFSYNTAX.RDFXML);
			if (!(ret instanceof Model)){
				System.out.println("Error");
				System.exit(-1);
			}

			Model model_more = (Model)ret;
//System.out.println(ToolJena.printModelToString(model_more));			
			//add transitive inference
			ToolJena.model_add_transtive(model_more, PMLR.dependsOn);
			
			ToolJena.model_merge(model_norm, model_more);
			
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
		getLogger().info(model_norm.size());

	}
	
	
	
	private static Logger getLogger() {
		return Logger.getLogger(ToolPml.class);
	}

	private static Model load(String sz_url, Map<String, Model> map_url_model){
		Model m = map_url_model.get(sz_url);
		if (null==m){
			System.out.println("load " + sz_url);
			AgentModelLoader loader = new AgentModelLoader(sz_url);
			m=loader.getModelData();
			if (null!=m)
				map_url_model.put(sz_url, m);
		}
		return m;
	}
	
	
	//public static final int MODEL_ME=0;
	//public static final int MODEL_ALL=1;
	
	public static Map<String, Model>  pml_load(String sz_url_pml ){
		Map<String, Model> map_url_model = new HashMap<String, Model> ();
		pml_load(sz_url_pml, map_url_model);
		
		/*
		Model[] ret = new Model[2];
		Model model_me = map_url_model.get(sz_url_pml);
		ret[MODEL_ME] = model_me;
		
		Model model_all= ModelFactory.createDefaultModel();
		for (Model m: map_url_model.values()){
			model_all.add(m);
		}
		ret[MODEL_ALL] = model_all; 
		*/
		
		return map_url_model;
	}



	/**
	 * recursively load pml model
	 * 
	 * @param sz_url_pml
	 * @param map_url_model
	 * @return
	 */
	public static Model pml_load(String sz_url_pml, Map<String, Model> map_url_model){
		HashSet<String> set_visited = new HashSet<String>();
		HashSet<String> set_to_visit= new HashSet<String>();
		sz_url_pml = DataQname.extractNamespaceUrl(sz_url_pml);
		if (ToolSafe.isEmpty(sz_url_pml))
			return null;
			
		set_to_visit.add(sz_url_pml);
		
		Resource[] ary_type = new Resource[]{
			PMLJ.NodeSet,	
			PMLP.Information,	
			PMLJ.InferenceStep,	
		};
		
		Property[] ary_property = new Property[]{
				PMLDS.first,	
			};
		
		do {
			//load the base model
			String sz_url_temp = set_to_visit.iterator().next();
			Model model = load(sz_url_temp, map_url_model);
			
			if (null==model)
				continue;
			
			set_visited.add(sz_url_temp);

			for (Resource type: ary_type){
				for (Resource instance: model.listSubjectsWithProperty(RDF.type, type).toSet()){
					if (!instance.isURIResource())
						continue;
					
					String sz_url_new = DataQname.extractNamespaceUrl(instance.getURI());
					if (ToolSafe.isEmpty(sz_url_new)){
						System.out.println(instance.getURI());
					}else{
						set_to_visit.add(sz_url_new);
					}
				}
			}
			
			for (Property property: ary_property){
				for (RDFNode node: model.listObjectsOfProperty(property).toSet()){
					if (!node.isURIResource())
						continue;
					
					Resource instance = (Resource) node;
					
					String sz_url_new = DataQname.extractNamespaceUrl(instance.getURI());
					if (ToolSafe.isEmpty(sz_url_new)){
						System.out.println(instance.getURI());
					}else{
						set_to_visit.add(sz_url_new);
					}
				}
			}
			
			
			set_to_visit.removeAll(set_visited);

		}while(set_to_visit.size()>0);

		return map_url_model.get(sz_url_pml);
	}
	
	
	private static int gid = 0;
	public static String IWV_BASE_URI = "http://inference-web.org/vocab/";
	public static Resource create_resource(Resource type, Model m){
		String sz_id = "_"+gid;
		gid++;
		if (PMLP.Information.equals(type))
			sz_id= "info"+sz_id;
		else if (PMLJ.NodeSet.equals(type))
			sz_id= "ns"+sz_id;
		else if (PMLJ.InferenceStep.equals(type))
			sz_id= "is"+sz_id;
		else if (PMLJ.NodeSetList.equals(type))
			sz_id= "list"+sz_id;
		
		return m.createResource(IWV_BASE_URI+sz_id).addProperty(RDF.type, type);
	}
	
	public static Model create_pml2_model(Set<Resource> set_res_step, Model model_ref){
		Model model_data = ModelFactory.createDefaultModel();
		for (Resource res_step: set_res_step){
			copy_step(model_data, res_step, model_ref);
		}
		model_data = ToolJena.model_unsignBlankNode(model_data, PMLJ.InferenceStep);
		model_data = ToolJena.model_unsignBlankNode(model_data, PMLP.Information);
		return model_data;
	}
	
	public static void copy_step(Model model_data, Resource res_step, Model model_ref ){
		Resource res_step_output= create_resource(PMLJ.InferenceStep, model_data);
		copy_description(model_data, res_step_output, model_ref, res_step, PMLJ.hasInferenceEngine);
		copy_description(model_data, res_step_output, model_ref, res_step, PMLJ.hasInferenceRule);
		
		Resource res_info_conclusion = (Resource) model_ref.listObjectsOfProperty(res_step, PMLR.hasOutput).next();
		Resource res_ns_conclusion = create_resource(PMLJ.NodeSet, model_data);
		res_ns_conclusion.addProperty(PMLJ.hasConclusion, res_info_conclusion);
		model_data.add(model_ref.listStatements(res_info_conclusion,null,(String)null));

		Set<Resource> set_ns_antecedent = new HashSet<Resource>();
		for (RDFNode node: model_ref.listObjectsOfProperty(res_step, PMLR.hasOutput).toSet()){
			Resource res_info = (Resource) node;
			Resource res_ns = create_resource(PMLJ.NodeSet, model_data);
			res_ns.addProperty(PMLJ.hasConclusion, res_info);
			model_data.add(model_ref.listStatements(res_info,null,(String)null));
			set_ns_antecedent.add(res_ns);
		}
		
		create_step(model_data,res_step, res_ns_conclusion,set_ns_antecedent );
	}
	
	private static void copy_description(Model model_data,Resource res_data, Model model_ref, Resource res_ref, Property prop){
		for (Statement stmt: model_ref.listStatements(res_ref,prop,(String)null).toSet()){
			model_data.add(model_data.createStatement(res_data, stmt.getPredicate(), stmt.getObject()));
		}
	}
	
	public static void create_step(Model model_data, Resource res_step, Resource res_conclsion, Set<Resource> set_antecedents ){
		res_conclsion.addProperty(PMLJ.isConsequentOf, res_step);

		Resource parent = null;
		for (Resource ns : set_antecedents){
			Resource list =create_resource(PMLJ.NodeSetList, model_data);
			list.addProperty(PMLDS.first, ns);
			if (null!=parent)
				parent.addProperty(PMLDS.rest, list);
			else
				res_step.addProperty(PMLJ.hasAntecedentList, list);
			parent = list;
		}
	}


	public static void pml_gen_deductive_ontology(String sz_filename){
		if (ToolSafe.isEmpty(sz_filename))
			sz_filename= "output/pml/pml-ontologies.rdf";
		ToolJena.printModelToFile(pml_gen_deductive_ontology(), sz_filename);

	}		
	public static Model pml_gen_deductive_ontology(){

		//load pml ontologies and make a decutive closure
		Model m= ModelFactory.createDefaultModel();
		m.read(PMLP.getURI());
		m.read(PMLJ.getURI());
		m.read(IW200407.getURI());
		
		getLogger().info("original ontology size: "+m.size());
		m = ToolJena.model_createDeductiveClosure(m);
		getLogger().info("deduction ontology size: "+m.size());
		
		return m;
	}
}
