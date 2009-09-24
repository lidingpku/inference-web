package org.inference_web.iwapp.hypergraph;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.util.StringTokenizer;

import sw4j.util.DataPVTMap;


public class ToolFormula {
		
	public static DataPVTMap <String,String>  map_eq_formula(Set<String> setStr){
		DataPVTMap <String,String> ret = new DataPVTMap<String,String>(); 
		
		for(String str:setStr){
			ret.add(normlize_formula(str),str);
		}
		return ret;
	}
	
	private static int count_char(String sz_text, char pattern ){
		int count=0;
		for (char ch: sz_text.toCharArray()){
			if (ch==pattern)
				count++;
		}
		return count;
	}
	
	public static String normlize_formula(String inputStr){
		//////////////////////////////////
		// predicate reorder
		TreeSet<String>set_temp = new TreeSet<String>();
		if (inputStr.indexOf('|')>0){
			StringTokenizer st = new StringTokenizer(inputStr,"|");
			boolean b_correct = true;
			while (st.hasMoreTokens()){
				String token =st.nextToken().trim();
				
				if (count_char(token,'(')!=count_char(token,')')){
					b_correct = false;
					break;
				}
				set_temp.add(token);
			}
			if (b_correct){
				String sz_temp = "";
				for (String token: set_temp){
					if (sz_temp.length()>0){
						sz_temp +=" | ";
					}
					sz_temp +=token;
				}
				//System.out.println("rewrote formula TO "+sz_temp +" FROM "+ inputStr);
				inputStr = sz_temp;
			}
		}		
		
		/////////////////////////////
		// variable renaming
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
