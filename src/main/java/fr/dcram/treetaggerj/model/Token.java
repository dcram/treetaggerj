package fr.dcram.treetaggerj.model;

public class Token {

	public static final String TOKEN_FORMAT = "%s_%s";
	String text;
	Tag tag;

	public Token(String text) {
		this.text = text;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getText() {
		return text;
	}

	public Tag getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return String.format(TOKEN_FORMAT, text, tag);
	}
}
