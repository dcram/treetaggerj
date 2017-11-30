package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.Tests;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.Token;
import fr.dcram.treetaggerj.trainer.utils.TrigramIterator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class TrigramIteratorSpec {

	Trainer trainer;
	TagSet tagSet;
	List<Trigram> trigrams;

	@Before
	public void setup() throws IOException {
		tagSet = new TagSet();
		trainer = new Trainer(tagSet);
	}

	@Test
	public void testHas() throws IOException {
		getTrigrams();
		TrigramIterator trigramIterator = new TrigramIterator(trigrams.iterator(), Tests.feature("A", 1), true);
		assertThat(trigramIterator)
				.extracting("tag1.label", "tag2.label", "tag3.label")
				.containsExactly(
						tuple("START", "A", "B"),
						tuple("C", "A", "B")
				);
	}

	@Test
	public void testDoesNotHave() throws IOException {
		getTrigrams();
		TrigramIterator trigramIterator = new TrigramIterator(trigrams.iterator(), Tests.feature("A", 1), false);
		assertThat(trigramIterator)
				.extracting("tag1.label", "tag2.label", "tag3.label")
				.containsExactly(
						tuple("START", "START", "A"),
						tuple("A", "B", "B"),
						tuple("B", "B", "C"),
						tuple("B", "C", "A"),
						tuple("A", "B", "B"),
						tuple("B", "B", "C")
				);
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
