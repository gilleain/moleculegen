package app;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface AugmentingGenerator {

    public void setHCount(int hCount);
    
    public void setElementSymbols(List<String> elementSymbols);
    
    public void extend(IAtomContainer atomContainer, int maxSize);

    public List<String> getElementSymbols();
    
    public int setParamsFromFormula(String formulaString);
    
    public int getHeavyAtomCount();
    
    public void finish();
    
}
