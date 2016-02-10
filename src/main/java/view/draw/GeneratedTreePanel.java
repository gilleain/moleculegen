package view.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import view.tree.layout.TreeLayout;

public class GeneratedTreePanel extends JPanel {
    
	private static final long serialVersionUID = 2144237936253828531L;

    private DrawNode root;
    
    private final double boxW = 50;
    private final double boxH = 50;
    
    private boolean isDirty;
    private Image image;
    private Drawer drawer;
    
    public GeneratedTreePanel(int screenWidth, int screenHeight) {
        this.setPreferredSize(new Dimension(screenWidth, screenWidth));
        drawer = new Drawer();
        isDirty = false;
    }
    
    public void setTree(DrawNode root, int treeWidth, int treeHeight) {
        int maxDepth = root.getHeight();
//        new TreeLayout(maxDepth).layoutTree(root, boxW, boxH, treeWidth, treeHeight);
        new TreeLayout(maxDepth).layoutTree(root, boxW, boxH);
        System.out.println("setting tree " + root);
        this.root = root;
        isDirty = true;
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (root == null) return;
        
        if (isDirty) {
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            drawer.paint(image.getGraphics(), root, boxW, boxH, getWidth(), getHeight());
            isDirty = false;
        }
        g.drawImage(image, 0, 0, null);
    }
   
    private void paintTextBox(Graphics g, DrawNode node) {
        String label = node.edgeLabel + node.label;
        Rectangle2D r = g.getFontMetrics().getStringBounds(label, g);
        int rw = (int)r.getWidth();
        int rh = (int)r.getHeight();
        double textX = node.getX() - (rw / 2);
        double textY = node.getY() + (rh / 2);
        int border = 3;
        double boundX = textX - border;
        double boundY = node.y - (rh / 2) - border;
        int boundW = rw + (2 * border);
        int boundH = rh + (2 * border);
        g.fillRect((int)boundX, (int)boundY, (int)boundW, (int)boundH);
        g.setColor(Color.BLACK);
        g.drawRect((int)boundX, (int)boundY, (int)boundW, (int)boundH);
        g.drawString(label, (int)textX, (int)textY); 
    }


    public void clear() {
        this.root = null;
    }

}
