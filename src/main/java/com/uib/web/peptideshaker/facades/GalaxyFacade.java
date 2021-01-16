package com.uib.web.peptideshaker.facades;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.GalaxyJobModel;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.Serializable;
import java.net.ConnectException;
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

    public GalaxyFacade() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.galaxyJobs = new HashMap<>();
    }

    public String authenticate(String userAPIKey) {
        try {
            Response response = appManagmentBean.getHttpClientUtil().doGet(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/users/?key=" + userAPIKey);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonArray jsonArray = new JsonArray(response.readEntity(String.class));
                this.galaxyJobs.clear();
                return jsonArray.getJsonObject(0).getString(CONSTANT.ID);
            }
            appManagmentBean.setAvailableGalaxy(true);
        } catch (Exception e) {
           
        }
        return null;
    }

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
        }
        return success;
    }

    public boolean deleteFile(GalaxyFileModel file, String userAPIKey) {

        JsonObject body = new JsonObject();
        body.put(CONSTANT.DELETED, Boolean.TRUE);
        body.put(CONSTANT.PURGED, Boolean.TRUE);
        Response response = appManagmentBean.getHttpClientUtil().doPut(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/histories/" + file.getHistoryId() + "/contents/datasets/" + file.getId() + "?key=" + userAPIKey, body);
        return (response.getStatus() == HttpStatus.SC_OK);

    }

    public Response uploadFile(FormDataMultiPart multipart) {
        return appManagmentBean.getHttpClientUtil().doUploadPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/tools/?key=" + appManagmentBean.getUserHandler().getLoggedinUserAPIKey(), multipart);

    }

    public Response uploadWorkFlow(JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/?key=" + appManagmentBean.getUserHandler().getLoggedinUserAPIKey(), body);

    }

    public Response deleteWorkFlow(String workflowid) {
        return appManagmentBean.getHttpClientUtil().doDelete(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/" + workflowid + "?key=" + appManagmentBean.getUserHandler().getLoggedinUserAPIKey());

    }

    public Response buildList(JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/tools/?key=" + appManagmentBean.getUserHandler().getLoggedinUserAPIKey(), body);

    }

    public Response invokeWorkFlow(String workflowId, JsonObject body) {
        return appManagmentBean.getHttpClientUtil().doPost(appManagmentBean.getAppConfig().getGalaxyServerUrl() + "/api/workflows/" + workflowId + "/invocations?key=" + appManagmentBean.getUserHandler().getLoggedinUserAPIKey(), body);

    }

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
            ex.printStackTrace();
        }
        return false;
    }
}
