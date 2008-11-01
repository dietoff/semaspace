package semaGL;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NetStack {
	HashMap<String,HashSet<Edge>> nets;
	SemaSpace app;
	Net global;
	Net view;

	NetStack(SemaSpace app_) {
		nets = new HashMap<String, HashSet<Edge>>();
		nets.put("none", new HashSet<Edge>());
		app = app_;
		global = new Net(app);
		view = new Net(app);
	}

	public void clear() {
		nets.clear();
		nets.put("none", new HashSet<Edge>());
		global.clearNet();
		view = new Net(app);
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

	public boolean edgeListLoad(File file) {
		HashSet<Edge> e = new HashSet<Edge>();
		e = global.edgelistLoad(file);
		if (e!=null&&e.size()>0) {
			nets.put(file.getName(), e);
			return true;
		}else return false;
	}
	
	public void addEdges(HashSet<Edge> e) {
		if (e!=null&&e.size()>0) {
			nets.put("subnet"+nets.size(),e);
		}
	}
	
	public void nodeListLoad(File file2) {
		global.nodelistLoad(file2);
	}

	public Net search(Node n, int searchdepth, boolean add) {
		if (n==null) {
			view = global;
		}
		else {
			Net s;
			s = global.generateSearchNet(global, n, searchdepth);
			if (add) view.netMerge(s); else view = s;
		}
		return view;
	}
	
	public Net search(String n, int searchdepth, boolean add) {
		Net s;
		s = global.generateSearchNet(global, n, searchdepth);
		if (add) view.netMerge(s); else view = s;
		return view;
	}
	
	public Net search(String n, int searchdepth, boolean add, String attribute) {
		Net s;
		s = global.generateAttribSearchNet(global, n, searchdepth, attribute);
		if (add) view.netMerge(s); else view = s;
		return view;
	}

	public HashSet<Edge> getSubnet(String out) {
		return nets.get(out);
	}
}