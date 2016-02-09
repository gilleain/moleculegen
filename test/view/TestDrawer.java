package view;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import augment.atom.AtomGenerator;
import handler.molecule.PrintStreamHandler;
import view.draw.DrawNode;
import view.draw.Drawer;
import view.tree.DrawingTreeWalker;
import view.tree.TreeBuilder;
import view.tree.layout.TreeLayout;

public class TestDrawer {
    
    @Test
    public void testC4H11N() {
        drawTree(getTree("C2NO", true));
    }
    
    private DrawNode getTree(String formula, boolean canonicalOnly) {
        TreeBuilder builder = new TreeBuilder(canonicalOnly);
        AtomGenerator generator = new AtomGenerator(formula, new PrintStreamHandler(System.out));
        generator.setCanonicalHandler(builder.getHandler());
        generator.run();
        
        DrawingTreeWalker walker = new DrawingTreeWalker();
        builder.walkTree(walker);
        return walker.getRoot();
    }
    
    private void drawTree(DrawNode root) {
        Drawer drawer = new Drawer();
//        int w = 1200;
//        int h = 400;
        double boxW = 50;
        double boxH = 50;
        Rectangle2D bounds = 
                new TreeLayout(root.getHeight()).layoutTree(root, boxW, boxH);
        int w = (int)bounds.getWidth();
        int h = (int)bounds.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        drawer.paint(image.getGraphics(), root, boxW, boxH, w, h);
        try {
            ImageIO.write((RenderedImage)image, "PNG", new FileOutputStream("test.png"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

}
