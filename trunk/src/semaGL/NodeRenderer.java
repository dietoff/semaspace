package semaGL;

import java.util.HashMap;

import javax.media.opengl.GL;

import nehe.TextureReader.Texture;

public class NodeRenderer {
	float[] frameColor ={0f,1f,0f,0f};
	float[] pickColor ={1f,0f,0f,0.8f};
	float[] rollOverColor = {1f,0.5f,0f,0.8f};
	float[] nodeColor = {0.2f,0.2f,0.5f,0.8f};
	float[] defaultcolor ={.5f,.5f,.5f,0.7f};
	float[] textColor ={0f,0f,0f,0.8f};
	float[] white ={1f,1f,1f,0.8f};
	float alpha=0.8f;
	float nodeSize = 5f;
	int picSize = 5;
	private int id;
	private SemaSpace app;
	private int[] textures;
	private float size;
	protected boolean colored = false;
	private boolean frame;
	private boolean locked;
	
	void NodeRenderer (SemaSpace app_){
		app=app_;
	}
	
	void render(GL gl, Node n) {
		if (n.newTex=true&&n.tex!=null){
			FuncGL.initGLTexture(gl,n.tex, textures);
			n.newTex = false;
			n.tex=null;
		}
		gl.glPushMatrix();
		gl.glLoadName(id);

		//transform model
		float xRot = app.cam.getYRot();//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);
		size = size(n);
		if (textures[0]!=0) size*=picSize;
//		if (colored) size*=2;
		//draw node
		gl.glPushMatrix();
		gl.glScalef(size, size, size);
		
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glColor4fv(n.color,0);
		//			gl.glColor4fv(pickColor,0); //pick color

		// textures 
		if (textures[0]!=0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
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
		if (pickColor[3]>0){
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
			gl.glLineWidth(2.5f);
			gl.glColor4fv(pickColor,0);
			FuncGL.quad(gl);	
		}

		if (locked) {
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
		if (n.rollover) {
			gl.glColor4fv(app.rollOverColor,0);
			drawFrame(gl);
		}
		
		
		// reset scale transformations
		gl.glPopMatrix();

		// reset all transformations
		gl.glPopMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	private float size(Node n) {
		return app.nodeSize+n.getDegree()*app.getNodevar();
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
	
	public void renderLabels(GL gl, Node n, int font){
		// render text
		String att="";
		float[] textcolor = {n.color[0]/2f, n.color[1]/2f, n.color[2]/2f, 1};
		float distToCam = app.cam.distToCam(n.pos);
		if (n.rollover) {
			att= n.genTextAttributeList();
			if (font==3) font=2;
		}
		else {
			if ((pickColor[3]==0&&(alpha<0.2f)||font==3)) return;
			att = n.genTextSelAttributes();
		}
		textColor[3]=alpha;

		gl.glPushMatrix();
		//transform model
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();
		gl.glTranslatef(n.pos.x, n.pos.y, n.pos.z);
		gl.glRotatef(xRot, 0, 1, 0);
		gl.glRotatef(yRot, 1, 0, 0);


		FuncGL.renderText(app, att, textcolor, app.fontsize, font, id, distToCam); //dont draw the text if alpha is too low
		// reset all transformations
		gl.glPopMatrix();
	}


}
