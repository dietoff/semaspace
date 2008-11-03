package semaGL;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

import javax.swing.SwingUtilities;
import nehe.TextureReader.Texture;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;

import net.sourceforge.ftgl.FTBBox;
import net.sourceforge.ftgl.glfont.FTFont;
import net.sourceforge.ftgl.glfont.FTGLBitmapFont;
import net.sourceforge.ftgl.glfont.FTGLExtrdFont;
import net.sourceforge.ftgl.glfont.FTGLOutlineFont;
import net.sourceforge.ftgl.glfont.FTGLPixmapFont;
import net.sourceforge.ftgl.glfont.FTGLPolygonFont;
import net.sourceforge.ftgl.glfont.FTGLTextureFont;
import ftgl.util.loader.LibraryLoader;

public class SemaSpace implements GLEventListener, MouseListener, MouseMotionListener, KeyListener  {
	private static final long serialVersionUID = -1864003907508879499L;
	GLUT glut = new GLUT();
	String filename = "./data/data.txt";
	//	String filepath = "./";
	String texfolder = "./textures/";
	String cacheDir = "./cache/";
	public  String texurl = "http://";
	int searchdepth = 1;
	private  boolean changed=false;
	public  boolean fadeEdges=false;
	public  boolean fadeNodes=false;
	float standardNodeDistance = 150f;
	float repellDist = 25f;
	float nodeSize = 5f;
	int picSize = 5;
	float strength = 0.15f;
	private float val = 0.20f;
	float repellStrength= 0.30f;
	public float clusterRad=10;
	public float radialDist = 250;
	public float boxdist = 100f;
	String url;
	String nodeUrl;
	String edgeUrl;
	int pickdepth = 2;
	long inflatetime = 500;
	int fonttype = 1;
	float edgewidth = 0.9f;
	float textwidth = 0.8f;
	float[] frameColor ={0f,1f,0f,0f};
	float[] pickColor ={1f,0f,0f,0.8f};
	float[] rollOverColor = {1f,0.5f,0f,0.8f};
	float[] nodeColor = {0.2f,0.2f,0.5f,0.8f};
	public float[] edgeColor = {0.7f,0.7f,1f,0.8f};;
	boolean opt = false; //optimized repelling
	boolean flat = true;
	public boolean animated=false;
	boolean repell = true;
	boolean distance = true;
	boolean inflate = true;
	public int pickID=-1;
	boolean pressed = false;
	boolean CTRL = false;
	boolean calculate = true;
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
	boolean edges=true;
	public boolean directed=true;
	private GLU glu;
	FileIO fileIO;
	public int ageThresh=Integer.MAX_VALUE;
	int perminflate=0;
	private SwingSema swingapp;
	private float frame;
	public Texture tex;
	public boolean texRead=false;
	protected boolean render=true;
	private boolean tree = false;
	private boolean radial = false;
	private boolean repNeighbors = false;
	private int overID;
	boolean moved;
	Layouter layout;
	private String attribute="none";
	int thumbsize=128;
	public Graphics2D j2d;
	private boolean timeline;
	private float nodevar=0.3f;
	private boolean repToggle=true;
	protected boolean textures=true;
	private Font font;
	FTFont outlinefont;
	FTFont polyfont;
	public float fontsize = 10f;
	private boolean initTree;
	private boolean screenshot = false;
	public boolean inheritEdgeColorFromNodes = false;
	protected boolean drawClusters= true;
	//	private NodeRenderer nodeRender;
	//	private GLPbuffer pbuffer;
	NetStack ns;
	FTGLTextureFont texturefont;
	private boolean cmd;
	protected boolean tilt=true;

	public SemaSpace(){
	}

	public void init(GLAutoDrawable gLDrawable) {
		glD = gLDrawable;
		GL gl = gLDrawable.getGL();
		glu = new GLU();
		gLDrawable.addMouseListener(this);
		gLDrawable.addMouseMotionListener(this);
		gLDrawable.addKeyListener(this);
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
		cam = new Cam(gLDrawable,FOV,0,0,zInc,focus,znear,zfar);

		initFonts(gl);

		fileIO = new FileIO(this);
		ns = (new NetStack(this));
		layout = new Layouter(this);
		if (random) ns.global.generateRandomNet (100, 146);		// random network
		else netLoad();
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
		//
		//		font = Font.decode("Arial Unicode MS").deriveFont(172f);
		font = Font.decode("Times New Roman").deriveFont(172f);
		FontRenderContext context = FTFont.STANDARDCONTEXT;
		//		outlinefont = new FTGLOutlineFont(font,context);
		//		polyfont = new FTGLPolygonFont(font,context);
		texturefont = new FTGLTextureFont(font,context);
		//		geofont = new FTGLPolygonFont(font, context);
		//		polyfont.setGLGLU(gl, glu);
		//		polyfont.faceSize(10f);
		//		outlinefont.setGLGLU(gl, glu);
		//		outlinefont.faceSize(10f);
		texturefont.setGLGLU(gl, glu);
		texturefont.faceSize(70f);
	}

	public void display(GLAutoDrawable gLDrawable) {
		try {

			if (screenshot) {
				//				int w = glD.getWidth();
				//				int h = glD.getHeight();
				//				glD.setSize(2000, 2000);
				//				reshape(glD, 0, 0, 2000, 2000);
				updateTime();
				layout();
				if (render) render();
				glD.swapBuffers();
				screenshot(glD.getWidth(), glD.getHeight(),"capt.tga");
				screenshot=false;
			}

			updateTime();
			//			if (nets.view.nNodes.size()==globalnets.view.nNodes.size()&&nets.view.eTable.size()==globalnets.view.eTable.size()) net=globalNet;
			layout();
			render();
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

	}

	void layout() {
		float str = strength;
		boolean rep = repell;
		repToggle=!repToggle;

		layout.setNet(ns.view);

		if (tree){

			if (calculate&&distance&&!initTree) layout.layoutDistanceTree(standardNodeDistance, getVal(), str); // +nets.view.nNodes.size()/5
			if (calculate&&rep&&!initTree) layout.layoutRepFruchtermannRadial(repellDist,repellStrength);

			if (initTree) {

				for (int i=0;i<50;i++) {
					layout.layoutDistanceTree(0, 1, 0.5f);
					layout.initRadial(0, 0, radialDist);
					layout.layoutEgocentric();
					//					layout.layoutFlat();
				}
				initTree = false;
			}
			layout.layoutLockPlace(ns.view);
			layout.layoutEgocentric();
			layout.layoutFlat();
		}
		else {

			if (calculate) {
				if (repNeighbors) layout.layoutRepNeighbors(repellStrength/4f, standardNodeDistance, ns.view);

				float inf = inflatetime-elapsedtime;
				if ((inf > 0&&inflate)&&ns.view.eTable.size()>1&&ns.view.fNodes.size()>1) {
					float r = elapsedtime/inflatetime;
					layout.layoutInflate(Math.min(ns.view.eTable.size(),1000)*(1-r),ns.view);
					//					if (inf>500)layout.layoutInflate(200, nets.view);
					str = 0.3f;
					rep = false;
					//				render = false;
				}
				//			else render = true;

				if (perminflate>0){
					layout.layoutInflate(perminflate, ns.view);
				}

				if (distance)
					layout.layoutDistance(standardNodeDistance , getVal(), str, ns.view); // +nets.view.nNodes.size()/5

				if (changed) {
					if (!flat) {
						//						layout.layoutNodePosZNoise();
						changed = false;
					}
				}

				layout.layoutTags(ns.view);

				if (rep){
					layout.layoutRepell(repellDist,repellStrength, ns.view);
				}

				layout.layoutLockPlace(ns.view);

				if (timeline) layout.layoutTimeline();

				if (flat) layout.layoutFlat();
			}
		}
	}

	public void render(){
		GL gl = glD.getGL();
		//		if (!moved) {
		if (FOG&&!flat) gl.glEnable(GL.GL_FOG); else gl.glDisable(GL.GL_FOG);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		cam.posIncrement(glD, yRotInc, xRotInc, zInc, focus); 
		if (edges) layout.renderEdges(gl, fonttype);
		if (!tree) layout.renderClusters(gl);
		//			layout.renderTags(gl,ns.view);
		layout.renderLabels(gl,fonttype);
		layout.renderNodes(gl,fonttype);
		//			layout.renderSelection(fonttype);
		gl.glFlush();
		gl.glFinish();
		//		}
		//		else
		//		{
		if (moved) {
			setOverID(selectCoord());
			if (pressed) select(); //initiate picking
			clearRollover();
			GraphElement n = ns.view.getByID(overID);
			if (n!=null) n.setRollover(true);
			moved=false;
		}
		statusMsg();
		//			redrawUI();
	}

	void select(){
		pickID = getOverID();
		if (pickID!=-1) select = true;
		pressed=false;
		if (CTRL) focus.setXYZ(ns.view.getPosByID(pickID)); //point to selected node's position
		updatePick(pickID);
	}

	int selectCoord(){
		GL gl = glD.getGL();
		GLU glu = new GLU();

		int buffsize = (ns.view.nNodes.size()+ns.view.nEdges.size())*4;
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
		cam.posIncrement(glD, yRotInc, xRotInc, zInc, focus);

		layout.renderNodes(gl, 0); //render the nets.viewwork 
		layout.renderEdges(gl, 0);
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
			float x=0;
			break;
		case KeyEvent.VK_SHIFT:
			break;
		case KeyEvent.VK_F1:
			screenshot  = true;
			break;
		case KeyEvent.VK_F2: 
			inflate=true;
			System.out.println("inflate = true");
			break;
		case KeyEvent.VK_F3: 
			opt=!opt;
			System.out.println("optimised repell= "+opt);
			break;
		case KeyEvent.VK_F4: 
			layout.layoutLocksRemove();
			break;
		case KeyEvent.VK_F5: 
			ns.view.findTriangles();
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
		Node picked = ns.view.getNodeByID(pickID);

		if (select&&!SwingUtilities.isRightMouseButton(evt)&&picked!=null&&ns.view.fNodes.contains(picked)) { //drag a node
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
			layout.layoutLockNode(picked,picked.pos, ns.view);
			//			else layout.layoutRemoveLock(picked);
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
		ns.view.addEdge(a,b);
		ns.view.updateNet();
		updateUI();
	}

	private void clearRollover() {
		for (Node n:ns.view.nNodes) n.setRollover(false);
		for (Edge e:ns.view.nEdges) e.setRollover(false);
	}

	public void delIsolated() {
		HashSet<Node> ne = new HashSet<Node>();
		for (Node n:ns.view.nNodes) {
			if (n.adList.size()==0&&n.inList.size()==0) ne.add(n);
		}
		for (Node n:ne) ns.view.removeNode(n);
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
			ns.view.removeNode(n);
		}
		updatePick();
	}

	public void delFramed(boolean inv) {
		boolean i=false;
		HashSet<Node> ne = new HashSet<Node>();
		for (Node n:ns.view.nNodes) {
			if (inv) i = !n.isFrame(); else i = n.isFrame();
			if (i) ne.add(n);
		}
		for (Node n:ne) {
			ns.view.removeNode(n);
		}
		updatePick();
	}

	public void delSelected() {
		Node sel = ns.view.getNodeByID(pickID);
		if (sel==null) return; 
		ns.view.removeNode(sel);
		updatePick();
	}

	public void delRegion( boolean b) {
		HashSet<Node> ne = new HashSet<Node>();

		for (Node n:ns.view.nNodes) {
			if (n.isPicked()) ne.add(n);
		}
		if (!b) {
			for (Node n:ne) {
				ns.view.removeNode(n);
			}
		} else {
			HashSet<Node> ne2 = new HashSet<Node>();
			ne2.addAll(ns.view.nNodes);
			for (Node n:ne2) {
				if (!ne.contains(n)) ns.view.removeNode(n);
			}
		}
		updatePick();
	}

	public void delAll(){
		ns.view.clearNet();
	}

	private void downloadTextures() {
		GL gl = glD.getGL();
		if(gl!=null) for (Node n:ns.global.nNodes) {
			n.deleteTexture(gl);
		}
		if (!textures) return;
		fileIO.loadTexturesUrl(texfolder, ns.view, thumbsize);
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

	public int getPermInflate() {
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

	public void netDownload(String urlpath) throws IOException{
		System.out.println("SemaSpace.download() "+urlpath);
		String dl = fileIO.getPage(urlpath);
		ns.view.edgelistParse(dl);
		ns.view.updateNet();
		System.out.println(ns.view.eTable.size());
		System.out.println(ns.view.nNodes.size());
	}

	public void netDownloadNodes(String urlpath) throws IOException{
		System.out.println("SemaSpace.download() "+urlpath);
		String dl = fileIO.getPage(urlpath);
		ns.view.nodelistParse(dl);
	}

	public void netExpandAll() {
		netExpandNodes(ns.view.nNodes);
	}

	public void netExpandFramed() {
		HashSet<Node> framed = new HashSet<Node>();
		for (Node n:ns.view.nNodes) if (n.isFrame()) framed.add(n);
		if (framed.size()==0) return;
		netExpandNodes(framed);
	}

	private void netExpandNodes(HashSet<Node> framed) {
		HashSet<Node> zi = ns.view.distances.getNodesAtDistance(0);
		Node z = null;
		if (zi!=null&&zi.size()>0) z = zi.iterator().next();
		int max = ns.view.distances.getMaxDist();

		for (Node n:framed) layout.layoutLockNode(n, n.pos, ns.view);
		Net result = ns.view.generateSearchNet(ns.global,framed, 1 );

		//		if ((result.eTable.size()+result.nNodes.size())>(ns.view.eTable.size()+ns.view.nNodes.size()))
		//see if net did actually grow
		{ 
			ns.view.netMerge(result);
			ns.view.app.clearFrames(ns.view);
		}
		if (z!=null) ns.view.distances.findSearchDistances(z, max+1);
		downloadTextures();
		ns.view.updateNet();
		updateUI();
	}

	public void netExpandNode() {
		Node sel = ns.view.getNodeByID(pickID);
		if (sel==null) return;
		HashSet<Node> n = new HashSet<Node>();
		n.add(sel);
		netExpandNodes(n);
	}

	private void netInit() {
		ns.view.updateNet();
		layout.setNet(ns.view);
		//		layout.layoutNodePosRandomize();
		layout.layoutBox(ns.view.nNodes);
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

	public void netLoad() {
		clearNets();
		//		try {
		//		downloadNet(getUrl());
		//		if (nodeUrl!=null) downloadNodeData(nodeUrl);
		//		if (edgeUrl!=null) downloadEdgeData(edgeUrl);
		//		} catch (IOException e) {
		//		}
		//		nets.view.loadNodeData(filename+".nodedata");
		//		nets.view.loadEdgeData(filename+".edgedata");
		if (edgeListLoad(new File(filename))) netStartRandom(false);
	}

	public boolean edgeListLoad(File file) {
		boolean success = ns.edgeListLoad(file);
		if (success) {
			File node = new File(file.getAbsoluteFile()+".n");
			ns.nodeListLoad(node);
		} 
		ns.view.updateNet();
		updateUI();
		return success;
	}

	public void nodeListLoad(File file2) {
		ns.nodeListLoad(file2);
		ns.view.updateNet();
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
		ns.view = ns.search(text, searchdepth, add);
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
		ns.view = ns.search(text, searchdepth, add, getAttribute());
		netInit();
	}
	public void netShowAll(){
		initTree = true;
		if (attribute == "none") {
			ns.view = ns.global.clone();}
		else {
			ns.view = new Net(this);
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(attribute)) {
					ns.view.addNode(n);
				}
			}
		}
		ns.view.app.clearFrames(ns.view);
		netInit();
	}

	public void netStartFirst(boolean add) {
		int ID = 0;
		Node n = (Node)ns.global.nNodes.toArray()[ID];
		netStartNode(n, add);
	}

	public void netStartRandom(boolean add) {
		if (attribute!="none") {
			HashSet<Node> hs = new HashSet<Node>();
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(attribute)) hs.add(n);
			}
			if (hs.size()==0) return;
			int ID = (int)(Math.random()*hs.size());
			Node res = (Node)hs.toArray()[ID];
			netStartNode(res, add);
		}else {
			if (ns.global.nNodes.size()==0) return;
			int ID = (int)(Math.random()*ns.global.nNodes.size());
			Node res = (Node)ns.global.nNodes.toArray()[ID];
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

	public void setPermInflate(int value) {
		perminflate=value;
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
		if (tree) ns.view.clearClusters();
		else {
			ns.view.findClusters();
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
		String line = ns.view.nNodes.size()+" nodes, "+ns.view.eTable.size()+" edges\n";
		//		line += "Xpos:"+mouseX+" Ypos:"+mouseY+" w:"+glD.getWidth()+" h:"+glD.getHeight();
		//		line += "\ncamX:"+cam.getX()+", camY:"+cam.getY()+", camZ:"+cam.getZ();
		//		line += "\ncamXrot:"+cam.getXRot()+", camYrot:"+cam.getYRot()+", camDist:"+cam.getDist();
		//		line += "\npID"+pickID+" selX:"+(int)nets.view.getPosByID(pickID).x+" selY:"+(int)nets.view.getPosByID(pickID).y+" selZ:"+(int)nets.view.getPosByID(pickID).z;
		//		line += "\n"+Math.sin(cam.getXRot()*TWO_PI/360);
		Node tmp = ns.global.getNodeByID(pickID);
		Edge tmp2 = ns.global.getEdgeByID(pickID);
		if (tmp!=null) line += "\n"+tmp.name+", attr:"+tmp.attributes.toString();
		if (tmp2!=null) line += "\n"+tmp2.name +", attr:"+tmp2.attributes.toString();
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
		if (pickID2 == -1) ns.view.distances.clearPick();
		ns.view.distances.findPickDistances(pickID2,pickdepth);
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


	public HashSet<Node> findSubstringAttributes(String text, String key) {
		String subString=text.toLowerCase();
		HashSet<Node> resultL = new HashSet<Node>();
		resultL.clear();

		for (Node n: ns.global.nNodes){
			String att = n.getAttribute(key);
			if (key=="none") {
				att=n.altName;
				if (att==null) att= n.name;
			}
			if (att==null) att= ""; else
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
		layout.layoutBox(ns.view.fNodes);
		calculate = false;
		updateUI();
	}

	public void layoutCircle() {
		layout.layoutConstrainCircle(ns.view.fNodes);
		calculate = false;
		updateUI();
	}

	public void layoutForce() {
		int tmp = perminflate;
		boolean rep = repell;
		calculate = true;
		repell = false;
		perminflate=50;
		for (int i=0; i<15; i++) layout(); //inflate
		perminflate=tmp;
		for (int i=0; i<Math.max(5, (int)(30000f/(float)ns.view.nEdges.size())); i++) layout(); //distance, no repell
		repell=rep;
		for (int i=0; i<15; i++) layout(); //repell
		calculate = false;
		updateUI();
	}


	public void screenshot(int w, int h, String file){
		try {
			Screenshot.writeToTargaFile(new File(file), w, w);
		} catch (GLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearFrames(Net net) {
		for (Node n:ns.view.nNodes){
			n.setFrame(false);
		}
	}

	public void setSubnet(String out) {
		HashSet<Edge> subnet = ns.getSubnet(out);
		clearFrames(ns.global);
		for (Edge e:ns.view.nEdges) e.setPicked(false);
		for (Edge e:subnet) {
			if (ns.view.nEdges.contains(e)) e.setPicked(true);
		}
	}

	public void saveNet() {
		ns.addEdges((HashSet<Edge>) ns.view.nEdges.clone());
		updateUI();
	}

	public void netRemoveLeafs() {
		ns.view.leafDelete();
		updateUI();
	}

	public void netRemoveClusters() {
		ns.view.clustersDelete();
		updateUI();
	}

	public float getSquareness() {
		return Math.max(h, 1f/h);
	}
}