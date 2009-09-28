package org.inference_web.iwapp.hypergraph;

import org.inference_web.iwapp.hypergraph.old.AgentCombine;
import org.inference_web.iwapp.hypergraph.old.ToolTptp;

public class TestCombineSimple {
	
	public static void main(String[] args) {
		test2("mapping");
		//test2("both");
	}
	
	public static void test2(String option){
		String [] problems = new String []{
				"PUZ001-1",
		};
		for (int i=0; i<problems.length; i++){
			AgentCombine acp = new AgentCombine();
			ToolTptp.init_one_problem(acp,
					"http://www.cs.rpi.edu/~dingl/work/tptp/combinesimple/"+option,
					"files/tptp/combinesimple/"+option,
					problems[i]);

			if (option.equals("mapping"))
				acp.USE_STRING_MAPPING = false;
			else
				acp.USE_STRING_MAPPING = true;
			
			acp.process();
		}
	}

}
