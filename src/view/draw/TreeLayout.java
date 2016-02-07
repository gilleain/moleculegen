package view.draw;

public class TreeLayout {
    
    public int totalLeafCount = 0;
    
    public int xSep;
    
    public int ySep;
    
    public final int maxDepth;
    
    public TreeLayout(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public void layoutTree(DrawNode root, int width, int height) {
        int leafCount = root.countLeaves();
        this.xSep = width / (leafCount + 1);
        this.ySep = height / (maxDepth + 1);
        System.out.println("xSep " + xSep + " ySep " + ySep + " leafCount " + leafCount);
        layout(root);
    }
    
    private int layout(DrawNode node) {
        node.y  = node.depth * ySep;
        if (node.isLeaf()) {
            totalLeafCount += 1;
            node.x = totalLeafCount * xSep;
            System.out.println("setting node pos to (" + node.x + ", " + node.y + ") " + node.depth);
            return node.x;
        } else {
            int min = 0;
            int max = 0;
            for (DrawNode child : node.children) {
                int childCenter = layout(child);
                if (min == 0) {
                    min = childCenter;
                }
                max = childCenter;
            }
            if (min == max) {
                node.x = min;
            } else {
                node.x = min + (max - min) / 2;
            }
            System.out.println("setting node pos to (" + node.x + ", " + node.y);
            return node.x;
        }
    }
    
}
