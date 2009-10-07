package temp.test;

import java.util.ArrayList;
import java.util.HashSet;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLR;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperEdge;
import sw4j.util.DataObjectGroupMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataHgStep {


	public String m_context= null;
	public Resource m_conclusion= null;
	public HashSet<Resource> m_antecedents = new HashSet<Resource>();
	public Resource m_is =null;
	public Resource m_inference_engine =null;
	public Resource m_inference_rule=null;

	public DataHgStep(Model m, Resource is, String context){ 
		/// 1. get sink and source
		//get conclusion
		m_conclusion = (Resource)(ToolJena.getValueOfProperty(m, is, PMLR.hasOutput, (Resource)null));

		//get antecedents
		for (RDFNode node: m.listObjectsOfProperty(is, PMLR.hasInput).toSet()){
			Resource res = (Resource) node;
			m_antecedents.add(res);
		}

		m_inference_engine= (Resource)(ToolJena.getValueOfProperty(m, is, PMLJ.hasInferenceEngine, (Resource)null));
		
		m_inference_rule = (Resource)(ToolJena.getValueOfProperty(m, is, PMLJ.hasInferenceRule, (Resource)null));
		if (null== m_inference_rule){
			System.out.println(is);
		}
		
		m_context = context;

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

	public void appendPmlrModel(Model m){
		m.add(this.m_is, RDF.type, PMLR.Step);
		m.add(this.m_is, PMLR.hasOutput, this.m_conclusion);
		for (Resource antecedent: this.m_antecedents){
			m.add(this.m_is, PMLR.hasInput, antecedent);
		}
	}
}
