import java.util.Set;
import java.util.HashSet;

public class Vertex {
   public Vector pos;
   public Vector vel;
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
      this.pos = pos;
      this.vel = vel;
      this.edges = edges;
   }

   public boolean addEdge(Vertex v) {
      return edges.add(v) && v.edges.add(this);
   }
}
