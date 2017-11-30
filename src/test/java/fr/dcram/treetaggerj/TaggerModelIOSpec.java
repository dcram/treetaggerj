package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.dtree.LeafDTreeNode;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.TaggingProbaTable;
import fr.dcram.treetaggerj.util.TreeTaggerModelIO;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaggerModelIOSpec {


	@Test
	public void test() throws IOException {
		Path path = Paths.get("src/test/resources/model1.json");
		TreeTaggerModel model = TreeTaggerModelIO.load(new FileReader(path.toFile()));
		// should not fail

		TaggingProbaTable tableResultat = (TaggingProbaTable)model.getLexicon().getTable("résultats");
		TagSet tagSet = model.getTagSet();
		Assertions.assertThat(tableResultat.getTotalFrequency())
				.isEqualTo(2);
		Assertions.assertThat(tableResultat.getTagIds())
				.containsExactly(tagSet.getTag("NOM").getId());
		Assertions.assertThat(tableResultat.getProba(tagSet.getTag("NOM")))
				.isEqualTo(1d);

		TaggingProbaTable tableUne = (TaggingProbaTable)model.getLexicon().getTable("une");
		Assertions.assertThat(tableUne.getProba(tagSet.getTag("NUM")))
				.isEqualTo(1.0/15);
		Assertions.assertThat(tableUne.getProba(tagSet.getTag("DET:ART")))
				.isEqualTo(14.0/15);

		Assertions.assertThat(model.getDecisionTree().getDepth()).isEqualTo(25);
		Assertions.assertThat(model.getDecisionTree().getRootNode())
				.isInstanceOf(FeatureDTreeNode.class)
				;

		FeatureDTreeNode rootNode = (FeatureDTreeNode) model.getDecisionTree().getRootNode();
		Assertions.assertThat(rootNode.getFeature().getBackPosition()).isEqualTo(1);
		Assertions.assertThat(rootNode.getFeature().getTag()).isEqualTo(tagSet.getTag("NOM"));

		FeatureDTreeNode yesNode = (FeatureDTreeNode)rootNode.getYes();
		Assertions.assertThat(yesNode.getFeature().getBackPosition()).isEqualTo(2);
		Assertions.assertThat(yesNode.getFeature().getTag()).isEqualTo(tagSet.getTag("DET:ART"));

		LeafDTreeNode yesYesNode = (LeafDTreeNode)yesNode.getYes();
		Assertions.assertThat(yesYesNode.getTable().getTotalFrequency()).isEqualTo(251);
		Assertions.assertThat(yesYesNode.getTable().getProba(tagSet.getTag("PRP")))
				.isEqualTo(78.0/251);


		ProbaTable mangeraient = model.getLexicon().getSuffixTree().get("mangeraient");
		Assertions.assertThat(mangeraient).isNotNull();
		Assertions.assertThat(mangeraient.getTotalFrequency()).isEqualTo(7);
		Assertions.assertThat(mangeraient.getProba(tagSet.getTag("VER:cond"))).isEqualTo(1.0/7);
		Assertions.assertThat(mangeraient.getProba(tagSet.getTag("VER:impf"))).isEqualTo(6.0/7);

	}

}