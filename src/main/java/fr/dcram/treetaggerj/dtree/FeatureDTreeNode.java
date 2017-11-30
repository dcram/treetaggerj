package fr.dcram.treetaggerj.dtree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.List;

@JsonIgnoreProperties({"depth", "nbLeaves", "nbNodes", "nbFNodes"} )
public class FeatureDTreeNode extends DTreeNode {


	@JsonProperty("test")
	private Feature feature;

	@JsonProperty("yes")
	private DTreeNode yes;

	@JsonProperty("no")
	private DTreeNode no;

	public FeatureDTreeNode(DTreeNode parent, Feature feature, DTreeNode yes, DTreeNode no) {
		super(parent);
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

	@Override
	public int getNbNodes() {
		return 1 + yes.getNbNodes() + no.getNbNodes();
	}

	@Override
	public int getNbFNodes() {
		return 1 + yes.getNbFNodes() + no.getNbFNodes();
	}

	@Override
	public int getNbLeaves() {
		return yes.getNbLeaves() + no.getNbLeaves();
	}

	@Override
	public int getDepth() {
		return 1 + Math.max(yes.getDepth(), no.getDepth());
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

	public void replaceChild(FeatureDTreeNode oldChild, LeafDTreeNode newChild) {
		if(yes == oldChild)
			yes = newChild;
		else if(no == oldChild)
			no = newChild;
		else
			throw new IllegalArgumentException(String.format("No such child newChild: %s. YES: %s, NO: %s", oldChild, yes, no));
	}

}
