package view.draw;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import augment.atom.AtomGenerator;
import handler.molecule.PrintStreamHandler;
import view.tree.DrawingTreeWalker;
import view.tree.TreeBuilder;

public class Viewer extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = -6803621893829833590L;

	private GeneratedTreePanel treePanel;
    
    private JTextField formulaField;
    
    private JButton runButton;
    
    private JButton showButton;
    
    private final int TREE_PANEL_WIDTH = 800;
    
    private final int TREE_PANEL_HEIGHT = 400;
    
    private DrawNode currentTree;
    
    public Viewer() {
        this.setLayout(new BorderLayout());
        
        treePanel = new GeneratedTreePanel(TREE_PANEL_WIDTH, TREE_PANEL_HEIGHT);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(treePanel);
        scrollPane.setPreferredSize(new Dimension(TREE_PANEL_WIDTH, TREE_PANEL_HEIGHT));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        formulaField = new JTextField("C3H8", 12);
        formulaField.addActionListener(this);
        controlPanel.add(formulaField);
        
        runButton = new JButton("Run");
        runButton.addActionListener(this);
        controlPanel.add(runButton);
        
        showButton = new JButton("Show");
        showButton.addActionListener(this);
        controlPanel.add(showButton);
        
        this.add(controlPanel, BorderLayout.NORTH);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == runButton) {
            String elementalFormula = formulaField.getText();
            currentTree = run(elementalFormula);
        } else if (e.getSource() == showButton) {
            if (currentTree != null) {
                treePanel.setTree(currentTree, TREE_PANEL_WIDTH - 20, TREE_PANEL_HEIGHT - 20);
                this.repaint();
            }
        }
    }
    
    private DrawNode run(String formula) {
        TreeBuilder builder = new TreeBuilder();
        AtomGenerator generator = new AtomGenerator(formula, new PrintStreamHandler(System.out));
        generator.setCanonicalHandler(builder.getHandler());
        generator.run();
        
        DrawingTreeWalker walker = new DrawingTreeWalker();
        builder.walkTree(walker);
        return walker.getRoot();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Generator Viewer");
        f.add(new Viewer());
        f.pack();
        f.setVisible(true);
    }

}
