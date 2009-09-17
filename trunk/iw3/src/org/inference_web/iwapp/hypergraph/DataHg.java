package org.inference_web.iwapp.hypergraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
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
import sw4j.util.ToolString;

public class DataHg{
	DataObjectGroupMap<Resource> m_map_res_vertex= new DataObjectGroupMap<Resource>();
	HashSet <DataHgStep> m_steps= new HashSet<DataHgStep>();
	DataPVHMap <String,DataHgStep> m_map_url_step= new DataPVHMap<String,DataHgStep>();
//	HashMap <String,String> m_map_url_defaultRoot= new HashMap<String,String>();
	DataPVHMap <DataHyperEdge,DataHgStep> m_map_edge_step = new DataPVHMap<DataHyperEdge,DataHgStep>();

	HashMap <Resource,String> m_map_res_text = new HashMap <Resource,String> ();
	private HashMap <Resource,String> m_map_res_lang = new HashMap <Resource,String> ();
	private HashSet<Integer> m_vertex_cnf = null;
	private HashSet<Integer> m_vertex_fof = null;
	

	private Model loadHg(String url_input){
		Model m = ModelFactory.createDefaultModel();
		System.out.println("load " + url_input);
		m.read(url_input);

		//System.out.println(ToolString.printCollectionToString(m.listNameSpaces().toSet()));
		System.out.println(ToolString.printCollectionToString(ToolJena.listNameSpaces(m)));
		for (String url : ToolJena.listNameSpacesData(m)){
			//skip original proof because we already have copy
			if (url.indexOf("proofs/tptp")>0)
				continue;
			
			//skip ontology
			if (url.indexOf("/2.0/")>0)
				continue;

			System.out.println("load " + url);
			Model model =  ModelFactory.createDefaultModel();
			model.read(url);
			m.add(model);
		}
		return m;
	}	
	
	public void addHg(String url_input){
		
		Model m =loadHg(url_input);
		
		// add steps
		for (Resource is: m.listSubjectsWithProperty(RDF.type, PMLR.Step).toList())
		{
				DataHgStep step = new DataHgStep(m, is, url_input);
				m_map_url_step.add(url_input, step);
				m_steps.add(step);
		}
		
		
		//prepare node info
		m_map_res_text= new HashMap<Resource,String> ();
		for (Statement stmt: m.listStatements(null, PMLP.hasRawString, (String)null).toSet()){
			m_map_res_text.put(stmt.getSubject(), ToolJena.getNodeString(stmt.getObject()));
		}
		
		m_map_res_lang= new HashMap<Resource,String> ();
		for (Statement stmt: m.listStatements(null, PMLP.hasLanguage, (String)null).toSet()){
			m_map_res_lang.put(stmt.getSubject(), ((Resource)stmt.getObject()).getLocalName());
		}		
		
		//add nodes
		HashSet<RDFNode> nodes = new HashSet<RDFNode>(); 
		nodes.addAll(m.listObjectsOfProperty(PMLR.hasInput).toSet());
		nodes.addAll(m.listObjectsOfProperty(PMLR.hasOutput).toSet());
		
		for (RDFNode node : nodes){
			Resource info = (Resource)node;
			m_map_res_vertex.addObject(info);
		}
		
		//System.out.println(this.getHyperGraph(szURL).data_export());
		
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
	
	public DataHyperGraph getHyperGraph(String szURL){
		DataHyperGraph lg = new DataHyperGraph();
			
		Iterator<DataHgStep> iter_step = this.m_map_url_step.getValues(szURL).iterator();
		while (iter_step.hasNext()){
			DataHgStep step = iter_step.next();
			DataHyperEdge  edge = step.getHyperEdge(this.m_map_res_vertex);
			lg.add(edge, szURL);
			
			m_map_edge_step.add(edge, step);
		}
		return lg;
	}
	
	public DataHyperGraph getHyperGraph(){
		DataHyperGraph lg = new DataHyperGraph();
		Iterator<String> iter = this.m_map_url_step.keySet().iterator();
		while (iter.hasNext()){
			String szURL = iter.next();
			
			DataHyperGraph lgx = getHyperGraph(szURL);
			lg.add(lgx);
		}
		return lg;
	}
/*
	public Model getPmlModel(DataHyperGraph lg, String szNamespace){
		Model tptpModel = ModelFactory.createDefaultModel();

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
				step.appendPmlModel(this.m_map_res_vertex, szNamespace, index, tptpModel);
				index++;
			}
		}
		
		return tptpModel;
	}	
*/	
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
	
	public HashMap<Integer,Properties> getMapNodeParams (){
		HashMap<Integer,Properties> ret = new HashMap<Integer,Properties>();
		for (Resource res: m_map_res_vertex.keyset()){
			String label = m_map_res_text.get(res);
			String lang = m_map_res_lang.get(res);
			
			Properties prop = new Properties();
			prop.put("label", label.replaceAll("\n", " "));

			//prop.put("URL", res.getURI());
			
			if ("TPTPCNF".equals(lang)){
				prop.put("color", "red");
			}
			
			Integer gid = m_map_res_vertex.getGid(res);
			ret.put(gid, prop);
		}
		
		return ret;
		
	}
	
	public HashMap<DataHyperEdge,Properties> getMapEdgeParams (){
		HashMap<DataHyperEdge,Properties> ret = new HashMap<DataHyperEdge,Properties>();
		for (DataHyperEdge e: this.m_map_edge_step.keySet() ){
			Properties prop = new Properties();
			for (DataHgStep hge:  m_map_edge_step.getValuesAsSet(e)){
				prop.put("URL", hge.m_url_context);
			}
			

			ret.put(e, prop);
		}
		
		return ret;
		
	}
}
