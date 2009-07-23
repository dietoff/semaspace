package semaGL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import javax.media.opengl.GL;

import data.*;
import semaGL.SemaSpace;

public class Layouter {


	private SemaParameters app;
	protected Net net;
	private float innerRad=100;
	private boolean first=true;
	HashMap<String, nodeTuple> replist;
	private int a=0;
	private int edgeTresh=2000;

	Layouter (SemaParameters app_) {
		app= app_;
		replist = new HashMap<String, nodeTuple>();
		edgeTresh= app.edgeThreshold;
	}

	public void applyAttributeColors() {
		for (Node n:app.getView().nNodes) n.genColorFromAtt();
		for (Edge e:app.getView().nEdges) e.genColorFromAtt();
	}
	public void applyPickColors() {
		float[] nodeHSV = new float[3];
		nodeHSV = Func.RGBtoHSV(app.pickGradEnd);
		float[] pickHSV = new float[3];
		pickHSV = Func.RGBtoHSV(app.pickGradStart);

		for (Node n :net.nNodes) {
			//	calculate hue based on network distance from selected node
			float max = app.getPickdepth();
			float grad = n.pickDistance/max;
			float hue = pickHSV[0]+grad*(nodeHSV[0]-pickHSV[0]);
			float[] result = new float[3];
			result = Func.HSVtoRGB(hue,nodeHSV[1],nodeHSV[2],0.8f);
			//	set the color of the selection frame			
			float alpha= Math.max(0f,Math.min(1,max-n.pickDistance+1)); 
			result[3]= alpha;
			n.setPickColor(result);

			//			set the alpha of the node color	based on selection		
			if (app.fadeNodes&&!n.rollover&&!n.isFrame()) n.setAlpha(Math.max(0.05f,alpha)); else n.setAlpha(app.nodeColor[3]);

			n.genColorFromAtt();
		}
	}

	private float calcClusterDistance(Node n) {
		if (n.cluster.size()==0) return n.size()/2f;

		float x;
		if (n.spiralcluster)
			x=spiral_rad(n, n.cluster.size());
		else
			x= circle_rad(n, n.cluster.size());

		return x;
	}

	private float spiral_angle(Node n, int i) {
		return (float)Math.sqrt(app.getClusterRad()*i+n.getSize())*75f;
	}
	private float spiral_rad(Node n, int i) {
		return app.getStandardNodeDistance()/2f+(float)Math.sqrt(app.getClusterRad()*i+n.getSize())*10f;
	}
	private float circle_rad(Node n, int i) {
		return app.getStandardNodeDistance()/2f+(float)Math.sqrt(app.getClusterRad()*n.cluster.size())*4f;
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
				gl.glTranslatef(-clusterDist, 0, 0);
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


	/**
	 * layout the leaf clusters
	 * @param gl
	 */
	public void clustersSetup(GL gl){
		float xRot = app.getApp().cam.getYRot();		//should be global camera orientation
		float yRot = app.getApp().cam.getXRot();


		for (Node aref:net.fNodes) {
			float rad = aref.cluster.size();

			//	if (fact>app.clusterRad*10f) 
			if (rad>30)
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
		if (app.layout2d) o*=0.5f;
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
		if (app.layout2d) offset*=0.5f;
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
		app.getApp().setYRotNew(0f);
		app.getApp().setXRotNew(0f);
		for (Node nref:net.fNodes) {
			nref.pos.z=0f;

		}
	}
	public void layoutInflate(float st_, Net net) {
		if (net==null) return;
		float strength = st_;
		BBox3D bounds = BBox3D.calcBounds(net.nNodes);
		layoutCenterOnPivot();
		if (net.fNodes.size()==1) return;
		for (Node nodeRef: net.fNodes) {
			if (!nodeRef.isLocked()) {
				Vector3D trans= new Vector3D();
				Vector3D corr= new Vector3D();
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
				if (radial) layoutConstrainCircle(m, 0, 0, innerRad+level*app.getRadialDist());
				else m.pos.y = level *app.getRadialDist();
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

	public void layoutLockPlace(Net net2) {
		for (Node n:net.fNodes) {
			if (n.isLocked()) n.pos = n.lockedPos.copy();
		}
	}
	public void layoutLockRemove(Node picked, Net net2) {
		picked.setLocked(false);
	}
	public void layoutLocksAll() {
		for (Node n:net.fNodes) {
			n.lockedPos= n.pos.copy();
			n.setLocked(true);
		}
	}
	public void layoutLocksRemove() {
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
	public void layoutRandomize(){
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
			if (tmp.getColor()[3]>0.2)net.repNodes.add(tmp);
		}

		for (Node a: net.fNodes) {
			for (Node b: net.fNodes) {
				if ((a!=b)&&(a.getColor()[3]>0.2&&b.getColor()[3]>0.2)) {
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

	/**
	 * render the view
	 * @param gl
	 * @param fonttype
	 * @param view
	 * @param nr
	 */
	void render(GL gl, int fonttype, Net view, GraphRenderer nr){

		Layouter layout = this;
		if (app.layout2d){
			if (!app.isTree()) layout.renderClusters(gl, nr);
			if (app.isGroups()) {layout.renderGroups(gl,nr, view,fonttype);
			layout.renderGroupLabels(gl, nr, view,fonttype);}
			if (app.isEdges()) layout.renderEdges(gl, nr, fonttype);
			layout.renderNodes(gl, nr, fonttype);
			//nr.renderBounds(gl, view.nNodes);
			layout.renderLabels(gl,nr, fonttype);
			
		}
		else {
			if (!app.isTree()) layout.renderClusters(gl, nr);
			if (app.isGroups()) {layout.renderGroups(gl,nr, view,fonttype);
			layout.renderGroupLabels(gl, nr, view,fonttype);}
			if (app.isEdges()) layout.renderEdges3D(gl, nr, fonttype);
			layout.renderNodes3D(gl,nr, fonttype);
		}
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
			if (rad>4&&app.drawClusters) {
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
	 * render edges contained in net in 3d
	 * @param gl
	 * @param nr
	 * @param text
	 */
	public  void renderEdges3D(GL gl, GraphRenderer nr, int text) {
		for (Edge eref: net.nEdges) {
			nr.renderEdges(gl, eref);
			nr.renderEdgeLabels(gl, eref, text, false);
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
		for (Edge eref: net.nEdges) nr.renderEdgeLabels(gl, eref, text, fast);
		for (Node nref: net.nNodes)	nr.renderNodeLabels(gl, nref, text, fast);
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
	/**
	 * render the nodes in net
	 * @param gl
	 * @param nr
	 * @param text
	 */
	public  void renderNodes3D(GL gl, GraphRenderer nr,  int text) {
		applyPickColors();
		for (Node n: net.nNodes) {
			nr.renderNode(gl, n);
			nr.renderNodeLabels(gl, n, text, false);
		}
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

		if (m.hasAttribute("color")) m.setColor(Func.parseColorInt(m.getAttribute("color"),16));
		else {
			float[] color = Func.colorGrad(level, nodeHSV, pickHSV);
			m.setColor(color);
		}
	}

	public void layoutEgocentric() {
		net.clearClusters();
		float offset = 0;

		HashSet<Node> nodes = net.distances.getNodesAtDistance(0);
		if (nodes!=null&&nodes.size()>1) offset = 0.15f;
		for (Node n:net.nNodes) {
			if (net.distances.contains(n)) {
				layoutConstrainCircle(n, 0, 0, (net.distances.getNodeDistance(n)+offset)*(app.getRadialDist()));
			}
		}
	}
	
	public void initTimeline() {
		
	}

	public void layoutTimeline() {
		net.clearClusters();
		Collection<Float> time = net.timeTable.values();
		if (time.size()==0) return;
		TreeSet<Float> b = new TreeSet<Float>();
		b.addAll(time);
		Float midpoint = (b.last()-b.first())/2f+b.first();
		HashMap<Float,Integer> counter = new HashMap<Float,Integer>();
		
		for (Node n:net.nNodes) {
			Float t = n.getTime();
			if (t!=null) {
				n.pos.setX( (t-midpoint)*100f);
				if (counter.containsKey(t)) {
					Integer numOccurancesAtT = counter.get(t);
					n.pos.y = numOccurancesAtT*app.getNodeSize()*5f;
					numOccurancesAtT++;
					counter.put(t, numOccurancesAtT);
				} else {
					counter.put(t, 1);
					n.pos.y=0f;
				}
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
		float abstand = app.getBoxdist();
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
		HashSet<Node> set = net.distances.getNodesAtDistance(0);

		for (Node start:set){
			//		Node start = net.distances.getNodesAtDistance(0).iterator().next();
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
}
