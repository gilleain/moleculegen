package app;

import handler.DataFormat;

import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.openscience.cdk.exception.CDKException;

public class AMG {
    
    public static void main(String[] args) throws CDKException, IOException {
        ArgumentHandler argsH = new ArgumentHandler();
        try {
            argsH.processArguments(args);
            run(argsH);
        } catch (ParseException pe) {
            // TODO
        }
    }
    
    public static void run(ArgumentHandler argsH) throws CDKException, IOException {
        AugmentingGenerator generator = AugmentingGeneratorFactory.build(argsH);
        int heavyAtomCount = generator.getHeavyAtomCount();
        
        if (argsH.isAugmentingFile() || argsH.isComparingToFile()) {
            String inputFile = argsH.getInputFilepath();
            if (inputFile == null) {
                error("No input file specified");
                return;
            } else {
            	if (argsH.isAugmentingFile()) {
	                DataFormat inputFormat = argsH.getInputFormat();
	                if (inputFormat == DataFormat.MOL) {
	                    SingleInputAugmentor.run(argsH, inputFile, generator, heavyAtomCount);
	                } else {
	                    MutipleInputAugmentor.run(argsH, inputFile, generator, heavyAtomCount);
	                }
            	} else {
            		FromScratchAugmentor.run(generator, heavyAtomCount);
            	}
            }
        } else if (argsH.isStartingFromScratch()) {
            FromScratchAugmentor.run(generator, heavyAtomCount);
        }
        generator.finish();
    }
    
    private static void error(String text) {
        System.out.println(text);
    }
}
