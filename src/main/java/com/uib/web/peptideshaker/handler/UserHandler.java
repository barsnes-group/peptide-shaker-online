package com.uib.web.peptideshaker.handler;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyCollectionModel;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.vaadin.server.VaadinSession;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * this class responsible for handling user login security
 *
 * @author Yehia Farag
 */
public class UserHandler implements Serializable {

    private String loggedinUserAPIKey;
    private final AppManagmentBean appManagmentBean;
    private Map<String, String> userInformationMap;
    private List<GalaxyCollectionModel> collectionList;
    private Map<String, GalaxyFileModel> filesMap;
    private Map<String, VisualizationDatasetModel> datasetMap;

    public UserHandler() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
    }

    public void setUserLoggedIn(String userAPIKey, String userId) {
        this.loggedinUserAPIKey = userAPIKey;
        this.userInformationMap = appManagmentBean.getGalaxyFacad().getUserInformation(loggedinUserAPIKey, userId);
        //initial user history
        appManagmentBean.getGalaxyFacad().initialPeptideShakerUserHistory(userAPIKey);
        Object[] userData = appManagmentBean.getGalaxyFacad().getUserData(loggedinUserAPIKey);
        this.collectionList = (List<GalaxyCollectionModel>) userData[0];
        this.filesMap = (Map<String, GalaxyFileModel>) userData[1];
        this.userInformationMap.put(CONSTANT.FILES_NUMBER, filesMap.size() + "");
        this.datasetMap = constructDatasets();
        this.userInformationMap.put(CONSTANT.PS_DATASET_NUMBER, datasetMap.size() + "");

    }

    public Map<String, VisualizationDatasetModel> constructDatasets() {
        Map<String, VisualizationDatasetModel> datasetsMap = new LinkedHashMap<>();
        filesMap.values().stream().filter((stepIFile) -> (stepIFile.getExtension().equals(CONSTANT.SEARCH_GUI_FILE_EXTENSION))).map((searchGUIFile) -> {
            VisualizationDatasetModel dataset = new VisualizationDatasetModel();
            dataset.setDatasetType(CONSTANT.ID_DATASET);
            dataset.setSearcGUIZipFile(searchGUIFile);
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
                            dataset.setName(stepIIFile.getName());
                            //add mgf files and indexes
                            for (GalaxyCollectionModel collectionModel : collectionList) {
                                if (collectionModel.getGalaxyJob() != null && collectionModel.getGalaxyJob().getInputFileIds().contains(searchGUIFile.getId())) {
                                    if (collectionModel.getElementsExtension().equals(CONSTANT.CUI_FILE_EXTENSION)) {
                                        dataset.setMgfIndexList(collectionModel);
                                    } else if (collectionModel.getElementsExtension().equals(CONSTANT.MGF_FILE_EXTENSION)) {
                                        //find indexed mgf list
                                        for (GalaxyCollectionModel stepIICollectionModel : collectionList) {
                                            if (stepIICollectionModel.getElementsExtension().equalsIgnoreCase(CONSTANT.TABULAR_FILE_EXTENSION) && stepIICollectionModel.getElements().get(0).getGalaxyJob().getInputFileIds().contains(collectionModel.getElements().get(0).getId())) {
                                                dataset.setMgfList(stepIICollectionModel);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case CONSTANT.TABULAR_FILE_EXTENSION:
                            if (stepIIFile.getName().equals(CONSTANT.PSM_REPORT_FILE_NAME) && dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                               //add quant data files 
                                 for (GalaxyCollectionModel collectionModel : collectionList) {
                                  if (collectionModel.getElementsExtension().equals(CONSTANT.TABULAR_FILE_EXTENSION) && collectionModel.getElements().get(0).getGalaxyJob().getInputFileIds().contains(stepIIFile.getId())) {
                                      dataset.setMoffList(collectionModel);
                                  }
                                 
                                 }
                            }
                            break;
                    }

                }
            }

            return dataset;
        }).forEachOrdered((dataset) -> {
            datasetsMap.put(dataset.getName(), dataset);
        });
        
        return datasetsMap;
    }

    public Map<String, String> getUserInformation() {
        return userInformationMap;
    }

    public void getUserStoredData() {

    }

    public void signout() {
        signinAsPublicUser();
    }

    public void signinAsPublicUser() {

    }

    public String getLoggedinUserAPIKey() {
        return loggedinUserAPIKey;
    }

}
