package fr.dcram.treetaggerj.trainer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class TrainerConfig {
	public static Properties getConfig() {
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(Paths.get("src/train/resources/trainer.properties").toFile()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return prop;
	}

	public static String getTreeTaggerPath() {
		return getConfig().getProperty("treetagger.home");
	}


}
