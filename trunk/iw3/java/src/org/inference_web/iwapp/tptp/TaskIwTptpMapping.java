package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.Set;

import sw4j.util.ToolString;


public class TaskIwTptpMapping extends AgentIwTptp{
	public static void main(String[] argv){
		run_cat();
	}

	public static void run(){
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
		
		Set<String> set_problem = prepare_tptp_problems();
		for (String problem: set_problem ){
			System.out.println("processing "+ problem);
			run_mapping(problem, sz_url_root_input);
		}
	}
	
	public static void run_cat(){
		String [] ARY_CATEGORY = new String []{
			 "http://inference-web.org/proofs/linked/PUZ/",
			 "http://inference-web.org/proofs/linked/SEU/",
			 "http://inference-web.org/proofs/linked/NUM/",
			 "http://inference-web.org/proofs/linked/SWC/",
		};
		String sz_url_root_input= "http://inference-web.org/proofs/linked";

		for (String category: ARY_CATEGORY){
			Set<String> set_problem = prepare_tptp_one_step(category);
			for (String problem: set_problem ){
				System.out.println("processing "+ problem);
				run_mapping(problem, sz_url_root_input);
			}			
		}
	}
	
	
	public static void run_mapping(String sz_url_problem,  String sz_url_root_input){
		//prepare seeds
		File dir_root_output = new File("www/proofs/linked");
		String sz_url_root_output = "http://inference-web.org/proofs/linked";
		
		TaskIwTptpMapping task = new TaskIwTptpMapping();
		task.init(sz_url_problem, sz_url_root_input, dir_root_output, sz_url_root_output);
				
		task.run_load_data();

		//create mappings
		task.run_create_mappings(true);
		
		//generate statistics
		task.run_create_stats();

		System.gc();
		System.out.println( ToolString.formatXMLDateTime()+" free memory: "+Runtime.getRuntime().freeMemory());		
	}
	
	/*
	private static DataPVHMap<String,String> list_tptp_problem_solution(String sz_url_root){
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
	*/

	
	
}
