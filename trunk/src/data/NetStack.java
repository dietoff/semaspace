package data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import semaGL.FileIO;
import semaGL.NetLoader;
import semaGL.SemaSpace;

public class NetStack {
	HashMap<String,HashSet<Edge>> nets;
	SemaSpace app;
	public Net global;
	public Net view;
	private NetLoader loader;

	public NetStack(SemaSpace app_) {
		nets = new HashMap<String, HashSet<Edge>>();
		nets.put("none", new HashSet<Edge>());
		app = app_;
		loader = new NetLoader(app);
		global = new Net(app);
		setView(new Net(app));
	}

	public void clear() {
		nets.clear();
		nets.put("none", new HashSet<Edge>());
		global.clearNet();
		setView(new Net(app));
	}

	public void add(Net n){
		HashSet<Edge> e = new HashSet<Edge>();
		e.addAll(n.nEdges);
		nets.put("subnet"+nets.size(),e);
		global.netMerge(n);
	}

	public void remove(Net n){
		if (nets.containsKey(n)) {
			nets.remove(n);
			global.netSubstract(n);
		}
	}

	public Net getGlobal(){
		return global;
	}

	public Set<String> getStack() {
		return nets.keySet();
	}

	public boolean edgeListLoad(File file, boolean tab) {
		Net e = new Net(app);
		if (tab) e= loader.edgelistLoadTab(file, global); else e = loader.edgelistLoad(file, global);
		if (e!=null&&e.nEdges.size()>0) {
			nets.put(file.getName(), e.nEdges);
			global.netMerge(e);
			return true;
		}else return false;
	}

	public boolean edgeListLoad(String jarRead, String name, boolean tab) {
			Net e = new Net(app);
			if (tab) e= loader.edgelistLoadTab(jarRead, global); else e = loader.edgelistLoad(jarRead, global);
			if (e!=null&&e.nEdges.size()>0) {
				nets.put(name, e.nEdges);
				global.netMerge(e);
				return true;
			}
		return false;
	}

	public void nodeListLoad(File file2, boolean tab) {
		if (tab) loader.nodelistLoad2(file2, global); else loader.nodelistLoad(file2, global);
	}
	
	public void nodeListLoad(String file2, boolean tab) {
		if (tab) loader.nodelistLoad2(file2, global); else loader.nodelistLoad(file2, global);
	}
	public void addSubnet(HashSet<Edge> e) {
		if (e!=null&&e.size()>0) {
			nets.put("subnet"+nets.size(),e);
		}
	}

	public void removeSubnet(String net) {
		nets.remove(net);
	}

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

	public Net search(String n, int searchdepth, boolean add) {
		Net s;
		s = global.generateSearchNet(global, n, searchdepth);
		if (add) getView().netMerge(s); else setView(s);
		return getView();
	}

	public Net search(String n, int searchdepth, boolean add, String attribute) {
		Net s;
		s = global.generateAttribSearchNet(global, n, searchdepth, attribute);
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

	public void setView(String net) {
		view.clearNet();
		HashSet<Edge> edges = nets.get(net);
		for (Edge e:edges) view.addEdge(e);
		view.updateNet();
	}

	public void exportGraphML (String filename){
		loader.saveGraphML(filename, view);
	}
	public void exportGML (String filename){
		loader.saveGML(filename, view);
	}
	public void exportNet(String filename, boolean tab) {
		if (!tab){
			loader.saveNet(filename, view);
			loader.saveNodeData(filename+".n", view); 
		} else {
			loader.saveNetTab(filename, view);
			loader.saveNodeDataTab(filename+".n", view); 
		}

	}
}
