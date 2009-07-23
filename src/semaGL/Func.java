package semaGL;

public class Func {

	public static float[] parseColorInt(String attribute) {
		int val = Integer.parseInt(attribute);
		return parseColorInt(val);
	}
	
	public static float[] parseColorInt(String attribute, int radix) {
		int val = Integer.parseInt(attribute, radix);
		return parseColorInt(val);
	}
	
	public static float[] parseColorInt(int attribute) {
		int val = attribute;
		float r = ((val>>16)&0xff)/255f;
		float g = ((val>>8)&0xff)/255f;
		float b = (val&0xff)/255f;
		return new float[]{r,g,b,0.7f};
	}

	static float[] colorGrad(int level, float[] nodeHSV, float[] pickHSV) {
		float grad = level/5f;
		float hue = pickHSV[0]+grad*(nodeHSV[0]-pickHSV[0]);
		float[] result = HSVtoRGB(hue,nodeHSV[1],nodeHSV[2], nodeHSV[3]);
		float[] color = {result[0], result[1], result[2],result[3]};
		return color;
	}
	
	static float[] interpolateRGB(float grad, float[] nodeHSV, float[] pickHSV) {
		float hue = pickHSV[0]+grad*(nodeHSV[0]-pickHSV[0]);
		float sat = pickHSV[1]+grad*(nodeHSV[1]-pickHSV[1]);
		float val = pickHSV[2]+grad*(nodeHSV[2]-pickHSV[2]);
		float alpha = pickHSV[3]+grad*(nodeHSV[3]-pickHSV[3]);
		float[] result = HSVtoRGB(hue,sat,val, alpha);
		float[] color = {result[0], result[1], result[2],result[3]};
		return color;
	}
	
	static float[] unpackColors(int rgb) {
		float [] color ={0,0,0,1};
		float N = 255.0f;

		color[0] = ((rgb>>16) & 0xff)/N;
		color[1] = ((rgb>>8)  & 0xff)/N;
		color[2] = ((rgb)     & 0xff)/N;
//		System.out.println(color[0]);
		return color;
	}
	public static int packColors(float[] col) {
		int N = 256;
		int r = Math.min(Math.round(col[0]*N),N-1);
		int g = Math.min(Math.round(col[1]*N),N-1);
		int b = Math.min(Math.round(col[2]*N),N-1);
		int rgb = ((r&0xff)<<16) | ((g&0xff)<<8) | b&0xff;
		return rgb;
	}
	static float[] RGBtoHSV (float[] col) {
		float R = col[0];
		float G = col[1];
		float B = col[2];
		float A = col[3];
		float[] HSV = new float[4];
		float H = 0, S = 0, V = 0;
		float cMax = 1f;
		float cHi = Math.max(R,Math.max(G,B));	// highest color value
		float cLo = Math.min(R,Math.min(G,B));	// lowest color value
		float cRng = cHi - cLo;				    // color range
		
		// compute value V
		V = cHi / cMax;
		
		// compute saturation S
		if (cHi > 0)
			S =  cRng / cHi;

		// compute hue H
		if (cRng > 0) {	// hue is defined only for color pixels
			float rr =(cHi - R) / cRng;
			float gg =(cHi - G) / cRng;
			float bb =(cHi - B) / cRng;
			float hh;
			if (R == cHi)                      // r is highest color value
				hh = bb - gg;
			else if (G == cHi)                 // g is highest color value
				hh = rr - bb + 2.0f;
			else                               // b is highest color value
				hh = gg - rr + 4.0f;
			if (hh < 0)
				hh= hh + 6;
			H = hh / 6;
		}
		HSV[0] = H; HSV[1] = S; HSV[2] = V; HSV[3]=col[3];
		return HSV;
	}
	
	static float[] HSVtoRGB (float h, float s, float v, float a) {
		// h,s,v in [0,1]
		float rr = 0, gg = 0, bb = 0;
		float hh = (6 * h) % 6;                 
		int   c1 = (int) hh;                     
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;	
		switch (c1) {
			case 0: rr=v; gg=z; bb=x; break;
			case 1: rr=y; gg=v; bb=x; break;
			case 2: rr=x; gg=v; bb=z; break;
			case 3: rr=x; gg=y; bb=v; break;
			case 4: rr=z; gg=x; bb=v; break;
			case 5: rr=v; gg=x; bb=y; break;
		}
		float r = rr;
		float g = gg;
		float b = bb;
		float rgb[] = {r,g,b,a};
		return rgb;
	}
	//generate random number
	public static float rnd(float start, float end){
		double nr;
		nr = Math.random();
		nr *= end-start;
		nr += start;
		return (float)nr;
	}
}

