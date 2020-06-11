package com.uib.web.peptideshaker.model.core.pdb;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps UniProt protein accession numbers to PDB file IDs updated to suit the
 * web environment.
 *
 * @author Yehia Farag
 */
public class PdbHandler {

    /**
     * Protein to PDB matches map.
     */
    private final Map<String, Map<String, PDBMatch>> proteinToPDBMap;
    /**
     * EBI web service for PDB data.
     */
    private final EBIRestService EBI_Rest_Service;
    private final Map<String, PDBMatch> pdbMachesMap;

    /**
     *
     */
    public PdbHandler() {
        this.proteinToPDBMap = new LinkedHashMap<>();
        this.EBI_Rest_Service = new EBIRestService();
        this.pdbMachesMap = new LinkedHashMap<>();
    }

    /**
     * Get PDB matches for the selected protein accession
     *
     * @param uniProtAccession UniProt accession
     * @return Map of PDB matches
     */
    public Map<String, PDBMatch> getData(String uniProtAccession) {
        final Map<String, PDBMatch> subMap;
        if (!proteinToPDBMap.containsKey(uniProtAccession)) {
            proteinToPDBMap.putAll(EBI_Rest_Service.getPdbIds(uniProtAccession, true));
        }
        Map<String, PDBMatch> Pdbs = proteinToPDBMap.get(uniProtAccession);
        if (Pdbs == null) {
            return null;
        }
        Map<String, PDBMatch> subIds = new LinkedHashMap<>();
        subMap = new LinkedHashMap<>();
        Pdbs.keySet().forEach((id) -> {
            if (!pdbMachesMap.containsKey(id)) {
                subIds.put(id, Pdbs.get(id));
            } else {
                subMap.put(id, pdbMachesMap.get(id));
            }
        });
        if (!subIds.isEmpty()) {
            subMap.putAll(EBI_Rest_Service.getPdbSummary(subIds));
        }
        pdbMachesMap.putAll(subMap);
        return subMap;
    }

    /**
     * Update PDB match information
     *
     * @param pdbMatch PDB match object
     * @param protSequence protein sequence
     * @param proteinAccession selected protein accession
     * @return updated PDB match object
     */
    public PDBMatch updatePdbInformation(String pdbMatch, String protSequence,Object proteinAccession) {
         if (pdbMachesMap.get(pdbMatch).getChains().isEmpty()) {
            return EBI_Rest_Service.updatePdbInformation(pdbMachesMap.get(pdbMatch), protSequence,proteinAccession);
        } else {
            return pdbMachesMap.get(pdbMatch);
        }
    }

}
