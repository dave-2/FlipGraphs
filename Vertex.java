import java.util.Set;
import java.util.HashSet;

public class Vertex {
   public Vector pos, vel, acc;
   public Set<Vertex> edges;

   public Vertex() {
      this(new Vector());
   }

   public Vertex(Vector pos) {
      this(pos, new HashSet<Vertex>());
   }

   public Vertex(Vector pos, Set<Vertex> edges) {
      this(pos, new Vector(), edges);
   }

   public Vertex(Vector pos, Vector vel, Set<Vertex> edges) {
      this(pos, vel, new Vector(), edges);
   }

   public Vertex(Vector pos, Vector vel, Vector acc, Set<Vertex> edges) {
      this.pos = pos;
      this.vel = vel;
      this.acc = acc;
      this.edges = edges;
   }

   public boolean addEdge(Vertex v) {
      return edges.add(v) && v.edges.add(this);
   }
}
