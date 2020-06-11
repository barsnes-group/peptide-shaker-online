package com.uib.web.peptideshaker.model.core.pdb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents EBI web service for PDB data
 *
 * @author Yehia Farag
 */
public class EBIRestService {

    /**
     * Match UniProt protein accession to PDB accession
     *
     * @param uniprotAccessionSet set of UniProt accessions
     * @return set of PDB accessions
     */
    public Map<String, Map<String, PDBMatch>> getPdbIds(Set<String> uniprotAccessionSet) {
        String accessions = uniprotAccessionSet.toString().replace("[", "").replace("]", "");
        return this.getPdbIds(accessions, uniprotAccessionSet.size() == 1);
    }

    /**
     * Get PDB matches set from UniProt accessions
     *
     * @param accessions set of UniProt accessions
     * @param isDoGet the HTTP method to use is GET
     * @return set of PDB matches accessions
     */
    public Map<String, Map<String, PDBMatch>> getPdbIds(String accessions, boolean isDoGet) {
        Map<String, Map<String, PDBMatch>> pdbMap = new LinkedHashMap<>();
        if (accessions == null || accessions.trim().equalsIgnoreCase("")) {
            return pdbMap;
        }
        try {
            String url = "https://www.ebi.ac.uk/pdbe/api/mappings/best_structures/";
            String urlParameters = accessions;
            String respond;
            if (isDoGet) {
                respond = sendGet(url + urlParameters);
            } else {
                respond = sendPost(url, urlParameters);
            }
            if (respond.equalsIgnoreCase("")) {
                return pdbMap;
            }
            JSONObject jsonObject = new JSONObject(respond);

            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                String[] accArr = accessions.split(",");
                for (String acc : accArr) {
                    Map<String, PDBMatch> map = new LinkedHashMap<>();
                    List<Object> l = (List<Object>) retMap.get(acc.toUpperCase().trim());
                    if (l != null) {
                        l.stream().map((o) -> (Map<String, Object>) o).forEachOrdered((supMap) -> {
                            String pdbId = supMap.get("pdb_id") + "";
                            if (!map.containsKey(pdbId)) {
                                map.put(pdbId, new PDBMatch(pdbId));
                            }
                            PDBMatch tMatch = map.get(pdbId);
                        });
                    } else {
                        System.out.println("not exist pdb for it " + acc);
                    }
                    pdbMap.put(acc, map);
                }

            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return pdbMap;
    }

    /**
     * Update PDB matches information (summary)
     *
     * @param pdbMatches map of PDB matches
     * @return update map of PDB matches
     */
    public Map<String, PDBMatch> getPdbSummary(Map<String, PDBMatch> pdbMatches) {
        try {
            String url = " https://www.ebi.ac.uk/pdbe/api/pdb/entry/summary/";
            String urlParameters = pdbMatches.keySet().toString().replace("[", "").replace("]", "");
            String respond;
            if (pdbMatches.size() == 1) {
                respond = sendGet(url + urlParameters);
            } else {
                respond = sendPost(url, urlParameters);
            }
            if (respond.equalsIgnoreCase("")) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(respond);
            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                pdbMatches.keySet().forEach((pdbId) -> {
                    List<Object> l = (List<Object>) retMap.get(pdbId.toLowerCase());
                    Map<String, Object> subMap = (Map<String, Object>) l.get(0);
                    PDBMatch match = pdbMatches.get(pdbId);
                    match.setDescription((String) subMap.get("title"));
                });

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
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
    public PDBMatch updatePdbInformation(PDBMatch pdbMatch, String proteinSequence,Object proteinAccession) {
        try {

            String url = "https://www.ebi.ac.uk/pdbe/api/pdb/entry/molecules/";
            String urlParameters = pdbMatch.getPdbId().toLowerCase();
            String respond;
            respond = sendGet(url + urlParameters);
            JSONObject jsonObject = new JSONObject(respond);
            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                List<Object> l = (List<Object>) retMap.get(urlParameters.toLowerCase());
                Map<String, Object> subMap;
                Map<Object, EntityData> entities = new LinkedHashMap<>();
                for (Object o : l) {
                    subMap = (Map<String, Object>) o;
                    int entityId = (int) subMap.get("entity_id");
                    String sequence = (String) subMap.get("sequence");
                    List inChains = (List) subMap.get("in_chains");
                    if (sequence == null) {
                        continue;
                    }
                    EntityData entity = new EntityData();
                    entity.setChainIds(inChains);
                    entity.setEntityId(entityId);
                    entity.setSequence(sequence);
                    entities.put(entity.getEntityId(), entity);
                }
                pdbMatch.setEntities(entities);
                pdbMatch.setSequence(proteinSequence);
            }

            url = "https://www.ebi.ac.uk/pdbe/api/mappings/uniprot/";
            respond = sendGet(url + urlParameters);
            jsonObject = new JSONObject(respond);
            if (jsonObject != JSONObject.NULL) {
                Map<String, Object> retMap = toMap(jsonObject);
                retMap = (Map<String, Object>) retMap.get(urlParameters.toLowerCase());
                retMap = (Map<String, Object>) retMap.get("UniProt");
                retMap = (Map<String, Object>) retMap.get(proteinAccession+"");
                List subList = (List) retMap.get("mappings");   
                
                for (Object submapObject : subList) {
                    Map<String, Object> subMap = (Map<String, Object>) submapObject;
                    int uniprotStart = (int) subMap.get("unp_start");
                    int uniprotEnd = (int) subMap.get("unp_end");
                    Map<String, Object> subChainData = (Map<String, Object>) subMap.get("start");
                    int start_residue_number = (Integer) subChainData.get("residue_number");
                    subChainData = (Map<String, Object>) subMap.get("end");
                    int end_residue_number = (Integer) subChainData.get("residue_number");
                    String chain_id = subMap.get("chain_id") + "";
                    String struct_asym_id = subMap.get("struct_asym_id") + "";
                    int entityId = (int) subMap.get("entity_id");
                    int def = (uniprotEnd - uniprotStart) - (end_residue_number - start_residue_number);
                    ChainBlock chainParam = new ChainBlock(struct_asym_id, chain_id, uniprotStart, start_residue_number, uniprotEnd, end_residue_number, proteinSequence.substring(uniprotStart - 1, uniprotEnd), pdbMatch.getEntities().get(entityId).getSequence().substring(start_residue_number - 1, end_residue_number));
                    chainParam.setEntityId(entityId);
                    chainParam.setDifferent(def);
                    pdbMatch.addChain(chainParam);
                }

            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return pdbMatch;
    }

//    private int length = -100;
//
//    ;
//    /**
//     * Update PDB matches information (full information)
//     *
//     * @param proteinSequence protein sequence
//     * @param pdbMatch PDB match
//     * @return update PDB match
//     */
//    public PDBMatch updatePdbInformation(PDBMatch pdbMatch, String proteinSequence) {
//        try {
//
//            String url = "https://www.ebi.ac.uk/pdbe/api/pdb/entry/molecules/";
//            String urlParameters = pdbMatch.getPdbId().toLowerCase();
//            String respond;
//            respond = sendGet(url + urlParameters);
//            JSONObject jsonObject = new JSONObject(respond);
//            if (jsonObject != JSONObject.NULL) {
//                Map<String, Object> retMap = toMap(jsonObject);
//                List<Object> l = (List<Object>) retMap.get(urlParameters.toLowerCase());
//                Map<String, Object> subMap = null;
//                List<EntityData> entities = new ArrayList<>();
//                length = -100;
//                for (Object o : l) {
//                    subMap = (Map<String, Object>) o;
//                    int entityId = (int) subMap.get("entity_id");
//                    String sequence = (String) subMap.get("sequence");
//                    List source = (List) subMap.get("source");
//                    List inChains = (List) subMap.get("in_chains");
//                    System.out.println("at subMap--length " + subMap.containsKey("length"));
//
//                    if (subMap.containsKey("length")) {
//                        length = (int) subMap.get("length");
//                    }
//
//                    if (source != null) {
//                        Map<String, Object> sub = (Map<String, Object>) source.get(0);
//                        System.out.println("at sub--length " + sub.containsKey("length"));
//                        List subList = (List) sub.get("mappings");
//                        Map<String, Object> subsub = (Map<String, Object>) subList.get(0);
//                        Map<String, Object> subsub2 = (Map<String, Object>) subsub.get("end");
//                        subsub = (Map<String, Object>) subsub.get("start");
//                        int start = (int) subsub.get("residue_number");
//                        int end = (int) subsub2.get("residue_number");
//                        EntityData entity = new EntityData();
//                        entity.setChainIds(inChains);
//                        entity.setEnd(end);
//                        entity.setEntityId(entityId);
//                        entity.setStart(start);
//                        entity.setSequence(sequence);
//                        entity.setLength(length);
//                        entities.add(entity);
//
//                    }
//
////                    if (o.toString().contains("pdb_sequence")) {                      
////                        for (String chainId : pdbMatch.getChainsIds()) {                         
////                            if (o.toString().contains("in_chains=[" + chainId)) {
////                                subMap = (Map<String, Object>) o;
////                                pdbMatch.setEntity_id(subMap.get("entity_id"));
////                                System.out.println("at sub map keys "+subMap.keySet());
////                            }
////                        }
////                    }
//                }
//                pdbMatch.setEntities(entities);
//                pdbMatch.setSequence(proteinSequence);
//
////                if (subMap != null && subMap.get("pdb_sequence") != null) {
////                    pdbMatch.setSequence(subMap.get("pdb_sequence") + "");
////                } else {
////                    return pdbMatch;
////                }
//                url = "https://www.ebi.ac.uk/pdbe/api/pdb/entry/polymer_coverage/";
//                respond = sendGet(url + urlParameters);
//                jsonObject = new JSONObject(respond);
//                if (jsonObject != JSONObject.NULL) {
//                    retMap = toMap(jsonObject);
//                    subMap = (Map<String, Object>) retMap.get(urlParameters.toLowerCase());
//                    l = (List<Object>) subMap.get("molecules");
//                    for (Object o : l) {
//                        subMap = (Map<String, Object>) o;
//                        int subEntId = (int) subMap.get("entity_id");
//                        if (!subMap.containsKey("entity_id")) {
//                            continue;
//                        }
//
//                        for (EntityData ent : entities) {
//                            if (ent.getEntityId() == subEntId) {
//                                List<Object> subList = (List<Object>) subMap.get("chains");
//                                for (Object subOject : subList) {
//                                    subMap = (Map<String, Object>) subOject;
//                                    String chain_id = subMap.get("chain_id") + "";
//                                    String struct_asym_id = subMap.get("struct_asym_id") + "";
//                                    List<Object> observed = (List<Object>) subMap.get("observed");
//                                    observed.stream().map((subObject) -> (Map<String, Object>) subObject).map((Map<String, Object> chainData) -> {
//                                        Map<String, Object> subChainData = (Map<String, Object>) chainData.get("start");
//                                        int start_author_residue_number = (Integer) subChainData.get("author_residue_number");
//                                        int start_residue_number = (Integer) subChainData.get("residue_number");
//                                        subChainData = (Map<String, Object>) chainData.get("end");
//                                        int end_author_residue_number = (Integer) subChainData.get("author_residue_number");
//                                        int end_residue_number = (Integer) subChainData.get("residue_number");
//                                        String uProtSeq = proteinSequence.replaceAll("L", "I");
//                                        String uChainSeq = ent.getSequence().replace("L", "I").substring(start_residue_number, end_residue_number);
//
//                                        if (start_author_residue_number > uProtSeq.length() || end_author_residue_number > uProtSeq.length()) {
//                                            return null;
//                                        }
//                                        System.out.println(" " + uProtSeq.substring(start_author_residue_number,end_author_residue_number));
//                                        System.out.println(" " + uChainSeq);
//                                        System.out.println(" " + uProtSeq.contains(uChainSeq));
//                                        System.out.println("-------------------------------------------------------------------------------------------------");
//////                                
//                                        ChainBlock chainParam = new ChainBlock(struct_asym_id, chain_id, start_author_residue_number, start_residue_number, end_author_residue_number, end_residue_number, uChainSeq);
//                                        chainParam.setEntityId(ent.getEntityId());
//                                        return chainParam;
//                                    }).forEach((chainParam) -> {
//                                        if (chainParam != null) {
//                                            pdbMatch.addChain(chainParam);
//                                        }
//                                    });
//
//                                }
//
//                            }
//
//                        }
//
//                    }
////                    for (EntityData ent : entities) {
//
////                    subMap = (Map<String, Object>) l.get(ent.getEntityId()-1);
////                    l = (List<Object>) subMap.get("chains");//
////                    for (Object o : l) {
////                        subMap = (Map<String, Object>) o;
////                        String chain_id = subMap.get("chain_id") + "";
////                        String struct_asym_id = subMap.get("struct_asym_id") + "";
////                        List<Object> observed = (List<Object>) subMap.get("observed");
////                        observed.stream().map((subObject) -> (Map<String, Object>) subObject).map((chainData) -> {
////                            Map<String, Object> subChainData = (Map<String, Object>) chainData.get("start");
////                            int start_author_residue_number = (Integer) subChainData.get("author_residue_number");
////                            int start_residue_number = (Integer) subChainData.get("residue_number");
////                            subChainData = (Map<String, Object>) chainData.get("end");
////                            int end_author_residue_number = (Integer) subChainData.get("author_residue_number");
////                            int end_residue_number = (Integer) subChainData.get("residue_number");
////
////                            String chainSequence = ent.getSequence();//.substring(start_residue_number - 1, end_residue_number - 1);
////                            System.out.println("at ent seq "+start_residue_number+"   "+end_residue_number+"  "+ent.getSequence());
////
////                            int tstart_author_residue_number = -5;
////                            int tend_author_residue_number = -5;
//////
////                            int uniprotLength = end_author_residue_number - start_author_residue_number;
////                            int diffrent = (end_residue_number - start_residue_number) - uniprotLength;
////                            String uProtSeq = proteinSequence.replace("L", "I");
////                            String uChainSeq = chainSequence.replace("L", "I");
////
////                           
////                            if (uProtSeq.contains(uChainSeq)) {
////                                tstart_author_residue_number = uProtSeq.indexOf(uChainSeq);
////                                tend_author_residue_number = tstart_author_residue_number + chainSequence.length();
////                            }
////
////                            if (tstart_author_residue_number < 0) {
////                                tstart_author_residue_number = start_author_residue_number;
////                            }
////                            if (tend_author_residue_number < 0) {
////                                tend_author_residue_number = end_author_residue_number;
////                            }
//////
////                            ChainBlock chainParam = new ChainBlock(struct_asym_id, chain_id, tstart_author_residue_number, start_residue_number, tend_author_residue_number, end_residue_number, uChainSeq);
////                            chainParam.setEntityId(ent.getEntityId());
////                            return chainParam;
////                        }).forEach((chainParam) -> {
////                            pdbMatch.addChain(chainParam);
////                        });
////
////                    }
////
////                    }
//                }
//
//            }
//
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//        return pdbMatch;
//    }
    /**
     * Send HTTP doPOST request.
     *
     * @param url link to server
     * @param urlParameters request parameters
     * @return response as string
     */
    private String sendPost(String url, String urlParameters) {

        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            //print result
            return response.toString();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";

    }

    /**
     * Send HTTP doGET request.
     *
     * @param url link to server
     * @return response as string
     */
    private String sendGet(String url) {

        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (Exception e) {

            }

            //print result
            return response.toString();
        } catch (MalformedURLException | ProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JSON object to Java readable list
     *
     * @param array JSON array to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

   
}
