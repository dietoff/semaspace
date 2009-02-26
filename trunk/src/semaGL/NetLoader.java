package semaGL;

import java.io.File;
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
	public Net edgelistLoad2(File file_) {
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

	public void saveNet2(String filename, Net net) {
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

	public void saveNodeData2( String filename, Net net){

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

}
