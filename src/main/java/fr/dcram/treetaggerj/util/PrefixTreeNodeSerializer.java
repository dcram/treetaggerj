package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.ptree.PrefixTreeNode;

import java.io.IOException;

public class PrefixTreeNodeSerializer extends StdSerializer<PrefixTreeNode> {

	public PrefixTreeNodeSerializer() {
		super(PrefixTreeNode.class);
	}

	@Override
	public void serialize(PrefixTreeNode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		if(value.get() != null) {
			gen.writeFieldName("table");
		}
		gen.writeEndObject();
	}
}
