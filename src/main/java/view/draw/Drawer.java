package view.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

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

public class Drawer {
    
    private AtomContainerRenderer renderer;
    
    public Drawer() {
        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        
        this.renderer = new AtomContainerRenderer(generators, new AWTFontManager());
        
        RendererModel model = renderer.getRenderer2DModel(); 
        model.set(BasicAtomGenerator.CompactAtom.class, true);
        model.set(BasicAtomGenerator.AtomRadius.class, 4.0);
        model.set(BasicAtomGenerator.CompactShape.class, BasicAtomGenerator.Shape.OVAL);
        model.set(BasicAtomGenerator.KekuleStructure.class, true);
        model.set(BasicBondGenerator.BondWidth.class, 2.0);
        model.set(BasicSceneGenerator.ForegroundColor.class, Color.GRAY);
    }
    
    public void paint(Graphics g, DrawNode root, double boxW, double boxH, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        if (root == null) return;
        paint(g, boxW, boxH, root);
    }
    
    private void paint(Graphics g, double boxW, double boxH, DrawNode node) {
        for (DrawNode child : node.children) {
            g.setColor(Color.BLACK);
            g.drawLine((int)node.getX(), (int)node.getY(), (int)child.getX(), (int)child.getY());
            paint(g, boxW, boxH, child);
        }
        paintAtomContainer(g, boxW, boxH, node);
    }
    
    private void paintAtomContainer(Graphics g, double boxW, double boxH, DrawNode node) {
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

}
