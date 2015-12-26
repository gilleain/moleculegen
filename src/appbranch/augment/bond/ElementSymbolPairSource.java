package appbranch.augment.bond;

import java.util.List;

import appbranch.FormulaParser;
import appbranch.augment.ExtensionSource;

public class ElementSymbolPairSource implements ExtensionSource<IndexPair, ElementPair> {
    
    private final FormulaParser formulaParser;
    
    public ElementSymbolPairSource(FormulaParser formulaParser) {
        this.formulaParser = formulaParser;
    }

    @Override
    public ElementPair getNext(IndexPair position) {
        List<String> elementSymbols = formulaParser.getElementSymbols();
        return new ElementPair(
                elementSymbols.get(position.getStart()),
                elementSymbols.get(position.getEnd())
       );
    }

}
