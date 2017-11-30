package fr.dcram.treetaggerj.util;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.TagSet;

import java.util.HashMap;
import java.util.Map;

public class FeatureSet {

	public static final String SERIALIZATION_FORMAT = "%d_%s";
	Map<String, Feature> features = new HashMap<>();
	TagSet tagSet;

	public FeatureSet(TagSet tagSet) {
		this.tagSet = tagSet;
	}

	public Feature getFeature(String tagLabel, int backPosition) {
		String serializedForm = String.format(SERIALIZATION_FORMAT, backPosition, tagLabel);
		if(!features.containsKey(serializedForm))
			features.put(serializedForm, new Feature(tagSet.getTag(tagLabel), backPosition));
		return features.get(serializedForm);
	}

	public Feature getFeature(String serializedForm) {
		int backPosition = Integer.parseInt(serializedForm.substring(0, 1));
		String tagLabel = serializedForm.substring(2);
		return getFeature(tagLabel, backPosition);

	}
}
