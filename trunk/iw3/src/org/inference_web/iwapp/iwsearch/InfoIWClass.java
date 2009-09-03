package org.inference_web.iwapp.iwsearch;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoIWClass extends HashMap{

	static String [] IW_NS = new String []{
			"http://inferenceweb.stanford.edu/2004/03/iw.owl#",
			"http://inferenceweb.stanford.edu/2004/07/iw.owl#",
	};
	public static void main(String[] args) {
		for (int i=0; i<IW_NS.length; i++)
			load(IW_NS[i]);
	}

	public static InfoIWClass createInstance(){
		InfoIWClass info = new InfoIWClass();
		for (int i=0; i<IW_NS.length; i++){
			HashMap map_subclass = load(IW_NS[i]);
			info.putAll(map_subclass);
		}
		return info;
	}
	
	private static HashMap load(String szURL){	
		Model m = ModelFactory.createDefaultModel();
		m.read(szURL);
		InfModel infm = ModelFactory.createRDFSModel(m);

		StmtIterator iter;
		
		HashMap map_class = new HashMap();
		iter = m.listStatements(null, RDF.type,(RDFNode)null);
		while(iter.hasNext()){
			Statement stmt = iter.nextStatement();
			if (stmt.getSubject().isAnon())
				continue;
			map_class.put(stmt.getSubject(), stmt.getObject());
			
		}
		
		// init subclass map
		HashMap map_subclass = new HashMap();
		iter = infm.listStatements(null, RDFS.subClassOf,(RDFNode)null);
		while(iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			if (!map_class.keySet().contains(stmt.getSubject()))
				continue;
			
			if (!map_class.keySet().contains(stmt.getObject()))
				continue;

			//if (stmt.getSubject().equals(stmt.getObject()))
			//	continue;

			HashSet temp = (HashSet) map_subclass.get(stmt.getSubject());
			if (null==temp)
				temp = new HashSet();
			temp.add(stmt.getObject());
			map_subclass.put(stmt.getSubject(), temp );
		}

		/*
		// print result
		Iterator iter_class = map_subclass.entrySet().iterator();
		while(iter_class.hasNext()){
			Map.Entry entry = (Map.Entry)iter_class.next();
			Resource key = (Resource)entry.getKey();
			HashSet val = (HashSet)entry.getValue();
			
			System.out.println(key);
			System.out.println(val);
		}
		*/
		return map_subclass;
	}
	
}
