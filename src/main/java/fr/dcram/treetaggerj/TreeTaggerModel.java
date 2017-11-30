package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.Lexicon;

public class TreeTaggerModel {
	private Lexicon lexicon;
	private DTree decisionTree;

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
}
