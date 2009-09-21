package org.inference_web.iwapp.crawler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.rdf.util.ToolJena;

public class PmlDeductiveOntology {
	public static void main(String[] args){
		//load pml ontologies and make a decutive closure
		Model m= ModelFactory.createDefaultModel();
		m.read(PMLP.getURI());
		m.read(PMLJ.getURI());
		m.read(IW200407.getURI());
		
		System.out.println(m.size());
		m = ToolJena.model_createDeductiveClosure(m);
		System.out.println(m.size());
		ToolJena.printModelToFile(m, "files/iwsearch/pml-ontologies.rdf");
	}
}
