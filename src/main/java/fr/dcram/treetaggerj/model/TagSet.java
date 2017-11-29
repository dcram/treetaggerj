package fr.dcram.treetaggerj.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TagSet {
	private Tag defaultTag;
	private Tag startTag;
	private Map<String, Tag> tags = new HashMap<>();

	private AtomicInteger id = new AtomicInteger(0);

	public TagSet() {
		this("DefaultTag", "START");
	}

	public TagSet(String defaultTagLabel, String startLabel) {
		this.startTag = addTag(startLabel);
		this.defaultTag = addTag(defaultTagLabel);
	}

	public Tag addTag(String label) {
		if(tags.containsKey(label))
			throw new IllegalArgumentException(String.format("Tag %s already in tagset", label));
		else {
			Tag tag = new Tag(id.getAndIncrement(), label);
			tags.put(label, tag);
			return tag;
		}
	}

	public Tag getDefaultTag() {
		return defaultTag;
	}

	public Tag getStartTag() {
		return startTag;
	}

	public Tag getTagCreateIfNull(String tagLabel) {
		if(!tags.containsKey(tagLabel))
			addTag(tagLabel);
		return  getTag(tagLabel);
	}

	public Tag getTag(String tagLabel) {
		return  tags.get(tagLabel);
	}

	public int size() {
		return tags.size();
	}

	public Collection<Tag> getTags() {
		return tags.values();
	}

	public Collection<Tag> getTagsButDefault() {
		List<Tag> tags = new ArrayList<>();
		tags.addAll(getTags());
		tags.remove(getDefaultTag());
		return tags;
	}
}
