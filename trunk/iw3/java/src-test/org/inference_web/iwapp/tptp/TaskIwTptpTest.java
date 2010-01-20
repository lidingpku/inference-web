package org.inference_web.iwapp.tptp;

import org.junit.Test;

public class TaskIwTptpTest {
	//@Test
	public void test_improve(){
		TaskIwTptpImprove.run_test();
	}

	@Test
	public void test_mapping(){
		String sz_url_problem = "http://inference-web.org/proofs/linked/AGT/AGT001+1/";
		String sz_url_root_input= "http://inference-web.org/proofs/linked";
		
		TaskIwTptpMapping.run_mapping(sz_url_problem, sz_url_root_input);
	}
}
