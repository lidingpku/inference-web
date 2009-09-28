package org.inference_web.pml;

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLR;
import sw4j.rdf.util.ToolJena;
import sw4j.util.DataQname;
import sw4j.util.ToolString;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ToolPmlTest {
	@Test
	public void test_load(){
		String[][] ary_url = new String[][]{
				//load multiple files
				{"http://inference-web.org/proofs/tonys/tonys_1/ns2.owl#ns2",
						"29",
						"2"},
						//load multiple files
						{"http://inference-web.org/proofs/tonys/tonys_2/ns8.owl#ns8",
								"119",
								"8"},					
						//load single file
			{"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/answer.owl",
				"706",
				"29"},		
				


	

			{"http://inference-web.org/proofs/tonys/tonys_1/ns2.owl",
						"29",
						"2"},
			{"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/combine_P2_combine.owl",
							"2917",
							"140"},//"74"},
		};
		
		for (String[] data : ary_url){
			String sz_url_pml = DataQname.extractNamespaceUrl(data[0]);
			
			Map<String, Model> map = ToolPml.pml_load(sz_url_pml);
			Model model_data = map.get(sz_url_pml);
			Model model_all = ToolJena.model_merge(map.values());

			System.out.println("-------------test load summary-----------");
			System.out.println("loaded:"+ToolString.printCollectionToString(map.keySet()));
			System.out.println(String.format("data size: %s, data all size: %s",model_data.size(), model_all.size()));
			if (!data[1].equals(""+model_all.size())){
				fail("not enough triples loaded");
			}
			//sign
			model_all = ToolPml.pml_sign(model_all, sz_url_pml);
			
			//index
			ToolPml.pml_index(model_all);
			test_index(model_all);
			
			test_transitive_steps(model_data, model_all, data[2]);
			
		}
	}
	

	private Model test_index(Model m){
		Property[] ary_property = new Property[]{
				PMLR.dependsOn,
				PMLR.dependsOnDirect,	
				PMLR.hasMember,	
			};
		for (Property property: ary_property){
			if (!m.listSubjectsWithProperty(property).hasNext())
				fail("expect property "+property);
		}
		return m;
	}
	
	private void test_transitive_steps(Model model_data, Model model_all, String sz_cnt_step){
		Set<Resource> set_root = ToolPml.list_roots(model_data);
		if (set_root.size()!=1){
			fail("expect exactly one root");
		}
		Resource res_root = set_root.iterator().next();
		
		Resource res_root_step = ToolJena.getValueOfProperty(model_all, res_root, PMLJ.isConsequentOf, (Resource)null);  
		Set<Resource> set_step = ToolPml.list_depending_steps(model_all, res_root_step);
		if (!sz_cnt_step.equals(""+set_step.size())){
			System.out.println(res_root);
			fail(String.format("unexpect number of steps: found %d, expected %s",set_step.size(), sz_cnt_step));
		}
	}

}
