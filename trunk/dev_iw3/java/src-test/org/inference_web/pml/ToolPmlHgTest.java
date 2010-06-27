package org.inference_web.pml;

import static org.junit.Assert.fail;

import org.junit.Test;

import sw4j.util.DataQname;
import com.hp.hpl.jena.rdf.model.Model;

public class ToolPmlHgTest {
	@Test
	public void test_load_one(){
		String sz_url_pml ="http://onto.rpi.edu/temp/mage-pml-v2.rdf";
		
		DataPmlHg hg = new DataPmlHg();
		hg.add_data(sz_url_pml, null);
		
		Model m = hg.getModelAll();
		ToolPml.pml_update_index(m);

		{
			String sz_dot = hg.graphviz_export_dot(hg.getSubHg());
			String subpath = sz_url_pml.replaceAll("\\W+", "_");
			DataPmlHg.graphviz_save(sz_dot, "local/output/pml/"+ subpath);			
		}

		
		
	}
	
	@Test
	public void test_load(){
		String[][] ary_url = new String[][]{
				//simple case, 2 files
				{"http://inference-web.org/proofs/tonys/tonys_1/ns2.owl#ns2",
					"49",
				"2"},

				//more files
				{"http://inference-web.org/proofs/tonys/tonys_2/ns8.owl",
					"263",
				"8"},	


				//single file, duplicated information
				{"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/answer.owl",
					"1896",
				"29"},		


				//multiple files, multiple inference steps
				{"http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/EP---1.1pre/combine_P2_combine.owl",
					"8468",
				"78"},
		};

		for (String[] data : ary_url){
			String sz_url_pml = DataQname.extractNamespaceUrl(data[0]);

			DataPmlHg hg = new DataPmlHg();
			hg.add_data(sz_url_pml, null);

			Model mapping_i = ToolPml.create_mappings(hg.getModelAll());
			hg.add_mapping(mapping_i);
			
			verify(data[1],""+hg.getModelAll().size());
			verify(data[2],""+hg.getSubHg(hg.getRoot(sz_url_pml)).size());

			String sz_dot = hg.graphviz_export_dot(hg.getSubHg(hg.getRoot(sz_url_pml)));
			
			String subpath = sz_url_pml.replaceAll("\\W+", "_");
			if ( sz_url_pml.indexOf("proofs")>0)
					subpath = sz_url_pml.substring(sz_url_pml.indexOf("proofs")).replaceAll("\\W+", "_");

			DataPmlHg.graphviz_save(sz_dot, "local/output/pml/"+ subpath);
		}
	}


	private void verify(String expected, String actual){
		if (!expected.equals(actual)){
			String message =String.format("ERROR: expected: %s, actual: %s", expected, actual);
			fail(message);
		}
	}
}
