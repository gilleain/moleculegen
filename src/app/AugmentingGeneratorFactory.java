package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import generate.AtomAugmentingGenerator;
import generate.AugmentationMethod;
import generate.BondAugmentingGenerator;
import generate.LabellerMethod;
import generate.ListerMethod;
import generate.ValidatorMethod;
import handler.CountingHandler;
import handler.DataFormat;
import handler.GenerateHandler;
import handler.PrintStreamStringHandler;
import handler.SDFHandler;
import handler.ZipDecoratingHandler;

public class AugmentingGeneratorFactory {

	private static ListerMethod DEFAULT_LISTER = ListerMethod.SYMMETRIC;

	private static LabellerMethod DEFAULT_LABELLER = LabellerMethod.SIGNATURE;

	private static ValidatorMethod DEFAULT_VALIDATOR = ValidatorMethod.REFINER;
	
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
        
        GenerateHandler handler;
        DataFormat format = argsH.getOutputFormat();
        
        if (format == DataFormat.NONE && ! argsH.isComparingToFile()) {
            handler = new CountingHandler(argsH.isTiming());
        } else {
            PrintStream outStream;
            boolean shouldNumberLines = argsH.isShouldNumberLines();
            boolean shouldShowParent = argsH.isShowParent();
            if (argsH.isStdOut()) {
                if (format == DataFormat.SDF) {
                    handler = new SDFHandler();
                } else {
                    outStream = System.out;
                    handler = new PrintStreamStringHandler(
                            outStream, format, shouldNumberLines, shouldShowParent);
                }
            } else {
                String outputFilename = argsH.getOutputFilepath();
                if (format == DataFormat.SDF) {
                    handler = new SDFHandler(outputFilename);
                } else {
                	if (argsH.isZipOutput()) {
                		String zipEntryName = formula + ".txt"; // TODO?
                		handler = new ZipDecoratingHandler(
                				outputFilename, zipEntryName, format, shouldNumberLines, shouldShowParent);
                	} else {
                		handler = new PrintStreamStringHandler(
                				new PrintStream(new FileOutputStream(outputFilename)),
                						format, shouldNumberLines, shouldShowParent);
                	}
                }
            }
        }
        
        // create the generator, with the appropriate handler and lister method
        ListerMethod listerMethod = (argsH.getListerMethod() == null)? DEFAULT_LISTER : argsH.getListerMethod();
        LabellerMethod labellerMethod = (argsH.getLabellerMethod() == null)? DEFAULT_LABELLER : argsH.getLabellerMethod();
        ValidatorMethod validatorMethod = (argsH.getValidatorMethod() == null)? DEFAULT_VALIDATOR : argsH.getValidatorMethod();
        AugmentationMethod augmentationMethod = (argsH.getAugmentationMethod() == null)? AugmentationMethod.ATOM : argsH.getAugmentationMethod();
        
        AugmentingGenerator generator;
        if (augmentationMethod == AugmentationMethod.ATOM) {
            generator = new AtomAugmentingGenerator(handler, listerMethod, labellerMethod, validatorMethod);
        } else {
            generator = new BondAugmentingGenerator(handler);
        }
        
        int heavyAtomCount = generator.setParamsFromFormula(formula);
        if (heavyAtomCount < 2) {
            error("Please specify more than 1 heavy atom");
            return null;
        }
        
        return generator;
	}

	// TODO : convert to throwing a custom exception - GeneratorBuildException?
	private static void error(String text) {
        System.out.println(text);
    }


}
