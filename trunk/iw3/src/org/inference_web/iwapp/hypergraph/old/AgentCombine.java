package org.inference_web.iwapp.hypergraph.old;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;


import sw4j.app.pml.ToolPML2Writer;
import sw4j.pellet.ToolPellet;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;
import sw4j.task.graph.AgentHyperGraphTraverse;
import sw4j.task.graph.DataHyperGraph;
import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.ToolURI;

public class AgentCombine {
	
	
	public static final String FEATURE_PROBLEM = "problem";
	public static final String FEATURE_SOLUTION= "solution";
	public static final String FEATURE_ALGORITHM = "algorithm";
	public static final String FEATURE_CONTEXT = "context_lg";
	public static final String FEATURE_FINISH = "finish";
	
	public static final String CONTEXT_ORIGINAL = "original";
	public static final String CONTEXT_COMBINED = "combined";
	
	public static final String FINISH_TIMEOUT = "timeout";
	public static final String FINISH_MAX_SOLUTION = "max_solution";

	public static final String CONFIGURE_BASE_FILE = "base_file";
	public static final String CONFIGURE_BASE_HTTP = "base_http";
	public static final String CONFIG_ROOT_URI = "root_uri";
	public static final String CONFIG_ROOT_URI_ID = "root_uri_id";
	public static final String CONFIG_USE_ALG = "use_alg";
	
	public static final String ALG_ORIGINAL = "original";
	public static final String ALG_TRAVERSE = "traverse";
	
	public static final String ALG_NODE_CNF  = "node_cnf1";
	public static final String ALG_AXIOM_CNF  = "axiom_cnf2";
	public static final String ALG_AXIOM_NODE_CNF = "axiom_node_cnf3";
	public static final String ALG_NODE_AXIOM_CNF = "node_axiom_cnf4";
	

	public int MAX_OUTPUT = -1;
//	public int MAX_TRAVERSE = 10000;
	public int MAX_TIMEOUT = 1*60*1000;
	public int MAX_TIMEOUT_TRAVERSE = 1*60*1000;
	
	public boolean USE_STRING_MAPPING = false;
	public boolean USE_STRING_MAPPING_ALT = false;

	boolean debug = false;
	
	//input
	DataSmartMap m_features = new DataSmartMap();	
	public DataSmartMap m_config= new DataSmartMap();
	public ArrayList<String> m_urls_mapping =  new ArrayList<String> ();
	public ArrayList<String> m_urls_pml =  new ArrayList<String> ();

	//intermediate
	DataPmlLinked m_pml_data = new DataPmlLinked();
	Model m_mappings_external = ModelFactory.createDefaultModel();
	Model m_mappings_string = ModelFactory.createDefaultModel();
	HashMap<String,Integer> m_baseline_quality = new HashMap<String,Integer>();
	
	//output
	PrintStream m_out_log = System.out;					//log.txt
	PrintStream m_out_index = System.out;			//index.html
	PrintStream m_out_csv = System.out;		//result.csv
	boolean m_bFirstResult= true;
	long m_starttime= 0;
	
	
	public static void run(ArrayList<String> urls_pml, ArrayList<String> urls_mapping, DataSmartMap features, DataSmartMap config ){
		AgentCombine acp = new AgentCombine();
		
		acp.m_features = features;
		acp.m_config = config;
		acp.m_urls_mapping = urls_mapping;
		acp.m_urls_pml = urls_pml;
		
		acp.process();
	}	
	
	public void process(){
		
		process_begin();

		step_load_pml();
		
		step_load_mappings();
		
		step_compute_string_mapping();

		step_search_baseline();

		DataHyperGraph lg = step_compute_linked_justification();
		
		step_search_linked_justification(lg);

		process_end();
	}
	
	private void process_begin(){
		m_starttime= System.currentTimeMillis();
		//initialize 
		try {
			{
				String szDir= String.format("%s/%s",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));
				ToolIO.deleteDirectory(new File(szDir));
			}
			
			{
				String szFilename= String.format("%s/%s/index.html",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));

				
				m_out_index = new PrintStream(ToolIO.prepareFileOutputStream(szFilename, false));
				m_bFirstResult = true;
				m_out_index.println( "<head><script src=\"http://tw.rpi.edu/style/sorttable.js\"></script></head>");
				m_out_index.println( "<ul>");
				m_out_index.println( "<li><a href=\"log.txt\">process log</a></li>");
				m_out_index.println( "<li><a href=\"lj.txt\">linked justification in hyper graph format</a></li>");
				m_out_index.println( "<li><a href=\"lj.owl\">linked justification in PML</a></li>");
				m_out_index.println( "<li><a href=\"lj-index.html\">linked justification (via iwbrowser)</a></li>");
				m_out_index.println( "<li><a href=\"more_mappings.owl\">more_mappings.owl</a></li>");
				m_out_index.println( "<li><a href=\"result.csv\">result.csv</a></li>");
				m_out_index.println( "</ul>");
				m_out_index.println( "<table class=\"sortable\" border=1>");
			}

			{
				String szFilename= String.format("%s/%s/result.csv",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));

				m_out_csv = new PrintStream(ToolIO.prepareFileOutputStream(szFilename, false));
			}

			{
				String szFilename= String.format("%s/%s/log.txt",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));
				m_out_log = new PrintStream(ToolIO.prepareFileOutputStream(szFilename, false));
			}	
			
			this.println_log("----------");
			this.println_log_sysinfo();
			
		
		} catch (Sw4jException e) {
			e.printStackTrace();
			m_out_log = System.out;
		}	
	}
	
	private void process_end() {
		this.println_log("----------");
		this.println_log_sysinfo();
		this.println_log( "seconds used: "+(System.currentTimeMillis()-m_starttime)/1000 );

		if (!System.out.equals(m_out_log))
			m_out_log.close();
			
		m_out_index.println( "</table>");

		if (!System.out.equals(m_out_index))
			m_out_index.close();
		
		if (!System.out.equals(m_out_csv))
			m_out_csv.close();
	}
	
	private void println_log(String szText){
		m_out_log.println(szText);
		m_out_log.flush();
		
		System.out.println("[C"+(System.currentTimeMillis()-this.m_starttime)/1000+"] "+szText);
	}


	
	private void do_save_string_mapping(){
		if (this.m_mappings_string != null){
			String szFilename= String.format("%s/%s/more_mappings.owl",
					m_config.getAsString(CONFIGURE_BASE_FILE),
					m_features.getAsString(FEATURE_PROBLEM));
			
			//compare mappings
				
				Model m1 = ToolPellet.createOntModel();
				m1.add( this.m_mappings_external);

				Model m2 = ToolPellet.createOntModel();
				m2.add( this.m_mappings_string);
				
				m2.remove(m1);
				
				if (m2.isEmpty())
					this.println_log("generated mappings contains all string mappings");
				else{
					this.println_log(String.format("%d (string) mappings not covered", m2.size()));
					ToolJena.printModelToFile(this.m_mappings_string, szFilename);				}
					
			
		}
	}
	
	private DataHyperGraph do_save_linked_justification(){
		DataHyperGraph g = null;
		try {
			{
				String szFilename= String.format("%s/%s/lj.txt",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));

				g = this.m_pml_data.getHyperGraph();
				ToolIO.pipeStringToFile(g.data_export(), szFilename, false);
				
			}
			
			{
				String szFilename= String.format("%s/%s/lj.owl",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));
				
				String szNamespace= String.format("%s/%s/lj.owl#",
						m_config.getAsString(CONFIGURE_BASE_HTTP),
						m_features.getAsString(FEATURE_PROBLEM));
				
				Model m =this.do_save_pml(szNamespace, szFilename, g);

				this.println_log(g.data_summary());

				
				//save an index file for iwbrowser
				szFilename= String.format("%s/%s/lj-index.html",
						m_config.getAsString(CONFIGURE_BASE_FILE),
						m_features.getAsString(FEATURE_PROBLEM));

				String szContent = "";
				szContent += "linked justifications (linked to iwbrowser) \n";
				szContent +=  "<ul>\n";
				
				Iterator<Integer> iter = g.getRoots().iterator();
				while(iter.hasNext()){
					Integer root = iter.next();
					
					ToolPML2Writer writer = new ToolPML2Writer(m, szNamespace);
					String  root_uri = writer.createResource(root).toString();
					
					szContent += String.format( "<li>%s - corresponding to %s</li>\n", getIWBrowserLink(root_uri), this.m_pml_data.m_map_res_vertex.getObjectsByGid(root),toString());
				}
				szContent +=  "</ul>\n";
				
				ToolIO.pipeStringToFile(szContent, szFilename, false);
			}
			

		} catch (Sw4jException e) {
			e.printStackTrace();
		}		
		
		return g;
	}
	
	
	private void step_load_pml() {
		println_log("============================================");
		println_log("1. Load PMLs");

		if (null==this.m_urls_pml)
			return;

		
		Iterator<String> iter = this.m_urls_pml.iterator();
		while (iter.hasNext()){
			String szURL = iter.next();
			this.println_log(szURL);

			m_pml_data.addPml(szURL);
		}
		
		this.println_log("summary");
		this.println_log_sysinfo();
		this.println_log(String.format("PML files loaded: %d", m_pml_data.m_map_url_step.entrySet().size()));
		this.println_log(String.format("steps loaded: %d", m_pml_data.m_steps.size()));
		this.println_log(String.format("nodeset loaded: %d", m_pml_data.m_map_res_vertex.getTotalObjects()));
		this.println_log(String.format("groups loaded: %d", m_pml_data.m_map_res_vertex.getTotalGids()));
		
	}

	private void step_load_mappings() {
		println_log("============================================");
		println_log("2. Load Mapping");

		if (null==this.m_urls_mapping)
			return;
		
		// add preset mappings
		Iterator<String> iter = this.m_urls_mapping.iterator();
		while (iter.hasNext()){
			String szURL = iter.next();
			this.println_log(szURL);

			// load model
			Model m = null;
			try {
				m = AgentModelManager.get().loadModel(szURL);
				if (null!=m)
					this.m_mappings_external.add(m);
			} catch (Sw4jException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
		this.println_log("summary");
		this.println_log_sysinfo();
		this.println_log("mapping files loaded: " + this.m_urls_mapping.size());
	}

	private void step_compute_string_mapping(){
		this.println_log("============================================");
		this.println_log("3. Compute String Mappings");
		
		
		// group conclusions by their text
		DataPVHMap<String, Resource> map_info_res = new DataPVHMap<String, Resource>();
	
		{
			
			Iterator<DataPmlStep> iter = this.m_pml_data.m_steps.iterator();
			while (iter.hasNext()){
				DataPmlStep step = iter.next();
				map_info_res.add(step.m_conclusion_text, step.m_conclusion);
			}
		}

		// build a mapping for conclusions having the same text
		this.m_mappings_string  = ModelFactory.createDefaultModel();
		int counter = 0;
		{

			Iterator<Map.Entry<String,Set<Resource>>> iter = map_info_res.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<String,Set<Resource>> entry = iter.next();

				if (entry.getValue().size()>1){
					counter++;
					addOwlSameAs(this.m_mappings_string ,entry.getValue());
				}
			}
			
		}
		
		do_save_string_mapping();		
		
		// print summary
		this.println_log("summary");
		this.println_log_sysinfo();
		this.println_log(String.format("conclusions (total): %d", map_info_res.keySet().size()));
		this.println_log(String.format("conclusions (mapped): %d", counter));
		this.println_log(String.format("mappings (total): %d", this.m_mappings_string.size()));
			
	}	


	private static void addOwlSameAs(Model m, Collection<Resource> resources){
		if (resources.size()>1){
			Iterator<Resource> iter = resources.iterator();
			while (iter.hasNext()){
				Resource subject = iter.next();
				
				Iterator<Resource> iter_obj = resources.iterator();
				while (iter_obj.hasNext()){
					Resource object= iter_obj.next();
					
					if (subject.equals(object))
						continue;

					if (DataPmlLinked.isBadMapping(subject, object))
						continue;
					
					m.add(m.createStatement(subject,OWL.sameAs, object));
				}
				
			}
		}
	}
	
	
	private void step_search_baseline(){
		this.println_log("============================================");
		this.println_log("4. Search Baseline ");
		
		DataSmartMap current = new DataSmartMap();
		current.copy(m_features);
		
		Iterator<String> iter = this.m_pml_data.m_map_url_step.keySet().iterator();
		while (iter.hasNext()){
			String szURL = iter.next();

			DataHyperGraph lg = this.m_pml_data.getHyperGraph(szURL);
			String root_uri = this.m_pml_data.m_map_url_defaultRoot.get(szURL);

			if (debug){
				this.println_log(lg.data_summary());
			}
			//parse solution
			current.put(FEATURE_SOLUTION,extract_solution(root_uri) );
			current.put(FEATURE_CONTEXT, CONTEXT_ORIGINAL);
			current.put(FEATURE_ALGORITHM, ALG_ORIGINAL);

			this.do_print_solution_stat(null, lg, current, root_uri);

			//save baseline for optimization search
			String solution=current.getAsString(FEATURE_SOLUTION);
			String szKey;
			
			szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_ONLY);
			m_baseline_quality.put(szKey, AgentHyperGraphOptimizeCnf.getQuality(lg, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_ONLY, this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof()));
			
			szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_NODE_ONLY);
			m_baseline_quality.put(szKey, AgentHyperGraphOptimizeCnf.getQuality(lg, AgentHyperGraphOptimizeCnf.OPTION_NODE_ONLY, this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof()));
			
			szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_NODE);
			m_baseline_quality.put(szKey, AgentHyperGraphOptimizeCnf.getQuality(lg, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_NODE, this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof()));
		}
	}
	
	private DataHyperGraph step_compute_linked_justification(){
		this.println_log("============================================");
		this.println_log("5. Compute Linked Justifications ");
		
		// apply mappings
		if (!ToolSafe.isEmpty(this.m_urls_mapping)){
			this.println_log("external mapping were used, size "+ this.m_mappings_external.size());
			this.m_pml_data.addMappings(this.m_mappings_external);
		}
		
		if (USE_STRING_MAPPING || (USE_STRING_MAPPING_ALT && ToolSafe.isEmpty(this.m_urls_mapping))){
			this.println_log("string mapping were used, size "+ this.m_mappings_string.size());
			this.m_pml_data.addMappings(this.m_mappings_string);
		}

		
		//combine and save it
		return  do_save_linked_justification();
	}
	
	private void step_search_linked_justification(DataHyperGraph lg){
		this.println_log("============================================");
		this.println_log("6. Search Linked Justification ");

		// set features
		DataSmartMap current = new DataSmartMap();
		current.copy(this.m_features);
		current.put(FEATURE_CONTEXT, CONTEXT_COMBINED);
		
		Iterator<String> iter = this.m_pml_data.m_map_url_step.keySet().iterator();
		while (iter.hasNext()){
			String szURL = iter.next();

			String root_uri = this.m_pml_data.m_map_url_defaultRoot.get(szURL);

			//parse solution
			current.put(FEATURE_SOLUTION, extract_solution(root_uri));
			this.m_config.put(CONFIG_ROOT_URI, root_uri);
			Resource root = ResourceFactory.createResource(root_uri);
			Integer root_id = this.m_pml_data.m_map_res_vertex.getGid(root);
			this.m_config.put(CONFIG_ROOT_URI_ID,root_id);


			this.do_optimize_one_solution(lg, current, this.m_config);
		}
	}		

	
	private void do_optimize_one_solution(DataHyperGraph lg, DataSmartMap features,  DataSmartMap config){
		String szConfigAlg = config.getAsString(CONFIG_USE_ALG);

		this.println_log("");
		this.println_log("+++++++++++++++++++++");
		this.println_log(String.format("[CONFIG]: %s", config.toString()));
		

		int root_id = Integer.parseInt(config.getAsString(CONFIG_ROOT_URI_ID));
		String solution= features.getAsString(FEATURE_SOLUTION);
		
		// simple traverse
		if (szConfigAlg.indexOf(ALG_TRAVERSE)>=0){
			this.println_log("-------"+AgentHyperGraphTraverse.class.getSimpleName()+"-------");
			features.put(FEATURE_ALGORITHM, ALG_TRAVERSE);
			AgentHyperGraphTraverse alg = new AgentHyperGraphTraverse();
			alg.traverse(lg, root_id, -1, MAX_TIMEOUT_TRAVERSE,1);
			//this.println_log(alg.getSummary(lg));
			do_save_optimize_result(	
					alg,
					root_id,
					features,
					config
					);
		}
		
			
		// cnf, axioms only
		if (szConfigAlg.indexOf(ALG_AXIOM_CNF)>=0){
			this.println_log("-------"+AgentHyperGraphOptimizeCnf.class.getSimpleName()+" (axiom only)-------");
			features.put(FEATURE_ALGORITHM, ALG_AXIOM_CNF);
			AgentHyperGraphOptimizeCnf alg = new AgentHyperGraphOptimizeCnf(this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof(), AgentHyperGraphOptimizeCnf.OPTION_AXIOM_ONLY);

			String szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_ONLY);
			alg.m_runtime_best_quality = m_baseline_quality.get(szKey);			
			
			alg.traverse(lg, root_id, -1, MAX_TIMEOUT,1);
			//this.println_log(alg.getSummary(lg));
			do_save_optimize_result(	
					alg, 
					root_id,
					features,
					config
					);
		}
		

		// cnf , node only
		if (szConfigAlg.indexOf(ALG_NODE_CNF)>=0){
			this.println_log("-------"+AgentHyperGraphOptimizeCnf.class.getSimpleName()+" (node)-------");
			features.put(FEATURE_ALGORITHM, ALG_NODE_CNF);
			AgentHyperGraphOptimizeCnf alg = new AgentHyperGraphOptimizeCnf(this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof(), AgentHyperGraphOptimizeCnf.OPTION_NODE_ONLY);

			String szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_NODE_ONLY);
			alg.m_runtime_best_quality = m_baseline_quality.get(szKey);			
			

			alg.traverse(lg, root_id, -1, MAX_TIMEOUT,1);
			//this.println_log(alg.getSummary(lg));
			do_save_optimize_result(	
					alg,
					root_id,
					features,
					config
					);
		}		
		
		// cnf , axiom and then node 
		if (szConfigAlg.indexOf(ALG_AXIOM_NODE_CNF)>=0){
			this.println_log("-------"+AgentHyperGraphOptimizeCnf.class.getSimpleName()+" (axiom -> node)-------");
			features.put(FEATURE_ALGORITHM, ALG_AXIOM_NODE_CNF);
			AgentHyperGraphOptimizeCnf alg = new AgentHyperGraphOptimizeCnf(this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof(), AgentHyperGraphOptimizeCnf.OPTION_AXIOM_NODE);

			
			//String szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_NODE);
			//alg.m_runtime_best_quality = m_baseline_quality.get(szKey);	
			//String szContext = DataQname.extractNamespaceUrl(config.getAsString(CONFIG_ROOT_URI));
			//DataHyperGraph baselineGraph = lg.getHyperGraphByContext(szContext);
			//alg.addPreferredVertices(baselineGraph.getVertices());

			alg.traverse(lg, root_id, -1, MAX_TIMEOUT,1);
			this.println_log(alg.getSummary(lg));
			do_save_optimize_result(	
					alg,
					root_id,
					features,
					config
					);
		}		
		
		// cnf ,  node then axiom
		if (szConfigAlg.indexOf(ALG_NODE_AXIOM_CNF)>=0){
			this.println_log("-------"+AgentHyperGraphOptimizeCnf.class.getSimpleName()+" (axiom -> node)-------");
			features.put(FEATURE_ALGORITHM, ALG_NODE_AXIOM_CNF);
			AgentHyperGraphOptimizeCnf alg = new AgentHyperGraphOptimizeCnf(this.m_pml_data.getVertexCnf(), this.m_pml_data.getVertexFof(), AgentHyperGraphOptimizeCnf.OPTION_NODE_AXIOM);

			
			//String szKey = String.format("%s,%d", solution, AgentHyperGraphOptimizeCnf.OPTION_AXIOM_NODE);
			//alg.m_runtime_best_quality = m_baseline_quality.get(szKey);	
			//String szContext = DataQname.extractNamespaceUrl(config.getAsString(CONFIG_ROOT_URI));
			//DataHyperGraph baselineGraph = lg.getHyperGraphByContext(szContext);
			//alg.addPreferredVertices(baselineGraph.getVertices());

			alg.traverse(lg, root_id, -1, MAX_TIMEOUT,1);
			this.println_log(alg.getSummary(lg));
			do_save_optimize_result(	
					alg,
					root_id,
					features,
					config
					);
		}		
	}	

	private void do_save_optimize_result(AgentHyperGraphTraverse alg, Integer root_id,  DataSmartMap features,  DataSmartMap config){
		// generate pml
		Iterator<DataHyperGraph> iter = alg.getSolutions().iterator();
		int id =0;
		
		// only print one pml (we may produce many)
		if (iter.hasNext()){
			DataHyperGraph lg = iter.next();
			
			id++;
			String szFilename= String.format("%s/%s/%s.%s.%s.combine%d.owl",
					config.getAsString(CONFIGURE_BASE_FILE),
					features.getAsString(FEATURE_PROBLEM),
					features.getAsString(FEATURE_SOLUTION),
					features.getAsString(FEATURE_CONTEXT),
					features.getAsString(FEATURE_ALGORITHM),
					id);
			
			String szNamespace= String.format("%s/%s/%s.%s.%s.combine%d.owl#",
					config.getAsString(CONFIGURE_BASE_HTTP),
					features.getAsString(FEATURE_PROBLEM),
					features.getAsString(FEATURE_SOLUTION),
					features.getAsString(FEATURE_CONTEXT),
					features.getAsString(FEATURE_ALGORITHM),
					id);
			
			Model m=this.do_save_pml(szNamespace, szFilename, lg);

			ToolPML2Writer writer = new ToolPML2Writer(m, szNamespace);
			String  root_uri = writer.createResource(root_id).toString();

			//print stat
			do_print_solution_stat(alg, lg, features, root_uri);
		}		
	}
	
	private Model do_save_pml(String szNamespace, String szFilename, DataHyperGraph lg){
		// write pml file
		Model m= this.m_pml_data.getPmlModel(lg,szNamespace);

		try {
			String temp = ToolJena.printModelToString(m);
			ToolIO.pipeStringToFile(temp, szFilename, false);
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
		
		
		return m;
	}	
	
	
	private void do_print_solution_stat(AgentHyperGraphTraverse alg, DataHyperGraph lg, DataSmartMap features, String root_uri){
		

		DataSmartMap data = new DataSmartMap();
		
		Set<Integer> nodes = lg.getVertices();
		data.put("nodes", nodes.size());
		
		nodes.retainAll(this.m_pml_data.getVertexCnf());
		data.put("nodes(cnf)", nodes.size());

		Set<Integer> axioms = lg.getAxioms();
		data.put("axioms", axioms.size());
		
		axioms.retainAll(this.m_pml_data.getVertexCnf());
		data.put("axioms(cnf)", axioms.size());
		
		DataSmartMap data_runtime = new DataSmartMap();
		data_runtime.put("solution_cnt", (null!=alg) ? alg.getSolutionCount(): "-" );

		data_runtime.put("process_time(sec)", (null!=alg) ? alg.getProcessSeconds():"-");

		if (null!=alg){
			if (alg.isAboveLimitSolutionCount())
				data_runtime.put("finish", "isAboveLimitSolutionCount");
			else if (alg.isAboveLimitTimeout())
				data_runtime.put("finish", "isAboveLimitTimeout");
			else
				data_runtime.put("finish", "ok");
		}else{
			data_runtime.put("finish", "-");
		}
		
		//save to result.csv
		String szCSV= String.format("%s%s%s",features.toCSVrow(), data.toCSVrow(), data_runtime.toCSVrow());
		m_out_csv.println(szCSV);
		m_out_csv.flush();

		//print
		this.println_log(String.format("[RESULT] %s %s %s",features.toCSVrow(), data, data_runtime));
		this.println_log_sysinfo();

		//save to index.html
		try {
			data.put("root_uri_browser", getIWBrowserLink(root_uri));
		} catch (Sw4jException e) {
		}
		
		if(m_bFirstResult){
			m_out_index.println(String.format("<tr>%s%s%s</tr>",features.toHTMLtablerowheader(), data.toHTMLtablerowheader(), data_runtime.toHTMLtablerowheader()));
			m_bFirstResult=false;
		}
		m_out_index.println(String.format("<tr>%s%s%s</tr>",features.toHTMLtablerow(), data.toHTMLtablerow(), data_runtime.toHTMLtablerow()));
		m_out_index.flush();
		
	}	
	
	private static String getIWBrowserLink(String root_uri) throws Sw4jException{
		return String.format("<a href=\"http://browser.inference-web.org/iwbrowser/NodeSetBrowser?w=900&mg=5&st=Dag&fm=Raw&url=%s\">%s</a><br/>", ToolURI.encodeURIString(root_uri), root_uri);
	}
	
	private static String extract_solution(String szUri){
		//parse solution
		int index2 = szUri.lastIndexOf("/");
		int index1 = szUri.lastIndexOf("/", index2-1);
		String solution = szUri.substring(index1+1, index2);
		return solution;
	}
	
	private void println_log_sysinfo(){
		this.println_log(String.format("[SYS-INFO] time: %s | free memory: %d",
				ToolString.formatXMLDateTime(System.currentTimeMillis()),
				Runtime.getRuntime().freeMemory() ));

	}
}
