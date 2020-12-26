package com.uib.web.peptideshaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class encapsulate main data for galaxy collection
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyCollectionModel implements Serializable{

    private String name;
    private String id;
    private String historyId;
    private String url;
    private Date createdDate;
    private String galaxyJobId;
    private GalaxyJobModel galaxyJob;

    public GalaxyJobModel getGalaxyJob() {
        return galaxyJob;
    }

    public void setGalaxyJob(GalaxyJobModel galaxyJob) {
        this.galaxyJob = galaxyJob;
    }
    private final List<GalaxyFileModel> elements;
    private final String type = CONSTANT.GALAXY_COLLECTION;

    public GalaxyCollectionModel() {
        elements = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getGalaxyJobId() {
        return galaxyJobId;
    }

    public void setGalaxyJobId(String galaxyJobId) {
        this.galaxyJobId = galaxyJobId;
    }

    public void addElement(GalaxyFileModel element) {
        elements.add(element);
    }

    public List<GalaxyFileModel> getElements() {
        return elements;
    }

    public String getElementsExtension() {
        Set<String> extension = new HashSet<>();
        for (GalaxyFileModel element : elements) {
            extension.add(element.getExtension());
        }
        if (extension.isEmpty()) {
            return "";
        } else if (extension.size() == 1) {
            return extension.iterator().next();
        } else {
            return "multi_types";
        }
    }
}
