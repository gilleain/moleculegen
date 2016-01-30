package augment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import app.ArgumentHandler;
import app.AugmentationMethod;
import augment.atom.AtomGenerator;
import augment.bond.BondGenerator;
import handler.CountingHandler;
import handler.DataFormat;
import handler.Handler;
import handler.PrintStreamStringHandler;
import handler.SDFHandler;
import handler.ZipDecoratingHandler;

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
        
        AugmentationMethod augmentationMethod = (argsH.getAugmentationMethod() == null)? AugmentationMethod.ATOM : argsH.getAugmentationMethod();
        if (augmentationMethod == AugmentationMethod.ATOM) {
            return new AtomGenerator(formula, getHandler(argsH));
        } else {
            return new BondGenerator(formula, getHandler(argsH));
        }
    }
    
    private static Handler getHandler(ArgumentHandler argsH) throws IOException {
        DataFormat format = argsH.getOutputFormat();
        
        if (format == DataFormat.NONE && ! argsH.isComparingToFile()) {
            return new CountingHandler(argsH.isTiming());
        } else {
            PrintStream outStream;
            boolean shouldNumberLines = argsH.isShouldNumberLines();
            if (argsH.isStdOut()) {
                if (format == DataFormat.SDF) {
                    return new SDFHandler();
                } else {
                    outStream = System.out;
                    return new PrintStreamStringHandler(outStream, format, shouldNumberLines);
                }
            } else {
                String outputFilename = argsH.getOutputFilepath();
                if (format == DataFormat.SDF) {
                    return new SDFHandler(outputFilename);
                } else {
                    if (argsH.isZipOutput()) {
                        String zipEntryName = argsH.getFormula() + ".txt"; // TODO?
                        return new ZipDecoratingHandler(
                                outputFilename, zipEntryName, format, shouldNumberLines);
                    } else {
                        PrintStream printStream = new PrintStream(new FileOutputStream(outputFilename)); 
                        return new PrintStreamStringHandler(printStream, format, shouldNumberLines);
                    }
                }
            }
        }
    }
    
    // TODO : convert to throwing a custom exception - GeneratorBuildException?
    private static void error(String text) {
        System.out.println(text);
    }

}
