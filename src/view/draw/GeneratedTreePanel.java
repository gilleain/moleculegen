package view.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;

public class GeneratedTreePanel extends JPanel {
    
	private static final long serialVersionUID = 2144237936253828531L;

    private DrawNode root;
    
    private AtomContainerRenderer renderer;
    
    private final double boxW = 50;
    private final double boxH = 50;    
    
    public GeneratedTreePanel(int screenWidth, int screenHeight) {
        this.setPreferredSize(new Dimension(screenWidth, screenWidth));
        
        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        
        this.renderer = new AtomContainerRenderer(generators, new AWTFontManager());
        
        RendererModel model = renderer.getRenderer2DModel(); 
        model.set(BasicAtomGenerator.CompactAtom.class, true);
        model.set(BasicAtomGenerator.AtomRadius.class, 3.0);
        model.set(BasicAtomGenerator.CompactShape.class, BasicAtomGenerator.Shape.OVAL);
        model.set(BasicAtomGenerator.KekuleStructure.class, true);
        model.set(BasicBondGenerator.BondWidth.class, 2.0);
    }
    
    public void setTree(DrawNode root, int treeWidth, int treeHeight) {
        int maxDepth = root.getHeight();
        new TreeLayout(maxDepth).layoutTree(root, treeWidth, treeHeight);
        System.out.println("setting tree " + root);
        this.root = root;
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (root == null) return;
        
        g.setColor(Color.BLACK);
        paint(g, root);
    }
    
    public void paint(Graphics g, DrawNode node) {
        for (DrawNode child : node.children) {
            g.drawLine(node.x, node.y, child.x, child.y);
            paint(g, child);
        }
        paintAtomContainer(g, node);
    }
    
    private void paintAtomContainer(Graphics g, DrawNode node) {
        IAtomContainer molecule = node.atomContainer;
        try {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(molecule, false);
            sdg.generateCoordinates();
            Rectangle2D canvas = 
                    new Rectangle2D.Double(node.x - (boxW / 2), node.y - (boxH / 2), boxW, boxH);
            renderer.paint(molecule, new AWTDrawVisitor((Graphics2D) g), canvas, true);
        } catch (Exception e) {
            // TODO
            System.out.println("Error for " + io.AtomContainerPrinter.toString(molecule)
            + e.getMessage());
        }
    }
    
    private void paintTextBox(Graphics g, DrawNode node) {
        String label = node.edgeLabel + node.label;
        Rectangle2D r = g.getFontMetrics().getStringBounds(label, g);
        int rw = (int)r.getWidth();
        int rh = (int)r.getHeight();
        int textX = node.x - (rw / 2);
        int textY = node.y + (rh / 2);
        int border = 3;
        int boundX = textX - border;
        int boundY = node.y - (rh / 2) - border;
        int boundW = rw + (2 * border);
        int boundH = rh + (2 * border);
        g.fillRect(boundX, boundY, boundW, boundH);
        g.setColor(Color.BLACK);
        g.drawRect(boundX, boundY, boundW, boundH);
        g.drawString(label, textX, textY); 
    }


    public void clear() {
        this.root = null;
    }

}
