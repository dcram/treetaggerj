package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.Tag;

import java.util.List;

public class LeafDTreeNode extends DTreeNode {


	private ProbaTable table;

	public LeafDTreeNode(ProbaTable table) {
		this.table = table;
	}

	@Override
	public ProbaTable getTable(List<Feature> features) {
		return table;
	}

	@Override
	public String toString(int depth) {
		return table.toString(tab(depth));
	}

	@Override
	public int getNbNodes() {
		return 1;
	}

	@Override
	public int getNbFNodes() {
		return 0;
	}

	@Override
	public int getNbLeaves() {
		return 1;
	}

	@Override
	public int getDepth() {
		return 0;
	}
}
