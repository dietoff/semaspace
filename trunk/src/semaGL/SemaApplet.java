package semaGL;

import javax.swing.JApplet;
import javax.swing.JSplitPane;
import nehe.GLDisplayPanel;
import UI.SwingSema;

import com.sun.opengl.util.FPSAnimator;

public class SemaApplet extends JApplet {
	private FPSAnimator animator;

	public SemaApplet() {
	}

	@Override
	public void init() {
//		MetalLookAndFeel.setCurrentTheme(new SemaTheme());
//		LookAndFeel laf = new MetalLookAndFeel();
//		try {
//			UIManager.setLookAndFeel(laf);
//		} catch (UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
		setSize(1000, 700);
		GLDisplayPanel semaGLDisplay = GLDisplayPanel.createGLDisplay("SemaSpace");
		SemaSpace space = new SemaSpace();
		semaGLDisplay.addGLEventListener(space);
		SwingSema application = new SwingSema();
		application.setSema(space);
		getParameters(space);
		
		JSplitPane splitPane = application.getJSplitPane();
		splitPane.setRightComponent(semaGLDisplay.getJPanel());
		add(splitPane);
		application.initFileChoosers();
		semaGLDisplay.start();
	}

	private void getParameters(SemaSpace space) {
		String urlpath = getParameter("file");
		String nodeurl = getParameter("nodes");
		String edgeurl = getParameter("edges");
		String radial = getParameter("radial");
		String tree = getParameter("tree");
		String cluster = getParameter("cluster");
		String radialDist = getParameter("radialDist");
		String distance = getParameter("distance");
		String repell = getParameter("repell");
		String font = getParameter("font");
		String attractS = getParameter("attractS");
		String repellS = getParameter("repellS");
		String pickdepth = getParameter("pickdepth");
		String repellDist = getParameter("repellDist");
		String perminflate = getParameter("perminflate");
		String inflateTime = getParameter("inflateTime");
		String valenceFact = getParameter("valence");
		
		if (urlpath!=null) space.p.setUrl(urlpath);
		if (nodeurl!=null) space.p.setNodeUrl(nodeurl);
		if (edgeurl!=null) space.p.setEdgeUrl(edgeurl);
		if (radial != null) space.p.setRadial(Boolean.parseBoolean(radial));
		if (tree != null) space.setTree(Boolean.parseBoolean(tree));
		if (cluster != null) space.p.setCluster(Boolean.parseBoolean(cluster));
		if (repell!=null) space.p.setRepell(Boolean.parseBoolean(repell));
		if (font!=null) space.p.setFonttype(Integer.parseInt(font));
		if (attractS!=null) space.p.setAttractStr(Float.parseFloat(attractS));
		if (repellS!=null) space.p.setRepellStr(Float.parseFloat(repellS));
		if (pickdepth!=null) space.p.setPickdepth(Integer.parseInt(pickdepth));
		if (distance!=null) space.p.setStandardNodeDistance(Float.parseFloat(distance));
		if (radialDist!=null) space.p.setRadialDist(Float.parseFloat(radialDist));
		if (repellDist!=null) space.p.setRepellDist(Float.parseFloat(repellDist));
		if (perminflate!=null) space.p.setPerminflate(Integer.parseInt(perminflate));
		if (inflateTime!=null) space.p.setInflatetime(Integer.parseInt(inflateTime));
		if (valenceFact!=null) space.p.setVal(Float.parseFloat(valenceFact));
	}
}