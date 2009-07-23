package semaGL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.UnsupportedEncodingException;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import data.BBox3D;
import data.Edge;
import data.GraphElement;
import data.Net;
import data.Node;
import data.Vector3D;



public class GraphRendererSVG {
	private SemaParameters app;
	private Net net;
	boolean circles= false;
	private boolean nodeAligned= false;

	public GraphRendererSVG (SemaParameters app_){
		app=app_;
		net = app.getView();
		circles = app.getSvgNodeCircles();
	}


	void renderSVG(GL gl,  Net net_, int text, String filename) {
		/*
		HashMap<Node,double[]> node2Dpos = new HashMap<Node, double[]>();
		HashMap<Edge,double[]> edge2Dvec = new HashMap<Edge, double[]>();

		if (!app.isTree()) renderClusters(gl, nr);

		for (Node n: net.nNodes) {
			double[] pos = nr.project2screen(gl, n.pos);
			node2Dpos.put(n, pos);
		}
		for (Edge e: net.nEdges) {
			double[] a = nr.project2screen(gl, e.getA().pos);
			double[] b = nr.project2screen(gl, e.getB().pos);
			double[] evec = {a[0],a[1],b[0],b[1]};
			edge2Dvec.put(e, evec);
		}*/
		net = net_;
		try {
			createSVG(filename);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void createSVG(String filename)
	throws UnsupportedEncodingException, SVGGraphics2DIOException {

		BBox3D bbx = BBox3D.calcBounds(net.nNodes);

		// Get a DOMImplementation.
		DOMImplementation domImpl =
			GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document doc = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgG = new SVGGraphics2D(doc);
		Dimension bounds = new Dimension((int)bbx.size.x+400, (int)bbx.size.y+400);

		svgG.setSVGCanvasSize(bounds);

		paintSVG(svgG, bbx.min.x-200, bbx.min.y-200);

		//		GVTBuilder builder = new GVTBuilder();
		//		BridgeContext ctx;
		//		ctx = new BridgeContext(new UserAgentAdapter());
		//		GraphicsNode gvtRoot = builder.build(ctx, document);
		//		Rectangle2D b2d = gvtRoot.getSensitiveBounds();
		//		svgGenerator.setSVGCanvasSize(new Dimension((int) b2d.getWidth(), (int) b2d.getHeight()));
		//		svgGenerator.getRoot().setAttributeNS(svgNS, "viewBox", b2d.getMinX()+","+b2d.getMinY()+","+b2d.getMaxX()+","+b2d.getMaxY()); 

		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = false; // we want to use CSS style attributes
		svgG.stream(filename, useCSS);
	}


	public void paintSVG(Graphics2D g2d, float origX, float origY) {
		int font = app.fonttype;
		BasicStroke sngl = new BasicStroke(1f);
		BasicStroke dbl = new BasicStroke(2f); 
		BasicStroke five = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); 
		Font ef = new Font(app.getFontFam(),Font.PLAIN, (int)(app.getLabelsize()));

		AffineTransform id = new AffineTransform();
		id.setToIdentity();

		AffineTransform t = new AffineTransform();
		t.setToIdentity();
		t.translate(-origX, -origY);
		g2d.setTransform(t);


		// Groups 
		if (app.isGroups()) {
			Node n;
			Font large = new Font(app.getFontFam(),Font.PLAIN, (int)(app.getLabelsize()*10f));
			g2d.setFont(large);
			for (String m:net.groups.keySet()) {
				Net group1 = net.groups.get(m);
				n = group1.hasNode(m);
				float[] c = GraphElement.colorFunction(n.name);
				g2d.setPaint(new Color(c[0],c[1],c[2],c[3]));
				g2d.translate(n.pos.x, n.pos.y);
				g2d.drawString(n.getName(), 0, 0);
				g2d.setTransform(t);
				Vector3D D;
				for (Node bref : group1.nNodes){
					if (bref!=n){
						D = bref.pos.copy();
						D.sub(n.pos); 
						D.mult(-1);
						g2d.setPaint(new Color(c[0],c[1],c[2],0.1f));
						groupArrow(g2d, bref.size()*1.5f, n.pos, D);
					}
				}
			}
		}

		// edges
		for (Edge e: net.nEdges) {
			e.genColorFromAtt();

			Node a = e.getA();
			Node b = e.getB();
			float af = a.size(); 
			float bf = b.size(); 

			Vector3D D = Vector3D.sub(b.pos, a.pos);
			Vector3D DN= D.copy();
			DN.normalize();

			Vector3D start = a.pos.copy();
			Vector3D end = b.pos.copy();
			start.add(Vector3D.mult(DN, af));
			end.sub(Vector3D.mult(DN, bf));

			//white background
			g2d.setStroke(dbl);
			g2d.setPaint(new Color(1,1,1,e.getColor()[3]));
			Line2D line = new Line2D.Float(start.x,start.y,end.x,end.y);
			g2d.draw(line);
			//picked
			boolean picked = a.getPickColor()[3]>0||b.getPickColor()[3]>0;
			if (picked){

				g2d.translate(start.x,	start.y);
				Vector3D segment = D.copy();
				segment.mult(0.1f);
				g2d.setStroke(dbl);
				for (int i=0; i<10; i++) {
					float[] interpolatedColor = Func.interpolateRGB(i/10f, Func.RGBtoHSV(b.pickColor), Func.RGBtoHSV(a.pickColor));
					Color paint = new Color(interpolatedColor[0],interpolatedColor[1],interpolatedColor[2],interpolatedColor[3]);
					g2d.setPaint(paint);
					line = new Line2D.Float(0,0,segment.x,segment.y); g2d.draw(line);
					g2d.translate(segment.x,segment.y);
				}
				g2d.setTransform(t);
				if (app.directed) {
					g2d.setPaint(new Color(b.pickColor[0],b.pickColor[1],b.pickColor[2],b.pickColor[3]));
					arrowHeadEmpty(g2d, 10, end, DN);
				}
			}
			else
			{
				//actual stroke
				g2d.setStroke(sngl);
				g2d.setPaint(new Color(e.getColor()[0],e.getColor()[1],e.getColor()[2],e.getColor()[3]));
				line = new Line2D.Float(start.x,start.y,end.x,end.y);
				g2d.draw(line);
				if (app.directed){
					arrowHeadEmpty(g2d, 10, end, DN);
				};
			};
		}

		// clusters
		if (app.isCluster()&&app.drawClusters){
			for (Node n: net.nNodes) {
				if (n.cluster.size()>1) {
					float[] col = GraphElement.colorFunction(n.name);
					col[3]=Math.min(n.alpha, 0.05f);
					Polygon2D p = new Polygon2D();
					p.addPoint(n.pos.x, n.pos.y);
					for (Node c:n.cluster){
						p.addPoint(c.pos.x, c.pos.y);
					}
					Node c= n.cluster.iterator().next();
					p.addPoint(c.pos.x, c.pos.y);
					p.addPoint(n.pos.x, n.pos.y);
					g2d.setPaint(new Color(col[0],col[1],col[2],col[3]));
					g2d.fill(p);
				}
			}
		}

		// nodes
		for (Node n: net.nNodes) {
			n.genColorFromAtt();
			float size = n.size()*2f;

			g2d.setPaint(new Color(n.getColor()[0],n.getColor()[1],n.getColor()[2],n.getColor()[3]));

			if (circles) {
				g2d.fillOval((int)(n.pos.x)-(int)(size/2), (int)(n.pos.y)-(int)(size/2), (int)size, (int)size);
			}
			else {
				g2d.fillRect((int)(n.pos.x)-(int)(size/2), (int)(n.pos.y)-(int)(size/2), (int)size, (int)size);
			}
			if (n.pickColor[3]>0) {
				g2d.setPaint(new Color(n.pickColor[0],n.pickColor[1],n.pickColor[2],1));
				g2d.drawRect((int)(n.pos.x)-(int)(size/2), (int)(n.pos.y)-(int)(size/2), (int)size, (int)size);
			}
		}

		if (font!=3){
			// edge labels
			for (Edge e: net.nEdges) {
				String txt = e.genTextSelAttributes();
				if (txt.length()>0){

					g2d.setFont(ef);
					Node a = e.getA();
					Node b = e.getB();

					Vector3D midP = Vector3D.midPoint(a.pos,b.pos);
					TextLayout tl = new TextLayout(txt,ef,g2d.getFontRenderContext());

					g2d.setPaint(new Color(e.getColor()[0],e.getColor()[1],e.getColor()[2],e.getColor()[3]));

					g2d.translate((midP.x),(midP.y));

					Vector3D sub = Vector3D.sub(a.pos, b.pos);
					float angle = (float) (Math.atan(sub.y/sub.x));
					g2d.rotate(angle);
					g2d.translate(0, -2);

					float advance = tl.getAdvance()/2f;
					g2d.translate(-advance, 0);

					if (e.getColor()[3]>0.2f&& txt.length()>0){
						if (font==0){
							g2d.setPaint(new Color(1f,1f,1f));
							Shape outline = tl.getOutline(id);
							g2d.setStroke(five);
							g2d.draw(outline);
							g2d.setPaint(new Color((e.getColor()[0]*0.5f),(e.getColor()[1]*0.5f),(e.getColor()[2]*0.5f),e.getColor()[3]));
							g2d.setStroke(sngl);
							g2d.fill(outline);

						} else {
							//white background
							//							g2d.setPaint(new Color(255,255,255,255));
							//							g2d.fillRect(0, -(int)tl.getAscent(), (int)tl.getAdvance(), (int)(tl.getAscent()));
							g2d.setFont(ef);
							g2d.setPaint(new Color(e.getColor()[0],e.getColor()[1],e.getColor()[2],e.getColor()[3]));
							g2d.drawString(txt, 0, 0);
						}
					}
					g2d.setTransform(t);
				}
			}

			// node labels
			for (Node n: net.nNodes) {
				String txt = n.genTextSelAttributes();

				if (txt.length()>0){
					float size = n.size();
					String[] sp = txt.split("\n");


					if ((n.pickColor[3]>0.2f&&app.fadeLabels)||(n.getColor()[3]>0.2&&!app.fadeLabels)  &&txt.length()>0) {
						for (int i = 0; i<sp.length; i++){
//														for (int i = 0; i<1; i++){
							g2d.translate((int)(n.pos.x), (int)(n.pos.y));
							if (!app.isTree()&&!app.labelsEdgeDir) {
								g2d.translate((int)(size/2),-(int)(size/2));
							}
							if (app.isTilt()) {
								g2d.rotate(-0.436332312998582);
							} 

							int fntsize = (int)((app.getLabelsize()+n.size()*app.getLabelVar())*1.5f);
							Font varFont = new Font(app.getFontFam(),Font.PLAIN, fntsize);
							FontRenderContext frc = g2d.getFontRenderContext();
							TextLayout tl = new TextLayout(sp[i],varFont,frc);

							if (app.isTree()&&app.getView().distances.getNodeDistance(n)>0) alignLabel(g2d, n.pos, n.size(), tl);
							else
								if (app.labelsEdgeDir&&!app.isTilt()){
									if (n.adList.size()==1&&n.inList.size()==0) {
										Vector3D sub = Vector3D.sub(n.pos, n.adList.iterator().next().pos);
										alignLabel(g2d, sub, n.size(), tl);
									} else
										if (n.inList.size()==1&&n.inList.size()==0) {
											Vector3D sub = Vector3D.sub(n.pos, n.inList.iterator().next().pos);
											alignLabel(g2d, sub, n.size(), tl);
										}
										else {
											float advance = tl.getAdvance()/2f;
											g2d.translate(-advance, -n.size()/2f);
										}
								}
							
							if (font==0) {
								g2d.setPaint(new Color(1f,1f,1f));
								g2d.setStroke(five);
								g2d.translate(0, i*fntsize);
								Shape outline = tl.getOutline(id);
								g2d.draw(outline);
								g2d.setPaint(new Color((n.getColor()[0]*0.5f),(n.getColor()[1]*0.5f),(n.getColor()[2]*0.5f),n.getColor()[3]));
								g2d.setStroke(sngl);
								g2d.fill(outline);
							} else {
								g2d.setPaint(new Color(255,255,255,255));
								g2d.fillRect(0, i*fntsize-(int)tl.getAscent(), (int)tl.getAdvance(), (int)(tl.getAscent()));
								g2d.setFont(varFont);
								g2d.setPaint(new Color((n.getColor()[0]*0.5f),(n.getColor()[1]*0.5f),(n.getColor()[2]*0.5f),n.getColor()[3]));
								g2d.drawString(sp[i], 0, i*fntsize);
							}
							g2d.setTransform(t);
						}
					}
				}
			}
		}
	}

	static void arrowHeadEmpty(Graphics2D g2d, float size, Vector3D pos, Vector3D dir) {
		Vector3D v1 = new Vector3D(pos.x-size*dir.x,pos.y-size*dir.y,pos.z-size*dir.z);
		Vector3D v2 = new Vector3D(pos.x-size*dir.x+size*0.3f*dir.y,pos.y-size*dir.y-size*0.3f*dir.x,pos.z-size*dir.z);
		Vector3D v3 = new Vector3D(pos.x,pos.y,pos.z);
		Polygon2D arrow = new Polygon2D();
		arrow.addPoint(v1.x, v1.y);
		arrow.addPoint(v2.x, v2.y);
		arrow.addPoint(v3.x, v3.y);
		g2d.fill(arrow);
	}

	static void groupArrow(Graphics2D g2d, float size, Vector3D pos, Vector3D dir) {
		Vector3D dn = dir.copy();
		dn.normalize();
		Vector3D v1 = new Vector3D(pos.x-dir.x-size*dn.y,pos.y-dir.y+size*dn.x,pos.z-dir.z);
		Vector3D v2 = new Vector3D(pos.x-dir.x+size*dn.y,pos.y-dir.y-size*dn.x,pos.z-dir.z);
		//white
		Vector3D v3 = new Vector3D(pos.x,pos.y,pos.z);
		Polygon2D arrow = new Polygon2D();
		arrow.addPoint(v1.x, v1.y);
		arrow.addPoint(v2.x, v2.y);
		arrow.addPoint(v3.x, v3.y);
		g2d.fill(arrow);
	}

	private void alignLabel(Graphics2D g2d, Vector3D n, float margin, TextLayout tl) {
		float angle = (float) (Math.atan(n.y/n.x));
		g2d.rotate(angle);
		float marg = margin+5;
		if (n.x<0) {
			float advance = tl.getAdvance()+marg;
			g2d.translate(-advance, 0);
		} else	g2d.translate(marg, 0);
	}
}
