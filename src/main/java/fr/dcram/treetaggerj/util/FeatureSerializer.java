package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.dcram.treetaggerj.model.Feature;

import java.io.IOException;

public class FeatureSerializer  extends StdSerializer<Feature> {

	protected FeatureSerializer() {
		super(Feature.class);
	}

	protected FeatureSerializer(Class<Feature> t) {
		super(t);
	}

	@Override
	public void serialize(Feature value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(String.format("%d_%s", value.getBackPosition(), value.getTag().getLabel()));
	}
}
