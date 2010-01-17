package org.inference_web.pml;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLP;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.task.load.AgentSimpleHttpCrawler;


public class AgentPmlCrawler {
	public int MAX_CRAWL_DEPTH=10;
	public TreeSet<String> m_results = new TreeSet<String>();
	public TreeSet<String> m_errors = new TreeSet<String>();
	
	
	public static Set<String> crawl_quick(String sz_seed_url, boolean b_validate){
		AgentPmlCrawler crawler = new AgentPmlCrawler();
		crawler.crawl(sz_seed_url, b_validate);
		return crawler.m_results;
	}

/*	public void crawl(String sz_seed_url){
		HashSet<String> patterns = new HashSet<String>();
		patterns.add( sz_seed_url+".*");
		crawl(sz_seed_url, patterns, true);
	}
*/
	public void crawl(String sz_seed_url, boolean b_validate){
		HashSet<String> patterns = new HashSet<String>();
		patterns.add( sz_seed_url+".*");
		crawl(sz_seed_url, patterns, b_validate);
	}

	
	public void crawl(String sz_seed_url, Set<String> set_sz_pattern, boolean b_validate){
		AgentSimpleHttpCrawler crawler = new AgentSimpleHttpCrawler();
		crawler.m_seed_url = sz_seed_url;
		for(String sz_pattern: set_sz_pattern)
			crawler.m_allowed_url_patterns.add(sz_pattern);
		crawler.m_max_crawl_depth= MAX_CRAWL_DEPTH;
		//crawler.m_max_results =10;
		crawler.crawl();	
		
		if (b_validate){
			//confirm RDF and PML
			Iterator<String> iter = crawler.m_results.iterator();
			while (iter.hasNext()){
				String szUrl = iter.next();
				
				//load rdf
				AgentModelLoader loader = new AgentModelLoader(szUrl);
				Model m = loader.getModelData();
				if (null==m)
					continue;
				
				if (loader.getParseRdf().getReport().hasError()){
					m_errors.add(szUrl);
					System.out.println(loader.getParseRdf().getReport().toString());
					//System.out.println(szUrl);
				}
					
				if (m.getNsPrefixMap().values().contains(PMLP.getURI())){
					m_results.add(szUrl);
				}
				if (m.getNsPrefixMap().values().contains(IW200407.getURI())){
					m_results.add(szUrl);
				}
			}
		}else{
			this.m_results.addAll(crawler.m_results);
		}
	}
}