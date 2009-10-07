/*
 * Created on May 20, 2005
 *
 */
package org.inference_web.iwapp.iwsearch;



import java.io.IOException;
import java.util.HashSet;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;





import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.rdf.model.Model;


/**
 * @author Li Ding
 *
 */
public class IWSearcher {
	
	public static void main(String[] args) {
		DataSearchTask task = new DataSearchTask();
		task.init(IWSEARCH.search_pml_instance.getLocalName(), "deborah");
//		task.init(IWSEARCH.search_pml_instance.getLocalName(), "label:Larry");
//		task.init(IWSEARCH.search_pml_instance.getLocalName(), "refersToMemberOf(5-29 , com.ibm.nimd.Organization)");
//		task.init(IWSEARCH.search_pml_instance.getLocalName(), "type:Person","food",null, null);
//		task.init(IWSEARCH.search_pml_instance.getLocalName(), "type:Person");
//		task.init(IWSEARCH.search_pml_instance.getLocalName(), "source:\"	http://inference-web.org/proofs/csctest/crashtest.owl\"");
//		m = task.setInputWithValidation(IWSEARCH.qt_search_pml_instance_by_type.getLocalName(), "Person");
		if (task.hasError()){
			// check for error
			System.out.println(ToolSearchRenderer.printSearchResponseToString(task));
			return;
		}
		
		try {
			IWSearcher searcher = new IWSearcher();
			searcher.search(task);
			
			System.out.println(ToolSearchRenderer.printSearchResponseToString(task));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static  IWSearcher gSearcher = null;
	public static IWSearcher get(){
		if (null== gSearcher)
			gSearcher = new IWSearcher();
		return gSearcher;
	}

	
	public void search(DataSearchTask task) throws IOException, ParseException, Sw4jException {
		// we assume task has been checked/
        Analyzer analyzer = new StandardAnalyzer();
        IWSearchSettings settings = IWSearchSettings.loadDefault();
        
        Searcher searcher = new IndexSearcher( settings.getIndexPath(task.getQueryOption()));
        System.out.println(searcher.maxDoc());


		long startTime = System.currentTimeMillis();
		Sort sort = null;
		if (task.hasSearchSortField()){
			sort = new Sort();
			sort.setSort(task.getSearchSortField());
		}
		
        String [] fields = IWSearchSettings.loadDefault().getIndexFields(task.getQueryOption());
        BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
        for (int i=0; i<flags.length; i++)
        	flags[i]= BooleanClause.Occur.SHOULD;
        
//        Query query = MyMultiFieldQueryParser.parse(q, IWSearchConfig.get().getIndexFields(query_option), analyzer);
//       Query query = MultiFieldQueryParser.parse(q, fields, flags,analyzer);
       MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
       parser.setDefaultOperator(QueryParser.AND_OPERATOR);
       Query query = parser.parse(task.getSearchString());
//        QueryParser parser = new MultiFieldQueryParser(IWSearchConfig.get().getIndexFields(query_option), analyzer);

       TopDocs hits =null;
       if (ToolSafe.isEmpty(sort))
    	   hits = searcher.search(query, null, 100);
       else
    	   hits =searcher.search(query, null, 100, sort);
       
        
        if (task.getSearchStart()-1< hits.totalHits){
			
    		Model m= ModelFactory.createDefaultModel();
    		Resource seq = m.createResource(RDF.Seq);
            task.setSearchResults(seq);

//    		int end = hits.length();
			int end = Math.min(hits.totalHits, task.getSearchStart()-1+ task.getSearchLimit());
			//end = Math.min(end, task.getUpperLimit());
			task.m_nSearchReturnedResults=0;
			for (int i = task.getSearchStart()-1; i < end; i++) {
				Document doc = searcher.doc(hits.scoreDocs[i].doc);
				task.m_nSearchReturnedResults++;
				
				Resource item;
				switch (task.getQueryOption()){
					case IWSearchSettings.OPTION_INDEX_INSTANCE:
						item = m.createResource();
						item.addProperty(RDF.type, IWSEARCH.InstanceMetadata);

						String temp;
						temp = doc.get(AgentIndexerInstance.FIELD_URI);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DC.subject,  m.createResource(temp));

						temp = doc.get(AgentIndexerInstance.FIELD_ID);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DC.identifier, temp);

						temp = doc.get(AgentIndexerInstance.FIELD_TYPE);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DC.type,  m.createResource(temp));
						
						temp = doc.get(AgentIndexerInstance.FIELD_LABEL);
						if (!ToolSafe.isEmpty(temp)){
							item.addProperty(RDFS.label,  (temp));
						}

						temp = doc.get(AgentIndexerInstance.FIELD_SOURCE);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DC.source,  m.createResource(temp));

						temp = doc.get(AgentIndexerInstance.FIELD_DATE_MODIFIED);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DCTerms.modified,  temp, XSDDatatype.XSDdate);
						
						temp = doc.get(AgentIndexerInstance.FIELD_DATE_SUBMIT);
						if (!ToolSafe.isEmpty(temp))
							item.addProperty(DCTerms.dateSubmitted,  temp, XSDDatatype.XSDdate);
						break;
					default: 
						continue;
				}

				seq.addProperty(RDF.li(i+1), item);
		  }
        }
        
        long lTime = System.currentTimeMillis()-startTime;

        //System.out.println(hits.length() + " total matching documents");
        task.m_nSearchTotalResults = hits.totalHits;
        task.m_fSearchProcessTimeSeconds = lTime/1000.0f;
	}
	
	
}

/*



	public String doStat(boolean viewRDF) {
		try {
			IWSearcher searcher = new IWSearcher(IWSearchConfig.get());
			Model m = searcher.stat();
			
			if (m!=null){
				if (viewRDF)
					return SearchRenderer.printQueryResultRDFXML(m);
				else{
					return SearchRenderer.printIndextaskHTML(m);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}	
	
	public Model stat() throws IOException{
		Model m = ModelFactory.createDefaultModel();
		
		long startTime = System.currentTimeMillis();
		Resource seq = m.createResource(RDF.Seq);
		
		for (int i=0; i<IWSearchConfig.INDEX_PML_INDEX_MAX; i++){
			Resource item = m.createResource(IWSEARCH.Indextask);
			item.addProperty(IWSEARCH.uri, IWSearchConfig.getIndexPmlRes(i));
			
			String index_dir = m_context.getIndexPmlDir(i);
			IndexReader reader = IndexReader.open(index_dir);
			item.addProperty(RDFS.comment, "total indexed:"+reader.numDocs());
			reader.close();
			seq.addProperty(RDF.li(i+1), item);
		}
	    long lTime = System.currentTimeMillis()-startTime;
		
        Resource response = m.createResource(SWOOGLE.QueryResponse);
		response.addProperty(RDFS.comment, "This result is generated by SWOOGLE3 at http://swoogle.umbc.edu." )
				.addProperty(SWOOGLE.hasSearchTime, lTime/1000.0+"", XSDDatatype.XSDinteger)
				.addProperty(SWOOGLE.hasQueryResult, SWOOGLE.success)
				.addProperty(SWOOGLE.hasResult, seq);

		return m;
	}


	



}
*/