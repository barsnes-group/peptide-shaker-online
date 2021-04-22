package com.uib.web.peptideshaker.model;

import io.vertx.core.json.JsonArray;

/**
 * This class is responsible for storing PDB entity data
 *
 * @author Yehia Mokhtar Farag
 */
public class EntityData {
    private int entityId;
    private String sequence;
    private JsonArray chainIds;
    private int start;
    private int end;
    private int length;

    /**
     *
     * @return
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     *
     * @param entityId
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    /**
     *
     * @return
     */
    public String getSequence() {
        return sequence;
    }

    /**
     *
     * @param sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     *
     * @return
     */
    public JsonArray getChainIds() {
        return chainIds;
    }

    /**
     *
     * @param chainIds
     */
    public void setChainIds(JsonArray chainIds) {
        this.chainIds = chainIds;
    }

    /**
     *
     * @return
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     *
     * @return
     */
    public int getEnd() {
        return end;
    }

    /**
     *
     * @param end
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

}
