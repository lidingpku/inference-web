/**
 * Copyright 2007 Inference Web Group.  All Rights Reserved.
*/
package org.inference_web.pml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sw4j.rdf.load.AgentModelManager;
import sw4j.rdf.util.ToolJena;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * This class enforces PML specific semantics.
 * @author Li
 *
 */
public class PmlAnalyzer{
	
	/**
	 * test if an RDF model has PML data
	 * 
	 * @param m
	 * @return  
	 */
	public static boolean hasPmlData(Model m){
		StmtIterator iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			// the domain of a PML predicate is always a PML class (double check?)
			if (PmlConst.testPmlNamespace(stmt.getPredicate())){
				return true;
			}
			
			// the instance of a PML class is a PML instance
			if (stmt.getPredicate().equals(RDF.type)&& PmlConst.testPmlNamespace(stmt.getObject())){
				return true;
			}
		}
		return false;
	}	
	
	/**
	 *  List all PML instances being described by at least one triple in an RDF graph:
	 * <ul>
	 * 	<li>the sujbect in a triple, where the predicate uses PML namespace</li>
	 * 	<li>the sujbect in a triple, where RDF.type is the predicate and the object uses PML namespace</li>
	 * </ul>
	 * 
	 * @param m
	 * @return
	 */
	public static Set<Resource> listDescribedPmlInstances(Model m){
		HashSet<Resource> instances = new HashSet<Resource>();
		StmtIterator iter = m.listStatements();
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			
			// if we know it is a PML instance, then don't check it again
			if (instances.contains(stmt.getSubject()))
				continue;
			
			// A PML class/property cannot be a PML instance
			if (PmlConst.testPmlNamespace(stmt.getSubject())){
				continue;
			}

			// the domain of a PML predicate is always a PML class (double check?)
			if (PmlConst.testPmlNamespace(stmt.getPredicate())){
				instances.add(stmt.getSubject());
				continue;
			}
			
			// the instance of a PML class is a PML instance
			if (stmt.getPredicate().equals(RDF.type)&& PmlConst.testPmlNamespace(stmt.getObject())){
				instances.add(stmt.getSubject());
				continue;
			}
		}
		return instances;
	}
	
	/**
	 * list PML instances defined outside the supplied RDF model and baseURL
	 *  
	 * @param m
	 * @param szBaseURL
	 * @return
	 */
	public static Set<Resource> listExternalPmlInstances(Model m, String xmlbase){
		HashSet<Resource> pml_instances = new HashSet<Resource>();
		HashSet<Resource> described_instances = new HashSet<Resource>();
		{
			StmtIterator iter = m.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();
	
				// skip class type definition
				if (stmt.getPredicate().equals(RDF.type)&& PmlConst.testPmlNamespace(stmt.getObject())){
					if (stmt.getSubject().isURIResource())
						pml_instances.add(stmt.getSubject());
					continue;
				}
				
				// count described instances (after filtering rdf:type description)
				if (stmt.getSubject().isURIResource())
					described_instances.add(stmt.getSubject());
				
				// the domain and range of a PML owl:ObjectProperty is always a PML instance
				if (PmlConst.testPmlNamespace(stmt.getPredicate())){
					if (stmt.getSubject().isURIResource())
						pml_instances.add(stmt.getSubject());
					
					if (stmt.getObject().isURIResource())
						pml_instances.add((Resource)stmt.getObject());
						
					continue;
				}
			}
		}

		// remove described instances
		pml_instances.removeAll(described_instances);
		
		// remove instances using the baseURL as namespace
		if (!ToolSafe.isEmpty(xmlbase)){
			Iterator<Resource> iter = pml_instances.iterator();
			while (iter.hasNext()){
				Resource res = iter.next();
				if (res.getURI().startsWith(xmlbase))
					iter.remove();
			}
		}
		
		return pml_instances;
	}
	
	/**
	 * update namespace-prefix mapping for namespaces used by m
	 * @param m
	 * @return
	 */
	public static Model appendPmlPrefixNamespaceMap(Model m){
		HashMap<String,String> map_ns_prefix = new HashMap<String,String>();
		for (int i=0; i<PmlConst.PML_NAMESPACES_PREFIX.length; i++)
			map_ns_prefix.put(PmlConst.PML_NAMESPACES_PREFIX[i][1],PmlConst.PML_NAMESPACES_PREFIX[i][0]);
		
		NsIterator iter = m.listNameSpaces();
		while (iter.hasNext()){
			String ns =iter.nextNs();
			String prefix = map_ns_prefix.get(ns);
			if (null!=prefix)
				m.setNsPrefix(prefix, ns);
		}
		return m;
	}	
	

	public static Model filterPMLInstanceData(String szURL){
		// load graph
		Model m = ModelFactory.createDefaultModel();
		try {
			ToolJena.update_copy(m, AgentModelManager.get().loadModel(szURL));
			return filterPMLInstanceData(m);
		} catch (Sw4jException e) {
			return null;
		}
		
	}
	
	/**
	 * filter PML instance data: PML instances and  RDF instances (recursively) referenced by PML data.
	 *  
	 * @param m
	 * @return
	 */
	public static Model filterPMLInstanceData(Model m){
		if (ToolSafe.isEmpty( m))
			return null;
	
		Model pml_model = ModelFactory.createDefaultModel();
		
		Set<Resource> current_instances = listDescribedPmlInstances(m);
		Set<Resource> visited_instances = new HashSet<Resource>();
	
		do {
			Set<Resource> todo_instances = new HashSet<Resource>();
	
			StmtIterator iter = m.listStatements();
			while (iter.hasNext()){
				Statement stmt = iter.nextStatement();
				
				Resource subject = stmt.getSubject();
				
				if (visited_instances.contains(subject)){
					continue;
				}
				
				if (current_instances.contains(subject)){
					pml_model.add(stmt);
					
					//skip type definition, instance data should not contain ontology definition. Abox only 
					if (RDF.type.equals(stmt.getPredicate()))
						continue;
					
					// add referenced RDF resource in todo list
					if (stmt.getObject().isResource()){
						Resource obj = (Resource)stmt.getObject();
						if (!visited_instances.contains(obj)&&!current_instances.contains(obj)){
							todo_instances.add(obj);
						}
					}
				}
			}
			visited_instances.addAll(current_instances);
			current_instances = todo_instances;
		}while (!current_instances.isEmpty());
		
		appendPmlPrefixNamespaceMap(pml_model);
		return pml_model;
	}

	
}

