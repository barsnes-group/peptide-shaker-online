package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents peptide object for Online PeptideShaker system the
 * class contains all the required information for visualising the peptide data
 *
 * @author Yehia Farag
 */
public class PeptideObject extends Peptide {

    /**
     * Set of main protein accessions for the peptide.
     */
    private final Set<String> proteinsSet;
    /**
     * Set of main protein group accessions for the peptide.
     */
    private final Set<String> proteinGroupsSet;
    /**
     * Instance of the PTM factory that is loading PTM from an XML file and
     * provide them on demand as standard class.
     */
    private final ModificationFactory ptmFactory = ModificationFactory.getInstance();
    /**
     * Peptide index from the exported PeptideShaker file.
     */
    private int index;
    /**
     * Standard peptide sequence.
     */
    private String sequence;
    /**
     * Peptide modified sequence.
     */
    private String modifiedSequence;
    /**
     * Peptide position.
     */
    private String postion;
    /**
     * Amino acid score before.
     */
    private String aasBefore;
    /**
     * Amino acid score after.
     */
    private String aasAfter;
    /**
     * Protein variable modifications included in the peptide sequence.
     */
    private ModificationMatch[] variableModifications;
    /**
     * Protein variable modifications included in the peptide sequence.
     */
    private ModificationMatch[] fixedModifications;
    /**
     * Protein fixed modifications included in the peptide sequence.
     */
    private String fixedModificationsAsString = "";
    /**
     * Localisation confidence.
     */
    private String localizationConfidence;
    /**
     * Number of valid protein groups that include the peptide.
     */
    private int validatedProteinGroupsNumber;
    /**
     * Number of unique databases.
     */
    private int uniqueDatabase;
    /**
     * Number of PSMs that include the peptide.
     */
    private int PSMsNumber;
    /**
     * Number of validated PSMs that include the peptide.
     */
    private int validatedPSMsNumber;
    /**
     * Confidence value.
     */
    private double confidence;
    /**
     * Validation value.
     */
    private String validation;
    /**
     * Peptide tool-tip text value.
     */
    private String tooltip;
    /**
     * UniProt protein group key.
     */
    private String proteinGroupKey;
    /**
     * Number of validated unique peptides to protein group.
     */
    private int validatedUniqueToGroupNumber;
    /**
     * The modifications carried by the peptide.
     */
//    private ArrayList<ModificationMatch> modificationMatches = null;
    /**
     * Intensity value of the quantification.
     */
    private double intensity = 0;
    /**
     * Intensity hash-code colour of the quantification.
     */
    private String intensityColor = "rgb(255,255,255)";
    /**
     * Intensity hash-code colour of the psmNumber.
     */
    private String psmColor = "rgb(120,120,120)";
    /**
     * Peptide not mapped to 3dView.
     */
    private boolean invisibleOn3d;
    private String variableModificationsAsString = "";

    /**
     * Constructor to initialise the main data structure.
     */
    public PeptideObject() {
        this.proteinsSet = new LinkedHashSet<>();
        this.proteinGroupsSet = new LinkedHashSet<>();

    }

    /**
     * Check if peptide visible on 3d structure
     *
     * @return peptide is not available
     */
    public boolean isInvisibleOn3d() {
        return invisibleOn3d;
    }

    /**
     * Set the peptide is not visible on 3d structure
     *
     * @param invisibleOn3d peptide not visible
     */
    public void setInvisibleOn3d(boolean invisibleOn3d) {
        this.invisibleOn3d = invisibleOn3d;
    }

    /**
     * Get PSM hashed colour
     *
     * @return Hashed colour
     */
    public String getPsmColor() {
        return psmColor;
    }

    /**
     * Set PSM hashed colour
     *
     * @param psmColor Hashed colour
     */
    public void setPsmColor(String psmColor) {
        this.psmColor = psmColor;
    }

    /**
     * Get PSM intensity value
     *
     * @return intensity value
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * Set PSM intensity value
     *
     * @param intensity intensity value
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * Get peptide tool-tip text value.
     *
     * @return tool tip text
     */
    public String getTooltip() {
        return this.tooltip;
    }

    /**
     * Set peptide tool-tip text value.
     *
     * @param tooltip tool tip text
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Set main protein accessions for the peptide.
     *
     * @param proteins ';'separated strings of accessions
     */
    public void setProteins(String proteins) {
        for (String acc : proteins.split(",")) {
            proteinsSet.add(acc.replace(" ", ""));
        }
    }

    /**
     * Get Standard peptide sequence.
     *
     * @return Standard peptide sequence.
     */
    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     * Set standard peptide sequence.
     *
     * @param sequence Standard peptide sequence.
     */
    @Override
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get peptide modified sequence
     *
     * @return peptide modified sequence
     */
    public String getModifiedSequence() {
        return modifiedSequence;
    }

    /**
     * Set peptide modified sequence
     *
     * @param modifiedSequence peptide modified sequence
     */
    public void setModifiedSequence(String modifiedSequence) {
        this.modifiedSequence = modifiedSequence;
    }

    /**
     * Get peptide positions.
     *
     * @return peptide position.
     */
    public String getPostion() {
        return postion;
    }

    /**
     * Set peptide position.
     *
     * @param postion peptide position.
     */
    public void setPostion(String postion) {
        this.postion = postion;
    }

    /**
     * Get Amino acid score before
     *
     * @return Amino acid score before
     */
    public String getAasBefore() {
        return aasBefore;
    }

    /**
     * Set Amino acid score before
     *
     * @param aasBefore Amino acid score before
     */
    public void setAasBefore(String aasBefore) {
        this.aasBefore = aasBefore;
    }

    /**
     * Get Amino acid score after
     *
     * @return Amino acid score before
     */
    public String getAasAfter() {
        return aasAfter;
    }

    /**
     * Set Amino acid score after
     *
     * @param aasAfter Amino acid score after
     */
    public void setAasAfter(String aasAfter) {
        this.aasAfter = aasAfter;
    }

    /**
     * Get protein variable modifications list included in the peptide sequence.
     *
     * @return modifications list.
     */
    @Override
    public ModificationMatch[] getVariableModifications() {
        if (variableModifications == null) {
            variableModifications = new ModificationMatch[]{};
        }
        return variableModifications;
    }

    /**
     * Set protein variable modifications list included in the peptide sequence.
     *
     * @param variableModifications protein variable modifications list(as
     *                              string) included in the peptide sequence.
     */
    @Override
    public void setVariableModifications(ModificationMatch[] variableModifications) {
        this.variableModifications = variableModifications;
    }

    /**
     * Set protein variable modifications list included in the peptide sequence.
     *
     * @param variableModifications protein variable modifications list(as
     *                              string) included in the peptide sequence.
     */
    public void setVariableModifications(String variableModifications) {

        if (variableModifications == null || variableModifications.trim().equalsIgnoreCase("")) {
            this.variableModifications = new ModificationMatch[]{};
            return;
        }
        variableModifications = variableModifications.replaceAll("\"", "");
        ArrayList<ModificationMatch> modificationMatchesList = new ArrayList<>();
        for (String modStr : variableModifications.replace("),", "__").split("__")) {
            String[] modStrArr = modStr.trim().replace("(", "__").split("__");
            Modification ptm = ptmFactory.getModification(modStrArr[0].trim());
            String[] indexArr = modStrArr[1].trim().replace(")", "").trim().split(",");
            for (String modIndex : indexArr) {
                ModificationMatch mod = new ModificationMatch(ptm.getName(), Integer.parseInt(modIndex.trim().split(";")[0]));
                modificationMatchesList.add(mod);
            }

        }
        this.variableModifications = new ModificationMatch[modificationMatchesList.size()];
        for (int i = 0; i < this.variableModifications.length; i++) {
            this.variableModifications[i] = modificationMatchesList.get(i);
        }
        this.variableModificationsAsString = variableModifications;
    }

    /**
     * Get peptide variable modification as one string
     *
     * @return variable modifications
     */
    public String getVariableModificationsAsString() {
        return variableModificationsAsString;
    }

    /**
     * Get peptide fixed modifications list included in the peptide sequence.
     *
     * @return set of modifications name.
     */
    public String getFixedModificationsAsString() {
        return fixedModificationsAsString;
    }

    /**
     * Set protein fixed modifications list included in the peptide sequence.
     *
     * @param fixedModificationsAsString protein fixed modifications list(as
     *                                   string) included in the peptide sequence.
     */
    public void setFixedModificationsAsString(String fixedModificationsAsString) {
        this.fixedModificationsAsString = fixedModificationsAsString;
        if (fixedModificationsAsString == null || fixedModificationsAsString.trim().equalsIgnoreCase("")) {
            this.fixedModifications = new ModificationMatch[]{};
            return;
        }
        fixedModificationsAsString = fixedModificationsAsString.replaceAll("\"", "");
        ArrayList<ModificationMatch> modificationMatchesList = new ArrayList<>();
        for (String modStr : fixedModificationsAsString.replace("),", "__").split("__")) {
            String[] modStrArr = modStr.trim().replace("(", "__").split("__");
            Modification ptm = ptmFactory.getModification(modStrArr[0].trim());
            String[] indexArr = modStrArr[1].trim().replace(")", "").trim().split(",");
            for (String modIndex : indexArr) {
                ModificationMatch mod = new ModificationMatch(ptm.getName(), Integer.parseInt(modIndex.trim().split(";")[0]));
                modificationMatchesList.add(mod);
            }

        }
        this.fixedModifications = new ModificationMatch[modificationMatchesList.size()];
        for (int i = 0; i < this.fixedModifications.length; i++) {
            this.fixedModifications[i] = modificationMatchesList.get(i);
        }
    }

    /**
     * Get peptide fixed modification as array
     *
     * @return array of fixed modifications
     */
    public ModificationMatch[] getFixedModifications() {
        return fixedModifications;
    }

    /**
     * Get localisation confidence
     *
     * @return localisation confidence
     */
    public String getLocalizationConfidence() {
        return localizationConfidence;
    }

    /**
     * Set localisation confidence
     *
     * @param localizationConfidence localisation confidence
     */
    public void setLocalizationConfidence(String localizationConfidence) {
        this.localizationConfidence = localizationConfidence;
    }

    /**
     * Set main protein groups accessions for the peptide.
     *
     * @param proteinGroups ';'separated strings of accessions
     */
    public void setProteinGroups(String proteinGroups) {
        proteinGroupKey = proteinGroups.replace("(Confident)", "").replace("(Doubtful)", "");
//        proteinGroupKey = proteinGroupKey.replace("Not Validated", "").replace("(","").replace(")", "");
        proteinGroupKey = proteinGroupKey.replace(" ", "").replace(",", "-_-");
        for (String protGroup : proteinGroups.split(",")) {
            proteinGroupsSet.add(protGroup.replace(" ", ""));
        }
    }

    /**
     * Get the unique key for the parent protein group
     *
     * @return unique key
     */
    public String getProteinGroupKey() {
        return proteinGroupKey;
    }

    /**
     * Get main protein accessions for the peptide.
     *
     * @return set of main protein accessions for the peptide
     */
    public Set<String> getProteinsSet() {
        if (proteinsSet.isEmpty()) {

            for (String acc : proteinGroupsSet) {
                acc = acc.replace("(Doubtful)", "").replace("(Confident)", "").replace("(NotValidated)", "").replace(" ", "").replace(",", ";");
                proteinsSet.addAll(Arrays.asList(acc.split(";")));
            }
        }
        return proteinsSet;
    }

    /**
     * Get main protein groups accessions for the peptide.
     *
     * @return set of main protein groups accessions for the peptide
     */
    public Set<String> getProteinGroupsSet() {
        return proteinGroupsSet;
    }

    /**
     * Get number of valid protein groups that include the peptide
     *
     * @return number of valid protein groups
     */
    public int getValidatedProteinGroupsNumber() {
        return validatedProteinGroupsNumber;
    }

    /**
     * Set number of valid protein groups that include the peptide
     *
     * @param validatedProteinGroupsNumber number of valid protein groups
     */
    public void setValidatedProteinGroupsNumber(int validatedProteinGroupsNumber) {
        this.validatedProteinGroupsNumber = validatedProteinGroupsNumber;
    }

    /**
     * Get number of unique database
     *
     * @return unique database number
     */
    public int getUniqueDatabase() {
        return uniqueDatabase;
    }

    /**
     * Set number of unique database
     *
     * @param uniqueDatabase unique database number
     */
    public void setUniqueDatabase(int uniqueDatabase) {
        this.uniqueDatabase = uniqueDatabase;
    }

    /**
     * Get number of validated unique peptides to protein group
     *
     * @return validated unique peptides number
     */
    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    /**
     * Set number of validated unique peptides to protein group
     *
     * @param validatedUniqueToGroupNumber validated unique peptides number
     */
    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    /**
     * Get number of validated PSMs that include the peptide.
     *
     * @return valid PSMs number
     */
    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    /**
     * Set number of validated PSMs that include the peptide.
     *
     * @param validatedPSMsNumber valid PSMs number
     */
    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    /**
     * Get number of PSMs that include the peptide.
     *
     * @return PSMs number
     */
    public int getPSMsNumber() {
        return PSMsNumber;
    }

    /**
     * Set number of PSMs that include the peptide.
     *
     * @param PSMsNumber PSMs number
     */
    public void setPSMsNumber(int PSMsNumber) {
        this.PSMsNumber = PSMsNumber;
    }

    /**
     * Get confidence as % value
     *
     * @return value of confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * Set confidence as % value
     *
     * @param confidence value of confidence
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     * Get validation of peptide
     *
     * @return validation value
     */
    public String getValidation() {
        return validation;
    }

    /**
     * Set validation of peptide
     *
     * @param validation validation value
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     * Get peptide index
     *
     * @return peptide index in the exported file
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set peptide index
     *
     * @param index peptide index in the exported file
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Check if the peptide is modified
     *
     * @return peptide is modified
     */
    public boolean isModified() {
        return (this.variableModifications != null && this.variableModifications.length > 0);
    }

    /**
     * Get intensity hashed colour as string
     *
     * @return hashed colour
     */
    public String getIntensityColor() {
        return intensityColor;
    }

    /**
     * *Set intensity hashed colour as string
     *
     * @param intensityColor hashed colour
     */
    public void setIntensityColor(String intensityColor) {
        this.intensityColor = intensityColor;
    }

}
