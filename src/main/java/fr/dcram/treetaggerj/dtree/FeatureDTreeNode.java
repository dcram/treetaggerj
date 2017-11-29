package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.Collections;
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

	@Override
	public String toString(int depth) {
		return String.format("%s%n%syes-> %s%nno ->%s",
				feature,
				tab(depth), yes.toString(depth+1),
				tab(depth), no.toString(depth+1)
				);
	}


	public Feature getFeature() {
		return feature;
	}

	public DTreeNode getYes() {
		return yes;
	}

	public DTreeNode getNo() {
		return no;
	}
}
