package org.inference_web.iwapp.tptp;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.inference_web.pml.ToolPml;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;
import sw4j.vocabulary.pml.PMLJ;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class TaskIwTptpNormalize extends AgentIwTptp{
	int MAX_TRIPLE= 500000; //.5 million
	
	public static void main(String[] argv){
		run_cat();
	}
	
	public static void run(){
		String sz_seed = "http://inference-web.org/proofs/tptp/Solutions/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
		
		Set<String> set_problem = prepare_tptp_problems(sz_seed);
		for (String problem: set_problem ){
			System.out.println("processing "+ problem);
			run_norm(problem, sz_url_root_input);
		}
	}
	
	public static void run_cat(){
		String [] ARY_CATEGORY = new String []{
			 "http://inference-web.org/proofs/tptp/Solutions/SWC/",
		};
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";

		for (String category: ARY_CATEGORY){
			Set<String> set_problem = prepare_tptp_one_step(category);
			for (String problem: set_problem ){
				System.out.println("processing "+ problem);
				run_norm(problem, sz_url_root_input);
			}			
		}
	}


	public static void run_test(){
		{
			String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/BOO/BOO014-4/";
			String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
			run_norm(sz_url_seed,sz_url_root_input);		
		}
		{
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/ALG/ALG032+1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
		run_norm(sz_url_seed,sz_url_root_input);		
		}
		{
		String sz_url_seed = "http://inference-web.org/proofs/tptp/Solutions/ALG/ALG016+1/";
		String sz_url_root_input= "http://inference-web.org/proofs/tptp/Solutions";
		run_norm(sz_url_seed,sz_url_root_input);	
		}
	}

	public static void run_norm(String sz_url_problem, String sz_url_root_input){
		//prepare seeds
		File dir_root_output = new File("www/proofs/linked");
		String sz_url_root_output = "http://inference-web.org/proofs/linked";
		
		TaskIwTptpNormalize task = new TaskIwTptpNormalize();
		task.init(sz_url_problem, sz_url_root_input, dir_root_output, sz_url_root_output);
		task.run_normalize();
	}
	
	
	void run_normalize(){
		for (String sz_url_pml: this.set_url_pml){
			Logger.getLogger(this.getClass()).info("processing: "+sz_url_pml);

			Model m = ModelFactory.createDefaultModel();
			try{
				m.read(sz_url_pml);
			}catch(Exception e){
				//encounter bad RDF file
				Logger.getLogger(this.getClass()).warn(" bad RDF file, skipped: "+ sz_url_pml);
				e.printStackTrace();
				
				File f_output_pml = new File(dir_root_output, "bad-pml.csv");
				try {
					System.out.println("write to "+f_output_pml.getAbsolutePath());
					ToolIO.pipeStringToFile(sz_url_pml+","+ m.size()+"\n", f_output_pml, false, true);
				} catch (Sw4jException e1) {
					e1.printStackTrace();
				}

				continue;
			}
			
			if (!m.listSubjectsWithProperty(RDF.type, PMLJ.InferenceStep).hasNext()){
				System.out.println("skipped (no pml inference step): "+sz_url_pml);
				continue;
			}
			
			Logger.getLogger(this.getClass()).info(" number of triples: "+ m.size());
			
			//skip very large files
			if (m.size()>MAX_TRIPLE){
				Logger.getLogger(this.getClass()).warn(" large file, skipped: "+ sz_url_pml);
				File f_output_pml = new File(dir_root_output, "skipped-pml.csv");
				try {
					System.out.println("write to "+f_output_pml.getAbsolutePath());
					ToolIO.pipeStringToFile(sz_url_pml+","+ m.size()+"\n", f_output_pml, false, true);
				} catch (Sw4jException e) {
					e.printStackTrace();
				}
				continue;
			}

			/////////////////////////////////////////
			// sign blank-node and write
			Model model_norm = ToolPml.pml_create_normalize(m, sz_url_pml+"#");

			
			String sz_path = prepare_path(sz_url_pml,null);

			File f_output_pml = new File(dir_root_output, sz_path);
			HashMap<String,String> map_relative_url = new HashMap<String,String> ();
			map_relative_url.put(sz_url_root_input, "../../..");

			ToolPml.pml_save_data(model_norm, f_output_pml, sz_url_pml+"#",  map_relative_url);
		}
		System.gc();
		System.out.println( ToolString.formatXMLDateTime()+" free memory: "+Runtime.getRuntime().freeMemory());
	}
	
	
	
	
}
