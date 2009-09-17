package org.inference_web.iwapp.hypergraph;

import java.util.ArrayList;
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
	public List<RDFNode> m_antecedents = new ArrayList<RDFNode>();
	public Resource m_is =null;
	
	public DataHgStep(Model m, Resource is, String url_context){ 
		/// 1. get sink and source
		//get conclusion
		m_conclusion = (Resource)(m.listObjectsOfProperty(is,PMLR.hasOutput).next());
		
		//get antecedents
		m_antecedents = m.listObjectsOfProperty(is, PMLR.hasInput).toList();
				
		m_url_context = url_context;
		
		m_is =is;
	}
	
	
	public DataHyperEdge getHyperEdge(DataObjectGroupMap<Resource> map_res_gid){
		Integer id_sink = map_res_gid.addObject(this.m_conclusion);

		Iterator<RDFNode> iter = m_antecedents.iterator();
		if (iter.hasNext()){
			ArrayList<Integer> id_sources = new ArrayList<Integer>();
			while (iter.hasNext()){
				Resource res = (Resource)iter.next();

				Integer id_source = map_res_gid.addObject(res);
				id_sources.add(id_source);				
			}
			
			return new DataHyperEdge(id_sink, id_sources);
		}else{
			return  new DataHyperEdge(id_sink);
		}
	}
	
}
