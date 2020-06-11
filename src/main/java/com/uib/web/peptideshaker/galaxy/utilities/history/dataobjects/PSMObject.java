package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents peptide spectra matched (PSM) for the protein on Online
 * PeptideShaker system the class contains all the required information for
 * visualising the PSM data
 *
 * @author Yehia Farag
 */
public class PSMObject {

    /**
     * Set of proteins where the PSM belong to.
     */
    private final Set<String> proteins;
    /**
     * Standard PSM sequence.
     */
    private String sequence;
    /**
     * PSM modified sequence.
     */
    private String modifiedSequence;
    /**
     * Amino acid score before.
     */
    private String aasBefore;
    /**
     * Amino acid score after.
     */
    private String aasAfter;
    /**
     * PSM positions.
     */
    private String postions;
    /**
     * Protein variable modifications list included in the PSM sequence.
     */
    private final Set<String> variableModifications;
    /**
     * Protein fixed modifications list included in the PSM sequence.
     */
    private final Set<String> fixedModifications;
    /**
     * Spectrum file include the PSM.
     */
    private String spectrumFile;
    /**
     * Spectrum file title include the PSM.
     */
    private String spectrumTitle;
    /**
     * PSM Spectrum scan number.
     */
    private String spectrumScanNumber;
    /**
     * Retention time.
     */
    private String RT;
    /**
     * Math to charge number of ions.
     */
    private String MZ;
    /**
     * Measured charge.
     */
    private String measuredCharge;
    /**
     * Identification charge.
     */
    private String identificationCharge;
    /**
     * Theoretical mass.
     */
    private double theoreticalMass;
    /**
     * Isotope number.
     */
    private int isotopeNumber;
    /**
     * Precursor error value in ppm (parts-per-million).
     */
    private double precursorMZError_PPM;
    /**
     * Intensity value of the quantification.
     */
    private double intensity = -10000;
    /**
     * Intensity hash-code colour of the quantification.
     */
    private String intensityColor;
    /**
     * The intensity value as percentage of the quantification using average of
     * all related peptides.
     */
    private double intensityPercentage = 0;

    /**
     * Get intensity as percentage
     *
     * @return double % value
     */
    public double getIntensityPercentage() {
        return intensityPercentage;
    }

    /**
     * Set intensity as percentage
     *
     * @param intensityPercentage % value
     */
    public void setIntensityPercentage(double intensityPercentage) {
        this.intensityPercentage = intensityPercentage;
    }

    /**
     * Get the hashed colour code for the intensity
     *
     * @return hashed color code
     */
    public String getIntensityColor() {
        return intensityColor;
    }

    /**
     * Set the hashed colour code for the intensity
     *
     * @param intensityColor hashed colour code
     */
    public void setIntensityColor(String intensityColor) {
        this.intensityColor = intensityColor;
    }

    /**
     * Get the intensity value
     *
     * @return value of the intensity
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * Set the intensity value
     *
     * @param intensity value of the intensity
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
    /**
     * Localisation confidence.
     */
    private String localizationConfidence;
    /**
     * Probabilistic PTM (Post-translational modification) Score.
     */
    private String probabilisticPTMScore;
    /**
     * D score.
     */
    private String D_Score;
    /**
     * Confidence value.
     */
    private double confidence;
    /**
     * Validation value.
     */
    private String validation;
    /**
     * PSM index.
     */
    private int index;
    /**
     * PSM tool-tip text value.
     */
    private String tooltip;

    /**
     * Constructor to initialise the main data structure.
     */
    public PSMObject() {
        proteins = new LinkedHashSet<>();
        variableModifications = new LinkedHashSet<>();
        fixedModifications = new LinkedHashSet<>();
    }

    /**
     * Get PSM index
     *
     * @return PSM index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set PSM index
     *
     * @param index PSM index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get set of proteins that include the PSM
     *
     * @return set of parents proteins
     */
    public Set<String> getProteins() {
        return proteins;
    }

    /**
     * Add protein to parent proteins set
     *
     * @param protein protein accession number
     */
    public void addProtein(String protein) {
        this.proteins.add(protein);
    }

    /**
     * Get Standard PSM sequence.
     *
     * @return Standard PSM sequence.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set standard PSM sequence.
     *
     * @param sequence Standard PSM sequence.
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get PSM modified sequence
     *
     * @return PSM modified sequence
     */
    public String getModifiedSequence() {
        return modifiedSequence;
    }

    /**
     * Set PSM modified sequence
     *
     * @param modifiedSequence PSM modified sequence
     */
    public void setModifiedSequence(String modifiedSequence) {
        this.modifiedSequence = modifiedSequence;
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
     * Get PSM positions.
     *
     * @return PSM positions.
     */
    public String getPostions() {
        return postions;
    }

    /**
     * Set PSM positions.
     *
     * @param postions PSM positions.
     */
    public void setPostions(String postions) {
        this.postions = postions;
    }

    /**
     * Get protein variable modifications list included in the PSM sequence.
     *
     * @return modifications list.
     */
    public Set<String> getVariableModifications() {
        return variableModifications;
    }

    /**
     * Add protein variable modification.
     *
     * @param variableModification modification name.
     */
    public void addVariableModification(String variableModification) {
        this.variableModifications.add(variableModification);
    }

    /**
     * Get protein fixed modifications list included in the PSM sequence.
     *
     * @return set of modifications name.
     */
    public Set<String> getFixedModifications() {
        return fixedModifications;
    }

    /**
     * Add protein fixed modifications.
     *
     * @param fixedModification modification name.
     */
    public void addFixedModification(String fixedModification) {
        this.fixedModifications.add(fixedModification);
    }

    /**
     * Get Spectrum file include the PSM.
     *
     * @return Spectrum file include the PSM.
     */
    public String getSpectrumFile() {
        return spectrumFile;
    }

    /**
     * Set Spectrum file include the PSM.
     *
     * @param spectrumFile Spectrum file include the PSM.
     */
    public void setSpectrumFile(String spectrumFile) {
        this.spectrumFile = spectrumFile;
    }

    /**
     * Get spectrum file title include the PSM.
     *
     * @return Spectrum file title include the PSM.
     */
    public String getSpectrumTitle() {
        return spectrumTitle;
    }

    /**
     * Set Spectrum file title include the PSM.
     *
     * @param spectrumTitle Spectrum file title include the PSM.
     */
    public void setSpectrumTitle(String spectrumTitle) {
        this.spectrumTitle = spectrumTitle;
    }

    /**
     * Get PSM Spectrum scan number.
     *
     * @return PSM Spectrum scan number.
     */
    public String getSpectrumScanNumber() {
        return spectrumScanNumber;
    }

    /**
     * Set PSM Spectrum scan number.
     *
     * @param spectrumScanNumber PSM Spectrum scan number.
     */
    public void setSpectrumScanNumber(String spectrumScanNumber) {
        this.spectrumScanNumber = spectrumScanNumber;
    }

    /**
     * Get retention time
     *
     * @return retention time
     */
    public String getRT() {
        return RT;
    }

    /**
     * Set retention time
     *
     * @param RT retention time
     */
    public void setRT(String RT) {
        this.RT = RT;
    }

    /**
     * Get math to charge number of ions
     *
     * @return Math to charge number of ions
     */
    public String getMZ() {
        return MZ;
    }

    /**
     * Set math to charge number of ions
     *
     * @param MZ Math to charge number of ions
     */
    public void setMZ(String MZ) {
        this.MZ = MZ;
    }

    /**
     * Get measured charge.
     *
     * @return measured charge.
     */
    public String getMeasuredCharge() {
        return measuredCharge;
    }

    /**
     * Set Measured charge.
     *
     * @param measuredCharge Measured charge.
     */
    public void setMeasuredCharge(String measuredCharge) {
        this.measuredCharge = measuredCharge;
    }

    /**
     * Set identification charge.
     *
     * @return identification charge.
     */
    public String getIdentificationCharge() {
        return identificationCharge;
    }

    /**
     * Set identification charge.
     *
     * @param identificationCharge identification charge.
     */
    public void setIdentificationCharge(String identificationCharge) {
        this.identificationCharge = identificationCharge;
    }

    /**
     * Get theoretical charge.
     *
     * @return theoretical charge.
     */
    public double getTheoreticalMass() {
        return theoreticalMass;
    }

    /**
     * Set theoretical charge.
     *
     * @param theoreticalMass theoretical charge.
     */
    public void setTheoreticalMass(double theoreticalMass) {
        this.theoreticalMass = theoreticalMass;
    }

    /**
     * Get isotope number
     *
     * @return isotope number
     */
    public int getIsotopeNumber() {
        return isotopeNumber;
    }

    /**
     * Set isotope number
     *
     * @param isotopeNumber isotope number
     */
    public void setIsotopeNumber(int isotopeNumber) {
        this.isotopeNumber = isotopeNumber;
    }

    /**
     * Get Precursor error value in ppm (parts-per-million).
     *
     * @return value in ppm.
     */
    public double getPrecursorMZError_PPM() {
        return precursorMZError_PPM;
    }

    /**
     * Set precursor error value in ppm (parts-per-million).
     *
     * @param precursorMZError_PPM value in ppm .
     */
    public void setPrecursorMZError_PPM(double precursorMZError_PPM) {
        this.precursorMZError_PPM = precursorMZError_PPM;
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
     * Get probabilistic PTM (Post-translational modification) score
     *
     * @return probabilistic PTM score
     */
    public String getProbabilisticPTMScore() {
        return probabilisticPTMScore;
    }

    /**
     * Set probabilistic PTM (Post-translational modification) score
     *
     * @param probabilisticPTMScore probabilistic PTM score
     */
    public void setProbabilisticPTMScore(String probabilisticPTMScore) {
        this.probabilisticPTMScore = probabilisticPTMScore;
    }

    /**
     * Get D-Score
     *
     * @return D-Score
     */
    public String getD_Score() {
        return D_Score;
    }

    /**
     * Set D-Score
     *
     * @param D_Score D-Score
     */
    public void setD_Score(String D_Score) {
        this.D_Score = D_Score;
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
     * Get validation of PSM
     *
     * @return validation value
     */
    public String getValidation() {
        return validation;
    }

    /**
     * Set validation of PSM
     *
     * @param validation validation value
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

    /**
     * Get PSM tool-tip text value.
     *
     * @return tool tip text
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Set PSM tool-tip text value.
     *
     * @param tooltip tool tip text
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

}
