package appbranch;

import java.io.IOException;

public class AugmentingGeneratorFactory {
    
    public static AugmentingGenerator build(ArgumentHandler argsH) throws IOException {
        if (argsH.isHelp()) {
            argsH.printHelp();
            return null;
        }
        
        String formula = argsH.getFormula();
        if (formula == null) {
            error("Please supply a formula");
            return null;
        }
        
        Handler handler = null;
        AtomGenerator generator = new AtomGenerator(formula, handler);
        
        return generator;
    }
    
    // TODO : convert to throwing a custom exception - GeneratorBuildException?
    private static void error(String text) {
        System.out.println(text);
    }

}
