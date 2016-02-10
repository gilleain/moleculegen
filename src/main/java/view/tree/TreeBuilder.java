package view.tree;

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
    
    private final boolean canonicalOnly;
    
    /**
     * A lookup for nodes, so that we can add children to the right parents.
     */
    private final Map<String, Node> nodeKeys;
    
    private Node root;
    
    public class Node {
        public List<Node> children;
        public IAtomContainer atomContainer;
        public boolean isCanonical;
        public int level;
        public int index;
        
        public Node(IAtomContainer atomContainer, boolean isCanonical, int level, int index) {
            this.atomContainer = atomContainer;
            this.children = new ArrayList<Node>();
            this.isCanonical = isCanonical;
            this.level = level;
            this.index = index;
        }
        
    }
    
    public class TreeCanonicalHandler implements CanonicalHandler<IAtomContainer> {
        
        private int index;

        @Override
        public void handle(IAtomContainer parent, IAtomContainer child, boolean isCanonical) {
            if (canonicalOnly && !isCanonical) return;
            
            String key = new MoleculeSignature(parent).toCanonicalString();
            Node parentNode; 
            if (nodeKeys.containsKey(key)) {
                parentNode = nodeKeys.get(key);
            } else {
                parentNode = new Node(parent, true, 0, 0);
                if (root == null) {
                    root = parentNode; 
                }
                nodeKeys.put(key, parentNode);
            }
            
            String childKey = new MoleculeSignature(child).toCanonicalString();
            Node childNode = new Node(child, isCanonical, parentNode.level + 1, ++index);
            nodeKeys.put(childKey, childNode);
            parentNode.children.add(childNode);
        }
        
    }
    
    public TreeBuilder() {
        this(false);
    }
    
    public TreeBuilder(boolean canonicalOnly) {
        this.canonicalOnly = canonicalOnly;
        this.handler = new TreeCanonicalHandler();
        this.nodeKeys = new HashMap<String, Node>();
    }
    
    public TreeCanonicalHandler getHandler() {
        return this.handler;
    }
    
    public void walkTree(TreeWalker walker) {
        walk(null, root, walker);
    }
    
    private void walk(Node parent, Node current, TreeWalker walker) {
        walker.walk(parent, current);
        for (Node child : current.children) {
            walk(current, child, walker);
        }
    }

}
