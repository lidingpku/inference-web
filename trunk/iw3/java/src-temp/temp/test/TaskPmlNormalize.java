package temp.test;
/*
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.inference_web.iwapp.crawler.PmlCrawler;
import org.inference_web.pml.DataPmlHg;
import org.inference_web.pml.DataPmlInfo;
import org.inference_web.pml.ToolPml;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import sw4j.app.pml.PMLDS;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLR;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.RDFSYNTAX;
import sw4j.rdf.util.AgentSparql;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;
*/

public class TaskPmlNormalize {
	/*
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
		File dir_root_output = new File("www/test/normal");
		String sz_url_root_output = "http://inference-web.org/test/normal";
		
		run(set_url_pml, sz_url_root_original, dir_root_output, sz_url_root_output);

	}
	
	public static void run(Set<String> set_url_pml, String sz_url_root_original, File dir_root_output, String sz_url_root_output){
		TaskPmlNormalize tpn = new TaskPmlNormalize();
		tpn.run_normalize(set_url_pml, dir_root_output, sz_url_root_output);
	}

	HashMap<String,Model> map_url_model_norm = new HashMap<String,Model>(); 


	
	public void run_normalize(Set<String> set_url_pml, File dir_root_output, String sz_url_root_output){
		DataPmlHg hg = new DataPmlHg();
		for (String sz_url_pml: set_url_pml){
			hg.add_data(sz_url_pml);
		}

		for (String sz_url_pml: hg.getContext()){

			try {
				Model m = hg.getModelOriginal(sz_url_pml);

				/////////////////////////////////////////
				// sign blank-node and write
				URL url_pml;
				url_pml = new URL(sz_url_pml);
				String sz_path = url_pml.getPath();
				String sz_filename = url_pml.getFile();
				System.out.println(sz_path);			
				System.out.println(sz_filename);	

				String sz_namespace_pml_output = sz_url_root_output + sz_path+"#";
				File f_output_pml = new File(dir_root_output, sz_path);

				Model model_norm = ToolPml.pml_sign(m, sz_namespace_pml_output);
				ToolPml.pml_index(model_norm);

				ToolJena.printModelToFile(model_norm, f_output_pml.getAbsolutePath(), RDFSYNTAX.RDFXML,false);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/////////////////////////////////////////
		// generate mapping file
		File f_output_mappings = new File(dir_root_output, "mappings_i.owl");

		Model model_mappings = hg.create_mappings();
		ToolJena.printModelToFile(model_mappings, f_output_mappings.getAbsolutePath(), RDFSYNTAX.RDFXML, false);
	}


	
	public void run_index(Set<String> set_url_pml, String sz_url_root_original, File dir_root_output, String sz_url_root_output){

		for (String sz_url_pml: set_url_pml){
			try {
				String sz_path_filename = sz_url_pml.substring(sz_url_root_original.length());
	
				/////////////////////////////////////////
				//load all pml data
				Model model_all = ToolPml.load_pml_data_all(sz_url_pml, map_url_model_norm);
				
				/////////////////////////////////////////
				// create index data using PMLR
				// replace .owl with _pmlr.rdf
				String sz_path_filename_simple = sz_path_filename.substring(0, sz_path_filename.length()-4)+"_pmlr.rdf";
				File f_output_pml_simple = new File(dir_root_output, sz_path_filename_simple);
				
				
				String sz_url_sparql = "http://inference-web.org/2009/09/pml2hg.sparql";
				String sz_sparql;

				sz_sparql = ToolIO.pipeUrlToString(sz_url_sparql);
				AgentSparql o_sparql = new  AgentSparql();
				Dataset dataset = DatasetFactory.create(model_all);
				Object ret = o_sparql.exec(sz_sparql, dataset, RDFSYNTAX.RDFXML);
				if (!(ret instanceof Model)){
					System.out.println("Error");
					System.exit(-1);
				}
				
				//add transitive relation
				Model model_index= (Model)ret;
				ToolJena.model_add_transtive(model_index, PMLR.dependsOn);
				
				
				//get root
				Model model_norm= map_url_model_norm.get(sz_url_pml);
				Set<Resource> roots = ToolPml.list_roots(model_norm);
				if (roots.size()!=1){
					System.out.println("ERROR!");
					System.exit(-1);
				}
				Resource res_root = roots.iterator().next();

				//keep only steps related to root
				Set<RDFNode> nodes = model_index.listObjectsOfProperty(res_root,PMLR.dependsOn).toSet();
				Iterator<Statement> iter = model_index.listStatements();
				while (iter.hasNext()){
					Statement stmt = iter.next();
					if (!nodes.contains(stmt.getSubject()))
							iter.remove();
				}					
				
				//save model
				ToolJena.printModelToFile(model_index, f_output_pml_simple.getAbsolutePath(), RDFSYNTAX.RDFXML, false);
				
				
				
				////////////////////////////////////////////////
				// enrich model_all with index data
				model_all.add(model_index);
				

				///////////////////////////////////////////
				//generate graphics
				
				//plot original
				{
					DataPmlHg hg = new DataPmlHg();
					hg.addHg(model_index, model_all, sz_url_pml);
					
					String sz_path_filename_graph = sz_path_filename.substring(0, sz_path_filename.length()-4)+"";
					File f_output_graph = new File(dir_root_output, sz_path_filename_graph);
		
					DataHyperGraph dhg= hg.getHyperGraph(sz_url_pml, DataPmlHg.OPTION_HG_WEIGHT_STEP);	
					ToolGraphviz.gen_dot(dhg, hg, f_output_graph.getAbsolutePath(),sz_url_pml);
				}	

				
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		////////////////////////////
		//generate graphics
		for (String sz_url_pml: map_url_model_norm.keySet()){

			String sz_path_filename = sz_url_pml.substring(sz_url_root_original.length());

			int pos = sz_url_pml.lastIndexOf("/");
			String sz_url_pml_base =sz_url_pml.substring(0,pos)+"/answer.owl";

			Model model_data = map_url_model_norm.get(sz_url_pml);
			DataPmlHg hg = new DataPmlHg();
			hg.addHg(model_data, sz_url_pml);

			//plot comparison if possible
			if(!sz_url_pml.equals(sz_url_pml_base)){
				String sz_path_filename_graph = sz_path_filename.substring(0, sz_path_filename.length()-4)+"-mapped";
				File f_output_graph = new File(dir_root_output, sz_path_filename_graph);

				Model model_base = map_url_model_norm.get(sz_url_pml_base);			
				hg.addHg(model_base, sz_url_pml_base);
				hg.addMappings(o_mapping.get_mappings());
				
				DataHyperGraph dhg= hg.getHyperGraph(sz_url_pml,DataPmlHg.OPTION_HG_WEIGHT_STEP);	
				DataHyperGraph dhg_all= hg.getHyperGraph();	
				ToolGraphviz.export_dot2(dhg, dhg_all, hg, f_output_graph.getAbsolutePath(),sz_url_pml_base);				
			}
				
		}
	}

*/	
}
