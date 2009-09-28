package temp.test;
/*
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.TreeMap;

import org.inference_web.iwapp.crawler.PmlCrawler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.DataHyperGraph;

*/
public class TaskPmlOptimize {
/*
	public static void main(String[] argv){
		task_self();
		task_all();
	}
	
	public static void task_all(){
		TaskPmlOptimize tool = new TaskPmlOptimize();
		File dir_base = new File("www/test/combine/");
		tool.init_all("http://inference-web.org/test/combine/",dir_base, get_dirs(),"PUZ/PUZ001-1/");
		tool.run_improve_all();		
	}

	public static void task_self(){
		TaskPmlOptimize tool = new TaskPmlOptimize();
		File dir_base = new File("www/test/combine/");
		tool.init_self("http://inference-web.org/test/combine/",dir_base, get_dirs(),"PUZ/PUZ001-1/");
		tool.run_improve_self();
	}
	
	
	TreeMap<String, Model> m_map_url_model = new TreeMap<String, Model>();
	Model m_model_mapping_i = null;
	//String m_url_mappings_i = null;
	File m_dir_output =null;

	public static HashSet<String> get_dirs(){
		HashSet<String> dirs = new HashSet<String>();
		dirs.add( "Ayane---1.1/");
		dirs.add( "EP---1.1pre/");
		dirs.add( "Faust---1.0/");
		dirs.add( "Metis---2.2/");
		dirs.add( "Otter---3.3/");
		dirs.add( "SNARK---20080805r005/");
		dirs.add( "SOS---2.0/");
		dirs.add( "Vampire---9.0/");
		return dirs;
	}

	private String get_filename(String url){
		int pos1 = url.lastIndexOf("/")-1;
		pos1 = url.lastIndexOf("/", pos1)+1;
		int pos2 = url.lastIndexOf(".");
		return url.substring(pos1, pos2);
	}


	public void init_all(String url_base, File dir_base, HashSet<String> dir_engines, String path1){
		for(String dir_input: dir_engines ){
			//prepare url of pml
			String sz_url_input = url_base+path1+dir_input+"answer_pmlr.rdf";

			//load data
			System.out.println("loading ..."+ sz_url_input);
			Model m = DataPmlHg.load_hg_data(sz_url_input);

			m_map_url_model.put(sz_url_input,m);
		}
		String url_mappings_i = url_base+path1+"mappings_i.owl";
		m_model_mapping_i = ModelFactory.createDefaultModel();
		m_model_mapping_i.read(url_mappings_i);
		m_dir_output = new File(dir_base, path1);
	}
	

	public void init_self(String url_base, File dir_base, HashSet<String> dir_engines, String path1){
		for(String dir_input: dir_engines ){
			//prepare url of pml
			String sz_url_seed_path = url_base+path1+dir_input;

			HashSet<String> set_url_seed = new HashSet<String>();
			set_url_seed.add(sz_url_seed_path);
			
			/////////////////////////////////////////
			// crawl all pages in one directory
			PmlCrawler crawler = new PmlCrawler();
			for (String url_seed : set_url_seed){
				crawler.crawl(url_seed,url_seed+".*", false);
			}
			
			for (String sz_url_pml: crawler.m_results){
				
				if (!sz_url_pml.endsWith("_pmlr.rdf"))
					continue;
				
				//load data
				System.out.println("loading ..."+ sz_url_pml);
				Model m = DataPmlHg.load_hg_data(sz_url_pml);
				
				m_map_url_model.put(sz_url_pml,m);
			}
		}
		String url_mappings_i = url_base+path1+"mappings_i.owl";
		m_model_mapping_i = ModelFactory.createDefaultModel();
		m_model_mapping_i.read(url_mappings_i);
		m_dir_output = new File(dir_base, path1);

	}

	public void run_improve_all(){
		String sz_context = "-all";
		System.out.println("run_improve:"+sz_context);

		DataPmlHg hg = new DataPmlHg();
		for(String sz_url_pml: m_map_url_model.keySet()){
			Model m= m_map_url_model.get(sz_url_pml);
			hg.addHg(m, sz_url_pml);
		}
		hg.addMappings(this.m_model_mapping_i);

		{
			//export combined
			DataHyperGraph dhg= hg.getHyperGraph();	
			File f_output_combined = new File(this.m_dir_output, "combined");
			ToolGraphviz.gen_dot(dhg, hg, f_output_combined.getAbsolutePath(),null);
	
			Model model_combined = hg.getPmlrModel(dhg);
			File f_output_combined_rdf = new File(this.m_dir_output, "combined-pmlr.rdf");
			ToolJena.printModelToFile(model_combined, f_output_combined_rdf.getAbsolutePath(), RDFSYNTAX.RDFXML,false);
		}
		
		// optimize 
		int [] options = new int[]{
	//			DataHg.OPTION_HG_WEIGHT_LEAF, 
				DataPmlHg.OPTION_HG_WEIGHT_STEP
		};

		for (int option: options){
			DataHyperGraph dhg= hg.getHyperGraph(option);			
			String szOption = DataPmlHg.getOptionString(option);


			for(String sz_url_pml: m_map_url_model.keySet()){
				DataHyperGraph dhg_origin = hg.getHyperGraph(sz_url_pml,option);
				int root = hg.getRootNode(sz_url_pml);

				AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
				hgt.traverse(dhg,root);		
				if (null!= hgt.getSolutions()){
					DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);

					DataHyperGraph dhg_all = new DataHyperGraph();
					dhg_all.add( dhg_origin );			
					dhg_all.add( dhg_optimal);			

					
					//String sz_url_pml=DataQname.extractNamespaceUrl(hg.m_map_res_vertex.getObjectsByGid(root).iterator().next().getURI());
					//sz_url_pml=sz_url_pml.substring(0,sz_url_pml.length()-1);
					String localname = get_filename(sz_url_pml);

					//save optimal model
					Model model_optimal = hg.getPmlrModel(dhg_optimal);
					File f_output_optimal_rdf = new File(this.m_dir_output, localname+sz_context+"-"+szOption+"-pmlr.rdf");
					ToolJena.printModelToFile(model_optimal, f_output_optimal_rdf.getAbsolutePath());
					
					//save graph

					File f_output_optimal = new File(this.m_dir_output, localname+sz_context+"-"+szOption);
					ToolGraphviz.export_dot2(dhg_optimal, dhg_all, hg, f_output_optimal.getAbsolutePath(),sz_url_pml);
				}
			}
		}
	}


	public void run_improve_self(){
		String sz_context = "-self";
		System.out.println("run_improve:"+sz_context);
		for(String sz_url_pml: m_map_url_model.keySet()){
			Model m= m_map_url_model.get(sz_url_pml);
			DataPmlHg hg = new DataPmlHg();
			hg.addHg(m, sz_url_pml);
			hg.addMappings(this.m_model_mapping_i);

			DataHyperGraph dhg = hg.getHyperGraph();
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();

			Integer root = hg.getRootNode(sz_url_pml);
			if (null==root)
				continue;
			
			hgt.traverse(dhg, root); // use the first root		
			if (null!= hgt.getSolutions()){
				DataHyperGraph dhg_optimal = hgt.getSolutions().get(0);
				DataHyperGraph dhg_all = dhg;
				
					
				String localname = get_filename(sz_url_pml);
				
				//save optimal model
				Model model_optimal = hg.getPmlrModel(dhg_optimal);
				File f_output_optimal_rdf = new File(this.m_dir_output, localname+sz_context+"-pmlr.rdf");
				ToolJena.printModelToFile(model_optimal, f_output_optimal_rdf.getAbsolutePath());
				
				//save graph
				File f_output_optimal = new File(this.m_dir_output, localname+sz_context);
				ToolGraphviz.export_dot2(dhg_optimal, dhg_all, hg, f_output_optimal.getAbsolutePath(),sz_url_pml);
			}
		}
	}
*/
}
