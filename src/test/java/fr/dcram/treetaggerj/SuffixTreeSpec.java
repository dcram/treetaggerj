package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.ptree.PrefixTreeNode;
import fr.dcram.treetaggerj.ptree.SuffixTree;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SuffixTreeSpec {

	SuffixTree suffixTree;
	ProbaTable tableIng;
	ProbaTable tableIne;
	ProbaTable tableNne;


	@Before
	public void setup() {
		suffixTree = new SuffixTree(new PrefixTreeNode<>());
		tableIng = Mockito.mock(ProbaTable.class);
		tableIne = Mockito.mock(ProbaTable.class);
		tableNne = Mockito.mock(ProbaTable.class);
		suffixTree.add("ing", tableIng);
		suffixTree.add("ine", tableIne);
		suffixTree.add("nne", tableNne);
	}

	@Test
	public void test() {
		Assertions.assertThat(suffixTree.get("ranging")).isEqualTo(tableIng).isNotEqualTo(tableIne);
		Assertions.assertThat(suffixTree.get("ing")).isEqualTo(tableIng);
		Assertions.assertThat(suffixTree.get("ring")).isEqualTo(tableIng);
		Assertions.assertThat(suffixTree.get("ng")).isNull();
		Assertions.assertThat(suffixTree.get("in")).isNull();
		Assertions.assertThat(suffixTree.get("")).isNull();
		Assertions.assertThat(suffixTree.get("rangine")).isEqualTo(tableIne);
		Assertions.assertThat(suffixTree.get("binne")).isEqualTo(tableNne);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_and_validate_exception() throws Exception {
		// there is already a table for such prefix
		suffixTree.add("ing", tableIne);
	}
}
