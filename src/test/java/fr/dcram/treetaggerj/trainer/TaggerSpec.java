package fr.dcram.treetaggerj.trainer;

import com.google.common.base.Splitter;
import fr.dcram.treetaggerj.Tagger;
import fr.dcram.treetaggerj.TreeTaggerJ;
import fr.dcram.treetaggerj.TreeTaggerModel;
import fr.dcram.treetaggerj.model.Token;
import fr.dcram.treetaggerj.util.TreeTaggerModelIO;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TaggerSpec {

	Tagger tagger;
	TreeTaggerModel model;

	@Before
	public void setup() throws IOException {
		File file = Paths.get("src/test/resources/model1.json").toFile();
		model = TreeTaggerModelIO.load(new FileReader(file));
		tagger = TreeTaggerJ.getTagger(model);
	}


	/*
	WARNING: tagging precision looks very poor on the example below.
	This is due to the very small sample model used model1.json.
	 */
	@Test
	public void test() {
		List<String> tokens = tokens("Le chat gris a mangé la belle souris verte .");
		List<Token> tagged = tokens.stream().map(s -> new Token(s, tagger.tag(s))).collect(toList());
		Assertions.assertThat(tagged)
				.hasSize(10)
				.extracting("text", "tag.label")
				.containsExactly(
						Tuple.tuple("Le", "DET:ART"),
						Tuple.tuple("chat", "NOM"),
						Tuple.tuple("gris", "ADJ"),
						Tuple.tuple("a", "VER:pres"),
						Tuple.tuple("mangé", "VER:pper"),
						Tuple.tuple("la", "DET:ART"),
						Tuple.tuple("belle", "UNK"),
						Tuple.tuple("souris", "PRP"),
						Tuple.tuple("verte", "NOM"),
						Tuple.tuple(".", "SENT")
				);
	}

	private List<String> tokens(String text) {
		return Splitter.on(" ").splitToList(text);
	}
}
