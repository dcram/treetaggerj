package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.*;
import fr.dcram.treetaggerj.util.FeatureSet;

import java.util.ArrayList;
import java.util.List;

/**
 * WARNING: not thread-safe
 *
 * @author Damien Cram
 */
public class Tagger {

	private DTree decisionTree;
	private Lexicon lexicon;
	private TagSet tagSet;
	private FeatureSet featureSet;
	private TreeTaggerModel model;
	Tag beforeLast, last;


	public Tagger(TreeTaggerModel model) {
		this.decisionTree = model.getDecisionTree();
		this.lexicon = model.getLexicon();
		this.tagSet = model.getTagSet();
		this.featureSet = model.getFeatureSet();
		this.beforeLast = tagSet.getStartTag();
		this.last = tagSet.getStartTag();
		this.model = model;
	}

	private List<Feature> toFeatureList(Tag beforeLast, Tag last) {
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

	public Tag tag(String token) {
		Tag tag = tagToken(beforeLast, last, token);
		beforeLast = last;
		last = tag;
		return tag;
	}


	private Tag tagToken(Tag beforeLast, Tag last, String tok) {
		TaggingProbaTable aprioriTable = (TaggingProbaTable)lexicon.getTable(tok);
		TaggingProbaTable lastNTable = (TaggingProbaTable)decisionTree.getTable(toFeatureList(beforeLast, last));
		return getMostProbable(aprioriTable, lastNTable);
	}

}
