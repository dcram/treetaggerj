package fr.dcram.treetaggerj.dtree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties({"depth", "leaves", "nbNodes", "fNodes"} )
public class DTree {

	@JsonProperty("root")
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
		return String.format("DTree[nodes: %d, leaves: %d,depth: %d]", rootNode.getNbNodes(), rootNode.getNbLeaves(), rootNode.getDepth());
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
