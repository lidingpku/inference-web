package org.inference_web.iwapp.tptp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.inference_web.pml.AgentPmlCrawler;
import org.inference_web.pml.DataPmlHg;
import org.inference_web.pml.ToolPml;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.ToolSafe;

public class AgentPmlTptp {
	
	String sz_url_seed = null;
	File dir_root_output = null;
	String sz_url_root_output= null;
	Set<String> set_url_pml = null;
	public void init(String sz_url_seed, File dir_root_output, String sz_url_root_output){
		set_url_pml = AgentPmlCrawler.crawl_quick(sz_url_seed);
		this.init(sz_url_seed, set_url_pml, dir_root_output, sz_url_root_output);
	}
	
	public void init(String sz_url_seed, Set<String> set_url_pml, File dir_root_output, String sz_url_root_output){
		this.sz_url_seed =sz_url_seed; 
		this.dir_root_output =dir_root_output; 
		this.sz_url_root_output =sz_url_root_output; 
		this.set_url_pml =set_url_pml; 
	}
	
	DataPmlHgTptp m_hg = new DataPmlHgTptp();
	
	private String prepare_path(String sz_url, String sz_ext)throws MalformedURLException{
		URL url;
		url = new URL(sz_url);
		String sz_path = url.getPath();
		if (null==sz_ext)
			return sz_path;
		else{
			int pos= sz_path.lastIndexOf(".");
			if (pos>0)
				return sz_path.substring(0, pos )+sz_ext;
			else
				return sz_path;
		}
	}
	
	public void run(){
		try {
			run_load();
		//	run_normalize();
			run_combine();
			run_plot_original();
			run_improve_self();
			run_improve_global();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void run_load(){
		
		for (String sz_url_pml: set_url_pml){
			m_hg.add_data(sz_url_pml);
			
		}	
	}
	public void run_normalize() throws MalformedURLException{


		for (String sz_url_pml: m_hg.getContext()){
			Model m = m_hg.getModelOriginal(sz_url_pml);

			/////////////////////////////////////////
			// sign blank-node and write
			String sz_path = prepare_path(sz_url_pml,null);

			String sz_namespace_pml_output = sz_url_root_output + sz_path+"#";
			File f_output_pml = new File(dir_root_output, sz_path);

			Model model_norm = ToolPml.pml_sign(m, sz_namespace_pml_output);
			//ToolPml.pml_index(model_norm);

			ToolJena.printModelToFile(model_norm, f_output_pml.getAbsolutePath(), RDFSYNTAX.RDFXML,false);
		}
		
	}

	public void run_plot_original() throws MalformedURLException{
		String sz_context = "_original";
		for (String sz_url_pml: set_url_pml){
		
			//plot it
			String sz_path = prepare_path(sz_url_pml, sz_context);
			File f_output_graph = new File(dir_root_output, sz_path);

			Resource res_root = m_hg.getRoot(sz_url_pml);
			String sz_dot = m_hg.graphviz_export_dot(this.m_hg.getSubHg(res_root));
			DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());			
		}
	}
	
	public void run_combine() throws MalformedURLException{
		/////////////////////////////////////////
		// generate mapping file
		{
			String sz_path = prepare_path(sz_url_seed,null)+"mappings_i.owl";
			File f_output_mappings = new File(dir_root_output, sz_path);
	
			Model model_mappings = m_hg.create_mappings();
			ToolJena.printModelToFile(model_mappings, f_output_mappings.getAbsolutePath(), RDFSYNTAX.RDFXML, false);
	
			this.m_hg.add_mapping(model_mappings);
		}
		
		//DataHyperGraph dhg = this.m_hg.getHyperGraph();
		String sz_path = prepare_path(sz_url_seed,null)+"combine";
		File f_output_graph = new File(dir_root_output, sz_path);

	
		String sz_dot = m_hg.graphviz_export_dot(m_hg.getSubHg());
		DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
	}
	
	
	public void run_improve_self() throws MalformedURLException{
		String sz_context ="_self";
		for (String sz_url_pml: set_url_pml){
			///////////////////////////////////////////
			//generate graphics
			Resource res_info_root = m_hg.getRoot(sz_url_pml);
			Integer root = m_hg.getHyperNode(res_info_root);
			Set<Resource> set_steps_original= this.m_hg.getSubHg(res_info_root);
			DataHyperGraph dhg = this.m_hg.getHyperGraph(set_steps_original);

			m_hg.hg_set_weight(dhg,DataPmlHg.OPTION_WEIGHT_STEP);
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,root);
			
			//plot
			if (ToolSafe.isEmpty(hgt.getSolutions())){
				System.exit(-1);
			}
				
			DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);
			Set<Resource> set_steps_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);

			String sz_path = prepare_path(sz_url_pml,sz_context);
			File f_output_graph = new File(dir_root_output, sz_path);

			String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_steps_optimal, set_steps_original);
			DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
		}
	}
	
	public void run_improve_global() throws MalformedURLException{
		String sz_context ="_global";
		for (String sz_url_pml: set_url_pml){
			///////////////////////////////////////////
			//generate graphics
			Resource res_info_root = m_hg.getRoot(sz_url_pml);
			Integer root = m_hg.getHyperNode(res_info_root);
			Set<Resource> set_steps_original= this.m_hg.getSubHg(res_info_root);
			DataHyperGraph dhg = this.m_hg.getHyperGraph();

			m_hg.hg_set_weight(dhg,DataPmlHg.OPTION_WEIGHT_STEP);
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,root);
			
			//plot
			if (ToolSafe.isEmpty(hgt.getSolutions())){
				System.exit(-1);
			}
				
			DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);
			Set<Resource> set_steps_optimal= this.m_hg.getSubHg(dhg_optimal, res_info_root, sz_url_pml);

			String sz_path = prepare_path(sz_url_pml,sz_context);
			File f_output_graph = new File(dir_root_output, sz_path);

			String sz_dot  = this.m_hg.graphviz_export_dot_diff(set_steps_optimal, set_steps_original);
			DataPmlHg.graphviz_save(sz_dot, f_output_graph.getAbsolutePath());
		}
	}
	
}
