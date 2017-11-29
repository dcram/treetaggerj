package fr.dcram.treetaggerj.dtree;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.List;

public abstract class DTreeNode {

	public abstract ProbaTable getTable(List<Feature> features);
}
