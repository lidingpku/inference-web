package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.Iterator;

import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;


public class ToolHypergraphDataConverter {

	
	
	public static Model mappingns2info(String[] urls, HashSet<String> urls_mapping){
		
		Model m= ModelFactory.createDefaultModel();
			
		for(int i=0;i<urls.length;i++ ){
			m.read(urls[i]);
		}
		
		Iterator<String> iter= urls_mapping.iterator();
		while(iter.hasNext()){
			m.read(iter.next());
		}

		
		String queryString= 
		 					"prefix owl:    <http://www.w3.org/2002/07/owl#>" +
		 					"prefix pmlj:   <http://inference-web.org/2.0/pml-justification.owl#>" +
		 					"CONSTRUCT {?i1 owl:sameAs ?i2 .}" +
		 					"where {" +
		 					"?ns1 owl:sameAs ?ns2 ." +
		 					"?ns1 pmlj:hasConclusion ?i1 ." +
		 					"?ns2 pmlj:hasConclusion ?i2 ." +
		 					"}";

		Dataset ds = DatasetFactory.create(m);

		Model mapping_info = (Model) new AgentSparql().exec(queryString, ds, null);	
		
		return mapping_info;
	}
	
	
	public static Model rdfmapping2hg(String url_rdf, Model mapping){
		
		Model m= ModelFactory.createDefaultModel();
		m.read(url_rdf);
		
		Model hg= combine(m,mapping);
		
		return hg;
	}
	
	public static Model rdfmapping2hg(String url_rdf, String url_mapping){
		
		Model mapping= ModelFactory.createDefaultModel();
		mapping.read(url_mapping);
		
		Model hg= rdfmapping2hg(url_rdf, mapping);
		

		return hg;
	}
	
	
	public static Model combine(Model hg, Model mapping){
		// combine rdf graph with mapping 
		
		ToolHypergraphData gd= new ToolHypergraphData();
		
		StmtIterator iter_stmt_mapping= mapping.listStatements(null, OWL.sameAs, (RDFNode)null);
		while(iter_stmt_mapping.hasNext()){
			Statement stmt= iter_stmt_mapping.next();
			gd.map_ns_nsChanged.put((Resource) stmt.getObject(),stmt.getSubject());
		}
		
		
		
		Model hg_sub= ModelFactory.createDefaultModel();
		Model hg_sub1= ModelFactory.createDefaultModel();
		
		ResIterator iter_sub= hg.listSubjects();
		while(iter_sub.hasNext()){
			Resource r= iter_sub.next();
			if (gd.map_ns_nsChanged.containsKey(r)) {
				StmtIterator iter_stmt= hg.listStatements(r, null, (RDFNode)null);
				while(iter_stmt.hasNext()){
					Statement stmt= iter_stmt.next();
					hg_sub.add(gd.map_ns_nsChanged.get(r), stmt.getPredicate(), stmt.getObject());
					hg_sub1.add(stmt);
				}
				
			}
				
		}
		NodeIterator iter_obj= hg.listObjects();
		while(iter_obj.hasNext()){
			RDFNode n=  iter_obj.next();
			if (gd.map_ns_nsChanged.containsKey(n)) {
				StmtIterator iter_stmt= hg.listStatements(null, null, (RDFNode)n);
				while(iter_stmt.hasNext()){
					Statement stmt= iter_stmt.next();
					hg_sub.add(stmt.getSubject(), stmt.getPredicate(), gd.map_ns_nsChanged.get((Resource) n));
					hg_sub1.add(stmt);					
				}
			}
		}
		
		hg.add(hg_sub);
		hg.remove(hg_sub1);
		
		return hg;
		
	}	
	
	public static void main(String[] args){
		
		String[] url_g1_rdf= {
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Ayane---1.1-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/EP---1.0-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Faust---1.0-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Metis---2.2-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Otter---3.3-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SNARK---20080805r005-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SOS---2.0-answer.owl.rdf",
			"http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Vampire---9.0-answer.owl.rdf",
		};
		
		
		String[] url_rdf={
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Ayane---1.1-answer.owl.rdf",
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/EP---1.0-answer.owl.rdf",
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Faust---1.0-answer.owl.rdf",
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Metis---2.2-answer.owl.rdf",
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Otter---3.3-answer.owl.rdf",
				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/SOS---2.0-answer.owl.rdf",
//				"http://tw2.tw.rpi.edu/pml/PUZ001-1/g2/Vampire---9.0-answer.owl.rdf",
		};

		HashSet<String> urls_mapping= new HashSet<String>();
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Ayane---1.1/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Metis---2.2/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Otter---3.3/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SNARK---20080805r005/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SOS---2.0/equalNS.owl");
		urls_mapping.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Vampire---9.0/equalNS.owl");
		
		Model mapping_mid= mappingns2info(url_g1_rdf,urls_mapping);
		
		ToolHypergraphData gd= new ToolHypergraphData();
		
		gd.replace_node(mapping_mid, "http://tw2.tw.rpi.edu/pml/PUZ001-1/mapping-i.owl#");
		
		Model mapping_info= gd.print_mapping();
				
		ToolJena.printModelToFile(mapping_info, "files/test/mapping-i.owl");

		Model m= ModelFactory.createDefaultModel();
		
		for(int i=0; i<url_rdf.length;i++) {
			m.add(rdfmapping2hg(url_rdf[i], mapping_info));
		}

		
		ToolGraphVizConverter vc= new ToolGraphVizConverter();	
		vc.RDF2GraphViz(m);
		
		ToolJena.printModelToFile(m,"files/test/g3/SOS---2.0-answer.owl.rdf");

	}
	
}
