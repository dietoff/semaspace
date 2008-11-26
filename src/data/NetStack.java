package data;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import semaGL.SemaSpace;

public class NetStack {
	HashMap<String,HashSet<Edge>> nets;
	SemaSpace app;
	public Net global;
	public Net view;

	public NetStack(SemaSpace app_) {
		nets = new HashMap<String, HashSet<Edge>>();
		nets.put("none", new HashSet<Edge>());
		app = app_;
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
		HashSet<Edge> e = new HashSet<Edge>();
		if (tab) e= global.edgelistLoad2(file); else e = global.edgelistLoad(file);
		if (e!=null&&e.size()>0) {
			nets.put(file.getName(), e);
			return true;
		}else return false;
	}
	
	public void addSubnet(HashSet<Edge> e) {
		if (e!=null&&e.size()>0) {
			nets.put("subnet"+nets.size(),e);
		}
	}
	
	public void removeSubnet(String net) {
		nets.remove(net);
	}
	
	public void nodeListLoad(File file2, boolean tab) {
		if (tab) global.nodelistLoad2(file2); else global.nodelistLoad(file2);
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
}
