package org.inference_web.iwapp.tptp;

import org.inference_web.app.tptp.AgentCrawler;
import org.junit.Test;


public class AgentCrawlerTest {
	@Test
	public void test_file_load() {
		String [][] aryURI_prefix = new String [][]{
//				{"/work/iw/swa/test/task_20080502_pml_iwregistry.n3",  "http://inference-web.org/registry/.*"},
//				{"/work/iw/swa/test/task_20080502_pml_iwproofs.n3",  "http://inference-web.org/proofs/.*"},
				{"http://inference-web.org/proofs/tptp/Solutions/",  "http://inference-web.org/proofs/tptp/Solutions/.*"},
		};
		System.out.println("test Task Crawler");
		for (int i=0; i<aryURI_prefix.length; i++){
			
			AgentCrawler crawler = new AgentCrawler();
			crawler.init(aryURI_prefix[i][0], aryURI_prefix[i][1]);
			crawler.m_max_crawl_depth=1;
			crawler.crawl();

			System.out.println(crawler.m_results.size());
			System.out.println(crawler.m_results);
		}
		
	}
}
