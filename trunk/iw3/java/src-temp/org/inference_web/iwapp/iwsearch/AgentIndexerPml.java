package org.inference_web.iwapp.iwsearch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import sw4j.app.pml.IW200403;
import sw4j.app.pml.IW200407;
import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.pellet.ToolPellet;
import sw4j.rdf.load.AgentModelLoader;
import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.DataInstance;
import sw4j.rdf.util.ToolJena;
import sw4j.rdf.util.ToolModelAnalysis;
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


public class AgentIndexerPml extends AgentIndexerInstance{

	
	public static void main(String[] args) {
		File dirJob;
		try {
			//dirJob = IWSearchSettings.loadDefault().getInputDir();
			String path_index = IWSearchSettings.loadDefault().getIndexPath(IWSearchSettings.OPTION_INDEX_INSTANCE);
			
			AgentIndexerPml indexer = new AgentIndexerPml();
			indexer.index( "files/iwsearch/iwsearch_input_urls.txt", path_index );
//			printIndex(path_index,false);
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		//filter PML instance only
		{
			boolean bIsPmlType = false;
			Collection<Resource> types = mapInstanceTypes_direct.getValues(ind.getSubject());
			Iterator<Resource> iter_type = types.iterator();
			while (iter_type.hasNext()){
				Resource type = iter_type.next();
				String szNamespace = type.getNameSpace();
				if (ToolSafe.isEmpty(szNamespace))
					continue;
				
				if (szNamespace.startsWith("http://inference-web.org/")){
					bIsPmlType =true;
					break;
				}
			}
			
			if (!bIsPmlType)
				return;
		}
		
		//add literal values of information as description of the instance referening it
		String szDesc = "";
		ExtendedIterator iter = ind.getModel().listObjects();
		while (iter.hasNext()){
			RDFNode node = (RDFNode) iter.next();
			if (node.isLiteral()){
				szDesc += "," + ToolJena.getNodeString(node);
			}else{
				Resource obj= (Resource)node;
				Collection<Resource> types = mapInstanceTypes_direct.getValues(obj);
				if (types.contains(PMLP.Information)){
					DataInstance di = mapInstances.get(obj);
					if (!ToolSafe.isEmpty(di)){
						ExtendedIterator iter_di = di.getModel().listObjects();
						while (iter_di.hasNext()){
							RDFNode node_di = (RDFNode) iter_di.next();
							if (node_di.isLiteral()){
								szDesc += "," + ToolJena.getNodeString(node_di);
							}
						}
					}
				}
					
			}
		}
		if (!ToolSafe.isEmpty(szDesc)){
			ind.addPropertyValue(RDFS.comment, szDesc);
		}
		
		super.do_add(
				writer,
				ind,
				mapInstances,
				mapInstanceTypes,
				mapInstanceTypes_direct,
				szSource,
				lastmodified);
		
	}
	

	/**
	 * ind: RDF specific concept, an instance of PML Class
	 * doc: lucene specific concept, an entry in the index, each will be returned as a line in search result
	 */
	protected void indexLabel(DataInstance ind, Document doc){
	    /////////////////
	    // label + tag
		// label: a natural language string as the name/label of that data instance
		// 
		
	    String label = null;
	    //TODO index label with better way
	    Property [] label_props= new Property[]{
	    		PMLP.hasName,
	    		IW200407.name,
	    		IW200403.name,
	    		PMLP.hasPrettyName,
	    		IW200407.hasConclusion,
	    		IW200403.conclusion,
	    };
	    for (int i=0; i<label_props.length; i++){
	    	Property prop = label_props[i];
		   	if (ToolSafe.isEmpty(label)){
			    label = ind.getPropertyValueAsString(prop);
			   	if (!ToolSafe.isEmpty(label)){
				    doc.add(new Field(AgentIndexerInstance.FIELD_TAG, prepareTokenizedLabel(label), Field.Store.NO, Field.Index.TOKENIZED ));
			    }
		   	}
	    }	   	
	    
	    // try raw string
	    if (ToolSafe.isEmpty(label)){
	    	label = ind.getPropertyPropertyValueAsString(PMLJ.hasConclusion, PMLP.hasRawString);
		   	if (!ToolSafe.isEmpty(label)){
			    doc.add(new Field(AgentIndexerInstance.FIELD_TAG, prepareTokenizedLabel(label), Field.Store.NO, Field.Index.TOKENIZED ));
		    }
	    }

	    if (ToolSafe.isEmpty(label)){
	    	label = ind.getPropertyPropertyValueAsString(PMLP.hasContent, PMLP.hasRawString);
		   	if (!ToolSafe.isEmpty(label) && label.length()<100){
			    doc.add(new Field(AgentIndexerInstance.FIELD_TAG, prepareTokenizedLabel(label), Field.Store.NO, Field.Index.TOKENIZED ));
		    }
	    }
	    

	    if (!ToolSafe.isEmpty(label))
		    doc.add(new Field(AgentIndexerInstance.FIELD_LABEL, label, Field.Store.YES, Field.Index.UN_TOKENIZED ));
	}
	
}
