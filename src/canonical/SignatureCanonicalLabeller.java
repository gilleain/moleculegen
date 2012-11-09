package canonical;

import java.util.Arrays;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.signature.MoleculeSignature;

import signature.AbstractVertexSignature;

public class SignatureCanonicalLabeller implements CanonicalLabeller {

    private static final String SIG_SEPARATOR = "+";

    @Override
    public String getCanonicalStringForm(IAtomContainer atomContainer) {
        Object connectedProp = atomContainer.getProperty("IS_CONNECTED");
        if (connectedProp == null || ((Boolean) connectedProp)) {
            MoleculeSignature molSig = new MoleculeSignature(atomContainer);
            return molSig.toCanonicalString();
        } else {
            IAtomContainerSet molSet = 
                    ConnectivityChecker.partitionIntoMolecules(atomContainer);
            int acCount = molSet.getAtomContainerCount(); 
            String[] labelArray = new String[acCount];
            int componentIndex = 0;
            for (IAtomContainer component : molSet.atomContainers()) {
                String maxComponentLabel = null;
                MoleculeSignature molSig = new MoleculeSignature(component);
                for (int i = 0; i < component.getAtomCount(); i++) {
                    String sigStringForI = molSig.signatureStringForVertex(i);
                    if (maxComponentLabel == null || maxComponentLabel.compareTo(sigStringForI) < 0) {
                        maxComponentLabel = sigStringForI;
                    }
                }
                labelArray[componentIndex] = maxComponentLabel;
                componentIndex++;
            }
            Arrays.sort(labelArray);
            StringBuffer fullLabel = new StringBuffer();
            for (String label : labelArray) {
                if (fullLabel.length() == 0) {
                    fullLabel.append(label);
                } else {
                    fullLabel.append(SIG_SEPARATOR).append(label);
                }
            }
            return fullLabel.toString();
        }
    }

    @Override
    public int[] getLabels(IAtomContainer atomContainer) {
        int atomCount = atomContainer.getAtomCount();
        Object connectedProp = atomContainer.getProperty("IS_CONNECTED");
        if (connectedProp == null || ((Boolean) connectedProp)) {
            MoleculeSignature molSig = new MoleculeSignature(atomContainer);
            int[] extLabels = molSig.getCanonicalLabels();
            int[] labels = new int[atomCount];
            // XXX this is effectively reversing the permutation again - don't reverse in first place? 
            for (int i = 0; i < atomCount; i++) {
                labels[extLabels[i]] = i;
            }
            return labels;
        } else {
            IAtomContainerSet molSet = 
                    ConnectivityChecker.partitionIntoMolecules(atomContainer);
            int[] labels = new int[atomCount];
            int componentOffset = 0;

            for (IAtomContainer component : molSet.atomContainers()) {
                int componentSize = component.getAtomCount();
                AbstractVertexSignature maxSigForComponent = null;
                String maxSigStrForComponent = null;
                MoleculeSignature molSig = new MoleculeSignature(component);
                for (int i = 0; i < component.getAtomCount(); i++) {
                    AbstractVertexSignature sigForI = molSig.signatureForVertex(i);
                    String sigForIStr = sigForI.toCanonicalString();
                    if (maxSigForComponent == null || maxSigStrForComponent.compareTo(sigForIStr) < 0) {
                        maxSigForComponent = sigForI;
                        maxSigStrForComponent = sigForIStr;
                    }
                }
                int[] componentLabels = maxSigForComponent.getCanonicalLabelling(componentSize);
                for (int i = 0; i < component.getAtomCount(); i++) {
                    int j = componentLabels[i];
                    labels[componentOffset + j] = i;
                }
                componentOffset += componentSize;
            }
            return labels;
        }
    }
}
