package fr.dcram.treetaggerj.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ProbaTable {

	Iterable<? extends Tag> getTags();

	double getProba(Tag tag);

	default String toString(String tab) {
		List<Map.Entry<Tag, Integer>> list = new ArrayList<>();
		list.addAll(getMap().entrySet());
		return list.stream()
				.sorted((e1,e2) -> Integer.compare(e2.getValue(), e1.getValue()))
				.map(e -> String.format("%s%-6s:%d", tab, e.getKey(),e.getValue().intValue()))
				.collect(Collectors.joining("\n"));
	}

	Map<Tag, Integer> getMap();

}
