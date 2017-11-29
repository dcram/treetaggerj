package fr.dcram.treetaggerj.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TagSet {
	private Tag defaultTag;
	private Tag notAWord;
	private Map<String, Tag> tags = new HashMap<>();

	private AtomicInteger id = new AtomicInteger(0);

	public TagSet() {
		this("DefaultTag", "NotAWord");
	}

	public TagSet(String defaultTagLabel, String notAWordLabel) {
		this.notAWord = addTag(notAWordLabel);
		this.defaultTag = addTag(defaultTagLabel);
	}

	public Tag addTag(String label) {
		if(tags.containsKey(label))
			throw new IllegalArgumentException(String.format("Tag %s already in tagset", label));
		else
			return tags.put(label, new Tag(id.getAndIncrement(), label));
	}

	public Tag getDefaultTag() {
		return defaultTag;
	}

	public Tag getNotAWord() {
		return notAWord;
	}

	public Tag getTag(String tagLabel) {
		return  tags.get(tagLabel);
	}
}
