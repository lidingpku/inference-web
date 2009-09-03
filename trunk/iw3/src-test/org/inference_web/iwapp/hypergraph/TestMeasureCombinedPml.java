package org.inference_web.iwapp.hypergraph;

import org.inference_web.iwapp.hypergraph.old.AgentCombine;
import org.inference_web.iwapp.hypergraph.old.ToolTptp;

public class TestMeasureCombinedPml {
	public static void main(String[] args) {
		
		test("PUZ031+1-a","http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ031+1/Vampire---10.0/combined.owl");
		test("PUZ031+1-b","http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ031+1/Vampire---10.0/answer.owl");
		test("PUZ031+1-c","http://plato.cs.rpi.edu/iw/hypergraph/combine200905-false/PUZ031+1/lj.owl");
	}
	
	public static void test(String szProblem, String szURL){
		AgentCombine acp = new AgentCombine();
		ToolTptp.init_one_problem(acp,
				"http://www.cs.rpi.edu/~dingl/work/hypergraph/t4",
				"files/hypergraph/t4",
				szProblem
		);
		acp.m_urls_mapping.clear();
		acp.m_urls_pml.clear();
		acp.m_urls_pml.add(szURL);
		acp.process();
		
	}
}
