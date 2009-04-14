package semaGL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import javax.media.opengl.GL;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import data.*;
import semaGL.SemaSpace;

public class Layouter {

	private String fontFam = "Helvetica";
	private SemaSpace app;
	protected Net net;
	private float innerRad=100;
	private boolean first=true;
	HashMap<String, nodeTuple> replist;
	private int a=0;
	private int edgeTresh=2000;
	boolean circles= false;
	private boolean nodeAligned;
	
	Layouter (SemaSpace app_) {
		app= app_;
		replist = new HashMap<String, nodeTuple>();
		edgeTresh=Integer.parseInt(Messages.getString("edgeTresholdRepell"));
		circles = Boolean.parseBoolean(Messages.getString("SVGNodesCircles"));
		nodeAligned = Boolean.parseBoolean(Messages.getString("SVGNodesAligned"));
		fontFam = Messages.getString("SVGFontFamily");
	}

	public void applyAttributeColors() {
		for (Node n:app.ns.getView().nNodes) n.genColorFromAtt();
		for (Edge e:app.ns.getView().nEdges) e.genColorFromAtt();
	}
	public void applyPickColors() {
		float[] nodeHSV = new float[3];
		nodeHSV = Func.RGBtoHSV(app.pickGradEnd);
		float[] pickHSV = new float[3];
		pickHSV = Func.RGBtoHSV(app.pickGradStart);

		for (Node n :net.nNodes) {
			//	calculate hue based on network distance from selected node
			float max = app.pickdepth;
			float grad = n.pickDistance/max;
			float hue = pickHSV[0]+grad*(nodeHSV[0]-pickHSV[0]);
			float[] result = new float[3];
			result = Func.HSVtoRGB(hue,nodeHSV[1],nodeHSV[2]);
			//	set the color of the selection frame			
			float alpha= Math.max(0f,max-n.pickDistance+1); 
			result[3]= alpha;
			n.setPickColor(result);

			//			set the alpha of the node color	based on selection		
			if (app.fadeNodes&&!n.rollover&&!n.isFrame()) n.setAlpha(Math.max(0.05f,alpha)); else n.setAlpha(app.nodeColor[3]);

			n.genColorFromAtt();
		}
	}

	private float calcClusterDistance(Node n) {
		float x;
		if (!n.spiralcluster)
			x= app.clusterRad*n.cluster.size()+n.size()/2f;
		else
			x=spiral_rad(n, n.cluster.size());
		return x;
	}

	private float calcDist(Node a, Node b, float offset, float val) {
		float factor = 1+.5f*(Edge.edgeName(a, b).hashCode()/Integer.MAX_VALUE);
		float vprod = Math.min(a.getiDegree(),b.getiDegree());
		//float vprod = a.getiDegree()*b.getiDegree();
		float clusterdist = calcClusterDistance(a)+calcClusterDistance(b);
		//		return 10f;
		return factor*(clusterdist+offset*(1+vprod*val));
	}

	public Vector3D calcPivot(Net net2) {
		Vector3D pivot = new Vector3D();
		for (Node nodeRef: net2.nNodes){
			pivot.add(nodeRef.pos);
		}
		pivot.div(net.nNodes.size());
		return pivot;
	}
	public Vector3D calcPivot(HashSet<Node> nodes) {
		Vector3D pivot = new Vector3D();
		for (Node nodeRef: nodes){
			pivot.add(nodeRef.pos);
		}
		pivot.div(nodes.size());
		return pivot;
	}

	private void clusterCircle(final GL gl, float xRot, float yRot, Node aref) {
		aref.spiralcluster=false;
		int jcount=0;
		float matrix[] = new float[16];
		float rad = aref.cluster.size();
		float clusterDist = calcClusterDistance(aref);
		for (Node bref : aref.cluster){
			if (bref != null) {
				gl.glPushMatrix();
				gl.glLoadIdentity();
				gl.glTranslatef(aref.pos.x, aref.pos.y, aref.pos.z);
				//				gl.glRotatef(xRot, 0, 1, 0);
				//				gl.glRotatef(yRot, 1, 0, 0);
				gl.glRotatef(90+360*jcount/rad, 0, 0, 1);
				gl.glTranslatef(-app.clusterRad-clusterDist, 0, 0);
				gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrix, 0);
				bref.pos.setXYZ(matrix[12], matrix[13], matrix[14]);
				gl.glPopMatrix();
				jcount++;
			}
		}
	}
	private void clusterSpiral(final GL gl, float xRot, float yRot, Node aref) {
		aref.spiralcluster=true;
		glSpiral(gl, aref, aref.cluster);
	}

	void glSpiral(final GL gl,  Node aref, HashSet<Node> cluster) {
		int i=0;
		float r=0;
		float matrix[] = new float[16];
		for (Node bref : cluster){
			if (bref != null) {
				i++;
				r=spiral_rad(aref, i);
				gl.glPushMatrix();
				gl.glLoadIdentity();
				gl.glTranslatef(aref.pos.x, aref.pos.y, aref.pos.z);
				gl.glRotatef(spiral_angle(aref, i), 0, 0, 1);
				gl.glTranslatef(-r, 0, 0);
				gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrix, 0);
				bref.pos.setXYZ(matrix[12], matrix[13], matrix[14]);
				gl.glPopMatrix();
			}
		}
	}

	private float spiral_angle(Node n, int i) {
		return 90+(float)Math.sqrt(app.clusterRad*i+n.getSize())*75f;
	}
	private float spiral_rad(Node n, int i) {
		return app.clusterRad+(float)Math.sqrt(app.clusterRad*i+n.getSize())*10f;
	}
	public void clustersSetup(GL gl){
		float xRot = app.cam.getYRot();		//should be global camera orientation
		float yRot = app.cam.getXRot();


		for (Node aref:net.fNodes) {
			float rad = aref.cluster.size();

			//	if (fact>app.clusterRad*10f) 
			if (rad>8)
				clusterSpiral(gl, xRot, yRot, aref); 
			else
				//					if (rad>0) 
				clusterCircle(gl, xRot, yRot, aref);
		}
	}
	public Net getNet() {
		return net;
	}

	public void layoutCenterOnPivot() {
		Vector3D v;
		if (app.isRadial()&&app.isTree()) 
			return;
		else 
			v= calcPivot(net);
		for (Node nref: net.nNodes) {
			nref.pos.sub(v);
		}
	}

	public void layoutConstrainCircle (Node n, float x_, float y_, float rad) {
		Vector3D center = new Vector3D(x_,y_,0);
		Vector3D sub = n.pos.sub(n.pos,center);
		sub.normalize();
		sub.mult(rad);
		n.pos.setXYZ(center.add(center, sub));
	}

	public void layoutConstrainCircle (HashSet<Node> nodes) {
		float rad = 0;
		for (Node n:nodes) rad+=calcClusterDistance(n);
		rad = 5*rad/(float)Math.PI;

		Vector3D center = new Vector3D(0,0,0);
		for (Node n:nodes){
			Vector3D sub = n.pos.sub(n.pos,center);
			sub.normalize();
			sub.mult(rad);
			n.pos.setXYZ(center.add(center, sub));
		}
	}

	public void layoutDistance(float offset, float valencefactor, float attenuation, Net net) {
		if (net.nEdges.size()==0) return;
		float o = offset;
		if (app.flat) o*=0.5f;
		float val = valencefactor;
		float att = attenuation;
		float dist;
		Node a;
		Node b;
		for (Edge e : net.nEdges) {
			a= e.getA();
			b= e.getB();
			if (net.fNodes.contains(a)&&net.fNodes.contains(b)) {
				dist = calcDist(a,b,o,val);
				e.chain(dist, Math.min(1f,att)); 
			}
		}
	}
	public void layoutDistanceTree(float o_, float v_, float att_) {
		float offset = o_;
		if (app.flat) offset*=0.5f;
		float val = v_;
		float att = att_;
		float dist;
		Node a;
		Node b;
		int adist;
		int bdist;
		for (Edge eref : net.nEdges) {
			a= eref.getA();
			b= eref.getB();
			if ((net.fNodes.contains(a)&&net.fNodes.contains(b))) {
				if (net.distances.contains(a)) adist = net.distances.getNodeDistance(a); else adist=Integer.MAX_VALUE;
				if (net.distances.contains(b)) bdist = net.distances.getNodeDistance(b); else bdist=Integer.MAX_VALUE;
				dist = calcDist(a,b,offset,val);
				if (adist<bdist) eref.chainB(dist, Math.min(1f,att*val)); 
				if (adist>bdist) eref.chainA(dist, Math.min(1f,att*val));
				if (adist==bdist) eref.chain(dist, Math.min(1f,att*val)); 
			}
		}
	}

	public void layoutFlat(){
		app.setYRotNew(0f);
		app.setXRotNew(0f);
		for (Node nref:net.fNodes) {
			nref.pos.z=0f;

		}
	}
	public void layoutInflate(float st_, Net net2) {
		float strength = st_;
		BBox3D bounds = BBox3D.calcBounds(net2.nNodes);
		layoutCenterOnPivot();
		if (net2.fNodes.size()==1) return;
		for (Node nodeRef: net2.fNodes) {
			if (!nodeRef.isLocked()) {
				Vector3D trans= new Vector3D();
				Vector3D corr= new Vector3D();
				//			if (nodeRef.adList.size()>0){
				trans.setXYZ(nodeRef.pos);
				trans.normalize();
				trans.mult(strength);
				corr.setXYZ(bounds.size);
				corr.normalize();
				trans.setXYZ(trans.x*(1-corr.x),trans.y*(1-corr.y),trans.z*(1-corr.z));
				nodeRef.pos.add(trans);
			}
		}
	}
	//in work, not finished ...
	public void layoutInflateLocal(float st_, HashSet<Node> nodes) {
		float strength = st_;
		Vector3D trans= new Vector3D();
		Vector3D piv = calcPivot(nodes);
		for (Node tmp: nodes) {
			trans.setXYZ(tmp.pos);
			trans.sub(piv);
			trans.normalize();
			trans.mult(strength);
			//			corr.setXYZ(bounds.size);
			//			corr.normalize();
			//			trans.setXYZ(trans.x*(1-corr.x),trans.y*(1-corr.y),trans.z*(1-corr.z));
			tmp.pos.add(trans);
			//			}
		}
	}

	public void layoutLineUp(boolean radial) {
		app.setCluster(false);
		int i=0;
		int level=0;
		ArrayList<Node> next = new ArrayList<Node>();
		ArrayList<Node> nextTmp = new ArrayList<Node>();
		HashMap<String, Node> all = new HashMap<String,Node>();

		for (Node n:net.nNodes) {
			if (n.inList.size()==0&&n.adList.size()>=0) {
				if (radial) layoutConstrainCircle(n, 0, 0, innerRad);
				else {
					n.pos.y = 0;
				}
				next.addAll(n.adList);
				all.put(n.name,n);
				if (first) for (Node nn:n.adList) nn.pos.setXYZ(n.pos);
				setNodeColor(level, n);
				i++;
			}
		}
		level++;
		innerRad = all.size()*app.TWO_PI;

		while (next.size()>0) {
			for (Node m:next){
				if (radial) layoutConstrainCircle(m, 0, 0, innerRad+level*app.radialDist);
				else m.pos.y = level *app.radialDist;
				if (!all.containsKey(m.name)){
					nextTmp.addAll(m.adList);
					setNodeColor(level, m);
					if (first) for (Node nn:m.adList) nn.pos.setXYZ(m.pos);
				}
				all.put(m.name, m);
			}
			next.clear();
			next.addAll(nextTmp);
			nextTmp.clear();
			level++;
		}
		first = false;
		//		disturbNodes(0.001f);
	}

	public void layoutLockNode(Node node, Vector3D vector3D, Net net) {
		net.posTable.put(node, vector3D.copy());
		node.setLocked(true);
	}

	public void layoutLockPlace(Net net2) {
		Set<Entry<Node, Vector3D>> locks = net2.posTable.entrySet();
		for (Entry<Node, Vector3D> n:locks) {
			n.getKey().pos.setXYZ(n.getValue());
		}
	}
	public void layoutLockRemove(Node picked, Net net2) {
		net2.posTable.remove(picked);
		picked.setLocked(false);
	}
	public void layoutLocksAll() {
		for (Node n:net.fNodes) {
			net.posTable.put(n, n.pos.copy());
			n.setLocked(true);
		}
	}
	public void layoutLocksRemove() {
		net.posTable.clear();
		for (Node n:net.nNodes) n.setLocked(false);
	}
	public void layoutNodePosJitter(float m) {
		for (Node n:net.nNodes) {
			n.pos.x *=(1+(Math.random()-0.5f)*m);
			n.pos.y *=(1+(Math.random()-0.5f)*m);
			n.pos.z *=(1+(Math.random()-0.5f)*m);
		}
	}
	public void layoutNodePosPlace(){
		for (Node n :net.fNodes) {
			float randPos = net.fNodes.size()*10f+50f;
			n.pos.setXYZ(Integer.MAX_VALUE/(float)n.getId(),Integer.MAX_VALUE/((float)String.valueOf(n.getId()+3).hashCode()), Func.rnd(-randPos,randPos));
		}
	}
	public void layoutNodePosRandomize(){
		for (Node n :net.fNodes) {
			float randPos = (float)Math.sqrt(net.fNodes.size())*50f+50f;
			n.pos.setXYZ(Func.rnd(-randPos,randPos), Func.rnd(-randPos,randPos), Func.rnd(-randPos,randPos));
		}
	}

	public void layoutNodePosZNoise() {
		for (Node n:net.fNodes) {
			n.pos.z+=(Math.random()-0.5)*2;
		}
	}

	//repell all nodes
	public void layoutRepell(float abstand, float strength , Net net){
		int etresh = net.fNodes.size();

		if (app.opt) layoutRepVisible ( abstand,  strength);
		else 
			if (etresh>edgeTresh) layoutRepFruchtermannLazy( abstand,  strength, net );
			else layoutRepFruchtermann( abstand,  strength, net );
	}

	private void layoutRepFruchtermann(float abstand, float strength, Net net ){
		Vector3D dist = new Vector3D();

		for (Node a: net.fNodes) {
			for (Node b: net.fNodes) {
				if (a!=b) {
					repFrucht(abstand, strength, dist, a, b, app.getRepellMax()); 
				}
			}
		}
	}

	/**
	 * optimized repulsions based on lists. nodes with distance > 5*rad are removed from list and not evaluated next time.
	 * @param abstand
	 * @param strength
	 * @param net
	 */
	private void layoutRepFruchtermannLazy(float abstand, float strength, Net net ){
		Vector3D dist = new Vector3D();
		//		System.out.println(replist.size());
		a++;

		Object[] array = net.fNodes.toArray();
		for (int i=0; i<net.fNodes.size(); i++){
			Node a = (Node)array[(int) (Math.random()*array.length)];
			Node b = (Node)array[(int) (Math.random()*array.length)];
			if (a!=b) {
				replist.put(nodeTuple.getName(a, b), new nodeTuple(a,b));
			}
		} 

		Object[] values = replist.values().toArray();
		for (Object n:values) {
			repFrucht(abstand, strength, dist, (nodeTuple)n,app.getRepellMax()); 
		}
	}

	private float repFrucht(float abstand, float strength, Vector3D dist, nodeTuple n, int max) {
		Node a = n.getA();
		Node b = n.getB();
		
		if (a.adList.size()+a.inList.size()==0||b.adList.size()+b.inList.size()==0) max = 0;
		dist.setXYZ(b.pos);
		dist.sub(a.pos);
		float d = dist.magnitude()+0.000000001f;
		float radius = calcClusterDistance(a)+calcClusterDistance(b)+abstand;
		float f=0;

		if (d<Math.max(max,radius)) {
			if (d<radius) {
				f = 1-(d/radius);
			}
			else {
				f = 0.1f/d;
			}
			dist.mult(f*strength);
			b.pos.add(dist);
			a.pos.sub(dist);
		} 

		if (d>radius*5) replist.remove(n.getName()); 
		return d;
	}

	public void layoutRepFruchtermannRadial(float abstand, float strength ){
		Vector3D dist = new Vector3D();
		for (HashSet<Node>e:net.distances.nodeSets()) {
			for (Node n:e) {
				for(Node m:e) {
					if (n!=m) repFrucht(abstand, strength, dist, n, m, app.getRepellMax()); 
				}
			}
		}
	}
	/**
	 * @param abstand
	 * @param strength
	 * @param dist
	 * @param a
	 * @param b
	 * @return
	 */
	private float repFrucht(float abstand, float strength, Vector3D dist,
			Node a, Node b, int max) {
		if (a.adList.size()+a.inList.size()==0||b.adList.size()+b.inList.size()==0) max = 0;
		dist.setXYZ(b.pos);
		dist.sub(a.pos);
		float d = dist.magnitude()+0.000000001f;
		float radius = calcClusterDistance(a)+calcClusterDistance(b)+abstand;
		float f=0;

		if (d<Math.max(max,radius)) {
			if (d<radius) {
				f = 1-(d/radius);
			}
			else {
				f = 0.1f/d;
			}
			dist.mult(f*strength);
			b.pos.add(dist);
			a.pos.sub(dist);
		}
		return d;
	}

	// experimental - repell only top. neighbourhood
	void layoutRepNeighbors(float strength, float offset, Net net2){
		Vector3D dist = new Vector3D();
		for (Node a: net2.fNodes) {
			HashSet<Node> tmp = new HashSet<Node>();
			for (Node a1:a.adList){
				if (net2.fNodes.contains(a1)) tmp.add(a1);
			}
			for (Node a1:a.inList){
				if (net2.fNodes.contains(a1)) tmp.add(a1);
			}
			if (tmp.size()>1&&tmp.size()<5){
				for (Node b: tmp) {
					for (Node c: tmp) {
						if (b!=c&&net2.fNodes.contains(b)&&net2.fNodes.contains(c)&&!b.adList.contains(c)&&!c.adList.contains(b)) {
							dist .setXYZ(c.pos);
							dist.sub(b.pos);
							float d = dist.magnitude();
							float radius=2*calcDist(a, b, offset, app.getVal())/tmp.size();
							if (d<radius) {
								dist.mult((1-(d/radius))*strength); 
								c.pos.add(dist);
								b.pos.sub(dist);
							}
						}
					}
				}
			}
		}
	}

	private void layoutRepNOpt(float abstand, float strength ){
		Vector3D dist = new Vector3D();
		for (Node n1ref: net.fNodes) {
			for (Node n2ref: net.fNodes) {
				if (n1ref!=n2ref) {
					dist.setXYZ(n2ref.pos);
					dist.sub(n1ref.pos);
					//					dist.normalize();
					float d = dist.magnitude();
					float radius = calcClusterDistance(n1ref)+calcClusterDistance(n2ref)+abstand;
					float f = 1-(d/radius);
					if (d<radius) {
						dist.mult(f*strength); 
						n2ref.pos.add(dist);
						n1ref.pos.sub(dist);
					}
				}
			}
		}
	}
	// here only the strongly visible nodes repell the others ... 
	private void layoutRepVisible(float abstand, float strength ){
		Vector3D dist = new Vector3D();
		net.repNodes.clear();
		for (Node tmp: net.fNodes) {
			if (tmp.color[3]>0.2)net.repNodes.add(tmp);
		}

		for (Node a: net.fNodes) {
			for (Node b: net.fNodes) {
				if ((a!=b)&&(a.color[3]>0.2&&b.color[3]>0.2)) {
					repFrucht(abstand, strength, dist, a, b, app.getRepellMax()); 
				}
			}
		}
	}
	public void layoutTree(boolean radial) {
		app.setCluster(false);
		int i=0;
		int total = 0;

		for (Node n:net.nNodes) {
			if (n.inList.size()==0&&n.adList.size()>=0) {
				i++;
			}
		}

		innerRad = i*app.TWO_PI;
		total=i;
		i=0;

		for (Node n:net.nNodes) {
			if (n.inList.size()==0&&n.adList.size()>=0) {
				if (radial) {
					layoutConstrainCircle(n, 0, 0, innerRad);
					float alpha = ((float)i/(float)total)*app.TWO_PI;
					n.pos.x=(float)Math.cos(alpha)*innerRad;
					n.pos.y=(float)Math.sin(alpha)*innerRad;
				}
				else {
					n.pos.y = 0;
					n.pos.x = i*100;
				}
				i++;
			}
		}
		layoutNodePosJitter(0.01f);
	}

	void render(GL gl, int fonttype, Net view, GraphRenderer nr){
		Layouter layout = this;
		if (!app.isTree()) layout.renderClusters(gl, nr);
		if (app.isGroups()) {layout.renderGroups(gl,nr, view,fonttype);
		layout.renderGroupLabels(gl, nr, view,fonttype);}
		if (app.isEdges()) layout.renderEdges(gl, nr, fonttype);
		layout.renderNodes(gl,nr, fonttype);
		layout.renderLabels(gl,nr, fonttype);
	}

	/**
	 * setup and render the grouped nodes in net
	 * @param gl
	 * @param nr
	 */
	void renderClusters(GL gl, GraphRenderer nr) {
		clustersSetup( gl );
		HashSet<Node>cl=null;
		for (Node n:net.fNodes) {
			cl=n.cluster;
			float rad = cl.size();
			if (rad!=0&&app.drawClusters) {
				nr.renderFan(gl, cl, n);
			}
		}
	}

	/**
	 * render edges contained in net
	 * @param gl
	 * @param nr
	 * @param text
	 */
	public  void renderEdges(GL gl, GraphRenderer nr, int text) {
		for (Edge eref: net.nEdges) {
			nr.renderEdges(gl, eref);
		}
	}

	/**
	 * render node labels with font = text 
	 * @param gl
	 * @param nr
	 * @param text
	 */
	void renderLabels(GL gl, GraphRenderer nr, int text) {
		//		boolean fast = (net.nNodes.size()>edgeTresh);
		boolean fast = false;
		for (Node nref: net.nNodes)	nr.renderNodeLabels(gl, nref, text, fast);
		for (Edge eref: net.nEdges) nr.renderEdgeLabels(gl, eref, text, fast);
	}

	/**
	 * render the nodes in net
	 * @param gl
	 * @param nr
	 * @param text
	 */
	public  void renderNodes(GL gl, GraphRenderer nr,  int text) {
		applyPickColors();
		for (Node n: net.nNodes) {
			nr.renderNode(gl, n);
		}
	}

	void renderSVG(GL gl, GraphRenderer nr,  int text, String filename) throws UnsupportedEncodingException, SVGGraphics2DIOException {
		/*
		HashMap<Node,double[]> node2Dpos = new HashMap<Node, double[]>();
		HashMap<Edge,double[]> edge2Dvec = new HashMap<Edge, double[]>();

		if (!app.isTree()) renderClusters(gl, nr);

		for (Node n: net.nNodes) {
			double[] pos = nr.project2screen(gl, n.pos);
			node2Dpos.put(n, pos);
		}
		for (Edge e: net.nEdges) {
			double[] a = nr.project2screen(gl, e.getA().pos);
			double[] b = nr.project2screen(gl, e.getB().pos);
			double[] evec = {a[0],a[1],b[0],b[1]};
			edge2Dvec.put(e, evec);
		}*/

		createSVG(filename);
	}

	private void createSVG(String filename)
	throws UnsupportedEncodingException, SVGGraphics2DIOException {

		BBox3D bbx = BBox3D.calcBounds(net.nNodes);

		// Get a DOMImplementation.
		DOMImplementation domImpl =
			GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document doc = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgG = new SVGGraphics2D(doc);
		Dimension bounds = new Dimension((int)bbx.size.x+400, (int)bbx.size.y+400);

		svgG.setSVGCanvasSize(bounds);

		paintSVG(svgG, bbx.min.x-200, bbx.min.y-200);

		//		GVTBuilder builder = new GVTBuilder();
		//		BridgeContext ctx;
		//		ctx = new BridgeContext(new UserAgentAdapter());
		//		GraphicsNode gvtRoot = builder.build(ctx, document);
		//		Rectangle2D b2d = gvtRoot.getSensitiveBounds();
		//		svgGenerator.setSVGCanvasSize(new Dimension((int) b2d.getWidth(), (int) b2d.getHeight()));
		//		svgGenerator.getRoot().setAttributeNS(svgNS, "viewBox", b2d.getMinX()+","+b2d.getMinY()+","+b2d.getMaxX()+","+b2d.getMaxY()); 

		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = false; // we want to use CSS style attributes
		svgG.stream(filename, useCSS);
	}

	public void paintSVG(Graphics2D g2d, float origX, float origY) {
		int font = app.fonttype;
		BasicStroke sngl = new BasicStroke(1f);
		BasicStroke dbl = new BasicStroke(2f); 
		Font ef = new Font(fontFam,Font.PLAIN, (int)(app.getLabelsize()));

		AffineTransform id = new AffineTransform();
		id.setToIdentity();

		AffineTransform t = new AffineTransform();
		t.setToIdentity();
		t.translate(-origX, -origY);
		g2d.setTransform(t);

		// edges
		for (Edge e: net.nEdges) {
			e.genColorFromAtt();

			Node a = e.getA();
			Node b = e.getB();
			float af = a.size(); 
			float bf = b.size(); 

			Vector3D D = Vector3D.sub(b.pos, a.pos);
			Vector3D DN= D.copy();
			DN.normalize();

			Vector3D start = a.pos.copy();
			Vector3D end = b.pos.copy();
			start.add(Vector3D.mult(DN, af));
			end.sub(Vector3D.mult(DN, bf));
			if (font==0){
				g2d.setStroke(dbl);
				g2d.setPaint(new Color(1,1,1,e.color[3]));
				g2d.drawLine((int)start.x,(int)start.y,(int)end.x,(int)end.y);
			}
			g2d.setStroke(sngl);
			g2d.setPaint(new Color(e.color[0],e.color[1],e.color[2],e.color[3]));
			g2d.drawLine((int)start.x,(int)start.y,(int)end.x,(int)end.y);
		}

		// clusters
		if (app.cluster&&app.drawClusters){
			for (Node n: net.nNodes) {
				if (n.cluster.size()>1) {
					float[] col = GraphElement.colorFunction(n.name);
					col[3]=Math.min(n.alpha, 0.05f);
					Polygon p = new Polygon();
					p.addPoint((int)n.pos.x, (int)n.pos.y);
					for (Node c:n.cluster){
						p.addPoint((int)c.pos.x, (int)c.pos.y);
					}
					Node c= n.cluster.iterator().next();
					p.addPoint((int)c.pos.x, (int)c.pos.y);
					p.addPoint((int)n.pos.x, (int)n.pos.y);
					g2d.setPaint(new Color(col[0],col[1],col[2],col[3]));
					g2d.fillPolygon(p);
				}
			}
		}

		// nodes
		for (Node n: net.nNodes) {
			n.genColorFromAtt();
			float size = n.size()*2f;
			g2d.setPaint(new Color(n.color[0],n.color[1],n.color[2],n.color[3]));
			if (circles) g2d.fillOval((int)(n.pos.x)-(int)(size/2), (int)(n.pos.y)-(int)(size/2), (int)size, (int)size);
			else g2d.fillRect((int)(n.pos.x)-(int)(size/2), (int)(n.pos.y)-(int)(size/2), (int)size, (int)size);
		}

		if (font!=3){
			// edge labels
			for (Edge e: net.nEdges) {
				String txt = e.genTextSelAttributes();
				if (txt.length()>0){

					g2d.setFont(ef);
					Node a = e.getA();
					Node b = e.getB();

					Vector3D midP = Vector3D.midPoint(a.pos,b.pos);
					TextLayout tl = new TextLayout(txt,ef,g2d.getFontRenderContext());

					g2d.setPaint(new Color(e.color[0],e.color[1],e.color[2],e.color[3]));

					g2d.translate((int)(midP.x), (int)(midP.y));

					Vector3D sub = Vector3D.sub(a.pos, b.pos);
					float angle = (float) (Math.atan(sub.y/sub.x));
					g2d.rotate(angle);
					g2d.translate(0, -2);

					float advance = tl.getAdvance()/2f;
					g2d.translate(-advance, 0);

					if (e.color[3]>0.2f&& txt.length()>0){
						if (font==0){
							g2d.setPaint(new Color(1,1,1,e.color[3]));
							Shape outline = tl.getOutline(id);
							g2d.setStroke(dbl);
							g2d.draw(outline);
							g2d.setPaint(new Color((e.color[0]*0.5f),(e.color[1]*0.5f),(e.color[2]*0.5f),e.color[3]));
							g2d.setStroke(sngl);
							g2d.fill(outline);

						} else {
							g2d.setFont(ef);
							g2d.setPaint(new Color(e.color[0],e.color[1],e.color[2],e.color[3]));
							g2d.drawString(txt, 0, 0);
						}
					}
					g2d.setTransform(t);
				}
			}

			// node labels
			for (Node n: net.nNodes) {
				String txt = n.genTextSelAttributes();

				if (txt.length()>0){
					float size = n.size();
					String[] sp = txt.split("\n");


					if (n.color[3]>0.2f&& txt.length()>0) {
						for (int i = 0; i<sp.length; i++){

							g2d.translate((int)(n.pos.x), (int)(n.pos.y));
							if (!app.isTree()&&!app.labelsEdgeDir) {
								g2d.translate((int)(size/2),-(int)(size/2));
							}
							if (app.tilt) {
								g2d.rotate(-0.436332312998582);
							} 

							int fntsize = (int)((app.getLabelsize()+n.size()*app.getLabelVar())*1.5f);
							Font varFont = new Font(fontFam,Font.PLAIN, fntsize);
							FontRenderContext frc = g2d.getFontRenderContext();
							TextLayout tl = new TextLayout(sp[i],varFont,frc);

							if (app.isTree()) alignLabel(g2d, n.pos, n.size(), tl);

							if (app.labelsEdgeDir&&!app.tilt){
								if (n.adList.size()==1) {
									Vector3D sub = Vector3D.sub(n.pos, n.adList.iterator().next().pos);
									alignLabel(g2d, sub, n.size(), tl);
								} else
									if (n.inList.size()==1) {
										Vector3D sub = Vector3D.sub(n.pos, n.inList.iterator().next().pos);
										alignLabel(g2d, sub, n.size(), tl);
									}
									else {
										float advance = tl.getAdvance()/2f;
										g2d.translate(-advance, -n.size()/2f);
									}
							}

							if (font==0) {
								g2d.setPaint(new Color(1,1,1,n.color[3]));
								g2d.setStroke(dbl);
								g2d.translate(0, i*fntsize);
								Shape outline = tl.getOutline(id);
								g2d.draw(outline);
								g2d.setPaint(new Color((n.color[0]*0.5f),(n.color[1]*0.5f),(n.color[2]*0.5f),n.color[3]));
								g2d.setStroke(sngl);
								g2d.fill(outline);
							} else {
								g2d.setFont(varFont);
								g2d.setPaint(new Color((n.color[0]*0.5f),(n.color[1]*0.5f),(n.color[2]*0.5f),n.color[3]));
								//								tl.draw(g2d, 0, i*fntsize);
								g2d.drawString(sp[i], 0, i*fntsize);
							}
							g2d.setTransform(t);
						}
					}
				}
			}
		}
	}

	private void alignLabel(Graphics2D g2d, Vector3D n, float margin, TextLayout tl) {
		float angle = (float) (Math.atan(n.y/n.x));
		g2d.rotate(angle);
		float marg = margin+5;
		if (n.x<0) {
			float advance = tl.getAdvance()+marg;
			g2d.translate(-advance, 0);
		} else	g2d.translate(marg, 0);
	}

	/**
	 * render the groups defined in the group attribute
	 * @param gl
	 * @param nr
	 * @param net
	 * @param fonttype
	 */
	public void renderGroups(GL gl, GraphRenderer nr, Net net, int fonttype) {
		for (String n:net.groups.keySet()) {
			Net group = net.groups.get(n);
			Node center = group.hasNode(n);
			nr.renderGroups(gl, group.nNodes, center);

			//						nr.renderNode(gl, center);
			//
			//						for (Node eref: group.nNodes) {
			//							nr.renderNode(gl, eref);
			//							nr.renderNodeLabels(gl, eref, 2, false);
			//						}
			//						
			//						for (Edge eref: group.nEdges) {
			//							nr.renderEdges(gl, eref);
			//						}
		}
	}
	/**
	 * render the group labels
	 * @param gl
	 * @param nr
	 * @param net
	 * @param fonttype
	 */
	public void renderGroupLabels(GL gl, GraphRenderer nr, Net net, int fonttype) {
		Node center;
		for (String m:net.groups.keySet()) {
			Net group1 = net.groups.get(m);
			center = group1.hasNode(m);
			nr.renderGroupLabels(gl, center, fonttype);
		}
	}

	public void setNet(Net net) {
		this.net = net;
	}
	/**
	 * calculate node color based on gradient and level
	 * @param level
	 * @param m
	 */
	void setNodeColor(int level, Node m) {
		float[] nodeHSV = new float[3];
		nodeHSV = Func.RGBtoHSV(app.pickGradEnd);
		float[] pickHSV = new float[3];
		pickHSV = Func.RGBtoHSV(app.pickGradStart);

		if (m.hasAttribute("color")) m.setColor(Func.parseColorInt(m.getAttribute("color")));
		else {
			float[] color = Func.colorGrad(level, nodeHSV, pickHSV);
			m.setColor(color);
		}
	}

	public void layoutEgocentric() {
		net.clearClusters();
		for (Node n:net.nNodes) {
			float f=0;
			if (net.distances.contains(n)) layoutConstrainCircle(n, 0, 0, (net.distances.getNodeDistance(n)+f)*(app.radialDist));
		}
	}
	public void layoutTimeline() {
		net.clearClusters();
		Collection<Float> time = net.timeTable.values();
		if (time.size()==0) return;
		TreeSet<Float> b = new TreeSet<Float>();
		b.addAll(time);
		Float midpoint = (b.last()-b.first())/2f+b.first();

		for (Node n:net.nNodes) {
			if (n.getTime()!=null) {
				n.pos.setX( (n.getTime()-midpoint)*100f);
				n.pos.y*=.9f;
			}
		}
	}
	public void layoutGroups(Net net) {


		for (String n:net.groups.keySet()) {
			Net group = net.groups.get(n);
			Vector3D center = calcPivot(group.nNodes);
			group.hasNode(n).pos.setXYZ(center);

			//					layoutDistance(app.nodeSize*4f, 0, 1f, group);
			//		layoutRepell(app.nodeSize*4f, .5f, group);
			//		layoutInflate(net.nNodes.size()+10f, net);
		}
	}

	public void layoutBox(HashSet<Node> nodes) {
		int total=0;
		int step=0;
		boolean y= false;
		Vector3D cursor = new Vector3D(0,0,0);
		float abstand = app.boxdist;
		Iterator<Node> it = nodes.iterator();
		Node last=null;
		int max = 1;
		int direction= 0;
		while (total<nodes.size()) {
			last = it.next();
			last.pos.setXYZ(cursor);
			oneStep(direction, cursor, abstand);
			step++;
			total++;
			if (step>=max) {
				direction++;
				step=0;
				y=!y;
				if (!y) max++;
				if (direction>3) direction=0;
			}
		}
	}
	public void oneStep(int direction, Vector3D curs, float abstand){
		switch (direction) {
		case 0:
			curs.x+=abstand;
			break;
		case 1:
			curs.y+=abstand;
			break;
		case 2:
			curs.x-=abstand;
			break;
		case 3:
			curs.y-=abstand;
			break;
		default:
			break;
		}
	}
	public void initRadial(float xn, float yn, float rad) {
		if (net.distances.getNodesAtDistance(0) == null) return;
		Node start = net.distances.getNodesAtDistance(0).iterator().next();

		HashSet<Node> first = new HashSet<Node>();
		first.addAll(start.adList);
		first.addAll(start.inList);
		float alpha = (float)Math.PI*2/(float)first.size();
		float x;
		float y;
		int i = 1;
		for (Node n:first) {
			y = (float) (yn + Math.sin(alpha*i)*rad);
			x = (float) (xn + Math.cos(alpha*i)*rad);
			n.pos.setXY(x, y);
			i++;
		}
	}
}
