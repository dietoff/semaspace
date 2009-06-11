package data;

import java.util.HashSet;

public class BBox3D {
	public Vector3D min = new Vector3D();
	public Vector3D max= new Vector3D();
	public Vector3D size = new Vector3D();
	public Vector3D center = new Vector3D();
	BBox3D(Vector3D min_, Vector3D max_) {
		min = min_;
		max = max_;
		size.setXYZ(max);
		size.sub(min);
	}

	public BBox3D() {
		min.setXYZ(0f,0f,0f);
		max.setXYZ(0f,0f,0f);
		size.setXYZ(0f,0f,0f);
		center.setXYZ(0f,0f,0f);
	}
	
	public static BBox3D calcBounds(HashSet<Node> net) {
		BBox3D bounds = new BBox3D();
		float maxValue = Float.MAX_VALUE;
		bounds.max.setXYZ(-maxValue,-maxValue,-maxValue);
		bounds.min.setXYZ(maxValue,maxValue,maxValue);
		bounds.size.setXYZ(0,0,0);
		for (Node nodeRef: net) {
			bounds.max.setX(Math.max(nodeRef.pos.x, bounds.max.x));
			bounds.max.setY(Math.max(nodeRef.pos.y, bounds.max.y));
			bounds.max.setZ(Math.max(nodeRef.pos.z, bounds.max.z));
			bounds.min.setX(Math.min(nodeRef.pos.x, bounds.min.x));
			bounds.min.setY(Math.min(nodeRef.pos.y, bounds.min.y));
			bounds.min.setZ(Math.min(nodeRef.pos.z, bounds.min.z));
		}
		bounds.size.setXYZ(bounds.max);
		bounds.size.sub(bounds.min);
		bounds.center.setXYZ(bounds.max);
		bounds.center.add(bounds.min);
		bounds.center.div(2f);
		return bounds;
	}
}
