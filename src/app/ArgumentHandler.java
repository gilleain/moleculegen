package app;

import handler.DataFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
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
    
    private Options options;
    
    public ArgumentHandler() {
        options = new Options();
        options.addOption(opt("e", "formula", "Elemental Formula"));
        options.addOption(opt("i", "path", "Input Filepath"));
        options.addOption(opt("I", "format", "Input Format (SMI, SIG, SDF)"));
        options.addOption(opt("o", "path", "Output Filepath"));
        options.addOption(opt("O", "format", "Output Format (SMI, SIG, SDF)"));
        options.addOption(opt("n", "Number output lines"));
        options.addOption(opt("h", "Print help"));
    }
    
    @SuppressWarnings("static-access")
    private Option opt(String o, String desc) {
        return OptionBuilder.withDescription(desc)
                            .create(o);
    }
    
    @SuppressWarnings("static-access")
    private Option opt(String o, String argName, String desc) {
        return OptionBuilder.hasArg()
                            .withDescription(desc)
                            .withArgName(argName)
                            .create(o);
    }
    
    
    public void processArguments(String[] args) throws ParseException {
        PosixParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args, true);
        
//        System.out.println(java.util.Arrays.toString(line.getOptions()));
        if (line.hasOption('h') || line.getOptions().length == 0) {
            setIsHelp(true);
        }
        
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
    
    public void setIsHelp(boolean isHelp) {
        this.isHelp = isHelp;
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
        System.out.println("Usage: java -jar AMG.jar -e <formula>");
        String format = "%-12s";
        for (Object op : options.getOptions()) {
            Option option = (Option) op;
            if (option.hasArg()) {
                String head = String.format(format, 
                        "-" + option.getOpt() + " <" + option.getArgName() + ">");
                System.out.println(head + " = " + option.getDescription());
            } else {
                String head = String.format(format, "-" + option.getOpt());
                System.out.println(head + " = " + option.getDescription());
            }
        }
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
        } else if (formatString.equals("SDF")) {
            return DataFormat.SDF;
        } else if (formatString.equals("NONE")) {
            return DataFormat.NONE;
        }
        return DataFormat.NONE; // being generous...
    }

    public void setIsStdOut(boolean isStdOut) {
        this.isStdOut = isStdOut;
    }
}
