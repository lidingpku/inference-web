package org.inference_web.iwapp.iwsearch;

import org.inference_web.pml.AgentPmlCrawler;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolString;

public class TaskCrawlPmlp {
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
		AgentPmlCrawler crawler = new AgentPmlCrawler();
		for (int i=0; i<arySeed.length; i++){
			crawler.crawl(arySeed[i][0]);
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
}
