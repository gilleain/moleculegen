package generate;

import group.SSPermutationGroup;
import handler.GenerateHandler;
import handler.PrintStreamHandler;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomAugmentingGenerator {
	
	private GenerateHandler handler;
	
	public AtomAugmentingGenerator() {
		this(new PrintStreamHandler());
	}
	
	public AtomAugmentingGenerator(GenerateHandler handler) {
		this.handler = handler;
	}
	
	public void extend(IAtomContainer parent, int currentAtomIndex, int size) {
		int maxDegreeForCurrent = getMaxDegree(parent.getAtom(currentAtomIndex));
		SSPermutationGroup autG = getGroup(parent);
		List<IAtomContainer> children = new ArrayList<IAtomContainer>();
		for (List<Integer> multiset : getMultisets(parent, maxDegreeForCurrent)) {
			if (isMinimal(multiset, autG)) {
				makeChild(parent, multiset, children);
			}
		}
		
		for (IAtomContainer child : children) {
			if (isCanonical(child)) {
				if (isConnected(child) && isValidMol(child)) {
					handler.handle(parent, child);
				}
				extend(child, currentAtomIndex + 1, size);
			}
		}
	}

	private boolean isValidMol(IAtomContainer child) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isConnected(IAtomContainer child) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isCanonical(IAtomContainer child) {
		// TODO Auto-generated method stub
		return false;
	}

	private void makeChild(
			IAtomContainer parent, List<Integer> multiset, List<IAtomContainer> children) {
		// TODO Auto-generated method stub
		
	}

	private boolean isMinimal(List<Integer> multiset, SSPermutationGroup autG) {
		// TODO Auto-generated method stub
		return false;
	}

	private List<List<Integer>> getMultisets(IAtomContainer parent, int maxDegreeForCurrent) {
		// TODO Auto-generated method stub
		return null;
	}

	private SSPermutationGroup getGroup(IAtomContainer parent) {
		// TODO Auto-generated method stub
		return null;
	}

	private int getMaxDegree(IAtom atom) {
		// TODO Auto-generated method stub
		return 0;
	}

}
