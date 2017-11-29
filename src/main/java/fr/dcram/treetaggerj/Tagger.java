package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.*;

import java.util.ArrayList;
import java.util.List;

public class Tagger {

	private DTree decisionTree;
	private Lexicon lexicon;
	private TagSet tagSet;


	private Tag beforeLast, last = tagSet.getNotAWord();


	public Tag tagToken(Token token) {
		Tag tag;
		ProbaTable aprioriTable = lexicon.getTable(token.getText());
		ProbaTable lastNTable = decisionTree.getTable(toFeatureList());
		tag = getMostProbable(aprioriTable, lastNTable);
		beforeLast = last;
		last = tag;
		return tag;
	}

	private List<Feature> toFeatureList() {
		List<Feature> features = new ArrayList<>(2);
		features.add(new Feature(beforeLast, 2));
		features.add(new Feature(last, 1));
		return features;
	}

	private Tag getMostProbable(ProbaTable aprioriTable, ProbaTable lastNTable) {
		Tag tag = tagSet.getDefaultTag();
		double max = 0d;
		for(Tag current:aprioriTable.getTags()) {
			double p = lastNTable.getProba(current) * aprioriTable.getProba(current);
			if(p>max) {
				max = p;
				tag = current;
			}
		}
		return tag;
	}
}
