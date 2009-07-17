package semaGL;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import com.sun.opengl.util.GLUT;

import data.Net;
import data.Vector3D;

public class SemaParameters {
	GLUT glut = new GLUT();
	//	HashSet<String> map = Messages.getArray("map");
	String cacheDir = "./cache/"; //$NON-NLS-1$
	public  String texurl = "http://"; //$NON-NLS-1$
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
	public int pickID=-1;
	public boolean calculate = true;
	int fogMode[] = {GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR};	// Storage For Three Types Of Fog ( new )
	int fogfilter = 2;								// Which Fog Mode To Use      ( new )
	float fogColor[] = {0.9f, 0.9f, 0.9f, 1.0f};		// Fog Color   
	float znear = 10.0f;
	float zfar = 100000f;
	String file[];
	final float TWO_PI =6.283185307179586476925286766559f;
	private float h;
	boolean FOG = true;
	float FOV = 70f;
	boolean edges=true;
	public boolean directed=true;
	public int ageThresh=Integer.MAX_VALUE;
	public boolean render= true;
	boolean tree = false;
	boolean radial = false;
	boolean repNeighbors = false;
	int overID;
	private String attribute="none"; //$NON-NLS-1$
	private boolean timeline;
	private boolean tabular = true;
	boolean labelsEdgeDir=true;
	public boolean fadeLabels=false;
	private int repellMax = 1000;
	public String splitAttribute = "; ";
	private float edgeAlpha;
	private boolean groups;
	private boolean enableSvg;
	private String fontFam;
	float maxLabelRenderDistance;
	public int shotres;
	public boolean fullscreen;
	boolean textureFont=false;
	private float labelsize;
	private float labelVar;
	private boolean inheritEdgeColorFromNodes;
	public boolean drawClusters;
	private boolean tilt;
	private float invar;
	private float outvar;
	boolean textures;
	int loadMethod; // load method: 0= local file, 1=from URL, 2= from inside jar. used filename see above
	private boolean cluster;
	private float perminflate;
	private int thumbsize;
	private String filename;
	private String texfolder;
	int searchdepth;
	private float standardNodeDistance;
	private float repellDist;
	private float nodeSize;
	private int picSize;
	float strength;
	private float val;
	float repellStrength;
	private float clusterRad;
	private float radialDist;
	private float boxdist;
	int pickdepth;
	private long inflatetime;
	private float edgewidth;
	private boolean exhibitionMode;
	private boolean repell;
	private boolean svgNodeCircles;
	public int edgeThreshold;
	private SemaSpace app;
	private boolean startWhole=false;


	public  SemaParameters(SemaSpace semaSpace) {
		setApp(semaSpace);
	}

	public SemaParameters loadSemaParameters(String file) {
		Properties props = new Properties();
		try {
			FileInputStream fi = new FileInputStream(file);
			props.load(fi);
			loadParams(props);
		} catch (IOException e) {
			System.out.println("File not found: "+file);
		}
		return this;
	}
	
	public SemaParameters loadSemaParameters(URL file) {
		Properties props = new Properties();
		try {
			InputStream str = file.openStream();
			props.load(str);
			loadParams(props);
		} catch (IOException e) {
			System.out.println("URL not found: "+file);
		}
		return this;
	}
	
	public SemaParameters loadSemaParametersJar(String file) {
		Properties props = new Properties();
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(file);
			props.load(is);
			loadParams(props);
		} catch (IOException e) {
			System.out.println("Ressource not found: "+file);
		}
		return this;
	}

	private void loadParams(Properties props) {
		setStartWhole(Boolean.parseBoolean(props.getProperty("showWholeNetOnStart")));
		fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen"));
		tabular = Boolean.parseBoolean(props.getProperty("tabularFileFormat"));
		edgeThreshold = Integer.parseInt(props.getProperty("edgeTresholdRepell"));
		setSvgNodeCircles(Boolean.parseBoolean(props.getProperty("SVGNodesCircles")));
		exhibitionMode = Boolean.parseBoolean(props.getProperty("exhibitionMode"));
		setRepell(Boolean.parseBoolean(props.getProperty("repellOn")));
		setFilename(props.getProperty("defaultFilename")); //$NON-NLS-1$
		setTexfolder(props.getProperty("textureDirectory")); //$NON-NLS-1$
		searchdepth = Integer.parseInt(props.getProperty("searchdepth"));
		setStandardNodeDistance(Float.parseFloat(props.getProperty("standardNodeDistance")));
		setRepellDist(Float.parseFloat(props.getProperty("repellDistance")));
		setNodeSize(Float.parseFloat(props.getProperty("nodeSize")));
		picSize = Integer.parseInt(props.getProperty("picSize"));
		strength = Float.parseFloat(props.getProperty("edgeStrength"));
		val = Float.parseFloat(props.getProperty("valenceFactor"));
		repellStrength = Float.parseFloat(props.getProperty("repellStrength"));
		clusterRad= Float.parseFloat(props.getProperty("clusterRadius"));
		setRadialDist(Float.parseFloat(props.getProperty("radialLayoutDistance")));
		setBoxdist(Float.parseFloat(props.getProperty("boxLayoutDistance")));
		pickdepth = Integer.parseInt(props.getProperty("pickDistance"));
		setInflatetime(Long.parseLong(props.getProperty("inflateTimeMs")));
		setEdgewidth(Float.parseFloat(props.getProperty("edgeWidth")));
		setPerminflate(Float.parseFloat(props.getProperty("permanentInflate")));
		setThumbsize(Integer.parseInt(props.getProperty("thumbnailRes")));
		cluster=Boolean.parseBoolean(props.getProperty("layoutClusters"));
		loadMethod = Integer.parseInt(props.getProperty("loadMethod")); //0 = local file, 1 = http, 2 = jar
		Color.decode(props.getProperty("pickGradientFar")).getComponents(pickGradEnd);
		Color.decode(props.getProperty("pickGradientCenter")).getComponents(pickGradStart);
		Color.decode(props.getProperty("rollOverColor")).getComponents(rollOverColor);
		Color.decode(props.getProperty("nodeColor")).getComponents(nodeColor);
		nodeColor[3]=Float.parseFloat(props.getProperty("nodeAlpha"));
		Color.decode(props.getProperty("edgeColor")).getComponents(edgeColor);
		Color.decode(props.getProperty("frameColor")).getComponents(frameColor);
		labelsEdgeDir = (Boolean.parseBoolean(props.getProperty("labelsEdgeDir")));
		repellMax = (int) Float.parseFloat(props.getProperty("repellMaxDist"));
		setEdgeAlpha(Float.parseFloat(props.getProperty("edgeAlpha")));
		groups =  Boolean.parseBoolean(props.getProperty("drawGroups"));
		setEnableSvg(Boolean.parseBoolean(props.getProperty("enableSVGexport")));
		setFontFam(props.getProperty("FontFamily"));
		shotres = Integer.parseInt(props.getProperty("screenshotResolution"));
		maxLabelRenderDistance = Float.parseFloat(props.getProperty("maxLabelRenderDistance"));
		textureFont= Boolean.parseBoolean(props.getProperty("useTextureFonts"));
		labelsize= Float.parseFloat(props.getProperty("labelSize"));
		labelVar= Float.parseFloat(props.getProperty("labelSizeVariance"));
		setInheritEdgeColorFromNodes(Boolean.parseBoolean(props.getProperty("inheritEdgeColorFromNodes")));
		drawClusters= Boolean.parseBoolean(props.getProperty("clusters"));
		setTilt(Boolean.parseBoolean(props.getProperty("tiltedLabels")));
		invar= Float.parseFloat(props.getProperty("nodeSizeInDegreeVariance"));
		outvar= Float.parseFloat(props.getProperty("nodeSizeOutDegreeVariance"));;
		textures= Boolean.parseBoolean(props.getProperty("textures"));
	}

	public void storeSemaParameters(String file) {
		Properties props = new SortedProperties();
		
		//general + files
		props.setProperty("exhibitionMode", String.valueOf(exhibitionMode));
		props.setProperty("defaultFilename", String.valueOf(getFilename())); //$NON-NLS-1$
		props.setProperty("loadMethod",""+loadMethod);
		props.setProperty("tabularFileFormat", String.valueOf(tabular));
		props.setProperty("textureDirectory", ""+texfolder); //$NON-NLS-1$
		props.setProperty("labelsEdgeDir",""+labelsEdgeDir);
		props.setProperty("screenshotResolution",""+shotres);
		props.setProperty("searchdepth", ""+searchdepth);
		props.setProperty("pickDistance",""+pickdepth);
		props.setProperty("showWholeNetOnStart",""+isStartWhole());

		//forces & layout
		props.setProperty("edgeTresholdRepell", String.valueOf(edgeThreshold));
		props.setProperty("repellOn",""+repell);
		props.setProperty("standardNodeDistance", ""+standardNodeDistance);
		props.setProperty("repellDistance",""+repellDist);
		props.setProperty("edgeStrength", ""+strength);
		props.setProperty("valenceFactor", ""+val);
		props.setProperty("repellStrength", ""+repellStrength);
		props.setProperty("permanentInflate",""+perminflate);
		props.setProperty("repellMaxDist",""+repellMax);
		props.setProperty("inflateTimeMs",""+inflatetime);
		
		//display
		props.setProperty("drawGroups", String.valueOf(groups));
		props.setProperty("nodeSize",""+nodeSize);
		props.setProperty("picSize",""+picSize);
		props.setProperty("clusterRadius", ""+clusterRad);
		props.setProperty("radialLayoutDistance",""+radialDist);
		props.setProperty("boxLayoutDistance", ""+boxdist);
		props.setProperty("edgeWidth",""+edgewidth);
		props.setProperty("thumbnailRes",""+thumbsize);
		props.setProperty("layoutClusters",""+cluster);
		props.setProperty("FontFamily",""+fontFam);
		props.setProperty("maxLabelRenderDistance",""+maxLabelRenderDistance);
		props.setProperty("useTextureFonts",""+textureFont);
		props.setProperty("labelSize",""+labelsize);
		props.setProperty("labelSizeVariance",""+labelVar);
		props.setProperty("inheritEdgeColorFromNodes",""+inheritEdgeColorFromNodes);
		props.setProperty("clusters",""+drawClusters);
		props.setProperty("tiltedLabels",""+isTilt());
		props.setProperty("nodeSizeInDegreeVariance",""+invar);
		props.setProperty("nodeSizeOutDegreeVariance",""+outvar);
		props.setProperty("textures",""+textures);
		
		//colors
		props.setProperty("pickGradientCenter","0x"+Integer.toHexString(Func.packColors(pickGradStart)));
		props.setProperty("pickGradientFar","0x"+Integer.toHexString(Func.packColors(pickGradEnd)));
		props.setProperty("rollOverColor","0x"+Integer.toHexString(Func.packColors(rollOverColor)));
		props.setProperty("edgeColor","0x"+Integer.toHexString(Func.packColors(edgeColor)));
		props.setProperty("edgeAlpha",""+edgeAlpha);
		props.setProperty("nodeColor","0x"+Integer.toHexString(Func.packColors(nodeColor)));
		props.setProperty("nodeAlpha",""+nodeColor[3]);
		props.setProperty("frameColor","0x"+Integer.toHexString(Func.packColors(frameColor)));

		//svg export
		props.setProperty("SVGNodesCircles", String.valueOf(svgNodeCircles));
		props.setProperty("enableSVGexport",""+enableSvg);

		OutputStream out;
		try {
			out = new FileOutputStream(file);
			props.store(out, "SemaSpace Project file");
//			props.storeToXML(out, "");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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

	public float getBoxdist() {
		return boxdist;
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

	public float getEdgeAlpha() {
		return edgeAlpha;
	}

	public String getEdgeUrl() {
		return edgeUrl;
	}

	public float getEdgewidth() {
		return edgewidth;
	}

	public String getFontFam() {
		return fontFam;
	}

	public int getFonttype() {
		return fonttype;
	}

	public long getInflatetime() {
		return inflatetime;
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

	public float getNodeSize() {
		return nodeSize;
	}

	public float getOutVar() {
		return outvar;
	}
	public int getOverID() {
		return overID;
	}

	public float getPerminflate() {
		return perminflate;
	}

	public float getPermInflate() {
		return getPerminflate();
	}

	public int getPickdepth() {
		return pickdepth;
	}

	public int getPicSize() {
		return picSize;
	}

	public float getRadialDist() {
		return radialDist;
	}

	public float getRepell() {
		return  getRepellDist();
	}
	public float getRepellDist() {
		return repellDist;
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

	public float getStandardNodeDistance() {
		return standardNodeDistance;
	}

	public float getStrength() {
		return strength;
	}

	public boolean getSvgNodeCircles() {
		return svgNodeCircles;
	}
	public String getTexfolder() {
		return texfolder;
	}

	public int getThumbsize() {
		return thumbsize;
	}

	public String getUrl() {
		return url;
	}

	public float getVal() {
		return val;
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

	public void setBoxdist(float boxdist) {
		this.boxdist = boxdist;
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

	public void setEdgeAlpha(float edgeAlpha) {
		this.edgeAlpha = edgeAlpha;
	}

	public void setEdges(boolean edges) {
		this.edges = edges;
	}

	public void setEdgeUrl(String edgeUrl) {
		this.edgeUrl = edgeUrl;
	}

	public void setEdgewidth(float edgewidth) {
		this.edgewidth = edgewidth;
	}

	public void setEnableSvg(boolean enableSvg) {
		this.enableSvg = enableSvg;
	}

	public void setExhibitionMode(boolean exhibitionMode) {
		this.exhibitionMode = exhibitionMode;
	}

	public void setFilename(String selectedFile) {
		filename = selectedFile;
	}

	public void setFontFam(String fontFam) {
		this.fontFam = fontFam;
	}

	public void setFonttype(int fonttype_) {
		fonttype = fonttype_;
	}

	public void setGroups(boolean groups) {
		this.groups = groups;
	}

	public void setInflatetime(long inflatetime) {
		this.inflatetime = inflatetime;
	}

	public void setInheritEdgeColorFromNodes(boolean inheritEdgeColorFromNodes) {
		this.inheritEdgeColorFromNodes = inheritEdgeColorFromNodes;
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

	public void setNodeSize(float nodeSize) {
		this.nodeSize = nodeSize;
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

	public void setPerminflate(float perminflate) {
		this.perminflate = perminflate;
	}

	public void setPermInflate(float f) {
		setPerminflate(f);
	}
	public void setPickdepth(int pickdepth) {
		this.pickdepth = pickdepth;
	}

	public void setPickID(int pickID) {
		this.pickID = pickID;
	}

	public void setPicSize(int picSize) {
		this.picSize = picSize;
	}

	public void setRadial(boolean selected) {
		radial = selected;
	}

	public void setRadialDist(float radialDist) {
		this.radialDist = radialDist;
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

	public void setRepellDist(float repellDist) {
		this.repellDist = repellDist;
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

	public void setStandardNodeDistance(float standardNodeDistance) {
		this.standardNodeDistance = standardNodeDistance;
	}

	public void setStrength(float f) {
		strength = f;
	}
	public void setSvgNodeCircles(boolean svgNodeCircles) {
		this.svgNodeCircles = svgNodeCircles;
	}

	public void setTabular(boolean tabular) {
		this.tabular = tabular;
	}

	public void setTexfolder(String texfolder) {
		this.texfolder = texfolder;
	}

	public void setTexFolder(String file2) {
		setTexfolder(file2);
	}

	public void setTextures(boolean textures) {
		this.textures = textures;
	}

	public void setThumbsize(int thumbsize) {
		this.thumbsize = thumbsize;
	}

	public void setTilt(boolean tilt) {
		this.tilt = tilt;
	}

	public void setTime(boolean selected) {
		timeline = selected;
	}	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public boolean isTilt() {
		return tilt;
	}

	public boolean isCluster() {
		return cluster;
	}

	public boolean isExhibitionMode() {
		return exhibitionMode;
	}

	public boolean isTextures() {
		return textures;
	}

	public boolean isTree() {
		return tree;
	}

	public boolean isInheritEdgeColorFromNodes() {
		return inheritEdgeColorFromNodes;
	}

	public boolean isLabelsEdgeDir() {
		return labelsEdgeDir;
	}

	public boolean isRadial() {
		return radial;
	}

	public boolean isGroups() {
		return groups;
	}

	public boolean isEdges() {
		return edges;
	}

	public boolean isRepell() {
		return repell;
	}

	public boolean isTabular() {
		return tabular;
	}

	public boolean isEnableSvg() {
		return enableSvg;
	}

	public boolean isRender() {
		return render;
	}

	public boolean isRepN() {
		return repNeighbors;
	}

	public boolean isTime() {
		return timeline;
	}

	public String getFilename() {
		return filename;
	}

	public void setApp(SemaSpace app) {
		this.app = app;
	}

	public SemaSpace getApp() {
		return app;
	}

	public Cam getCam() {
		return app.cam;
	}

	public GL getGL() {
		return app.glD.getGL();
	}

	public GLAutoDrawable getGlD() {
		return app.glD;
	}

	public Net getView() {
		return app.ns.getView();
	}

	public void setStartWhole(boolean startWhole) {
		this.startWhole = startWhole;
	}

	public boolean isStartWhole() {
		return startWhole;
	}

}

class SortedProperties extends Properties {
	  public Enumeration keys() {
	     Enumeration keysEnum = super.keys();
	     Vector<String> keyList = new Vector<String>();
	     while(keysEnum.hasMoreElements()){
	       keyList.add((String)keysEnum.nextElement());
	     }
	     Collections.sort(keyList);
	     return keyList.elements();
	  }
	  
	}