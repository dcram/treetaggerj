package fr.dcram.treetaggerj.model;

public interface ProbaTable {

	double getProba(Tag tag);

	int getTotalFrequency();
}
