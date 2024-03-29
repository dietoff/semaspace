/**
 * 
 */
package data;

public final class Vector3D {
	public float x;

	public float y;

	public float z;

	public Vector3D(float x_, float y_, float z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	Vector3D(float x_, float y_) {
		x = x_;
		y = y_;
		z = 0f;
	}

	public Vector3D() {
		x = 0f;
		y = 0f;
		z = 0f;
	}

	public void setX(float x_) {
		x = x_;
	}

	public void setY(float y_) {
		y = y_;
	}

	public void setZ(float z_) {
		z = z_;
	}

	public void setXY(float x_, float y_) {
		x = x_;
		y = y_;
	}

	public void setXYZ(float x_, float y_, float z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	public void setXYZ(Vector3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public float magnitudesq() {
		return (float) x * x + y * y + z * z;
	}

	public Vector3D copy() {
		return new Vector3D(x, y, z);
	}

	public Vector3D copy(Vector3D v) {
		return new Vector3D(v.x, v.y, v.z);
	}

	public void add(Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public void sub(Vector3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	public Vector3D mult(float n) {
		x *= n;
		y *= n;
		z *= n;
		return this;
	}

	public void div(float n) {
		x /= n;
		y /= n;
		z /= n;
	}

	public void normalize() {
		float m = magnitude();
		if (m > 0) {
			div(m);
		}
	}

	public void limit(float max) {
		if (magnitude() > max) {
			normalize();
			mult(max);
		}
	}

	public float heading2D() {
		float angle = (float) Math.atan2(-y, x);
		return -1 * angle;
	}

	public static Vector3D add(Vector3D v1, Vector3D v2) {
		Vector3D v = new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
		return v;
	}

	public static Vector3D sub(Vector3D v1, Vector3D v2) {
		Vector3D v = new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
		return v;
	}

	public static Vector3D div(Vector3D v1, float n) {
		Vector3D v = new Vector3D(v1.x / n, v1.y / n, v1.z / n);
		return v;
	}

	public static Vector3D mult(Vector3D v1, float n) {
		Vector3D v = new Vector3D(v1.x * n, v1.y * n, v1.z * n);
		return v;
	}

	public static float distance(Vector3D v1, Vector3D v2) {
		float dx = v1.x - v2.x;
		float dy = v1.y - v2.y;
		float dz = v1.z - v2.z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public static Vector3D cross(Vector3D v1, Vector3D v2){
		Vector3D v = new Vector3D(v1.y*v2.z-v1.z*v2.y,v1.x*v2.z-v1.z*v2.x,v1.x*v2.y-v1.y*v2.x);
		return v;
	}
	public static Vector3D midPoint(Vector3D v1, Vector3D v2) {
		Vector3D midP = v2.copy();
		midP.sub(v1); //direction of the edge
		midP.mult(0.5f);
		midP.add(v1);
		return midP;
	}
}
