package fr.dcram.treetaggerj.model;

public class Feature {
	private Tag tag;
	private int backPosition;

	public Feature(Tag tag, int backPosition) {
		this.tag = tag;
		this.backPosition = backPosition;
	}

	public Tag getTag() {
		return tag;
	}

	public int getBackPosition() {
		return backPosition;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o instanceof Feature
								&& ((Feature)o).tag.equals(tag)
								&& ((Feature)o).backPosition == backPosition);
	}

	@Override
	public int hashCode() {
		return 2^16*tag.hashCode()+backPosition;
	}

	@Override
	public String toString() {
		return String.format("Back%d_%s", backPosition, tag.getLabel());
	}
}
