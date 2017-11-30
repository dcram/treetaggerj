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
import fr.dcram.treetaggerj.trainer.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fr.dcram.treetaggerj.trainer.utils.Utils.getIq;

public class Trainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Trainer.class);
	public static final String SEPARATOR = " ";
	public static final int RECURSION_FREQUENCY_THRESHOLD = 2;
	public static final int WEIGHTED_INFORMATION_GAIN_TH = 5;

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


	}

	private TagSet tagSet;

	public Trainer(TagSet tagSet) {
		this.tagSet = tagSet;
	}

	public DTree train(List<Trigram> trigrams) {
		List<Feature> features = toFeatureSet(tagSet);
		DTree dTree = new DTree(getDecisionTreeNode(null, features, trigrams, tagSet));
		logDTree(dTree);

		LOGGER.info("Pruning dTree with threshold {} ", WEIGHTED_INFORMATION_GAIN_TH);
		int nbPruned = prune(dTree, WEIGHTED_INFORMATION_GAIN_TH);
		LOGGER.info("Pruned {} nodes", nbPruned);
		logDTree(dTree);

		return dTree;
	}

	private void logDTree(DTree dTree) {
		LOGGER.info("Nb nodes : {} (NodeIterator method: {})", dTree.getNbNodes(), Iterators.size(dTree.nodeIterator()));
		LOGGER.info("Nb leaves: {} ", dTree.getLeaves());
		LOGGER.info("Nb features nodes: {} ", dTree.getFNodes());
		LOGGER.info("Depth: {} ", dTree.getDepth());
	}

	private DTreeNode getDecisionTreeNode(DTreeNode parent, List<Feature> features, List<Trigram> trigrams, TagSet tagSet) {
		Feature best = selectBestFeature(features, trigrams, tagSet);
		int nb_ok = Iterators.size(new TrigramIterator(trigrams.iterator(), best, true));
		int nb_ko = trigrams.size() - nb_ok;
		if(nb_ok <= RECURSION_FREQUENCY_THRESHOLD || nb_ko <= RECURSION_FREQUENCY_THRESHOLD) {
			return new LeafDTreeNode(parent, toPTable(trigrams));
		} else {
			List<Feature> newFeatureSet = new ArrayList<>();
			newFeatureSet.addAll(features);
			newFeatureSet.remove(best);

			DTreeNode yesNode = getDecisionTreeNode(
					null,
					newFeatureSet,
					Lists.newArrayList(new TrigramIterator(trigrams.iterator(), best, true)),
					tagSet);
			DTreeNode noNode = getDecisionTreeNode(
					null,
					newFeatureSet,
					Lists.newArrayList(new TrigramIterator(trigrams.iterator(), best, false)),
					tagSet);
			FeatureDTreeNode node = new FeatureDTreeNode(
					parent,
					best,
					yesNode,
					noNode
			);
			yesNode.setParent(node);
			noNode.setParent(node);
			return node;
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
			double info = getIq(feature, allTrigrams);
			if(info < minInfo) {
				minInfo = info;
				best = feature;
			}
		}
		return best;
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



	public int prune(DTree dTree, double weightedInformationGainTh) {
		List<DTreeNode> allNodes = Lists.newArrayList(dTree.nodeIterator());
		int rem = 0;
		for(DTreeNode node: allNodes) {
			if(node instanceof FeatureDTreeNode) {
				FeatureDTreeNode fNode = (FeatureDTreeNode)node;
				if(fNode.getYes() instanceof LeafDTreeNode && fNode.getNo() instanceof LeafDTreeNode ) {
					TrainingProbaTable table1  = (TrainingProbaTable) ((LeafDTreeNode)fNode.getYes()).getTable();
					TrainingProbaTable table2 = (TrainingProbaTable)((LeafDTreeNode)fNode.getNo()).getTable();
					double g = Utils.getWeightedInformationGain(table1, table2);
					if(g < weightedInformationGainTh) {
						rem++;
						prune(fNode, table1, table2);
					}
				}
			}
		}

		if(rem > 0) {
			int recPrune = prune(dTree, weightedInformationGainTh);
			return rem + recPrune;
		}
		else
			return 0;
	}

	private void prune(FeatureDTreeNode fNode, TrainingProbaTable table1, TrainingProbaTable table2) {
		LeafDTreeNode node  = new LeafDTreeNode(
				fNode.getParent(),
				Utils.merge(table1, table2));
		if(fNode.getParent() != null)
			((FeatureDTreeNode)fNode.getParent()).replaceChild(fNode, node);
	}
}
