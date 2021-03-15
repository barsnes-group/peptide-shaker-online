package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PRIDECompactProjectModel;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;

/**
 * This class provides utilities for searching and getting data from PRIDE API
 * Rest service
 *
 * @author Yehia Mokhtar Farag
 */
public class PRIDEUtils implements Serializable {

    private final AppManagmentBean appManagmentBean;

    public PRIDEUtils() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
    }

    public Map<String, PRIDECompactProjectModel> searchPRIDEProjects(String keyword, int pagendex) {
        keyword = keyword.replace("'", "").replace(" ", ",");
        keyword = appManagmentBean.getURLUtils().encodeURL(keyword);
        String url = "https://www.ebi.ac.uk/pride/ws/archive/v2/search/projects?keyword=" + keyword + "&filter=organisms%3D%22Homo%20sapiens%20(human)%22&page=" + pagendex + "&sortDirection=DESC&sortFields=submission_date";
        Response response = appManagmentBean.getHttpClientUtil().doGet(url);
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        JsonObject responseData = new JsonObject(response.readEntity(String.class));
        Map<String, PRIDECompactProjectModel> projectsMap = new LinkedHashMap<>();
        if (!responseData.containsKey("_embedded")) {
            return projectsMap;
        }

        JsonArray compactProjectData = responseData.getJsonObject("_embedded").getJsonArray("compactprojects");

        for (int i = 0; i < compactProjectData.size(); i++) {
            JsonObject project = compactProjectData.getJsonObject(i);
            PRIDECompactProjectModel projectModel = new PRIDECompactProjectModel();
            projectModel.setAccession(project.getString("accession"));
            projectModel.setTitle(project.getString("title"));
            projectModel.setProjectDescription(project.getString("projectDescription"));
            projectModel.setSampleProcessingProtocol(project.getString("sampleProcessingProtocol"));
            projectModel.setDataProcessingProtocol(project.getString("dataProcessingProtocol"));
            projectModel.setPublicationDate(project.getString("publicationDate"));
            projectModel.setSubmitters(project.getJsonArray("submitters").toString());
            projectModel.setAffiliations(project.getJsonArray("affiliations").toString());
            projectModel.setInstruments(project.getJsonArray("instruments").toString());
            projectsMap.put(projectModel.getAccession(), projectModel);

        }
        return projectsMap;
    }

    public Map<Integer, ProteinGroupObject> importProjectProteins(String projectAccession) {
        Map<Integer, ProteinGroupObject> proteins = new LinkedHashMap<>();
        int currentPageNumber = 0;
        while (true) {
            String url = "https://www.ebi.ac.uk/pride/ws/archive/v2/proteinevidences?projectAccession=" + projectAccession + "&pageSize=100&page=" + currentPageNumber + "&sortDirection=DESC&sortConditions=projectAccession";
            Response response = appManagmentBean.getHttpClientUtil().doGet(url);
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            JsonObject responseData = new JsonObject(response.readEntity(String.class));
            if (responseData.containsKey("_embedded") && responseData.getJsonObject("_embedded").containsKey("proteinevidences")) {
                JsonArray arr = responseData.getJsonObject("_embedded").getJsonArray("proteinevidences");
                for (int i = 0; i < arr.size(); i++) {
                    ProteinGroupObject protein = new ProteinGroupObject();
                    protein.setAccession(arr.getJsonObject(i).getString("reportedAccession").replace("#", "").replace(">", "").replace("sp|", "").replace("tr|", "").replace("|", "+or+"));
                    protein.setOreginalProteinGroup(arr.getJsonObject(i).getJsonArray("proteinGroupMembers").toString());
                    protein.setPeptidesNumber(arr.getJsonObject(i).getInteger("numberPeptides"));
                    protein.setPSMsNumber(arr.getJsonObject(i).getInteger("numberPSMs"));
                    if (!arr.getJsonObject(i).getValue("sequenceCoverage").toString().equalsIgnoreCase("NaN")) {
                        protein.setCoverage(arr.getJsonObject(i).getInteger("sequenceCoverage"));
                    }
                    if (arr.getJsonObject(i).getBoolean("valid")) {
                        protein.setValidation(CONSTANT.VALIDATION_CONFIDENT);
                    } else {
                        protein.setValidation(CONSTANT.VALIDATION_NOT_VALID);
                    }
                    protein.setPeptideEvidencesLink(arr.getJsonObject(i).getString("peptideevidences"));
                    proteins.put((i + (currentPageNumber * 100)), protein);
                }

            }
            if (responseData.getJsonObject("page").getInteger("totalPages") == (currentPageNumber + 1)) {
                break;
            }            
            currentPageNumber++;
        }
        System.out.println("done loading protiens");
        return mapProteinsToUniProt(proteins);

    }

    private Map<Integer, ProteinGroupObject> mapProteinsToUniProt(Map<Integer, ProteinGroupObject> proteinsToMap) {
        if (proteinsToMap.isEmpty()) {
            return proteinsToMap;
        }

        String fastaAsString = "";
        Set<String> accessions = new LinkedHashSet<>();
        int counter = 0;
        for (ProteinGroupObject protein : proteinsToMap.values()) {
            accessions.add(protein.getAccession());
            if ((counter > 0 && counter % 500 == 0) || counter == proteinsToMap.size() - 1) {
                System.out.println(" call is ready "+counter);
                String strAccs = accessions.toString().substring(1, accessions.toString().length() - 2).replace(" ", "").replace(",", "+or+");
                fastaAsString += getProtienInformationAsFastaFormat(strAccs);
                accessions.clear();

            }
            counter++;
        }
        try {
            if (!fastaAsString.equals("")) {
                InputStream stream = new ByteArrayInputStream(fastaAsString.getBytes(StandardCharsets.UTF_8));
                FastaReader<ProteinSequence, AminoAcidCompound> fastaReader
                        = new FastaReader<>(
                                stream,
                                new GenericFastaHeaderParser<>(),
                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
                LinkedHashMap<String, ProteinSequence> sequenceMap = fastaReader.process();                
                System.out.println(">>><<<<<<<<<<<>>>>>>>>>>>>>>---sequenceMap " + sequenceMap.size() + "  " + proteinsToMap.size());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        appManagmentBean.getNotificationFacade().hideBusyProcess();
        return proteinsToMap;

    }

    private String getProtienInformationAsFastaFormat(String accessions) {
        String url = "https://www.uniprot.org/uniprot/?query=" + accessions + "&format=fasta";
        Response response = appManagmentBean.getHttpClientUtil().doGet(url);
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            System.out.println("error in connection to uniprot");
            return "";
        }
        String str = response.readEntity(String.class);
        response.close();
        return str;

    }

}
