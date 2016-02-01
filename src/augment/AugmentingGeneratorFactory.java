package augment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.openscience.cdk.interfaces.IAtomContainer;

import app.ArgumentHandler;
import app.AugmentationMethod;
import augment.bond.BondGenerator;
import augment.vertex.VertexGenerator;
import handler.Handler;
import handler.graph.MoleculeAdaptor;
import handler.molecule.CountingHandler;
import handler.molecule.DataFormat;
import handler.molecule.HBondCheckingHandler;
import handler.molecule.PrintStreamStringHandler;
import handler.molecule.SDFHandler;
import handler.molecule.ZipDecoratingHandler;
import model.Graph;

public class AugmentingGeneratorFactory {
    
    @SuppressWarnings("rawtypes")   // not sure how we could avoid this...
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
        
        AugmentationMethod augmentationMethod = (argsH.getAugmentationMethod() == null)? 
                AugmentationMethod.ATOM : argsH.getAugmentationMethod();
        
        if (augmentationMethod == AugmentationMethod.ATOM) {
//            return new AtomGenerator(formula, getHandler(argsH));
            return new VertexGenerator(formula, getGraphHandler(argsH));
        } else {
            return new BondGenerator(formula, getHandler(argsH));
        }
    }
    
    private static boolean isCounting(ArgumentHandler argsH) {
        return argsH.getOutputFormat() == DataFormat.NONE && !argsH.isComparingToFile();
    }
    
    private static Handler<Graph> getGraphHandler(ArgumentHandler argsH) throws IOException {
        return new MoleculeAdaptor(
                new HBondCheckingHandler(argsH.getFormula(), getHandler(argsH)));
    }
    
    private static Handler<IAtomContainer> getHandler(ArgumentHandler argsH) throws IOException {
        DataFormat format = argsH.getOutputFormat();
        
        if (isCounting(argsH)) {
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
