
package com.uib.web.peptideshaker.model.core.pdb;

import java.util.List;

/**
 *This class is responsible for storing PDB entity data
 * @author y-mok
 */
public class EntityData {
    private int entityId;
    private String sequence;
    private List chainIds;
    private int start;
    private int end;
    private int length;

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List getChainIds() {
        return chainIds;
    }

    public void setChainIds(List chainIds) {
        this.chainIds = chainIds;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
}
