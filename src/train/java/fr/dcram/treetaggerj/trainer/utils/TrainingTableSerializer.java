package fr.dcram.treetaggerj.trainer.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.dcram.treetaggerj.trainer.TrainingProbaTable;

import java.io.IOException;

public class TrainingTableSerializer extends StdSerializer<TrainingProbaTable> {

	protected TrainingTableSerializer() {
		super(TrainingProbaTable.class);
	}

	protected TrainingTableSerializer(Class<TrainingProbaTable> t) {
		super(t);
	}

	@Override
	public void serialize(TrainingProbaTable value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartObject();
		jgen.writeNumberField("total", value.getTotalFrequency());
		jgen.writeFieldName("tags");
		jgen.writeStartObject();
		value.getMap().entrySet().stream()
				.sorted((e1,e2)->Integer.compare(e2.getValue(), e1.getValue()))
				.forEach(e -> {
					try {
						jgen.writeNumberField(e.getKey().getLabel(), e.getValue());
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				});
		jgen.writeEndObject();
		jgen.writeEndObject();
	}
}
