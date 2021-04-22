package com.uib.web.peptideshaker.model;

/**
 * Protein object is represents the basic protein information extracted from
 * fasta file
 *
 * @author Yehia Mokhtar Farag
 */
public class ProteinObject {

    private String accession;
    private String description;
    private String sequence;

    /**
     * Get uniprot accession
     *
     * @return
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Set uniprot accession
     *
     * @param accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * Get protein description (name)
     *
     * @return protein name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set protein description (name)
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get protein sequence
     *
     * @return
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set protein sequence
     *
     * @param sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
