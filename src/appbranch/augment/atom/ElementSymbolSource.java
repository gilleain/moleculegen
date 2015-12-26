package appbranch.augment.atom;

import appbranch.FormulaParser;
import appbranch.augment.ExtensionSource;

public class ElementSymbolSource implements ExtensionSource<Integer, String> {
    
    private final FormulaParser formulaParser;
    
    public ElementSymbolSource(FormulaParser formulaParser) {
        this.formulaParser = formulaParser;
    }

    @Override
    public String getNext(Integer position) {
        return formulaParser.getElementSymbols().get(position);
    }

}
