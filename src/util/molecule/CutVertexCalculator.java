package util.molecule;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class CutVertexCalculator {

    int time = 0;
    static final int NIL = -1;

    // A recursive function that find articulation points using DFS
    // u --> The vertex to be visited next
    // visited[] --> keeps tract of visited vertices
    // disc[] --> Stores discovery times of visited vertices
    // parent[] --> Stores parent vertices in DFS tree
    // ap[] --> Store articulation points
    void calculateCutVertices(IAtomContainer mol, 
            int u, boolean visited[], int disc[], int low[], int parent[], boolean ap[]) {

        // Count of children in DFS Tree
        int children = 0;

        // Mark the current node as visited
        visited[u] = true;

        // Initialize discovery time and low value
        disc[u] = low[u] = ++time;

        // Go through all vertices adjacent to this
        for (IAtom atom : mol.getConnectedAtomsList(mol.getAtom(u))) {
            int v = mol.getAtomNumber(atom);

            // If v is not visited yet, then make it a child of u
            // in DFS tree and recur for it
            if (!visited[v]) {
                children++;
                parent[v] = u;
                calculateCutVertices(mol, v, visited, disc, low, parent, ap);

                // Check if the subtree rooted with v has a connection to
                // one of the ancestors of u
                low[u] = Math.min(low[u], low[v]);

                // u is an articulation point in following cases:

                // (1) u is root of DFS tree and has two or more children.
                if (parent[u] == NIL && children > 1)
                    ap[u] = true;

                // (2) If u is not root and low value of one of its child
                // is more than discovery value of u.
                if (parent[u] != NIL && low[v] >= disc[u])
                    ap[u] = true;
            }

            // Update low value of u for parent function calls.
            else if (v != parent[u])
                low[u] = Math.min(low[u], disc[v]);
        }
    }

    // The function to do DFS traversal.
    public List<Integer> getCutVertices(IAtomContainer atomContainer) {
        int V = atomContainer.getAtomCount();
        // Mark all the vertices as not visited
        boolean visited[] = new boolean[V];
        int disc[] = new int[V];
        int low[] = new int[V];
        int parent[] = new int[V];
        boolean ap[] = new boolean[V]; // To store articulation points

        // Initialize parent and visited, 
        // and ap(articulation point) arrays
        for (int i = 0; i < V; i++) {
            parent[i] = NIL;
            visited[i] = false;
            ap[i] = false;
        }

        // Call the recursive helper function to find articulation
        // points in DFS tree rooted with vertex 'i'
        for (int i = 0; i < V; i++) {
            if (visited[i] == false) {
                calculateCutVertices(atomContainer, i, visited, disc, low, parent, ap);
            }
        }

        // Now ap[] contains articulation points, print them
        List<Integer> articulationPoints = new ArrayList<Integer>();
        for (int i = 0; i < V; i++) {
            if (ap[i] == true) {
//                System.out.print(i + " ");
                articulationPoints.add(i);
            }
        }
        return articulationPoints;
    }

}
