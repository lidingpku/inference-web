package org.inference_web.pml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.inference_web.util.DataColorMap;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.app.pml.PMLR;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataPmlHg {
	public Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	HashMap<String,Model> m_context_model_data = new HashMap<String,Model>();;
	DataObjectGroupMap<Resource> m_map_res_vertex= new DataObjectGroupMap<Resource>();

	public void add_data(String sz_url_pml, Model m){
		if (ToolSafe.isEmpty(sz_url_pml))
			return;

		if (null!=m){
			m_context_model_data.put(sz_url_pml, m);
		}
		
		ToolPml.pml_load(sz_url_pml, m_context_model_data);
		clear_cache();
	}
	

	public void add_mapping(Model model_mapping){
		if (ToolSafe.isEmpty(model_mapping))
			return;
		
		// list all owl:sameAs relation
		for (Statement stmt: model_mapping.listStatements(null, OWL.sameAs, (Resource)null).toSet()){
			m_map_res_vertex.addSameObjectAs(stmt.getSubject(), ((Resource)(stmt.getObject())));
		}
		clear_cache();
	}
	
	private void clear_cache() {
		m_model_all = null;
		m_dhg = null;
		m_map_step_edge= new HashMap <Resource,DataHyperEdge>();
		m_map_edge_step= new DataPVHMap <DataHyperEdge,Resource>();	
	}

	
	//cached data
	protected Model m_model_all = null;
	boolean m_b_model_all_index =false;
	DataHyperGraph m_dhg = null;
	HashMap <Resource,DataHyperEdge> m_map_step_edge= new HashMap <Resource,DataHyperEdge>();
	DataPVHMap <DataHyperEdge,Resource> m_map_edge_step= new DataPVHMap <DataHyperEdge,Resource>();
	
	public Model getModelAll(boolean bNeedIndex) {
		//merge all models
		if (null==m_model_all){
			m_model_all = ToolJena.create_copy(this.m_context_model_data.values());

			//update vertex group
			for(Resource info: m_model_all.listSubjectsWithProperty(RDF.type,PMLP.Information).toSet())
				m_map_res_vertex.addObject(info);
			
			m_map_res_vertex.normalize();
		}
		
		//add index data
		if (bNeedIndex){
			if (!m_b_model_all_index)
				ToolPml.pml_update_index(m_model_all);
			m_b_model_all_index = true;
		}
			
		return m_model_all;
	}

	DataPVHMap<Resource, String> m_map_step_contex = new DataPVHMap<Resource, String>();
	/**
	 * list all context related to a step
	 * @param step
	 * @return
	 */
	public Set<String> getContext(Resource step){
		if (m_map_step_contex.keySet().contains(step))
			return m_map_step_contex.getValuesAsSet(step);
		else{
			HashSet<String> ret = new HashSet<String>();
			for (String context: this.m_context_model_data.keySet()){
				Model m = this.m_context_model_data.get(context);
				if (m.listSubjects().toSet().contains(step))
					ret.add(context);
			}
			m_map_step_contex.add(step, ret);
			return ret;
		}
	}
	
	public DataHyperGraph getHyperGraph(){
		//check cache
		if (null!=m_dhg)
			return m_dhg;
		
		getModelAll(false);
		
		for (Resource info: getModelAll(false).listSubjectsWithProperty(RDF.type, PMLP.Information).toSet()){
			m_map_res_vertex.addObject(info);
		}
		
		m_map_res_vertex.normalize();
		
		m_dhg = new DataHyperGraph();
		for (Resource res_step: getModelAll(false).listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep).toSet()){
			DataHyperEdge edge = createHyperEdge(getModelAll(true), res_step, m_map_res_vertex);
			m_dhg.add(edge, getContext(res_step));
			m_map_step_edge.put(res_step, edge);
			m_map_edge_step.add(edge, res_step);
		}
		return m_dhg;
	}

	public DataHyperGraph getHyperGraph(Set<Resource> set_res_step){
		getHyperGraph();
		
		DataHyperGraph dhg = new DataHyperGraph();
		for (Resource res_step: set_res_step){
			dhg.add(this.getHyperEdge(res_step), getContext(res_step));
		}
		return dhg;
	}
	
	public Resource getRoot(String sz_url_pml){
		
		Model model_data = this.m_context_model_data.get(sz_url_pml);
		
		if (ToolSafe.isEmpty(model_data)||ToolSafe.isEmpty(sz_url_pml))
			return null;
		
		//assume a context only have one root
		Set<Resource> roots = ToolPml.list_roots(model_data);
		if (roots.size()!=1)
			return null;
		
		Resource res_root =  roots.iterator().next();
		return res_root;
	}
	
	/**
	 * get a subgraph rooted at res_root;
	 * 
	 * @param res_root
	 * @return
	 */
	public Set<Resource> getSubHg(Resource res_info_root){
		return ToolPml.list_depending_steps(getModelAll(true), res_info_root);
	}
	
	
	public Set<Resource> getSubHg(String sz_url_pml){
		if (ToolSafe.isEmpty(sz_url_pml))
			return new HashSet<Resource>();

		Model model_data = this.m_context_model_data.get(sz_url_pml);
		
		if (ToolSafe.isEmpty(model_data))
			return new HashSet<Resource>();
		
		return model_data.listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep).toSet();		
	}

	public Set<Resource> getSubHg(){
		return getModelAll(false).listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep).toSet();		
	}
	
	public Set<Resource> getSubHgKeepRoot(String sz_url_pml_chosen, int id_root) {
		Set<Resource> ret = getSubHg();
		for (String sz_url_pml: this.m_context_model_data.keySet()){
			Model m = m_context_model_data.get(sz_url_pml);
			for (Resource res_step: m.listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep).toSet()){
				DataHyperEdge edge = this.m_map_step_edge.get(res_step);
				if (!sz_url_pml.equals(sz_url_pml_chosen)){
					if (edge.getOutput()==id_root){
						ret.remove(res_step);
					}
				}
			}
		}
		return ret;
	}

	public Set<Resource> getSubHg( DataHyperGraph dhg, Resource res_root, String sz_url_pml ){
		getHyperGraph();

		Set<Resource> set_res_step_dependant = ToolPml.list_depending_steps(getModelAll(true), res_root);

		Set<Resource> ret = new HashSet<Resource>();
		//try to reuse PML resources from the supplied context
		for(DataHyperEdge edge: dhg.getEdges()){
			Resource res_edge_chosen = null;
			for(Resource res_edge: this.m_map_edge_step.getValuesAsSet(edge)){
				if (null==res_edge_chosen)
					res_edge_chosen=res_edge;
				
				boolean bHasRoot = (null!=res_root)&& getModelAll(true).listStatements(res_edge, PMLR.hasOutput, res_root).hasNext(); 
				boolean bInContext = (null!=set_res_step_dependant)&&set_res_step_dependant.contains(res_edge);
				
				if (bInContext || bHasRoot){
					res_edge_chosen=res_edge;
					if (bInContext && bHasRoot){
						break;
					}
				}				
			}
			
			//must not be null here
			ret.add(res_edge_chosen);
		}
		
		return ret;
	}

	private static DataHyperEdge createHyperEdge(Model model_data, Resource res_step, DataObjectGroupMap<Resource> map_res_gid){
		//list output
		Resource res_output = (ToolJena.getValueOfProperty(model_data, res_step, PMLR.hasOutput, (Resource)null));
		Integer  id_output = map_res_gid.addObject(res_output);
		
		//list inputs
		Set<RDFNode> set_res_input = model_data.listObjectsOfProperty(res_step, PMLR.hasInput).toSet();  
		Set<Integer> set_id_input = new  HashSet<Integer>();
		for (RDFNode node_input: set_res_input){
			Resource res_input = (Resource) node_input;
			set_id_input.add( map_res_gid.addObject(res_input));
		}

		//get antecedents
		return new DataHyperEdge(id_output, set_id_input);
	}
	
	private static String graphviz_print_node(String id, Properties prop){
		String params="";
		for (Object key :prop.keySet()){
			params += String.format("%s=\"%s\" ", key, prop.get(key));
		}
		
		String ret = String.format(" \"%s\" [ %s ];\n ", id, params);
		return ret;
	}
	
	private static String graphviz_print_arc(String label_from, String label_to){
		return String.format(" \"%s\" -> \"%s\";\n ", label_from, label_to  );
	}
	
	private static String graphviz_get_id(Integer node){
		return "x_"+node;
	}

	private static String graphviz_get_id(Resource res_edge){
		return ToolJena.getNodeString(res_edge);
	}
	
	public String graphviz_export_dot ( Set<Resource> set_res_edge ){
		getHyperGraph();

		Set<Resource> set_res_node = ToolPml.list_info(getModelAll(true), set_res_edge, ToolPml.OPT_LIST_ALL);
		DataHyperGraph dhg = this.getHyperGraph(set_res_edge);

		String ret ="";
		//add maps for nodes
		for (Resource res_node: set_res_node){
			Integer gid=this.getHyperNode(res_node);
			Properties prop =  graphviz_get_params_node(res_node, gid, dhg);
			String label_node = graphviz_get_id(gid);
			ret += graphviz_print_node(label_node, prop);	
		}
		
		//add maps for edges
		for (Resource res_edge: set_res_edge){
			DataHyperEdge edge = this.getHyperEdge(res_edge);
			Properties prop =  graphviz_get_params_edge(res_edge, edge);
			String label_edge = graphviz_get_id(res_edge);

			ret += graphviz_print_node(label_edge, prop);

			Integer output= edge.getOutput();
			String label_node = graphviz_get_id(output);
			//changed
			ret += graphviz_print_arc( label_edge, label_node );
			for(Integer input : edge.getInputs()){
				label_node = graphviz_get_id(input);
				ret += graphviz_print_arc(  label_node, label_edge );
			}
		}
		
		String label=this.stat(set_res_edge).toString();

		
		ret = String.format("digraph g \n{ labelloc=b label=\"%s\"  \n%s }\n",label, ret);
		return ret;
	}

	public String graphviz_export_dot_diff ( Set<Resource> set_res_edge_optimal, Set<Resource> set_res_edge_original ){
		getHyperGraph();

		String ret_background ="";

		//background
		{
			Set<Resource> set_res_edge = new HashSet<Resource>();
			set_res_edge.addAll(set_res_edge_optimal);
			set_res_edge.addAll(set_res_edge_original);

			Set<Resource> set_res_node = ToolPml.list_info(getModelAll(true), set_res_edge, ToolPml.OPT_LIST_ALL);
			DataHyperGraph dhg = this.getHyperGraph(set_res_edge);
			DataHyperGraph dhg_original = this.getHyperGraph(set_res_edge_original);
			Set<Integer> set_vertex_original = dhg_original.getVertices();

			//add maps for nodes
			for (Resource res_node: set_res_node){
				Integer gid=this.getHyperNode(res_node);
				Properties prop =  graphviz_get_params_node(res_node, gid, dhg);
				
				if (!set_vertex_original.contains(gid))
					prop.put("fontcolor", "green");
				
				String label_node = graphviz_get_id(gid);
				ret_background += graphviz_print_node(label_node, prop);	
			}
			
			//add maps for edges
			for (Resource res_edge: set_res_edge){
				DataHyperEdge edge = this.getHyperEdge(res_edge);
				Properties prop =  graphviz_get_params_edge(res_edge, edge);
				String label_edge = graphviz_get_id(res_edge);

				ret_background += graphviz_print_node(label_edge, prop);

				Integer output= edge.getOutput();
				String label_node = graphviz_get_id(output);
				// changed
				ret_background += graphviz_print_arc( label_edge, label_node );
				for(Integer input : edge.getInputs()){
					label_node = graphviz_get_id(input);
					ret_background += graphviz_print_arc( label_node, label_edge );
				}
			}
		}

		String ret_optimal ="";
		DataHyperGraph dhg_optimal = this.getHyperGraph(set_res_edge_optimal);
		Set<Integer> set_vertex_optimal = dhg_optimal.getVertices();

		{
			Set<Resource> set_res_edge = set_res_edge_optimal;

			//add maps for nodes
			for (Integer gid: set_vertex_optimal){;
				String label_node = graphviz_get_id(gid);
				ret_optimal += String.format(" %s ;\n",label_node);	
			}
			
			//add maps for edges
			for (Resource res_edge: set_res_edge){
				String label_edge = graphviz_get_id(res_edge);

				ret_optimal += String.format(" \"%s\" ;\n",label_edge);	
			}
			String label=this.stat(set_res_edge_optimal, set_res_edge_original).toString();

			//insert change line
			int pos = label.indexOf(",",label.length()/2)+1;
			label=label.substring(0,pos)+"\\n"+label.substring(pos);
			
			ret_optimal = String.format("subgraph cluster_opt \n{ labelloc=b label=\"%s\" \n fontsize=30 fillcolor=cornsilk style=filled \n %s \n}\n", label, ret_optimal);
		}
		
		//optimal solution
		
		String ret = String.format("digraph g \n{  \n %s \n %s \n}\n",ret_background, ret_optimal);
		return ret;
	}

	protected Properties graphviz_get_params_edge(Resource res_edge, DataHyperEdge edge){ 
		getHyperGraph();

		Properties prop = new Properties();

		//set shape
		if (edge.isLeaf()){
			prop.put("shape", "invhouse");				
		}else{
			prop.put("shape", "diamond");
		}

		prop.put("penwidth", "5");

		//add link
		if (res_edge.isURIResource()){
			prop.put("URL", res_edge.getURI());
		}else{
			//link to iw browser?
			Resource nodeset= getModelAll(false).listSubjectsWithProperty(PMLJ.isConsequentOf,res_edge).next();
			String sz_url;
			try {
				sz_url = String.format("%s?url=%s","http://browser.inference-web.org/iwbrowser/BrowseNodeSet",ToolURI.encodeURIString(nodeset.getURI()));
				prop.put("URL", sz_url);	
			} catch (Sw4jException e) {
				e.printStackTrace();
			}

		}
	
		// set fill color
		prop.put("style", "filled");
		prop.put("fillcolor", "white");							
		
		//set border color
		Resource res_engine = (ToolJena.getValueOfProperty(getModelAll(false), res_edge, PMLJ.hasInferenceEngine, (Resource)null));
		if (!ToolSafe.isEmpty(res_engine)){
			String color = graphviz_get_engine_color(res_engine);
			prop.put("color", color);					
		}
		
		// set label
		Resource res_rule= (ToolJena.getValueOfProperty(getModelAll(false), res_edge, PMLJ.hasInferenceRule, (Resource)null));
		if (!ToolSafe.isEmpty(res_rule))
			prop.put("label", res_rule.getLocalName());	
		else
			prop.put("label", edge.toString());
		
		return prop;
	}



	public DataHyperEdge getHyperEdge(Resource resEdge) {
		getHyperGraph();
		return this.m_map_step_edge.get(resEdge);
	}

	public Integer getHyperNode(Resource resNode) {
		getHyperGraph();
		return this.m_map_res_vertex.getGid(resNode);
	}
	
	protected Properties graphviz_get_params_node(Resource res_node, Integer gid, DataHyperGraph dhg){
		getHyperGraph();

		Properties prop = new Properties();
		//set shape
		prop.put("shape", "box");
		
		
		if (null==dhg||!dhg.getVertices().contains(gid))
			return prop;

		
		int antecedents = 0;
		for (DataHyperEdge edge: dhg.getEdgesByOutput(gid)){
			antecedents+=dhg.getContextsByEdge(edge).size();
		}
		prop.put("style", "filled");
		if (antecedents>1){
			prop.put("fillcolor", "red");
		}else{
			prop.put("fillcolor", "white");			
		}
	
		
		//add border color
		Resource res_lang = ToolJena.getValueOfProperty(getModelAll(false), res_node, PMLP.hasLanguage, (Resource)null);
		prop.put("color", graphviz_get_languge_color(res_lang));
		
		//add link
		//possibly, we can use the nodeset uri here
		if (res_node.isURIResource())
			prop.put("URL", res_node.getURI());
		else{
		}
		
		//add label
// TODO
 		String sz_label = ToolJena.getValueOfProperty(getModelAll(false), res_node, PMLP.hasRawString, (String)null);
		if (!ToolSafe.isEmpty(sz_label))
			prop.put("label", sz_label.replaceAll("\n", " "));
		else
			System.out.println("no label");
	
		
		return prop;
	}


	protected DataColorMap m_map_color_lang= new DataColorMap();
	protected String graphviz_get_languge_color(Resource resLang) {
		return m_map_color_lang.getColor( (null!=resLang)?resLang.getURI():"n/a");
	}


	protected DataColorMap m_map_color_engine= new DataColorMap();
	protected String graphviz_get_engine_color(Resource resEngine) {
		return m_map_color_engine.getColor( (null!=resEngine)?resEngine.getURI():"n/a");
	}

	/*
	public static void export_dot2(DataHyperGraph dhg, DataHyperGraph dhg_all, DataPmlHg hg, String file_output, String sz_context){
		String title= String.format("The optimal proof (%d nodes) reduced %d nodes from original proof at %s",
				 dhg.getEdges().size(),
				 dhg_all.getEdges().size()-dhg.getEdges().size(),
				 sz_context);
		System.out.println(title);
	
		String subgraph="";
		Set<Integer> nodes = dhg.getNodes(); 
		for (Integer node: nodes){
			subgraph += "\"x_"+node+"\";\n";
		}
		for (DataHyperEdge edge: dhg_all.getEdges()){
			if (dhg.getEdges().contains(edge)){
			//if (nodes.containsAll(edge.getInputs()) && nodes.contains(edge.getOutput())){
				subgraph += "\""+edge.getID()+"\";\n";		
			}
		}
		subgraph = "subgraph cluster_opt { label= \""+title+"\"\n"+subgraph+"}\n";
			
		String sz_content = DataHyperGraph.data_export_graphviz(dhg_all,null, hg.getMapNodeParams(dhg_all),hg.getMapEdgeParams(sz_context), subgraph);
		
		save_graph(sz_content, file_output);
	}
	*/
	public static void graphviz_save(String sz_content, String sz_file_output_common){
		if (ToolSafe.isEmpty(sz_file_output_common)){
			System.out.println(sz_content);
		}else{
			try {
				File f_dot = new File(sz_file_output_common+".dot");
				
				ToolIO.pipeStringToFile(sz_content, f_dot.getAbsolutePath(), false);
	
				String [] formats = new String[]{"svg","png"};
				for (String format :formats){
					File f_format = new File(sz_file_output_common+"."+format);
					String command = "dot  -T"+format+" -o"+ f_format.getAbsolutePath() +" "+ f_dot.getAbsolutePath() ;
					System.out.println("run command: "+command);
					Runtime.getRuntime().exec(command);
				}
			} catch (Sw4jException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}


	public Model getModelOriginal(String sz_url_pml) {
		return this.m_context_model_data.get(sz_url_pml);
	}

	


	public Set<String> getContext() {
		return m_context_model_data.keySet();
	}
	
	public final static int OPTION_WEIGHT_STEP =0;
	public void hg_set_weight(DataHyperGraph dhg, int option){
		switch (option){
		case OPTION_WEIGHT_STEP:
		default:
			for (DataHyperEdge edge: dhg.getEdges()){
				edge.setWeight(1);
			}
		}
		
	}
	
	public DataObjectGroupMap<Resource> getInfoMap() {
		return this.m_map_res_vertex;
	}

	public Set<Resource> copy_without_loop(Set<Resource> set_step){
		HashSet<Resource> ret = new HashSet<Resource>();
		for (Resource res_step: set_step){
			DataHyperEdge edge = this.m_map_step_edge.get(res_step);
			if (!edge.hasLoop()){
				ret.add(res_step);
			}
		}
		return ret;
	}

	public Collection<Model> getModels() {
		return this.m_context_model_data.values();
	}

	
	public static String STAT_WEIGHT = "weight";
	public static String STAT_WEIGHT_SAVING = "weight[saving]";
	
	public static String STAT_STEP = "step";
	public static String STAT_STEP_NEW = "step[new]";
	public static String STAT_STEP_SAVING = "step[saving]";


	public static String STAT_STEP_AXIOM = "step(axiom)";
	public static String STAT_STEP_AXIOM_SAVING = "step(axiom)[saving]";

	public static String STAT_STEP_LEAF = "step(leaf)";
	public static String STAT_STEP_LEAF_SAVING = "step(leaf)[saving]";

	public static String STAT_FORMULA = "formula";
	public static String STAT_FORMULA_NEW = "formula[new]";

	public static String STAT_FORMULA_AXIOM = "formula(axiom)";
	public static String STAT_FORMULA_AXIOM_SAVING = "formula(axiom)[saving]";

	public static String STAT_FORMULA_LEAF = "formula(leaf)";
	public static String STAT_FORMULA_LEAF_SAVING = "formula(leaf)[saving]";
	
	public static String STAT_FORMULA_UNION = "formula[union]";
	public static String STAT_FORMULA_INTERSECTION = "formula[intersection]";
	
	public static String STAT_FORMULA_AXIOM_UNION = "formula(axiom)[union]";
	public static String STAT_FORMULA_AXIOM_INTERSECTION = "formula(axiom)[intersection]";
	public static String STAT_FORMULA_LEAF_UNION = "formula(leaf)[union]";
	public static String STAT_FORMULA_LEAF_INTERSECTION = "formula(leaf)[intersection]";
	
	public static String STAT_RULE= "rule";
	public static String STAT_RULE_NEW = "rule[new]";

	public DataSmartMap stat(Set<Resource> set_step_improved, Set<Resource> set_step_original){
		DataSmartMap data= new DataSmartMap();
		DataHyperGraph g_improved = this.getHyperGraph(set_step_improved);
		DataHyperGraph g_original = this.getHyperGraph(set_step_original);
		
		DataSmartMap data1= stat(set_step_improved);
		data.copy(data1);

		Set<Integer> set_formular_new = g_improved.getVertices();
		set_formular_new.removeAll(g_original.getVertices());
		data.put(STAT_FORMULA_NEW, set_formular_new.size());
		
		data.put(STAT_STEP_SAVING, set_step_original.size() -  set_step_improved.size());

		Set<DataHyperEdge> set_step_new = g_improved.getEdges();
		set_step_new.removeAll(g_original.getEdges());
		data.put(STAT_STEP_NEW, set_step_new.size());

		Set<RDFNode> set_res_rule_improved = new HashSet<RDFNode>();
		Set<RDFNode> set_res_rule_original = new HashSet<RDFNode>();
		for (Statement stmt: getModelAll(false).listStatements(null, PMLJ.hasInferenceRule, (String)null).toSet()){
			if (set_step_improved.contains(stmt.getSubject()))
				set_res_rule_improved.add(stmt.getObject());

			if (set_step_original.contains(stmt.getSubject()))
				set_res_rule_original.add(stmt.getObject());
		}

		set_step_improved.removeAll(set_step_original);
		data.put(STAT_RULE_NEW, set_step_improved.size());

		
		return data;
	}

	public DataSmartMap stat_diff(Set<Resource> set_step_improved, Set<Resource> set_step_original){
		DataSmartMap data= new DataSmartMap();
		DataHyperGraph g_improved = this.getHyperGraph(set_step_improved);
		DataHyperGraph g_original = this.getHyperGraph(set_step_original);
		
		DataSmartMap data1= stat(set_step_improved, set_step_original);
		data.copy(data1);

		Set<Integer> set_formula_union = g_improved.getVertices();
		set_formula_union.addAll(g_original.getVertices());
		data.put(STAT_FORMULA_UNION, set_formula_union.size());

		Set<Integer> set_formula_intersetion= g_improved.getVertices();
		set_formula_intersetion.retainAll(g_original.getVertices());
		data.put(STAT_FORMULA_INTERSECTION, set_formula_intersetion.size());

		Set<Integer> leaf_vertices_improved  = new HashSet<Integer>();
		Set<DataHyperEdge> leaf_edges_improved = new HashSet<DataHyperEdge>();
		Set<Integer> axiom_vertices_improved  = new HashSet<Integer>();
		Set<DataHyperEdge> axiom_edges_improved = new HashSet<DataHyperEdge>();
		
		count_leaf(set_step_improved, leaf_vertices_improved,leaf_edges_improved,axiom_vertices_improved,axiom_edges_improved);

		Set<Integer> leaf_vertices_original  = new HashSet<Integer>();
		Set<DataHyperEdge> leaf_edges_original = new HashSet<DataHyperEdge>();
		Set<Integer> axiom_vertices_original  = new HashSet<Integer>();
		Set<DataHyperEdge> axiom_edges_original = new HashSet<DataHyperEdge>();
		
		count_leaf(set_step_original, leaf_vertices_original,leaf_edges_original,axiom_vertices_original,axiom_edges_original);
		
		{
			Set<Integer> set_formula_axiom_union = new  HashSet<Integer>();
			set_formula_axiom_union.addAll(axiom_vertices_improved);
			set_formula_axiom_union.addAll(axiom_vertices_original);
			data.put(STAT_FORMULA_AXIOM_UNION, set_formula_axiom_union.size());
	
			Set<Integer> set_formula_axiom_intersetion=  new  HashSet<Integer>();
			set_formula_axiom_intersetion.addAll(axiom_vertices_improved);
			set_formula_axiom_intersetion.retainAll(axiom_vertices_original);
			data.put(STAT_FORMULA_AXIOM_INTERSECTION, set_formula_axiom_intersetion.size());
		}
		{
			Set<Integer> set_formula_leaf_union = new  HashSet<Integer>();
			set_formula_leaf_union.addAll(leaf_vertices_improved);
			set_formula_leaf_union.addAll(leaf_vertices_original);
			data.put(STAT_FORMULA_LEAF_UNION, set_formula_leaf_union.size());
	
			Set<Integer> set_formula_leaf_intersetion=  new  HashSet<Integer>();
			set_formula_leaf_intersetion.addAll(leaf_vertices_improved);
			set_formula_leaf_intersetion.retainAll(leaf_vertices_original);
			data.put(STAT_FORMULA_LEAF_INTERSECTION, set_formula_leaf_intersetion.size());
		}
		
		return data;
	}

	
	public DataSmartMap stat(Set<Resource> set_step){
		DataSmartMap data= new DataSmartMap();
		DataHyperGraph dhg = this.getHyperGraph(set_step);

		data.put(STAT_WEIGHT, dhg.getTotalWeight());
		data.put(STAT_STEP, dhg.getEdges().size());
		data.put(STAT_FORMULA, dhg.getVertices().size());
		
		Set<Integer> leaf_vertices  = new HashSet<Integer>();
		Set<DataHyperEdge> leaf_edges = new HashSet<DataHyperEdge>();
		Set<Integer> axiom_vertices  = new HashSet<Integer>();
		Set<DataHyperEdge> axiom_edges = new HashSet<DataHyperEdge>();
		
		count_leaf(set_step, leaf_vertices,leaf_edges,axiom_vertices,axiom_edges);
		
		data.put(STAT_STEP_LEAF, leaf_edges.size());
		data.put(STAT_FORMULA_LEAF, leaf_vertices.size());
		data.put(STAT_STEP_AXIOM, axiom_edges.size());
		data.put(STAT_FORMULA_AXIOM, axiom_vertices.size());
		
		Set<RDFNode> set_res_rule = new HashSet<RDFNode>();
		for (Statement stmt: getModelAll(false).listStatements(null, PMLJ.hasInferenceRule, (String)null).toSet()){

			if (set_step.contains(stmt.getSubject()))
				set_res_rule.add(stmt.getObject());
		}
		data.put(STAT_RULE, set_res_rule.size());
		return data;
	}

	private void count_leaf(	Set<Resource> set_step, 
									Set<Integer> leaf_vertices ,
									Set<DataHyperEdge> leaf_edges,
									Set<Integer> axiom_vertices,
									Set<DataHyperEdge> axiom_edges){
		for (Resource res_step:set_step){
			DataHyperEdge edge = this.getHyperEdge(res_step);
			if (!edge.isLeaf())
				continue;
			
			leaf_edges.add(edge);
			leaf_vertices.add(edge.getOutput());
			
			Model m = res_step.getModel();
			Set<RDFNode> rules = m.listObjectsOfProperty(res_step, PMLJ.hasInferenceRule).toSet();
			if (rules.contains(m.createResource("http://inference-web.org/registry/DPR/Told.owl#Told") )){
				axiom_edges.add(edge);
				axiom_vertices.add(edge.getOutput());
			}
		}
	}
	
	public String stat_all(String problem, boolean bHeader){
		String ret ="";
		
		Set<Resource> set_step = this.getSubHg();
		DataSmartMap data= stat(set_step);
		
		data.put("problem",problem);
		stat_url_param(problem,"p", data);
		
		DataSmartMap data1 = this.m_dhg.get_data_summary(false);
		data.copy(data1);
		
		//stat triples
		int sum= 0;
		for (Model m: this.m_context_model_data.values()){
			sum+=m.size();
		}
		data.put("triples", sum);

		if (bHeader){
			if (ret.length()==0){
				ret += data.toCSVheader();
				ret += "\n";
			}
		}
		ret += data.toCSVrow();
		ret += "\n";
		
		return ret;
	}

	public String stat_one(String problem, boolean bHeader){
		String ret ="";
		for (Map.Entry<String, Model> entry: this.m_context_model_data.entrySet()){
			String sz_context = entry.getKey();
			Set<Resource> set_step = this.getSubHg(sz_context);
			DataSmartMap data= stat(set_step);
			
			data.put("problem",problem);
			stat_url_param(problem,"p", data);

			stat_url_param(sz_context.substring(problem.length()),"s", data);

			data.put("triples", entry.getValue().size());

			if (bHeader){
				if (ret.length()==0){
					ret += data.toCSVheader();
					ret += "\n";
				}
			}
			ret += data.toCSVrow();
			ret += "\n";
		}
		return ret;
	}


	public String stat_diff(String problem){
		String ret ="";
		for (String sz_context1: this.m_context_model_data.keySet()){
			for (String sz_context2: this.m_context_model_data.keySet()){
				if (sz_context1.equals(sz_context2))
					continue;
				DataSmartMap data= stat_diff(this.getSubHg(sz_context1),this.getSubHg(sz_context2));
				
				data.put("problem",problem);
				stat_url_param(problem,"p", data);

				stat_url_param(sz_context1.substring(problem.length()),"s1", data);
				stat_url_param(sz_context2.substring(problem.length()),"s2", data);

				if (ret.length()==0){
					ret += data.toCSVheader();
					ret += "\n";
				}
				ret += data.toCSVrow();
				ret += "\n";
			}
		}
		return ret;
	}

	private static void stat_url_param(String sz_url, String prefix, DataSmartMap data){
		int counter = 1;
		for (String item : sz_url.split("/")){
			if (item.length()==0)
				continue;
			if (item.equals("proofs"))
				continue;
			if (item.equals("linked"))
				continue;
			if (item.equals("inference-web.org"))
				continue;
			if (item.equals("http:"))
				continue;
			if (item.equals("answer.owl"))
				continue;
			String key = String.format("%s-k%d",prefix, counter);
			data.put(key,item);
			counter ++;
		}
	}
}
