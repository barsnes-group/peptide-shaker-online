package com.uib.web.peptideshaker.model;

import com.compomics.util.experiment.biology.proteins.Protein;
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
    private  RangeColorGenerator peptideIntinsityColorScale;
    private Map<String, Set<String>> peptidesPsmMap;    
    private Map<String, Map<String, NetworkGraphNode>>  proteoformsMap;
      /**
     * MGF index file map (.cui) files.
     */
    private final Map<String, MgfIndex> importedMgfIndexObjectMap;
    
    
    
    

    public Map<String, Map<String, NetworkGraphNode>> getProteoformsMap() {
        return proteoformsMap;
    }

    public void setProteoformsMap(Map<String, Map<String, NetworkGraphNode>> proteoformsMap) {
        this.proteoformsMap = proteoformsMap;
    }

    public Map<String, FilterUpdatingEvent> getDatasetFilterEventsMap() {
        return datasetFilterEventsMap;
    }

    public VisualizationDatasetModel() {
        this.datasetFilterEventsMap = new LinkedHashMap<>();
        this.proteinsGraphDataMap = new HashMap<>();
        this.importedMgfIndexObjectMap=new HashMap<>();

    }

    public void addProteinGraphData(String proteinAccession, Object[] graphData) {
        proteinsGraphDataMap.put(proteinAccession, graphData);
    }

    public Map<String, Object[]> getProteinsGraphDataMap() {
        return proteinsGraphDataMap;
    }

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

    public TreeMap<Comparable, Set<Integer>> getUniquePeptideIntensityMap() {
        return uniquePeptideIntensityMap;
    }

    public void setUniquePeptideIntensityMap(TreeMap<Comparable, Set<Integer>> uniquePeptideIntensityMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(uniquePeptideIntensityMap);
        this.datasetFilterEventsMap.put(CONSTANT.INTENSITY_UNIQUE_FILTER_ID, event);
        this.uniquePeptideIntensityMap = uniquePeptideIntensityMap;
    }

    public TreeMap<Comparable, Set<Integer>> getAllPeptideIntensityMap() {
        return allPeptideIntensityMap;
    }

    public void setAllPeptideIntensityMap(TreeMap<Comparable, Set<Integer>> allPeptideIntensityMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(allPeptideIntensityMap);
        this.datasetFilterEventsMap.put(CONSTANT.INTENSITY_FILTER_ID, event);
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
    private Map<Comparable, Set<Integer>> modificationMap;
    private Map<Comparable, Set<Integer>> proteinTableMap;
    private LinkedHashMap<String, ProteinSequence> proteinSequenceMap;
//    private ModificationMatrixModel modificationMatrixModel;

    public LinkedHashMap<String, ProteinSequence> getProteinSequenceMap() {
        return proteinSequenceMap;
    }

    public void setProteinSequenceMap(LinkedHashMap<String, ProteinSequence> proteinSequenceMap) {
        this.proteinSequenceMap = proteinSequenceMap;
    }

    public Map<Comparable, Set<Integer>> getProteinTableMap() {
        return proteinTableMap;
    }

    public void setProteinTableMap(Map<Comparable, Set<Integer>> proteinTableMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinTableMap);
        this.datasetFilterEventsMap.put(CONSTANT.PROTEIN_TABLE_FILTER_ID, event);
        this.proteinTableMap = proteinTableMap;
    }

//    public ModificationMatrixModel getModificationMatrixModel() {
//        return modificationMatrixModel;
//    }
//
//    public void setModificationMatrixModel(ModificationMatrixModel modificationMatrixModel) {
//        this.modificationMatrixModel = modificationMatrixModel;
//    }
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

    public Map<Comparable, Set<Integer>> getProteinInferenceMap() {
        return proteinInferenceMap;
    }

    public void setProteinInferenceMap(Map<Comparable, Set<Integer>> proteinInferenceMap) {
        this.proteinInferenceMap = proteinInferenceMap;
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinInferenceMap);
        this.datasetFilterEventsMap.put(CONSTANT.PI_FILTER_ID, event);
    }

    public Map<Comparable, Set<Integer>> getProteinValidationMap() {
        return proteinValidationMap;
    }

    public void setProteinValidationMap(Map<Comparable, Set<Integer>> proteinValidationMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(proteinValidationMap);
        this.datasetFilterEventsMap.put(CONSTANT.VALIDATION_FILTER_ID, event);
        this.proteinValidationMap = proteinValidationMap;
    }

    public Map<Comparable, Set<Integer>> getChromosomeMap() {
        return chromosomeMap;
    }

    public void setChromosomeMap(Map<Comparable, Set<Integer>> chromosomeMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(chromosomeMap);
        this.datasetFilterEventsMap.put(CONSTANT.CHROMOSOME_FILTER_ID, event);
        this.chromosomeMap = chromosomeMap;
    }

    public TreeMap<Comparable, Set<Integer>> getValidatedPetideMap() {
        return validatedPetideMap;
    }

    public void setValidatedPetideMap(TreeMap<Comparable, Set<Integer>> validatedPetideMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedPetideMap);
        this.datasetFilterEventsMap.put(CONSTANT.PEPTIDES_NUMBER_FILTER_ID, event);
        this.validatedPetideMap = validatedPetideMap;
    }

    public TreeMap<Comparable, Set<Integer>> getValidatedPsmsMap() {
        return validatedPsmsMap;
    }

    public void setValidatedPsmsMap(TreeMap<Comparable, Set<Integer>> validatedPsmsMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedPsmsMap);
        this.datasetFilterEventsMap.put(CONSTANT.PSM_NUMBER_FILTER_ID, event);
        this.validatedPsmsMap = validatedPsmsMap;
    }

    public TreeMap<Comparable, Set<Integer>> getValidatedCoverageMap() {
        return validatedCoverageMap;
    }

    public void setValidatedCoverageMap(TreeMap<Comparable, Set<Integer>> validatedCoverageMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(validatedCoverageMap);
        this.datasetFilterEventsMap.put(CONSTANT.COVERAGE_FILTER_ID, event);
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

    public Map<Comparable, Set<Integer>> getModificationMap() {
        return modificationMap;
    }

    public void setModificationMap(Map<Comparable, Set<Integer>> modificationMap) {
        FilterUpdatingEvent event = new FilterUpdatingEvent();
        event.setSelectionMap(modificationMap);
        this.datasetFilterEventsMap.put(CONSTANT.MODIFICATIONS_FILTER_ID, event);
        this.modificationMap = modificationMap;
    }

    public RangeColorGenerator getPeptideIntinsityColorScale() {
        return peptideIntinsityColorScale;
    }

    public void setPeptideIntinsityColorScale(RangeColorGenerator peptideIntinsityColorScale) {
        this.peptideIntinsityColorScale = peptideIntinsityColorScale;
    }

    public Map<String, Set<String>> getPeptidesPsmMap() {
        return peptidesPsmMap;
    }

    public void setPeptidesPsmMap(Map<String, Set<String>> peptidesPsmMap) {
        this.peptidesPsmMap = peptidesPsmMap;
    }

    public Map<String, MgfIndex> getImportedMgfIndexObjectMap() {
        return importedMgfIndexObjectMap;
    }

}
