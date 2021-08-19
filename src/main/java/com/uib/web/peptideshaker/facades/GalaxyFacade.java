package com.uib.web.peptideshaker.facades;

import com.compomics.util.experiment.mass_spectrometry.spectra.Peak;
import com.compomics.util.experiment.mass_spectrometry.spectra.Precursor;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.GalaxyJobModel;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 * This class responsible for interaction with Galaxy server
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyFacade implements Serializable {

    private final AppManagmentBean appManagmentBean;
    private final Map<String, GalaxyJobModel> galaxyJobs;
    /**
     *
     */
    public GalaxyFacade() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.galaxyJobs = new HashMap<>();
    }
    /**
     * authenticate user on galaxy server
     *
     * @param userAPIKey
     * @return user name in case of successful login
     */
    public String authenticate(String userAPIKey) {
        try {
            Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/users/?key=" + userAPIKey);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
                this.galaxyJobs.clear();
                appManagmentBean.setAvailableGalaxy(true);
                return jsonArray.getJsonObject(0).getString(CONSTANT.ID);
            }

        } catch (Exception e) {
            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 65 " + e);
        }
        return null;
    }
    /**
     * Create functional user history on galaxy server
     *
     * @param userAPIKey
     */
    public void initialPeptideShakerUserHistory(String userAPIKey) {

        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/?key=" + userAPIKey);
        Set<String> historyNames = new HashSet<>();
        String mainHistoryId = "";
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
            for (int i = 0; i < jsonArray.size(); i++) {
                historyNames.add(jsonArray.getJsonObject(i).getString(CONSTANT.NAME));
                if (jsonArray.getJsonObject(i).getString(CONSTANT.NAME).equals(CONSTANT.WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY)) {
                    mainHistoryId = jsonArray.getJsonObject(i).getString(CONSTANT.ID);
                }

            }
            if (!historyNames.contains(CONSTANT.WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY)) {
                JsonObject body = new JsonObject();
                body.put(CONSTANT.NAME, CONSTANT.WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY);
                response = appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories?key=" + userAPIKey, body);
                if (response.getStatus() != HttpStatus.SC_OK) {
                    System.out.println("Error: history is not created");
                }
                mainHistoryId = new JsonObject(response.readEntity(String.class)).getString(CONSTANT.ID);

            }
            appManagmentBean.getAppConfig().setMainGalaxyHistoryId(mainHistoryId);
        }
    }
    /**
     * Get user information
     *
     * @param userAPIKey
     * @param userId
     * @return map of user information
     */
    public Map<String, String> getUserInformation(String userAPIKey, String userId) {
        Map<String, String> userDetailsMap = new HashMap<>();
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/users/" + userId + "?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            userDetailsMap.put(CONSTANT.USERNAME, jsonObject.getString(CONSTANT.USERNAME).replace("public_user", "Guest User <i style='font-size: 10px;position: relative;top: -23px;left: 101px;'>(public data)</i>"));
            userDetailsMap.put(CONSTANT.STORAGE, jsonObject.getString(CONSTANT.STORAGE));
        }
        return userDetailsMap;
    }
    /**
     * Get user stored data (files and lists)
     *
     * @param userAPIKey
     * @return array of data 1 list of files, 2 list of collections
     */
    public Object[] getStoredData(String userAPIKey) {
        List<GalaxyCollectionModel> collectionList = new ArrayList<>();
        Map<String, GalaxyFileModel> filesMap = new LinkedHashMap<>();
        galaxyJobs.clear();
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories?key=" + userAPIKey + "&q=deleted&q=purged&qv=False&qv=False");
        if (response.getStatus() != HttpStatus.SC_OK) {
            appManagmentBean.getNotificationFacade().showErrorNotification("Error connecting to galaxy server");
            return null;
        }
        JsonArray histories = new JsonArray(response.readEntity(String.class));
        for (int i = 0; i < histories.size(); i++) {
            response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + histories.getJsonObject(i).getString(CONSTANT.ID) + "/contents?key=" + userAPIKey + "&order=hid&v=dev&q=deleted&q=purged&&qv=False&qv=False");
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
                for (int x = 0; x < jsonArray.size(); x++) {
                    JsonObject dataset = jsonArray.getJsonObject(x);
                    if (dataset.getString(CONSTANT.TYPE).equals(CONSTANT.GALAXY_COLLECTION)) {
                        if (dataset.getBoolean(CONSTANT.DELETED)) {
                            continue;
                        }
                        GalaxyCollectionModel collection = new GalaxyCollectionModel();
                        collection.setName(dataset.getString(CONSTANT.NAME));
                        collection.setId(dataset.getString(CONSTANT.ID));
                        collection.setHistoryId(dataset.getString(CONSTANT.HISTORY_ID));
                        collection.setGalaxyJobId(dataset.getString(CONSTANT.JOB_SOURCE_ID));
                        collection.setUrl(appManagmentBean.getAppConfig().getGalaxyServerUrl() + dataset.getString(CONSTANT.URL));
                        try {
                            collection.setCreatedDate(appManagmentBean.getCoreUtils().getDateFormater().parse(dataset.getString(CONSTANT.CREATE_TIME)));
                        } catch (ParseException ex) {
                            System.out.println("Error : GalaxyFacad - " + ex);
                        }
                        collectionList.add(collection);
                    } else {
                        if (dataset.getBoolean(CONSTANT.DELETED) || dataset.getBoolean(CONSTANT.PURGED) || dataset.getString(CONSTANT.STATE).equals(CONSTANT.ERROR)) {
                            continue;
                        }
                       
                        GalaxyFileModel file = new GalaxyFileModel();
                        file.setDeleted(dataset.getBoolean(CONSTANT.DELETED));
                        file.setName(dataset.getString(CONSTANT.NAME));
                        file.setId(dataset.getString(CONSTANT.ID));
                        file.setVisible(dataset.getBoolean(CONSTANT.VISIBLE));
                        file.setHistoryId(dataset.getString(CONSTANT.HISTORY_ID));
                        String stat = dataset.getString(CONSTANT.STATE);
                        switch (stat) {
                            case CONSTANT.OK_STATUS:
                                file.setStatus(CONSTANT.OK_STATUS);
                                break;
                            case "new":
                            case "running":
                            case "queued":
                                file.setStatus(CONSTANT.RUNNING_STATUS);
                                break;
                            default:
                                file.setStatus(CONSTANT.ERROR_STATUS);
                                break;
                        }
                        try {
                            file.setCreatedDate(appManagmentBean.getCoreUtils().getDateFormater().parse(dataset.getString(CONSTANT.CREATE_TIME)));
                        } catch (ParseException ex) {
                            System.out.println("Error : GalaxyFacad - " + ex);
                        }
                        file.setDownloadUrl(appManagmentBean.getAppConfig().getGalaxyServerUrl() + dataset.getString(CONSTANT.URL) + "/display?key=" + userAPIKey);
                        file.setExtension(dataset.getString(CONSTANT.EXTENSION));
                        filesMap.put(file.getId(), file);
                    }

                }
            }
        }
        filesMap.values().forEach((file) -> {
            fillFileDetails(file, userAPIKey);
            if (galaxyJobs.containsKey(file.getGalaxyJobId()) && galaxyJobs.get(file.getGalaxyJobId()) == null) {
                GalaxyJobModel jobModel = fillJobDetails(file.getGalaxyJobId(), userAPIKey);
                galaxyJobs.replace(file.getGalaxyJobId(), jobModel);
            }
            file.setGalaxyJob(galaxyJobs.get(file.getGalaxyJobId()));
        });
        collectionList.forEach((collection) -> {
            populateCollections(collection, userAPIKey, filesMap);
            if (galaxyJobs.containsKey(collection.getGalaxyJobId()) && galaxyJobs.get(collection.getGalaxyJobId()) == null) {
                GalaxyJobModel jobModel = fillJobDetails(collection.getGalaxyJobId(), userAPIKey);
                galaxyJobs.replace(collection.getGalaxyJobId(), jobModel);
            } else if (!galaxyJobs.containsKey(collection.getGalaxyJobId())) {
                GalaxyJobModel jobModel = fillJobDetails(collection.getGalaxyJobId(), userAPIKey);
                galaxyJobs.put(collection.getGalaxyJobId(), jobModel);

            }
            collection.setGalaxyJob(galaxyJobs.get(collection.getGalaxyJobId()));

        });
        /**
         * update indexed mgf file names
         */
        for (GalaxyCollectionModel collectionModel : collectionList) {
            if (collectionModel.getElements() != null && !collectionModel.getElements().isEmpty()) {
                if (collectionModel.getElements().get(0).getGalaxyJob().getToolId().equals(appManagmentBean.getAppConfig().getCONVERT_CHARACTERS_TOOL_ID())) {
                    collectionModel.getElements().forEach((file) -> {
                        String name = getFileName(userAPIKey, file.getGalaxyJob().getInputFileIds().iterator().next());
                        if (name != null) {
                            file.setName(name);
                        }
                    });
                }

            }

        }

        return new Object[]{collectionList, filesMap};
    }
    private void fillFileDetails(GalaxyFileModel file, String userAPIKey) {
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/datasets/" + file.getId() + "?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            file.setPeek(jsonObject.getString(CONSTANT.PEEK));
            file.setSize(jsonObject.getLong(CONSTANT.FILE_SIZE));
            file.setFileOverview(jsonObject.getString(CONSTANT.GALAXY_FILE_OVERVIEW));
            if (!galaxyJobs.containsKey(jsonObject.getString(CONSTANT.CREATING_JOB_ID))) {
                galaxyJobs.put(jsonObject.getString(CONSTANT.CREATING_JOB_ID), null);
            }
            file.setGalaxyJobId(jsonObject.getString(CONSTANT.CREATING_JOB_ID));

        }

    }
    private String getFileName(String userAPIKey, String fileId) {
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/datasets/" + fileId + "?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            return jsonObject.getString(CONSTANT.NAME);

        }
        return null;

    }
    private void populateCollections(GalaxyCollectionModel collection, String userAPIKey, Map<String, GalaxyFileModel> filesMap) {
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + collection.getHistoryId() + "/contents/dataset_collections/" + collection.getId() + "?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            JsonArray elementsArr = jsonObject.getJsonArray(CONSTANT.COLLECTION_ELEMENTS);
            for (int i = 0; i < elementsArr.size(); i++) {
                JsonObject element = elementsArr.getJsonObject(i);
                String fileId = element.getJsonObject(CONSTANT.COLLECTION_OBJECT).getString(CONSTANT.ID);
                if (filesMap.containsKey(fileId)) {
                    collection.addElement(filesMap.get(fileId));
                }
            }
        }
    }
    private GalaxyJobModel fillJobDetails(String jobId, String userAPIKey) {
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/jobs/" + jobId + "?key=" + userAPIKey);
        GalaxyJobModel jobModel = new GalaxyJobModel();
        if (response.getStatus() == HttpStatus.SC_OK) {

            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            jobModel.setId(jobId);
            jobModel.setToolId(jsonObject.getString(CONSTANT.TOOL_ID));
            JsonObject inputs = jsonObject.getJsonObject(CONSTANT.JOB_INPUTS);
            Set<String> keys = inputs.fieldNames();
            keys.forEach((key) -> {
                jobModel.addInputFileIds(inputs.getJsonObject(key).getString(CONSTANT.ID));
            });
            JsonObject outputs = jsonObject.getJsonObject(CONSTANT.JOB_OUTPUTS);
            keys = outputs.fieldNames();
            keys.forEach((key) -> {
                jobModel.addOutputFileIds(outputs.getJsonObject(key).getString(CONSTANT.ID));
            });
        }
        return jobModel;
    }

    /**
     * find the tool id used to produce the dataset
     *
     * @param datasetId
     * @param userAPIKey
     * @return tool id
     */
    public String trackBackDatasetTool(String datasetId, String userAPIKey) {
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/datasets/" + datasetId + "?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
            response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/jobs/" + jsonObject.getString(CONSTANT.CREATING_JOB_ID) + "?key=" + userAPIKey);
            if (response.getStatus() == HttpStatus.SC_OK) {
                jsonObject = new JsonObject(response.readEntity(String.class));
                return jsonObject.getString(CONSTANT.TOOL_ID);
            }
        }
        return null;
    }

    /**
     * delete list of files on galaxy server
     *
     * @param collection
     * @param userAPIKey
     * @return deleted
     */
    public boolean deleteCollection(GalaxyCollectionModel collection, String userAPIKey) {

        boolean success = true;
        if (collection == null || collection.getElements() == null) {
            return true;
        }
        for (GalaxyFileModel element : collection.getElements()) {
            boolean deleted = deleteFile(element, userAPIKey);
            if (!deleted) {
                success = false;
            }
        }
        JsonObject body = new JsonObject();
        body.put(CONSTANT.DELETED, Boolean.TRUE);
        Response response = appManagmentBean.getHttpClientUtil().doPut(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + collection.getHistoryId() + "/contents/dataset_collections/" + collection.getId() + "?key=" + userAPIKey, body);
        if (response.getStatus() != HttpStatus.SC_OK) {
            success = false;
        }
        return success;
    }

    /**
     * Delete file from galaxy server
     *
     * @param file
     * @param userAPIKey
     * @return deleted
     */
    public boolean deleteFile(GalaxyFileModel file, String userAPIKey) {

        JsonObject body = new JsonObject();
        body.put(CONSTANT.DELETED, Boolean.TRUE);
        body.put(CONSTANT.PURGED, Boolean.TRUE);
        if (file == null || file.getHistoryId() == null || file.getId() == null) {
            return true;
        }
        Response response = appManagmentBean.getHttpClientUtil().doPut(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + file.getHistoryId() + "/contents/datasets/" + file.getId() + "?key=" + userAPIKey, body);
        return (response.getStatus() == HttpStatus.SC_OK);

    }

    /**
     * Upload file to galaxy server
     *
     * @param multipart
     * @return Response
     */
    public Response uploadFile(FormDataMultiPart multipart) {
        return appManagmentBean.getHttpClientUtil().doUploadPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/tools/?key=" + appManagmentBean.getUserHandler().getUserAPIKey(), multipart);

    }

    /**
     * Upload workflow as json text to galaxy server
     *
     * @param body
     * @return Response
     */
    public Response uploadWorkFlow(JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/?key=" + appManagmentBean.getUserHandler().getUserAPIKey(), body);

    }

    /**
     * Delete workflow
     *
     * @param workflowid
     * @return Response
     */
    public Response deleteWorkFlow(String workflowid) {
        return appManagmentBean.getHttpClientUtil().doDelete(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/" + workflowid + "?key=" + appManagmentBean.getUserHandler().getUserAPIKey());

    }

    /**
     * build list of input files on galaxy server
     *
     * @param body
     * @return Response
     */
    public Response buildList(JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/tools/?key=" + appManagmentBean.getUserHandler().getUserAPIKey(), body);

    }

    /**
     * Invoke workflow on galaxy server
     *
     * @param workflowId
     * @param body
     * @return Response
     */
    public Response invokeWorkFlow(String workflowId, JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/" + workflowId + "/invocations?key=" + appManagmentBean.getUserHandler().getUserAPIKey(), body);

    }

    /**
     * Check if there is a job still running
     *
     * @param userAPIKey
     * @return job is running
     */
    public boolean isHistoryBusy(String userAPIKey) {
        try {
            Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories?key=" + userAPIKey);
            if (response.getStatus() != HttpStatus.SC_OK) {
                appManagmentBean.getNotificationFacade().showErrorNotification("Error connecting to galaxy server");
                return false;
            }

            JsonArray histories = new JsonArray(response.readEntity(String.class));
            for (int i = 0; i < histories.size(); i++) {
                response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + histories.getJsonObject(i).getString(CONSTANT.ID) + "?key=" + userAPIKey);
                if (response.getStatus() != HttpStatus.SC_OK) {
                    appManagmentBean.getNotificationFacade().showErrorNotification("Error connecting to galaxy server");
                    return false;
                }
                JsonObject history = new JsonObject(response.readEntity(String.class));
                if (!history.getString(CONSTANT.STATE).equals(CONSTANT.OK_STATUS) && !(history.getString(CONSTANT.STATE).equals(CONSTANT.NEW_STATUS) && history.getLong(CONSTANT.SIZE) == 0)) {
                    return true;
                }

            }
        } catch (Exception ex) {
            appManagmentBean.getNotificationFacade().showErrorNotification("Error connecting to galaxy server");
            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 431 " + ex);
        }
        return false;
    }

    /**
     * Get MSn spectrum object using HTML request to Galaxy server (byte serving
     * support).
     *
     * @param startIndex the spectra index on the MGF file
     * @param historyId the Galaxy Server History ID that contain the MGF file
     * @param fileId The ID of the MGF file on Galaxy Server
     * @param MGFFileName The MGF file name
     * @param charge
     * @param userAPIKey
     * @return MSnSpectrum spectrum object
     */
    public Spectrum streamSpectrum(long startIndex, String historyId, String fileId, String MGFFileName, int charge, String userAPIKey) {
        String locationBuilder = (appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + historyId + "/contents/" + fileId + "/display?key=" + userAPIKey);
        double precursorMz = 0, precursorIntensity = 0, rt = -1.0, rt1 = -1, rt2 = -1;
        int[] precursorCharges = null;
        String scanNumber = "", spectrumTitle = "";
        HashMap<Double, Peak> spectrum = new HashMap<>();
        String line;
        boolean insideSpectrum = false;
        ArrayList<Double> mzList = new ArrayList<>(0);
        ArrayList<Double> intensityList = new ArrayList<>(0);
        try (BufferedReader bin = appManagmentBean.getHttpClientUtil().byteServingStreamFile(locationBuilder, startIndex + "", "")) {
            while ((line = bin.readLine()) != null) {
                String[] spectrumData = line.split("\n");
                for (String str : spectrumData) {
                    line = str;
                    // fix for lines ending with \r
                    if (line.endsWith("\r")) {
                        line = line.replace("\r", "");
                    }

                    if (line.startsWith("BEGIN")) {
                        insideSpectrum = true;
                        mzList = new ArrayList<>();
                        intensityList = new ArrayList<>();
                    } else if (line.startsWith("TITLE")) {
                        insideSpectrum = true;
                        spectrumTitle = line.substring(line.indexOf('=') + 1);
                        try {
                            spectrumTitle = URLDecoder.decode(spectrumTitle, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("Error: 113 :An exception was thrown when trying to decode an mgf title: " + spectrumTitle + "  " + e);
                        }
                    } else if (line.startsWith("CHARGE")) {
                        precursorCharges = parseCharges(line);
                    } else if (line.startsWith("PEPMASS")) {
                        String temp = line.substring(line.indexOf("=") + 1);
                        String[] values = temp.split("\\s");
                        precursorMz = Double.parseDouble(values[0]);
                        if (values.length > 1) {
                            precursorIntensity = Double.parseDouble(values[1]);
                        } else {
                            precursorIntensity = 0.0;
                        }
                    } else if (line.startsWith("RTINSECONDS")) {
                        try {
                            String rtInput = line.substring(line.indexOf('=') + 1);
                            String[] rtWindow = rtInput.split("-");
                            if (rtWindow.length == 1) {
                                String tempRt = rtWindow[0];
                                // possible fix for values like RTINSECONDS=PT121.250000S
                                if (tempRt.startsWith("PT") && tempRt.endsWith("S")) {
                                    tempRt = tempRt.substring(2, tempRt.length() - 1);
                                }
                                rt = new Double(tempRt);
                            } else if (rtWindow.length == 2) {
                                rt1 = new Double(rtWindow[0]);
                                rt2 = new Double(rtWindow[1]);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("An exception was thrown when trying to decode the retention time: " + spectrumTitle);
                            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 508 " + e);
                            // ignore exception, RT will not be parsed
                        }
                    } else if (line.startsWith("TOLU")) {
                        // peptide tolerance unit not implemented
                    } else if (line.startsWith("TOL")) {
                        // peptide tolerance not implemented
                    } else if (line.startsWith("SEQ")) {
                        // sequence qualifier not implemented
                    } else if (line.startsWith("COMP")) {
                        // composition qualifier not implemented
                    } else if (line.startsWith("ETAG")) {
                        // error tolerant search sequence tag not implemented
                    } else if (line.startsWith("TAG")) {
                        // sequence tag not implemented
                    } else if (line.startsWith("SCANS")) {
                        try {
                            scanNumber = line.substring(line.indexOf('=') + 1);
                        } catch (Exception e) {
                            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 527 " + e);
                            throw new IllegalArgumentException("Cannot parse scan number.");
                        }
                    } else if (line.startsWith("INSTRUMENT")) {
                        // ion series not implemented
                    } else if (line.startsWith("END")) {

                        if (precursorCharges == null) {
                            precursorCharges = new int[]{charge};
                        }
                        Precursor precursor;
                        if (rt1 != -1 && rt2 != -1) {
                            precursor = new Precursor(precursorMz, precursorIntensity, precursorCharges, rt1, rt2);
                        } else {
                            precursor = new Precursor(rt, precursorMz, precursorIntensity, precursorCharges);
                        }

                        double[] mzArray = mzList.stream()
                                .mapToDouble(
                                        a -> a
                                )
                                .toArray();
                        double[] intensityArray = intensityList.stream()
                                .mapToDouble(
                                        a -> a
                                )
                                .toArray();
                        bin.close();
                        return new Spectrum(precursor, mzArray, intensityArray);

                    } else if (insideSpectrum && !line.equals("")) {
                        try {
                            String values[] = line.split("\\s+");
                            double mz = Double.parseDouble(values[0]);
                            mzList.add(mz);
                            double intensity = Double.parseDouble(values[1]);
                            intensityList.add(intensity);
                        } catch (NumberFormatException e1) {
                            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 565 " + e1);
                            // ignore comments and all other lines
                        }
                    }

                }

            }

        } catch (Exception ex) {
            System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 575 " + ex);
        }
        System.out.println("Error: 214 : null spectrum is returned");
        return null;
    }

    /**
     * Parses the charge line of an MGF files.
     *
     * @param chargeLine the charge line
     * @return the possible charges found
     * @throws IllegalArgumentException
     */
    private int[] parseCharges(String chargeLine) {

        ArrayList<Integer> result = new ArrayList<>(1);
        String tempLine = chargeLine.substring(chargeLine.indexOf("=") + 1);
        String[] chargesAnd = tempLine.split(" and ");
        ArrayList<String> chargesAsString = new ArrayList<>();

        for (String charge : chargesAnd) {
            for (String charge2 : charge.split(",")) {
                chargesAsString.add(charge2.trim());
            }
        }
        chargesAsString.stream().map((chargeAsString) -> chargeAsString.trim()).filter((chargeAsString) -> (!chargeAsString.isEmpty())).forEachOrdered((chargeAsString) -> {
            try {
                if (chargeAsString.endsWith("+")) {
                    int value = Integer.parseInt(chargeAsString.substring(0, chargeAsString.length() - 1));
                    result.add(value);
                } else if (chargeAsString.endsWith("-")) {
                    int value = Integer.parseInt(chargeAsString.substring(0, chargeAsString.length() - 1));
                    result.add(value);
                } else if (!chargeAsString.equalsIgnoreCase("Mr")) {
                    result.add(Integer.parseInt(chargeAsString));
                }
            } catch (NumberFormatException e) {
                System.out.println("at Error " + GalaxyFacade.class.getName() + "  line: 613 " + e);
                throw new IllegalArgumentException("\'" + chargeAsString + "\' could not be processed as a valid precursor charge!");
            }
        });
        // if empty, add a default charge of 1
        if (result.isEmpty()) {
            result.add(1);
        }

        return result.stream()
                .mapToInt(a -> a)
                .toArray();
    }

}
