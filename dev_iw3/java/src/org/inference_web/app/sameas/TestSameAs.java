package org.inference_web.app.sameas;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import sw4j.rdf.util.ToolJena;
import sw4j.util.DataObjectCounter;
import sw4j.util.DataObjectGroupMap;
import sw4j.util.DataPVHMap;
import sw4j.util.DataSmartMap;
import sw4j.util.Sw4jException;
import sw4j.util.ToolHash;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.ToolURI;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;

public class TestSameAs {
	final static int  MAX_GROUPID =100000;
	static String dir_root = ".";
	public static void main (String[] args){
		//run_merge();
		if (args.length>0){
			dir_root = args[0];
		}
		run_gen();
	}
	
	/*
	public static void run_merge(){
		try {
			merge_rdf("people");
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void merge_rdf(String sz_context) throws Sw4jException{
		File f_input = new File(String.format("local/output/%s/",sz_context));
		File f_output = new File(String.format("local/output/data.csv"));
		
		String [] files =list_rdf_files(f_input);
		for (String file :files){
			System.out.println(file);
			File f_data = new File(f_input,file);
			Model m = ModelFactory.createDefaultModel();
			m.read(ToolIO.prepareFileInputStream(f_data.getAbsolutePath()),"");
			
			if (!ToolSafe.isEmpty(m)&& m.size()>0){
				for (Statement stmt: m.listStatements().toList()){
					DataSmartMap map = new DataSmartMap();
					map.put("context", sz_context);
					map.put("file", file);
					map.put("subject", stmt.getSubject().getURI());
					map.put("predicate", stmt.getPredicate().getURI());
					map.put("object", ToolJena.getNodeString(stmt.getObject()));
					
					print_csv(f_output,map, true);
				}
			}
		}
		
	}	

	private static String[] list_rdf_files(File dir){
		if (!dir.exists())
			return null;
		
		//filter based on file type
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir_input, String name) {
				String lcname = name.toLowerCase();
				final String ff = ".rdf";
				return lcname.endsWith(ff);
			}
		};
		
		return dir.list(filter);
	}
*/	
	public static void run_gen(){
		TestSameAs test = new TestSameAs();
		test.load_data("people");
		test.load_data("organizations");
		test.load_data("locations");	
		
		
		{
			File f= new File(String.format("%s/local/output/property_mapping_by_value.txt",dir_root));
			ToolIO.pipeStringToFile(test.m_property_mapping_by_value.toPrettyString(), f);				
		}

		{
			File f= new File(String.format("%s/local/output/property_mapping_by_localname.txt",dir_root));
			ToolIO.pipeStringToFile(test.m_property_mapping_by_localname.toPrettyString(), f);				
		}

		{
			File f= new File(String.format("%s/local/output/property_uri.csv",dir_root));
			save_count(test.m_map_property_uri, f);
		}
		
		{
			File f= new File(String.format("%s/local/output/property_group.csv",dir_root));
			save_count(test.m_map_property_group, f);
		}
	}

	//DataObjectCounter<String> m_properties_all = new DataObjectCounter<String>();
	//DataObjectCounter<String> m_properties_seed = new DataObjectCounter<String>();
	DataObjectCounter<String> m_sources = new DataObjectCounter<String>();
	Model m_sameas= ModelFactory.createDefaultModel();
	DataPVHMap<String,String> m_map_property_uri= new DataPVHMap<String,String>();
	DataPVHMap<String,String> m_map_property_group= new DataPVHMap<String,String>();

	DataObjectGroupMap<String> m_property_mapping_by_value = new DataObjectGroupMap<String>();
	DataObjectGroupMap<String> m_property_mapping_by_localname = new DataObjectGroupMap<String>();

	public void load_data(String sz_context ){
		File f_input = new File(String.format("%s/files/sameas/%s.rdf",dir_root,sz_context));
		File f_output = new File(String.format("%s/local/output/%s/", dir_root,sz_context));
		Set<Resource> seeds;
		
		//m_properties_all.clear();
		//m_properties_seed.clear();
		m_sources.clear();
		m_sameas= ModelFactory.createDefaultModel();
		//m_property_mapping.clear();  -- this is globally computed
		
		try {
			seeds = prepare_seeds(f_input);
			int groupid = 1;
			for (Resource seed: seeds){
				load_data(seed, f_output, sz_context, groupid );
				groupid++;
				
				if (groupid>MAX_GROUPID)
					break;
			}
			
			{
				File f= new File(f_output, String.format("sameas.owl"));
				ToolJena.printModelToFile(m_sameas, f);
			}

/*			{
				File f= new File(f_output, "property_all.csv");
				ToolIO.pipeStringToFile(ToolString.printCollectionToString(m_properties_all.getSortedDataByCount()), f);				
			}
			{
				File f= new File(f_output, "property_seed.csv");
				ToolIO.pipeStringToFile(ToolString.printCollectionToString(m_properties_seed.getSortedDataByCount()), f);				
			}
*/			{
				File f= new File(f_output, String.format("source.csv"));
				ToolIO.pipeStringToFile(ToolString.printCollectionToString(m_sources.getSortedDataByCount()), f);				
			}


			

		} catch (Sw4jException e) {
			e.printStackTrace();
		}
	}
	
	private Set<Resource> prepare_seeds(File f_input) throws Sw4jException{
		Model m = ModelFactory.createDefaultModel();
		m.read(ToolIO.prepareFileInputStream(f_input),"");
		return m.listSubjectsWithProperty(OWL.sameAs).toSet();
	}
	
	private static String normalize(String url){
		return url.replaceAll("\\W+","_" );
	}

	/**
	 * handle on seeding uri
	 * 
	 * @param seed
	 * @param dir
	 * @param sz_context
	 * @param groupid
	 * @throws Sw4jException
	 */
	public void load_data(Resource seed, File dir, String sz_context, int groupid) throws Sw4jException{
		Set<Resource> tovisit= new HashSet<Resource>();
		Set<Resource> visited= new HashSet<Resource>();
		tovisit.add(seed);
		
		File f_group= new File(dir, "../group.csv");
		File f_summary = new File(dir, "../summary.csv");
		File f_all = new File(dir, "../alldata.csv");

		DataPVHMap<String,String> map_property_uri= new DataPVHMap<String,String>();
		DataPVHMap<String,String> map_property_source = new DataPVHMap<String,String>();

		DataPVHMap<String,String> map_datatypeproperty_uri= new DataPVHMap<String,String>();
		DataPVHMap<String,String> map_datatypeproperty_source = new DataPVHMap<String,String>();

		DataPVHMap<String,String> map_literal_uri= new DataPVHMap<String,String>();
		DataPVHMap<String,String> map_literal_source = new DataPVHMap<String,String>();
		
		DataPVHMap<String,String> map_literal_property = new DataPVHMap<String,String>();
		DataPVHMap<String,String> map_localname_property = new DataPVHMap<String,String>();

		DataSmartMap data_group = new DataSmartMap();
		data_group.put("c-context", sz_context);
		data_group.put("c-groupid", groupid);
		data_group.put("c-seed", seed.getURI());

		int cnt_sameas_triple=0;
		int cnt_sameas_uri=0;
		int cnt_sameas_uri_good=0;

		while (tovisit.size()>0){
			Set<Resource> tovisit_new = new HashSet<Resource>();
			for (Resource res: tovisit){
				visited.add(res);
				cnt_sameas_uri ++;
				
				DataSmartMap data = new DataSmartMap();

				data.put("c-context", sz_context);
				data.put("c-groupid", groupid);
				data.put("c-uri", res.getURI());
				String sz_source = ToolURI.string2uri(res.getURI().substring(0,res.getURI().lastIndexOf("/"))).getHost();
				data.put("c-source", sz_source);
				m_sources.count(sz_source);

				data.put("triples", "");
				data.put("triples-sameas", "");
				data.put("properties", "");
				
				data.put("value_literal", "");
				data.put("value_uri", "");					

				//Model m1 = AgentModelManager.get().loadModel(res.getURI());
				
				Model m= ModelFactory.createDefaultModel();
				try{
					System.out.println("loading "+res.getURI());
					m.read(res.getURI());
				}catch(Exception e){
					m=null;
					e.printStackTrace();
				}
				
					
				
				
				if (!ToolSafe.isEmpty(m)&& m.size()>0){
					Model m_desc = ToolJena.createCBD(m, res);
					data.put("triples", m_desc.size());
					
					if (m_desc.size()>0){
						cnt_sameas_uri_good ++;
						cnt_sameas_triple += m_desc.size();

						
						String filename = normalize(res.getURI());
						File f_output= new File(dir, String.format("%05d-%s-%s.rdf",groupid,filename,ToolHash.hash_sum_md5(filename.getBytes())));
						ToolJena.printModelToFile(m_desc, f_output);

						
						for (RDFNode next: m_desc.listObjectsOfProperty(res, OWL.sameAs).toList()){
							tovisit_new.add((Resource)next);
						}
						data.put("triples-sameas", m_desc.listObjectsOfProperty(res, OWL.sameAs).toSet().size());
						m_sameas.add(m_desc.listStatements(res, OWL.sameAs,(String)null));
						
					
						HashSet<Property> set_prop = new HashSet<Property>();
						HashSet<String> set_value_literal= new HashSet<String>();
						HashSet<RDFNode> set_value_uri= new HashSet<RDFNode>();
						for (Statement stmt: m_desc.listStatements().toList()){

							map_localname_property.add(stmt.getPredicate().getLocalName(),stmt.getPredicate().getURI());

//							m_properties_all.count(stmt.getPredicate().getURI());

							set_prop.add(stmt.getPredicate());
							
							map_property_uri.add(stmt.getPredicate().getURI(), res.getURI());
							map_property_source.add(stmt.getPredicate().getURI(), sz_source);
							
							m_map_property_uri.add(stmt.getPredicate().getURI(), res.getURI());
							m_map_property_group.add(stmt.getPredicate().getURI(), sz_source);

							if (stmt.getObject().isLiteral()){
								//the object must be literal

								map_datatypeproperty_uri.add(stmt.getPredicate().getURI(), res.getURI());
								map_datatypeproperty_source.add(stmt.getPredicate().getURI(), sz_source);

								String value = ToolJena.getNodeString(stmt.getObject());
								value = value.replaceAll("\n", " ");
								if (value.length()>100)
									value = value.substring(0, 100);
								
								set_value_literal.add(value);
								
								map_literal_uri.add(value, res.getURI());
								map_literal_source.add(value,sz_source);
								
								
								// two properties are eq-candidate when they share the same subject and value
								if (stmt.getSubject().equals(res))
									map_literal_property.add(value,stmt.getPredicate().getURI());

							}else if (stmt.getObject().isURIResource() )
								set_value_uri.add(stmt.getObject());
							
							DataSmartMap map = new DataSmartMap();
							map.put("c-context", sz_context);
							map.put("c-seed", seed.getURI());
							map.put("subject", stmt.getSubject().toString());
							map.put("predicate", stmt.getPredicate().getURI());
							map.put("object", ToolJena.getNodeString(stmt.getObject()));
							
							print_csv(f_all,map, true);

						}
						data.put("properties", set_prop.size());
						data.put("value_literal", set_value_literal.size());
						data.put("value_uri", set_value_uri.size());					
					}

				}
				print_csv(f_summary, data, true);
				
				
			}
			tovisit.addAll(tovisit_new);
			tovisit.removeAll(visited);
		}
		
		
		//post visiting a seed
		data_group.put("sameas_triple", cnt_sameas_triple);
		data_group.put("sameas_uri", cnt_sameas_uri);
		data_group.put("sameas_uri_good", cnt_sameas_uri_good);

		DataObjectCounter<String> oc;
		
		data_group.put("property_total", map_property_uri.keySet().size());
		oc = save_count(map_property_uri,new File(dir, String.format("%05d-oc-property-uri.csv",groupid)));
		data_group.put("property_uri2", oc.size());		
		oc = save_count(map_property_source,new File(dir, String.format("%05d-oc-property-source.csv",groupid)));
		data_group.put("property_source2", oc.size());		
		
		data_group.put("datatypeproperty_total", map_datatypeproperty_uri.keySet().size());
		oc = save_count(map_datatypeproperty_uri,new File(dir, String.format("%05d-oc-datatypeproperty-uri.csv",groupid)));
		data_group.put("datatypeproperty_uri2", oc.size());		
		oc = save_count(map_datatypeproperty_source,new File(dir, String.format("%05d-oc-datatypeproperty-source.csv",groupid)));
		data_group.put("datatypeproperty_source2", oc.size());		
		

		data_group.put("literal_total", map_literal_uri.keySet().size());
		oc =save_count(map_literal_uri,new File(dir, String.format("%05d-oc-literal-uri.csv",groupid)));
		data_group.put("literal_uri2", oc.size());		
		oc =save_count(map_literal_source,new File(dir, String.format("%05d-oc-literal-source.csv",groupid)));
		data_group.put("literal_source2", oc.size());		

		oc =save_count(map_literal_property,new File(dir, String.format("%05d-oc-literal-property.csv",groupid)));
		data_group.put("literal_property2", oc.size());		
		count_property_set(map_literal_property, sz_context, groupid, new File(dir, "../property_set_by_value.csv"), m_property_mapping_by_value);
		
		oc =save_count(map_localname_property,new File(dir, String.format("%05d-oc-localname-property.csv",groupid)));
		data_group.put("localname_property2", oc.size());		
		count_property_set(map_localname_property, sz_context, groupid, new File(dir, "../property_set_by_localname.csv"), m_property_mapping_by_localname);

		
		//count source coverage on literal
		TreeSet<String> sources = new TreeSet<String>();
		sources.add("dbpedia.org");
		sources.add("rdf.freebase.com");
		//sources.add("data.nytimes.com");
		//sources.add("sws.geonames.org");
		
		for (String source1: sources){
			String [] names1 = source1.split("\\.");
			String name1= names1[names1.length-2];
			for (String source2: sources){
				String [] names2 = source2.split("\\.");
				String name2= names2[names2.length-2];
				String title = String.format("lt_%s_%s",name1,name2);
				switch (source1.compareTo(source2)){
				case 0:
					title = String.format("lt_%s",name1);
					break;
				case 1:
					continue;
				}
				HashSet<String> sources_check = new HashSet<String>();
				sources_check.add(source1);
				sources_check.add(source2);
				data_group.put(title, count_literal_source(map_literal_source,sources_check, true));
			}
		}
		data_group.put("lt_other", count_literal_source(map_literal_source,sources, false));
		
		print_csv(f_group, data_group, true);
	}
	
	private static void  count_property_set( DataPVHMap<String, String> map, String sz_context, int groupid, File f, DataObjectGroupMap<String> ogm) throws Sw4jException {
		//count mergeable properties
		{
			DataObjectCounter<TreeSet<String>> oc_property_set= new DataObjectCounter<TreeSet<String>>();
			for (String key : map.keySet()){
				//skip one-element eq-sets
				if (map.getValuesCount(key)<=1)
					continue;

				//skip integer number based eq-sets
				Integer integer = Integer.getInteger(key);
				if (null!=integer){
					continue;
				}

				TreeSet<String> property_set = new TreeSet<String>(map.getValues(key));
				oc_property_set.count(property_set);
			}
			
			for (TreeSet<String> property_set : oc_property_set.keySet()){
				DataSmartMap data_property_set = new DataSmartMap();
				data_property_set.put("c-context", sz_context);
				data_property_set.put("c-groupid", groupid);
				data_property_set.put("property_set", property_set);
				data_property_set.put("property_set_size", property_set.size());
				data_property_set.put("property_set_count", oc_property_set.getCount(property_set));
		
				print_csv(f, data_property_set, true);

				ogm.addObjectAllSame(property_set);
			}
			
		}		
	}
	
	private static int count_literal_source(
			DataPVHMap<String, String> mapLiteralSource, Set<String> sources, boolean bIncludeOrExclude) {
		int count =0;
		for (Set<String> values: mapLiteralSource.values()){
			if (bIncludeOrExclude){
				if (values.containsAll(sources)){
					count++;
				}
			}else{
				boolean contains=false;
				for (String source:  sources){
					if (values.contains(source)){
						contains =true;
					}					
				}
				if (!contains)
					count++;
			}
		}
		return count;
	}

	private static DataObjectCounter<String> save_count(DataPVHMap<String,String> map, File f_output){
		DataObjectCounter<String> oc= new DataObjectCounter<String>();
		for (String value : map.keySet()){
			if (map.getValuesCount(value)<=1)
				continue;
			oc.setCount(value, map.getValuesCount(value),map.getValuesAsSet(value));
		}
		ToolIO.pipeStringToFile(ToolString.printCollectionToString(oc.getSortedDataByCount()),f_output);
		return oc;
	}
/*
  load seed file
  identify seed URIs
  iteratively, find all owl:sameAs relation, and dereference their graph
    people-1/http__.....rdf
    people-1/same.owl
    people-1/same.csv
  
  link properties
    list of all properties from each source
    group properties sharing the same local name
    group properties sharing the same value
    
    align them automatically and then manually.
    
    people/property-group.csv
    
  link matched, conflict triples
    
  
  	
*/
	
	public static void print_csv(File f_output, DataSmartMap data, boolean append) throws Sw4jException{
		
		String ret ="";
		if (!f_output.exists()|| !append){
			ret += data.toCSVheader();
			ret += "\n";
		}
		ret += data.toCSVrow();
		ret += "\n";
		
		ToolIO.pipeStringToFile(ret, f_output, false, append);
	}
}
