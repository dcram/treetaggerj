package fr.dcram.treetaggerj.dtree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"depth", "nbLeaves", "nbNodes", "fNodes"} )
public abstract class DTreeNode {

	@JsonIgnore
	private DTreeNode parent;

	public DTreeNode(DTreeNode parent) {
		this.parent = parent;
	}

	public abstract ProbaTable getTable(List<Feature> features);

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

	public void setParent(DTreeNode parent) {
		this.parent = parent;
	}
}
