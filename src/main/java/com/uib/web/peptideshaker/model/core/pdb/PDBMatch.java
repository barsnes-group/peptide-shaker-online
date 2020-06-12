package com.uib.web.peptideshaker.model.core.pdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PDB match object to store PDB match information
 *
 * @author Yehia Farag
 */
public class PDBMatch {

    /**
     * PDB name (description).
     */
    private String description;
    /**
     * PDB match id.
     */
    private final String pdbId;
    /**
     * PDB match sequence.
     */
    private String sequence;

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    /**
     * List of included chain blocks.
     */
    private final Map<String, List<ChainBlock>> chains;

    /**
     * List of included chain blocks.
     */
    private final List<ChainBlock> fullChainBlocks;
    /**
     * List of chain block IDs.
     */
    int minStartAuth = Integer.MAX_VALUE;
    int maxEndAuth = (-1* Integer.MAX_VALUE);
//    private final Set<String> chainsIds;
    /**
     * Entity ID 'default value is 1'
     */
    private int entity_id = -1;
    private Map<Object,EntityData>  entities;

    public Map<Object,EntityData>  getEntities() {
        return entities;
    }

    public void setEntities(Map<Object,EntityData>  entities) {
        this.entities = entities;
    }

    /**
     * Constructor to initialise main variables and data structure
     *
     * @param pdbId PDB match id.
     */
    public PDBMatch(String pdbId) {
        this.pdbId = pdbId;
        this.chains = new LinkedHashMap<>();
        this.fullChainBlocks = new ArrayList<>();
//        this.chainsIds = new LinkedHashSet<>();
    }

    /**
     * Get list of chain block IDs.
     *
     * @return List IDs.
     */
//    public Set<String> getChainsIds() {
//        return chainsIds;
//    }
    /**
     * Get list of included chain blocks.
     *
     * @return list of chain blocks.
     */
    public List<ChainBlock> getChains() {
        return fullChainBlocks;
    }
     public List<ChainBlock> getChainBlocks(String chainId) {
        return chains.get(chainId);
    }

    /**
     * Set PDB name (description).
     *
     * @param description PDB description
     */
    public void setDescription(String description) {
        this.description = description;
    }

//    /**
//     * Add included chain block ID
//     *
//     * @param chainId chain block ID
//     */
//    public void addChainId(String chainId) {
//
//        this.chainsIds.add(chainId);
//    }
    /**
     * Add included chain block
     *
     * @param chain chain block
     */
    public void addChain(ChainBlock chain) {
        fullChainBlocks.add(chain);
        if (!chains.containsKey(chain.getChain_id())) {
            chains.put(chain.getChain_id(), new ArrayList<>());
        }
        this.chains.get(chain.getChain_id()).add(chain);
        if (chain.getStart_author_residue_number() < minStartAuth) {
            minStartAuth = chain.getStart_author_residue_number();
        }
        if (chain.getEnd_author_residue_number() > maxEndAuth) {
            maxEndAuth = chain.getEnd_author_residue_number();
        }
        if (minStartAuth < 0) {
            minStartAuth = 0;
        }
        if (entity_id == -1) {
            entity_id = chain.getEntityId();
        } else if (entity_id != chain.getEntityId()) {
            entity_id = -2;
        }
    }

    /**
     * Get main PDB match sequence
     *
     * @param chainId selected chain id
     * @return PDB match sequence
     */
    public List<String> getSequence(String chainId) {
        List<String> sequences = new ArrayList<>();
        switch (chainId) {
            case "All":
                chains.values().forEach((chainBlocks) -> {
                    chainBlocks.forEach((block) -> {
                        sequences.add(block.getChain_sequence());
                    });
                });
                break;
            default:
                chains.get(chainId).forEach((block) -> {
                    sequences.add(block.getChain_sequence());
                });
        }
        return sequences;
    }
//
//    /**
//     * Set main PDB match sequence
//     *
//     * @param sequence PDB match sequence
//     */
//    public void setSequence(String sequence) {
//        this.sequence = sequence;
//    }

    /**
     * Get PDB match ID
     *
     * @return PDB match ID
     */
    public String getPdbId() {
        return pdbId;
    }

    /**
     * Get PDB match description
     *
     * @return PDB match short description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get PDB match entity ID
     *
     * @param chainId selected chain ID
     * @return Entity ID 'default value is 1'
     */
    public int getEntity_id(String chainId) {
        switch (chainId) {
            case "All":
                return entity_id;
            default:
                return chains.get(chainId).get(0).getEntityId();
        }
    }
}
