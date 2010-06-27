package org.inference_web.iwapp.hypergraph.old;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import sw4j.task.load.ToolLoadHttp;
import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.util.ToolSafe;
import sw4j.util.ToolString;
import sw4j.util.web.AgentURLExtractor;

public class ToolTptpProblemGen {
	
	public static void main(String[] args) {
		
		try {
			new ToolTptpProblemGen().run();
		} catch (Sw4jException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public Set<String> set_category;
	public Set<String> set_problem;
	public Set<String> set_reasoner;
	public Set<String> set_solution;

	public  void run() throws Sw4jException{
		ToolIO.pipeStringToFile(
				"",
				"files/hypergraph/input/solution.csv",
				false);

		
		
//		HashSet<String> ret = new HashSet<String>();
		set_problem = new HashSet<String>();
		set_reasoner = new HashSet<String>();
		
		// list all categories
		set_category = listDirLink(ToolTptp.TPTP_BASE_URL, true, false);
		
		// list all problems
		{
			Iterator<String> iter = set_category.iterator();
			while (iter.hasNext()){
				String szCategory = iter.next();
				String category_url = String.format("%s%s/", ToolTptp.TPTP_BASE_URL, szCategory);
				Set<String> problem_data = listDirLink(category_url, true, false);
				
				set_problem.addAll(problem_data);

				//now visit each problem
				Iterator<String> iter_problem = problem_data.iterator();
				while (iter_problem.hasNext()){
					String szProblem = iter_problem.next();
					String problem_url = String.format("%s%s/", category_url, szProblem);
					Set<String> solution_data = listDirLink(problem_url, true, false);
					
					set_reasoner.addAll(solution_data);
					
					// now String each solution
					Iterator<String> iter_solution= solution_data.iterator();
					while (iter_solution.hasNext()){
						String szSolution = iter_solution.next();
						
						ToolIO.pipeStringToFile(
								String.format("%s,%s,%s\n", szCategory, szProblem, szSolution),
								"files/hypergraph/input/solution.csv",
								false,
								true);

					}
				}

			}
		}
		
		// save results
		ToolIO.pipeStringToFile(
				ToolString.printCollectionToString(set_category),
				"files/hypergraph/input/category.txt",
				false);
		ToolIO.pipeStringToFile(
				ToolString.printCollectionToString(set_problem),
				"files/hypergraph/input/problem.txt",
				false);
			
		ToolIO.pipeStringToFile(
				ToolString.printCollectionToString(set_reasoner),
				"files/hypergraph/input/reasoner.txt",
				false);

	}
	
	
	public static Set<String> listDirLink(String szBaseUrl, boolean bKeepDirectory, boolean bKeepFile){
		HashSet<String> ret = new HashSet<String>();
		String content = ToolLoadHttp.wget(szBaseUrl);
		if (ToolSafe.isEmpty(ret))
			return ret ;
		
		Iterator<String> iter = AgentURLExtractor.process(content, szBaseUrl).iterator();
		while (iter.hasNext()){
			String szUrl = iter.next();
			
			if (!szUrl.startsWith(szBaseUrl))
				continue;


			boolean bIsDirectory = szUrl.endsWith("/");
			boolean bIsFile = !bIsDirectory && !szUrl.endsWith(".");

			if (!bKeepDirectory &&  bIsDirectory)
				continue;
			
			if (!bKeepFile && bIsFile )
				continue;

			//extract sub link
			String temp = szUrl.substring(szBaseUrl.length(), szUrl.length()- (bIsDirectory?1:0));
			System.out.println(temp);

			ret.add(temp);
		}
		return ret;
	}
	
}
