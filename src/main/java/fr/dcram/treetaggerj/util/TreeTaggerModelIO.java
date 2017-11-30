package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dcram.treetaggerj.TreeTaggerModel;

import java.io.IOException;
import java.io.Writer;

public class TreeTaggerModelIO {

	public static void save(TreeTaggerModel model, Writer writer) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		mapper
				.writerWithDefaultPrettyPrinter()
				.writeValue(writer, model);
	}
}
