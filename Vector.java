public class Vector {
   public float x, y, z;

   public Vector() {
      this(0, 0, 0);
   }

   public Vector(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public float mag() {
      return (float)Math.sqrt(x*x + y*y + z*z);
   }

   public Vector normalize() {
      float magnitude = (float)Math.sqrt(x*x + y*y + z*z);
      return new Vector(x / magnitude, y / magnitude, z / magnitude);
   }

   public Vector mult(float rhs) {
      return new Vector(x * rhs, y * rhs, z * rhs);
   }

   public Vector div(float rhs) {
      return new Vector(x / rhs, y / rhs, z / rhs);
   }

   public Vector add(Vector rhs) {
      return new Vector(x + rhs.x, y + rhs.y, z + rhs.z);
   }

   public Vector sub(Vector rhs) {
      return new Vector(x - rhs.x, y - rhs.y, z - rhs.z);
   }

   public Vector cross(Vector rhs) {
      return new Vector(y*rhs.z - z*rhs.y, z*rhs.x - x*rhs.z, x*rhs.y - y*rhs.x);
   }

   public static Vector sphereToRect(float rho, float theta, float phi) {
	   float xyRho = rho * (float)Math.sin(phi);
	   return new Vector(xyRho * (float)Math.sin(theta), xyRho * (float)Math.cos(theta), rho * (float)Math.cos(phi));
   }

   public static Vector min(Vector v1, Vector v2) {
      return new Vector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
   }

   public static Vector max(Vector v1, Vector v2) {
      return new Vector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
   }

   @Override
   public String toString() {
      return "(" + x + ", " + y + ", " + z + ")";
   }
}
