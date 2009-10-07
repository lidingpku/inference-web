package org.inference_web.iwapp.iwsearch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ExampleIWSearchClient {

	public static void main(String[] arg){
        try {
	        // step 1. prepare service URI
        	String szService = "http://onto.rpi.edu/iwsearch/iwsearch?";

        	// step 2. prepare query
        	String szQuery ="";
        	szService += "usesSearchService="+"search_pml_instance";
        	szService += "&";
        	szService += "hasSearchString="+URLEncoder.encode("type:Person","UTF-8");
        	szService += "&";
        	szService += "usesSearchResultSyntax="+"RDFXML";

        	// step 3. genearte query URL
        	String url =  szService+ szQuery;

        	// step 3. genearte query URL
        	Model m  = ModelFactory.createDefaultModel();
        	m.read(url);
        	
        	// check if the results
        	Iterator iter = m.listSubjectsWithProperty(RDF.type, IWSEARCH.InstanceMetadata );
        	int cnt=1;
        	while (iter.hasNext()){
        		Resource subject = (Resource) iter.next();

        		// get the metadata about this resource
        		String label = getAttributeValue(m, subject, RDFS.label);
        		String uri  = getAttributeValue(m, subject, DC.identifier);
        		String source  = getAttributeValue(m, subject, DC.source);
        		String date = getAttributeValue(m, subject, DC.date);
        		
        		System.out.print(cnt++);
        		System.out.print("\t");
        		System.out.print(label);
        		System.out.print("\t");
        		System.out.print(uri);
        		System.out.print("\t");
        		System.out.print(source);
        		System.out.print("\t");
        		System.out.print(date);
        		System.out.print("\t");
        		System.out.println();
        	}

        
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getAttributeValue(Model m, Resource subject, Property property){
		Iterator iter = m.listObjectsOfProperty(subject, property);
		if (iter.hasNext()){
			RDFNode value = (RDFNode) iter.next();
			if (value.isURIResource()){
				return ((Resource)value).getURI();
			}else if (value.isLiteral()){
				return ((Literal)value).getString();
			}
		}
		return null;                      
	}   	            
}
