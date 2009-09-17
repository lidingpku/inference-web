package org.inference_web.iwapp.hypergraph;

import java.io.PrintStream;
import java.util.HashMap;

import org.junit.Test;

import sw4j.app.pml.PMLR;
import sw4j.util.DataPVHMap;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;




public class ToolGraphVizConverter {
	
	
	
	public static String pmlj= "http://inference-web.org/2.0/pml-justification.owl#";
	public static String pmlp= "http://inference-web.org/2.0/pml-provenance.owl#";
	public static String pmlr= "http://inference-web.org/2.0/pml-relation.owl#";
	static PrintStream out= System.out;
	
	public static HashMap<Resource, String> m_is_localis= new HashMap<Resource, String>();
	public static HashMap<Resource, String> m_info_localinfo= new HashMap<Resource, String>();
	public static DataPVHMap<String, Resource> m_localinfo_sources= new DataPVHMap<String, Resource>();
	
	
	public  void RDF2GraphViz(Model m){
		
		int is_count= 1;
		int info_count= 1;
		
		out.println("digraph g { node [ shape = box]; \n");
		StmtIterator stmt_iter_in= m.listStatements(null,PMLR.hasInput,(String)null);
		while(stmt_iter_in.hasNext()){
			String localis= "s" + is_count;
			String localinfo= "" + info_count;
			String localsub= "";
			String localobj= "";
			Statement stmt= stmt_iter_in.next();
			Resource sub= stmt.getSubject();
			Resource obj= (Resource) stmt.getObject();
			if (m_is_localis.containsKey(sub)) {
				localsub= m_is_localis.get(sub);	
			} else {
				localsub= localis;
				m_is_localis.put(sub, localis);
				is_count++;
			}
			
			if (m_info_localinfo.containsKey(obj)){
				localobj= m_info_localinfo.get(obj);
			} else {
				localobj= localinfo;
				m_info_localinfo.put(obj, localinfo);
				info_count++;
			}
			
			out.println("\"" + localsub + "\" [shape=diamond];\n");
			out.println("\"" + localsub + "\" [URL=\"" + sub + "\"]; \n");
			out.println("\"" + localsub + "\" -> \"" + localobj + "\"; \n");
			out.println("\"" + localobj + "\" [URL=\"" + obj + "\"]; \n");
			
//			out.println("\"" + localsub + "\" [shape=diamond,URL=\"" + sub + "\"]; \n" );
//			out.println("\"" + localsub + "\" -> \"" + localobj + "\"[URL=\"" + obj + "; \n");
		}
		
		StmtIterator stmt_iter_out= m.listStatements(null,PMLR.hasOutput,(String)null);
		while(stmt_iter_out.hasNext()){
			String localis= "s" + is_count;
			String localinfo= "" + info_count;
			String localsub= "";
			String localobj= "";
			Statement stmt= stmt_iter_out.next();
			Resource sub= stmt.getSubject();
			Resource obj= (Resource) stmt.getObject();
			if (m_is_localis.containsKey(sub)) {
				localsub= m_is_localis.get(sub);	
			} else {
				localsub= localis;
				m_is_localis.put(sub, localis);
				is_count++;
			}
			
			if (m_info_localinfo.containsKey(obj)){
				localobj= m_info_localinfo.get(obj);
			} else {
				localobj= localinfo;
				m_info_localinfo.put(obj, localinfo);
				info_count++;
			}
			
			out.println("\"" + localsub + "\" [shape=diamond];\n");
			out.println("\"" + localsub + "\" [URL=\"" + sub + "\"]; \n");
			out.println("\"" + localobj + "\" -> \"" + localsub + "\"; \n");
			out.println("\"" + localobj + "\" [URL=\"" + obj + "\"]; \n");
		}
		out.println("}");
	}

	public static void getOrNode(Model m){
		
		
		
	}
	
	@Test
	public static void main(String[] args){
		
		String url= 
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/EP---1.0-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Faust---1.0-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Metis---2.2-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Otter---3.3-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/SNARK---20080805r005-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/SOS---2.0-answer.owl.rdf";
					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Vampire---9.0-answer.owl.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Ayane---1.1-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/EP---1.0-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Faust---1.0-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Metis---2.2-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Otter---3.3-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/SOS---2.0-answer.rdf";
//					"http://tw2.tw.rpi.edu/pml/PUZ001-1/g3/Vampire---9.0-answer.rdf";
		
		Model m= ModelFactory.createDefaultModel();
			
		m.read(url);	
		
		ToolGraphVizConverter vc= new ToolGraphVizConverter();
		
		vc.RDF2GraphViz(m);
		
	}
	
}
