package org.inference_web.iwapp.hypergraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import sw4j.app.pml.PMLR;
import sw4j.task.graph.DataHyperEdge;
import sw4j.util.DataObjectGroupMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class DataHgStep {

	
	public String m_url_context= null;
	public Resource m_conclusion= null;
	public HashSet<Resource> m_antecedents = new HashSet<Resource>();
	public Resource m_is =null;
	
	public DataHgStep(Model m, Resource is, String url_context){ 
		/// 1. get sink and source
		//get conclusion
		m_conclusion = (Resource)(m.listObjectsOfProperty(is,PMLR.hasOutput).next());
		
		//get antecedents
		for (RDFNode node: m.listObjectsOfProperty(is, PMLR.hasInput).toSet()){
			Resource res = (Resource) node;
			m_antecedents.add(res);
		}
				
		m_url_context = url_context;
		
		m_is =is;
	}
	
	
	public DataHyperEdge getHyperEdge(DataObjectGroupMap<Resource> map_res_gid){
		Integer id_sink = map_res_gid.addObject(this.m_conclusion);

		if (m_antecedents.size()>0){
			ArrayList<Integer> id_sources = new ArrayList<Integer>();
			for (Resource res : m_antecedents){
				Integer id_source = map_res_gid.addObject(res);
				id_sources.add(id_source);				
			}			
			return new DataHyperEdge(id_sink, id_sources);
		}else{
			return  new DataHyperEdge(id_sink);
		}
	}
	
}
