package fr.dcram.treetaggerj.trainer;

import fr.dcram.treetaggerj.trainer.model.Word;
import fr.univnantes.lina.uima.engines.TreeTaggerWrapper;
import fr.univnantes.lina.uima.models.TreeTaggerParameter;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import uima.sandbox.lexer.engines.Lexer;
import uima.sandbox.lexer.resources.SegmentBank;
import uima.sandbox.lexer.resources.SegmentBankResource;

import java.nio.file.Paths;

public class EngineFactory {
	public static AnalysisEngineDescription createTokenizer(String lang) {
		try {
			AnalysisEngineDescription tokenizer = AnalysisEngineFactory.createEngineDescription(
					Lexer.class,
					Lexer.PARAM_TYPE, Word.class.getName()
			);

			ExternalResourceFactory.bindResource(
					tokenizer,
					SegmentBank.KEY_SEGMENT_BANK,
					ExternalResourceFactory.createExternalResourceDescription(
							SegmentBankResource.class,
							Paths.get("src", "train", "resources", lang, "segment-bank.xml").toUri().toURL()
					)
			);

			return tokenizer;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}


	public static AnalysisEngineDescription createTreeTagger(String lang) {
		try {
			AnalysisEngineDescription treeTaggerAE = AnalysisEngineFactory.createEngineDescription(
					TreeTaggerWrapper.class,
					TreeTaggerWrapper.PARAM_ANNOTATION_TYPE, Word.class.getName(),
					TreeTaggerWrapper.PARAM_TAG_FEATURE, "tag",
					TreeTaggerWrapper.PARAM_LEMMA_FEATURE, "lemma",
					TreeTaggerWrapper.PARAM_UPDATE_ANNOTATION_FEATURES, true,
					TreeTaggerWrapper.PARAM_TT_HOME_DIRECTORY, TrainerConfig.getTreeTaggerPath().toString()
			);

			ExternalResourceFactory.bindResource(
					treeTaggerAE,
					TreeTaggerParameter.KEY_TT_PARAMETER,
					ExternalResourceFactory.createExternalResourceDescription(
							TreeTaggerParameter.class,
							Paths.get("src", "train", "resources", lang, "treetagger.xml").toUri().toURL()
					)
			);


			return treeTaggerAE;
		} catch (Exception e) {
			throw new RuntimeException();

		}

	}

}
