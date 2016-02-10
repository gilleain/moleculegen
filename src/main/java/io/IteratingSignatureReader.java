package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.signature.AtomSignature;
import org.openscience.cdk.signature.MoleculeFromSignatureBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

public class IteratingSignatureReader extends DefaultIteratingChemObjectReader<IAtomContainer> {
    
    private BufferedReader input;
    
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(IteratingSMILESReader.class);
    
    private String currentLine;
    
    private boolean nextAvailableIsKnown;
    
    private boolean hasNext;
    
    private IAtomContainer nextMolecule;
    
    private MoleculeFromSignatureBuilder molBuilder;
    
    private IChemObjectBuilder builder;
    
    /**
     * Constructs a new IteratingSignatureReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder
     */
    public IteratingSignatureReader(Reader in, IChemObjectBuilder builder) {
        this.builder = builder;
        molBuilder = new MoleculeFromSignatureBuilder(builder);
        setReader(in);
    }
    
    /**
     * Constructs a new IteratingSignatureReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     */
    public IteratingSignatureReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    @Override
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        // XXX signature 'format' not defined...
        return null;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            // now try to parse the next Molecule
            try {
                if (input.ready()) {
                    currentLine = input.readLine().trim();
                    logger.debug("Line: ", currentLine);

                    String signatureString = currentLine;
                    
                    // XXX annoying, but the signature builder is broken...
                    molBuilder = new MoleculeFromSignatureBuilder(builder);
                    molBuilder.makeFromColoredTree(AtomSignature.parse(signatureString));
                    IAtomContainer builtMol = molBuilder.getAtomContainer();
                    
                    // XXX fix for the problem of bonds with the wrong atom order...
                    for (IBond bond : builtMol.bonds()) {
                        IAtom a0 = bond.getAtom(0);
                        IAtom a1 = bond.getAtom(1);
                        if (builtMol.getAtomNumber(a0) > builtMol.getAtomNumber(a1)) {
                            bond.setAtom(a0, 1);
                            bond.setAtom(a1, 0);
                        }
                    }
                    nextMolecule = builtMol;
                    if (nextMolecule.getAtomCount() > 0) {
                        hasNext = true;
                    } else {
                        hasNext = false;
                    }
                } else {
                    hasNext = false;
                }
            } catch (Exception exception) {
                logger.error("Error while reading next molecule: ", exception.getMessage());
                logger.debug(exception);
                hasNext = false;
            }
            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    @Override
    public IAtomContainer next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }
    
    public void setReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            input = (BufferedReader)reader;
        } else {
            input = new BufferedReader(reader);
        }
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }
    
    public void setReader(InputStream reader) {
        setReader(new InputStreamReader(reader));
    }

}
