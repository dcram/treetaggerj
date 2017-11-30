package fr.dcram.treetaggerj.ptree;

import fr.dcram.treetaggerj.model.ProbaTable;

import java.util.*;
import java.util.function.Supplier;

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


	public T get() {
		return value;
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

	public List<PrefixTreeNode<T>> getNodes() {
		ArrayList<PrefixTreeNode<T>> thisAsList = new ArrayList<>();
		thisAsList.add(this);
		if(children != null)
			for(Map.Entry<Character, PrefixTreeNode<T>> e:children.entrySet())
				thisAsList.addAll(e.getValue().getNodes());
		return thisAsList;
	}

	public boolean isLeaf() {
		return children == null;
	}

	public PrefixTreeNode<T> getParent() {
		return parent;
	}

	public List<T> getAllValues() {
		List<T> values = new ArrayList<>();
		if(this.value != null)
			values.add(this.value);
		if(children != null)
			for(PrefixTreeNode<T> c:children.values())
				values.addAll(c.getAllValues());
		return values;
	}

	public T getOrCreate(Supplier<T> supplier) {
		if(value == null)
			value = supplier.get();
		return value;
	}

	public void removeChild(PrefixTreeNode<T> node) {
		if(children == null)
			throw new IllegalStateException("Cannot remove child from a null children map");
		Optional<Map.Entry<Character, PrefixTreeNode<T>>> childEntry = children.entrySet()
				.stream()
				.filter(e -> e.getValue() == node)
				.findFirst();
		if(!childEntry.isPresent())
			throw new IllegalStateException("No such child for current node");
		else {
			children.remove(childEntry.get().getKey());
		}
	}

	public Map<Character, PrefixTreeNode<T>> getChildren() {
		return children;
	}
}
