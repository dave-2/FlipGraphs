import processing.core.*;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class GraphDrawing extends PApplet {
   private final JFileChooser chooser = new JFileChooser("graphs");

   private final float scale = 50;
   private final float camDist = 3 * scale;
   private final Vector camUp = new Vector(0, 0, 1);

   private float camTheta = 0, camPhi = PI * 0.5f;

   private File file = null;
   private ForceGraph graph;

   private int prevMouseX, prevMouseY;

   private void loadGraph() {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            switch (chooser.showOpenDialog(null)) {
               case JFileChooser.APPROVE_OPTION:
                  file = chooser.getSelectedFile();
                  break;
               case JFileChooser.CANCEL_OPTION:
                  break;
               default:
                  System.exit(-1);
                  break;
            }
         }
      });
   }

   private void setupGraph() {
      try {
         graph = ForceGraph.fromFile(file);

         while (graph.canGrow())
            graph.grow();
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }
   }

   public void setup() {
      size(600, 600, P3D);
      frameRate(60);

      fill(0, 64, 128);

      frustum(-10, 10, -10, 10, 15, 1000);

      loadGraph();
   }

   public void draw() {
      if (file != null) {
         setupGraph();
         file = null;
      }

      Vector cam = Vector.sphereToRect(camDist, camTheta, camPhi);
      camera(cam.x, cam.y, cam.z, 0, 0, 0, camUp.x, camUp.y, camUp.z);

      background(255);

      if (graph != null) {
         graph.move();
         graph.draw(this, scale);
      }
   }

   public void mouseClicked() {
      loadGraph();
   }

   public void mousePressed() {
      prevMouseX = mouseX;
      prevMouseY = mouseY;
   }

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

   static public void main(String args[]) {
      PApplet.main(new String[] {"GraphDrawing"});
   }
}
