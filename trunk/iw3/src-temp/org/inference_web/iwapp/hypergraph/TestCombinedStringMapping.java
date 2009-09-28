package org.inference_web.iwapp.hypergraph;

import org.inference_web.iwapp.hypergraph.old.AgentCombine;
import org.inference_web.iwapp.hypergraph.old.ToolTptp;

public class TestCombinedStringMapping {
	
	public static void main(String[] args) {
		test1a();
		test1b();
		test1c();
	}
	
	
	// test1a:  provided mapping  
	public static void test1a(){
		AgentCombine acp = new AgentCombine();
		ToolTptp.init_one_problem(acp,
				"http://www.cs.rpi.edu/~dingl/work/tptp/t1/mapping",
				"files/tptp/t1/mapping",
				"PUZ001-1"
		);
		acp.m_config.put(AgentCombine.CONFIG_USE_ALG, 
				AgentCombine.ALG_NODE_CNF +","+
				AgentCombine.ALG_AXIOM_CNF   +","+
				AgentCombine.ALG_AXIOM_NODE_CNF   +","+
				AgentCombine.ALG_TRAVERSE );

		
		acp.USE_STRING_MAPPING =false;

		acp.process();
	}
	
	// test1c:  string mapping 
	public static void test1b(){
		AgentCombine acp = new AgentCombine();
		ToolTptp.init_one_problem(acp,
				"http://www.cs.rpi.edu/~dingl/work/tptp/t1/string",
				"files/tptp/t1/string",
				"PUZ001-1"
		);
		acp.m_config.put(AgentCombine.CONFIG_USE_ALG, 
				AgentCombine.ALG_NODE_CNF +","+
				AgentCombine.ALG_AXIOM_CNF   +","+
				AgentCombine.ALG_AXIOM_NODE_CNF   +","+
				AgentCombine.ALG_TRAVERSE );

		acp.m_urls_mapping.clear();
		acp.USE_STRING_MAPPING =true;
		acp.process();
	}
	
	// test1b:  provided mapping + string mapping 
	public static void test1c(){
		AgentCombine acp = new AgentCombine();
		ToolTptp.init_one_problem(acp,
				"http://www.cs.rpi.edu/~dingl/work/tptp/t1/both",
				"files/tptp/t1/both",
				"PUZ001-1"
		);
		acp.m_config.put(AgentCombine.CONFIG_USE_ALG, 
				AgentCombine.ALG_NODE_CNF +","+
				AgentCombine.ALG_AXIOM_CNF   +","+
				AgentCombine.ALG_AXIOM_NODE_CNF   +","+
				AgentCombine.ALG_TRAVERSE );

		acp.USE_STRING_MAPPING =true;

		acp.process();
	}

}
