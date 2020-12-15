package com.uib.web.peptideshaker.handler;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.utils.DatasetUtils;
import com.vaadin.server.VaadinSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    private List<GalaxyFileModel> filesToViewList;
    private Map<String, VisualizationDatasetModel> datasetMap;

    public Map<String, GalaxyFileModel> getFilesMap() {
        return filesMap;
    }

    public Map<String, VisualizationDatasetModel> getDatasetMap() {
        return datasetMap;
    }
    private final DatasetUtils datasetUtils;

    public UserHandler() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.datasetUtils = new DatasetUtils();
    }

    public void setUserLoggedIn(String userAPIKey, String userId) {
        appManagmentBean.getAppConfig().setUserFolderUri(userId);
        this.loggedinUserAPIKey = userAPIKey;
        this.loggedinUserId = userId;
        this.filesToViewList = new ArrayList<>();
        appManagmentBean.getGalaxyFacad().initialPeptideShakerUserHistory(loggedinUserAPIKey);
        updateUserHistory();
        this.datasetMap = constructDatasets();

    }

    private void updateUserHistory() {
        filesToViewList.clear();
        this.userInformationMap = appManagmentBean.getGalaxyFacad().getUserInformation(loggedinUserAPIKey, loggedinUserId);
        //initial user history       
        Object[] userData = appManagmentBean.getGalaxyFacad().getUserData(loggedinUserAPIKey);
        this.collectionList = (List<GalaxyCollectionModel>) userData[0];
        this.filesMap = (Map<String, GalaxyFileModel>) userData[1];
        this.userInformationMap.put(CONSTANT.FILES_NUMBER, filesToViewList.size() + "");
        filesToViewList.addAll(filesMap.values());
        Collections.sort(filesToViewList);
    }

    private Map<String, VisualizationDatasetModel> constructDatasets() {

        Map<String, VisualizationDatasetModel> datasetsMap = new TreeMap<>();
        filesMap.values().stream().filter((stepIFile) -> (stepIFile.getExtension().equals(CONSTANT.SEARCH_GUI_FILE_EXTENSION))).map(new Function<GalaxyFileModel, VisualizationDatasetModel>() {
            @Override
            public VisualizationDatasetModel apply(GalaxyFileModel searchGUIFile) {
                VisualizationDatasetModel dataset = new VisualizationDatasetModel();
                dataset.setUploadedDataset(false);
                dataset.setDatasetType(CONSTANT.ID_DATASET);
                dataset.setSearchGUIZipFile(searchGUIFile);
                dataset.setSearchEngines(searchGUIFile.getFileOverview().split("DB:")[0].trim());
                dataset.setFastaFileName(searchGUIFile.getFileOverview().split("DB:")[1].split("sequences:")[0].trim());

                filesToViewList.remove(searchGUIFile);

                for (String mgfId : searchGUIFile.getGalaxyJob().getInputFileIds()) {
                    if (appManagmentBean.getGalaxyFacad().trackBackDatasetTool(mgfId, loggedinUserAPIKey).contains("thermo_raw_file_converter")) {
                        dataset.setDatasetType(CONSTANT.QUANT_DATASET);
                        break;
                    }
                }
                for (GalaxyFileModel stepIIFile : filesMap.values()) {
                    if (stepIIFile.getGalaxyJob().getInputFileIds().contains(searchGUIFile.getId())) {
                        switch (stepIIFile.getExtension()) {
                            case CONSTANT.ZIP_FILE_EXTENSION:
                                dataset.setPsZipFile(stepIIFile);
                                filesToViewList.remove(stepIIFile);
                                dataset.setDownloadUrl(stepIIFile.getDownloadUrl());
                                dataset.setName(stepIIFile.getName());
                                //init search param identification object
                                dataset.setIdentificationParametersObject(datasetUtils.initIdentificationParametersObject(dataset.getPsZipFile().getId(), dataset.getSearchGUIZipFile().getDownloadUrl()));
                                //add mgf files and indexes                                
                                for (GalaxyCollectionModel collectionModel : collectionList) {
                                    if (collectionModel.getGalaxyJob() != null && collectionModel.getGalaxyJob().getInputFileIds().contains(searchGUIFile.getId())) {
                                        if (collectionModel.getElementsExtension().equals(CONSTANT.CUI_FILE_EXTENSION)) {
                                            dataset.setMgfIndexList(collectionModel);
                                            collectionModel.getElements().forEach((element) -> {
                                                filesToViewList.remove(element);
                                            });
                                        }
                                    } else if (collectionModel.getElementsExtension().equals(CONSTANT.TABULAR_FILE_EXTENSION)) {
                                        collectionModel.getElements().get(0).getGalaxyJob().getInputFileIds().stream().filter((id) -> (dataset.getPsZipFile().getGalaxyJob().getOutputFileIds().contains(id))).map(new Function<String, String>() {
                                            @Override
                                            public String apply(String _item) {
                                                if (collectionModel.getElements().get(0).getGalaxyJob().getToolId().equals(CONSTANT.CONVERT_CHARACTERS_TOOL_ID)) {
                                                    dataset.setMgfList(collectionModel);
                                                } else if (collectionModel.getElements().get(0).getGalaxyJob().getToolId().contains(CONSTANT.MOFF_TOOL_ID)) {
                                                    dataset.setMoffList(collectionModel);
                                                    
                                                }
                                                return _item;
                                            }
                                        }).forEachOrdered((_item) -> {
                                            collectionModel.getElements().forEach((element) -> {
                                                filesToViewList.remove(element);
                                            });
                                        });
                                    }
                                }
                                break;
//
//                            case CONSTANT.TABULAR_FILE_EXTENSION:
//                                if (stepIIFile.getName().equals(CONSTANT.PSM_REPORT_FILE_NAME) && dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
//                                    //add quant data files
//                                    collectionList.stream().filter((collectionModel) -> (collectionModel.getElementsExtension().equals(CONSTANT.TABULAR_FILE_EXTENSION) && collectionModel.getElements().get(0).getGalaxyJob().getInputFileIds().contains(stepIIFile.getId()))).map((GalaxyCollectionModel collectionModel) -> {
//                                        dataset.setMoffList(collectionModel);
//                                        return collectionModel;
//                                    }).forEachOrdered((GalaxyCollectionModel collectionModel) -> {
//                                        collectionModel.getElements().forEach((o) -> filesToViewList.remove(o));
//                                    });
//                                }
//                                break;
                        }

                    }
                }

                return dataset;
            }
        }).
                forEachOrdered((dataset) -> {
                    datasetsMap.put(dataset.getId(), dataset);
                });
        this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, datasetsMap.size() + "");
        return datasetsMap;
    }

    public Map<String, String> getUserInformation() {
        return userInformationMap;
    }

    public void getUserStoredData() {

    }

    public List<GalaxyFileModel> getFilesToViewList() {
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
            this.datasetMap.remove(dataset.getId());
            this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, datasetMap.size() + "");
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

}
