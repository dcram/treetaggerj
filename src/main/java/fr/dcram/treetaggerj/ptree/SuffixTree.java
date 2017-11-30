package fr.dcram.treetaggerj.ptree;

import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.List;

public class SuffixTree {

	private PrefixTreeNode<ProbaTable> root;

	public SuffixTree(PrefixTreeNode<ProbaTable> root) {
		this.root = root;
	}

	public ProbaTable get(String string) {
		return root.get(new StringBuilder(string).reverse().toString());
	}


	public void add(String suffix, ProbaTable table) {
		root.add(new StringBuilder(suffix).reverse().toString(), table);
	}

	public int getNbNodes() {
		return root.getNbNodes();
	}

	public int getNbLeaves() {
		return root.getNbLeaves();
	}

	public int getDepth() {
		return root.getDepth();
	}

	public List<PrefixTreeNode<ProbaTable>> getAllNodes() {
		return root.getNodes();
	}

	@Override
	public String toString() {
		return String.format("SuffixTree[nodes: %d, leaves: %d]", root.getNbNodes(), root.getNbLeaves());
	}
}
