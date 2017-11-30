package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.Collections;
import java.util.List;

public abstract class DTreeNode {

	private DTreeNode parent;

	public DTreeNode(DTreeNode parent) {
		this.parent = parent;
	}

	public abstract ProbaTable getTable(List<Feature> features);

	public abstract String toString(int depth);

	protected String tab(int depth) {
		return String.join("", Collections.nCopies(depth, "\t"));
	}

	public abstract int getNbNodes();
	public abstract int getNbFNodes();
	public abstract int getNbLeaves();
	public abstract int getDepth();

	public DTreeNode getParent() {
		return parent;
	}

	public void setParent(FeatureDTreeNode parent) {
		this.parent = parent;
	}

}
