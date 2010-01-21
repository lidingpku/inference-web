package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.inference_web.pml.DataPmlHg;
import org.inference_web.pml.ToolPml;

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class AgentIwTptp {

	protected File dir_root_output = null;
	protected Set<String> set_url_pml = null;
	protected String sz_url_root_output= null;
	protected String sz_url_problem = null;
	protected String sz_url_root_input = null;


	public void init(String sz_url_problem, String sz_url_root_input, Set<String> set_url_pml, File dir_root_output, String sz_url_root_output){
		this.sz_url_problem =sz_url_problem; 
		this.sz_url_root_input =sz_url_root_input; 
		this.dir_root_output =dir_root_output; 
		this.sz_url_root_output =sz_url_root_output; 
		this.set_url_pml =set_url_pml; 
	}

	public void init(String sz_url_problem,  String sz_url_root_input, File dir_root_output, String sz_url_root_output){
		//set_url_pml = AgentPmlCrawler.crawl_quick(sz_url_problem, bValidate);
		Set<String> url_pml = prepare_tptp_solutions(sz_url_problem);
		this.init(sz_url_problem, sz_url_root_input, url_pml, dir_root_output, sz_url_root_output);
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
	DataSmartMap m_hg_stat = new DataSmartMap();
	
	public void run_create_mappings(boolean bSave){
		//HashSet<Resource> set_res_info_skip = new HashSet<Resource>();
		//TODO to skip or not skip, it is a question

		//for (Model m: m_hg.getModels()){
		//	set_res_info_skip.addAll(ToolPml.list_roots(m));
		//}	
		
		DataPVHMap<String,Resource> eqmap = ToolPml.create_mappings(m_hg.getModels());
		
		getLogger().info("create mappings ... ");
		Model model_mappings = ToolJena.create_sameas(eqmap.values());
		
		
		if (bSave){
			getLogger().info("save mappings ...");

			String filename = "mappings.owl";
			String sz_ns = sz_url_problem +filename+"#";

			String sz_path = prepare_path(sz_url_problem,null)+ filename;
			File f_output_mappings = new File(dir_root_output, sz_path);
			
			Model model_allsame = ToolJena.create_allsame(eqmap.values(), sz_ns); 

			ToolJena.update_copy(model_allsame, model_mappings);
			ToolJena.printModelToFile(model_allsame, RDFSYNTAX.RDFXML, f_output_mappings,false);

			int count_shared =0;
			int sum_shared =0;
			int max_shared =0;
			for(Set<Resource> set_res: eqmap.values()){
				if (set_res.size()>1){
					count_shared++;
					sum_shared+=set_res.size();
					max_shared = Math.max(set_res.size(), max_shared);
				}
			}
			
			m_hg_stat.put("info[occurence]", eqmap.getValues().size());
			m_hg_stat.put("vertex[unique]", eqmap.keySet().size());
			m_hg_stat.put("vertex(shared by info)", count_shared);
			m_hg_stat.put("vertex(shared by info)[max]", max_shared);
			if (count_shared>0)
				m_hg_stat.put("vertex(shared by info)[avg]", 100*sum_shared/count_shared/100.0);
			else
				m_hg_stat.put("vertex(shared by info)[avg]", 0);
			m_hg_stat.put("triple(sameas)", model_mappings.size());
			
		}
		this.m_hg.add_mapping(model_mappings);
	}
	
	
	public void run_create_stats(){
		//skip linked proofs with over 10000 info occurrence
		int total_node= Integer.parseInt(m_hg_stat.getAsString("info[occurence]"));
		getLogger().info("processing run_create_stats: info[occurence]="+ total_node);
		boolean bStatGraph = (total_node <10000);
		
		{
			getLogger().info("create  stat_all.csv ...");

			String filename = "stat_all.csv";
			//String sz_path = prepare_path(sz_url_problem,null)+ filename;
			File f_output = new File(dir_root_output, filename);

			DataSmartMap data = m_hg.stat_all(sz_url_problem, bStatGraph);
			m_hg_stat.copy(data);

			String ret = DataPmlHg.print_csv(m_hg_stat, !f_output.exists());

			getLogger().info("write to "+ f_output.getAbsolutePath());
			//getLogger().info(ret);
			try {
				ToolIO.pipeStringToFile(ret, f_output, false, true);
			} catch (Sw4jException e) {
				e.printStackTrace();
			}
		}
		
		if (!bStatGraph){
			getLogger().info("skipped very large linked proofs");

			String filename = "skipped-problem-stat.csv";
			//String sz_path = prepare_path(sz_url_problem,null)+ filename;
			File f_output = new File(dir_root_output, filename);


			String ret = sz_url_problem+"\n";

			getLogger().info("write to "+ f_output.getAbsolutePath());
			//getLogger().info(ret);
			try {
				ToolIO.pipeStringToFile(ret, f_output, false, true);
			} catch (Sw4jException e) {
				e.printStackTrace();
			}

			return;
		}
		
		{
			getLogger().info("create  stat_one.csv ...");
			String ret = m_hg.stat_one(sz_url_problem, true);
			{
				String filename = "stat_one.csv";
				String sz_path = prepare_path(sz_url_problem,null)+ filename;
				File f_output = new File(dir_root_output, sz_path);
	
	
				getLogger().info("write to "+ f_output.getAbsolutePath());
				//getLogger().info(ret);
				ToolIO.pipeStringToFile(ret, f_output);
			}
			//write big file
			{
				String filename = "stat_one.csv";
				File f_output = new File(dir_root_output, filename);
	
				getLogger().info("write to "+ f_output.getAbsolutePath());
				//getLogger().info(ret);
				try {
					ToolIO.pipeStringToFile(ret, f_output, false, true);
				} catch (Sw4jException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		{
			getLogger().info("create  stat_diff.csv ...");
			String ret = m_hg.stat_diff(sz_url_problem);
			{
				String filename = "stat_diff.csv";
				String sz_path = prepare_path(sz_url_problem,null)+ filename;
				File f_output = new File(dir_root_output, sz_path);
				

				getLogger().info("write to "+ f_output.getAbsolutePath());
				//getLogger().info(ret);
				ToolIO.pipeStringToFile(ret, f_output);				
			}
			
			//write big file
			{
				String filename = "stat_diff.csv";
				File f_output = new File(dir_root_output, filename);
				

				getLogger().info("write to "+ f_output.getAbsolutePath());
				//getLogger().info(ret);
				try {
					ToolIO.pipeStringToFile(ret, f_output, false, true);
				} catch (Sw4jException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}

	public void run_load_data(){
		getLogger().info("run_load_data ...");
		for (String sz_url_pml: set_url_pml){
		//	getLogger().info("loading..."+ sz_url_pml);
			m_hg.add_data(sz_url_pml, null);
			
			//record skipped files
			if (m_hg.getModelOriginal(sz_url_pml)==null){
				String filename = "skipped_rdf.csv";
				//String sz_path = prepare_path(sz_url_problem,null)+ filename;
				File f_output = new File(dir_root_output, filename);
				
				String ret = sz_url_pml+"\n";

				getLogger().info("write to "+ f_output.getAbsolutePath());
				ToolIO.pipeStringToFile(ret, f_output);

			}
		}	
	}

	private static Logger getLogger(){
		return Logger.getLogger(AgentIwTptp.class);
	}
	
	public static Set<String> prepare_tptp_problems(String sz_seed){
		AgentCrawler crawler = new AgentCrawler();
		crawler.init(sz_seed);
		crawler.m_max_crawl_depth=1;
		crawler.crawl();

		Iterator<String> iter = crawler.m_results.iterator();
		while (iter.hasNext()){
			String url = iter.next();
			if (url.length() <= sz_seed.length()+4){
				//getLogger().info("removed "+url);
				iter.remove();
			}
		}
		
		getLogger().info(crawler.m_results.size());
		return crawler.m_results;
	}



	public static Set<String> prepare_tptp_one_step(String sz_seed){
		AgentCrawler crawler = new AgentCrawler();
		crawler.init(sz_seed);
		crawler.m_max_crawl_depth=0;
		crawler.crawl();

		Iterator<String> iter = crawler.m_results.iterator();
		while (iter.hasNext()){
			String url = iter.next();
			if (url.length() <= sz_seed.length()){
				getLogger().info("removed "+url);
				iter.remove();
			}
		}
		
		getLogger().info(crawler.m_results.size());
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
			if (!url.endsWith("answer.owl")){
				getLogger().info("removed "+url);
				iter.remove();
			}
		}
		
		getLogger().info(crawler.m_results.size());
		return crawler.m_results;
	}
	
	public static Set<String> getReasonersCurrent(){
		Set<String> reasoners = new TreeSet<String>();
		reasoners.add("Ayane---1.1");
		reasoners.add("Bliksem---1.12");
		reasoners.add("CiME---2.01");
		reasoners.add("Darwin---1.4.5");
		reasoners.add("DarwinFM---1.4.5");
		reasoners.add("DCTP---1.31");
		reasoners.add("E---1.1");
		reasoners.add("E-Darwin---1.2");
		reasoners.add("E-KRHyper---1.1.3");
		reasoners.add("EP---1.1");
		reasoners.add("EQP---0.9e");
		reasoners.add("Equinox---4.1");
		reasoners.add("Fampire---1.3");
		reasoners.add("FDP---0.9.16");
		reasoners.add("Fiesta---2");
		reasoners.add("Geo---2007f");
		reasoners.add("GrAnDe---1.1");
		reasoners.add("Imogen---2");
		reasoners.add("iProver---0.7");
		reasoners.add("IsabelleM---2009-1");
		reasoners.add("IsabelleN---2009-1");
		reasoners.add("IsabelleP---2009-1");
		reasoners.add("leanCoP---2.1");
		reasoners.add("LeanTAP---2.3");
		reasoners.add("LEO-II---1.1");
		reasoners.add("Mace2---2.2");
		reasoners.add("Mace4---908");
		reasoners.add("MANSEX---1");
		reasoners.add("Matita---1");
		reasoners.add("Metis---2.2");
		reasoners.add("Muscadet---3");
		reasoners.add("Otter---3.3");
		reasoners.add("Paradox---3");
		reasoners.add("PKRHyper---805");
		reasoners.add("Prover9---908");
		reasoners.add("S-SETHEO---0");
		reasoners.add("Satallax---1.1");
		reasoners.add("SETHEO---3.3");
		reasoners.add("SInE---0.4");
		reasoners.add("SNARK---20080805r018b");
		reasoners.add("SNARKA---20080805r018b");
		reasoners.add("SOS---2");
		reasoners.add("SPASS---3.5");
		reasoners.add("SPASS-XDB---3.01X0.3");
		reasoners.add("SRASS---0.1");
		reasoners.add("Theo---2006");
		reasoners.add("ToFoF---0.1");
		reasoners.add("TPS---3.080227G1d");
		reasoners.add("Vampire---11");
		reasoners.add("Vampire---9");
		reasoners.add("Vampire---SUMO");
		reasoners.add("Waldmeister---C09a");
		reasoners.add("zChaff---04.11.15");
		reasoners.add("Zenon---0.5.0");

		return reasoners;
	}
	
	public static String extractReasoner(String sz_url_pml){
		for (String component : sz_url_pml.split("/")){
			if (component.contains("---"))
				return component;
		}
		return "";
	}
	
	public static void filterSolution(Set<String> solutions, Set<String> reasoners){
		Iterator<String> iter = solutions.iterator();
		while (iter.hasNext()){
			String sz_solution=iter.next();
			String reasoner = extractReasoner(sz_solution);
			if (!ToolSafe.isEmpty(reasoners)&&!reasoners.contains(reasoner))
				iter.remove();
		}
	}
}

