package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.dcram.treetaggerj.model.Feature;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureDeserializer extends StdDeserializer<Feature> {

	public static final Pattern FEATURE_PATTERN = Pattern.compile("(\\d)_(.+)");

	private FeatureSet featureSet;
	public FeatureDeserializer(FeatureSet featureSet) {
		super(Feature.class);
		this.featureSet = featureSet;
	}

	@Override
	public Feature deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String s = p.getValueAsString();
		Matcher matcher = FEATURE_PATTERN.matcher(s);
		if(matcher.find()) {
			int backPosition = Integer.parseInt(matcher.group(1));
			String tagLabel = matcher.group(2);
			return featureSet.getFeature(tagLabel, backPosition);
		} else
			throw new IllegalArgumentException("Bad feature format: " + s);
	}

	protected FeatureDeserializer(Class<Feature> t) {
		super(t);
	}

}
