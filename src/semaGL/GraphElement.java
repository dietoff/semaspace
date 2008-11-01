package semaGL;

import java.util.HashMap;

import javax.media.opengl.GL;



public abstract class GraphElement {
	int id; 
	float[] defaultcolor ={.5f,.5f,.5f,0.7f}; //free
	float[] color ={.5f,.5f,.5f,0.7f}; //free
	float[] textColor ={0f,0f,0f,0.8f};
	float[] color2 = null;
	float[] white ={1f,1f,1f,0.8f};
	float alpha=0.8f;
	String name;
	String altName;
	SemaSpace app;
	HashMap<String,String> attributes;
	boolean rollover;
	protected boolean colored = false;

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

	protected void genColorFromAtt(){
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
				color = Func.parseColorInt(String.valueOf(attributes.get(app.getAttribute()).hashCode()*726.12344381f).hashCode());
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


	protected String genTextSelAttributes() {
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

	String genTextAttributeList(){
		String content = attributes.toString();
		content = content.substring(1, content.length()-1);
		String result = content.replaceAll(", ", "\n");
		return result;
	}

	void genId() {
		id = name.hashCode();
	}
	int getId() {
		return id;
	}
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
		genId();
	}
	boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	void setAttribute(String key, String value) {
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
	void render(GL gl) {
		// TODO Auto-generated method stub
		return;
	}
}
