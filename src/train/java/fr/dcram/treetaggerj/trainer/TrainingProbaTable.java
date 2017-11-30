package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingProbaTable implements ProbaTable {
	private Map<Tag, AtomicInteger> map = new HashMap<>();
	private AtomicInteger total = new AtomicInteger(0);


	public void add(Tag tag) {
		total.incrementAndGet();
		if(!map.containsKey(tag))
			map.put(tag, new AtomicInteger(1));
		else
			map.get(tag).incrementAndGet();
	}

	@Override
	public Iterable<? extends Tag> getTags() {
		return map.keySet();
	}

	@Override
	public double getProba(Tag tag) {
		return map.containsKey(tag) ? ((double)map.get(tag).intValue())/total.intValue() : 0;
	}

	@Override
	public Map<Tag, Integer> getMap() {
		Map<Tag, Integer> m = new HashMap<>();
		for(Map.Entry<Tag, AtomicInteger> e:map.entrySet())
			m.put(e.getKey(), e.getValue().intValue());
		return m;
	}

	public void merge(TrainingProbaTable table) {
		for(Map.Entry<Tag, AtomicInteger> e:table.map.entrySet()) {
			if(map.containsKey(e.getKey())) {
				map.get(e.getKey()).addAndGet(e.getValue().intValue());
			}
			else {
				map.put(e.getKey(), new AtomicInteger(e.getValue().intValue()));
			}
		}
		total.addAndGet(table.getTotalFrequency());
	}

	@Override
	public int getTotalFrequency() {
		return total.intValue();
	}
}
