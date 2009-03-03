package semaGL;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;


import data.*;

public class GraphRenderer {
	private SemaSpace app;
	private GLU glu;
	private double[] projection= new double[16];
	private double[] model = new double[16];
	private int[] view = new int[16];


	public GraphRenderer (SemaSpace app_){
		glu = new GLU();
		app=app_;
	}

	synchronized void renderNode(GL gl, Node n) {
		if (app.flat&&outsideView(n)) return;

		if (n.newTex=true&&n.tex!=null){
			FuncGL.initGLTexture(gl,n.tex, n.textures);
			n.newTex = false;
			n.tex=null;
		}
		gl.glPushMatrix();
		gl.glLoadName(n.id);

		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		float size = n.size();
		if (n.textures[0]!=0) size*=app.picSize;
		//		if (colored) size*=2;
		//draw node
		gl.glPushMatrix();
		gl.glScalef(size, size, size);

		gl.glColor4fv(n.color,0);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
		gl.glLineWidth(1f);
		FuncGL.quad(gl);

		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		//			gl.glColor4fv(pickColor,0); //pick color

		// textures
		if (n.textures[0]!=0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, n.textures[0]);
			gl.glColor4f(1f,1f,1f,n.color[3]);
			//				gl.glColor4f(1f,1f,1f,pickColor[3]); //pick color 
		}

		//split quad or solid quad
		if (n.color2 !=null) {
			FuncGL.triangle1(gl);
			gl.glColor4fv(n.color2,0);
			FuncGL.triangle2(gl);
		} else
			FuncGL.quad(gl);

		//pick frame
		if (n.pickColor[3]>0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
			gl.glLineWidth(2.5f);
			gl.glColor4fv(n.pickColor,0);
			FuncGL.quad(gl);	
		}

		if (n.isLocked()) {
			gl.glPushMatrix();
			gl.glLineWidth(1.5f);
			gl.glTranslatef(0, 1, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0,0,0);
			gl.glVertex3f(0,1,0);
			gl.glEnd();
			gl.glPopMatrix();
		}

		//hilight frame
		if (n.isFrame()) {
			gl.glColor4fv(app.frameColor,0);
			drawFrame(gl);
		}

		//rollover frame
		if (n.rollover) {
			gl.glColor4fv(app.rollOverColor,0);
			drawFrame(gl);
		}

		// reset scale transformations
		gl.glPopMatrix();

		// reset all transformations
		gl.glPopMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		//		double[] ps = project2screen(gl, n.pos);
		//		System.out.println((int)ps[0]+","+(int)ps[1]);
	}

	public double[] project2screen(GL gl, Vector3D pos) {
		gl.glGetIntegerv(GL.GL_VIEWPORT, view,0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection,0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model,0);
		double[] winPos = new double[3];
		glu.gluUnProject(pos.x, pos.y, pos.z, model, 0, projection, 0, view, 0, winPos,0);
		//		System.out.println(winPos[0]+","+winPos[1]);
		return winPos;
	}

	public synchronized void renderNodeLabels(GL gl, Node n, int font, boolean fast){
		if (app.flat&&outsideView(n)) return;

		float distToCam = app.cam.distToCam(n.pos);
		String att="";
		float[] textcolor = {n.color[0]/2f, n.color[1]/2f, n.color[2]/2f, 1};
		if (n.rollover) {
			att= n.genTextAttributeList();
			if (font==3) font=2;
		}
		else {
			if ((n.pickColor[3]==0&&(n.alpha<0.2f)||font==3)) return;
			if (distToCam>app.maxLabelRenderDistance) return; 
			att = n.genTextSelAttributes();
		}
		n.textColor[3]=n.alpha;

		gl.glPushMatrix();
		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);

		float fsize = app.getLabelsize()+n.size()*app.getLabelVar();
		if (font<2){

			if (app.isTree()) {
				float angle = (float) ((Math.atan(n.pos.y/n.pos.x))/(2*Math.PI)*360f);

				gl.glRotatef(angle, 0, 0, 1);
				if (n.pos.x<0) {
					String[] split = att.split("\n");
					float advance=0;
					if (font==0)
						advance = -app.texturefont.advance(split[0])*fsize*0.025f-n.size()-25f;
					else 
						advance = -FuncGL.stringlength(app, split[0])*fsize*0.01f-n.size()-25f;

					gl.glTranslatef(advance, 0, 0);
				} else	gl.glTranslatef(n.size(), 0, 0);
			} else
			{
				if (app.tilt) gl.glRotatef(25, 0, 0, 1);
			}
		}

		FuncGL.renderText(app, att, textcolor, fsize, font, n.id, distToCam, false, fast); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}

	public synchronized void renderGroupLabels(GL gl, Node n, int font){

		if (app.flat&&outsideView(n)) return;

		float distToCam = app.cam.distToCam(n.pos);
		//		float[] textcolor = {n.color[0]/2f, n.color[1]/2f, n.color[2]/2f, 1};
		float[] textcolor = GraphElement.colorFunction(n.name);
		n.textColor[3]=1;
		gl.glPushMatrix();
		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2&&app.tilt) gl.glRotatef(25, 0, 0, 1);
		FuncGL.renderText(app, n.name, textcolor, 1.5f*app.getLabelsize()+n.size()*app.getLabelVar(), font, n.id, distToCam, true, false); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}

	synchronized void renderEdges(GL gl, Edge e){
		e.genColorFromAtt();

		Node a = e.getA();
		Node b = e.getB();
		float af = a.size(); //length of "arrowheads"
		float bf = b.size();

		Vector3D D = Vector3D.sub(b.pos, a.pos);
		Vector3D DN= D.copy();
		DN.normalize();

		Vector3D start = a.pos.copy();
		Vector3D end = b.pos.copy();
		start.add(Vector3D.mult(DN, af));
		end.sub(Vector3D.mult(DN, bf));

		gl.glPushMatrix();
		gl.glLoadName(e.id);

		//draw edge
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		if  (e.isPartofTriangle){
			gl.glEnable(GL.GL_LINE_STIPPLE);
			gl.glLineStipple (5, (short)0xAAAA);
		}

		//edge or nodes picked: 
		if (e.isPicked()||(a.getPickColor()[3]>0||b.getPickColor()[3]>0)||e.rollover||e.isFrame())
		{
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(2.5f);
			if (e.isPicked()) 
				FuncGL.drawLine(gl, start, end,app.pickGradStart,app.pickGradStart);
			if (e.rollover) 
				FuncGL.drawLine(gl, start, end,app.rollOverColor,app.rollOverColor);
			if (e.isFrame())
				FuncGL.drawLine(gl, start, end,app.frameColor,app.frameColor);

			FuncGL.drawLine(gl, start, end,a.getPickColor(),b.getPickColor());
		} 
		else 
		{
			// draw actual edge
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(app.edgewidth);
			gl.glBegin(GL.GL_LINES);
			float[] aCol = e.color.clone();
			float[] bCol = e.color.clone();

			if (e.colored) {
				bCol[3]=1f;
				aCol[3]=1f;
			} else {
				float f = app.edgeAlpha;
				aCol[3] = e.getA().alpha*f;
				bCol[3] = e.getB().alpha*f;
			}

			if (app.inheritEdgeColorFromNodes) {
				aCol = e.getA().color.clone();
				bCol = e.getB().color.clone();
			}

			FuncGL.drawLine(gl, start, end, aCol, bCol);
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}

		// draw property vector 
		if (e.getProperty()!=-1) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.propertyVector(gl, e.getProperty(), 3f, end, DN);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			FuncGL.propertyVector(gl, e.getProperty(), 3f, end, DN);
		}

		//draw arrowhead
		if (app.directed&&!e.getB().adList.contains(e.getA())){
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.arrowHeadEmpty(gl,20,end,DN);
			//			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			//			FuncGL.arrowHead(gl,20,end,DN);
		}
		gl.glPopMatrix();
	}
	synchronized void renderEdgeLabels(GL gl, Edge e, int Text, boolean fast) {
		float[] color = e.color;
		int font = Text;
		Node a = e.getA();
		Node b = e.getB();

		float[] textcolor = {color [0]/2f, color[1]/2f, color[2]/2f, 1};
		if (!e.isPicked()&&(!e.attributes.containsKey(app.getAttribute())||color[3]<0.2f)&&!e.rollover) return;
		if ((e.isPicked()||e.rollover)&&font==3) font=2;
		Vector3D midP = b.pos.copy();
		midP.sub(a.pos); //direction of the edge
		midP.mult(0.5f);
		midP.add(a.pos);
		float distToCam = app.cam.distToCam(midP);
		if (distToCam>app.maxLabelRenderDistance) return; 

		String rText = e.genTextSelAttributes();
		gl.glPushMatrix();
		float xRot = app.cam.getYRot();		//billboard; should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(midP.x,midP.y,midP.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2&&app.tilt) gl.glRotatef(25, 0, 0, 1);
		FuncGL.renderText(app, rText, textcolor,app.getLabelsize(), font, e.getId(), distToCam, false, fast); //render text in dark grey, with alpha of edge
		gl.glPopMatrix();
	}
	synchronized void renderFan(GL gl, HashSet<Node> nodes, Node center) {
		float[] col = Func.parseColorInt(center.name.hashCode()+"");
		col[3]=Math.min(center.alpha, 0.1f);
		gl.glColor4fv(col, 0);
		Node tmp=null;
		int jcount=0;
		gl.glPushMatrix();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3f(center.pos.x, center.pos.y, center.pos.z);
		for (Node bref : nodes){
			if (bref != center) {
				if (jcount==0) tmp=bref;
				gl.glVertex3f(bref.pos.x, bref.pos.y, bref.pos.z);
				jcount++;
			}
		}
		gl.glVertex3f(tmp.pos.x, tmp.pos.y, tmp.pos.z);
		gl.glEnd();
		gl.glPopMatrix();
	}
	synchronized void renderStar(GL gl, HashSet<Node> nodes, Node center){
		float[] col = GraphElement.colorFunction(center.name);
		col[3]=Math.min(center.alpha, 0.15f);

		gl.glPushMatrix();
		gl.glColor4fv(col, 0);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		Vector3D D;
		for (Node bref : nodes){
			if (bref!=center){
				D = bref.pos.copy();
				D.sub(center.pos); 
				D.mult(-1);
				FuncGL.symArrowHead(gl, bref.size()*1.5f, center.pos, D);
			}
		}
		gl.glPopMatrix();
	}

	private float size(Node n) {
		return app.nodeSize+n.getDegree()*app.getNodevar();
	}

	private boolean outsideView(Node n) {
		Vector3D p = app.cam.getFocalPoint();
		float d = Vector3D.distance(n.pos, p);
		//		float d = pos.magnitude();
		if (d>app.cam.getDist()*app.getSquareness()) return true; else return false;
	}

	private void drawFrame(GL gl) {
		gl.glPushMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
		gl.glLineWidth(1.5f);
		gl.glScalef(1.25f, 1.25f, 1.25f);
		FuncGL.quad(gl);
		gl.glPopMatrix();
	}
}
