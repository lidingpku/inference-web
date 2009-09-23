package org.inference_web.iwapp.hypergraph;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.inference_web.iwapp.crawler.PmlCrawler;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLR;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;

public class TaskPmlNormalize {
	public static void main(String[] argv){
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/"; 

		HashSet<String> set_url_seed = new HashSet<String>();
		set_url_seed.add(sz_url_seed);
		
		/////////////////////////////////////////
		// crawl all pages in one directory
		PmlCrawler crawler = new PmlCrawler();
		for (String url_seed : set_url_seed){
			crawler.crawl(url_seed,url_seed+".*", false);
		}

		String szContent =ToolString.printCollectionToString(crawler.m_results);
		System.out.println(szContent);
		//szContent =ToolString.printCollectionToString(crawler.m_errors);
		//System.out.println(szContent);

		
		/////////////////////////////////////////
		//generate 
		Set<String> set_url_pml = crawler.m_results;
		String sz_url_root_original =  sz_url_seed;
		File dir_root_output = new File("./files/tptp/combine/PUZ/PUZ001-1/");
		String sz_url_root_output = "http://inference-web.org/test/combine/PUZ/PUZ001-1/";
		run(set_url_pml, sz_url_root_original, dir_root_output, sz_url_root_output);

	}
	
	
	public static void run(Set<String> set_url_pml, String sz_url_root_original, File dir_root_output, String sz_url_root_output){
		DataPmlInfoMapping o_mapping = new DataPmlInfoMapping();
		for (String sz_url_pml: set_url_pml){
			AgentModelLoader loader = new AgentModelLoader(sz_url_pml);
			Model m = loader.getModelData();
			if (null==m)
				continue;
			
			System.out.println("loading pml data: "+ sz_url_pml);
			/////////////////////////////////////////
			// sign blank-node and write
			String sz_path_filename = sz_url_pml.substring(sz_url_root_original.length());
			File f_output_pml = new File(dir_root_output, sz_path_filename);
			String sz_namespace_pml_output = sz_url_root_output + sz_path_filename+"#";
			
			
			
			Model model_norm= ToolJena.model_signBlankNode(m, sz_namespace_pml_output);
        	ToolJena.model_update_List2Map(model_norm, RDF.first, RDF.rest, PMLR.hasMember, false);
        	ToolJena.model_update_List2Map(model_norm, PMLDS.first, PMLDS.rest, PMLR.hasMember, false);
        	model_norm.setNsPrefix(PMLR.class.getSimpleName().toLowerCase(), PMLR.getURI());
			ToolJena.printModelToFile(model_norm, f_output_pml.getAbsolutePath(), RDFSYNTAX.RDFXML,false);

			o_mapping.add_model(model_norm);
			
			/////////////////////////////////////////
			// create simplified data using only pmlr
			// replace .owl with _pmlr.rdf
			String sz_path_filename_simple = sz_path_filename.substring(0, sz_path_filename.length()-4)+"_pmlr.rdf";
			File f_output_pml_simple = new File(dir_root_output, sz_path_filename_simple);
			
			String sz_url_sparql = "http://inference-web.org/2009/09/pml2hg.sparql";
			String sz_sparql;
			try {
				sz_sparql = ToolIO.pipeUrlToString(sz_url_sparql);
				AgentSparql o_sparql = new  AgentSparql();
				Dataset dataset = DatasetFactory.create(model_norm);
				Object ret = o_sparql.exec(sz_sparql, dataset, RDFSYNTAX.RDFXML);
				if (ret instanceof Model)
					ToolJena.printModelToFile((Model)ret, f_output_pml_simple.getAbsolutePath(), RDFSYNTAX.RDFXML, false);
				else
					System.out.println("Error");

			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/////////////////////////////////////////
		// generate mapping file
		File f_output_mappings = new File(dir_root_output, "mappings_i.owl");

		Model model_mappings = o_mapping.get_mappings();
		ToolJena.printModelToFile(model_mappings, f_output_mappings.getAbsolutePath(), RDFSYNTAX.RDFXML, false);
		
		
	}

}
