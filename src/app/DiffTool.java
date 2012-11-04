package app;

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

import test.AtomContainerPrinter;

public class DiffTool {
    
    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    public static void main(String[] args) throws IOException {
        if (args.length < 2) return;
        String filenameA = args[0];
        String filenameB = args[1];
        
        List<IAtomContainer> listA = read(filenameA);
        List<IAtomContainer> listB = read(filenameB);
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

    private static List<IAtomContainer> read(String filenameA) throws IOException {
        Reader in = new FileReader(filenameA);
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
        return list;
    }

}
