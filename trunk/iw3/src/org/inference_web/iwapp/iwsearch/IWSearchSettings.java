package org.inference_web.iwapp.iwsearch;

import java.io.File;
import java.io.IOException;
import sw4j.util.DataSettings;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;




public class IWSearchSettings extends DataSettings{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DIR_IWSearch_HOME ="dir_IWSearch_home";

	final static String [] FIELDS_REQUIRED ={
		DIR_IWSearch_HOME,
	};
	
	public static final String DIR_IWSearch_INPUT ="dir_input";
	public static final String DIR_IWSearch_OUTPUT ="dir_output";
	public static final String DIR_IWSearch_RUNTIME ="dir_runtime";
	public static final String FILE_RUNTIME_PERMIT = "file_runtime_permit";
	public static final String FILE_RUNTIME_FAILURE = "file_runtime_failure";

	public static final String RESULT_OK = "ok";


	public static String [][] MORE_FIELD_VALUE = new String [][]{
		{DIR_IWSearch_INPUT, "input"},
		{DIR_IWSearch_OUTPUT, "output"},
		{DIR_IWSearch_RUNTIME, "runtime"},
		{FILE_RUNTIME_PERMIT, "runtime/permit"},
		{FILE_RUNTIME_FAILURE, "runtime/failure"},
	};
	
	public static final String STR_DEFAULT_IWSearch_CONFIG = "iwsearch.conf";
	public static final String STR_DEFAULT_IWSearch_HOME = "/work/iw/iwsearch";
	

	/**
	 * test if the default settings is ok
	 * @param args
	 */
	public static void main(String[] args){
		IWSearchSettings settings = IWSearchSettings.loadDefault();
		if (null!=settings){
			System.out.println("Test succeed!");
		}else{
			System.out.println("Test failed!");
		}
	}	
	
	/** 
	 * load settings from default path
	 * 
	 * @return
	 */
	public static IWSearchSettings loadDefault(){
		System.out.println("use default path");
		
    	String filename = STR_DEFAULT_IWSearch_CONFIG;
    	IWSearchSettings settings = new IWSearchSettings();
		if (settings.load(filename)){
			return settings;
		}else{
			return null;
		}
	}
	
	/** 
	 * load settings from a file 
	 * @param filename
	 * @return
	 */
	public boolean load(String filename){
		try {
			try{
				load( ToolIO.prepareFileInputStream(filename));
				getLogger().info("loading configuration from "+ filename);
			}catch(Sw4jException e){
				getLogger().warning("configure file not exist, use default loading");
				this.setProperty(DIR_IWSearch_HOME, STR_DEFAULT_IWSearch_HOME);
			}
			
			// get the base file dir
			String szBase = getProperty(DIR_IWSearch_HOME);
			
			//  users cannot override the following fields
			for (int i=0; i<MORE_FIELD_VALUE.length; i++){
				//if (null==settings.get(MORE_FIELD_VALUE[i][0]))
				String field = MORE_FIELD_VALUE[i][0];
				String value = szBase+"/"+MORE_FIELD_VALUE[i][1];
				
				put(field, value);
			}
			
			return isValid(FIELDS_REQUIRED);
		}catch (IOException e){
			e.printStackTrace();
			System.out.println("cannot find Settings file: "+filename);
			//System.exit(0); //if the settings cannot be loaded, than we must stop any further operation
			return false;
		} 
	}
	
	public File getInputDir() throws Sw4jException {
		String szDir = getProperty(IWSearchSettings.DIR_IWSearch_INPUT);
		return ToolIO.prepareFile( szDir );
	}
	
	public String getFilepathIWSearchIndex() throws Sw4jException{
		String szDir = getProperty(IWSearchSettings.DIR_IWSearch_OUTPUT);
		File f = ToolIO.prepareFile( szDir );
		return f.getAbsolutePath();
	}
	
	public static final int OPTION_UNKNOWN = -1;
	public static final int OPTION_INDEX_INSTANCE =0;
	private static final String [] INDEX_PATH = {
		"inst",
	};
	public String getIndexPath(int option) throws Sw4jException{
		return getFilepathIWSearchIndex()+"/"+INDEX_PATH[option];
	}

	
	
	
	private final static  String [][] INDEX_PML_FIELDS = {
		{
			AgentIndexerInstance.FIELD_URI,
			AgentIndexerInstance.FIELD_TYPE,
			AgentIndexerInstance.FIELD_ID,
			AgentIndexerInstance.FIELD_LABEL,
			AgentIndexerInstance.FIELD_SOURCE,
			AgentIndexerInstance.FIELD_DATE_MODIFIED,
			AgentIndexerInstance.FIELD_DATE_SUBMIT,
			AgentIndexerInstance.FIELD_TAG,
		},
	};
	
	public String [] getIndexFields(int option){
		return INDEX_PML_FIELDS[option];
	}
	
	private final static  String [][] SORT_PML_FIELDS = {
		{
			AgentIndexerInstance.FIELD_URI,
			AgentIndexerInstance.FIELD_ID,
			AgentIndexerInstance.FIELD_LABEL,
			AgentIndexerInstance.FIELD_DATE_MODIFIED,
			AgentIndexerInstance.FIELD_DATE_SUBMIT,
		},
	};
	
	public static String [] getSortFields(int option){
		return SORT_PML_FIELDS[option];
	}

	

}

