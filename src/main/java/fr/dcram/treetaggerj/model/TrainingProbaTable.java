package fr.dcram.treetaggerj.model;

import java.util.HashMap;
import java.util.Map;

public class TrainingProbaTable implements ProbaTable {
	Map<Tag, Integer> probas = new HashMap<>();

	@Override
	public Iterable<? extends Tag> getTags() {
		return probas.keySet();
	}

	@Override
	public double getProba(Tag tag) {
		return probas.containsKey(tag) ? probas.get(tag) : 0d;
	}

	@Override
	public Map<Tag, Integer> getMap() {
		return probas;
	}
}
