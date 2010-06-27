package org.inference_web.app.sameas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import sw4j.util.DataQname;
import sw4j.util.Sw4jException;

public class TestSplitUri {
	public static void main (String[] args){
		
		{
			File f= new File(String.format("local/input/classes.txt"));
			File f_out= new File(String.format("local/output/classes.tsv"));
		    String thisLine;
		  
		      //Open the file for reading
		      try {
			    //PrintWriter out = new PrintWriter(System.out);
			    PrintWriter out = new PrintWriter( new FileWriter(f_out));
		        BufferedReader br = new BufferedReader(new FileReader(f));
		        while ((thisLine = br.readLine()) != null) { // while loop begins here
		        	
		        	if (thisLine.startsWith("http://")|| thisLine.startsWith("https://")){
	        	
			        	DataQname qname = DataQname.create(thisLine);
			        	
			        	out.println(String.format("%s\t%s\t%s", thisLine, qname.getNamespace(), qname.getLocalname()));
		        	}else
			        	out.println(String.format("%s\t\t", thisLine));
		        	
		        } // end while 
	        	out.flush();
	        	out.close();
		      } // end try
		      catch (IOException e) {
		        System.err.println("Error: " + e);
		      } catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
