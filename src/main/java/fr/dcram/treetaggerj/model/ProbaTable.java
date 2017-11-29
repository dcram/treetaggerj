package fr.dcram.treetaggerj.model;

public interface ProbaTable {

	Iterable<? extends Tag> getTags();

	double getProba(Tag tag);
}
