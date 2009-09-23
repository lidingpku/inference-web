package org.inference_web.iwapp.hypergraph;

import java.util.HashMap;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.rdf.util.ToolJena;
import sw4j.util.DataPVTMap;
import sw4j.util.ToolString;

public class DataPmlInfoMapping {
	HashMap<String, Resource> map_formula_info = new HashMap<String, Resource>();

	public void add_model(Model m){
		if (null==m)
			return;
		
		//safe mappings
		Set<RDFNode> nodes = m.listObjectsOfProperty(PMLJ.hasConclusion).toSet();
		for (Statement stmt: m.listStatements(null,PMLP.hasRawString, (String)null).toSet()){
			if (!nodes.contains(stmt.getSubject()))
				continue;
			
			map_formula_info.put(ToolJena.getNodeString(stmt.getObject()), stmt.getSubject());
		}
	}
	
	public Model get_mappings(){
		DataPVTMap<String,String> map_norm_formula = ToolFormula.map_eq_formula(map_formula_info.keySet());
		
		Model model_mappings = ModelFactory.createDefaultModel();
		for (String norm: map_norm_formula.keySet()){
			Set<String> set_formula = map_norm_formula.getValuesAsSet(norm);
			Resource res_info_root = null;
			for (String formula: set_formula){
				Resource res_info = map_formula_info.get(formula);
				
				if (null==res_info_root)
					res_info_root = res_info;
				else
					model_mappings.add(model_mappings.createStatement(res_info_root, OWL.sameAs, res_info));
			}
		}

		System.out.println(ToolString.printCollectionToString(map_norm_formula.keySet()));

		return model_mappings;

	}
	
}
