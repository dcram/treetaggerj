package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.TagSet;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class TrainerFuncTest {
	Trainer trainer;
	TagSet tagSet;
	List<Trigram> trigrams;

	@Before
	public void setup() throws IOException {
		tagSet = new TagSet();
		trainer = new Trainer(tagSet);
		File file = Paths.get("src/train/resources/frwikinews.tt_100").toFile();
		trigrams = Trainer.parseTrigrams(new FileReader(file), tagSet);

	}

	@Test
	public void test() {
		DTree tree = trainer.train(trigrams);
		System.out.println(tree);
	}

}
