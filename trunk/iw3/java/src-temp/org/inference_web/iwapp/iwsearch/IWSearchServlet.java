package org.inference_web.iwapp.iwsearch;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryParser.ParseException;

import sw4j.util.Sw4jException;

import com.hp.hpl.jena.rdf.model.Model;


public class IWSearchServlet extends HttpServlet{
	

	
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException
		{
	    	doProcessQuery(request,response);
		}
		
	    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException
		{
	    	doProcessQuery(request,response);
		}
	    
		public void sendResponse(DataSearchTask task, HttpServletResponse response) throws IOException {
			Model m = task.getSearchResponseModel();
	        response.setContentType(task.getSearchResultMimeType());
	        PrintWriter out = response.getWriter();
			out.print(ToolSearchRenderer.printSearchResponseToString(task));
			out.close();
		}
	    
	    public void doProcessQuery(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException
	    {
	    	DataSearchTask task = new DataSearchTask();

	    	//set result syntax
	    	task.initSearchResultSyntax( (String) request.getParameter(SEARCH.usesSearchResultSyntax.getLocalName()) );
	    	
			// pre-process request from http-request parameters
			boolean bInitSucceed = task.init (
					(String) request.getParameter(SEARCH.usesSearchService.getLocalName()),
					(String) request.getParameter(SEARCH.hasSearchString.getLocalName()),
					(String) request.getParameter(SEARCH.hasSearchSortField.getLocalName()),
					(String) request.getParameter(SEARCH.hasSearchStart.getLocalName()),
					(String) request.getParameter(SEARCH.hasSearchLimit.getLocalName())    );
			
			if (!bInitSucceed){
				sendResponse(task, response);
				return;
			}
			
			try {
				IWSearcher.get().search(task);
			} catch (ParseException e) {
				task.setError(e.getMessage());
			} catch (Sw4jException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sendResponse( task,  response);
	    }
}
