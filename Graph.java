import processing.core.PApplet;
import javax.media.opengl.*;
import com.sun.opengl.util.*;
import processing.opengl.*;

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
      PGraphicsOpenGL pgl = (PGraphicsOpenGL)g.g;
      GL gl = pgl.beginGL();

      gl.glEnable(gl.GL_LINE_SMOOTH);
      gl.glEnable(gl.GL_BLEND);
      gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
      gl.glHint(gl.GL_LINE_SMOOTH_HINT, gl.GL_NICEST);
      gl.glLineWidth(1.5f);

      GLUT glut = new GLUT();

      float scale = 1;
      for (Vertex v : vertices)
         scale = Math.max(scale, v.pos.mag());
      scale = bound / scale;

      gl.glColor3f(0, 0.25f, 0.5f);
      for (Vertex v : vertices) {
         gl.glTranslatef(v.pos.x * scale, v.pos.y * scale, v.pos.z * scale);
         glut.glutSolidIcosahedron();
         gl.glTranslatef(-v.pos.x * scale, -v.pos.y * scale, -v.pos.z * scale);
      }

      gl.glBegin(gl.GL_LINES);
      gl.glColor3f(0.5f, 0.5f, 0.5f);
      for (Vertex v : vertices) {
         for (Vertex n : v.edges) {
            if (v.hashCode() < n.hashCode() && vertices.contains(n)) {
               gl.glVertex3f(v.pos.x * scale, v.pos.y * scale, v.pos.z * scale);
               gl.glVertex3f(n.pos.x * scale, n.pos.y * scale, n.pos.z * scale);
            }
         }
      }
      gl.glEnd();

      pgl.endGL();
   }
}
