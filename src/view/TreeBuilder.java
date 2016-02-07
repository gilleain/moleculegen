package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

import handler.CanonicalHandler;

public class TreeBuilder {
    
    /**
     * Captures canonical change events to tree nodes.
     */
    private final TreeCanonicalHandler handler;
    
    /**
     * A lookup for nodes, so that we can add children to the right parents.
     */
    private final Map<String, Node> nodeKeys;
    
    private Node root;
    
    private class Node {
        public List<Node> children;
        public IAtomContainer atomContainer;
        public boolean isCanonical;
        public Node(IAtomContainer atomContainer, boolean isCanonical) {
            this.atomContainer = atomContainer;
            this.children = new ArrayList<Node>();
            this.isCanonical = isCanonical;
        }
        
    }
    
    public class TreeCanonicalHandler implements CanonicalHandler<IAtomContainer> {

        @Override
        public void handle(IAtomContainer parent, IAtomContainer child, boolean isCanonical) {
            String key = new MoleculeSignature(parent).toCanonicalString();
            Node parentNode; 
            if (nodeKeys.containsKey(key)) {
                parentNode = nodeKeys.get(key);
            } else {
                parentNode = new Node(parent, true);
                if (root == null) {
                    root = parentNode; 
                }
                nodeKeys.put(key, parentNode);
            }
            
            String childKey = new MoleculeSignature(child).toCanonicalString();
            Node childNode = new Node(child, isCanonical);
            nodeKeys.put(childKey, childNode);
            parentNode.children.add(childNode);
        }
        
    }
    
    public TreeBuilder() {
        this.handler = new TreeCanonicalHandler();
        this.nodeKeys = new HashMap<String, Node>();
    }
    
    public TreeCanonicalHandler getHandler() {
        return this.handler;
    }
    
    public void printTree() {
        walk(0, root);
    }
    
    private void walk(int level, Node current) {
        String tabs = "";
        for (int index = 0; index < level; index++) {
            tabs += "\t";
        }
        String acp =io.AtomContainerPrinter.toString(current.atomContainer);
        System.out.println(tabs + acp + " " + (current.isCanonical? "C" : "N"));
        for (Node child : current.children) {
            walk(level + 1, child);
        }
    }

}
