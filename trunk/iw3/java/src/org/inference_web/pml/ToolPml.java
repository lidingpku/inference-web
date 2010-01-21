package org.inference_web.pml;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
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
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVHMap;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
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


	public static final int OPT_LIST_INPUT=0;
	public static final int OPT_LIST_OUTPUT=1;
	public static final int OPT_LIST_ALL=2;

	
	public static Set<Resource> listInfoOfStep(Model m, Set<Resource> set_res_step, int option){
		Set<Resource> ret = new HashSet<Resource>();
		for(Resource res_step: set_res_step){
			ret.addAll(listInfoOfStep(m,res_step, option));
		}
		return ret;
	}
	
	public static Set<Resource> listInfoOfStep(Model m, Resource res_step, int option){
		
		Set<Resource> ret = new HashSet<Resource>();
		if (option==OPT_LIST_INPUT || option==OPT_LIST_ALL)
			for (RDFNode node: listInfoInputOfStep(res_step,m)){
				ret.add((Resource)node);
			}
		
		if (option==OPT_LIST_OUTPUT || option==OPT_LIST_ALL)
			for (RDFNode node: listInfoOutputOfStep(res_step,m)){
				ret.add((Resource)node);
			}
		return ret;
	}
	 /*
	private static Set<Resource> list_info(Model m, int option){
		Set<Resource> ret = new HashSet<Resource>();
		for (RDFNode node: m.listObjectsOfProperty(PMLR.hasInput).toSet()){
			ret.add((Resource)node);
		}
		for (RDFNode node: m.listObjectsOfProperty(PMLR.hasOutput).toSet()){
			ret.add((Resource)node);
		}
		return ret;
	}
*/
	public static Set<Resource> listStepDerivingInfoRecursive(Model m, Resource res_info_root){
		//Set<Resource> set_step = m.listSubjectsWithProperty(RDF.type,PMLJ.InferenceStep).toSet();
		Set<Resource> set_step_depends = new HashSet<Resource>();
		for(Resource res_step_root: listStepDerivingInfo(res_info_root, m)){
			set_step_depends.add(res_step_root);
			for (RDFNode node_input: listInfoInputOfStep(res_step_root, m) ){
				set_step_depends.addAll(listStepDerivingInfoRecursive(m, (Resource)node_input));
			}
			
/*TODO
  			if (null==set_step_depends) 
				set_step_depends = m.listObjectsOfProperty(res_step_root,PMLR.dependsOn).toSet();
			else
				set_step_depends.addAll( m.listObjectsOfProperty(res_step_root,PMLR.dependsOn).toSet());
*/		}
		//set_step.retainAll(set_step_depends);
		return set_step_depends;
	}

	


	
	public static Model pml_create_normalize(Model m, String sz_namespace_base){
			
		Model model_norm = ToolJena.create_signBlankNode(m, sz_namespace_base);

		//TODO: to be remove in the future
		model_norm = ToolJena.create_unsignBlankNode(model_norm, PMLJ.InferenceStep);
		model_norm = ToolJena.create_unsignBlankNode(model_norm, PMLJ.NodeSetList);

		//decouple list
		pml_update_decouple_list(model_norm);

		return model_norm;
	}



	public static void pml_update_decouple_list(Model model_norm){
		//increment model with decoupled list
    	ToolJena.update_decoupleList(model_norm, RDF.first, RDF.rest, PMLR.hasMember, false);
    	ToolJena.update_decoupleList(model_norm, PMLDS.first, PMLDS.rest, PMLR.hasMember, false);
    	
    	//add PMLR namespace declaration
    	model_norm.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(), PMLR.getURI());
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
	public static void pml_update_index(Model model_norm){
		pml_update_decouple_list(model_norm);
		Model model_more = pml_create_index(model_norm);
		ToolJena.update_copy(model_norm, model_more);

	}
	public static Model pml_create_index(Model model_norm){	
    	////////////////////////////////
    	// add index data
    	// * transitive dependency relation among inferencestep and nodeset
    	// * pmlr step connecting information as input and output
    	try {
    		String filename = "pml_index.sparql";
    		getLogger().info(ToolPml.class.getResource(filename));
        	InputStream in = ToolPml.class.getResourceAsStream(filename);
			String sz_sparql_query = ToolIO.pipeInputStreamToString(in);
			
			AgentSparql o_sparql = new  AgentSparql();
			Dataset dataset = DatasetFactory.create(model_norm);
			Object ret = o_sparql.exec(sz_sparql_query, dataset, RDFSYNTAX.RDFXML);
			if (!(ret instanceof Model)){
				getLogger().fatal("Error");
				System.exit(-1);
			}

			Model model_more = (Model)ret;
//System.out.println(ToolJena.printModelToString(model_more));			
			//add transitive inference
//TODO
//			ToolJena.updateModelTranstive(model_more, PMLR.dependsOn);
			
			ToolJena.update_copyNsPrefix(model_more, model_norm);
			
			getLogger().info("create pml index with size: " + model_more.size());
			return model_more;
		} catch (Sw4jException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private static Logger getLogger() {
		return Logger.getLogger(ToolPml.class);
	}

	private static Model load(String sz_url, Map<String, Model> map_url_model){
		Model m = map_url_model.get(sz_url);
		if (null==m){
			//getLogger().info("load " + sz_url);
			AgentModelLoader loader = new AgentModelLoader(sz_url);
			m=loader.getModelData();
			if (null!=m)
				map_url_model.put(sz_url, m);
		}
		return m;
	}
	
	public static Set<RDFNode> listInfoOutputOfStep(Resource res_step, Model m){
		//try hasOutput first
		Set<RDFNode> ret =  m.listObjectsOfProperty(res_step,PMLR.hasOutput).toSet();
		if (ret.size()>0)
			return ret;

		//then try conventional structure
		ret = new HashSet<RDFNode>();
		Set<Resource> set_ns = m.listSubjectsWithProperty(PMLJ.isConsequentOf,res_step).toSet();
		for (Resource ns: set_ns){
			ret.addAll(m.listObjectsOfProperty(ns,PMLJ.hasConclusion).toSet());
		}
		return ret;
	}
	
	public static Set<RDFNode> listInfoInputOfStep(Resource res_step, Model m){
		//try hasOutput first
		Set<RDFNode> ret =  m.listObjectsOfProperty(res_step,PMLR.hasInput).toSet();
		if (ret.size()>0)
			return ret;

		//then try conventional structure		
		ret = new HashSet<RDFNode>();
		Set<RDFNode> set_list = m.listObjectsOfProperty(res_step, PMLJ.hasAntecedentList).toSet();
		for (RDFNode list: set_list){
			Set<RDFNode> set_ns= m.listObjectsOfProperty((Resource)list, PMLR.hasMember).toSet();
			for (RDFNode ns: set_ns){
				ret.addAll(m.listObjectsOfProperty((Resource)ns,PMLJ.hasConclusion).toSet());
			}
		}
		return ret;
	}
	
	public static Set<Resource> listStepDerivingInfo(Resource res_info, Model m){
		//try hasOutput first
		Set<Resource> ret =  m.listSubjectsWithProperty(PMLR.hasOutput, res_info).toSet();
		if (ret.size()>0)
			return ret;

		//then try conventional structure
		ret = new HashSet<Resource>();
		Set<Resource> set_ns = m.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).toSet();
		for (Resource ns: set_ns){
			for (RDFNode step : m.listObjectsOfProperty(ns,PMLJ.isConsequentOf).toSet()){
				ret.add((Resource)step);
			}
		}
		return ret;
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
			
			set_to_visit.remove(sz_url_temp);
			if (null==model)
				continue;
			
			set_visited.add(sz_url_temp);

			for (Resource type: ary_type){
				for (Resource instance: model.listSubjectsWithProperty(RDF.type, type).toSet()){
					if (!instance.isURIResource())
						continue;
					
					String sz_url_new = DataQname.extractNamespaceUrl(instance.getURI());
					if (ToolSafe.isEmpty(sz_url_new)){
						getLogger().info("instance URI: "+instance.getURI());
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
						getLogger().info("instance URI: "+instance.getURI());
					}else{
						set_to_visit.add(sz_url_new);
					}
				}
			}
			
			
			set_to_visit.removeAll(set_visited);

		}while(set_to_visit.size()>0);

		return map_url_model.get(sz_url_pml);
	}
	
	public static String IWV_NAMESPACE = "http://inference-web.org/vocab#";
	/*
	
	private static int ggid = 0;
	public static Resource create_resource(Resource type, Model m){
		
		String sz_id = "_"+ggid;
		ggid++;
		if (PMLP.Information.equals(type))
			sz_id= "info"+sz_id;
		else if (PMLJ.NodeSet.equals(type))
			sz_id= "ns"+sz_id;
		else if (PMLJ.InferenceStep.equals(type))
			sz_id= "is"+sz_id;
		else if (PMLJ.NodeSetList.equals(type))
			return m.createResource().addProperty(RDF.type, type);
		else 
			sz_id="other" +sz_id;
		
		return m.createResource(IWV_NAMESPACE+sz_id).addProperty(RDF.type, type);
		
		//return m.createResource().addProperty(RDF.type, type);
	}*/
	
	public static void pml_save_data(Model model, File f_output, String sz_namespace, Map<String,String> map_relative_url){
		String sz_content= ToolJena.printModelToString(model, RDFSYNTAX.RDFXML, sz_namespace);
		if (null!= map_relative_url)
			for (String pattern :map_relative_url.keySet()){
				sz_content = sz_content.replaceAll(pattern, map_relative_url.get(pattern));
			}

		getLogger().info(ToolString.formatXMLDateTime(System.currentTimeMillis()));
		ToolIO.pipeStringToFile(sz_content, f_output);
	}
	
	public static void pml_create_by_copy(Set<Resource>[] ary_set_res_step, Model model_ref, DataObjectGroupMap<Resource> map_info_id, Resource res_info_root, File f_output){
		Model model_data= ToolPml.pml_create_by_copy(ary_set_res_step, model_ref, map_info_id,res_info_root);
		ToolPml.pml_save_data(model_data, f_output, ToolPml.IWV_NAMESPACE, null);
	}
	
	public static Model pml_create_by_copy(Set<Resource>[] ary_set_res_step, Model model_ref, DataObjectGroupMap<Resource> map_info_id, Resource res_info_root){
		Model model_data = ModelFactory.createDefaultModel();
		
		HashMap<RDFNode,Resource> map_res_res= new HashMap<RDFNode,Resource>();
		HashMap<Integer,Resource> map_gid_info = new  HashMap<Integer,Resource>();
		HashMap<Integer,Resource> map_gid_ns = new  HashMap<Integer,Resource>();

		//process root
		Resource res_ns_root =model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info_root).next();
		int gid_root = map_info_id.getGid(res_info_root);
		Resource res_ns_root_mapped = model_data.createResource("#answer");
		{
			map_gid_info.put(gid_root, res_info_root);
			if (res_info_root.isAnon()){
				map_res_res.put(res_info_root, model_data.createResource("#info_"+gid_root));
				
				//copy info data
				ToolJena.update_copyResourceDescription(model_data, model_ref, res_info_root, null,true);
			}
			
			map_gid_ns.put(gid_root, res_ns_root);
			map_res_res.put(res_ns_root, res_ns_root_mapped);

			//copy ns data
			ToolJena.update_copyResourceDescription(model_data, model_ref, res_ns_root, null,false);
			ToolJena.update_copyResourceDescription(model_data, model_ref, res_ns_root, PMLJ.hasVariableMapping, true);
			
			getLogger().info("root gid:" +gid_root);
		}
		/*
		for(Set<Resource> set_step: ary_set_res_step){
			for (Resource res_info: ToolPml.list_info(model_ref, set_step)){
				int gid = map_info_id.getGid(res_info);
				Resource res_info_mapped = map_gid_info.get(gid);
				if (null==res_info_mapped){
					res_info_mapped = res_info;
					map_gid_info.put(gid, res_info);
				}
				map_info_info.put(res_info, res_info_mapped);
				
				Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
				Resource res_ns_mapped = map_gid_ns.get(gid);
				if (null==res_ns_mapped){
					res_ns_mapped = res_ns;
					map_gid_ns.put(gid, res_ns);
				}
				map_ns_ns.put(res_ns, res_ns_mapped);
			}
		}
		*/
		//map_gid_info.clear();
	//	map_gid_ns.clear();
			
		//copy data
		for(Set<Resource> set_res_step: ary_set_res_step){
			for (Resource res_step: set_res_step){
				//skip input=output
				
				//copy step data
				ToolJena.update_copyResourceDescription(model_data, model_ref, res_step, null, false);
				model_data.add(ToolJena.create_copyList(model_ref, ToolJena.getValueOfProperty(model_ref, res_step, PMLJ.hasAntecedentList, (Resource)null), PMLDS.first, PMLDS.rest));
//				ToolJena.update_copyResourceDescription(model_data, model_ref, res_step, PMLJ.hasAntecedentList, true);
				
				ToolJena.update_copyResourceDescription(model_data, model_ref, ToolJena.getValueOfProperty(model_ref, res_step, PMLJ.hasSourceUsage, (Resource)null),null, false);

				map_res_res.put(res_step, model_data.createResource());

				
				//copy info
				for (Resource res_info: ToolPml.listInfoOfStep(model_ref, res_step, OPT_LIST_ALL)){
					int gid = map_info_id.getGid(res_info);
					if (res_info.isAnon()){
						Resource res_info_mapped = map_gid_info.get(gid);
						if (null==res_info_mapped){
							res_info_mapped = res_info;
							map_gid_info.put(gid, res_info);
							
							//copy info data
							ToolJena.update_copyResourceDescription(model_data, model_ref, res_info_mapped, null,true);
						}
						map_res_res.put(res_info, model_data.createResource("#info_"+gid));
					}
					
					Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
					Resource res_ns_mapped = map_gid_ns.get(gid);
					if (null==res_ns_mapped){
						res_ns_mapped = res_ns;
						map_gid_ns.put(gid, res_ns_mapped);
						
						//copy ns data
						ToolJena.update_copyResourceDescription(model_data, model_ref, res_ns_mapped, null,false);
						ToolJena.update_copyResourceDescription(model_data, model_ref, res_ns_mapped, PMLJ.hasVariableMapping, true);
					}
					
					if (gid==gid_root)
						map_res_res.put(res_ns, res_ns_root_mapped);
					else
						map_res_res.put(res_ns, model_data.createResource("#ns_"+gid));
					
				}
			}
		}
		
		
		//filter
		model_data =  ToolJena.create_filter(model_data, new Property[]{
//				PMLR.dependsOn, PMLR.dependsOnDirect, 
				PMLJ.hasIndex, PMLJ.fromAnswer, PMLJ.fromQuery, PMLJ.isConsequentOf});
		

		
		//TODO: add isconseuquentof index, fromanser
		HashMap<Resource, Integer> map_ns_index = new HashMap<Resource, Integer>();
		for (Resource res_step: ToolPml.listStep(model_data)){
			for (RDFNode node_info: ToolPml.listInfoInputOfStep(res_step, model_data)){
				Resource res_info =(Resource) node_info;
				
				Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
				
				model_data.add(res_ns, PMLJ.isConsequentOf, res_step);
				if (ary_set_res_step[0].contains(res_step)){
					res_step.addLiteral(PMLJ.hasIndex,0);
					map_ns_index.put(res_ns, 1);
				}
			}
		}
		
		for (Resource res_step: ToolPml.listStep(model_data)){
			for (RDFNode node_info: ToolPml.listInfoOutputOfStep(res_step, model_data)){
				Resource res_info =(Resource) node_info;
				
				Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
				if (ary_set_res_step[0].contains(res_step)){
					
				}else{
					Integer id = map_ns_index.get(res_ns);
					if (null==id)
						id=0;
					res_step.addLiteral(PMLJ.hasIndex,id);
					id++;
					map_ns_index.put(res_ns, id);
				}
			}
		}
		
		
/*		for(Statement stmt: model_data.listStatements(null, PMLR.hasOutput, (String)null).toSet()){
			Resource res_step =stmt.getSubject();
			Resource res_info =(Resource) stmt.getObject();

			Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
			
			model_data.add(res_ns, PMLJ.isConsequentOf, res_step);
			if (ary_set_res_step[0].contains(res_step)){
				res_step.addLiteral(PMLJ.hasIndex,0);
				map_ns_index.put(res_ns, 1);
			}
		}
		
		for(Statement stmt: model_data.listStatements(null, PMLR.hasOutput, (String)null).toSet()){
			Resource res_step =stmt.getSubject();
			Resource res_info =(Resource) stmt.getObject();
						
			Resource res_ns = model_ref.listSubjectsWithProperty(PMLJ.hasConclusion, res_info).next();
			if (ary_set_res_step[0].contains(res_step)){
				
			}else{
				Integer id = map_ns_index.get(res_ns);
				if (null==id)
					id=0;
				res_step.addLiteral(PMLJ.hasIndex,id);
				id++;
				map_ns_index.put(res_ns, id);
			}
		}
	*/	
		//rename resources
		model_data = ToolJena.create_rename(model_data, map_res_res);

	//	model_data =  ToolJena.model_filter(model_data, new Property[]{ PMLJ.hasIndex});


		//model_data = ToolJena.model_unsignBlankNode(model_data, PMLJ.InferenceStep);
		//model_data = ToolJena.model_unsignBlankNode(model_data, PMLJ.NodeSet);
		//model_data = ToolJena.model_unsignBlankNode(model_data, PMLJ.NodeSetList);
		ToolJena.update_copyNsPrefix(model_data, model_ref);
		return model_data;
	}
/*	
	public static void copy_step(Model model_data, Resource res_step, Model model_ref,  Map<Resource,Resource> map_res_res){
		//copy description
		//copy inconsequentof
		//	copy description
		//  copy hasconclusion
		//	  copy description
		//    copy hasdescription
		//copy hasantecedentlist
		
		
		Resource res_step_output= create_resource(PMLJ.InferenceStep, model_data);
		copy_resource_description(model_data, res_step_output, model_ref, res_step, PMLJ.hasInferenceEngine);
		copy_resource_description(model_data, res_step_output, model_ref, res_step, PMLJ.hasInferenceRule);
		
		Resource res_info_conclusion = (Resource) model_ref.listObjectsOfProperty(res_step, PMLR.hasOutput).next();
		res_info_conclusion = map_info_info.get(res_info_conclusion);
		Resource res_ns_conclusion = map_info_ns.get(res_info_conclusion);
		if (null==res_ns_conclusion){
			res_ns_conclusion=create_resource(PMLJ.NodeSet, model_data);
			map_info_ns.put(res_info_conclusion, res_ns_conclusion);
		}
		res_ns_conclusion.addProperty(PMLJ.hasConclusion, res_info_conclusion);
		model_data.add(model_ref.listStatements(res_info_conclusion,null,(String)null));

		Set<Resource> set_ns_antecedent = new HashSet<Resource>();
		for (RDFNode node: model_ref.listObjectsOfProperty(res_step, PMLR.hasOutput).toSet()){
			Resource res_info = (Resource) node;
			res_info = map_info_info.get(res_info);
			Resource res_ns = map_info_ns.get(res_info);
			if (null==res_ns){
				res_ns=create_resource(PMLJ.NodeSet, model_data);
				map_info_ns.put(res_info, res_ns);
			}
			res_ns.addProperty(PMLJ.hasConclusion, res_info);
			model_data.add(model_ref.listStatements(res_info,null,(String)null));
			set_ns_antecedent.add(res_ns);
		}
		
		create_step(model_data,res_step_output, res_ns_conclusion,set_ns_antecedent );
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
*/	

	


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
		m = ToolJena.create_deduction(m);
		getLogger().info("deduction ontology size: "+m.size());
		
		return m;
	}




	public static Model create_mappings(Model m){
		HashSet<Model> set_model = new HashSet<Model>();
		set_model.add(m);
		return create_mappings(m);
	}

	public static DataPVHMap<String,Resource> create_mappings(Collection<Model> models){
		DataPVHMap<String,Resource> map_norm_info = new DataPVHMap<String,Resource>();
		for (Model model: models){
			//we should only consider Information as conclusion of nodeset, other instances of information should be skipped
			//for (Resource res_info: model.listSubjectsWithProperty(RDF.type, PMLP.Information).toSet()){
			for (RDFNode info: listInfoOutput(model)){
				Resource res_info =(Resource) info;
				DataPmlInfo dpi = new DataPmlInfo(res_info, model);
				map_norm_info.add(dpi.getNormalizedString(), res_info);
			}
		}
		
		return map_norm_info;
		
	}
	
	public static Set<RDFNode> listInfoOutput(Model m){
		return m.listObjectsOfProperty(PMLJ.hasConclusion).toSet();
	}

	public static Set<Resource> listStep(Model m) {
		return m.listSubjectsWithProperty(RDF.type,PMLJ.InferenceStep).toSet();
	}
}
