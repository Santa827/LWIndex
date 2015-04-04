package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;

/**
 * Extension of the simple index interface
 * 
 * @author dayuyuan
 * 
 */
public interface IIndex2 extends IIndex1 {
	/**
	 * Given a query graph 'query', return the minimal supergraph of this query
	 * 
	 * @param query
	 * @return
	 */
	public List<Integer> minimalSupergraphs(Graph query, SearchStatus status,
			List<Integer> maxSubs);

	public List<Integer> maxSubgraphs(FastSUCompleteEmbedding fastSu,
			SearchStatus status);

	/**
	 * Return the maximum subgraph terms for Lindex
	 * 
	 * @param query
	 * @param maxSubs
	 * @param maximumSubgraph
	 *            : return
	 * @param TimeComponent
	 * @return: fastSu Mapping
	 */
	public FastSUCompleteEmbedding designedSubgraph(Graph query,
			List<Integer> maxSubs, int[] maximumSubgraph, SearchStatus status);

	public int designedSubgraph(List<Integer> maxSubs, SearchStatus status);
}
