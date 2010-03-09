package org.inference_web.pml;

import java.util.Set;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Resource;

public class DataPmlGraph {
	Set<Resource> set_info = new HashSet<Resource>();
	Set<Resource> set_step = new HashSet<Resource>();
	Set<Resource> set_nodeset = new HashSet<Resource>();
	Resource res_root_info = null;
}
