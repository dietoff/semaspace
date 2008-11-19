package data;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.naming.TimeLimitExceededException;

import semaGL.FileIO;
import semaGL.Func;
import semaGL.SemaSpace;
import semaGL.WildcardToRegex;

/**
 * @author d
 *
 */
/**
 * @author d
 *
 */
/**
 * @author d
 *
 */
/**
 * @author d
 *
 */
public class Net {
	public SemaSpace app;
	public HashSet<Node> nNodes;
	public HashSet<Edge> nEdges;
	public HashSet<Node> fNodes;
	public HashSet<Node> repNodes;
	public HashMap<String,Edge> eTable;
	private HashMap<Integer, Edge> eTableID;
	HashMap<String, Node> nTable; 							//hashtable for name lookup
	private HashMap<Integer, Node> nTableID; 				//hashtable for id lookup
	public HashMap<Node, Vector3D> posTable;						//hashtable for locked positions
	public HashMap<Node, Float> timeTable;
	//	BBox3D bounds;
	int depth= 1;
	private int maxdepth;
	public boolean opt=false;
	private HashSet<String> nTriangles;
	public HashSet<String> nodeattributes;
	public HashSet<String> edgeattributes;
	boolean directed;
	public HashMap<Node, Net> tags;
	private String lineBreak;
	private String separator;
	private String value;
	public DistanceTable distances;

	public Net(SemaSpace app_) {
		app = app_;
		nTable = new HashMap<String, Node>(); 
		posTable = new HashMap<Node, Vector3D>();

		distances = new DistanceTable(this);
		timeTable = new HashMap<Node, Float>();
		nTableID = new HashMap<Integer, Node>(); 
		eTable = new HashMap<String,Edge>();
		eTableID = new HashMap<Integer, Edge>(); 
		tags = new HashMap<Node, Net>();
		nNodes = new HashSet<Node>();
		nEdges = new HashSet<Edge>();
		fNodes = new HashSet<Node>();
		repNodes = new HashSet<Node>();
		nTriangles = new HashSet<String>();
		//		bounds = new BBox3D();
		nodeattributes = new HashSet<String>();
		edgeattributes = new HashSet<String>();
	}

	/**
	 * add an existing edge object to the network
	 * @param edge
	 */
	public void addEdge(Edge e) {
		nEdges.add(e);
		eTable.put(e.name,e);
		eTableID.put(e.getId(), e);
		addNode(e.getA());
		addNode(e.getB());
	}

	/**
	 * add an edge to the network using existing nodes
	 * @param node1
	 * @param node2
	 */
	public Edge addEdge(Node aref, Node bref){
		return addEdge(aref, bref, "");
	}

	/**
	 * add an edge to the network using existing nodes with id (for example a timestamp)
	 * @param n1 (Node)
	 * @param n2 (Node)
	 * @param timestamp (String)
	 */
	public Edge addEdge(Node aref, Node bref, String id){
		Edge newEdge;
		String name = Edge.edgeName(aref,bref,id);
		boolean dir = app.directed;
		if (eTable.containsKey(name)) { //check if edge exists
			return eTable.get(name);
		} else  //add a new edge.
		{
			newEdge = new Edge(app, aref, bref, id);
			addEdge(newEdge);

			aref.adList.add(bref);
			if (!dir) bref.adList.add(aref); else bref.inList.add(aref);

			return eTable.get(name);
		}
	}

	/**
	 * Adds an edge based on two node names. If nodes with the same name exist, the edge will use them, if one node exists, new node will be created in proximity to the existing node
	 * @param name1
	 * @param name2
	 * @return
	 */
	public Edge addEdge(String name1, String name2) {
		Node node1;
		Node node2;
		Edge newEdge=null;
		Node tmpNode;
		if (name1.contentEquals(name2)) return null;
		if (name1==null||name1.contentEquals("")) {
			addNode(name2, new Vector3D(0.5f,-.5f,0));
			return null;
		}
		if (name2==null||name2.contentEquals("")) {
			addNode(name1, new Vector3D(0.5f,-.5f,0));
			return null;
		}
		if (nTable.containsKey(name1)&&!nTable.containsKey(name2)){
			nTable.put(name2,tmpNode=addNode(name2, nTable.get(name1).pos.copy().mult(1.5f)));
			nTableID.put(tmpNode.getId(), tmpNode);
		}
		if (nTable.containsKey(name2)&&!nTable.containsKey(name1)){
			nTable.put(name1,tmpNode=addNode(name1, nTable.get(name2).pos.copy().mult(1.5f)));
			nTableID.put(tmpNode.getId(), tmpNode);
		}
		if (!nTable.containsKey(name1)&&!nTable.containsKey(name2)){
			float n=nTable.size()+1;
			nTable.put(name2,tmpNode=addNode(name2, Func.rnd(-n,n),Func.rnd(-n,n),Func.rnd(-n,n)));
			nTableID.put(tmpNode.getId(), tmpNode);
			nTable.put(name1,tmpNode=addNode(name1, Func.rnd(-n,n),Func.rnd(-n,n),Func.rnd(-n,n)));
			nTableID.put(tmpNode.getId(), tmpNode);
		}
		node1 = nTable.get(name1);
		node2 = nTable.get(name2);
		newEdge = addEdge(node1,node2);
		return newEdge;
	}

	/**
	 * Adds a new edge, creates the nodes of the specified names do not exist. 
	 * @param name1
	 * @param name2
	 * @return
	 */
	public Edge addEdgeSimple(String name1, String name2) {
		Node node1;
		Node node2;
		Edge newEdge=null;
		node1=	addNode(name1);
		node2=	addNode(name2);
		newEdge = addEdge(node1,node2);
		return newEdge;
	}
	/**
	 * Adds an existing node to the network
	 * @param node
	 * @return
	 */
	public Node addNode(Node node){
		if (!nTable.containsKey(node.name)){
			nNodes.add(node);
			fNodes.add(node);
			nTable.put(node.name,node);
			nTableID.put(node.getId(),node);
		};
		return node;
	}
	/**
	 * Add an edge to the network and create nodes that dont exist yet
	 * @param name1
	 * @param name2
	 */
	public Node addNode(String name){
		return addNode(name, new Vector3D(0,0,0));
	}
	/**
	 * Create a new node at a specific position
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Node addNode(String name,float x,float y,float z) {
		Vector3D pos = new Vector3D(x,y,z);
		return addNode(name,pos);
	}
	/**
	 * Create a new node at a specific position
	 * @param name1
	 * @param vector3D
	 * @return
	 */
	public Node addNode(String name, Vector3D pos) {
		Node newObj;
		if (!nTable.containsKey(name)) {
			newObj = new Node(app, name, pos); 
			newObj.setPickColor(app.frameColor);
			nNodes.add(newObj);
			fNodes.add(newObj);
			nTable.put(name, newObj);
			nTableID.put(newObj.getId(), newObj);
		} else newObj =nTable.get(name); 
		return newObj;
	}

	/**
	 * Add a new node in proximity to an existing one
	 * @param name
	 * @param ref
	 * @return
	 */
	public Node addNode(String name, Node ref){
		Node tmpNode;
		if (!nTable.containsKey(name)){
			float rnd = ((float)Math.random()-0.5f)*0.3f;
			float rnd2 = ((float)Math.random()-0.5f)*0.3f;
			float rnd3 = ((float)Math.random()-0.5f)*0.3f;
			nTable.put(name,tmpNode=addNode(name, ref.pos.x+rnd,ref.pos.y+rnd2,ref.pos.z+rnd3));
			nTableID.put(tmpNode.getId(), tmpNode);
		} else tmpNode=nTable.get(name);
		return tmpNode;
	}

	/**
	 * Delete a node and all connected edges
	 * @param n
	 * @return
	 */
	public Node removeNode(Node n) {
		removeConnectedEdges(n);
		nNodes.remove(n);
		fNodes.remove(n);
		nTable.remove(n.name);
		nTableID.remove(n.getId());
		return n;
	}

	/**
	 * Remove an edge
	 * @param n
	 * @return
	 */
	public Edge removeEdge(Edge n) {
		nEdges.remove(n);
		eTable.remove(n.name);
		eTableID.remove(n.getId());
		return n;
	}

	/**
	 * Remove all edges referencing a specific node
	 * @param n
	 */
	private void removeConnectedEdges(Node n) {
		for (Node m:n.adList) {
			nEdges.remove(eTable.remove(Edge.edgeName(n, m)));
			nEdges.remove(eTable.remove(Edge.edgeName(m, n)));
		}
		for (Node m:n.inList) {
			nEdges.remove(eTable.remove(Edge.edgeName(m, n)));
			nEdges.remove(eTable.remove(Edge.edgeName(n, m)));
		}
	}
	/**
	 * Define a triangle of three connected nodes (experimental)
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	private String addTriangle(Node i, Node j, Node k) {
		//		sort nodes ascending		
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(i.name);
		tmp.add(j.name);
		tmp.add(k.name);
		Collections.sort(tmp);
		String back = tmp.get(0)+"\t"+tmp.get(1)+"\t"+tmp.get(2);
		nTriangles.add(back);
		return back;
	}

	/**
	 * remove all clusters 
	 */
	public void clearClusters() {
		fNodes.addAll(nNodes);
		for (Node aref: nNodes) {
			aref.cluster.clear();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Net clone(){
		Net clone = new Net(app);
		clone.nEdges.addAll(nEdges);
		clone.eTable.putAll(eTable);
		clone.eTableID.putAll(eTableID);
		clone.nNodes.addAll(nNodes);
		clone.fNodes.addAll(fNodes);
		clone.nTable.putAll(nTable);
		clone.nTableID.putAll(nTableID);
		clone.timeTable.putAll(timeTable);
		clone.tags.putAll(tags);
//		clone.updateNet();
		return clone;
	}

	/**
	 * delete clusters 
	 */
	public void clustersDelete() {
		findClusters(); //recalculate clusters, in case they were turned off

		HashSet<Node> queueN = new HashSet<Node>();
		for (Node tmp1 : nNodes) {
			queueN.addAll(tmp1.cluster);
		}

		//remove queued nodes
		for (Node tmp:queueN){
			removeNode(tmp);
		}
		// recompute connectivity, clusters ... 
		updateNet();
	}
	
	/**
	 * delete leaf nodes (nodes with only one connection)
	 */
	public void leafDelete() {
		HashSet<Node> queueN = new HashSet<Node>();
		HashSet<Node> ad = new HashSet<Node>();
		for (Node tmp1 : nNodes) {
			ad.clear();
			ad.addAll(tmp1.adList);
			ad.addAll(tmp1.inList);
			if (ad.size()<2) queueN.add(tmp1);
		}

		//remove queued nodes
		for (Node tmp:queueN){
			removeNode(tmp);
		}
		// recompute connectivity, clusters ... 
		updateNet();
	}
	
	/**
	 * load an edge list (format 1)
	 *
	 * @param file_
	 */
	public HashSet<Edge> edgelistLoad(File file_) {
		HashSet<Edge> edges = null;
		boolean a = app.isRender();
		boolean b = app.calculate;
		app.setRender(false);
		app.calculate=true;
		String file=FileIO.loadFile(file_);
		if (file!=null&&file.length()>0) {
			edges = edgelistParse(file); 
			updateNet();
		}
		app.setRender(a);
		app.calculate=b;
		return edges;
	}
	/**
	 * load an edge list (format 2)
	 * @param file_
	 * @return 
	 */
	public HashSet<Edge> edgelistLoad2(File file_) {
		HashSet<Edge> edges = null;
		boolean a = app.isRender();
		boolean b = app.calculate;
		app.setRender(false);
		app.calculate=true;
		String file=FileIO.loadFile(file_);
		if (file!=null&&file.length()>0)  edges = edgelistParse2(file); 
		updateNet();
		app.setRender(a);
		app.calculate=b;
		return edges;
	}
	/**
	 * parse an edge list (format 1)
	 * @param content
	 */
	public HashSet<Edge> edgelistParse(String content) {
		lineBreak = "\r\n|\n+|\r+|\f+";
		separator = "\t+";
		value = "=";
		Edge tmp = null;
		HashSet<Edge> edges = new HashSet<Edge>();
		String lines[]= content.split(lineBreak);
		for (int i=0; i<lines.length; i++){
			String line = lines[i].replaceAll(lineBreak, "").replaceAll("\t+", "\t");
			String cols[] = lines[i].split(separator);

			if (cols!=null&&cols.length>1) 
			{
				tmp = addEdge (cols[0].trim(),cols[1].trim());

				if (tmp!=null){
					// database id as first attribute
					tmp.setAttribute("id", tmp.name);
					edgeattributes.add("id");

					// db id as first attribute
					tmp.getA().setAttribute("id", cols[0].trim());
					nodeattributes.add("id");
					tmp.getB().setAttribute("id", cols[1].trim());
					nodeattributes.add("id");

					edges.add(tmp);
				}
			}

			if (cols.length>2) {
				for (int j=2; j<cols.length; j++){
					String val[]=cols[j].split(value);	
					if (val.length>1) tmp.setAttribute(val[0].toLowerCase().trim(), val[1].trim());
					edgeattributes.add(val[0].toLowerCase().trim());
					setedgeAttributes(tmp);
				}
			}
		}
		return edges;
	}

	/**
	 * parse an edge list (format 2)
	 * the fist line specifies the field names, the rest is values
	 * @param content
	 */
	HashSet<Edge> edgelistParse2(String content) {
		HashSet<Edge> edges = new HashSet<Edge>();
		Edge tmp = null;
		String lines[]= content.split(lineBreak);
		String fields[] = null; 
		for (int i=0; i<lines.length; i++){
			String line = lines[i].replaceAll(lineBreak, "");
			String cols[] = line.split(separator);

			if (cols!=null&&cols.length>1) 
			{			
				if (i==0){
					fields=cols.clone();
					for (String s:fields) edgeattributes.add(s.toLowerCase());
				} else {

					tmp = addEdge (cols[0],cols[1]);
					// database id as first attribute
					tmp.setAttribute("id", tmp.name);
					edgeattributes.add("id");

					// db id as first attribute
					tmp.getA().setAttribute("id", cols[0]);
					tmp.getB().setAttribute("id", cols[1]);

					edges.add(tmp);

					if (cols.length>2) {
						for (int j=2; j<cols.length; j++){
							if (cols[j].length()>0) tmp.setAttribute(fields[j].toLowerCase(), cols[j]);
							setedgeAttributes(tmp);
						}
					}
				}
			}
		}
		return edges;
	}

	/**
	 * parse some predefined edge attributes
	 * @param tmp
	 */
	private void setedgeAttributes(Edge tmp) {
		if (tmp.hasAttribute("function")) tmp.setAltName(tmp.getAttribute("function"));
		if (tmp.hasAttribute("similarity"))
			try {
				tmp.setProperty(10f*Float.parseFloat(tmp.getAttribute("similarity")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
	}

	/**
	 * method for identifying clusters of single leaf nodes attached to a central node
	 */
	public void findClusters() {
		HashSet<Node> comp = new HashSet<Node>();
		HashSet<Node> comp2 = new HashSet<Node>();
		fNodes.addAll(nNodes);
		for (Node n: nNodes) {
			n.cluster.clear();
			comp2.clear();
			comp2.addAll(n.adList);
			comp2.addAll(n.inList);

			for (Node bref:comp2) {
				comp.clear();
				comp.addAll(bref.adList);
				comp.addAll(bref.inList);
				if (comp.size()==1) {
					n.cluster.add(bref);
					posTable.remove(bref);
					bref.setLocked(false);
				}
			}

			if ((n.cluster.size()<2)||n.clusterCenter)
				n.cluster.clear();
			else{
				for (Node yy:n.cluster){
					fNodes.remove(yy);
				}
			}
		}
	}

	public void  findTagged(){
		//		for (Node n:nNodes) {
		//			
		//			//create tag net
		//			if (n.clusterCenter) {
		//				Net tagnet = new Net(app);
		//				for (Node m:n.adList) tagnet.addNode(m);
		//				for (Node m:n.inList) tagnet.addNode(m);
		//				Node last = null;
		//				Node first = null;
		//				for (Node m:tagnet.nNodes) {
		//					if (last!=null) tagnet.addEdge(last, m); else first = m;
		//					last = m;
		//				}
		//				// add edge ring for connecting the boundary
		//				tagnet.addEdge(last, first);
		//				fNodes.remove(n);
		//				// register them for later use
		//				tags.put(n,tagnet);
		//			}
		//		}
	}


	/**
	 * Method for finding triangles - three nodes connected with edges
	 */
	public void findTriangles() {
		for (Node i: nNodes){
			if (i.adList.size()>1){
				for (Node j: i.adList){
					if (j.adList.size()>1){
						for (Node k: j.adList){
							if (k.adList.contains(i)){
								addTriangle(i,j,k);	
								if (app.directed){
									eTable.get(Edge.edgeName(i,j)).setTriangle(true);
									eTable.get(Edge.edgeName(j,k)).setTriangle(true);
									eTable.get(Edge.edgeName(k,i)).setTriangle(true);
								} else {
									try {
										eTable.get(Edge.edgeName(i,j)).setTriangle(true);
										eTable.get(Edge.edgeName(j,k)).setTriangle(true);
										eTable.get(Edge.edgeName(k,i)).setTriangle(true);
										eTable.get(Edge.edgeName(j,i)).setTriangle(true);
										eTable.get(Edge.edgeName(k,j)).setTriangle(true);
										eTable.get(Edge.edgeName(i,k)).setTriangle(true);
									} catch (NullPointerException e) {
									}
								}
							}
						}
					}
				}
			}
		}
		for (String tmp:nTriangles) {
			System.out.println(tmp);
		}
	}

	/**
	 * Generate a random network
	 * @param n_ number of nodes
	 * @param e_ number of edges
	 */
	public void generateRandomNet(int n_, int e_) {
		int num_n = n_;
		int num_e = e_;
		num_e = Math.min(num_n*(num_n-1),num_e);
		// create random nodes
		nTableID.clear();
		nTable.clear();
		Node addN;
		for (int i = 0; i < num_n; i++) {
			String name = "N"+i;

			addN = new Node(app, name, Func.rnd(-nNodes.size(),nNodes.size()), Func.rnd(-nNodes.size(),nNodes.size()), Func.rnd(-nNodes.size(),nNodes.size()));
			addNode(addN);
			addN.setPickColor(app.frameColor);
		}

		// create random edges
		int a;
		int b;
		for (int i = 0; i < num_e; i++) {
			do {
				a = (int) Func.rnd(0, nNodes.size());
				b = (int) Func.rnd(0, nNodes.size());
			} while (a==b);
			Node aref = (Node)nNodes.toArray()[a];
			Node bref = (Node)nNodes.toArray()[b];
			addEdge(aref, bref);
		}
		updateNet();
	}

	/**
	 * look for a subnet starting from a group of nodes up to a specified depth
	 * @param source
	 * @param searchNodes
	 * @param depth
	 * @return
	 */
	public Net generateSearchNet(Net source, HashSet<Node> searchNodes,  int depth) {
		source.updateAdLists(app.directed);
		Node nodeA;
		Node nodeB;
		Net resultNet = new Net(app);
		for (Node tmp:searchNodes){
			resultNet.addNode(tmp);
			resultNet.distances.addDistances(tmp, 0);
		}

		for (int i=0; i<depth; i++ ){
			for (Node tmpNode :searchNodes){
				for (Edge tmpEdge: source.nEdges){
					nodeA = tmpEdge.getA();
					nodeB = tmpEdge.getB();
					if (nodeA==tmpNode){
						resultNet.addEdge(tmpEdge);
					}
					if (nodeB==tmpNode){
						resultNet.addEdge(tmpEdge);
					}
				}
			}
			for (Node tmp:resultNet.nNodes) {
				if (!resultNet.distances.contains(tmp)) resultNet.distances.addDistances(tmp, i+1);
				if (tmp!=null) searchNodes.add(tmp);
			}
		}
//		resultNet.updateNet();
		return resultNet;
	}

	public Net generateSearchNet(Net source, String term, int depth) {
		Node searchNode = source.nTable.get(term);
		if (searchNode==null) return new Net(app);
		HashSet<Node> searchNodes = new HashSet<Node>();
		searchNodes.add(searchNode);
		return generateSearchNet(source,  searchNodes, depth);
	}

	public Net generateSearchNet(Net source, Node n, int depth) {
		Node searchNode = n;
		if (searchNode==null) return new Net(app);
		HashSet<Node> searchNodes = new HashSet<Node>();
		searchNodes.add(searchNode);
		return generateSearchNet(source,  searchNodes, depth);
	}

	public Net generateAttribSearchNet(Net source, String term, int depth, String key) {
		Net result = new Net(app);
		String term2 = WildcardToRegex.wildcardToRegex(term); 
		if (term==null||term=="") return null;
		String subString = term2.toLowerCase();

		HashSet<Node> searchNodes = new HashSet<Node>();

		for (Node n:nNodes){
			String att = n.getAttribute(key);
			if (key=="none") att=n.altName;
			if (att!=null) {
				//				att= n.name;
				att = att.toLowerCase();
				n.setFrame(false);
				if (att.matches(subString))searchNodes.add(n);
			}
		}
		HashSet<Node> tmp = new HashSet<Node>();
		tmp.addAll(searchNodes);
		result = generateSearchNet(source, searchNodes, depth);

		if (tmp.size()>1){
			result.distances.incDistances();
			Node s = result.addNode("results \""+term+"\"");
			result.distances.addDistances(s, 0);
			for (Node n:tmp){
				result.addEdge(s, n);
			}
		}

		for (Edge e:eTable.values()) {
			String att = e.getAttribute(key);
			if (att!=null) {
				att = att.toLowerCase();
				if (att.matches(subString)){
					result.addEdge(e);
				}
			}
		}
		return result;
	}

	public Edge getEdgeByID(int id){
		return eTableID.get(id);
	}

	public Node getNodeByID(int id){
		return nTableID.get(id);
	}
	public Vector3D getPosByID(int pickID_)  {
		Node selected = nTableID.get(pickID_);
		if (selected==null)  return new Vector3D(0f,0f,0f);
		else
			return selected.pos;
	}
	public GraphElement getByID(int overID) {
		Node n = getNodeByID(overID);
		if (n!=null) return n; 
		else return getEdgeByID(overID);
	}

	public void getStatus(){
		System.out.println("nEdges "+eTable.size());
		System.out.println("nNodes "+nNodes.size());
		System.out.println("fNodes "+fNodes.size());
		System.out.println("nTable "+nTable.size());
		System.out.println("nTableID "+nTableID.size());
	}


	public void interrupt() {
		System.out.println();

	}

	public void clearNet(){
		nEdges.clear();
		eTable.clear();
		eTableID.clear();
		nNodes.clear();
		fNodes.clear();
		nTable.clear();
		nTableID.clear();
		nodeattributes.clear();
		edgeattributes.clear();
		distances.clear();
		timeTable.clear();
		tags.clear();
		updateNet();
	}
	public void netMerge (Net net) {
		mergeRaw(net);
		updateNet();
	}

	void mergeRaw(Net net) {
		nNodes.addAll(net.nNodes);
		nEdges.addAll(net.nEdges);
		nTable.putAll(net.nTable);
		eTable.putAll(net.eTable);
		eTableID.putAll(net.eTableID);
		nTableID.putAll(net.nTableID);
		timeTable.putAll(net.timeTable);
		tags.putAll(net.tags);
		nodeattributes.addAll(net.nodeattributes);
		edgeattributes.addAll(net.edgeattributes);
		distances.clear();
	}

	public void netReplace(Net replace) {
		clearNet();
		netMerge(replace);
	}

	public void nodelistLoad(File file2) {
		String file = FileIO.loadFile(file2);
		if  (file!=null&&file.length()>0) {
			nodelistParse(file);
			updateNet();
		}
	}
	public void nodelistLoad2(File file_) {
		String file = FileIO.loadFile(file_);
		if  (file!=null&&file.length()>0)  nodelistParse2(file);
		updateNet();
	}
	public void nodelistParse(String file) {
		Node tmp;
		String lines[]= file.split(lineBreak);
		for (int i=0; i<lines.length; i++){
			String cols[] = lines[i].split(separator);
			tmp = addNode(cols[0].trim());
			//			tmp = nTable.get(cols[0].trim());
			if (tmp!=null){
				for (int j=1; j<cols.length;j++) {
					String val[]=cols[j].split(value);	
					if (val.length>1) {
						tmp.setAttribute(val[0].toLowerCase().trim(), val[1].trim());
						nodeattributes.add(val[0].toLowerCase().trim());
						parseAttributes(tmp);
					}
				}
			}
		}
	}


	//second format: the first line specifies the name of the attribute
	public void nodelistParse2(String file) {
		Node tmp;
		String lines[]= file.split(lineBreak);
		String fields[] = null;
		for (int i=0; i<lines.length; i++){
			String line = lines[i].replaceAll(lineBreak, "");
			String cols[] = line.split(separator);

			if (i==0) {
				fields = cols.clone();
				for (String s:fields) nodeattributes.add(s.toLowerCase());
			} else {
				tmp = nTable.get(cols[0]);
				if (tmp!=null){
					for (int j=1; j<cols.length;j++) {
						if (cols[j].length()>0) {
							tmp.setAttribute(fields[j].toLowerCase(), cols[j]);
							parseAttributes(tmp);
						}
					}
				}
			}
		}
	}

	private void parseAttributes(Node tmp) {
		if (tmp.hasAttribute("color")) tmp.setColor(Func.parseColorInt(tmp.getAttribute("color")));
		if (tmp.hasAttribute("color2")) tmp.setColor2(Func.parseColorInt(tmp.getAttribute("color2")));
		if (tmp.hasAttribute("project")) tmp.altName=tmp.getAttribute("project");
		if (tmp.hasAttribute("person")) tmp.altName=tmp.getAttribute("person");
		if (tmp.hasAttribute("tagged")) tmp.clusterCenter=true; else tmp.clusterCenter=false;
		if (tmp.hasAttribute("year")){
			try {
				tmp.setTime(Float.parseFloat(tmp.getAttribute("year")));
				timeTable.put(tmp, tmp.getTime());
			} catch (NumberFormatException e) {
			}
		}
	}

	public void saveEdgeData( String filename){
		String outString="";
		for (String nRef :eTable.keySet()){
			Edge tmp = eTable.get(nRef);
			if (tmp.ages!=null){
				for (int i = 0; i< tmp.ages.size(); i++) {
					outString+=nRef+"\t"+tmp.ages.get(i)+"\t"+tmp.text.get(i)+"\n";
				}
			}
		}
		FileIO.fileWrite(filename, outString); 
	}

	public void saveNet(String filename) {
		String outString="";

		for (Edge nRef :nEdges){
			outString+=nRef.getA().name+"\t"+nRef.getB().name+"\n";
		}
		FileIO.fileWrite(filename, outString); 
	}
	public void saveNodeData( String filename){
		String outString="";
		//		for (Node nRef :nNodes){
		//		outString+=nRef.name+"\t"+nRef.altName+"\t"+nRef.vote+"\t"+nRef.pageViews+"\t"+nRef.scraped()+"\n";
		//		}
		FileIO.fileWrite(filename, outString); 
	}

	public void updateAdLists(boolean d) {
		Node a=null;
		Node b=null;
		for (Node n : nNodes){
			n.adList.clear();
			n.inList.clear();
		}
		for (Edge e : nEdges){
			a=e.getA();
			b=e.getB();
			a.adList.add(b);
			if (!d){
				b.adList.add(a);
			}
			else{
				b.inList.add(a);
			}
		}
		if (app.isCluster()) findClusters(); else clearClusters();// recalculate the clusters, since they might have changed ... 
	}

	public void updateNet() {
		updateAdLists(app.directed); //update adj. lists etc.
		findTagged();
		timeTable.clear();
		for (Node n:nNodes) {
			if (n.getTime()!=null) timeTable.put(n, n.getTime());
		}
	}

	public boolean isEmpty() {
		return (nEdges.size()==0&&nNodes.size()==0);
	}

	public void netSubstract(Net net) {
		nNodes.removeAll(net.nNodes);
		nEdges.removeAll(net.nEdges);
		for ( String n:net.nTable.keySet()) nTable.remove(n);
		for ( String n:net.eTable.keySet()) eTable.remove(n);
		for ( Integer n:net.eTableID.keySet()) eTableID.remove(n);
		for ( Integer n:net.nTableID.keySet()) nTableID.remove(n);
		for ( Node n:net.timeTable.keySet()) timeTable.remove(n);
		for ( Node n:net.tags.keySet()) tags.remove(n);
		nodeattributes.removeAll(net.nodeattributes);
		edgeattributes.removeAll(net.edgeattributes);
		distances.clear();
		updateNet();
	}

}