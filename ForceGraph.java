import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class ForceGraph extends Graph {
   private Graph baseGraph;

   public static ForceGraph fromFile(File file) throws IOException {
      ForceGraph g = new ForceGraph();
      g.baseGraph = Graph.fromFile(file);
      return g;
   }

   public static ForceGraph fromStream(InputStream stream) throws IOException {
      ForceGraph g = new ForceGraph();
      g.baseGraph = Graph.fromStream(stream);
      return g;
   }

   public float getMaxForce() {
      float force = 0;
      for (Vertex v : vertices)
         force = Math.max(force, v.acc.mag());

      return force;
   }

   public boolean canGrow() {
      return baseGraph.vertices.size() > 0;
   }

   public void grow() {
      // add first vertex
      if (vertices.size() == 0) {
         // find highest degree vertex
         Vertex maxVertex = null;
         int maxDegree = 0;
         for (Vertex v : baseGraph.vertices) {
            maxVertex = v;
            maxDegree = v.edges.size();
            break;
         }

         for (Vertex v : baseGraph.vertices) {
            if (v.edges.size() > maxDegree) {
               maxVertex = v;
               maxDegree = v.edges.size();
            }
         }

         baseGraph.remove(maxVertex);
         vertices.add(maxVertex);
         return;
      }

      // find vertex with highest degree
      // with respect to nodes already added
      Vertex maxVertex = null;
      int maxDegree = 0;
      for (Vertex v : baseGraph.vertices) {
         maxVertex = v;
         for (Vertex n : v.edges)
            if (vertices.contains(n))
               ++maxDegree;
         break;
      }

      for (Vertex v : baseGraph.vertices) {
         int degree = 0;
         for (Vertex n : v.edges)
            if (vertices.contains(n))
               ++degree;

         if (degree > maxDegree) {
            maxVertex = v;
            maxDegree = degree;
         }
      }

      baseGraph.remove(maxVertex);
      vertices.add(maxVertex);

      // if degree is only 1 then add new vertex
      // 1 unit distance away from its neighbor
      if (maxDegree == 1) {
         for (Vertex n : maxVertex.edges) {
            if (vertices.contains(n)) {
               if (n.pos.mag() == 0)
                  maxVertex.pos = new Vector(0, 0, 1);
               else
                  maxVertex.pos = n.pos.add(n.pos.normalize());
            }
         }
      }
      // otherwise, add new vertex at the average position of its neighbors
      else if (maxDegree > 1) {
         for (Vertex n : maxVertex.edges)
            if (vertices.contains(n))
               maxVertex.pos = maxVertex.pos.add(n.pos);
         maxVertex.pos = maxVertex.pos.div(maxDegree);
      }

      // jitter points randomly to put them in general position
      float rho = 0.1f;
      float theta = (float)Math.random() * (float)Math.PI * 2;
      float phi = (float)Math.random() * (float)Math.PI;
      Vector random = Vector.sphereToRect(rho, theta, phi);
      maxVertex.pos = maxVertex.pos.add(random);
   }

   public void move(float timestep, float force, float damping) {
      // velocity verlet integration, part 1
      for (Vertex v : vertices) {
         v.pos = v.pos.add(v.vel.mult(timestep)).add(v.acc.mult(timestep*timestep/2));
         v.vel = v.vel.add(v.acc.mult(timestep/2));
      }

      // calculate forces
      for (Vertex v : vertices) {
         v.acc = new Vector();

         for (Vertex n : vertices) {
            if (v == n)
               continue;

            Vector diff = n.pos.sub(v.pos);
            if (v.edges.contains(n)) {
               // adjacent vertices are attached with a spring of length 1
               Vector magicDiff = diff.sub(diff.normalize());
               v.acc = v.acc.add(magicDiff.mult(force));
            }
            else {
               // repel nonadjacent vertices
               float dist = diff.mag();
               Vector magicDiff = diff.normalize().div(dist * dist);
               v.acc = v.acc.sub(magicDiff.mult(force));
            }
         }

         // damping
         v.acc = v.acc.sub(v.vel.mult(damping));
      }

      // velocity verlet integration, part 2
      for (Vertex v : vertices)
         v.vel = v.vel.add(v.acc.mult(timestep/2));

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
   }
}
