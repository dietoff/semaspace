package data;

public class nodeTuple {
	private Node nodeA;
	private Node nodeB;

	public nodeTuple(Node a, Node b) {
		setA(a);
		setB(b);
	}
	
	public static String getName(Node a, Node b) {
		return a.genId()+"&"+b.genId();
	}

	public void setA(Node nodeA) {
		this.nodeA = nodeA;
	}

	public Node getA() {
		return nodeA;
	}

	public void setB(Node nodeB) {
		this.nodeB = nodeB;
	}

	public Node getB() {
		return nodeB;
	}

	public String getName() {
		return nodeA.genId()+"&"+nodeB.genId();
	}
}
