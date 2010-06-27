package org.inference_web.pml;



import sw4j.rdf.util.ToolJena;
import sw4j.task.common.AbstractTaskDesc;
import sw4j.util.Sw4jMessage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


public class InspectFilterPmlData extends AbstractTaskDesc{

	////////////////////////////////////////////////
	// SwutilEvaluationTask (super class)
	////////////////////////////////////////////////
	public static final String ERROR_SUMMARY_1 = "No PML data.";
	public static final String INFO_SUMMARY_2 = "some non-PML data removed.";
	public static final String WARN_SUMMARY_3 = "A PML individual should not be anonymous.";

	public static final String REPORT_TITLE ="Inspect presence of PML data";
	public static final String REPORT_DESC ="This service checkes there exists PML data in the supplied data, and keep only pml data thereafter";

	@Override
	public String getTitle(){
		return REPORT_TITLE;//DataTaskLoad.class.getSimpleName();
	}
	
	@Override
	public String getDescription(){
		return REPORT_DESC;//"load content from specified file, URL or text string";
	}
	
	/**
 	 * filter PML data from an RDF graph, inspect it.  
 	 * 
	 * @param model_data
	 * @param removeNonPml	whether remove non-pml data from the input model
	 * @return
	 */
	public static InspectFilterPmlData inspect(Model model_data, boolean removeNonPml){
		InspectFilterPmlData task = new InspectFilterPmlData();
		if (null==model_data)
			return null;
		
		Model m = PmlAnalyzer.filterPMLInstanceData(model_data);
		if (null==m || m.isEmpty()){
			int error_level =Sw4jMessage.STATE_ERROR;
			String error_summary = ERROR_SUMMARY_1;
			String error_details = "Cannot find PML data in specific document or its referred ontologies. The existence of PML data can be decided by checking the usage of classes and properties defined by PML ontologies. " +
			"Currently, PMLValidator supports the following PML ontologies: " + PmlConst.listSupportedPmlNamespace()+"."+
			"The PML ontology published at Stanford are old versions, and the current PML ontologies are published under http://inference-web.org/2.0/ .}";
			
			String error_creator = task.getClass().getSimpleName();

			task.getReport().addEntry(error_level, error_summary, error_creator, error_details, false);		
			return task;
		}

		if (m.size()> model_data.size()){
			int error_level =Sw4jMessage.STATE_INFO;
			String error_summary = INFO_SUMMARY_2;
			String error_details = String.format("%d triples are kept and %d triples has been removed",m.size(), model_data.size()-m.size());
			String error_creator = task.getClass().getSimpleName();

			task.getReport().addEntry(error_level, error_summary, error_creator, error_details, false);		
		}
		
		task.validatePmlClassMustHaveUri(m);

		if (removeNonPml){
			model_data.removeAll();
			ToolJena.update_copy(model_data, m);
		}
		return task;
	}


	
	/**
	 * validation(WARNING). Instances of some PML classes must have specific URI.
	 *    
	 * 
	 * @param m_model  a jena.Model containing PML data 
	 * @return  false if found violation
	 */
	public void validatePmlClassMustHaveUri(Model model_data){
		StmtIterator iter = model_data.listStatements(null, RDF.type, (RDFNode)null);
		while (iter.hasNext()){
			Statement stmt = iter.nextStatement();
			Resource inst = stmt.getSubject();
			Resource type = (Resource)stmt.getObject();

			if (inst.isAnon()){
				if (PmlConst.getPmlClassMustHaveUri().contains(type)){
					int error_level =Sw4jMessage.STATE_WARNING;
					String error_summary = WARN_SUMMARY_3;
					String error_details = String.format("found an anonymous instance of PML class %s ", type);
					String error_creator = this.getClass().getSimpleName();

					if (null== this.getReport().addEntry(error_level, error_summary,  error_creator, error_details, true))
						break;
				}
			}
		}
	}
}
