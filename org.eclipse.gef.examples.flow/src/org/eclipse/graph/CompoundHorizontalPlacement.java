package org.eclipse.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hudsonr
 * Created on Jul 3, 2003
 */
public class CompoundHorizontalPlacement extends HorizontalPlacement {

class LeftRight {//$TODO Delete and use NodePair class, equivalent
	Object left, right;
	LeftRight(Object l, Object r) {
		left = l; right = r;
	}
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		LeftRight entry = (LeftRight)obj;
		return entry.left.equals(left) && entry.right.equals(right);
	}
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}
}

Set entries = new HashSet();

/**
 * @see org.eclipse.graph.HorizontalPlacement#applyGPrime()
 */
void applyGPrime() {
	super.applyGPrime();
	NodeList subgraphs = ((CompoundDirectedGraph)graph).subgraphs;
	for (int i = 0; i < subgraphs.size(); i++) {
		Subgraph s = (Subgraph)subgraphs.get(i);
		s.x = s.left.x;
		s.width = s.right.x + s.right.width - s.x;
	}
}

/**
 * @see org.eclipse.graph.HorizontalPlacement#buildRankSeparators(org.eclipse.graph.RankList)
 */
void buildRankSeparators(RankList ranks) {
	/*
	 * $TODO Width of a subgraph should be ensured by connecting left to right separated
	 * by delta equal to subgraph's width
	 */
	CompoundDirectedGraph g = (CompoundDirectedGraph)graph;
	
	Rank rank;
	for (int row = 0; row < g.ranks.size(); row++) {
		rank = g.ranks.getRank(row);
		Node n = null, prev = null;
		for (int j = 0; j < rank.size(); j++) {
			n = rank.getNode(j);
			if (prev == null) {
				addSeparatorsLeft(n, null);
			} else {
				//$TODO support nested virtual nodes.
				Subgraph s = GraphUtilities.getCommonAncestor((Node)prev,(Node) n);
				Node left = addSeparatorsRight(prev, s);
				Node right = addSeparatorsLeft(n, s);
				createEdge(left, right);
			}
			prev = n;
		}
		if (n != null)
			addSeparatorsRight(n, null);
	}
}

void createEdge(Node left, Node right) {
	LeftRight entry = new LeftRight(left, right);
	if (entries.contains(entry))
		return;
	entries.add(entry);
	int separation = left.width + Math.max(
		graph.getPadding(left).right,
		graph.getPadding(right).left);
	prime.edges.add(new Edge(
		getPrime(left), getPrime(right), separation, 0
	));
}

Node addSeparatorsLeft(Node n, Subgraph graph) {
	Subgraph parent = n.getParent();
	while (parent != graph && parent != null) {
		createEdge(getLeft(parent), n);
		n = parent.left;
		parent = parent.getParent();
	}
	return n;
}

Node addSeparatorsRight(Node n, Subgraph graph) {
	Subgraph parent = n.getParent();
	while (parent != graph && parent != null) {
		createEdge(n, getRight(parent));
		n = parent.right;
		parent = parent.getParent();
	}
	return n;
}

Node getLeft(Subgraph s) {
	if (s.left == null) {
		s.left = new SubgraphBoundary(s, graph.getPadding(s), 1);
		s.left.rank = (s.head.rank + s.tail.rank)/2;
		//$TODO this weight value is arbitrary
		prime.edges.add(new Edge(getPrime(s.left), getPrime(getRight(s)),0,1));
	}
	return s.left;
}

Node getRight(Subgraph s) {
	if (s.right == null) {
		s.right = new SubgraphBoundary(s, graph.getPadding(s), 3);
		s.right.rank = (s.head.rank + s.tail.rank)/2;
	}
	return s.right;
}

Node getPrime(Node n) {
	Node nPrime = get(n);
	if (nPrime == null) {
		nPrime = new Node(n);
		prime.nodes.add(nPrime);
		map(n, nPrime);
	}
	return nPrime;
}

public void visit(DirectedGraph g) {
	super.visit(g);
}

}
