package com.uib.web.peptideshaker.model;

import com.compomics.util.experiment.biology.proteins.Protein;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PSMObject;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    //case of uploaded  dataset
    private GalaxyFileModel fastaFileModel;
    private GalaxyFileModel proteinFileModel;
    private GalaxyFileModel peptideFileModel;

    private Map<String, Set<Integer>> proteinInferenceMap;
    private Map<String, Set<Integer>> proteinValidationMap;
    private Map<String, Set<Integer>> chromosomeMap;
    private TreeMap<Integer, Set<Integer>> validatedPetideMap;
    private TreeMap<Integer, Set<Integer>> validatedPsmsMap;
    private TreeMap<Integer, Set<Integer>> validatedCoverageMap;
    private Map<String, Set<Integer>> accessionToGroupMap;
    private Map<Integer, ProteinGroupObject> proteinsMap;
    private TreeMap<Integer, Set<Integer>> uniquePeptideIntensityMap;
    private TreeMap<Integer, Set<Integer>> allPeptideIntensityMap;
    private Map<Integer, PeptideObject> peptidesMap;
    private Map<String, PSMObject> psmsMap;

    public Map<String, PSMObject> getPsmsMap() {
        return psmsMap;
    }

    public void setPsmsMap(Map<String, PSMObject> psmsMap) {
        this.psmsMap = psmsMap;
    }

    public Map<Integer, PeptideObject> getPeptidesMap() {
        return peptidesMap;
    }

    public void setPeptidesMap(Map<Integer, PeptideObject> peptidesMap) {
        this.peptidesMap = peptidesMap;
    }

    public TreeMap<Integer, Set<Integer>> getUniquePeptideIntensityMap() {
        return uniquePeptideIntensityMap;
    }

    public void setUniquePeptideIntensityMap(TreeMap<Integer, Set<Integer>> uniquePeptideIntensityMap) {
        this.uniquePeptideIntensityMap = uniquePeptideIntensityMap;
    }

    public TreeMap<Integer, Set<Integer>> getAllPeptideIntensityMap() {
        return allPeptideIntensityMap;
    }

    public void setAllPeptideIntensityMap(TreeMap<Integer, Set<Integer>> allPeptideIntensityMap) {
        this.allPeptideIntensityMap = allPeptideIntensityMap;
    }
    private String datasetSource;
    private String status;
    private GalaxyFileModel psZipFile;
    private GalaxyFileModel searchGUIZipFile;
    private GalaxyCollectionModel mgfList;
    private GalaxyCollectionModel mgfIndexList;
    private GalaxyCollectionModel moffList;
    private String downloadUrl;
    private Date createdTime;
    private String fastaFileName;
    private double maxMS2Quant;
    private double maxMW;
    private Map<String, Set<Integer>> modificationMap;
    private ModificationMatrixModel modificationMatrixModel;

    public ModificationMatrixModel getModificationMatrixModel() {
        return modificationMatrixModel;
    }

    public void setModificationMatrixModel(ModificationMatrixModel modificationMatrixModel) {
        this.modificationMatrixModel = modificationMatrixModel;
    }

    public Map<Integer, ProteinGroupObject> getProteinsMap() {
        return proteinsMap;
    }

    public void setProteinsMap(Map<Integer, ProteinGroupObject> proteinsMap) {
        this.proteinsMap = proteinsMap;
    }

    public String getDatasetTypeAsString() {
        return datasetTypeAsString;
    }

    public void setDatasetTypeAsString(String datasetTypeAsString) {
        this.datasetTypeAsString = datasetTypeAsString;
    }

    public GalaxyFileModel getFastaFileModel() {
        return fastaFileModel;
    }

    public void setFastaFileModel(GalaxyFileModel fastaFileModel) {
        this.fastaFileModel = fastaFileModel;
    }

    public GalaxyFileModel getProteinFileModel() {
        return proteinFileModel;
    }

    public void setProteinFileModel(GalaxyFileModel proteinFileModel) {
        this.proteinFileModel = proteinFileModel;
    }

    public GalaxyFileModel getPeptideFileModel() {
        return peptideFileModel;
    }

    public void setPeptideFileModel(GalaxyFileModel peptideFileModel) {
        this.peptideFileModel = peptideFileModel;
    }

    public String getFastaFileName() {
        if (datasetSource.equals(CONSTANT.USER_UPLOAD_SOURCE)) {
            return fastaFileModel.getName();
        }
        return fastaFileName;
    }

    public void setFastaFileName(String fastaFileName) {
        this.fastaFileName = fastaFileName;
    }
    //
    private IdentificationParameters identificationParametersObject;

    public String getSharingLink() {
        return sharingLink;
    }

    public String getId() {
        return id;
    }

    public void setIdentificationParametersObject(IdentificationParameters identificationParametersObject) {
        this.identificationParametersObject = identificationParametersObject;
    }

    public void setSharingLink(String sharingLink) {
        this.sharingLink = sharingLink;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getProteinFileName() {
        return proteinFileModel.getName();
    }

    public String getPeptideFileName() {
        return peptideFileModel.getName();
    }

    public IdentificationParameters getIdentificationParametersObject() {
        return identificationParametersObject;
    }

    public String getDatasetSource() {
        return datasetSource;
    }

    public void setDatasetSource(String datasetSource) {
        this.datasetSource = datasetSource;
    }

    public Map<String, Set<Integer>> getProteinInferenceMap() {
        return proteinInferenceMap;
    }

    public void setProteinInferenceMap(Map<String, Set<Integer>> proteinInferenceMap) {
        this.proteinInferenceMap = proteinInferenceMap;
    }

    public Map<String, Set<Integer>> getProteinValidationMap() {
        return proteinValidationMap;
    }

    public void setProteinValidationMap(Map<String, Set<Integer>> proteinValidationMap) {
        this.proteinValidationMap = proteinValidationMap;
    }

    public Map<String, Set<Integer>> getChromosomeMap() {
        return chromosomeMap;
    }

    public void setChromosomeMap(Map<String, Set<Integer>> chromosomeMap) {
        this.chromosomeMap = chromosomeMap;
    }

    public TreeMap<Integer, Set<Integer>> getValidatedPetideMap() {
        return validatedPetideMap;
    }

    public void setValidatedPetideMap(TreeMap<Integer, Set<Integer>> validatedPetideMap) {
        this.validatedPetideMap = validatedPetideMap;
    }

    public TreeMap<Integer, Set<Integer>> getValidatedPsmsMap() {
        return validatedPsmsMap;
    }

    public void setValidatedPsmsMap(TreeMap<Integer, Set<Integer>> validatedPsmsMap) {
        this.validatedPsmsMap = validatedPsmsMap;
    }

    public TreeMap<Integer, Set<Integer>> getValidatedCoverageMap() {
        return validatedCoverageMap;
    }

    public void setValidatedCoverageMap(TreeMap<Integer, Set<Integer>> validatedCoverageMap) {
        this.validatedCoverageMap = validatedCoverageMap;
    }

    public Map<String, Set<Integer>> getAccessionToGroupMap() {
        return accessionToGroupMap;
    }

    public void setAccessionToGroupMap(Map<String, Set<Integer>> accessionToGroupMap) {
        this.accessionToGroupMap = accessionToGroupMap;
    }

    public double getMaxMS2Quant() {
        return maxMS2Quant;
    }

    public void setMaxMS2Quant(double maxMS2Quant) {
        this.maxMS2Quant = maxMS2Quant;
    }

    public double getMaxMW() {
        return maxMW;
    }

    public void setMaxMW(double maxMW) {
        this.maxMW = maxMW;
    }

    public Map<String, Set<Integer>> getModificationMap() {
        return modificationMap;
    }

    public void setModificationMap(Map<String, Set<Integer>> modificationMap) {
        this.modificationMap = modificationMap;
    }

}
