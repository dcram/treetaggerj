package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

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
}
