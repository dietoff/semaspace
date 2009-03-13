package semaGL;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import data.Edge;
import data.Net;
import data.Node;

public class NetLoader {
	SemaSpace app;
	private String lineBreak = "\r\n|\n|\r";
	private String separator = "\t";
	private String value = "=";

	public NetLoader(SemaSpace app_){
		app = app_;
	}

	/**
	 * load an edge list (format 1)
	 *
	 * @param file_
	 */
	public Net edgelistLoad(File file_) {
		Net edges = null;
		String file=FileIO.loadFile(file_);
		if (file!=null&&file.length()>0) {
			edges = edgelistParse(file); 
		}
		return edges;
	}
	/**
	 * load an edge list (format 2)
	 * @param file_
	 * @return 
	 */
	public Net edgelistLoadTab(File file_) {
		Net edges = null;
		String file=FileIO.loadFile(file_);
		if (file!=null&&file.length()>0)  edges = edgelistParse2(file); 
		return edges;
	}
	/**
	 * parse an edge list (format 1)
	 * @param content
	 */
	public Net edgelistParse(String content) {
		Edge tmp = null;
		Net r= new Net(app);
		String lines[]= content.split(lineBreak);
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
					String val1 = val[0].toLowerCase().trim();
					if (val.length>1) {
						String val2 = val[1].trim();
						tmp.setAttribute(val1, val2);
					}
					r.edgeattributes.add(val1);
					setedgeAttributes(tmp);
				}
			}
		}
		return r;
	}

	/**
	 * parse an edge list (format 2)
	 * the fist line specifies the field names, the rest is values
	 * @param content
	 */
	Net edgelistParse2(String content) {
		Net r= new Net(app);
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
					for (String s:fields) r.edgeattributes.add(s.toLowerCase());
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
									String val1 = fields[j].toLowerCase().trim();
									String val2 = cols[j].trim();
									tmp.setAttribute(val1, val2);
								}
								setedgeAttributes(tmp);
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
	private void setedgeAttributes(Edge tmp) {
		if (tmp==null) return;
		if (tmp.hasAttribute("function")) 
			tmp.setAltName(tmp.getAttribute("function"));
		if (tmp.hasAttribute("similarity"))
			try {
				tmp.setProperty(10f*Float.parseFloat(tmp.getAttribute("similarity")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
	}

	public void nodelistLoad(File file2, Net n) {
		String file = FileIO.loadFile(file2);
		if  (file!=null&&file.length()>0) {
			nodelistParse(file, n);
		}
	}
	public void nodelistLoad2(File file_, Net n) {
		String file = FileIO.loadFile(file_);
		if  (file!=null&&file.length()>0)  nodelistParse2(file, n);
	}
	public void nodelistParse(String file, Net n) {
		Node tmp;
		String lines[]= file.split(lineBreak);
		for (int i=0; i<lines.length; i++){
			String cols[] = lines[i].split(separator);
			String col1 = null;
			if (cols[0].length()>1) col1 = cols[0].trim();
			tmp = n.addNode(col1);
			//			tmp = nTable.get(cols[0].trim());
			if (tmp!=null){
				for (int j=1; j<cols.length;j++) {
					String val[]=cols[j].split(value);	
					if (val.length>1) {
						String key = val[0].toLowerCase().trim();
						String value = val[1].trim();

						if (tmp.hasAttribute(key)) {
							String attribute = tmp.getAttribute(key);
							if (attribute!=value) tmp.setAttribute(key, attribute+","+value);
						} else {
							tmp.setAttribute(key, value);
							n.nodeattributes.add(key);
						}
						parseAttributes(tmp, n);
					}
				}
			}
		}
	}

	//second format: the first line specifies the name of the attribute
	public void nodelistParse2(String file, Net n) {
		Node tmp;
		String lines[]= file.split(lineBreak);
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
						if (value.length()>0) {
							String key = fields[j].toLowerCase().trim();

							if (tmp.hasAttribute(key)) {
								String attribute = tmp.getAttribute(key);
								if (!attribute.contentEquals(value)) tmp.setAttribute(key, attribute+","+value);
							} else {
								tmp.setAttribute(key, value);
							}
							parseAttributes(tmp, n);
						}
					}
				}
			}
		}
	}

	public void parseAttributes(Node tmp, Net n) {
		if (tmp.hasAttribute("name")) tmp.altName=tmp.getAttribute("name");
		if (tmp.hasAttribute("color")) tmp.setColor(Func.parseColorInt(tmp.getAttribute("color")));
		if (tmp.hasAttribute("color2")) tmp.setColor2(Func.parseColorInt(tmp.getAttribute("color2")));
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

	public void saveNodeData( String filename, Net net){
		StringBuffer sb = new StringBuffer();

		for (Node n :net.nNodes){
			sb.append(n.name); //+"\t"+nRef.altName+"\t";
			if (n.altName!=""&&n.altName.hashCode()!=n.name.hashCode()) sb.append("\tname="+n.altName);
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

	public void saveNodeDataTab( String filename, Net net){

		StringBuffer sb = new StringBuffer();
		TreeSet<String> attrib = new TreeSet<String>();
		for (Node e :net.nNodes) attrib.addAll(e.attributes.keySet());
		attrib.remove("id");

		sb.append("node");
		for (String l:attrib) sb.append("\t"+l);
		sb.append("\n");

		for (Node n :net.nNodes){
			sb.append(n.name); 
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
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\nhttp://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n<graph id=\"G\" edgedefault=\""+dir+"\">\n");

		for (String key:net.nodeattributes) {
			String enc = FileIO.HTMLEntityEncode(key);
			sb.append("<key id=\""+enc+"\" for=\"node\" attr.name=\""+enc+"\" attr.type=\"string\"/>\n");
		}
		for (String key:net.edgeattributes) {
			String enc = FileIO.HTMLEntityEncode(key);
			sb.append("<key id=\""+enc+"\" for=\"edge\" attr.name=\""+enc+"\" attr.type=\"string\"/>\n");
		}
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
