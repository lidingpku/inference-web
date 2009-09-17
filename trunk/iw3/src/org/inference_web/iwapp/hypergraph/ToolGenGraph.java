package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sw4j.rdf.util.ToolJena;
import sw4j.util.DataObjectGroupMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

public class ToolGenGraph {
	
	public static void main(String[] argv){
		ToolGenGraph tool = new ToolGenGraph();
		tool.test();

	}
	public void test(){
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf");
//		to_dot("http://inference-web.org/test/combine/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		HashSet<String> test = new HashSet<String>();
		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/EP---1.0-answer.owl.rdf");
		test.add("http://inference-web.org/test/combine/PUZ001-1/g2/SOS---2.0-answer.owl.rdf");

	//	test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Ayane---1.1-answer.owl.rdf");
		//test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/EP---1.0-answer.owl.rdf");
		
//		test.add("http://tw2.tw.rpi.edu/pml/PUZ001-1/g4/combined-EP-SOS-answer.owl.rdf");
		to_dot(test);
	}
	
	public void run(){
		//1 create mapping i
		create_mapping_i(
				"http://inference-web.org/test/combine/PUZ001-1/mapping_i_pre.rdf",
				"files/tptp/mapping_i.rdf",
				"http://inference-web.org/test/combine/PUZ001-1/mapping_i.rdf#"
		);		
		
		//2 generate graph
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
	

	public void create_mapping_i(String url_mapping,  String filename_output, String namespace_output){
		DataObjectGroupMap<Resource> map = new  DataObjectGroupMap<Resource>();

		//load mapping data, merge group
		{
			Model m = ModelFactory.createDefaultModel();
			m.read(url_mapping);
			Iterator<Statement> iter = m.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.next();
				map.addSameObjectAs(stmt.getSubject(), (Resource)(stmt.getObject())	);
			}
			System.out.println(m.listSubjects().toSet().size());
		}
		//generate new data
		{
			Model m = ModelFactory.createDefaultModel();
			Iterator<Integer> iter = map.listGids();
			while (iter.hasNext()){
				Integer gid = iter.next();
				Resource subject = m.createResource(namespace_output+gid);
				Iterator<Resource> iter_res = map.getObjectsByGid(gid).iterator();
				while (iter_res.hasNext()){
					Resource object = iter_res.next();
					subject.addProperty(OWL.sameAs, object);
				}
			}
			ToolJena.printModelToFile(m, filename_output,"RDF/XML",false);
		}
	}

	public static void to_dot(String url_input){
		//build hg
		DataHg hg = new DataHg();
		System.out.println(url_input);
		hg.addHg(url_input);
		
		System.out.println(hg.getHyperGraph().data_export_graphviz(null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* hello */"));

	}
	
	public static void to_dot(Set<String> urls_input){
		//build hg
		DataHg hg = new DataHg();
		for(String url_input: urls_input){
			System.out.println(url_input);
			hg.addHg(url_input);
		}
		hg.addMappings("http://inference-web.org/test/combine/PUZ001-1/mapping_i_pre.rdf");
		
		System.out.println(hg.getHyperGraph().data_export_graphviz(null, hg.getMapNodeParams(),hg.getMapEdgeParams(),"/* hello */"));

	}
}
