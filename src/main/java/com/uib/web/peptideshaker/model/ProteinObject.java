
package com.uib.web.peptideshaker.model;

/**
 *Protein object is represents the basic protein information extracted from fasta file
 * @author Yehia Mokhtar Farag
 */
public class ProteinObject {
    private String accession;

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    private String description;
    private String sequence;
}
