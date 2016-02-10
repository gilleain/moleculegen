package app;

import io.AtomContainerPrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class DiffTool {
    
    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    public static void main(String[] args) throws IOException {
        if (args.length < 3) return;
        String format    = args[0];
        String filenameA = args[1];
        String filenameB = args[2];
        
        List<IAtomContainer> listA = read(format, filenameA);
        List<IAtomContainer> listB = read(format, filenameB);
        if (listA.size() < listB.size()) {
            diff(listA, listB);
        } else {
            diff(listB, listA);
        }
    }

    private static void diff(List<IAtomContainer> listA, List<IAtomContainer> listB) {
        Map<String, IAtomContainer> mapA = new HashMap<String, IAtomContainer>();
        for (IAtomContainer ac : listA) {
            String cert = new MoleculeSignature(ac).toCanonicalString();
            mapA.put(cert, ac);
        }
        List<IAtomContainer> diffList = new ArrayList<IAtomContainer>();
        for (IAtomContainer ac : listB) {
            String cert = new MoleculeSignature(ac).toCanonicalString();
            if (mapA.containsKey(cert)) {
                continue;
            } else {
                diffList.add(ac);
            }
        }
        int count = 0;
        for (IAtomContainer ac : diffList) {
            System.out.println(count + "\t" + AtomContainerPrinter.toString(ac));
            count++;
        }
    }

    private static List<IAtomContainer> read(String format, String filename) throws IOException {
        if (format.equals("SMI")) {
            return readSmiles(filename);
        } else if (format.equals("ACP")) {
            return readACP(filename);
        } else {
            return new ArrayList<IAtomContainer>();
        }
    }
    
    private static List<IAtomContainer> readACP(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        List<IAtomContainer> list = new ArrayList<IAtomContainer>();
        while ((line = in.readLine()) != null) {
            String s = line.trim();
            list.add(AtomContainerPrinter.fromString(s, builder));
        }
        in.close();
        return list;
    }
    
    private static List<IAtomContainer> readSmiles(String filename) throws IOException {
        Reader in = new FileReader(filename);
        IIteratingChemObjectReader<IAtomContainer> reader = 
                new IteratingSMILESReader(in, builder);
        List<IAtomContainer> list = new ArrayList<IAtomContainer>();
        while (reader.hasNext()) {
            IAtomContainer ac = reader.next();
            for (IBond bond : ac.bonds()) {
                IAtom a0 = bond.getAtom(0);
                IAtom a1 = bond.getAtom(1);
                int a0n = ac.getAtomNumber(a0);
                int a1n = ac.getAtomNumber(a1);
                if (a0n > a1n) {
                    bond.setAtom(a1, 0);
                    bond.setAtom(a0, 1);
                }
            }
            list.add(ac);
        }
        reader.close();
        return list;
    }

}
