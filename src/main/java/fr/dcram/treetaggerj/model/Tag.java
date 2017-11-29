package fr.dcram.treetaggerj.model;

public class Tag {
	private int id;
	private String label;

	public Tag(int id, String label) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		return o == this
				|| (o instanceof Tag && ((Tag)o).id == this.id);
	}

	@Override
	public String toString() {
		return label;
	}
}
