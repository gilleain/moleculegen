package view.draw;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class DrawNode {
    
    double x = -1;
    
    double y = -1;
    
    int depth;
    
    int color;
    
    List<DrawNode> children = new ArrayList<DrawNode>();
    
    String label;
    
    IAtomContainer atomContainer;
    
    DrawNode parent;
    
    String edgeLabel;
    
    public DrawNode(String label, DrawNode parent, int d, int color) {
        this.label = label;
        this.parent = parent;
        this.depth = d;
        this.color = color;
        edgeLabel = "";
    }
    
    public int getDepth() {
        return depth;
    }
    
    public void setAtomContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
    }
    
    public DrawNode(
        String label, DrawNode parent, int d, int color, String edgeLabel) {
        this(label, parent, d, color);
        this.edgeLabel = edgeLabel;
    }
    
    public int countLeaves() {
        if (this.isLeaf()) {
            return 1;
        } else {
            int c = 0;
            for (DrawNode child : this.children) {
                c += child.countLeaves();
            }
            return c;
        }
    }
    
    public boolean isLeaf() {
        return this.children.size() == 0;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        this.toString(buffer);
        return buffer.toString();
    }
    
    private void toString(StringBuffer buffer) {
        buffer.append(label);
        buffer.append('[').append(x).append(',').append(y).append(']');
        for (DrawNode child : this.children) {
            child.toString(buffer);
        }
    }

    public int getHeight() {
        int maxChildHeight = 0;
        for (DrawNode child : children) {
            int childHeight = child.getHeight();
            if (childHeight > maxChildHeight) {
                maxChildHeight = childHeight;
            }
        }
        return maxChildHeight + 1;
    }

    public void addChild(DrawNode node) {
        this.children.add(node);
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getX() {
        return this.x;
    }
    
    public Iterable<DrawNode> children() {
        return this.children;
    }
    
    public String getPosString() {
        return "(" + this.x + ", " + this.y + ") ";
    }

    public double getY() {
        return this.y;
    }
}
