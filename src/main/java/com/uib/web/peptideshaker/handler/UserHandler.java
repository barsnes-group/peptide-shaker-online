package com.uib.web.peptideshaker.handler;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.vaadin.server.VaadinSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * this class responsible for handling user login security
 *
 * @author Yehia Farag
 */
public class UserHandler implements Serializable {

    private String loggedinUserAPIKey;
    private String loggedinUserId;
    private final AppManagmentBean appManagmentBean;
    private Map<String, String> userInformationMap;
    private List<GalaxyCollectionModel> collectionList;
    private Map<String, GalaxyFileModel> filesMap;
    private Set<GalaxyFileModel> filesToViewList;
    private Set<VisualizationDatasetModel> datasetSet;
    private final Set<String> datasetNames;

    public void addToFilesMap(GalaxyFileModel file) {
        filesMap.put(file.getId(), file);
        filesToViewList.add(file);
        userInformationMap.put(CONSTANT.FILES_NUMBER, filesToViewList.size() + "");

    }

    public void addToDatasetMap(VisualizationDatasetModel dataset) {
        datasetSet.add(dataset);
        this.datasetNames.add(dataset.getName().trim().toLowerCase());
        this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, datasetSet.size() + "");

    }

    public Map<String, GalaxyFileModel> getFilesMap() {
        return filesMap;
    }

    public Set<VisualizationDatasetModel> getDatasetSet() {
        return datasetSet;
    }

    public UserHandler() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.datasetNames = new HashSet<>();
    }

    public void setUserLoggedIn(String userAPIKey, String userId) {
        if (appManagmentBean.isAvailableGalaxy()) {
            appManagmentBean.getAppConfig().setUserFolderUri(userId);
            this.loggedinUserAPIKey = userAPIKey;
            this.loggedinUserId = userId;
            this.filesToViewList = new TreeSet<>();
            appManagmentBean.getGalaxyFacad().initialPeptideShakerUserHistory(loggedinUserAPIKey);
            synchronizeWithGalaxyHistory();
        } else {
            userId = VaadinSession.getCurrent().getSession().getId();
             appManagmentBean.getAppConfig().setUserFolderUri(userId);
            this.loggedinUserAPIKey = userAPIKey;
            this.loggedinUserId = userId;
            this.filesToViewList = new TreeSet<>();            
            this.filesMap = new LinkedHashMap<>();
            this.collectionList = new ArrayList<>();
            this.userInformationMap = new HashMap<>();
            this.userInformationMap.put(CONSTANT.USERNAME, "Offine User");
            this.userInformationMap.put(CONSTANT.STORAGE, CONSTANT.NO_INFORMATION);
        }
        this.datasetSet = constructDatasets();
    }

    private boolean synchronizeWithGalaxyHistory() {
        boolean busyHistory = checkBusyHistory();
        filesToViewList.clear();
        this.userInformationMap = appManagmentBean.getGalaxyFacad().getUserInformation(loggedinUserAPIKey, loggedinUserId);
        //initial user history       
        Object[] storedData = appManagmentBean.getGalaxyFacad().getStoredData(loggedinUserAPIKey);
        this.collectionList = (List<GalaxyCollectionModel>) storedData[0];
        this.filesMap = (Map<String, GalaxyFileModel>) storedData[1];
        filesToViewList.addAll(filesMap.values());
        return busyHistory;
    }

    private Set<VisualizationDatasetModel> constructDatasets() {
        this.datasetNames.clear();
        Set<VisualizationDatasetModel> tempDatasetSet = new TreeSet<>();
        filesMap.values().stream().filter((stepIFile) -> (stepIFile.getExtension().equals(CONSTANT.SEARCH_GUI_FILE_EXTENSION))).map(new Function<GalaxyFileModel, VisualizationDatasetModel>() {
            @Override
            public VisualizationDatasetModel apply(GalaxyFileModel searchGUIFile) {
                VisualizationDatasetModel dataset;
                if (searchGUIFile.getStatus().equals(CONSTANT.OK_STATUS)) {
                    VisualizationDatasetModel tempdataset = new VisualizationDatasetModel();
                    tempdataset.setDatasetSource(CONSTANT.GALAXY_SOURCE);
                    tempdataset.setName(searchGUIFile.getName());
                    tempdataset.setDatasetType(CONSTANT.ID_DATASET);
                    tempdataset.setSearchGUIZipFile(searchGUIFile);
                    tempdataset.setCreatedTime(searchGUIFile.getCreatedDate());
                    tempdataset.setSearchEngines(searchGUIFile.getFileOverview().split("DB:")[0].trim());
                    tempdataset.setFastaFileName(searchGUIFile.getFileOverview().split("DB:")[1].split("sequences:")[0].trim());
                    for (String mgfId : searchGUIFile.getGalaxyJob().getInputFileIds()) {
                        if (appManagmentBean.getGalaxyFacad().trackBackDatasetTool(mgfId, loggedinUserAPIKey).contains("thermo_raw_file_converter")) {
                            tempdataset.setDatasetType(CONSTANT.QUANT_DATASET);
                            break;
                        }
                    }
                    filesToViewList.remove(searchGUIFile);
                    for (GalaxyFileModel stepIIFile : filesMap.values()) {
                        if (!stepIIFile.isVisible() || !stepIIFile.getStatus().equals(CONSTANT.OK_STATUS)) {
                            filesToViewList.remove(stepIIFile);
                        }
                        if (stepIIFile.getGalaxyJob().getInputFileIds().contains(searchGUIFile.getId())) {
                            switch (stepIIFile.getExtension()) {
                                case CONSTANT.ZIP_FILE_EXTENSION:
                                    if (stepIIFile.getStatus().equals(CONSTANT.OK_STATUS)) {
                                        tempdataset.setPsZipFile(stepIIFile);
                                        filesToViewList.remove(stepIIFile);
                                        tempdataset.setDownloadUrl(stepIIFile.getDownloadUrl());
                                        //init search param identification object
                                        tempdataset.setIdentificationParametersObject(appManagmentBean.getDatasetUtils().initIdentificationParametersObject(tempdataset.getId(), tempdataset.getSearchGUIZipFile().getDownloadUrl()));
                                        //add mgf files and indexes 
                                        for (GalaxyCollectionModel collectionModel : collectionList) {
                                            if (collectionModel.getGalaxyJob() != null && collectionModel.getGalaxyJob().getInputFileIds().contains(searchGUIFile.getId())) {
                                                if (collectionModel.getElementsExtension().equals(CONSTANT.CUI_FILE_EXTENSION)) {
                                                    tempdataset.setMgfIndexList(collectionModel);
                                                    collectionModel.getElements().forEach((element) -> {
                                                        filesToViewList.remove(element);
                                                    });
                                                }
                                            } else if (collectionModel.getElementsExtension().equals(CONSTANT.TABULAR_FILE_EXTENSION)) {
                                                collectionModel.getElements().get(0).getGalaxyJob().getInputFileIds().stream().filter((id) -> (tempdataset.getPsZipFile().getGalaxyJob().getOutputFileIds().contains(id))).map((String _item) -> {
                                                    if (collectionModel.getElements().get(0).getGalaxyJob().getToolId().equals(CONSTANT.CONVERT_CHARACTERS_TOOL_ID)) {                                                       
                                                        tempdataset.setMgfList(collectionModel);     
                                                    } else if (collectionModel.getElements().get(0).getGalaxyJob().getToolId().contains(CONSTANT.MOFF_TOOL_ID)) {
                                                        if (collectionModel.getElements().get(0).getStatus().equals(CONSTANT.OK_STATUS)) {
                                                            tempdataset.setMoffList(collectionModel);
                                                        } else {
                                                            tempdataset.setStatus(CONSTANT.RUNNING_STATUS);
                                                        }

                                                    }
                                                    return _item;
                                                }).forEachOrdered((_item) -> {
                                                    collectionModel.getElements().forEach((element) -> {
                                                        filesToViewList.remove(element);
                                                    });
                                                });
                                            }
                                        }

                                    } else {
                                        tempdataset.setStatus(CONSTANT.RUNNING_STATUS);
                                    }
                                    break;
                            }

                        }
                    }
                    dataset = tempdataset;
                } else {
                    dataset = appManagmentBean.getDatasetUtils().getOnProgressDataset(CONSTANT.ID_DATASET);
                    dataset.setName(searchGUIFile.getName());
                }

                return dataset;
            }
        }).forEachOrdered((dataset) -> {
            // init sharing link
            if (dataset.getStatus().equals(CONSTANT.OK_STATUS)) {
                dataset.setSharingLink(appManagmentBean.getDatasetUtils().createSharingLink(loggedinUserAPIKey, dataset));
            } else {
                dataset.setCreatedTime(java.util.Calendar.getInstance().getTime());
            }
            tempDatasetSet.add(dataset);
            this.datasetNames.add(dataset.getName().trim().toLowerCase());
        });
        this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, tempDatasetSet.size() + "");
        this.userInformationMap.put(CONSTANT.FILES_NUMBER, filesToViewList.size() + "");
        return tempDatasetSet;
    }

    public Map<String, String> getUserInformation() {
        return userInformationMap;
    }

    public void getUserStoredData() {

    }

    public Set<GalaxyFileModel> getFilesToViewList() {
        return filesToViewList;
    }

    public void signout() {
        signinAsPublicUser();
    }

    public void signinAsPublicUser() {

    }

    public String getLoggedinUserAPIKey() {
        return loggedinUserAPIKey;
    }

    public void deleteDataset(VisualizationDatasetModel dataset) {
        boolean success = true;
        boolean deleted = appManagmentBean.getGalaxyFacad().deleteFile(dataset.getSearchGUIZipFile(), loggedinUserAPIKey);
        if (!deleted) {
            success = false;
        }
        deleted = appManagmentBean.getGalaxyFacad().deleteFile(dataset.getPsZipFile(), loggedinUserAPIKey);
        if (!deleted) {
            success = false;
        }
        deleted = appManagmentBean.getGalaxyFacad().deleteCollection(dataset.getMgfIndexList(), loggedinUserAPIKey);
        if (!deleted) {
            success = false;
        }
        deleted = appManagmentBean.getGalaxyFacad().deleteCollection(dataset.getMgfList(), loggedinUserAPIKey);
        if (!deleted) {
            success = false;
        }
        if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
            deleted = appManagmentBean.getGalaxyFacad().deleteCollection(dataset.getMoffList(), loggedinUserAPIKey);
            if (!deleted) {
                success = false;
            }
        }
        if (success) {
            appManagmentBean.getNotificationFacade().showInfoNotification("Dataset successfully deleted");
            this.datasetSet.remove(dataset);
            this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, datasetSet.size() + "");
            appManagmentBean.getUI_Manager().updateAll();
        } else {
            appManagmentBean.getNotificationFacade().showAlertNotification("Some or all files are not deleted, try again");
        }
    }

    public void deleteFile(GalaxyFileModel file) {
        boolean deleted = appManagmentBean.getGalaxyFacad().deleteFile(file, loggedinUserAPIKey);
        if (deleted) {
            appManagmentBean.getNotificationFacade().showInfoNotification("File successfully deleted");
            //update user history
            this.filesToViewList.remove(file);
            this.userInformationMap.put(CONSTANT.FILES_NUMBER, filesToViewList.size() + "");
            //update UI
            appManagmentBean.getUI_Manager().updateAll();
        } else {
            appManagmentBean.getNotificationFacade().showAlertNotification("File is not deleted, try again");
        }
    }

    public void clearHistory() {
        boolean busyHistory = appManagmentBean.getGalaxyFacad().isHistoryBusy(loggedinUserAPIKey);
        if (busyHistory) {
            return;
        }
        Set<Object> galaxyItemsToDelete = new HashSet<>();
        for (GalaxyCollectionModel collectionModel : collectionList) {
            if (!collectionModel.getHistoryId().equals(appManagmentBean.getAppConfig().getMainGalaxyHistoryId())) {
                continue;
            }
            if (collectionModel.getElementsExtension().equals(CONSTANT.mzML_FILE_EXTENSION) || collectionModel.getElementsExtension().equals(CONSTANT.THERMO_RAW_FILE_EXTENSION) || (collectionModel.getElementsExtension().equals(CONSTANT.MGF_FILE_EXTENSION) || collectionModel.getElementsExtension().equals(CONSTANT.TXT_FILE_EXTENSION))) {
                galaxyItemsToDelete.add(collectionModel);
            }
        }
        for (GalaxyFileModel fileModel : filesMap.values()) {
            if (!fileModel.getHistoryId().equals(appManagmentBean.getAppConfig().getMainGalaxyHistoryId())) {
                continue;
            }
            if (fileModel.getGalaxyJob().getToolId().equals(CONSTANT.PEPTIDESHAKER_TOOL_ID) && (fileModel.getExtension().equals(CONSTANT.TXT_FILE_EXTENSION) || fileModel.getExtension().equals(CONSTANT.TABULAR_FILE_EXTENSION))) {
                galaxyItemsToDelete.add(fileModel);
            }
        }
        if (!galaxyItemsToDelete.isEmpty()) {
            for (Object object : galaxyItemsToDelete) {
                if (object instanceof GalaxyCollectionModel) {
                    appManagmentBean.getGalaxyFacad().deleteCollection((GalaxyCollectionModel) object, loggedinUserAPIKey);
                } else {
                    appManagmentBean.getGalaxyFacad().deleteFile((GalaxyFileModel) object, loggedinUserAPIKey);
                }
            }
        }

    }

    public Set<String> getDatasetNames() {
        return datasetNames;
    }

    public void syncAndUpdateUserData() {
        this.synchronizeWithGalaxyHistory();
        this.datasetSet = this.constructDatasets();

    }

    public boolean checkBusyHistory() {
        boolean busyHistory = appManagmentBean.getGalaxyFacad().isHistoryBusy(loggedinUserAPIKey);
        if (busyHistory) {
            forceBusyHistory();
        }
        return busyHistory;

    }

    private boolean toFollowUpBusyHistory;
    private ScheduledFuture toFollowUpBusyHistoryFuture;

    public void forceBusyHistory() {
        toFollowUpBusyHistory = true;
        appManagmentBean.getUI_Manager().setOngoingJob(true);
        toFollowUpBusyHistoryFuture = appManagmentBean.getScheduler().scheduleAtFixedRate(() -> {
            boolean busyHistory = appManagmentBean.getGalaxyFacad().isHistoryBusy(loggedinUserAPIKey);
            if (busyHistory) {
                toFollowUpBusyHistory = false;
            }
            if (!busyHistory && !toFollowUpBusyHistory) {
                syncAndUpdateUserData();
                appManagmentBean.getUI_Manager().updateAll();
                appManagmentBean.getUI_Manager().setOngoingJob(false);
                toFollowUpBusyHistoryFuture.cancel(true);
                appManagmentBean.getNotificationFacade().showInfoNotification("Data are ready to visualize!");
            }

        }, 0, 20, TimeUnit.SECONDS);
        appManagmentBean.addScheduledFuture(toFollowUpBusyHistoryFuture);

    }

    public VisualizationDatasetModel getDataset(String datasetId) {
        for (VisualizationDatasetModel dataset : datasetSet) {
            if (dataset.getId().equalsIgnoreCase(datasetId)) {
                return dataset;
            }
        }
        return null;

    }

}