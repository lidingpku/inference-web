package org.inference_web.iwapp.iwsearch;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import sw4j.util.ToolSafe;

public class AgentLucene {

	boolean debug = false;
	
	public AgentLucene(String dir){
		m_szIndexDir = dir;
	}

	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public void close(){
		closeIndexSearcher();
		closeIndexReader();
		closeIndexWriter();
		closeDirectory();
	}
	
	String m_szIndexDir = null;
	public String getIndexDir(){
		return m_szIndexDir;
	}

	
	Directory m_Directory = null;
	public Directory getDirectory() throws IOException{
		if (null== m_Directory){
			if (ToolSafe.isEmpty(getIndexDir())){
				m_Directory = new RAMDirectory();
			}else{
				m_Directory =  FSDirectory.getDirectory(getIndexDir());
			}
		}
		return m_Directory;
	}
	
	public void closeDirectory() {
		if (null!=m_Directory){
			try{
			
				m_Directory.close();
				m_Directory =null;
				
				if (debug)
					System.out.println("closeDirectory");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	Analyzer m_Analyzer = null;
	public Analyzer getAnalyzer(){
		if (null == m_Analyzer ){
			m_Analyzer = new SimpleAnalyzer();
		}
		
		return m_Analyzer;
	}
	
	IndexReader m_IndexReader = null;
	public IndexReader getIndexReader() throws CorruptIndexException, IOException{
		if (null == m_IndexReader){
			m_IndexReader = IndexReader.open(getDirectory());
		}		
		return m_IndexReader;
	}
	
	public void closeIndexReader(){
		if (null != m_IndexReader){
			try{
				m_IndexReader.close();
				m_IndexReader =null;
				
				if (debug)
					System.out.println("closeIndexReader");
			
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	IndexWriter m_IndexWriter = null;
	public IndexWriter getIndexWriter(boolean bRefresh) throws CorruptIndexException, IOException{
		if (null == m_IndexWriter){
				
			if (!bRefresh && IndexReader.indexExists(getDirectory())){
				m_IndexWriter = new IndexWriter(
						getDirectory(), 
						getAnalyzer(), 
						false );
			}else{
				m_IndexWriter = new IndexWriter(
						getDirectory(), 
						getAnalyzer(), 
						true );				
			}
			
		}		
		return m_IndexWriter;
	}
	
	public void closeIndexWriter(){
		if (null != m_IndexWriter){
			try {
				m_IndexWriter.close();
				m_IndexWriter =null;

				// unlock the directory
/*				if (IndexWriter.isLocked(getDirectory())){
					if (debug)
						System.out.println("unlock directory");
					IndexWriter.unlock(getDirectory());
				}
*/
				if (debug)
					System.out.println("closeIndexWriter");
				
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}	
	
	IndexSearcher m_IndexSearcher = null;
	public IndexSearcher getIndexSearcher() throws CorruptIndexException, IOException{
		if (null == m_IndexSearcher){
			m_IndexSearcher = new IndexSearcher( getIndexReader());
		}		
		return m_IndexSearcher;
	}
	
	public void closeIndexSearcher() {
		if (null != m_IndexSearcher){
			try{
				m_IndexSearcher.close();
				m_IndexSearcher =null;
				
				if (debug)
					System.out.println("closeIndexSearcher");
				
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			closeIndexReader();			
		}		
	}	
	
	/**
	 * print index for test
	 */
	public void printIndex(){
		try {
			IndexReader reader = getIndexReader();
			System.out.println(getIndexDir());
			System.out.println(reader.maxDoc());
			System.out.println(reader.numDocs());
	
			System.out.println("---------------docs----------");
			System.out.println();
			for (int i=0; i< reader.numDocs(); i++){
				Document doc = reader.document(i);
				System.out.println( doc.toString());
			};
			
			System.out.println("---------------terms----------");
			System.out.println();
			TermEnum te = reader.terms();
			do {
				if (te.term()!=null)
					System.out.println(te.term().toString());
			}while (te.next());
				
		}catch (IOException e){
			e.printStackTrace();
		}
	}	
	
	/**
	 * original demo code without using this class
	 * @throws IOException
	 */
    public static void test1a() throws IOException {
        RAMDirectory directory = new RAMDirectory();
        IndexWriter writer = 
          new IndexWriter(directory, new SimpleAnalyzer(), true);
            
        Document doc = new Document(); 
        doc.add(new Field("partnum", "Q36", Field.Store.YES, Field.Index.UN_TOKENIZED));   
        doc.add(new Field("description", "Illidium Space Modulator", Field.Store.YES, Field.Index.TOKENIZED)); 
        writer.addDocument(doc); 
        writer.close();

        IndexSearcher searcher = new IndexSearcher(directory);
        Query query = new TermQuery(new Term("partnum", "Q36"));   
        TopDocs rs = searcher.search(query, null, 10);
        System.out.println(rs.totalHits);

        Document firstHit = searcher.doc(rs.scoreDocs[0].doc);
        System.out.println(firstHit.getField("partnum").name());
    }
    
    /**
     * demo code rewritten using this class
     * 
     * @throws IOException
     */
    public static void test1b() throws IOException {
    	AgentLucene oAgentLucence = new AgentLucene(null);
    	
        IndexWriter writer = oAgentLucence.getIndexWriter(false);
            
        Document doc = new Document(); 
        doc.add(new Field("partnum", "Q36", Field.Store.YES, Field.Index.UN_TOKENIZED));   
        doc.add(new Field("descriptio	n", "Illidium Space Modulator", Field.Store.NO, Field.Index.TOKENIZED)); 
        writer.addDocument(doc); 
        oAgentLucence.closeIndexWriter();

        IndexSearcher searcher = oAgentLucence.getIndexSearcher();
        Query query = new TermQuery(new Term("partnum", "Q36"));   
        TopDocs rs = searcher.search(query, null, 10);
        System.out.println(rs.totalHits);

        Document firstHit = searcher.doc(rs.scoreDocs[0].doc);
        System.out.println(firstHit.getField("partnum").name());
        System.out.println(firstHit.getField("partnum").stringValue());

        oAgentLucence.printIndex();
    }
    
    public static void main(String[] args) throws IOException {
    	test1b();
    }
}
