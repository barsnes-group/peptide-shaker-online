package com.uib.web.peptideshaker.model;

/**
 * This class contains all files and data needed to visualise the data
 *
 * @author Yehia Mokhtar Farag
 */
public class VisualizationDatasetModel {
    
    private String datasetType;
    private String name;
    private GalaxyFileModel psZipFile;
    private GalaxyFileModel searcGUIZipFile;
    private GalaxyCollectionModel mgfList;
    private GalaxyCollectionModel mgfIndexList;
    private GalaxyCollectionModel moffList;
    private String statues;
    
    public String getDatasetType() {
        return datasetType;
    }
    
    public GalaxyFileModel getPsZipFile() {
        return psZipFile;
    }
    
    public GalaxyFileModel getSearcGUIZipFile() {
        return searcGUIZipFile;
    }
    
    public GalaxyCollectionModel getMgfList() {
        return mgfList;
    }
    
    public GalaxyCollectionModel getMgfIndexList() {
        return mgfIndexList;
    }
    
    public GalaxyCollectionModel getMoffList() {
        return moffList;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }
    
    public void setPsZipFile(GalaxyFileModel psZipFile) {
        this.psZipFile = psZipFile;
    }
    
    public void setSearcGUIZipFile(GalaxyFileModel searcGUIZipFile) {
        this.searcGUIZipFile = searcGUIZipFile;
    }
    
    public void setMgfList(GalaxyCollectionModel mgfList) {
        this.mgfList = mgfList;
    }
    
    public void setMgfIndexList(GalaxyCollectionModel mgfIndexList) {
        this.mgfIndexList = mgfIndexList;
    }
    
    public void setMoffList(GalaxyCollectionModel moffList) {
        this.moffList = moffList;
    }

    public void validateDataset() {
        /**
         * validate type
         */
        if (name.endsWith("(Q)")) {
            this.setDatasetType("quant");
        }
        /**
         * validate files
         */
        if(this.datasetType.equals("quant")){
        
        }else{
        
        }
    }
    
}
