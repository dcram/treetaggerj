package fr.dcram.treetaggerj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.Lexicon;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.util.FeatureSet;

public class TreeTaggerModel {
	private Lexicon lexicon;

	@JsonProperty("decision-tree")
	private DTree decisionTree;

	@JsonIgnore
	private TagSet tagSet;

	@JsonIgnore
	private FeatureSet featureSet;

	public TreeTaggerModel() {

	}

	public void setTagSet(TagSet tagSet) {
		this.tagSet = tagSet;
	}

	public TreeTaggerModel(Lexicon lexicon, DTree decisionTree) {
		this.lexicon = lexicon;
		this.decisionTree = decisionTree;
	}

	public Lexicon getLexicon() {
		return lexicon;
	}

	public DTree getDecisionTree() {
		return decisionTree;
	}

	@Override
	public String toString() {
		return String.format("TreeTaggerModel[lexicon:%s,dtree:%s]", lexicon, decisionTree);
	}

	public TagSet getTagSet() {
		return tagSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	public FeatureSet getFeatureSet() {
		return featureSet;
	}
}
