package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.List;

public class FeatureDTreeNode extends DTreeNode {

	private Feature feature;
	private DTreeNode yes;
	private DTreeNode no;

	public FeatureDTreeNode(Feature feature, DTreeNode yes, DTreeNode no) {
		this.feature = feature;
		this.yes = yes;
		this.no = no;
	}

	@Override
	public ProbaTable getTable(List<Feature> features) {
		return (features.contains(feature) ? yes : no).getTable(features);
	}
}
