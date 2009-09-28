package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TaskPmlTptp {
	public static void main(String[] argv){
		run_test_test();
	}
	
	public static void run_crawl_all(){
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/";
		File dir_root_output = new File("www/test/normal");
		String sz_url_root_output = "http://inference-web.org/test/normal";
		
		AgentPmlTptp tpn = new AgentPmlTptp();
		tpn.init(sz_url_seed,  dir_root_output, sz_url_root_output);
		tpn.run();

	}
	
	public static void run_test_test(){
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		File dir_root_output = new File("www/test/normal");
		String sz_url_root_output = "http://inference-web.org/test/normal";
		Set<String> set_url_pml = new HashSet<String>();
		set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/answer.owl"); 
		set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Otter---3.3/answer.owl"); 
		
		AgentPmlTptp tpn = new AgentPmlTptp();
		tpn.init(sz_url_seed,  set_url_pml, dir_root_output, sz_url_root_output);
		tpn.run();
	}

}
