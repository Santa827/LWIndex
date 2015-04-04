package edu.psu.chemxseer.structure.setcover.IO.features;

import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.util.OrderedIntSets;

/**
 * A wrapper of PostingFeaturesMultiClass Different from CoverSet_FeatureWrapper
 * since the postings are stored on-disk, instead of in-memory
 * 
 * @author dayuyuan
 * 
 */
public class FeatureWrapperOnDiskImpl implements FeatureWrapperInterface,
		Comparable<FeatureWrapperOnDiskImpl> {
	private int fID;
	private PostingFeaturesMultiClass postings;
	protected int gain; // Number of Items the Cover-Set contains

	public FeatureWrapperOnDiskImpl(PostingFeaturesMultiClass features, int ID,
			int gain) {
		this.postings = features;
		this.fID = ID;
		this.gain = gain;
	}

	public int[] containedDatabaseGraphs() {
		return this.postings.getPosting(fID)[0];
	}

	public int[] containedQueryGraphs() {
		return this.postings.getPosting(fID)[2];
	}

	public int[] notContainedDatabaseGraphs(int totalDBGraphs) {
		int[] result = OrderedIntSets.getCompleteSet(
				this.postings.getPosting(fID)[0], totalDBGraphs);
		return result;
	}

	public int[] notContainedQueryGraphs(int totalQueryGraphs) {
		int[] result = OrderedIntSets.getCompleteSet(
				this.postings.getPosting(fID)[2], totalQueryGraphs);
		return result;
	}

	public int[] getEquavalentDatabaseGraphs() {
		return this.postings.getPosting(fID)[1];
	}

	public int[] getEquavalentQueryGraphs() {
		return this.postings.getPosting(fID)[3];
	}

	public OneFeatureMultiClass getFeature() {
		return this.postings.getMultiFeatures().getFeature(fID);
	}

	public int getFetureID() {
		return this.fID;
	}

	public int[][] getPosting() {
		return this.postings.getPosting(fID);
	}

	public int compareTo(FeatureWrapperOnDiskImpl other) {
		if (this.gain < other.gain)
			return -1;
		else if (this.gain == other.gain)
			return 0;
		else
			return 1;
	}

	public double getGain() {
		return gain;
	}
}

// class SetComparator implements Comparator<CoverSet_FeatureWrapper2>{
// private IFeatureSetConverter converter;
// private int maxGain = 0;
//
// public SetComparator (IFeatureSetConverter converter){
// this.converter = converter;
// }
// public int getMaxGain(){
// return this.maxGain;
// }
//
// @Override
// public int compare(CoverSet_FeatureWrapper2 arg0,
// CoverSet_FeatureWrapper2 arg1) {
// if(arg0.gain == -1)
// arg0.gain = converter.featureToSet_Array(arg0).getItemCount();
// if(arg1.gain == -1)
// arg1.gain = converter.featureToSet_Array(arg1).getItemCount();
// if(arg0.gain < arg1.gain){
// if(arg1.gain > this.maxGain)
// this.maxGain = arg1.gain;
// return -1;
// }
// else if(arg0.gain == arg1.gain)
// return 0;
// else{
// if(arg0.gain > this.maxGain)
// this.maxGain = arg0.gain;
// return 1;
// }
// }
//
// }
