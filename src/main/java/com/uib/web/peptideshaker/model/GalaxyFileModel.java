package com.uib.web.peptideshaker.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class encapsulate main data for galaxy file
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyFileModel implements Serializable{

    private String name;
    private String id;
    private String historyId;
    private final String galaxyType= CONSTANT.GALAXY_FILE;
    private String url;
    private Date createdDate;
    private String extension;
    private String peek;
    private String galaxyJobId;
    private GalaxyJobModel galaxyJob;

    public String getGalaxyJobId() {
        return galaxyJobId;
    }

    public void setGalaxyJobId(String galaxyJobId) {
        this.galaxyJobId = galaxyJobId;
    }

    public String getPeek() {
        return peek;
    }

    public void setPeek(String peek) {
        this.peek = peek;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
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

    public String getGalaxyType() {
        return galaxyType;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    private long size;

    public GalaxyJobModel getGalaxyJob() {
        return galaxyJob;
    }

    public void setGalaxyJob(GalaxyJobModel galaxyJob) {
        this.galaxyJob = galaxyJob;
    }

}
