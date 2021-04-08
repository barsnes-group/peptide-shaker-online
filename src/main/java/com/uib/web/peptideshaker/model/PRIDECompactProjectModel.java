
package com.uib.web.peptideshaker.model;

/**
 *This class represents PRIDE project compact information
 * @author Yehia Mokhtar Farag
 */
public class PRIDECompactProjectModel {
    private String accession;
    private String title;
    private String projectDescription;
    private String sampleProcessingProtocol;
    private String dataProcessingProtocol;
    
     private String publicationDate;
      private String submitters;
       private String instruments;
   private String affiliations;

    /**
     *
     * @return
     */
    public String getAccession() {
        return accession;
    }

    /**
     *
     * @param accession
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     *
     * @param projectDescription
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     *
     * @return
     */
    public String getSampleProcessingProtocol() {
        return sampleProcessingProtocol;
    }

    /**
     *
     * @param sampleProcessingProtocol
     */
    public void setSampleProcessingProtocol(String sampleProcessingProtocol) {
        this.sampleProcessingProtocol = sampleProcessingProtocol;
    }

    /**
     *
     * @return
     */
    public String getDataProcessingProtocol() {
        return dataProcessingProtocol;
    }

    /**
     *
     * @param dataProcessingProtocol
     */
    public void setDataProcessingProtocol(String dataProcessingProtocol) {
        this.dataProcessingProtocol = dataProcessingProtocol;
    }

    /**
     *
     * @return
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     *
     * @param publicationDate
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     *
     * @return
     */
    public String getSubmitters() {
        return submitters;
    }

    /**
     *
     * @param submitters
     */
    public void setSubmitters(String submitters) {
        this.submitters = submitters;
    }

    /**
     *
     * @return
     */
    public String getInstruments() {
        return instruments;
    }

    /**
     *
     * @param instruments
     */
    public void setInstruments(String instruments) {
        this.instruments = instruments;
    }

    /**
     *
     * @return
     */
    public String getAffiliations() {
        return affiliations;
    }

    /**
     *
     * @param affiliations
     */
    public void setAffiliations(String affiliations) {
        this.affiliations = affiliations;
    }
}
