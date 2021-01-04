package com.uib.web.peptideshaker.utils;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PSMObject;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.TableHeaderConstatnts;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
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

    public DatasetUtils() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        table_headers = new TableHeaderConstatnts();
    }

    public IdentificationParameters initIdentificationParametersObject(String datasetId, String initIdentificationParametersFileURI) {
        String fileName = datasetId + "_SEARCHGUI_IdentificationParameters.par";
        File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                file = appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(initIdentificationParametersFileURI, 1, file);
            }
            return IdentificationParameters.getIdentificationParameters(file);
        } catch (IOException ex) {
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
     * @param csf_pr_Accession_List
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
        Set<String> csf_pr_Accession_List;
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
        appManagmentBean.getUI_Manager().viewLayout(FileSystemView.class.getName());

        return dataset;

    }

    public void processDatasetProteins(VisualizationDatasetModel dataset) {
        switch (dataset.getDatasetSource()) {
            case CONSTANT.GALAXY_SOURCE:
                String fileName = dataset.getPsZipFile().getId() + "_proteins_.txt";
                File proteinsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);

                fileName = dataset.getPsZipFile().getId() + "_Peptides_.txt";
                File peptidesFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);

                fileName = dataset.getPsZipFile().getId() + "_PSMs_.txt";
                File psmsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
//
//                fileName = dataset.getPsZipFile().getId() + "_proteoforms_.txt";
//                File proteoformsFile = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                try {
                    if (!proteinsFile.exists()) {
                        proteinsFile.createNewFile();
                        appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), 1, proteinsFile);
                    }
                    dataset.setProteinsMap(processGalaxyProteinsFile(proteinsFile));
                    if (!peptidesFile.exists()) {
                        peptidesFile.createNewFile();
                        appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), 2, peptidesFile);
                    }
                    dataset.setPeptidesMap(processGalaxyPeptidesFile(peptidesFile));

                    if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                        if (!psmsFile.exists()) {
                            psmsFile.createNewFile();
                            appManagmentBean.getHttpClientUtil().downloadFileFromZipFolder(dataset.getPsZipFile().getDownloadUrl(), 3, psmsFile);
                        }
                        dataset.setPsmsMap(processGalaxyPsmsFile(psmsFile));
                        for (GalaxyFileModel moffFile : dataset.getMoffList().getElements()) {
                            fileName = moffFile.getId() + "_moff_.tabular";
                            File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
                            if (!file.exists()) {
                                file.createNewFile();
                                appManagmentBean.getHttpClientUtil().downloadFile(moffFile.getDownloadUrl(), file);
                            }
                            processGalaxyMoFFFile(file, dataset.getPsmsMap());
                        }
                    }

                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
                break;
            case CONSTANT.USER_UPLOAD_SOURCE:
                break;
            case CONSTANT.PRIDE_SOURCE:
                break;
        }
        initialiseDatasetFilterMaps(dataset);

    }

    private Map<Integer, ProteinGroupObject> processGalaxyProteinsFile(File file) {

//        proteinsMap = new LinkedHashMap<>();
//            this.proteinInferenceMap = new LinkedHashMap<>();
//            this.proteinInferenceMap.put("No Information", new LinkedHashSet<>());
//            this.proteinValidationMap = new LinkedHashMap<>();
//            this.proteinValidationMap.put("No Information", new LinkedHashSet<>());
//            this.proteinValidationMap.put("Confident", new LinkedHashSet<>());
//            this.proteinValidationMap.put("Doubtful", new LinkedHashSet<>());
//            this.chromosomeMap = new LinkedHashMap<>();
//            this.chromosomeMap.put(-2, new LinkedHashSet<>());
//            this.proteinCoverageMap = new TreeMap<>();
//            this.proteinPSMNumberMap = new TreeMap<>();
//            this.protein_ProteinGroup_Map = new HashMap<>();
//            this.accToGroupKeyMap = new HashMap<>();
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
//                proteinGroup.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(proteinGroup.getAccession().trim()));
                proteinGroup.setProteinGroup(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Group)]);
                proteinGroup.setProteinGroupKey(proteinGroup.getProteinGroup());
                proteinGroup.setDescription(arr[proteinFileHeaderIndexerMap.get(table_headers.Description)]);
                proteinGroup.setChromosome(arr[proteinFileHeaderIndexerMap.get(table_headers.Chromosome)]);
//                int chrIndex = -1;
//                try {
//                    chrIndex = Integer.parseInt(proteinGroup.getChromosome());
//                } catch (NumberFormatException ex) {
//                    if (proteinGroup.getChromosome().contains("HSCHR")) {
//                        chrIndex = Integer.parseInt(proteinGroup.getChromosome().split("HSCHR")[1].split("_")[0].replaceAll("[\\D]", ""));
//                    } else if (proteinGroup.getChromosome().equalsIgnoreCase("X")) {
//                        chrIndex = 23;
//                    } else if (proteinGroup.getChromosome().equalsIgnoreCase("Y")) {
//                        chrIndex = 24;
//                    }
//
//                }
////                    proteinGroup.setQuantValue(generateQuantValue());
//                proteinGroup.setChromosomeIndex(chrIndex);
                if (proteinGroup.getChromosome().trim().isEmpty()) {
                    proteinGroup.setChromosome("No Information");
//                    proteinGroup.setChromosomeIndex(-2);
//                    chromosomeMap.get(proteinGroup.getChromosomeIndex()).add(proteinGroup.getProteinGroupKey());
                }
//                else {
//                    if (!chromosomeMap.containsKey(proteinGroup.getChromosomeIndex())) {
//                        chromosomeMap.put(proteinGroup.getChromosomeIndex(), new LinkedHashSet<>());
//                    }
//                    chromosomeMap.get(proteinGroup.getChromosomeIndex()).add(proteinGroup.getProteinGroupKey());
//                }

                proteinGroup.setValidatedPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Peptides)]));
//                if (proteinGroup.getValidatedPeptidesNumber() > maxPeptideNumber) {
//                    maxPeptideNumber = proteinGroup.getValidatedPeptidesNumber();
//                }
                proteinGroup.setPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Peptides)]));
//                if (!proteinPeptidesNumberMap.containsKey(proteinGroup.getValidatedPeptidesNumber())) {
//                    proteinPeptidesNumberMap.put(proteinGroup.getValidatedPeptidesNumber(), new LinkedHashSet<>());
//                }
//                proteinPeptidesNumberMap.get(proteinGroup.getValidatedPeptidesNumber()).add(proteinGroup.getProteinGroupKey());
                proteinGroup.setValidatedPSMsNumber(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_PSMs)]).intValue());
//                if (proteinGroup.getValidatedPSMsNumber() > maxPsmNumber) {
//                    maxPsmNumber = proteinGroup.getValidatedPSMsNumber();
//                }
                proteinGroup.setPSMsNumber(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_PSMs)]));
//                if (!proteinPSMNumberMap.containsKey(proteinGroup.getValidatedPSMsNumber())) {
//                    proteinPSMNumberMap.put(proteinGroup.getValidatedPSMsNumber(), new LinkedHashSet<>());
//                }
//                proteinPSMNumberMap.get(proteinGroup.getValidatedPSMsNumber()).add(proteinGroup.getProteinGroupKey());
                proteinGroup.setConfidence(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));

                /**
                 * check if it is uploaded project or imported from galaxy
                 */
//                if (uploadedProject) {
//                    proteinGroup.setIndex(i++);
//                    proteinGroup.setProteinInference(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Inference)].replace("0", "Single Protein").replace("1", "Related").replace("2", "Related & Unrelated").replace("3", "Unrelated"));
//
//                    if (proteinGroup.getProteinInference().trim().isEmpty()) {
//                        proteinGroup.setProteinInference("No Information");
//                        proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
//                    } else {
//                        if (!proteinInferenceMap.containsKey(proteinGroup.getProteinInference())) {
//                            proteinInferenceMap.put(proteinGroup.getProteinInference(), new LinkedHashSet<>());
//                        }
//                        proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
//                    }
//                    proteinGroup.setValidation(arr[proteinFileHeaderIndexerMap.get(table_headers.Validation)]);
//                    if (proteinGroup.getValidation().trim().isEmpty() || proteinGroup.getValidation().trim().equalsIgnoreCase("-1")) {
//                        proteinGroup.setValidation("No Information");
//                        proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
//                    } else {
//                        proteinGroup.setValidation(proteinGroup.getValidation().replace("0", "Not Validated").replace("1", "Doubtful").replace("2", "Confident"));
//                        if (!proteinValidationMap.containsKey(proteinGroup.getValidation())) {
//                            proteinValidationMap.put(proteinGroup.getValidation(), new LinkedHashSet<>());
//                        }
//                        proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
//                    }
//                } else {
                proteinGroup.setIndex(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Index)]));
                proteinGroup.setGeneName(arr[proteinFileHeaderIndexerMap.get(table_headers.Gene_Name)]);
                proteinGroup.setMW(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.MW_kDa)]));
//                    if (proteinGroup.getMW() > maxMW) {
//                        maxMW = proteinGroup.getMW();
//                    }
                proteinGroup.setPossibleCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Possible_Coverage_Pers)]));
                proteinGroup.setCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Coverage_Pers)]));
                int pc = (int) Math.round(proteinGroup.getCoverage());
//                    if (!proteinCoverageMap.containsKey(pc)) {
//                        proteinCoverageMap.put(pc, new LinkedHashSet<>());
//                    }
//                    proteinCoverageMap.get(pc).add(proteinGroup.getProteinGroupKey());
                proteinGroup.setSpectrumCounting(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Spectrum_Counting)]));
//                    if (proteinGroup.getSpectrumCounting() > maxMS2Quant) {
//                        maxMS2Quant = proteinGroup.getSpectrumCounting();
//                    }
                if ((arr[proteinFileHeaderIndexerMap.get(table_headers.Confidently_Localized_Modification_Sites)] + "").trim().equalsIgnoreCase("")) {
                    proteinGroup.setConfidentlyLocalizedModificationSites("No Modification");
                } else {
                    proteinGroup.setConfidentlyLocalizedModificationSites(arr[proteinFileHeaderIndexerMap.get(table_headers.Confidently_Localized_Modification_Sites)]);//.split("\\(")[0]);
                }
                proteinGroup.setConfidentlyLocalizedModificationSitesNumber(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Confidently_Localized_Modification_Sites)]);

                proteinGroup.setAmbiguouslyLocalizedModificationSites(arr[proteinFileHeaderIndexerMap.get(table_headers.Ambiguously_Localized_Modification_Sites)]);
                proteinGroup.setAmbiguouslyLocalizedModificationSitesNumber(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Ambiguously_Localized_Modification_Sites)]);
                proteinGroup.setProteinInference(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Inference)].replace("Proteins", "").replace("and", "&"));

                if (proteinGroup.getProteinInference().trim().isEmpty()) {
                    proteinGroup.setProteinInference("No Information");
//                        proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
                }
//                    else {
//                        if (!proteinInferenceMap.containsKey(proteinGroup.getProteinInference())) {
//                            proteinInferenceMap.put(proteinGroup.getProteinInference(), new LinkedHashSet<>());
//                        }
//                        proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
//                    }

                proteinGroup.setSecondaryAccessions(arr[proteinFileHeaderIndexerMap.get(table_headers.Secondary_Accessions)]);
                proteinGroup.setUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Unique_Peptides)]));
                proteinGroup.setValidatedUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Unique_Peptides)]));
                proteinGroup.setValidation(arr[proteinFileHeaderIndexerMap.get(table_headers.Validation)]);
                if (proteinGroup.getValidation().trim().isEmpty()) {
                    proteinGroup.setValidation("No Information");
//                        proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
                }
                proteinsMap.put(proteinGroup.getIndex(), proteinGroup);
//                    else {
//                        if (!proteinValidationMap.containsKey(proteinGroup.getValidation())) {
//                            proteinValidationMap.put(proteinGroup.getValidation(), new LinkedHashSet<>());
//                        }
//                        proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
//                    }
//
//                }

//                if (proteinsMap.containsKey(proteinGroup.getProteinGroupKey())) {
//                    System.out.println("at Error in proteins file key , key repeated : " + proteinGroup.getProteinGroupKey());
//                } else {
//                    proteinsMap.put(proteinGroup.getProteinGroupKey(), proteinGroup);
//                }
//                proteinGroup.getProteinGroupSet().stream().map((acc) -> {
//                    if (!protein_relatedProteins_Map.containsKey(acc)) {
//                        Set<ProteinGroupObject> protenHashSet = new LinkedHashSet<>();
//                        protein_relatedProteins_Map.put(acc, protenHashSet);
//                    }
//
//                    if (!protein_ProteinGroup_Map.containsKey(acc)) {
//                        Set<String> protenHashSet = new LinkedHashSet<>();
//                        protein_ProteinGroup_Map.put(acc, protenHashSet);
//                    }
//                    protein_ProteinGroup_Map.get(acc).add(proteinGroup.getProteinGroupKey());
//                    return acc;
//                }).forEachOrdered((acc) -> {
//                    protein_relatedProteins_Map.get(acc).add(proteinGroup);
//                });
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
//        proteinsMap.keySet().forEach((String key) -> {
//            String[] accs;
//            accs = key.split("-_-");
//            ProteinGroupObject pgo = proteinsMap.get(key);
//            for (String acc : accs) {
//                accToGroupKeyMap.put(acc.trim(), pgo);
//            }
//        });
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
//                psm.setMeasuredCharge((arr[psmMoffFileHeaderIndexerMap.get(table_headers.Measured_Charge)]));
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

//                if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence())) {
//                    processPeptidesTask.getPSMsMap().get(psm.getModifiedSequence()).add(psm);
//                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("L", "I"))) {
//                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I with L ?" + psm.getModifiedSequence());
//                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("I", "L"))) {
//                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
//                } else {
//                    System.out.println("at Error for psm II mapping...not exist peptide " + psm.getModifiedSequence());
//                }
                psmsMap.put(psm.getSpectrumTitle() + "_" + psm.getSpectrumFile(), psm);
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

    private void processGalaxyMoFFFile(File file, Map<String, PSMObject> psmsMap) {
        HashMap<String, Integer> moFFFileHeaderIndexerMap = new HashMap<>();
        moFFFileHeaderIndexerMap.put(table_headers.Intensity, -1);
        moFFFileHeaderIndexerMap.put(table_headers.Spectrum_Title, -1);
        moFFFileHeaderIndexerMap.put(table_headers.Spectrum_File, -1);
        BufferedReader bufferedReader = null;
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
//                String mod_seq = arr[psmMoffFileHeaderIndexerMap.get(table_headers.Mod_peptide)];
                String spectrumFile = arr[moFFFileHeaderIndexerMap.get(table_headers.Spectrum_File)];
                String spectrumTitle = arr[moFFFileHeaderIndexerMap.get(table_headers.Spectrum_Title)];
                double intensity = Double.parseDouble(arr[moFFFileHeaderIndexerMap.get(table_headers.Intensity)]);
                String key = spectrumTitle + "_" + spectrumFile;
                if (psmsMap.containsKey(key)) {
                    psmsMap.get(key).setIntensity(intensity);
                } else {
                    System.out.println("at inteinsity of non existed " + key + "   " + intensity);
                }
            }
//

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
    }

    private void initialiseDatasetFilterMaps(VisualizationDatasetModel dataset) {
        Map<String, Set<Integer>> proteinInferenceMap = new LinkedHashMap<>();
        Map<String, Set<Integer>> proteinValidationMap = new LinkedHashMap<>();
        proteinValidationMap.put("No Information", new LinkedHashSet<>());
        Map<String, Set<Integer>> chromosomeMap = new LinkedHashMap<>();
        TreeMap<Integer, Set<Integer>> validatedPetideMap = new TreeMap<>();
        TreeMap<Integer, Set<Integer>> validatedPsmsMap = new TreeMap<>();
        TreeMap<Integer, Set<Integer>> validatedCoverageMap = new TreeMap<>();
        Map<String, Set<Integer>> accessionToGroupMap = new TreeMap<>();
        Map<String, Set<Integer>> modificationMap = new LinkedHashMap<>();
        modificationMap.put("No Modification", new LinkedHashSet<>(dataset.getProteinsMap().keySet()));
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
            if (!chromosomeMap.containsKey(protein.getChromosome())) {
                chromosomeMap.put(protein.getChromosome(), new LinkedHashSet<>());
            }
            chromosomeMap.get(protein.getChromosome()).add(protein.getIndex());

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
        dataset.setModificationMap(modificationMap);
        dataset.setModificationMatrixModel(appManagmentBean.getModificationMatrixUtilis().generateMatrixModel(modificationMap));

    }

}
