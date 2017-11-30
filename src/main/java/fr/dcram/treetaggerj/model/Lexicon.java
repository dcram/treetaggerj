package fr.dcram.treetaggerj.model;

import fr.dcram.treetaggerj.ptree.SuffixTree;

import java.util.Map;

public class Lexicon {

	private Map<String, ProbaTable> lexicon;
	private SuffixTree suffixTree;

	public Lexicon(Map<String, ProbaTable> fullformLexicon, SuffixTree suffixTree) {
		this.lexicon = fullformLexicon;
		this.suffixTree = suffixTree;
	}

	public ProbaTable getTable(String text) {
		if(lexicon.containsKey(text))
			return lexicon.get(text);
		else if(lexicon.containsKey(text.toLowerCase()))
			return lexicon.get(text.toLowerCase());
		else
			return suffixTree.get(text.toLowerCase());
	}

	@Override
	public String toString() {
		return String.format("Lexicon[fullforms:%s, suffixTree:%s]", lexicon.size(),suffixTree );
	}
}
