package semaGL;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import data.Vector3D;

/**
 * OpenGL Camera class
 * @author d
 *
 */
public class Cam {
	private final GLAutoDrawable gLDrawable;
	float mat[] = 	{1f,0f,0f,0f,
					0f,1f,0f,0f,
					0f,0f,1f,0f,
					0f,0f,0f,1f};

	private Vector3D camLocal = new Vector3D(0,0,0);
	private Vector3D camPos = new Vector3D(0,0,0);
	private Vector3D tmpPos = new Vector3D(0,0,0);
	private float newx, newy, newz;
	private float dx, dy, dz;
	private float yRot, xRot, zRot;
	private float h, znear, zfar, dist;
	float modelview[] = new float[16];
	final float TWO_PI =6.283185307179586476925286766559f;
	
	Cam (GLAutoDrawable space,float FOV , float xwinkel, float ywinkel, float dist, Vector3D lookAt, float znear_, float zfar_) {
		gLDrawable = space;
		final GL gl = gLDrawable.getGL();
		final GLU glu = new GLU();
		camLocal.setXYZ(0, 0, 0);
		yRot += xwinkel;
		xRot += ywinkel;
		
		h = gLDrawable.getWidth()/gLDrawable.getHeight();
		this.znear = znear_;
		this.zfar = zfar_;
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(FOV, h, znear, zfar);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	void posIncrement(GL gl, float yrot, float xrot, float dist_, Vector3D focus_) {
		xRot+=xrot;
		yRot+=yrot;
		
//		xRot = 0f;
		
//		yRot+= Math.cos(yRot*TWO_PI/360)*xwinkel_;
//		xRot+= Math.cos(yRot*TWO_PI/360)*ywinkel_;
//		yRot+= Math.cos(yRot*TWO_PI/360)*ywinkel_-Math.sin(xRot*TWO_PI/360)*xwinkel_;
		
//		xRot = Math.max(-30, Math.min(30, xRot));
		xRot = xRot % 360;
		yRot = yRot % 360;
		
		dist+=dist_;
		tmpPos.setXYZ(focus_);
		tmpPos.sub(camLocal);
		tmpPos.mult(.5f);
		camLocal.add(tmpPos);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity(); //overwrite new
		gl.glTranslatef(0, 0, -dist);
		gl.glRotatef(-xRot, 1, 0, 0);
		gl.glRotatef(-yRot, 0, 1, 0); // TODO change axis to get local coordinates
//		gl.glRotatef(-zRot, 0, 0, 1);
		gl.glTranslatef(-camLocal.x,-camLocal.y,-camLocal.z);
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelview,0);
		
	}
	void posAbsolute(GLAutoDrawable space, float yrot, float xrot, float dist_, Vector3D focus_) {
		final GL gl = gLDrawable.getGL();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		xRot=xrot;
		yRot=yrot;
		dist=dist_;
		camLocal.setXYZ(focus_);
		gl.glLoadIdentity(); //overwrite new
		gl.glTranslatef(0, 0, -dist);
		gl.glRotatef(-xRot, 1, 0, 0);
		gl.glRotatef(-yRot, 0, 1, 0); // TODO change axis to get local coordinates
//		gl.glRotatef(-zRot, 0, 0, 1);
		gl.glTranslatef(-camLocal.x,-camLocal.y,-camLocal.z);
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelview,0);
	}
	
	void posAbsolute(GLAutoDrawable space, float dist_, Vector3D focus_) {
		final GL gl = gLDrawable.getGL();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		dist=dist_;
		camLocal.setXYZ(focus_);
		gl.glLoadIdentity(); //overwrite new
		gl.glTranslatef(0, 0, -dist);
		gl.glTranslatef(-camLocal.x,-camLocal.y,-camLocal.z);
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelview,0);
	}
	
	public float distToCam(Vector3D midP) {
		Vector3D aCam = new Vector3D(0,0,0);
		aCam.setX(modelview[0]*midP.x+modelview[4]*midP.y+modelview[8]*midP.z+modelview[12]);
		aCam.setY(modelview[1]*midP.x+modelview[5]*midP.y+modelview[9]*midP.z+modelview[13]);
		aCam.setZ(modelview[2]*midP.x+modelview[6]*midP.y+modelview[10]*midP.z+modelview[14]);
		float distA = aCam.magnitude();
		return distA;
	}
	
	public Vector3D getFocalPoint(){
		Vector3D fp = camLocal.copy();
//		fp.z += dist;
		return fp;
	}
	
	public float getYRot(){
		return yRot;
	}
	public float getXRot(){
		return xRot;
	}
	public void setYRot(float rot) {
		yRot = rot;
	}
	public void setXRot(float rot) {
		xRot = rot;
	}
	public float getDist() {
		return dist;
	}
	public void setDist(float dist) {
		this.dist = dist;
	}
	public float getX() {
		return camLocal.x;
	}
	public void getX(float oldx) {
		this.camLocal.x = oldx;
	}
	public float getY() {
		return camLocal.y;
	}
	public void setY(float oldy) {
		this.camLocal.y = oldy;
	}
	public float getZ() {
		return camLocal.z;
	}
	public void setZ(float oldz) {
		this.camLocal.z = oldz;
	}
}

