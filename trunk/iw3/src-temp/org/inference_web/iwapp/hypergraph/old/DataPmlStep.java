package org.inference_web.iwapp.hypergraph.old;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.app.pml.ToolPML2Writer;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperEdge;
import sw4j.util.DataObjectGroupMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataPmlStep {

	
	public String m_conclusion_langauge = null; 
	public String m_conclusion_text = null;
	public Resource m_conclusion= null;
	public List<RDFNode> m_antecedents = new ArrayList<RDFNode>();
	public Model m_model = ModelFactory.createDefaultModel();
	
	public DataPmlStep(Model m, Resource is){ 
		/// 1. get sink and source
		//get conclusion
		m_conclusion = m.listSubjectsWithProperty(PMLJ.isConsequentOf, is).nextResource();
		
		//get antecedents
		NodeIterator iter_list = m.listObjectsOfProperty(is, PMLJ.hasAntecedentList);
		if (iter_list.hasNext())
			m_antecedents = ToolJena.listListMembers(m,(Resource)(iter_list.nextNode()), PMLDS.first, PMLDS.rest);
	
		
				
		// 2. save a temporary model
		
		// save description about conclusion
		m_model.add(m.listStatements(m_conclusion, null, (String)null));
		
		// save description about information of conclusion
		Resource info = (Resource) (m.listObjectsOfProperty(m_conclusion, PMLJ.hasConclusion).next());
		ToolJena.update_copyResourceDescription(m_model, m, info, null, true);
	
		// save description about inference step
		m_model.add(m.listStatements(is, null, (String)null));
		
		// add hasAntecedent
		{
			NodeIterator iter = m.listObjectsOfProperty(is, PMLJ.hasAntecedentList);
			while (iter.hasNext()){
				Resource res = (Resource) iter.next();
				
				m_model.add(m.listStatements(res, null, (String)null));
				iter = m.listObjectsOfProperty(res,PMLDS.rest);
			}
		}
		
		// save hasSourceUsage associated with this inference step
		{
			NodeIterator iter= m.listObjectsOfProperty(is, PMLJ.hasSourceUsage);
			if (iter.hasNext()){
				Resource res = (Resource) iter.next();
				ToolJena.update_copyResourceDescription(m_model, m, res, null, true);
			}
		}

		// save prefix mapping
		ToolJena.update_copyNsPrefix(m_model, m);
		
	
		
		// 3. save text of information
		m_conclusion_text="";
		Property [] PROPS_INFO = new Property[]{
				PMLP.hasLanguage,
				PMLP.hasRawString,
		};
		for (int i=0; i<PROPS_INFO.length; i++){
			Property p = PROPS_INFO[i];
			
			if ( m.listObjectsOfProperty(info,p).hasNext()){
				m_conclusion_text += ToolJena.getNodeString(m.listObjectsOfProperty(info,p).nextNode()) +"\n";
			}
		}
		m_conclusion_text = m_conclusion_text.replaceAll("http://inference-web.org/registry/LG/TPTPFOF.owl", "");
		m_conclusion_text = m_conclusion_text.replaceAll("http://inference-web.org/registry/LG/TPTPCNF.owl", "");

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
	
	//static final String BASE_TEMP = "http://inference-web.org/tptp.owl#"; 
	
	public void appendPmlModel(DataObjectGroupMap<Resource> map_res_gid, String szNamespace, int index, Model m){
		Model tptpModel = ModelFactory.createDefaultModel();
		
		// use preferred namespace
		ToolPML2Writer writer = new ToolPML2Writer(tptpModel, szNamespace);

		HashSet<RDFNode> nodesets = new HashSet<RDFNode>();
		nodesets.add(this.m_conclusion);
		nodesets.addAll(this.m_antecedents);
		
		StmtIterator iter = this.m_model.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();

			//remove old index info
			if (stmt.getPredicate().equals(PMLJ.hasIndex))
				continue;
			
			Resource subject = stmt.getSubject();
			if (nodesets.contains(subject)){
				subject = writer.createResource(map_res_gid.addObject(subject));
			}
			
			RDFNode object = stmt.getObject();
			if (nodesets.contains(object)){
				object = writer.createResource(map_res_gid.addObject((Resource)object));
			}
			
			tptpModel.add(tptpModel.createStatement(subject, stmt.getPredicate(), object));
		}

		//set new index info
		tptpModel.add(tptpModel.createStatement(m_model.listSubjectsWithProperty(RDF.type,PMLJ.InferenceStep).nextResource(), PMLJ.hasIndex, m.createTypedLiteral(index)));
		
		ToolJena.update_copy(m, tptpModel);
		ToolJena.update_copyNsPrefix(m, m_model);
	}
}
