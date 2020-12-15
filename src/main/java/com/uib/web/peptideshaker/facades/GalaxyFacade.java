package com.uib.web.peptideshaker.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.GalaxyJobModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpStatus;

/**
 * This class responsible for interaction with Galaxy server
 *
 * @author Yehia Mokhtar Farag
 */
public class GalaxyFacade implements Serializable {

    private final AppManagmentBean appManagmentBean;
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss");
    private final Map<String, GalaxyJobModel> galaxyJobs;

    public GalaxyFacade() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.galaxyJobs = new HashMap<>();
    }

    public String authenticate(String userAPIKey) {

        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/users/?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
            this.galaxyJobs.clear();
            return jsonArray.getJsonObject(0).getString(CONSTANT.ID);
        }
        return null;
    }

    public void initialPeptideShakerUserHistory(String userAPIKey) {

        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/?key=" + userAPIKey);
        Set<String> historyNames = new HashSet<>();
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
            for (int i = 0; i < jsonArray.size(); i++) {
                historyNames.add(jsonArray.getJsonObject(i).getString(CONSTANT.NAME));
            }
            if (!historyNames.contains(CONSTANT.WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY)) {
                JsonObject body = new JsonObject();
                body.put(CONSTANT.NAME, CONSTANT.WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY);
                response = appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories?key=" + userAPIKey, body);
                if (response.getStatus() != HttpStatus.SC_OK) {
                    System.out.println("Error: history is not created");
                }

            }
        }
    }

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

    public Object[] getUserData(String userAPIKey) {
        List<GalaxyCollectionModel> collectionList = new ArrayList<>();
        Map<String, GalaxyFileModel> filesMap = new TreeMap<>();
        Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/datasets/?key=" + userAPIKey);
        if (response.getStatus() == HttpStatus.SC_OK) {
            JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject dataset = jsonArray.getJsonObject(i);
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
                        collection.setCreatedDate(format.parse(dataset.getString(CONSTANT.CREATE_TIME)));
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
                        file.setHistoryId(dataset.getString(CONSTANT.HISTORY_ID));
                        String stat = dataset.getString(CONSTANT.STATE);
                        if (stat.equals(CONSTANT.OK_STATUS)) {
                            file.setStatus(CONSTANT.OK_STATUS);
                        } else if (stat.equals("new") || stat.equals("running") || stat.equals("queued")) {
                            file.setStatus(CONSTANT.RUNNING_STATUS);
                        } else {
                            file.setStatus(CONSTANT.ERROR_STATUS);
                        }
                        try {
                            file.setCreatedDate(format.parse(dataset.getString(CONSTANT.CREATE_TIME)));
                        } catch (ParseException ex) {
                            System.out.println("Error : GalaxyFacad - " + ex);
                        }
                        file.setDownloadUrl(appManagmentBean.getAppConfig().getGalaxyServerUrl() + dataset.getString(CONSTANT.URL) + "/display?key=" + userAPIKey);
                        file.setExtension(dataset.getString(CONSTANT.EXTENSION));
                        filesMap.put(file.getId(), file);
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

    public boolean deleteCollection(GalaxyCollectionModel collection, String userAPIKey) {

        boolean success = true;
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
        };
        return success;
    }

    public boolean deleteFile(GalaxyFileModel file, String userAPIKey) {

        JsonObject body = new JsonObject();
        body.put(CONSTANT.DELETED, Boolean.TRUE);
        body.put(CONSTANT.PURGED, Boolean.TRUE);
        Response response = appManagmentBean.getHttpClientUtil().doPut(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + file.getHistoryId() + "/contents/datasets/" + file.getId() + "?key=" + userAPIKey, body);
        return (response.getStatus() == HttpStatus.SC_OK);

    }
}
