package com.uib.web.peptideshaker.model.core.pdb;

/**
 * This class contain the main information for the chain block required by PDB
 * 3D protein structure visualisation.
 *
 * @author Yehia Farag
 */
public class ChainBlock {

    /**
     * Chain structure id.
     */
    private final String struct_asym_id;
    /**
     * Chain id.
     */
    private final String chain_id;
    /**
     * UniProt Chain sequence.
     */
    private final String uniprot_chain_sequence;
    /**
     * Chain sequence.
     */
    private final String chain_sequence;
    /**
     * Author index of start residue.
     */
    private final int start_author_residue_number;
    /**
     * Index of start residue.
     */
    private final int start_residue_number;
    /**
     * Author index of last residue.
     */
    private final int end_author_residue_number;
    /**
     * Index of last residue.
     */
    private final int end_residue_number;
    private int entityId;
    private int different;

    /**
     * Constructor
     *
     * @param struct_asym_id Chain structure id.
     * @param chain_id Chain id.
     * @param start_author_residue_number Author index of last residue.
     * @param start_residue_number Index of start residue.
     * @param end_author_residue_number Author index of last residue.
     * @param end_residue_number Index of last residue.
     * @param uniprot_chain_sequence Chain sequence.
     * @param chain_sequence actual chain sequence
     */
    public ChainBlock(String struct_asym_id, String chain_id, int start_author_residue_number, int start_residue_number, int end_author_residue_number, int end_residue_number, String uniprot_chain_sequence, String chain_sequence) {
        this.struct_asym_id = struct_asym_id;
        this.chain_id = chain_id;
        this.start_author_residue_number = start_author_residue_number;
        this.start_residue_number = start_residue_number;
        this.end_author_residue_number = end_author_residue_number;
        this.end_residue_number = end_residue_number;
        this.uniprot_chain_sequence = uniprot_chain_sequence;
        this.chain_sequence = chain_sequence;
      
    }

    public String getChain_sequence() {
        return chain_sequence;
    }

    /**
     * Get the chain sequence.
     *
     * @return chain sequence.
     */
    public String getUniprot_chain_sequence() {
        return uniprot_chain_sequence;
    }

    /**
     * Get the chain structure id.
     *
     * @return string of chain structure id.
     */
    public String getStruct_asym_id() {
        return struct_asym_id;
    }

    /**
     * Get author index of the start residue.
     *
     * @return index
     */
    public int getStart_author_residue_number() {
        return start_author_residue_number;
    }

    /**
     * Get index of the start residue.
     *
     * @return index
     */
    public int getStart_residue_number() {
        return start_residue_number;
    }

    /**
     * Get Author index of the last residue.
     *
     * @return index
     */
    public int getEnd_author_residue_number() {
        return end_author_residue_number;
    }

    /**
     * Get index of the last residue.
     *
     * @return index
     */
    public int getEnd_residue_number() {
        return end_residue_number;
    }

    /**
     * Get chain id
     *
     * @return chain id
     */
    public String getChain_id() {
        return chain_id;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getDifferent() {
        return different;
    }

    public void setDifferent(int different) {
        this.different = different;
    }

}
