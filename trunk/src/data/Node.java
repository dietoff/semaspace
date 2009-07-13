/**
 * 
 */
package data;

import java.util.HashSet;

import javax.media.opengl.GL;
import semaGL.SemaParameters;
import nehe.TextureReader.Texture;

public class Node extends GraphElement {
	public Vector3D pos = new Vector3D(0, 0, 0);
	public Vector3D lockedPos = new Vector3D(0, 0, 0);
	private float size;
	public float[] pickColor ={1f,0.9f,0f,0.0f}; //this is the color of the selection frame
	public HashSet<Node> adList;
	public HashSet<Node> inList;
	public HashSet<Node> cluster;
	public int[] textures = new int[1]; //texture ids
	public Texture tex=null;	//actual texture
	public boolean newTex = false; //new texture loaded
	//	boolean colored;
	public int pickDistance=Integer.MAX_VALUE;
	public String imgurl = null;
	private boolean locked = false;
	public boolean spiralcluster = false;
	public boolean partOfGroup = false;
	private Float time = null;
	public boolean group = false;


	Node (SemaParameters app, String n_, float x, float y, float z) {
		this(app,n_, new Vector3D(x,y,z));
	}

	Node (SemaParameters app_, String n_, Vector3D pos_) {
		super(app_,n_);
		defaultcolor = app.nodeColor.clone();
		pos.setXYZ(pos_);
		adList = new HashSet<Node>();
		inList = new HashSet<Node>();
		cluster= new HashSet<Node>();
	}

	
	public float size() {
		return app.getNodeSize()+inList.size()*app.getInVar()+adList.size()*app.getOutVar();
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

	public boolean isPickRegion() {
		if (pickColor[3]!=0) return true; else return false;
	}
	public boolean isPicked() {
		if (pickDistance==0) return true; else return false;
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
		int size2;
		if (inList!=null) {
			 size2 = inList.size();
		} else size2=0;
		return adList.size()+size2;
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

	public int inOut() {
		return adList.size()+inList.size();
	}

	public void lock(Vector3D pos2) {
		lockedPos = pos2.copy();
		locked = true;
	}
}


