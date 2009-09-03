package org.inference_web.iwapp.hypergraph.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;



public class ToolTptpStat {
    
	public static String DIR_STAT= "files/hypergraph/stat";
	
    public static void main(String[] args) {
    	String szFilename = args.length>0? args[0]: "files/hypergraph";
    	System.out.println("starting at - "+ szFilename);
    	
    	try {
        	//reset stat
			ToolIO.deleteDirectory(ToolIO.prepareFile(DIR_STAT));

			//generate new stat
			new ToolTptpStat().traverse(new File(szFilename));
		} catch (Sw4jException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    
    private void processDir(File dir) {
    	if (!dir.getName().endsWith(".csv"))
    		return;

        System.out.print( (dir.isDirectory() ? "[D] : " : "[F] : "));
        System.out.println(dir);

        try {
        	BufferedReader in = new  BufferedReader(new FileReader(dir));
			String line =null;
			while (null!=(line=in.readLine())){
				StringTokenizer st = new StringTokenizer(line,",");
				String szFileName = String.format("%s/%s-%s.csv",DIR_STAT, st.nextToken(), st.nextToken());
				//System.out.println(szFileName);
				
				ToolIO.pipeStringToFile(line+"\n", szFileName, false, true);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    private void traverse(File dir) {

        processDir(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                traverse(new File(dir, children[i]));
            }
        }

    }



}
