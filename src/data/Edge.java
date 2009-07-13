/**
 * 
 */
package data;

import java.lang.Math;
import java.util.ArrayList;
import javax.media.opengl.GL;

import semaGL.Func;
import semaGL.SemaParameters;
import semaGL.SemaSpace;


public class Edge extends GraphElement{
	private Node a;
	private Node b;
	ArrayList<String> text;
	ArrayList<Integer> ages;
	public boolean isPartofTriangle = false;
	private boolean picked = false;
	private float property = -1f;

	Edge(SemaParameters app_, Node a_, Node b_)  {
		this(app_,a_,b_,"");
	}

	public Edge(SemaParameters app, Node a_, Node b_, String id) {
		super(app);
		app = app;
		a = a_;	
		b = b_;
		name = Edge.edgeName(a,b,id);
		defaultcolor = app.edgeColor.clone();
		setName(name);
	}

	public void chain(float d, float att) {
		//		d*=2f;
		Vector3D D = a.pos.copy();
		D.sub(b.pos);
		float faktor =  d-D.magnitude();
		if (isPartofTriangle) d/=2f;
		if (Math.abs(faktor) > 0.1) {
			Vector3D DN= D.copy();
			DN.normalize();
			DN.mult(faktor*att/2);
			a.pos.add(DN);
			b.pos.sub(DN);
		}
	}

	public void chainA(float d, float att) {
		//d*=2f;
		Vector3D D = a.pos.copy();
		D.sub(b.pos);
		float faktor =  d-D.magnitude();
		if (isPartofTriangle) d/=2f;
		if (Math.abs(faktor) > 0.1) {
			Vector3D DN= D.copy();
			DN.normalize();
			DN.mult(faktor*att/2);
			if (!a.isLocked()) a.pos.add(DN);
			DN.mult(0.1f);
			if (!b.isLocked()) b.pos.sub(DN);
		}
	}
	public void chainB(float d, float att) {
		//d*=2f;
		Vector3D D = a.pos.copy();
		D.sub(b.pos);
		float faktor =  d-D.magnitude();
		if (isPartofTriangle) d/=2f;
		if (Math.abs(faktor) > 0.1) {
			Vector3D DN= D.copy();
			DN.normalize();
			DN.mult(faktor*att/2);
			if (!b.isLocked()) b.pos.sub(DN);
			DN.mult(0.1f);
			if (!a.isLocked()) a.pos.add(DN);
		}
	}

	public Node getA() {
		return a;
	}

	public Node getB() {
		return b;
	}

	void addText(String comment) {
		text.add(comment);
	}

	// this is for textual comments
	public void addComment(int age, String comment) {
		if (ages==null) {
			ages = new ArrayList<Integer>();
			text = new ArrayList<String>();
		}
		if (!ages.contains(age)){
			ages.add(age);
			addText(comment);
		}
	}

	// this is for creating colored comment stripes
	public void addComment(int age, float[] col) {
		if (ages==null) {
			ages = new ArrayList<Integer>();
			text = new ArrayList<String>();
		}
		int rgb = Func.packColors( col);
		ages.add(age);
		addText(Integer.toString(rgb));
	}

	public void setTriangle(boolean c) {
		isPartofTriangle  = c;
	}
	void delComments(){
		ages=null;
		text=null;
	}
	public boolean contains(Node n){
		return (a==n||b==n);
	}

	public static String edgeName(Node node1, Node node2) {
		return edgeName( node1,  node2, "");
	}
	
	static String edgeName(Node node1, Node node2, String id) {
		return node1.name+"@"+node2.name+"@"+id;
	}
	
	public void setPicked(boolean picked) {
		this.picked = picked;
	}

	public boolean isPicked() {
		return picked;
	}

	public float getProperty() {
		return property;
	}

	public void setProperty(float property) {
		this.property = property;
	}

	public void setA(Node n) {
		a = n;
	}
	public void setB(Node n) {
		b = n;
	}
}