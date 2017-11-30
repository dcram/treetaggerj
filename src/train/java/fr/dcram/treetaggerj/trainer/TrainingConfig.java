package fr.dcram.treetaggerj.trainer;

public class TrainingConfig {
	public static final int RECURSION_FREQUENCY_THRESHOLD = 2;
	public static final int DTREE_WEIGHTED_INFORMATION_GAIN_TH = 5;
	public static final int STREE_WEIGHTED_INFORMATION_GAIN_TH = 10;
	public static final int SUFFIX_LENGTH = 5;
	public static final double LEXICON_TEXT_TAG_THRESHOLD = .01d;
	public static final int LEXICON_ABSOLUTE_FREQUENCY_THRESHOLD = 1;
	public static final int LEXICON_FULLFORM_MAX_SIZE = 10000;

	private int recursionFrequencyThreshold = RECURSION_FREQUENCY_THRESHOLD;
	private int dTreeWeightedInformationGainTh = DTREE_WEIGHTED_INFORMATION_GAIN_TH;
	private int sTreeWeightedInformationGainTh = STREE_WEIGHTED_INFORMATION_GAIN_TH;
	private int suffixLength = SUFFIX_LENGTH;
	private double lexiconTextTagTh = LEXICON_TEXT_TAG_THRESHOLD;
	private int lexiconAbsoluteFrequencyTh = LEXICON_ABSOLUTE_FREQUENCY_THRESHOLD;
	private int lexiconFullformMaxSize = LEXICON_FULLFORM_MAX_SIZE;


	public int getRecursionFrequencyTh() {
		return recursionFrequencyThreshold;
	}

	public void setRecursionFrequencyThreshold(int recursionFrequencyThreshold) {
		this.recursionFrequencyThreshold = recursionFrequencyThreshold;
	}

	public int getdTreeWeightedInformationGainTh() {
		return dTreeWeightedInformationGainTh;
	}

	public void setdTreeWeightedInformationGainTh(int dTreeWeightedInformationGainTh) {
		this.dTreeWeightedInformationGainTh = dTreeWeightedInformationGainTh;
	}

	public int getsTreeWeightedInformationGainTh() {
		return sTreeWeightedInformationGainTh;
	}

	public void setsTreeWeightedInformationGainTh(int sTreeWeightedInformationGainTh) {
		this.sTreeWeightedInformationGainTh = sTreeWeightedInformationGainTh;
	}

	public int getSuffixLength() {
		return suffixLength;
	}

	public void setSuffixLength(int suffixLength) {
		this.suffixLength = suffixLength;
	}

	public double getLexiconTextTagTh() {
		return lexiconTextTagTh;
	}

	public void setLexiconTextTagTh(double lexiconTextTagTh) {
		this.lexiconTextTagTh = lexiconTextTagTh;
	}

	public int getLexiconAbsoluteFrequencyTh() {
		return lexiconAbsoluteFrequencyTh;
	}

	public void setLexiconAbsoluteFrequencyTh(int lexiconAbsoluteFrequencyTh) {
		this.lexiconAbsoluteFrequencyTh = lexiconAbsoluteFrequencyTh;
	}

	public int getLexiconFullformMaxSize() {
		return lexiconFullformMaxSize;
	}

	public void setLexiconFullformMaxSize(int lexiconFullformMaxSize) {
		this.lexiconFullformMaxSize = lexiconFullformMaxSize;
	}
}
