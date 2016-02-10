package view.tree;

import view.tree.TreeBuilder.Node;

public class PrintingTreeWalker implements TreeWalker {

    @Override
    public void walk(Node parent, Node current) {
        int level = current.level;
        String tabs = "";
        for (int index = 0; index < level; index++) {
            tabs += "\t";
        }
        String acp =io.AtomContainerPrinter.toString(current.atomContainer);
        System.out.println(tabs + acp + " " + (current.isCanonical? "C" : "N"));
    }

}
