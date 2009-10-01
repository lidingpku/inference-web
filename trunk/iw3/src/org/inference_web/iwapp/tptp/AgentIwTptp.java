package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.inference_web.pml.AgentPmlCrawler;
import org.inference_web.pml.ToolPml;

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;

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

	public void init(String sz_url_seed,  String sz_url_root_input, File dir_root_output, String sz_url_root_output){
		set_url_pml = AgentPmlCrawler.crawl_quick(sz_url_seed);
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
	
	
	public void filter_url_pml_base(){
		HashSet<String> set_engine = new HashSet<String>();
		set_engine.add("Ayane---1.1");
		set_engine.add("EP---1.1pre");
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
			String sz_path = prepare_path(sz_url_seed,null)+"mappings_i.owl";
			File f_output_mappings = new File(dir_root_output, sz_path);
			ToolJena.printModelToFile(model_mappings, RDFSYNTAX.RDFXML, f_output_mappings,false);
		}
		this.m_hg.add_mapping(model_mappings);
	}

	public void run_load_data(){
		for (String sz_url_pml: set_url_pml){
			System.out.println("loading..."+ sz_url_pml);
			m_hg.add_data(sz_url_pml);
		}	
	}

}
