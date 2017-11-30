package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.TaggingProbaTable;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.IOException;

public class ProbaTableDeserializer extends StdDeserializer<ProbaTable> {

	public static final String TOTAL = "total";
	public static final String TAGS = "tags";
	public static final String TABLE = "table";
	TagSet tagSet;

	public ProbaTableDeserializer(TagSet tagSet) {
		super(ProbaTable.class);
		this.tagSet = tagSet;
	}

	@Override
	public ProbaTable deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		int totalFrequency = -1;
		TIntIntHashMap map = null;

		// useful var for debug
		JsonToken tok;

		String fieldname, tag;
		while ((tok = p.nextToken()) != JsonToken.END_OBJECT) {
			fieldname = p.getCurrentName();
			if (TOTAL.equals(fieldname))
				totalFrequency = p.nextIntValue(-1);
			else if(TAGS.equals(fieldname)) {
				map = new TIntIntHashMap(totalFrequency);
				p.nextToken();
				while ((tok = p.nextToken()) != JsonToken.END_OBJECT) {
					tag = p.getCurrentName();
					int f = p.nextIntValue(-1);
					map.put(tagSet.getTagCreateIfNull(tag).getId(), f);
				}
			}
		}
		return new TaggingProbaTable(tagSet, totalFrequency, map);
	}
}
