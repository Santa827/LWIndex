package edu.psu.chemxseer.structure.postings.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcherPrefix;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResultPref;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IIndexPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class GraphFetcherIndexPrefix implements IGraphFetcherPrefix {
	private IIndexPrefix gDB;
	private GraphFetcherDB gFetcher;

	/**
	 * Constructor the GraphFetcherDBPrefix.
	 * 
	 * @param gFetcher
	 * @param gDB
	 *            : the database containing all the features
	 */
	public GraphFetcherIndexPrefix(GraphFetcherDB gFetcher, IIndexPrefix gDB) {
		this.gFetcher = gFetcher;
		this.gDB = gDB;
	}

	/**
	 * The Only Different is that here the getGraphs return the
	 * "GraphREsultNormalPrefix" which implements the GraphResultPrefix
	 * interface
	 */
	public List<IGraphResultPref> getGraphs(SearchStatus status) {
		if (gFetcher.start == gFetcher.orderedGIDs.length)
			return null;
		else {
			long startTime = System.currentTimeMillis();
			int end = Math.min(gFetcher.start + batchCount,
					gFetcher.orderedGIDs.length);
			List<IGraphResultPref> results = new ArrayList<IGraphResultPref>();
			for (int i = gFetcher.start; i < end; i++) {
				int prefixID = this.gDB.getPrefixID(gFetcher.orderedGIDs[i]);
				IGraphResultPref temp = new GraphResultNormalPrefix(
						gFetcher.orderedGIDs[i], prefixID,
						gDB.getExtension(gFetcher.orderedGIDs[i]), gDB);
				results.add(temp);
			}
			gFetcher.start = end;
			status.addDbLoadingTime(System.currentTimeMillis() - startTime);
			return results;
		}
	}

	public List<IGraphResultPref> getAllGraphs(SearchStatus status) {
		List<IGraphResultPref> answer = new ArrayList<IGraphResultPref>();
		List<IGraphResultPref> temp = this.getGraphs(status);
		while (temp != null) {
			answer.addAll(temp);
			temp = this.getGraphs(status);
		}
		Collections.sort(answer);
		return answer;
	}
	
	public int[] getOrderedIDs() {
		return gFetcher.getOrderedIDs();
	}

	public IGraphFetcherPrefix join(IGraphFetcher fetcher) {
		this.gFetcher.join(fetcher);
		return this;
	}

	public IGraphFetcherPrefix remove(IGraphFetcher fetcher) {
		gFetcher.remove(fetcher);
		return this;
	}

	public IGraphFetcherPrefix remove(int[] orderedSet) {
		gFetcher.remove(orderedSet);
		return this;
	}

	public int size() {
		return gFetcher.size();
	}

}
