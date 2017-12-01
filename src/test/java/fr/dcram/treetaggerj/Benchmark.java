package fr.dcram.treetaggerj;

import com.google.common.base.Stopwatch;
import fr.dcram.treetaggerj.trainer.model.Word;
import fr.dcram.treetaggerj.util.TreeTaggerModelIO;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uima.sandbox.lexer.engines.Lexer;
import uima.sandbox.lexer.resources.SegmentBank;
import uima.sandbox.lexer.resources.SegmentBankResource;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Benchmark {

	private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);

	public static void main(String[] args) throws IOException, UIMAException {
		String modelFile = args[0];
		String textFile = args[1];

		LOGGER.info("Loading model file: {}", modelFile);
		Stopwatch sw = Stopwatch.createStarted();
		TreeTaggerModel model = TreeTaggerModelIO.load(new FileReader(modelFile));
		sw.stop();
		LOGGER.info("Model loaded in {}ms", sw.elapsed(TimeUnit.MILLISECONDS));

		LOGGER.info("Loading token sequences: {}", textFile);
		sw = Stopwatch.createStarted();
		List<String> sentences = Files.readAllLines(Paths.get(textFile));
		List<List<String>> sequences = tokenize(sentences);
		sw.stop();
		LOGGER.info("{} sequences ({} tokens) loaded and tokenized in {}ms",
				sequences.size(),
				countTokens(sequences),
				sw.elapsed(TimeUnit.MILLISECONDS));

		LOGGER.info("Tagging {} sequences", sequences.size());
		sw = Stopwatch.createStarted();
		int cnt = 0;
		for(List<String> sequence:sequences) {
			Tagger tagger = TreeTaggerJ.getTagger(model);
			cnt ++;
			if(cnt%1000==0)
				LOGGER.info("Tagged {} sequences", cnt);
			for(String tok:sequence)
				tagger.tag(tok);
		}
		sw.stop();
		LOGGER.info("Tagged in {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
	}

	private static List<List<String>> tokenize(List<String> sentences) throws UIMAException, MalformedURLException {
		AnalysisEngineDescription tokenizer = AnalysisEngineFactory.createEngineDescription(
				Lexer.class,
				Lexer.PARAM_TYPE, Word.class.getName()
		);

		ExternalResourceFactory.bindResource(
				tokenizer,
				SegmentBank.KEY_SEGMENT_BANK,
				ExternalResourceFactory.createExternalResourceDescription(
						SegmentBankResource.class,
						Paths.get("src/train/resources/fr/segment-bank.xml").toUri().toURL()
				)
		);

		AnalysisEngine engine = UIMAFramework.produceAnalysisEngine(tokenizer);
		JCas jCas = JCasFactory.createJCas();
		List<List<String>> sequences = new ArrayList<>();
		for(String sentence:sentences) {
			jCas.reset();
			jCas.setDocumentText(sentence);
			engine.process(jCas);
			List<String> sequence = new ArrayList<>();
			FSIterator<Word> it = jCas.getAnnotationIndex(Word.class).iterator();
			while(it.hasNext())
				sequence.add(it.next().getCoveredText());
			sequences.add(sequence);
		}
		return sequences;
	}

	private static int countTokens(List<List<String>> tokens) {
		int cnt = 0;
		for(List<String> s:tokens)
			cnt += s.size();
		return cnt;

	}
}
