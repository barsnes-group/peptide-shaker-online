package com.uib.web.peptideshaker.model.core;

import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidPattern;
import com.compomics.util.experiment.biology.atoms.Atom;
import com.compomics.util.experiment.biology.atoms.AtomChain;
import com.compomics.util.experiment.biology.atoms.AtomImpl;
import com.compomics.util.experiment.biology.ions.NeutralLoss;
import com.compomics.util.experiment.biology.ions.impl.ReporterIon;
import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationType;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.compomics.util.parameters.identification.search.ModificationParameters;
import com.compomics.util.pride.CvTerm;
import java.awt.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This factory will load Modification from an XML file and provide them on
 * demand as standard class.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class ModificationFactory {

    /**
     * A map linking indexes with modifications.
     */
    private final HashMap<String, Modification> modificationMap = new HashMap<>();
    /**
     * List of the indexes of default modifications.
     */
    private final ArrayList<String> defaultMods = new ArrayList<>();
    /**
     * List of the indexes of user modifications.
     */
    private final ArrayList<String> userMods = new ArrayList<>();
    /**
     * Mapping of the expected modification names to the color used.
     */
    private final HashMap<String, Integer> userColors = new HashMap<>();
    /**
     * Map of modification names mapping to a given PSI-MOD accession number
     * (key provided without the "MOD:" part).
     */
    private final HashMap<String, ArrayList<String>> psiModMap = new HashMap<>();
    /**
     * Suffix for the modification clone targeting a single amino acid instead
     * of a pattern.
     */
    public final String SINGLE_AA_SUFFIX = "|single_aa";
    /**
     * Set to true if the default mods are sorted alphabetically.
     */
    public boolean defaultModsSorted = false;
    /**
     * Set to true if the users mods are sorted alphabetically.
     */
    public boolean usersModsSorted = false;

    /**
     * Constructor for the factory.
     */
    public ModificationFactory() {
        defaultModsSorted = false;
        setDefaultModifications();
    }

   

    
    /**
     * Returns a clone of the given Modification targeting a single amino acid
     * instead of a pattern.
     *
     * @param modification the modification of interest
     *
     * @return a clone of the given Modification targeting a single amino acid
     * instead of a pattern
     */
    public Modification getSingleAAModification(Modification modification) {
        if (!modification.isStandardSearch()) {
            return new Modification(modification.getModificationType(), modification.getShortName(),
                    modification.getName() + SINGLE_AA_SUFFIX, modification.getAtomChainAdded(),
                    modification.getAtomChainRemoved(), modification.getPattern().getStandardSearchPattern());
        } else {
            return modification;
        }
    }

    /**
     * Returns a clone of the given Modification targeting a single amino acid
     * instead of a pattern.
     *
     * @param modificationName the name of the modification of interest
     *
     * @return a clone of the given Modification targeting a single amino acid
     * instead of a pattern
     */
    public Modification getSingleAAModification(String modificationName) {
        Modification modification = getModification(modificationName);
        return getSingleAAModification(modification);
    }

    /**
     * Adds a new user modification.
     *
     * @param modification the new modification to add
     */
    public void addUserModification(Modification modification) {

        String modn_proteiname = modification.getName();
        modificationMap.put(modn_proteiname, modification);
        if (!userMods.contains(modn_proteiname)) {
            userMods.add(modn_proteiname);
        } else {
            userMods.set(userMods.indexOf(modn_proteiname), modn_proteiname);
        }
        usersModsSorted = false;

        // add the neutral losses and reporter ions
        for (NeutralLoss neutralLoss : modification.getNeutralLosses()) {
            if (NeutralLoss.getNeutralLoss(neutralLoss.name) == null) {
                NeutralLoss.addNeutralLoss(neutralLoss);
            }
        }
        for (ReporterIon reporterIon : modification.getReporterIons()) {
            if (ReporterIon.getReporterIon(reporterIon.getName()) == null) {
                ReporterIon.addReporterIon(reporterIon);
            }
        }
    }

    /**
     * Removes a user Modification.
     *
     * @param modificationName the name of the Modification to remove
     */
    public void removeUserPtm(String modificationName) {
        if (defaultMods.contains(modificationName)) {
            throw new IllegalArgumentException("Impossible to remove default modification " + modificationName);
        }
        modificationMap.remove(modificationName);
        userMods.remove(modificationName);
    }

    /**
     * Returns the Modification indexed by its name. Null if none found.
     *
     * @param name the name of the modification of interest
     *
     * @return the modification of interest
     */
    public Modification getModification(String name) {
        return modificationMap.get(name);
    }

    /**
     * Returns a boolean indicating whether the Modification is loaded in the
     * factory.
     *
     * @param name the name of the Modification
     * @return a boolean indicating whether the Modification is loaded in the
     * factory
     */
    public boolean containsModification(String name) {
        return modificationMap.containsKey(name);
    }

    /**
     * Returns the names of the default modifications.
     *
     * @return the names of the default modifications
     */
    public ArrayList<String> getDefaultModifications() {
        return defaultMods;
    }

    /**
     * Returns the alphabetically ordered names of the default modifications.
     *
     * @return the alphabetically ordered names of the default modifications
     */
    public ArrayList<String> getDefaultModificationsOrdered() {
        if (!defaultModsSorted) {
            Collections.sort(defaultMods);
            defaultModsSorted = true;
        }
        return defaultMods;
    }

    /**
     * Returns the names of the user defined modifications.
     *
     * @return the names of the user defined modifications
     */
    public ArrayList<String> getUserModifications() {
        return userMods;
    }

    /**
     * Returns the alphabetically ordered names of the user defined
     * modifications.
     *
     * @return the alphabetically ordered names of the user defined
     * modifications
     */
    public ArrayList<String> getUserModificationsOrdered() {
        if (!usersModsSorted) {
            Collections.sort(userMods);
            usersModsSorted = true;
        }
        return userMods;
    }

    /**
     * Returns the names of all imported Modifications.
     *
     * @return the names of all imported Modifications
     */
    public ArrayList<String> getModifications() {
        return new ArrayList<>(modificationMap.keySet());
    }

    /**
     * Convenience method returning a boolean indicating whether a Modification
     * is user defined or default.
     *
     * @param modificationName the name of the Modification
     * @return boolean indicating whether a Modification is user defined
     */
    public boolean isUserDefined(String modificationName) {
        return !defaultMods.contains(modificationName);
    }

    /**
     * Verifies that the modifications backed-up in the search parameters are
     * loaded and alerts the user in case conflicts are found.
     *
     * @param searchParameters the search parameters to load
     * @param overwrite if true, overwrite the modification
     * @return returns a list of modifications already loaded which should be
     * checked.
     */
    public ArrayList<String> loadBackedUpModifications(SearchParameters searchParameters, boolean overwrite) {
        ModificationParameters modificationProfile = searchParameters.getModificationParameters();
        ArrayList<String> toCheck = new ArrayList<>();
        for (String newModificationName : modificationProfile.getBackedUpModifications().keySet()) {
            if (containsModification(newModificationName)) {
                Modification oldModification = getModification(newModificationName);
                Modification newModification = modificationProfile.getModification(newModificationName);
                if (!oldModification.isSameAs(newModification)) {
                    toCheck.add(newModificationName);
                    if (overwrite) {
                        modificationMap.put(newModificationName, newModification);
                        for (NeutralLoss neutralLoss : newModification.getNeutralLosses()) {
                            NeutralLoss.addNeutralLoss(neutralLoss);
                        }
                    }
                }
            } else {
                Modification modification = modificationProfile.getModification(newModificationName);
                addUserModification(modification);
                for (NeutralLoss neutralLoss : modification.getNeutralLosses()) {
                    NeutralLoss.addNeutralLoss(neutralLoss);
                }
            }
        }
        return toCheck;
    }

    /**
     * Returns the color used to code the given modification.
     *
     * @param modification the name of the given expected modification
     * @return the corresponding color
     */
    public int getColor(String modification) {
        if (!userColors.containsKey(modification)) {
            setColor(modification, getDefaultColor(modification));
        }
        return userColors.get(modification);
    }

    /**
     * Sets a new color for the given expected modification.
     *
     * @param expectedModification the name of the expected modification
     * @param color the new color
     */
    public void setColor(String expectedModification, int color) {
        userColors.put(expectedModification, color);
    }

    /**
     * Returns a default color based on the modification name.
     *
     * @param modification the name of the modification
     * @return a default color.
     */
    public int getDefaultColor(String modification) {
        if (modification.contains("no modification")) {
            return Color.LIGHT_GRAY.getRGB();
        } else if (modification.toLowerCase().contains("phospho")) {
            return Color.RED.getRGB();
        } else if (modification.toLowerCase().contains("pyro")) {
            return new Color(255, 102, 51).getRGB();
        } else if (modification.toLowerCase().contains("ox")) {
            return Color.BLUE.getRGB();
        } else if (modification.toLowerCase().contains("itraq")) {
            return Color.ORANGE.getRGB();
        } else if (modification.toLowerCase().contains("tmt")) {
            return Color.ORANGE.getRGB();
        } else if (modification.toLowerCase().contains("carbamido")) {
            return Color.LIGHT_GRAY.getRGB();
        } else if (modification.toLowerCase().contains("ace")) {
            return new Color(153, 153, 0).getRGB();
        } else if (modification.toLowerCase().contains("glyco")) {
            return Color.MAGENTA.getRGB();
        } else {
            float r = (float) Math.random();
            float g = (float) Math.random();
            float b = (float) Math.random();
            return new Color(r, g, b).getRGB();
        }
    }

    /**
     * Tries to convert a PRIDE Modification to utilities Modification name, and
     * add it to the modification profile. Unknown Modifications are added to
     * the unknown Modifications arraylist.
     *
     * @param pridePtmName the PRIDE Modification name
     * @param modProfile the modification profile to add the Modifications to
     * @param unknownPtms the list of unknown ModificationS, updated during this
     * method
     * @param isFixed if true, the Modification will be added as a fixed
     * modification
     * @return a pride parameters report as a string (for use in PRIDE Reshake)
     */
    public String convertPridePtm(String pridePtmName, ModificationParameters modProfile, ArrayList<String> unknownPtms, boolean isFixed) {

        String prideParametersReport = "";

        // special cases for when multiple modifications are needed
        if (pridePtmName.equalsIgnoreCase("iTRAQ4plex")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex reporter+balance reagent N-acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-114 reporter+balance reagent N6-acylated lysine")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-114 reporter+balance reagent O4&apos;-acylated tyrosine")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-114 reporter+balance reagent acylated N-terminal")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-114 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-116 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ4plex-117 reporter+balance reagent N6-acylated lysine")) {

            if (!modProfile.contains("iTRAQ 4-plex of K")) {
                modProfile.addFixedModification(getModification("iTRAQ 4-plex of K"));
                prideParametersReport += "<br>" + "iTRAQ 4-plex of K" + " (assumed fixed)";
            }
            if (!modProfile.contains("iTRAQ 4-plex of N-term")) {
                modProfile.addFixedModification(getModification("iTRAQ 4-plex of N-term"));
                prideParametersReport += "<br>" + "iTRAQ 4-plex of N-term" + " (assumed fixed)";
            }
            if (!modProfile.contains("iTRAQ 4-plex of Y")) {
                modProfile.addVariableModification(getModification("iTRAQ 4-plex of Y"));
                prideParametersReport += "<br>" + "iTRAQ 4-plex of Y" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("iTRAQ8plex")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-113 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-114 reporter+balance reagent N6-acylated lysine")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-114 reporter+balance reagent O4&apos;-acylated tyrosine")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-114 reporter+balance reagent acylated N-terminal")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-115 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex-116 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("iTRAQ8plex:13C(6)15N(2)")) {

            if (!modProfile.contains("iTRAQ 8-plex of K")) {
                modProfile.addFixedModification(getModification("iTRAQ 8-plex of K"));
                prideParametersReport += "<br>" + "iTRAQ 8-plex of K" + " (assumed fixed)";
            }
            if (!modProfile.contains("iTRAQ 8-plex of N-term")) {
                modProfile.addFixedModification(getModification("iTRAQ 8-plex of N-term"));
                prideParametersReport += "<br>" + "iTRAQ 8-plex of N-term" + " (assumed fixed)";
            }
            if (!modProfile.contains("iTRAQ 8-plex of Y")) {
                modProfile.addVariableModification(getModification("iTRAQ 8-plex of Y"));
                prideParametersReport += "<br>" + "iTRAQ 8-plex of Y" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("TMT2plex") || pridePtmName.equalsIgnoreCase("TMTduplex")) {

            if (!modProfile.contains("TMT 2-plex of K")) {
                modProfile.addFixedModification(getModification("TMT 2-plex of K"));
                prideParametersReport += "<br>" + "TMT 2-plex of K" + " (assumed fixed)";
            }
            if (!modProfile.contains("TMT 2-plex of N-term")) {
                modProfile.addFixedModification(getModification("TMT 2-plex of N-term"));
                prideParametersReport += "<br>" + "TMT 2-plex of N-term" + " (assumed fixed)";
            }

        } else if (pridePtmName.equalsIgnoreCase("TMT6plex")
                || pridePtmName.equalsIgnoreCase("TMT6plex-126 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("TMT6plex-131 reporter+balance reagent N6-acylated lysine")) {

            if (!modProfile.contains("TMT 6-plex of K")) {
                modProfile.addFixedModification(getModification("TMT 6-plex of K"));
                prideParametersReport += "<br>" + "TMT 6-plex of K" + " (assumed fixed)";
            }
            if (!modProfile.contains("TMT 6-plex of N-term")) {
                modProfile.addFixedModification(getModification("TMT 6-plex of N-term"));
                prideParametersReport += "<br>" + "TMT 6-plex of N-term" + " (assumed fixed)";
            }

        } else if (pridePtmName.equalsIgnoreCase("TMT10plex")
                || pridePtmName.equalsIgnoreCase("TMT10plex-126 reporter+balance reagent acylated residue")
                || pridePtmName.equalsIgnoreCase("TMT10plex-131 reporter+balance reagent N6-acylated lysine")) {

            if (!modProfile.contains("TMT 10-plex of K")) {
                modProfile.addFixedModification(getModification("TMT 10-plex of K"));
                prideParametersReport += "<br>" + "TMT 10-plex of K" + " (assumed fixed)";
            }
            if (!modProfile.contains("TMT 10-plex of N-term")) {
                modProfile.addFixedModification(getModification("TMT 10-plex of N-term"));
                prideParametersReport += "<br>" + "TMT 10-plex of N-term" + " (assumed fixed)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Phosphorylation")
                || pridePtmName.equalsIgnoreCase("L-aspartic 4-phosphoric anhydride")
                || pridePtmName.equalsIgnoreCase("O-phosphorylated residue")
                || pridePtmName.equalsIgnoreCase("Phospho")
                || pridePtmName.equalsIgnoreCase("phosphorylated residue")) {

            if (!modProfile.contains("Phosphorylation of S")) {
                modProfile.addVariableModification(getModification("Phosphorylation of S"));
                prideParametersReport += "<br>" + "Phosphorylation of S" + " (assumed variable)";
            }
            if (!modProfile.contains("Phosphorylation of T")) {
                modProfile.addVariableModification(getModification("Phosphorylation of T"));
                prideParametersReport += "<br>" + "Phosphorylation of T" + " (assumed variable)";
            }
            if (!modProfile.contains("Phosphorylation of Y")) {
                modProfile.addVariableModification(getModification("Phosphorylation of Y"));
                prideParametersReport += "<br>" + "Phosphorylation of Y" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Palmitoylation")) {

            if (!modProfile.contains("Palmitoylation of C")) {
                modProfile.addVariableModification(getModification("Palmitoylation of C"));
                prideParametersReport += "<br>" + "Palmitoylation of C" + " (assumed variable)";
            }
            if (!modProfile.contains("Palmitoylation of K")) {
                modProfile.addVariableModification(getModification("Palmitoylation of K"));
                prideParametersReport += "<br>" + "Palmitoylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Palmitoylation of S")) {
                modProfile.addVariableModification(getModification("Palmitoylation of S"));
                prideParametersReport += "<br>" + "Palmitoylation of S" + " (assumed variable)";
            }
            if (!modProfile.contains("Palmitoylation of T")) {
                modProfile.addVariableModification(getModification("Palmitoylation of T"));
                prideParametersReport += "<br>" + "Palmitoylation of T" + " (assumed variable)";
            }
            if (!modProfile.contains("Palmitoylation of protein N-term")) {
                modProfile.addVariableModification(getModification("Palmitoylation of protein N-term"));
                prideParametersReport += "<br>" + "Palmitoylation of protein N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Formylation")) {

            if (!modProfile.contains("Formylation of K")) {
                modProfile.addVariableModification(getModification("Formylation of K"));
                prideParametersReport += "<br>" + "Formylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Formylation of S")) {
                modProfile.addVariableModification(getModification("Formylation of S"));
                prideParametersReport += "<br>" + "Formylation of S" + " (assumed variable)";
            }
            if (!modProfile.contains("Formylation of T")) {
                modProfile.addVariableModification(getModification("Formylation of T"));
                prideParametersReport += "<br>" + "Formylation of T" + " (assumed variable)";
            }
            if (!modProfile.contains("Formylation of peptide N-term")) {
                modProfile.addVariableModification(getModification("Formylation of peptide N-term"));
                prideParametersReport += "<br>" + "Formylation of peptide N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Carbamylation")
                || pridePtmName.equalsIgnoreCase("carbamoylated residue")) {

            if (!modProfile.contains("Carbamylation of K")) {
                modProfile.addVariableModification(getModification("Carbamylation of K"));
                prideParametersReport += "<br>" + "Carbamylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Carbamilation of protein N-term")) {
                modProfile.addVariableModification(getModification("Carbamilation of protein N-term"));
                prideParametersReport += "<br>" + "Carbamilation of protein N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("3x(12)C labeled N6-propanoyl-L-lysine")) {

            if (!modProfile.contains("Propionyl of K light")) {
                modProfile.addVariableModification(getModification("Propionyl of K light"));
                prideParametersReport += "<br>" + "Propionyl of K light" + " (assumed variable)";
            }
            if (!modProfile.contains("Propionyl of peptide N-term light")) {
                modProfile.addVariableModification(getModification("Propionyl of peptide N-term light"));
                prideParametersReport += "<br>" + "Propionyl of peptide N-term light" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("3x(13)C labeled N6-propanoyl-L-lysine")) {

            if (!modProfile.contains("Propionyl of K heavy")) {
                modProfile.addVariableModification(getModification("Propionyl of K heavy"));
                prideParametersReport += "<br>" + "Propionyl of K heavy" + " (assumed variable)";
            }
            if (!modProfile.contains("Propionyl of peptide N-term heavy")) {
                modProfile.addVariableModification(getModification("Propionyl of peptide N-term heavy"));
                prideParametersReport += "<br>" + "Propionyl of peptide N-term heavy" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("3x(2)H residue methyl ester")) {

            if (!modProfile.contains("Trideuterated Methyl Ester of D")) {
                modProfile.addVariableModification(getModification("Trideuterated Methyl Ester of D"));
                prideParametersReport += "<br>" + "Trideuterated Methyl Ester of D" + " (assumed variable)";
            }
            if (!modProfile.contains("Trideuterated Methyl Ester of E")) {
                modProfile.addVariableModification(getModification("Trideuterated Methyl Ester of E"));
                prideParametersReport += "<br>" + "Trideuterated Methyl Ester of E" + " (assumed variable)";
            }
            if (!modProfile.contains("Trideuterated Methyl Ester of K")) {
                modProfile.addVariableModification(getModification("Trideuterated Methyl Ester of K"));
                prideParametersReport += "<br>" + "Trideuterated Methyl Ester of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Trideuterated Methyl Ester of R")) {
                modProfile.addVariableModification(getModification("Trideuterated Methyl Ester of R"));
                prideParametersReport += "<br>" + "Trideuterated Methyl Ester of R" + " (assumed variable)";
            }
            if (!modProfile.contains("Trideuterated Methyl Ester of peptide C-term")) {
                modProfile.addVariableModification(getModification("Trideuterated Methyl Ester of peptide C-term"));
                prideParametersReport += "<br>" + "Trideuterated Methyl Ester of peptide C-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("6x(13)C labeled residue")) {

            if (!modProfile.contains("Arginine 13C6")) {
                modProfile.addVariableModification(getModification("Arginine 13C6"));
                prideParametersReport += "<br>" + "Arginine 13C6" + " (assumed variable)";
            }
            if (!modProfile.contains("Lysine 13C6")) {
                modProfile.addVariableModification(getModification("Lysine 13C6"));
                prideParametersReport += "<br>" + "Lysine 13C6" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Acetyl")
                || pridePtmName.equalsIgnoreCase("N-acetylated residue")
                || pridePtmName.equalsIgnoreCase("N-acylated residue")
                || pridePtmName.equalsIgnoreCase("acetylated residue")) {

            if (!modProfile.contains("Acetylation of K")) {
                modProfile.addVariableModification(getModification("Acetylation of K"));
                prideParametersReport += "<br>" + "Acetylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Acetylation of peptide N-term")) {
                modProfile.addVariableModification(getModification("Acetylation of peptide N-term"));
                prideParametersReport += "<br>" + "Acetylation of peptide N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("dimethylated residue")) {

            if (!modProfile.contains("Dimethylation of K")) {
                modProfile.addVariableModification(getModification("Dimethylation of K"));
                prideParametersReport += "<br>" + "Dimethylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Dimethylation of R")) {
                modProfile.addVariableModification(getModification("Dimethylation of R"));
                prideParametersReport += "<br>" + "Dimethylation of R" + " (assumed variable)";
            }
            if (!modProfile.contains("Dimethylation of peptide N-term")) {
                modProfile.addVariableModification(getModification("Dimethylation of peptide N-term"));
                prideParametersReport += "<br>" + "Dimethylation of peptide N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("trimethylated residue")) {

            if (!modProfile.contains("Trimethylation of K")) {
                modProfile.addVariableModification(getModification("Trimethylation of K"));
                prideParametersReport += "<br>" + "Trimethylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Trimethylation of R")) {
                modProfile.addVariableModification(getModification("Trimethylation of R"));
                prideParametersReport += "<br>" + "Trimethylation of R" + " (assumed variable)";
            }
            if (!modProfile.contains("Trimethylation of protein N-term A")) {
                modProfile.addVariableModification(getModification("Trimethylation of protein N-term A"));
                prideParametersReport += "<br>" + "Trimethylation of protein N-term A" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Acetylation")) {

            if (!modProfile.contains("Acetylation of K")) {
                modProfile.addVariableModification(getModification("Acetylation of K"));
                prideParametersReport += "<br>" + "Acetylation of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Acetylation of peptide N-term")) {
                modProfile.addVariableModification(getModification("Acetylation of peptide N-term"));
                prideParametersReport += "<br>" + "Acetylation of peptide N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Guanidination")) {

            if (!modProfile.contains("Guanidination of K")) {
                modProfile.addVariableModification(getModification("Guanidination of K"));
                prideParametersReport += "<br>" + "Guanidination of K" + " (assumed variable)";
            }
            if (!modProfile.contains("Guanidination of peptide N-term")) {
                modProfile.addVariableModification(getModification("Guanidination of peptide N-term"));
                prideParametersReport += "<br>" + "Guanidination of peptide N-term" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Methylthio")) {

            if (!modProfile.contains("Methylthio of N")) {
                modProfile.addVariableModification(getModification("Methylthio of N"));
                prideParametersReport += "<br>" + "Methylthio of N" + " (assumed variable)";
            }
            if (!modProfile.contains("Methylthio of D")) {
                modProfile.addVariableModification(getModification("Methylthio of D"));
                prideParametersReport += "<br>" + "Methylthio of D" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Sulfo")
                || pridePtmName.equalsIgnoreCase("sulfated residue")) {

            if (!modProfile.contains("Sulfonation of Y")) {
                modProfile.addVariableModification(getModification("Sulfonation of Y"));
                prideParametersReport += "<br>" + "Sulfonation of Y" + " (assumed variable)";
            }
            if (!modProfile.contains("Sulfonation of S")) {
                modProfile.addVariableModification(getModification("Sulfonation of S"));
                prideParametersReport += "<br>" + "Sulfonation of S" + " (assumed variable)";
            }
            if (!modProfile.contains("Sulfonation of T")) {
                modProfile.addVariableModification(getModification("Sulfonation of T"));
                prideParametersReport += "<br>" + "Sulfonation of T" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Deamination")
                || pridePtmName.equalsIgnoreCase("Deamidated")
                || pridePtmName.equalsIgnoreCase("deamidated L-glutamine")
                || pridePtmName.equalsIgnoreCase("deamidated residue")
                || pridePtmName.equalsIgnoreCase("deaminated residue")) {

            if (!modProfile.contains("Deamidation of N")) {
                modProfile.addVariableModification(getModification("Deamidation of N"));
                prideParametersReport += "<br>" + "Deamidation of N" + " (assumed variable)";
            }
            if (!modProfile.contains("Deamidation of Q")) {
                modProfile.addVariableModification(getModification("Deamidation of Q"));
                prideParametersReport += "<br>" + "Deamidation of Q" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("Dioxidation")) {

            if (!modProfile.contains("Dioxidation of M")) {
                modProfile.addVariableModification(getModification("Dioxidation of M"));
                prideParametersReport += "<br>" + "Dioxidation of M" + " (assumed variable)";
            }
            if (!modProfile.contains("Dioxidation of W")) {
                modProfile.addVariableModification(getModification("Dioxidation of W"));
                prideParametersReport += "<br>" + "Dioxidation of W" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("dehydrated residue")
                || pridePtmName.equalsIgnoreCase("Dehydratation")) {

            if (!modProfile.contains("Dehydration of S")) {
                modProfile.addVariableModification(getModification("Dehydration of S"));
                prideParametersReport += "<br>" + "Dehydration of S" + " (assumed variable)";
            }
            if (!modProfile.contains("Dehydration of T")) {
                modProfile.addVariableModification(getModification("Dehydration of T"));
                prideParametersReport += "<br>" + "Dehydration of T" + " (assumed variable)";
            }

        } else if (pridePtmName.equalsIgnoreCase("No Modifications are included in the dataset")) {
            // ignore
        } else {

            // single modification mapping
            String utilitiesPtmName = convertPridePtmToUtilitiesPtm(pridePtmName);

            if (utilitiesPtmName != null) {
                if (!modProfile.contains(utilitiesPtmName)) {
                    if (isFixed) {
                        modProfile.addFixedModification(getModification(utilitiesPtmName));
                        prideParametersReport += "<br>" + utilitiesPtmName + " (assumed fixed)";
                    } else {
                        modProfile.addVariableModification(getModification(utilitiesPtmName));
                        prideParametersReport += "<br>" + utilitiesPtmName + " (assumed variable)";
                    }
                }
            } else if (!unknownPtms.contains(pridePtmName)) {
                unknownPtms.add(pridePtmName);
            }
        }

        return prideParametersReport;
    }

    /**
     * Tries to convert a PRIDE Modification name to utilities Modification
     * name.
     *
     * @param pridePtmName the PRIDE Modification name
     * @return the utilities Modification name, or null if there is no mapping
     */
    private String convertPridePtmToUtilitiesPtm(String pridePtmName) {

        if (pridePtmName.equalsIgnoreCase("Carbamidomethyl")
                || pridePtmName.equalsIgnoreCase("S-carboxamidomethyl-L-cysteine")
                || pridePtmName.equalsIgnoreCase("iodoacetamide - site C")
                || pridePtmName.equalsIgnoreCase("iodoacetamide -site C")
                || pridePtmName.equalsIgnoreCase("iodoacetamide derivatized residue")
                || pridePtmName.equalsIgnoreCase("Iodoacetamide derivative")) {
            return "Carbamidomethylation of C";
        } else if (pridePtmName.equalsIgnoreCase("Oxidation")
                || pridePtmName.equalsIgnoreCase("monohydroxylated residue")
                || pridePtmName.equalsIgnoreCase("oxidized residue")) {
            return "Oxidation of M";
        } else if (pridePtmName.equalsIgnoreCase("Amidation")) {
            return "Amidation of the peptide C-term";
        } else if (pridePtmName.equalsIgnoreCase("Carboxymethyl")
                || pridePtmName.equalsIgnoreCase("S-carboxymethyl-L-cysteine")
                || pridePtmName.equalsIgnoreCase("iodoacetic acid derivatized residue")) {
            return "Carboxymethylation of C";
        } else if (pridePtmName.equalsIgnoreCase("Farnesylation")) {
            return "Farnesylation of C";
        } else if (pridePtmName.equalsIgnoreCase("Geranyl-geranyl")) {
            return "Geranyl-geranyl of C";
        } else if (pridePtmName.equalsIgnoreCase("Homoserine")) {
            return "Homoserine of peptide C-term M";
        } else if (pridePtmName.equalsIgnoreCase("Homoserine lactone")) {
            return "Homoserine lactone of peptide C-term M";
        } else if (pridePtmName.equalsIgnoreCase("ICAT-C")
                || pridePtmName.equalsIgnoreCase("Applied Biosystems cleavable ICAT(TM) light")) {
            return "ICAT-O";
        } else if (pridePtmName.equalsIgnoreCase("ICAT-C:13C(9)")
                || pridePtmName.equalsIgnoreCase("Applied Biosystems cleavable ICAT(TM) heavy")) {
            return "ICAT-9";
        } else if (pridePtmName.equalsIgnoreCase("Lipoyl")) {
            return "Lipoyl of K";
        } else if (pridePtmName.equalsIgnoreCase("NIPCAM(C)")) {
            return "NIPCAM of C";
        } else if (pridePtmName.equalsIgnoreCase("Phosphopantetheine")) {
            return "phosphopantetheine s";
        } else if (pridePtmName.equalsIgnoreCase("Propionamide(C)")
                || pridePtmName.equalsIgnoreCase("Acrylamide adduct")) {
            return "Propionamide of C";
        } else if (pridePtmName.equalsIgnoreCase("Pyridylethyl")) {
            return "Pyridylethyl of C";
        } else if (pridePtmName.equalsIgnoreCase("(18)O label at both C-terminal oxygens")) {
            return "18O(2) of peptide C-term";
        } else if (pridePtmName.equalsIgnoreCase("(18)O monosubstituted residue")) {
            return "18O(1) of peptide C-term";
        } else if (pridePtmName.equalsIgnoreCase("(4,4,5,5-(2)H4)-L-lysine")) {
            return "Lysine 2H4";
        } else if (pridePtmName.equalsIgnoreCase("2-pyrrolidone-5-carboxylic acid (Gln)")
                || pridePtmName.equalsIgnoreCase("Ammonia-loss")) {
            return "Pyrolidone from Q";
        } else if (pridePtmName.equalsIgnoreCase("2-pyrrolidone-5-carboxylic acid (Glu)")
                || pridePtmName.equalsIgnoreCase("Glu->pyro-Glu")) {
            return "Pyrolidone from E";
        } else if (pridePtmName.equalsIgnoreCase("3-hydroxy-L-proline")) {
            return "Oxidation of P";
        } else if (pridePtmName.equalsIgnoreCase("3x(2)H labeled L-aspartic acid 4-methyl ester")) {
            return "Trideuterated Methyl Ester of D";
        } else if (pridePtmName.equalsIgnoreCase("4x(2)H labeled alpha-dimethylamino N-terminal residue")) {
            return "Dimethylation of peptide N-term 2H(4)";
        } else if (pridePtmName.equalsIgnoreCase("4x(2)H labeled dimethylated L-lysine")) {
            return "Dimethylation of K 2H(4)";
        } else if (pridePtmName.equalsIgnoreCase("5-methyl-L-arginine")) {
            return "Methylation of R";
        } else if (pridePtmName.equalsIgnoreCase("6x(13)C labeled L-arginine")) {
            return "Arginine 13C6";
        } else if (pridePtmName.equalsIgnoreCase("6x(13)C,4x(15)N labeled L-arginine")) {
            return "Arginine 13C6 15N4";
        } else if (pridePtmName.equalsIgnoreCase("6x(13)C labeled L-lysine")) {
            return "Lysine 13C6";
        } else if (pridePtmName.equalsIgnoreCase("6x(13)C,2x(15)N labeled L-lysine")) {
            return "Lysine 13C6 15N2";
        } else if (pridePtmName.equalsIgnoreCase("L-aspartic acid 4-methyl ester")) {
            return "Methylation of D";
        } else if (pridePtmName.equalsIgnoreCase("L-cysteic acid (L-cysteine sulfonic acid)")) {
            return "Oxidation of C";
        } else if (pridePtmName.equalsIgnoreCase("L-cysteine glutathione disulfide")) {
            return "Glutathione of C";
        } else if (pridePtmName.equalsIgnoreCase("L-cysteine methyl disulfide")
                || pridePtmName.equalsIgnoreCase("methyl methanethiosulfonate")) {
            return "Methylthio of C";
        } else if (pridePtmName.equalsIgnoreCase("L-cystine (cross-link)")) {
            return "Didehydro of T";
        } else if (pridePtmName.equalsIgnoreCase("L-glutamic acid 5-methyl ester (Glu)")
                || pridePtmName.equalsIgnoreCase("methylated glutamic acid")) {
            return "Methylation of E";
        } else if (pridePtmName.equalsIgnoreCase("L-homoarginine")) {
            return "Guanidination of K";
        } else if (pridePtmName.equalsIgnoreCase("L-methionine (R)-sulfoxide")
                || pridePtmName.equalsIgnoreCase("L-methionine (S)-sulfoxide")
                || pridePtmName.equalsIgnoreCase("L-methionine sulfoxide")) {
            return "Oxidation of M";
        } else if (pridePtmName.equalsIgnoreCase("L-methionine sulfone")) {
            return "Dioxidation of M";
        } else if (pridePtmName.equalsIgnoreCase("N-acetyl-L-asparagine")
                || pridePtmName.equalsIgnoreCase("N-acetyl-L-cysteine")
                || pridePtmName.equalsIgnoreCase("N-acetyl-L-glutamic acid")
                || pridePtmName.equalsIgnoreCase("N-acetyl-L-isoleucine")
                || pridePtmName.equalsIgnoreCase("N-acetyl-L-serine")
                || pridePtmName.equalsIgnoreCase("N-acetyl-L-tyrosine")
                || pridePtmName.equalsIgnoreCase("N2-acetyl-L-tryptophan")
                || pridePtmName.equalsIgnoreCase("alpha-amino acetylated residue")) {
            return "Acetylation of protein N-term";
        } else if (pridePtmName.equalsIgnoreCase("N-acetylated L-lysine")
                || pridePtmName.equalsIgnoreCase("N6-acetyl-L-lysine")) {
            return "Acetylation of K";
        } else if (pridePtmName.equalsIgnoreCase("N-ethylmaleimide derivatized cysteine")) {
            return "Nethylmaleimide of C";
        } else if (pridePtmName.equalsIgnoreCase("N-formyl-L-methionine")) {
            return "FormylMet of protein N-term";
        } else if (pridePtmName.equalsIgnoreCase("N-formylated residue")) {
            return "Formylation of peptide N-term"; // note: could also be the other formylations
        } else if (pridePtmName.equalsIgnoreCase("N-methyl-L-serine")) {
            return "Methylation of S";
        } else if (pridePtmName.equalsIgnoreCase("N6,N6-dimethyl-L-lysine")) {
            return "Dimethylation of K";
        } else if (pridePtmName.equalsIgnoreCase("N6-formyl-L-lysine")) {
            return "Formylation of K";
        } else if (pridePtmName.equalsIgnoreCase("N6-methyl-L-lysine")
                || pridePtmName.equalsIgnoreCase("methylated lysine")
                || pridePtmName.equalsIgnoreCase("monomethylated L-lysine")) {
            return "Methylation of K";
        } else if (pridePtmName.equalsIgnoreCase("N6-propanoyl-L-lysine")) {
            return "Propionyl of K light";
        } else if (pridePtmName.equalsIgnoreCase("O-(N-acetylamino)glucosyl-L-serine")) {
            return "HexNAc of S";
        } else if (pridePtmName.equalsIgnoreCase("O-(N-acetylamino)glucosyl-L-threonine")) {
            return "HexNAc of T";
        } else if (pridePtmName.equalsIgnoreCase("O-phospho-L-serine")) {
            return "Phosphorylation of S";
        } else if (pridePtmName.equalsIgnoreCase("O-phospho-L-threonine")) {
            return "Phosphorylation of T";
        } else if (pridePtmName.equalsIgnoreCase("O4&apos;-phospho-L-tyrosine")) {
            return "Phosphorylation of Y";
        } else if (pridePtmName.equalsIgnoreCase("S-carboxamidoethyl-L-cysteine")) {
            return "Propionamide of C";
        } else if (pridePtmName.equalsIgnoreCase("S-methyl-L-cysteine")) {
            return "Methylation of C";
        } else if (pridePtmName.equalsIgnoreCase("alpha-amino dimethylated residue")) {
            return "Dimethylation of N-term";
        } else if (pridePtmName.equalsIgnoreCase("amidated residue")) {
            return "Amidation of the peptide C-term";
        } else if (pridePtmName.equalsIgnoreCase("deamidated L-asparagine")
                || pridePtmName.equalsIgnoreCase("deglycosylated asparagine")) {
            return "Deamidation of N";
        } else if (pridePtmName.equalsIgnoreCase("dihydroxylated residue - site W")) {
            return "Dioxidation of W";
        } else if (pridePtmName.equalsIgnoreCase("diiodinated residue")) {
            return "Diiodination of Y";
        } else if (pridePtmName.equalsIgnoreCase("hydroxylated lysine")
                || pridePtmName.equalsIgnoreCase("monohydroxylated lysine")) {
            return "Oxidation of K";
        } else if (pridePtmName.equalsIgnoreCase("iodoacetamide -site E")
                || pridePtmName.equalsIgnoreCase("iodoacetamide - site E")) {
            return "Carbamidomethylat\"iodoacetamide -site E\"ion of E";
        } else if (pridePtmName.equalsIgnoreCase("iodoacetamide N6-derivatized lysine")) {
            return "Carbamidomethylation of K";
        } else if (pridePtmName.equalsIgnoreCase("monomethylated L-aspartic acid")) {
            return "Methylation of D";
        } else if (pridePtmName.equalsIgnoreCase("thioacylation of primary amines - site N-term")) {
            return "Thioacyl of peptide N-term";
        } else if (pridePtmName.equalsIgnoreCase("ubiquitination signature dipeptidyl lysine")) {
            return "Ubiquitination of K";
        } else if (pridePtmName.equalsIgnoreCase("Label:13C(6)15N(2)")) {
            return "Lysine 13C(6) 15N(2)";
        } else {
            return null;
        }
    }

    

    /**
     * Sets the default modifications.
     */
    private void setDefaultModifications() {

        // Acetylation of K
        AtomChain atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        AtomChain atomChainRemoved = new AtomChain();
        AminoAcidPattern aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        String modificationName = "Acetylation of K";
        Modification modification = new Modification(ModificationType.modaa, modificationName, "ace", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1", "Acetyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00723", "N-acetylated L-lysine", null));
        modification.addReporterIon(ReporterIon.ACE_K_126);
        modification.addReporterIon(ReporterIon.ACE_K_143);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Acetylation of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Acetylation of peptide N-term"; // note: if name changed also change in TandemProcessBuilder
        modification = new Modification(ModificationType.modn_peptide, modificationName, "ace", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1", "Acetyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01458", "alpha-amino acetylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Acetylation of protein N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Acetylation of protein N-term"; // note: if name changed also change in TandemProcessBuilder of SearchGUI and PsmImporter of PeptideShaker
        modification = new Modification(ModificationType.modn_protein, modificationName, "ace", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01458", "alpha-amino acetylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carbamidomethylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Carbamidomethylation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "cmm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01060", "S-carboxamidomethyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carbamidomethylation of E
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("E");
        modificationName = "Carbamidomethylation of E";
        modification = new Modification(ModificationType.modaa, modificationName, "cmm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01216", "iodoacetamide derivatized glutamic acid", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carbamidomethylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Carbamidomethylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "cmm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:4", "Carbamidomethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01212", "iodoacetamide N6-derivatized lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Oxidation of M
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("M");
        modificationName = "Oxidation of M";
        modification = new Modification(ModificationType.modaa, modificationName, "ox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:35", "Oxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00719", "L-methionine sulfoxide", null)); // @TODO: could also map to MOD:00425?
        modification.addNeutralLoss(NeutralLoss.CH4OS);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Oxidation of P
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("P");
        modificationName = "Oxidation of P";
        modification = new Modification(ModificationType.modaa, modificationName, "ox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:35", "Oxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00039", "4-hydroxy-L-proline", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Oxidation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Oxidation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "ox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:35", "Oxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01047", "monohydroxylated lysine", null)); // @TODO: maps to parent term "monohydroxylated lysine"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Oxidation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Oxidation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "ox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:35", "Oxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00210", "L-cysteine sulfenic acid", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dioxydation of M
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("M");
        modificationName = "Dioxidation of M";
        modification = new Modification(ModificationType.modaa, modificationName, "diox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:425", "Dioxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00428", "dihydroxylated residue", null)); // @TODO: maps to parent term "dihydroxylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dioxydation of W
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("W");
        modificationName = "Dioxidation of W";
        modification = new Modification(ModificationType.modaa, modificationName, "diox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:425", "Dioxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00428", "dihydroxylated residue", null)); // @TODO: maps to parent term "dihydroxylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trioxidation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Trioxidation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "triox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:345", "Trioxidation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00460", "L-cysteic acid (L-cysteine sulfonic acid)", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Phosphorylation of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0));
        atomChainAdded.append(new AtomImpl(Atom.P, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Phosphorylation of S";
        modification = new Modification(ModificationType.modaa, modificationName, "p", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:21", "Phospho", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00046", "O-phospho-L-serine", null));
        modification.addNeutralLoss(NeutralLoss.H3PO4);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Phosphorylation of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0));
        atomChainAdded.append(new AtomImpl(Atom.P, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Phosphorylation of T";
        modification = new Modification(ModificationType.modaa, modificationName, "p", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:21", "Phospho", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00047", "O-phospho-L-threonine", null));
        modification.addNeutralLoss(NeutralLoss.H3PO4);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Phosphorylation of Y
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0));
        atomChainAdded.append(new AtomImpl(Atom.P, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Y");
        modificationName = "Phosphorylation of Y";
        modification = new Modification(ModificationType.modaa, modificationName, "p", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:21", "Phospho", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00048", "O4'-phospho-L-tyrosine", null));
        modification.addNeutralLoss(NeutralLoss.HPO3);
        modification.addReporterIon(ReporterIon.PHOSPHO_Y);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Arg6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Arginine 13C(6)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01331", "6x(13)C labeled L-arginine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Arg10
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 4);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 4);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Arginine 13C(6) 15N(4)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:267", "Label:13C(6)15N(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00587", "6x(13)C,4x(15)N labeled L-arginine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Lys4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 4);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Lysine 2H(4)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:481", "Label:2H(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00942", "(4,4,5,5-(2)H4)-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Lys6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Lysine 13C(6)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:188", "Label:13C(6)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01334", "6x(13)C labeled L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Lys8
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Lysine 13C(6) 15N(2)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:259", "Label:13C(6)15N(2)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00582", "6x(13)C,2x(15)N labeled L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Pro5
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 5);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 5);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("P");
        modificationName = "Proline 13C(5)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:772", "Label:13C(5)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01297", "5x(13)C labeled L-proline", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // 4-Hydroxyloproline
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 5);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 9);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("P");
        modificationName = "4-Hydroxyproline";
        modification = new Modification(ModificationType.modaa, modificationName, "hydroxy", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Leu7
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("L");
        modificationName = "Leucine 13C(6) 15N(1)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:695", "Label:13C(6)15N(1)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01285", "6x(13)C,1x(15)N labeled L-leucine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Ile7
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 6);
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("I");
        modificationName = "Isoleucine 13C(6) 15N(1)";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:695", "Label:13C(6)15N(1)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01286", "6x(13)C,1x(15)N labeled L-isoleucine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Label of K 2H4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 4);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Label of K 2H(4)";
        modification = new Modification(ModificationType.modaa, modificationName, "2H(4)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:481", "Label:2H(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00942", "(4,4,5,5-(2)H4)-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of K 2H4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Dimethylation of K 2H(4)";
        modification = new Modification(ModificationType.modaa, modificationName, "dimeth4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:199 ", "Dimethyl:2H(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01254", "4x(2)H labeled dimethylated L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of K 2H6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Dimethylation of K 2H(6)";
        modification = new Modification(ModificationType.modaa, modificationName, "dimeth6", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1291", "Dimethyl:2H(6)", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of K 2H(6) 13C(2)
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 6);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Dimethylation of K 2H(6) 13C(2)";
        modification = new Modification(ModificationType.modaa, modificationName, "dimeth8", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:330", " Dimethyl:2H(6)13C(2)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of peptide N-term 2H4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Dimethylation of peptide N-term 2H(4)";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "dimeth4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:199 ", "Dimethyl:2H(4)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of peptide N-term 2H6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 6);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Dimethylation of peptide N-term 2H(6)";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "dimeth6", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD: 1291", "Dimethyl:2H(6)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of peptide N-term 2H(6) 13C(2)
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 6);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Dimethylation of peptide N-term 2H(6) 13C(2)";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "dimeth8", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:330", " Dimethyl:2H(6)13C(2)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // 18O(2) of peptide C-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 2), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 2);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "18O(2) of peptide C-term";
        modification = new Modification(ModificationType.modc_peptide, modificationName, "18O", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD: 193", "Label:18O(2)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00546", "(18)O label at both C-terminal oxygens", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // 18O(1) of peptide C-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 2), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "18O(1) of peptide C-term";
        modification = new Modification(ModificationType.modc_peptide, modificationName, "18O", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:258", "Label:18O(1)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00581", "(18)O monosubstituted residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICAT-0
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 17);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "ICAT-O";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:105", "ICAT-C", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00480", "Applied Biosystems cleavable ICAT(TM) light", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICAT-9
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 9);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 17);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "ICAT-9";
        modification = new Modification(ModificationType.modaa, modificationName, "*", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:106", "ICAT-C:13C(9)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00481", "Applied Biosystems cleavable ICAT(TM) heavy", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL0 of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "ICPL0 of K";
        modification = new Modification(ModificationType.modaa, modificationName, "icpl0", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:365", "ICPL", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01230", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, light form - site K", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL0 of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "ICPL0 of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "icpl0", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:365", "ICPL", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL4 of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "ICPL4 of K";
        modification = new Modification(ModificationType.modaa, modificationName, "icpl4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:687", "ICPL:2H(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01359", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, medium form - site K", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL4 of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "ICPL4 of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "icpl4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:687", "ICPL:2H(4)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01358", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, medium form - site N-term", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL6 of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "ICPL6 of K";
        modification = new Modification(ModificationType.modaa, modificationName, "icpl6", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:364", "ICPL:13C(6)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL6 of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "ICPL6 of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "icpl6", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:364", "ICPL:13C(6)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL10 of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "ICPL10 of K";
        modification = new Modification(ModificationType.modaa, modificationName, "icpl10", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:866", "ICPL:13C(6)2H(4)", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01287", "Bruker Daltonics SERVA-ICPL(TM) quantification chemistry, heavy form - site K", null)); // @TODO: the mass in Unimod and PSI-MOD is not the same!
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // ICPL10 of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "ICPL10 of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "icpl10", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:866", "ICPL:13C(6)2H(4)", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of K light
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "mTRAQ of K light";
        modification = new Modification(ModificationType.modaa, modificationName, "mTRAQ0", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD: 888", "mTRAQ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of peptide N-term light
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "mTRAQ of peptide N-term light";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "mTRAQ0", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:888", "mTRAQ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of K 13C3 15N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "mTRAQ of K 13C(3) 15N";
        modification = new Modification(ModificationType.modaa, modificationName, "mTRAQ4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:889", "mTRAQ:13C(3)15N(1) ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of peptide N-term 13C3 15N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "mTRAQ of peptide N-term 13C(3) 15N";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "mTRAQ4", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:889", "mTRAQ:13C(3)15N(1) ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...

        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of K 13C6 15N2
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "mTRAQ of 13C(6) 15N(2)";
        modification = new Modification(ModificationType.modaa, modificationName, "mTRAQ8", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1302", "mTRAQ:13C(6)15N(2) ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // mTRAQ of peptide N-term 13C3 15N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "mTRAQ of peptide N-term 13C(6) 15N(2)";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "mTRAQ8", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1302", "mTRAQ:13C(6)15N(2) ", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01863", "mTRAQ reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "mTRAQ reporter+balance reagent acylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 4-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "iTRAQ 4-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:214", "iTRAQ4plex", null)); // @TODO: check cv term and mass!!!
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01518", "iTRAQ4plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ4plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_117);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 4-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "iTRAQ 4-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:214", "iTRAQ4plex", null)); // @TODO: check cv term and mass!!!
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01518", "iTRAQ4plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ4plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_117);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 4-plex of Y
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Y");
        modificationName = "iTRAQ 4-plex of Y";
        modification = new Modification(ModificationType.modaa, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:214", "iTRAQ4plex", null)); // @TODO: check cv term and mass!!!
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01518", "iTRAQ4plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ4plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ4Plex_117);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 8-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 24);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "iTRAQ 8-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:730", "iTRAQ8plex", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01526", "iTRAQ8plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ8plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_113);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_117);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_118);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_119);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_121);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 8-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 24);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "iTRAQ 8-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:730", "iTRAQ8plex", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01526", "iTRAQ8plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ8plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_113);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_117);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_118);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_119);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_121);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iTRAQ 8-plex of Y
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 24);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Y");
        modificationName = "iTRAQ 8-plex of Y";
        modification = new Modification(ModificationType.modaa, modificationName, "iTRAQ", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:730", "iTRAQ8plex", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01526", "iTRAQ8plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "iTRAQ8plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_113);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_114);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_115);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_116);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_117);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_118);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_119);
        modification.addReporterIon(ReporterIon.iTRAQ8Plex_121);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 2-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 11);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "TMT 2-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:738", "TMT2plex", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 2-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 11);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 2-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:738", "TMT2plex", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        /// TMT 6-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = null;
        modificationName = "TMT 6-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01715", "TMT6plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "TMT6plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 6-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 6-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: does not have a PSI name, using interim name
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01715", "TMT6plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "TMT6plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 6-plex + K+4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 6-plex of K+4";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 6-plex of K+6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 6-plex of K+6";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 6-plex of K+8
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 6-plex of K+8";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 10-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = null;
        modificationName = "TMT 10-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: "PSI: Alt. Description: Also applies to TMT10plex", i.e., no term for 10-plex
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01715", "TMT6plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "TMT6plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 10-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 10-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: "PSI: Alt. Description: Also applies to TMT10plex", i.e., no term for 10-plex
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 10-plex + K+4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 10-plex of K+4";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        /// @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 10-plex of K+6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 10-plex of K+6";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 10-plex of K+8
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 10-plex of K+8";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        //// @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 11-plex of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = null;
        modificationName = "TMT 11-plex of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: "PSI: Alt. Description: Also applies to TMT10plex", i.e., no term for 10-plex (no mention of 11-plex though...)
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01715", "TMT6plex reporter+balance reagent acylated residue", null)); // @TODO: maps to parent term "TMT6plex reporter+balance reagent acylated residue"...
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_131C);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        modification.addReporterIon(ReporterIon.TMT_131C_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 11-plex of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 11-plex of K";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:737", "TMT6plex", null)); // note: "PSI: Alt. Description: Also applies to TMT10plex", i.e., no term for 10-plex (no mention of 11-plex though...)
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_131C);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        modification.addReporterIon(ReporterIon.TMT_131C_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 11-plex + K+4
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 11-plex of K+4";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        /// @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        modification.addReporterIon(ReporterIon.TMT_131C_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 11-plex of K+6
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 11-plex of K+6";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        modification.addReporterIon(ReporterIon.TMT_131C_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // TMT 11-plex of K+8
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 10);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 20);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "TMT 11-plex of K+8";
        modification = new Modification(ModificationType.modaa, modificationName, "TMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        modification.addReporterIon(ReporterIon.TMT_126);
        modification.addReporterIon(ReporterIon.TMT_127C);
        modification.addReporterIon(ReporterIon.TMT_127N);
        modification.addReporterIon(ReporterIon.TMT_128C);
        modification.addReporterIon(ReporterIon.TMT_128N);
        modification.addReporterIon(ReporterIon.TMT_129C);
        modification.addReporterIon(ReporterIon.TMT_129N);
        modification.addReporterIon(ReporterIon.TMT_130C);
        modification.addReporterIon(ReporterIon.TMT_130N);
        modification.addReporterIon(ReporterIon.TMT_131);
        modification.addReporterIon(ReporterIon.TMT_126_ETD);
        modification.addReporterIon(ReporterIon.TMT_127C_ETD);
        modification.addReporterIon(ReporterIon.TMT_127N_ETD);
        modification.addReporterIon(ReporterIon.TMT_128C_ETD);
        modification.addReporterIon(ReporterIon.TMT_128N_ETD);
        modification.addReporterIon(ReporterIon.TMT_129C_ETD);
        modification.addReporterIon(ReporterIon.TMT_129N_ETD);
        modification.addReporterIon(ReporterIon.TMT_130C_ETD);
        modification.addReporterIon(ReporterIon.TMT_130N_ETD);
        modification.addReporterIon(ReporterIon.TMT_131_ETD);
        modification.addReporterIon(ReporterIon.TMT_131C_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iodoTMT zero of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 28);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "iodoTMT zero of C";
        modification = new Modification(ModificationType.modaa, modificationName, "iodoTMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1341", "iodoTMT", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI_MOD cv term?
        modification.addReporterIon(ReporterIon.iodoTMT_zero);
        modification.addReporterIon(ReporterIon.iodoTMT_zero_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // iodoTMT 6-plex of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 28);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 4);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 1), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "iodoTMT 6-plex of C";
        modification = new Modification(ModificationType.modaa, modificationName, "iodoTMT", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1342", "iodoTMT", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI_MOD cv term?
        modification.addReporterIon(ReporterIon.iodoTMT_126);
        modification.addReporterIon(ReporterIon.iodoTMT_127);
        modification.addReporterIon(ReporterIon.iodoTMT_128);
        modification.addReporterIon(ReporterIon.iodoTMT_129);
        modification.addReporterIon(ReporterIon.iodoTMT_130);
        modification.addReporterIon(ReporterIon.iodoTMT_131);
        modification.addReporterIon(ReporterIon.iodoTMT_126_ETD);
        modification.addReporterIon(ReporterIon.iodoTMT_127_ETD);
        modification.addReporterIon(ReporterIon.iodoTMT_128_ETD);
        modification.addReporterIon(ReporterIon.iodoTMT_129_ETD);
        modification.addReporterIon(ReporterIon.iodoTMT_130_ETD);
        modification.addReporterIon(ReporterIon.iodoTMT_131_ETD);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Ubiquitination of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Ubiquitination of K";
        modification = new Modification(ModificationType.modaa, modificationName, "ub", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:121", "GG", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00492", "ubiquitination signature dipeptidyl lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0));
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Methylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00085", "N6-methyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0));
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Methylation of R";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00414", "monomethylated L-arginine", null)); // @TODO: maps to parent term "monomethylated L-arginine"...
        modification.addReporterIon(ReporterIon.METHYL_R_70);
        modification.addReporterIon(ReporterIon.METHYL_R_87);
        modification.addReporterIon(ReporterIon.METHYL_R_112);
        modification.addReporterIon(ReporterIon.METHYL_R_115);
        modification.addReporterIon(ReporterIon.METHYL_R_143);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of E
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("E");
        modificationName = "Methylation of E";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00081", "L-glutamic acid 5-methyl ester (Glu)", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Methylation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00660", "methylated cysteine", null)); // @TODO: maps to parent term "methylated cysteine"
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of D
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("D");
        modificationName = "Methylation of D";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00079", "N4-methyl-L-asparagine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylation of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Methylation of S";
        modification = new Modification(ModificationType.modaa, modificationName, "meth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:34", "Methyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01782", "N-methyl-L-serine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Dimethylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "dimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:36", "Dimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00084", "N6,N6-dimethyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Dimethylation of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "dimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:36", "Dimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01686", "alpha-amino dimethylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dimethylation of R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Dimethylation of R";
        modification = new Modification(ModificationType.modaa, modificationName, "dimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:36", "Dimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00783", "dimethylated L-arginine", null));
        modification.addReporterIon(ReporterIon.DI_METHYL_R_112);
        modification.addReporterIon(ReporterIon.DI_METHYL_R_115);
        modification.addReporterIon(ReporterIon.DI_METHYL_R_157);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trimethylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 6);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Trimethylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "trimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:37", "Trimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00855", "N6,N6,N6-trimethyl-L-lysine (from L-lysinium residue)", null));
        modification.addNeutralLoss(NeutralLoss.C3H9N);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trimethylation of R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 6);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Trimethylation of R";
        modification = new Modification(ModificationType.modaa, modificationName, "trimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:37", "Trimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01669", "trimethyl-L-arginine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trimethylation of protein N-term A
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 6);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("A");
        modificationName = "Trimethylation of protein N-term A";
        modification = new Modification(ModificationType.modn_protein, modificationName, "trimeth", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:37", "Trimethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01687", "alpha-amino trimethylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Pyrolidone from E
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("E");
        modificationName = "Pyrolidone from E"; // note: if name changed also change in TandemProcessBuilder of SearchGUI and PsmImporter of PeptideShaker
        modification = new Modification(ModificationType.modnaa_peptide, modificationName, "pyro", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:27", "Glu->pyro-Glu", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00420", "2-pyrrolidone-5-carboxylic acid (Glu)", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Pyrolidone from Q
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 3);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Q");
        modificationName = "Pyrolidone from Q"; // note: if name changed also change in TandemProcessBuilder of SearchGUI and PsmImporter of PeptideShaker
        modification = new Modification(ModificationType.modnaa_peptide, modificationName, "pyro", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:28", "Gln->pyro-Glu", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00040", "2-pyrrolidone-5-carboxylic acid (Gln)", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Pyrolidone from carbamidomethylated C
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 3);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Pyrolidone from carbamidomethylated C"; // note: if name changed also change in TandemProcessBuilder of SearchGUI and PsmImporter of PeptideShaker
        modification = new Modification(ModificationType.modnaa_peptide, modificationName, "pyro", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:385", "Ammonia-loss", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01871", "cyclized N-terminal S-carboxamidomethyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // HexNAc of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 13);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 5);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "HexNAc of S";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:43", "HexNAc", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // HexNAc of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 13);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 5);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "HexNAc of T";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:43", "HexNAc", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Hex(1)NAc(1) of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 14);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 23);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 10);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Hex(1)NAc(1) of S";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:793", "Hex(1)HexNAc(1) ", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Hex(1)NAc(1) of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 14);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 23);
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 10);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Hex(1)NAc(1) of T";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:793", "Hex(1)HexNAc(1) ", null)); // note: does not have a PSI name, using interim name
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Hexose of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 10);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 5);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Hexose of K";
        modification = new Modification(ModificationType.modaa, modificationName, "hex", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:41", "Hex", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01347", "hexose glycated L-lysine", null));

        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Hex(5) HexNAc(4) NeuAc(2) of N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 84);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 136);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 61);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 6);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("N");
        modificationName = "Hex(5) HexNAc(4) NeuAc(2) of N";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Hex(5) HexNAc(4) NeuAc(2) Na of N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 84);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 135);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 61);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.Na, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("N");
        modificationName = "Hex(5) HexNAc(4) NeuAc(2) Na of N";
        modification = new Modification(ModificationType.modaa, modificationName, "glyco", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:1408", "Hex(5)HexNAc(4)NeuAc(2) ", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // SUMO-2/3 Q87R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 18);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 29);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 8);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "SUMO-2/3 Q87R";
        modification = new Modification(ModificationType.modaa, modificationName, "sumo", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        // @TODO: add Unimod mapping?
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01149", "sumoylated lysine", null)); // @TODO: maps to parent term "sumoylated lysine"...
        modification.addReporterIon(ReporterIon.QQ);
        modification.addReporterIon(ReporterIon.QQ_H2O);
        modification.addReporterIon(ReporterIon.QQT);
        modification.addReporterIon(ReporterIon.QQT_H2O);
        modification.addReporterIon(ReporterIon.QQTG);
        modification.addReporterIon(ReporterIon.QQTG_H2O);
        modification.addReporterIon(ReporterIon.QQTGG);
        modification.addReporterIon(ReporterIon.QQTGG_H2O);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Deamidation of N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("N");
        modificationName = "Deamidation of N";
        modification = new Modification(ModificationType.modaa, modificationName, "deam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:7", "Deamidated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00684", "deamidated L-asparagine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Deamidation of Q
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Q");
        modificationName = "Deamidation of Q";
        modification = new Modification(ModificationType.modaa, modificationName, "deam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:7", "Deamidated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00685", "deamidated L-glutamine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Deamidation of N 18O
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 2), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("N");
        modificationName = "Deamidation of N 18O";
        modification = new Modification(ModificationType.modaa, modificationName, "deam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD: 366", "Deamidation in presence of O18", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00791", "1x(18)O labeled deamidated L-glutamine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carbamylation of protein N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0));
        atomChainAdded.append(new AtomImpl(Atom.H, 0));
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Carbamilation of protein N-term";
        modification = new Modification(ModificationType.modn_protein, modificationName, "cm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:5", "Carbamyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01679", "alpha-aminocarbamoylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carbamylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0));
        atomChainAdded.append(new AtomImpl(Atom.H, 0));
        atomChainAdded.append(new AtomImpl(Atom.N, 0));
        atomChainAdded.append(new AtomImpl(Atom.O, 0));
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Carbamilation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "cm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:5", "Carbamyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01678", "N6-carbamoyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Acetaldehyde +26
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Acetaldehyde +26";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "ace", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:254", "Delta:H(2)C(2)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00577", "acetaldehyde +26", null)); // @TODO: this PSI-MOD not peptide n-term specific
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Sodium adduct to D
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.Na, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("D");
        modificationName = "Sodium adduct to D";
        modification = new Modification(ModificationType.modaa, modificationName, "Na", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:30", "Cation:Na", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01295", "monosodium L-aspartate", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Sodium adduct to E
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.Na, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("E");
        modificationName = "Sodium adduct to E";
        modification = new Modification(ModificationType.modaa, modificationName, "Na", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:30", "Cation:Na", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01296", "monosodium L-glutamate", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Amidation of the peptide C-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Amidation of the peptide C-term";
        modification = new Modification(ModificationType.modc_peptide, modificationName, "am", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:2", "Amidated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00883", "C1-amidated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Amidation of the protein C-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Amidation of the protein C-term";
        modification = new Modification(ModificationType.modc_protein, modificationName, "am", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:2", "Amidated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00883", "C1-amidated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Sulfation of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Sulfation of S";
        modification = new Modification(ModificationType.modaa, modificationName, "s", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:40", "Sulfo", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00366", "O-sulfo-L-serine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Sulfation of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Sulfation of T";
        modification = new Modification(ModificationType.modaa, modificationName, "s", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:40", "Sulfo", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00367", "O-sulfo-L-threonine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Sulfation of Y
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Y");
        modificationName = "Sulfation of Y";
        modification = new Modification(ModificationType.modaa, modificationName, "s", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:40", "Sulfo", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00181", "O4'-sulfo-L-tyrosine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Palmitoylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 30);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Palmitoylation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "palm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:47", "Palmitoylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00440", "palmitoylated residue", null)); // @TODO: maps to parent term "palmitoylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Palmitoylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 30);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Palmitoylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "palm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:47", "Palmitoylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00086", "N6-palmitoyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Palmitoylation of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 30);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Palmitoylation of S";
        modification = new Modification(ModificationType.modaa, modificationName, "palm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:47", "Palmitoylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00089", "O-palmitoyl-L-serine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Palmitoylation of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 30);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Palmitoylation of T";
        modification = new Modification(ModificationType.modaa, modificationName, "palm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:47", "Palmitoylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00088", "O-palmitoyl-L-threonine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Palmitoylation of protein N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 30);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 16);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Palmitoylation of protein N-term";
        modification = new Modification(ModificationType.modn_protein, modificationName, "palm", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:47", "Palmitoylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01685", "alpha-amino palmitoylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Formylation of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Formylation of K";
        modification = new Modification(ModificationType.modaa, modificationName, "form", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:122", "Formylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00216", "N6-formyl-L-lysine", null));
        modification.addReporterIon(ReporterIon.FORMYL_K);
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Formylation of S
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Formylation of S";
        modification = new Modification(ModificationType.modaa, modificationName, "form", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:122", "Formylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01222", "O-formyl-L-serine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Formylation of T
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Formylation of T";
        modification = new Modification(ModificationType.modaa, modificationName, "form", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:122", "Formylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01221", "O-formyl-L-threonine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Formylation of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Formylation of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "form", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:122", "Formylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00409", "N-formylated residue", null)); // @TODO: maps to parent term "N-formylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Formylation of protein N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Formylation of protein N-term";
        modification = new Modification(ModificationType.modn_protein, modificationName, "form", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:122", "Formylation", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00409", "N-formylated residue", null)); // @TODO: maps to parent term "N-formylated residue"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionyl of K light
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Propionyl of K light";
        modification = new Modification(ModificationType.modaa, modificationName, "prop", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:58", "Propionyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01232", "3x(12)C labeled N6-propanoyl-L-lysine", null));

        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionyl of peptide N-term light
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Propionyl of peptide N-term light";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "prop", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:58", "Propionyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01895", "alpha-amino 3x(12)C-labeled propanoylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionyl of K heavy
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Propionyl of K heavy";
        modification = new Modification(ModificationType.modaa, modificationName, "prop", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:59", "Propionyl:13C(3)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01231", "3x(13)C labeled N6-propanoyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionyl of peptide N-term heavy
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Propionyl of peptide N-term heavy";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "prop", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:59", "Propionyl:13C(3)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00452", "alpha-amino 3x(13)C-labeled propanoylated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trideuterated Methyl Ester of D
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("D");
        modificationName = "Trideuterated Methyl Ester of D";
        modification = new Modification(ModificationType.modaa, modificationName, "methyl(d3)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:298", "Methyl:2H(3)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01241", "3x(2)H labeled L-aspartic acid 4-methyl ester", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trideuterated Methyl Ester of E
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("E");
        modificationName = "Trideuterated Methyl Ester of E";
        modification = new Modification(ModificationType.modaa, modificationName, "methyl(d3)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:298", "Methyl:2H(3)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01242", "3x(2)H labeled L-glutamic acid 5-methyl ester", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trideuterated Methyl Ester of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Trideuterated Methyl Ester of K";
        modification = new Modification(ModificationType.modaa, modificationName, "methyl(d3)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:298", "Methyl:2H(3)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trideuterated Methyl Ester of R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Trideuterated Methyl Ester of R";
        modification = new Modification(ModificationType.modaa, modificationName, "methyl(d3)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:298", "Methyl:2H(3)", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Trideuterated Methyl Ester of peptide C-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 1), 3);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Trideuterated Methyl Ester of peptide C-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "methyl(d3)", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:298", "Methyl:2H(3)", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00617", "3x(2)H residue methyl ester", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Carboxymethylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Carboxymethylation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "carbox", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:6", "Carboxymethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01061", "S-carboxymethyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Farnesylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 24);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 15);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Farnesylation of C";
        modification = new Modification(ModificationType.modaa, modificationName, "far", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:44", "Farnesyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00111", "S-farnesyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Geranyl-geranyl of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 32);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 20);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Geranyl-geranyl of C";
        modification = new Modification(ModificationType.modaa, modificationName, "geranyl", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:48", "GeranylGeranyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00113", "S-geranylgeranyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Guanidination of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Guanidination of K";
        modification = new Modification(ModificationType.modaa, modificationName, "guan", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:52", "Guanidinyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00445", "L-homoarginine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Guanidination of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 2);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Guanidination of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "guan", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:52", "Guanidinyl", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Homoserine of peptide C-term M
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.S, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("M");
        modificationName = "Homoserine of peptide C-term M";
        modification = new Modification(ModificationType.modcaa_peptide, modificationName, "hse", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:10", "Met->Hse", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00403", "homoserine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Homoserine lactone of peptide C-term M
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 4);
        atomChainRemoved.append(new AtomImpl(Atom.C, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.S, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("M");
        modificationName = "Homoserine lactone of peptide C-term M";
        modification = new Modification(ModificationType.modcaa_peptide, modificationName, "hsel", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:11", "Met->Hsl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00404", "homoserine lactone", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Lipoyl of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 12);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 8);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Lipoyl of K";
        modification = new Modification(ModificationType.modaa, modificationName, "lip", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:42", "Lipoyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00127", "N6-lipoyl-L-lysine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylthio of D
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("D");
        modificationName = "Methylthio of D";
        modification = new Modification(ModificationType.modaa, modificationName, "mmts", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:39", "Methylthio", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00237", "L-beta-methylthioaspartic acid", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylthio of N
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("N");
        modificationName = "Methylthio of N";
        modification = new Modification(ModificationType.modaa, modificationName, "mmts", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:39", "Methylthio", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00325", "L-beta-methylthioasparagine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Methylthio of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Methylthio of C";
        modification = new Modification(ModificationType.modaa, modificationName, "mmts", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:39", "Methylthio", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00110", "L-cysteine methyl disulfide", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // NIPCAM of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 9);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 5);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "NIPCAM of C";
        modification = new Modification(ModificationType.modaa, modificationName, "nipcam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:17", "NIPCAM", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00410", "S-(N-isopropylcarboxamidomethyl)-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionamide of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 5);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Propionamide of C";
        modification = new Modification(ModificationType.modaa, modificationName, "propam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:24", "Propionamide", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00417", "S-carboxamidoethyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionamide of K
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 5);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("K");
        modificationName = "Propionamide of K";
        modification = new Modification(ModificationType.modaa, modificationName, "propam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:24", "Propionamide", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Propionamide of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 5);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Propionamide of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "propam", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:24", "Propionamide", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Pyridylethyl of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Pyridylethyl of C";
        modification = new Modification(ModificationType.modaa, modificationName, "pyri", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:31", "Pyridylethyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00424", "S-pyridylethyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dehydration of S
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("S");
        modificationName = "Dehydration of S";
        modification = new Modification(ModificationType.modaa, modificationName, "dehyd", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:23", "Dehydrated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00189", "dehydroalanine (Ser)", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Dehydration of T
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        atomChainRemoved.append(new AtomImpl(Atom.O, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Dehydration of T";
        modification = new Modification(ModificationType.modaa, modificationName, "dehyd", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:23", "Dehydrated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00190", "dehydrobutyrine (Thr)", null)); // @TODO: maps to parent term "dehydrobutyrine (Thr)"...
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Nethylmaleimide of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 7);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Nethylmaleimide of C";
        modification = new Modification(ModificationType.modaa, modificationName, "nem", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:108", "Nethylmaleimide", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00483", "N-ethylmaleimide derivatized cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Glutathione of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 15);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 10);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Glutathione of C";
        modification = new Modification(ModificationType.modaa, modificationName, "glut", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:55", "Glutathione", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00234", "L-cysteine glutathione disulfide", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // FormylMet of protein N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 9);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 2);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 6);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "FormylMet of protein N-term";
        modification = new Modification(ModificationType.modn_protein, modificationName, "nmet", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:107", "FormylMet", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Didehydro of T
        atomChainAdded = new AtomChain();
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("T");
        modificationName = "Didehydro of T";
        modification = new Modification(ModificationType.modaa, modificationName, "didehyro", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:401", "Didehydro", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01345", "2-amino-3-oxobutanoic acid", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Thioacyl of peptide N-term
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 3);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.S, 0), 1);
        atomChainRemoved = new AtomChain();
        aminoAcidPattern = new AminoAcidPattern();
        modificationName = "Thioacyl of peptide N-term";
        modification = new Modification(ModificationType.modn_peptide, modificationName, "thioacyl", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:126", "Thioacyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01223", "thioacylation of primary amines - site N-term", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Diiodination of Y
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.I, 0), 2);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 2);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("Y");
        modificationName = "Diiodination of Y";
        modification = new Modification(ModificationType.modaa, modificationName, "diiodo", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:130", "Diiodo", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:01140", "diiodinated tyrosine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Citrullination of R
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.N, 0), 1);
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("R");
        modificationName = "Citrullination of R";
        modification = new Modification(ModificationType.modaa, modificationName, "cit", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:7", "Deamidated", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00400", "deamidated residue", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // S-nitrosylation of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 1);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 1);
        atomChainRemoved = new AtomChain();
        atomChainRemoved.append(new AtomImpl(Atom.H, 0), 1);
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "S-nitrosylation";
        modification = new Modification(ModificationType.modaa, modificationName, "nitrosyl", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:275", "Nitrosyl", null));
        modification.setPsiModCvTerm(new CvTerm("MOD", "MOD:00235", "S-nitrosyl-L-cysteine", null));
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Heme B of C
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 32);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 34);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.Fe, 0), 1);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("C");
        modificationName = "Heme B of C";
        modification = new Modification(ModificationType.modaa, modificationName, "heme", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:390", "Heme", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);

        // Heme B of H
        atomChainAdded = new AtomChain();
        atomChainAdded.append(new AtomImpl(Atom.H, 0), 32);
        atomChainAdded.append(new AtomImpl(Atom.C, 0), 34);
        atomChainAdded.append(new AtomImpl(Atom.N, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.O, 0), 4);
        atomChainAdded.append(new AtomImpl(Atom.Fe, 0), 1);
        atomChainRemoved = null;
        aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString("H");
        modificationName = "Heme B of H";
        modification = new Modification(ModificationType.modaa, modificationName, "heme", atomChainAdded, atomChainRemoved, aminoAcidPattern);
        modification.setUnimodCvTerm(new CvTerm("UNIMOD", "UNIMOD:390", "Heme", null));
        // @TODO: add PSI-MOD mapping?
        defaultMods.add(modificationName);
        modificationMap.put(modificationName, modification);
    }

    /**
     * Returns an array list of the utilities modification names mapping to the
     * given PSI-MOD accession number. The accession number should be provided
     * without the leading "MOD:" part.
     *
     * @param psiModAccession the PSI-MOD accession number to look up (provided
     * without the leading "MOD:" part)
     * @return an array list of the utilities modification names mapping to the
     * given PSI-MOD accession number
     */
    public ArrayList<String> getModificationsForPsiAccession(String psiModAccession) {

        if (psiModMap.isEmpty()) {
            createPsiModMap();
        }
        return psiModMap.get(psiModAccession);
    }

    /**
     * Create the PSI-MOD map.
     */
    private void createPsiModMap() {

        Iterator<String> modificationIterator = modificationMap.keySet().iterator();
        String modName;
        while (modificationIterator.hasNext()) {

            modName = modificationIterator.next();

            CvTerm psiCvTerm = modificationMap.get(modName).getPsiModCvTerm();

            if (psiCvTerm != null) {

                String psiModAccession = psiCvTerm.getAccession();
                psiModAccession = psiModAccession.substring(psiModAccession.indexOf(":") + 1); // remove the ontology name, i.e. "MOD:"

                ArrayList<String> utilitiesModifications = psiModMap.get(psiModAccession);

                if (utilitiesModifications == null) {
                    utilitiesModifications = new ArrayList<>();
                    psiModMap.put(psiModAccession, utilitiesModifications);
                }

                utilitiesModifications.add(modName);

            }
        }

    }
}
