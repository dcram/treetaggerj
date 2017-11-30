package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.Token;
import fr.dcram.treetaggerj.trainer.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static fr.dcram.treetaggerj.Tests.feature;
import static fr.dcram.treetaggerj.trainer.utils.Utils.plogp;
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
		List<List<Token>> sequences = Trainer.parseSequences(new StringReader("À_PRP la_DET:ART suite_NOM de_PRP la_DET:ART parution_NOM le_DET:ART matin_NOM ._SENT"), tagSet);
		trigrams = Trainer.toTrigrams(sequences, tagSet);

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
	public void testComputeInformation() throws IOException {
		getTrigrams();
		double information = Utils.getIq(feature("A", 1), trigrams);
		double expected = -0.25*(
								plogp(1)//B
							) - 0.75 * (
								plogp(2d/6)//A
							 +  plogp(2d/6)//B
							 +  plogp(2d/6)//C
							);
		assertThat(information)
				.isEqualTo(expected)
				.isEqualTo(1.188721875540867)
		;
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
		List<List<Token>> sequences = Trainer.parseSequences(new StringReader("a_A a_B a_B a_C _A a_B a_B a_C"), tagSet);
		trigrams = Trainer.toTrigrams(sequences, tagSet);

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
