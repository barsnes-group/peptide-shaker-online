package com.uib.web.peptideshaker.utils;

import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidPattern;
import com.compomics.util.experiment.biology.enzymes.Enzyme;
import com.compomics.util.experiment.biology.ions.Charge;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.identification.utils.PeptideUtils;
import com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfIndex;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.compomics.util.io.file.SerializationUtils;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.compomics.util.parameters.identification.search.DigestionParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.PSMObject;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.TableHeaderConstatnts;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.ModificationMatrixModel;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.subviews.DatasetProteinsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.peptidespsmviews.components.SpectrumInformation;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import graphmatcher.NetworkGraphEdge;
import graphmatcher.NetworkGraphNode;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletContext;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import pl.exsio.plupload.PluploadFile;

/**
 * This class provides utils for visualization data-set
 *
 * @author Yehia Mokhtar Farag
 */
public class DatasetUtils implements Serializable {
    
    private final AppManagmentBean appManagmentBean;
    /**
     * Standard header values for protein, peptides, psm and moff table files
     */
    private final TableHeaderConstatnts table_headers;
    private RangeColorGenerator proteinUniqueIntensityColorGenerator;
    private RangeColorGenerator proteinAllIntensityColorGenerator;
    private RangeColorGenerator peptideIntensityColorGenerator;
    private RangeColorGenerator psmIntensityColorGenerator;
    private final Set<String> csf_pr_Accession_List;
    
    public DatasetUtils() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        table_headers = new TableHeaderConstatnts();
        this.csf_pr_Accession_List = appManagmentBean.getDatabaseUtils().getCsfprAccList();
    }
    
    public IdentificationParameters initIdentificationParametersObject(String datasetId, String initIdentificationParametersFileURI) {
        String fileName = datasetId + "_SEARCHGUI_IdentificationParameters.par";
        File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                file = appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(initIdentificationParametersFileURI, "IdentificationParameters.par", file);
                
            }
            
            return IdentificationParameters.getIdentificationParameters(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error : DatasetUtil " + ex);
        }
        return null;
    }
    
    public String createSharingLink(String userAPIKey, VisualizationDatasetModel dataset) {
        String dsKey = userAPIKey + "_" + dataset.getId();
        String encryptedDsKey = appManagmentBean.getURLUtils().encrypt(dsKey);
        int dsIndex = appManagmentBean.getDatabaseUtils().getDatasetSharingKey(encryptedDsKey);
        if (dsIndex == -1) {
            JsonObject object = new JsonObject();
            object.put("apiKey", userAPIKey);
            object.put("dsName", dataset.getName());
            object.put("sgui", dataset.getSearchGUIZipFile().getId());
            object.put("ps", dataset.getId());
            object.put("history", dataset.getSearchGUIZipFile().getHistoryId());
            object.put("mgf", dataset.getMgfList().getId());
            object.put("cui", dataset.getMgfIndexList().getId());
            if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                object.put("moffid", dataset.getMoffList().getId());
            }
            String datasetDetails;
            try {
                datasetDetails = URLEncoder.encode(object.toString(), "UTF-8");
                dsIndex = appManagmentBean.getDatabaseUtils().insertDatasetSharingData(encryptedDsKey, datasetDetails);
            } catch (UnsupportedEncodingException ex) {
                System.out.println("at Error: dataset utils " + ex);
                return null;
            }
        }
        ServletContext scx = VaadinServlet.getCurrent().getServletContext();
        String appName = scx.getContextPath();
        String encryptedDsIndex = appManagmentBean.getURLUtils().encrypt(dsIndex + "");
        
        try {
            encryptedDsIndex = URLEncoder.encode("datasetid=" + encryptedDsIndex, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("at Error: dataset utils " + ex);
        }
        String encodedLink = Page.getCurrent().getLocation().toString().split(appName)[0] + "/" + appName + "?" + encryptedDsIndex;
        return encodedLink;
    }
    
    public VisualizationDatasetModel getOnProgressDataset(String datasetType) {
        VisualizationDatasetModel dataset = new VisualizationDatasetModel();
        dataset.setDatasetSource(CONSTANT.GALAXY_SOURCE);
        dataset.setDatasetType(datasetType);
        dataset.setStatus(CONSTANT.RUNNING_STATUS);
        dataset.setSharingLink(CONSTANT.RUNNING_STATUS);
        return dataset;
    }

    /**
     * Prepare visualisation dataset from uploaded uploaded files by checking
     * all uploaded files
     *
     * @param projectName
     * @param uploadedFileMap
     * @return array of checked valid files
     */
    public VisualizationDatasetModel initialiseDatasetFromUploadedFiles(String projectName, Map<String, PluploadFile> uploadedFileMap) {
        
        VisualizationDatasetModel dataset = new VisualizationDatasetModel();
        dataset.setDatasetSource(CONSTANT.USER_UPLOAD_SOURCE);
        dataset.setName(projectName);
        dataset.setDatasetType(CONSTANT.ID_DATASET);
        dataset.setStatus(CONSTANT.OK_STATUS);
        dataset.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        dataset.setId(projectName + "_" + dataset.getCreatedTime());
        for (String key : uploadedFileMap.keySet()) {
            switch (key) {
                case CONSTANT.FASTA_FILE_EXTENSION:
                    File fastaFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    GalaxyFileModel fastaFileModel = new GalaxyFileModel();
                    fastaFileModel.setDownloadUrl(fastaFile.getAbsolutePath());
                    fastaFileModel.setName(uploadedFileMap.get(key).getName());
                    dataset.setFastaFileModel(fastaFileModel);
                    break;
                case CONSTANT.PROTEIN_FILE_TYPE:
                    File proteinFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    GalaxyFileModel proteinFileModel = new GalaxyFileModel();
                    proteinFileModel.setDownloadUrl(proteinFile.getAbsolutePath());
                    proteinFileModel.setName(uploadedFileMap.get(key).getName());
                    dataset.setProteinFileModel(proteinFileModel);
                    break;
                case CONSTANT.PEPTIDE_FILE_TYPE:
                    File peptideFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    GalaxyFileModel peptideileModel = new GalaxyFileModel();
                    peptideileModel.setDownloadUrl(peptideFile.getAbsolutePath());
                    peptideileModel.setName(uploadedFileMap.get(key).getName());
                    dataset.setPeptideFileModel(peptideileModel);
                    break;
                
            }
        }
        
        appManagmentBean.getUserHandler().addToDatasetMap(dataset);
        appManagmentBean.getUI_Manager().updateAll();
        appManagmentBean.getUI_Manager().setSelectedDatasetId(dataset.getId());
        appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), DatasetProteinsSubView.class.getName());
        return dataset;
        
    }

    /**
     * Prepare visualisation dataset from uploaded uploaded files by checking
     * all uploaded files
     *
     * @param projectName
     * @param uploadedFileMap
     * @return array of checked valid files
     */
    public VisualizationDatasetModel runUploadedFilesDemo() {
        System.out.println("at run upload demo ?");
        VisualizationDatasetModel dataset = new VisualizationDatasetModel();
        dataset.setDatasetSource(CONSTANT.USER_UPLOAD_SOURCE);
        dataset.setName("Uploaded_Dataset_Demo");
        dataset.setDatasetType(CONSTANT.ID_DATASET);
        dataset.setStatus(CONSTANT.OK_STATUS);
        dataset.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        dataset.setId("Uploaded_Dataset_Demo_" + dataset.getCreatedTime());
        
        File fastaFile = new File(appManagmentBean.getAppConfig().getWorking_folder_path() + "_FASTA_.fasta");
        GalaxyFileModel fastaFileModel = new GalaxyFileModel();
        fastaFileModel.setDownloadUrl(fastaFile.getAbsolutePath());
        fastaFileModel.setName(fastaFile.getName());
        dataset.setFastaFileModel(fastaFileModel);
        
        File proteinFile = new File(appManagmentBean.getAppConfig().getWorking_folder_path() + "_proteins_.txt");
        GalaxyFileModel proteinFileModel = new GalaxyFileModel();
        proteinFileModel.setDownloadUrl(proteinFile.getAbsolutePath());
        proteinFileModel.setName(proteinFile.getName());
        dataset.setProteinFileModel(proteinFileModel);
        
        File peptideFile = new File(appManagmentBean.getAppConfig().getWorking_folder_path() + "_Peptides_.txt");
        GalaxyFileModel peptideileModel = new GalaxyFileModel();
        peptideileModel.setDownloadUrl(peptideFile.getAbsolutePath());
        peptideileModel.setName(peptideFile.getName());
        dataset.setPeptideFileModel(peptideileModel);
        
        appManagmentBean.getUserHandler().addToDatasetMap(dataset);
        appManagmentBean.getUI_Manager().updateAll();
        appManagmentBean.getUI_Manager().setSelectedDatasetId(dataset.getId());
        appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), DatasetProteinsSubView.class.getName());
        return dataset;
        
    }
    private final int[] idFileIndexes = new int[]{1, 2, 3, 4, 6};
    private final int[] quantFileIndexes = new int[]{3, 1, 2, 4, 6};
    
    public void processDatasetProteins(VisualizationDatasetModel dataset) {
        switch (dataset.getDatasetSource()) {
            case CONSTANT.GALAXY_SOURCE:
                try {
//                int[] filesIndex = idFileIndexes;
//                if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
//                    filesIndex = quantFileIndexes;
//                }

                String fileName = dataset.getPsZipFile().getId() + "_proteins_.txt";
                File proteinsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                
                fileName = dataset.getPsZipFile().getId() + "_Peptides_.txt";
                File peptidesFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                
                fileName = dataset.getPsZipFile().getId() + "_PSMs_.txt";
                File psmsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                try {
                    
                    if (!proteinsFile.exists()) {
                        proteinsFile.createNewFile();
                        appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), "Default_Protein_Report_with_non-validated_matches", proteinsFile);
                        
                    }
                    dataset.setProteinsMap(processGalaxyProteinsFile(proteinsFile));
                    if (!peptidesFile.exists()) {
                        peptidesFile.createNewFile();
                        appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), "Default_Peptide_Report_with_non-validated_matches", peptidesFile);
                    }
                    dataset.setPeptidesMap(processGalaxyPeptidesFile(peptidesFile));
                    
                    if (!psmsFile.exists()) {
                        psmsFile.createNewFile();
                        appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), "Default_PSM_Report_with_non-validated_matches", psmsFile);
                    }
                    dataset.setPsmsMap(processGalaxyPsmsFile(psmsFile));
                    if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                        double topValue = Double.MIN_VALUE;
                        for (GalaxyFileModel moffFile : dataset.getMoffList().getElements()) {
                            fileName = moffFile.getId() + "_moff_.tabular";
                            File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                            if (!file.exists()) {
                                file.createNewFile();
                                appManagmentBean.getHttpClientUtil().downloadFile(moffFile.getDownloadUrl(), file);
                            }
                            topValue = Math.max(topValue, processGalaxyMoFFFile(file, dataset.getPsmsMap()));
                        }
                        psmIntensityColorGenerator = new RangeColorGenerator(topValue);
                    }
                    /**
                     * map PSM Objects to peptides
                     */
                    Map<String, Set<String>> peptidesPsmMap = new HashMap<>();
                    for (PSMObject psm : dataset.getPsmsMap().values()) {
                        if (!peptidesPsmMap.containsKey(psm.getModifiedSequence())) {
                            peptidesPsmMap.put(psm.getModifiedSequence(), new HashSet<>());
                        }
                        peptidesPsmMap.get(psm.getModifiedSequence()).add(psm.getKey());
                    }
                    dataset.setPeptidesPsmMap(peptidesPsmMap);
                    /**
                     * init mgf file indexes*
                     */
                    for (GalaxyFileModel galaxyFileModel : dataset.getMgfIndexList().getElements()) {
                        fileName = galaxyFileModel.getId() + "_mgfindex_.cui";
                        File cuiFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                        if (!cuiFile.exists()) {
                            cuiFile.createNewFile();
                            appManagmentBean.getHttpClientUtil().downloadFile(galaxyFileModel.getDownloadUrl(), cuiFile);
                            Object object = SerializationUtils.readObject(cuiFile);
                            dataset.getImportedMgfIndexObjectMap().put(galaxyFileModel.getName(), (MgfIndex) object);
                            
                        }
                        
                    }
                    
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
            case CONSTANT.USER_UPLOAD_SOURCE:
                dataset.setProteinsMap(processGalaxyProteinsFile(new File(dataset.getProteinFileModel().getDownloadUrl())));
                dataset.setPeptidesMap(processGalaxyPeptidesFile(new File(dataset.getPeptideFileModel().getDownloadUrl())));
                dataset.setProteinSequenceMap(processFastaFile(new File(dataset.getFastaFileModel().getDownloadUrl())));
                dataset.getProteinsMap().values().stream().map((protein) -> {
                    String[] descArr = dataset.getProteinSequenceMap().get(protein.getAccession()).getDescription().split("\\s");
                    protein.setDescription(descArr[0].replace("OS", "").trim());
                    protein.setProteinEvidence(CONSTANT.PROTEIN_EVIDENCE[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
                    return protein;
                }).forEachOrdered((protein) -> {
                    protein.setSequence(dataset.getProteinSequenceMap().get(protein.getAccession()).getSequenceAsString());
                });
                break;
            case CONSTANT.PRIDE_SOURCE:
                break;
        }
        initialiseDatasetFilterMaps(dataset);
        
    }
    
    private Map<Integer, ProteinGroupObject> processGalaxyProteinsFile(File file) {
        Map<Integer, ProteinGroupObject> proteinsMap = new LinkedHashMap<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file), 1024 * 100);
            String line;
            /**
             * escape header
             */
            line = bufferedReader.readLine();
            line = line.replace("\"", "");
            HashMap<String, Integer> proteinFileHeaderIndexerMap = new HashMap<>();
            proteinFileHeaderIndexerMap.put(table_headers.Index, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Main_Accession, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Description, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Gene_Name, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Chromosome, -1);
            proteinFileHeaderIndexerMap.put(table_headers.MW_kDa, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Possible_Coverage_Pers, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Coverage_Pers, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Spectrum_Counting, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Confidently_Localized_Modification_Sites, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Confidently_Localized_Modification_Sites, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Ambiguously_Localized_Modification_Sites, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Ambiguously_Localized_Modification_Sites, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Protein_Inference, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Secondary_Accessions, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Protein_Group, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Validated_Peptides, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Peptides, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Unique_Peptides, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Validated_Unique_Peptides, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_Validated_PSMs, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Number_PSMs, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Confidence_Pers, -1);
            proteinFileHeaderIndexerMap.put(table_headers.Validation, -1);
            int i = 0;
            for (String str : line.split("\\t")) {
                str = str.replace(" ", "").toLowerCase();
                if (proteinFileHeaderIndexerMap.containsKey(str)) {
                    proteinFileHeaderIndexerMap.replace(str, i);
                }
                
                i++;
            }
            i = 1;
            while ((line = bufferedReader.readLine()) != null) {
                
                line = line.replace("\"", "");
                String[] arr = line.split("\\t");
                ProteinGroupObject proteinGroup = new ProteinGroupObject();
                proteinGroup.setAccession(arr[proteinFileHeaderIndexerMap.get(table_headers.Main_Accession)]);
                proteinGroup.setOreginalProteinGroup(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Group)]);
                proteinGroup.setDescription(arr[proteinFileHeaderIndexerMap.get(table_headers.Description)]);
                proteinGroup.setChromosome(arr[proteinFileHeaderIndexerMap.get(table_headers.Chromosome)]);
                if (proteinGroup.getChromosome().trim().isEmpty()) {
                    proteinGroup.setChromosome("No Information");
                }
                proteinGroup.setValidatedPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Peptides)]));
                proteinGroup.setPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Peptides)]));
                proteinGroup.setValidatedPSMsNumber(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_PSMs)]).intValue());
                proteinGroup.setPSMsNumber(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_PSMs)]));
                proteinGroup.setConfidence(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));
                proteinGroup.setIndex(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Index)]));
                proteinGroup.setGeneName(arr[proteinFileHeaderIndexerMap.get(table_headers.Gene_Name)]);
                proteinGroup.setMW(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.MW_kDa)]));
                proteinGroup.setPossibleCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Possible_Coverage_Pers)]));
                proteinGroup.setCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Coverage_Pers)]));
                proteinGroup.setSpectrumCounting(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Spectrum_Counting)]));
                if ((arr[proteinFileHeaderIndexerMap.get(table_headers.Confidently_Localized_Modification_Sites)] + "").trim().equalsIgnoreCase("")) {
                    proteinGroup.setConfidentlyLocalizedModificationSites("No Modification");
                } else {
                    proteinGroup.setConfidentlyLocalizedModificationSites(arr[proteinFileHeaderIndexerMap.get(table_headers.Confidently_Localized_Modification_Sites)]);//.split("\\(")[0]);
                }
                proteinGroup.setConfidentlyLocalizedModificationSitesNumber(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Confidently_Localized_Modification_Sites)]);
                
                proteinGroup.setAmbiguouslyLocalizedModificationSites(arr[proteinFileHeaderIndexerMap.get(table_headers.Ambiguously_Localized_Modification_Sites)]);
                proteinGroup.setAmbiguouslyLocalizedModificationSitesNumber(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Ambiguously_Localized_Modification_Sites)]);
                proteinGroup.setProteinInference(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Inference)].replace(" Proteins", "").replace(" Protein", "").replace("and", "&").trim());
                
                if (proteinGroup.getProteinInference().trim().isEmpty()) {
                    proteinGroup.setProteinInference("No Information");
                }
                proteinGroup.setSecondaryAccessions(arr[proteinFileHeaderIndexerMap.get(table_headers.Secondary_Accessions)]);
                proteinGroup.setUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Unique_Peptides)]));
                proteinGroup.setValidatedUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Unique_Peptides)]));
                proteinGroup.setValidation(arr[proteinFileHeaderIndexerMap.get(table_headers.Validation)]);
                
                if (proteinGroup.getValidation().trim().isEmpty()) {
                    proteinGroup.setValidation("No Information");
                }
                proteinsMap.put(proteinGroup.getIndex(), proteinGroup);
//                
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("Error : line 2601 " + ex);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex1) {
                }
            }
        }
        return proteinsMap;
    }
    
    private Map<Integer, PeptideObject> processGalaxyPeptidesFile(File file) {
        Map<Integer, PeptideObject> peptidesMap = new LinkedHashMap<>();
        BufferedReader bufferedReader = null;
        Map<String, Integer> peptideFileHeaderIndexerMap = new HashMap<>();
        peptideFileHeaderIndexerMap.put(table_headers.Index, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Proteins, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Protein_Groups, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Number_Validated_Protein_Groups, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Unique_Protein_Group, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Sequence, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Modified_Sequence, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Position, -1);
        peptideFileHeaderIndexerMap.put(table_headers.AAs_Before, -1);
        peptideFileHeaderIndexerMap.put(table_headers.AAs_After, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Variable_Modifications, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Fixed_Modifications, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Localization_Confidence, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Number_Validated_PSMs, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Number_PSMs, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Confidence_Pers, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Validation, -1);
        peptideFileHeaderIndexerMap.put(table_headers.Quant, -1);
//        if (uploadedProject) {
//            intensitySet = new TreeSet<>();
//        }
        try {
            bufferedReader = new BufferedReader(new FileReader(file), 1024 * 100);
            /**
             * escape header
             */
            String line = bufferedReader.readLine();
            line = line.replace("\"", "");
            int i = 0;
            for (String str : line.split("\\t")) {
                str = str.replace(" ", "").toLowerCase();
                if (peptideFileHeaderIndexerMap.containsKey(str)) {
                    peptideFileHeaderIndexerMap.replace(str, i);
                    
                }
                i++;
            }
            while ((line = bufferedReader.readLine()) != null) {
                
                line = line.replace("\"", "");
                String[] arr = line.split("\\t");
                PeptideObject peptide = new PeptideObject();
                peptide.setProteinGroups(arr[peptideFileHeaderIndexerMap.get(table_headers.Protein_Groups)]);
                peptide.setSequence(arr[peptideFileHeaderIndexerMap.get(table_headers.Sequence)]);
                peptide.setModifiedSequence(arr[peptideFileHeaderIndexerMap.get(table_headers.Modified_Sequence)]);
                peptide.setVariableModifications(arr[peptideFileHeaderIndexerMap.get(table_headers.Variable_Modifications)]);
                peptide.setFixedModificationsAsString(arr[peptideFileHeaderIndexerMap.get(table_headers.Fixed_Modifications)]);
                peptide.setValidatedPSMsNumber(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Number_Validated_PSMs)]));
                peptide.setPSMsNumber(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Number_PSMs)]));
                peptide.setConfidence(Double.parseDouble(arr[peptideFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));
                peptide.setValidation(arr[peptideFileHeaderIndexerMap.get(table_headers.Validation)]);
                
                peptide.setIndex(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Index)]));
                peptide.setProteins(arr[peptideFileHeaderIndexerMap.get(table_headers.Proteins)]);
                peptide.setValidatedProteinGroupsNumber(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Number_Validated_Protein_Groups)]));
                peptide.setUniqueDatabase(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Unique_Protein_Group)]));
                peptide.setPostion(arr[peptideFileHeaderIndexerMap.get(table_headers.Position)]);
                peptide.setAasBefore(arr[peptideFileHeaderIndexerMap.get(table_headers.AAs_Before)]);
                peptide.setAasAfter(arr[peptideFileHeaderIndexerMap.get(table_headers.AAs_After)]);
                peptide.setLocalizationConfidence(arr[peptideFileHeaderIndexerMap.get(table_headers.Localization_Confidence)]);
                
                if (peptide.isModified() && peptide.getVariableModificationsAsString().contains("0") && peptide.getSequence().startsWith("NH2")) {
                    peptide.setModifiedSequence(peptide.getModifiedSequence().replaceFirst("NH2", "pyro"));
                }
                peptidesMap.put(peptide.getIndex(), peptide);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error : line 2100  IOException " + ex);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex1) {
                    System.out.println("Error : line 2105  IOException " + ex);
                }
            }
        }
        return peptidesMap;
    }
    
    private Map<String, PSMObject> processGalaxyPsmsFile(File file) {
        Map<String, PSMObject> psmsMap = new LinkedHashMap<>();
        HashMap<String, Integer> psmFileHeaderIndexerMap = new HashMap<>();
        psmFileHeaderIndexerMap.put(table_headers.Index, -1);
        psmFileHeaderIndexerMap.put(table_headers.Proteins, -1);
        psmFileHeaderIndexerMap.put(table_headers.Sequence, -1);
        psmFileHeaderIndexerMap.put(table_headers.AAs_Before, -1);
        psmFileHeaderIndexerMap.put(table_headers.AAs_After, -1);
        psmFileHeaderIndexerMap.put(table_headers.Position, -1);
        psmFileHeaderIndexerMap.put(table_headers.Modified_Sequence, -1);
        psmFileHeaderIndexerMap.put(table_headers.Variable_Modifications, -1);
        psmFileHeaderIndexerMap.put(table_headers.Fixed_Modifications, -1);
        psmFileHeaderIndexerMap.put(table_headers.Spectrum_File, -1);
        psmFileHeaderIndexerMap.put(table_headers.Spectrum_Title, -1);
        psmFileHeaderIndexerMap.put(table_headers.Spectrum_Scan_Number, -1);
        psmFileHeaderIndexerMap.put(table_headers.RT, -1);
        psmFileHeaderIndexerMap.put(table_headers.MZ, -1);
        psmFileHeaderIndexerMap.put(table_headers.Measured_Charge, -1);
        psmFileHeaderIndexerMap.put(table_headers.Identification_Charge, -1);
        psmFileHeaderIndexerMap.put(table_headers.Theoretical_Mass, -1);
        psmFileHeaderIndexerMap.put(table_headers.Isotope_Number, -1);
        psmFileHeaderIndexerMap.put(table_headers.Precursor_mz_Error_ppm, -1);
        psmFileHeaderIndexerMap.put(table_headers.Localization_Confidence, -1);
        psmFileHeaderIndexerMap.put(table_headers.Probabilistic_PTM_score, -1);
        psmFileHeaderIndexerMap.put(table_headers.D_score, -1);
        psmFileHeaderIndexerMap.put(table_headers.Confidence_Pers, -1);
        psmFileHeaderIndexerMap.put(table_headers.Validation, -1);
        BufferedReader bufferedReader = null;
        int index = 1;
        try {//     
            bufferedReader = new BufferedReader(new FileReader(file), 1024 * 100);
            
            String line = bufferedReader.readLine();
            line = line.replace("\"", "");
            int i = 0;
            for (String str : line.split("\\t")) {
                str = str.replace(" ", "").toLowerCase();
                if (psmFileHeaderIndexerMap.containsKey(str)) {
                    psmFileHeaderIndexerMap.replace(str, i);
                }
                i++;
            }
            
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("\"", "");
                String[] arr = line.split("\\t");
                PSMObject psm = new PSMObject();
                psm.setIndex(index++);//              
                psm.setSequence(arr[psmFileHeaderIndexerMap.get(table_headers.Sequence)]);
//                psm.setAasBefore(arr[psmMoffFileHeaderIndexerMap.get(table_headers.AAs_Before)]);
//                psm.setAasAfter((arr[psmMoffFileHeaderIndexerMap.get(table_headers.AAs_After)]));
//                psm.setPostions(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Position)]);
                psm.setModifiedSequence(arr[psmFileHeaderIndexerMap.get(table_headers.Modified_Sequence)]);
//                for (String mod : arr[psmMoffFileHeaderIndexerMap.get(table_headers.Variable_Modifications)].split(",")) {
//                    psm.addVariableModification(mod);
//                }
//                for (String mod : arr[psmMoffFileHeaderIndexerMap.get(table_headers.Fixed_Modifications)].split(",")) {
//                    psm.addFixedModification(mod);
//                }
                psm.setSpectrumFile(arr[psmFileHeaderIndexerMap.get(table_headers.Spectrum_File)]);
                psm.setSpectrumTitle(arr[psmFileHeaderIndexerMap.get(table_headers.Spectrum_Title)]);
//                if (psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number) != -1 && !arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number)].equalsIgnoreCase("")) {
//                    psm.setTheoreticalMass(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number)]));
//                }
//                    psm.setSpectrumScanNumber(arr[psmMoffFileHeaderIndexerMap.get("spectrum scan number")]);
//                psm.setRT(arr[psmMoffFileHeaderIndexerMap.get(table_headers.RT)]);
//                psm.setMZ(arr[psmMoffFileHeaderIndexerMap.get(table_headers.MZ)]);
                psm.setMeasuredCharge((arr[psmFileHeaderIndexerMap.get(table_headers.Measured_Charge)]));
                psm.setIdentificationCharge((arr[psmFileHeaderIndexerMap.get(table_headers.Identification_Charge)]));
//                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Theoretical_Mass)].equalsIgnoreCase("")) {
//                    psm.setTheoreticalMass(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Theoretical_Mass)]));
//                }
//                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Isotope_Number)].equalsIgnoreCase("")) {
//                    psm.setIsotopeNumber(Integer.parseInt(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Isotope_Number)]));
//                }
                if (!arr[psmFileHeaderIndexerMap.get(table_headers.Precursor_mz_Error_ppm)].equalsIgnoreCase("")) {
                    psm.setPrecursorMZError_PPM(Double.parseDouble(arr[psmFileHeaderIndexerMap.get(table_headers.Precursor_mz_Error_ppm)]));
                }
//                psm.setLocalizationConfidence(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Localization_Confidence)]);
//
//                psm.setProbabilisticPTMScore((arr[psmMoffFileHeaderIndexerMap.get(table_headers.Probabilistic_PTM_score)]));

//                psm.setD_Score((arr[psmMoffFileHeaderIndexerMap.get(table_headers.D_score)]));
                if (!arr[psmFileHeaderIndexerMap.get(table_headers.Confidence_Pers)].equalsIgnoreCase("")) {
                    psm.setConfidence(Double.parseDouble(arr[psmFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));
                }
                psm.setValidation(arr[psmFileHeaderIndexerMap.get(table_headers.Validation)]);
                String key = psm.getSpectrumTitle() + "_" + psm.getSpectrumFile();
                psm.setKey(key);

//                if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence())) {
//                    processPeptidesTask.getPSMsMap().get(psm.getModifiedSequence()).add(psm);
//                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("L", "I"))) {
//                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I with L ?" + psm.getModifiedSequence());
//                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("I", "L"))) {
//                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
//                } else {
//                    System.out.println("at Error for psm II mapping...not exist peptide " + psm.getModifiedSequence());
//                }
                psmsMap.put(key, psm);
            }
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex1) {
                }
            }
        }
        return psmsMap;
    }
    
    private double processGalaxyMoFFFile(File file, Map<String, PSMObject> psmsMap) {
        HashMap<String, Integer> moFFFileHeaderIndexerMap = new HashMap<>();
        moFFFileHeaderIndexerMap.put(table_headers.Intensity, -1);
        moFFFileHeaderIndexerMap.put(table_headers.Spectrum_Title, -1);
        moFFFileHeaderIndexerMap.put(table_headers.Spectrum_File, -1);
        BufferedReader bufferedReader = null;
        double topValue = Double.MIN_VALUE;
        try {// 
            bufferedReader = new BufferedReader(new FileReader(file), 1024 * 100);
            /**
             * index header header
             */
            String line = bufferedReader.readLine();
            line = line.replace("\"", "");
            int i = 0;
            for (String str : line.split("\\t")) {
                str = str.replace(" ", "").toLowerCase();
                if (moFFFileHeaderIndexerMap.containsKey(str)) {
                    moFFFileHeaderIndexerMap.replace(str, i);
                }
                i++;
                
            }

//                    int index = 1;
//
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("\"", "");
                String[] arr = line.split("\\t");
                String spectrumFile = arr[moFFFileHeaderIndexerMap.get(table_headers.Spectrum_File)];
                String spectrumTitle = arr[moFFFileHeaderIndexerMap.get(table_headers.Spectrum_Title)];
                double intensity = Double.parseDouble(arr[moFFFileHeaderIndexerMap.get(table_headers.Intensity)]);
                String key = spectrumTitle + "_" + spectrumFile;
                if (psmsMap.containsKey(key)) {
                    psmsMap.get(key).setIntensity(intensity);
                    psmsMap.get(key).setKey(key);
                    if (intensity > topValue) {
                        topValue = intensity;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error : line 2100  IOException " + ex);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex1) {
                    System.out.println("Error : line 2105  IOException " + ex);
                }
            }
        }
        return topValue;
    }
    
    private void initialiseDatasetFilterMaps(VisualizationDatasetModel dataset) {
        Map<Comparable, Set<Integer>> proteinInferenceMap = new LinkedHashMap<>();
        proteinInferenceMap.put("No Information", new LinkedHashSet<>());
        proteinInferenceMap.put("Single", new LinkedHashSet<>());
        proteinInferenceMap.put("Related", new LinkedHashSet<>());
        proteinInferenceMap.put("Related & Unrelated", new LinkedHashSet<>());
        proteinInferenceMap.put("Unrelated", new LinkedHashSet<>());
        
        Map<Comparable, Set<Integer>> proteinValidationMap = new LinkedHashMap<>();
        proteinValidationMap.put(CONSTANT.NO_INFORMATION, new LinkedHashSet<>());
        proteinValidationMap.put(CONSTANT.VALIDATION_CONFIDENT, new LinkedHashSet<>());
        proteinValidationMap.put(CONSTANT.VALIDATION_DOUBTFUL, new LinkedHashSet<>());
        proteinValidationMap.put(CONSTANT.VALIDATION_NOT_VALID, new LinkedHashSet<>());
        
        Map<Comparable, Set<Integer>> chromosomeMap = new LinkedHashMap<>();
        TreeMap<Comparable, Set<Integer>> validatedPetideMap = new TreeMap<>();
        TreeMap<Comparable, Set<Integer>> validatedPsmsMap = new TreeMap<>();
        TreeMap<Comparable, Set<Integer>> validatedCoverageMap = new TreeMap<>();
        Map<String, Set<Integer>> accessionToGroupMap = new TreeMap<>();
        Map<Comparable, Set<Integer>> modificationMap = new LinkedHashMap<>();
        Map<Comparable, Set<Integer>> proteinTableMap = new LinkedHashMap<>();
        proteinTableMap.put(CONSTANT.PROTEIN_TABLE_FILTER_ID, new LinkedHashSet<>(dataset.getProteinsMap().keySet()));
        dataset.setProteinTableMap(proteinTableMap);
//        Map<Comparable, Set<Integer>> compModificationMap = new LinkedHashMap<>();
        modificationMap.put("No Modification", new LinkedHashSet<>(dataset.getProteinsMap().keySet()));
//        compModificationMap.put("No Modification", new LinkedHashSet<>(dataset.getProteinsMap().keySet()));
        double maxMS2Quant = Integer.MIN_VALUE;
        double maxMW = Integer.MIN_VALUE;
        for (ProteinGroupObject protein : dataset.getProteinsMap().values()) {
            if (protein.getSpectrumCounting() > maxMS2Quant) {
                maxMS2Quant = protein.getSpectrumCounting();
            }
            if (protein.getMW() > maxMW) {
                maxMW = protein.getMW();
            }
            if (!proteinInferenceMap.containsKey(protein.getProteinInference())) {
                proteinInferenceMap.put(protein.getProteinInference(), new LinkedHashSet<>());
            }
            proteinInferenceMap.get(protein.getProteinInference()).add(protein.getIndex());
            if (!proteinValidationMap.containsKey(protein.getValidation())) {
                proteinValidationMap.put(protein.getValidation(), new LinkedHashSet<>());
            }
            proteinValidationMap.get(protein.getValidation()).add(protein.getIndex());
            if (!chromosomeMap.containsKey(protein.getChromosomeIndex())) {
                chromosomeMap.put(protein.getChromosomeIndex(), new LinkedHashSet<>());
            }
            chromosomeMap.get(protein.getChromosomeIndex()).add(protein.getIndex());
            
            if (!validatedPetideMap.containsKey(protein.getValidatedPeptidesNumber())) {
                validatedPetideMap.put(protein.getValidatedPeptidesNumber(), new LinkedHashSet<>());
            }
            validatedPetideMap.get(protein.getValidatedPeptidesNumber()).add(protein.getIndex());
            
            if (!validatedPsmsMap.containsKey(protein.getValidatedPSMsNumber())) {
                validatedPsmsMap.put(protein.getValidatedPSMsNumber(), new LinkedHashSet<>());
            }
            validatedPsmsMap.get(protein.getValidatedPSMsNumber()).add(protein.getIndex());
            
            int roundedPercentage = (int) Math.round(protein.getCoverage());
            if (!validatedCoverageMap.containsKey(roundedPercentage)) {
                validatedCoverageMap.put(roundedPercentage, new LinkedHashSet<>());
            }
            validatedCoverageMap.get(roundedPercentage).add(protein.getIndex());
            for (String accession : protein.getProteinGroupSet()) {
                accession = accession.trim();
                if (!accessionToGroupMap.containsKey(accession)) {
                    accessionToGroupMap.put(accession, new LinkedHashSet<>());
                }
                accessionToGroupMap.get(accession).add(protein.getIndex());
            }
            
        }
        dataset.setProteinInferenceMap(proteinInferenceMap);
        dataset.setProteinValidationMap(proteinValidationMap);
        dataset.setChromosomeMap(chromosomeMap);
        dataset.setValidatedPetideMap(validatedPetideMap);
        dataset.setValidatedPsmsMap(validatedPsmsMap);
        dataset.setValidatedCoverageMap(validatedCoverageMap);
        dataset.setAccessionToGroupMap(accessionToGroupMap);
        dataset.setMaxMS2Quant(maxMS2Quant);
        dataset.setMaxMW(maxMW);
        
        for (PeptideObject peptide : dataset.getPeptidesMap().values()) {
            /**
             * calculate modifications from peptide file*
             */
            for (ModificationMatch modification : peptide.getVariableModifications()) {
                if (!modificationMap.containsKey(modification.getModification())) {
                    modificationMap.put(modification.getModification(), new LinkedHashSet<>());
                }
                for (String acc : peptide.getProteinsSet()) {
                    if (accessionToGroupMap.containsKey(acc)) {
                        modificationMap.get(modification.getModification()).addAll(accessionToGroupMap.get(acc));
                        if (!modification.getModification().equals("No Modification")) {
                            modificationMap.get("No Modification").removeAll(accessionToGroupMap.get(acc));
                        }
                    }
                }
                
            }
        }
        ModificationMatrixModel matrixModel = appManagmentBean.getModificationMatrixUtilis().generateMatrixModel(modificationMap);
        modificationMap.clear();
        matrixModel.getColumns().keySet().forEach((key) -> {
            Comparable updatedKey = key;
            if (!key.toString().contains("[")) {
                updatedKey = "[" + key + "]";
            }
            modificationMap.put(updatedKey, new LinkedHashSet<>(matrixModel.getColumns().get(key)));
        });
        matrixModel.getRows().keySet().forEach((key) -> {
            modificationMap.put(key, new LinkedHashSet<>(matrixModel.getColumns().get(key)));
        });
        dataset.setModificationMap(modificationMap);
        if (dataset.getDatasetSource().equals(CONSTANT.GALAXY_SOURCE)) {
            mapProteinsPeptidesPsms(dataset);
        } else if (dataset.getDatasetSource().equals(CONSTANT.USER_UPLOAD_SOURCE)) {
            mapProteinsPeptides(dataset);
        }
        
    }
    
    private void mapProteinsPeptides(VisualizationDatasetModel dataset) {
        
        Map<String, List<PeptideObject>> peptideToProteinMap = new HashMap<>();
        for (PeptideObject peptideObject : dataset.getPeptidesMap().values()) {
            for (String key : peptideObject.getProteinGroupsSet()) {
                if (!peptideToProteinMap.containsKey(key)) {
                    peptideToProteinMap.put(key, new ArrayList<>());
                }
                peptideToProteinMap.get(key).add(peptideObject);
            }
            
        }
        for (ProteinGroupObject proteinObject : dataset.getProteinsMap().values()) {
            proteinObject.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(proteinObject.getAccession()));
            List<PeptideObject> peptides = peptideToProteinMap.get(proteinObject.getProteinGroupKey());
            if (peptides != null) {
                for (PeptideObject peptide : peptides) {
                    proteinObject.addPeptide(peptide.getIndex());//                    
                }
            }
        }
        
    }
    
    private void mapProteinsPeptidesPsms(VisualizationDatasetModel dataset) {
        
        Map<String, List<PSMObject>> psmToPeptideMap = new HashMap<>();
        for (PSMObject psmObject : dataset.getPsmsMap().values()) {
            if (!psmToPeptideMap.containsKey(psmObject.getModifiedSequence())) {
                psmToPeptideMap.put(psmObject.getModifiedSequence(), new ArrayList<>());
            }
            psmToPeptideMap.get(psmObject.getModifiedSequence()).add(psmObject);
        }
        Map<String, List<PeptideObject>> peptideToProteinMap = new HashMap<>();
        double topValue = Double.MIN_VALUE;
        for (PeptideObject peptideObject : dataset.getPeptidesMap().values()) {
            List<PSMObject> psms = psmToPeptideMap.get(peptideObject.getModifiedSequence());
            double inteinsity = 0d;
            if (psms != null) {
                for (PSMObject psm : psms) {
                    if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                        inteinsity += psm.getIntensity();
                        int per = (int) Math.round((psm.getIntensity() / psmIntensityColorGenerator.getMax()) * 100.0);
                        psm.setIntensityPercentage(per);
                        psm.setIntensityColor(psmIntensityColorGenerator.getGradeColor(psm.getIntensity()));
                    }
                    peptideObject.addPSmKey(psm.getKey());
                }
                if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                    inteinsity = inteinsity / (double) psms.size();
                    peptideObject.setIntensity(inteinsity);
                    if (inteinsity > topValue) {
                        topValue = inteinsity;
                    }
                }
            }
            for (String key : peptideObject.getProteinGroupsSet()) {
                if (!peptideToProteinMap.containsKey(key)) {
                    peptideToProteinMap.put(key, new ArrayList<>());
                }
                peptideToProteinMap.get(key).add(peptideObject);
            }
            
        }
        peptideIntensityColorGenerator = new RangeColorGenerator(topValue);
        dataset.setPeptideIntinsityColorScale(peptideIntensityColorGenerator);
        topValue = Double.MIN_VALUE;
        double topValue2 = Double.MIN_VALUE;
        for (ProteinGroupObject proteinObject : dataset.getProteinsMap().values()) {
            proteinObject.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(proteinObject.getAccession()));
            List<PeptideObject> peptides = peptideToProteinMap.get(proteinObject.getProteinGroupKey());
            double allPeptidesInteinsity = 0d;
            double uniquePeptidesInteinsity = 0d;
            double uniquePeptidesNumber = 0d;
            if (peptides != null) {
                for (PeptideObject peptide : peptides) {
                    proteinObject.addPeptide(peptide.getIndex());
                    if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                        peptide.setIntensityColor(peptideIntensityColorGenerator.getGradeColor(peptide.getIntensity()));
                        allPeptidesInteinsity += peptide.getIntensity();
                        if (peptide.getProteinGroupsSet().size() == 1) {
                            uniquePeptidesInteinsity += peptide.getIntensity();
                            uniquePeptidesNumber++;
                        }
                    }
                }
                if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                    allPeptidesInteinsity = allPeptidesInteinsity / (double) peptides.size();
                    uniquePeptidesInteinsity = uniquePeptidesInteinsity / uniquePeptidesNumber;
                    proteinObject.setAllPeptidesIntensity(allPeptidesInteinsity);
                    proteinObject.setUniquePeptidesIntensity(uniquePeptidesInteinsity);
                    if (topValue < allPeptidesInteinsity) {
                        topValue = allPeptidesInteinsity;
                    }
                    if (topValue2 < uniquePeptidesInteinsity) {
                        topValue2 = uniquePeptidesInteinsity;
                    }
                }
            }
        }
        proteinAllIntensityColorGenerator = new RangeColorGenerator(topValue);
        proteinUniqueIntensityColorGenerator = new RangeColorGenerator(topValue2);
        if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
            TreeMap<Comparable, Set<Integer>> proteinsAllPeptidesIntensityMap = new TreeMap<>();
            TreeMap<Comparable, Set<Integer>> proteinsUniquePeptidesIntensityMap = new TreeMap<>();
            for (ProteinGroupObject protein : dataset.getProteinsMap().values()) {
                
                protein.setAllPeptideIintensityColor(proteinAllIntensityColorGenerator.getGradeColor(protein.getAllPeptidesIntensity()));
                protein.setUniquePeptideIintensityColor(proteinUniqueIntensityColorGenerator.getGradeColor(protein.getUniquePeptidesIntensity()));
                int per = (int) Math.round((protein.getAllPeptidesIntensity() / proteinAllIntensityColorGenerator.getMax()) * 100.0);
                protein.setPercentageAllPeptidesIntensity(per);
                if (!proteinsAllPeptidesIntensityMap.containsKey(per)) {
                    proteinsAllPeptidesIntensityMap.put(per, new HashSet<>());
                }
                proteinsAllPeptidesIntensityMap.get(per).add(protein.getIndex());
                per = (int) Math.round((protein.getUniquePeptidesIntensity() / proteinUniqueIntensityColorGenerator.getMax()) * 100.0);
                protein.setPercentageAllPeptidesIntensity(per);
                if (!proteinsUniquePeptidesIntensityMap.containsKey(per)) {
                    proteinsUniquePeptidesIntensityMap.put(per, new HashSet<>());
                }
                proteinsUniquePeptidesIntensityMap.get(per).add(protein.getIndex());
                
            }
            dataset.setAllPeptideIntensityMap(proteinsAllPeptidesIntensityMap);
            dataset.setUniquePeptideIntensityMap(proteinsUniquePeptidesIntensityMap);
            
        }
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            String fastaFileName = dataset.getPsZipFile().getId() + "_FASTA_.txt";
            File fastaFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fastaFileName);
            if (!fastaFile.exists()) {
                try {
                    fastaFile.createNewFile();
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            }
            appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), ".fasta", fastaFile);
            dataset.setProteinSequenceMap(processFastaFile(fastaFile));
            dataset.getProteinsMap().values().stream().map((protein) -> {
                String[] descArr = dataset.getProteinSequenceMap().get(protein.getAccession()).getDescription().split("\\s");
                protein.setDescription(descArr[0].replace("OS", "").trim());
                protein.setProteinEvidence(CONSTANT.PROTEIN_EVIDENCE[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
                return protein;
            }).forEachOrdered((protein) -> {
                protein.setSequence(dataset.getProteinSequenceMap().get(protein.getAccession()).getSequenceAsString());
            });
            String proteoformsFileName = dataset.getPsZipFile().getId() + "_proteoforms_.txt";
            File proteoformsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), proteoformsFileName);
            if (!proteoformsFile.exists()) {
                try {
                    proteoformsFile.createNewFile();
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            }
            appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), "proteins_proteoforms", proteoformsFile);
            dataset.setProteoformsMap(processProteoformFile(proteoformsFile));
            
        });
        executorService.shutdown();

//        psmsMap.keySet().forEach((modSeq) -> {
//            PeptideObject peptide = peptidesMap.get(modSeq);
//            double quant = 0d;
//            quant = psmsMap.get(modSeq).stream().map((psm) -> psm.getIntensity()).reduce(quant, (accumulator, _item) -> accumulator + _item);
//            if (quant > 0) {
//                double finalquant = quant / (double) psmsMap.get(modSeq).size();
//                peptide.setIntensity(finalquant);
//            }
//        });
//        //calc outliers
//        colorGenerator = new RangeColorGenerator(topIntensityValue);
//        psmsMap.keySet().forEach((modSeq) -> {
//            PeptideObject peptide = peptidesMap.get(modSeq);
//            peptide.setIntensityColor(colorGenerator.getGradeColor(peptide.getIntensity(), topIntensityValue, intensitySet.first()));
//        });
//    }
//    //calc quant for proteins
//    double quant = 0.0;
//    proteinIntensityValuesSet  = new TreeSet<>();
//    double quant2 = 0.0;
//    TreeSet<Double> treeSet3 = new TreeSet<>();
//    for (String protGroupKey
//
//    : proteinsGroupMap.keySet () 
//        ) {
//                ProteinGroupObject proteinGroup = proteinsGroupMap.get(protGroupKey);
//        double counter = 0.0;
//        double counter2 = 0.0;
//        for (String modSeq : proteinGroup.getRelatedPeptidesList()) {
//            if (peptidesMap.get(modSeq).getIntensity() > 0) {
//                quant += peptidesMap.get(modSeq).getIntensity();
//                counter++;
//                if (!peptidesMap.get(modSeq).getProteinGroupKey().contains("-_-")) {
//                    quant2 += peptidesMap.get(modSeq).getIntensity();
//                    counter2++;
//                }
//            }
//        }
//        if (counter > 0) {
//            quant = quant / counter;
//            proteinGroup.setAllPeptidesIntensity(quant);
//            proteinIntensityValuesSet.add(quant);
//        }
//        if (counter2 > 0) {
//            quant2 = quant2 / counter2;
//            proteinGroup.setUniquePeptidesIntensity(quant2);
//            treeSet3.add(quant2);
//        }
//    }
//
//    proteinsGroupMap.values () 
//        .stream().map((proteinGroup) -> {
//                proteinGroup.setAllPeptideIintensityColor(colorGenerator.getGradeColor(proteinGroup.getAllPeptidesIntensity(), topIntensityValue, intensitySet.first()));
//
//        int per = (int) Math.round((proteinGroup.getAllPeptidesIntensity() / proteinIntensityValuesSet.last()) * 100.0);
//        proteinGroup.setPercentageAllPeptidesIntensity(per);
//        if (!this.proteinIntensityAllPeptideMap.containsKey(per)) {
//            this.proteinIntensityAllPeptideMap.put(per, new LinkedHashSet<>());
//        }
//        this.proteinIntensityAllPeptideMap.get(per).add(proteinGroup.getProteinGroupKey());
//        return proteinGroup;
//    }
//
//    ).forEachOrdered(
//             
//        (proteinGroup) -> {
//                proteinGroup.setUniquePeptideIintensityColor(colorGenerator.getGradeColor(proteinGroup.getUniquePeptidesIntensity(), topIntensityValue, intensitySet.first()));
//        int per = (int) Math.round((proteinGroup.getUniquePeptidesIntensity() / treeSet3.last()) * 100.0);
//        proteinGroup.setPercentageUniquePeptidesIntensity(per);
//        if (!this.proteinIntensityUniquePeptideMap.containsKey(per)) {
//            this.proteinIntensityUniquePeptideMap.put(per, new LinkedHashSet<>());
//        }
//        this.proteinIntensityUniquePeptideMap.get(per).add(proteinGroup.getProteinGroupKey());
//    }
//
//
//);
    }
//
//    /**
//     * Get protein object from the protein list
//     *
//     * @param proteinKey (accession)
//     * @return protein object
//     */
//    public ProteinGroupObject getProtein(String proteinKey) {
//        checkAndUpdateProtein(proteinKey);
//        if (processProteinsTask.getProteinsMap().containsKey(proteinKey)) {
//            return processProteinsTask.getProteinsMap().get(proteinKey);
//        } else if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
//            return processFastaFileTask.getFastaProteinMap().get(proteinKey);
//        } else {
//            ProteinGroupObject newRelatedProt = updateProteinInformation(null, proteinKey);
//            return newRelatedProt;
//        }
//
//    }
//
//    /**
//     * Update protein information
//     *
//     * @param id protein ID (accession)
//     */
//    private void checkAndUpdateProtein(String id) {
//        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() != null) {
//            return;
//        }
//        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() == null) {
//            completeProteinInformation(processProteinsTask.getProteinsMap().get(id));
//        } else if (!processFastaFileTask.getFastaProteinMap().containsKey(id) && processFastaFileTask.getFastaProteinSequenceMap().containsKey(id)) {
//            initialiseFromFastaFile(id);
//        }
//
//    }
//
//    /**
//     * Add missing information to protein object
//     *
//     * @param protein object to be updated
//     */
//    public void completeProteinInformation(ProteinGroupObject protein) {
//        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
//        String protDesc = entry.getDescription().split("OS")[0];
//        String[] descArr = entry.getDescription().split("\\s");
//        protein.setDescription(protDesc.replace(descArr[0], "").trim());
//        protein.setSequence(entry.getSequenceAsString());
//        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
//        processFastaFileTask.getFastaProteinMap().put(protein.getAccession(), protein);
//
//    }
//
//    /**
//     * Initialise protein object from the provided FASTA file
//     *
//     * @param proteinkey protein accession used as a key
//     */
//    private void initialiseFromFastaFile(String proteinkey) {
//        ProteinGroupObject protein = new ProteinGroupObject();
//        protein.setAccession(proteinkey);
//        protein.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(protein.getAccession().trim()));
//        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
//        String[] descArr = entry.getDescription().split("\\s");
//        protein.setDescription(descArr[0].replace("OS", "").trim());
//        protein.setSequence(entry.getSequenceAsString());
//        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
//        processFastaFileTask.getFastaProteinMap().put(proteinkey, protein);
//
//    }

    private LinkedHashMap<String, ProteinSequence> processFastaFile(File fastaFile) {
        FileInputStream inStream;
        try {
            inStream = new FileInputStream(fastaFile);
            FastaReader<ProteinSequence, AminoAcidCompound> fastaReader
                    = new FastaReader<>(
                            inStream,
                            new GenericFastaHeaderParser<>(),
                            new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
            return fastaReader.process();
            
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
        
    }
    
    public ProteinGroupObject createSubGroupProtein(VisualizationDatasetModel dataset, String accession) {
        ProteinGroupObject proteinGroup = new ProteinGroupObject();
        proteinGroup.setAccession(accession);
        proteinGroup.setValidation(CONSTANT.NO_INFORMATION);
        proteinGroup.setPSMsNumber(0);
        if (dataset.getProteinSequenceMap().containsKey(accession)) {
            ProteinSequence sequence = dataset.getProteinSequenceMap().get(accession);
            String[] descArr = sequence.getDescription().split("\\s");
            proteinGroup.setDescription(descArr[0].replace("OS", "").trim());
            proteinGroup.setProteinEvidence(CONSTANT.PROTEIN_EVIDENCE[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
            proteinGroup.setSequence(sequence.getSequenceAsString());
        }
        
        return proteinGroup;
        
    }
    
    private int maxPSMsNumber;
    
    public Object[] initialiseProteinGraphData(String datasetId, int selectedProteinIndex) {
        VisualizationDatasetModel dataset = appManagmentBean.getUserHandler().getDataset(datasetId);
        ProteinGroupObject selectedProteinObject = dataset.getProteinsMap().get(selectedProteinIndex);
        Map<String, ProteinGroupObject> proteinNodes = new LinkedHashMap<>();
        Map<String, PeptideObject> peptidesNodes = new LinkedHashMap<>();
        HashMap<String, Set<String>> edges = new HashMap<>();
        maxPSMsNumber = 0;
        index = 0;
        populateGraphProteinList(selectedProteinObject, dataset, proteinNodes, peptidesNodes, edges);
        edges.keySet().stream().filter((pepkey) -> (edges.get(pepkey).size() == 1 && peptidesNodes.get(pepkey).getValidation().equals(CONSTANT.VALIDATION_CONFIDENT))).forEachOrdered((pepkey) -> {
            proteinNodes.get(edges.get(pepkey).iterator().next()).setValidation(CONSTANT.VALIDATION_CONFIDENT);
        });
        RangeColorGenerator psmsColorScale = new RangeColorGenerator(maxPSMsNumber);
        RangeColorGenerator intinsityColorScale = dataset.getPeptideIntinsityColorScale();
        return new Object[]{selectedProteinObject, proteinNodes, peptidesNodes, edges, psmsColorScale, intinsityColorScale};
        
    }
    int index = 0;
    
    private void populateGraphProteinList(ProteinGroupObject selectedProteinObject, VisualizationDatasetModel dataset, Map<String, ProteinGroupObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, HashMap<String, Set<String>> edges) {
        
        if (proteinNodes.keySet().containsAll(selectedProteinObject.getProteinGroupSet())) {
            return;
        }
        boolean enzymaticDigestion = false;
        if (dataset.getDatasetSource().equals(CONSTANT.GALAXY_SOURCE)) {
            DigestionParameters digestionParameters = dataset.getIdentificationParametersObject().getSearchParameters().getDigestionParameters();
            enzymaticDigestion = (digestionParameters.getCleavageParameter() == DigestionParameters.CleavageParameter.enzyme);
        }
        Map<String, ProteinGroupObject> newAccessionList = new HashMap<>();
        for (String proteinAcc : selectedProteinObject.getProteinGroupSet()) {
            if (dataset.getAccessionToGroupMap().containsKey(proteinAcc)) {
                for (int proteinGroupIndex : dataset.getAccessionToGroupMap().get(proteinAcc)) {
                    ProteinGroupObject oreginalProteinObject = dataset.getProteinsMap().get(proteinGroupIndex);
                    if (oreginalProteinObject.getAccession().equalsIgnoreCase(proteinAcc) && oreginalProteinObject.getProteinInference().equals("Single")) {
                        newAccessionList.put(proteinAcc + "_" + oreginalProteinObject.getProteinGroupKey(), oreginalProteinObject);
                    } else {
                        ProteinGroupObject newProteinObject = createSubGroupProtein(dataset, proteinAcc);
                        newProteinObject.setOreginalProteinGroup(oreginalProteinObject.getOreginalProteinGroup());
                        newProteinObject.getPeptidesSet().addAll(oreginalProteinObject.getPeptidesSet());
                        newAccessionList.put(proteinAcc + "_" + oreginalProteinObject.getProteinGroupKey(), newProteinObject);
                    }
                }
                
            }
        }
        Set<PeptideObject> newPeptides = new HashSet<>();
        for (ProteinGroupObject newProteinObject : newAccessionList.values()) {
            for (int peptideIndex : newProteinObject.getPeptidesSet()) {
                PeptideObject peptide = dataset.getPeptidesMap().get(peptideIndex);
                maxPSMsNumber = Math.max(maxPSMsNumber, peptide.getPSMsNumber());
                if (!edges.containsKey(peptide.getModifiedSequence())) {
                    edges.put(peptide.getModifiedSequence(), new HashSet<>());
                }
                
                edges.get(peptide.getModifiedSequence()).add(newProteinObject.getAccession());
                peptidesNodes.put(peptide.getModifiedSequence(), peptide);
                proteinNodes.put(newProteinObject.getAccession(), newProteinObject);
                newPeptides.add(peptide);
                if (!newProteinObject.getRelatedPeptidesList().contains(peptide.getModifiedSequence()) && enzymaticDigestion) {
                    if (enzymaticDigestion) {
                        newProteinObject.addPeptideType(peptide.getModifiedSequence(), isEnzymatic(newProteinObject.getSequence(), peptide.getSequence(), dataset.getIdentificationParametersObject().getSearchParameters().getDigestionParameters().getEnzymes(), dataset.getIdentificationParametersObject().getSequenceMatchingParameters()));
                    } else {
                        newProteinObject.addPeptideType(peptide.getModifiedSequence(), true);
                    }
                }
            }
        }
        for (PeptideObject peptide : newPeptides) {
            for (String proteinGropAcc : peptide.getProteinsSet()) {
                if (proteinNodes.containsKey(proteinGropAcc)) {
                    edges.get(peptide.getModifiedSequence()).add(proteinGropAcc);
                    if (!proteinNodes.get(proteinGropAcc).getRelatedPeptidesList().contains(peptide.getModifiedSequence())) {
                        if (enzymaticDigestion) {
                            proteinNodes.get(proteinGropAcc).addPeptideType(peptide.getModifiedSequence(), isEnzymatic(proteinNodes.get(proteinGropAcc).getSequence(), peptide.getSequence(), dataset.getIdentificationParametersObject().getSearchParameters().getDigestionParameters().getEnzymes(), dataset.getIdentificationParametersObject().getSequenceMatchingParameters()));
                        } else {
                            proteinNodes.get(proteinGropAcc).addPeptideType(peptide.getModifiedSequence(), true);
                        }
                        
                    }
                    continue;
                }
                if (dataset.getAccessionToGroupMap().containsKey(proteinGropAcc)) {
                    for (int proteinGroupIndex : dataset.getAccessionToGroupMap().get(proteinGropAcc)) {
                        ProteinGroupObject oreginalProteinObject = dataset.getProteinsMap().get(proteinGroupIndex);
                        if (oreginalProteinObject.getAccession().equalsIgnoreCase(proteinGropAcc)) {
                            populateGraphProteinList(oreginalProteinObject, dataset, proteinNodes, peptidesNodes, edges);
                        } else {
                            ProteinGroupObject newProteinObject = createSubGroupProtein(dataset, proteinGropAcc);
                            newProteinObject.setOreginalProteinGroup(oreginalProteinObject.getOreginalProteinGroup());
                            newProteinObject.getPeptidesSet().addAll(oreginalProteinObject.getPeptidesSet());
                            populateGraphProteinList(newProteinObject, dataset, proteinNodes, peptidesNodes, edges);
                        }
                    }
                } else {
                    
                    ProteinGroupObject newProteinObject = createSubGroupProtein(dataset, proteinGropAcc);
                    newProteinObject.setOreginalProteinGroup(proteinGropAcc);
                    newProteinObject.getPeptidesSet().add(peptide.getIndex());
                    proteinNodes.put(newProteinObject.getAccession(), newProteinObject);
                    if (!edges.containsKey(peptide.getModifiedSequence())) {
                        edges.put(peptide.getModifiedSequence(), new HashSet<>());
                    }
                    edges.get(peptide.getModifiedSequence()).add(newProteinObject.getAccession());
                    if (!newProteinObject.getRelatedPeptidesList().contains(peptide.getModifiedSequence())) {
                        if (enzymaticDigestion) {
                            newProteinObject.addPeptideType(peptide.getModifiedSequence(), isEnzymatic(newProteinObject.getSequence(), peptide.getSequence(), dataset.getIdentificationParametersObject().getSearchParameters().getDigestionParameters().getEnzymes(), dataset.getIdentificationParametersObject().getSequenceMatchingParameters()));
                        } else {
                            newProteinObject.addPeptideType(peptide.getModifiedSequence(), true);
                        }
                        
                    }
                }
                
            }
        }
        
    }

    /**
     * Returns the list of indexes where a peptide can be found in the protein
     * sequence. 0 is the first amino acid.
     *
     * @param peptideSequence the sequence of the peptide of interest
     * @param sequenceMatchingPreferences the sequence matching preferences
     * @return the list of indexes where a peptide can be found in a protein
     * sequence
     */
    private int[] getPeptideStart(String sequence, String peptideSequence, SequenceMatchingParameters sequenceMatchingPreferences) {
        AminoAcidPattern aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString(peptideSequence);
        ArrayList<Integer> result = new ArrayList<>(1);
        int index = 0;
        while ((index = aminoAcidPattern.firstIndex(sequence, sequenceMatchingPreferences, index)) >= 0) {
            result.add(index);
            index++;
        }
        return result.stream().mapToInt(a -> a).toArray();
    }

    /**
     * Returns a boolean indicating whether the peptide is enzymatic using one
     * of the given enzymes.
     *
     * @param peptide the peptide
     * @param proteinAccession the accession of the protein
     * @param proteinSequence the sequence of the protein
     * @param enzymes the enzymes used for digestion
     *
     * @return a boolean indicating whether the peptide is enzymatic using one
     * of the given enzymes
     */
    private boolean isEnzymatic(String proteinSequence, String peptideSequence, List<Enzyme> enzymes, SequenceMatchingParameters sequenceMatchingPreferences) {
        proteinSequence = proteinSequence.replace("I", "L");
        peptideSequence = peptideSequence.replace("I", "L");
        int[] startIndexes = getPeptideStart(proteinSequence, peptideSequence, sequenceMatchingPreferences);
        if (startIndexes == null) {
            return false;
        }
        for (Enzyme enzyme : enzymes) {
            for (int peptideStart : startIndexes) {
                int peptideEnd = peptideStart + peptideSequence.length() - 1;
                int nTermini = PeptideUtils.getNEnzymaticTermini(peptideStart, peptideEnd, proteinSequence, enzyme);
                if (nTermini == 2) {
                    return true;
                }
            }
        }
        return false;
        
    }

    /**
     * Constructor to initialise the main variables.
     *
     * @param proteoform_file input proteoform file
     */
    private Map<String, Map<String, NetworkGraphNode>> processProteoformFile(File proteoform_file) {
        
        Map<String, Map<String, NetworkGraphNode>> nodes = new HashMap<>();
        List<String> readerSet = new ArrayList<>();
        String line;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(proteoform_file), "UTF-8");
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(is)) {
                while ((line = bufferedReader.readLine()) != null) {
                    line += ";";
                    readerSet.add(line);
                }
                readerSet.stream().map((parsline) -> parsline.trim()).forEachOrdered((proteoformId) -> {
                    
                    String accession = proteoformId.split(";")[0];
                    if (!nodes.containsKey(accession)) {
                        nodes.put(accession, new LinkedHashMap<>());
                    }
                    NetworkGraphNode node;
                    if (!nodes.get(accession).containsKey(proteoformId)) {
                        node = new NetworkGraphNode(proteoformId, true, false) {
                            @Override
                            public void selected(String id) {
                                
                            }
                        };
                        node.setType(3);
                        nodes.get(accession).put(proteoformId, node);
                    }
                });
                is.close();
                bufferedReader.close();
                // Always close files.
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Error :  FileNotFoundException " + ex);
        } catch (IOException ex) {
            System.out.println("Error :  IOException " + ex);
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
        }
        return nodes;
    }
    
    public Set<NetworkGraphEdge> getProteoformsNetworkEdges(VisualizationDatasetModel dataset, String proteinAcc, ProteinGroupObject selectedProtein) {
        
        if (selectedProtein.isProteoformUpdated()) {
            return selectedProtein.getFullEdgesSet();
        }
        
        Set<NetworkGraphEdge> edges = new HashSet<>();
        Map<String, NetworkGraphNode> tNodes = new HashMap<>();
        Map<String, NetworkGraphNode> accMap = dataset.getProteoformsMap().get(proteinAcc);
        NetworkGraphNode parentNode = new NetworkGraphNode(proteinAcc, true, true) {
            @Override
            public void selected(String id) {
                System.out.println("at selected parent node  id " + id);
            }
            
        };
        selectedProtein.setParentNode(parentNode);
        if (accMap == null) {
            NetworkGraphNode singleNode = new NetworkGraphNode(proteinAcc + ";", true, false) {
                @Override
                public void selected(String id) {
                    System.out.println("at selected single id " + id);
                }
                
            };
            singleNode.setType(3);
            singleNode.setParentNode(parentNode);
            selectedProtein.addProteoformNode(singleNode);
            tNodes.put(singleNode.getNodeId(), singleNode);
        } else {
            accMap.values().stream().map((n) -> {
                n.setParentNode(parentNode);
                return n;
            }).forEachOrdered((n) -> {
                selectedProtein.addProteoformNode(n);
                tNodes.put(n.getNodeId(), n);
            });
            
        }
        
        selectedProtein.setProteoformUpdated(true);
        edges.addAll(selectedProtein.getLocalEdges());

        //get all edges
        Set<String[]> edgesData = appManagmentBean.getDatabaseUtils().getPathwayEdges(new String[]{proteinAcc});
//        Set<NetworkGraphEdge> edges = new HashSet<>();

        edgesData.stream().map((String[] arr) -> {
            arr[0] = arr[0] + ";";
            arr[1] = arr[1] + ";";
            ProteinGroupObject p1 = null;
            ProteinGroupObject p2 = null;
            if (proteinAcc.equals(arr[0])) {
                p1 = selectedProtein;
            }
            if (proteinAcc.equals(arr[1])) {
                p2 = selectedProtein;
            }

//            if(p1==null || p2 ==null)
            NetworkGraphNode n1;
            NetworkGraphNode n2;
            n2 = null;
            if ((p1 == null || !(p1.getProteoformsNodes().containsKey(arr[0].trim()))) && !tNodes.containsKey(arr[0].trim())) {
                n1 = new NetworkGraphNode(arr[0], false, false) {
                    @Override
                    public void selected(String id) {
                        System.out.println("at selected node 2 " + id);
                    }
                };
                tNodes.put(n1.getNodeId(), n1);
                if (!tNodes.containsKey(n1.getAccession())) {
                    NetworkGraphNode newParentNode = new NetworkGraphNode(n1.getAccession(), false, true) {
                        @Override
                        public void selected(String id) {
                            System.out.println("at selected parent node  id " + id);
                        }
                    };
                    n1.setParentNode(newParentNode);
                    tNodes.put(n1.getAccession(), newParentNode);
                }
            } else if (tNodes.containsKey(arr[0])) {
                n1 = tNodes.get(arr[0]);
            } else {
                n1 = p1.getProteoformsNodes().get(arr[0]);
            }
            if ((p2 == null || !(p2.getProteoformsNodes().containsKey(arr[1]))) && !tNodes.containsKey(arr[1])) {
                n2 = new NetworkGraphNode(arr[1], false, false) {
                    @Override
                    public void selected(String id) {
                        System.out.println("at selected node 2 " + id);
                    }
                };
                tNodes.put(n2.getNodeId(), n2);
                if (!tNodes.containsKey(n2.getAccession())) {
                    NetworkGraphNode NewSecParentNode = new NetworkGraphNode(n2.getAccession(), false, true) {
                        @Override
                        public void selected(String id) {
                            System.out.println("at selected parent node  id " + id);
                        }
                        
                    };
                    tNodes.put(n2.getAccession(), NewSecParentNode);
                    
                }
                n2.setParentNode(tNodes.get(n2.getAccession()));
            } else if (tNodes.containsKey(arr[1])) {
                n2 = tNodes.get(arr[1]);
                
            } else if (p2 != null && p2.getProteoformsNodes() != null) {
                n2 = p2.getProteoformsNodes().get(arr[1]);
            }
            NetworkGraphEdge edge;
            edge = null;
            if (n2 != null) {
                edge = new NetworkGraphEdge(n1, n2, false);
                n1.addEdge(edge);
                n2.addEdge(edge);
            }
            
            return edge;
        }).forEachOrdered((edge) -> {
            edges.add(edge);
        });
        selectedProtein.setFullEdgesSet(edges);
        return edges;
    }
    
    public Map<Object, SpectrumInformation> getSelectedSpectrumData(List<PSMObject> PSMs, PeptideObject peptideObject, VisualizationDatasetModel dataset, String userAPIKey) {//SpectrumPlot plot

        Map<Object, SpectrumInformation> spectrumInformationMap = new LinkedHashMap<>();
        int maxCharge = (-1 * Integer.MAX_VALUE);
        double maxError = (-1.0 * Double.MAX_VALUE);
        for (PSMObject selectedPsm : PSMs) {
//            try {
//                if (!dataset.getImportedMgfIndexObjectMap().containsKey(selectedPsm.getSpectrumFile())) {
//                    Object object = SerializationUtils.readObject(cuiFileMap.get(selectedPsm.getSpectrumFile().replace(".thermo", "")));
//                    importedMgfIndexObjectMap.put(selectedPsm.getSpectrumFile(), (MgfIndex) object);
//                }
//
//            } catch (IOException | ClassNotFoundException ex) {
//                ex.printStackTrace();
//            }

            MgfIndex mgfIndex = dataset.getImportedMgfIndexObjectMap().get(selectedPsm.getSpectrumFile());
            String indexedMgfFileId = "";
            String indexedMgfFileHistoryId = "";
            for (GalaxyFileModel mgfFile : dataset.getMgfList().getElements()) {
                if (mgfFile.getName().equalsIgnoreCase(selectedPsm.getSpectrumFile().replace(".thermo", ""))) {
                    indexedMgfFileId = mgfFile.getId();
                    indexedMgfFileHistoryId = mgfFile.getHistoryId();
                    break;
                }
            }
            if (mgfIndex == null) {
                return null;
            }
            Spectrum spectrum = appManagmentBean.getGalaxyFacad().streamSpectrum(mgfIndex.getIndex(selectedPsm.getSpectrumTitle()), indexedMgfFileHistoryId, indexedMgfFileId, selectedPsm.getSpectrumFile().replace(".thermo", ""), Integer.parseInt(selectedPsm.getIdentificationCharge().replace("+", "")), userAPIKey);
            int tCharge = 0;
            if (!selectedPsm.getMeasuredCharge().trim().equalsIgnoreCase("")) {
                try {
                    tCharge = Integer.parseInt(selectedPsm.getMeasuredCharge().replace("+", ""));
                } catch (NumberFormatException nfx) {
                    tCharge = (int) Double.parseDouble(selectedPsm.getMeasuredCharge().replace("+", ""));
                }
            }
            
            if (tCharge > maxCharge) {
                maxCharge = tCharge;
            }
            if (selectedPsm.getPrecursorMZError_PPM() > maxError) {
                maxError = selectedPsm.getPrecursorMZError_PPM();
            }
            
            ArrayList<ModificationMatch> psModificationMatches = null;
            if (peptideObject.isModified()) {
                psModificationMatches = new ArrayList<>(peptideObject.getVariableModifications().length);
                for (ModificationMatch seModMatch : peptideObject.getVariableModifications()) {
                    psModificationMatches.add(seModMatch);
                }
            }
            
            Peptide psPeptide = new Peptide(peptideObject.getSequence(), peptideObject.getVariableModifications());
            PeptideAssumption psAssumption = new PeptideAssumption(psPeptide, tCharge);
            SpectrumMatch spectrumMatch = new SpectrumMatch(selectedPsm.getSpectrumFile(), selectedPsm.getSpectrumTitle());
            spectrumMatch.setBestPeptideAssumption(psAssumption);
            SpectrumInformation spectrumInformation = new SpectrumInformation();
            spectrumInformation.setCharge(Charge.getChargeAsFormattedString(tCharge));
            spectrumInformation.setFragmentIonAccuracy(dataset.getIdentificationParametersObject().getSearchParameters().getFragmentIonAccuracy());
            spectrumInformation.setIdentificationParameters(dataset.getIdentificationParametersObject());
            spectrumInformation.setSpectrumMatch(spectrumMatch);
            spectrumInformation.setSpectrumId(selectedPsm.getIndex());
            spectrumInformation.setSpectrum(spectrum);
            spectrumInformationMap.put(selectedPsm.getIndex(), spectrumInformation);
        }
        for (SpectrumInformation spectrumInformation : spectrumInformationMap.values()) {
            spectrumInformation.setMaxCharge(maxCharge);
            spectrumInformation.setMzError(maxError);
        }
        return spectrumInformationMap;
        
    }
    
}
