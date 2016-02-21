package org.openscience.cdk.silent;

import org.openscience.cdk.DynamicFactory;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * A factory class to provide implementation independent {@link ICDKObject}s.
 * <p/>
 * <pre>{@code
 *     IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
 *
 *     IAtom a = builder.newInstance(IAtom.class);
 *     IAtom c12 = builder.newInstance(IAtom.class, "C");
 *     IAtom c13 = builder.newInstance(IAtom.class,
 *                                     builder.newInstance(IIsotope.class,
 *                                                         "C", 13));
 * }</pre>
 *
 * @author        egonw
 * @author        john may
 * @cdk.module    silent
 * @cdk.githash
 * @see DynamicFactory
 * Copied and modified -1) to expose private operations
 *    2) to implement more efficient implementations
 */
public class FastChemObjectBuilder  implements IChemObjectBuilder {

    protected static volatile IChemObjectBuilder instance = null;
    protected static final Object                LOCK     = new Object();

    /**
     * Access the singleton instance of this SilentChemObjectBuilder. <p/>
     * <pre>{@code
     *
     * // get the builder instance
     * IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
     *
     * // using the builder...
     * // create an IAtom using the default constructor
     * IAtom atom = builder.newInstance(IAtom.class);
     *
     * // create a carbon atom
     * IAtom c1 = builder.newInstance(IAtom.class, "C");
     * }</pre>
     *
     * @return a SilentChemObjectBuilder instance
     */
    public static IChemObjectBuilder getInstance() {
        IChemObjectBuilder result = instance;
        if (result == null) {
            result = instance;
            synchronized (LOCK) {
                if (result == null) {
                    instance = result = new FastChemObjectBuilder();
                }
            }
        }
        return result;
    }
    protected final DynamicFactory factory  = new DynamicFactory(200);

    protected FastChemObjectBuilder() {

        // self reference required for stereo-elements
        final IChemObjectBuilder self = this;

        // elements
        factory.register(IAtom.class, Atom.class);
        factory.register(IPseudoAtom.class, PseudoAtom.class);
        factory.register(IElement.class, Element.class);
        factory.register(IAtomType.class, AtomType.class);
        factory.register(IFragmentAtom.class, FragmentAtom.class);
        factory.register(IPDBAtom.class, PDBAtom.class);
        factory.register(IIsotope.class, Isotope.class);

        // electron containers
        factory.register(IBond.class, Bond.class);
        factory.register(IElectronContainer.class, ElectronContainer.class);
        factory.register(ISingleElectron.class, SingleElectron.class);
        factory.register(ILonePair.class, LonePair.class);

        // atom containers
        factory.register(IAtomContainer.class, AtomContainer.class);
        factory.register(IRing.class, Ring.class);
        factory.register(ICrystal.class, Crystal.class);
        factory.register(IPolymer.class, Polymer.class);
        factory.register(IPDBPolymer.class, PDBPolymer.class);
        factory.register(IMonomer.class, Monomer.class);
        factory.register(IPDBMonomer.class, PDBMonomer.class);
        factory.register(IBioPolymer.class, BioPolymer.class);
        factory.register(IPDBStructure.class, PDBStructure.class);
        factory.register(IAminoAcid.class, AminoAcid.class);
        factory.register(IStrand.class, Strand.class);

        // reactions
        factory.register(IReaction.class, Reaction.class);
        factory.register(IReactionScheme.class, ReactionScheme.class);

        // formula
        factory.register(IMolecularFormula.class, MolecularFormula.class);
        factory.register(IAdductFormula.class, AdductFormula.class);

        // chem object sets
        factory.register(IAtomContainerSet.class, AtomContainerSet.class);
        factory.register(IMolecularFormulaSet.class, MolecularFormulaSet.class);
        factory.register(IReactionSet.class, ReactionSet.class);
        factory.register(IRingSet.class, RingSet.class);
        factory.register(IChemModel.class, ChemModel.class);
        factory.register(IChemFile.class, ChemFile.class);
        factory.register(IChemSequence.class, ChemSequence.class);
        factory.register(ISubstance.class, Substance.class);

        // stereo components (requires some modification after instantiation)
        factory.register(ITetrahedralChirality.class, TetrahedralChirality.class,
                new DynamicFactory.CreationModifier<TetrahedralChirality>() {

                    @Override
                    public void modify(TetrahedralChirality instance) {
                        instance.setBuilder(self);
                    }
                });
        factory.register(IDoubleBondStereochemistry.class, DoubleBondStereochemistry.class,
                new DynamicFactory.CreationModifier<DoubleBondStereochemistry>() {

                    @Override
                    public void modify(DoubleBondStereochemistry instance) {
                        instance.setBuilder(self);
                    }
                });

        // miscellaneous
        factory.register(IMapping.class, Mapping.class);
        factory.register(IChemObject.class, ChemObject.class);

    }



    /**
     * @inheritDoc
     */
    @Override
    public <T extends ICDKObject> T newInstance(Class<T> clazz, Object... params) throws IllegalArgumentException {
        return factory.ofClass(clazz, params);
    }

}
