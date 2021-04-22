package com.uib.web.peptideshaker.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class encapsulate main data for galaxy file
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyFileModel implements Serializable, Comparable<GalaxyFileModel> {

    private String name;
    private String id;
    private String historyId;
    private final String galaxyType = CONSTANT.GALAXY_FILE;
    private Date createdDate;
    private String extension;
    private String peek;
    private String galaxyJobId;
    private GalaxyJobModel galaxyJob;
    private String status;
    private String fileOverview;
    private boolean deleted;
    private boolean visible;

    /**
     * file is visible on galaxy server
     *
     * @return visible file
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * file is visible on galaxy server
     *
     * @param visible file on galaxy
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * file is deleted on galaxy server
     *
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * set file deleted
     *
     * @param deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     *URL path for the file on galaxy
     * @return url path as string
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     *URL path for the file on galaxy
     * @param downloadUrl
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    private String downloadUrl;

    /**
     * Job id used to produce the file
     * @return
     */
    public String getGalaxyJobId() {
        return galaxyJobId;
    }

    /**
     *Set galaxy file id
     * @param galaxyJobId
     */
    public void setGalaxyJobId(String galaxyJobId) {
        this.galaxyJobId = galaxyJobId;
    }

    /**
     * get the overview of the file
     * @return
     */
    public String getPeek() {
        return peek;
    }

    /**
     *set the overview of the file
     * @param peek
     */
    public void setPeek(String peek) {
        this.peek = peek;
    }

    /**
     *get file extension
     * @return
     */
    public String getExtension() {
        return extension;
    }

    /**
     *set file extension
     * @param extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
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
     *Get unique file id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *Set unique file id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The container id (the galaxy history id)
     * @return
     */
    public String getHistoryId() {
        return historyId;
    }

    /**
     * The container id (the galaxy history id)
     * @param historyId
     */
    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    /**
     *Get file type
     * @return
     */
    public String getGalaxyType() {
        return galaxyType;
    }

    /**
     *Get created date
     * @return
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     *Set created date
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     *file size
     * @return
     */
    public long getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }
    private long size;

    /**
     *Get Job object that is used to produce the file
     * @return  GalaxyJobModel
     */
    public GalaxyJobModel getGalaxyJob() {
        return galaxyJob;
    }

    /**
     *Set Job object that is used to produce the file
     * @param galaxyJob
     */
    public void setGalaxyJob(GalaxyJobModel galaxyJob) {
        this.galaxyJob = galaxyJob;
    }

    /**
     *Get file statues (ok,running,new..)
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     *SGet file statues (ok,running,new..)
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(GalaxyFileModel o) {
        return o.createdDate.compareTo(this.createdDate);
    }

    /**
     *
     * @return
     */
    public String getFileOverview() {
        return fileOverview;
    }

    /**
     *
     * @param fileOverview
     */
    public void setFileOverview(String fileOverview) {
        this.fileOverview = fileOverview;
    }

}
