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

    /**
     *
     * @return
     */
    public GalaxyJobModel getGalaxyJob() {
        return galaxyJob;
    }

    /**
     *
     * @param galaxyJob
     */
    public void setGalaxyJob(GalaxyJobModel galaxyJob) {
        this.galaxyJob = galaxyJob;
    }
    private final List<GalaxyFileModel> elements;
    private final String type = CONSTANT.GALAXY_COLLECTION;

    /**
     *
     */
    public GalaxyCollectionModel() {
        elements = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
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
    public String getHistoryId() {
        return historyId;
    }

    /**
     *
     * @param historyId
     */
    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     *
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     *
     * @return
     */
    public String getGalaxyJobId() {
        return galaxyJobId;
    }

    /**
     *
     * @param galaxyJobId
     */
    public void setGalaxyJobId(String galaxyJobId) {
        this.galaxyJobId = galaxyJobId;
    }

    /**
     *
     * @param element
     */
    public void addElement(GalaxyFileModel element) {
        elements.add(element);
    }

    /**
     *
     * @return
     */
    public List<GalaxyFileModel> getElements() {
        return elements;
    }

    /**
     *
     * @return
     */
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
