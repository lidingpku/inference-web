package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import sw4j.rdf.util.ToolJena;

public class TestToolPmlTranslator {
	
	public static String pmlj= "http://inference-web.org/2.0/pml-justification.owl#";
	public static String pmlp= "http://inference-web.org/2.0/pml-provenance.owl#";
	public static String pmlr= "http://inference-web.org/2.0/pml-relation.owl#";
	
	@Test
	public void test_load_pml(){
		
		
		String [] address = new String[]{			
//			"http://www.rpi.edu/~huangr3/sw4j/PUZ001-1/testCombineSimple/testcombinesimple.owl",
			"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl",
		};

		
		HashSet<String> mapping_urls= new HashSet<String>();
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Ayane---1.1/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Metis---2.2/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Otter---3.3/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SNARK---20080805r005/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SOS---2.0/equalNS.owl");
		mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Vampire---9.0/equalNS.owl");
	
		
		Model rdf_all= ModelFactory.createDefaultModel();

		ToolHypergraphData gd= new ToolHypergraphData();
		
		Model n= gd.normalize_mappings(mapping_urls, null);
		
		for (int i=0; i< address.length; i++){
			String szURL  = address[i];
			Model m = ToolHypergraphData.pml2hg(szURL,null);
			
			Model after_m= gd.combine(m, n);
			
			rdf_all.add(after_m);
//			ToolJena.printModelToFile(m, "files/test/test2.rdf");
		}
		
		System.out.println("after combining all files generated rdf file contains:" + rdf_all.size() +" triples");
		
		rdf_all.setNsPrefix("pmlj",pmlj);
		rdf_all.setNsPrefix("pmlp",pmlp);
		rdf_all.setNsPrefix("pmlr",pmlr);
		
		ToolJena.printModelToFile(rdf_all,"files/test/PUZ001-1/EP---1.0/answer.rdf");
	}
}
