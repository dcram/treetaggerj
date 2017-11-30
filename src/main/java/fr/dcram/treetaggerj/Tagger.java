package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.*;
import fr.dcram.treetaggerj.util.FeatureSet;

import java.util.ArrayList;
import java.util.List;

public class Tagger {

	private DTree decisionTree;
	private Lexicon lexicon;
	private TagSet tagSet;
	private FeatureSet featureSet;


	private Tag beforeLast, last = tagSet.getStartTag();


	public Tag tagToken(Token token) {
		Tag tag;
		TaggingProbaTable aprioriTable = (TaggingProbaTable)lexicon.getTable(token.getText());
		TaggingProbaTable lastNTable = (TaggingProbaTable)decisionTree.getTable(toFeatureList());
		tag = getMostProbable(aprioriTable, lastNTable);
		beforeLast = last;
		last = tag;
		return tag;
	}

	private List<Feature> toFeatureList() {
		List<Feature> features = new ArrayList<>(2);
		features.add(featureSet.getFeature(beforeLast.getLabel(), 2));
		features.add(featureSet.getFeature(last.getLabel(), 1));
		return features;
	}

	private Tag getMostProbable(TaggingProbaTable aprioriTable, TaggingProbaTable lastNTable) {
		Tag tag = tagSet.getDefaultTag();
		double max = 0d;
		for(int tagId:aprioriTable.getTagIds()) {
			double p = lastNTable.getProba(tagId) * aprioriTable.getProba(tagId);
			if(p>max) {
				max = p;
				tag = tagSet.getTag(tagId);
			}
		}
		return tag;
	}
}
