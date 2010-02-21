package org.inference_web.pml;

import java.util.HashSet;

import sw4j.util.DataPVTMap;
import sw4j.util.ToolString;
import static org.junit.Assert.fail;

import org.inference_web.util.ToolFormulaFol;
import org.junit.Test;


public class ToolFormulaFolTest {

	@Test
	public void test_normalize(){
		HashSet<String> test_str= new HashSet<String>();
		//	1
		test_str.add("richer(butler,agatha)");
		test_str.add("richer(butler,agatha)");
		
		//	2
		test_str.add("richer(butler,agatha)  | hates(butler,butler)");
		
		//	3
		// xAA is not variable name
		test_str.add("~ hates(xAA_,agatha)  | ~ hates(xAA_,butler)  | ~ hates(xAA_,charles)");
		
		//	4
		//change variable name
		test_str.add("~ hates(AA_,agatha)  | ~ hates(AA_,butler)  | ~ hates(AA_,charles)");
		test_str.add("~ hates(A_,agatha)  | ~ hates(A_,butler)  | ~ hates(A_,charles)");
		test_str.add("~ hates(X,agatha)  | ~ hates(X,butler)  | ~ hates(X,charles)");
		
		//change order
		test_str.add("~ hates(X0,agatha)  | ~ hates(X0,butler)  | ~ hates(X0,charles)");
		test_str.add("~ hates(X0,butler)  | ~ hates(X0,charles)  | ~ hates(X0,agatha)");
		test_str.add("~ hates(X1,agatha)  | ~ hates(X1,butler)  | ~ hates(X1,charles)");
		
		//	5
		test_str.add("~ hates(agatha,A)  | hates(butler,A)");

		// 6
		test_str.add("~ killed(X,Y)  | hates(X,Y)");
		test_str.add(" hates(X1,X2)  | ~ killed(X1,X2) ");
		
		DataPVTMap <String,String> ret = ToolFormulaFol.map_eq_formula(test_str);

		System.out.println(ToolString.printCollectionToString(ret.entrySet()));
		
		if (ret.keySet().size()!=5){
			String message=  String.format("expect %d , found %d \n", 6, ret.keySet().size());
			System.out.println(message);
			fail(message);
		}
	}
}
