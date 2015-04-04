package edu.psu.chemxseer.structure.postings.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcherPrefix;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResultPref;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IIndexPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class GraphFetcherLucenePrefix implements IGraphFetcherPrefix {
	private IIndexPrefix prefixIndex;
	private GraphFetcherLucene lucene;

	/**
	 * Current support the DFS graph parser only
	 * 
	 * @param lucene
	 * @param prefixIndex
	 *            : the Index Storing the Prefix Features of the Database Graphs
	 */
	public GraphFetcherLucenePrefix(GraphFetcherLucene lucene,
			IIndexPrefix prefixIndex) {
		this.lucene = lucene;
		this.prefixIndex = prefixIndex;
	}

	public GraphFetcherLucenePrefix(IndexSearcher luceneSearcher, TopDocs hits,
			GraphParser gParser, IIndexPrefix prefixIndex) {
		this.lucene = new GraphFetcherLucene(luceneSearcher, hits, gParser);
		this.prefixIndex = prefixIndex;
	}

	/**
	 * The difference is that GraphFetcherLucenePrefix return the GraphResultPrefix
	 */
	public List<IGraphResultPref> getGraphs(SearchStatus status) {
		if (lucene.start == lucene.scoreDocs.length)
			return null; // no graphs need to return
		else {
			long startTime = System.currentTimeMillis();
			int end = Math.min(lucene.start + batchCount,
					lucene.scoreDocs.length);
			List<IGraphResultPref> results = new ArrayList<IGraphResultPref>();
			for (int i = lucene.start; i < end; i++) {
				int docID = lucene.scoreDocs[i].doc;
				Document graphDoc = null;
				try {
					graphDoc = lucene.searcher.doc(docID);
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (graphDoc != null)
					results.add(new GraphResultLucenePrefix(graphDoc, docID,
							this.prefixIndex));
			}
			lucene.start = end;
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
		return lucene.getOrderedIDs();
	}

	public IGraphFetcherPrefix join(IGraphFetcher fetcher) {
		lucene.join(fetcher);
		return this;
	}

	public IGraphFetcherPrefix remove(IGraphFetcher fetcher) {
		lucene.remove(fetcher);
		return this;
	}

	public IGraphFetcherPrefix remove(int[] orderedSet) {
		lucene.remove(orderedSet);
		return this;
	}

	public int size() {
		return lucene.size();
	}
}
