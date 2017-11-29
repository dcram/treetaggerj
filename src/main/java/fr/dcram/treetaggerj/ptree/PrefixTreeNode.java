package fr.dcram.treetaggerj.ptree;

import java.util.HashMap;
import java.util.Map;

public class PrefixTreeNode<T> {

	Map<Character, PrefixTreeNode<T>> children;
	PrefixTreeNode<T> parent;
	T value;

	PrefixTreeNode() {
		// root not constructor
		this.parent = null;
	}

	public PrefixTreeNode(PrefixTreeNode<T> parent) {
		this.parent = parent;
	}

	public T get(String string) {
		if(string.isEmpty())
			return value;
		else {
			if(value != null)
				return value;
			else if(children == null)
				return null;
			else if(!children.containsKey(string.charAt(0)))
				return null;
			else
				return children.get(string.charAt(0)).get(string.substring(1));
		}
	}

	public void add(String prefix, T value) {
		if(prefix.length() == 0) {
			if(this.value != null)
				throw new IllegalArgumentException("There is already a va for given prefix");
			else
				this.value = value;
		} else {
			PrefixTreeNode<T> child;
			if(children == null)
				children = new HashMap<>();
			if(children.containsKey(prefix.charAt(0)))
				child = children.get(prefix.charAt(0));
			else {
				child = new PrefixTreeNode<T>(this);
				children.put(prefix.charAt(0), child);
			}
			child.add(prefix.substring(1), value);
		}
	}
}
