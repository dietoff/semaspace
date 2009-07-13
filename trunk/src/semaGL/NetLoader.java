package semaGL;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import data.Edge;
import data.GraphElement;
import data.Net;
import data.Node;
import data.Vector3D;

public class NetLoader {
	SemaParameters app;
	private String lineBreak = "\r\n|\n|\r";
	private String separator = "\t";
	private String value = "=";

	public NetLoader(SemaParameters app2){
		app = app2;
	}

	/**
	 * load an edge list (format 1)
	 *
	 * @param file_
	 * @param global 
	 */
	public Net edgelistLoad(File file_, Net global) {
		String file=FileIO.loadFile(file_);
		return edgelistLoad(file,global);
	}
	/**
	 * load an edge list (format 1)
	 *
	 * @param file_
	 * @param global 
	 */
	public Net edgelistLoad(String file, Net global) {
		Net edges = null;
		if (file!=null&&file.length()>0) {
			edges = edgelistParse(file, global); 
		}
		return edges;
	}
	/**
	 * load an edge list (format 2)
	 * @param file_
	 * @param global 
	 * @return 
	 */
	public Net edgelistLoadTab(File file_, Net global) {
		String file=FileIO.loadFile(file_);
		return edgelistLoadTab(file,global);
	}

	/**
	 * load an edge list (format 2) 
	 * @param file_
	 * @param global 
	 * @return 
	 */
	public Net edgelistLoadTab(String file, Net global) {
		Net edges = null;
		if (file!=null&&file.length()>0)  edges = edgelistParseTab(file, global); 
		return edges;
	}

	/**
	 * parse an edge list (format 1)
	 * @param content
	 * @param global 
	 */
	public Net edgelistParse(String content, Net global) {
		Edge tmp = null;
		Net r= new Net(app);
		String lines[]= content.trim().split(lineBreak);
		for (int i=0; i<lines.length; i++){
			String cols[] = lines[i].split(separator);

			if (cols!=null&&cols.length>1) 
			{
				String col1 = cols[0].trim();
				String col2 = cols[1].trim();
				tmp = r.addEdge (col1,col2);
				if (tmp!=null){
					// database id as first attribute
					tmp.setAttribute("id", tmp.name);
					r.edgeattributes.add("id");

					// db id as first attribute
					tmp.getA().setAttribute("id", col1);
					tmp.getB().setAttribute("id", col2);
					r.nodeattributes.add("id");
				} else 
					if (cols.length==1){
						r.addNode(cols[0].trim());
					}
			}

			if (cols.length>2) {
				for (int j=2; j<cols.length; j++){
					String val[]=cols[j].split(value);	
					String key = val[0].toLowerCase().trim();
					if (val.length>1) {
						String value = val[1].trim();
						addAttribute(r, tmp, value, key);
						parseEdgeAttributes(tmp);
					}
				}
			}
		}
		return r;
	}

	/**
	 * parse an edge list (format 2)
	 * the fist line specifies the field names, the rest is values
	 * @param content
	 * @param global 
	 */
	Net edgelistParseTab(String content, Net global) {
		Net r= new Net(app);
		Edge tmp = null;
		String lines[]= content.trim().split(lineBreak);
		String fields[] = null; 
		for (int i=0; i<lines.length; i++){
			String line = lines[i].replaceAll(lineBreak, "");
			String cols[] = line.split(separator);

			if (cols!=null&&cols.length>1) 
			{			
				if (i==0){
					fields=cols.clone();
					if (fields.length>2){
						for (int j=2; i<fields.length;i++) {
							String s = fields[j].trim();
							r.edgeattributes.add(s.toLowerCase());
						}
					}
				} else {

					String col1 = cols[0].trim();
					String col2 = cols[1].trim();
					tmp = r.addEdge (col1,col2);

					if (tmp!=null){
						// database id as first attribute
						tmp.setAttribute("id", tmp.name);
						r.edgeattributes.add("id");

						// db id as first attribute
						tmp.getA().setAttribute("id", col1);
						r.nodeattributes.add("id");
						tmp.getB().setAttribute("id", col2);

						if (cols.length>2) {
							for (int j=2; j<cols.length; j++){
								if (cols[j].length()>0) {
									String key = fields[j];
									String value = cols[j].trim();

									if (value!=null&&value.length()>0) {
										addAttribute(r, tmp, value, key);
										r.edgeattributes.add(key);
										parseEdgeAttributes(tmp);
									}
								}
							}
						}
					}
				}
			}
		}
		return r;
	}

	/**
	 * parse some predefined edge attributes
	 * @param tmp
	 */
	private void parseEdgeAttributes(Edge tmp) {
		if (tmp==null) return;
		if (tmp.hasAttribute("function")) tmp.setAltName(tmp.getAttribute("function"));
		if (tmp.hasAttribute("name")) tmp.setAltName(tmp.getAttribute("name"));
		if (tmp.hasAttribute("color")) {
			tmp.setAssignedColor(Func.parseColorInt(tmp.getAttribute("color"),16));
			tmp.removeAttribute("color");
		}
		if (tmp.hasAttribute("color2")) {
			tmp.setAssignedColor2(Func.parseColorInt(tmp.getAttribute("color2"),16));
			tmp.removeAttribute("color2");
		}
		if (tmp.hasAttribute("type")) tmp.setAltName(tmp.getAttribute("type"));
		if (tmp.hasAttribute("role")) tmp.setAltName(tmp.getAttribute("role"));
		if (tmp.hasAttribute("similarity"))
			try {
				tmp.setProperty(10f*Float.parseFloat(tmp.getAttribute("similarity")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
	}

	/**
	 * @param file2
	 * @param n
	 */
	public void nodelistLoad(File file2, Net n) {
		String file = FileIO.loadFile(file2);
		nodelistLoad(file, n);
	}
	/**
	 * @param file2
	 * @param n
	 */
	public void nodelistLoad(String file, Net n) {
		if  (file!=null&&file.length()>0) {
			nodelistParse(file, n);
		}
	}
	/**
	 * @param file_
	 * @param n
	 */
	public void nodelistLoadTab(File file_, Net n) {
		String file = FileIO.loadFile(file_);
		nodelistLoadTab(file, n);
	}
	/**
	 * @param file_
	 * @param n
	 */
	public void nodelistLoadTab(String file, Net n) {
		if  (file!=null&&file.length()>0)  nodelistParseTab(file, n);
	}


	public void nodelistParse(String file, Net n) {
		Node tmp= null;
		String lines[]= file.trim().split(lineBreak);
		for (int i=0; i<lines.length; i++){
			String cols[] = lines[i].split(separator);
			String col1 = "";
			if (cols[0].length()>0) col1 = cols[0].trim();
			if (col1.length()>0) tmp = n.addNode(col1);

			if (tmp!=null){
				for (int j=1; j<cols.length;j++) {
					String val[]=cols[j].split(value);	
					if (val.length>1) {
						String key = val[0].toLowerCase().trim();
						String value = val[1].trim();
						if (key.length()>0&&value.length()>0) {
							if (key.contentEquals("pos")) {
								setLockPos(tmp,cols[1].trim());
							} else {
								addAttribute(n, tmp, value, key);
							}
							parseNodeAttributes(tmp, n);
						}
					}
				}
			}
		}
	}

	//second format: the first line specifies the name of the attribute
	public void nodelistParseTab(String file, Net n) {
		Node tmp= null;
		String lines[]= file.trim().split(lineBreak);
		String fields[] = null;

		for (int i=0; i<lines.length; i++){
			String line = lines[i].replaceAll(lineBreak, "");
			String cols[] = line.split(separator);

			if (i==0) {
				fields = cols.clone();
				for (String s:fields) n.nodeattributes.add(s.toLowerCase().trim());
			} else {
				tmp = n.hasNode(cols[0].trim());

				if (tmp!=null){
					for (int j=1; j<cols.length;j++) {
						String value = cols[j].trim();
						if (value!=null&&value.length()>0) {
							String key = fields[j].toLowerCase().trim();
							if (key.contentEquals("pos")) {
								setLockPos(tmp,cols[1].trim());
							} else {
								addAttribute(n, tmp, value, key);
							}
							parseNodeAttributes(tmp, n);
						}
					}
				}
			}
		}
	}

	private void setLockPos(Node tmp, String trim) {
		String pos[] = trim.split(",");
		if (pos.length<3) return;
		try {
			tmp.pos.x = Float.parseFloat(pos[0]);
			tmp.pos.y = Float.parseFloat(pos[1]);
			tmp.pos.z = Float.parseFloat(pos[2]);
			tmp.setLocked(true);
			tmp.lockedPos= new Vector3D (tmp.pos.x,tmp.pos.y,tmp.pos.z);

		} catch (NumberFormatException e) {
			System.out.println("bad node position: "+trim);
		}
	}

	private void addAttribute(Net n, GraphElement tmp, String value, String key) {
		// chain attributes
		if (tmp.hasAttribute(key)) {
			String attribute = tmp.getAttribute(key);
			if (!attribute.contains(value)) tmp.setAttribute(key, attribute+app.splitAttribute+value);
		} 
		else 
		{
			tmp.setAttribute(key, value);
			if (tmp instanceof Node) n.nodeattributes.add(key);
			if (tmp instanceof Edge) n.edgeattributes.add(key);
		}
	}

	public void parseNodeAttributes(Node tmp, Net n) {
		if (tmp.hasAttribute("name")) tmp.altName=tmp.getAttribute("name");
		if (tmp.hasAttribute("color")) {
			tmp.setAssignedColor(Func.parseColorInt(tmp.getAttribute("color"),16));
			tmp.removeAttribute("color");
		}
		if (tmp.hasAttribute("color2")) {
			tmp.setAssignedColor2(Func.parseColorInt(tmp.getAttribute("color2"),16));
			tmp.removeAttribute("color2");
		}
		if (tmp.hasAttribute("project")) tmp.altName=tmp.getAttribute("project");
		if (tmp.hasAttribute("person")) tmp.altName=tmp.getAttribute("person");
		if (tmp.hasAttribute("group")) tmp.partOfGroup=true; else tmp.partOfGroup=false;
		if (tmp.hasAttribute("year")){
			try {
				tmp.setTime(Float.parseFloat(tmp.getAttribute("year")));
				n.timeTable.put(tmp, tmp.getTime());
			} catch (NumberFormatException e) {
			}
		}
	}

	public void saveNet(String filename, Net net) {
		StringBuffer sb = new StringBuffer();
		for (Edge e :net.nEdges){
			sb.append(e.getA().name+"\t"+e.getB().name);
			if (e.attributes.size()>1) {
				String attributes = "";
				for (Entry ent:e.attributes.entrySet()) {
					if (ent.getKey()!="id") attributes+="\t"+ent.getKey()+"="+ent.getValue();
				}
				sb.append(attributes);
			}
			sb.append("\n");
		}
		String outString = sb.toString();
		FileIO.fileWrite(filename, outString); 
	}

	public void saveNodeData( String filename, Net net){
		StringBuffer sb = new StringBuffer();

		for (Node n :net.nNodes){
//			sb.append(n.name); //+"\t"+nRef.altName+"\t";
//			if (n.altName!=""&&n.altName.hashCode()!=n.name.hashCode()) sb.append("\tname="+n.altName);

			if (n.isLocked()) sb.append("pos="+n.lockedPos.x+","+n.lockedPos.y+","+n.lockedPos.z);
			
			if (n.attributes.size()>1) {
				String attributes = "";
				for (Entry ent:n.attributes.entrySet()) {
					if (ent.getKey()!="id") attributes+="\t"+ent.getKey()+"="+ent.getValue();
				}
				sb.append(attributes);
			}
			sb.append("\n");
		}
		String outString = sb.toString();
		FileIO.fileWrite(filename, outString); 
	}

	public void saveNetTab(String filename, Net net) {
		StringBuffer sb = new StringBuffer();
		TreeSet<String> attrib = new TreeSet<String>();
		for (Edge e :net.nEdges) attrib.addAll(e.attributes.keySet());
		attrib.remove("id");

		sb.append("start\ttarget");
		for (String l:attrib) sb.append("\t"+l);
		sb.append("\n");

		for (Edge e :net.nEdges){
			sb.append(e.getA().name+"\t"+e.getB().name);
			for (String l:attrib) {
				sb.append("\t");
				if (l!="id"&&e.attributes!=null&&e.attributes.containsKey(l)) sb.append(e.attributes.get(l));
			}
			sb.append("\n");
		}
		String outString = sb.toString();
		FileIO.fileWrite(filename, outString); 
	}

	public void saveNodeDataTab( String filename, Net net){

		StringBuffer sb = new StringBuffer();
		TreeSet<String> attrib = new TreeSet<String>();
		for (Node e :net.nNodes) attrib.addAll(e.attributes.keySet());
		attrib.remove("id");

		sb.append("id\tpos");
		for (String l:attrib) sb.append("\t"+l);
		sb.append("\n");

		for (Node n :net.nNodes){
			sb.append(n.name); 
			sb.append("\t");
			if (n.isLocked()) sb.append(n.lockedPos.x+","+n.lockedPos.y+","+n.lockedPos.z);

			for (String l:attrib) {
				sb.append("\t");
				if (l!="id"&&n.attributes!=null&&n.attributes.containsKey(l)) sb.append(n.attributes.get(l));
			}
			sb.append("\n");
		}
		String outString = sb.toString();
		FileIO.fileWrite(filename, outString); 
	}

	public void saveGraphML (String filename, Net net) {
		StringBuffer sb = new StringBuffer();
		String dir; 
		if (app.directed) dir="directed"; else dir = "undirected";
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\nhttp://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
		for (String key:net.nodeattributes) {
			String enc = FileIO.HTMLEntityEncode(key);
			sb.append("<key attr.name=\""+enc+"\" attr.type=\"string\" for=\"node\" id=\""+enc+"\"/>\n");
		}
		for (String key:net.edgeattributes) {
			String enc = FileIO.HTMLEntityEncode(key);
			sb.append("<key id=\""+enc+"\" for=\"edge\" attr.name=\""+enc+"\" attr.type=\"string\"/>\n");
		}
		sb.append("<graph id=\"G\" edgedefault=\""+dir+"\">\n");
		for (Node n :net.nNodes){
			sb.append("<node id=\""+FileIO.HTMLEntityEncode(n.name)+"\">\n");
			for (String att:n.attributes.keySet()) {
				String enc = FileIO.HTMLEntityEncode(att);
				sb.append("<data key=\""+enc+"\">"+FileIO.HTMLEntityEncode(n.attributes.get(att))+"</data>\n");
			}
			sb.append("</node>\n");
		}
		for (Edge e :net.nEdges){
			sb.append("<edge source=\""+FileIO.HTMLEntityEncode(e.getA().name)+"\" target=\""+FileIO.HTMLEntityEncode(e.getB().name)+"\">\n");
			for (String att:e.attributes.keySet()) {
				String enc = FileIO.HTMLEntityEncode(att);
				sb.append("<data key=\""+enc+"\">"+FileIO.HTMLEntityEncode(e.attributes.get(att))+"</data>\n");
			}
			sb.append("</edge>\n");
		}
		sb.append("</graph>\n</graphml>\n");
		String outString = sb.toString();
		String encode;
		FileIO.fileWrite(filename, outString); 
	}

	public void saveGML (String filename, Net net) {
		StringBuffer sb = new StringBuffer();
		String dir; 
		if (app.directed) dir="directed"; else dir = "undirected";
		sb.append("Creator	\"SemaSpace\"\nVersion	1.0\ngraph	[\n");
		//		for (String key:net.nodeattributes) {
		//			String enc = FileIO.HTMLEntityEncode(key);
		//			sb.append("<key id=\""+enc+"\" for=\"node\" attr.name=\""+enc+"\" attr.type=\"string\"/>\n");
		//		}
		//		for (String key:net.edgeattributes) {
		//			String enc = FileIO.HTMLEntityEncode(key);
		//			sb.append("<key id=\""+enc+"\" for=\"edge\" attr.name=\""+enc+"\" attr.type=\"string\"/>\n");
		//		}
		for (Node n :net.nNodes){
			String altName = FileIO.HTMLEntityEncode(n.altName.replace('\"', '\''));
			sb.append("\tnode\t[\n\troot_index\t"+n.genId()+"\n\tid\t"+n.genId()+"\n");
			sb.append("\tgraphics\t[\n\t\tx\t"+n.pos.x+"\n");
			sb.append("\t\ty\t"+n.pos.y+"\n");
			sb.append("\t\ttype\t\"ellipse\"\n");
			sb.append("\t\toutline_width\t0\n");
			sb.append("\t]\n");
			sb.append("\tlabel\t\""+altName+"\"\n");
			//			for (String att:n.attributes.keySet()) {
			//				String enc = FileIO.HTMLEntityEncode(att);
			//				sb.append("\t"+enc+"\"\t"+FileIO.HTMLEntityEncode(n.attributes.get(att))+"\"\n");
			//			}
			sb.append("\t]\n");
		}
		for (Edge e :net.nEdges){
			String altName = FileIO.HTMLEntityEncode(e.altName.replace('\"', '\''));
			sb.append("\tedge\t[\n\troot_index\t"+e.genId()+"\n\ttarget\t"+e.getB().genId()+"\n\tsource\t"+e.getA().genId()+"\n\tlabel\t\""+altName+"\"\n\t]\n");
			//			for (String att:e.attributes.keySet()) {
			//				String enc = FileIO.HTMLEntityEncode(att);
			//				sb.append("<data key=\""+enc+"\">"+FileIO.HTMLEntityEncode(e.attributes.get(att))+"</data>\n");
			//			}
		}
		sb.append("]\n");
		String outString = sb.toString();
		String encode;
		FileIO.fileWrite(filename, outString); 
	}
}
