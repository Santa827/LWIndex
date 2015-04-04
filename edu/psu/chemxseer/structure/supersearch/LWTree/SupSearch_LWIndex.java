package edu.psu.chemxseer.structure.supersearch.LWTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Impl.GraphFetcherLucene;
import edu.psu.chemxseer.structure.postings.Impl.GraphFetcherLucenePrefix;
import edu.psu.chemxseer.structure.postings.Impl.PostingFetcherLucene;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISOPrefix;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class SupSearch_LWIndex implements ISearcher {

	private LWIndexSearcher lwIndex;
	private VerifierISOPrefix verifier;
	private PostingFetcherLucene postingFetcher;

	public SupSearch_LWIndex(LWIndexSearcher indexSearcher,
			PostingFetcherLucene postingFetcher, VerifierISOPrefix verifier) {
		this.lwIndex = indexSearcher;
		this.verifier = verifier;
		this.postingFetcher = postingFetcher;
	}

	public int[][] getAnswerIDs(Graph query) {
		List<IGraphResult> answer = this.getAnswer(query, new SearchStatus());
		int[] result = new int[answer.size()];
		List<Integer> result2 = new ArrayList<Integer>();
		int counter1 = 0;
		for (IGraphResult oneAnswer : answer) {
			if (oneAnswer.getG().getEdgeCount() == query.getEdgeCount())
				result2.add(oneAnswer.getID());
			else
				result[counter1++] = oneAnswer.getID();
		}
		int[][] finalResult = new int[2][];
		finalResult[0] = Arrays.copyOf(result, counter1);
		finalResult[1] = new int[result2.size()];
		for (int w = 0; w < result2.size(); w++)
			finalResult[1][w] = result2.get(w);
		return finalResult;
	}

	
	/**
	 * TimeComponent[0] = posting fetching 
	 * TimeComponent[1] = DB graph loading time 
	 * TimeComponent[2] = filtering cost
	 * TimeComponent[3] = verification cost
	 * Number[0] = verified graphs number 
	 * Number[1] = True answer size
	 */
	public List<IGraphResult> getAnswer(Graph query, SearchStatus status) {
		// 1. First step is to find all features that are contained in the query
		// graph
		List<Integer> allSubgraphs = lwIndex.subgraphs(query, status);
		// 2. Get all the candidate graphs: just a summation operation
		GraphFetcherLucene temp = (GraphFetcherLucene) postingFetcher.getUnion(
				allSubgraphs, status);
		GraphFetcherLucenePrefix candidateFetcher = new GraphFetcherLucenePrefix(
				temp, lwIndex);
		status.setVerifiedCount(candidateFetcher.size());
		// 3. Verification phase
		List<IGraphResult> answer = this.verifier.verify(candidateFetcher,
				query, status);
		status.setTrueAnswerCount(answer.size());
		return answer;
	}

	public static String getIndexName() {
		return "index";
	}

	public int getFeatureCount() {
		return this.lwIndex.getFeatureCount();
	}
}
