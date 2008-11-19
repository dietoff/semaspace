package data;

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
}
