/**
 * IWSEARCH.java
 *
 *  This file is automatically generated by PML API 
 *	generated on  Wed Oct 24 15:11:31 EDT 2007
 *  
 *  @author: Li Ding (http://www.cs.rpi.edu/~dingl )
 *
 */
package  org.inference_web.iwapp.iwsearch;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 *
 *  
 *  Ontology information 
 *   	label:  IWSearch Ontology@en-US
 *   	comment:   This ontology defines terms for IWSearch. This ontology is create by Li Ding (http://www.cs.rpi.edu/~dingl/).
  @en-US
 */
public class IWSEARCH{

    protected static final String NS = "http://inference-web.org/2007/10/service/iwsearch.owl#";

    public static final String getURI(){ return NS; }

	// Class (3)
	 public final static String ClassMetadata_lname = "ClassMetadata";
	 public final static String ClassMetadata_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#ClassMetadata";
	 public final static Resource  ClassMetadata = ResourceFactory.createResource(ClassMetadata_uri);

	 public final static String InstanceMetadata_lname = "InstanceMetadata";
	 public final static String InstanceMetadata_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#InstanceMetadata";
	 public final static Resource  InstanceMetadata = ResourceFactory.createResource(InstanceMetadata_uri);

	 public final static String IWIndexStatistics_lname = "IWIndexStatistics";
	 public final static String IWIndexStatistics_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#IWIndexStatistics";
	 public final static Resource  IWIndexStatistics = ResourceFactory.createResource(IWIndexStatistics_uri);

	// Property (3)
	 public final static String hasTotalType_lname = "hasTotalType";
	 public final static String hasTotalType_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#hasTotalType";
	 public final static Property  hasTotalType = ResourceFactory.createProperty(hasTotalType_uri);

	 public final static String hasTotalInstance_lname = "hasTotalInstance";
	 public final static String hasTotalInstance_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#hasTotalInstance";
	 public final static Property  hasTotalInstance = ResourceFactory.createProperty(hasTotalInstance_uri);

	 public final static String hasTotalSource_lname = "hasTotalSource";
	 public final static String hasTotalSource_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#hasTotalSource";
	 public final static Property  hasTotalSource = ResourceFactory.createProperty(hasTotalSource_uri);

	// Instance (4)
	 public final static String search_pml_instance_lname = "search_pml_instance";
	 public final static String search_pml_instance_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#search_pml_instance";
	 public final static Resource  search_pml_instance = ResourceFactory.createResource(search_pml_instance_uri);

	 public final static String example_stat_lname = "example_stat";
	 public final static String example_stat_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#example_stat";
	 public final static Resource  example_stat = ResourceFactory.createResource(example_stat_uri);

	 public final static String example_class_person_lname = "example_class_person";
	 public final static String example_class_person_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#example_class_person";
	 public final static Resource  example_class_person = ResourceFactory.createResource(example_class_person_uri);

	 public final static String example_instance_dlm_lname = "example_instance_dlm";
	 public final static String example_instance_dlm_uri = "http://inference-web.org/2007/10/service/iwsearch.owl#example_instance_dlm";
	 public final static Resource  example_instance_dlm = ResourceFactory.createResource(example_instance_dlm_uri);



}


 