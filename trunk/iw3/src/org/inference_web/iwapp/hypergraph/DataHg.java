package org.inference_web.iwapp.hypergraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;


import sw4j.app.pml.PMLP;
import sw4j.app.pml.PMLR;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVHMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;

public class DataHg{
	HashSet <DataHgStep> m_steps= new HashSet<DataHgStep>();
	DataPVHMap <String,DataHgStep> m_map_url_step= new DataPVHMap<String,DataHgStep>();

	DataObjectGroupMap<Resource> m_map_res_vertex= new DataObjectGroupMap<Resource>();
	
	//	HashMap <String,String> m_map_url_defaultRoot= new HashMap<String,String>();
	DataPVHMap <DataHyperEdge,DataHgStep> m_map_edge_step = new DataPVHMap<DataHyperEdge,DataHgStep>();

	HashMap <Resource,String> m_map_res_text = new HashMap <Resource,String> ();
	private HashMap <Resource,String> m_map_res_lang = new HashMap <Resource,String> ();
	private HashSet<Integer> m_vertex_cnf = null;
	private HashSet<Integer> m_vertex_fof = null;
	
	HashMap <String,String> m_map_reasoner_color = new HashMap <String,String>();
	
	HashMap<String,Resource> m_url_root_node = new HashMap<String,Resource>();;
	
	public DataHg(){
		m_map_reasoner_color.put("EP", "blue" );
		m_map_reasoner_color.put("SOS", "green" );
		m_map_reasoner_color.put("Ayane", "red" );
		m_map_reasoner_color.put("Metis", "yellow" );
		m_map_reasoner_color.put("Faust", "brown" );
		m_map_reasoner_color.put("Otter", "grey" );
		m_map_reasoner_color.put("SNARK", "purple" );
		m_map_reasoner_color.put("Vampire", "cyan" );
	}
	
	
	public void addHg(DataHg hg){
		this.m_map_res_text.putAll(hg.m_map_res_text);
		this.m_map_res_lang.putAll(hg.m_map_res_lang);
		this.m_map_res_vertex.add(hg.m_map_res_vertex);
		this.m_map_url_step.add(hg.m_map_url_step);
		this.m_steps.addAll(hg.m_steps);
	}
	
	public static Model load_hg_data(String url_input){
		Model m = ModelFactory.createDefaultModel();
		System.out.println("load " + url_input);
		m.read(url_input);

		//System.out.println(ToolString.printCollectionToString(m.listNameSpaces().toSet()));
		//System.out.println(ToolString.printCollectionToString(ToolJena.listNameSpaces(m)));
		for (String url : ToolJena.namespace_listByParse(m.listSubjectsWithProperty(RDF.type).toSet())){
			//skip original proof because we already have copy
			
			System.out.println("load more ..." + url);
			Model model =  ModelFactory.createDefaultModel();
			model.read(url);
//			ToolJena.printModel(model);
			m.add(model);
		}
		return m;
	}
		
	public void addHg(String url_input){
		Model m =load_hg_data(url_input);
		addHg(m,url_input);
	}
	
	public void addHg(Model m,String url_input){
		// add steps
		for (Resource is: m.listSubjectsWithProperty(RDF.type, PMLR.Step).toList())
		{
				DataHgStep step = new DataHgStep(m, is, url_input);
				m_map_url_step.add(url_input, step);
				m_steps.add(step);
		}
		
		
		//prepare node info
		for (Statement stmt: m.listStatements(null, PMLP.hasRawString, (String)null).toSet()){
			m_map_res_text.put(stmt.getSubject(), ToolJena.getNodeString(stmt.getObject()));
		}
		
//		for (Statement stmt: m.listStatments(null, ){
//			
//		}
		
		for (Statement stmt: m.listStatements(null, PMLP.hasLanguage, (String)null).toSet()){
			m_map_res_lang.put(stmt.getSubject(), ((Resource)stmt.getObject()).getLocalName());
		}		
		
		// TODO: fix bug here!! 
		//add nodes
		HashSet<RDFNode> nodes = new HashSet<RDFNode>(); 
		Set<RDFNode> nodes_input= m.listObjectsOfProperty(PMLR.hasInput).toSet();
		Set<RDFNode> nodes_output= m.listObjectsOfProperty(PMLR.hasOutput).toSet();
		nodes.addAll(nodes_input);
		nodes.addAll(nodes_output);
		
		for (RDFNode node : nodes){
			Resource info = (Resource)node;
			m_map_res_vertex.addObject(info);
		}
		
		//System.out.println(this.getHyperGraph(szURL).data_export());
		
		//set root node
		nodes_output.removeAll(nodes_input);
		if (nodes_output.size()!=1){
			System.out.println(nodes_output);
		}else{
			m_url_root_node.put(url_input, (Resource) nodes_output.iterator().next());			
		}
		
		resetCache();
	}
	
	public void addMappings(String szURL){
		// load model
		Model m = null;
		try {
			m = AgentModelManager.get().loadModel(szURL);
		} catch (Sw4jException e) {
			e.printStackTrace();
			return;
		}
		
		addMappings(m);
	}
	
	public void addMappings(Model m){
		if (null==m)
			return;
		
		// list all owl:sameAs relation
		StmtIterator iter = m.listStatements(null, OWL.sameAs, (Resource)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			if (isBadMapping(stmt.getSubject(),stmt.getObject()))
				continue;
			
			m_map_res_vertex.addSameObjectAs(stmt.getSubject(), ((Resource)(stmt.getObject())));
		}		

		resetCache();
	}

	public static boolean isBadMapping(Resource subject, RDFNode object){
		
		//skip mapping to root nodeset
		boolean bTouchRoot = subject.getURI().endsWith("#answer");
		bTouchRoot |= ToolJena.getNodeString(object).endsWith("#answer");
		
		boolean bSameProof = ToolSafe.isEqual(subject.getNameSpace(), ((Resource)(object)).getNameSpace());
		
		if (bTouchRoot){
			if (bSameProof){
				
			}else{
				return true;
			}
		}
		
		return false;
	}
	
	private void resetCache(){
		this.m_vertex_cnf=null;
		this.m_vertex_fof=null;
	}
	
	final public static int OPTION_HG_WEIGHT_LEAF= 0; 
	final public static int OPTION_HG_WEIGHT_STEP= 1; 
	public static String getOptionString(int option){
		switch(option){
		case OPTION_HG_WEIGHT_LEAF:
			return "leaf";
		case OPTION_HG_WEIGHT_STEP:
			default:
			return "step";
		}
	}
	
	public Integer getRootNode(String url_pml){
		Resource root = m_url_root_node.get(url_pml);
		if (null==root){
			return null;
		}else{
			return this.m_map_res_vertex.getGid(root);
		}
	}
	
	public DataHyperGraph getHyperGraph(String szURL, int option){
		DataHyperGraph lg = new DataHyperGraph();
			
		Iterator<DataHgStep> iter_step = this.m_map_url_step.getValues(szURL).iterator();
		while (iter_step.hasNext()){
			DataHgStep step = iter_step.next();
			DataHyperEdge  edge = step.getHyperEdge(this.m_map_res_vertex);
			Integer weight= 0;
			switch (option){
			case OPTION_HG_WEIGHT_LEAF:
				if (edge.isAtomic()){
					weight= 1;
				}				
				break;
			case OPTION_HG_WEIGHT_STEP:
			default:
				weight= 1;
				break;
			}
			
			edge.setWeigth(weight);
			lg.add(edge, szURL);
			m_map_edge_step.add(edge, step);
		}
		return lg;
	}
	
	public DataHyperGraph getHyperGraph(){
		return getHyperGraph(OPTION_HG_WEIGHT_STEP);
	}
	
	public DataHyperGraph getHyperGraph(int option){
		DataHyperGraph lg = new DataHyperGraph();
		Iterator<String> iter = this.m_map_url_step.keySet().iterator();
		while (iter.hasNext()){
			String szURL = iter.next();
			
			DataHyperGraph lgx = getHyperGraph(szURL, option);
			lg.add(lgx);
		}
		return lg;
	}


	public Model getPmlrModel(DataHyperGraph lg){
		Model model_ret = ModelFactory.createDefaultModel();

		for(DataHyperEdge edge: lg.getEdges()){
			if (m_map_edge_step.getValuesCount(edge)==0){
				System.out.println("Exception");
				continue;
			}
			
			DataHgStep step = m_map_edge_step.getValues(edge).iterator().next();
			step.appendPmlrModel(model_ret);	
		}
		/*
		Iterator<Integer> iter_sink = lg.getOutputs().iterator();
		while (iter_sink.hasNext()){
			Integer sink = iter_sink.next();
			Iterator<DataHyperEdge> iter = lg.getEdgesByOutput(sink).iterator();
			int index=0;
			while (iter.hasNext()){
				DataHyperEdge edge = iter.next();
				
				if (m_map_edge_step.getValuesCount(edge)==0){
					System.out.println("Exception");
					continue;
				}
				
				DataHgStep step = m_map_edge_step.getValues(edge).iterator().next();
				step.appendPmlrModel(model_ret);
				index++;
			}
		}*/
		
		return model_ret;
	}	
	
	public HashSet<Integer> getVertexCnf(){
		if (null==this.m_vertex_cnf){
			this.m_vertex_cnf = new HashSet<Integer>();
			Iterator<Resource> iter = this.m_map_res_lang.keySet().iterator();
			while (iter.hasNext()){
				Resource res = iter.next();
				String lang = m_map_res_lang.get(res);
				if ("TPTPCNF".equals(lang))
					this.m_vertex_cnf.add(this.m_map_res_vertex.getGid(res));
			}
		}
		return this.m_vertex_cnf;
	}

	public HashSet<Integer> getVertexFof(){
		if (null==this.m_vertex_fof){
			this.m_vertex_fof = new HashSet<Integer>();
			Iterator<Resource> iter = this.m_map_res_lang.keySet().iterator();
			while (iter.hasNext()){
				Resource res = iter.next();
				String lang = m_map_res_lang.get(res);
				if ("TPTPFOF".equals(lang))
					this.m_vertex_fof.add(this.m_map_res_vertex.getGid(res));
			}
		}
		return this.m_vertex_fof;
	}
	
	public HashMap<Integer,Properties> getMapNodeParams (DataHyperGraph dhg){
		
		HashMap<Integer,Properties> ret = new HashMap<Integer,Properties>();
		for (Resource res: m_map_res_text.keySet()){
			String label = m_map_res_text.get(res);
			String lang = m_map_res_lang.get(res);
			
			Properties prop = new Properties();
			if (!ToolSafe.isEmpty(label))
				prop.put("label", label.replaceAll("\n", " "));
			else
				System.out.println("no label");
	
			
			prop.put("URL", res.getURI());
			
			if ("TPTPFOF".equals(lang)){
				prop.put("color", "grey");
			}
			
			Integer gid = m_map_res_vertex.getGid(res);
			
			if (null!=dhg){
				int antecedents = dhg.getEdgesByOutput(gid).size();
				if (antecedents>1)
					prop.put("style", "filled");
					prop.put("fillcolor", "red");
			}
			
			ret.put(gid, prop);
		}
		
		return ret;
		
	}
	
	private static String parse_engine_name(String engine_name){
		engine_name =engine_name.replaceAll("[^a-zA-Z]+.+", "");
		//Pattern p = Pattern.compile("\\p{Alpha}+");
		//Matcher m = p.matcher(engine_name);
		//if ( m.matches())
		//	return m.group();
		//else
			return engine_name;
	}

	public HashMap<DataHyperEdge,Properties> getMapEdgeParams (String sz_context){
		HashMap<DataHyperEdge,Properties> ret = new HashMap<DataHyperEdge,Properties>();
		for (DataHyperEdge e: this.m_map_edge_step.keySet() ){
			Properties prop = new Properties();
			//select the optimal one
			
			DataHgStep hge = null;
			for (DataHgStep hgedge:  m_map_edge_step.getValuesAsSet(e)){
				if (null==hge)
					hge=hgedge;
				else if (hgedge.m_context.equals(sz_context))
					hge=hgedge;					
			}

			//add link
			prop.put("URL", hge.m_is.getURI() );

			//set color
			if (!ToolSafe.isEmpty(hge.m_inference_engine)){
				String engine_name = parse_engine_name(hge.m_inference_engine.getLocalName());
				
				String color = this.m_map_reasoner_color.get(engine_name);
				if (null==color)
					color="black";
				prop.put("color", color);					
			}
			
			// set label
			if (!ToolSafe.isEmpty(hge.m_inference_rule))
				prop.put("label", hge.m_inference_rule.getLocalName());

			ret.put(e, prop);
		}
		
		return ret;
		
	}
}
