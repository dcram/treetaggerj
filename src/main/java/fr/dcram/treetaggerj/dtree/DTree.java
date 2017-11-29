package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.List;

public class DTree {

	private DTreeNode rootNode;

	public DTree(DTreeNode root) {
		this.rootNode = root;
	}

	public ProbaTable getTable(List<Feature> features) {
		return rootNode.getTable(features);
	}
}
