package org.inference_web.app.sameas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;


import sw4j.rdf.util.ToolJena;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;

public class TestSameasNetwork {
	public static void main (String[] args){
		String [] ary_options = new String []{
				"people",
				"organizations",
				"locations",
		};
		
		for (String option : ary_options){
			String input = String.format("local/output/%s/sameas.owl", option);
			String output_owl =  String.format("local/output/sameas-%s.owl", option);
			String output_dot =  String.format("local/output/sameas-%s.txt", option);
			test(input,output_owl,output_dot,false);			
		}
		/*	
		for (String option : ary_options){
			String input = String.format("local/output/%s/sameas.owl", option);
			String output_owl =  String.format("local/output/sameas-%s-extra.owl", option);
			String output_dot =  String.format("local/output/sameas-%s-extra.txt", option);
			test(input,output_owl,output_dot,true);			
		}
	*/	
	}

	public static void test(String input, String output_owl, String output_dot, boolean bLoadExtra){
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(new FileInputStream(input),"");
			
			//load extra same As
			if (bLoadExtra){
				Set<RDFNode> nodes = m.listObjects().toSet();
				nodes.removeAll(m.listSubjects().toSet());
				
				for (RDFNode node : nodes){
					Resource res = (Resource)(node);
					String uri = res.getURI();
					try{
						Model mx = ModelFactory.createDefaultModel();
						mx.read(uri);
						m.add(mx.listStatements(null, OWL.sameAs, (String)null));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			//counting connected components
			DataObjectGroupMap<Resource> map = new DataObjectGroupMap<Resource>();
			for(Statement stmt: m.listStatements().toSet()){
				map.addSameObjectAs(stmt.getSubject(), (Resource)(stmt.getObject()));
			}
			
			ToolJena.printModelToFile(m, output_owl);
			
			//partition graphs
			HashMap<Integer,Model> output_models = new HashMap<Integer,Model>();
			HashMap<Integer,MyDigraph<Resource>> output_graphs = new HashMap<Integer,MyDigraph<Resource>>();
			HashMap<Resource,String> map_node_context = new HashMap<Resource,String>();
			String sz_output ="";
			for(Statement stmt: m.listStatements().toSet()){
				int id = map.getGid(stmt.getSubject());
				{
					Model m1 = output_models.get(id);
					if (null==m1){
						m1=ModelFactory.createDefaultModel();
						output_models.put(id,m1);
					}
					m1.add(stmt);
				}
				
				MyDigraph<Resource> graph = output_graphs.get(id);
				if (null==graph){
					graph= new MyDigraph<Resource>();
					output_graphs.put(id,graph);
				}
				graph.add(stmt.getSubject(),(Resource)(stmt.getObject()));
				
				
				{
					Resource res = stmt.getSubject();
					String label = res.getLocalName();
					String namespace = res.getNameSpace();
					if (ToolSafe.isEmpty(label)){
						DataQname qname = DataQname.create(res.getURI());
						label = qname.getLocalname();
						namespace = qname.getNamespace();
					}
					graph.addNodeParam(res,"label",res.getURI());
					graph.addNodeParam(res,"shape",getShape(namespace));
					graph.addNodeParam(res,"color", getColor(namespace));
					map_node_context.put(res, namespace );
				}
				{
					Resource res = (Resource)(stmt.getObject());
					String label = res.getLocalName();
					String namespace = res.getNameSpace();
					if (ToolSafe.isEmpty(label)){
						DataQname qname = DataQname.create(res.getURI());
						label = qname.getLocalname();
						namespace = qname.getNamespace();
					}
					graph.addNodeParam(res,"label",res.getURI());
					graph.addNodeParam(res,"shape",getShape(namespace));
					graph.addNodeParam(res,"color", getColor(namespace));
					map_node_context.put(res, namespace );
				}
			}
			
			//generate graphs
			for (MyDigraph<Resource> graph : output_graphs.values()){
				String output = graph.data_export_graphviz_subgraph("label=\"sameas network\"", map_node_context);
				sz_output+=output;
			}
			
			ToolIO.pipeStringToFile(sz_output, output_dot,false);

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getShape(String namespace){
		HashMap<String,String> map = new HashMap<String,String>() ;
		map.put("http://dbpedia.org/resource/","box" );
		map.put("http://rdf.freebase.com/ns/","polygon" );
		map.put("http://data.nytimes.com/","ellipse" );
		return map.get(namespace);
	}

	public static String getColor(String namespace){
		HashMap<String,String> map = new HashMap<String,String>() ;
		map.put("http://dbpedia.org/resource/","blue" );
		map.put("http://rdf.freebase.com/ns/","red" );
		map.put("http://data.nytimes.com/","green" );
		return map.get(namespace);
	}
	public static String getPrefix(String namespace){
		HashMap<String,String> map = new HashMap<String,String>() ;
		map.put("http://sw.opencyc.org/2008/06/10/concept/", "opencyc");
		
		return map.get(namespace);
	}
}
