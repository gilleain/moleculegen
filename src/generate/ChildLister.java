package generate;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ChildLister {
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex);
    
    public void setElementSymbols(List<String> elementSymbols);

    public List<String> getElementSymbols();

}
