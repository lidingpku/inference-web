package org.inference_web.iwapp.iwsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import sw4j.task.load.TaskLoad;
import sw4j.util.ToolSafe;

abstract public class AbstractIndexer {
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass());
	}
	
	
	public void index(String szFileOrUri, String szPathIndex){
		AgentLucene lucence= new AgentLucene(szPathIndex);
		
		index(lucence, parseUrls(szFileOrUri), false);
	}
	
	public static Set<String> parseUrls(String szUrlFile){
		HashSet<String> urls = new HashSet<String>();
		
		TaskLoad oTaskLoad = TaskLoad.load(szUrlFile);
		if (oTaskLoad.isLoadSucceed()){
			oTaskLoad.getContent();
		}
		
		BufferedReader in = new BufferedReader(new StringReader(oTaskLoad.getContent()));
		String line = "";
		try {
			while (null!=(line = in.readLine())){
				urls.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urls;
	}
	
	public void index(AgentLucene lucene, Set<String> sources, boolean bRefresh){
		if (ToolSafe.isEmpty(sources))
			return;

		try {
			lucene.getIndexWriter(bRefresh);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}			
		
		try {
			Iterator<String> iter = sources.iterator();
			while (iter.hasNext()){
				String szSource = iter.next();
				
				do_delete(lucene, szSource);
				
				do_add(lucene, szSource);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

		lucene.closeIndexWriter();

	}
	


	
	abstract void do_delete(AgentLucene lucene, String szSource) throws IOException ;
	

	abstract void do_add(AgentLucene lucene, String szSource) throws CorruptIndexException, IOException ;
	
	
	
	
}
