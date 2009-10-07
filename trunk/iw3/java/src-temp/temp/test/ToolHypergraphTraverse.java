package temp.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sw4j.util.Sw4jException;
import sw4j.util.ToolIO;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;
import sw4j.task.graph.DataDigraph;

/**
 * traverse a hypergraph (aka AND/OR graph) and report solutions
 * psudo-code
 * 
 * search
 *   * if isGoal then return 
 *   * step = findNextStep
 *   * processNextStep
 * 
 * 
 * @author Li Ding
 *
 */
public class ToolHypergraphTraverse {
	
	public static boolean debug = false;
	

	// threshold for how many seconds can be used between two effective solutions
	long m_config_timer_limit  = -1;
	long m_runtime_timer_start;
	long m_runtime_timer_end;
	public boolean isAboveLimitTimeout(){
		return (-1 != m_config_timer_limit && (m_runtime_timer_end - m_runtime_timer_start)>= m_config_timer_limit);
	}

	// how many seconds have been used 
	long m_runtime_process_start;
	long m_runtime_process_end;
	/**
	 * return the seconds used by this process
	 * @return
	 */
	public double getProcessSeconds(){
		return (this.m_runtime_process_end- this.m_runtime_process_start)/1000.0;
	}

	
	// threshold for how many solutions have been seen
	int m_config_solution_count_limit  = -1;
	int m_runtime_solution_count = 0;
	public boolean isAboveLimitSolutionCount(){
		return -1 != m_config_solution_count_limit &&  m_runtime_solution_count>= m_config_solution_count_limit;
	}

	/**
	 * return how many solutions have been found
	 * @return
	 */
	public int getSolutionCount() {
		return this.m_runtime_solution_count;
	}
	
	
	// record samples of found solutions
	int m_config_solutions_limit = 1;
	ArrayList<DataHyperGraph> m_runtime_solutions = new ArrayList<DataHyperGraph>();

	protected void saveSolution(DataHyperGraph g){

		if (-1 == m_config_solution_count_limit ||  m_runtime_solutions.size() < m_config_solutions_limit)
			m_runtime_solutions.add(g);
	}
	
	
	// file based run time control to stop the process
	String m_szFileName_runtime = null;


	/**
	 * return a list of recorded solutions
	 * @return
	 */
	public List<DataHyperGraph> getSolutions(){
		return this.m_runtime_solutions;
	}

	
	/**
	 * traverse hypergraph to find solution graph
	 * @param G
	 * @param v
	 */
	public void traverse(DataHyperGraph G, Integer v){
		traverse(G, v, -1, -1, -1);
	}	
	
	/**
	 * traverse hypergraph to find solution graph with a limit of returned result
	 * @param G
	 * @param v
	 * @param limit - total result to be returned (-1 means no limit)
	 * @return
	 */
	public void traverse(DataHyperGraph G, Integer v, int solution_count_limit, int timeout_limit, int solutions_limit){
		before_traverse(G,v);
		
		//additional init
		m_config_solution_count_limit = solution_count_limit;
		m_config_timer_limit = timeout_limit;
		m_config_solutions_limit= solutions_limit;
		
		HashSet<Integer> Vx = new HashSet<Integer> ();
		Vx.add(v);

		// run process
		on_traverse(G, v, Vx, new DataHyperGraph());
		
		
		after_traverse();
		
	}

	protected void before_traverse(DataHyperGraph G, Integer v){
		//init
		m_runtime_solution_count =0;

		m_runtime_process_start = System.currentTimeMillis();
		m_runtime_timer_start = m_runtime_process_start;
		
		// set run token file
		try {
			m_szFileName_runtime = "run-"+m_runtime_timer_start;
			ToolIO.pipeStringToFile("remove file to stop",m_szFileName_runtime,false,false);
		} catch (Sw4jException e) {
			e.printStackTrace();
		}		
	}

	protected void after_traverse(){
		//record when finished
		m_runtime_process_end = System.currentTimeMillis();

		// remove run token file
		File f=null;
		if (null!= m_szFileName_runtime && (f=new File(m_szFileName_runtime)).exists()){
			f.delete();
		}

		System.gc();
		System.gc();
		System.gc();
		//System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
		
	}


	/**
	 * traverse concise subgraphs for a hypergraph starting from a given vertex v.
	 * 
	 * @param G		the given hypergraph
	 * @param v		the given starting vertex
	 * @param Vx	the vertices to be justified
	 * @param Gx	the current path, i.e. concise selection of hyperedge
	 * @return	if some solution was found
	 */
	protected boolean on_traverse(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		// check if g is not meeting other criteria
		if (canDiscard(G, Vx, Gx))
			return false;

		// check the hyperedge selection
		if (checkSolution(G, v, Vx, Gx)){
			return true;
		}

		// stop if there are no more vertices
		if (Vx.isEmpty())
			return false;

		//select one vertex 
		Integer vh = select_next_vertex(G,v,Vx,Gx);
		
		if (null==vh)
			return false;
		

		//list the edge (filter, reorder...)
		Iterator<DataHyperEdge> iter = reorder_next_edges(G,v,Vx,Gx, 
										filter_next_edges(G,v,Vx,Gx, 
												G.getEdgesByOutput(vh)) ).iterator();

		// try each edge,
		// in case iter is empty, that mean vh is not justified by any hyper edge
		boolean bRet = false;
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();

			if (isMustStop())
				return false;

			// prepare new to-visit vertex
			HashSet<Integer> new_vx = new HashSet<Integer> ();
			new_vx.addAll(Vx);
			new_vx.addAll(g.getInputs());
			new_vx.removeAll(Gx.getOutputs());
			new_vx.remove(g.getOutput());

			// no need to track provenance in intermediate result
			DataHyperGraph new_gx = new DataHyperGraph(Gx);
			new_gx.add(g, G.getContextsByEdge(g));
			
			bRet |= on_traverse(G, v, new_vx, new_gx);
		}
		
		return bRet;
	}
	
	/**
	 * check if we can discard a search branch. Normal traverse will say "no" but further optimization can say yes.
	 * 
	 * @param G
	 * @param Vx
	 * @param Gx
	 * @return
	 */
	protected boolean canDiscard(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		if (debug){
			log(String.format("edges %d, vertices %d, solutions %d. todo %s ", 
					Gx.getEdges().size(), 
					Gx.getVertices().size(),
					this.m_runtime_solution_count,
					Vx.toString()));
		}
		
		return false;
	}
	
	protected boolean isSolution(Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		boolean bRet = (Vx.isEmpty() && Gx.isComplete() && Gx.isSingleRoot(v) && Gx.isAcyclic());
		if (bRet){
			this.m_runtime_solution_count ++;
		}
		
		return bRet;
	}
	
	protected boolean checkSolution(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		if (isSolution(v,Vx,Gx)){	

			saveSolution(Gx);

			if (debug){
				System.out.println(Gx.data_summary());
			}
			return true;
		}else{
			return false;			
		}
	}
	
	protected boolean isMustStop(){
		//stop if two many solutions were found
		if (isAboveLimitSolutionCount())
			return true;

		//stop if no better answers can be found in 5 minutes
		m_runtime_timer_end = System.currentTimeMillis();
		if (isAboveLimitTimeout())
			return  true;

		//stop if the run token file is removed by user
		if (null!= m_szFileName_runtime && !new File(m_szFileName_runtime).exists())
			return  true;

		return false;	
	}
	

	protected Integer select_next_vertex(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx){
		// simply pick the edges associated with the first to-visit vertex.
		if (!Vx.isEmpty())
			return Vx.iterator().next();
		else
			return null;
	}
	
	protected Collection<DataHyperEdge> filter_next_edges(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		ArrayList<DataHyperEdge> new_next = new ArrayList<DataHyperEdge>();

		//generate a directed graph 
		DataDigraph adj = Gx.getDigraph();

		Iterator<DataHyperEdge> iter =edges.iterator();
		while (iter.hasNext()){
			DataHyperEdge g = iter.next();
			
			// skip if g is definitely causing incomplete linkedGraph
			if (!G.getOutputs().containsAll(g.getInputs())){
				continue;
			}

			// avoid cycle
			if (adj.isReachable(g.getInputs(), g.getOutput())){
				continue;
			}

			//if (m_runtime_preferred_vertex.containsAll(g.getSources()))
			//	new_next.add(0, g);
			//else
			new_next.add(g);
		}		
		
		return new_next;
	}
	protected Collection<DataHyperEdge> reorder_next_edges(DataHyperGraph G, Integer v, Set<Integer> Vx, DataHyperGraph Gx, Collection<DataHyperEdge> edges){
		return edges;
	}	


	public String getSummary(DataHyperGraph G) {
		return String.format("found %d solutions in %.3f seconds", this.m_runtime_solution_count, this.getProcessSeconds());
	}
	
	
	protected void log(String szText){
		System.out.println("[T:"+(System.currentTimeMillis()-this.m_runtime_process_start)/1000+"] "+szText);
	}
	
}

