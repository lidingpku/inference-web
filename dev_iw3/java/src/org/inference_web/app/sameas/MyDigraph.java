package org.inference_web.app.sameas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;


public class MyDigraph<T> {
	
	ArrayList <MyDigraphArc<T>> arcs = new ArrayList <MyDigraphArc<T>>();
	HashMap<T, Properties> map_vertice_params = new HashMap<T,Properties>();
	HashSet<T> vertices = new HashSet<T>();
	
	public void add (MyDigraphArc<T> arc){
		arcs.add(arc);
		vertices.add(arc.from);
		vertices.add(arc.to);
	}
	
	public void addNodeParam(T node, String key, String value){
		if (null==value)
			return;
		
		Properties params = map_vertice_params.get(node);
		if (null==params){
			params = new Properties();
			 map_vertice_params.put(node, params);
		}
		params.put(key, value);
	}
	
	public HashSet<T> getVertices(){
		return vertices;
	}

	public ArrayList<MyDigraphArc<T>> getArcs(){
		return arcs;
	}
	
	public String getNodeLabel(T node){
		return node.toString();
	}

	public String getParams(T node){
		Properties params = map_vertice_params.get(node);
		String ret ="";
		if (null!=params){
			for (Object key : params.keySet()){
				Object value = params.get(key);
				ret += String.format("%s=\"%s\"",key, value);
			}
		}
		return ret;
	}

	public String getParams( MyDigraphArc<T> arc){
		return "";
	}

	public MyDigraphArc<T>  add(T from, T to){
		MyDigraphArc<T> arc = new MyDigraphArc<T> ();
		arc.from = from;
		arc.to = to;
		add(arc);
		return arc;
	}

	public String data_export_graphviz_subgraph(String sz_more, HashMap<T,String> map_node_context){
		String sz_content ="";

		//node
		HashMap<String,String> map_context_cluster = new HashMap<String,String>();
		for (T node: this.getVertices()){
			String sz_temp =String.format("\"%s\" [ %s ];\n",getNodeLabel(node),getParams(node));
			
			if (null==map_node_context ||map_node_context.size()==0){
				sz_content += sz_temp;
			}else{
				String context = map_node_context.get(node);
				if (null==context){
					sz_content +=sz_temp;
				}else{
					String sz_content_context = map_context_cluster.get(context);
					if (null==sz_content_context){
						sz_content_context = sz_temp; 
					}else{
						sz_content_context += sz_temp; 						
					}
					map_context_cluster.put(context, sz_content_context);
				}
			}

		}
		
		for (String context : map_context_cluster.keySet()){
			String sz_content_context = map_context_cluster.get(context);
			sz_content += String.format("\n subgraph  { label=\"%s\"; \n %s }\n",  context, sz_content_context );
		}		
		
		

		
		//cluster nodes by context
		for (MyDigraphArc<T> arc: this.getArcs()){
			String sz_temp =String.format("\"%s\" -> \"%s\" [ %s ] ;\n",getNodeLabel(arc.from), getNodeLabel(arc.to), getParams(arc));
			
			sz_content += sz_temp;

		}
/*
		for (MyDigraphArc<T> arc: this.getArcs()){
			String sz_temp =String.format("\"%s\" -> \"%s\" [ %s ] ;\n",getNodeLabel(arc.from), getNodeLabel(arc.to), getParams(arc));
			if (null==map_node_context ||map_node_context.size()==0){
				sz_content += sz_temp;
			}else{
				String context = map_node_context.get(arc.from);
				if (null==context){
					sz_content +=sz_temp;
				}else{
					String sz_content_context = map_context_cluster.get(context);
					if (null==sz_content_context){
						sz_content_context = sz_temp; 
					}else{
						sz_content_context += sz_temp; 						
					}
					map_context_cluster.put(context, sz_content_context);
				}
			}
		}
		
		//complete the context content
		int counter=1;
		for (String context : map_context_cluster.keySet()){
			String sz_content_context = map_context_cluster.get(context);
			sz_content += String.format("\n subgraph  { label=\"%s\"; \n %s }\n",  context, sz_content_context );
			counter++;
		}		
*/
		return String.format("#################################################### \ndigraph g { \n{  %s \n fillcolor=cornsilk; style=filled; \n %s \n}\n", sz_more, sz_content);
	}
}
