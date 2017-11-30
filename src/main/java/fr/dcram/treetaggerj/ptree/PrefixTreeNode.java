package fr.dcram.treetaggerj.ptree;

import java.util.HashMap;
import java.util.Map;

public class PrefixTreeNode<T> {

	Map<Character, PrefixTreeNode<T>> children;
	PrefixTreeNode<T> parent;
	T value;

	public PrefixTreeNode() {
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

	public int getNbLeaves() {
		if(children == null) {
			return 1;
		}
		else {
			int nbLeaves = 0;
			for(Map.Entry<Character, PrefixTreeNode<T>> e:children.entrySet()) {
				nbLeaves+=e.getValue().getNbLeaves();
			}
			return nbLeaves;
		}
	}

	public int getNbNodes() {
		if(children == null) {
			return 1;
		}
		else {
			int nbChildNodes = 0;
			for(Map.Entry<Character, PrefixTreeNode<T>> e:children.entrySet()) {
				nbChildNodes+=e.getValue().getNbNodes();
			}
			return 1 + nbChildNodes;
		}
	}

	public int getDepth() {
		if(children == null) {
			return 1;
		}
		else {
			int depth = 0;
			for(Map.Entry<Character, PrefixTreeNode<T>> e:children.entrySet()) {
				depth= Math.max(e.getValue().getDepth(), depth);
			}
			return 1 + depth;
		}
	}
}
