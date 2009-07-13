package data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import semaGL.*;


public abstract class GraphElement {
	public int id; 
	public float[] defaultcolor ={.5f,.5f,.5f,0.7f}; //free
	private float[] color ={.5f,.5f,.5f,0.7f}; //free
	public float[] textColor ={0f,0f,0f,0.8f};
	private float[] defaultcolor2 = null;
	public float[] white ={1f,1f,1f,0.8f};
	private boolean frame = false;
	public float alpha=0.8f;
	public String name;
	public String altName;
	SemaParameters app;
	public HashMap<String,String> attributes;
	public boolean rollover;
	public boolean colored = false;
	public boolean fade = false;
	
	// timestamps in years for now
	int timeStart = 0;
	int timeEnd = Integer.MAX_VALUE;
	
	
	GraphElement(SemaParameters app2) {
		this(app2,"");
	}
	GraphElement(SemaParameters app2, String name_) {
		app = app2;
		setName(name_);
		setAltName(name_); //by default altname = name
		attributes = new HashMap<String, String>();		
	}

	public void genColorFromAtt(){
		float[] col=defaultcolor.clone();
		col[3]=Math.min(1,Math.max(0,alpha));

		if (app.getAttribute().contentEquals("none")) {
			setColor(col);
			colored = false;
			return;
		}
		if (attributes!=null){
			String a = attributes.get(app.getAttribute());
			if (a!=null) {
				setColor(colorFunction(attributes.get(app.getAttribute())));
				colored = true;
				alpha = 0.7f;
			} else {
				setColor(col);
				colored = false;
			}
		}  else {
			setColor(col);
			colored = false;
		}
		return;
	}
	public static float[] colorFunction(String param) {
		Random generator = new Random(param.hashCode());
		int col = generator.nextInt(0x777777);
		return Func.parseColorInt(col);
	}


	public String genTextSelAttributes() {
		String id=name;
		if (altName!=null)  id = altName;
		String a = attributes.get(app.getAttribute());
		String disp="";
		if (id.length()>50) disp = id.substring(0,49)+"..."; else disp = id;
		if (a==null) return disp;
		if (a!=null&&!id.contentEquals(a)) {
			disp +="\n"+a;
		} 
		return disp;
	}

	public String genTextAttributeList(){
		String content = attributes.toString();
		String result ="";
		for (Entry<String, String> c:attributes.entrySet()) {
			result += c.getKey()+"="+c.getValue()+"\n";
		}
		content = content.substring(1, content.length()-1);
		return result;
	}

	public int genId() {
		id = name.hashCode();
		return id;
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
	
	public String removeAttribute(String key) {
		return attributes.remove(key);
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public float[] getNodeColor() {
		return getColor();
	}
	public void setColor(float[] col) {
		this.color = col;
	}

	public void setAssignedColor2(float[] col) {
		this.defaultcolor2 = col;
	}
	
	public void setAssignedColor(float[] col) {
		this.defaultcolor = col;
	}

	public void setAlpha(float alpha_) {
		alpha=alpha_;
		getColor()[3]= alpha;
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
	public float[] getColor() {
		return color;
	}
	public float[] getColor2() {
		return defaultcolor2;
	}
}
