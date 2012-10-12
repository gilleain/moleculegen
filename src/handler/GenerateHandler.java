package handler;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Handle a generated molecule.  
 * 
 * @author maclean
 *
 */
public interface GenerateHandler {
	
	public void handle(IAtomContainer parent, IAtomContainer child);
	
	public void finish();

}
