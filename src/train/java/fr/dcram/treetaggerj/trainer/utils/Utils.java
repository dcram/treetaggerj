package fr.dcram.treetaggerj.trainer.utils;

import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.Tag;
import fr.dcram.treetaggerj.trainer.TrainingProbaTable;
import fr.dcram.treetaggerj.trainer.Trigram;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}


	/**
	 * Gets Iq, the information of test q (i.e. testing if trigram has param feature)
	 */
	public static double getIq(Feature feature, List<Trigram> trigrams) {
		int total_ok = 0, total_ko = 0;
		Map<Tag, AtomicInteger> counter_ok = new HashMap<>(), counter_ko = new HashMap<>();
		for(Trigram trigram:trigrams) {
			if(trigram.hasFeature(feature)) {
				total_ok++;
				countTrigram(counter_ok, trigram);
			} else {
				total_ko++;
				countTrigram(counter_ko, trigram);
			}
		}
		double p_ok = ((double)total_ok)/trigrams.size();
		double p_ko = 1 - p_ok;
		double sum_ok = getI0(counter_ok, total_ok);
		double sum_ko = getI0(counter_ko, total_ko);


		double info = - p_ok * sum_ok -  p_ko * sum_ko;
		return info;
	}


	public static void countTrigram(Map<Tag, AtomicInteger> counter_ok, Trigram trigram) {
		if(!counter_ok.containsKey(trigram.getTag3()))
			counter_ok.put(trigram.getTag3(), new AtomicInteger(1));
		else
			counter_ok.get(trigram.getTag3()).incrementAndGet();
	}

	public static double getI0(Map<Tag, AtomicInteger> table, int total) {
		double sum = 0;
		for(Map.Entry<Tag, AtomicInteger> e:table.entrySet())
			sum += plogp(((double)e.getValue().intValue())/total);
		return sum;
	}

	public static double plogp(double v) {
		return v == 0 ? 0d : v * (Math.log(v) / Math.log(2));
	}

	public static TrainingProbaTable merge(TrainingProbaTable table1, TrainingProbaTable table2) {
		TrainingProbaTable m = new TrainingProbaTable();
		m.merge(table1);
		m.merge(table2);
		return m;
	}

	public static double getI0(TrainingProbaTable table1) {
		double sum = 0;
		int total = table1.getTotalFrequency();
		for(Map.Entry<Tag, Integer> e:table1.getMap().entrySet())
			sum -= plogp(((double)e.getValue())/total);
		return sum;
	}

	public static double getIq(TrainingProbaTable table1, TrainingProbaTable table2) {
		int total = table1.getTotalFrequency() + table2.getTotalFrequency();
		double p1 = ((double)table1.getTotalFrequency())/total;
		double p2 = ((double)table2.getTotalFrequency())/total;
		double i1 = getI0(table1);
		double i2 = getI0(table2);
		double sum = p1 * i1 + p2 * i2;
		return sum;
	}


	public static double getWeightedInformationGain(TrainingProbaTable table1, TrainingProbaTable table2) {
		TrainingProbaTable merged = merge(table1, table2);
		int f = merged.getTotalFrequency();
		double i0 = getI0(merged);
		double iq = getIq(table1, table2);
		double g = f * (i0 - iq);
		return g;
	}

	public static TrainingProbaTable merge(Iterable<ProbaTable> tables) {
		TrainingProbaTable m = new TrainingProbaTable();
		for(ProbaTable table:tables) {
			m.merge((TrainingProbaTable)table);
		}
		return m;

	}
}
