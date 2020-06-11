package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import java.util.Date;

/**
 * This class represents the Galaxy file (Dataset) in Online PeptideShaker
 * application
 *
 * @author Yehia Farag
 */
public class GalaxyFileObject {

    /**
     * File name.
     */
    private String name;
    /**
     * File Id on Galaxy Server.
     */
    private String galaxyId;
    /**
     * Galaxy history Id on Galaxy Server where the file located.
     */
    private String historyId;
    /**
     * Direct download link from Galaxy Server or NeLS storage system.
     */
    private String downloadUrl;
    /**
     * File statues on Galaxy Server (file is under processing).
     */
    private String status;
    /**
     * File type.
     */
    private String type;   
    /**
     * File created time on Galaxy Server .
     */
    private Date create_time;
    /**
     * File description.
     */
    private String overview;
    /**
     * File size.
     */
    private double size;   
    /**
     * File available on Galaxy Server.
     */
    private boolean availableOnGalaxy = true;

    /**
     * Get initial file description
     *
     * @return Short description for the file content
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Set initial file description
     *
     * @param overview Short description for the file content
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * Get File created time on Galaxy Server
     *
     * @return Date object include file creation time on Galaxy Server
     */
    public Date getCreate_time() {
        return create_time;
    }

    /**
     * Set File created time on Galaxy Server
     *
     * @param create_time Date object include file creation time on Galaxy
     * Server
     */
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    } 
    /**
     * Check file available on Galaxy Server.
     *
     * @return file ready on Galaxy Server
     */
    public boolean isAvailableOnGalaxy() {
        return availableOnGalaxy;
    }

    /**
     * Set file available on Galaxy Server.
     *
     * @param availableOnGalaxy file exist on Galaxy Server
     */
    public void setAvailableOnGalaxy(boolean availableOnGalaxy) {
        this.availableOnGalaxy = availableOnGalaxy;
    }
    /**
     * Get file statues on Galaxy Server (file is under processing)
     *
     * @return file statues
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set file statues on Galaxy Server (file is under processing)
     *
     * @param status file statues
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get file type
     *
     * @return file type
     */
    public String getType() {
        return type;
    }

    /**
     * Set File type
     *
     * @param type file type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get file size
     *
     * @return file size
     */
    public double getSize() {
        return size;
    }

    /**
     * Set file size
     *
     * @param size file size
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Get the direct download link from Galaxy Server or NeLS storage system
     *
     * @return Direct download link from Galaxy Server or NeLS storage system
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Set the direct download link from Galaxy Server or NeLS storage system
     *
     * @param downloadUrl Direct download link from Galaxy Server or NeLS
     * storage system
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * Get file name
     *
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * Set file name
     *
     * @param name file name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get file Id on Galaxy Server.
     *
     * @return File Id on Galaxy Server.
     */
    public String getGalaxyId() {
        return galaxyId;
    }

    /**
     *Set file Id on Galaxy Server.
     * @param galaxyId File Id on Galaxy Server.
     */
    public void setGalaxyId(String galaxyId) {
        this.galaxyId = galaxyId;
    }

    /**
     *Get galaxy history Id on Galaxy Server where the file located.
     * @return Galaxy history Id on Galaxy Server where the file located
     */
    public String getHistoryId() {
        return historyId;
    }

    /**
     *Set Galaxy history Id on Galaxy Server where the file located
     * @param historyId Galaxy history Id on Galaxy Server where the file located
     */
    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    
}
