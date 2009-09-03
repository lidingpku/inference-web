package org.inference_web.iwapp.hypergraph;


import java.util.Collection;
import java.util.Iterator;

import sw4j.app.pml.PMLDS;
import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ToolHypergraphData {

	
	public static Model pml2hg(String url_pml, String xmlbase){
		Model pml = ModelFactory.createDefaultModel();
		pml.read(url_pml);
		return pml2hg(pml,xmlbase);
	}
	
	public static Model pml2hg(Model pml, String xmlbase){
		ToolJena.model_update_List2Map(pml, PMLDS.first, PMLDS.rest, false);

		//ToolJena.printModel(pml);
		
		//TODO: update this sparql query
		String queryString = "CONSTRUCT {?s ?p ?o .} WHERE {?s ?p ?o .}";
		Dataset ds = DatasetFactory.create(pml);
		Model m = (Model) new AgentSparql().exec(queryString, ds, null);
		
		return m;
		// we will add uri for each inference step later
	}
	
	/**
	 * apply mapping to hg
	 * @param hg
	 * @param mapping
	 * @return
	 */
	public static Model combine(Model hg, Model mapping){
		//TODO: replace B1, B2 with B
		
		return null;
		
	}
	
	public static Model normalize_mappings(Collection<String> urls, String xmlbase){
		Model ret = ModelFactory.createDefaultModel();

		Iterator<String> iter =urls.iterator();
		while (iter.hasNext()){
			String url_mapping = iter.next();
			Model m= ModelFactory.createDefaultModel();
			m.read(url_mapping);
			
			//TODO: add mappings to ret
			
		}
		
		return ret;
	}
	
	public static Model hg2pml(Model pml_original, Model hg, Model mapping, String xmlbase){
		// need to create new nodesets
		
		//TODO
		return null;
	}
}
