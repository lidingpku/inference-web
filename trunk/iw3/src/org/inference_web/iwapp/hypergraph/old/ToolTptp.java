package org.inference_web.iwapp.hypergraph.old;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import sw4j.task.load.ToolLoadHttp;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.web.AgentURLExtractor;

public class ToolTptp {
	public static String TPTP_BASE_URL = "http://inference-web.org/proofs/tptp/Solutions/";
	
	public static void main(String[] args) {
		
    	String szProblemFilename = "files/hypergraph/problem.txt";
    	
    	test(szProblemFilename, false);

    	test(szProblemFilename, true);
 	}
	
	
	private static void test(String szProblemFilename, boolean bUseStringMapping ){
		System.out.println("starting at - "+ szProblemFilename);
    	
		try {
			ToolIO.pipeStringToFile("#no mapping\n","files/hypergraph/no-mapping.txt",false, false);
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
        	BufferedReader in = new  BufferedReader(new FileReader(szProblemFilename));
			String line =null;
			while (null!=(line=in.readLine())){
				line= line.trim();

				//commented
				if (line.startsWith("#"))
					continue;
				
				process_one_problem(line, bUseStringMapping);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	private static void process_one_problem(String szProblem, boolean bUseStringMapping){
		AgentCombine acp = new AgentCombine();
		String subdir="combine200909-"+bUseStringMapping;
		init_one_problem(acp, 
				"http://plato.cs.rpi.edu/iw/hypergraph/"+subdir, 
				"files/hypergraph/"+subdir,	
				szProblem);
		
		// do not combine files without mapping file
		if (acp.m_urls_mapping.isEmpty()){
			try {
				ToolIO.pipeStringToFile(szProblem+"\n","files/hypergraph/no-mapping.txt",false, true);
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (bUseStringMapping){
			acp.USE_STRING_MAPPING=true;
			acp.process();
		}else{
			if (!acp.m_urls_mapping.isEmpty())
				acp.process();
		}
	}
	
	

	
	
	public static void init_one_problem(AgentCombine acp, String base_http, String base_file, String szProblem){
		init_pml_mappings(acp, szProblem);
		init_config(acp, base_http, base_file, szProblem);
	}
	
	


	private static void init_pml_mappings( AgentCombine acp, String szProblem){
		String szUrlTptpSolution = String.format("%s%s/%s/", TPTP_BASE_URL, szProblem.substring(0,3), szProblem);
		
		System.out.println("processing " + szUrlTptpSolution);
		
		String ret = ToolLoadHttp.wget(szUrlTptpSolution);

		if (ToolSafe.isEmpty(ret))
			return;
		
		Set<String> url_engines = AgentURLExtractor.process(ret, szUrlTptpSolution);
		
		//use the solution generate by the latest version of an engine
		Iterator<String> iter = url_engines.iterator();
		HashMap<String,String> map_engine_name_url = new HashMap<String,String>(); 
		while (iter.hasNext()){
			String szUrlEngine = iter.next();
			
			if (!szUrlEngine.endsWith("/") || !szUrlEngine.startsWith(szUrlTptpSolution))
				continue;

			String szEngine = szUrlEngine.substring(szUrlTptpSolution.length(), szUrlEngine.indexOf("---"));
			
			String szUrlOld = map_engine_name_url.get(szEngine);
			if (null==szUrlOld || szUrlOld.compareTo(szUrlEngine)<0)
				map_engine_name_url.put(szEngine, szUrlEngine);			
		}
		
		iter = map_engine_name_url.values().iterator();
		while (iter.hasNext()){
			String szUrlEngine = iter.next();
		

			if (!szUrlEngine.endsWith("/") || !szUrlEngine.startsWith(szUrlTptpSolution))
				continue;
			//skip some unwanted engines
			if (szUrlEngine.indexOf("Faust")>0)
				continue;
			
			if (szUrlEngine.indexOf("SPASS")>0)
				continue;
			
			ret = ToolLoadHttp.wget(szUrlEngine);
			Set<String> urls = AgentURLExtractor.process(ret, szUrlEngine);
			//System.out.println(ToolString.printCollectionToString(urls));
			
			String szUrlAnswer = String.format("%sanswer.owl", szUrlEngine);
			if (urls.contains(szUrlAnswer))
				acp.m_urls_pml.add(szUrlAnswer);

			String szUrlEqualNS = String.format("%sequalNS.owl", szUrlEngine);
			if (urls.contains(szUrlEqualNS))
				acp.m_urls_mapping.add(szUrlEqualNS);

		}
		
	}
	
	private static void init_config( AgentCombine acp, String base_http, String base_file, String szProblem){
		acp.MAX_OUTPUT = 1;
//		acp.MAX_TRAVERSE = 1000;
//		acp.MAX_TIMEOUT = 5*60*1000;

		acp.m_features = new DataSmartMap();
		acp.m_features.put(AgentCombine.FEATURE_PROBLEM, szProblem);

		acp.m_config = new DataSmartMap();
		acp.m_config.put(AgentCombine.CONFIGURE_BASE_FILE, base_file);
		acp.m_config.put(AgentCombine.CONFIGURE_BASE_HTTP, base_http);
		acp.m_config.put(AgentCombine.CONFIG_USE_ALG, 
		//		AgentCombine.ALG_NODE_CNF +","+
		//		AgentCombine.ALG_AXIOM_CNF   +","+
				AgentCombine.ALG_AXIOM_NODE_CNF   +","+
				AgentCombine.ALG_NODE_AXIOM_CNF   +","+
				AgentCombine.ALG_TRAVERSE );
	}

}
