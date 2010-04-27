import processing.core.PApplet;

import java.io.Reader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Graph {
   protected Set<Vertex> vertices = new HashSet<Vertex>();

   public static Graph fromFile(File file) throws IOException {
      return fromReader(new FileReader(file));
   }

   public static Graph fromStream(InputStream stream) throws IOException {
      return fromReader(new InputStreamReader(stream));
   }

   private static Graph fromReader(Reader reader) throws IOException {
      BufferedReader bufReader = new BufferedReader(reader);

      // need an ordered list of new vertices
      List<Vertex> newVerts = new ArrayList<Vertex>();

      // parse file
      String line;
      while ((line = bufReader.readLine()) != null) {
         // parse line
         StringTokenizer tokenizer = new StringTokenizer(line);
         if (!tokenizer.hasMoreTokens())
            continue;

         int v1 = Integer.parseInt(tokenizer.nextToken());
         int v2 = Integer.parseInt(tokenizer.nextToken());

         // add vertices to list if necessary
         while (v1 > newVerts.size())
            newVerts.add(new Vertex());
         while (v2 > newVerts.size())
            newVerts.add(new Vertex());

         // add edge
         newVerts.get(v1-1).addEdge(newVerts.get(v2-1));
      }

      // add all verts to a new Graph and return it
      Graph g = new Graph();
      for (Vertex v : newVerts)
         g.vertices.add(v);
      return g;
   }

   public boolean addVertex(Vector pos) {
      return vertices.add(new Vertex(pos));
   }

   public boolean addEdge(Vertex v1, Vertex v2) {
      return v1.addEdge(v2);
   }

   public boolean remove(Vertex v) {
      return vertices.remove(v);
   }

   public int size() {
      return vertices.size();
   }

   public void draw(PApplet g, float bound) {
      float scale = 1;
      for (Vertex v : vertices)
         scale = Math.max(scale, v.pos.mag());
      scale = bound / scale;

      g.noSmooth();
      g.noStroke();
      for (Vertex v : vertices) {
         g.translate(v.pos.x * scale, v.pos.y * scale, v.pos.z * scale);
if (v.acc.mag() > 100 || v.vel.mag() > 100)
   g.fill(255, 0, 0);
else
   g.fill(0, 64, 128);
         g.sphere(1);
         g.translate(-v.pos.x * scale, -v.pos.y * scale, -v.pos.z * scale);
      }

      g.smooth();
      g.stroke(1);
      for (Vertex v : vertices)
         for (Vertex n : v.edges)
            if (v.hashCode() < n.hashCode() && vertices.contains(n))
               g.line(v.pos.x * scale, v.pos.y * scale, v.pos.z * scale,
                      n.pos.x * scale, n.pos.y * scale, n.pos.z * scale);
   }
}