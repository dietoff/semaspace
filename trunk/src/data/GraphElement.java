package data;

import java.util.HashMap;
import semaGL.*;


public abstract class GraphElement {
	public int id; 
	public float[] defaultcolor ={.5f,.5f,.5f,0.7f}; //free
	public float[] color ={.5f,.5f,.5f,0.7f}; //free
	public float[] textColor ={0f,0f,0f,0.8f};
	public float[] color2 = null;
	public float[] white ={1f,1f,1f,0.8f};
	private boolean frame = false;
	public float alpha=0.8f;
	public String name;
	public String altName;
	SemaSpace app;
	public HashMap<String,String> attributes;
	public boolean rollover;
	public boolean colored = false;

	GraphElement() {
		this(null,"");
	}
	GraphElement(SemaSpace app_) {
		this(app_,"");
	}
	GraphElement(SemaSpace app_, String name_) {
		app = app_;
		setName(name_);
		setAltName(name_); //by default altname = name
		attributes = new HashMap<String, String>();		
	}

	public void genColorFromAtt(){
		float[] col=defaultcolor.clone();
		col[3]=alpha;

		if (app.getAttribute().contentEquals("none")) {
			color = col;
			colored = false;
			return;
		}
		if (attributes!=null){
			String a = attributes.get(app.getAttribute());
			if (a!=null) {
				color = colorFunction(attributes.get(app.getAttribute()));
				colored = true;
				alpha = 0.7f;
			} else {
				color = col;
				colored = false;
			}
		}  else {
			color = col;
			colored = false;
		}
		return;
	}
	public static float[] colorFunction(String param) {
		return Func.parseColorInt(String.valueOf(param.hashCode()*726.12344381f).hashCode());
	}


	public String genTextSelAttributes() {
		String id=name;
		if (altName!=null)  id = altName;
		String a = attributes.get(app.getAttribute());
		String disp;
		if (id.length()>30) disp = id.substring(0,29)+"..."; else disp = id;
		if (a==null) return disp;
		if (a!=null&&!id.contentEquals(a)) {
			disp +="\n"+a;
		} 
		return disp;
	}

	public String genTextAttributeList(){
		String content = attributes.toString();
		content = content.substring(1, content.length()-1);
		String result = content.replaceAll(", ", "\n");
		return result;
	}

	void genId() {
		id = name.hashCode();
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
		genId();
	}
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public float[] getNodeColor() {
		return color;
	}
	public void setColor(float[] nodeColor2) {
		this.color = nodeColor2;
	}

	public void setColor2(float[] nodeColor_) {
		this.color2 = nodeColor_;
	}

	public void setAlpha(float alpha_) {
		alpha=alpha_;
		color[3]= alpha;
	}
	public String getAltName() {
		return altName;
	}
	public void setAltName(String altName) {
		this.altName = altName;
	}

	public void setRollover(boolean b) {
		rollover = b;
	}
	
	public boolean isFrame() {
		return frame;
	}

	public void setFrame(boolean b) {
		frame  = b;
	}
}
