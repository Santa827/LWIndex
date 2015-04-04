package edu.psu.chemxseer.structure.subsearch.Interfaces;

/**
 * TimeComponent[0] = posting fetching 
 * TimeComponent[1] = DB graph loading time
 * TimeComponent[2] = filtering cost (index lookup: maximum subgraph search &
 * minimal supergraph search) 
 * TimeComponent[3] = verification cost (subgraph
 * isomorphism test in verification step) 
 * Number[0] = verified graphs number
 * Number[1] = True answer size
 * 
 * @author dayuyuan
 * 
 */
public class SearchStatus {
	private long postFetchingTime;
	private long dbLoadingTime;
	private long filteringTime;
	private long verifyTime;
	private int verifiedCount;
	private int trueAnswerCount;

	public void refresh() {
		postFetchingTime = 0;
		dbLoadingTime = 0;
		filteringTime = 0;
		verifyTime = 0;
		verifiedCount = 0;
		trueAnswerCount = 0;
	}

	public String toString() {
		String str = postFetchingTime + "\t" + dbLoadingTime + "\t"
				+ filteringTime + "\t" + verifyTime + "\t" + verifiedCount
				+ "\t" + trueAnswerCount + "\t";
		return str;
	}

	/**
	 * @return the postFetchingTime
	 */
	public long getPostFetchingTime() {
		return postFetchingTime;
	}

	/**
	 * @return the dbLoadingTime
	 */
	public long getDbLoadingTime() {
		return dbLoadingTime;
	}

	/**
	 * @return the filteringTime
	 */
	public long getFilteringTime() {
		return filteringTime;
	}

	/**
	 * @return the verifyTime
	 */
	public long getVerifyTime() {
		return verifyTime;
	}

	/**
	 * @return the verifiedCount
	 */
	public int getVerifiedCount() {
		return verifiedCount;
	}

	/**
	 * @return the trueAnswerCount
	 */
	public int getTrueAnswerCount() {
		return trueAnswerCount;
	}

	/**
	 * @param postFetchingTime
	 *            the postFetchingTime to set
	 */
	public void addPostFetchingTime(long delta) {
		this.postFetchingTime += delta;
	}

	/**
	 * @param dbLoadingTime
	 *            the dbLoadingTime to set
	 */
	public void addDbLoadingTime(long delta) {
		this.dbLoadingTime += delta;
	}

	/**
	 * @param filteringTime
	 *            the filteringTime to set
	 */
	public void addFilteringTime(long delta) {
		this.filteringTime += delta;
	}

	/**
	 * @param verifyTime
	 *            the verifyTime to set
	 */
	public void addVerifyTime(long delta) {
		this.verifyTime += delta;
	}

	/**
	 * @param verifiedCount
	 *            the verifiedCount to set
	 */
	public void addVerifiedCount(int delta) {
		this.verifiedCount += delta;
	}

	/**
	 * @param trueAnswerCount
	 *            the trueAnswerCount to set
	 */
	public void addTrueAnswerCount(int delta) {
		this.trueAnswerCount += delta;
	}

	public void setVerifiedCount(int verifiedCount) {
		this.verifiedCount = verifiedCount;
	}

	public void setTrueAnswerCount(int trueAnswerCount) {
		this.trueAnswerCount = trueAnswerCount;
	}

	public void setVerifyTime(int verifyTime) {
		this.verifyTime = verifyTime;
	}

}
