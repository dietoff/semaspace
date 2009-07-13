package data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import semaGL.FileIO;
import semaGL.NetLoader;
import semaGL.SemaParameters;
import semaGL.SemaSpace;

public class NetStack {
	HashMap<String,HashSet<Edge>> nets;
	SemaParameters app;
	public Net global;
	public Net view;
	private NetLoader loader;

	public NetStack(SemaParameters app_) {
		nets = new HashMap<String, HashSet<Edge>>();
		nets.put("none", new HashSet<Edge>());
		app = app_;
		loader = new NetLoader(app);
		global = new Net(app);
		setView(new Net(app));
	}

	/**
	 * delete the net stack
	 */
	public void clear() {
		nets.clear();
		nets.put("none", new HashSet<Edge>());
		global.clearNet();
		setView(new Net(app));
	}

	/**
	 * merge network to the global network and add to the stack
	 * @param n
	 */
	public void add(Net n){
		HashSet<Edge> e = new HashSet<Edge>();
		e.addAll(n.nEdges);
		nets.put("subnet"+nets.size(),e);
		global.netMerge(n);
	}

	/**
	 * remove a network from the stack
	 * @param n
	 */
	public void remove(Net n){
		if (nets.containsKey(n)) {
			nets.remove(n);
			global.netSubstract(n);
		}
	}

	/**
	 * get global, merged network
	 * @return
	 */
	public Net getGlobal(){
		return global;
	}

	public Set<String> getStack() {
		return nets.keySet();
	}


	/**
	 * parse network from provided unparsed string
	 * @param unparsed
	 * @param name
	 * @param tab
	 * @return
	 */
	public boolean edgeListParse(String unparsed, String name, boolean tab) {
			Net e = new Net(app);
			if (tab) e= loader.edgelistLoadTab(unparsed, global); else e = loader.edgelistLoad(unparsed, global);
			if (e!=null&&e.nEdges.size()>0) {
				nets.put(name, e.nEdges);
				global.netMerge(e);
				return true;
			}
		return false;
	}

	/**
	 * parse node attributes from provided unparsed string
	 * @param unparsed
	 * @param tab
	 */
	public void nodeListParse(String unparsed, boolean tab) {
		if (tab) loader.nodelistLoadTab(unparsed, global); else loader.nodelistLoad(unparsed, global);
	}
	
	/**
	 * add a subnet to the netstack
	 * @param e
	 */
	public void addSubnet(HashSet<Edge> e) {
		if (e!=null&&e.size()>0) {
			nets.put("subnet"+nets.size(),e);
		}
	}

	/**
	 * remove subnet from the network stack
	 * @param net
	 */
	public void removeSubnet(String net) {
		nets.remove(net);
	}

	/**
	 * find the context of a node & generate a view
	 * @param n
	 * @param searchdepth
	 * @param add
	 * @return
	 */
	public Net search(Node n, int searchdepth, boolean add) {
		if (n==null) {
			setView(global);
		}
		else {
			Net s;
			s = global.generateSearchNet(global, n, searchdepth);
			if (add) getView().netMerge(s); else setView(s);
		}
		return getView();
	}

	/**
	 * search a regex string & generate a view
	 * @param n
	 * @param searchdepth
	 * @param add
	 * @return
	 */
	public Net search(String n, int searchdepth, boolean add) {
		Net s;
		s = global.generateSearchNet(global, n, searchdepth);
		if (add) getView().netMerge(s); else setView(s);
		return getView();
	}

	/**
	 * search a substring & generate a new view
	 * @param text
	 * @param searchdepth
	 * @param add
	 * @param attribute
	 * @return
	 */
	public Net search(String text, int searchdepth, boolean add, String attribute) {
		Net s;
		s = global.generateAttribSearchNet(global, text, searchdepth, attribute);
		if (add) getView().netMerge(s); else setView(s);
		return getView();
	}

	public HashSet<Edge> getSubnet(String out) {
		return nets.get(out);
	}

	public void setView(Net view) {
		this.view = view;
	}

	public Net getView() {
		return view;
	}

	/**
	 * get view from the provided network name
	 * @param net
	 */
	public void setView(String net) {
		view.clearNet();
		HashSet<Edge> edges = nets.get(net);
		for (Edge e:edges) view.addEdge(e);
		view.updateNet();
	}

	/**
	 * export the view in graphML format
	 * @param filename
	 */
	public void exportGraphML (String filename){
		loader.saveGraphML(filename, view);
	}
	
	/**
	 * export the view in GML format
	 * @param filename
	 */
	public void exportGML (String filename){
		loader.saveGML(filename, view);
	}
	
	/**
	 * export the view in SemaSpace formats
	 * @param filename
	 * @param tab
	 */
	public void exportNet(String filename, boolean tab, boolean _view) {
		Net export;
		if (_view) export = view; else export = global;
		if (!tab){
			loader.saveNet(filename, export);
			loader.saveNodeData(filename+".n", export); 
		} else {
			loader.saveNetTab(filename, export);
			loader.saveNodeDataTab(filename+".n", export); 
		}

	}
}
