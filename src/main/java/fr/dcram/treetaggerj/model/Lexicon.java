package fr.dcram.treetaggerj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dcram.treetaggerj.ptree.SuffixTree;

import java.util.Map;

public class Lexicon {

	@JsonProperty("fullforms")
	private Map<String, ProbaTable> fullforms;

	@JsonProperty("suffix-tree")
	private SuffixTree suffixTree;

	public Lexicon() {

	}

	public Lexicon(Map<String, ProbaTable> fullformLexicon, SuffixTree suffixTree) {
		this.fullforms = fullformLexicon;
		this.suffixTree = suffixTree;
	}

	public ProbaTable getTable(String text) {
		if(fullforms.containsKey(text))
			return fullforms.get(text);
		else {

			String o = text.toLowerCase();
			if(fullforms.containsKey(o))
				return fullforms.get(o);
			else
				return suffixTree.get(o);
		}
	}

	@Override
	public String toString() {
		return String.format("Lexicon[fullforms:%s, suffixTree:%s]", fullforms.size(),suffixTree );
	}

	public Map<String, ProbaTable> getFullforms() {
		return fullforms;
	}

	public SuffixTree getSuffixTree() {
		return suffixTree;
	}
}
