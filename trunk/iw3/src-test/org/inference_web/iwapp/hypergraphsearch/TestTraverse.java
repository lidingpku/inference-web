package org.inference_web.iwapp.hypergraphsearch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.inference_web.iwapp.hypergraph.DataHg;
import org.inference_web.iwapp.hypergraph.ToolHypergraphTraverse;

import sw4j.task.graph.DataHyperGraph;

public class TestTraverse {
	
	public static void main(String[] argv){
		TestTraverse traverse = new TestTraverse();
		traverse.test();

	}
	public void test(){
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf");
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		HashSet<String> test = new HashSet<String>();
		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/SOS---2.0-answer.owl.rdf");
		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Faust---1.0-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Metis---2.2-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Otter---3.3-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/SNARK---20080805r005-answer.owl.rdf");
//		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/Vampire---9.0-answer.owl.rdf");

		traverse(test);
	}

	public static void traverse(String url_input){

		HashSet<String> urls_input= new HashSet<String>();
		urls_input.add(url_input);
		traverse(urls_input);
	}
	
	public static void traverse(Set<String> urls_input){
		//build hg
		DataHg hg = new DataHg();
		for(String url_input: urls_input){
			System.out.println(url_input);
			hg.addHg(url_input);
		}
		hg.addMappings("http://inference-web.org/test/combine/PUZ001-1/mapping_i_pre.rdf");
	
		DataHyperGraph dhg= hg.getHyperGraph(DataHg.OPTION_HG_WEIGHT_LEAF);				
		
		ToolHypergraphTraverse hgt= new ToolHypergraphTraverse();
	
		Iterator<Integer> iter= dhg.getRoots().iterator();
		
		DataHyperGraph optimal_graph= new DataHyperGraph();
		Integer optimal_graph_cost= 10000;
		
		while(iter.hasNext()) {
			int root= iter.next();
			hgt.traverse(dhg,root);		
			if (null!= hgt.getSolutions()){
				System.out.println("root is " + root);
				Iterator<DataHyperGraph> iter_graph= hgt.getSolutions().iterator();
				while(iter_graph.hasNext()){
					DataHyperGraph graph= iter_graph.next();
					if (graph.getCost() < optimal_graph_cost) {
						optimal_graph_cost = graph.getCost();
						optimal_graph= graph;
					}
				}
			}
			System.out.println("optimal graph is " + optimal_graph.data_export());
			System.out.println("total cost is " + optimal_graph_cost);
			System.out.println(optimal_graph.data_export_graphviz(null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* more */"));
		}
	
	}
}

