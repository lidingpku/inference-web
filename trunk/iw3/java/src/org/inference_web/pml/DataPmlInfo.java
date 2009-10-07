package org.inference_web.pml;

import sw4j.app.pml.PMLP;
import sw4j.rdf.util.ToolJena;
import sw4j.util.ToolSafe;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class DataPmlInfo {
	Resource res_info = null;
	Resource res_language = null;
	String sz_raw_string  = null;
	String sz_pretty_string = null;
	
	public static final int LANG_TPTPCNF = 0;
	public static final int LANG_TPTPFOF = 1;
	public static final int LANG_KIF = 2;
	public static final String [] LANG = new String []{
		"TPTPCNF",
		"TPTPFOF",
		"KIF",
	};
	
	public DataPmlInfo(Resource res_info, Model m){
		this.res_info = res_info;
		this.res_language = ToolJena.getValueOfProperty(m, res_info, PMLP.hasLanguage, (Resource)null);
		this.sz_raw_string = ToolJena.getValueOfProperty(m, res_info, PMLP.hasRawString, (String)null);
		this.sz_pretty_string = ToolJena.getValueOfProperty(m, res_info, PMLP.hasPrettyString, (String)null);
	}
	
	public String getRawString(){
		return sz_raw_string;
	}
	
	public String getPrettyString(){
		if (null!=sz_pretty_string){
			return sz_pretty_string;
		}
		return getPrettyString(sz_raw_string, res_language);
	}
	
	public static String getPrettyString(String sz_raw_string, Resource res_language){
		if (ToolSafe.isEmpty(sz_raw_string))
			return "";
		
		if (check_language(LANG_KIF, res_language))
			return getPrettyStringKif(sz_raw_string);

		return sz_raw_string;	
	}
	
	private boolean check_language(int lang_index ){
		return check_language(lang_index, res_language);
	}
	
	public static boolean check_language(int lang_index, Resource res_language){
		if (ToolSafe.isEmpty(res_language)||ToolSafe.isEmpty(res_language.getLocalName())|| lang_index>=LANG.length)
			return false;
		
		return LANG[lang_index].equals(res_language.getLocalName());
	}
	
	
	public String getNormalizedString(){
		if (check_language(LANG_TPTPCNF))
			return ToolFormulaFol.normalize(sz_raw_string);
		else if (check_language(LANG_TPTPFOF))
			return ToolFormulaFol.normalize(sz_raw_string);
		else
			return sz_raw_string;
	}

	public String getLanguageString(){
		if (ToolSafe.isEmpty(res_language)||ToolSafe.isEmpty(res_language.getLocalName()))
			return "";
		else
			return res_language.getLocalName();
	}
	
	@Override
	public String toString() {
		return getRawString()+"@"+getLanguageString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((res_language == null) ? 0 : res_language.hashCode());
		result = prime * result
				+ ((getNormalizedString() == null) ? 0 : getNormalizedString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataPmlInfo other = (DataPmlInfo) obj;
		if (res_language == null) {
			if (other.res_language != null)
				return false;
		} else if (!res_language.equals(other.res_language))
			return false;
		if (getNormalizedString() == null) {
			if (other.getNormalizedString() != null)
				return false;
		} else if (!getNormalizedString().equals(other.getNormalizedString()))
			return false;
		return true;
	}

	private static String getPrettyStringKif(String sz_raw_string){
		if (ToolSafe.isEmpty(sz_raw_string))
			return "";
		return sz_raw_string.replaceAll("\\|[^\\|]\\|::","");
	}
}
