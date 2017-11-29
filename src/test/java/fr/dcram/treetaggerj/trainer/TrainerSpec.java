package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.Tests;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.TrainingProbaTable;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static fr.dcram.treetaggerj.Tests.feature;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class TrainerSpec {

	Trainer trainer;
	TagSet tagSet;
	List<Trigram> trigrams;

	@Before
	public void setup() throws IOException {
		tagSet = new TagSet();
		trainer = new Trainer(tagSet);
	}

	@Test
	public void testParseTrigrams() throws IOException {
		trigrams = Trainer.parseTrigrams(new StringReader("À_PRP la_DET:ART suite_NOM de_PRP la_DET:ART parution_NOM le_DET:ART matin_NOM ._SENT"), tagSet);
		assertThat(trigrams)
				.extracting("tag1.label", "tag2.label", "tag3.label")
				.containsExactly(
						tuple("START", "START", "PRP"),
						tuple("START", "PRP", "DET:ART"),
						tuple("PRP", "DET:ART", "NOM"),
						tuple("DET:ART", "NOM", "PRP"),
						tuple("NOM", "PRP", "DET:ART"),
						tuple("PRP", "DET:ART", "NOM"),
						tuple("DET:ART", "NOM", "DET:ART"),
						tuple("NOM", "DET:ART", "NOM"),
						tuple("DET:ART", "NOM", "SENT")
				);
	}

	@Test
	public void testTrain() throws IOException {
		getTrigrams();
		double information = Trainer.computeFeatureInformation(feature("A", 1), trigrams, tagSet);

	}

	private void getTrigrams() throws IOException {
	/*
	Trigrams
	- - A
	- A B
	A B B
	B B C
	B C A
	C A B
	A B B
	B B C
	 */
		trigrams = Trainer.parseTrigrams(new StringReader("a_A a_B a_B a_C _A a_B a_B a_C"), tagSet);
		assertThat(trigrams)
				.extracting("tag1.label", "tag2.label", "tag3.label")
				.containsExactly(
						tuple("START", "START", "A"),
						tuple("START", "A", "B"),
						tuple("A", "B", "B"),
						tuple("B", "B", "C"),
						tuple("B", "C", "A"),
						tuple("C", "A", "B"),
						tuple("A", "B", "B"),
						tuple("B", "B", "C")
				);
	}

}
