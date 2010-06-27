package temp.test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class TDBTest {
	public static void main (String[] args){
		test1();
	}
	
	public static void test1(){
		test1_read();
		test1_write();
		test1_read();
	}

	public static void test1_write(){
		  String directory = "files/tdb/db/db1" ;
		  Model model = TDBFactory.createModel(directory) ;		  
		  model.add(model.createStatement(RDF.type, RDF.type, RDF.Property));
		  System.out.println(model.size());
		  TDB.sync(model);
		  model.close() ;
		
	}
	
	public static void test1_read(){
		  String directory = "files/tdb/db/db1" ;
		  Model model = TDBFactory.createModel(directory) ;
		  System.out.println(model.listStatements().toSet());
		  model.close() ;
	}

	private String m_dataset_filepath = "files/tdb/db/ds1";
	public static void test2(){
		TDBTest test = new TDBTest();
		String url ="http://www.cs.rpi.edu/~dingl/foaf.rdf";
		test.test2_load(url);
		test.test2_read(url);
	}
	
	private void test2_load(String url){
		  Dataset dataset = TDBFactory.createDataset(m_dataset_filepath) ;
		  Model data = ModelFactory.createDefaultModel();
		  data.read(url);
		  System.out.println(data.size());
		  Model m = dataset.getNamedModel(url);
		  m.add(data);
		  TDB.sync(dataset);
		  dataset.close() ;
	}
	
	private void test2_read(String url){
		  Dataset dataset = TDBFactory.createDataset(m_dataset_filepath) ;
		  Model m = dataset.getNamedModel(url);
		  System.out.println(m.size());
		  dataset.close() ;
	}
}
