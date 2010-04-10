import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ForceGraph extends Graph {
   private Graph baseGraph;

   public static ForceGraph fromFile(File file) throws FileNotFoundException, IOException {
      ForceGraph g = new ForceGraph();
      g.baseGraph = Graph.fromFile(file);
      return g;
   }

   public boolean canGrow() {
      return baseGraph.vertices.size() > 0;
   }

   public void grow() {
      for (Vertex v : baseGraph.vertices) {
         baseGraph.remove(v);
         vertices.add(v);

         v.pos.x = (float)Math.random() - 0.5f;
         v.pos.y = (float)Math.random() - 0.5f;
         v.pos.z = (float)Math.random() - 0.5f;
         break;
      }
   }

   public void move() {
      // calculate forces
      for (Vertex v : vertices) {
         for (Vertex n : v.edges) {
            if (!vertices.contains(n))
               continue;

            Vector diff = n.pos.sub(v.pos);
            Vector dist = diff.sub(diff.normalize());
            v.vel = v.vel.add(dist.mult(0.1f));
         }

         // damping
         v.vel = v.vel.mult(0.75f);
      }

      // numerical integration
      for (Vertex v : vertices)
         v.pos = v.pos.add(v.vel);

      // calculate center of bounding box
      Vector min = null;
      Vector max = null;
      for (Vertex v : vertices) {
         if (min == null) {
            min = v.pos;
            max = v.pos;
            continue;
         }
         min = Vector.min(min, v.pos);
         max = Vector.max(max, v.pos);
      }

      // make center the new origin
      if (min != null) {
         Vector center = min.add(max).div(2);
         for (Vertex v : vertices)
            v.pos = v.pos.sub(center);
      }

      // fudge factor
      for (Vertex v : vertices)
         if (v.pos.mag() > 0)
            v.pos = v.pos.add(v.pos.normalize().mult(0.1f));
   }
}
