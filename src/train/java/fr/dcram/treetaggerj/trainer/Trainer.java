package fr.dcram.treetaggerj.trainer;

import com.google.common.base.Splitter;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.dtree.DTreeNode;
import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.dtree.LeafDTreeNode;
import fr.dcram.treetaggerj.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
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
		List<Trigram> allTrigrams = parseTrigrams(new FileReader(trainingCorpus), tagSet);
		LOGGER.info("Parsed {} trigrams", allTrigrams.size());
		DTree dTree = trainer.train(allTrigrams);
		System.out.println(dTree);
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
		List<List<Trigram>> partition = partition(best, trigrams);
		List<Trigram> l_ok = partition.get(0);
		List<Trigram> l_ko = partition.get(1);
		if(l_ko.size() <= RECURSION_FREQUENCY_THRESHOLD || l_ok.size() <= RECURSION_FREQUENCY_THRESHOLD) {
			return new LeafDTreeNode(toPTable(trigrams));
		} else {
			List<Feature> newFeatureSet = new ArrayList<>();
			newFeatureSet.addAll(features);
			newFeatureSet.remove(best);
			return new FeatureDTreeNode(
					best,
					getDecisionTreeNode(newFeatureSet, l_ok, tagSet),
					getDecisionTreeNode(newFeatureSet, l_ko, tagSet)
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

	private static List<List<Trigram>> partition(Feature feature, List<Trigram> trigrams) {
		List<List<Trigram>> partition = new ArrayList<>(2);
		List<Trigram> l_ok = new ArrayList<>();
		List<Trigram> l_ko = new ArrayList<>();
		for(Trigram trigram:trigrams) {
			if(hasFeature(trigram, feature))
				l_ok.add(trigram);
			else
				l_ko.add(trigram);
		}
		partition.add(l_ok);
		partition.add(l_ko);
		return partition;
	}

	public static double computeFeatureInformation(Feature feature, List<Trigram> trigrams, TagSet tagSet) {
		List<List<Trigram>> partition = partition(feature, trigrams);
		List<Trigram> l_ok = partition.get(0);
		List<Trigram> l_ko = partition.get(1);
		double p_ok = ((double)l_ok.size())/trigrams.size();
		double p_ko = 1 - p_ok;

		double sum_ok = tagSet.getTags().stream().map(tag -> plogp(tag, l_ok)).mapToDouble(Double::doubleValue).sum();
		double sum_ko = tagSet.getTags().stream().map(tag -> plogp(tag, l_ko)).mapToDouble(Double::doubleValue).sum();
		double info = - p_ok * sum_ok -  p_ko * sum_ko;
		return info;
	}

	private static double plogp(Tag tag, List<Trigram> context) {
		long cnt = context.stream().filter(t->t.getTag3().equals(tag)).count();
		double p = ((double)cnt)/context.size();
		double v = cnt == 0 ? 0d : p * (Math.log(p) / Math.log(2));
		return v;
	}

	private static boolean hasFeature(Trigram trigram, Feature feature) {
		if(feature.getBackPosition() == 2)
			return trigram.getTag1().equals(feature.getTag());
		else if(feature.getBackPosition() == 1)
			return trigram.getTag2().equals(feature.getTag());
		else
			throw new IllegalStateException();

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
