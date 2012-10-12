package app;

import handler.DataFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ArgumentHandler {
    
    /**
     * Help requested.
     */
    private boolean isHelp;
    
    /**
     * Augmenting each molecule in a file.
     */
    private boolean isAugmentingFile;
    
    /**
     * Starting from a single atom.
     */
    private boolean isStartingFromScratch;
    
    /**
     * True if printing to standard out. 
     */
    private boolean isStdOut;
    
    /**
     * If true, each line of output is numbered.
     */
    private boolean shouldNumberLines;
    
    /**
     * The chemical formula.
     */
    private String formula;
    
    /**
     * Path to an input file of structures.
     */
    private String inputFilepath;
    
    /**
     * Path to an output file.
     */
    private String outputFilepath;
    
    /**
     * The format of the output.
     */
    private String outputStringFormat;
    
    /**
     * The format of the input.
     */
    private String inputStringFormat;
    
    
    public void processArguments(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("e", true, "Elemental Formula");
        options.addOption("i", true, "Input Filepath");
        options.addOption("I", true, "Input Format");
        options.addOption("o", true, "Output Filepath");
        options.addOption("O", true, "Output Format");
        options.addOption("n", false, "Number output lines");
        
        PosixParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args, true);
        
        if (line.hasOption('n')) {
            setShouldNumberLines(true);
        }
        
        if (line.hasOption('e')) {
            setFormula(line.getOptionValue('e'));
        }
        
        if (line.hasOption('I')) {
            setInputStringFormat(line.getOptionValue('I'));
        }
        
        if (line.hasOption('i')) {
            setInputFilepath(line.getOptionValue('i'));
            setIsAugmentingFile(true);
        } else {
            setIsStartingFromScratch(true);
        }
        
        if (line.hasOption('O')) {
            setOutputStringFormat(line.getOptionValue('O'));
        } else {
            setOutputStringFormat("NONE");
        }
        
        if (line.hasOption('o')) {
            setOutputFilepath(line.getOptionValue('o'));
        } else {
            setIsStdOut(true);
        }
        
    }
    
    public void setIsStartingFromScratch(boolean isStartingFromScratch) {
        this.isStartingFromScratch = isStartingFromScratch;
    }

    public void setIsAugmentingFile(boolean isAugmentingFile) {
        this.isAugmentingFile = isAugmentingFile;
    }

    public String getInputStringFormat() {
        return inputStringFormat;
    }

    public void setInputStringFormat(String inputStringFormat) {
        this.inputStringFormat = inputStringFormat;
    }

    public boolean isStdOut() {
        return isStdOut;
    }

    public void setStdOut(boolean isStdOut) {
        this.isStdOut = isStdOut;
    }

    public String getInputFilepath() {
        return inputFilepath;
    }

    public void setInputFilepath(String inputFilepath) {
        this.inputFilepath = inputFilepath;
    }

    public String getOutputFilepath() {
        return outputFilepath;
    }

    public void setOutputFilepath(String outputFilepath) {
        this.outputFilepath = outputFilepath;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setHelp(boolean isHelp) {
        this.isHelp = isHelp;
    }

    public void setAugmentingFile(boolean isAugmentingFile) {
        this.isAugmentingFile = isAugmentingFile;
    }

    public void setStartingFromScratch(boolean isStartingFromScratch) {
        this.isStartingFromScratch = isStartingFromScratch;
    }

    public boolean isHelp() {
        return isHelp;
    }

    public void printHelp() {
        System.out.println("Usage:");
    }

    public boolean isAugmentingFile() {
        return isAugmentingFile;
    }

    public boolean isStartingFromScratch() {
        return isStartingFromScratch;
    }

    public boolean isShouldNumberLines() {
        return shouldNumberLines;
    }

    public void setShouldNumberLines(boolean shouldNumberLines) {
        this.shouldNumberLines = shouldNumberLines;
    }

    public DataFormat getOutputFormat() {
        return convert(outputStringFormat);
    }

    public String getOutputStringFormat() {
        return outputStringFormat;
    }

    public void setOutputStringFormat(String outputStringFormat) {
        this.outputStringFormat = outputStringFormat;
    }

    public DataFormat getInputFormat() {
        return convert(inputStringFormat);
    }
    
    private DataFormat convert(String formatString) {
        if (formatString.equals("SMI")) {
            return DataFormat.SMILES;
        } else if (formatString.equals("SIG")) {
            return DataFormat.SIGNATURE;
        } else if (formatString.equals("NONE")) {
            return DataFormat.NONE;
        }
        return DataFormat.NONE; // being generous...
    }

    public void setIsStdOut(boolean isStdOut) {
        this.isStdOut = isStdOut;
    }
}
