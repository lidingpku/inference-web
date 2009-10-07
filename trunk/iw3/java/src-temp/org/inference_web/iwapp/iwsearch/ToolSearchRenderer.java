package org.inference_web.iwapp.iwsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sw4j.app.pml.PMLJ;
import sw4j.app.pml.PMLP;
import sw4j.rdf.util.DataInstance;
import sw4j.rdf.util.ToolJena;
import sw4j.util.Sw4jException;
import sw4j.util.ToolSafe;
import sw4j.util.ToolURI;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ToolSearchRenderer {

	public static final String STR_NODESET_SERVICE = "http://browser.inference-web.org/iwbrowser/BrowseNodeSet?uri=";
	public static final String STR_IWBROWSE_SERVICE = "http://browser.inference-web.org/iwbrowser/NodeSetBrowser?url=";
	public static final String STR_REGISTRY_SERVICE = "http://registrar.inference-web.org/iwregistrar/ViewProvenance?uri=";
	public static final String STR_BROWSER2 = "http://onto.rpi.edu/browser2/browse?uri=";
	
	static Map gNS_PREFIX_MAP=null;
	static public Map get_NS_PREFIX_MAP(){
		if (null == gNS_PREFIX_MAP){
			gNS_PREFIX_MAP= new HashMap();
		     gNS_PREFIX_MAP.put("search","http://inference-web.org/2007/10/service/search.owl#");
		     gNS_PREFIX_MAP.put("iwsearch","http://inference-web.org/2007/10/service/iwsearch.owl#");
		}
		return gNS_PREFIX_MAP;
	}
	
	public static String printSearchResponseToString(DataSearchTask task){
		if (ARCHIVE.HTML.equals(task.getSearchResultSyntax())){
			return printQueryResponseInHTML(task);
		}else{
			Model m = task.getSearchResponseModel();
			m.setNsPrefixes(get_NS_PREFIX_MAP());
			return ToolJena.printModelToString(m, task.getSearchResultSyntaxJena());
		}
	}
	
	private static String printQueryResponseInHTML(DataSearchTask task){
		String ret ="";
		
		ret += "<html><head>" +
				"<link href=\"http://tw.rpi.edu/2008/05/sorttable.css\" rel=\"stylesheet\" type=\"text/css\" /> " +
				"<script src=\"http://tw.rpi.edu/2008/05/sorttable.js\"></script>"+
				"</head><body>";
		
		ret += renderSearchInfo(task);
		if (!task.hasError()){
			Model m = task.getSearchResponseModel();
			
			if (IWSEARCH.search_pml_instance.equals(task.getSearchServiceResource())){
				// render instances
				Iterator iter = m.listSubjectsWithProperty(RDF.type, IWSEARCH.InstanceMetadata);
				int index= task.getSearchStart();
				if (iter.hasNext()){
					ret +="<table class=\"sortable\" border=1>";
					ret +="<td><b>index</b></td>";
					ret +="<td><b>"+AgentIndexerInstance.FIELD_LABEL+"</b></td>";
					ret +="<td><b>"+AgentIndexerInstance.FIELD_TYPE+"</b></td>";
//					ret +="<td><b>"+AgentIndexerInstance.FIELD_ID+"</b></td>";
					ret +="<td><b>more</b></td>";
					ret +="<td><b>"+AgentIndexerInstance.FIELD_SOURCE+"</b></td>";
					ret +="<td><b>"+AgentIndexerInstance.FIELD_DATE_MODIFIED+"</b></td>";
					ret +="<td><b>"+AgentIndexerInstance.FIELD_DATE_SUBMIT+"</b></td>";
					while (iter.hasNext()){
						ret +=renderOnePMLInstance(m, (Resource)iter.next(), index);
						index++;
					}
					ret +="</table>";
				}
			}
			
			ret +=renderSearchNav(task);
		}else{
			// print error 
			ret ="Encountered Error: "+ task.getError();
		}
		ret +="</body></html>";
		return ret;
		
	}
	
	private static String renderOnePMLInstance(Model m, Resource instance_desc, int index){
		DataInstance ind = new DataInstance(instance_desc,m);
		
		Resource uri = ind.getPropertyValueAsURIResource(DC.subject);
		String identifier = ind.getPropertyValueAsString(DC.identifier);
		Resource type = ind.getPropertyValueAsURIResource(DC.type);
		String label = ind.getPropertyValueAsString(RDFS.label);
		Resource source =ind.getPropertyValueAsURIResource(DC.source);

		String ret = "<tr>";
		

		ret+="<td>";
		ret+=index;
		ret+="</td>";

		ret+="<td>";
		if (null!= label){
			 ret +=label;
		}else{
			ret+="n/a";
		}
		ret+="</td>";

		ret+="<td>";
		if (null!= type){
			
			// ret +=renderURL(type.getURI(),type.getLocalName(),true);
			String szUri = type.getURI();
			try {
				szUri = String.format("%s%s",
						STR_BROWSER2,
						ToolURI.encodeURIString(type.getURI()));
			} catch (Sw4jException e) {
				e.printStackTrace();
			}
			 ret +=renderURL(szUri, type.getLocalName(), true);

		}else{
			ret+="n/a";
		}
		ret+="</td>";
		
		ret+="<td>";
		if (null!= uri){
			String cell_content = "";
			
			String szUri = uri.getURI();
			if (null!=type && PMLJ.getURI().equals(type.getNameSpace())){
				if (type.getLocalName().equals(PMLJ.NodeSet_lname)){
					try {
						szUri = String.format("%s%s",
								STR_NODESET_SERVICE,
								ToolURI.encodeURIString(szUri));
					} catch (Sw4jException e) {
						e.printStackTrace();
					}
				}else{
					try{
						szUri = String.format("%s%s",
								STR_IWBROWSE_SERVICE,
								ToolURI.encodeURIString(szUri));
					} catch (Sw4jException e) {
						e.printStackTrace();
					}
				}
				cell_content =renderURL(szUri,"browse", true);
			}else if (null!=type && PMLP.getURI().equals(type.getNameSpace())){
				try {
					szUri = String.format("%s%s",
							STR_REGISTRY_SERVICE,
							ToolURI.encodeURIString(szUri));
				} catch (Sw4jException e) {
					e.printStackTrace();
				}
				cell_content =renderURL(szUri,"browse", true);
			}
		
			
			{
				szUri = uri.getURI();
				try {
					szUri = String.format("%s%s",
							STR_BROWSER2,
							ToolURI.encodeURIString(szUri));
				} catch (Sw4jException e) {
					e.printStackTrace();
				}
				
				String szTemp = renderURL(szUri,"browser2", true);
				if (cell_content.length()==0)
					cell_content = szTemp;
				else
					cell_content = String.format("%s&nbsp;,&nbsp;%s", szTemp, cell_content );
			}
			
			ret += cell_content;
			//result += renderURL(service_uri+ forURL(uri.getURI()),"IWBrowser", true);
		}else{
			ret+="n/a";
			//TODO
			// skip anonymous instances
			//return "";
		}
		ret+="</td>";


		
		ret+="<td>";
		if (null!=source){
			 ret +=renderURL(source.getURI(),true);
		}else{
			ret+="n/a";
		}
		ret+="</td>";

		
		
		ret+="<td>";
		String date =ind.getPropertyValueAsString(DCTerms.modified);
		if (null!= date){
			 ret +=date;
		}else{
			ret+="n/a";
		}
		ret+="</td>";

		
		ret+="<td>";
		date =ind.getPropertyValueAsString(DCTerms.dateSubmitted);
		if (null!= date){
			 ret +=date;
		}else{
			ret+="n/a";
		}
		ret+="</td>";
		
		
		ret +="</tr>";

		
		return ret;
		
	}
	
	
	private static String renderSearchInfo(DataSearchTask task){
		int temp=task.getSearchStart();
		if (temp<=task.m_nSearchTotalResults){
			return str_remark(temp)
			+"&nbsp;-&nbsp;"
			+str_remark(task.getSearchStart()-1 + task.m_nSearchReturnedResults)
			+" of total "
			+str_remark(task.m_nSearchTotalResults)
			+" results for "
			+str_remark(task.getSearchString())
			+" in "
			+str_remark(task.m_fSearchProcessTimeSeconds)
			+" seconds";
		}else{
			return 
			"0 answers"
			+" of total "
			+str_remark(task.m_nSearchTotalResults)
			+" results for "
			+str_remark(task.getSearchString())
			+" in "
			+str_remark(task.m_fSearchProcessTimeSeconds)
			+" seconds";
		}
	}
	
	private static String renderSearchNav(DataSearchTask task){
		String temp ="";
		int start = task.getSearchStart();
		int totoal = task.m_nSearchTotalResults;
		
		if (start>0){
			// show prev
			int prev_start = Math.max(1,start - task.getSearchLimit());
		}
		
		return temp;
	}
	
	private static String str_remark(int number){
		return "<b>"+number+"</b>";
	}
	private static String str_remark(double number){
		return "<b>"+number+"</b>";
	}
	private static String str_remark(String msg){
		return "<b>"+msg+"</b>";
	}
	private static String renderURL(String link, boolean bNormal){
		String temp = "<a href=\""+link+"\">";
		if (!bNormal)
			temp +="<font color=\"000000\">";
		temp += link;
		if (!bNormal)
			temp +="</font>";
		temp +="</a>";
		return temp;
	}	
	
	private static String renderURL(String link, String text, boolean bNormal){
		String temp = "<a href=\""+link+"\">";
		if (!bNormal)
			temp +="<font color=\"000000\">";
		
		if (ToolSafe.isEmpty(text))
			temp+= link;
		else
			temp += text;
		
		if (!bNormal)
			temp +="</font>";
		temp +="</a>";
		return temp;

	}	
}
