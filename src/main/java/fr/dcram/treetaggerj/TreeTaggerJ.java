package fr.dcram.treetaggerj;

public class TreeTaggerJ {
	public static Tagger getTagger(TreeTaggerModel model) {
		return new Tagger(model);
	}
}
