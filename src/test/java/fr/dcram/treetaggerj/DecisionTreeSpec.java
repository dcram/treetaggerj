package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.dtree.DTreeNode;
import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.dtree.LeafDTreeNode;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static fr.dcram.treetaggerj.Tests.feature;
import static fr.dcram.treetaggerj.Tests.tag;

public class DecisionTreeSpec {
	DTree decisionTree;
	ProbaTable table1;
	ProbaTable table2;
	ProbaTable table3;



	@Before
	public void setup() {
		table1 = Mockito.mock(ProbaTable.class);
		table2 = Mockito.mock(ProbaTable.class);
		table3 = Mockito.mock(ProbaTable.class);

		DTreeNode root = new FeatureDTreeNode(
				null,
				new Feature(tag("A"), 1),
				new FeatureDTreeNode(
						null,
						new Feature(tag("B"), 2),
						new LeafDTreeNode(null, table1),
						new LeafDTreeNode(null, table2)
				),
				new LeafDTreeNode(null, table3)
		);
		decisionTree = new DTree(root);
	}

	@Test
	public void test() {
		Feature a1 = feature("A", 1);
		Feature b2 = feature("B", 2);
		Feature c1 = feature("C", 1);
		Assertions.assertThat(decisionTree.getTable(Lists.newArrayList(a1, b2))).isEqualTo(table1);
		Assertions.assertThat(decisionTree.getTable(Lists.newArrayList(a1, c1))).isEqualTo(table2);
		Assertions.assertThat(decisionTree.getTable(Lists.newArrayList(b2, c1))).isEqualTo(table3);
	}
}
