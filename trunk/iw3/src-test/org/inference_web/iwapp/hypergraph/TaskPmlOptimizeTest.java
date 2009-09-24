package org.inference_web.iwapp.hypergraph;

import java.io.File;
import java.util.HashSet;
import org.inference_web.iwapp.hypergraph.TaskPmlOptimize;
import org.junit.Test;


public class TaskPmlOptimizeTest {
	
	@Test
	public void test(){
		HashSet<String> test = new HashSet<String>();
		test.add("EP---1.1pre/");
		test.add("Metis---2.2/");

		TaskPmlOptimize tool = new TaskPmlOptimize();
		File dir_base = new File("files/tptp/combine/");
		tool.init_self("http://inference-web.org/test/combine/",dir_base, test, "PUZ/PUZ001-1/");
		tool.run_improve_self();

	}
}

