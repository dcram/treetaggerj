package fr.dcram.treetaggerj.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.dcram.treetaggerj.dtree.DTree;
import fr.dcram.treetaggerj.dtree.DTreeNode;
import fr.dcram.treetaggerj.dtree.FeatureDTreeNode;
import fr.dcram.treetaggerj.dtree.LeafDTreeNode;
import fr.dcram.treetaggerj.model.Feature;
import fr.dcram.treetaggerj.model.ProbaTable;
import fr.dcram.treetaggerj.model.TagSet;
import fr.dcram.treetaggerj.model.TaggingProbaTable;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.IOException;

public class DTreeDeserializer extends StdDeserializer<DTree> {

	public static final String TEST = "test";
	public static final String ROOT = "root";
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String TABLE = "table";
	FeatureSet featureSet;
	ProbaTableDeserializer tableDeserializer;
	public DTreeDeserializer(FeatureSet featureSet, ProbaTableDeserializer tableDeserializer) {
		super(DTree.class);
		this.featureSet = featureSet;
		this.tableDeserializer = tableDeserializer;
	}

	@Override
	public DTree deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		DTree dTree = null;
		// useful var for debug
		JsonToken tok;

		String fieldname;
		while ((tok = p.nextToken()) != JsonToken.END_OBJECT) {
			fieldname = p.getCurrentName();
			if (ROOT.equals(fieldname)) {
				DTreeNode root = readDTreeNode(p, ctxt);
				dTree = new DTree(root);
			}

		}
		return dTree;
	}

	private DTreeNode readDTreeNode(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonToken tok;
		DTreeNode node = null;
		String fieldname;
		DTreeNode yes = null, no = null;
		Feature test = null;
		ProbaTable table = null;
		p.nextToken();
		while ((tok = p.nextToken()) != JsonToken.END_OBJECT) {
			fieldname = p.getCurrentName();
			if (TEST.equals(fieldname)) {
				test = featureSet.getFeature(p.nextTextValue());
			} else if (YES.equals(fieldname)) {
				yes = readDTreeNode(p, ctxt);
			} else if (NO.equals(fieldname)) {
				no = readDTreeNode(p, ctxt);
			} else if (TABLE.equals(fieldname))
				table = tableDeserializer.deserialize(p, ctxt);
		}
		if(test != null) {
			node = new FeatureDTreeNode(test, yes, no);
			yes.setParent(node);
			no.setParent(node);
		}
		else
			node = new LeafDTreeNode(table);

		return node;
	}
}
