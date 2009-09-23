package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;

import sw4j.util.DataPVTMap;
import sw4j.util.ToolString;
import static org.junit.Assert.fail;

import org.junit.Test;


public class TestToolFormula {
	@Test
	public void test_normalize(){
		HashSet<String> test_str= new HashSet<String>();

		test_str.add("richer(butler,agatha)");
		test_str.add("richer(butler,agatha)");
		test_str.add("richer(butler,agatha)  | hates(butler,butler)");
		test_str.add("~ hates(xAA_,agatha)  | ~ hates(xAA_,butler)  | ~ hates(xAA_,charles)");
		test_str.add("~ hates(AA_,agatha)  | ~ hates(AA_,butler)  | ~ hates(AA_,charles)");
		test_str.add("~ hates(A_,agatha)  | ~ hates(A_,butler)  | ~ hates(A_,charles)");
		test_str.add("~ hates(X,agatha)  | ~ hates(X,butler)  | ~ hates(X,charles)");
		test_str.add("~ hates(X0,agatha)  | ~ hates(X0,butler)  | ~ hates(X0,charles)");
		test_str.add("~ hates(X0,butler)  | ~ hates(X0,charles)  | ~ hates(X0,agatha)");
		test_str.add("~ hates(X1,agatha)  | ~ hates(X1,butler)  | ~ hates(X1,charles)");
		test_str.add("~ hates(agatha,A)  | hates(butler,A)");
		
		
		DataPVTMap <String,String> ret = ToolFormula.map_eq_formula(test_str);

		System.out.println(ToolString.printCollectionToString(ret.entrySet()));
		
		if (ret.keySet().size()!=6){
			fail();
		}
	}
}
