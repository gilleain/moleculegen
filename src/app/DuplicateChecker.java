package app;

import io.AtomContainerPrinter;
import io.IteratingACPReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class DuplicateChecker {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length != 1) return;   // TODO
        
        String filename = args[0];
        InputStream in = new FileInputStream(filename);
        IteratingACPReader reader = new IteratingACPReader(in, builder);
        
        // map signature strings to lists of atom containers
        Map<String, List<IAtomContainer>> duplicateMap = 
            new HashMap<String, List<IAtomContainer>>();
        
        // read in from the file, and add to the map
        while (reader.hasNext()) {
            IAtomContainer atomContainer = reader.next();
            String signature = new MoleculeSignature(atomContainer).toCanonicalString();
            List<IAtomContainer> duplicates; 
            if (duplicateMap.containsKey(signature)) {
                duplicates = duplicateMap.get(signature);
            } else {
                duplicates = new ArrayList<IAtomContainer>();
                duplicateMap.put(signature, duplicates);
            }
            duplicates.add(atomContainer);
        }
        
        // now print out the duplicates
        int count = 0;
        for (String signature : duplicateMap.keySet()) {
            List<IAtomContainer> bin = duplicateMap.get(signature);
            if (bin.size() > 1) {
                System.out.println("Bin : " + count + " Size " + bin.size());
                for (IAtomContainer atomContainer : bin) {
                    System.out.println(AtomContainerPrinter.toString(atomContainer));
                }
                System.out.println("-----------------");
                count++;
            }
        }
        reader.close();
        
    }

}
