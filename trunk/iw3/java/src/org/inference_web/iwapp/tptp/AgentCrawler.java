package org.inference_web.iwapp.tptp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import sw4j.task.load.TaskLoad;
import sw4j.util.ToolSafe;
import sw4j.util.web.AgentURLExtractor;

public class AgentCrawler {
	public static boolean debug = false;
	public boolean canContinue(){
		if (m_max_results>0 && getResultSize()>m_max_results)
			return false;
			
		return true;
	}
	
	public int getResultSize(){
		return m_results.size();
	}
	
	public String m_seed_url = null;
	public int m_max_crawl_depth=10;
	public int m_max_results= -1;
	public Set<String> m_allowed_url_patterns = new HashSet<String>();

	public Set<String> m_results=  new TreeSet<String>();

	public void init(String sz_seed_url){
		String sz_pattern = sz_seed_url+".*";
		sz_pattern = sz_pattern.replaceAll("\\+", "\\\\+");
		init(sz_seed_url, sz_pattern);
	}

	public void init(String sz_seed_url, String sz_pattern){
		HashSet<String> patterns = new HashSet<String>();
		patterns.add(sz_pattern);
		init(sz_seed_url, patterns);
	}

	public void init(String seed_url, Set<String> allowed_url_patterns){
		m_seed_url = seed_url;
		m_allowed_url_patterns = new HashSet<String>(allowed_url_patterns);
	}

	public void crawl() {
		Set<String> visited=  new HashSet<String>();
		Set<String> tovisit=  new TreeSet<String>();

		//init
		tovisit.add(m_seed_url);
	

		//limited depth crawl
		for (int i = 0; i<= this.getMaxCrawlDepth() && !tovisit.isEmpty(); i++){
			// init next round to visit
			TreeSet<String> to_visit_next = new TreeSet<String>();
			
			Iterator<String> iter= tovisit.iterator();
			while (iter.hasNext()){
				if (!canContinue())
					return;

				// init
				String szURL = iter.next();
				TaskLoad data_load = null;
				
				visited.add(szURL);

				// retrieve content from the url
				if (debug)
					System.out.println("download file");
				data_load = TaskLoad.load(szURL);				
				
				if (!data_load.isLoadSucceed())
					continue;

				//if there more links to follow, then extract URls
				String szText = data_load.getContent();
				if (!ToolSafe.isEmpty(szText)){
					//get more links
					Set<String> links =AgentURLExtractor.process(szText, data_load.getXmlBase());
					
					//remove visited
					links.removeAll(visited);
					
					//check if url is allowed
					for (String link: links){
						// check if the URL is allowed to be crawl
						if (!this.isAllowed(link)){
							System.out.println("disallowed url: "+ link);
							continue;
						}
						
						to_visit_next.add(link);

						//skip directory URL
						//if (isWebDirectory(link)){
							//System.out.println(szURL);
						//	continue;
						//}
						
						m_results.add(link);
					}
				}
			}
			
			tovisit = to_visit_next;
		}		
	}
	
	private int getMaxCrawlDepth() {
		return m_max_crawl_depth;
	}
	
	public static boolean isWebDirectory(String szURL) {
		return szURL.endsWith("/");
	}

	private boolean isAllowed(String szURL) {
		if (m_seed_url.equals(szURL))
			return true;
		
		if (m_allowed_url_patterns.size()==0)
			return true;
		
		Iterator<String> iter = m_allowed_url_patterns.iterator();
		while (iter.hasNext()){
			String pattern = iter.next();
			if (szURL.matches(pattern))
				return true;
		}
		return false;
	}
	
}
