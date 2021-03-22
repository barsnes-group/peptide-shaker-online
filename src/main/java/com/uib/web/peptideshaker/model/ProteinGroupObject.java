package com.uib.web.peptideshaker.model;

import com.compomics.util.experiment.biology.proteins.Protein;
import graphmatcher.NetworkGraphEdge;
import graphmatcher.NetworkGraphNode;

import java.util.*;

/**
 * This class represents protein for Online PeptideShaker system the class
 * contains all the required information for visualising the proteins data
 *
 * @author Yehia Mokhtar Farag
 */
public class ProteinGroupObject extends Protein {

    private final Set<Integer> peptidesSet;
    private final Map<String, NetworkGraphNode> proteoformsNodes;
    private final Set<NetworkGraphEdge> localEdges;
    /**
     * Set of secondary accessions related to the main accession protein.
     */
    private final Set<String> secondaryAccessionSet;
    /**
     * Protein group accessions set.
     */
    private final Set<String> proteinGroupSet;
    /**
     * List of peptides related to protein group.
     */
    private final Map<String, Boolean> relatedPeptidesList;
    private boolean proteoformUpdated;
    private boolean availableOn_CSF_PR;
    /**
     * UniProt accession number.
     */
    private String accession;
    /**
     * UniProt protein group key.
     */
    private double quantValue;
    /**
     * Intensity value of the quantification using average of all related
     * peptides.
     */
    private double allPeptidesIntensity = 0;
    /**
     * The intensity value as percentage of the quantification using average of
     * all related peptides.
     */
    private double percentageAllPeptidesIntensity = 0;
    /**
     * Intensity hash-code colour of the quantification using average of all
     * related peptides.
     */
    private String allPeptideIintensityColor = "rgb(" + 255 + "," + 255 + "," + 255 + ")";
    /**
     * Intensity value of the quantification using average of unique related
     * peptides.
     */
    private double uniquePeptidesIntensity = 0;
    /**
     * The intensity value as percentage of the quantification using average of
     * unique related peptides.
     */
    private double percentageUniquePeptidesIntensity = 0;
    /**
     * Intensity hash-code colour of the quantification using average of unique
     * related peptides.
     */
    private String uniquePeptideIintensityColor = "rgb(" + 255 + "," + 255 + "," + 255 + ")";
    /**
     * Protein short name.
     */
    private String description;
    /**
     * Gene name.
     */
    private String geneName;
    /**
     * Chromosome name.
     */
    private String chromosome;
    /**
     * Chromosome index.
     */
    private int chromosomeIndex = -1;
    /**
     * Molecular weight.
     */
    private double MW;
    /**
     * Possible protein coverage.
     */
    private double possibleCoverage;
    /**
     * Protein coverage.
     */
    private double coverage;
    /**
     * Number of spectrum.
     */
    private double spectrumCounting;
    /**
     * Confidently localised modification sites.
     */
    private String confidentlyLocalizedModificationSites;
    /**
     * Number of confidently localised modification sites.
     */
    private String ConfidentlyLocalizedModificationSitesNumber;
    /**
     * Ambiguous localised modification sites.
     */
    private String ambiguouslyLocalizedModificationSites;
    /**
     * Number of ambiguous localised modification sites.
     */
    private String ambiguouslyLocalizedModificationSitesNumber;
    /**
     * Protein inference type.
     */
    private String proteinInference;
    /**
     * Secondary accessions related to the main accession protein.
     */
    private String secondaryAccessions;
    /**
     * Protein group that is related to the main protein.
     */
    private String oreginalProteinGroup;
    /**
     * Number of validated peptides.
     */
    private int validatedPeptidesNumber;
    /**
     * Total number of peptides.
     */
    private int peptidesNumber;
    /**
     * Unique number of peptides to the protein.
     */
    private int uniqueNumber;
    /**
     * Unique number of validated peptides to the protein.
     */
    private int validatedUniqueNumber;
    /**
     * Unique number of peptides to the protein group.
     */
    private int uniqueToGroupNumber;
    /**
     * Unique number of validated peptides to the protein group.
     */
    private int validatedUniqueToGroupNumber;
    /**
     * Number of validated PSMs.
     */
    private int validatedPSMsNumber;
    /**
     * Number of PSMs.
     */
    private int PSMsNumber;
    /**
     * Protein confident value in percentage.
     */
    private double confidence;
    /**
     * Validation value.
     */
    private String validation = "Not Validated";
    /**
     * Protein index from the exported PeptideShaker file.
     */
    private int index;
    /**
     * Protein evidence value.
     */
    private String proteinEvidence;
    /**
     * Protein sequence.
     */
    private String sequence;
    private NetworkGraphNode parentNode;
    private String peptideEvidencesLink;

    public String getPeptideEvidencesLink() {
        return peptideEvidencesLink;
    }

    public void setPeptideEvidencesLink(String peptideEvidencesLink) {
        this.peptideEvidencesLink = peptideEvidencesLink;
    }

    /**
     * Constructor to initialise the main data structure.
     */
    public ProteinGroupObject() {
        this.secondaryAccessionSet = new LinkedHashSet<>();
        this.proteinGroupSet = new LinkedHashSet<>();
        this.relatedPeptidesList = new HashMap<>();
        this.peptidesSet = new LinkedHashSet<>();
        this.proteoformsNodes = new LinkedHashMap<>();

        this.localEdges = new HashSet<>();
    }

    public void addPeptide(int peptideIndex) {
        peptidesSet.add(peptideIndex);
    }

    public Set<Integer> getPeptidesSet() {
        return peptidesSet;
    }

    /**
     * Get colour of all peptides intensity
     *
     * @return hashed colour
     */
    public String getAllPeptideIintensityColor() {
        return allPeptideIintensityColor;
    }

    /**
     * Set colour of all peptides intensity
     *
     * @param allPeptideIintensityColor hashed colour
     */
    public void setAllPeptideIintensityColor(String allPeptideIintensityColor) {
        this.allPeptideIintensityColor = allPeptideIintensityColor;
    }

    /**
     * Get the value for intensity for all peptides in protein
     *
     * @return calculated intensity from all quantified peptides
     */
    public double getAllPeptidesIntensity() {
        return allPeptidesIntensity;
    }

    /**
     * *Set the value for intensity for all peptides in protein
     *
     * @param allPeptidesIntensity calculated intensity from all quantified
     * peptides
     */
    public void setAllPeptidesIntensity(double allPeptidesIntensity) {
        this.allPeptidesIntensity = allPeptidesIntensity;
    }

    /**
     * Get protein group unique key
     *
     * @return protein group key
     */
    public String getProteinGroupKey() {
        return oreginalProteinGroup;
    }

    /**
     * Get parent node
     *
     * @return network graph node
     */
    public NetworkGraphNode getParentNode() {
        return parentNode;
    }

    /**
     * Set parent node
     *
     * @param parentNode network graph node
     */
    public void setParentNode(NetworkGraphNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Get chromosome index
     *
     * @return chromosome index
     */
    public int getChromosomeIndex() {
        if (chromosomeIndex == -1) {
            try {
                chromosomeIndex = Integer.parseInt(chromosome);
            } catch (NumberFormatException ex) {
                if (chromosome.equalsIgnoreCase("X")) {
                    chromosomeIndex = 23;
                } else if (chromosome.equalsIgnoreCase("Y")) {
                    chromosomeIndex = 24;
                } else {
                    chromosomeIndex = 25;
                }
            }
        }
        return chromosomeIndex;
    }

    /**
     * Set chromosome index
     *
     * @param chromosomeIndex chromosome number
     */
    public void setChromosomeIndex(int chromosomeIndex) {
        this.chromosomeIndex = chromosomeIndex;
    }

    /**
     * Get protein evidence value.
     *
     * @return Protein evidence value.
     */
    public String getProteinEvidence() {
        return proteinEvidence;
    }

    /**
     * Set protein evidence value.
     *
     * @param proteinEvidence Protein evidence value.
     */
    public void setProteinEvidence(String proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    /**
     * Get protein sequence
     *
     * @return sequence**
     */
    @Override
    public String getSequence() {
        return sequence;
    }

    /**
     * Set protein sequence.
     *
     * @param sequence Standard protein sequence.
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Get set of peptides related to protein group.
     *
     * @return set of peptides keys (modified sequence)
     */
    public Set<String> getRelatedPeptidesList() {
        return relatedPeptidesList.keySet();
    }

    /**
     * Add peptide
     *
     * @param peptideKey peptides keys (modified sequence)
     * @param enzymatic
     */
    public void addPeptideType(String peptideKey, boolean enzymatic) {
        relatedPeptidesList.put(peptideKey, enzymatic);
    }

//    /**
//     * Update peptide type
//     *
//     * @param peptideKey peptides keys (modified sequence)
//     * @param enzymatic enzymatic peptide
//     */
//    public void updatePeptideType(String peptideKey, boolean enzymatic) {
//        relatedPeptidesList.put(peptideKey, enzymatic);
//    }
    /**
     * Check if the peptide is enzymatic
     *
     * @param peptideKey peptide key (modified sequence)
     * @return is enzymatic peptide
     */
    public boolean isEnymaticPeptide(String peptideKey) {
        if (relatedPeptidesList.containsKey(peptideKey)) {
            return relatedPeptidesList.get(peptideKey);
        } else {
            return false;
        }

    }

    /**
     * Check if the peptide is related to protein
     *
     * @param peptideKey peptide key (modified sequence)
     * @return is related peptide
     */
    public boolean isRelatedPeptide(String peptideKey) {
        return relatedPeptidesList.containsKey(peptideKey);
    }

    /**
     * Get the main protein accession
     *
     * @return accession UniProt protein accession
     */
    @Override
    public String getAccession() {
        return accession;
    }

    /**
     * Set the main protein accession
     *
     * @param accession UniProt protein accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * Get protein short name.
     *
     * @return protein name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set protein short name.
     *
     * @param description protein short name.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get gene name
     *
     * @return gene name
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * Set gene name
     *
     * @param geneName gene name
     */
    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    /**
     * Get chromosome name
     *
     * @return chromosome name
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * set chromosome name
     *
     * @param chromosome chromosome name
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * Get molecular weight
     *
     * @return double value
     */
    public double getMW() {
        return MW;
    }

    /**
     * Set molecular weight
     *
     * @param MW double value
     */
    public void setMW(double MW) {
        this.MW = MW;
    }

    /**
     * Get possible protein coverage
     *
     * @return possible protein coverage
     */
    public double getPossibleCoverage() {
        return possibleCoverage;
    }

    /**
     * Set possible protein coverage
     *
     * @param possibleCoverage possible protein coverage
     */
    public void setPossibleCoverage(double possibleCoverage) {
        this.possibleCoverage = possibleCoverage;
    }

    /**
     * Get protein coverage
     *
     * @return protein coverage
     */
    public double getCoverage() {
        return coverage;
    }

    /**
     * Set protein coverage
     *
     * @param coverage protein coverage
     */
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    /**
     * Get number of spectrum
     *
     * @return Number of spectrum
     */
    public double getSpectrumCounting() {
        return spectrumCounting;
    }

    /**
     * Set number of spectrum
     *
     * @param spectrumCounting number of spectrum
     */
    public void setSpectrumCounting(double spectrumCounting) {
        this.spectrumCounting = spectrumCounting;
    }

    /**
     * Get confidently localised modification sites
     *
     * @return Confidently localised modification sites
     */
    public String getConfidentlyLocalizedModificationSites() {
        return confidentlyLocalizedModificationSites;
    }

    /**
     * Set confidently localised modification sites
     *
     * @param confidentlyLocalizedModificationSites confidently localised
     * modification sites
     */
    public void setConfidentlyLocalizedModificationSites(String confidentlyLocalizedModificationSites) {
        this.confidentlyLocalizedModificationSites = confidentlyLocalizedModificationSites;
    }

    /**
     * Get number of confidently localised modification sites.
     *
     * @return Number of confidently localised modification sites.
     */
    public String getConfidentlyLocalizedModificationSitesNumber() {
        return ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     * Set number of confidently localised modification sites.
     *
     * @param ConfidentlyLocalizedModificationSitesNumber number of confidently
     * localised modification sites.
     */
    public void setConfidentlyLocalizedModificationSitesNumber(String ConfidentlyLocalizedModificationSitesNumber) {
        this.ConfidentlyLocalizedModificationSitesNumber = ConfidentlyLocalizedModificationSitesNumber;
    }

    /**
     * Get ambiguous localised modification sites
     *
     * @return Ambiguous localised modification sites
     */
    public String getAmbiguouslyLocalizedModificationSites() {
        return ambiguouslyLocalizedModificationSites;
    }

    /**
     * Set ambiguous localised modification sites
     *
     * @param ambiguouslyLocalizedModificationSites Ambiguous localised
     * modification sites
     */
    public void setAmbiguouslyLocalizedModificationSites(String ambiguouslyLocalizedModificationSites) {
        this.ambiguouslyLocalizedModificationSites = ambiguouslyLocalizedModificationSites;
    }

    /**
     * Get number of ambiguous localised modification sites.
     *
     * @return Number of ambiguous localised modification sites.
     */
    public String getAmbiguouslyLocalizedModificationSitesNumber() {
        return ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     * Set number of ambiguous localised modification sites.
     *
     * @param ambiguouslyLocalizedModificationSitesNumber Number of ambiguous
     * localised modification sites.
     */
    public void setAmbiguouslyLocalizedModificationSitesNumber(String ambiguouslyLocalizedModificationSitesNumber) {
        this.ambiguouslyLocalizedModificationSitesNumber = ambiguouslyLocalizedModificationSitesNumber;
    }

    /**
     * Get protein inference type.
     *
     * @return protein inference type.
     */
    public String getProteinInference() {
        return proteinInference;
    }

    /**
     * Set protein inference type.
     *
     * @param proteinInference protein inference type.
     */
    public void setProteinInference(String proteinInference) {
        this.proteinInference = proteinInference;
    }

    /**
     * Get secondary accessions related to the main accession protein
     *
     * @return Secondary accessions related to the main accession protein
     */
    public String getSecondaryAccessions() {
        return secondaryAccessions;
    }

    /**
     * Set secondary accessions related to the main accession protein
     *
     * @param secondaryAccessions Secondary accessions related to the main
     * accession protein
     */
    public void setSecondaryAccessions(String secondaryAccessions) {
        this.secondaryAccessions = secondaryAccessions;
        secondaryAccessionSet.addAll(Arrays.asList(secondaryAccessions.replace(" ", "").split(",")));
    }

    /**
     * Get protein group that is related to the main protein.
     *
     * @return protein accessions
     */
    public String getOreginalProteinGroup() {
        return oreginalProteinGroup;
    }

    /**
     * Set protein group that is related to the main protein.
     *
     * @param oreginalProteinGroup protein accessions
     */
    public void setOreginalProteinGroup(String oreginalProteinGroup) {
        this.oreginalProteinGroup = oreginalProteinGroup;
    }

    /**
     * Get number of the validated peptides
     *
     * @return validated peptides number
     */
    public int getValidatedPeptidesNumber() {
        return validatedPeptidesNumber;
    }

    /**
     * Set number of the validated peptides
     *
     * @param validatedPeptidesNumber number of the validated peptides
     */
    public void setValidatedPeptidesNumber(int validatedPeptidesNumber) {
        this.validatedPeptidesNumber = validatedPeptidesNumber;
    }

    /**
     * Get number of the peptides
     *
     * @return total number of the peptides
     */
    public int getPeptidesNumber() {
        return peptidesNumber;
    }

    /**
     * Set number of peptides
     *
     * @param peptidesNumber number of peptides
     */
    public void setPeptidesNumber(int peptidesNumber) {
        this.peptidesNumber = peptidesNumber;
    }

    /**
     * Get set of secondary accessions related to the main accession protein.
     *
     * @return set of protein accessions.
     */
    public Set<String> getSecondaryAccessionSet() {
        return secondaryAccessionSet;
    }

    /**
     * Get set of accessions in the protein group.
     *
     * @return set of protein accessions.
     */
    public Set<String> getProteinGroupSet() {
        if (proteinGroupSet.isEmpty()) {
            proteinGroupSet.addAll(Arrays.asList(oreginalProteinGroup.replace(" ", "").split(",")));
        }
        return proteinGroupSet;
    }

    /**
     * Get unique number of peptides to the protein.
     *
     * @return Unique number of peptides to the protein.
     */
    public int getUniqueNumber() {
        return uniqueNumber;
    }

    /**
     * Set unique number of peptides to the protein.
     *
     * @param uniqueNumber Unique number of peptides to the protein.
     */
    public void setUniqueNumber(int uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    /**
     * Get unique number of validated peptides to the protein.
     *
     * @return Unique number of validated peptides to the protein.
     */
    public int getValidatedUniqueNumber() {
        return validatedUniqueNumber;
    }

    /**
     * Set unique number of validated peptides to the protein.
     *
     * @param validatedUniqueNumber Unique number of validated peptides to the
     * protein.
     */
    public void setValidatedUniqueNumber(int validatedUniqueNumber) {
        this.validatedUniqueNumber = validatedUniqueNumber;
    }

    /**
     * Get unique number of peptides to the protein group.
     *
     * @return Unique number of peptides to the protein group.
     */
    public int getUniqueToGroupNumber() {
        return uniqueToGroupNumber;
    }

    /**
     * Set unique number of peptides to the protein group.
     *
     * @param uniqueToGroupNumber Unique number of peptides to the protein
     * group.
     */
    public void setUniqueToGroupNumber(int uniqueToGroupNumber) {
        this.uniqueToGroupNumber = uniqueToGroupNumber;
    }

    /**
     * Get unique number of validated peptides to the protein group.
     *
     * @return Unique number of validated peptides to the protein group.
     */
    public int getValidatedUniqueToGroupNumber() {
        return validatedUniqueToGroupNumber;
    }

    /**
     * Set unique number of validated peptides to the protein group.
     *
     * @param validatedUniqueToGroupNumber Unique number of validated peptides
     * to the protein group.
     */
    public void setValidatedUniqueToGroupNumber(int validatedUniqueToGroupNumber) {
        this.validatedUniqueToGroupNumber = validatedUniqueToGroupNumber;
    }

    /**
     * Get number of validated PSMs.
     *
     * @return Number of validated PSMs.
     */
    public int getValidatedPSMsNumber() {
        return validatedPSMsNumber;
    }

    /**
     * Set number of validated PSMs.
     *
     * @param validatedPSMsNumber Number of validated PSMs.
     */
    public void setValidatedPSMsNumber(int validatedPSMsNumber) {
        this.validatedPSMsNumber = validatedPSMsNumber;
    }

    /**
     * Get Number of PSMs.
     *
     * @return Number of PSMs.
     */
    public int getPSMsNumber() {
        return PSMsNumber;
    }

    /**
     * Set number of PSMs.
     *
     * @param PSMsNumber Number of PSMs.
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
     * Check if accession avaliable on CSF-PR
     *
     * @return available on csf-pr
     */
    public boolean isAvailableOn_CSF_PR() {
        return availableOn_CSF_PR;
    }

    /**
     * Set if the accession avaliable on CSF-PR
     *
     * @param availableOn_CSF_PR protein available on CSF-PR
     */
    public void setAvailableOn_CSF_PR(boolean availableOn_CSF_PR) {
        this.availableOn_CSF_PR = availableOn_CSF_PR;
    }

    /**
     * Add related proteoform node
     *
     * @param node network graph node
     */
    public void addProteoformNode(NetworkGraphNode node) {
        proteoformsNodes.put(node.getNodeId(), node);

    }

    /**
     * Get related proteoform nodes
     *
     * @return map of related proteoforms nodes
     */
    public Map<String, NetworkGraphNode> getProteoformsNodes() {
        return proteoformsNodes;
    }

    /**
     * Check if the proteoform is updated
     *
     * @return proteoform is updated
     */
    public boolean isProteoformUpdated() {
        return proteoformUpdated;
    }

    /**
     * Set proteoform updated
     *
     * @param proteoformUpdated set as updated proteoform
     */
    public void setProteoformUpdated(boolean proteoformUpdated) {
        this.proteoformUpdated = proteoformUpdated;
        for (NetworkGraphNode node : proteoformsNodes.values()) {
            NetworkGraphEdge edge = new NetworkGraphEdge(parentNode, node, true);
            parentNode.addEdge(edge);
            node.addEdge(edge);
            localEdges.add(edge);

        }
    }

    /**
     * Get local edges between proteoform exist in the dataset
     *
     * @return set of edges
     */
    public Set<NetworkGraphEdge> getLocalEdges() {
        return localEdges;
    }

    /**
     * Get calculated quant value
     *
     * @return quantification value
     */
    public double getQuantValue() {
        return quantValue;
    }

    /**
     * Set calculated quant value for the protein
     *
     * @param quantValue quantification value
     */
    public void setQuantValue(double quantValue) {
        this.quantValue = quantValue;
    }

    /**
     * Get intensity based on the unique peptides only
     *
     * @return intensity value
     */
    public double getUniquePeptidesIntensity() {
        return uniquePeptidesIntensity;
    }

    /**
     * Set intensity based on the unique peptides only
     *
     * @param uniquePeptidesIntensity intensity value
     */
    public void setUniquePeptidesIntensity(double uniquePeptidesIntensity) {
        this.uniquePeptidesIntensity = uniquePeptidesIntensity;
    }

    /**
     * Get unique peptide intensity colour range
     *
     * @return hash colour
     */
    public String getUniquePeptideIintensityColor() {
        return uniquePeptideIintensityColor;
    }

    /**
     * Set unique peptide intensity colour range
     *
     * @param uniquePeptideIintensityColor hash colour
     */
    public void setUniquePeptideIintensityColor(String uniquePeptideIintensityColor) {
        this.uniquePeptideIintensityColor = uniquePeptideIintensityColor;
    }

    /**
     * Get total intensity as percentage
     *
     * @return % of intensity
     */
    public double getPercentageAllPeptidesIntensity() {
        return percentageAllPeptidesIntensity;
    }

    /**
     * Set total intensity as percentage
     *
     * @param percentageAllPeptidesIntensity % of intensity
     */
    public void setPercentageAllPeptidesIntensity(double percentageAllPeptidesIntensity) {
        this.percentageAllPeptidesIntensity = percentageAllPeptidesIntensity;
    }

    /**
     * Get intensity based on unique peptides
     *
     * @return
     */
    public double getPercentageUniquePeptidesIntensity() {
        return percentageUniquePeptidesIntensity;
    }

    /**
     * Set intensity based on unique peptides
     *
     * @param percentageUniquePeptidesIntensity intensity value
     */
    public void setPercentageUniquePeptidesIntensity(double percentageUniquePeptidesIntensity) {
        this.percentageUniquePeptidesIntensity = percentageUniquePeptidesIntensity;
    }

}
