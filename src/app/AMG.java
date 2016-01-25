package app;

import handler.DataFormat;

import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.openscience.cdk.exception.CDKException;
import augment.AugmentingGenerator;
import augment.AugmentingGeneratorFactory;

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
	if (generator == null) {
		return;
	}
        
        if (argsH.isAugmentingFile() || argsH.isComparingToFile()) {
            String inputFile = argsH.getInputFilepath();
            if (inputFile == null) {
                error("No input file specified");
                return;
            } else {
            	if (argsH.isAugmentingFile()) {
	                DataFormat inputFormat = argsH.getInputFormat();
	                if (inputFormat == DataFormat.MOL) {
	                    SingleInputAugmentor.run(argsH, inputFile, generator);
	                } else {
	                    MutipleInputAugmentor.run(argsH, inputFile, generator);
	                }
            	} else {
            		generator.run();
            	}
            }
        } else if (argsH.isStartingFromScratch()) {
            generator.run();
        }
        generator.finish();
    }
    
    private static void error(String text) {
        System.out.println(text);
    }
}
