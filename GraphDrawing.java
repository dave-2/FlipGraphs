import processing.core.*;
import processing.opengl.*;
import controlP5.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.net.URISyntaxException;

public class GraphDrawing extends PApplet {
   private final String graphDir = "graphs/";
   private final String extension = ".graph";

   private final float growThreshold = 5;
   private final float force = 1;
   private final float damping = 1;
   private final float iterations = 10;

   private final float scale = 100;
   private final float camDist = 2 * scale;
   private final Vector camUp = new Vector(0, 0, 1);
   private float camTheta = 0, camPhi = (float)Math.PI / 2;

   private ForceGraph graph;
   private List<String> graphs;
   private int prevMouseX, prevMouseY;

   private final int listMargin = 10;
   private final int listItemHeight = 15;
   private final int listWidth = 120;
   private ControlP5 controlP5;
   private ListBox graphList;

   private List<String> loadGraphList() throws IOException, URISyntaxException {
      List<String> graphs = new ArrayList<String>();
      for (String str : Resource.getResourceListing(getClass(), graphDir))
         if (str.endsWith(extension))
            graphs.add(str.substring(0, str.lastIndexOf('.')));

      Collections.sort(graphs);
      return graphs;
   }

   private void loadGraph(String name) {
      try {
         graph = ForceGraph.fromStream(getClass().getResourceAsStream(graphDir + name + extension));
      }
      catch (Exception e) {
         System.out.println("Unable to load graph " + name + extension + "!");
         e.printStackTrace();
         System.exit(-1);
      }
   }

   private void setupGUI(List<String> graphs) {
      controlP5 = new ControlP5(this);

      graphList = controlP5.addListBox("Graph List", listMargin, listMargin+listItemHeight, listWidth, height-listMargin);
      graphList.setItemHeight(listItemHeight);
      graphList.setBarHeight(listItemHeight);

      graphList.captionLabel().style().marginTop = 3;
      graphList.valueLabel().style().marginTop = 3; // the +/- sign

      graphList.setColorBackground(color(0, 64, 128));
      graphList.setColorActive(color(128, 192, 255));

      for (int i = 0; i < graphs.size(); ++i)
         graphList.addItem(graphs.get(i), i);
   }

   @Override
   public void setup() {
      size(800, 600, OPENGL);
      hint(ENABLE_OPENGL_4X_SMOOTH);
      frameRate(20);

      try {
         graphs = loadGraphList();
         setupGUI(graphs);
      }
      catch (Exception e) {
         System.out.println("Unable to read graphs!");
         e.printStackTrace();
         System.exit(-1);
      }
   }

   @Override
   public void draw() {
      background(255);

      Vector cam = Vector.sphereToRect(camDist, camTheta, camPhi);
      camera(cam.x, cam.y, cam.z, 0, 0, 0, camUp.x, camUp.y, camUp.z);

      if (graph != null) {
         if (graph.canGrow() && graph.getMaxForce() < growThreshold)
            graph.grow();

         for (int i = 0; i < iterations; ++i)
            graph.move(1/frameRate, force, damping);

         graph.draw(this, scale);
      }

      camera();
      controlP5.draw();

      System.out.println(frameRate);
   }

   public void controlEvent(ControlEvent event) {
      if (event.isGroup())
         if (event.group() == graphList)
            loadGraph(graphs.get((int)graphList.value()));
   }

   @Override
   public void mouseClicked() {
   }

   @Override
   public void mousePressed() {
      prevMouseX = mouseX;
      prevMouseY = mouseY;
   }

   @Override
   public void mouseDragged() {
      camTheta += map(mouseX-prevMouseX, 0, width, 0, TWO_PI * 1.5f);
      camPhi += map(mouseY-prevMouseY, 0, height, 0, PI);

      if (camPhi < EPSILON)
         camPhi = EPSILON;
      if (camPhi > PI - EPSILON)
         camPhi = PI - EPSILON;

      prevMouseX = mouseX;
      prevMouseY = mouseY;
   }

   public static void main(String args[]) {
      PApplet.main(new String[] {"GraphDrawing"});
   }
}
