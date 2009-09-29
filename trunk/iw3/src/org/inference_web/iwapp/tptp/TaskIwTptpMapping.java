package org.inference_web.iwapp.tptp;

import java.io.File;

public class TaskIwTptpMapping extends AgentIwTptp{
	public static void main(String[] argv){
		run_mapping();
	}
	
	public static void run_mapping(){
		//prepare seeds
		String sz_url_seed = "http://inference-web.org/test/norm/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/test/norm";
		File dir_root_output = new File("www/test/norm");
		String sz_url_root_output = "http://inference-web.org/test/norm";
		
		TaskIwTptpMapping task = new TaskIwTptpMapping();
		task.init(sz_url_seed, sz_url_root_input, dir_root_output, sz_url_root_output);
		task.run_load_data();
		task.run_create_mappings(true);
	}
	
	
	
	
	
}
