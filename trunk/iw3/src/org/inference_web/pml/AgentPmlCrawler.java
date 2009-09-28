package org.inference_web.pml;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLP;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.task.load.AgentSimpleHttpCrawler;


public class AgentPmlCrawler {
	public TreeSet<String> m_results = new TreeSet<String>();
	public TreeSet<String> m_errors = new TreeSet<String>();
	
	
	public static Set<String> crawl_quick(String sz_seed_url){
		AgentPmlCrawler crawler = new AgentPmlCrawler();
		crawler.crawl(sz_seed_url, true);
		return crawler.m_results;
	}
	
	public void crawl(String sz_seed_url, boolean b_validate){
		crawl(sz_seed_url, sz_seed_url+".*", b_validate);
	}
	
	public void crawl(String sz_seed_url, String sz_pattern, boolean b_validate){
		AgentSimpleHttpCrawler crawler = new AgentSimpleHttpCrawler();
		crawler.m_seed_url = sz_seed_url;
		crawler.m_allowed_url_patterns.add(sz_pattern);
		crawler.m_max_crawl_depth=3;
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