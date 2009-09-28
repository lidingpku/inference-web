package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TaskPmlTptp {
	public static void main(String[] argv){
		run_improve();
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
	
	public static void run_improve(){
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		File dir_root_output = new File("www/test/normal");
		String sz_url_root_output = "http://inference-web.org/test/normal";
		
		AgentPmlTptp tpn = new AgentPmlTptp();
		tpn.init(sz_url_seed,  dir_root_output, sz_url_root_output);
		//filter sz_url_pml
		HashSet<String> set_engine = new HashSet<String>();
		set_engine.add("Ayane---1.1");
		set_engine.add("EP---1.1pre");
		set_engine.add("Faust---1.0");
		set_engine.add("Metis---2.2");
		set_engine.add("Otter---3.3");
		set_engine.add("SNARK---20080805r005");
		set_engine.add("SOS---2.0");
		set_engine.add("Vampire---9.0");
		Iterator<String> iter = tpn.set_url_pml.iterator();
		while (iter.hasNext()){
			String sz_url_pml = iter.next();
			if (!sz_url_pml.endsWith("answer.owl")){
				iter.remove();
				continue;
			}
			boolean bProcess=false;
			for (String sz_engine:set_engine){
				if (sz_url_pml.indexOf(sz_engine)>0){
					bProcess=true;
					break;//avoid remove
				}
			}
			
			if (!bProcess)
				iter.remove();
		}
		
		tpn.run();

	}
	public static void run_test(){
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
