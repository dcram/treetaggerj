package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.Tag;

public class Trigram {

	private Tag tag1, tag2, tag3;

	public Trigram(Tag tag1, Tag tag2, Tag tag3) {
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.tag3 = tag3;
	}

	public Tag getTag1() {
		return tag1;
	}

	public Tag getTag2() {
		return tag2;
	}

	public Tag getTag3() {
		return tag3;
	}

	@Override
	public String toString() {
		return String.format("%s_%s_%s", tag1, tag2, tag3);
	}


	public boolean hasFeature(Feature feature) {
		if(feature.getBackPosition() == 2)
			return tag1.equals(feature.getTag());
		else if(feature.getBackPosition() == 1)
			return tag2.equals(feature.getTag());
		else
			throw new IllegalStateException();

	}

}
