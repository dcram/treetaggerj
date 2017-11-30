package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.ptree.PrefixTreeNode;

import java.io.IOException;

public class PrefixTreeNodeDeserializer extends StdDeserializer<PrefixTreeNode<ProbaTable>> {
	TagSet tagSet;
	public PrefixTreeNodeDeserializer(TagSet tagSet) {
		super(PrefixTreeNode.class);
		this.tagSet = tagSet;
	}

	@Override
	public PrefixTreeNode<ProbaTable> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return null;
	}
}
