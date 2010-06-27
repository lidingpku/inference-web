package org.inference_web.onto;


import java.util.TreeSet;

import sw4j.rdf.util.AgentModelStat;
import sw4j.util.ToolString;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ProvenanceOntologyAnalyzer {
	public static void main(String[] args) {
		String [][] onto = new String [][]{
				{"http://github.com/lucmoreau/OpenProvenanceModel/raw/master/elmo/src/main/resources/opm.owl","http://openprovenance.org/ontology#"},
				{"http://inference-web.org/2.0/pml-provenance.owl#","http://inference-web.org/2.0/pml-provenance.owl#"},
				{"http://inference-web.org/2.0/pml-justification.owl#","http://inference-web.org/2.0/pml-justification.owl#"},
				{"http://purl.org/dc/terms/ ","http://purl.org/dc/terms/ "},
				{"http://purl.org/net/provenance/ns.rdf","http://purl.org/net/provenance/ns#"},
				{"http://knoesis.wright.edu/provenir/provenir.owl#","http://knoesis.wright.edu/provenir/provenir.owl#"},
				{"http://www.obofoundry.org/ro/ro.owl#","http://www.obofoundry.org/ro/ro.owl#"},
		};
		
		for (int i=0; i<onto.length; i++){
			String szUrlOntology = onto[i][0];
			String szNsOntology = onto[i][1];
			run(szUrlOntology,szNsOntology);
		}
	}
	
	
	public static void run(String szUrlOntology, String szNsOntology){
		Model m = ModelFactory.createDefaultModel();
		m.read( szUrlOntology, szUrlOntology,"" );
		
		System.out.println("---------------------------");
		//print size
		System.out.println(m.size()+"\ttriples");
		
		AgentModelStat ams = new AgentModelStat();
		ams.traverse(m);
		
		//print defined class
		stat_meta_usage(ams, AgentModelStat.META_USAGE_DEF_C);

		//print defined property
		stat_meta_usage(ams, AgentModelStat.META_USAGE_DEF_P);

		TreeSet<String> construct= new TreeSet<String> ();

		//print used class
		construct.addAll(stat_meta_usage(ams, AgentModelStat.META_USAGE_INS_C));

		//print used property
		construct.addAll(stat_meta_usage(ams, AgentModelStat.META_USAGE_INS_P));

		test_construct(construct);
	}
	
	public static TreeSet<String> stat_meta_usage(AgentModelStat ams, String option){
		TreeSet<String> ret = new TreeSet<String> (ams.getMetaTermsByUsage(option));
		System.out.println(ret.size()+"\t"+option);
		System.out.println(ToolString.printCollectionToString(ret));
		return ret;
	}
	
	public static void test_construct(TreeSet<String> construct){
		// taxonomy
		{
			String [] test = new String[]{
					RDFS.subClassOf.getURI(),
					RDFS.subPropertyOf.getURI(),
					OWL.disjointWith.getURI(),
					OWL.unionOf.getURI(),
					OWL.equivalentClass.getURI(),
					
			};
			for (int i=0; i<test.length; i++){
				print_row(construct.remove(test[i]),test[i]);
			}			
		}
		
		// relation inference
		{
			String [] test = new String[]{
					OWL.inverseOf.getURI(),
					OWL.TransitiveProperty.getURI(),
					OWL.FunctionalProperty.getURI(),
					
			};
			for (int i=0; i<test.length; i++){
				print_row(construct.remove(test[i]),test[i]);
			}			
		}
	
		// integrity constraint - domain/range
		{
			TreeSet<String> test = new TreeSet<String> ();
			test.add(RDFS.domain.getURI());
			test.add(RDFS.range.getURI());

			print_row(construct.removeAll(test),test.toString());
		}


		//  integrity constraint - value
		{
			String [] test = new String[]{
					OWL.allValuesFrom.getURI(),
					OWL.someValuesFrom.getURI(),
					
			};
			for (int i=0; i<test.length; i++){
				print_row(construct.remove(test[i]),test[i]);
			}			
		}
		
		//  integrity constraint - card
		{
			TreeSet<String> test = new TreeSet<String> ();
			test.add(OWL.cardinality.getURI());
			test.add(OWL.maxCardinality.getURI());
			test.add(OWL.minCardinality.getURI());

			print_row(construct.removeAll(test),test.toString());
		}
		
		
		// import 
		{
			String [] test = new String[]{
					OWL.imports.getURI(),
					
			};
			for (int i=0; i<test.length; i++){
				print_row(construct.remove(test[i]),test[i]);
			}			
		}


		//list
		{
			TreeSet<String> test = new TreeSet<String> ();
			test.add(RDF.first.getURI());
			test.add(RDF.rest.getURI());
			print_row(construct.removeAll(test),test.toString());
		}
		
		//comments
		{
			TreeSet<String> test = new TreeSet<String> ();
			test.add(RDFS.comment.getURI());
			test.add(RDFS.label.getURI());
			test.add(DCTERMS.description.getURI());
			test.add(DCTERMS.title.getURI());
			print_row(construct.removeAll(test),test.toString());
		}
		
		//ignored
		{
			TreeSet<String> test = new TreeSet<String> ();
			test.add(OWL.ObjectProperty.getURI());
			test.add(OWL.DatatypeProperty.getURI());
			test.add(OWL.Class.getURI());
			test.add(OWL.Class.getURI());
			test.add(OWL.Ontology.getURI());
			test.add(OWL.Restriction.getURI());
			test.add(OWL.onProperty.getURI());
			test.add(OWL.versionInfo.getURI());
			test.add(RDF.type.getURI());
			construct.removeAll(test);
		}
		
		if (construct.size()>0){
			System.out.println("WARNING: "+ construct);			
		}
	}
	
	public static void print_row(boolean value, String msg){
		if (value){
			System.out.print("X");
		}else{				
			System.out.print("");
		}
		System.out.println("\t"+msg);		
	}
}
