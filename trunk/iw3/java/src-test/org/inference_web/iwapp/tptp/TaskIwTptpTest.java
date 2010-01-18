package org.inference_web.iwapp.tptp;

import org.junit.Test;

public class TaskIwTptpTest {
	//@Test
	public void test_improve(){
		TaskIwTptpImprove.run_test();
	}

	@Test
	public void test_mapping(){
		TaskIwTptpMapping.run_test();
	}
}
