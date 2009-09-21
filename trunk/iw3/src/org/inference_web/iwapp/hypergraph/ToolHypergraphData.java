package org.inference_web.iwapp.hypergraph;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLR;
import sw4j.app.pml.PMLJ;
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
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RDF;
import sw4j.util.DataPVHMap;

public class ToolHypergraphData {

	
		
	//save the mapping between B and B1,B2...
	public HashMap<Resource, Resource> map_ns_nsChanged= new HashMap<Resource, Resource>();
	public static DataPVHMap<Resource, Resource> map_nsChanged_ns= new DataPVHMap<Resource, Resource>();
	
	public static Model pml2hg(String url_pml, String xmlbase){
		Model pml = ModelFactory.createDefaultModel();
		pml.read(url_pml);
		return pml2hg(pml,xmlbase, url_pml);
	}
	
	public static Model pml2hg(Model pml, String xmlbase, String url_pml){
		ToolJena.model_update_List2Map(pml, PMLDS.first, PMLDS.rest, null,false);

		StmtIterator iter = pml.listStatements(null, RDFS.member, (String)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			pml.add(pml.createStatement(stmt.getSubject(), pml.createProperty(PMLR.getURI()+"hasMember"), (RDFNode)(stmt.getObject())));
		}

		Resource r= pml.createResource(url_pml);
		ResIterator iter_res= pml.listResourcesWithProperty(RDF.type, PMLJ.InferenceStep);
		while (iter_res.hasNext()){
			pml.add(pml.createStatement(iter_res.nextResource(), PMLR.isPartOf, r));
		}
		
//		ToolJena.printModel(pml);

		
		//sparql query generates RDF graph from pml file
		String queryString = "prefix pmlj:   <http://inference-web.org/2.0/pml-justification.owl#> " +
							 "prefix pmlp:   <http://inference-web.org/2.0/pml-provenance.owl#> " +
							 "prefix pmlr:   <http://inference-web.org/2.0/pml-relation.owl#> " +
							 "prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							 "CONSTRUCT {?bn pmlr:hasOutput ?ns1 ." +
							 "			 ?bn pmlr:hasInput ?ns2 ." +
							 "           ?bn pmlr:isPartOf ?j ." + 
							 "           ?bn rdf:type pmlj:InferenceStep .}" +
							 "where {" +
							 "?ns1 pmlj:hasConclusion ?c1 ." +
							 "?ns1 pmlj:isConsequentOf ?bn ." +
							 "?bn pmlr:isPartOf ?j ." +
							 "?bn rdf:type pmlj:InferenceStep ." +
							 "?bn pmlj:hasAntecedentList ?nsl ." +
							 "?nsl pmlr:hasMember ?ns2 ." +
							 "?ns2 pmlj:hasConclusion ?c2 ." +
							 "}";
		
		Dataset ds = DatasetFactory.create(pml);
		
		Model m = (Model) new AgentSparql().exec(queryString, ds, null);
		
		

		
		return m;
		// we will add uri for each inference step later
	}
	
	
	/**
	 * apply mapping to hg
	 * @param hg
	 * @param mapping
	 * @return
	 */
	public Model combine(Model hg, Model mapping){
		// combine rdf graph with mapping 
				
		String xmlbase= "http://www.rpi.edu/~huangr3/sw4j/";
		replace_node(mapping, xmlbase);
		
		Model hg_sub= ModelFactory.createDefaultModel();
		Model hg_sub1= ModelFactory.createDefaultModel();
		
		ResIterator iter_sub= hg.listSubjects();
		while(iter_sub.hasNext()){
			Resource r= iter_sub.next();
			if (map_ns_nsChanged.containsKey(r)) {
				StmtIterator iter_stmt= hg.listStatements(r, null, (RDFNode)null);
				while(iter_stmt.hasNext()){
					Statement stmt= iter_stmt.next();
					hg_sub.add(map_ns_nsChanged.get(r), stmt.getPredicate(), stmt.getObject());
					hg_sub1.add(stmt);
				}
				
			}
				
		}
		NodeIterator iter_obj= hg.listObjects();
		while(iter_obj.hasNext()){
			RDFNode n=  iter_obj.next();
			if (map_ns_nsChanged.containsKey(n)) {
				StmtIterator iter_stmt= hg.listStatements(null, null, (RDFNode)n);
				while(iter_stmt.hasNext()){
					Statement stmt= iter_stmt.next();
					hg_sub.add(stmt.getSubject(), stmt.getPredicate(), map_ns_nsChanged.get((Resource) n));
					hg_sub1.add(stmt);					
				}
			}
		}
		
		hg.add(hg_sub);
		hg.remove(hg_sub1);
		
		print_mapping();
		return hg;
		
	}	
	
	
	public Model print_mapping()	{
		
		Iterator<Map.Entry<Resource,Resource>> iter_map= map_ns_nsChanged.entrySet().iterator(); 
		
		while(iter_map.hasNext()){
			Map.Entry<Resource,Resource> entry = iter_map.next();
			map_nsChanged_ns.add(entry.getValue(),entry.getKey());
		}
		
		Model all_mapping= ModelFactory.createDefaultModel();
		{

			Iterator<Map.Entry<Resource,Set<Resource>>> iter = map_nsChanged_ns.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<Resource,Set<Resource>> entry = iter.next();

				if (entry.getValue().size()>1){
					addOwlSameAs(all_mapping,entry.getKey(),entry.getValue());
				}
			}
			
		}
		
		return all_mapping;
//		ToolJena.printModelToFile(all_mapping,"files/test/PUZ001-1/all_mapping.rdf");
	}
	

	private static void addOwlSameAs(Model m, Resource res, Collection<Resource> resources){
		if (resources.size()>1){
			Iterator<Resource> iter = resources.iterator();
			Resource subject= res;
			while (iter.hasNext()){
				Resource object = iter.next();

				if (subject.equals(object))
						continue;

//				if (DataPmlLinked.isBadMapping(subject, object))
//						continue;
					
				m.add(m.createStatement(subject,OWL.sameAs, object));
			}
		}
	}
	
	
	public void replace_node(Model mapping, String xmlbase){
		//replace B1, B2 with B
		
		int count= 1;
		
		//get all resources(subjects and objects) in the mapping
		HashSet<Resource> res= new HashSet<Resource>();
		ResIterator iter_sub= mapping.listSubjects();
		while(iter_sub.hasNext()){
			res.add(iter_sub.nextResource());
		}
		
		NodeIterator iter_obj= mapping.listObjects();
		while(iter_obj.hasNext()){
			res.add((Resource) iter_obj.nextNode());
		}
		
		
		
		HashSet<Resource> res_used= new HashSet<Resource>();		
		
		Iterator<Resource> iter= res.iterator();
		
		while(iter.hasNext()){
			Resource r= iter.next();
			String new_url= xmlbase + "_" + count;
			Resource r_changed= mapping.createResource(new_url);

			if (!res_used.contains(r)) {
				HashSet<Resource> rs= (HashSet<Resource>) find_related_node(mapping, r);
				map_ns_nsChanged.put(r, r_changed);
				Iterator<Resource> iter_rs= rs.iterator();
				while(iter_rs.hasNext()){
					Resource r1= iter_rs.next();
					map_ns_nsChanged.put(r1, r_changed);
				}
				res_used.addAll(rs);
				count++;
			}			
		}
		
	}
	
	public static Collection<Resource> find_related_node(Model m, Resource r) {
		
		HashSet<Resource> res= new HashSet<Resource>();
		
		NodeIterator iter_obj= m.listObjectsOfProperty(r, OWL.sameAs);
		while(iter_obj.hasNext()){
			res.add((Resource) iter_obj.nextNode());
		}
		
		ResIterator iter_sub= m.listResourcesWithProperty(OWL.sameAs, (RDFNode) r);
		while(iter_sub.hasNext()){
			res.add(iter_sub.nextResource());
		}
		
		return res;
		
	}
	
	public Model normalize_mappings(Model m, String xmlbase){

		Model ret = ModelFactory.createDefaultModel();
		
		//add all OWL:sameAs mappings to ret		
		StmtIterator iter_stmt =m.listStatements(null,OWL.sameAs, (String)null);
		
		while(iter_stmt.hasNext()){
			Statement stmt= iter_stmt.nextStatement();
			ret.add(stmt);
		}	
		
		return ret;
	}
	
	public Model normalize_mappings(HashSet<String> urls, String xmlbase){
		Model m= ModelFactory.createDefaultModel();
		
		Iterator<String> iter =urls.iterator();
		while (iter.hasNext()){
			String url_mapping = iter.next();
			m.read(url_mapping);	
		}
		
		return normalize_mappings(m, xmlbase);
	}
	
	public static Model hg2pml(Model pml_original, Model hg, Model mapping, String xmlbase){
		// need to create new nodesets
		
		//TODO
		return null;
	}
	
//	public static void main(String[] args){
//		String [] address = new String[]{			
//				"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/answer.owl",
//			};
//
//			
//			HashSet<String> mapping_urls= new HashSet<String>();
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.0/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Ayane---1.1/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Metis---2.2/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Otter---3.3/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SNARK---20080805r005/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SOS---2.0/equalNS.owl");
//			mapping_urls.add("http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/Vampire---9.0/equalNS.owl");
//			
//			Model rdf_all= ModelFactory.createDefaultModel();
//
//			Model n= ToolHypergraphData.normalize_mappings(mapping_urls, null);
//			
//			for (int i=0; i< address.length; i++){
//				String szURL  = address[i];
//				Model m = ToolHypergraphData.pml2hg(szURL,null);
//				
//				Model after_m= ToolHypergraphData.combine(m, n);
//				
//				rdf_all.add(after_m);
////				ToolJena.printModelToFile(m, "files/test/test2.rdf");
//			}
//			
//			String pmlj= "http://inference-web.org/2.0/pml-justification.owl#";
//			String pmlp= "http://inference-web.org/2.0/pml-provenance.owl#";
//			String pmlr= "http://inference-web.org/2.0/pml-relation.owl#";
//			
//			rdf_all.setNsPrefix("pmlj",pmlj);
//			rdf_all.setNsPrefix("pmlp",pmlp);
//			rdf_all.setNsPrefix("pmlr",pmlr);
//
//			
//			ToolJena.printModelToFile(rdf_all,"files/test/test.rdf");
//	}
	
	

}
