package org.inference_web.iwapp.hypergraph;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

public class ToolGraphviz {
	
	public static void export_dot(DataHyperGraph dhg, DataHg hg, String file_output, String sz_context){

		String sz_content = DataHyperGraph.data_export_graphviz(dhg,null, hg.getMapNodeParams(dhg),hg.getMapEdgeParams(sz_context),"/* hello */");
		
		save_graph(sz_content, file_output);
	}

	public static void export_dot2(DataHyperGraph dhg, DataHyperGraph dhg_all, DataHg hg, String file_output, String sz_context){
		String title= String.format("The optimal proof (%d nodes) reduced %d nodes from original proof at %s",
				 dhg.getEdges().size(),
				 dhg_all.getEdges().size()-dhg.getEdges().size(),
				 sz_context);
		System.out.println(title);

		String subgraph="";
		Set<Integer> nodes = dhg.getNodes(); 
		for (Integer node: nodes){
			subgraph += "\"x_"+node+"\";\n";
		}
		for (DataHyperEdge edge: dhg_all.getEdges()){
			if (dhg.getEdges().contains(edge)){
			//if (nodes.containsAll(edge.getInputs()) && nodes.contains(edge.getOutput())){
				subgraph += "\""+edge.getID()+"\";\n";		
			}
		}
		subgraph = "subgraph cluster_opt { label= \""+title+"\"\n"+subgraph+"}\n";
			
		String sz_content = DataHyperGraph.data_export_graphviz(dhg_all,null, hg.getMapNodeParams(dhg_all),hg.getMapEdgeParams(sz_context), subgraph);
		
		save_graph(sz_content, file_output);
	}
	
	public static void save_graph(String sz_content, String file_output){
		if (ToolSafe.isEmpty(file_output)){
			System.out.println(sz_content);
		}else{
			try {
				File f_dot = new File(file_output+".dot");

				ToolIO.pipeStringToFile(sz_content, f_dot.getAbsolutePath(), false);

				String [] formats = new String[]{"svg","png"};
				for (String format :formats){
					File f_format = new File(file_output+"."+format);
					String command = "dot  -T"+format+" -o"+ f_format.getAbsolutePath() +" "+ f_dot.getAbsolutePath() ;
					System.out.println("run command: "+command);
					Runtime.getRuntime().exec(command);
				}
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
