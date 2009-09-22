package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;


public class ToolGenGraph {

	public static void main(String[] argv){
		ToolGenGraph tool = new ToolGenGraph();
		tool.init("http://inference-web.org/test/combine","/PUZ/PUZ001-1","/g2");
		tool.run_original();
		tool.run_combine_self();
		tool.run_combine_all();
	}

	TreeMap<String, Model> m_map_url_model = new TreeMap<String, Model>();
	Model m_model_mapping_i = null;
	//String m_url_mappings_i = null;
	String m_dir_output =null;

	public void init(String url_base,String path1, String path2){
		for(String dir_input: get_dirs()){
			//prepare url of pml
			String url_input = url_base+path1+path2+"/"+dir_input+"-answer.owl.rdf";

			//load data
			System.out.println("loading ..."+ url_input);
			Model m = DataHg.load_hg_data(url_input);

			m_map_url_model.put(url_input,m);
		}
		String url_mappings_i = url_base+path1+"/mapping_i.rdf";
		m_model_mapping_i = ModelFactory.createDefaultModel();
		m_model_mapping_i.read(url_mappings_i);
		m_dir_output = "files/tptp/combine"+ path1+path2;
	}


	/**
	 * draw graph for original data
	 */
	public void run_original(){
		System.out.println("run_original ");
		for(String url_pml: m_map_url_model.keySet()){
			Model m= m_map_url_model.get(url_pml);
			DataHg hg = new DataHg();
			hg.addHg(m, url_pml);

			String localname = get_filename(url_pml);
			DataHyperGraph dhg = hg.getHyperGraph();
			export_dot(dhg, hg, this.m_dir_output+"/"+localname+".original.dot");
		}

	}

	/**
	 * draw graph for self-improvement data
	 */
	public void run_combine_self(){
		System.out.println("run_combine_self");
		for(String url_pml: m_map_url_model.keySet()){
			Model m= m_map_url_model.get(url_pml);
			DataHg hg = new DataHg();
			hg.addHg(m, url_pml);
			hg.addMappings(this.m_model_mapping_i);

			DataHyperGraph dhg = hg.getHyperGraph();
			AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
			hgt.traverse(dhg,dhg.getRoots().iterator().next()); // use the first root		
			if (null!= hgt.getSolutions()){
				DataHyperGraph optimal_graph = hgt.getSolutions().get(0);

				String localname = get_filename(url_pml);
				export_dot(optimal_graph, hg, this.m_dir_output+"/"+localname+".self.dot");
			}
		}
	}

	private String get_filename(String url){
		int pos = url.lastIndexOf("/")+1;
		return url.substring(pos);
	}

	public void run_test(){
		HashSet<String> test = new HashSet<String>();
		test.add("http://inference-web.org/test/combine/PUZ/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		test.add("http://inference-web.org/test/combine/PUZ/PUZ001-1/g2/SOS---2.0-answer.owl.rdf");

		to_dot(test);
	}


	private static void export_dot(DataHyperGraph dhg, DataHg hg, String file_output){
		String content = DataHyperGraph.data_export_graphviz(dhg,null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* hello */");
		if (ToolSafe.isEmpty(file_output)){
			System.out.println(content);
		}else{
			try {
				ToolIO.pipeStringToFile(content, file_output, false);
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void run_combine_all(){
		System.out.println("run_combine all");
		DataHg hg = new DataHg();
		for(String url_pml: m_map_url_model.keySet()){
			Model m= m_map_url_model.get(url_pml);
			hg.addHg(m, url_pml);
		}
		hg.addMappings(this.m_model_mapping_i);

		//export combine
		{
			DataHyperGraph dhg = hg.getHyperGraph();
			export_dot(dhg, hg, this.m_dir_output+"/combine.dot");
		}
		
		// optimize 
		int [] options = new int[]{
				DataHg.OPTION_HG_WEIGHT_LEAF, 
				DataHg.OPTION_HG_WEIGHT_STEP
		};

		for (int option: options){
			DataHyperGraph dhg= hg.getHyperGraph(option);			

			for (int root : dhg.getRoots()){
				AgentHyperGraphOptimize hgt= new AgentHyperGraphOptimize();
				hgt.traverse(dhg,root);		
				if (null!= hgt.getSolutions()){
					DataHyperGraph optimal_graph = hgt.getSolutions().get(0);

					String url_pml=DataQname.extractNamespaceUrl(hg.m_map_res_vertex.getObjectsByGid(root).iterator().next().getURI());
					//url_pml=url_pml.substring(0,url_pml.length()-1);
					String localname = get_filename(url_pml);
					export_dot(optimal_graph, hg, this.m_dir_output+"/"+localname+".all.dot");
				}
			}
		}





	}


	public HashSet<String> get_dirs(){
		HashSet<String> dirs = new HashSet<String>();
	//	dirs.add( "Ayane---1.1");
		dirs.add( "EP---1.0");
	//	dirs.add( "Faust---1.0");
	//	dirs.add( "Metis---2.2");
	//	dirs.add( "Otter---3.3");
	//	dirs.add( "SNARK---20080805r005");
		dirs.add( "SOS---2.0");
	//	dirs.add( "Vampire---9.0");
		return dirs;
	}



	public static void to_dot(String url_input){
		//build hg
		DataHg hg = new DataHg();
		System.out.println(url_input);
		hg.addHg(url_input);

		DataHyperGraph dhg = hg.getHyperGraph();
		export_dot(dhg,hg, null);
	}

	public static void to_dot(Set<String> urls_input){
		//build hg
		DataHg hg = new DataHg();
		for(String url_input: urls_input){
			System.out.println(url_input);
			hg.addHg(url_input);
		}
		hg.addMappings("http://inference-web.org/test/combine/PUZ/PUZ001-1/mapping_i.rdf");
		DataHyperGraph dhg = hg.getHyperGraph();
		export_dot(dhg,hg, null);
	}

}
