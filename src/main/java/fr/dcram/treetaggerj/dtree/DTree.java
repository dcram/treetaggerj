package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

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
}
