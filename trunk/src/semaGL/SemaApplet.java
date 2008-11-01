package semaGL;

import javax.media.opengl.GLCanvas;
import javax.swing.JApplet;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.sun.opengl.util.FPSAnimator;

public class SemaApplet extends JApplet {
	private FPSAnimator animator;

	public SemaApplet() {
	}

	@Override
	public void init() {
		MetalLookAndFeel.setCurrentTheme(new SemaTheme());
		LookAndFeel laf = new MetalLookAndFeel();
		try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
//		setSize(1000, 700);
		SemaSpace space = new SemaSpace();
		getParameters(space);
		SwingSema application = new SwingSema();
		GLCanvas canvas = new GLCanvas();
		application.setSema(space);
		
//		application.getJFrame2();
//		JPanel controlp = application.getControlPanel();
//		setJMenuBar(application.getJJMenuBar());
//		JSplitPane jSplitPane1 = new JSplitPane();
//		jSplitPane1.setPreferredSize(getSize());
//		jSplitPane1.setDividerLocation(220);
//		jSplitPane1.setDividerSize(0);
//		jSplitPane1.setLeftComponent(controlp);
//		jSplitPane1.setRightComponent(canvas);
//		add(jSplitPane1);
		add(canvas);
//		add(controlp);
		canvas.addGLEventListener(space);
		animator = new FPSAnimator(canvas, 60);
		canvas.requestFocus();
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
		
		if (urlpath!=null) space.setUrl(urlpath);
		if (nodeurl!=null) space.setNodeUrl(nodeurl);
		if (edgeurl!=null) space.setEdgeUrl(edgeurl);
		if (radial != null) space.setRadial(Boolean.parseBoolean(radial));
		if (tree != null) space.setTree(Boolean.parseBoolean(tree));
		if (cluster != null) space.setCluster(Boolean.parseBoolean(cluster));
		if (repell!=null) space.setRepell(Boolean.parseBoolean(repell));
		if (font!=null) space.setFonttype(Integer.parseInt(font));
		if (attractS!=null) space.setAttractStr(Float.parseFloat(attractS));
		if (repellS!=null) space.setRepellStr(Float.parseFloat(repellS));
		if (pickdepth!=null) space.pickdepth = Integer.parseInt(pickdepth);
		if (distance!=null) space.standardNodeDistance = Float.parseFloat(distance);
		if (radialDist!=null) space.radialDist = Float.parseFloat(radialDist);
		if (repellDist!=null) space.repellDist = Float.parseFloat(repellDist);
		if (perminflate!=null) space.perminflate = Integer.parseInt(perminflate);
		if (inflateTime!=null) space.inflatetime = Integer.parseInt(inflateTime);
		if (valenceFact!=null) space.setVal(Float.parseFloat(valenceFact));
	}

	@Override
	public void start() {
		animator.start();
	}

	@Override
	public void stop() {
		animator.stop();
	}
}