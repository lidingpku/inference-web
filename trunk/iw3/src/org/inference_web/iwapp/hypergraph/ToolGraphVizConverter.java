package org.inference_web.iwapp.hypergraph;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	public static DataPVHMap<String, String> m_localinfo_localis= new DataPVHMap<String, String>();
	public static DataPVHMap<String, String> m_localis_inlocalinfo= new DataPVHMap<String, String>();
	public static HashMap<String, String> m_localis_outlocalinfo= new HashMap<String,String>();
	public static HashSet<String> localinfoleaf= new HashSet<String>();
	
	
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
			
			m_localis_inlocalinfo.add(localsub,localobj);
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
			
			m_localis_outlocalinfo.put(localsub,localobj);
			m_localinfo_localis.add(localobj, localsub);
			
			out.println("\"" + localsub + "\" [shape=diamond];\n");
			out.println("\"" + localsub + "\" [URL=\"" + sub + "\"]; \n");
			out.println("\"" + localobj + "\" -> \"" + localsub + "\"; \n");
			out.println("\"" + localobj + "\" [URL=\"" + obj + "\"]; \n");
		}
		
		setOrNode();
		setLeafNode();
		setLeafNodeRank();
		out.println("}");
	}

	public static void setOrNode(){
	
		Iterator<Map.Entry<String,Set<String>>> iter = m_localinfo_localis.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String,Set<String>> entry = iter.next();

			if (entry.getValue().size()>1){
				out.println("\"" + entry.getKey() + "\" [color=yellow,style=filled]; \n");
			}
		}
		
	}
	
	public static void setLeafNode(){
		Iterator<Map.Entry<String,String>> iter = m_localis_outlocalinfo.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String,String> entry = iter.next();
			if (!m_localis_inlocalinfo.keySet().contains(entry.getKey())){	
				out.println("\"" + entry.getValue() + "\" [color=red,style=filled]; \n");
				localinfoleaf.add(entry.getValue());
			}
		}
		
	}
	
	private static void setLeafNodeRank(){
		Iterator<String> leaf_iter= localinfoleaf.iterator();
		String rankStr= "{rank = same; ";
		while (leaf_iter.hasNext()) {
			String leaf= leaf_iter.next();
			rankStr+= "\"" + leaf + "\"; ";
		}
		out.println(rankStr + " }");
		
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
