package fr.dcram.treetaggerj.trainer;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.dtree.DTreeNode;
import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.dtree.LeafDTreeNode;
import fr.dcram.treetaggerj.model.*;
import fr.dcram.treetaggerj.trainer.utils.TrigramIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Trainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Trainer.class);
	public static final String SEPARATOR = " ";
	public static final int RECURSION_FREQUENCY_THRESHOLD = 2;

	public static void main(String[] args) throws IOException {
		String trainingCorpus = args[0];
		String outputModel = args[1];

		LOGGER.info("Training TreeTagger model.");
		LOGGER.info("Training corpus: {}", trainingCorpus);
		LOGGER.info("output model: {}", outputModel);

		TagSet tagSet = new TagSet();

		Trainer trainer = new Trainer(tagSet);
		Stopwatch sw = Stopwatch.createStarted();
		List<Trigram> allTrigrams = parseTrigrams(new FileReader(trainingCorpus), tagSet);
		sw.stop();
		LOGGER.info("Parsed {} trigrams in {}ms", allTrigrams.size(), sw.elapsed(TimeUnit.MILLISECONDS));
		sw = Stopwatch.createStarted();
		DTree dTree = trainer.train(allTrigrams);
		sw.stop();
		LOGGER.info("Trained {} trigrams in {}sec", allTrigrams.size(), sw.elapsed(TimeUnit.SECONDS));

		LOGGER.info("Nb nodes : {} ", dTree.getNbNodes());
		LOGGER.info("Nb leaves: {} ", dTree.getLeaves());
		LOGGER.info("Nb fnode: {} ", dTree.getFNodes());
		LOGGER.info("Depth: {} ", dTree.getDepth());

	}

	private TagSet tagSet;

	public Trainer(TagSet tagSet) {
		this.tagSet = tagSet;
	}

	public DTree train(List<Trigram> trigrams) {
		List<Feature> features = toFeatureSet(tagSet);
		DTree dTree = new DTree(getDecisionTreeNode(features, trigrams, tagSet));
		return dTree;
	}

	private DTreeNode getDecisionTreeNode(List<Feature> features, List<Trigram> trigrams, TagSet tagSet) {
		Feature best = selectBestFeature(features, trigrams, tagSet);
		int nb_ok = Iterators.size(new TrigramIterator(trigrams.iterator(), best, true));
		int nb_ko = trigrams.size() - nb_ok;
		if(nb_ok <= RECURSION_FREQUENCY_THRESHOLD || nb_ko <= RECURSION_FREQUENCY_THRESHOLD) {
			return new LeafDTreeNode(toPTable(trigrams));
		} else {
			List<Feature> newFeatureSet = new ArrayList<>();
			newFeatureSet.addAll(features);
			newFeatureSet.remove(best);

			DTreeNode yesNode = getDecisionTreeNode(
					newFeatureSet,
					Lists.newArrayList(new TrigramIterator(trigrams.iterator(), best, true)),
					tagSet);
			DTreeNode noNode = getDecisionTreeNode(
					newFeatureSet,
					Lists.newArrayList(new TrigramIterator(trigrams.iterator(), best, false)),
					tagSet);
			return new FeatureDTreeNode(
					best,
					yesNode,
					noNode
				);
		}
	}

	private static ProbaTable toPTable(List<Trigram> trigrams) {
		TrainingProbaTable table = new TrainingProbaTable();
		for(Trigram t:trigrams)
			table.add(t.getTag3());
		return table;
	}

	public static Feature selectBestFeature(List<Feature> features, List<Trigram> allTrigrams, TagSet tagSet) {
		Feature best = features.get(0);
		double minInfo = Double.MAX_VALUE;
		for(Feature feature:features) {
			double info = computeFeatureInformation(feature, allTrigrams, tagSet);
			if(info < minInfo) {
				minInfo = info;
				best = feature;
			}
		}
		return best;
	}

//	private static List<List<Trigram>> partition(Feature feature, List<Trigram> trigrams) {
//		List<List<Trigram>> partition = new ArrayList<>(2);
//		List<Trigram> l_ok = new ArrayList<>();
//		List<Trigram> l_ko = new ArrayList<>();
//		for(Trigram trigram:trigrams) {
//			if(trigram.hasFeature(feature))
//				l_ok.add(trigram);
//			else
//				l_ko.add(trigram);
//		}
//		partition.add(l_ok);
//		partition.add(l_ko);
//		return partition;
//	}

	public static double computeFeatureInformation(Feature feature, List<Trigram> trigrams, TagSet tagSet) {

//		double sum_ok = getInformation(tagSet, new TrigramIterator(trigrams.iterator(), feature, true));
//		double sum_ko = getInformation(tagSet, new TrigramIterator(trigrams.iterator(), feature, false));

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
		double sum_ok = plogpsum(counter_ok, total_ok);
		double sum_ko = plogpsum(counter_ko, total_ko);


		double info = - p_ok * sum_ok -  p_ko * sum_ko;
		return info;
	}

	private static void countTrigram(Map<Tag, AtomicInteger> counter_ok, Trigram trigram) {
		if(!counter_ok.containsKey(trigram.getTag3()))
			counter_ok.put(trigram.getTag3(), new AtomicInteger(1));
		else
			counter_ok.get(trigram.getTag3()).incrementAndGet();
	}

	private static double plogpsum(Map<Tag, AtomicInteger> counter, int total) {
		double sum = 0;
		for(Map.Entry<Tag, AtomicInteger> e:counter.entrySet())
			sum += plogp(((double)e.getValue().intValue())/total);
		return sum;
	}

	private static double plogp(double v) {
		return v == 0 ? 0d : v * (Math.log(v) / Math.log(2));
	}


	private static List<Feature> toFeatureSet(TagSet tagSet) {
		List<Feature> features = new ArrayList<>(tagSet.size()*2);
		for(Tag tag:tagSet.getTagsButDefault()) {
			features.add(new Feature(tag, 1));
			features.add(new Feature(tag, 2));
		}
		return features;
	}

	public static List<Trigram> parseTrigrams(Reader trainingCorpus, TagSet tagSet) throws IOException {
		String text;
		List<Trigram> allTrigrams = new ArrayList<>();
		BufferedReader br = new BufferedReader(trainingCorpus);
		int line = 0;
		LOGGER.debug("Starts parsing trigrams");
		while((text = br.readLine())!=null) {
			List<Token> tokens = Splitter.on(SEPARATOR).splitToList(text)
					.stream()
					.map(String::trim)
					.filter(str -> !str.isEmpty())
					.map(string -> Token.parse(string, tagSet))
					.collect(Collectors.toList());
			for(int i = 0; i < tokens.size(); i++) {
				Tag tag1 = (i < 2) ? tagSet.getStartTag() : tokens.get(i-2).getTag();
				Tag tag2 = (i < 1) ? tagSet.getStartTag() : tokens.get(i-1).getTag();
				Tag tag3 = tokens.get(i).getTag();
				allTrigrams.add(new Trigram(tag1, tag2, tag3));
			}
		}
		return allTrigrams;
	}
}
