package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.ChainBlock;
import com.uib.web.peptideshaker.model.PDBMatch;
import com.uib.web.peptideshaker.model.EntityData;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;

/**
 * Maps UniProt protein accession numbers to PDB file IDs updated to suit the
 * web environment.
 *
 * @author Yehia Mokhtar Farag
 */
public class PDBUtils implements Serializable{

    /**
     * Protein to PDB matches map.
     */
    private final Map<String, Map<String, PDBMatch>> proteinToPDBMap;
    /**
     * EBI web service for PDB data.
     */
    private final Map<String, PDBMatch> pdbMachesMap;
    private final AppManagmentBean appManagmentBean;

    /**
     *
     */
    public PDBUtils() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.proteinToPDBMap = new LinkedHashMap<>();
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
            proteinToPDBMap.putAll(this.getPdbIds(uniProtAccession));
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
            subMap.putAll(this.getPdbSummary(subIds));
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
    public PDBMatch updatePdbInformation(String pdbMatch, String protSequence, Object proteinAccession) {
        if (pdbMachesMap.get(pdbMatch).getChains().isEmpty()) {
            return this.updatePdbInformation(pdbMachesMap.get(pdbMatch), protSequence, proteinAccession);
        } else {
            return pdbMachesMap.get(pdbMatch);
        }
    }

    /**
     * Update PDB matches information (summary)
     *
     * @param pdbMatches map of PDB matches
     * @return update map of PDB matches
     */
    private Map<String, PDBMatch> getPdbSummary(Map<String, PDBMatch> pdbMatches) {
        try {
            String url = "https://www.ebi.ac.uk/pdbe/api/pdb/entry/summary/";
            Response response;
            if (pdbMatches.size() == 1) {
                url += pdbMatches.keySet().iterator().next();
                response = appManagmentBean.getHttpClientUtil().doGet(url);
            } else {
                String str = "";
                for (String pdbID : pdbMatches.keySet()) {
                    str += ",";
                    str += (pdbID);
                }
                str = str.replaceFirst(",", "");
                response = appManagmentBean.getHttpClientUtil().doPost(url, str);

            }
            JsonObject data = new JsonObject(response.readEntity(String.class));
            pdbMatches.keySet().forEach((pdbId) -> {
                String title = data.getJsonArray(pdbId.toLowerCase()).getJsonObject(0).getString("title");
                pdbMatches.get(pdbId).setDescription(title);
            });
        } catch (Exception ex) {
             System.out.println("at Error "+PDBUtils.class.getName()+"   "+ex);
        }
        return pdbMatches;
    }

    /**
     * Update PDB matches information (full information)
     *
     * @param proteinSequence protein sequence
     * @param pdbMatch PDB match
     * @param proteinAccession selected protein accession
     * @return update PDB match
     */
    private PDBMatch updatePdbInformation(PDBMatch pdbMatch, String proteinSequence, Object proteinAccession) {
        try {

            String url = "https://www.ebi.ac.uk/pdbe/api/pdb/entry/molecules/";
            url += pdbMatch.getPdbId().toLowerCase();
            Response response = appManagmentBean.getHttpClientUtil().doGet(url);
            JsonObject data = new JsonObject(response.readEntity(String.class));
            if (data.containsKey(pdbMatch.getPdbId().toLowerCase())) {
                JsonArray subDataArr = data.getJsonArray(pdbMatch.getPdbId().toLowerCase());
                Map<Object, EntityData> entities = new LinkedHashMap<>();
                for (int i = 0; i < subDataArr.size(); i++) {
                    JsonObject entity = subDataArr.getJsonObject(i);
                    int entityId = entity.getInteger("entity_id");
                    String sequence = entity.getString("sequence");
                    if (sequence == null) {
                        continue;
                    }
                    JsonArray inChains = entity.getJsonArray("in_chains");
                    EntityData entityDataObject = new EntityData();
                    entityDataObject.setChainIds(inChains);
                    entityDataObject.setEntityId(entityId);
                    entityDataObject.setSequence(sequence);
                    entities.put(entityDataObject.getEntityId(), entityDataObject);
                }
                pdbMatch.setEntities(entities);
                pdbMatch.setSequence(proteinSequence);

            }
            url = "https://www.ebi.ac.uk/pdbe/api/mappings/uniprot/";
            url += pdbMatch.getPdbId().toLowerCase();
            response = appManagmentBean.getHttpClientUtil().doGet(url);
            data = new JsonObject(response.readEntity(String.class));
            if (data.containsKey(pdbMatch.getPdbId().toLowerCase())) {
                JsonArray subDataArr = data.getJsonObject(pdbMatch.getPdbId().toLowerCase()).getJsonObject("UniProt").getJsonObject(proteinAccession.toString()).getJsonArray("mappings");
                for (int i = 0; i < subDataArr.size(); i++) {
                    JsonObject entityAsJson = subDataArr.getJsonObject(i);
                    int uniprotStart = entityAsJson.getInteger("unp_start");
                    int uniprotEnd = entityAsJson.getInteger("unp_end");
                    JsonObject subChainData = entityAsJson.getJsonObject("start");
                    int start_residue_number = subChainData.getInteger("residue_number");
                    subChainData = entityAsJson.getJsonObject("end");
                    int end_residue_number = subChainData.getInteger("residue_number");
                    String chain_id = entityAsJson.getString("chain_id");
                    String struct_asym_id = entityAsJson.getString("struct_asym_id");
                    int entityId = entityAsJson.getInteger("entity_id");
                    int def = (uniprotEnd - uniprotStart) - (end_residue_number - start_residue_number);
                    ChainBlock chainParam = new ChainBlock(struct_asym_id, chain_id, uniprotStart, start_residue_number, uniprotEnd, end_residue_number, proteinSequence.substring(uniprotStart - 1, uniprotEnd), pdbMatch.getEntities().get(entityId).getSequence().substring(start_residue_number - 1, end_residue_number));
                    chainParam.setEntityId(entityId);
                    chainParam.setDifferent(def);
                    pdbMatch.addChain(chainParam);

                }

            }

        } catch (Exception ex) {
            System.out.println("at Error "+PDBUtils.class.getName()+"   "+ex);
        }
        return pdbMatch;
    }

    /**
     * Match UniProt protein accession to PDB accession
     *
     * @param uniprotAccession UniProt accession
     * @return set of PDB accessions
     */
    public Map<String, Map<String, PDBMatch>> getPdbIds(String uniprotAccession) {
        Set<String> accessions = new HashSet<>();
        accessions.add(uniprotAccession);
        return this.getPdbIds(accessions);
    }

    /**
     * Get PDB matches set from UniProt accessions
     *
     * @param accessions set of UniProt accessions
     * @param isDoGet the HTTP method to use is GET
     * @return set of PDB matches accessions
     */
    private Map<String, Map<String, PDBMatch>> getPdbIds(Set<String> uniprotAccessionSet) {
        Map<String, Map<String, PDBMatch>> pdbMap = new LinkedHashMap<>();
        if (uniprotAccessionSet == null || uniprotAccessionSet.isEmpty()) {
            return pdbMap;
        }
        try {
            String url = "https://www.ebi.ac.uk/pdbe/api/mappings/best_structures/";
            Response response;
            if (uniprotAccessionSet.size() == 1) {
                url += uniprotAccessionSet.iterator().next();
                response = appManagmentBean.getHttpClientUtil().doGet(url);
            } else {
                JsonObject json = new JsonObject();
                uniprotAccessionSet.forEach((accessios) -> {
                    json.put(accessios, new JsonArray());
                });
                response = appManagmentBean.getHttpClientUtil().doPost(url, json);

            }
            JsonObject data = new JsonObject(response.readEntity(String.class));
            uniprotAccessionSet.forEach((acc) -> {
                Map<String, PDBMatch> map = new LinkedHashMap<>();
                if (data.containsKey(acc)) {
                    JsonArray subDataArr = data.getJsonArray(acc);
                    for (int i = 0; i < subDataArr.size(); i++) {
                        JsonObject pdbMachData = subDataArr.getJsonObject(i);
                        if (!map.containsKey(pdbMachData.getString("pdb_id"))) {
                            map.put(pdbMachData.getString("pdb_id"), new PDBMatch(pdbMachData.getString("pdb_id")));
                        }
                    }

                }
                pdbMap.put(acc, map);
            });

        } catch (Exception ex) {
            System.out.println("at Error "+PDBUtils.class.getName()+"   "+ex);
        }
        return pdbMap;
    }

}
