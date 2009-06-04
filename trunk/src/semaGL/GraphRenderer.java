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

	/**
	 * monolithic function for rendering nodes
	 * @param gl
	 * @param n
	 */
	synchronized void renderNode(GL gl, Node n) {
		if (app.layout2d&&outsideView(n)) return;

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

	/**
	 * get 2d screen position from 3d vector, given current projection & viewpoint 
	 * @param gl
	 * @param pos
	 * @return
	 */
	public double[] project2screen(GL gl, Vector3D pos) {
		gl.glGetIntegerv(GL.GL_VIEWPORT, view,0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection,0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model,0);
		double[] winPos = new double[3];
		glu.gluUnProject(pos.x, pos.y, pos.z, model, 0, projection, 0, view, 0, winPos,0);
		return winPos;
	}

	/**
	 * render node labels
	 * @param gl
	 * @param n
	 * @param font
	 * @param fast
	 */
	public synchronized void renderNodeLabels(GL gl, Node n, int font, boolean fast){
		if (app.layout2d&&outsideView(n)) return;

		float distToCam = app.cam.distToCam(n.pos);
		String att="";
		float[] textcolor = {n.color[0]/2f, n.color[1]/2f, n.color[2]/2f, 1};

		if (app.fadeLabels&&n.pickColor[3]==0&&!n.rollover&&!n.isFrame()) {
			font=3;
		}

		if (n.rollover) {
			att= n.genTextAttributeList();
			if (font==3) font=2;
		}
		else {
			if (n.pickColor[3]==0&&(n.alpha<0.2f)) return;
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
		String[] split = att.split("\n");

		if (font<2){

			if (app.isTree()&&app.ns.view.distances.getNodeDistance(n)>0) {
				alignLabel(gl,n.pos, n.size(), font, fsize, split[0]);
			} else
			{
				if (app.tilt) gl.glRotatef(25, 0, 0, 1); 
				else
				{

					if (app.isLabelsEdgeDir()){
						n.getDegree();
						if (n.adList.size()==1&&n.inList.size()==0) {
							Vector3D sub = Vector3D.sub(n.pos, n.adList.iterator().next().pos);
							alignLabel(gl, sub, n.size(), font, fsize, split[0]);

						} else
							if (n.inList.size()==1&&n.adList.size()==0) {
								Vector3D sub = Vector3D.sub(n.pos, n.inList.iterator().next().pos);
								alignLabel(gl, sub, n.size(), font, fsize, split[0]);
							}
							else {
								float advance = getAdvance(n.size(), font, fsize, split[0])/2f;
								gl.glTranslatef(advance+n.size()/2f, n.size()/2f, 0);
							}
					}
				}
			}
		}

		FuncGL.renderText(app, att, textcolor, fsize, font, n.id, distToCam, false, fast); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}

	private float alignLabel(GL gl, Vector3D n, float nSize, int font, float fsize, String split) {

		float angle = (float) ((Math.atan(n.y/n.x))/(2*Math.PI)*360f); // this has to be fixed for 3D
		float advance = getAdvance(nSize, font, fsize, split);

		if (app.layout2d) {
			gl.glRotatef(angle, 0, 0, 1);
			if (n.x<0) {
				gl.glTranslatef(advance, 0, 0);
			} else	
				gl.glTranslatef(nSize, 0, 0);
		}
		return advance;
	}

	/**
	 * Get the Advance (horizontal length) of a jftgl string 
	 * @param nSize
	 * @param font
	 * @param fsize
	 * @param split
	 * @return
	 */
	private float getAdvance(float nSize, int font, float fsize, String split) {
		float advance=0;
		if (font==0)
			advance = -nSize-fsize*(app.hiQfont.advance(split)*0.025f+2f);
		else 
			advance = -nSize-fsize*(FuncGL.stringlength(app, split)*0.01f+2f);
		return advance;
	}

	/**
	 * Render the group lables
	 * @param gl
	 * @param n
	 * @param font
	 */
	/**
	 * @param gl
	 * @param n
	 * @param font
	 */
	public synchronized void renderGroupLabels(GL gl, Node n, int font){

		if (app.layout2d&&outsideView(n)) return;

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

	/**
	 * monolithic method for rendering all sorts of edges
	 * @param gl
	 * @param e
	 */
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
		if (app.directed){
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			FuncGL.arrowHead(gl,10,end,DN);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.arrowHeadEmpty(gl,10,end,DN);
			//			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			//			FuncGL.arrowHead(gl,20,end,DN);
		}
		gl.glPopMatrix();
	}

	/**
	 * render edge labels
	 * @param gl
	 * @param e
	 * @param Text
	 * @param fast
	 */
	synchronized void renderEdgeLabels(GL gl, Edge e, int Text, boolean fast) {
		float[] color = e.color;
		int font = Text;
		Node a = e.getA();
		Node b = e.getB();

		float[] textcolor = {color [0]/2f, color[1]/2f, color[2]/2f, 0.5f};
		if ((app.fadeLabels||app.fadeNodes)&&!e.rollover&&!e.isPicked()&&!e.isFrame()&&!(a.getPickColor()[3]>0&&b.getPickColor()[3]>0)) return;

		if ((e.isPicked()||e.rollover)&&font==3) font=2;
		Vector3D dir = b.pos.copy();
		dir.sub(a.pos); //direction of the edge
		Vector3D midP = dir.copy();
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
		if (font<2) 
			if (app.tilt) gl.glRotatef(25, 0, 0, 1);

			else {
				float advance = alignLabel(gl,dir, 0, font, app.getLabelsize(), rText);
				if (dir.x>=0) {
					gl.glTranslatef(advance/2f,-2*app.getLabelsize(),0);
				} else {
					gl.glTranslatef(-advance/2f,-0.5f*app.getLabelsize(),0);
				}
			}

		FuncGL.renderText(app, rText, textcolor,app.getLabelsize(), font, e.getId(), distToCam, false, fast); //render text in dark grey, with alpha of edge
		gl.glPopMatrix();
	}

	/**
	 * render the colored background of the clusters
	 * @param gl
	 * @param nodes
	 * @param center
	 */
	synchronized void renderFan(GL gl, HashSet<Node> nodes, Node center) {
		float[] col = Func.parseColorInt(center.name.hashCode()+"");
		col[3]=Math.min(center.alpha, 0.05f);
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
	/**
	 * render the group
	 * @param gl
	 * @param nodes
	 * @param center
	 */
	synchronized void renderGroups(GL gl, HashSet<Node> nodes, Node center){
		float[] col = GraphElement.colorFunction(center.name);
		col[3]=Math.min(center.alpha, 0.15f);
		float[] white = {1,1,1,0};
		float[] col2 = col.clone();
		col2[3]=0;

		gl.glPushMatrix();
		gl.glColor4fv(col, 0);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

		Vector3D D;
		for (Node bref : nodes){
			if (bref!=center){
				D = bref.pos.copy();
				D.sub(center.pos); 
				D.mult(-1);
				//				FuncGL.symArrowHead(gl, bref.size()*1.5f, center.pos, D);
				gl.glLineWidth(5);
				FuncGL.drawLine(gl, center.pos, bref.pos, white, col);
			}
		}
		gl.glPopMatrix();
	}

	/**
	 * test if node is outside the view
	 * @param n
	 * @return
	 */
	private boolean outsideView(Node n) {
		Vector3D p = app.cam.getFocalPoint();
		float d = Vector3D.distance(n.pos, p);
		//		float d = pos.magnitude();
		if (d>app.cam.getDist()*app.getSquareness()) return true; else return false;
	}

	/**
	 * render frame around node
	 * @param gl
	 */
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
