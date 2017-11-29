package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.trainer.model.Word;
import fr.dcram.treetaggerj.trainer.utils.Utils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TreeTaggerTagGetter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeTaggerTagGetter.class);

	public static final String WORD_TAG = "%s_%s";
	public static final String WHITESPACE = " ";
	private AnalysisEngine tokenizer;
	private JCas cas;

	public TreeTaggerTagGetter(String lang) {
		try {
			tokenizer = UIMAFramework.produceAnalysisEngine(
					AnalysisEngineFactory.createEngineDescription(
					EngineFactory.createTokenizer(lang),
					EngineFactory.createTreeTagger(lang))
			);
			cas = JCasFactory.createJCas();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void tokenizeAndTag(String text, Writer toWriter) {
		try {
			cas.reset();
			cas.setDocumentText(text);
			tokenizer.process(cas);
			FSIterator<Word> it = cas.getAnnotationIndex(Word.class).iterator();
			while(it.hasNext()) {
				Word w = it.next();
				toWriter.append(String.format(WORD_TAG, w.getCoveredText(), w.getTag()));
				toWriter.append(WHITESPACE);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static void main(String[] args) throws IOException {
		String lang = args[0];
		String src = args[1];
		String target = args[2];

		LOGGER.info("Tagging input text");
		LOGGER.info("lang: {}", lang);
		LOGGER.info("srcFile: {}", src);
		LOGGER.info("target: {}", target);

		int totalLine = Utils.countLines(src);
		FileWriter writer = new FileWriter(target);
		BufferedReader br = new BufferedReader(new FileReader(src));

		String text;

		TreeTaggerTagGetter treeTaggerTagGetter = new TreeTaggerTagGetter(lang);
		int i = 0;
		LOGGER.info("Starts reading text file ", src);
		while((text = br.readLine())!=null) {
			i++;
			if(i%100 == 0)
				LOGGER.info("Reading line {}/{}", i, totalLine);
			treeTaggerTagGetter.tokenizeAndTag(text, writer);
			String LINE_SEP = "\n";
			writer.append(LINE_SEP);
		}
		writer.flush();
		br.close();
		writer.close();
	}

}
