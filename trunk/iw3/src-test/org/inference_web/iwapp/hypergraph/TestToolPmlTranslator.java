package org.inference_web.iwapp.hypergraph;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.rdf.util.ToolJena;

public class TestToolPmlTranslator {
	
	@Test
	public void test_load_pml(){
		String [] address = new String[]{			
			"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl",
		};

		for (int i=0; i< address.length; i++){
			String szURL  = address[i];
			Model m = ToolHypergraphData.pml2hg(szURL,null);
			ToolJena.printModel(m);
		}
	}
}
