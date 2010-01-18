package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.inference_web.pml.AgentPmlCrawler;
import org.inference_web.pml.ToolPml;

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.util.ToolIO;

import com.hp.hpl.jena.rdf.model.Model;

public class AgentIwTptp {

	protected File dir_root_output = null;
	protected Set<String> set_url_pml = null;
	protected String sz_url_root_output= null;
	protected String sz_url_seed = null;
	protected String sz_url_root_input = null;


	public void init(String sz_url_seed, String sz_url_root_input, Set<String> set_url_pml, File dir_root_output, String sz_url_root_output){
		this.sz_url_seed =sz_url_seed; 
		this.sz_url_root_input =sz_url_root_input; 
		this.dir_root_output =dir_root_output; 
		this.sz_url_root_output =sz_url_root_output; 
		this.set_url_pml =set_url_pml; 
	}

	public void init(String sz_url_seed,  String sz_url_root_input, File dir_root_output, String sz_url_root_output, boolean bValidate){
		set_url_pml = AgentPmlCrawler.crawl_quick(sz_url_seed, bValidate);
		this.init(sz_url_seed, sz_url_root_input, set_url_pml, dir_root_output, sz_url_root_output);
	}


	protected String prepare_path(String sz_url, String sz_ext){
		String sz_path= sz_url.substring(sz_url_root_input.length());
		
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
	
	public void filter_url_answer_only(){
		//only keep those ends with answe
		Iterator<String> iter = this.set_url_pml.iterator();
		while (iter.hasNext()){
			String url = iter.next();
			if (!url.endsWith("answer.owl"))
				iter.remove();
		}
	}
	
	public void filter_url_pml_base(){
		HashSet<String> set_engine = new HashSet<String>();
		set_engine.add("Ayane---1.1");
		set_engine.add("EP---1.1");
		set_engine.add("Faust---1.0");
		set_engine.add("Metis---2.2");
		set_engine.add("Otter---3.3");
		set_engine.add("SNARK---20080805r005");
		set_engine.add("SOS---2.0");
		set_engine.add("Vampire---9.0");
		Iterator<String> iter = set_url_pml.iterator();
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
	}
	
	DataPmlHgTptp m_hg = new DataPmlHgTptp();

	
	public void run_create_mappings(boolean bSave){
		//HashSet<Resource> set_res_info_skip = new HashSet<Resource>();
		//TODO to skip or not skip, it is a question

		//for (Model m: m_hg.getModels()){
		//	set_res_info_skip.addAll(ToolPml.list_roots(m));
		//}	

		Model model_mappings = ToolPml.create_mappings(m_hg.getModels());
		if (bSave){
			String filename = "mappings.owl";
			
			String sz_ns = sz_url_seed +filename+"#";
			Model model_allsame = ToolJena.create_allsame(model_mappings, sz_ns); 
			
			String sz_path = prepare_path(sz_url_seed,null)+ filename;
			File f_output_mappings = new File(dir_root_output, sz_path);
			
			ToolJena.update_copy(model_allsame, model_mappings);
			ToolJena.printModelToFile(model_allsame, RDFSYNTAX.RDFXML, f_output_mappings,false);
			
		}
		this.m_hg.add_mapping(model_mappings);
	}

	public void run_create_stats(){
		{
			String ret = m_hg.stat_all(sz_url_seed);
			String filename = "stat_all.csv";
			String sz_path = prepare_path(sz_url_seed,null)+ filename;
			File f_output = new File(dir_root_output, sz_path);
			
			System.out.println(ret);
			ToolIO.pipeStringToFile(ret, f_output);
		}
		
		{
			String ret = m_hg.stat_one(sz_url_seed);
			String filename = "stat_one.csv";
			String sz_path = prepare_path(sz_url_seed,null)+ filename;
			File f_output = new File(dir_root_output, sz_path);
			
			System.out.println(ret);
			ToolIO.pipeStringToFile(ret, f_output);
		}
		
		{
			String ret = m_hg.stat_diff(sz_url_seed);
			String filename = "stat_diff.csv";
			String sz_path = prepare_path(sz_url_seed,null)+ filename;
			File f_output = new File(dir_root_output, sz_path);
			
			System.out.println(ret);
			ToolIO.pipeStringToFile(ret, f_output);
		}
	}

	public void run_load_data(){
		for (String sz_url_pml: set_url_pml){
			System.out.println("loading..."+ sz_url_pml);
			m_hg.add_data(sz_url_pml, null);
		}	
	}

	public static Set<String> prepare_tptp_problems(){
		String sz_seed = "http://inference-web.org/proofs/tptp/Solutions/";
		AgentCrawler crawler = new AgentCrawler();
		crawler.init(sz_seed);
		crawler.m_max_crawl_depth=1;
		crawler.crawl();

		Iterator<String> iter = crawler.m_results.iterator();
		while (iter.hasNext()){
			String url = iter.next();
			if (url.length() <= sz_seed.length()+4){
				System.out.println("removed "+url);
				iter.remove();
			}
		}
		
		System.out.println(crawler.m_results.size());
		return crawler.m_results;
	}
	
	public static Set<String> prepare_tptp_solutions(String problem){
		AgentCrawler crawler = new AgentCrawler();
		crawler.init(problem);
		crawler.m_max_crawl_depth=1;
		crawler.crawl();

		Iterator<String> iter = crawler.m_results.iterator();
		while (iter.hasNext()){
			String url = iter.next();
			
			//filter non solutions
			if (!url.endsWith("answer.owl"))
				iter.remove();
		}
		
		System.out.println(crawler.m_results.size());
		return crawler.m_results;
	}
}
