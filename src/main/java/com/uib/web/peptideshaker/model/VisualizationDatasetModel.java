package com.uib.web.peptideshaker.model;

import com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfIndex;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import graphmatcher.NetworkGraphNode;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.biojava.nbio.core.sequence.ProteinSequence;

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
    private Map<Comparable, Set<Integer>> proteinInferenceMap;
    private Map<Comparable, Set<Integer>> proteinValidationMap;
    private Map<Comparable, Set<Integer>> chromosomeMap;
    private TreeMap<Comparable, Set<Integer>> validatedPetideMap;
    private TreeMap<Comparable, Set<Integer>> validatedPsmsMap;
    private TreeMap<Comparable, Set<Integer>> validatedCoverageMap;
    private Map<String, Set<Integer>> accessionToGroupMap;
    private Map<Integer, ProteinGroupObject> proteinsMap;
    private TreeMap<Comparable, Set<Integer>> uniquePeptideIntensityMap;
    private TreeMap<Comparable, Set<Integer>> allPeptideIntensityMap;
    private Map<Integer, PeptideObject> peptidesMap;
    private Map<String, PSMObject> psmsMap;
    private final Map<String, FilterUpdatingEvent> datasetFilterEventsMap;
    private final Map<String, Object[]> proteinsGraphDataMap;
    private RangeColorGenerator peptideIntinsityColorScale;
    private Map<String, Set<String>> peptidesPsmMap;
    private LinkedHashMap<String, LinkedHashMap<String, NetworkGraphNode>> proteoformsMap;
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
    private Map<Comparable, Set<Integer>> modificationMap;
    private Map<Comparable, Set<Integer>> proteinTableMap;
    private LinkedHashMap<String, ProteinSequence> proteinSequenceMap;
    /**
     * MGF index file map (.cui) files.
     */
    private final Map<String, MgfIndex> importedMgfIndexObjectMap;

    /**
     *
     * @return
     */
    public Map<String, LinkedHashMap<String, NetworkGraphNode>> getProteoformsMap() {
        return proteoformsMap;
    }

    /**
     *
     * @param proteoformsMap
     */
    public void setProteoformsMap(LinkedHashMap<String, LinkedHashMap<String, NetworkGraphNode>> proteoformsMap) {
        this.proteoformsMap = proteoformsMap;
    }

    /**
     *
     * @return
     */
    public Map<String, FilterUpdatingEvent> getDatasetFilterEventsMap() {
        return datasetFilterEventsMap;
    }

    /**
     *
     */
    public VisualizationDatasetModel() {
        this.datasetFilterEventsMap = new LinkedHashMap<>();
        this.proteinsGraphDataMap = new HashMap<>();
        this.importedMgfIndexObjectMap = new HashMap<>();

    }

    /**
     *
     * @param proteinAccession
     * @param graphData
     */
    public void addProteinGraphData(String proteinAccession, Object[] graphData) {
        proteinsGraphDataMap.put(proteinAccession, graphData);
    }

    /**
     *
     * @return
     */
    public Map<String, Object[]> getProteinsGraphDataMap() {
        return proteinsGraphDataMap;
    }

    /**
     *
     * @return
     */
    public Map<String, PSMObject> getPsmsMap() {
        return psmsMap;
    }

    /**
     *
     * @param psmsMap
     */
    public void setPsmsMap(Map<String, PSMObject> psmsMap) {
        this.psmsMap = psmsMap;
    }

    /**
     *
     * @return
     */
    public Map<Integer, PeptideObject> getPeptidesMap() {
        return peptidesMap;
    }

    /**
     *
     * @param peptidesMap
     */
    public void setPeptidesMap(Map<Integer, PeptideObject> peptidesMap) {
        this.peptidesMap = peptidesMap;
    }

    /**
     *
     * @return
     */
    public TreeMap<Comparable, Set<Integer>> getUniquePeptideIntensityMap() {
        return uniquePeptideIntensityMap;
    }

    /**
     *
     * @param uniquePeptideIntensityMap
     */
    public void setUniquePeptideIntensityMap(TreeMap<Comparable, Set<Integer>> uniquePeptideIntensityMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(uniquePeptideIntensityMap);
        this.datasetFilterEventsMap.put(CONSTANT.INTENSITY_UNIQUE_FILTER_ID, event);
        this.uniquePeptideIntensityMap = uniquePeptideIntensityMap;
    }

    /**
     *
     * @return
     */
    public TreeMap<Comparable, Set<Integer>> getAllPeptideIntensityMap() {
        return allPeptideIntensityMap;
    }

    /**
     *
     * @param allPeptideIntensityMap
     */
    public void setAllPeptideIntensityMap(TreeMap<Comparable, Set<Integer>> allPeptideIntensityMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(allPeptideIntensityMap);
        this.datasetFilterEventsMap.put(CONSTANT.INTENSITY_FILTER_ID, event);
        this.allPeptideIntensityMap = allPeptideIntensityMap;
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, ProteinSequence> getProteinSequenceMap() {
        return proteinSequenceMap;
    }

    /**
     *
     * @param proteinSequenceMap
     */
    public void setProteinSequenceMap(LinkedHashMap<String, ProteinSequence> proteinSequenceMap) {
        this.proteinSequenceMap = proteinSequenceMap;
    }

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getProteinTableMap() {
        return proteinTableMap;
    }

    /**
     *
     * @param proteinTableMap
     */
    public void setProteinTableMap(Map<Comparable, Set<Integer>> proteinTableMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinTableMap);
        this.datasetFilterEventsMap.put(CONSTANT.PROTEIN_TABLE_FILTER_ID, event);
        this.proteinTableMap = proteinTableMap;
    }

    /**
     *
     * @return
     */
    public Map<Integer, ProteinGroupObject> getProteinsMap() {
        return proteinsMap;
    }

    /**
     *
     * @param proteinsMap
     */
    public void setProteinsMap(Map<Integer, ProteinGroupObject> proteinsMap) {
        this.proteinsMap = proteinsMap;
    }

    /**
     *
     * @return
     */
    public String getDatasetTypeAsString() {
        return datasetTypeAsString;
    }

    /**
     *
     * @param datasetTypeAsString
     */
    public void setDatasetTypeAsString(String datasetTypeAsString) {
        this.datasetTypeAsString = datasetTypeAsString;
    }

    /**
     *
     * @return
     */
    public GalaxyFileModel getFastaFileModel() {
        return fastaFileModel;
    }

    /**
     *
     * @param fastaFileModel
     */
    public void setFastaFileModel(GalaxyFileModel fastaFileModel) {
        this.fastaFileModel = fastaFileModel;
    }

    /**
     *
     * @return
     */
    public GalaxyFileModel getProteinFileModel() {
        return proteinFileModel;
    }

    /**
     *
     * @param proteinFileModel
     */
    public void setProteinFileModel(GalaxyFileModel proteinFileModel) {
        this.proteinFileModel = proteinFileModel;
    }

    /**
     *
     * @return
     */
    public GalaxyFileModel getPeptideFileModel() {
        return peptideFileModel;
    }

    /**
     *
     * @param peptideFileModel
     */
    public void setPeptideFileModel(GalaxyFileModel peptideFileModel) {
        this.peptideFileModel = peptideFileModel;
    }

    /**
     *
     * @return
     */
    public String getFastaFileName() {
        if (datasetSource.equals(CONSTANT.USER_UPLOAD_SOURCE)) {
            return fastaFileModel.getName();
        }
        return fastaFileName;
    }

    /**
     *
     * @param fastaFileName
     */
    public void setFastaFileName(String fastaFileName) {
        this.fastaFileName = fastaFileName;
    }
    //
    private IdentificationParameters identificationParametersObject;

    /**
     *
     * @return
     */
    public String getSharingLink() {
        return sharingLink;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param identificationParametersObject
     */
    public void setIdentificationParametersObject(IdentificationParameters identificationParametersObject) {
        this.identificationParametersObject = identificationParametersObject;
    }

    /**
     *
     * @param sharingLink
     */
    public void setSharingLink(String sharingLink) {
        this.sharingLink = sharingLink;
    }

    /**
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    private String searchEngines;

    /**
     * @return @todo:replace with par file.
     */
    public String getSearchEngines() {
        return searchEngines;
    }

    /**
     *
     * @param searchEngines
     */
    public void setSearchEngines(String searchEngines) {
        this.searchEngines = searchEngines;
    }

    /**
     *
     * @return
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     *
     * @param downloadUrl
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     *
     * @return
     */
    public String getDatasetType() {
        return datasetType;
    }

    /**
     *
     * @return
     */
    public String getDatasetTypeString() {
        return datasetTypeAsString;
    }

    /**
     *
     * @return
     */
    public GalaxyFileModel getPsZipFile() {
        return psZipFile;
    }

    /**
     *
     * @return
     */
    public GalaxyFileModel getSearchGUIZipFile() {
        return searchGUIZipFile;
    }

    /**
     *
     * @return
     */
    public GalaxyCollectionModel getMgfList() {
        return mgfList;
    }

    /**
     *
     * @return
     */
    public GalaxyCollectionModel getMgfIndexList() {
        return mgfIndexList;
    }

    /**
     *
     * @return
     */
    public GalaxyCollectionModel getMoffList() {
        return moffList;
    }

    /**
     *
     * @return
     */
    public String getName() {

        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param datasetType
     */
    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
        if (datasetType.equals(CONSTANT.QUANT_DATASET)) {
            datasetTypeAsString = "Quant Dataset";
        } else {
            datasetTypeAsString = "Identification Dataset";
        }
    }

    /**
     *
     * @param psZipFile
     */
    public void setPsZipFile(GalaxyFileModel psZipFile) {
        this.psZipFile = psZipFile;
        this.setId(psZipFile.getId());
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @param searchGUIZipFile
     */
    public void setSearchGUIZipFile(GalaxyFileModel searchGUIZipFile) {
        this.searchGUIZipFile = searchGUIZipFile;
    }

    /**
     *
     * @param mgfList
     */
    public void setMgfList(GalaxyCollectionModel mgfList) {
        this.mgfList = mgfList;
    }

    /**
     *
     * @param mgfIndexList
     */
    public void setMgfIndexList(GalaxyCollectionModel mgfIndexList) {
        this.mgfIndexList = mgfIndexList;
    }

    /**
     *
     * @param moffList
     */
    public void setMoffList(GalaxyCollectionModel moffList) {
        this.moffList = moffList;
    }

    /**
     *
     * @return
     */
    public String getStatus() {
        if (status != null) {
            return status;
        }
        if (searchGUIZipFile == null) {
            return CONSTANT.ERROR_STATUS;
        }
        if (psZipFile == null) {
            return CONSTANT.ERROR_STATUS;
        }
        if (mgfIndexList == null) {
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

    /**
     *
     * @return
     */
    public Date getCreatedTime() {
        if (createdTime == null) {
            return psZipFile.getCreatedDate();
        }
        return createdTime;
    }

    /**
     *
     * @param createdTime
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     *
     * @return
     */
    public List<String> getVariableModification() {
        return identificationParametersObject.getSearchParameters().getModificationParameters().getVariableModifications();
    }

    /**
     *
     * @return
     */
    public List<String> getFixedModification() {
        return identificationParametersObject.getSearchParameters().getModificationParameters().getFixedModifications();
    }

    /**
     *
     * @return
     */
    public String getProteinFileName() {
        return proteinFileModel.getName();
    }

    /**
     *
     * @return
     */
    public String getPeptideFileName() {
        return peptideFileModel.getName();
    }

    /**
     *
     * @return
     */
    public IdentificationParameters getIdentificationParametersObject() {
        return identificationParametersObject;
    }

    /**
     *
     * @return
     */
    public String getDatasetSource() {
        return datasetSource;
    }

    /**
     *
     * @param datasetSource
     */
    public void setDatasetSource(String datasetSource) {
        this.datasetSource = datasetSource;
    }

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getProteinInferenceMap() {
        return proteinInferenceMap;
    }

    /**
     *
     * @param proteinInferenceMap
     */
    public void setProteinInferenceMap(Map<Comparable, Set<Integer>> proteinInferenceMap) {
        this.proteinInferenceMap = proteinInferenceMap;
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinInferenceMap);
        this.datasetFilterEventsMap.put(CONSTANT.PI_FILTER_ID, event);
    }

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getProteinValidationMap() {
        return proteinValidationMap;
    }

    /**
     *
     * @param proteinValidationMap
     */
    public void setProteinValidationMap(Map<Comparable, Set<Integer>> proteinValidationMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinValidationMap);
        this.datasetFilterEventsMap.put(CONSTANT.VALIDATION_FILTER_ID, event);
        this.proteinValidationMap = proteinValidationMap;
    }

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getChromosomeMap() {
        return chromosomeMap;
    }

    /**
     *
     * @param chromosomeMap
     */
    public void setChromosomeMap(Map<Comparable, Set<Integer>> chromosomeMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(chromosomeMap);
        this.datasetFilterEventsMap.put(CONSTANT.CHROMOSOME_FILTER_ID, event);
        this.chromosomeMap = chromosomeMap;
    }

    /**
     *
     * @return
     */
    public TreeMap<Comparable, Set<Integer>> getValidatedPetideMap() {
        return validatedPetideMap;
    }

    /**
     *
     * @param validatedPetideMap
     */
    public void setValidatedPetideMap(TreeMap<Comparable, Set<Integer>> validatedPetideMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedPetideMap);
        this.datasetFilterEventsMap.put(CONSTANT.PEPTIDES_NUMBER_FILTER_ID, event);
        this.validatedPetideMap = validatedPetideMap;
    }

    /**
     *
     * @return
     */
    public TreeMap<Comparable, Set<Integer>> getValidatedPsmsMap() {
        return validatedPsmsMap;
    }

    /**
     *
     * @param validatedPsmsMap
     */
    public void setValidatedPsmsMap(TreeMap<Comparable, Set<Integer>> validatedPsmsMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedPsmsMap);
        this.datasetFilterEventsMap.put(CONSTANT.PSM_NUMBER_FILTER_ID, event);
        this.validatedPsmsMap = validatedPsmsMap;
    }

    /**
     *
     * @return
     */
    public TreeMap<Comparable, Set<Integer>> getValidatedCoverageMap() {
        return validatedCoverageMap;
    }

    /**
     *
     * @param validatedCoverageMap
     */
    public void setValidatedCoverageMap(TreeMap<Comparable, Set<Integer>> validatedCoverageMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedCoverageMap);
        this.datasetFilterEventsMap.put(CONSTANT.COVERAGE_FILTER_ID, event);
        this.validatedCoverageMap = validatedCoverageMap;
    }

    /**
     *
     * @return
     */
    public Map<String, Set<Integer>> getAccessionToGroupMap() {
        return accessionToGroupMap;
    }

    /**
     *
     * @param accessionToGroupMap
     */
    public void setAccessionToGroupMap(Map<String, Set<Integer>> accessionToGroupMap) {
        this.accessionToGroupMap = accessionToGroupMap;
    }

    /**
     *
     * @return
     */
    public double getMaxMS2Quant() {
        return maxMS2Quant;
    }

    /**
     *
     * @param maxMS2Quant
     */
    public void setMaxMS2Quant(double maxMS2Quant) {
        this.maxMS2Quant = maxMS2Quant;
    }

    /**
     *
     * @return
     */
    public double getMaxMW() {
        return maxMW;
    }

    /**
     *
     * @param maxMW
     */
    public void setMaxMW(double maxMW) {
        this.maxMW = maxMW;
    }

    /**
     *
     * @return
     */
    public Map<Comparable, Set<Integer>> getModificationMap() {
        return modificationMap;
    }

    /**
     *
     * @param modificationMap
     */
    public void setModificationMap(Map<Comparable, Set<Integer>> modificationMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(modificationMap);
        this.datasetFilterEventsMap.put(CONSTANT.MODIFICATIONS_FILTER_ID, event);
        this.modificationMap = modificationMap;
    }

    /**
     *
     * @return
     */
    public RangeColorGenerator getPeptideIntinsityColorScale() {
        return peptideIntinsityColorScale;
    }

    /**
     *
     * @param peptideIntinsityColorScale
     */
    public void setPeptideIntinsityColorScale(RangeColorGenerator peptideIntinsityColorScale) {
        this.peptideIntinsityColorScale = peptideIntinsityColorScale;
    }

    /**
     *
     * @return
     */
    public Map<String, Set<String>> getPeptidesPsmMap() {
        return peptidesPsmMap;
    }

    /**
     *
     * @param peptidesPsmMap
     */
    public void setPeptidesPsmMap(Map<String, Set<String>> peptidesPsmMap) {
        this.peptidesPsmMap = peptidesPsmMap;
    }

    /**
     *
     * @return
     */
    public Map<String, MgfIndex> getImportedMgfIndexObjectMap() {
        return importedMgfIndexObjectMap;
    }

}
