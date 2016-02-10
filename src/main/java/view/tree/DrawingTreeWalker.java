package view.tree;

import java.util.HashMap;
import java.util.Map;

import view.draw.DrawNode;
import view.tree.TreeBuilder.Node;

public class DrawingTreeWalker implements TreeWalker {
    
    private DrawNode drawRoot;
    
    private Map<Integer, DrawNode> nodeMap;
    
    public DrawingTreeWalker() {
        drawRoot = null;
        this.nodeMap = new HashMap<Integer, DrawNode>();
    }
    
    public DrawNode getRoot() {
        return drawRoot;
    }

    @Override
    public void walk(Node parent, Node node) {
        DrawNode current;
        DrawNode parentDrawNode;
        int depth;
        if (drawRoot == null) {
            parentDrawNode = null;
            depth = 0;
        } else {
            parentDrawNode = nodeMap.get(parent.index);
            depth = parentDrawNode.getDepth() + 1;
        }
        current = new DrawNode("", parentDrawNode, depth, 1);
        if (parentDrawNode == null) {
            drawRoot = current;
        } else {
            parentDrawNode.addChild(current);
        }
        current.setAtomContainer(node.atomContainer);
        nodeMap.put(node.index, current);
    }

}
