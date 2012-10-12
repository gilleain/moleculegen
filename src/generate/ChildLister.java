package generate;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ChildLister {
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex);
    
    public void setElementString(String elementString);

    public String getCertificate(IAtomContainer parent);

    public List<String> getElementSymbols();

}
