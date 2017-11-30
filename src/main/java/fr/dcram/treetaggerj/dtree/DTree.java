package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.Iterator;
import java.util.List;

public class DTree {

	private DTreeNode rootNode;

	public DTree(DTreeNode root) {
		this.rootNode = root;
	}

	public DTreeNode getRootNode() {
		return rootNode;
	}

	public ProbaTable getTable(List<Feature> features) {
		return rootNode.getTable(features);
	}

	@Override
	public String toString() {
		return rootNode.toString(0);
	}

	public int getNbNodes() {
		return rootNode.getNbNodes();
	}

	public int getLeaves() {
		return rootNode.getNbLeaves();
	}

	public int getFNodes() {
		return rootNode.getNbFNodes();
	}


	public int getDepth() {
		return rootNode.getDepth();
	}

	public Iterator<DTreeNode> nodeIterator() {
		return new NodeIterator(rootNode);
	}
}
