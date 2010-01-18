package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.Set;

import org.inference_web.pml.AgentPmlCrawler;

import sw4j.util.DataPVHMap;

public class TaskIwTptpMapping extends AgentIwTptp{
	public static void main(String[] argv){
		run_test();
	}

	public static void run(){
		DataPVHMap<String,String> ps = list_tptp_problem_solution("http://inference-web.org/proofs/linked/");
		
		String sz_url_root_input= "http://inference-web.org/proofs/linked";
		for (String sz_url_seed: ps.keySet())
			run_mapping(sz_url_seed, ps.getValuesAsSet(sz_url_seed),sz_url_root_input);		
	}
	
	public static void run_test(){
		String sz_url_seed = "http://inference-web.org/proofs/linked/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/proofs/linked";
		
		run_mapping(sz_url_seed, null,sz_url_root_input);
	}
	
	public static void run_mapping(String sz_url_seed, Set<String> set_url_pml, String sz_url_root_input){
		//prepare seeds
		File dir_root_output = new File("www/proofs/linked");
		String sz_url_root_output = "http://inference-web.org/proofs/linked";
		
		TaskIwTptpMapping task = new TaskIwTptpMapping();
		if (null==set_url_pml)
			task.init(sz_url_seed, sz_url_root_input, dir_root_output, sz_url_root_output, false);
		else
			task.init(sz_url_seed, sz_url_root_input,set_url_pml, dir_root_output, sz_url_root_output);
		
		task.filter_url_answer_only();
		
		task.run_load_data();

		//create mappings
		task.run_create_mappings(true);
		
		//generate statistics
		task.run_create_stats();
	}
	
	
	public static DataPVHMap<String,String> list_tptp_problem_solution(String sz_url_root){
		Set<String> set_url_pml = AgentPmlCrawler.crawl_quick(sz_url_root,false);
		int LIMIT = 2;
			
		DataPVHMap<String,String> map = new DataPVHMap<String,String>();
		for (String sz_url_pml: set_url_pml){
			//skip non- answer.owl
			if (!sz_url_pml.endsWith("answer.owl"))
				continue;
			
			
			String sz_url_base = sz_url_pml;
			for (int i=0; i<LIMIT; i++)
				sz_url_base = sz_url_pml.substring(0, sz_url_base.lastIndexOf("/"));
			map.add(sz_url_base+"/", sz_url_pml);
		}
		return map;
	}
	

	
	
}
