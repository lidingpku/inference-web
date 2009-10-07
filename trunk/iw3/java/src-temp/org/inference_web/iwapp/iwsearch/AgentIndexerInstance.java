package org.inference_web.iwapp.iwsearch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import sw4j.app.oie.AgentModelLoaderOie;
import sw4j.pellet.ToolPellet;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.DataInstance;
import sw4j.rdf.util.ToolJena;
import sw4j.rdf.util.ToolModelAnalysis;
import sw4j.task.load.TaskLoad;
import sw4j.util.AbstractPropertyValuesMap;
import sw4j.util.DataPVHMap;
import sw4j.util.DataQname;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class AgentIndexerInstance extends AbstractIndexer{

	public static final String FIELD_URI = "uri";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_ID = "id";
	
	public static final String FIELD_SOURCE = "source";
	public static final String FIELD_LABEL = "label";
	public static final String FIELD_DATE_MODIFIED = "modified";
	public static final String FIELD_DATE_SUBMIT = "indexed";
	public static final String FIELD_LABEL_DATATYPE = "label_datatype";
	public static final String FIELD_MD5 = "md5";
	public static final String FIELD_TAG = "tag";

	
	public static void main(String[] args) {
		File dirJob;
		try {
			//dirJob = IWSearchSettings.loadDefault().getInputDir();
			String path_index = IWSearchSettings.loadDefault().getIndexPath(IWSearchSettings.OPTION_INDEX_INSTANCE);
			
			AgentIndexerInstance indexer = new AgentIndexerInstance();
			indexer.index( "files/iwsearch/iwsearch_input_urls.txt", path_index );
//			printIndex(path_index,false);
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void do_delete(IndexWriter writer, String szSource) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
		Term t = new Term(FIELD_SOURCE, szSource);
		writer.deleteDocuments(t);		
	}


	
	/**
	 * index a set of URLs  (update entries)
	 * 
	 * @param urls
	 * @param path_index
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public void do_add(IndexWriter writer, String szSource ) throws CorruptIndexException, IOException{
	
		TaskLoad result = TaskLoad.load(szSource);
		if (null==result)
			return;
		
		AgentModelLoaderOie aml = new 	AgentModelLoaderOie (result);
		//cached date modified
		Model m = aml.getModelData();
		long lastmodified= aml.getLoad().getLastmodified();
		
		if (null==m){
			getLogger().error("no RDF: "+szSource);
			return;
		}
		
		
		if (!aml.getInspectOwl().isConsistent()){
			getLogger().error("inconsistent: "+szSource);
			return;
		}
		
		Model model_all = aml.getModelAll_deduction();

		//TODO the following code can be optimized 
		AbstractPropertyValuesMap<Resource,Resource> mapInstanceTypes = ToolModelAnalysis.getMapInstanceTypes(model_all);
	    AbstractPropertyValuesMap<Resource,Resource> mapInstanceTypes_direct = ToolModelAnalysis.getMapInstanceTypes(m);
	    Map<Resource,DataInstance> mapInstances= ToolModelAnalysis.listInstanceDescription(m);
	    
		Iterator<DataInstance> iter_inst = mapInstances.values().iterator();
		while (iter_inst.hasNext()){
			DataInstance ind = iter_inst.next();

		    // index one pml instance
			do_add(
					writer,
					ind,
					mapInstances,
					mapInstanceTypes,
					mapInstanceTypes_direct,
					szSource,
					lastmodified);
			
		}

	}		

	
	/**
	 * index an instance  
	 * 
	 * @param ind	the instance to be index
	 * @param mapInstances	
	 * @param mapInstanceTypes	all applicable types of the instance
	 * @param mapInstanceTypes_direct	the most specific type of the instance
	 * @param szSourceURL   the URL of the document that describes the instance
	 * @param lastmodified	the last modified time of the document
	 * @return
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	protected void do_add(
			IndexWriter writer,
			DataInstance ind,
			Map<Resource,DataInstance> mapInstances,
			AbstractPropertyValuesMap<Resource, Resource> mapInstanceTypes,
			AbstractPropertyValuesMap<Resource, Resource> mapInstanceTypes_direct,
			String szSource, 
			long lastmodified) throws CorruptIndexException, IOException
	{
		
	    // skip trivial definition
	    if (ind.getModel().size()<=1)
	    	return ;

		//skip anonymous instance, cannot make them addressible
		if (ind.getSubject().isAnon()){
	    	return ;
		}
		
	    Document doc = new Document();
	    
	    //URI  + tag
	    indexUri(ind,doc);
	    
	    // label + tag
	    indexLabel(ind,doc);

		// type + tag
	    indexType(ind, doc , mapInstanceTypes, mapInstanceTypes_direct);
	    
	    // source, last-modified
	    indexProvenance(doc, szSource, lastmodified );

	    // desc + tag
	    indexDescription(ind,doc);

		writer.addDocument(doc);
		
	}
	
	protected void indexUri(DataInstance ind, Document doc){
	    if (ind.getSubject().isURIResource()){
	    	doc.add(new Field(FIELD_URI, ind.getSubject().getURI(), Field.Store.YES, Field.Index.UN_TOKENIZED ));
	    	
	    	DataQname qname;
			try {
				qname = DataQname.create(ind.getSubject().getURI(),null);
		    	String temp = qname.getLocalname();
		    	
		    	if (!ToolSafe.isEmpty(temp)){
		            
		    		doc.add(new Field(FIELD_ID, temp, Field.Store.YES, Field.Index.UN_TOKENIZED));
		    		doc.add(new Field(FIELD_TAG, temp, Field.Store.NO, Field.Index.TOKENIZED ));
		    	}
			} catch (Sw4jException e) {
				e.printStackTrace();
			}
	    }		
	}

	protected void indexLabel(DataInstance ind, Document doc){
	    String label = null;
	    //TODO index label with better way
	    Property [] label_props= new Property[]{
	    		RDFS.label,
	    		DC.title,
	    		FOAF.name,
	    };
	    for (int i=0; i<label_props.length; i++){
	    	Property prop = label_props[i];
	    	String szTemp = ind.getPropertyValueAsString(prop); 
		   	if (!ToolSafe.isEmpty(szTemp)){
			   	if (ToolSafe.isEmpty(label)){
				    label = szTemp;
			   	}
			    doc.add(new Field(FIELD_TAG, prepareTokenizedLabel(label), Field.Store.NO, Field.Index.TOKENIZED ));
		    }
	    }	   	
	    
	    if (!ToolSafe.isEmpty(label))
		    doc.add(new Field(FIELD_LABEL, label, Field.Store.YES, Field.Index.UN_TOKENIZED ));		
	}
	
	protected void indexDescription(DataInstance ind, Document doc){
	    Property [] props= new Property[]{
	    		RDFS.comment,
	    		DC.description,
	    };
	    for (int i=0; i<props.length; i++){
	    	Property prop = props[i];
	   		NodeIterator iter = ind.listPropertyValue(prop);
	   		while (iter.hasNext()){
	   			RDFNode node =iter.nextNode();
	   			if (node.isAnon())
	   				continue;
	   			
	   			String temp = ToolJena.getNodeString(node);
	   			temp =  temp.replaceAll("\\p{Punct}", " ");
	   			StringTokenizer st = new StringTokenizer(temp);
	   			temp ="";
	   			while (st.hasMoreTokens()){
	   				String szToken = st.nextToken();
	   				if (szToken.length()<100)
	   					temp+=szToken+" ";
	   			}
	   			
			    doc.add(new Field(FIELD_TAG, temp, Field.Store.NO, Field.Index.TOKENIZED ));
	   		}
	    }	   	
	}

	protected void indexType(DataInstance ind, Document doc, AbstractPropertyValuesMap<Resource, Resource> mapInstanceTypes,
			AbstractPropertyValuesMap<Resource, Resource> mapInstanceTypes_direct){
	    if (mapInstanceTypes.getValuesCount(ind.getSubject() )>0){ 
		    Iterator<Resource> iter_type = mapInstanceTypes.getValues(ind.getSubject()).iterator();
		    while (iter_type.hasNext()){
		    	Resource type = iter_type.next();
			    //doc.add(new Field("type",type.getLocalName(), Field.Store.NO, Field.Index.TOKENIZED )); 
			    if (mapInstanceTypes_direct.getValues(ind.getSubject() ).contains(type))
			    	doc.add(new Field(FIELD_TYPE,type.getURI(), Field.Store.YES, Field.Index.TOKENIZED)); 
			    else
			    	doc.add(new Field(FIELD_TYPE,type.getLocalName(), Field.Store.NO, Field.Index.TOKENIZED )); 
		    }
	    }else{
	    	Iterator <Resource> iter_types = mapInstanceTypes_direct.getValues(ind.getSubject()).iterator();
	    	if (iter_types.hasNext()){
			    doc.add(new Field(FIELD_TYPE,iter_types.next().getURI(), Field.Store.YES, Field.Index.TOKENIZED)); 
	    	}
	    }
		
	}

	protected void indexProvenance(Document doc, String szSource, long lastmodified){
	    doc.add(new Field(FIELD_SOURCE,szSource, Field.Store.YES, Field.Index.UN_TOKENIZED ));

	    //index host URL
	    URI host;
		try {
			host = ToolURI.extractHostUrl(ToolURI.string2uri(szSource));
		    String szHostURL = host.toString();
		    szHostURL.replaceAll("http://", "");
		    doc.add(new Field(FIELD_SOURCE,szSource, Field.Store.NO, Field.Index.TOKENIZED )); 
		} catch (Sw4jException e) {
			//e.printStackTrace();
		}
	    

	    Date dateSubmit = new Date(); 
	    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd @ hh:mm");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	   
	    if (0<lastmodified){
		    String modDate = sdf.format(new Date(lastmodified)); 
		    doc.add(new Field(FIELD_DATE_MODIFIED, modDate, Field.Store.YES, Field.Index.UN_TOKENIZED )); 
	    }
	    String rptDate = sdf.format(dateSubmit); 
	    doc.add(new Field(FIELD_DATE_SUBMIT, rptDate, Field.Store.YES, Field.Index.UN_TOKENIZED )); 		
	}
	

	

	protected String prepareTokenizedLabel(String szText){
		if (szText.length()>500)
			szText = szText.substring(0,500);
		return szText.replaceAll("\\p{Punct}", " ");
	}

	@Override
	void do_add(AgentLucene lucene, String szSource)
			throws CorruptIndexException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void do_delete(AgentLucene lucene, String szSource) throws IOException {
		// TODO Auto-generated method stub
		
	}

	

	
}
