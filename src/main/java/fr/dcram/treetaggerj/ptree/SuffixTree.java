package fr.dcram.treetaggerj.ptree;

import fr.dcram.treetaggerj.model.ProbaTable;

public class SuffixTree {

	private PrefixTreeNode<ProbaTable> root = new PrefixTreeNode<>();

	public ProbaTable get(String string) {
		return root.get(new StringBuilder(string).reverse().toString());
	}


	public void add(String suffix, ProbaTable table) {
		root.add(new StringBuilder(suffix).reverse().toString(), table);
	}
}
