package org.inference_web.iwapp.hypergraph;

import org.inference_web.iwapp.hypergraph.old.AgentCombine;
import org.inference_web.iwapp.hypergraph.old.ToolTptp;

public class TestCombinedMany {
	
	public static void main(String[] args) {
		test2("mapping");
		test2("both");
	}
	
	public static void test2(String option){
		String [] problems = new String []{
				"CSR014+1",
				"CSR034+3",
				"CSR063+1",
				"LAT300+1",
				"NUM294+1",
				"PUZ001-1",
				"SEU368+1",
				"SWC028+1",
				"SWC203+1",
				"SWV139+1",
				"SWV140+1",
				"SWV141+1",
				"SWV142+1",
				"SWV143+1",
				"SWV144+1",
				"SWV151+1",
				"SWV152+1",
				"SWV164+1",
				"SWV165+1",
				"SWV181+1",
				"SWV416+1",
				"TOP028+1",
		};
		for (int i=0; i<problems.length; i++){
			AgentCombine acp = new AgentCombine();
			ToolTptp.init_one_problem(acp,
					"http://www.cs.rpi.edu/~dingl/work/tptp/t2/"+option,
					"files/tptp/t2/"+option,
					problems[i]);

			if (option.equals("mapping"))
				acp.USE_STRING_MAPPING = false;
			else
				acp.USE_STRING_MAPPING = true;
			
			acp.process();
		}
	}

}
