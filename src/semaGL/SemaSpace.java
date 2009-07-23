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
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
	final float TWO_PI =6.283185307179586476925286766559f;
	public SemaParameters p;
	GLUT glut = new GLUT();
	public GLAutoDrawable glD;
	private GLU glu;
	String file[];
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
	boolean select;
	public FileIO fileIO;
	public Texture tex;
	public boolean texRead= false;
	public boolean moved=false;
	public Layouter layout;
	public GraphRenderer renderer;
	public Graphics2D j2d;
	private boolean timeline;
	private Font font;
	FTFont outlinefont;
	FTFont hiQfont;
	private boolean initTree;
	public NetStack ns;
	int screenshotcounter = 0;
	private boolean SVGexport;
	private String svgFilename;
	private GraphRendererSVG SVGrenderer;
	private List<SemaListener> _listeners = new ArrayList<SemaListener>();
	private boolean SHIFT;
	public String splitAttribute = "; ";
	private boolean CTRL;
	private boolean inflate= false;
	private boolean changed=false;
	private boolean pressed=false;

	public SemaSpace(){
		p = new SemaParameters(this);
		p.loadSemaParametersJar("sema.config");
		fileIO = new FileIO(p);
		ns = (new NetStack(p));
		layout = new Layouter(p);
		renderer = new GraphRenderer(p);
		if (p.isEnableSvg()) SVGrenderer = new GraphRendererSVG(p);
		initFonts();
		netLoad();
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
		cam = new Cam(gLDrawable,p.FOV,0,0,zInc,focus,p.znear,p.zfar);
		updateFonts(gl, glu);
		starttime = System.currentTimeMillis();
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
		gl.glFogi(GL.GL_FOG_MODE, p.fogMode[p.fogfilter]);				// Fog Mode
		gl.glFogfv(GL.GL_FOG_COLOR, p.fogColor, 0);					// Set Fog Color
		gl.glFogf(GL.GL_FOG_DENSITY, 0.0005f);						// How Dense Will The Fog Be
		gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);					// Fog Hint Value
		gl.glFogf(GL.GL_FOG_START, 1000f);							// Fog Start Depth
		gl.glFogf(GL.GL_FOG_END, 10000f);							// Fog End Depth
		if (p.FOG) gl.glEnable(GL.GL_FOG);							// Enables GL.GL_FOG
	}

	private void initFonts() {
		/*
		try {
//			InputStream is = getClass().getClassLoader().getResourceAsStream("machtgth.ttf");
			InputStream is = getClass().getClassLoader().getResourceAsStream("Tall Films Expanded.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}*/

		font = Font.decode(p.getFontFam()).deriveFont(1f); //$NON-NLS-1$
		FontRenderContext context = FTFont.STANDARDCONTEXT;

		if (p.textureFont) {
			hiQfont = new FTGLTextureFont(font,context); 
		}
		else {
			hiQfont = new FTGLPolygonFont(font,context);
			outlinefont =  new FTGLOutlineFont(font,context);
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

	void clearPick() {
		ns.getView().distances.clearPick();
		layout.applyPickColors();
		p.pickID=-1;
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
			if (n.hasAttribute(p.getAttribute())) ne.add(n);
		}
		if (p.directed) {
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

	public void exportSVG(String file) {
		svgFilename = file;
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

	public Node getPicked() {
		Node picked = null;
		if (p.pickID!=-1) {
			picked = ns.global.getNodeByID(p.pickID);
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

	public float getSquareness() {
		return Math.max(h, 1f/h);
	}

	public void inflate() {
		Net view = ns.getView();
		for (int i=0;i<50;i++){
			layout.layoutInflate(100,ns.getView());
			layout.layoutDistance(p.getStandardNodeDistance(), p.getVal(), 1, view); 
		}
		inflate = false;
		resetCam();
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
		if (p.layout2d) layout.layoutBox(view.nNodes);
		else layout.layoutRandomize();
		//		layout.layoutLocksRemove();
		updatePick(-1);
		if (p.tree) layout.layoutTree(p.radial);
		reloadTextures();
		updateUI();
		initInflate();
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
		case KeyEvent.VK_ENTER:
			if (evt.isAltDown()) fireSemaEvent(SemaEvent.EnterFullscreen);
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
		float str = p.strength;
		boolean rep = p.isRepell();

		layout.setNet(ns.getView());

		if (p.tree){
			layoutTreeSequence(str, rep);
		}

		else {
			if (p.calculate) {
				if (p.repNeighbors) layout.layoutRepNeighbors(p.repellStrength/4f, p.getStandardNodeDistance(), ns.getView());


				if (inflate) inflate();


				if (p.getPerminflate()>0) layout.layoutInflate(p.getPerminflate()*100f, ns.getView());

				if (p.distance) layout.layoutDistance(p.getStandardNodeDistance() , p.getVal(), str, ns.getView()); 

				if (changed&&!p.layout2d) changed = false;

				if (rep) layout.layoutRepell(p.getRepellDist(), p.repellStrength, ns.getView());

				layout.layoutLockPlace(ns.getView());

				layout.layoutGroups(ns.getView());

				if (p.isTime()) {
					layout.layoutTimeline();
				}

				if (p.layout2d) layout.layoutFlat();

				float inf = p.getInflatetime()-elapsedtime;
				if (inf>0) {
					resetCam();
				}
			}
		}
	}

	public void layoutBox() {
		layout.layoutBox(ns.getView().fNodes);
		p.calculate = false;
		updateUI();
	}

	public void layoutCircle() {
		layout.layoutConstrainCircle(ns.getView().fNodes);
		p.calculate = false;
		updateUI();
	}

	public void layoutForce() {
		float tmp = p.getPerminflate();
		boolean rep = p.isRepell();
		p.calculate = true;
		p.setRepell(false);
		p.setPerminflate(50);
		for (int i=0; i<15; i++) layout(); //inflate
		p.setPerminflate(tmp);
		for (int i=0; i<Math.max(5, (int)(30000f/ns.getView().nEdges.size())); i++) layout(); //distance, no repell
		p.setRepell(rep);
		for (int i=0; i<15; i++) layout(); //repell
		p.calculate = false;
		updateUI();
	}

	private void layoutTreeSequence(float str, boolean rep) {
		if (p.calculate&&p.distance&&!initTree) layout.layoutDistanceTree(p.getStandardNodeDistance(), p.getVal(), str); // +nets.view.nNodes.size()/5
		if (p.calculate&&rep&&!initTree) layout.layoutRepFruchtermannRadial(p.getRepellDist(),p.repellStrength);

		if (initTree) {
			for (int i=0;i<50;i++) {
				layout.layoutDistanceTree(0, 1, 0.5f);
				layout.initRadial(0, 0, p.getRadialDist());
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
			p.loadMethod = 0;
			p.setFilename(file.getAbsolutePath());
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
			p.loadMethod = 1;
			p.setFilename(url);
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
		Node picked = ns.getView().getNodeByID(p.pickID);

		if (select&&!SwingUtilities.isRightMouseButton(evt)&&picked!=null&&picked.rollover&&ns.getView().fNodes.contains(picked)) { //drag a node
			float wHeight = glD.getHeight();
			float wWidth = glD.getWidth();
			float dragX = mouseX-(wWidth/2f);
			float dragY = mouseY-(wHeight/2f);
			float screenfactor = (float)(cam.getDist()*2f*Math.tan((p.FOV/2)*TWO_PI/360)/wHeight);

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
				zoomNew = Math.min(zoomNew, p.zfar);
				zoomNew = Math.max(zoomNew, p.znear);
			}
			else 
				//rotate (only in 3d mode)
				if (!p.layout2d) {
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
		zoomNew = Math.min(zoomNew, p.zfar);
		zoomNew = Math.max(zoomNew, p.znear);
	}

	private String nameCurrentAttribute() {
		String attribute = p.getAttribute();
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
		ns.clear();
		switch (p.loadMethod) {
		case 0:
			loadNetwork(new File(p.getFilename()), p.isTabular());
			break;
		case 1:
			loadNetworkHttp(p.getFilename(), true);
			break;
		case 2:
			loadNetworkJar(p.getFilename(), p.isTabular());
			break;
		} 
		if (p.isStartWhole()) {
			netShowAll();
			p.setStartWhole(false);
		} else
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
			Net result = view.generateSearchNet(ns.global,pickeds, p.searchdepth);
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
		Net result = ns.search(text, p.searchdepth, add, p.getAttribute());
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
		Net result = ns.search(text, p.searchdepth, add, attribute);
		ns.setView(result);
		initNet();
	}

	/**
	 * generate view from whole network
	 */
	public void netShowAll(){
		if (p.getAttribute() == "none") { //$NON-NLS-1$
			ns.setView(ns.global.clone());}
		else {
			ns.setView(new Net(p));
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(p.getAttribute())) {
					ns.getView().addNode(n);
				}
			}
		}
		ns.getView().distances.clear();
		clearFrames(ns.getView());
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
		return ns.search(n, p.searchdepth, add);
	}

	/**
	 * generate view from random node
	 * @param add- add to existing view
	 */
	public void netStartRandom(boolean add) {
		Net net;
		if (p.getAttribute()!="none") { //$NON-NLS-1$
			HashSet<Node> hs = new HashSet<Node>();
			for (Node n:ns.global.nNodes) {
				if (n.hasAttribute(p.getAttribute())) hs.add(n);
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
		Net search = ns.search(text, p.searchdepth, add);
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
		if (!p.isTextures()) return;
		fileIO.loadTexturesUrl(p.getTexfolder(), ns.getView(), p.getThumbsize());
	}

	public void removeNet(String net) {
		ns.removeSubnet(net);
		updateUI();
	}

	public synchronized void removeSemaListener( SemaListener l ) {
		_listeners.remove( l );
	}

	public void render(GL gl){
		if (p.isEnableSvg()&&SVGexport) {
			SVGexport=false;
			SVGrenderer.renderSVG(gl, ns.getView(), p.fonttype, svgFilename);
		}

		if (!p.render) return;
		if (p.FOG&&!p.layout2d) gl.glEnable(GL.GL_FOG); else gl.glDisable(GL.GL_FOG);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		cam.posIncrement(gl, yRotInc, xRotInc, zInc, focus); 
		layout.render(gl, p.fonttype, ns.view, renderer);
		gl.glFlush();
		gl.glFinish();
		//		}
		//		else
		//		{
		if (moved) {
			p.setOverID(selectCoord(gl));
			if (pressed) select(); //initiate picking
			clearRollover();
			GraphElement n = ns.getView().getByID(p.overID);
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
		glu.gluPerspective(p.FOV, 1, p.znear, p.zfar);
		boolean r = p.render;
		p.render=true;
		render(gl);
		p.render= r;
	}

	public void resetCam() {
		if (cam==null) return;
		BBox3D bounds = BBox3D.calcBounds(ns.getView().nNodes);
		float size = Math.max(bounds.size.x, bounds.size.y)/3f;
		//		if (layout2d) {
		float tan = (float)Math.tan(p.FOV/2);
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
		glu.gluPerspective(p.FOV, h, p.znear, p.zfar);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		updateUI();
	}

	@SuppressWarnings("unchecked")
	public void storeNet() {
		ns.addSubnet((HashSet<Edge>) ns.getView().nEdges.clone());
		updateUI();
	}

	public void screenshot (int width, int height, String filename2) {
		if (!GLDrawableFactory.getFactory().canCreateGLPbuffer()) return;
//		boolean f = p.layout2d;
//		p.layout2d = false;

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

//		p.layout2d = f;
	}

	void select(){
		p.pickID = p.getOverID();
		if (p.pickID!=-1) select = true;
		else select = false;
		pressed=false;
		if (CTRL) focus.setXYZ(ns.getView().getPosByID(p.pickID)); //point to selected node's position
		if (select) updatePick(p.pickID);
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
		glu.gluPerspective(p.FOV, h, p.znear, p.zfar);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		cam.posIncrement(gl, yRotInc, xRotInc, zInc, focus);

		layout.renderNodes(gl, renderer, 0); //render the nets.viewwork 
		if (p.edges) layout.renderEdges(gl, renderer, 0);

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


	public void setPickID(int pickID) {
		p.pickID = pickID;
		updatePick(pickID);
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

	public void setTextures(boolean textures) {
		p.textures = textures;
		reloadTextures();
	}

	public void setTree(boolean selected) {
		if (!p.tree&&selected) initTree = true;
		p.tree = selected;
		HashSet<Node> set = ns.getView().distances.getNodesAtDistance(0);
		if (p.tree&&set != null) ns.getView().clearClusters();
		else {
			ns.getView().findClusters();
			layout.clustersSetup(glD.getGL());
			updatePick();
			if (p.tree) {
				p.tree=false;
				fireSemaEvent(SemaEvent.UpdateUI);
			}
		}
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
		Node tmp = ns.global.getNodeByID(p.pickID);
		Edge tmp2 = ns.global.getEdgeByID(p.pickID);
		if (tmp!=null) msgline += "\n"+tmp.name+", attr:"+tmp.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		if (tmp2!=null) msgline += "\n"+tmp2.name +", attr:"+tmp2.attributes.toString(); //$NON-NLS-1$ //$NON-NLS-2$
		//		if (swingapp!=null) swingapp.setMsg(msgline);
		fireSemaEvent(SemaEvent.MSGupdate, msgline);
	}

	public void toggle3D() {
		p.layout2d=!p.layout2d;
		changed=true;
		if (!p.layout2d) layout.layoutRandomize();
	}

	private void updateFonts(GL gl, GLU glu) {
		if (hiQfont!=null){
			hiQfont.setGLGLU(gl, glu);
			hiQfont.faceSize(70f);
		}
		if (!p.textureFont&&outlinefont!=null) {
			outlinefont.setGLGLU(gl, new GLU());
			outlinefont.faceSize(70f);
		}
	}

	public void updatePick() {
		updatePick(p.pickID);
	}

	void updatePick(int pickID2) {
		if (pickID2 == -1) ns.getView().distances.clearPick();
		ns.getView().distances.findPickDistances(pickID2, p.pickdepth,SHIFT);
		layout.applyPickColors();
	}

	void updatePicks() {
		ns.getView().distances.findPickDistancesMultiple(p.pickdepth);
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

	/**
	 * load a project file complete with all settings
	 * @param file
	 */
	public void loadProject(File file) {
		p.loadSemaParameters(file.getAbsolutePath());
		char sep = File.separatorChar;
		String parent = file.getParent();

		//make path absolute
		if (p.loadMethod == 0){
			String filename = parent+sep+p.getFilename();
			p.setFilename(filename);
		}
		initFonts();
		updateFonts(this.glD.getGL(), glu);
		netLoad();
	}

	/**
	 * save a project file complete with all settings
	 * @param filename
	 */
	public void saveProject(File file, boolean view) {
		String filename= file.getName();
		String parent = file.getParent();
		if (parent==null) parent ="";
		// set rel pathname for project file
		String base =filename;
		if (filename.endsWith(".sema")) base = filename.substring(0, filename.length()-5);
		base+=".tab";
		p.setFilename(base);
		p.loadMethod=0;
		p.setTabular(true);
		// set 
		String path = file.getPath();
		if (!path.endsWith(".sema")) path+=".sema";
		p.storeSemaParameters(path);
		ns.exportNet(parent+File.separatorChar+base , true, view);
	}

	public void setTimeline(boolean selected) {
//		if (selected!=p.isTime()) {
//			if (selected) layout.initTimeline();
			p.setTime(selected);
//		}
	}
}