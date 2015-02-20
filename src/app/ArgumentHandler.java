package app;

import generate.AugmentationMethod;
import generate.LabellerMethod;
import generate.ListerMethod;
import generate.ValidatorMethod;
import handler.DataFormat;

import java.util.ArrayList;
import java.util.List;

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
     * If true, print out the parent of each molecule.
     */
    private boolean showParent;
    
    /**
     * If true, print out the time taken.
     */
    private boolean isTiming;

    /**
     * If true, zip the output
     */
    private boolean isZipOutput;
    
    /**
     * If true, we are comparing the results to the contents of inputFilepath
     */
    private boolean isComparingToFile;
    
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
    
    /**
     * A string like 10:20 that indicates a section of an input file to use.
     */
    private String rangeString;
  
    /**
     * The child listing method to use.
     */
    private ListerMethod listerMethod;
    
    /**
     * The child listing labeller method to use.
     */
    private LabellerMethod labellerMethod;
    
    /**
     * The canonical validation method to use.
     */
    private ValidatorMethod validatorMethod;
    
    /**
     * Atom or Bond augmentation
     */
    private AugmentationMethod augmentationMethod;
    
    private Options options;
    
    public ArgumentHandler() {
        options = new Options();
        
        // short options
        options.addOption(opt("c", "Compare the output to the input file"));
        options.addOption(opt("e", "formula", "Elemental Formula"));
        options.addOption(opt("h", "Print help"));
        options.addOption(opt("i", "path", "Input Filepath"));
        options.addOption(opt("I", "format", "Input Format (SMI, SIG, SDF, MOL)"));
        options.addOption(opt("o", "path", "Output Filepath"));
        options.addOption(opt("O", "format", "Output Format (SMI, SIG, SDF, MOL)"));
        options.addOption(opt("n", "Number output lines"));
        options.addOption(opt("p", "Show parent of each molecule"));
        options.addOption(opt("r", "min:max", "Range of input file to use"));
        options.addOption(opt("t", "Time the run"));
        options.addOption(opt("z", "Compress (zip) the output"));

        // long options
        options.addOption(lopt("lister", "method", "Lister method for children (FILTER, SYMMETRIC)"));
        options.addOption(lopt("labeller", "method", "Labeller method for filtering children (REFINER, SIGNATURE)"));
        options.addOption(lopt("validator", "method", "Validator method for canonical checking (REFINER, SIGNATURE)"));
        options.addOption(lopt("augmentation", "method", "Augmentation method (ATOM, BOND)"));
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
    
    @SuppressWarnings("static-access")
    private Option lopt(String lo, String argName, String desc) {
        return OptionBuilder.hasArg()
                            .withDescription(desc)
                            .withArgName(argName)
                            .withLongOpt(lo)
                            .create();
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
        
        if (line.hasOption('p')) {
            setShowParent(true);
        }
        
        if (line.hasOption('e')) {
            setFormula(line.getOptionValue('e'));
        }
        
        if (line.hasOption('I')) {
            setInputStringFormat(line.getOptionValue('I'));
        }
        
        if (line.hasOption('i') || line.hasOption('c')) {
            setInputFilepath(line.getOptionValue('i'));
            if (line.hasOption('c')) {
            	setComparingToFile(true);
            } else {
            	setIsAugmentingFile(true);
            }
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
        
        if (line.hasOption('r')) {
            setRangeString(line.getOptionValue('r'));
        }
        
        if (line.hasOption('t')) {
            setTiming(true);
        }
        
        if (line.hasOption('z')) {
        	setZipOutput(true);
        }
        
        if (line.hasOption("lister")) {
            ListerMethod chosenLister = 
                ListerMethod.valueOf(line.getOptionValue("lister"));
            setListerMethod(chosenLister);
        }
        
        if (line.hasOption("labeller")) {
            LabellerMethod chosenLabeller = 
                LabellerMethod.valueOf(line.getOptionValue("labeller"));
            setLabellerMethod(chosenLabeller);
        }
        
        if (line.hasOption("validator")) {
            ValidatorMethod chosenValidator = 
                ValidatorMethod.valueOf(line.getOptionValue("validator"));
            setValidatorMethod(chosenValidator);
        }
        
        if (line.hasOption("augmentation")) {
            AugmentationMethod chosenAugmentation = 
                AugmentationMethod.valueOf(line.getOptionValue("augmentation"));
            setAugmentationMethod(chosenAugmentation);
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

    public boolean isShowParent() {
        return showParent;
    }

    public void setShowParent(boolean showParent) {
        this.showParent = showParent;
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
        List<Option> shortOps = new ArrayList<Option>();
        List<Option> longOps = new ArrayList<Option>();
        for (Object op : options.getOptions()) {
            Option option = (Option) op;
            if (option.hasLongOpt()) {
               longOps.add(option);
            } else {
               shortOps.add(option); 
            }
        }
        
        String shortFormat = "%-14s";
        System.out.println("\nBasic options :");
        for (Option shortOp : shortOps) {
            printShortOp(shortOp, shortFormat);
        }
        
        String longFormat = "%-23s";
        System.out.println("\nAdvanced options :");
        for (Option longOp : longOps) {
            printLongOp(longOp, longFormat);
        }
    }
    
    private void printLongOp(Option option, String format) {
        if (option.hasArg()) {
            String head = String.format(format, 
                    "--" + option.getLongOpt() + "=<" + option.getArgName() + ">");
            System.out.println(head + "  " + option.getDescription());
        } else {
            String head = String.format(format, "-" + option.getLongOpt());
            System.out.println(head + "  " + option.getDescription());
        }
    }
    
    private void printShortOp(Option option, String format) {
        if (option.hasArg()) {
            String head = String.format(format, 
                    "-" + option.getOpt() + " <" + option.getArgName() + ">");
            System.out.println(head + "  " + option.getDescription());
        } else {
            String head = String.format(format, "-" + option.getOpt());
            System.out.println(head + "  " + option.getDescription());
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
    
    public boolean isTiming() {
        return isTiming;
    }

    public void setTiming(boolean isTiming) {
        this.isTiming = isTiming;
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
        } else if (formatString.equals("MOL")) {
            return DataFormat.MOL;
        } else if (formatString.equals("ACP")) {
            return DataFormat.ACP;
        } else if (formatString.equals("NONE")) {
            return DataFormat.NONE;
        }
        return DataFormat.NONE; // being generous...
    }

    public void setIsStdOut(boolean isStdOut) {
        this.isStdOut = isStdOut;
    }

    public String getRangeString() {
        return rangeString;
    }

    public void setRangeString(String rangeString) {
        this.rangeString = rangeString;
    }

    public ListerMethod getListerMethod() {
        return listerMethod;
    }

    public void setListerMethod(ListerMethod listerMethod) {
        this.listerMethod = listerMethod;
    }

    public ValidatorMethod getValidatorMethod() {
        return this.validatorMethod;
    }

    public void setValidatorMethod(ValidatorMethod validatorMethod) {
        this.validatorMethod = validatorMethod;
    }

    public AugmentationMethod getAugmentationMethod() {
        return augmentationMethod;
    }

    public void setAugmentationMethod(AugmentationMethod augmentationMethod) {
        this.augmentationMethod = augmentationMethod;
    }
    
    public void setLabellerMethod(LabellerMethod labellerMethod) {
        this.labellerMethod = labellerMethod;
    }

    public LabellerMethod getLabellerMethod() {
        return labellerMethod;
    }

	public boolean isZipOutput() {
		return isZipOutput;
	}

	public void setZipOutput(boolean isZipOutput) {
		this.isZipOutput = isZipOutput;
	}

	public boolean isComparingToFile() {
		return isComparingToFile;
	}

	public void setComparingToFile(boolean isComparingToFile) {
		this.isComparingToFile = isComparingToFile;
	}
}
