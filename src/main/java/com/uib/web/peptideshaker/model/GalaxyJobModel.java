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

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getInputFileIds() {
        return inputFileIds;
    }

    public void addInputFileIds(String inputFileId) {
        this.inputFileIds.add(inputFileId);
    }

    public Set<String> getOutputFileIds() {
        return outputFileIds;
    }

    public void addOutputFileIds(String outputFileId) {
        this.outputFileIds.add(outputFileId);
    }

    public GalaxyJobModel() {
        this.inputFileIds = new HashSet<>();
        this.outputFileIds = new HashSet<>();
    }

}
