package org.inference_web.iwapp.crawler;

import java.util.Iterator;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Model;

import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLP;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.task.load.AgentSimpleHttpCrawler;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;

public class PmlCrawler {
	public static void main(String[] args){
		String [][] arySeed = new String [][]{
				{"http://inference-web.org/registry/"},
				{"http://iw.cs.utep.edu/astronomy/solar/registry/"},
				{"http://iw.cs.utep.edu/earthscience/crustal/registry/"},
				{"http://iw.cs.utep.edu/earthscience/gravity/registry/"},
				{"http://iw.cs.utep.edu/earthscience/seismic/"},
				{"http://iw.vsto.org/registry"},
				{"http://escience.rpi.edu/pml/"},
		};
		String szFileName= "files/iwsearch/pmlp-urls.txt";
		PmlCrawler crawler = new PmlCrawler();
		for (int i=0; i<arySeed.length; i++){
			crawler.crawl(arySeed[i][0],arySeed[i][0]+".*");
		}
		String szContent =ToolString.printCollectionToString(crawler.m_results);
		System.out.println(szContent);
		try {
			ToolIO.pipeStringToFile(szContent, szFileName,false);
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
		
		szContent =ToolString.printCollectionToString(crawler.m_errors);
		szFileName= "files/iwsearch/pmlp-error-urls.txt";
		System.out.println(szContent);
		try {
			ToolIO.pipeStringToFile(szContent, szFileName,false);
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
	}
	
	public TreeSet<String> m_results = new TreeSet<String>();
	public TreeSet<String> m_errors = new TreeSet<String>();
	
	public void crawl(String szSeedUrl, String szPattern){
		AgentSimpleHttpCrawler crawler = new AgentSimpleHttpCrawler();
		crawler.m_seed_url = szSeedUrl;
		crawler.m_allowed_url_patterns.add(szPattern);
		crawler.m_max_crawl_depth=3;
		//crawler.m_max_results =10;
		crawler.crawl();	
		
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
	}
}