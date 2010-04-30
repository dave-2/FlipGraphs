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

   public int zoom = 100;
   private final float camDist = 200;
   private final Vector camUp = new Vector(0, 0, 1);
   private float camTheta = 0, camPhi = (float)Math.PI / 2;

   public float force = 1;
   public float damping = 1;
   public float growthRate = 1;
   public int speed = 10;

   private ForceGraph graph;
   private List<String> graphs;
   private int mouseDownX, mouseDownY;
   private int prevMouseX, prevMouseY;

   private ControlP5 controlP5;
   private final int margin = 10;
   private final int itemHeight = 15;
   private final int controlWidth = 120;

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

      // create list of graphs
      graphList = controlP5.addListBox("Graph List", margin, margin+itemHeight, controlWidth, height-margin);
      graphList.setItemHeight(itemHeight);
      graphList.setBarHeight(itemHeight);

      graphList.captionLabel().style().marginTop = 3;
      graphList.valueLabel().style().marginTop = 3; // the +/- sign

      for (int i = 0; i < graphs.size(); ++i)
         graphList.addItem(graphs.get(i), i);

      // create sliders
      Slider slider;

      slider = controlP5.addSlider("zoom", 10, 200, zoom, margin, 400, controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("speed", 1, 50, speed, margin, 400 + itemHeight + margin, controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("growthRate", 0, 5, growthRate, margin, 400 + 2 * (itemHeight + margin), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("force", 0, 10, force, margin, 400 + 3 * (itemHeight + margin), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("damping", 0, 10, damping, margin, 400 + 4 * (itemHeight + margin), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);
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
         for (int i = 0; i < speed; ++i) {
            graph.move(1/frameRate, force, damping);

            if (graph.canGrow() && graph.getMaxForce() < growthRate)
               graph.grow();
         }

         graph.draw(this, zoom);
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
      mouseDownX = mouseX;
      mouseDownY = mouseY;
      prevMouseX = mouseX;
      prevMouseY = mouseY;
   }

   @Override
   public void mouseDragged() {
      if (mouseDownX > 2 * margin + controlWidth) {
         camTheta += map(mouseX-prevMouseX, 0, width, 0, TWO_PI * 1.5f);
         camPhi += map(mouseY-prevMouseY, 0, height, 0, PI);

         if (camPhi < EPSILON)
            camPhi = EPSILON;
         if (camPhi > PI - EPSILON)
            camPhi = PI - EPSILON;
      }

      prevMouseX = mouseX;
      prevMouseY = mouseY;
   }

   public static void main(String args[]) {
      PApplet.main(new String[] {"GraphDrawing"});
   }
}
