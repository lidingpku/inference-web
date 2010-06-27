package org.inference_web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sw4j.util.DataPVTMap;

import com.ibm.icu.util.StringTokenizer;


public class ToolFormulaFol {
		
	public static DataPVTMap <String,String>  map_eq_formula(Set<String> setStr){
		DataPVTMap <String,String> ret = new DataPVTMap<String,String>(); 
		
		for(String str:setStr){
			ret.add(normalize(str),str);
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
	
	public static String normalize(String inputStr){
		
		//////////////////////////////////
		// predicate reorder
		ArrayList<String> list_temp = new ArrayList<String>();
		if (inputStr.indexOf('|')>0){
			StringTokenizer st = new StringTokenizer(inputStr,"|");
			boolean b_correct = true;
			while (st.hasMoreTokens()){
				String token =st.nextToken().trim();
				
				if (count_char(token,'(')!=count_char(token,')')){
					b_correct = false;
					break;
				}
				list_temp.add(token);
			}
			
			//sort array
			Object[] ary_temp= list_temp.toArray();
			Arrays.sort(ary_temp);
			
			
			if (b_correct){
				String sz_temp = "";
				for (Object token: ary_temp){
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
		String patternStr = "(?<=\\W)([A-Z][\\w]*)";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    
	    // Replace all occurrences of pattern in input
	    HashMap<String,String> var_map = new HashMap<String,String> ();
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
	        
	    	String id = matcher.group(0);
	    	String newid = var_map.get(id);
	    	if (null==newid){
		        //generate new variable id
	    		newid= String.format("X%03d",var_id);
		        var_id++;
		        var_map.put(id, newid);
	    	}
	        
	        //replace variable
	        matcher.appendReplacement(buf, newid);
	    }
	    //append the rest
	    matcher.appendTail(buf);
	    return buf.toString();

	}
	
}
