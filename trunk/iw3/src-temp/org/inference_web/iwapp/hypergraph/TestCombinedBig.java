package org.inference_web.iwapp.hypergraph;

import org.inference_web.iwapp.hypergraph.old.AgentCombine;
import org.inference_web.iwapp.hypergraph.old.ToolTptp;

import sw4j.task.graph.AgentHyperGraphTraverse;


public class TestCombinedBig {
	
	public static void main(String[] args) {
		test3a();
	}
	
	
	public static void test3a(){
		AgentCombine acp = new AgentCombine();

		
		ToolTptp.init_one_problem(acp,
				"http://www.cs.rpi.edu/~dingl/work/hypergraph/t3",
				"files/hypergraph/t3",
				/*
				 * 
				"PUZ001-1"
				"PUZ056-2.005"
				"PUZ035-7"
				*/
				"PUZ007-1"
		);
		
		AgentHyperGraphTraverse.debug= false;
		
		acp.MAX_TIMEOUT_TRAVERSE =1000;
		acp.MAX_TIMEOUT =1000;
		acp.process();
	}
	

}
