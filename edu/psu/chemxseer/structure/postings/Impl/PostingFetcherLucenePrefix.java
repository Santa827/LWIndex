package edu.psu.chemxseer.structure.postings.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcherPrefix;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IIndex0;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IIndexPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

// same as posting fetcher: 
public class PostingFetcherLucenePrefix implements IPostingFetcherPrefix {
	private IndexSearcher luceneSearcher;
	private int dbSize;
	private GraphParser gParser;
	private IIndexPrefix prefixIndex;

	/**
	 * 
	 * @param lucenePath
	 * @param dbSize
	 * @param gParser
	 *            : parse the graphs
	 */
	public PostingFetcherLucenePrefix(String lucenePath, int dbSize,
			GraphParser gParser, boolean inMemory, IIndexPrefix prefixIndex) {
		Directory luceneDic = null;
		try {
			luceneDic = new NIOFSDirectory(new File(lucenePath));
			if (inMemory)
				luceneDic = new RAMDirectory(luceneDic);
		} catch (IOException e1) {
			System.out.println("No Lucene Index Exists in such Address");
			e1.printStackTrace();
			return;
		}

		try {
			this.luceneSearcher = new IndexSearcher(IndexReader.open(luceneDic,
					true));
			luceneDic.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.dbSize = dbSize;
		this.gParser = gParser;
		this.prefixIndex = prefixIndex;
	}

	@Override
	public void finalize() {
		// Try to close the lucene searcher
		if (this.luceneSearcher != null)
			try {
				this.luceneSearcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public IGraphFetcherPrefix getPosting(int featureID, SearchStatus status) {
		String byteString = new Integer(featureID).toString();
		Term queryTerm = new Term("subGraphs", byteString);
		TermQuery termQ = new TermQuery(queryTerm);
		return searchIndex(termQ, status);
	}

	public int[] getPostingID(int featureID) {
		IGraphFetcherPrefix fetcher = this.getPosting(featureID, new SearchStatus());
		return fetcher.getOrderedIDs();
	}

	public IGraphFetcherPrefix getPosting(String featureString,
			SearchStatus status) {
		Term queryTerm = new Term("subGraphs", featureString);
		TermQuery termQ = new TermQuery(queryTerm);
		return searchIndex(termQ, status);
	}

	public IGraphFetcherPrefix getJoin(List<Integer> featureIDs,
			SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		if (BooleanQuery.getMaxClauseCount() < featureIDs.size())
			BooleanQuery.setMaxClauseCount(featureIDs.size());

		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.MUST);
		}
		return this.searchIndex(bQuery, status);
	}

	public IGraphFetcherPrefix getJoin(String[] featureStrings,
			SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		if (BooleanQuery.getMaxClauseCount() < featureStrings.length)
			BooleanQuery.setMaxClauseCount(featureStrings.length);
		for (int i = 0; i < featureStrings.length; i++) {
			Term queryTerm = new Term("subGraphs", featureStrings[i]);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.MUST);
		}
		return this.searchIndex(bQuery, status);
	}

	// TimeComponent[0] for posting Fetching
	public IGraphFetcherPrefix getUnion(List<Integer> featureIDs,
			SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		if (BooleanQuery.getMaxClauseCount() < featureIDs.size())
			BooleanQuery.setMaxClauseCount(featureIDs.size());
		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.SHOULD);
		}
		return this.searchIndex(bQuery, status);
	}

	public IGraphFetcherPrefix getComplement(List<Integer> featureIDs,
			SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		if (BooleanQuery.getMaxClauseCount() <= featureIDs.size())
			BooleanQuery.setMaxClauseCount(featureIDs.size() + 1);

		// 1. After simple modification, all the lucene indexes "-1", a dummy
		// term
		// so that I can use Occur.Mutst_NOT
		Term dummyTerm = new Term("subGraphs", (new Integer(-1)).toString());
		bQuery.add(new TermQuery(dummyTerm), Occur.MUST);

		// 2. Get the complementary indexes
		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.MUST_NOT);
		}
		return this.searchIndex(bQuery, status);
	}

	// /**
	// * TimeComponent[1] = get graphs
	// * @param hits
	// * @param TimeComponent
	// * @return
	// * @throws CorruptIndexException
	// * @throws IOException
	// */
	// protected List<GraphResult> getGraphDocs(TopDocs hits, long[]
	// TimeComponent) throws CorruptIndexException, IOException{
	//
	// long start = System.currentTimeMillis();
	// List<GraphResult> results = new ArrayList<GraphResult>(hits.totalHits);
	// ScoreDoc[] scoreDocs = hits.scoreDocs;
	// for(int i = 0; i< scoreDocs.length; i++){
	// int docID = scoreDocs[i].doc;
	// Document graphDoc = this.luceneSearcher.doc(docID);
	// results.add(new GraphResultLucene(graphDoc));
	// }
	// TimeComponent[1] += System.currentTimeMillis() - start;
	// return results;
	// }

	// TimeComponent[0] for posting fetching
	protected IGraphFetcherPrefix searchIndex(Query query, SearchStatus status) {
		long startTime = System.currentTimeMillis();
		TopDocs hits;
		try {
			hits = this.luceneSearcher.search(query, this.dbSize);
			/*
			 * if(hits.totalHits==0) System.out.println(
			 * "Empty Search Result in PostingFetcherLucene::searchIndex");
			 */
			IGraphFetcherPrefix fetcher = new GraphFetcherLucenePrefix(
					this.luceneSearcher, hits, gParser, prefixIndex);
			status.addPostFetchingTime(System.currentTimeMillis() - startTime);
			return fetcher;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public PostingBuilderMem loadPostingIntoMemory(IIndex0 indexSearcher) {
		int[] termIDs = indexSearcher.getAllFeatureIDs();
		PostingBuilderMem mem = new PostingBuilderMem();
		SearchStatus status = new SearchStatus();
		for (int id : termIDs) {
			mem.insertOnePosting(id, this.getPosting(id, status)
					.getOrderedIDs());
		}
		return mem;
	}

	public int getDBSize() {
		return dbSize;
	}
}
