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
import fr.dcram.treetaggerj.ptree.PrefixTreeNode;
import fr.dcram.treetaggerj.ptree.SuffixTree;
import fr.dcram.treetaggerj.trainer.utils.TrigramIterator;
import fr.dcram.treetaggerj.trainer.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fr.dcram.treetaggerj.trainer.utils.Utils.getIq;

public class Trainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Trainer.class);
	public static final String SEPARATOR = " ";
	public static final int RECURSION_FREQUENCY_THRESHOLD = 2;
	public static final int DTREE_WEIGHTED_INFORMATION_GAIN_TH = 5;
	public static final int STREE_WEIGHTED_INFORMATION_GAIN_TH = 10;
	public static final int SUFFIX_LENGTH = 3;
	public static final double LEXICON_FREQUENCY_THRESHOLD = .01d;

	public static void main(String[] args) throws IOException {
		String trainingCorpus = args[0];
		String outputModel = args[1];

		LOGGER.info("Training TreeTagger model.");
		LOGGER.info("Training corpus: {}", trainingCorpus);
		LOGGER.info("output model: {}", outputModel);

		TagSet tagSet = new TagSet();

		Trainer trainer = new Trainer(tagSet);
		Stopwatch sw = Stopwatch.createStarted();
		List<List<Token>> sequences = parseSequences(new FileReader(trainingCorpus), tagSet);
		sw.stop();
		LOGGER.info("Parsed {} sequences in {}ms", sequences.size(), sw.elapsed(TimeUnit.MILLISECONDS));
		sw = Stopwatch.createStarted();
		trainer.train(sequences);
		sw.stop();

		LOGGER.info("Trained model in {}sec", sw.elapsed(TimeUnit.SECONDS));

	}

	private TagSet tagSet;

	public Trainer(TagSet tagSet) {
		this.tagSet = tagSet;
	}

	public DTree train(List<List<Token>> sequences) {
		List<Feature> features = toFeatureSet(tagSet);
		LOGGER.info("Learning dTree");
		DTree dTree = learnDTree(sequences, features);

		LOGGER.info("Learning Lexicon");
		Lexicon lexicon = learnLexicon(sequences);

		return dTree;
	}

	private Lexicon learnLexicon(List<List<Token>> sequences) {
		LOGGER.info("Parsing full-form lexicon");
		SuffixTree suffixTree = new SuffixTree(new PrefixTreeNode<ProbaTable>());
		Map<String, ProbaTable> fullformLexicon = new HashMap<>();
		int nbTokens = 0;
		String text, suffix;
		for(List<Token> sequence:sequences) {
			for(Token tok:sequence) {
				nbTokens++;
				text = tok.getText();
				if(!fullformLexicon.containsKey(text))
					fullformLexicon.put(text, new TrainingProbaTable());
				((TrainingProbaTable)fullformLexicon.get(text)).add(tok.getTag());

				if(text.length() >= SUFFIX_LENGTH) {
					suffix = text.substring(Math.max(0, text.length()- SUFFIX_LENGTH)).toLowerCase();
					if(suffixTree.get(suffix) == null)
						suffixTree.add(suffix, new TrainingProbaTable());
					((TrainingProbaTable)suffixTree.get(suffix)).add(tok.getTag());
				}
			}
		}
		LOGGER.debug("Parsed full-form lexicon size: {}", fullformLexicon.size());
		int frequencyThreshold = (int)(LEXICON_FREQUENCY_THRESHOLD * nbTokens);
		for(Iterator<Map.Entry<String, ProbaTable>> it = fullformLexicon.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, ProbaTable> entry = it.next();
			if(entry.getValue().getTotalFrequency() <= frequencyThreshold)
				it.remove();
		}
		LOGGER.debug("Parsed full-form lexicon size after pruning: {}", fullformLexicon.size());
		logSuffixTree(suffixTree);
		LOGGER.info("Pruning suffix tree");
		int nbPruned = prune(suffixTree, STREE_WEIGHTED_INFORMATION_GAIN_TH);
		LOGGER.info("Pruned {} nodes", nbPruned);
		logSuffixTree(suffixTree);

		return new Lexicon(fullformLexicon, suffixTree);
	}

	private void logSuffixTree(SuffixTree suffixTree) {
		LOGGER.debug("Suffix tree nb nodes: {} (getAllNodes method: {})", suffixTree.getNbNodes(),suffixTree.getAllNodes().size());
		LOGGER.debug("Suffix tree nb leaves: {}", suffixTree.getNbLeaves());
		LOGGER.debug("Suffix tree depth: {}", suffixTree.getDepth());
	}

	private int prune(SuffixTree suffixTree, double th) {
		List<PrefixTreeNode<ProbaTable>> allNodes = suffixTree.getAllNodes();
		int rem = 0;
		for(PrefixTreeNode<ProbaTable> node:allNodes) {
			PrefixTreeNode<ProbaTable> parent = node.getParent();
			TrainingProbaTable table = (TrainingProbaTable)node.get();
			if(node.isLeaf()) {
				// G(aS) = F(aS)(I(S) - I(aS))
				double fas = table.getTotalFrequency();
				double ias = Utils.getI0((TrainingProbaTable)node.get());
				TrainingProbaTable parentTable = Utils.merge(parent.getAllValues());
				double is = Utils.getI0(parentTable);
				double gas = fas*(is-ias);
				if(gas < th)
					rem += removeChildFromParent(node, parent);
			}
		}

		return rem;
	}

	private int removeChildFromParent(PrefixTreeNode<ProbaTable> child, PrefixTreeNode<ProbaTable> parent) {
		TrainingProbaTable ptable = (TrainingProbaTable)parent.getOrCreate(() -> new TrainingProbaTable());
		ptable.merge((TrainingProbaTable) child.get());
		parent.removeChild(child);
		if(parent.getChildren().isEmpty() && parent.getParent() != null)
			return 1 + removeChildFromParent(parent, parent.getParent());
		else
			return 1;
	}

	private DTree learnDTree(List<List<Token>> sequences, List<Feature> features) {
		DTreeNode learnedRootNode = getDecisionTreeNode(null, features, toTrigrams(sequences, tagSet), tagSet);
		DTree dTree = new DTree(learnedRootNode);
		logDTree(dTree);

		LOGGER.info("Pruning dTree with threshold {} ", DTREE_WEIGHTED_INFORMATION_GAIN_TH);
		int nbPruned = prune(dTree, DTREE_WEIGHTED_INFORMATION_GAIN_TH);
		LOGGER.debug("Pruned {} nodes", nbPruned);
		logDTree(dTree);
		return dTree;
	}

	private void logDTree(DTree dTree) {
		LOGGER.debug("Nb nodes : {} (NodeIterator method: {})", dTree.getNbNodes(), Iterators.size(dTree.nodeIterator()));
		LOGGER.debug("Nb leaves: {} ", dTree.getLeaves());
		LOGGER.debug("Nb features nodes: {} ", dTree.getFNodes());
		LOGGER.debug("Depth: {} ", dTree.getDepth());
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

	public static List<List<Token>> parseSequences(Reader trainingCorpus, TagSet tagSet) throws IOException {
		List<List<Token>> sequences = new ArrayList<>();
		String text;
		BufferedReader br = new BufferedReader(trainingCorpus);
		LOGGER.debug("Starting training corpus parsing");
		while((text = br.readLine())!=null) {
			List<Token> tokens = Splitter.on(SEPARATOR).splitToList(text)
					.stream()
					.map(String::trim)
					.filter(str -> !str.isEmpty())
					.map(string -> Token.parse(string, tagSet))
					.collect(Collectors.toList());
			sequences.add(tokens);
		}
		return sequences;
	}

	public static List<Trigram> toTrigrams(List<List<Token>> sequences, TagSet tagSet) {
		List<Trigram> allTrigrams = new ArrayList<>();
		for(List<Token> sequence:sequences) {
			for(int i = 0; i < sequence.size(); i++) {
				Tag tag1 = (i < 2) ? tagSet.getStartTag() : sequence.get(i-2).getTag();
				Tag tag2 = (i < 1) ? tagSet.getStartTag() : sequence.get(i-1).getTag();
				Tag tag3 = sequence.get(i).getTag();
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
