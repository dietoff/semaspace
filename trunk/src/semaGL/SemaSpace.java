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
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;


import nehe.TextureReader.Texture;
import UI.SemaEvent;
import UI.SemaListener;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;

import data.BBox3D;
import data.Edge;
import data.GraphElement;
import data.Net;
import data.NetStack;
import data.Node;
import data.Vector3D;

import net.sourceforge.ftgl.glfont.FTFont;
import net.sourceforge.ftgl.glfont.FTGLOutlineFont;
import net.sourceforge.ftgl.glfont.FTGLPolygonFont;
import net.sourceforge.ftgl.glfont.FTGLTextureFont;

public class SemaSpace implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener  {
	private static final long serialVersionUID = -1864003907508879499L;
	GLUT glut = new GLUT();
	//	HashSet<String> map = Messages.getArray("map");
	String cacheDir = "./cache/"; //$NON-NLS-1$
	public  String texurl = "http://"; //$NON-NLS-1$
	private  boolean changed=false;
	public  boolean fadeEdges=false;
	public  boolean fadeNodes=false;
	String url;
	String nodeUrl;
	String edgeUrl;
	int fonttype = 1;
	float textwidth = 0.8f;
	public float[] pickGradEnd = {0f,1f,0f,0f};
	public float[] frameColor={0f,0f,1f,0.8f};
	float[] pickGradStart ={1f,0f,0f,0.8f};
	float[] rollOverColor = {1f,0.5f,0f,0.8f};
	public float[] nodeColor = {0.2f,0.2f,0.5f,0.8f};
	public float[] edgeColor = {0.7f,0.7f,1f,0.8f};
	boolean opt = false; //optimized repelling
	boolean layout2d = true;
	public boolean animated=false;
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
	String file[];
	final float TWO_PI =6.283185307179586476925286766559f;
	private float yRotInc, xRotInc = 0;
	private float yRotNew, xRotNew = 0;
	private float mouseY=0, newY=0;
	private float mouseX=0, newX=0;
	private float zInc = 300;
	private float zoomNew = zInc;
	Vector3D focus = new Vector3D(0f,0f,0f);
	Cam cam;
	private float h;
	int viewPort[] = new int[4];
	long starttime, elapsedtime, lasttime, deltatime, currenttime;
	private boolean FOG = true;
	boolean select;
	private float FOV = 70f;
	public GLAutoDrawable glD;
	private boolean edges=true;
	public boolean directed=true;
	private GLU glu;
	public FileIO fileIO;
	public int ageThresh=Integer.MAX_VALUE;
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
	public Graphics2D j2d;
	private boolean timeline;
	private Font font;
	FTFont outlinefont;
	FTFont hiQfont;
	private boolean initTree;
	public NetStack ns;
	int screenshotcounter = 0;
	private boolean tabular = false;
	private boolean SVGexport;
	private String svgFile;
	boolean labelsEdgeDir=true;
	public boolean fadeLabels=false;
	private int repellMax = 1000;
	private GraphRendererSVG SVGrenderer;
	private List<SemaListener> _listeners = new ArrayList<SemaListener>();
	private boolean SHIFT;
	private Net inflateGroup;
	public String splitAttribute = "; ";
	private float edgeAlpha;
	private boolean groups;
	private boolean enableSvg;
	private String fontFam;
	float maxLabelRenderDistance;
	public int shotres;
	private boolean textureFont;
	private float labelsize;
	private float labelVar;
	private boolean inheritEdgeColorFromNodes;
	public boolean drawClusters;
	private boolean tilt;
	private float invar;
	private float outvar;
	private boolean textures;
	private int loadMethod;
	private boolean cluster;
	private float perminflate;
	private int thumbsize;
	private String filename;
	private String texfolder;
	private int searchdepth;
	private float standardNodeDistance;
	private float repellDist;
	private float nodeSize;
	private int picSize;
	private float strength;
	private float val;
	private float repellStrength;
	private float clusterRad;
	private float radialDist;
	private float boxdist;
	private int pickdepth;
	private long inflatetime;
	private float edgewidth;
	private boolean exhibitionMode;
	private boolean repell;
	private boolean svgNodeCircles;
	public int edgeThreshold;

	public SemaSpace(){
		loadSemaParameters();
		fileIO = new FileIO(this);
		ns = (new NetStack(this));
		layout = new Layouter(this);
		renderer = new GraphRenderer(this);
		if (isEnableSvg()) SVGrenderer = new GraphRendererSVG(this);
		initFonts();
		netLoad();
	}

	private void loadSemaParameters() {
		edgeThreshold = Integer.parseInt(Messages.getString("edgeTresholdRepell"));
		setSvgNodeCircles(Boolean.parseBoolean(Messages.getString("SVGNodesCircles")));
		exhibitionMode = Boolean.parseBoolean(Messages.getString("exhibitionMode"));
		setRepell(Boolean.parseBoolean(Messages.getString("repellOn")));
		filename = Messages.getString("defaultFilename"); //$NON-NLS-1$
		setTexfolder(Messages.getString("textureDirectory")); //$NON-NLS-1$
		searchdepth = Integer.parseInt(Messages.getString("searchdepth"));
		setStandardNodeDistance(Float.parseFloat(Messages.getString("standardNodeDistance")));
		setRepellDist(Float.parseFloat(Messages.getString("repellDistance")));
		setNodeSize(Float.parseFloat(Messages.getString("nodeSize")));
		picSize = Integer.parseInt(Messages.getString("picSize"));
		strength = Float.parseFloat(Messages.getString("edgeStrength"));
		val = Float.parseFloat(Messages.getString("valenceFactor"));
		repellStrength = Float.parseFloat(Messages.getString("repellStrength"));
		clusterRad= Float.parseFloat(Messages.getString("clusterRadius"));
		setRadialDist(Float.parseFloat(Messages.getString("radialLayoutDistance")));
		setBoxdist(Float.parseFloat(Messages.getString("boxLayoutDistance")));
		pickdepth = Integer.parseInt(Messages.getString("pickDistance"));
		setInflatetime(Long.parseLong(Messages.getString("inflateTimeMs")));
		setEdgewidth(Float.parseFloat(Messages.getString("edgeWidth")));
		setPerminflate(Float.parseFloat(Messages.getString("permanentInflate")));
		setThumbsize(Integer.parseInt(Messages.getString("thumbnailRes")));
		cluster=Boolean.parseBoolean(Messages.getString("layoutClusters"));
		loadMethod = Integer.parseInt(Messages.getString("loadMethod")); //0 = local file, 1 = http, 2 = jar
		Color.decode(Messages.getString("pickGradientFar")).getComponents(pickGradEnd);
		Color.decode(Messages.getString("pickGradientCenter")).getComponents(pickGradStart);
		Color.decode(Messages.getString("rollOverColor")).getComponents(rollOverColor);
		Color.decode(Messages.getString("nodeColor")).getComponents(nodeColor);
		nodeColor[3]=Float.parseFloat(Messages.getString("nodeAlpha"));
		Color.decode(Messages.getString("edgeColor")).getComponents(edgeColor);
		Color.decode(Messages.getString("frameColor")).getComponents(frameColor);
		labelsEdgeDir = (Boolean.parseBoolean(Messages.getString("labelsEdgeDir")));
		repellMax = (int) Float.parseFloat(Messages.getString("repellMaxDist"));
		setEdgeAlpha(Float.parseFloat(Messages.getString("edgeAlpha")));
		groups =  Boolean.parseBoolean(Messages.getString("drawGroups"));
		setEnableSvg(Boolean.parseBoolean(Messages.getString("enableSVGexport")));
		setFontFam(Messages.getString("FontFamily"));
		shotres = Integer.parseInt(Messages.getString("screenshotResolution"));
		maxLabelRenderDistance = Float.parseFloat(Messages.getString("maxLabelRenderDistance"));
		textureFont= Boolean.parseBoolean(Messages.getString("useTextureFonts"));
		labelsize= Float.parseFloat(Messages.getString("labelSize"));
		labelVar= Float.parseFloat(Messages.getString("labelSizeVariance"));
		setInheritEdgeColorFromNodes(Boolean.parseBoolean(Messages.getString("inheritEdgeColorFromNodes")));
		drawClusters= Boolean.parseBoolean(Messages.getString("clusters"));
		setTilt(Boolean.parseBoolean(Messages.getString("tiltedLabels")));
		invar= Float.parseFloat(Messages.getString("nodeSizeInDegreeVariance"));
		outvar= Float.parseFloat(Messages.getString("nodeSizeOutDegreeVariance"));;
		textures= Boolean.parseBoolean(Messages.getString("textures"));
	}

	public void addEdge(String a, String b) {
		ns.global.addEdge(a,b);
		ns.getView().addEdge(a,b);
		ns.getView().updateNet();
		updateUI();
	}

	public synchronized void addSemaListener( SemaListener l ) {
		_listeners.add( l );
	}

	public void camOnSelected() {
		zInc = 300;
		zoomNew = zInc;
		Node picked = getPicked();
		if (picked != null) focus.setXYZ(picked.pos.copy()); else focus.setXYZ(0,0,0);
		cam.posAbsolute(glD,0f,0f,zInc,focus);
	}

	public void clearFrames(Net net) {
		for (Node n:ns.getView().nNodes){
			n.setFrame(false);
		}
		for (Edge e:ns.getView().nEdges){
			e.setFrame(false);
		}
	}

	public void clearNets() {
		ns.clear();
	}

	void clearPick() {
		ns.getView().distances.clearPick();
		layout.applyPickColors();
		pickID=-1;
	}

	private void clearRollover() {
		for (Node n:ns.getView().nNodes) n.setRollover(false);
		for (Edge e:ns.getView().nEdges) e.setRollover(false);
	}

	public void delAll(){
		ns.getView().clearNet();
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
		Net view = ns.getView();

		for (Node n:view.nNodes) {
			if (n.hasAttribute(attribute)) ne.add(n);
		}
		if (directed) {
			for (Node n:ne) {
				if (n.adList.size()>0&&n.inList.size()>0) {
					for (Node from:n.inList) {
						for (Node to:n.adList) {
							view.addEdge(from, to);
						}
					}
				}
			}
		}
		view.updateNet();

		for (Node n:ne) {
			view.removeNode(n);
		}
		view.updateNet();
		updatePick();
	}

	/**
	 * @param b - invert the selection
	 */
	public void delRegion( boolean b) {
		HashSet<Node> ne = new HashSet<Node>();

		for (Node n:ns.getView().nNodes) {
			if (n.isPickRegion()) ne.add(n);
		}
		clearPick();
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
		ns.getView().updateNet();

	}

	public void delSelected() {
		HashSet<Node> pickeds = getPickeds();
		clearPick();
		if (pickeds.size()>0){
			for (Node sel:pickeds){
				ns.getView().removeNode(sel);
			}
			ns.getView().updateNet();
		}
	}

	public void display(GLAutoDrawable gLDrawable) {
		try {
			updateTime();
			layout();
			//			if (calculate&&(deltatime > 1000)) calculate=false;
			render(gLDrawable.getGL());
		}
		catch (ConcurrentModificationException e) {
		}
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void exportSVG(String file) {
		svgFile = file;
		SVGexport=true;
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

	private SemaEvent fireSemaEvent(int semaEventCode) {
		SemaEvent evt = new SemaEvent( this, semaEventCode );
		Iterator<SemaListener> listeners = _listeners.iterator();
		while( listeners.hasNext() ) {
			( listeners.next() ).eventReceived( evt );
		}
		return evt;
	}

	private synchronized void fireSemaEvent(int semaEventCode, String msg) {
		fireSemaEvent(semaEventCode).setContent(msg);
	}

	public boolean get3D() {
		return layout2d;
	}

	public int getAgeThresh() {
		return ageThresh;
	}

	public String getAttribute() {
		return attribute;
	}

	public boolean getCalc() {
		return calculate;
	}

	public float getClusterRad() {
		return clusterRad;
	}

	public int getDepth() {
		return searchdepth;
	}

	public float getDistance() {
		return getStandardNodeDistance();
	}

	public String getEdgeUrl() {
		return edgeUrl;
	}

	public int getFonttype() {
		return fonttype;
	}

	public Net getInflateGroup() {
		return inflateGroup;
	}

	public float getInVar() {
		return invar;
	}

	public float getLabelsize() {
		return labelsize;
	}

	public float getLabelVar() {
		return labelVar;
	}

	public float getOutVar() {
		return outvar;
	}

	public int getOverID() {
		return overID;
	}

	public float getPermInflate() {
		return getPerminflate();
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

	public HashSet<Node> getPickeds() {

		HashSet<Node> result = new HashSet<Node>();

		for (Node n:ns.view.nNodes) {
			if (n.isPicked()) result.add(n);
		}
		return result;
	}
	public int getPicSize() {
		return picSize;
	}

	public float getRepell() {
		return  getRepellDist();
	}

	public int getRepellMax() {
		return repellMax;
	}

	public float getRepStr() {
		return repellStrength;
	}

	public float getSize() {
		return getNodeSize();
	}

	public float getSquareness() {
		return Math.max(h, 1f/h);
	}

	public float getStrength() {
		return strength;
	}

	public String getUrl() {
		return url;
	}

	public float getVal() {
		return val;
	}
	public void inflate() {
		Net view = ns.getView();
		for (int i=0;i<50;i++){
			layout.layoutInflate(100,ns.getView());
			layout.layoutDistance(getStandardNodeDistance(), getVal(), 1, view); 
		}
		inflate = false;
		resetCam();
	}
	public void init(GLAutoDrawable gLDrawable) {
		glD = gLDrawable;
		GL gl = gLDrawable.getGL();
		glu = new GLU();
		gLDrawable.addMouseListener(this);
		gLDrawable.addMouseMotionListener(this);
		gLDrawable.addMouseWheelListener(this);
		gLDrawable.addKeyListener(this);
		initGLsettings(gl);
		cam = new Cam(gLDrawable,FOV,0,0,zInc,focus,znear,zfar);
		updateFonts(gl, glu);
		starttime = System.currentTimeMillis();
	}

	private void initFonts() {

		/*
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("Tall Films Expanded.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}*/


		font = Font.decode(getFontFam()).deriveFont(1f); //$NON-NLS-1$
		FontRenderContext context = FTFont.STANDARDCONTEXT;

		if (textureFont) {
			hiQfont = new FTGLTextureFont(font,context); 
		}
		else {
			hiQfont = new FTGLPolygonFont(font,context);
			outlinefont =  new FTGLOutlineFont(font,context);
		}
	}

	private void initGLsettings(GL gl) {
		gl.setSwapInterval(0);
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


	public void initInflate() {
		starttime = System.currentTimeMillis();
		lasttime = starttime;
		inflate=true;
	}

	public void initNet() {
		initTree=true;
		Net view = ns.getView();
		view.updateNet();
		layout.replist.clear();
		layout.setNet(view);
		if (layout2d) layout.layoutBox(view.nNodes);
		else layout.layoutRandomize();
		//		layout.layoutLocksRemove();
		updatePick(-1);
		if (tree) layout.layoutTree(radial);
		reloadTextures();
		updateUI();
		initInflate();
	}

	public boolean isCluster() {
		return cluster;
	}

	public boolean isEdges() {
		return edges;
	}
	public boolean isExhibitionMode() {
		return exhibitionMode;
	}

	public boolean isGroups() {
		return groups;
	}

	public boolean isLabelsEdgeDir() {
		return labelsEdgeDir;
	}

	public boolean isRadial() {
		return radial;
	}

	public boolean isRender() {
		return render;
	}

	public boolean isRepN() {
		return repNeighbors;
	}

	public boolean isTabular() {
		return tabular;
	}

	public boolean isTime(){
		return timeline;
	}

	public boolean isTree() {
		return tree;
	}

	public void keyPressed(KeyEvent evt) {
		CTRL = evt.isControlDown();
		SHIFT = evt.isShiftDown();
		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_F2: 
			break;
		case KeyEvent.VK_SHIFT:
			break;
		}
	}

	public void keyReleased(KeyEvent evt) {
		CTRL = evt.isControlDown();
		SHIFT = evt.isShiftDown();

		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_0:
			ns.view.updateNet();
			break;
		case KeyEvent.VK_SPACE:
			break;
		case KeyEvent.VK_F1:
			String name = nameCurrentAttribute();
			System.out.println("applied "+name);
			break;
		case KeyEvent.VK_F2: 
			inflate=true;
			System.out.println("inflate = true"); //$NON-NLS-1$
			break;
		case KeyEvent.VK_F3:
			SVGrenderer.circles=!SVGrenderer.circles;
			System.out.println("SVG circles = "+SVGrenderer.circles);
			break;
		case KeyEvent.VK_F4: 
			layout.layoutLocksRemove();
			break;
		case KeyEvent.VK_F5: 
			break;
		case KeyEvent.VK_F6:
			break;
		case KeyEvent.VK_F7:
			break;
		case KeyEvent.VK_F8: 
			break;
		case KeyEvent.VK_F9: 
			break;
		case KeyEvent.VK_F12:
			fireSemaEvent(SemaEvent.EnterFullscreen);
			break;
		case KeyEvent.VK_F11: 
			break;
		case KeyEvent.VK_ESCAPE:
			fireSemaEvent(SemaEvent.LeaveFullscreen);
			break;
		}
	}

	public void keyTyped(KeyEvent evt) {
	}

	public void layout() {
		float str = strength;
		boolean rep = isRepell();

		layout.setNet(ns.getView());

		if (tree){
			layoutTreeSequence(str, rep);
		}

		else {
			if (calculate) {
				if (repNeighbors) layout.layoutRepNeighbors(repellStrength/4f, getStandardNodeDistance(), ns.getView());


				if (inflate) inflate();


				if (getPerminflate()>0) layout.layoutInflate(getPerminflate()*100f, ns.getView());

				if (distance) layout.layoutDistance(getStandardNodeDistance() , getVal(), str, ns.getView()); 

				if (changed&&!layout2d) changed = false;

				if (rep) layout.layoutRepell(getRepellDist(),repellStrength, ns.getView());

				layout.layoutLockPlace(ns.getView());

				layout.layoutGroups(ns.getView());

				if (timeline) layout.layoutTimeline();

				if (layout2d) layout.layoutFlat();

				float inf = getInflatetime()-elapsedtime;
				if (inf>0) resetCam();
			}
		}
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
		float tmp = getPerminflate();
		boolean rep = isRepell();
		calculate = true;
		setRepell(false);
		setPerminflate(50);
		for (int i=0; i<15; i++) layout(); //inflate
		setPerminflate(tmp);
		for (int i=0; i<Math.max(5, (int)(30000f/ns.getView().nEdges.size())); i++) layout(); //distance, no repell
		setRepell(rep);
		for (int i=0; i<15; i++) layout(); //repell
		calculate = false;
		updateUI();
	}

	private void layoutTreeSequence(float str, boolean rep) {
		if (calculate&&distance&&!initTree) layout.layoutDistanceTree(getStandardNodeDistance(), getVal(), str); // +nets.view.nNodes.size()/5
		if (calculate&&rep&&!initTree) layout.layoutRepFruchtermannRadial(getRepellDist(),repellStrength);

		if (initTree) {
			for (int i=0;i<50;i++) {
				layout.layoutDistanceTree(0, 1, 0.5f);
				layout.initRadial(0, 0, getRadialDist());
				layout.layoutEgocentric();
			}
			initTree = false;
		}
		layout.layoutGroups(ns.getView());
		layout.layoutLockPlace(ns.getView());
		layout.layoutEgocentric();
		layout.layoutFlat();
	}

	/**
	 * Load a new network
	 * @param file
	 * @param tab - tabular file format?
	 * @return
	 */
	public boolean loadNetwork(File file, boolean tab) {
		String cont = FileIO.loadFile(file);
		boolean success = ns.edgeListParse(cont, file.getName(), tab);

		if (success) {
			cont = null;
			File node = new File(file.getAbsoluteFile()+".n"); //$NON-NLS-1$
			if (node.exists()) 	cont = FileIO.loadFile(node);
			ns.nodeListParse(cont, tab);
		}
		return success;
	}
	/**
	 * Load a new network from net
	 * @param url
	 * @param tab - tabular file format?
	 * @return
	 */
	public boolean loadNetworkHttp(String url, boolean tab) {
		String dl;
		dl = fileIO.getPage(url);
		boolean success = ns.edgeListParse(dl, url, tab);
		if (success) {
			String dlNodes = fileIO.getPage(url+".n"); 
			ns.nodeListParse(dlNodes, tab);
		} 
		return success;
	}
	/**
	 * Load a new network from jar
	 * @param file
	 * @param tab - tabular file format?
	 * @return
	 */
	public boolean loadNetworkJar(String file, boolean tab) {
		String jarRead;
		try {
			jarRead = fileIO.jarRead(file);
			boolean success = ns.edgeListParse(jarRead, file, tab);
			if (success) {
				String jarNodes = fileIO.jarRead(file+".n"); 
				ns.nodeListParse(jarNodes, tab);
			} 
			return success;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void lockAll() {
		layout.layoutLocksAll();
	}


	public void locksRemove() {
		layout.layoutLocksRemove();
	}

	public void mouseClicked(MouseEvent evt) {
		if (evt.getClickCount() == 2) netExpandPickedNodes();
		updatePick();
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

		if (select&&!SwingUtilities.isRightMouseButton(evt)&&picked!=null&&picked.rollover&&ns.getView().fNodes.contains(picked)) { //drag a node
			float wHeight = glD.getHeight();
			float wWidth = glD.getWidth();
			float dragX = mouseX-(wWidth/2f);
			float dragY = mouseY-(wHeight/2f);
			float screenfactor = (float)(cam.getDist()*2f*Math.tan((FOV/2)*TWO_PI/360)/wHeight);

			// drag a node
			float localX = cam.getX()+dragX*screenfactor;
			float localY = cam.getY()-dragY*screenfactor;

			picked.pos.x = (float)Math.cos(cam.getYRot()*TWO_PI/360)*localX;
			picked.pos.y = (float)Math.cos(cam.getXRot()*TWO_PI/360)*localY;
			picked.pos.z = (float)Math.sin(cam.getXRot()*TWO_PI/360)*localY-(float)Math.sin(cam.getYRot()*TWO_PI/360)*localX+cam.getZ();

			if (!evt.isAltDown()) picked.lock(picked.pos);
			else picked.setLocked(false);
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
				if (!layout2d) {
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
		//		pressed = false;
		//		select = false;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		zoomNew *= 1-(notches*0.001f*deltatime) ;
		zoomNew = Math.min(zoomNew, zfar);
		zoomNew = Math.max(zoomNew, znear);
	}
	private String nameCurrentAttribute() {
		ns.global.altNameByAttribute(attribute);
		return attribute;
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
		Net view = ns.getView();
		HashSet<Node> zi = view.distances.getNodesAtDistance(0);
		int max = view.distances.getMaxDist();

		//		for (Node n:framed) layout.layoutLockNode(n, n.pos, view);
		Net result = view.generateSearchNet(ns.global,framed, 1 );
		//		Net sub = new Net(this);
		//		for (Node n:result.nNodes) {
		//			if (!view.nNodes.contains(n))
		//				sub.addNode(n);
		//		}
		{ 
			view.netMerge(result);
		}
		//		initInflate(sub);
		if (zi==null||zi.size()==0) return; 
		view.distances.findSearchDistances(zi, max+1);

		reloadTextures();
		updateUI();
		updatePicks();
	}

	public void netExpandPickedNodes() {
		HashSet<Node> n = getPickeds();
		if (n.size()>0)	netExpandNodes(n);
	}

	public void netLoad() {
		clearNets();
		switch (loadMethod) {
		case 0:
			loadNetwork(new File(filename), isTabular());
			break;
		case 1:
			loadNetworkHttp(filename, true);
			break;
		case 2:
			loadNetworkJar(filename, isTabular());
			break;
		} 
		netStartRandom(false);
	}

	public void netRemoveClusters() {
		ns.getView().clustersDelete();
		updateUI();
	}

	public void netRemoveLeafs() {
		ns.getView().leafDelete();
		updateUI();
	}

	/**
	 * New view from picked node
	 * @param add - add to existing view
	 */
	public void netSearchPicked(boolean add) {
		Node picked = getPicked();
		if (picked!=null) {
			netStartString(picked.name, add);
		}
	}

	/**
	 * New view from picked nodes
	 * @param add - add to existing view
	 */
	public void netSearchPickedMultiple(boolean add) {
		Net view = ns.getView();
		HashSet<Node> pickeds = getPickeds();
		if (pickeds.size()>0){
			Net result = view.generateSearchNet(ns.global,pickeds, searchdepth);
			ns.setView(result);
			initNet();
		}
	}

	/**
	 * generate view through substring search in node names
	 * @param text
	 * @param add- add to existing view
	 */
	public void netSearchSubstring(String text, boolean add) {
		Net result = ns.search(text, searchdepth, add, getAttribute());
		ns.setView(result);
		initNet();
	}

	/**
	 * generate view through substring search in node names
	 * @param text
	 * @param add
	 * @param attribute
	 */
	public void netSearchSubstring(String text, boolean add, String attribute) {
		Net result = ns.search(text, searchdepth, add, attribute);
		ns.setView(result);
		initNet();
	}

	/**
	 * generate view from whole network
	 */
	public void netShowAll(){
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
		ns.getView().distances.clear();
		ns.getView().app.clearFrames(ns.getView());
		initNet();
	}

	/**
	 * view from first node in nodearray
	 * @param add- add to existing view
	 */
	public void netStartFirst(boolean add) {
		int ID = 0;
		Node n = (Node)ns.global.nNodes.toArray()[ID];
		Net net = netStartNode(n, add);
		initNet();
	}

	/**
	 * generate view from specified node
	 * @param n
	 * @param add- add to existing view
	 * @return 
	 */
	public Net netStartNode(Node n, boolean add) {
		return ns.search(n, searchdepth, add);
	}

	/**
	 * generate view from random node
	 * @param add- add to existing view
	 */
	public void netStartRandom(boolean add) {
		Net net;
		if (attribute!="none") { //$NON-NLS-1$
			HashSet<Node> hs = new HashSet<Node>();
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(attribute)) hs.add(n);
			}
			if (hs.size()==0) return;
			int ID = (int)(Math.random()*hs.size());
			Node res = (Node)hs.toArray()[ID];
			net = netStartNode(res, add);
		} else {
			if (ns.global.nNodes.size()==0) return;
			int ID =  new Random().nextInt(ns.global.nNodes.size());
			Node res = (Node) ns.global.nNodes.toArray()[ID];
			net = netStartNode(res, add);
		}
		initNet();
	}

	/**
	 * generate view from node name
	 * @param text
	 * @param add- add to existing view
	 */
	public void netStartString(String text, boolean add) {
		Net search = ns.search(text, searchdepth, add);
		ns.setView(search);
		initNet();
	}

	/**
	 * add node parameter file
	 * @param file2 
	 * @param tab - tabular file format?
	 */
	public void nodeListLoad(File file2, boolean tab) {
		String cont = FileIO.loadFile(file2);
		ns.nodeListParse(cont, tab);
		ns.getView().updateNet();
		updateUI();
	}

	void redrawUI() {
		fireSemaEvent(SemaEvent.RedrawUI);
	}
	public void reloadTextures() {
		if (glD!=null) {
			GL gl = glD.getGL();
			for (Node n:ns.global.nNodes) {
				n.deleteTexture(gl);
			}
		}
		if (!isTextures()) return;
		fileIO.loadTexturesUrl(getTexfolder(), ns.getView(), getThumbsize());
	}

	public void removeNet(String net) {
		ns.removeSubnet(net);
		updateUI();
	}

	public synchronized void removeSemaListener( SemaListener l ) {
		_listeners.remove( l );
	}

	public void render(GL gl){
		if (isEnableSvg()&&SVGexport) {
			SVGexport=false;
			SVGrenderer.renderSVG(gl, ns.getView(), fonttype, svgFile);
		}

		if (!render) return;
		if (FOG&&!layout2d) gl.glEnable(GL.GL_FOG); else gl.glDisable(GL.GL_FOG);
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
			if (n instanceof Edge) { 
				// if edge, also activate connected nodes
				((Edge) n).getA().setRollover(true);
				((Edge) n).getB().setRollover(true);
			}
			moved=false;
		}
		statusMsg();
	}

	public void renderPbuffer(GL gl, int width, int height) {
		if (height <= 0) height = 1;

		initGLsettings(gl);
		reloadTextures();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(FOV, 1, znear, zfar);
		render(gl);
	}

	public void resetCam() {
		if (cam==null) return;
		BBox3D bounds = BBox3D.calcBounds(ns.getView().nNodes);
		float size = Math.max(bounds.size.x, bounds.size.y)/3f;
		//		if (layout2d) {
		float tan = (float)Math.tan(FOV/2);
		zInc = Math.max(300, size/tan);
		//		} else zInc = 300;
		zoomNew = zInc;
		focus.setXYZ(bounds.center.x,bounds.center.y, bounds.center.z);
		cam.posAbsolute(glD,zInc,focus);
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
	}
	@SuppressWarnings("unchecked")
	public void saveNet() {
		ns.addSubnet((HashSet<Edge>) ns.getView().nEdges.clone());
		updateUI();
	}

	public void screenshot (int width, int height, String filename2) {
		if (!GLDrawableFactory.getFactory().canCreateGLPbuffer()) return;
		boolean f = layout2d;
		layout2d = false;

		GLCapabilities caps = new GLCapabilities();
		GLPbuffer pbuffer = GLDrawableFactory.getFactory().createGLPbuffer(caps, null, width, height, null);
		pbuffer.getContext().makeCurrent();
		GL gl = pbuffer.getGL();
		moved = false;

		updateFonts(gl, new GLU());

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

		updateFonts(gl, glu);

		layout2d = f;
	}

	void select(){
		pickID = getOverID();
		if (pickID!=-1) select = true;
		else select = false;
		pressed=false;
		if (CTRL) focus.setXYZ(ns.getView().getPosByID(pickID)); //point to selected node's position
		if (select) updatePick(pickID);
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

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
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

	public void setCalc(boolean b) {
		calculate = b;
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
		setStandardNodeDistance(f);
	}

	public void setEdges(boolean edges) {
		this.edges = edges;
	}

	public void setEdgeUrl(String edgeUrl) {
		this.edgeUrl = edgeUrl;
	}

	public void setExhibitionMode(boolean exhibitionMode) {
		this.exhibitionMode = exhibitionMode;
	}

	public void setFilename(String selectedFile) {
		filename = selectedFile;
	}

	public void setFonttype(int fonttype_) {
		fonttype = fonttype_;
	}

	public void setGroups(boolean groups) {
		this.groups = groups;
	}

	public void setInVar(float value) {
		invar = value;
	}

	public void setLabelsEdgeDir(boolean labelsEdgeDir) {
		this.labelsEdgeDir = labelsEdgeDir;
	}

	public void setLabelsize(float labelsize) {
		this.labelsize = labelsize;
	}

	public void setLabelVar(float labelVar) {
		this.labelVar = labelVar;
	}


	public void setNodeUrl(String nodeurl) {
		nodeUrl = nodeurl;

	}

	public void setOutVar(float value) {
		outvar = value;
	}

	public void setOverID(int overID) {
		this.overID = overID;
	}

	public void setPermInflate(float f) {
		setPerminflate(f);
	}

	public void setPickdepth(int pickdepth) {
		this.pickdepth = pickdepth;
	}


	public void setPickID(int pickID) {
		this.pickID = pickID;
		updatePick(pickID);
	}

	public void setPicSize(int picSize) {
		this.picSize = picSize;
	}

	public void setRadial(boolean selected) {
		radial = selected;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public void setRepell(boolean repell_) {
		repell = repell_;
	}

	public void setRepell(float value) {
		setRepellDist(value);

	}

	public void setRepellMax(int value) {
		repellMax = value;
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
		setNodeSize(f);
	}

	public void setStrength(float f) {
		strength = f;
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

	public void setTabular(boolean tabular) {
		this.tabular = tabular;
	}

	public void setTexFolder(String file2) {
		setTexfolder(file2);
	}

	public void setTime(boolean selected) {
		timeline = selected;
	}

	public void setTree(boolean selected) {
		if (!tree&&selected) initTree = true;
		tree = selected;
		HashSet<Node> set = ns.getView().distances.getNodesAtDistance(0);
		if (tree&&set != null) ns.getView().clearClusters();
		else {
			ns.getView().findClusters();
			layout.clustersSetup(glD.getGL());
			updatePick();
			if (tree) {
				tree=false;
				fireSemaEvent(SemaEvent.UpdateUI);
			}
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public void setView(String net) {
		ns.setView(net);
	}

	public void setXRotNew(float rotNew) {
		xRotNew = rotNew;
	}

	public void setYRotNew(float rotNew) {
		yRotNew = rotNew;
	}


	private void startSystemEvent(String command) {
		String os = System.getProperty("os.name");
		if (os.contains("Mac"))
			try {
				Runtime.getRuntime().exec("open \""+command+"\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (os.contains("Windows"))
				try {
					Runtime.getRuntime().exec("start \""+command+"\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	private void statusMsg() {
		String msgline = ns.getView().nNodes.size()+" nodes, "+ns.getView().eTable.size()+" edges\n"; //$NON-NLS-1$ //$NON-NLS-2$
		//		line += "Xpos:"+mouseX+" Ypos:"+mouseY+" w:"+glD.getWidth()+" h:"+glD.getHeight();
		//		line += "\ncamX:"+cam.getX()+", camY:"+cam.getY()+", camZ:"+cam.getZ();
		//		line += "\ncamXrot:"+cam.getXRot()+", camYrot:"+cam.getYRot()+", camDist:"+cam.getDist();
		//		line += "\npID"+pickID+" selX:"+(int)nets.view.getPosByID(pickID).x+" selY:"+(int)nets.view.getPosByID(pickID).y+" selZ:"+(int)nets.view.getPosByID(pickID).z;
		//		line += "\n"+Math.sin(cam.getXRot()*TWO_PI/360);
		Node tmp = ns.global.getNodeByID(pickID);
		Edge tmp2 = ns.global.getEdgeByID(pickID);
		if (tmp!=null) msgline += "\n"+tmp.name+", attr:"+tmp.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		if (tmp2!=null) msgline += "\n"+tmp2.name +", attr:"+tmp2.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		//		if (swingapp!=null) swingapp.setMsg(msgline);
		fireSemaEvent(SemaEvent.MSGupdate, msgline);
	}

	public void toggle3D() {
		layout2d=!layout2d;
		changed=true;
		if (!layout2d) layout.layoutRandomize();
	}

	private void updateFonts(GL gl, GLU glu) {
		if (hiQfont!=null){
			hiQfont.setGLGLU(gl, glu);
			hiQfont.faceSize(70f);
		}
		if (!textureFont&&outlinefont!=null) {
			outlinefont.setGLGLU(gl, new GLU());
			outlinefont.faceSize(70f);
		}
	}
	public void updatePick() {
		updatePick(pickID);
	}

	void updatePick(int pickID2) {
		if (pickID2 == -1) ns.getView().distances.clearPick();
		ns.getView().distances.findPickDistances(pickID2, pickdepth,SHIFT);
		layout.applyPickColors();
	}

	void updatePicks() {
		ns.getView().distances.findPickDistancesMultiple(pickdepth);
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
	}

	void updateUI() {
		fireSemaEvent(SemaEvent.UpdateUI);
	}

	public void setTextures(boolean textures) {
		this.textures = textures;
		reloadTextures();
	}

	public boolean isTextures() {
		return textures;
	}

	public boolean isRepell() {
		return repell;
	}

	public void setTilt(boolean tilt) {
		this.tilt = tilt;
	}

	public boolean isTilt() {
		return tilt;
	}

	public void setTexfolder(String texfolder) {
		this.texfolder = texfolder;
	}

	public String getTexfolder() {
		return texfolder;
	}

	public void setThumbsize(int thumbsize) {
		this.thumbsize = thumbsize;
	}

	public int getThumbsize() {
		return thumbsize;
	}

	public void setEnableSvg(boolean enableSvg) {
		this.enableSvg = enableSvg;
	}

	public boolean isEnableSvg() {
		return enableSvg;
	}

	public void setEdgewidth(float edgewidth) {
		this.edgewidth = edgewidth;
	}

	public float getEdgewidth() {
		return edgewidth;
	}

	public void setEdgeAlpha(float edgeAlpha) {
		this.edgeAlpha = edgeAlpha;
	}

	public float getEdgeAlpha() {
		return edgeAlpha;
	}

	public void setInheritEdgeColorFromNodes(boolean inheritEdgeColorFromNodes) {
		this.inheritEdgeColorFromNodes = inheritEdgeColorFromNodes;
	}

	public boolean isInheritEdgeColorFromNodes() {
		return inheritEdgeColorFromNodes;
	}

	public void setFontFam(String fontFam) {
		this.fontFam = fontFam;
	}

	public String getFontFam() {
		return fontFam;
	}

	public void setStandardNodeDistance(float standardNodeDistance) {
		this.standardNodeDistance = standardNodeDistance;
	}

	public float getStandardNodeDistance() {
		return standardNodeDistance;
	}

	public void setRadialDist(float radialDist) {
		this.radialDist = radialDist;
	}

	public float getRadialDist() {
		return radialDist;
	}

	public void setBoxdist(float boxdist) {
		this.boxdist = boxdist;
	}

	public float getBoxdist() {
		return boxdist;
	}

	public void setRepellDist(float repellDist) {
		this.repellDist = repellDist;
	}

	public float getRepellDist() {
		return repellDist;
	}

	public void setInflatetime(long inflatetime) {
		this.inflatetime = inflatetime;
	}

	public long getInflatetime() {
		return inflatetime;
	}

	public void setPerminflate(float perminflate) {
		this.perminflate = perminflate;
	}

	public float getPerminflate() {
		return perminflate;
	}

	public void setSvgNodeCircles(boolean svgNodeCircles) {
		this.svgNodeCircles = svgNodeCircles;
	}

	public boolean getSvgNodeCircles() {
		return svgNodeCircles;
	}

	public void setNodeSize(float nodeSize) {
		this.nodeSize = nodeSize;
	}

	public float getNodeSize() {
		return nodeSize;
	}

}