package fr.dcram.treetaggerj.dtree;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class NodeIterator implements Iterator<DTreeNode> {

	public NodeIterator(DTreeNode root) {
		this.next = new LinkedList<>();
		this.next.add(root);
		doNext();
	}

	private void doNext() {
		DTreeNode node = next.peek();
		if(node instanceof FeatureDTreeNode) {
			FeatureDTreeNode fNode = (FeatureDTreeNode )node;
			next.add(fNode.getYes());
			next.add(fNode.getNo());
		}
	}

	private Deque<DTreeNode> next;


	@Override
	public boolean hasNext() {
		return !next.isEmpty();
	}

	@Override
	public DTreeNode next() {
		DTreeNode retVal = next.poll();
		doNext();
		return retVal;
	}
}
