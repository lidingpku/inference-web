package org.inference_web.iwapp.hypergraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sw4j.task.graph.AgentHyperGraphOptimize;
import sw4j.task.graph.DataHyperEdge;
import sw4j.task.graph.DataHyperGraph;

/**
 * optimize number of CNF nodes/axioms (minimize) 
 * 
 * @author Li Ding
 *
 */
public class AgentHyperGraphOptimizeCnf extends AgentHyperGraphOptimize {
	
	boolean m_bOptimize = true;
	HashSet<Integer> m_nodes_cnf = new HashSet<Integer>();
	HashSet<Integer> m_nodes_fof = new HashSet<Integer>();
	
	int m_option = OPTION_AXIOM_NODE;
	
	public static final int OPTION_AXIOM_ONLY =1;
	public static final int OPTION_NODE_ONLY = 2;
	public static final int OPTION_AXIOM_NODE = 3;
	public static final int OPTION_NODE_AXIOM = 4;
	public AgentHyperGraphOptimizeCnf(HashSet<Integer> nodes_cnf, HashSet<Integer> nodes_fof, int option){
		m_nodes_cnf = nodes_cnf;
		m_nodes_fof = nodes_fof;
		m_option = option;
	}
	
	@Override
	public int getQuality(DataHyperGraph G){
		return getQuality( G, m_option,m_nodes_cnf, m_nodes_fof);
	}
	
	public static int getQuality(DataHyperGraph G, int option,  HashSet<Integer> cnf, HashSet<Integer> fof){
		HashSet<Integer> axioms =null;
		axioms =G.getAxioms();
		axioms.retainAll(cnf);

		HashSet<Integer> nodes =null;
		nodes= G.getVertices();
		nodes.retainAll(cnf);
		
		return getQuality( option, axioms.size(), nodes.size());
	}
	private static int getQuality(int option, int axiom_only, int node_only){
		switch (option){
			case OPTION_AXIOM_ONLY:{
				return axiom_only;
			}
			case OPTION_NODE_ONLY:{
				return node_only;
			}
			case OPTION_NODE_AXIOM:{
				return node_only*10000 +axiom_only;
			}
			case OPTION_AXIOM_NODE:
			default:{
				return axiom_only*10000 +node_only;
			}
		}
	}
	
	@Override
	public int predictTotalQuality(DataHyperGraph G, Set<Integer> Vx, DataHyperGraph Gx){
		int predicted_quality = getQuality(Gx);
		
		Iterator<Integer> iter = Vx.iterator();
		while (iter.hasNext()){
			Integer sink = iter.next();
			
			int min_quality =-1;
			Iterator<DataHyperEdge> iter_edge = G.getEdgesByOutput(sink).iterator();
			while (iter_edge.hasNext()){
				DataHyperEdge edge = iter_edge.next();
				
				int cnt_axiom = (this.m_nodes_cnf.contains(edge.getOutput()) && edge.isAtomic() )? 1:0;
				HashSet<Integer> sources = new  HashSet<Integer>(edge.getInputs());
				sources.removeAll(Gx.getVertices()); 	// new
				sources.retainAll(this.m_nodes_cnf);	// is cnf
				int cnt_node = sources.size();
				
				int quality = getQuality(m_option, cnt_axiom, cnt_node);
				if (-1 == min_quality){
					min_quality = quality;
				}
				
				min_quality = Math.min(min_quality, quality);
			}
			
			if (-1 == min_quality){
				// if the sink does not have any hyperedge associated with it
				// there is no need to check any more
				return Integer.MAX_VALUE;
			}else{
				predicted_quality += min_quality;
			}
		}
		
		return  predicted_quality;
	}
	
}
