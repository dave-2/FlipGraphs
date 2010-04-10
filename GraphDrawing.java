import processing.core.*;

//import java.io.File;
//import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class GraphDrawing extends PApplet {
   //private final JFileChooser chooser = new JFileChooser("graphs");

   private final float scale = 50;
   private final float camDist = 3 * scale;
   private final Vector camUp = new Vector(0, 0, 1);

   private float camTheta = 0, camPhi = PI * 0.5f;

   private boolean setupGraph = false;
   private ForceGraph graph;

   private int prevMouseX, prevMouseY;

   private void loadGraph() {
      try {
         graph = ForceGraph.fromStream(getClass().getResourceAsStream("/graphs/stellated_dodecahedron.graph"));
         setupGraph = true;

         /*
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               switch (chooser.showOpenDialog(null)) {
                  case JFileChooser.APPROVE_OPTION:
                     try {
                        File file = chooser.getSelectedFile();
                        graph = ForceGraph.fromFile(file);
                        setupGraph = true;
                     }
                     catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                     }
                     break;
                  case JFileChooser.CANCEL_OPTION:
                     break;
                  default:
                     System.exit(-1);
                     break;
               }
            }
         });
         */
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }
   }

   private void setupGraph() {
      while (graph.canGrow())
         graph.grow();

      setupGraph = false;
   }

   public void setup() {
      size(600, 600, P3D);
      frameRate(60);

      fill(0, 64, 128);

      frustum(-10, 10, -10, 10, 15, 1000);

      loadGraph();
   }

   public void draw() {
      if (setupGraph)
         setupGraph();

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
