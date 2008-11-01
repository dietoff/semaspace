/**
 * 
 */
package semaGL;

import java.util.HashSet;
import java.util.TreeSet;

import javax.media.opengl.GL;

import nehe.TextureReader.Texture;

class Node extends GraphElement {
	Vector3D pos = new Vector3D(0, 0, 0);
	private float size;
	float[] pickColor ={1f,0.9f,0f,0.0f}; //this is the color of the selection frame
	HashSet<Node> adList;
	HashSet<Node> inList;
	HashSet<Node> cluster;
	private int[] textures = new int[1]; //texture ids
	Texture tex=null;	//actual texture
	boolean newTex = false; //new texture loaded
	//	boolean colored;
	public int pickDistance=Integer.MAX_VALUE;
	public String imgurl = null;
	private boolean frame = false;
	private boolean locked = false;
	boolean spiralcluster = false;
	boolean clusterCenter = false;
	boolean clusterPart = false;
	private Float time = null;


	Node(SemaSpace app_, String n_, float x, float y, float z) {
		this(app_,n_, new Vector3D(x,y,z));
	}

	Node(SemaSpace app_, String n_, Vector3D pos_) {
		super(app_,n_);
		defaultcolor = app.nodeColor.clone();
		pos.setXYZ(pos_);
		adList = new HashSet<Node>();
		inList = new HashSet<Node>();
		cluster= new HashSet<Node>();
	}

	@Override
	void render(GL gl) {
		if (app.flat&&outsideView()) return;
		
		if (newTex=true&&tex!=null){
			FuncGL.initGLTexture(gl,tex, textures);
			newTex = false;
			tex=null;
		}
		gl.glPushMatrix();
		gl.glLoadName(id);

		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(pos.x, pos.y, pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		size = size();
		if (textures[0]!=0) size*=app.picSize;
//		if (colored) size*=2;
		//draw node
		gl.glPushMatrix();
		gl.glScalef(size, size, size);
		
		gl.glColor4fv(color,0);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
		gl.glLineWidth(1f);
		FuncGL.quad(gl);
		
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
		//			gl.glColor4fv(pickColor,0); //pick color

		// textures
		if (textures[0]!=0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
			gl.glColor4f(1f,1f,1f,color[3]);
			//				gl.glColor4f(1f,1f,1f,pickColor[3]); //pick color 
		}

		//split quad or solid quad
		if (color2 !=null) {
			FuncGL.triangle1(gl);
			gl.glColor4fv(color2,0);
			FuncGL.triangle2(gl);
		} else
			FuncGL.quad(gl);

		//pick frame
		if (pickColor[3]>0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
			gl.glLineWidth(2.5f);
			gl.glColor4fv(pickColor,0);
			FuncGL.quad(gl);	
		}

		if (isLocked()) {
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
		if (frame) {
			gl.glColor4f(1,0,0,1);
			drawFrame(gl);
		}

		//rollover frame
		if (rollover) {
			gl.glColor4fv(app.rollOverColor,0);
			drawFrame(gl);
		}
		
		
		// reset scale transformations
		gl.glPopMatrix();

		// reset all transformations
		gl.glPopMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	private float size() {
		return app.nodeSize+getDegree()*app.getNodevar();
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
	
	public void renderLabels(GL gl, int font){
		
		if (app.flat&&outsideView()) return;
		
		float distToCam = app.cam.distToCam(pos);
		String att="";
		float[] textcolor = {color[0]/2f, color[1]/2f, color[2]/2f, 1};
		if (rollover) {
			att= genTextAttributeList();
			if (font==3) font=2;
		}
		else {
			if ((pickColor[3]==0&&(alpha<0.2f)||font==3)) return;
			if (distToCam>2000) return; 
			att = genTextSelAttributes();
		}
		textColor[3]=alpha;

		gl.glPushMatrix();
		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(pos.x, pos.y, pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		if (font<2&&app.tilt) gl.glRotatef(25, 0, 0, 1);
		FuncGL.renderText(app, att, textcolor, app.fontsize, font, id, distToCam); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}

	private boolean outsideView() {
		Vector3D p = app.cam.getFocalPoint();
		float d = Vector3D.distance(pos, p);
//		float d = pos.magnitude();
		if (d>app.cam.getDist()*app.getSquareness()) return true; else return false;
	}

	public boolean isFrame() {
		return frame;
	}

	public void setFrame(boolean b) {
		frame  = b;
	}

	public void setPickColor(float[] col) {
		this.pickColor = col;
	}

	public float[] getPickColor() {
		return pickColor;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public boolean isPicked() {
		if (pickColor[3]!=0) return true; else return false;
	}
	public boolean hasTexture(){
		if (textures[0]!=0) return true; else return false;
	}

	public void deleteTexture(GL gl){
		gl.glDeleteTextures(1, textures, 0);
		newTex = false;
		tex=null;
		textures[0]=0;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return locked;
	}

	public int getDegree() {
		return adList.size();
	}
	public int getiDegree() {
		return adList.size()+inList.size();
	}

	public void setTime(float f) {
		time = f;
	}

	public Float getTime() {
		return time;
	}
	public String getAltName() {
		String r = altName;
		if (r==null) r=name;
		return r;
	}
}


