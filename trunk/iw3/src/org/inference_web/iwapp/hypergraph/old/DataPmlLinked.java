package org.inference_web.iwapp.hypergraph.old;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVHMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;

public class DataPmlLinked{
	DataObjectGroupMap<Resource> m_map_res_vertex= new DataObjectGroupMap<Resource>();
	HashSet <DataPmlStep> m_steps= new HashSet<DataPmlStep>();
	DataPVHMap <String,DataPmlStep> m_map_url_step= new DataPVHMap<String,DataPmlStep>();
	HashMap <String,String> m_map_url_defaultRoot= new HashMap<String,String>();
	DataPVHMap <DataHyperEdge,DataPmlStep> m_map_edge_step = new DataPVHMap<DataHyperEdge,DataPmlStep>();

	private HashSet<Resource> m_res_cnf = new HashSet<Resource>();
	private HashSet<Resource> m_res_fof = new HashSet<Resource>();
	private HashSet<Integer> m_vertex_cnf = null;
	private HashSet<Integer> m_vertex_fof = null;
	
	public void addPml(String szURL){
		// load model
		Model m = null;
		try {
			m = AgentModelManager.get().loadModel(szURL);
		} catch (Sw4jException e) {
			e.printStackTrace();
			return;
		}
		
		addPml (m, szURL);
		
	}
	
	@SuppressWarnings("unchecked")
	public void addPml(Model m, String szURL){
		if (null==m)
			return;
		// add default root
		m_map_url_defaultRoot.put(szURL, szURL+"#answer");
		
		Set<RDFNode> refered_nodeset =  m.listObjectsOfProperty(PMLDS.first).toSet();
		// add vertex registration 
		{
			StmtIterator iter = m.listStatements(null, RDF.type, PMLJ.NodeSet);
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();
				this.m_map_res_vertex.addObject(stmt.getSubject());

				// count CNF resources
				if (m.listObjectsOfProperty(stmt.getSubject(), PMLJ.hasConclusion).hasNext()){
					Resource info = (Resource) (m.listObjectsOfProperty(stmt.getSubject(), PMLJ.hasConclusion).next());
					
					if (m.listObjectsOfProperty(info,PMLP.hasLanguage).hasNext()){
						if ("http://inference-web.org/registry/LG/TPTPCNF.owl#TPTPCNF".equals(m.listObjectsOfProperty(info,PMLP.hasLanguage).nextNode().toString()))
							m_res_cnf.add(stmt.getSubject());

						if ("http://inference-web.org/registry/LG/TPTPFOF.owl#TPTPFOF".equals(m.listObjectsOfProperty(info,PMLP.hasLanguage).nextNode().toString()))
							m_res_fof.add(stmt.getSubject());
					}	
				}
				
				if (!refered_nodeset.contains(stmt.getSubject())){
					m_map_url_defaultRoot.put(szURL, stmt.getSubject().getURI());
				}
			}
		}

		// add steps
		{
			ResIterator iter =m.listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep);
			while (iter.hasNext()){
				Resource is = iter.nextResource();
				DataPmlStep step = new DataPmlStep(m, is);
				m_map_url_step.add(szURL, step);
				m_steps.add(step);
			}
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
			
		Iterator<DataPmlStep> iter_step = this.m_map_url_step.getValues(szURL).iterator();
		while (iter_step.hasNext()){
			DataPmlStep step = iter_step.next();
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
				
				DataPmlStep step = m_map_edge_step.getValues(edge).iterator().next();
				step.appendPmlModel(this.m_map_res_vertex, szNamespace, index, tptpModel);
				index++;
			}
		}
		
		return tptpModel;
	}	
	
	public HashSet<Integer> getVertexCnf(){
		if (null==this.m_vertex_cnf){
			this.m_vertex_cnf = new HashSet<Integer>();
			Iterator<Resource> iter = this.m_res_cnf.iterator();
			while (iter.hasNext()){
				Resource res = iter.next();
				this.m_vertex_cnf.add(this.m_map_res_vertex.getGid(res));
			}
		}
		return this.m_vertex_cnf;
	}

	public HashSet<Integer> getVertexFof(){
		if (null==this.m_vertex_fof){
			this.m_vertex_fof = new HashSet<Integer>();
			Iterator<Resource> iter = this.m_res_fof.iterator();
			while (iter.hasNext()){
				Resource res = iter.next();
				this.m_vertex_fof.add(this.m_map_res_vertex.getGid(res));
			}
		}
		return this.m_vertex_fof;
	}
}
