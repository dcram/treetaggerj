package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.dcram.treetaggerj.TreeTaggerModel;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class TreeTaggerModelIO {

	public static void save(TreeTaggerModel model, Writer writer) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		mapper
				.writerWithDefaultPrettyPrinter()
				.writeValue(writer, model);
	}


	public static TreeTaggerModel load(Reader reader) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TagSet tagSet = new TagSet();
		FeatureSet featureSet = new FeatureSet(tagSet);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Feature.class, new FeatureDeserializer(featureSet));
		ProbaTableDeserializer deser = new ProbaTableDeserializer(tagSet);
		module.addDeserializer(DTree.class, new DTreeDeserializer(featureSet, deser));
		module.addDeserializer(ProbaTable.class, deser);
		mapper.registerModule(module);
		TreeTaggerModel treeTaggerModel = mapper.readValue(reader, TreeTaggerModel.class);
		treeTaggerModel.setTagSet(tagSet);
		treeTaggerModel.setFeatureSet(featureSet);

		return treeTaggerModel;
	}

}
