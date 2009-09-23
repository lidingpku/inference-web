package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sw4j.util.DataPVHMap;


public class ToolFormula {
		
	public static DataPVHMap <String,String>  map_eq_formula(HashSet<String> setStr){
		DataPVHMap <String,String> ret = new DataPVHMap<String,String>(); 
		
		for(String str:setStr){
			ret.add(normlize_formula(str),str);
		}
		return ret;
	}
	
	
	
	public static String normlize_formula(String inputStr){
		//used non-capture lookbehind feature 
		//see http://java.sun.com/j2se/1.5.0/docs/api/java/util/regex/Pattern.html
		//
		String patternStr = "(?<!\\w)([A-Z][\\w]*)";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    
	    // Replace all occurrences of pattern in input
	    StringBuffer buf = new StringBuffer();
	    boolean found = false;
		int var_id= 0;
	    while ((found != matcher.find())) {
	        //matcher.group();
	    	//debug
	    	if (matcher.groupCount()!=1){
	    		System.out.println("ERROR!");
	    		for (int i=0;i<matcher.groupCount();i++){
	    			System.out.println(i+":"+matcher.group(i));
	    		}
	    	}
	        
	        //generate new variable id
	        String replaceStr = String.format("X%03d",var_id);
	        var_id++;
	        
	        //replace variable
	        matcher.appendReplacement(buf, replaceStr);
	    }
	    //append the rest
	    matcher.appendTail(buf);
	    return buf.toString();

	}
	
}
