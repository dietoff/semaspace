package semaGL;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
//import java.text.Normalizer;

import javax.imageio.ImageIO;

import data.Net;
import data.Node;

import nehe.TextureReader;

public class FileIO {
	class LoadTextures extends Thread {
		Net net; 
		String loc;
		Boolean running = true;

		public LoadTextures(String loc_, Net net_){
			loc = loc_;
			net = net_;
			System.out.println("LoadTextures.LoadTextures()");
		}
		public void end() 
		{
			running = false;
		}

		@Override
		public void run()
		{
			System.out.println("LoadTextures.run()");
			for (Node n:net.nNodes)
			{
				if (!running) return;
				loadTexture(loc+"/"+n.getId()+".jpg", n);
			}
		}
	}
	class LoadTexturesUrl extends Thread {
		Net net; 
		int size;
		boolean running;
		private String loc;

		public LoadTexturesUrl(String loc_,Net net_, int size_){
			loc = loc_;
			net = net_;
			size = size_;
		}
		public void end() {
			running=false;
		}

		@Override
		public void run() {
			running = true;
			for (Node n:net.nNodes)
			{
				if (!running) return;
				try {
					if (!loadTexture(loc+"/"+n.getName()+".png", n)&&!loadTexture(loc+"/"+n.getName()+".jpg", n)) {
						BufferedImage img = loadTextureUrl(n, size);
						if (img!=null) saveBufferedImage(img, loc+"/"+n.getName()+".png");
					}
					//					while(n.newTex){ };
				} catch (IOException e) {
				}
			}
		}
	}
	static Net net;
	static ByteBuffer bb;
	public static BufferedImage copyBufImage(BufferedImage source, BufferedImage target) {
		Graphics2D g2 = target.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		double scalex = (double) target.getWidth()/ source.getWidth();
		double scaley = (double) target.getHeight()/ source.getHeight();
		AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
		g2.drawRenderedImage(source, xform);
		g2.dispose();
		return target;
	}

	public static String fileRead(File file) throws IOException {
		// Create an input stream and file channel
		// Using first argument as file name to read in
		FileInputStream fis = new FileInputStream(file);
		//		Reader in = new InputStreamReader(fis, "UTF-8");
		FileChannel fc = fis.getChannel();
		// Read the contents of a file into a ByteBuffer
		bb = ByteBuffer.allocate((int)fc.size());
		fc.read(bb);
		fc.close();
		fis.close();
		// Convert ByteBuffer to one long String
		String content = new String(bb.array(), "UTF-8");
//		String content2 = java.text.Normalizer.normalize(content, Normalizer.Form.NFC);

		bb = null;
		System.gc();
		return content;
	}

	public static void fileWrite(String filename, String outString) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(outString);
			out.close();
		} catch (IOException e) {
		}
	}

	public static GraphicsConfiguration getDefaultConfiguration() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.getDefaultConfiguration();
	}

	public static String loadFile(File file_) {
		String file="";
		try {
			file = fileRead(file_);
		} catch (IOException e) {
			System.out.println("file read error: "+file_);
			return null;
		}
		return file;
	}


	public static boolean loadTexture(String filename, Node node) {
		//		System.out.println("load texture:"+texfolder+node.name+".jpg");
		try {
			node.tex = 
				TextureReader.readTexture(filename);
			if (node.tex!=null) {
				node.newTex =true;
				//			System.out.println("success");
				return true;
			} else return false;

		} catch (IOException e) {
			return false;
		}
	}

	public static BufferedImage loadTextureUrl(Node node, int size) throws IOException {
		if (node.hasAttribute("url")&&!node.hasTexture()) {

			BufferedImage image = ImageIO.read(new URL(node.getAttribute("url")));
			float scalex = size/(float)image.getWidth();
			float scaley = size/(float)image.getHeight();

			float scale = Math.max(scalex, scaley);
			//			System.out.println("FileIO.loadTextureUrl() x:"+image.getWidth()+" y:"+image.getHeight());
			//rescale image
			BufferedImage thmb = FuncGL.scale(image, scale, scale);
			//			System.out.println("FileIO.loadTextureUrl() w:"+thmb.getWidth()+" h:"+thmb.getHeight());
			//crop
			BufferedImage img = thmb.getSubimage(0, 0, size, size); //getScaledInstance

			//set as texture
			node.tex = 
				TextureReader.readPixels(img, false);
			if (node.tex!=null) {
				node.newTex =true;
			}
			return img;
		}
		else return null;
	}

//	public static String readCachedPage(String filename) throws IOException {
//		String page;
//		page = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(FileIO.fileRead(filename));
//		return page;
//	}

	static void saveImg(String folder, Node node) throws IOException {
		Node n = node;
		URL url = new URL(n.imgurl);

		if (n!=null&&!FileIO.loadTexture(folder+"/"+n.getId()+".jpg",n)&&n.imgurl!=null) {

			BufferedImage image = ImageIO.read(url);
			GraphicsConfiguration gc = getDefaultConfiguration();
			String filename = folder+n.name+".jpg";
			ImageIO.write(copyBufImage(image, gc.createCompatibleImage(64, 64, 1)), "jpeg", new File(filename));
			saveImg(folder,n);
		}
	}

	static boolean saveBufferedImage(BufferedImage img, String filename) {
		if (img!=null)
			try {
				ImageIO.write(  img, "png", new File(filename));
				return true;
			} catch (IOException e) {
				return false;
			}
			else return false;
	}

	HttpClient httpClient;
	private SemaSpace app;
	private LoadTexturesUrl t1;
	private LoadTextures t2;

	public FileIO(SemaSpace app_){
		app= app_;
		httpClient = new HttpClient();
	}

	public String getPage(String url) {
		String page = httpClient.getPage(url);
		return page;
	}
	public void loadTextures(String loc, Net net_) {
		t2 =   new LoadTextures(loc, net_) ;
		t2.start();
	}
	public void loadTexturesUrl(String loc, Net net, int size) {
		if (t1!=null) {
			t1.end();
		}
		//		else
		t1 =   new LoadTexturesUrl(loc, net, size) ;
		t1.start();
	}

	public boolean storeStream(String url, String filename) {
		return httpClient.storeStream(url, filename);
	}
}

