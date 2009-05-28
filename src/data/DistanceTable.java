package data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class DistanceTable {
	private HashMap<Node, Integer> distTable;
	private HashMap<Integer,HashSet<Node>> distMap;
	private Net net;
	private int maxdepth;

	public DistanceTable(Net net_){
		net = net_;
		distTable = new HashMap<Node, Integer>();
		distMap = new HashMap<Integer, HashSet<Node>>();
	}

	/*
	 * find distances, depth first
	 */
	public void findSearchDistances(HashSet<Node> set, int maxdepth_) {
		net.updateNet(); // to make sure adjacency lists are consistent
		maxdepth = maxdepth_;
		int depth = 0;
		clear();
		for (Node a:set) {
			// depth 1
			addDistances(a,depth); 
			iterSearchDepth(a, depth);
		}
	}

	void iterSearchDepth(Node a, int depth_) {
		int depth = depth_;
		depth++;
		if (depth>maxdepth) return;
		else{
			HashSet<Node> list = new HashSet<Node>();
			list.addAll(a.adList);
			list.addAll(a.inList);
			for (Node n:list){
				iterSearchDepth(n,depth);
				if (getNodeDistance(n)>depth) {
					addDistances(n, depth);
				}
			}
		}
	}

	void addDistances(Node tmp, int i) {
		if (distTable.containsKey(tmp))	removeDistances(tmp);

		distTable.put(tmp, i);
		if (distMap.get(i)!=null) distMap.get(i).add(tmp);
		else {
			HashSet<Node> n = new HashSet<Node>();
			n.add(tmp);
			distMap.put(i, n);
		}
	}

	void removeDistances(Node tmp){
		if (distTable.containsKey(tmp)) {
			int dist = distTable.get(tmp);
			distMap.get(dist).remove(tmp);
			distTable.remove(tmp);
		}
	}

	void incDistances() {
		HashMap<Node, Integer> s = (HashMap<Node, Integer>) distTable.clone();
		clear();
		for (Node n:s.keySet()) {
			int v = s.get(n);
			addDistances(n, v+1);
		}
	}

	public void clear(){
		distTable.clear();
		distMap.clear();
	}
	public int getNodeDistance(Node n){
		if (distTable.get(n)==null)
			return Integer.MAX_VALUE;
		else return distTable.get(n);
	}
	public HashSet<Node> getNodesAtDistance (int i) {
		return distMap.get(i);
	}

	public boolean contains(Node tmp) {
		return distTable.containsKey(tmp);
	}

	public Collection<Integer> distvalues() {
		return distTable.values();
	}

	public Collection<HashSet<Node>> nodeSets() {
		return distMap.values();
	}

	public int getMaxDist() {
		int max = 0;
		for (int x:distvalues()) if (x>max) max = x;
		return max;
	}
	/*
	 * find distances, depth first
	 */
	public void findPickDistances(int ID, int maxdepth_, boolean shift) {
		maxdepth = maxdepth_;
		int depth = 0;
		Node a;
		a=net.getNodeByID(ID);
		if (!shift) clearPick(); //set depth counters to MAXVALUE

		if (a!=null){
			// depth 1
			a.pickDistance=depth; //node a depth = 0
			iterDepth(a, depth);
		} 
		else {
			Edge e = net.getEdgeByID(ID);
			if (e!=null) {
				e.setPicked(true);
				e.getA().pickDistance=0;
				iterDepth(e.getA(), depth);
				e.getB().pickDistance=0;
				iterDepth(e.getB(), depth);
			}
		}
	}

	/*
	 * find distances, depth first
	 */
	public void findPickDistancesMultiple( int maxdepth_) {
		maxdepth = maxdepth_;
		int depth = 0;
		HashSet<Node> result = new HashSet<Node>();

		for (Node n:net.nNodes) {
			if (n.isPicked()) result.add(n);
		} 
		for (Edge e:net.nEdges) {
			if (e.isPicked()) {
				result.add(e.getA());
				result.add(e.getB());
			}
		}
		clearPick(); //set depth counters to MAXVALUE

		for (Node a:result) {
			a.pickDistance=depth; //node a depth = 0
			iterDepth(a, depth);
		}
	}

	void iterDepth(Node a, int depth_) {
		int depth = depth_;
		depth++;
		if (depth>maxdepth) return;
		else{

			HashSet<Node> list = new HashSet<Node>();
			list.addAll(a.adList);
			list.addAll(a.inList);

			for (Node n:list){
				iterDepth(n,depth);
				if (n.pickDistance>depth) {
					n.pickDistance=depth;
				}
			}
		}
	}
	
	
	public void clearPick() {
		for (Node nodeN:net.nNodes)	{
			nodeN.pickDistance=Integer.MAX_VALUE;
		}
		//		for (Edge e:edgeArray)	{
		for (Edge e:net.nEdges)	{
			e.setPicked(false);
		}
	}
}
