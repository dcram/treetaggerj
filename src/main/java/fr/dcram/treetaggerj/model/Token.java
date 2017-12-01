package fr.dcram.treetaggerj.model;

public class Token {

	public static final String TOKEN_FORMAT = "%s_%s";
	public static final String SEP = "_";
	String text;
	Tag tag;


	public Token(String text) {
		this.text = text;
	}

	public Token(String s, Tag tag) {
		this(s);
		this.tag = tag;
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

	public static Token parse(String string, TagSet tagSet) {
		int sep = string.lastIndexOf(SEP);
		String text = string.substring(0, sep);
		Tag tag = tagSet.getTagCreateIfNull(string.substring(sep+1,string.length()));
		Token token = new Token(text);
		token.setTag(tag);
		return token;
	}
}
