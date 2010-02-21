package org.inference_web.pml;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.rdf.load.AgentModelLoader;
import sw4j.task.load.AgentSimpleHttpCrawler;
import sw4j.vocabulary.pml.IW200407;
import sw4j.vocabulary.pml.PMLP;


public class AgentPmlCrawler {
	public TreeSet<String> m_results = new TreeSet<String>();
	public TreeSet<String> m_errors = new TreeSet<String>();
	
 	public static Set<String> crawl_quick(String sz_seed_url, boolean b_validate){
		AgentPmlCrawler crawler = new AgentPmlCrawler();
		crawler.crawl(sz_seed_url, b_validate);
		return crawler.m_results;
	}
	
	/*

	public void crawl(String sz_seed_url){
		HashSet<String> patterns = new HashSet<String>();
		patterns.add( sz_seed_url+".*");
		crawl(sz_seed_url, patterns, true);
	}
*/
	public void crawl(String sz_seed_url, boolean b_validate){
		crawl(sz_seed_url, null, b_validate);
	}

	
	public void crawl(String sz_seed_url, Set<String> set_sz_pattern, boolean b_validate){
		AgentSimpleHttpCrawler crawler = new AgentSimpleHttpCrawler();
		if (null==set_sz_pattern){
			crawler.init(sz_seed_url);
		}else{
			crawler.init(sz_seed_url,set_sz_pattern);			
		}

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