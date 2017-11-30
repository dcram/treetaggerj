package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.TreeTaggerModel;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.Token;
import fr.dcram.treetaggerj.util.TreeTaggerModelIO;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class TrainerFuncTest {
	Trainer trainer;
	TagSet tagSet;
	List<Trigram> trigrams;
	List<List<Token>> sequences;

	@Before
	public void setup() throws IOException {
		tagSet = new TagSet();
		trainer = new Trainer(tagSet);
		File file = Paths.get("src/train/resources/frwikinews.tt_100").toFile();
		sequences = Trainer.parseSequences(new FileReader(file), tagSet);
		trigrams = Trainer.toTrigrams(sequences, tagSet);

	}

	@Test
	public void test() throws IOException {
		TreeTaggerModel model = trainer.train(sequences);
		System.out.println(model);
		TreeTaggerModelIO.save(model, new PrintWriter(System.out));
	}

}
