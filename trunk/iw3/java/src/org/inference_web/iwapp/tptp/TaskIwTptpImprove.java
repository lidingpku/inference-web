package org.inference_web.iwapp.tptp;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.inference_web.pml.DataPmlHg;
import org.inference_web.pml.ToolPml;

import com.hp.hpl.jena.rdf.model.Resource;

import sw4j.task.graph.AgentHyperGraphAoStar;
import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.AgentHyperGraphTraverse;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class TaskIwTptpImprove extends AgentIwTptp {
	static boolean gbPlotDot = true;
	
	public static void main(String[] argv){
		//String problem = "http://inference-web.org/proofs/linked/PUZ/PUZ001-1/";

		//run_test();
		//run_improve_pair(problem);
		//run_improve(problem,getReasonersEP(),"version/EP");
		//run_improve_version(problem,getReasonersMetis(),"Metis");
		run_improve();
	}
	
	public static void run_test(){
		String problem =  "http://inference-web.org/proofs/linked/NUM/NUM390+1/";
		
		Set<String> reasoners = new HashSet<String>();
		reasoners.add("EP---1.0");
		reasoners.add("SInE---0.3");
		TaskIwTptpImprove.run_improve(problem,reasoners,"test");
	}
	
	public static void run_improve(){
		//gbPlotDot = false;
		//String sz_url_seed = "http://inference-web.org/proofs/linked/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/linked/PUZ/";
		
		Set<String> set_problem = prepare_tptp_problems(sz_url_seed);
		
		for (String problem: set_problem ){
			TaskIwTptpImprove.run_improve(problem, null, "complete");
		}		
	}
	
	public static Set<String> getReasonersCurrent(){
		Set<String> reasoners2 = new TreeSet<String>();
		reasoners2.add("Ayane---1.1");
	//	reasoners2.add("EP---0.999");	
	//	reasoners2.add("EP---1.0");
		reasoners2.add("EP---1.1");
	//	reasoners2.add("EP---1.1pre");
		reasoners2.add("Faust---1.0");
	//	reasoners2.add("Metis---2.1");
		reasoners2.add("Metis---2.2");
		reasoners2.add("Otter---3.3");
		reasoners2.add("SNARK---20080805r005");
		reasoners2.add("SOS---2.0");
	//	reasoners2.add("SPASS---3.01");
		reasoners2.add("Vampire---9.0");
		
		return reasoners2;
	}
	
	public static Set<String> getReasonersEP(){
		Set<String> reasoners2 = new TreeSet<String>();
		reasoners2.add("EP---0.999");	
		reasoners2.add("EP---1.0");
		reasoners2.add("EP---1.1");
		reasoners2.add("EP---1.1pre");
		
		return reasoners2;
	}

	public static Set<String> getReasonersMetis(){
		Set<String> reasoners2 = new TreeSet<String>();
		reasoners2.add("Metis---2.1");
		reasoners2.add("Metis---2.2");
		
		return reasoners2;
	}
	
	public static String extractReasoner(String sz_url_pml){
		for (String component : sz_url_pml.split("/")){
			if (component.contains("---"))
				return component;
		}
		return "";
	}
	
	public static void run_improve_pair(String problem){
		Set<String> reasoners = getReasonersCurrent();

		String sz_url_root_input= getRootUrl(problem);

		Set<String>  solutions  =prepare_tptp_solutions(problem);
		for (String sz_solution_1: solutions){
			String reasoner1 = extractReasoner(sz_solution_1);
			if (!reasoners.contains(reasoner1))
				continue;
			
			for (String sz_solution_2: solutions){
				String reasoner2 = extractReasoner(sz_solution_2);
				if (!reasoners.contains(reasoner2))
					continue;

				if (sz_solution_1.compareTo(sz_solution_2)>=0)
					continue;
				
				String dir_name= reasoner1+"_"+reasoner2;
				File dir_root_output = new File("www/proofs/linked-analysis/pair/"+dir_name);
				String sz_url_root_output = "http://inference-web.org/proofs/linked-analysis/pair/"+dir_name;
				
				TaskIwTptpImprove tpn = new TaskIwTptpImprove();
				Set<String> set_url_pml = new HashSet<String>();
				set_url_pml.add(sz_solution_1); 
				set_url_pml.add(sz_solution_2); 
				
				tpn.init(problem, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
				tpn.run();
			}
		}
	}

	public static void run_improve(String problem, Set<String> reasoners, String dir_name){
		File dir_root_output = new File("www/proofs/linked-analysis/"+dir_name);
		String sz_url_root_output = "http://inference-web.org/proofs/linked-analysis/"+dir_name;
		String sz_url_root_input= getRootUrl(problem);
		
		Set<String> solutions  =prepare_tptp_solutions(problem);
		Iterator<String> iter = solutions.iterator();
		while (iter.hasNext()){
			String sz_solution=iter.next();
			String reasoner = extractReasoner(sz_solution);
			if (!ToolSafe.isEmpty(reasoners)&&!reasoners.contains(reasoner))
				iter.remove();
		}

		TaskIwTptpImprove tpn = new TaskIwTptpImprove();
		tpn.init(problem, sz_url_root_input, solutions, dir_root_output, sz_url_root_output);
		tpn.run();						

	}

	public static String getRootUrl(String problem){
		String ret = problem;
		for (int i=0; i<3;i++){
			ret = ret.substring(0,ret.lastIndexOf("/"));
		}
		return ret;
	}
	
	
	
	public void run(){
		try {
			run_load_data();
			run_plot_original();
			run_combine();
			run_improve(CONTEXT_IMPROVE_SELF, DataPmlHg.OPTION_WEIGHT_STEP);
			run_improve(CONTEXT_IMPROVE_ROOT, DataPmlHg.OPTION_WEIGHT_STEP);
			run_improve(CONTEXT_IMPROVE_GLOBAL, DataPmlHg.OPTION_WEIGHT_STEP);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void run_plot_original() throws MalformedURLException{
		//plot unless required
		if (!gbPlotDot)
			return;
		

		String sz_context= "_original";
		for (String sz_url_pml: set_url_pml){
						
			//plot it
			String sz_path = prepare_path(sz_url_pml, sz_context);
			File f_output_graph = new File(dir_root_output, sz_path);

			Resource res_root = m_hg.getRoot(sz_url_pml);
			Set<Resource> set_step = m_hg.getSubHg(res_root);

			
			String sz_dot = m_hg.graphviz_export_dot(set_step);
			DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());			
		}
	}
	
	public void run_combine() throws MalformedURLException{
		run_create_mappings(false);
		
		//DataHyperGraph dhg = this.m_hg.getHyperGraph();
		String sz_path = prepare_path(sz_url_problem,null)+"combine";
		File f_output_graph = new File(dir_root_output, sz_path);

		//merge node now
		m_hg.getHyperGraph();

		
		
		//plot unless required
		if (!gbPlotDot)
			return;
		// show the combined graph
		String sz_dot = m_hg.graphviz_export_dot(m_hg.getSubHg());
		DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
	}
	
	/*	
	@SuppressWarnings("unchecked")
	public void run_improve_self() throws MalformedURLException{
		String sz_context ="_improve_self";
		for (String sz_url_pml: set_url_pml){
			///////////////////////////////////////////
			//generate graphics
			Resource res_info_root = m_hg.getRoot(sz_url_pml);
			Integer root = m_hg.getHyperNode(res_info_root);
			Set<Resource> set_step_original= this.m_hg.getSubHg(res_info_root);
			DataHyperGraph dhg = this.m_hg.getHyperGraph(set_step_original);

			m_hg.hg_set_weight(dhg,DataPmlHg.OPTION_WEIGHT_STEP);
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,root);
	
			
			//plot
			if (ToolSafe.isEmpty(hgt.getSolutions())){
				System.exit(-1);
			}
				
			DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);
			Set<Resource> set_step_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);
			{
				String sz_path = prepare_path(sz_url_pml,sz_context);
				File f_output_graph = new File(dir_root_output, sz_path);

				String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_step_optimal, set_step_original);
				DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());				
			}

			//save data
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
						this.m_hg.copy_without_loop(set_step_original),
				};
				
				Model model_self= ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root);
				String sz_path = prepare_path(sz_url_pml,sz_context+"_combine.rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_save_data(model_self, f_output_rdf, ToolPml.IWV_NAMESPACE, null);
			}
			
			//save data
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
				};
				
				Model model_self= ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root);
				String sz_path = prepare_path(sz_url_pml,sz_context+".rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_save_data(model_self, f_output_rdf, ToolPml.IWV_NAMESPACE, null);
			}
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void run_improve_global() throws MalformedURLException{
		String sz_context ="_improve_global";
		for (String sz_url_pml: set_url_pml){
			///////////////////////////////////////////
			//generate graphics
			Resource res_info_root = m_hg.getRoot(sz_url_pml);
			Integer root = m_hg.getHyperNode(res_info_root);
			Set<Resource> set_step_original= this.m_hg.getSubHg(res_info_root);
			DataHyperGraph dhg = this.m_hg.getHyperGraph();

			m_hg.hg_set_weight(dhg,DataPmlHg.OPTION_WEIGHT_STEP);
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,root);
			

			//plot
			if (ToolSafe.isEmpty(hgt.getSolutions())){
				System.exit(-1);
			}

			DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);
			Set<Resource> set_step_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);
			{
				String sz_path = prepare_path(sz_url_pml,sz_context);
				File f_output_graph = new File(dir_root_output, sz_path);
	
				String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_step_optimal, set_step_original);
				DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
			}

			//save data
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
						this.m_hg.copy_without_loop(set_step_original),
				};
				
				Model model_self= ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root);
				String sz_path = prepare_path(sz_url_pml,sz_context+"_combine.rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_save_data(model_self, f_output_rdf, ToolPml.IWV_NAMESPACE, null);
			}
			
			//save data
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
				};
				
				Model model_self= ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root);
				String sz_path = prepare_path(sz_url_pml,sz_context+".rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_save_data(model_self, f_output_rdf, ToolPml.IWV_NAMESPACE, null);
			}

		}
	}
*/	
	public static final String CONTEXT_IMPROVE_GLOBAL = "_improve_global";
	public static final String CONTEXT_IMPROVE_SELF = "_improve_self";
	public static final String CONTEXT_IMPROVE_ROOT = "_improve_root";
	
	HashMap<DataSmartMap, DataHyperGraph> m_cache_problem_solution= new HashMap<DataSmartMap, DataHyperGraph>();
	private DataHyperGraph find_solution(String sz_context, int gid_root, int option_weight, String sz_url_pml, Set<Resource> set_step_original, AgentHyperGraphTraverse alg){
		DataSmartMap key = new DataSmartMap();
		key.put("context",sz_context);
		key.put("root_id",gid_root);
		key.put("opt_weight",option_weight);
		key.put("url_pml",sz_url_pml);
		key.put("alg",alg.getClass().getSimpleName());
		
		if (m_cache_problem_solution.keySet().contains(key)){
			return m_cache_problem_solution.get(key);
		}else{
			DataHyperGraph dhg_orginal = this.m_hg.getHyperGraph(set_step_original);
			
			DataHyperGraph dhg;
			if (CONTEXT_IMPROVE_GLOBAL.equals(sz_context))
				dhg= this.m_hg.getHyperGraph();
			else if (CONTEXT_IMPROVE_ROOT.equals(sz_context)){
				Set<Resource> set_step_keep_root = m_hg.getSubHgKeepRoot(sz_url_pml, gid_root);
				dhg= this.m_hg.getHyperGraph(set_step_keep_root);
			}else {
				dhg = dhg_orginal;
			}
			m_hg.hg_set_weight(dhg, option_weight);

			alg.traverse(dhg,gid_root);
			//plot

			DataHyperGraph dhg_optimal =null;
			int match_optimal = -1;
			Set<DataHyperEdge> edges_original = dhg_orginal.getEdges();
			for (DataHyperGraph dgh_candidate: alg.getResultSolutions()){
				Set<DataHyperEdge> edges_optimal = new HashSet<DataHyperEdge> (dgh_candidate.getEdges());
				edges_optimal.retainAll(edges_original);
				int match = edges_optimal.size();
				
				if (match_optimal == -1 || match_optimal<match){
					dhg_optimal = dgh_candidate;
					match_optimal = match;
				}
			}
			
			if (ToolSafe.isEmpty(dhg_optimal)){
				getLogger().info("empty result graphs");
				return new DataHyperGraph();
			}

			m_cache_problem_solution.put(key, dhg_optimal);
			return dhg_optimal;
	
		}
		
	}
	
	private Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public void run_improve(String sz_context, int option_weight) throws MalformedURLException{
		
		for (String sz_url_pml: set_url_pml){
			AgentHyperGraphTraverse[] ary_alg = new AgentHyperGraphTraverse[]{
					new AgentHyperGraphOptimize(),
					new AgentHyperGraphAoStar(),
			};
			for (AgentHyperGraphTraverse alg: ary_alg){
				///////////////////////////////////////////
				//generate graphics
				Resource res_info_root = m_hg.getRoot(sz_url_pml);
				Integer gid_root = m_hg.getHyperNode(res_info_root);
				Set<Resource> set_step_original= m_hg.getSubHg(res_info_root);
				
				DataHyperGraph dhg_optimal = find_solution( sz_context, gid_root,option_weight,sz_url_pml,set_step_original, alg);

				Set<Resource> set_step_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);

				//save log
				{
					getLogger().info("create stat_diff  improve ...");

					String filename = "stat_diff_improve.csv";
					//String sz_path = prepare_path(sz_url_problem,null)+ filename;
					File f_output = new File(dir_root_output, filename);

					DataSmartMap data= m_hg.stat(set_step_optimal,set_step_original, true);
					
					data.put("solution",sz_url_pml);
					DataPmlHg.stat_url_param(sz_url_pml,"s", data);
					data.put("s_context", sz_context);
					data.put("s_alg", alg.getLabel());
					data.put("s_option_weigth", option_weight);
					
					data.copy(alg.getResultSummaryData());

					//make it shared for "pair" option
					if (dir_root_output.getAbsolutePath().indexOf("pair")>0){
						f_output = new File(dir_root_output.getParentFile(), filename);
						data.put("s_path", dir_root_output.getName());
					}
					
					
					String ret = DataPmlHg.print_csv(data, !f_output.exists());

					getLogger().info("write to "+ f_output.getAbsolutePath());
					//getLogger().info(ret);
					try {
						ToolIO.pipeStringToFile(ret, f_output, false, true);
					} catch (Sw4jException e) {
						e.printStackTrace();
					}
				}
				
				if (dhg_optimal.isEmpty()){
					continue;
				}
				
				//plot unless required
				if (gbPlotDot )
				{
					String sz_path = prepare_path(sz_url_pml,sz_context);
					File f_output_graph = new File(dir_root_output, sz_path);
		
					String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_step_optimal, set_step_original);
					DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath()+"_"+alg.getLabel());
				}

				//save data (optimal solution)
				{

					Set<Resource>[] ary_set_step_all= new Set[]{
							set_step_optimal,
					};
					
					String sz_path = prepare_path(sz_url_pml,sz_context+"_"+alg.getLabel()+".rdf");
					File f_output_rdf= new File(dir_root_output, sz_path);

					ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root,f_output_rdf);

				}

				//save data (optimal solution + original solution)
				{

					Set<Resource>[] ary_set_step_all= new Set[]{
							set_step_optimal,
							this.m_hg.copy_without_loop(set_step_original),
					};
					
					String sz_path = prepare_path(sz_url_pml,sz_context+"_"+alg.getLabel()+"_alt.rdf");
					File f_output_rdf= new File(dir_root_output, sz_path);

					ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root,f_output_rdf);
				}
				
			}
			
			System.gc();
			System.out.println( ToolString.formatXMLDateTime()+" free memory: "+Runtime.getRuntime().freeMemory());

		}
	}
}
