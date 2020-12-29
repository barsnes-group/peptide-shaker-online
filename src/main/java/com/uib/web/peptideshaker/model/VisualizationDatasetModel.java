package com.uib.web.peptideshaker.model;

import com.compomics.util.parameters.identification.IdentificationParameters;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This class contains all files and data needed to visualise the data
 *
 * @author Yehia Mokhtar Farag
 */
public class VisualizationDatasetModel implements Comparable<VisualizationDatasetModel>, Serializable {

    @Override
    public int compareTo(VisualizationDatasetModel o) {
        return o.createdTime.compareTo(this.createdTime);
    }

    private String datasetType;
    private String name;
    private String id;
    private String datasetTypeAsString;
    private String sharingLink;

    public String getSharingLink() {
        return sharingLink;
    }

    public String getId() {
        return id;
    }
    private GalaxyFileModel psZipFile;
    private GalaxyFileModel searchGUIZipFile;
    private GalaxyCollectionModel mgfList;
    private GalaxyCollectionModel mgfIndexList;
    private GalaxyCollectionModel moffList;
    private String downloadUrl;
    private Date createdTime;
    //
    private IdentificationParameters identificationParametersObject;

    public void setIdentificationParametersObject(IdentificationParameters identificationParametersObject) {
        this.identificationParametersObject = identificationParametersObject;
    }

    public void setSharingLink(String sharingLink) {
        this.sharingLink = sharingLink;
    }
    //case of uploaded  dataset
    private String fastaFileName;
    private String proteinFileName;
    private String peptideFileName;
    private boolean uploadedDataset;
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isUploadedDataset() {
        return uploadedDataset;
    }
    private String searchEngines;

    /**
     * @todo:replace with par file.
     */
    public String getSearchEngines() {
        return searchEngines;
    }

    public void setSearchEngines(String searchEngines) {
        this.searchEngines = searchEngines;
    }

    public String getFastaFileName() {
        return fastaFileName;
    }

    public void setFastaFileName(String fastaFileName) {
        this.fastaFileName = fastaFileName;
    }

    public String getProteinFileName() {
        return proteinFileName;
    }

    public void setProteinFileName(String proteinFileName) {
        this.proteinFileName = proteinFileName;
    }

    public String getPeptideFileName() {
        return peptideFileName;
    }

    public void setPeptideFileName(String peptideFileName) {
        this.peptideFileName = peptideFileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDatasetType() {
        return datasetType;
    }

    public String getDatasetTypeString() {
        return datasetTypeAsString;
    }

    public GalaxyFileModel getPsZipFile() {
        return psZipFile;
    }

    public GalaxyFileModel getSearchGUIZipFile() {
        return searchGUIZipFile;
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
        if (datasetType.equals(CONSTANT.QUANT_DATASET)) {
            datasetTypeAsString = "Quant Dataset";
        } else {
            datasetTypeAsString = "Identification Dataset";
        }
    }

    public void setPsZipFile(GalaxyFileModel psZipFile) {
        this.psZipFile = psZipFile;
        this.setId(psZipFile.getId());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSearchGUIZipFile(GalaxyFileModel searchGUIZipFile) {
        this.searchGUIZipFile = searchGUIZipFile;
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

    public String getStatus() {
        if (status != null) {
            System.out.println("error 0");
            return status;
        }
        if (searchGUIZipFile == null) {
             System.out.println("error i");
            return CONSTANT.ERROR_STATUS;
        }
        if (psZipFile == null) {
             System.out.println("error i2");
            return CONSTANT.ERROR_STATUS;
        }
        if (mgfIndexList == null) {
             System.out.println("error i3");
            return CONSTANT.ERROR_STATUS;
        }
        if (mgfList == null) {
             System.out.println("error i4");
            return CONSTANT.ERROR_STATUS;
        }
        if (datasetType.equals(CONSTANT.QUANT_DATASET) && moffList == null) {
             System.out.println("error i6");
            return CONSTANT.ERROR_STATUS;
        }

        if (!searchGUIZipFile.getStatus().equals(CONSTANT.OK_STATUS)) {
             System.out.println("error 7");
            return searchGUIZipFile.getStatus();
        }
        if (!psZipFile.getStatus().equals(CONSTANT.OK_STATUS)) {
             System.out.println("error 8");
            return psZipFile.getStatus();
        }
        if (mgfIndexList.getElements().isEmpty()) {
             System.out.println("error 9");
            return CONSTANT.ERROR_STATUS;
        }
        if (mgfList.getElements().isEmpty()) {
             System.out.println("error i0");
            return CONSTANT.ERROR_STATUS;
        }
        if (datasetType.equals(CONSTANT.QUANT_DATASET) && moffList.getElements().isEmpty()) {
             System.out.println("error i1");
            return CONSTANT.ERROR_STATUS;
        }
        if (datasetType.equals(CONSTANT.QUANT_DATASET) && !moffList.getElements().get(0).getStatus().equals(CONSTANT.OK_STATUS)) {
             System.out.println("error i2");
            return moffList.getElements().get(0).getStatus();
        }
        return CONSTANT.OK_STATUS;//statues;
    }

    public Date getCreatedTime() {
        if (createdTime == null) {
            return psZipFile.getCreatedDate();
        }
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public List<String> getVariableModification() {
        return identificationParametersObject.getSearchParameters().getModificationParameters().getVariableModifications();
    }

    public List<String> getFixedModification() {
        return identificationParametersObject.getSearchParameters().getModificationParameters().getFixedModifications();
    }

    public void setUploadedDataset(boolean uploadedDataset) {
        this.uploadedDataset = uploadedDataset;
    }

}
