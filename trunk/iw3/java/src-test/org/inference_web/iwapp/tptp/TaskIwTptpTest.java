package org.inference_web.iwapp.tptp;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TaskIwTptpTest {
	@Test
	public void test_improve1(){
		String problem = "http://inference-web.org/proofs/linked/SEU/SEU139+1/";
		
		Set<String> reasoners = new HashSet<String>();
		reasoners.add("EP---1.1");
		reasoners.add("Otter---3.3");
		TaskIwTptpImprove.run_improve(problem,null,"test");
	}

	//@Test
	public void test_mapping(){
		String sz_url_problem = "http://inference-web.org/proofs/linked/AGT/AGT001+1/";
		String sz_url_root_input= "http://inference-web.org/proofs/linked";
		
		TaskIwTptpMapping.run_mapping(sz_url_problem, sz_url_root_input);
	}
}
