package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.Set;

import sw4j.task.graph.DataHyperGraph;


public class ToolGenGraph {
	
	public static void main(String[] argv){
		ToolGenGraph tool = new ToolGenGraph();
		tool.run("http://inference-web.org/test/combine/PUZ/PUZ001-1/g2");

	}
	public void test(){
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf");
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		HashSet<String> test = new HashSet<String>();
		test.add("http://inference-web.org/test/combine/PUZ/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		test.add("http://inference-web.org/test/combine/PUZ/PUZ001-1/g2/SOS---2.0-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Faust---1.0-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Metis---2.2-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Otter---3.3-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/SNARK---20080805r005-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Vampire---9.0-answer.owl.rdf");

	//	test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Ayane---1.1-answer.owl.rdf");
		//test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/EP---1.0-answer.owl.rdf");
		
//		test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g4/combined-EP-SOS-answer.owl.rdf");
		to_dot(test);
	}
	
	public void run(String url_base){
		// plot original graph
		//build hg
		DataHg hg = new DataHg();
		for(String dir_input: get_dirs()){
			String url_input = url_base+"/"+dir_input+"-answer.owl.rdf";
			System.out.println("loading ..."+ url_input);
			
			hg.addHg(url_input);
		}
		hg.addMappings(url_base+"/mapping_i.rdf");


		
		//TODO: call api to display original graph
		//data_export_graphviz(null, hg.getMapNodeParams(), hg.getMapEdgeParams(),"/* more */");
		
		
		// optimize 
		int [] options = new int[]{
				DataHg.OPTION_HG_WEIGHT_LEAF, 
				DataHg.OPTION_HG_WEIGHT_STEP
		};
		
		for (int option: options){
			DataHyperGraph dhg= hg.getHyperGraph(option);			
			
			for (int root : dhg.getRoots()){
				ToolHypergraphTraverse hgt= new ToolHypergraphTraverse();
				hgt.traverse(dhg,root);		
				if (null!= hgt.getSolutions()){
//					System.out.println("root is " + root);
					int optimal_graph_cost = 10000;
					DataHyperGraph optimal_graph= null;

					for (DataHyperGraph graph :  hgt.getSolutions()){
						int cost =graph.getCost();
						if (null==optimal_graph || cost < optimal_graph_cost){
							optimal_graph =graph;
							optimal_graph_cost = cost;
						}					
					}
					
					System.out.println(optimal_graph.data_export());
					//TODO: call api
					//System.out.println("optimal graph is " + optimal_graph.data_export());
//					System.out.println("total cost is " + optimal_graph_cost);
//					System.out.println(optimal_graph.data_export_graphviz(null, hg.getMapNodeParams(), hg.getMapEdgeParams(),"/* more */"));
				}
			}
		}
		
		
	
	
		
	}
	
	
	 public HashSet<String> get_dirs(){
		HashSet<String> dirs = new HashSet<String>();
		dirs.add( "Ayane---1.1");
		dirs.add( "EP---1.0");
		dirs.add( "Faust---1.0");
		dirs.add( "Metis---2.2");
		dirs.add( "Otter---3.3");
		dirs.add( "SNARK---20080805r005");
		dirs.add( "SOS---2.0");
		dirs.add( "Vampire---9.0");
		return dirs;
	}
	


	public static void to_dot(String url_input){
		//build hg
		DataHg hg = new DataHg();
		System.out.println(url_input);
		hg.addHg(url_input);
		
		System.out.println(hg.getHyperGraph(DataHg.OPTION_HG_WEIGHT_LEAF).data_export_graphviz(null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* hello */"));
	}
	
	public static void to_dot(Set<String> urls_input){
		//build hg
		DataHg hg = new DataHg();
		for(String url_input: urls_input){
			System.out.println(url_input);
			hg.addHg(url_input);
		}
		hg.addMappings("http://inference-web.org/test/combine/PUZ/PUZ001-1/mapping_i.rdf");
		System.out.println(hg.getHyperGraph(DataHg.OPTION_HG_WEIGHT_LEAF).data_export_graphviz(null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* hello */"));

	}
}
