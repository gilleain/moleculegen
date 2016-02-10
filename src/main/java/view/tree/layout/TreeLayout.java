package view.tree.layout;

import java.awt.geom.Rectangle2D;

import view.draw.DrawNode;

public class TreeLayout {
    
    public int totalLeafCount = 0;
    
    public double xSep;
    
    public double ySep;
    
    public final int maxDepth;
    
    public TreeLayout(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public Rectangle2D layoutTree(DrawNode root, double canvasW, double canvasH) {
        int leafCount = root.countLeaves();
//        this.xSep = width / (leafCount + 1);
//        this.ySep = height / (maxDepth + 1);
        this.xSep = canvasW;
        this.ySep = canvasH;
        System.out.println("xSep " + xSep + " ySep " + ySep + " leafCount " + leafCount);
        layout(root);
        return new Rectangle2D.Double(0, 0, xSep * leafCount, ySep * root.getHeight());
    }
    
    private double layout(DrawNode node) {
        node.setY(node.getDepth() * ySep);
        if (node.isLeaf()) {
            totalLeafCount += 1;
            node.setX(totalLeafCount * xSep);
            System.out.println("setting node pos to " + node.getPosString() + " " + node.getDepth());
            return node.getX();
        } else {
            double min = 0;
            double max = 0;
            for (DrawNode child : node.children()) {
                double childCenter = layout(child);
                if (min == 0) {
                    min = childCenter;
                }
                max = childCenter;
            }
            if (min == max) {
                node.setX(min);
            } else {
                node.setX(min + (max - min) / 2);
            }
            System.out.println("setting node pos to " + node.getPosString());
            return node.getX();
        }
    }
    
}
