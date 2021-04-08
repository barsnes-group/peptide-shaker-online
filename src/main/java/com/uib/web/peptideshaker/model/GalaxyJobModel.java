package com.uib.web.peptideshaker.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulate galaxy job details
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyJobModel implements Serializable{

    private String id;
    private Set<String> inputFileIds;
    private Set<String> outputFileIds;
    private String toolId;

    /**
     *
     * @return
     */
    public String getToolId() {
        return toolId;
    }

    /**
     *
     * @param toolId
     */
    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public Set<String> getInputFileIds() {
        return inputFileIds;
    }

    /**
     *
     * @param inputFileId
     */
    public void addInputFileIds(String inputFileId) {
        this.inputFileIds.add(inputFileId);
    }

    /**
     *
     * @return
     */
    public Set<String> getOutputFileIds() {
        return outputFileIds;
    }

    /**
     *
     * @param outputFileId
     */
    public void addOutputFileIds(String outputFileId) {
        this.outputFileIds.add(outputFileId);
    }

    /**
     *
     */
    public GalaxyJobModel() {
        this.inputFileIds = new HashSet<>();
        this.outputFileIds = new HashSet<>();
    }

}
