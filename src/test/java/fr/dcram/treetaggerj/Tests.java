package fr.dcram.treetaggerj;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.Tag;
import fr.dcram.treetaggerj.model.TagSet;


public class Tests {

	private static TagSet tagSet = null;

	public static Tag tag(String tagLabel) {
		if(tagSet().getTag(tagLabel) == null)
			tagSet().addTag(tagLabel);
		return tagSet().getTag(tagLabel);
	}

	public static TagSet tagSet() {
		if(tagSet == null)
			tagSet = new TagSet();
		return tagSet;
	}

	public static Feature feature(String tagLabel, int backPosition) {
		return new Feature(tag(tagLabel), backPosition);
	}
}
