package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class TaskIwTptpStat extends AgentIwTptp {
	
	public static void main(String[] argv){
		run_stat();
	}
	
	public static  Set<String> prepareProblemAll(){
		String sz_url_seed = "http://inference-web.org/proofs/linked/";
		
		Set<String> set_problem = prepare_tptp_problems(sz_url_seed);
		return set_problem;
	}
	
	public static Set<String> prepareProblemByCategory(){
		String [] ARY_CATEGORY = new String []{
				"http://inference-web.org/proofs/linked/PUZ/",
			//	"http://inference-web.org/proofs/linked/SEU/",
			};

		Set<String> set_problem = new TreeSet<String>();
		for (String category: ARY_CATEGORY){
			set_problem.addAll( prepare_tptp_one_step(category));
		}
		
		return set_problem;
	}
	
	public static void run_stat(){	
		//Set<String> set_problem =prepareProblemByCategory();
		Set<String> set_problem = prepareProblemAll();
		for (String problem: set_problem ){
			TaskIwTptpStat.run_improve(problem, null, "stat");
		}		
	}
	

	

	public static void run_improve(String problem, Set<String> reasoners, String dir_name){
		File dir_root_output = new File("www/proofs/linked-analysis/"+dir_name);
		String sz_url_root_output = "http://inference-web.org/proofs/linked-analysis/"+dir_name;
		String sz_url_root_input= getRootUrl(problem);
		
		Set<String> solutions  =prepare_tptp_solutions(problem);

		filterSolutionGeneral(solutions,reasoners);
		
		filterSolutionUnqiue(solutions);

		TaskIwTptpStat tpn = new TaskIwTptpStat();
		tpn.init(problem, sz_url_root_input, solutions, dir_root_output, sz_url_root_output);

		tpn.run_load_data();
		tpn.run_create_mappings(true);
		tpn.run_create_stats_global("mup");

	}

	public static String getRootUrl(String problem){
		String ret = problem;
		for (int i=0; i<3;i++){
			ret = ret.substring(0,ret.lastIndexOf("/"));
		}
		return ret;
	}
	
}
