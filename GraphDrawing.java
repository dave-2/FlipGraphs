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

   private final float camDist = 200;
   private final Vector camUp = new Vector(0, 0, 1);
   private float camTheta = 0, camPhi = (float)Math.PI / 2;

   private ForceGraph graph;
   private List<String> graphs;
   private int mouseDownX, mouseDownY;
   private int prevMouseX, prevMouseY;

   private ControlP5 controlP5;
   private final int margin = 10;
   private final int padding = 5;
   private final int itemHeight = 15;
   private final int controlWidth = 120;

   private ListBox graphList;
   private Button pauseButton;
   private CheckBox showVelBox;
   private CheckBox showAccBox;

   public int zoom = 100;

   public float force = 1;
   public float damping = 1;
   public float growthRate = 1;
   public int speed = 10;

   private boolean paused = false;

   private boolean showVel = false;
   private boolean showAcc = false;

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

      slider = controlP5.addSlider("speed", 1, 50, speed, margin, 400 + itemHeight + padding, controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("growthRate", 0, 5, growthRate, margin, 400 + 2 * (itemHeight + padding), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("force", 0, 10, force, margin, 400 + 3 * (itemHeight + padding), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      slider = controlP5.addSlider("damping", 0, 10, damping, margin, 400 + 4 * (itemHeight + padding), controlWidth, itemHeight);
      slider.captionLabel().setColor(0);

      // create buttons
      pauseButton = controlP5.addButton("pause", 0, margin, 400 + 5 * (itemHeight + padding) + margin, controlWidth, itemHeight);

      // create checkboxes
      showVelBox = controlP5.addCheckBox("showVelBox", margin, 400 + 6 * (itemHeight + padding) + 2 * margin);
      showVelBox.addItem("show velocity", 0);
      showVelBox.setColorLabel(0);

      showAccBox = controlP5.addCheckBox("showAccBox", margin, 400 + 7 * (itemHeight + padding) + 2 * margin);
      showAccBox.addItem("show acceleration", 0);
      showAccBox.setColorLabel(0);
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
         if (!paused) {
            for (int i = 0; i < speed; ++i) {
               graph.move(1/frameRate, force, damping);

               if (graph.canGrow() && graph.getMaxForce() < growthRate)
                  graph.grow();
            }
         }

         graph.draw(this, zoom, showVel, showAcc);
      }

      camera();
      controlP5.draw();
   }

   public void controlEvent(ControlEvent event) {
      if (event.isGroup()) {
         if (event.group() == graphList)
            loadGraph(graphs.get((int)graphList.value()));
         else if (event.group() == showVelBox)
            showVel =  showVelBox.arrayValue()[0] > 0;
         else if (event.group() == showAccBox)
            showAcc =  showAccBox.arrayValue()[0] > 0;
      }
   }

   public void pause() {
      paused = !paused;
      if (paused)
         pauseButton.setCaptionLabel("Play");
      else
         pauseButton.setCaptionLabel("Pause");
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
