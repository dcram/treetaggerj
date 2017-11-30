package fr.dcram.treetaggerj.model;

import gnu.trove.map.hash.TIntIntHashMap;

public class TaggingProbaTable implements ProbaTable {

	TagSet tagSet;
	int totalFrequency;
	TIntIntHashMap map;

	public TaggingProbaTable(TagSet tagSet, int totalFrequency, TIntIntHashMap map) {
		this.map = map;
		this.tagSet = tagSet;
		this.totalFrequency = totalFrequency;
	}

	@Override
	public double getProba(Tag tag) {
		return map.containsKey(tag.getId()) ? (double)map.get(tag.getId())/totalFrequency : 0d;
	}

	public double getProba(int tagId) {
		return map.containsKey(tagId) ? (double)map.get(tagId)/totalFrequency : 0d;
	}

		@Override
	public int getTotalFrequency() {
		return totalFrequency;
	}

	public int[] getTagIds() {
		return map.keys();
	}
}
