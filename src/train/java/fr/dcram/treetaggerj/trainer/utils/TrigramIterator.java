package fr.dcram.treetaggerj.trainer.utils;

import com.google.common.collect.AbstractIterator;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.trainer.Trigram;

import java.util.Iterator;

public class TrigramIterator extends AbstractIterator<Trigram> {
	private Iterator<Trigram> trigramIterator;
	private Feature feature;
	private boolean has;

	public TrigramIterator(Iterator<Trigram> all, Feature feature, boolean has) {
		this.trigramIterator = all;
		this.feature = feature;
		this.has = has;
	}

	Trigram trigram;

	@Override
	protected Trigram computeNext() {
		while(trigramIterator.hasNext()) {
			trigram = trigramIterator.next();
			if(trigram.hasFeature(feature)) {
				if(has)
					return trigram;
			} else {
				if(!has)
					return trigram;
			}
		}
		return endOfData();
	}
}
