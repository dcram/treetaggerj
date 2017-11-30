package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.trainer.Trainer;
import fr.dcram.treetaggerj.util.TreeTaggerModelIO;

import java.io.*;
import java.nio.file.Paths;

public class GenerateModel1Json {
	public static void main(String[] args) throws IOException {
		TagSet tagSet = new TagSet();
		Trainer trainer = new Trainer(tagSet);
		File file = Paths.get("src/train/resources/frwikinews.tt_100").toFile();
		trainer.getConfig().setsTreeWeightedInformationGainTh(4);
		TreeTaggerModel model = trainer.train(Trainer.parseSequences(new FileReader(file), tagSet));
		TreeTaggerModelIO.save(model, new FileWriter(Paths.get("src/test/resources/model1.json").toFile()));
	}
}
