package semaGL;

import java.util.HashSet;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;


import data.*;

public class GraphRenderer {
	private SemaParameters par;
	private GLU glu;
	private double[] projection= new double[16];
	private double[] model = new double[16];
	private int[] view = new int[16];



	public GraphRenderer (SemaParameters app_){
		glu = new GLU();
		par=app_;
	}

	private float alignLabel(GL gl, Vector3D n, float nSize, int font, float fsize, String split) {

		float angle = (float) ((Math.atan(n.y/n.x))/(2*Math.PI)*360f); // this has to be fixed for 3D
		float advance = getAdvance(nSize, font, fsize, split);

		if (par.layout2d) {
			gl.glRotatef(angle, 0, 0, 1);
			if (n.x<0) {
				gl.glTranslatef(advance, 0, 0);
			} else	
				gl.glTranslatef(nSize, 0, 0);
		}
		return advance;
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
			advance = -nSize-fsize*(par.getApp().hiQfont.advance(split)*0.025f+2f);
		else 
			advance = -nSize-fsize*(FuncGL.stringlength(par, split)*0.01f+2f);
		return advance;
	}

	/**
	 * test if node is outside the view
	 * @param n
	 * @return
	 */
	private boolean outsideView(Node n) {
		Vector3D p = par.getCam().getFocalPoint();
		float d = Vector3D.distance(n.pos, p);
		//		float d = pos.magnitude();
		if (d>par.getCam().getDist()*par.getSquareness()) return true; else return false;
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

	void renderBounds(GL gl, HashSet<Node> nodes){
		gl.glPushMatrix();
//		gl.glLoadIdentity();
		BBox3D bounds = BBox3D.calcBounds(nodes);
		gl.glTranslatef(bounds.center.x, bounds.center.y, bounds.center.z);
//		gl.glScalef(30,30, 1);
		gl.glScalef(bounds.size.x/2f, bounds.size.y/2f, bounds.size.z/2f);
//		System.out.println(bounds.center.x+" "+bounds.center.y+" "+bounds.center.z);
		gl.glColor4f(255,0,1,255);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glColor4fv(par.frameColor,0);
		FuncGL.quad(gl);
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
		float[] color = e.getColor();
		int font = Text;
		Node a = e.getA();
		Node b = e.getB();

		float[] textcolor = {color [0]/2f, color[1]/2f, color[2]/2f, 0.5f};
		if (par.fadeLabels&&!e.rollover&&!e.isPicked()&&!e.isFrame()&&!(a.getPickColor()[3]>0&&b.getPickColor()[3]>0)) return;

		if (e.alpha<0.2) return;
		
		if ((e.isPicked()||e.rollover)&&font==3) font=2;
		Vector3D dir = b.pos.copy();
		dir.sub(a.pos); //direction of the edge
		Vector3D midP = dir.copy();
		midP.mult(0.5f);
		midP.add(a.pos);
		float distToCam = par.getCam().distToCam(midP);
		if (distToCam>par.maxLabelRenderDistance) return; 

		String rText = e.genTextSelAttributes();
		gl.glPushMatrix();
		float xRot = par.getCam().getYRot();		//billboard; should be global camera orientation
		float yRot = par.getCam().getXRot();
		gl.glTranslatef(midP.x,midP.y,midP.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2) 
			if (par.isTilt()) gl.glRotatef(25, 0, 0, 1);

			else {
				float advance = alignLabel(gl,dir, 0, font, par.getLabelsize(), rText);
				if (dir.x>=0) {
					gl.glTranslatef(advance/2f,-2*par.getLabelsize(),0);
				} else {
					gl.glTranslatef(-advance/2f,-0.5f*par.getLabelsize(),0);
				}
			}

		FuncGL.renderText(par, rText, textcolor,par.getLabelsize(), font, e.getId(), distToCam, false, fast); //render text in dark grey, with alpha of edge
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
				FuncGL.drawLine(gl, start, end,par.pickGradStart,par.pickGradStart);
			if (e.rollover) 
				FuncGL.drawLine(gl, start, end,par.rollOverColor,par.rollOverColor);
			if (e.isFrame())
				FuncGL.drawLine(gl, start, end,par.frameColor,par.frameColor);

			FuncGL.drawLine(gl, start, end,a.getPickColor(),b.getPickColor());
		} 
		else 
		{
			// draw actual edge
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glLineWidth(par.getEdgewidth());
			gl.glBegin(GL.GL_LINES);
			float[] aCol = e.getColor().clone();
			float[] bCol = e.getColor().clone();

			if (e.colored) {
				bCol[3]=1f;
				aCol[3]=1f;
			} else {
				float f = par.getEdgeAlpha();
				aCol[3] = e.getA().alpha*f;
				bCol[3] = e.getB().alpha*f;
			}
			
			if (par.isInheritEdgeColorFromNodes()) {
				aCol = e.getA().getColor().clone();
				bCol = e.getB().getColor().clone();
			}

			if (e.fade) {
				bCol[3]=0.1f;
				aCol[3]=0.1f;
			}
			
			FuncGL.drawLine(gl, start, end, aCol, bCol);
			gl.glDisable(GL.GL_LINE_STIPPLE);
			
			e.alpha = (bCol[3]+aCol[3]) / 2f;
		}

		// draw property vector 
		if (e.getProperty()!=-1) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			FuncGL.propertyVector(gl, e.getProperty(), 3f, end, DN);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			FuncGL.propertyVector(gl, e.getProperty(), 3f, end, DN);
		}

		//draw arrowhead
		if (par.directed){
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

		if (par.layout2d&&outsideView(n)) return;

		float distToCam = par.getCam().distToCam(n.pos);
		//		float[] textcolor = {n.color[0]/2f, n.color[1]/2f, n.color[2]/2f, 1};
		float[] textcolor = GraphElement.colorFunction(n.name);
		n.textColor[3]=1;
		gl.glPushMatrix();
		//transform model
		float xRot = par.getCam().getYRot();		//should be global camera orientation
		float yRot = par.getCam().getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2&&par.isTilt()) gl.glRotatef(25, 0, 0, 1);
		
		float size = 10f*par.getLabelsize();
		String name = n.name;
		float advance = getAdvance(0, font, size,name)/2f;
		gl.glTranslatef(advance/2f,0, 0);
		
		FuncGL.renderText(par, name, textcolor, size, font, n.id, distToCam, true, false); //dont draw the text if alpha is too low
		// reset all transformations
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
		col[3]=Math.min(center.alpha, 0.20f);
		float[] white = {1,1,1,0};
		float[] col2 = col.clone();
		col2[3]=0;

		gl.glPushMatrix();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		Vector3D D;
		for (Node bref : nodes){
			if (bref!=center){
				D = bref.pos.copy();
				D.sub(center.pos); 
				D.mult(-1);
				gl.glColor4fv(col, 0);
				FuncGL.groupArrow(gl, bref.size()*1.5f, center.pos, D);
//				gl.glLineWidth(5);
//				FuncGL.drawLine(gl, center.pos, bref.pos, white, col);
			}
		}
		gl.glPopMatrix();
	}
	/**
	 * monolithic function for rendering nodes
	 * @param gl
	 * @param n
	 */
	synchronized void renderNode(GL gl, Node n) {
		if (par.layout2d&&outsideView(n)) return;

		if (n.newTex=true&&n.tex!=null){
			FuncGL.initGLTexture(gl,n.tex, n.textures);
			n.newTex = false;
			n.tex=null;
		}
		gl.glPushMatrix();
		gl.glLoadName(n.id);

		//transform model
		float xRot = par.getCam().getYRot();		//should be global camera orientation
		float yRot = par.getCam().getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		float size = n.size();
		if (n.textures[0]!=0) size*=par.getPicSize();
		//		if (colored) size*=2;
		//draw node
		gl.glPushMatrix();
		gl.glScalef(size, size, size);

		float[] color = n.getColor();

		if (n.fade) {
			color[3]=0.1f;
		}
		
		gl.glColor4fv(color,0);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
		gl.glLineWidth(1f);
		FuncGL.quad(gl);

		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		//			gl.glColor4fv(pickColor,0); //pick color

		// textures
		if (n.textures[0]!=0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, n.textures[0]);
			gl.glColor4f(1f,1f,1f,n.getColor()[3]);
			//				gl.glColor4f(1f,1f,1f,pickColor[3]); //pick color 
		}
		
		

		//split quad or solid quad
		float[] color2 = n.getColor2();
		if (color2 !=null) {
			FuncGL.triangle1(gl);
			if (n.fade) color2[3]=0.1f;
			gl.glColor4fv(color2,0);
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
			gl.glColor4fv(par.frameColor,0);
			drawFrame(gl);
		}

		//rollover frame
		if (n.rollover) {
			gl.glColor4fv(par.rollOverColor,0);
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
	 * render node labels
	 * @param gl
	 * @param n
	 * @param font
	 * @param fast
	 */
	public synchronized void renderNodeLabels(GL gl, Node n, int font, boolean fast){
		if (par.layout2d&&outsideView(n)) return;

		float distToCam = par.getCam().distToCam(n.pos);
		String att="";
		float[] textcolor = {n.getColor()[0]/2f, n.getColor()[1]/2f, n.getColor()[2]/2f, 1};

		if (par.fadeLabels&&n.pickColor[3]==0&&!n.rollover&&!n.isFrame()) {
			font=3;
		}

		if (n.rollover) {
			att= n.genTextAttributeList();
			if (font==3) font=2;
		}
		else {
			if (n.pickColor[3]==0&&(n.alpha<0.2f)) return;
			if (distToCam>par.maxLabelRenderDistance) return; 
			att = n.genTextSelAttributes();
		}
		n.textColor[3]=n.alpha;


		gl.glPushMatrix();
		//transform model
		float xRot = par.getCam().getYRot();		//should be global camera orientation
		float yRot = par.getCam().getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);

		float fsize = par.getLabelsize()+n.size()*par.getLabelVar();
		String[] split = att.split("\n");

		if (font<2){

			if (par.isTree()&&par.getApp().ns.view.distances.getNodeDistance(n)>0) {
				alignLabel(gl,n.pos, n.size(), font, fsize, split[0]);
			} else
			{
				if (par.isTilt()) gl.glRotatef(25, 0, 0, 1); 
				else
				{

					if (par.isLabelsEdgeDir()){
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

		FuncGL.renderText(par, att, textcolor, fsize, font, n.id, distToCam, false, fast); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}
}
