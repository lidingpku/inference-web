package org.inference_web.iwapp.tptp;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.inference_web.pml.DataPmlHg;
import org.inference_web.pml.ToolPml;

import com.hp.hpl.jena.rdf.model.Resource;
import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataSmartMap;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;

public class TaskIwTptpImprove extends AgentIwTptp {
	public static void main(String[] argv){
		//run_test();
		run_improve_pair();
		run_improve_version_EP();
		//run_improve_version_Metis();
		run_improve();
	}
	
	public static void run_improve(){
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions/PUZ";
		File dir_root_output = new File("www/test/tptp-iw/all");
		String sz_url_root_output = "http://inference-web.org/test/tptp-iw/all";
		
		TaskIwTptpImprove tpn = new TaskIwTptpImprove();
		tpn.init(sz_url_seed, sz_url_root_input, dir_root_output, sz_url_root_output);
		//filter sz_url_pml
		tpn.filter_url_pml_base();
		tpn.run();
	}

	public static void run_improve_pair(){
		Set<String> reasoners1 = new TreeSet<String>();
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
		
		reasoners1.add("EP---1.1");
		//reasoners1.add("Ayane---1.1");
		
		for (String reasoner1: reasoners1){
			for (String reasoner2: reasoners2){
				if (reasoner1.equals(reasoner2))
					continue;
				
				//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
				String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
				String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions/PUZ";
				
				String dir_name= reasoner1.substring(0,reasoner1.indexOf("---"))+"-"+reasoner2.substring(0,reasoner2.indexOf("---"));
				
				File dir_root_output = new File("www/test/tptp-iw/"+dir_name);
				String sz_url_root_output = "http://inference-web.org/test/tptp-iw/"+dir_name;
				Set<String> set_url_pml = new HashSet<String>();
				set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/"+reasoner1+"/answer.owl"); 
				set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/"+reasoner2+"/answer.owl"); 
				
				TaskIwTptpImprove tpn = new TaskIwTptpImprove();
				tpn.init(sz_url_seed, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
				tpn.run();
				
			}			
		}
	}

	public static void run_improve_version_EP(){
		Set<String> reasoners2 = new TreeSet<String>();
		reasoners2.add("EP---0.999");	
		reasoners2.add("EP---1.0");
		reasoners2.add("EP---1.1");
		reasoners2.add("EP---1.1pre");

		//reasoners2.add("Metis---2.1");
		//reasoners2.add("Metis---2.2");
		
		
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions/PUZ";
		String dir_name= "EP";
		File dir_root_output = new File("www/test/tptp-iw/"+dir_name);
		String sz_url_root_output = "http://inference-web.org/test/tptp-iw/"+dir_name;
		Set<String> set_url_pml = new HashSet<String>();
		for (String reasoner2: reasoners2){
			set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/"+reasoner2+"/answer.owl"); 
		}
		TaskIwTptpImprove tpn = new TaskIwTptpImprove();
		tpn.init(sz_url_seed, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
		tpn.run();						
	}
	
	public static void run_improve_version_Metis(){
		Set<String> reasoners2 = new TreeSet<String>();
		reasoners2.add("Metis---2.1");
		reasoners2.add("Metis---2.2");
		
		
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions/PUZ";
		String dir_name= "Metis";
		File dir_root_output = new File("www/test/tptp-iw/"+dir_name);
		String sz_url_root_output = "http://inference-web.org/test/tptp-iw/"+dir_name;
		Set<String> set_url_pml = new HashSet<String>();
		for (String reasoner2: reasoners2){
			set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/"+reasoner2+"/answer.owl"); 
		}
		TaskIwTptpImprove tpn = new TaskIwTptpImprove();
		tpn.init(sz_url_seed, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
		tpn.run();						
	}
	public static void run_test(){
		//String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
		File dir_root_output = new File("www/test/tptp-iw");
		String sz_url_root_output = "http://inference-web.org/test/tptp-iw";
		Set<String> set_url_pml = new HashSet<String>();
		set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1/answer.owl"); 
//		set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Otter---3.3/answer.owl"); 
		set_url_pml.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Faust---1.0/answer.owl");
		
		TaskIwTptpImprove tpn = new TaskIwTptpImprove();
		tpn.init(sz_url_seed, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
		tpn.run();
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
		String sz_context= "_original";
		for (String sz_url_pml: set_url_pml){
		
			//plot it
			String sz_path = prepare_path(sz_url_pml, sz_context);
			File f_output_graph = new File(dir_root_output, sz_path);

			Resource res_root = m_hg.getRoot(sz_url_pml);
			String sz_dot = m_hg.graphviz_export_dot(m_hg.getSubHg(res_root));
			DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());			
		}
	}
	
	public void run_combine() throws MalformedURLException{
		run_create_mappings(false);
		
		//DataHyperGraph dhg = this.m_hg.getHyperGraph();
		String sz_path = prepare_path(sz_url_seed,null)+"combine";
		File f_output_graph = new File(dir_root_output, sz_path);

		//merge node now
		m_hg.getHyperGraph();
		
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
	private DataHyperGraph find_solution(String sz_context, int gid_root, int option_weight, String sz_url_pml, Set<Resource> set_step_original){
		DataSmartMap key = new DataSmartMap();
		key.put("context",sz_context);
		key.put("root_id",gid_root);
		key.put("opt_weight",option_weight);
		key.put("url_pml",sz_url_pml);
		
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

			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,gid_root);

			//plot

			DataHyperGraph dhg_optimal =null;
			int match_optimal = -1;
			Set<DataHyperEdge> edges_original = dhg_orginal.getEdges();
			for (DataHyperGraph dgh_candidate: hgt.getSolutions()){
				Set<DataHyperEdge> edges_optimal = new HashSet<DataHyperEdge> (dgh_candidate.getEdges());
				edges_optimal.retainAll(edges_original);
				int match = edges_optimal.size();
				
				if (match_optimal == -1 || match_optimal<match){
					dhg_optimal = dgh_candidate;
					match_optimal = match;
				}
			}
			
			if (ToolSafe.isEmpty(dhg_optimal)){
				System.exit(-1);
			}

			m_cache_problem_solution.put(key, dhg_optimal);
			return dhg_optimal;
	
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void run_improve(String sz_context, int option_weight) throws MalformedURLException{
		for (String sz_url_pml: set_url_pml){
			///////////////////////////////////////////
			//generate graphics
			Resource res_info_root = m_hg.getRoot(sz_url_pml);
			Integer gid_root = m_hg.getHyperNode(res_info_root);
			Set<Resource> set_step_original= m_hg.getSubHg(res_info_root);
			
			DataHyperGraph dhg_optimal = find_solution( sz_context, gid_root,option_weight,sz_url_pml,set_step_original );
			if (null==dhg_optimal){
				System.exit(-1);
			}
			
			Set<Resource> set_step_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);
			{
				String sz_path = prepare_path(sz_url_pml,sz_context);
				File f_output_graph = new File(dir_root_output, sz_path);
	
				String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_step_optimal, set_step_original);
				DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
			}

			//save data (optimal solution)
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
				};
				
				String sz_path = prepare_path(sz_url_pml,sz_context+".rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root,f_output_rdf);

			}

			//save data (optimal solution + original solution)
			{

				Set<Resource>[] ary_set_step_all= new Set[]{
						set_step_optimal,
						this.m_hg.copy_without_loop(set_step_original),
				};
				
				String sz_path = prepare_path(sz_url_pml,sz_context+"_alt.rdf");
				File f_output_rdf= new File(dir_root_output, sz_path);

				ToolPml.pml_create_by_copy(ary_set_step_all, this.m_hg.getModelAll(), this.m_hg.getInfoMap(),res_info_root,f_output_rdf);
			}
			
			System.gc();
			System.out.println( ToolString.formatXMLDateTime()+" free memory: "+Runtime.getRuntime().freeMemory());

		}
	}
}
