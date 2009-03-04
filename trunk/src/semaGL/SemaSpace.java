package semaGL;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.apache.batik.svggen.SVGGraphics2DIOException;

import nehe.TextureReader.Texture;
import UI.SwingSema;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;

import data.Edge;
import data.GraphElement;
import data.Net;
import data.NetStack;
import data.Node;
import data.Vector3D;

import net.sourceforge.ftgl.glfont.FTFont;
import net.sourceforge.ftgl.glfont.FTGLTextureFont;

public class SemaSpace implements GLEventListener, MouseListener, MouseMotionListener, KeyListener  {
	private static final long serialVersionUID = -1864003907508879499L;
	GLUT glut = new GLUT();

	//	HashSet<String> map = Messages.getArray("map");

	String filename = Messages.getString("defaultFilename"); //$NON-NLS-1$
	public String texfolder = Messages.getString("textureDirectory"); //$NON-NLS-1$
	String cacheDir = "./cache/"; //$NON-NLS-1$
	public  String texurl = "http://"; //$NON-NLS-1$
	int searchdepth = Integer.parseInt(Messages.getString("searchdepth"));
	private  boolean changed=false;
	public  boolean fadeEdges=false;
	public  boolean fadeNodes=false;
	float standardNodeDistance = Float.parseFloat(Messages.getString("standardNodeDistance"));
	float repellDist = Float.parseFloat(Messages.getString("repellDistance"));
	public float nodeSize = Float.parseFloat(Messages.getString("nodeSize"));
	int picSize = Integer.parseInt(Messages.getString("picSize"));
	float strength = Float.parseFloat(Messages.getString("edgeStrength"));
	private float val = Float.parseFloat(Messages.getString("valenceFactor"));
	float repellStrength = Float.parseFloat(Messages.getString("repellStrength"));
	public float clusterRad= Float.parseFloat(Messages.getString("clusterRadius"));
	public float radialDist = Float.parseFloat(Messages.getString("radialLayoutDistance"));
	public float boxdist = Float.parseFloat(Messages.getString("boxLayoutDistance"));
	String url;
	String nodeUrl;
	String edgeUrl;
	int pickdepth = Integer.parseInt(Messages.getString("pickDistance"));
	long inflatetime = Long.parseLong(Messages.getString("inflateTimeMs"));
	int fonttype = 1;
	float edgewidth = Float.parseFloat(Messages.getString("edgeWidth"));
	float textwidth = 0.8f;
	public float[] pickGradEnd = {0f,1f,0f,0f};
	public float[] frameColor={0f,0f,1f,0.8f};
	float[] pickGradStart ={1f,0f,0f,0.8f};
	float[] rollOverColor = {1f,0.5f,0f,0.8f};
	public float[] nodeColor = {0.2f,0.2f,0.5f,0.8f};
	public float[] edgeColor = {0.7f,0.7f,1f,0.8f};
	boolean opt = false; //optimized repelling
	boolean flat = true;
	public boolean animated=false;
	public boolean repell = Boolean.parseBoolean(Messages.getString("repellOn"));
	boolean distance = true;
	boolean inflate = true;
	public int pickID=-1;
	boolean pressed = false;
	boolean CTRL = false;
	public boolean calculate = true;
	private int fogMode[] = {GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR};	// Storage For Three Types Of Fog ( new )
	private int fogfilter = 2;								// Which Fog Mode To Use      ( new )
	private float fogColor[] = {0.9f, 0.9f, 0.9f, 1.0f};		// Fog Color   
	private float znear = 10.0f;
	private float zfar = 100000f;
	//	Net net;
	String file[];
	final float TWO_PI =6.283185307179586476925286766559f;
	private float yRotInc, xRotInc = 0;
	private float yRotNew, xRotNew = 0;
	private float mouseY=0, newY=0;
	private float mouseX=0, newX=0;
	private float zInc = 700;
	private float zoomNew = zInc;
	Vector3D focus = new Vector3D(0f,0f,0f);
	Cam cam;
	private float h;
	int viewPort[] = new int[4];
	long starttime, elapsedtime, lasttime, deltatime, currenttime;
	boolean cluster=true;
	private boolean FOG = true;
	private boolean random = false;
	boolean select;
	private float FOV = 70f;
	public GLAutoDrawable glD;
	private boolean edges=true;
	public boolean directed=true;
	private GLU glu;
	public FileIO fileIO;
	public int ageThresh=Integer.MAX_VALUE;
	float perminflate= Float.parseFloat(Messages.getString("permanentInflate"));
	private SwingSema swingapp;
	private float frame;
	public Texture tex;
	public boolean texRead= false;
	public boolean render= true;
	private boolean tree = false;
	private boolean radial = false;
	private boolean repNeighbors = false;
	private int overID;
	public boolean moved;
	public Layouter layout;
	public GraphRenderer renderer;
	private String attribute="none"; //$NON-NLS-1$
	public int thumbsize = Integer.parseInt(Messages.getString("thumbnailRes"));
	public Graphics2D j2d;
	private boolean timeline;
	private float nodevar= Float.parseFloat(Messages.getString("nodeSizeVariance"));
	public boolean textures= Boolean.parseBoolean(Messages.getString("textures"));
	private Font font;
	FTFont outlinefont;
	FTFont polyfont;
	private float labelsize= Float.parseFloat(Messages.getString("labelSize"));
	private float labelVar= Float.parseFloat(Messages.getString("labelSizeVariance"));
	private boolean initTree;
	public boolean inheritEdgeColorFromNodes = Boolean.parseBoolean(Messages.getString("inheritEdgeColorFromNodes"));
	public boolean drawClusters= Boolean.parseBoolean(Messages.getString("clusters"));
	//	private GLPbuffer pbuffer;
	public NetStack ns;
	FTGLTextureFont texturefont;
	public boolean tilt = Boolean.parseBoolean(Messages.getString("tiltedLabels"));
	int screenshotcounter = 0;
	public int shotres = Integer.parseInt(Messages.getString("screenshotResolution"));
	public float maxLabelRenderDistance = Float.parseFloat(Messages.getString("maxLabelRenderDistance"));
	private boolean tabular = false;
	public float edgeAlpha = Float.parseFloat(Messages.getString("edgeAlpha"));
	private boolean groups =  Boolean.parseBoolean(Messages.getString("drawGroups"));
	private boolean SVGexport;
	private String svgFile;
	private boolean labelsEdgeDir=true;

	public SemaSpace(){
		Color.decode(Messages.getString("pickGradientFar")).getComponents(pickGradEnd);
		Color.decode(Messages.getString("pickGradientCenter")).getComponents(pickGradStart);
		Color.decode(Messages.getString("rollOverColor")).getComponents(rollOverColor);
		Color.decode(Messages.getString("nodeColor")).getComponents(nodeColor);
		nodeColor[3]=Float.parseFloat(Messages.getString("nodeAlpha"));
		Color.decode(Messages.getString("edgeColor")).getComponents(edgeColor);
		Color.decode(Messages.getString("frameColor")).getComponents(frameColor);
		labelsEdgeDir = (Boolean.parseBoolean(Messages.getString("labelsEdgeDir")));
		fileIO = new FileIO(this);
		ns = (new NetStack(this));
		layout = new Layouter(this);
		renderer = new GraphRenderer(this);

	}

	public void init(GLAutoDrawable gLDrawable) {
		glD = gLDrawable;
		GL gl = gLDrawable.getGL();
		glu = new GLU();
		gLDrawable.addMouseListener(this);
		gLDrawable.addMouseMotionListener(this);
		gLDrawable.addKeyListener(this);
		initGLsettings(gl);
		cam = new Cam(gLDrawable,FOV,0,0,zInc,focus,znear,zfar);

		initFonts(gl);
		if (random) ns.global.generateRandomNet (100, 146);		// random network
		else netLoad(isTabular());
	}

	private void initGLsettings(GL gl) {
		gl.glEnable(GL.GL_TEXTURE_2D);								// Enable Texture Mapping
		gl.glShadeModel(GL.GL_SMOOTH);              				// Enable Smooth Shading
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really Nice Perspective Calculations
		gl.glClearColor(1f, 1f, 1f, 1f);   
		gl.glEnable(GL.GL_BLEND);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);	// Set The Blending Function For Translucency (new ) GL_ONE
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
		gl.glFogi(GL.GL_FOG_MODE, fogMode[fogfilter]);				// Fog Mode
		gl.glFogfv(GL.GL_FOG_COLOR, fogColor, 0);					// Set Fog Color
		gl.glFogf(GL.GL_FOG_DENSITY, 0.0005f);						// How Dense Will The Fog Be
		gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);					// Fog Hint Value
		gl.glFogf(GL.GL_FOG_START, 1000f);							// Fog Start Depth
		gl.glFogf(GL.GL_FOG_END, 10000f);							// Fog End Depth
		if (FOG) gl.glEnable(GL.GL_FOG);							// Enables GL.GL_FOG
	}


	private void initFonts(GL gl) {
		//		try {
		//			File file = new File("machtgth.ttf");
		//			FileInputStream is = new FileInputStream(file);
		//			font = Font.createFont(Font.TRUETYPE_FONT, is);
		//		} catch (MalformedURLException e) {
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		} catch (FontFormatException e) {
		//			e.printStackTrace();
		//		}
		font = Font.decode("Times New Roman").deriveFont(172f); //$NON-NLS-1$
		FontRenderContext context = FTFont.STANDARDCONTEXT;
		texturefont = new FTGLTextureFont(font,context);
		texturefont.setGLGLU(gl, glu);
		texturefont.faceSize(70f);
	}

	public void display(GLAutoDrawable gLDrawable) {
		try {
			updateTime();
			layout();
			render(gLDrawable.getGL());
		}
		catch (ConcurrentModificationException e) {
		}
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		GL gl = gLDrawable.getGL();
		glu = new GLU();
		if (height <= 0) height = 1;
		h = (float)width/height;
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort, 0);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(FOV, h, znear, zfar);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		updateUI();
		redrawUI();
	}

	public void layout() {
		float str = strength;
		boolean rep = repell;

		layout.setNet(ns.getView());

		if (tree){
			layoutTreeSequence(str, rep);
		}

		else {
			if (calculate) {
				if (repNeighbors) layout.layoutRepNeighbors(repellStrength/4f, standardNodeDistance, ns.getView());

				float inf = inflatetime-elapsedtime;
				if ((inf > 0&&inflate)&&ns.getView().eTable.size()>1&&ns.getView().fNodes.size()>1) {
					float r = elapsedtime/inflatetime;
					layout.layoutInflate(Math.min(ns.getView().eTable.size(),1000)*(1-r),ns.getView());
					str = 0.3f;
					rep = false;
				}

				if (perminflate>0) layout.layoutInflate(perminflate*100f, ns.getView());

				if (distance) layout.layoutDistance(standardNodeDistance , getVal(), str, ns.getView()); 

				if (changed&&!flat) changed = false;

				if (rep) layout.layoutRepell(repellDist,repellStrength, ns.getView());

				layout.layoutLockPlace(ns.getView());

				layout.layoutGroups(ns.getView());

				if (timeline) layout.layoutTimeline();

				if (flat) layout.layoutFlat();
			}
		}
	}

	private void layoutTreeSequence(float str, boolean rep) {
		if (calculate&&distance&&!initTree) layout.layoutDistanceTree(standardNodeDistance, getVal(), str); // +nets.view.nNodes.size()/5
		if (calculate&&rep&&!initTree) layout.layoutRepFruchtermannRadial(repellDist,repellStrength);

		if (initTree) {
			for (int i=0;i<50;i++) {
				layout.layoutDistanceTree(0, 1, 0.5f);
				layout.initRadial(0, 0, radialDist);
				layout.layoutEgocentric();
			}
			initTree = false;
		}
		layout.layoutGroups(ns.getView());
		layout.layoutLockPlace(ns.getView());
		layout.layoutEgocentric();
		layout.layoutFlat();
	}

	public void render(GL gl){
		if (SVGexport) {
			try {
				layout.renderSVG(gl, renderer, fonttype, svgFile);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (SVGGraphics2DIOException e) {
				e.printStackTrace();
			}
			SVGexport=false;
		}

		if (!render) return;
		if (FOG&&!flat) gl.glEnable(GL.GL_FOG); else gl.glDisable(GL.GL_FOG);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		cam.posIncrement(gl, yRotInc, xRotInc, zInc, focus); 
		layout.render(gl, fonttype, ns.view, renderer);
		gl.glFlush();
		gl.glFinish();

		//		}
		//		else
		//		{
		if (moved) {
			setOverID(selectCoord(gl));
			if (pressed) select(); //initiate picking
			clearRollover();
			GraphElement n = ns.getView().getByID(overID);
			if (n!=null) n.setRollover(true);
			moved=false;
		}
		statusMsg();


	}

	public void renderPbuffer(GL gl, int width, int height) {
		if (height <= 0) height = 1;
		float h = (float)width/(float)height;
		//		float e = edgewidth;
		//		float t = textwidth;
		//		edgewidth=2f;
		//		textwidth=2f;

		initGLsettings(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(FOV, 1, znear, zfar);
		render(gl);

		//		edgewidth = e;
		//		textwidth = t;
	}

	void select(){
		pickID = getOverID();
		if (pickID!=-1) select = true;
		pressed=false;
		if (CTRL) focus.setXYZ(ns.getView().getPosByID(pickID)); //point to selected node's position
		updatePick(pickID);
	}

	int selectCoord(GL gl){
		GLU glu = new GLU();

		int buffsize = (ns.getView().nNodes.size()+ns.getView().nEdges.size())*4;
		double x = mouseX, y = mouseY;
		IntBuffer selectBuffer = BufferUtil.newIntBuffer(buffsize);
		int hits = 0;
		gl.glSelectBuffer(buffsize, selectBuffer);

		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();
		gl.glPushName(0);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluPickMatrix(x, glD.getHeight() - y, 5.0d, 5.0d, viewPort, 0);
		glu.gluPerspective(FOV, h, znear, zfar);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		cam.posIncrement(gl, yRotInc, xRotInc, zInc, focus);

		layout.renderNodes(gl, renderer, 0); //render the nets.viewwork 
		if (edges) layout.renderEdges(gl, renderer, 0);
		//		gl.glFlush();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		//		gl.glMatrixMode(GL.GL_MODELVIEW); // Select The Modelview Matrix
		hits = gl.glRenderMode(GL.GL_RENDER);
		int overID=-1;
		if (hits!=0){
			float z1=0;
			int tempID=0;
			float tempZ=0;
			for (int i = 0; i<hits; i++){
				tempZ = selectBuffer.get((i*4)+1);
				tempID= selectBuffer.get((i*4)+3);
				if (tempZ<z1) {
					overID=tempID; 
					z1=tempZ;}
			}
		}
		return overID;
	}

	public void keyPressed(KeyEvent evt) {
		CTRL = evt.isControlDown();
		//		if(evt.isAltDown()) pause=!pause;

		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_SPACE:
			break;
		case KeyEvent.VK_SHIFT:
			break;
		case KeyEvent.VK_F1:
			nameCurrentAttribute();
			break;
		case KeyEvent.VK_F2: 
			inflate=true;
			System.out.println("inflate = true"); //$NON-NLS-1$
			break;
		case KeyEvent.VK_F3:
			break;
		case KeyEvent.VK_F4: 
			layout.layoutLocksRemove();
			break;
		case KeyEvent.VK_F5: 
			ns.getView().findTriangles();
			break;
		case KeyEvent.VK_F6:
			fonttype++;
			fonttype=fonttype%3;
			break;
		case KeyEvent.VK_F7:
			break;
		case KeyEvent.VK_F8: 
			break;
		case KeyEvent.VK_F9: 
			break;
		case KeyEvent.VK_F12: 
			break;
		case KeyEvent.VK_F11: 
			break;
		}
	}

	private void nameCurrentAttribute() {
		ns.global.altNameByAttribute(attribute);
	}

	public void keyReleased(KeyEvent evt) {
		CTRL = evt.isControlDown();
		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_F2: 
			inflate=false;
			break;
		case KeyEvent.VK_SHIFT:
			break;
		}
	}

	public void keyTyped(KeyEvent evt) {
	}

	public void mouseClicked(MouseEvent evt) {
	}

	public void mouseDragged(MouseEvent evt) {
		//		System.out.println("SemaSpace.mouseDragged()"+select);
		pressed = false;
		newX = evt.getX();
		newY = evt.getY();
		float diffx = (mouseX - newX) /glD.getWidth();
		float diffy = (mouseY - newY)/glD.getWidth();
		mouseY = evt.getY();
		mouseX = evt.getX();
		Node picked = ns.getView().getNodeByID(pickID);

		if (select&&!SwingUtilities.isRightMouseButton(evt)&&picked!=null&&ns.getView().fNodes.contains(picked)) { //drag a node
			float wHeight = glD.getHeight();
			float wWidth = glD.getWidth();
			float dragX = mouseX-(wWidth/2f);
			float dragY = mouseY-(wHeight/2f);

			float screenfactor = (float)(cam.getDist()*1.4f*Math.tan((FOV/2)*TWO_PI/360)/(Math.sqrt(wHeight*wHeight+wWidth*wWidth)/2f));

			// drag a node
			float localX = cam.getX()+dragX*screenfactor;
			float localY = cam.getY()-dragY*screenfactor;

			picked.pos.x = (float)Math.cos(cam.getYRot()*TWO_PI/360)*localX;
			picked.pos.y = (float)Math.cos(cam.getXRot()*TWO_PI/360)*localY;
			picked.pos.z = (float)Math.sin(cam.getXRot()*TWO_PI/360)*localY-(float)Math.sin(cam.getYRot()*TWO_PI/360)*localX+cam.getZ();

			//			if (evt.isShiftDown()) 
			//			if (!layout.isLocked(picked))
			if (!evt.isAltDown()) layout.layoutLockNode(picked,picked.pos, ns.getView());
			else layout.layoutLockRemove(picked,ns.getView());
		}

		else {
			//zoom
			if (SwingUtilities.isRightMouseButton(evt)) {
				zoomNew *= 1-(diffy*0.08f*deltatime) ;
				zoomNew = Math.min(zoomNew, zfar);
				zoomNew = Math.max(zoomNew, znear);
			}
			else 
				//rotate (only in 3d mode)
				if (!flat) {
					yRotNew += diffx*deltatime*10;
					xRotNew += Math.cos(cam.getXRot()*TWO_PI/360)*diffy*deltatime*10;
				}
			//drag cam (only in 2d mode)
				else {
					Vector3D offV = new Vector3D(diffx,-diffy,0f);
					offV.mult(zoomNew*2f);
					focus.add(offV);
				}
		}
	}

	public void mouseEntered(MouseEvent evt) {}

	public void mouseExited(MouseEvent evt) {}

	public void mouseMoved(MouseEvent evt) {
		mouseY = evt.getY();
		mouseX = evt.getX();
		moved = true;
	}

	public void mousePressed(MouseEvent evt) {
		moved = true;
		pressed = true;
		mouseY = evt.getY();
		mouseX = evt.getX();
	}

	public void mouseReleased(MouseEvent evt) {
		pressed = false;
		select = false;
	}

	public void addEdge(String a, String b) {
		ns.global.addEdge(a,b);
		ns.getView().addEdge(a,b);
		ns.getView().updateNet();
		updateUI();
	}

	private void clearRollover() {
		for (Node n:ns.getView().nNodes) n.setRollover(false);
		for (Edge e:ns.getView().nEdges) e.setRollover(false);
	}

	public void delIsolated() {
		HashSet<Node> ne = new HashSet<Node>();
		for (Node n:ns.getView().nNodes) {
			if (n.adList.size()==0&&n.inList.size()==0) ne.add(n);
		}
		for (Node n:ne) ns.getView().removeNode(n);
		updatePick();
	}

	public void delNodesAtt() {
		HashSet<Node> ne = new HashSet<Node>();
		ns.global.updateNet();
		for (Node n:ns.global.nNodes) {
			if (n.hasAttribute(attribute)) ne.add(n);
		}
		for (Node n:ne) {
			ns.global.removeNode(n);
			ns.getView().removeNode(n);
		}
		updatePick();
	}

	public void delFramed(boolean inv) {
		boolean i=false;
		HashSet<GraphElement> ne = new HashSet<GraphElement>();

		// del nodes
		for (Node n:ns.getView().nNodes) {
			if (inv) i = !n.isFrame(); else i = n.isFrame();
			if (i) ne.add(n);
		}
		for (GraphElement n:ne) {
			ns.getView().removeNode((Node)n);
		}
		ne.clear();
		//del edges
		for (Edge e:ns.getView().nEdges) {
			if (inv) i = !e.isFrame(); else i = e.isFrame();
			if (i) ne.add(e);
		}
		for (GraphElement e:ne) {
			ns.getView().removeEdge((Edge) e);
		}
		updatePick();
	}

	public void delSelected() {
		Node sel = ns.getView().getNodeByID(pickID);
		if (sel==null) return; 
		ns.getView().removeNode(sel);
		updatePick();
	}

	public void delRegion( boolean b) {
		HashSet<Node> ne = new HashSet<Node>();

		for (Node n:ns.getView().nNodes) {
			if (n.isPicked()) ne.add(n);
		}
		if (!b) {
			for (Node n:ne) {
				ns.getView().removeNode(n);
			}
		} else {
			HashSet<Node> ne2 = new HashSet<Node>();
			ne2.addAll(ns.getView().nNodes);
			for (Node n:ne2) {
				if (!ne.contains(n)) ns.getView().removeNode(n);
			}
		}
		updatePick();
	}

	public void delAll(){
		ns.getView().clearNet();
	}

	private void downloadTextures() {
		GL gl = glD.getGL();
		if(gl!=null) for (Node n:ns.global.nNodes) {
			n.deleteTexture(gl);
		}
		if (!textures) return;
		fileIO.loadTexturesUrl(texfolder, ns.getView(), thumbsize);
	}

	public boolean get3D() {
		return flat;
	}

	public int getAgeThresh() {
		return ageThresh;
	}

	public String getAttribute() {
		return attribute;
	}

	public float getClusterRad() {
		return clusterRad;
	}
	public int getDepth() {
		return searchdepth;
	}

	public float getDistance() {
		return standardNodeDistance;
	}

	public int getFonttype() {
		return fonttype;
	}

	public String getEdgeUrl() {
		return edgeUrl;
	}

	public boolean getCalc() {
		return calculate;
	}

	public float getFrame() {
		return frame;
	}

	public int getOverID() {
		return overID;
	}

	public float getPermInflate() {
		return perminflate;
	}

	public int getPickdepth() {
		return pickdepth;
	}

	public Node getPicked() {
		Node picked = null;
		if (pickID!=-1) {
			picked = ns.global.getNodeByID(pickID);
		}
		return picked;
	}

	public float getRepell() {
		return  repellDist;
	}

	public float getRepStr() {
		return repellStrength;
	}

	public float getSize() {
		return nodeSize;
	}


	public float getStrength() {
		return strength;
	}

	public SwingSema getSwing() {
		return swingapp;
	}

	public String getUrl() {
		return url;
	}


	public boolean isCluster() {
		return cluster;
	}

	public boolean isRadial() {
		return radial;
	}

	public boolean isRepN() {
		return repNeighbors;
	}
	public boolean isTree() {
		return tree;
	}

	public void lockAll() {
		layout.layoutLocksAll();
	}

	public void locksRemove() {
		layout.layoutLocksRemove();
	}

	public void netExpandAll() {
		netExpandNodes(ns.getView().nNodes);
	}

	public void netExpandFramed() {
		HashSet<Node> framed = new HashSet<Node>();
		for (Node n:ns.getView().nNodes) if (n.isFrame()) framed.add(n);
		if (framed.size()==0) return;
		netExpandNodes(framed);
	}

	private void netExpandNodes(HashSet<Node> framed) {
		HashSet<Node> zi = ns.getView().distances.getNodesAtDistance(0);
		Node z = null;
		if (zi!=null&&zi.size()>0) z = zi.iterator().next();
		int max = ns.getView().distances.getMaxDist();

		for (Node n:framed) layout.layoutLockNode(n, n.pos, ns.getView());
		Net result = ns.getView().generateSearchNet(ns.global,framed, 1 );

		//		if ((result.eTable.size()+result.nNodes.size())>(ns.view.eTable.size()+ns.view.nNodes.size()))
		//see if net did actually grow
		{ 
			ns.getView().netMerge(result);
			ns.getView().app.clearFrames(ns.getView());
		}
		if (z!=null) ns.getView().distances.findSearchDistances(z, max+1);
		downloadTextures();
		ns.getView().updateNet();
		updateUI();
	}

	public void netExpandNode() {
		Node sel = ns.getView().getNodeByID(pickID);
		if (sel==null) return;
		HashSet<Node> n = new HashSet<Node>();
		n.add(sel);
		netExpandNodes(n);
	}

	private void netInit() {
		ns.getView().updateNet();
		layout.replist.clear();
		layout.setNet(ns.getView());
		//		layout.layoutNodePosRandomize();
		layout.layoutBox(ns.getView().nNodes);
		//		layout.layoutConstrainCircle();
		layout.layoutLocksRemove();
		starttime = System.currentTimeMillis();
		lasttime = starttime;
		pickID=-1;
		updatePick(pickID);
		if (tree) layout.layoutTree(radial);
		focus.setXYZ(0,0,0);
		downloadTextures();
		updateUI();
	}

	public void netLoad(boolean tab) {
		clearNets();
		//		try {
		//		downloadNet(getUrl());
		//		if (nodeUrl!=null) downloadNodeData(nodeUrl);
		//		if (edgeUrl!=null) downloadEdgeData(edgeUrl);
		//		} catch (IOException e) {
		//		}
		if (loadNetwork(new File(filename), tab)) netStartRandom(false);
	}

	public boolean loadNetwork(File file, boolean tab) {
		boolean success = ns.edgeListLoad(file, tab);
		if (success) {
			File node = new File(file.getAbsoluteFile()+".n"); //$NON-NLS-1$
			ns.nodeListLoad(node, tab);
		} 
		ns.getView().updateNet();
		updateUI();
		return success;
	}

	public void nodeListLoad(File file2, boolean tab) {
		ns.nodeListLoad(file2, tab);
		ns.getView().updateNet();
		updateUI();
	}

	public void netSearchPicked(boolean add) {
		Node picked = getPicked();
		if (picked!=null) {
			initTree = true;
			netStartString(picked.name, add);
		}
	}

	public void netStartNode(Node n, boolean add) {
		initTree = true;
		ns.search(n, searchdepth, add);
	}

	public void netStartString(String text, boolean add) {
		initTree = true;
		ns.setView(ns.search(text, searchdepth, add));
		netInit();
	}

	public void netSearchSubstring(String text, boolean add) {
		//		Net searchNet;
		//		if (text.length()==0) {
		//			return;
		//		}
		//		else {
		//			initTree = true;
		//			searchNet = nets.global.generateSubstringSearchNet(nets.global, text, searchdepth, getAttribute());
		//		}
		//		if (add) nets.view.netMerge(searchNet); else net=searchNet; 
		//		
		initTree = true;
		ns.setView(ns.search(text, searchdepth, add, getAttribute()));
		netInit();
	}
	public void netShowAll(){
		initTree = true;
		if (attribute == "none") { //$NON-NLS-1$
			ns.setView(ns.global.clone());}
		else {
			ns.setView(new Net(this));
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(attribute)) {
					ns.getView().addNode(n);
				}
			}
		}
		ns.getView().app.clearFrames(ns.getView());
		netInit();
	}

	public void netStartFirst(boolean add) {
		int ID = 0;
		Node n = (Node)ns.global.nNodes.toArray()[ID];
		netStartNode(n, add);
	}

	public void netStartRandom(boolean add) {
		if (attribute!="none") { //$NON-NLS-1$
			HashSet<Node> hs = new HashSet<Node>();
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(attribute)) hs.add(n);
			}
			if (hs.size()==0) return;
			int ID = (int)(Math.random()*hs.size());
			Node res = (Node)hs.toArray()[ID];
			netStartNode(res, add);
		} else {
			if (ns.global.nNodes.size()==0) return;
			int ID =  new Random().nextInt(ns.global.nNodes.size());
			Node res = (Node) ns.global.nNodes.toArray()[ID];
			netStartNode(res, add);
		}
		initTree = true;
		netInit();
		//		updateUI();
	}

	public void setAgeThresh(int age) {
		this.ageThresh = age;
	}

	public void setAttractStr(float str) {
		strength = str;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setCacheDir(String string) {
		cacheDir=string;

	}
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
	public void setClusterRad(float a) {
		clusterRad = a;

	}

	public void setDepth(int value) {
		searchdepth = value;
	}
	public void setDistance(float f) {
		standardNodeDistance = f;
	}

	public void setEdgeUrl(String edgeUrl) {
		this.edgeUrl = edgeUrl;
	}

	public void setFilename(String selectedFile) {
		filename = selectedFile;
	}

	//	public void setFilepath(String string) {
	//		filepath= string;
	//
	//	}
	public void setFonttype(int fonttype_) {
		fonttype = fonttype_;
	}

	public void setCalc(boolean b) {
		calculate = b;
	}

	public void setInflate(boolean inf) {
		if(inf);
		starttime = System.currentTimeMillis();
		lasttime = starttime;
	}

	public void setNodeUrl(String nodeurl) {
		nodeUrl = nodeurl;

	}

	public void setOverID(int overID) {
		this.overID = overID;
	}

	public void setPermInflate(float f) {
		perminflate=f;
	}

	public void setPickdepth(int pickdepth) {
		this.pickdepth = pickdepth;
	}

	public void setPickID(int pickID) {
		this.pickID = pickID;
		updatePick(pickID);
	}

	public void setRadial(boolean selected) {
		radial = selected;
	}

	public void setRepell(boolean repell_) {
		repell = repell_;
	}

	public void setRepell(float value) {
		repellDist = value;

	}

	public void setRepellStr(float parseFloat) {
		repellStrength = parseFloat;

	}

	public void setRepN(boolean repN) {
		this.repNeighbors = repN;
	}

	public void setRepStr(float f) {
		repellStrength = f;
	}

	public void setSize(float f) {
		nodeSize = f;
	}

	public void setStrength(float f) {
		strength = f;
	}
	public void setSwing(SwingSema sema) {
		swingapp=sema;
	}
	public void setTexFolder(String file2) {
		texfolder = file2;
	}

	public void setTree(boolean selected) {
		if (!tree&&selected) initTree = true;
		tree = selected;
		if (tree) ns.getView().clearClusters();
		else {
			ns.getView().findClusters();
			layout.clustersSetup(glD.getGL());
			updatePick();
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setXRotNew(float rotNew) {
		xRotNew = rotNew;
	}

	public void setYRotNew(float rotNew) {
		yRotNew = rotNew;
	}

	private void statusMsg() {
		String line = ns.getView().nNodes.size()+" nodes, "+ns.getView().eTable.size()+" edges\n"; //$NON-NLS-1$ //$NON-NLS-2$
		//		line += "Xpos:"+mouseX+" Ypos:"+mouseY+" w:"+glD.getWidth()+" h:"+glD.getHeight();
		//		line += "\ncamX:"+cam.getX()+", camY:"+cam.getY()+", camZ:"+cam.getZ();
		//		line += "\ncamXrot:"+cam.getXRot()+", camYrot:"+cam.getYRot()+", camDist:"+cam.getDist();
		//		line += "\npID"+pickID+" selX:"+(int)nets.view.getPosByID(pickID).x+" selY:"+(int)nets.view.getPosByID(pickID).y+" selZ:"+(int)nets.view.getPosByID(pickID).z;
		//		line += "\n"+Math.sin(cam.getXRot()*TWO_PI/360);
		Node tmp = ns.global.getNodeByID(pickID);
		Edge tmp2 = ns.global.getEdgeByID(pickID);
		if (tmp!=null) line += "\n"+tmp.name+", attr:"+tmp.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		if (tmp2!=null) line += "\n"+tmp2.name +", attr:"+tmp2.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		if (swingapp!=null) swingapp.setMsg(line);
	}

	public void toggle3D() {
		flat=!flat;
		changed=true;
	}
	public void updatePick() {
		updatePick(pickID);
	}
	void updatePick(int pickID2) {
		if (pickID2 == -1) ns.getView().distances.clearPick();
		ns.getView().distances.findPickDistances(pickID2,pickdepth);
		//		}
		layout.applyPickColors();
	}

	void updateTime(){
		currenttime = System.currentTimeMillis();
		elapsedtime = currenttime-starttime;
		deltatime = currenttime-lasttime;
		lasttime = currenttime;
		zInc = (zoomNew-cam.getDist());
		xRotInc = (xRotNew-cam.getXRot());
		yRotInc = (yRotNew-cam.getYRot());
		frame = (elapsedtime/100f)%100f;
	}
	void updateUI() {
		swingapp.updateUI(ns);
	}
	void redrawUI() {
		swingapp.redrawUI();
	}



	public HashSet<GraphElement> findSubstringAttributes(String text, String key) {
		String subString=text.toLowerCase();
		HashSet<GraphElement> resultL = new HashSet<GraphElement>();
		resultL.clear();
		HashSet<GraphElement> source = new HashSet<GraphElement>();
		source.addAll(ns.global.nNodes);
		source.addAll(ns.global.nEdges);

		for (GraphElement n:source){
			String att = n.getAttribute(key);
			if (key=="none") { //$NON-NLS-1$
				att=n.altName;
				if (att==null) att= n.name;
			}
			if (att==null) att= ""; else //$NON-NLS-1$
				att = att.toLowerCase();

			if (att.contains(subString)) {
				n.setFrame(true);
				resultL.add(n);
			}
			else n.setFrame(false);
		}
		return resultL;
	}

	public void clearNets() {
		ns.clear();
	}

	public int getPicSize() {
		return picSize;
	}

	public void setPicSize(int picSize) {
		this.picSize = picSize;
	}

	public void setTime(boolean selected) {
		timeline = selected;
	}
	public boolean isTime(){
		return timeline;
	}

	public void setNodeVar(float value) {
		nodevar = value;
	}

	public float getNodevar() {
		return nodevar;
	}

	public void setNodevar(float nodevar) {
		this.nodevar = nodevar;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public float getVal() {
		return val;
	}
	public void resetCam() {
		zInc = 700;
		zoomNew = zInc;
		focus.setXYZ(0, 0, 0);
		cam.posAbsolute(glD,0f,0f,zInc,focus);
	}
	public void camOnSelected() {
		zInc = 700;
		zoomNew = zInc;
		Node picked = getPicked();
		if (picked != null) focus.setXYZ(picked.pos.copy()); else focus.setXYZ(0,0,0);
		cam.posAbsolute(glD,0f,0f,zInc,focus);
	}

	public void layoutBox() {
		layout.layoutBox(ns.getView().fNodes);
		calculate = false;
		updateUI();
	}

	public void layoutCircle() {
		layout.layoutConstrainCircle(ns.getView().fNodes);
		calculate = false;
		updateUI();
	}

	public void layoutForce() {
		float tmp = perminflate;
		boolean rep = repell;
		calculate = true;
		repell = false;
		perminflate=50;
		for (int i=0; i<15; i++) layout(); //inflate
		perminflate=tmp;
		for (int i=0; i<Math.max(5, (int)(30000f/ns.getView().nEdges.size())); i++) layout(); //distance, no repell
		repell=rep;
		for (int i=0; i<15; i++) layout(); //repell
		calculate = false;
		updateUI();
	}


	public void screenshot (int width, int height, String filename2) {
		if (!GLDrawableFactory.getFactory().canCreateGLPbuffer()) return;
		boolean f = flat;
		flat = false;
		//		int width = glD.getWidth()*2;
		//		int height = glD.getHeight()*2;

		GLCapabilities caps = new GLCapabilities();
		GLPbuffer pbuffer = GLDrawableFactory.getFactory().createGLPbuffer(caps, null, width, height, null);
		pbuffer.getContext().makeCurrent();
		GL gl = pbuffer.getGL();
		moved = false;

		//		FontRenderContext context = FTFont.STANDARDCONTEXT;
		//		texturefont = new FTGLTextureFont(font,context);
		texturefont.setGLGLU(gl, new GLU());
		texturefont.faceSize(70f);

		renderPbuffer(gl, width, height);

		try {
			Screenshot.writeToTargaFile(new File(filename2), width, height);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pbuffer.destroy();
		screenshotcounter++;

		glD.getContext().makeCurrent();
		texturefont.setGLGLU(gl, glu);
		texturefont.faceSize(70f);
		flat = f;
	}

	public void clearFrames(Net net) {
		for (Node n:ns.getView().nNodes){
			n.setFrame(false);
		}
		for (Edge e:ns.getView().nEdges){
			e.setFrame(false);
		}
	}

	public void setSubnet(String out) {
		HashSet<Edge> subnet = ns.getSubnet(out);
		clearFrames(ns.global);
		for (Edge e:ns.getView().nEdges) e.setFrame(false);
		for (Edge e:subnet) {
			if (ns.getView().nEdges.contains(e)){
				e.setFrame(true);
				e.getA().setFrame(true);
				e.getB().setFrame(true);
			}
		}
	}

	public void saveNet() {
		ns.addSubnet((HashSet<Edge>) ns.getView().nEdges.clone());
		updateUI();
	}


	public void netRemoveLeafs() {
		ns.getView().leafDelete();
		updateUI();
	}

	public void netRemoveClusters() {
		ns.getView().clustersDelete();
		updateUI();
	}

	public float getSquareness() {
		return Math.max(h, 1f/h);
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public boolean isRender() {
		return render;
	}

	public void setEdges(boolean edges) {
		this.edges = edges;
	}

	public boolean isEdges() {
		return edges;
	}

	public void removeNet(String net) {
		ns.removeSubnet(net);
		updateUI();
	}

	public void setView(String net) {
		ns.setView(net);
	}

	public void setLabelsize(float labelsize) {
		this.labelsize = labelsize;
	}

	public float getLabelsize() {
		return labelsize;
	}

	public void setLabelVar(float labelVar) {
		this.labelVar = labelVar;
	}

	public float getLabelVar() {
		return labelVar;
	}

	public void setTabular(boolean tabular) {
		this.tabular = tabular;
	}

	public boolean isTabular() {
		return tabular;
	}

	public void setGroups(boolean groups) {
		this.groups = groups;
	}

	public boolean isGroups() {
		return groups;
	}

	public void exportSVG(String file) {
		svgFile = file;
		SVGexport=true;
	}

	public void setLabelsEdgeDir(boolean labelsEdgeDir) {
		//		this.tilt=false;
		this.labelsEdgeDir = labelsEdgeDir;
	}

	public boolean isLabelsEdgeDir() {
		return labelsEdgeDir;
	}
}