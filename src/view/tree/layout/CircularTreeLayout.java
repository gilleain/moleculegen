package view.tree.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import view.draw.DrawNode;

public class CircularTreeLayout {
    
    private int currentLeafCount;
    
    public Rectangle2D layoutTree(DrawNode root, double canvasW, double canvasH) {
        int leafCount = root.countLeaves();
        currentLeafCount = 0;
        double dim = (canvasW * leafCount) / 2;
        Point2D center = new Point2D.Double(dim, dim);
        layout(root, dim, center, leafCount, canvasW, canvasH);
        return new Rectangle2D.Double(0, 0, canvasW * leafCount, canvasW * leafCount);
    }
    
    private double layout(DrawNode node, double maxR, Point2D center, int totalLeafCount, double xSep, double ySep) {
        if (node.isLeaf()) {
            currentLeafCount += 1;
            double angle = currentLeafCount * (Math.toRadians(360) / totalLeafCount);
            set(node, circularPoint(angle, center, maxR));
            return angle;
        } else {
           
            double min = 0;
            double max = 0;
            for (DrawNode child : node.children()) {
                double childAngle = layout(child, maxR, center, totalLeafCount, xSep, ySep);
                if (min == 0) {
                    min = childAngle;
                }
                max = childAngle;
            }
            double angle;
            if (min == max) {
                angle = min;
            } else {
                angle = min + (max - min) / 2;
            }

            if (node.getDepth() == 0) {
                set(node, center);
                return 0;
            } else {
                double r = maxR - (maxR / (node.getDepth() + 1));
                set(node, circularPoint(angle, center, r));
                return angle;
            }
        }
    }
    
    private void set(DrawNode node, Point2D p) {
        node.setX(p.getX());
        node.setY(p.getY());
    }
    
    private Point2D circularPoint(double angle, Point2D origin, double r) {
        double xp = origin.getX() + (r * Math.cos(angle));
        double yp = origin.getY() + (r * Math.sin(angle));
        return new Point2D.Double(xp, yp);
    }

}
