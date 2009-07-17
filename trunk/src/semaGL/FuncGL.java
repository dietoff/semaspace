package semaGL;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
//import java.text.Normalizer.Form;
import javax.media.opengl.GL;
import sun.text.Normalizer;
import nehe.TextureReader.Texture;
import net.sourceforge.ftgl.FTBBox;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;
import data.Vector3D;

public class FuncGL {
	private static int strokefont = GLUT.STROKE_ROMAN;
	private static int fontOffset;
	SemaParameters app;
	private TextRenderer renderer;

	FuncGL(SemaParameters space) {
		app=space;
		
	}

	static void renderText( SemaParameters app, String text, float[] textColor, float offset , int font, int id, float distToCam, boolean centerOnNode, boolean fast) {

		switch (font){
		case 0:
			renderHiqTxt(app, text, textColor, id, offset, centerOnNode);
			break;
		case 1:
			renderTxt(app, text, textColor, id, offset, centerOnNode, fast);
			break;
		case 2:
			renderBtxt(app, text, textColor, id, offset, distToCam);
			break;
		}
	}

	static void renderBtxt(SemaParameters app, String text, float[] textColor, int id, float offset, float distToCam) {
		GL gl=app.getGL();
		gl.glPushMatrix();
		float h = app.getGlD().getHeight();
		gl.glColor4f(textColor[0],textColor[1],textColor[2],textColor[3]);
		int font =	GLUT.BITMAP_HELVETICA_12;

		String[] lines = text.split("\n"); 
		float pos[] = new float[4];
		gl.glGetFloatv( GL.GL_CURRENT_RASTER_POSITION, pos, 0 );
		float factor = pos[3]/h;

		for (int i = 0; i<lines.length; i++){
			//			j2d.drawString(lines[i], pos[0], pos[1]);


			if (app.layout2d) { 
				//paint background frame
				int len = app.glut.glutBitmapLength(font, lines[i]);
				gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
				//				gl.glColor4f(.9f, .9f, .9f, textColor[3]);
				gl.glColor4f(1f, 1f, 1f, 0.9f);
				gl.glRecti((int)offset, (int)(-factor*(i*25f+7)), (int)(offset+factor*len*1.4f),(int)(factor*(-i*25f+18)));
			}

			//paint text
			gl.glColor4f(textColor[0],textColor[1],textColor[2],textColor[3]);
			gl.glRasterPos2i((int)offset, (int)(-factor*i*25f));

			app.glut.glutBitmapString(font, lines[i]);

		}
		gl.glPopMatrix();
	}

	static void renderHiqTxt( SemaParameters p, String text, float[] textColor, int id, float offset, boolean center) {
		GL gl=p.getGL();

		gl.glPushMatrix();
		//		gl.glLoadName(id);
		float scale = offset*0.025f;
		gl.glScalef(scale, scale, scale);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

		String[] lines = text.split("\n"); 
		if (!center) gl.glTranslatef(40, 90, 0);
		for (int i = 0; i<lines.length; i++){
			gl.glTranslatef(0, -60f, 0);
			
			gl.glPushMatrix();
				FTBBox box = p.getApp().hiQfont.getBBox(lines[i]);
				gl.glTranslatef(box.lowerX,box.lowerY,0);
				gl.glScalef(box.getWidth()/2f,box.getHeight()/2f,0);
				gl.glColor4f(1,1,1,0.9f);
				gl.glTranslatef(1f,1f,0);
				quad(gl);
			gl.glPopMatrix();
//			
//			gl.glLineWidth(5f);
//			gl.glColor4f(1,1,1,1);
//			app.outlinefont.render(lines[i]);
			
			gl.glLineWidth(0.5f);
			gl.glColor4f(textColor[0],textColor[1],textColor[2],textColor[3]);
//			gl.glColor4f(0,0,0,1);
			if (p.getApp().outlinefont!=null) {
				p.getApp().outlinefont.render(lines[i]);
			}
			gl.glColor4f(textColor[0],textColor[1],textColor[2],textColor[3]);
			p.getApp().hiQfont.render(lines[i]);
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		}
		gl.glLineWidth(p.textwidth);
		gl.glPopMatrix();
	}
	
	static void renderTxt(SemaParameters app, String text, float[] textColor, int id, float offset, boolean center, boolean fast) {
		GL gl=app.getGL();
		gl.glLoadName(id);
		int fontsize = 8;
		int off = fontsize;
		if (center) off = 0;
		String[] lines = text.split("\n"); 
		for (int i = 0; i<lines.length; i++){
			gl.glPushMatrix();
			float scale = offset*0.01f;
			gl.glScalef(scale, scale, scale);
			gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
			if (!fast) {
				gl.glLineWidth(5);
				gl.glColor4f(1f,1f,1f,1f);
				renderStrokeString( app, strokefont, lines[i], off, off-2*i*fontsize); // Print GL Text To The Screen
			}
			gl.glLineWidth(app.textwidth);
			gl.glColor4f(textColor[0],textColor[1],textColor[2],textColor[3]);
			renderStrokeString( app, strokefont, lines[i], off, off-2*i*fontsize); // Print GL Text To The Screen
			gl.glPopMatrix();
		}
	}

	static void renderStrokeString( SemaParameters app, int font, String string, float offset, float offsety) {
//		String normalize = Normalizer.normalize(string, Form.NFD, 0);
//		String normalize = Normalizer.normalize(string, Normalizer.DECOMP, 0);
		String normalize = UmlautToAscii.umlautToAscii(string);
		GL gl=app.getGL();
		stroke(app, font, normalize, offset, offsety, gl);
	}

	private static void stroke(SemaParameters app, int font, String string,
			float offset, float offsety, GL gl) {
		gl.glPushMatrix();
		gl.glTranslatef(offset*10f, offsety*10f, 0);

		// Render The Text
		app.glut.glutStrokeString(font, string);
		gl.glPopMatrix();
	}

	static float stringlength (SemaParameters app, String st){
		return app.glut.glutStrokeLengthf(strokefont, st);
	}

	static void drawLine(GL gl, Vector3D start, Vector3D end, float[] startc, float[] endc) {
		gl.glBegin(GL.GL_LINES);
		gl.glColor4fv(startc,0);
		gl.glVertex3f(start.x,start.y,start.z);
		gl.glColor4fv(endc,0);
		gl.glVertex3f(end.x,end.y,end.z);
		gl.glEnd();
	}

	static void propertyVector(GL gl, float size, float width, Vector3D pos, Vector3D dir) {
		gl.glBegin(GL.GL_POLYGON);
		//		gl.glVertex3f(pos.x-size*dir.x,pos.y-size*dir.y,pos.z-size*dir.z);
		gl.glVertex3f(pos.x-size*dir.x-width*dir.y,pos.y-size*dir.y+width*dir.x,pos.z-size*dir.z);
		//		gl.glVertex3f(pos.x-(size-width)*dir.x,pos.y-(size-width)*dir.y,pos.z-(size-width)*dir.z);
		gl.glVertex3f(pos.x-size*dir.x+width*dir.y,pos.y-size*dir.y-width*dir.x,pos.z-size*dir.z);
		gl.glVertex3f(pos.x+width*dir.y,pos.y-width*dir.x,pos.z);
		gl.glVertex3f(pos.x-width*dir.y,pos.y+width*dir.x,pos.z);
		//		gl.glVertex3f(pos.x,pos.y,pos.z);
		gl.glEnd();
	}

	static void arrowHead(GL gl, float size, Vector3D pos, Vector3D dir) {
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(pos.x-size*dir.x,pos.y-size*dir.y,pos.z-size*dir.z);
		gl.glVertex3f(pos.x-size*dir.x+size*0.3f*dir.y,pos.y-size*dir.y-size*0.3f*dir.x,pos.z-size*dir.z);
		gl.glVertex3f(pos.x,pos.y,pos.z);
		gl.glEnd();
	}
	static void symArrowHead(GL gl, float size, Vector3D pos, Vector3D dir) {
		Vector3D dn = dir.copy();
		dn.normalize();
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(pos.x-dir.x-size*dn.y,pos.y-dir.y+size*dn.x,pos.z-dir.z);
		gl.glVertex3f(pos.x-dir.x+size*dn.y,pos.y-dir.y-size*dn.x,pos.z-dir.z);
		gl.glVertex3f(pos.x,pos.y,pos.z);
		gl.glEnd();
	}
	
	static void groupArrow(GL gl, float size, Vector3D pos, Vector3D dir) {
		Vector3D dn = dir.copy();
		dn.normalize();
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(pos.x-dir.x-size*dn.y,pos.y-dir.y+size*dn.x,pos.z-dir.z);
		gl.glVertex3f(pos.x-dir.x+size*dn.y,pos.y-dir.y-size*dn.x,pos.z-dir.z);
		gl.glColor4i(255, 255, 255, 0);
		gl.glVertex3f(pos.x,pos.y,pos.z);
		gl.glEnd();
	}
	
	static void arrowHeadEmpty(GL gl, float size, Vector3D pos, Vector3D dir) {
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(pos.x-size*dir.x,pos.y-size*dir.y,pos.z-size*dir.z);
		gl.glVertex3f(pos.x-size*dir.x+size*0.3f*dir.y,pos.y-size*dir.y-size*0.3f*dir.x,pos.z-size*dir.z);
		gl.glVertex3f(pos.x,pos.y,pos.z);
		gl.glEnd();
	}

	static void printString(GL gl, String s) {
		ByteBuffer str = BufferUtil.newByteBuffer(s.length());
		str.put(s.getBytes());
		str.rewind();

		gl.glPushAttrib(GL.GL_LIST_BIT);
		gl.glListBase(fontOffset);
		gl.glCallLists(s.length(), GL.GL_UNSIGNED_BYTE, str);
		gl.glPopAttrib();
	}

	static void makeRasterFont(GL gl) {
		int i;
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		fontOffset = gl.glGenLists(128);
		for (i = 32; i < 127; i++) {
			gl.glNewList(i + fontOffset, GL.GL_COMPILE);
			gl.glBitmap(8, 13, 0.0f, 2.0f, 10.0f, 0.0f, rasters[i - 32], 0);
			gl.glEndList();
		}
	}

	static void quad(GL gl) {
		gl.glBegin(GL.GL_QUADS);           	// Draw A Quad
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glEnd();							// Done Drawing The Quad
	}

	static void triangle1(GL gl) {
		gl.glBegin(GL.GL_TRIANGLES);           	// Draw A Quad
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glEnd();							// Done Drawing The Quad
	}
	static void triangle2(GL gl) {
		gl.glBegin(GL.GL_TRIANGLES);           	// Draw A Quad
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();							// Done Drawing The Quad
	}
	static void initGLTexture(GL gl, Texture tex, int textures[]) {
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexImage2D(GL.GL_TEXTURE_2D,0,3,tex.getWidth(),tex.getHeight(),0,GL.GL_RGB,GL.GL_UNSIGNED_BYTE,tex.getPixels());
		tex=null;
	}


	static public BufferedImage scale(BufferedImage image2D, float x , float y) {

		BufferedImage thumbnail = new BufferedImage((int)(image2D.getWidth()*x+1),(int)(image2D.getHeight()*y+1),BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = thumbnail.createGraphics();
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// scale image
		AffineTransform xform = AffineTransform.getScaleInstance(x,y);
		bg.drawRenderedImage(image2D, xform);
		bg.dispose();

		return thumbnail;
	}

	private static byte rasters[][] = {
		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00 },
			{ 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18,
				0x18, 0x18 },
				{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x36,
					0x36, 0x36 },
					{ 0x00, 0x00, 0x00, 0x66, 0x66, (byte) 0xff, 0x66, 0x66,
						(byte) 0xff, 0x66, 0x66, 0x00, 0x00 },
						{ 0x00, 0x00, 0x18, 0x7e, (byte) 0xff, 0x1b, 0x1f, 0x7e,
							(byte) 0xf8, (byte) 0xd8, (byte) 0xff, 0x7e, 0x18 },
							{ 0x00, 0x00, 0x0e, 0x1b, (byte) 0xdb, 0x6e, 0x30, 0x18, 0x0c,
								0x76, (byte) 0xdb, (byte) 0xd8, 0x70 },
								{ 0x00, 0x00, 0x7f, (byte) 0xc6, (byte) 0xcf, (byte) 0xd8, 0x70,
									0x70, (byte) 0xd8, (byte) 0xcc, (byte) 0xcc, 0x6c, 0x38 },
									{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x1c,
										0x0c, 0x0e },
										{ 0x00, 0x00, 0x0c, 0x18, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
											0x18, 0x0c },
											{ 0x00, 0x00, 0x30, 0x18, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c,
												0x18, 0x30 },
												{ 0x00, 0x00, 0x00, 0x00, (byte) 0x99, 0x5a, 0x3c, (byte) 0xff,
													0x3c, 0x5a, (byte) 0x99, 0x00, 0x00 },
													{ 0x00, 0x00, 0x00, 0x18, 0x18, 0x18, (byte) 0xff, (byte) 0xff,
														0x18, 0x18, 0x18, 0x00, 0x00 },
														{ 0x00, 0x00, 0x30, 0x18, 0x1c, 0x1c, 0x00, 0x00, 0x00, 0x00, 0x00,
															0x00, 0x00 },
															{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff, (byte) 0xff,
																0x00, 0x00, 0x00, 0x00, 0x00 },
																{ 0x00, 0x00, 0x00, 0x38, 0x38, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
																	0x00, 0x00 },
																	{ 0x00, 0x60, 0x60, 0x30, 0x30, 0x18, 0x18, 0x0c, 0x0c, 0x06, 0x06,
																		0x03, 0x03 },
																		{ 0x00, 0x00, 0x3c, 0x66, (byte) 0xc3, (byte) 0xe3, (byte) 0xf3,
																			(byte) 0xdb, (byte) 0xcf, (byte) 0xc7, (byte) 0xc3, 0x66,
																			0x3c },
																			{ 0x00, 0x00, 0x7e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x78,
																				0x38, 0x18 },
																				{ 0x00, 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, 0x60, 0x30,
																					0x18, 0x0c, 0x06, 0x03, (byte) 0xe7, 0x7e },
																					{ 0x00, 0x00, 0x7e, (byte) 0xe7, 0x03, 0x03, 0x07, 0x7e, 0x07,
																						0x03, 0x03, (byte) 0xe7, 0x7e },
																						{ 0x00, 0x00, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, (byte) 0xff,
																							(byte) 0xcc, 0x6c, 0x3c, 0x1c, 0x0c },
																							{ 0x00, 0x00, 0x7e, (byte) 0xe7, 0x03, 0x03, 0x07, (byte) 0xfe,
																								(byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																								(byte) 0xff },
																								{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																									(byte) 0xc7, (byte) 0xfe, (byte) 0xc0, (byte) 0xc0,
																									(byte) 0xc0, (byte) 0xe7, 0x7e },
																									{ 0x00, 0x00, 0x30, 0x30, 0x30, 0x30, 0x18, 0x0c, 0x06, 0x03, 0x03,
																										0x03, (byte) 0xff },
																										{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																											(byte) 0xe7, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																											(byte) 0xe7, 0x7e },
																											{ 0x00, 0x00, 0x7e, (byte) 0xe7, 0x03, 0x03, 0x03, 0x7f,
																												(byte) 0xe7, (byte) 0xc3, (byte) 0xc3, (byte) 0xe7, 0x7e },
																												{ 0x00, 0x00, 0x00, 0x38, 0x38, 0x00, 0x00, 0x38, 0x38, 0x00, 0x00,
																													0x00, 0x00 },
																													{ 0x00, 0x00, 0x30, 0x18, 0x1c, 0x1c, 0x00, 0x00, 0x1c, 0x1c, 0x00,
																														0x00, 0x00 },
																														{ 0x00, 0x00, 0x06, 0x0c, 0x18, 0x30, 0x60, (byte) 0xc0, 0x60,
																															0x30, 0x18, 0x0c, 0x06 },
																															{ 0x00, 0x00, 0x00, 0x00, (byte) 0xff, (byte) 0xff, 0x00,
																																(byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x00 },
																																{ 0x00, 0x00, 0x60, 0x30, 0x18, 0x0c, 0x06, 0x03, 0x06, 0x0c, 0x18,
																																	0x30, 0x60 },
																																	{ 0x00, 0x00, 0x18, 0x00, 0x00, 0x18, 0x18, 0x0c, 0x06, 0x03,
																																		(byte) 0xc3, (byte) 0xc3, 0x7e },
																																		{ 0x00, 0x00, 0x3f, 0x60, (byte) 0xcf, (byte) 0xdb, (byte) 0xd3,
																																			(byte) 0xdd, (byte) 0xc3, 0x7e, 0x00, 0x00, 0x00 },
																																			{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																				(byte) 0xff, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, 0x66,
																																				0x3c, 0x18 },
																																				{ 0x00, 0x00, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3,
																																					(byte) 0xc7, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3,
																																					(byte) 0xc3, (byte) 0xc7, (byte) 0xfe },
																																					{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc0, (byte) 0xc0,
																																						(byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																						(byte) 0xc0, (byte) 0xe7, 0x7e },
																																						{ 0x00, 0x00, (byte) 0xfc, (byte) 0xce, (byte) 0xc7, (byte) 0xc3,
																																							(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																							(byte) 0xc7, (byte) 0xce, (byte) 0xfc },
																																							{ 0x00, 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																								(byte) 0xc0, (byte) 0xfc, (byte) 0xc0, (byte) 0xc0,
																																								(byte) 0xc0, (byte) 0xc0, (byte) 0xff },
																																								{ 0x00, 0x00, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																									(byte) 0xc0, (byte) 0xc0, (byte) 0xfc, (byte) 0xc0,
																																									(byte) 0xc0, (byte) 0xc0, (byte) 0xff },
																																									{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																																										(byte) 0xcf, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																										(byte) 0xc0, (byte) 0xe7, 0x7e },
																																										{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																											(byte) 0xc3, (byte) 0xff, (byte) 0xc3, (byte) 0xc3,
																																											(byte) 0xc3, (byte) 0xc3, (byte) 0xc3 },
																																											{ 0x00, 0x00, 0x7e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18,
																																												0x18, 0x7e },
																																												{ 0x00, 0x00, 0x7c, (byte) 0xee, (byte) 0xc6, 0x06, 0x06, 0x06,
																																													0x06, 0x06, 0x06, 0x06, 0x06 },
																																													{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc6, (byte) 0xcc, (byte) 0xd8,
																																														(byte) 0xf0, (byte) 0xe0, (byte) 0xf0, (byte) 0xd8,
																																														(byte) 0xcc, (byte) 0xc6, (byte) 0xc3 },
																																														{ 0x00, 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																															(byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																															(byte) 0xc0, (byte) 0xc0, (byte) 0xc0 },
																																															{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																(byte) 0xc3, (byte) 0xc3, (byte) 0xdb, (byte) 0xff,
																																																(byte) 0xff, (byte) 0xe7, (byte) 0xc3 },
																																																{ 0x00, 0x00, (byte) 0xc7, (byte) 0xc7, (byte) 0xcf, (byte) 0xcf,
																																																	(byte) 0xdf, (byte) 0xdb, (byte) 0xfb, (byte) 0xf3,
																																																	(byte) 0xf3, (byte) 0xe3, (byte) 0xe3 },
																																																	{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																																																		(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																		(byte) 0xc3, (byte) 0xe7, 0x7e },
																																																		{ 0x00, 0x00, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																																			(byte) 0xc0, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3,
																																																			(byte) 0xc3, (byte) 0xc7, (byte) 0xfe },
																																																			{ 0x00, 0x00, 0x3f, 0x6e, (byte) 0xdf, (byte) 0xdb, (byte) 0xc3,
																																																				(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, 0x66,
																																																				0x3c },
																																																				{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc6, (byte) 0xcc, (byte) 0xd8,
																																																					(byte) 0xf0, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3,
																																																					(byte) 0xc3, (byte) 0xc7, (byte) 0xfe },
																																																					{ 0x00, 0x00, 0x7e, (byte) 0xe7, 0x03, 0x03, 0x07, 0x7e,
																																																						(byte) 0xe0, (byte) 0xc0, (byte) 0xc0, (byte) 0xe7, 0x7e },
																																																						{ 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18,
																																																							0x18, (byte) 0xff },
																																																							{ 0x00, 0x00, 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3,
																																																								(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																								(byte) 0xc3, (byte) 0xc3, (byte) 0xc3 },
																																																								{ 0x00, 0x00, 0x18, 0x3c, 0x3c, 0x66, 0x66, (byte) 0xc3,
																																																									(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																									(byte) 0xc3 },
																																																									{ 0x00, 0x00, (byte) 0xc3, (byte) 0xe7, (byte) 0xff, (byte) 0xff,
																																																										(byte) 0xdb, (byte) 0xdb, (byte) 0xc3, (byte) 0xc3,
																																																										(byte) 0xc3, (byte) 0xc3, (byte) 0xc3 },
																																																										{ 0x00, 0x00, (byte) 0xc3, 0x66, 0x66, 0x3c, 0x3c, 0x18, 0x3c,
																																																											0x3c, 0x66, 0x66, (byte) 0xc3 },
																																																											{ 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x3c, 0x66,
																																																												0x66, (byte) 0xc3 },
																																																												{ 0x00, 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, 0x60, 0x30,
																																																													0x7e, 0x0c, 0x06, 0x03, 0x03, (byte) 0xff },
																																																													{ 0x00, 0x00, 0x3c, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
																																																														0x30, 0x3c },
																																																														{ 0x00, 0x03, 0x03, 0x06, 0x06, 0x0c, 0x0c, 0x18, 0x18, 0x30, 0x30,
																																																															0x60, 0x60 },
																																																															{ 0x00, 0x00, 0x3c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c,
																																																																0x0c, 0x3c },
																																																																{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
																																																																	(byte) 0xc3, 0x66, 0x3c, 0x18 },
																																																																	{ (byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
																																																																		0x00, 0x00, 0x00, 0x00, 0x00 },
																																																																		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x38,
																																																																			0x30, 0x70 },
																																																																			{ 0x00, 0x00, 0x7f, (byte) 0xc3, (byte) 0xc3, 0x7f, 0x03,
																																																																				(byte) 0xc3, 0x7e, 0x00, 0x00, 0x00, 0x00 },
																																																																				{ 0x00, 0x00, (byte) 0xfe, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																																					(byte) 0xc3, (byte) 0xfe, (byte) 0xc0, (byte) 0xc0,
																																																																					(byte) 0xc0, (byte) 0xc0, (byte) 0xc0 },
																																																																					{ 0x00, 0x00, 0x7e, (byte) 0xc3, (byte) 0xc0, (byte) 0xc0,
																																																																						(byte) 0xc0, (byte) 0xc3, 0x7e, 0x00, 0x00, 0x00, 0x00 },
																																																																						{ 0x00, 0x00, 0x7f, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																																							(byte) 0xc3, 0x7f, 0x03, 0x03, 0x03, 0x03, 0x03 },
																																																																							{ 0x00, 0x00, 0x7f, (byte) 0xc0, (byte) 0xc0, (byte) 0xfe,
																																																																								(byte) 0xc3, (byte) 0xc3, 0x7e, 0x00, 0x00, 0x00, 0x00 },
																																																																								{ 0x00, 0x00, 0x30, 0x30, 0x30, 0x30, 0x30, (byte) 0xfc, 0x30,
																																																																									0x30, 0x30, 0x33, 0x1e },
																																																																									{ 0x7e, (byte) 0xc3, 0x03, 0x03, 0x7f, (byte) 0xc3, (byte) 0xc3,
																																																																										(byte) 0xc3, 0x7e, 0x00, 0x00, 0x00, 0x00 },
																																																																										{ 0x00, 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																																											(byte) 0xc3, (byte) 0xc3, (byte) 0xfe, (byte) 0xc0,
																																																																											(byte) 0xc0, (byte) 0xc0, (byte) 0xc0 },
																																																																											{ 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00,
																																																																												0x18, 0x00 },
																																																																												{ 0x38, 0x6c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x00, 0x00,
																																																																													0x0c, 0x00 },
																																																																													{ 0x00, 0x00, (byte) 0xc6, (byte) 0xcc, (byte) 0xf8, (byte) 0xf0,
																																																																														(byte) 0xd8, (byte) 0xcc, (byte) 0xc6, (byte) 0xc0,
																																																																														(byte) 0xc0, (byte) 0xc0, (byte) 0xc0 },
																																																																														{ 0x00, 0x00, 0x7e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18,
																																																																															0x18, 0x78 },
																																																																															{ 0x00, 0x00, (byte) 0xdb, (byte) 0xdb, (byte) 0xdb, (byte) 0xdb,
																																																																																(byte) 0xdb, (byte) 0xdb, (byte) 0xfe, 0x00, 0x00, 0x00,
																																																																																0x00 },
																																																																																{ 0x00, 0x00, (byte) 0xc6, (byte) 0xc6, (byte) 0xc6, (byte) 0xc6,
																																																																																	(byte) 0xc6, (byte) 0xc6, (byte) 0xfc, 0x00, 0x00, 0x00,
																																																																																	0x00 },
																																																																																	{ 0x00, 0x00, 0x7c, (byte) 0xc6, (byte) 0xc6, (byte) 0xc6,
																																																																																		(byte) 0xc6, (byte) 0xc6, 0x7c, 0x00, 0x00, 0x00, 0x00 },
																																																																																		{ (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xfe, (byte) 0xc3,
																																																																																			(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xfe, 0x00,
																																																																																			0x00, 0x00, 0x00 },
																																																																																			{ 0x03, 0x03, 0x03, 0x7f, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3,
																																																																																				(byte) 0xc3, 0x7f, 0x00, 0x00, 0x00, 0x00 },
																																																																																				{ 0x00, 0x00, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
																																																																																					(byte) 0xc0, (byte) 0xe0, (byte) 0xfe, 0x00, 0x00, 0x00,
																																																																																					0x00 },
																																																																																					{ 0x00, 0x00, (byte) 0xfe, 0x03, 0x03, 0x7e, (byte) 0xc0,
																																																																																						(byte) 0xc0, 0x7f, 0x00, 0x00, 0x00, 0x00 },
																																																																																						{ 0x00, 0x00, 0x1c, 0x36, 0x30, 0x30, 0x30, 0x30, (byte) 0xfc,
																																																																																							0x30, 0x30, 0x30, 0x00 },
																																																																																							{ 0x00, 0x00, 0x7e, (byte) 0xc6, (byte) 0xc6, (byte) 0xc6,
																																																																																								(byte) 0xc6, (byte) 0xc6, (byte) 0xc6, 0x00, 0x00, 0x00,
																																																																																								0x00 },
																																																																																								{ 0x00, 0x00, 0x18, 0x3c, 0x3c, 0x66, 0x66, (byte) 0xc3,
																																																																																									(byte) 0xc3, 0x00, 0x00, 0x00, 0x00 },
																																																																																									{ 0x00, 0x00, (byte) 0xc3, (byte) 0xe7, (byte) 0xff, (byte) 0xdb,
																																																																																										(byte) 0xc3, (byte) 0xc3, (byte) 0xc3, 0x00, 0x00, 0x00,
																																																																																										0x00 },
																																																																																										{ 0x00, 0x00, (byte) 0xc3, 0x66, 0x3c, 0x18, 0x3c, 0x66,
																																																																																											(byte) 0xc3, 0x00, 0x00, 0x00, 0x00 },
																																																																																											{ (byte) 0xc0, 0x60, 0x60, 0x30, 0x18, 0x3c, 0x66, 0x66,
																																																																																												(byte) 0xc3, 0x00, 0x00, 0x00, 0x00 },
																																																																																												{ 0x00, 0x00, (byte) 0xff, 0x60, 0x30, 0x18, 0x0c, 0x06,
																																																																																													(byte) 0xff, 0x00, 0x00, 0x00, 0x00 },
																																																																																													{ 0x00, 0x00, 0x0f, 0x18, 0x18, 0x18, 0x38, (byte) 0xf0, 0x38,
																																																																																														0x18, 0x18, 0x18, 0x0f },
																																																																																														{ 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18,
																																																																																															0x18, 0x18 },
																																																																																															{ 0x00, 0x00, (byte) 0xf0, 0x18, 0x18, 0x18, 0x1c, 0x0f, 0x1c,
																																																																																																0x18, 0x18, 0x18, (byte) 0xf0 },
																																																																																																{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, (byte) 0x8f,
																																																																																																	(byte) 0xf1, 0x60, 0x00, 0x00, 0x00 } };

}
