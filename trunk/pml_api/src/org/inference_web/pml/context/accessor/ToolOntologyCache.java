/* Copyright 2007 Inference Web Group.  All Rights Reserved. */
package org.inference_web.pml.context.accessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;

public class ToolOntologyCache {
	/**
	 * whether or not use cache (this is critical to PML API's offline mode)
	 */
	public static boolean bUseOntologyCache = true;
	/**
	 * whether recursive import ontologies
	 */
	public static boolean bRecursivelyImport = true;

	private static String url2filename(String szURL){
		String temp = szURL;
		temp =temp.replaceAll("http://", "");
		temp =temp.replaceAll("/", "___");
		return temp;
	}
	
	
	private void downloadFile(String szURL, String szFilename){
        URL url = null;
        try {
			url = new URL(szURL);
	        URLConnection conn = null;
	        conn = url.openConnection();
	        conn.connect();
	        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

	        ByteArrayOutputStream bytearray=new ByteArrayOutputStream();
	    	final int BUFFER_SIZE= 8192;
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int nRead;
	        while ((nRead=bis.read(buffer,0,BUFFER_SIZE)) != -1) {
	        	bytearray.write(buffer,0,nRead);
	        }
	        
	        FileOutputStream out = new FileOutputStream(szFilename);
			out.write(bytearray.toByteArray());
			out.close();
	         
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void initOntologyCache(){
		String [] aryOntologyURL = new String []{
				"http://inference-web.org/2.0/ds.owl",
				"http://inference-web.org/2.0/pml-provenance.owl",
				"http://inference-web.org/2.0/pml-justification.owl",
				"http://inference-web.org/2.0/pml-owl.owl",
		};
		
		ToolOntologyCache cache = new ToolOntologyCache();
		for (int i=0; i< aryOntologyURL.length; i++){
			String szOntologyURL = aryOntologyURL[i];
	        String temp = cache.getClass().getPackage().getName();
	        temp = temp.replace('.', '/');
			cache.downloadFile( szOntologyURL, "src/"+temp+"/"+url2filename(szOntologyURL));
		}
		
	}
	
	public InputStream loadFromOntologyCache(String szOntURL) {
	    
	    try {
	    	InputStream is = this.getClass().getResourceAsStream(url2filename(szOntURL));
	    	return is;
	    }catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
    	}
	}	

	public static void loadModel(Model m, String szOntURL){
		loadModel(m, szOntURL, new HashSet());
	}
	
	private static void loadModel(Model m, String szOntURL, HashSet visited){
		// preprocess URL
		int index = szOntURL.indexOf("#");
		if (index>0)
			szOntURL = szOntURL.substring(0,index);
		else if (index==0)
			return; // no host infomation

		if (visited.contains(szOntURL))
			return;
		
		// create a default model
		//Model local = ModelFactory.createDefaultModel();

		//check if the ontology has been ached in local cache directory
		InputStream in =null;
		if (bUseOntologyCache){
			in = new ToolOntologyCache().loadFromOntologyCache(szOntURL);
			if (null!=in){
				m.read(in, szOntURL);
//				System.out.println("load ontology from cached - "+szOntURL);
			}
		}
		
		//	load the URL otherwise
		if (null==in){
			m.read(szOntURL);
//			System.out.println("load ontology from web - "+szOntURL);
		}
		
		
		// append this model to 
		//m.add(local);
		visited.add(szOntURL);

		//recusively load imported
		if (!bRecursivelyImport)
			return;
		
		Iterator iter = m.listObjectsOfProperty(OWL.imports);
		while (iter.hasNext()){
			String szNewOntURL =  iter.next().toString();
			loadModel(m, szNewOntURL, visited);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// init
		initOntologyCache();

		// test
		OntModel tempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
		loadModel(tempModel,"http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl", new HashSet());
		System.out.println(tempModel.getNsPrefixMap());
		tempModel.setNsPrefixes(tempModel.getNsPrefixMap());
		tempModel.write(System.out,"RDF/XML-ABBREV");
		//System.out.println(tempModel);
	}

}
