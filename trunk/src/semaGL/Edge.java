/**
 * 
 */
package semaGL;

import java.lang.Math;
import java.util.ArrayList;
import javax.media.opengl.GL;


class Edge extends GraphElement{
	private Node a;
	private Node b;
	ArrayList<String> text;
	ArrayList<Integer> ages;
	private String dispText;
	private boolean isPartofTriangle = false;
	private boolean isColor = false;
	private boolean picked = false;
	private float property = -1f;
	private String edgeid;

	Edge(SemaSpace app_, Node a_, Node b_)  {
		this(app_,a_,b_,"");
	}

	public Edge(SemaSpace app_, Node a_, Node b_, String id) {
		super(app_);
		edgeid = id;
		app = app_;
		a = a_;	
		b = b_;
		name = Edge.edgeName(a,b,id);
		defaultcolor = app.edgeColor.clone();
		setName(name);
	}

	void chain(float d, float att) {
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

	void chainA(float d, float att) {
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
	void chainB(float d, float att) {
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

	@Override
	public synchronized void render(GL gl) {
		
		float af = a.getSize(); //length of "arrowheads"
		float bf = b.getSize();

		Vector3D D = b.pos.copy();
		D.sub(a.pos); //direction of the edge
		Vector3D midP = D.copy();
		midP.mult(0.5f);
		midP.add(a.pos);
		Vector3D DN= D.copy();
		DN.normalize();

		Vector3D start = a.pos.copy();
		Vector3D end = b.pos.copy();
		start.add(DN.mult(DN, af));
		end.sub(DN.mult(DN, bf));

		gl.glPushMatrix();
		gl.glLoadName(id);

		//draw edge
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		if  (isPartofTriangle){
			gl.glEnable(GL.GL_LINE_STIPPLE);
			gl.glLineStipple (5, (short)0xAAAA);
		}

		//edge or nodes picked: 
		if (isPicked()||(a.getPickColor()[3]>0||b.getPickColor()[3]>0)||rollover)
		{
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(2);
			if (isPicked()) 
				FuncGL.drawLine(gl, start, end,app.pickColor,app.pickColor);
			else if (rollover) 
				FuncGL.drawLine(gl, start, end,app.rollOverColor,app.rollOverColor);
			else 
				FuncGL.drawLine(gl, start, end,a.getPickColor(),b.getPickColor());
		} 
		else 
		{
			// draw actual edge
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(app.edgewidth);
			gl.glBegin(GL.GL_LINES);
			float[] aCol = color.clone();
			float[] bCol = color.clone();

			if (colored) {
				bCol[3]=1f;
				aCol[3]=1f;
			} else {
				aCol[3] = a.alpha;
				bCol[3] = b.alpha;
			}
			
			if (app.inheritEdgeColorFromNodes) {
				aCol = a.color.clone();
				bCol = b.color.clone();
			}
			
			FuncGL.drawLine(gl, start, end, aCol, bCol);
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}

		// draw property vector 
		if (property!=-1) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.propertyVector(gl, property, 3f, end, DN);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			FuncGL.propertyVector(gl, property, 3f, end, DN);
		}

		//draw arrowhead
		if (app.directed&&!b.adList.contains(a)){
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.arrowHeadEmpty(gl,20,end,DN);
//			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//			FuncGL.arrowHead(gl,20,end,DN);
		}
		gl.glPopMatrix();
	}

	void renderLabels(GL gl, int font) {
		float[] textcolor = {color[0]/2f, color[1]/2f, color[2]/2f, 1};
		if (!picked&&(!attributes.containsKey(app.getAttribute())||color[3]<0.2f)&&!rollover) return;
		if ((picked||rollover)&&font==3) font=2;
		Vector3D midP = b.pos.copy();
		midP.sub(a.pos); //direction of the edge
		midP.mult(0.5f);
		midP.add(a.pos);
		float distToCam = app.cam.distToCam(midP);
		if (distToCam>2000) return; 

		String rText = genTextSelAttributes();
		gl.glPushMatrix();
		float xRot = app.cam.getYRot();		//billboard; should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(midP.x,midP.y,midP.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2&&app.tilt) gl.glRotatef(25, 0, 0, 1);
		FuncGL.renderText(app, rText, textcolor,0, font, getId(), distToCam); //render text in dark grey, with alpha of edge
		gl.glPopMatrix();
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
		int rgb = Func.packColors(age, col);
		ages.add(age);
		addText(Integer.toString(rgb));
		isColor  = true;
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

	static String edgeName(Node node1, Node node2) {
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
}