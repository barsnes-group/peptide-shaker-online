package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

import com.uib.web.peptideshaker.galaxy.utilities.history.FastaFileWebService;
import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidPattern;
import com.compomics.util.experiment.biology.enzymes.Enzyme;
import com.compomics.util.experiment.biology.enzymes.EnzymeFactory;
import com.compomics.util.experiment.biology.ions.Charge;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfIndex;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.compomics.util.io.file.SerializationUtils;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.GalaxyDatasetServingUtil;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.utilities.TableHeaderConstatnts;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.pscomponents.SpectrumInformation;
import elemental.json.JsonArray;
import elemental.json.impl.JreJsonArray;
import graphmatcher.NetworkGraphEdge;
import graphmatcher.NetworkGraphNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections15.map.LinkedMap;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents dataset visualisation that store data for viewing files
 * on web
 *
 * @author Yehia Farag
 */
public abstract class PeptideShakerVisualizationDataset extends GalaxyFileObject implements Comparable<PeptideShakerVisualizationDataset> {

    /**
     * Check if the project uploaded or imported from Galaxy
     *
     * @return project is uploaded
     */
    public boolean isUploadedProject() {
        return uploadedProject;
    }

    /**
     * Dataset (Project) name.
     */
    private final String projectName;
    /**
     * User data files folder.
     */
    private final File user_folder;
    /**
     * Galaxy Server web address.
     */
    private final String galaxyLink;
    /**
     * Galaxy server user API key.
     */
    private final String apiKey;
    /**
     * SearchGUI results file.
     */
    private GalaxyFileObject SearchGUIResultFile;
    /**
     * MGF input files list.
     */
    private final Map<String, GalaxyFileObject> indexedMGFFilesMap;
    private String indexedMgfGalaxyId;
    /**
     * PeptideShaker results file (compressed folder) ID on Galaxy Server.
     */
    private String PeptideShakerResultsFileId;
    /**
     * PeptideShaker results file (compressed folder) type.
     */
    private String file_ext;
    /**
     * Input FASTA file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile fasta_file;
    /**
     * Output proteins file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile proteins_file;
    /**
     * Output peptides file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile peptides_file;
    /**
     * Output PSM file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile psm_file;
    /**
     * Output Proteoforms file representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile proteoform_file;
    /**
     * Output ZIP file (folder) representation on Online PeptideShaker.
     */
    private GalaxyTransferableFile zip_file;
    /**
     * Input search parameter enzyme.
     */
    private String enzyme;
    private RangeColorGenerator colorGenerator;
    private TreeSet<Double> intensitySet;
//    private TreeSet<Double> logIntensitySet;
    private double topIntensityValue;
    private final Set<String> inputMgffilesName;
    /**
     * Standard header values for protein, peptides, psm and moff table files
     */
    private final TableHeaderConstatnts table_headers;
    /**
     * Output MOFF quant file representation on Online PeptideShaker.
     */
    private Set<GalaxyTransferableFile> moff_quant_files;
    private String moffGalaxyId;

    public String getMoffGalaxyId() {
        return moffGalaxyId;
    }

    private boolean quantDataset = false;

    public Map<String, GalaxyFileObject> getIndexedMGFFilesMap() {
        return indexedMGFFilesMap;
    }

    public String getIndexedMgfGalaxyId() {
        return indexedMgfGalaxyId;
    }

    public Set<GalaxyTransferableFile> getCuiFileSet() {
        return cuiFileSet;
    }

    public String getCuiListGalaxyId() {
        return cuiListGalaxyId;
    }

    /**
     * Get Moff file object
     *
     * @return galaxy transferable file
     */
    public Set<GalaxyTransferableFile> getMoff_quant_file() {
        return moff_quant_files;
    }

    /**
     * Set Moff File
     *
     * @param moff_quant_file galaxy transferable file
     */
    public void setMoff_quant_files(String moffGalaxyId, Set<GalaxyTransferableFile> moff_quant_files) {
        this.quantDataset = moff_quant_files.iterator().next().getStatus().equalsIgnoreCase("ok");
        this.moff_quant_files = moff_quant_files;
        this.moffGalaxyId = moffGalaxyId;
    }
    /**
     * FASTA utilities that allow getting protein FASTA information using the
     * UniProt web service.
     */
    private FastaFileWebService FastaFileWebService;
    /**
     * MGF index file map (.cms) files.
     */
    private final Map<String, File> cuiFileMap;
    /**
     * Zip Folder contains mgf index files (cui).
     */
    private Set<GalaxyTransferableFile> cuiFileSet;
    private String cuiListGalaxyId;
    /**
     * MGF index file map (.cui) files.
     */
    private final Map<String, MgfIndex> importedMgfIndexObjectMap;

    /**
     * Protein evidence options array.
     */
    private final String[] proteinEvidence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};
    /**
     * Protein modifications map (based on user search input).
     */
    private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;
    /**
     * Protein to peptides number map (to be used in datasets filters).
     */
    private final TreeMap<Comparable, Set<Comparable>> proteinPeptidesNumberMap;
    /**
     * The sequence matching options.
     */
    private SequenceMatchingParameters sequenceMatchingPreferences;
    /**
     * This factory will provide the implemented enzymes.
     */
    private EnzymeFactory enzymeFactory;
    /**
     * Managing the integration and data transfer between Galaxy Server and
     * Online Peptide Shaker (managing requests and responses)
     */
    private final GalaxyDatasetServingUtil galaxyDatasetServingUtil;
    /**
     * Generic class grouping the parameters used for protein identifications.
     */
    private IdentificationParameters identificationParameters;
    /**
     * Creating time for the datasets (to sort based on creation date).
     */
    private Date createTime;
    /**
     * Datasets statues (valid, not valid, or in progress).
     */
    private String psDatasetStat;
    /**
     * Thread that is used to read the PeptideShaker results zip folder.
     */
    private Thread PeptideShakerResultsFolderThread;
    /**
     * Thread that is used to read the output PSM file.
     */
    private Thread PSMFileThread;
    /**
     * Task used to process output peptide file.
     */
    private ProcessPeptidesTask processPeptidesTask;
    /**
     * Task used to process output protein file.
     */
    private ProcessProteinsTask processProteinsTask;
    /**
     * Task used to process input FASTA file.
     */
    private ProcessFastaFileTask processFastaFileTask;
    /**
     * Future results from input FASTA file task.
     */
    private Future proteinProcessFuture;
    /**
     * Future results from output peptide file task.
     */
    private Future peptideProcessFuture;
    /**
     * Task used to process pathway matcher files.
     */
    private ProcessPathwayMatcherFilesTask processPathwayMatcherFilesTask;

    /**
     * PSM File already initialised.
     */
    private boolean PSMFileInitialized = false;

    private final Set<String> csf_pr_Accession_List;
    /**
     * Link to share in public.
     */
    private String linkToShare;
    private File uploadedFastaFile;
    private File uploadedProteinFile;
    private File uploadedPeptideFile;
    private final boolean uploadedProject;
    private boolean toShareDataset;

    public boolean isToShareDataset() {
        return toShareDataset;
    }

    public void setToShareDataset(boolean toShareDataset) {
        this.toShareDataset = toShareDataset;
    }

    /**
     * Get dataset public share link that allow users to share their data
     *
     * @return url link
     */
    public String getLinkToShare() {
        if (linkToShare == null) {
            try {
                linkToShare = "";
                if (this.getProjectName() == null || SearchGUIResultFile == null || PeptideShakerResultsFileId == null || SearchGUIResultFile.getOverview() == null || cuiFileSet == null) {
                    return linkToShare;
                }

                JSONObject object = new JSONObject();
                object.put("dsName", this.getProjectName());
                object.put("sqi", this.SearchGUIResultFile.getGalaxyId());
                object.put("ps", PeptideShakerResultsFileId);
                object.put("ps_history", this.getHistoryId());
                object.put("mgf", indexedMgfGalaxyId);
                object.put("cui", cuiListGalaxyId);
                object.put("overviewSGUI", SearchGUIResultFile.getOverview());

                JSONObject filesIdName = new JSONObject();

                for (GalaxyTransferableFile f : cuiFileSet) {
                    filesIdName.put(f.getGalaxyId(), f.getName());
                }
                object.put("cuiSet", filesIdName);

                filesIdName = new JSONObject();
                for (GalaxyFileObject f : indexedMGFFilesMap.values()) {
                    filesIdName.put(f.getGalaxyId(), f.getName());
                }
                object.put("mgfIdSet", filesIdName);
                object.put("apiKey", apiKey);

                if (moff_quant_files != null) {
                    object.put("moffid", moffGalaxyId);
                    filesIdName = new JSONObject();
                    for (GalaxyTransferableFile file : moff_quant_files) {
                        filesIdName.put(file.getGalaxyId(), file.getName());
                    }
                    object.put("moffSet", filesIdName);

//                    linkToShare = this.getProjectName() + "-_-sgi-_:_-" + SearchGUIResultFile.getGalaxyId() + "-_-pszip-_:_-" + PeptideShakerResultsFileId + "-_-mgf-_:_-" + indexedMgfGalaxyId + "-_-mgfindexfolder-_:_-" + cuiListGalaxyId + "-_-sgioverview-_:_-" + SearchGUIResultFile.getOverview() + "-_-quant-_:_-" + moffGalaxyId;
                }
                linkToShare = URLEncoder.encode(object.toString(), "UTF-8");
            } catch (JSONException ex) {
                System.out.println("Error: json JSONException 349 : " + ex);
            } catch (UnsupportedEncodingException ex) {
                System.out.println("Error: UnsupportedEncodingException error 351 : " + ex);
            }

        }
        return linkToShare;
    }

    /**
     * Constructor to initialise the main variables required to visualise
     * PeptideShaker results
     *
     * @param projectName Dataset (Project) name.
     * @param user_folder User data files folder
     * @param galaxyLink Galaxy Server web address
     * @param apiKey Galaxy server user API key.
     * @param galaxyDatasetServingUtil Managing the integration and data
     * transfer between Galaxy Server and Online Peptide Shaker (managing
     * requests and responses)
     * @param csf_pr_Accession_List
     */
    public PeptideShakerVisualizationDataset(String projectName, File user_folder, String galaxyLink, String apiKey, GalaxyDatasetServingUtil galaxyDatasetServingUtil, Set<String> csf_pr_Accession_List) {
        this.projectName = projectName;
        this.user_folder = user_folder;
        this.galaxyLink = galaxyLink;
        this.apiKey = apiKey;
        this.indexedMGFFilesMap = new LinkedHashMap<>();
        this.cuiFileMap = new LinkedHashMap<>();
        this.importedMgfIndexObjectMap = new LinkedHashMap<>();
        this.proteinPeptidesNumberMap = new TreeMap<>();
        this.modificationMap = new ConcurrentHashMap<>();
        this.modificationMap.put("No Modification", new LinkedHashSet<>());
        this.galaxyDatasetServingUtil = galaxyDatasetServingUtil;
        this.csf_pr_Accession_List = csf_pr_Accession_List;
        this.uploadedProject = false;
        this.table_headers = new TableHeaderConstatnts();
        this.inputMgffilesName = new LinkedHashSet<>();
    }

    /**
     * Constructor to initialise the main variables required to visualise
     * PeptideShaker results for uploaded projects type 1 ad 2
     *
     * @param projectName Dataset (Project) name.
     * @param fastaFile
     *
     * @param csf_pr_Accession_List
     * @param peptideFile
     * @param proteinFile
     */
    public PeptideShakerVisualizationDataset(String projectName, File fastaFile, File proteinFile, File peptideFile, Set<String> csf_pr_Accession_List) {
        this.projectName = projectName;
        PeptideShakerVisualizationDataset.this.setName(projectName);
        PeptideShakerVisualizationDataset.this.setGalaxyId(projectName);
        this.user_folder = null;
        this.galaxyLink = null;
        this.apiKey = null;
        this.indexedMGFFilesMap = new LinkedHashMap<>();
        this.cuiFileMap = new LinkedHashMap<>();
        this.importedMgfIndexObjectMap = new LinkedHashMap<>();
        this.proteinPeptidesNumberMap = new TreeMap<>();
        this.modificationMap = new ConcurrentHashMap<>();
        this.modificationMap.put("No Modification", new LinkedHashSet<>());
        this.galaxyDatasetServingUtil = null;
        this.csf_pr_Accession_List = csf_pr_Accession_List;
        this.uploadedFastaFile = fastaFile;
        this.uploadedProteinFile = proteinFile;
        this.uploadedPeptideFile = peptideFile;
        this.uploadedProject = true;
        this.psDatasetStat = "ok";
        PeptideShakerVisualizationDataset.this.setType("User uploaded Project");
        this.table_headers = new TableHeaderConstatnts();
        PeptideShakerVisualizationDataset.this.processDataFiles();        
        this.inputMgffilesName = new LinkedHashSet<>();

    }

    /**
     * Get search engines used in the SearchGUI search
     *
     * @return list of used search engines used in the search
     */
    public String getSearchEngines() {
        if (SearchGUIResultFile != null && SearchGUIResultFile.getOverview() != null) {

            return SearchGUIResultFile.getOverview().split("DB:")[0];
        }
        return "";
    }

    public Set<String> getInputDataFiles() {
        inputMgffilesName.clear();
        if (quantDataset) {
            if (moff_quant_files != null && SearchGUIResultFile.getOverview() != null) {
                for (GalaxyTransferableFile ds : moff_quant_files) {
                    System.out.println("search gui " + ds.getOverview());
                }
            }
        } else {

            if (SearchGUIResultFile != null && SearchGUIResultFile.getOverview() != null) {
                String[] arr = SearchGUIResultFile.getOverview().split("\\n");
                for (String spec : arr) {
                    if (spec.startsWith("Spectrums:")) {
                        inputMgffilesName.add(spec.replace("Spectrums:", "").split("(API")[0]);
                    }
                }

            }

        }
        return this.inputMgffilesName;
    }

    /**
     * Check if decoy database added and used in the search process
     *
     * @return decoy database added
     */
    public boolean isDecoyDBAdded() {
        if (SearchGUIResultFile != null) {
            return SearchGUIResultFile.getOverview().contains("Creating decoy database");
        }
        return false;
    }

    /**
     * Get current state of the dataset
     *
     * @return dataset state
     */
    @Override
    public String getStatus() {

        if (externalDataset || uploadedProject) {
            return psDatasetStat;
        }

        if (SearchGUIResultFile == null) {
            return "Error";
        }
        return isValidDataset();
    }

    private boolean externalDataset;

    private String isValidDataset() {

        if (!psDatasetStat.equalsIgnoreCase("ok")) {
            return psDatasetStat;
        }
        if (!SearchGUIResultFile.getStatus().equalsIgnoreCase("ok")) {
            return SearchGUIResultFile.getStatus();
        }
        for (GalaxyFileObject ds : indexedMGFFilesMap.values()) {
            if (ds.getStatus().equalsIgnoreCase("ok")) {
                return ds.getStatus();

            }
        }
        for (GalaxyTransferableFile ds : cuiFileSet) {
            if (ds.getStatus().equalsIgnoreCase("ok")) {
                return ds.getStatus();

            }
        }
        if (quantDataset) {
            for (GalaxyTransferableFile ds : moff_quant_files) {
                if (ds.getStatus().equalsIgnoreCase("ok")) {
                    return ds.getStatus();

                }
            }

        }

        return "ok";
    }

    /**
     * Set current state of the dataset
     *
     * @param status dataset state
     * @param external dataset is external
     */
    public void setStatus(String status, boolean external) {
        this.psDatasetStat = status;
        this.externalDataset = external;
    }

    /**
     * Set current state of the dataset
     *
     * @param status dataset state
     */
    @Override
    public void setStatus(String status) {
        this.psDatasetStat = status;
    }

    /**
     * Get dataset (Project) name.
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Get SearchGUI result file
     *
     * @return SearchGUI results file.
     */
    public GalaxyFileObject getSearchGUIFile() {
        return SearchGUIResultFile;
    }

    /**
     * Set SearchGUI result file
     *
     * @param searchGUIResultFile SearchGUI results file.
     */
    public void setSearchGUIResultFile(GalaxyFileObject searchGUIResultFile) {
        this.SearchGUIResultFile = searchGUIResultFile;

    }

    /**
     * Initialise MGF index files map (.cms).
     */
    private void initCUIFilesMap() {
        cuiFileMap.clear();
        for (GalaxyTransferableFile f : cuiFileSet) {
            try {
                cuiFileMap.put(f.getName(), f.getFile());
            } catch (IOException ex) {
                System.out.println("Error: init cui files map : " + ex);
            }
        }
    }

    /**
     * Get the parameters used for protein identification.
     *
     * @return IdentificationParameters object.
     */
    public IdentificationParameters getSearchingParameters() {

        if (SearchGUIResultFile == null || (!getStatus().equalsIgnoreCase("ok"))) {
            return null;
        }
        if (identificationParameters == null) {
            initialiseIdintificationParameters();
        }

        return identificationParameters;
    }

//    /**
//     * Get MGF input files list.
//     *
//     * @return map of MGF input files used in generating the dataset.
//     */
//    public Map<String, GalaxyFileObject> getInputMGFFiles() {
//        if (multiMgf) {
//            Map<String, GalaxyFileObject> tempinputMGFFile = new LinkedHashMap<>();
//            for (String mgfName : indexedMGFFilesMap.keySet()) {
//                if (mgfName.contains("-Multi-Indexed-MGF")) {
//                    tempinputMGFFile.put(mgfName, indexedMGFFilesMap.get(mgfName));
//                }
//            }
//            this.indexedMGFFilesMap.clear();
//            this.indexedMGFFilesMap.putAll(tempinputMGFFile);
//            multiMgf = false;
//        }
//        return indexedMGFFilesMap;
//    }
//    private boolean multiMgf = false;
    /**
     * Add MGF file to the dataset
     *
     * @param indexedMgfGalaxyId MGF file id on Galaxy Server
     * @param mgfDs MGF file representation on Online PeptideShaker
     */
    public void setIndexedMgfFiles(String indexedMgfGalaxyId, Set<GalaxyFileObject> indexedMgfFiles) {
        for (GalaxyFileObject file : indexedMgfFiles) {
            this.indexedMGFFilesMap.put(file.getGalaxyId(), file);
        }
        this.indexedMgfGalaxyId = indexedMgfGalaxyId;

    }

    /**
     * Add MGF file to the dataset
     *
     * @param cuiListGalaxyId MGF file id on Galaxy Server
     * @param mgfDs MGF file representation on Online PeptideShaker
     */
    public void setCuiFiles(String cuiListGalaxyId, Set<GalaxyTransferableFile> cuiFileSet) {
        this.cuiFileSet = cuiFileSet;
        this.cuiListGalaxyId = cuiListGalaxyId;

    }

    /**
     * Set main file type (used for export)
     *
     * @param file_ext export file type.
     */
    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    /**
     * Set PeptideShaker Results File Id on Galaxy Server
     *
     * @param PeptideShakerResultsFileId PeptideShaker Results File Id on Galaxy
     * Server
     * @param external shared dataset with other users
     */
    public void setPeptideShakerResultsFileId(String PeptideShakerResultsFileId, final boolean external) {
        this.PeptideShakerResultsFileId = PeptideShakerResultsFileId;
        PeptideShakerResultsFolderThread = new Thread(() -> {
//            if (!external) {
            initialiseDataFiles(PeptideShakerResultsFileId);
//            if (mgfIndexFolder != null) {
//                initMgfIndexFiles();
//            }
//            } else {
//                initialiseExternalDataFiles(PeptideShakerResultsFileId);
//            }
        });
        PeptideShakerResultsFolderThread.start();

    }

    /**
     * Initialise PeptideShaker Results File and prepare inside folder files
     *
     * @param PeptideShakerResultsFileId PeptideShaker Results File Id on Galaxy
     * Server
     */
    private void initialiseDataFiles(String PeptideShakerResultsFileId) {
        //validate zipFile
        GalaxyFileObject ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-ZIP");
        ds.setType("ZIP File");
        ds.setGalaxyId(PeptideShakerResultsFileId);
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        ds.setStatus(this.psDatasetStat);
        zip_file = new GalaxyTransferableFile(user_folder, ds, true);
        zip_file.setDownloadUrl("to_ext=" + file_ext);
        zip_file.setHistoryId(this.getHistoryId());

//init fasta file 
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-FASTA");
        ds.setType("FASTA File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__data/input_fasta_file.fasta");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        fasta_file = new GalaxyTransferableFile(user_folder, ds, true);
        fasta_file.setDownloadUrl("to_ext=" + file_ext);
        //init protein file
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PROTEINS");
        ds.setType("Protein File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Protein_Report_with_non-validated_matches.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        proteins_file = new GalaxyTransferableFile(user_folder, ds, true);
        proteins_file.setDownloadUrl("to_ext=" + file_ext);
        //init peptides file
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PEPTIDES");
        ds.setType("Peptides File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Peptide_Report_with_non-validated_matches.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);
        peptides_file = new GalaxyTransferableFile(user_folder, ds, true);
        peptides_file.setDownloadUrl("to_ext=" + file_ext);
        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-PSM");
        ds.setType("PSM File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_PSM_Report_with_non-validated_matches.txt");
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);

        psm_file = new GalaxyTransferableFile(user_folder, ds, true);
        psm_file.setDownloadUrl("to_ext=" + file_ext);

        ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-Proteoforms");
        ds.setType("Proteoforms File");
        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/proteins_proteoforms.txt");//PeptideShakerResultsFileId + "__reports/Default_Proteoform_with_non-validated_matches.txt"
        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);//PeptideShakerResultsFileId
        proteoform_file = new GalaxyTransferableFile(user_folder, ds, true);//true
        proteoform_file.setDownloadUrl("to_ext=" + "txt");

    }

//    /**
//     * Initialise PeptideShaker Results File and prepare inside folder files
//     *
//     * @param PeptideShakerResultsFileId PeptideShaker Results File Id on Galaxy
//     * Server
//     */
//    private void initialiseExternalDataFiles(String PeptideShakerResultsFileId) {
//        //validate zipFile
//        GalaxyFileObject ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-ZIP");
//        ds.setType("ZIP File");
//        ds.setGalaxyId(PeptideShakerResultsFileId);
//
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey + "&to_ext=zip");
//        ds.setStatus(this.status);
//        zip_file = new GalaxyTransferableFile(user_folder, ds, true);
//        zip_file.setDownloadUrl("to_ext=" + file_ext);
//        zip_file.setHistoryId(this.getHistoryId());
//
////init fasta file 
//        ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-FASTA");
//        ds.setType("FASTA File");
//        ds.setGalaxyId(PeptideShakerResultsFileId + "__data/input_fasta_file.fasta");
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey + "&to_ext=zip");
//        fasta_file = new GalaxyTransferableFile(user_folder, ds, true);
//        fasta_file.setDownloadUrl("to_ext=" + file_ext);
//        //init protein file
//        ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-PROTEINS");
//        ds.setType("Protein File");
//        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Protein_Report_with_non-validated_matches.txt");
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey + "&to_ext=zip");
//        proteins_file = new GalaxyTransferableFile(user_folder, ds, true);
//        proteins_file.setDownloadUrl("to_ext=" + file_ext);
//        //init peptides file
//        ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-PEPTIDES");
//        ds.setType("Peptides File");
//        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_Peptide_Report_with_non-validated_matches.txt");
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey + "&to_ext=zip");
//        peptides_file = new GalaxyTransferableFile(user_folder, ds, true);
//        peptides_file.setDownloadUrl("to_ext=" + file_ext);
//        ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-PSM");
//        ds.setType("PSM File");
//        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/Default_PSM_Report_with_non-validated_matches.txt");
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey + "&to_ext=zip");
//
//        psm_file = new GalaxyTransferableFile(user_folder, ds, true);
//        psm_file.setDownloadUrl("to_ext=" + file_ext);
//
//        ds = new GalaxyFileObject();
//        ds.setName(this.projectName + "-Proteoforms");
//        ds.setType("Proteoforms File");
//        ds.setGalaxyId(PeptideShakerResultsFileId + "__reports/proteins_proteoforms.txt");//PeptideShakerResultsFileId + "__reports/Default_Proteoform_with_non-validated_matches.txt"
//        ds.setDownloadUrl(galaxyLink + "/api/histories/" + this.getHistoryId() + "/contents/" + PeptideShakerResultsFileId + "/display?key=" + apiKey);//PeptideShakerResultsFileId
//        proteoform_file = new GalaxyTransferableFile(user_folder, ds, true);//true
//        proteoform_file.setDownloadUrl("to_ext=" + "txt");
//
//        if (SearchGUIResultFile != null) {
//            initMgfIndexFiles();
//        }
//
//    }
    /**
     * Get Input FASTA file used in the search name
     *
     * @return FASTA file name
     */
    public String getFastaFileName() {
        if (SearchGUIResultFile != null && SearchGUIResultFile.getOverview() != null) {
            return SearchGUIResultFile.getOverview().split("sequences:")[0].split("DB:")[1].trim();
        }

        return "input_fasta_file.fasta";
    }

    /**
     * Get list of variable modification used as input in searching process
     *
     * @return list of variable modification
     */
    public ArrayList<String> getVariableModification() {

        if (identificationParameters == null) {
            initialiseIdintificationParameters();
        }
        ArrayList<String> variableModifications = identificationParameters.getSearchParameters().getModificationParameters().getVariableModifications();

        variableModifications.forEach((mod) -> {
            modificationMap.put(mod.trim(), new LinkedHashSet<>());
        });
        return variableModifications;
    }

    /**
     * Get list of fixed modification used as input in searching process
     *
     * @return list of fixed modification
     */
    public ArrayList<String> getFixedModification() {
        if (identificationParameters == null) {
            initialiseIdintificationParameters();
        }
        ArrayList<String> fixedModifications = identificationParameters.getSearchParameters().getModificationParameters().getFixedModifications();
        fixedModifications.forEach((mod) -> {
            modificationMap.put(mod.trim(), new LinkedHashSet<>());
        });

        return fixedModifications;

    }

    /**
     * Initialise protein object from the provided FASTA file
     *
     * @param proteinkey protein accession used as a key
     */
    private void initialiseFromFastaFile(String proteinkey) {
        ProteinGroupObject protein = new ProteinGroupObject();
        protein.setAccession(proteinkey);
        protein.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(protein.getAccession().trim()));
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(descArr[0].replace("OS", "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(proteinkey, protein);

    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    /**
     * Set input enzyme used in search process
     *
     * @param enzyme enzyme name.
     */
    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }

    /**
     * Select and update protein information
     *
     * @param selectedIds selected protein IDs (accessions)
     */
    public void selectUpdateProteins(Set<Comparable> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty() || selectedIds.contains(null + "") || processFastaFileTask.getFastaProteinSequenceMap() == null) {
            return;
        }
        selectedIds.stream().map((id) -> processProteinsTask.getProteinsMap().get(id.toString())).forEachOrdered((prot) -> {

            checkAndUpdateProtein(prot.getAccession());
            prot.getProteinGroupSet().forEach((relatedProt) -> {
                checkAndUpdateProtein(relatedProt);
            });
        });

    }

    /**
     * Update protein information
     *
     * @param id protein ID (accession)
     */
    private void checkAndUpdateProtein(String id) {
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() != null) {
            return;
        }
        if (processProteinsTask.getProteinsMap().containsKey(id) && processProteinsTask.getProteinsMap().get(id).getSequence() == null) {
            completeProteinInformation(processProteinsTask.getProteinsMap().get(id));
        } else if (!processFastaFileTask.getFastaProteinMap().containsKey(id) && processFastaFileTask.getFastaProteinSequenceMap().containsKey(id)) {
            initialiseFromFastaFile(id);
        }

    }

    /**
     * Add missing information to protein object
     *
     * @param protein object to be updated
     */
    public void completeProteinInformation(ProteinGroupObject protein) {
        ProteinSequence entry = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession());
        String protDesc = entry.getDescription().split("OS")[0];
        String[] descArr = entry.getDescription().split("\\s");
        protein.setDescription(protDesc.replace(descArr[0], "").trim());
        protein.setSequence(entry.getSequenceAsString());
        protein.setProteinEvidence(proteinEvidence[Integer.parseInt(descArr[descArr.length - 2].replace("PE=", "").trim())]);
        processFastaFileTask.getFastaProteinMap().put(protein.getAccession(), protein);

    }

    /**
     * Process files inside the PeptideShaker results file, (execute proteins,
     * peptides,and FASTA file tasks).
     *
     */
    public void processDataFiles() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            if (!uploadedProject) {
                uploadedProteinFile = proteins_file.getFile();
                uploadedFastaFile = fasta_file.getFile();
                uploadedPeptideFile = peptides_file.getFile();
                while (PeptideShakerResultsFolderThread.isAlive()) {
                }
            }

            processProteinsTask = new ProcessProteinsTask(uploadedProteinFile);
            proteinProcessFuture = executorService.submit(processProteinsTask);
            processFastaFileTask = new ProcessFastaFileTask(uploadedFastaFile);
            executorService.submit(processFastaFileTask);
            while (!proteinProcessFuture.isDone()) {
            }
            processPeptidesTask = new ProcessPeptidesTask(uploadedPeptideFile, processProteinsTask.call(), processProteinsTask.getProtein_ProteinGroup_Map(), modificationMap);
            peptideProcessFuture = executorService.submit(processPeptidesTask);
            if (!uploadedProject) {
                processPathwayMatcherFilesTask = new ProcessPathwayMatcherFilesTask(proteoform_file);
                executorService.submit(processPathwayMatcherFilesTask);
            }
           
            executorService.shutdown();
            if (!uploadedProject) {
                processPSMFile();
                while (PSMFileThread.isAlive()) {
                }
            } else {

                while (!peptideProcessFuture.isDone()) {
                } 
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Process output PSM file.
     */
    private void processPSMFile() {
        while (!peptideProcessFuture.isDone()) {

        }
        if (PSMFileInitialized) {
            return;
        }
        PSMFileThread = new Thread(this::processPsmFile);
        PSMFileThread.start();
        PSMFileThread.setPriority(Thread.MIN_PRIORITY);
        PSMFileInitialized = true;
        if (cuiFileSet != null) {
            initCUIFilesMap();
        }
    }

    /**
     * Get list of proteins objects included in the dataset
     *
     * @return map of protein objects (accessions to protein objects)
     */
    public Map<String, ProteinGroupObject> getProteinsMap() {
        while (!proteinProcessFuture.isDone()) {
        }
        return processProteinsTask.getProteinsMap();

    }

    /**
     * Get list of peptides objects included in the dataset
     *
     * @return map of peptides objects (modified sequence to protein objects)
     */
    public Map<Object, PeptideObject> getPeptidesMap() {
        while (!peptideProcessFuture.isDone()) {
        }
        return processPeptidesTask.getPeptidesMap();
    }

    /**
     * Get maximum molecular weight
     *
     * @return double value
     */
    public double getMaxMW() {
        return processProteinsTask.getMaxMW();
    }

    /**
     * Get maximum MS2 quantitative
     *
     * @return double value
     */
    public double getMaxMS2Quant() {
        return processProteinsTask.getMaxMS2Quant();
    }

    /**
     * Get maximum peptides number (highest number of peptides from a protein)
     *
     * @return number of peptides
     */
    public int getMaxPeptideNumber() {
        return processProteinsTask.getMaxPeptideNumber();
    }

    /**
     * Get maximum PSM number (highest number of PSM from a protein)
     *
     * @return number of PSM
     */
    public int getMaxPsmNumber() {
        return processProteinsTask.getMaxPsmNumber();
    }

    /**
     * Process output PSM file.
     */
    private void processPsmFile() {
        while (PeptideShakerResultsFolderThread.isAlive() || !peptideProcessFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        HashMap<String, Integer> psmMoffFileHeaderIndexerMap = new HashMap<>();
        psmMoffFileHeaderIndexerMap.put(table_headers.Index, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Proteins, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Sequence, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.AAs_Before, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.AAs_After, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Position, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Modified_Sequence, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Variable_Modifications, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Fixed_Modifications, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Spectrum_File, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Spectrum_Title, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Spectrum_Scan_Number, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.RT, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.MZ, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Measured_Charge, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Identification_Charge, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Theoretical_Mass, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Isotope_Number, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Precursor_mz_Error_ppm, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Localization_Confidence, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Probabilistic_PTM_score, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.D_score, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Confidence_Pers, -1);
        psmMoffFileHeaderIndexerMap.put(table_headers.Validation, -1);

        BufferedReader bufferedReader = null;
        try {//     
            File f = psm_file.getFile();
            bufferedReader = new BufferedReader(new FileReader(f), 1024 * 100);

            String line = bufferedReader.readLine();
            line = line.replace("\"", "");
            int i = 0;
            for (String str : line.split("\\t")) {
                str = str.replace(" ", "").toLowerCase();
                if (psmMoffFileHeaderIndexerMap.containsKey(str)) {
                    psmMoffFileHeaderIndexerMap.replace(str, i);
                }
                i++;
            }

            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("\"", "");
                String[] arr = line.split("\\t");
                PSMObject psm = new PSMObject();
                psm.setIndex(Integer.parseInt(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Index)]));
                for (String acc : arr[psmMoffFileHeaderIndexerMap.get(table_headers.Proteins)].split(",")) {
                    psm.addProtein(acc);
                }
                psm.setProteinsAsString(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Proteins)]);
                psm.setSequence(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Sequence)]);
                psm.setAasBefore(arr[psmMoffFileHeaderIndexerMap.get(table_headers.AAs_Before)]);
                psm.setAasAfter((arr[psmMoffFileHeaderIndexerMap.get(table_headers.AAs_After)]));
                psm.setPostions(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Position)]);
                psm.setModifiedSequence(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Modified_Sequence)]);
                for (String mod : arr[psmMoffFileHeaderIndexerMap.get(table_headers.Variable_Modifications)].split(",")) {
                    psm.addVariableModification(mod);
                }
                for (String mod : arr[psmMoffFileHeaderIndexerMap.get(table_headers.Fixed_Modifications)].split(",")) {
                    psm.addFixedModification(mod);
                }
                psm.setSpectrumFile(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_File)]);
                psm.setSpectrumTitle(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Title)]);
                if (psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number) != -1 && !arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number)].equalsIgnoreCase("")) {
                    psm.setTheoreticalMass(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Spectrum_Scan_Number)]));
                }
//                    psm.setSpectrumScanNumber(arr[psmMoffFileHeaderIndexerMap.get("spectrum scan number")]);
                psm.setRT(arr[psmMoffFileHeaderIndexerMap.get(table_headers.RT)]);
                psm.setMZ(arr[psmMoffFileHeaderIndexerMap.get(table_headers.MZ)]);
                psm.setMeasuredCharge((arr[psmMoffFileHeaderIndexerMap.get(table_headers.Measured_Charge)]));
                psm.setIdentificationCharge((arr[psmMoffFileHeaderIndexerMap.get(table_headers.Identification_Charge)]));
                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Theoretical_Mass)].equalsIgnoreCase("")) {
                    psm.setTheoreticalMass(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Theoretical_Mass)]));
                }
                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Isotope_Number)].equalsIgnoreCase("")) {
                    psm.setIsotopeNumber(Integer.parseInt(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Isotope_Number)]));
                }
                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Precursor_mz_Error_ppm)].equalsIgnoreCase("")) {
                    psm.setPrecursorMZError_PPM(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Precursor_mz_Error_ppm)]));
                }
                psm.setLocalizationConfidence(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Localization_Confidence)]);

                psm.setProbabilisticPTMScore((arr[psmMoffFileHeaderIndexerMap.get(table_headers.Probabilistic_PTM_score)]));

                psm.setD_Score((arr[psmMoffFileHeaderIndexerMap.get(table_headers.D_score)]));
                if (!arr[psmMoffFileHeaderIndexerMap.get(table_headers.Confidence_Pers)].equalsIgnoreCase("")) {
                    psm.setConfidence(Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));
                }
                psm.setValidation(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Validation)]);

                if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence())) {
                    processPeptidesTask.getPSMsMap().get(psm.getModifiedSequence()).add(psm);
                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("L", "I"))) {
                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I with L ?" + psm.getModifiedSequence());
                } else if (processPeptidesTask.getPSMsMap().containsKey(psm.getModifiedSequence().replace("I", "L"))) {
                    System.out.println("at Error for psm I mapping...not exist peptide need to replace I" + psm.getModifiedSequence());
                } else {
                    System.out.println("at Error for psm II mapping...not exist peptide " + psm.getModifiedSequence());
                }
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

        if (isQuantDataset()) {
            psmMoffFileHeaderIndexerMap.clear();
            psmMoffFileHeaderIndexerMap.put(table_headers.Prot, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.Mod_peptide, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.RT, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.mz, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.Mass, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.Charge, -1);
            psmMoffFileHeaderIndexerMap.put(table_headers.Intensity, -1);
            bufferedReader = null;
            intensitySet = new TreeSet<>();
            List<PSMObject> innerPSMList = new ArrayList<>();
            try {//     
                for (GalaxyTransferableFile moff_quant_file : moff_quant_files) {
                    File f = moff_quant_file.getFile();
                    bufferedReader = new BufferedReader(new FileReader(f), 1024 * 100);
                    /**
                     * index header header
                     */
                    String line = bufferedReader.readLine();
                    line = line.replace("\"", "");
                    int i = 0;
                    for (String str : line.split("\\t")) {
                        str = str.replace(" ", "").toLowerCase();
                        if (psmMoffFileHeaderIndexerMap.containsKey(str)) {
                            psmMoffFileHeaderIndexerMap.replace(str, i);
                        }
                        i++;

                    }

//                    int index = 1;
//
                    while ((line = bufferedReader.readLine()) != null) {
                        line = line.replace("\"", "");
                        String[] arr = line.split("\\t");
                        String mod_seq = arr[psmMoffFileHeaderIndexerMap.get(table_headers.Mod_peptide)];
                        if (processPeptidesTask.getPSMsMap().containsKey(mod_seq)) {
                            for (PSMObject psm : processPeptidesTask.getPSMsMap().get(mod_seq)) {
                                if (psm.getProteinsAsString().equalsIgnoreCase(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Prot)]) && psm.getRT().equals(arr[psmMoffFileHeaderIndexerMap.get(table_headers.RT)]) && psm.getMZ().equalsIgnoreCase(arr[psmMoffFileHeaderIndexerMap.get(table_headers.mz)]) && psm.getMeasuredCharge().equalsIgnoreCase(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Charge)]) && psm.getTheoreticalMass() == Double.valueOf(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Mass)])) {
                                    double intensity = Double.parseDouble(arr[psmMoffFileHeaderIndexerMap.get(table_headers.Intensity)]);
                                    if (psm.getIntensity() != intensity && psm.getIntensity() != -10000.0 && intensity != -1) {
                                        System.out.println("at psm numbers for " + mod_seq + "  is" + psm.getSpectrumTitle() + "  " + psm.getIntensity() + "  --  " + intensity);
                                        System.out.println("---------------------------------------------------------------");
                                    }
                                    if (intensity > 0) {
                                        psm.setIntensity(intensity);
                                        intensitySet.add(psm.getIntensity());
                                    }
                                    innerPSMList.add(psm);
                                    break;
                                }
                            }

                        } else {
                            System.out.println("----->>>>>this mod-seq not exist " + mod_seq + "   " + processPeptidesTask.getPSMsMap().containsKey(mod_seq.replace("I", "L")) + "   " + processPeptidesTask.getPSMsMap().containsKey(mod_seq.replace("L", "I")));
                        }
                    }
//
                }
                topIntensityValue = intensitySet.last();
                processPeptidesTask.calculateQuant(false);

                innerPSMList.forEach((psm) -> {
                    if (psm.getIntensity() > 0) {
                        int per = (int) Math.round((psm.getIntensity() / intensitySet.last()) * 100.0);
                        psm.setIntensityPercentage(per);
                        psm.setIntensityColor(colorGenerator.getGradeColor(psm.getIntensity(), topIntensityValue, intensitySet.first()));
                    } else {

                        System.out.println("error moff spectra without equivelent line " + psm.getModifiedSequence());
                    }
                });
            } catch (IOException | NumberFormatException ex) {
                ex.printStackTrace();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex1) {
                    }
                }
            }

        }

    }

    /**
     * Calculated matrix for Diva Matrix Layout Chart Filter
     *
     * @return Modification matrix object
     */
    public ModificationMatrix getModificationMatrix() {
        try {
            while (!peptideProcessFuture.isDone()) {
            }
            return (ModificationMatrix) peptideProcessFuture.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Get chromosome map that is used in chromosome filter
     *
     * @return map of proteins to the chromosome index
     */
    public Map<Integer, Set<Comparable>> getChromosomeMap() {
        return processProteinsTask.getChromosomeMap();
    }

    /**
     * Get protein inference map
     *
     * @return map of protein inference label to proteins
     */
    public Map<String, Set<Comparable>> getProteinInferenceMap() {
        return processProteinsTask.getProteinInferenceMap();
    }

    /**
     * Get protein validation map
     *
     * @return map of protein validation label to proteins
     */
    public Map<String, Set<Comparable>> getProteinValidationMap() {
        return processProteinsTask.getProteinValidationMap();
    }

    /**
     * Get protein to peptides number map
     *
     * @return map of peptides number to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinPeptidesNumberMap() {
        return proteinPeptidesNumberMap;
    }

    /**
     * Get protein to PSM number map
     *
     * @return map of PSM number to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
        return processProteinsTask.getProteinPSMNumberMap();
    }

    /**
     * Get protein to coverage value map
     *
     * @return map of coverage percent to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
        return processProteinsTask.getProteinCoverageMap();
    }

    /**
     * Get protein to intensity (all peptides intensity) value map
     *
     * @return map of intensity percent to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinIntensityAllPeptideMap() {
        return processPeptidesTask.getProteinIntensityAllPeptideMap();
    }

    /**
     * Get protein to intensity (unique peptides intensity) value map
     *
     * @return map of intensity percent to proteins
     */
    public TreeMap<Comparable, Set<Comparable>> getProteinIntensityUniquePeptideMap() {
        return processPeptidesTask.getProteinIntensityUniquePeptideMap();
    }

    /**
     * Get protein object from the protein list
     *
     * @param proteinKey (accession)
     * @return protein object
     */
    public ProteinGroupObject getProtein(String proteinKey) {
        checkAndUpdateProtein(proteinKey);
        if (processProteinsTask.getProteinsMap().containsKey(proteinKey)) {
            return processProteinsTask.getProteinsMap().get(proteinKey);
        } else if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
            return processFastaFileTask.getFastaProteinMap().get(proteinKey);
        } else {
            ProteinGroupObject newRelatedProt = updateProteinInformation(null, proteinKey);
            return newRelatedProt;
        }

    }

    /**
     * Get protein object from the protein list
     *
     * @param proteinKey (accession)
     * @param peptideKey peptide key
     * @return protein object
     */
    public ProteinGroupObject getProtein(String proteinKey, String peptideKey) {
        checkAndUpdateProtein(proteinKey);
        ProteinGroupObject proteinGroupObject;
        if (processProteinsTask.getProteinsMap().containsKey(proteinKey)) {
            proteinGroupObject = processProteinsTask.getProteinsMap().get(proteinKey);
            completeProteinInformation(proteinGroupObject);
        } else if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
            proteinGroupObject = processFastaFileTask.getFastaProteinMap().get(proteinKey);
        } else {
            proteinGroupObject = updateProteinInformation(null, proteinKey);
            completeProteinInformation(proteinGroupObject);
        }
        if (enzyme != null) {
            if (sequenceMatchingPreferences == null) {
                this.sequenceMatchingPreferences = SequenceMatchingParameters.getDefaultSequenceMatching();
            }
            if (enzymeFactory == null) {
                this.enzymeFactory = EnzymeFactory.getInstance();
            }
            proteinGroupObject.addPeptideSequence(peptideKey);
            proteinGroupObject.updatePeptideType(peptideKey, isEnzymaticPeptide(proteinGroupObject.getSequence(), processPeptidesTask.getPeptidesMap().get(peptideKey).getSequence(), enzymeFactory.getEnzyme(enzyme), sequenceMatchingPreferences));
        }
        if (proteinGroupObject.getProteinGroupKey() == null && processProteinsTask.getAccToGroupKeyMap().containsKey(proteinGroupObject.getAccession())) {
            ProteinGroupObject mainGropProt = processProteinsTask.getAccToGroupKeyMap().get(proteinGroupObject.getAccession());
            proteinGroupObject.setAllPeptidesIntensity(mainGropProt.getAllPeptidesIntensity());
            proteinGroupObject.setPercentageAllPeptidesIntensity(mainGropProt.getPercentageAllPeptidesIntensity());
            proteinGroupObject.setAllPeptideIintensityColor(mainGropProt.getAllPeptideIintensityColor());
            proteinGroupObject.setUniquePeptidesIntensity(mainGropProt.getUniquePeptidesIntensity());
            proteinGroupObject.setPercentageUniquePeptidesIntensity(mainGropProt.getPercentageUniquePeptidesIntensity());
            proteinGroupObject.setUniquePeptideIintensityColor(mainGropProt.getUniquePeptideIintensityColor());
        } else if (!processProteinsTask.getAccToGroupKeyMap().containsKey(proteinGroupObject.getAccession())) {
            double quant = 0.0;
            double counter = 0;
            for (String pepKey : proteinGroupObject.getRelatedPeptidesList()) {
                PeptideObject peptide = processPeptidesTask.getPeptidesMap().get(pepKey);
                quant += peptide.getIntensity();
                counter++;

            }
            if (counter > 0 && this.isQuantDataset()) {
                quant = quant / counter;
                proteinGroupObject.setAllPeptidesIntensity(quant);
                proteinGroupObject.setAllPeptideIintensityColor(processPeptidesTask.getProteinIntensityColorGenerator().getGradeColor(proteinGroupObject.getAllPeptidesIntensity(), topIntensityValue, intensitySet.first()));
                int per = (int) Math.round((proteinGroupObject.getAllPeptidesIntensity() / processPeptidesTask.getProteinIntensityValuesSet().last()) * 100.0);
                proteinGroupObject.setPercentageAllPeptidesIntensity(per);
            }

        }

        return proteinGroupObject;

    }

    /**
     * Update protein information to be display
     *
     * @param proteinObject protein object
     * @param proteinKey protein key (accession)
     * @return updated protein object
     */
    public ProteinGroupObject updateProteinInformation(ProteinGroupObject proteinObject, String proteinKey) {
        if (proteinObject == null) {
            if (processFastaFileTask.getFastaProteinMap().containsKey(proteinKey)) {
                proteinObject = processFastaFileTask.getFastaProteinMap().get(proteinKey);
            } else if (processFastaFileTask.getFastaProteinSequenceMap().containsKey(proteinKey)) {
                initialiseFromFastaFile(proteinKey);
                proteinObject = processFastaFileTask.getFastaProteinMap().get(proteinKey);
            } else {
                if (FastaFileWebService == null) {
                    FastaFileWebService = new FastaFileWebService();
                }
                proteinObject = FastaFileWebService.updateProteinInformation(proteinObject, proteinKey);
                processFastaFileTask.getFastaProteinMap().put(proteinKey, proteinObject);
            }
        }
        if (enzyme != null) {
            if (sequenceMatchingPreferences == null) {
                this.sequenceMatchingPreferences = SequenceMatchingParameters.getDefaultSequenceMatching();
            }
            if (enzymeFactory == null) {
                this.enzymeFactory = EnzymeFactory.getInstance();
            }
            for (String str : proteinObject.getRelatedPeptidesList()) {
                proteinObject.updatePeptideType(str, isEnzymaticPeptide(proteinObject.getSequence(), processPeptidesTask.getPeptidesMap().get(str).getSequence(), enzymeFactory.getEnzyme(enzyme), sequenceMatchingPreferences));
            }
        }
        proteinObject.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(proteinObject.getAccession().trim()));
        return proteinObject;
    }

    /**
     * Returns true of the peptide is non-enzymatic, i.e., has one or more end
     * points that cannot be caused by the enzyme alone. False means that both
     * the endpoints of the peptides could be caused by the selected enzyme, or
     * that it is a terminal peptide (where one end point is most likely not
     * enzymatic). Note that if a peptide maps to multiple locations in the
     * protein sequence this method returns true if one or more of these
     * peptides are enzymatic, even if not all mappings are enzymatic.
     *
     * @param sequence
     * @param peptideSequence the peptide sequence to check
     * @param enzyme the enzyme to use
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return true of the peptide is non-enzymatic
     */
    public boolean isEnzymaticPeptide(String sequence, String peptideSequence, Enzyme enzyme, SequenceMatchingParameters sequenceMatchingPreferences) {

        // get the surrounding amino acids
        HashMap<Integer, String[]> surroundingAminoAcids = getSurroundingAA(sequence, peptideSequence, 1, sequenceMatchingPreferences);
        String firstAA = peptideSequence.charAt(0) + "";
        String lastAA = peptideSequence.charAt(peptideSequence.length() - 1) + "";

        // iterate the possible extended peptide sequences
        for (int index : surroundingAminoAcids.keySet()) {
            String before = surroundingAminoAcids.get(index)[0];
            String after = surroundingAminoAcids.get(index)[1];
            // @TODO: how to handle semi-specific enzymes??
            if (!enzyme.isCleavageSite(before, firstAA) || !enzyme.isCleavageSite(lastAA, after) && before.length() != 0 || !enzyme.isCleavageSite(lastAA, after) && (!enzyme.isCleavageSite(before, firstAA) || after.length() != 0)) {
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the amino acids surrounding a peptide in the sequence of the
     * given protein in a map: peptide start index &gt; (amino acids before,
     * amino acids after).
     *
     * @param peptide the sequence of the peptide of interest
     * @param nAA the number of amino acids to include
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the amino acids surrounding a peptide in the protein sequence
     *
     * @throws IOException Exception thrown whenever an error occurred while
     * parsing the protein sequence
     */
    private HashMap<Integer, String[]> getSurroundingAA(String sequence, String peptide, int nAA, SequenceMatchingParameters sequenceMatchingPreferences) {

        int[] startIndexes = getPeptideStart(sequence, peptide, sequenceMatchingPreferences);
        HashMap<Integer, String[]> result = new HashMap<>();

        for (int startIndex : startIndexes) {
            result.put(startIndex, new String[2]);
            String subsequence = "";
            int stringIndex = startIndex - 1;
            for (int aa = stringIndex - nAA; aa < stringIndex; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }
            result.get(startIndex)[0] = subsequence;
            subsequence = "";

            for (int aa = stringIndex + peptide.length(); aa < stringIndex + peptide.length() + nAA; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }

            result.get(startIndex)[1] = subsequence;
        };

        return result;
    }

    /**
     * Returns the list of indexes where a peptide can be found in the protein
     * sequence. 1 is the first amino acid.
     *
     * @param peptideSequence the sequence of the peptide of interest
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the list of indexes where a peptide can be found in a protein
     * sequence
     */
    private int[] getPeptideStart(String sequence, String peptideSequence, SequenceMatchingParameters sequenceMatchingPreferences) {
        AminoAcidPattern aminoAcidPattern = AminoAcidPattern.getAminoAcidPatternFromString(peptideSequence);
        return aminoAcidPattern.getIndexes(sequence, sequenceMatchingPreferences);
    }

    /**
     * Get set of peptides related to selected protein
     *
     * @param proteinKey protein accession
     * @return set of peptide objects
     */
    public Set<PeptideObject> getPeptides(String proteinKey) {

        if (processPeptidesTask.getProtein_peptide_Map().containsKey(proteinKey)) {
            return processPeptidesTask.getProtein_peptide_Map().get(proteinKey);
        } else if (processProteinsTask.getProtein_ProteinGroup_Map().containsKey(proteinKey)) {
            Set<String> keys = processProteinsTask.getProtein_ProteinGroupkeys(proteinKey);
            Set<PeptideObject> peptides = new LinkedHashSet<>();
            keys.forEach((key) -> {
                peptides.addAll(processPeptidesTask.getProtein_peptide_Map().get(key));
            });
            return peptides;

        }
        return new HashSet<>();

    }

    /**
     * Get related PSM to selected peptide
     *
     * @param peptideKey peptide modified sequence
     * @return list of PSM objects
     */
    public List<PSMObject> getPSM(String peptideKey) {
        if (processPeptidesTask.getPSMsMap().containsKey(peptideKey)) {
            return processPeptidesTask.getPSMsMap().get(peptideKey);
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Get range colour generator
     *
     * @return colour generator
     */
    public RangeColorGenerator getProteinIntensityColorGenerator() {
        return colorGenerator;
    }

    /**
     * Initialise identification parameters from identification parameter file
     * (.par).
     */
    private void initialiseIdintificationParameters() {
        GalaxyFileObject ds = new GalaxyFileObject();
        ds.setName(this.projectName + "-Param");
        ds.setType("Param File");
        ds.setGalaxyId(SearchGUIResultFile.getGalaxyId() + "__SEARCHGUI_IdentificationParameters.par");
        ds.setDownloadUrl(SearchGUIResultFile.getDownloadUrl() + "?key" + apiKey);
        GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, true);
        file.setDownloadUrl("to_ext=" + file_ext);
        File f;
        try {
            f = file.getFile();
            this.identificationParameters = IdentificationParameters.getIdentificationParameters(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Get selected spectrum data that is related to selected peptide.
     *
     * @param PSMs selected PSMs files
     * @param peptideObject peptide object
     * @return map of Spectrum Information
     */
    public Map<Object, SpectrumInformation> getSelectedSpectrumData(List<PSMObject> PSMs, PeptideObject peptideObject) {//SpectrumPlot plot
//        if (multiMgf) {
//            Map<String, GalaxyFileObject> tempinputMGFFile = new LinkedHashMap<>();
//            for (String mgfName : indexedMGFFilesMap.keySet()) {
//                if (mgfName.contains("-Multi-Indexed-MGF")) {
//                    tempinputMGFFile.put(mgfName, indexedMGFFilesMap.get(mgfName));
//                }
//            }
//            this.indexedMGFFilesMap.clear();
//            this.indexedMGFFilesMap.putAll(tempinputMGFFile);
//            multiMgf = false;
//        }
        if (identificationParameters == null) {
            initialiseIdintificationParameters();
        }
        Map<Object, SpectrumInformation> spectrumInformationMap = new LinkedHashMap<>();
        int maxCharge = (-1 * Integer.MAX_VALUE);
        double maxError = (-1.0 * Double.MAX_VALUE);

        for (PSMObject selectedPsm : PSMs) {
            try {
                if (!importedMgfIndexObjectMap.containsKey(selectedPsm.getSpectrumFile())) {
                    Object object = SerializationUtils.readObject(cuiFileMap.get(selectedPsm.getSpectrumFile()));
                    importedMgfIndexObjectMap.put(selectedPsm.getSpectrumFile(), (MgfIndex) object);
                }

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            MgfIndex mgfIndex = importedMgfIndexObjectMap.get(selectedPsm.getSpectrumFile());
            String galaxyFileId = "";
            String galaxyHistoryId = "";
            for (GalaxyFileObject ds : indexedMGFFilesMap.values()) {
                if (ds.getName().equalsIgnoreCase(selectedPsm.getSpectrumFile())) {
                    galaxyFileId = ds.getGalaxyId();
                    galaxyHistoryId = ds.getHistoryId();
                    break;
                } else if (ds.getName().endsWith("-Multi-Indexed-MGF") && ds.getName().contains(selectedPsm.getSpectrumFile().replace(".mgf", ""))) {
                    galaxyFileId = ds.getGalaxyId();
                    galaxyHistoryId = ds.getHistoryId();
                    break;
                }
            }
            if (mgfIndex == null) {
                return null;
            }
            long index = 0;
            if (false || selectedPsm.getSpectrumTitle().contains("scan=6769ttt")) {
                index = 13833838;
            } 
//            else if (selectedPsm.getSpectrumTitle().contains("scan=6612ttt")) {
//                index = 13162541;
//            } else if (selectedPsm.getSpectrumTitle().contains("scan=6689ttt")) {
//                index = 13499177;
//            } else if (selectedPsm.getSpectrumTitle().contains("scan=6750tttt")) {
//                index = 13499177;
//            } else if (selectedPsm.getSpectrumTitle().contains("scan=6849tttt")) {
//                index = 14122145;
//            } else if (selectedPsm.getSpectrumTitle().contains("scan=6652ttt")) {
//                index = 13334512;
//            } 
            else {
//                System.out.println("selectedPsm.getSpectrumTitle() " + selectedPsm.getSpectrumTitle());//mgfIndex.getIndex(selectedPsm.getSpectrumTitle())
                continue;
            }
            Spectrum spectrum = galaxyDatasetServingUtil.getSpectrum(index, galaxyHistoryId, galaxyFileId, selectedPsm.getSpectrumFile(), Integer.parseInt(selectedPsm.getIdentificationCharge().replace("+", "")));

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

            //            Protein psProtein = new Protein(galaxyLink, enzyme)
            Peptide psPeptide = new Peptide(peptideObject.getSequence(), peptideObject.getVariableModifications());//modifiedPeptideSequence.replace("NH2-", "").replace("-COOH", "")
//            psPeptide.setProteinMapping(new ArrayList<>(selectedPsm.getProteins()));
            PeptideAssumption psAssumption = new PeptideAssumption(psPeptide, tCharge);

            SpectrumMatch spectrumMatch = new SpectrumMatch(selectedPsm.getSpectrumFile(), selectedPsm.getSpectrumTitle());
            spectrumMatch.setBestPeptideAssumption(psAssumption);
            SpectrumInformation spectrumInformation = new SpectrumInformation();
            spectrumInformation.setCharge(Charge.getChargeAsFormattedString(tCharge));
            spectrumInformation.setFragmentIonAccuracy(identificationParameters.getSearchParameters().getFragmentIonAccuracy());
            spectrumInformation.setIdentificationParameters(identificationParameters);
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

    private MgfIndex converIndex(com.compomics.util.experiment.io.mass_spectrometry.mgf.MgfIndex oldIndex) {
        /**
         * The map of all indexes: spectrum title &gt; index in the file.
         */
        HashMap<String, Long> indexMap = new HashMap<>();
        /**
         * A map of all the spectrum titles and which rank they have in the
         * file, i.e., the first spectrum has rank 0, the second rank 1, etc.
         */
        HashMap<String, Integer> spectrumNumberIndexMap = new HashMap<>();
        /**
         * Map of the precursor mz values.
         */
        HashMap<Integer, Double> precursorMzMap = new HashMap<>();
        oldIndex.getSpectrumTitles().stream().map((key) -> {
            indexMap.put(key, oldIndex.getIndex(key));
            return key;
        }).forEachOrdered((key) -> {
            spectrumNumberIndexMap.put(key, oldIndex.getSpectrumIndex(key));
        });

        spectrumNumberIndexMap.values().forEach((index) -> {
            precursorMzMap.put(index, oldIndex.getPrecursorMz(index));
        });

        return new MgfIndex(oldIndex.getSpectrumTitles(), indexMap, spectrumNumberIndexMap, precursorMzMap, oldIndex.getFileName(), oldIndex.getMinRT(), oldIndex.getMaxRT(), oldIndex.getMaxMz(), oldIndex.getMaxIntensity(), oldIndex.getMaxCharge(), oldIndex.getMaxPeakCount(), oldIndex.isPeakPicked(), oldIndex.isPrecursorChargesMissing(), oldIndex.getLastModified());

    }

    /**
     * Get creating time for the datasets (to sort based on creation date).
     *
     * @return date object
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Set creating time for the datasets (to sort based on creation date).
     *
     * @param createTime date object
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Compare method to sort datasets based on creation date
     *
     * @param peptideShakerVisualizationDataset
     * @return integer value
     */
    @Override
    public int compareTo(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        return this.createTime.compareTo(peptideShakerVisualizationDataset.createTime);
    }

    /**
     * Check if the dataset has quant data (Moff File)
     *
     * @return the dataset has quant information
     */
    public boolean isQuantDataset() {
        return quantDataset;

    }

    /**
     * This class is used to create task that is used to process output peptide
     * file.
     */
    private final class ProcessPeptidesTask implements Callable<ModificationMatrix> {

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter.
         */
        private ModificationMatrix modificationMatrix;
        /**
         * Protein modifications map (based on user search input).
         */
        private final ConcurrentHashMap<String, Set<Comparable>> modificationMap;
        /**
         * Protein to peptides map.
         */
        private final Map<String, Set<PeptideObject>> protein_peptide_Map;
        /**
         * Peptide to PSMs map.
         */
        private final Map<String, List<PSMObject>> PSMsMap;
        /**
         * Peptide map (modified sequence to peptide objects).
         */
        private final Map<Object, PeptideObject> peptidesMap;
        private final Map<String, Set<String>> unMappedProteinMap;
        private final Map<String, ProteinGroupObject> proteinsGroupMap;
        /**
         * Protein to intensity based on all peptides intensity value map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinIntensityAllPeptideMap;

        /**
         * Protein to intensity based on unique peptides intensity value map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinIntensityUniquePeptideMap;
//        private RangeColorGenerator proteinIntensityColorGenerator;
//        private TreeSet<Double> proteinIntensityValuesSet;

        public Map<String, Set<String>> getUnMappedProteinMap() {
            return unMappedProteinMap;
        }
        private final Map<String, Integer> peptideFileHeaderIndexerMap;

        /**
         * Constructor to initialise the main variables.
         *
         * @param peptides_file output peptides file
         * @param proteinsMap Protein map.
         * @param modificationMap map of modifications used in search inputs.
         */
        public ProcessPeptidesTask(File peptides_file, Map<String, ProteinGroupObject> proteinsGroupMap, Map<String, Set<String>> protein_ProteinGroup_Map, ConcurrentHashMap<String, Set<Comparable>> modificationMap) {

            this.proteinsGroupMap = proteinsGroupMap;
            this.modificationMap = new ConcurrentHashMap<>();
            this.modificationMap.put("No Modification", new LinkedHashSet<>());

            this.modificationMap.putAll(modificationMap);
            this.peptidesMap = new LinkedHashMap<>();
            protein_peptide_Map = new HashMap<>();
            PSMsMap = new LinkedHashMap<>();
            unMappedProteinMap = new HashMap<>();
            this.proteinIntensityAllPeptideMap = new TreeMap<>();
            this.proteinIntensityUniquePeptideMap = new TreeMap<>();
            BufferedReader bufferedReader = null;
            peptideFileHeaderIndexerMap = new HashMap<>();
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
            if (uploadedProject) {
                intensitySet = new TreeSet<>();
            }
            try {//           
//                System.out.println("start loading peptides");
                File f = peptides_file;
                bufferedReader = new BufferedReader(new FileReader(f), 1024 * 100);
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
                    /**
                     * check if it is uploaded project or imported from galaxy
                     */
                    i = 1;
                    if (uploadedProject) {
                        peptide.setIndex(i++);
                        if (peptideFileHeaderIndexerMap.get(table_headers.Quant) != -1) {
                            peptide.setIntensity(Double.valueOf(arr[peptideFileHeaderIndexerMap.get(table_headers.Quant)]));
                            intensitySet.add(peptide.getIntensity());
                        }
                    } else {
                        peptide.setIndex(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Index)]));
                        peptide.setProteins(arr[peptideFileHeaderIndexerMap.get(table_headers.Proteins)]);
                        peptide.setValidatedProteinGroupsNumber(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Number_Validated_Protein_Groups)]));
                        peptide.setUniqueDatabase(Integer.parseInt(arr[peptideFileHeaderIndexerMap.get(table_headers.Unique_Protein_Group)]));
                        peptide.setPostion(arr[peptideFileHeaderIndexerMap.get(table_headers.Position)]);
                        peptide.setAasBefore(arr[peptideFileHeaderIndexerMap.get(table_headers.AAs_Before)]);
                        peptide.setAasAfter(arr[peptideFileHeaderIndexerMap.get(table_headers.AAs_After)]);
                        peptide.setLocalizationConfidence(arr[peptideFileHeaderIndexerMap.get(table_headers.Localization_Confidence)]);

                    }

                    if (peptidesMap.containsKey(peptide.getModifiedSequence())) {
                        System.out.println("at Error:  peptide file key repeated : " + peptide.getModifiedSequence() + " protein key: " + peptide.getProteinGroupKey() + " Peptide postion: " + peptide.getPostion());
                    } else {
                        PSMsMap.put(peptide.getModifiedSequence(), new ArrayList<>());
                        peptidesMap.put(peptide.getModifiedSequence(), peptide);
                    }

                }

                peptidesMap.values().forEach((peptide) -> {
                    peptide.getProteinsSet().forEach((acc) -> {
                        acc = acc.trim();
                        if (!protein_ProteinGroup_Map.containsKey(acc)) {
                            if (!unMappedProteinMap.containsKey(acc)) {
                                unMappedProteinMap.put(acc, new HashSet<>());
                            }
                            unMappedProteinMap.get(acc).add(acc);
                            if (!protein_peptide_Map.containsKey(acc)) {
                                protein_peptide_Map.put(acc, new LinkedHashSet<>());
                            }
                            protein_peptide_Map.get(acc).add(peptide);
                        } else {
                            Set<String> protGroupKeys = protein_ProteinGroup_Map.get(acc);
                            protGroupKeys.stream().map((key) -> {
                                if (!protein_peptide_Map.containsKey(key)) {
                                    protein_peptide_Map.put(key, new LinkedHashSet<>());
                                }
                                return key;
                            }).map((key) -> {
                                protein_peptide_Map.get(key).add(peptide);
                                return key;
                            }).forEachOrdered((key) -> {
                                ProteinGroupObject pObj = proteinsGroupMap.get(key);
                                pObj.addPeptideSequence(peptide.getModifiedSequence());
                                /**
                                 * calculate modifications from peptide file*
                                 */
                                for (ModificationMatch modification : peptide.getVariableModifications()) {
                                    if (!modificationMap.containsKey(modification.getModification())) {
                                        modificationMap.put(modification.getModification(), new LinkedHashSet<>());
                                    }
                                    modificationMap.get(modification.getModification()).add(key);
                                }
                            });

                        }
                    });
                });
                for (String proteinGroupKey : proteinsGroupMap.keySet()) {
                    boolean added = false;
                    for (String modification : modificationMap.keySet()) {
                        if (modificationMap.get(modification).contains(proteinGroupKey)) {
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        modificationMap.get("No Modification").add(proteinGroupKey);
                    }

                }

                modificationMatrix = new ModificationMatrix(new LinkedMap<>(modificationMap));

                if (uploadedProject) {
                    if (peptideFileHeaderIndexerMap.get(table_headers.Quant) != -1) {
                        topIntensityValue = intensitySet.last();
                        calculateQuant(true);
                        quantDataset = true;
                    }
                    calculateCoverage();

                }

            } catch (IOException | NumberFormatException ex) {
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

        private void calculateCoverage() {
            TreeMap<Comparable, Set<Comparable>> proteinCoverageMap = processProteinsTask.getProteinCoverageMap();
            processProteinsTask.getProteinsMap().keySet().forEach((proteinKey) -> {
                ProteinGroupObject protein = processProteinsTask.getProteinsMap().get(proteinKey);
                if (!(processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession()) == null)) {
                    String seq = processFastaFileTask.getFastaProteinSequenceMap().get(protein.getAccession()).getSequenceAsString().replace("i", "l");
                    int[] coverage = new int[seq.length()];

                    if (protein_peptide_Map.containsKey(proteinKey)) {
                        protein_peptide_Map.get(proteinKey).stream().map((pep) -> pep.getSequence().replace("i", "l")).filter((pepSeq) -> (seq.contains(pepSeq))).forEachOrdered((pepSeq) -> {
                            int index = 0;
                            index = seq.indexOf(pepSeq, index);
                            while (index > -1) {
                                int length = index + pepSeq.length();
                                for (int x = index; x < length; x++) {
                                    coverage[x] = 1;
                                }
                                index = seq.indexOf(pepSeq, length);
                            }
                        });
                    } else {
                    }
                    double counter = 0.0;
                    for (int i : coverage) {
                        if (i > 0) {
                            counter++;
                        }
                    }
                    protein.setCoverage((counter / (double) coverage.length) * 100.0);
                    int pc = (int) Math.round(protein.getCoverage());
                    if (!proteinCoverageMap.containsKey(pc)) {
                        proteinCoverageMap.put(pc, new LinkedHashSet<>());
                    }
                    proteinCoverageMap.get(pc).add(protein.getProteinGroupKey());
                }
            });

        }

        /**
         * Get list of peptides objects included in the dataset
         *
         * @return map of peptides objects (modified sequence to protein
         * objects)
         */
        public Map<Object, PeptideObject> getPeptidesMap() {
            return peptidesMap;
        }

        /**
         * Get PSMs map included in the datasets
         *
         * @return map of PSMs objects
         */
        public Map<String, List<PSMObject>> getPSMsMap() {
            return PSMsMap;
        }

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter
         *
         * @return Modification matrix object
         */
        public ModificationMatrix getModificationMatrix() {
            return modificationMatrix;
        }

        /**
         * Get map of modifications used in search inputs
         *
         * @return Modification map
         */
        public ConcurrentHashMap<String, Set<Comparable>> getModificationMap() {
            return modificationMap;
        }

        /**
         * Get protein to peptides map.
         *
         * @return Protein to peptides map.
         */
        public Map<String, Set<PeptideObject>> getProtein_peptide_Map() {
            return protein_peptide_Map;
        }

        /**
         * Calculated matrix for Diva Matrix Layout Chart Filter and return it
         * when the files are ready
         *
         * @return Modification matrix object
         * @throws Exception
         */
        @Override
        public ModificationMatrix call() throws Exception {
            return this.modificationMatrix;
        }

        public RangeColorGenerator getProteinIntensityColorGenerator() {
            return colorGenerator;
        }

        public TreeSet<Double> getProteinIntensityValuesSet() {
            return proteinIntensityValuesSet;
        }
        private TreeSet<Double> proteinIntensityValuesSet;

        public void calculateQuant(boolean uploadedProject) {

            if (!uploadedProject) {
                PSMsMap.keySet().forEach((modSeq) -> {
                    PeptideObject peptide = peptidesMap.get(modSeq);
                    double quant = 0d;
                    quant = PSMsMap.get(modSeq).stream().map((psm) -> psm.getIntensity()).reduce(quant, (accumulator, _item) -> accumulator + _item);
                    if (quant > 0) {
                        double finalquant = quant / (double) PSMsMap.get(modSeq).size();
                        peptide.setIntensity(finalquant);
                    }
                });
                //calc outliers

                colorGenerator = new RangeColorGenerator(topIntensityValue);
                PSMsMap.keySet().forEach((modSeq) -> {
                    PeptideObject peptide = peptidesMap.get(modSeq);
                    peptide.setIntensityColor(colorGenerator.getGradeColor(peptide.getIntensity(), topIntensityValue, intensitySet.first()));
                });
            } else {
                colorGenerator = new RangeColorGenerator(topIntensityValue);
                peptidesMap.values().forEach((peptide) -> {
                    peptide.setIntensityColor(colorGenerator.getGradeColor(peptide.getIntensity(), topIntensityValue, intensitySet.first()));
                });
            }
            //calc quant for proteins
            double quant = 0.0;
            proteinIntensityValuesSet = new TreeSet<>();
            double quant2 = 0.0;
            TreeSet<Double> treeSet3 = new TreeSet<>();
            for (String protGroupKey : proteinsGroupMap.keySet()) {
                ProteinGroupObject proteinGroup = proteinsGroupMap.get(protGroupKey);
                double counter = 0.0;
                double counter2 = 0.0;
                for (String modSeq : proteinGroup.getRelatedPeptidesList()) {
                    if (peptidesMap.get(modSeq).getIntensity() > 0) {
                        quant += peptidesMap.get(modSeq).getIntensity();
                        counter++;
                        if (!peptidesMap.get(modSeq).getProteinGroupKey().contains("-_-")) {
                            quant2 += peptidesMap.get(modSeq).getIntensity();
                            counter2++;
                        }
                    }
                }
                if (counter > 0) {
                    quant = quant / counter;
                    proteinGroup.setAllPeptidesIntensity(quant);
                    proteinIntensityValuesSet.add(quant);
                }
                if (counter2 > 0) {
                    quant2 = quant2 / counter2;
                    proteinGroup.setUniquePeptidesIntensity(quant2);
                    treeSet3.add(quant2);
                }
            }

            proteinsGroupMap.values().stream().map((proteinGroup) -> {
                proteinGroup.setAllPeptideIintensityColor(colorGenerator.getGradeColor(proteinGroup.getAllPeptidesIntensity(), topIntensityValue, intensitySet.first()));

                int per = (int) Math.round((proteinGroup.getAllPeptidesIntensity() / proteinIntensityValuesSet.last()) * 100.0);
                proteinGroup.setPercentageAllPeptidesIntensity(per);
                if (!this.proteinIntensityAllPeptideMap.containsKey(per)) {
                    this.proteinIntensityAllPeptideMap.put(per, new LinkedHashSet<>());
                }
                this.proteinIntensityAllPeptideMap.get(per).add(proteinGroup.getProteinGroupKey());
                return proteinGroup;
            }).forEachOrdered((proteinGroup) -> {
                proteinGroup.setUniquePeptideIintensityColor(colorGenerator.getGradeColor(proteinGroup.getUniquePeptidesIntensity(), topIntensityValue, intensitySet.first()));
                int per = (int) Math.round((proteinGroup.getUniquePeptidesIntensity() / treeSet3.last()) * 100.0);
                proteinGroup.setPercentageUniquePeptidesIntensity(per);
                if (!this.proteinIntensityUniquePeptideMap.containsKey(per)) {
                    this.proteinIntensityUniquePeptideMap.put(per, new LinkedHashSet<>());
                }
                this.proteinIntensityUniquePeptideMap.get(per).add(proteinGroup.getProteinGroupKey());
            });
        }

        public TreeMap<Comparable, Set<Comparable>> getProteinIntensityAllPeptideMap() {
            return proteinIntensityAllPeptideMap;
        }

        public TreeMap<Comparable, Set<Comparable>> getProteinIntensityUniquePeptideMap() {
            return proteinIntensityUniquePeptideMap;
        }
    }

    /**
     * This class is used to create task that is used to process output protein
     * file.
     */
    private class ProcessProteinsTask implements Callable<Map<String, ProteinGroupObject>> {

        /**
         * Protein to related protein map.
         */
        private Map<String, Set<ProteinGroupObject>> protein_relatedProteins_Map;

        /**
         * Protein accession to protein group map.
         */
        private Map<String, Set<String>> protein_ProteinGroup_Map;
        /**
         * Proteins map (accession to proteins object).
         */
        private Map<String, ProteinGroupObject> proteinsMap;
        /**
         * Protein inference map.
         */
        private final Map<String, Set<Comparable>> proteinInferenceMap;
        /**
         * Protein validation map.
         */
        private final Map<String, Set<Comparable>> proteinValidationMap;
        /**
         * Protein to gropKey.
         */
        private final Map<String, ProteinGroupObject> accToGroupKeyMap;
        /**
         * Map of proteins to the chromosome index.
         */
        private final Map<Integer, Set<Comparable>> chromosomeMap;
//        /**
//         * Map of proteins accession to groups keys.
//         */
//        private final Map<String, Set<Comparable>> accessionToGroupKeyMap;
        /**
         * Protein to coverage value map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinCoverageMap;
        /**
         * Protein to PSM numbers map.
         */
        private final TreeMap<Comparable, Set<Comparable>> proteinPSMNumberMap;
        /**
         * Maximum molecular weight.
         */
        private double maxMW = (-1.0 * Double.MAX_VALUE);
        /**
         * Maximum MS2 quantitative.
         */
        private double maxMS2Quant = (-1.0 * Double.MAX_VALUE);
        /**
         * Maximum peptides number.
         */
        private int maxPeptideNumber = (-1 * Integer.MAX_VALUE);
        /**
         * Maximum PSM numbers.
         */
        private int maxPsmNumber = (-1 * Integer.MAX_VALUE);

        /**
         * Constructor to initialise the main variables.
         *
         * @param proteins_file output protein file
         */
        public ProcessProteinsTask(File proteins_file) {
            this.protein_relatedProteins_Map = new HashMap<>();
//            this.accessionToGroupKeyMap = new HashMap<>();
            proteinsMap = new LinkedHashMap<>();
            this.proteinInferenceMap = new LinkedHashMap<>();
            this.proteinInferenceMap.put("No Information", new LinkedHashSet<>());
            this.proteinValidationMap = new LinkedHashMap<>();
            this.proteinValidationMap.put("No Information", new LinkedHashSet<>());
            this.proteinValidationMap.put("Confident", new LinkedHashSet<>());
            this.proteinValidationMap.put("Doubtful", new LinkedHashSet<>());
            this.chromosomeMap = new LinkedHashMap<>();
            this.chromosomeMap.put(-2, new LinkedHashSet<>());
            this.proteinCoverageMap = new TreeMap<>();
            this.proteinPSMNumberMap = new TreeMap<>();
            this.protein_ProteinGroup_Map = new HashMap<>();
            this.accToGroupKeyMap = new HashMap<>();

            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(proteins_file), 1024 * 100);
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

                    ProteinGroupObject proteinGroup;
                    proteinGroup = new ProteinGroupObject();
                    proteinGroup.setAccession(arr[proteinFileHeaderIndexerMap.get(table_headers.Main_Accession)]);
                    proteinGroup.setAvailableOn_CSF_PR(csf_pr_Accession_List.contains(proteinGroup.getAccession().trim()));
                    proteinGroup.setProteinGroup(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Group)]);
                    proteinGroup.setProteinGroupKey(proteinGroup.getProteinGroup().replace(" ", "").replace(",", "-_-"));
                    proteinGroup.setDescription(arr[proteinFileHeaderIndexerMap.get(table_headers.Description)]);
                    proteinGroup.setChromosome(arr[proteinFileHeaderIndexerMap.get(table_headers.Chromosome)]);
                    int chrIndex = -1;
                    try {
                        chrIndex = Integer.parseInt(proteinGroup.getChromosome());
                    } catch (NumberFormatException ex) {
                        if (proteinGroup.getChromosome().contains("HSCHR")) {
                            chrIndex = Integer.parseInt(proteinGroup.getChromosome().split("HSCHR")[1].split("_")[0].replaceAll("[\\D]", ""));
                        } else if (proteinGroup.getChromosome().equalsIgnoreCase("X")) {
                            chrIndex = 23;
                        } else if (proteinGroup.getChromosome().equalsIgnoreCase("Y")) {
                            chrIndex = 24;
                        }

                    }
//                    proteinGroup.setQuantValue(generateQuantValue());
                    proteinGroup.setChromosomeIndex(chrIndex);
                    if (proteinGroup.getChromosome().trim().isEmpty()) {
                        proteinGroup.setChromosome("No Information");
                        proteinGroup.setChromosomeIndex(-2);
                        chromosomeMap.get(proteinGroup.getChromosomeIndex()).add(proteinGroup.getProteinGroupKey());
                    } else {
                        if (!chromosomeMap.containsKey(proteinGroup.getChromosomeIndex())) {
                            chromosomeMap.put(proteinGroup.getChromosomeIndex(), new LinkedHashSet<>());
                        }
                        chromosomeMap.get(proteinGroup.getChromosomeIndex()).add(proteinGroup.getProteinGroupKey());
                    }

                    proteinGroup.setValidatedPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Peptides)]));
                    if (proteinGroup.getValidatedPeptidesNumber() > maxPeptideNumber) {
                        maxPeptideNumber = proteinGroup.getValidatedPeptidesNumber();
                    }
                    proteinGroup.setPeptidesNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Peptides)]));
                    if (!proteinPeptidesNumberMap.containsKey(proteinGroup.getValidatedPeptidesNumber())) {
                        proteinPeptidesNumberMap.put(proteinGroup.getValidatedPeptidesNumber(), new LinkedHashSet<>());
                    }
                    proteinPeptidesNumberMap.get(proteinGroup.getValidatedPeptidesNumber()).add(proteinGroup.getProteinGroupKey());
                    proteinGroup.setValidatedPSMsNumber(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_PSMs)]).intValue());
                    if (proteinGroup.getValidatedPSMsNumber() > maxPsmNumber) {
                        maxPsmNumber = proteinGroup.getValidatedPSMsNumber();
                    }
                    proteinGroup.setPSMsNumber(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_PSMs)]));
                    if (!proteinPSMNumberMap.containsKey(proteinGroup.getValidatedPSMsNumber())) {
                        proteinPSMNumberMap.put(proteinGroup.getValidatedPSMsNumber(), new LinkedHashSet<>());
                    }
                    proteinPSMNumberMap.get(proteinGroup.getValidatedPSMsNumber()).add(proteinGroup.getProteinGroupKey());
                    proteinGroup.setConfidence(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Confidence_Pers)]));

                    /**
                     * check if it is uploaded project or imported from galaxy
                     */
                    if (uploadedProject) {
                        proteinGroup.setIndex(i++);
                        proteinGroup.setProteinInference(arr[proteinFileHeaderIndexerMap.get(table_headers.Protein_Inference)].replace("0", "Single Protein").replace("1", "Related").replace("2", "Related & Unrelated").replace("3", "Unrelated"));

                        if (proteinGroup.getProteinInference().trim().isEmpty()) {
                            proteinGroup.setProteinInference("No Information");
                            proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
                        } else {
                            if (!proteinInferenceMap.containsKey(proteinGroup.getProteinInference())) {
                                proteinInferenceMap.put(proteinGroup.getProteinInference(), new LinkedHashSet<>());
                            }
                            proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
                        }
                        proteinGroup.setValidation(arr[proteinFileHeaderIndexerMap.get(table_headers.Validation)]);
                        if (proteinGroup.getValidation().trim().isEmpty() || proteinGroup.getValidation().trim().equalsIgnoreCase("-1")) {
                            proteinGroup.setValidation("No Information");
                            proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
                        } else {
                            proteinGroup.setValidation(proteinGroup.getValidation().replace("0", "Not Validated").replace("1", "Doubtful").replace("2", "Confident"));
                            if (!proteinValidationMap.containsKey(proteinGroup.getValidation())) {
                                proteinValidationMap.put(proteinGroup.getValidation(), new LinkedHashSet<>());
                            }
                            proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
                        }
                    } else {
                        proteinGroup.setIndex(Integer.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Index)]));
                        proteinGroup.setGeneName(arr[proteinFileHeaderIndexerMap.get(table_headers.Gene_Name)]);
                        proteinGroup.setMW(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.MW_kDa)]));
                        if (proteinGroup.getMW() > maxMW) {
                            maxMW = proteinGroup.getMW();
                        }
                        proteinGroup.setPossibleCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Possible_Coverage_Pers)]));
                        proteinGroup.setCoverage(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Coverage_Pers)]));
                        int pc = (int) Math.round(proteinGroup.getCoverage());
                        if (!proteinCoverageMap.containsKey(pc)) {
                            proteinCoverageMap.put(pc, new LinkedHashSet<>());
                        }
                        proteinCoverageMap.get(pc).add(proteinGroup.getProteinGroupKey());
                        proteinGroup.setSpectrumCounting(Double.valueOf(arr[proteinFileHeaderIndexerMap.get(table_headers.Spectrum_Counting)]));
                        if (proteinGroup.getSpectrumCounting() > maxMS2Quant) {
                            maxMS2Quant = proteinGroup.getSpectrumCounting();
                        }
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
                            proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
                        } else {
                            if (!proteinInferenceMap.containsKey(proteinGroup.getProteinInference())) {
                                proteinInferenceMap.put(proteinGroup.getProteinInference(), new LinkedHashSet<>());
                            }
                            proteinInferenceMap.get(proteinGroup.getProteinInference()).add(proteinGroup.getProteinGroupKey());
                        }

                        proteinGroup.setSecondaryAccessions(arr[proteinFileHeaderIndexerMap.get(table_headers.Secondary_Accessions)]);

                        proteinGroup.setUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Unique_Peptides)]));
                        proteinGroup.setValidatedUniqueNumber(Integer.parseInt(arr[proteinFileHeaderIndexerMap.get(table_headers.Number_Validated_Unique_Peptides)]));
                        proteinGroup.setValidation(arr[proteinFileHeaderIndexerMap.get(table_headers.Validation)]);
                        if (proteinGroup.getValidation().trim().isEmpty()) {
                            proteinGroup.setValidation("No Information");
                            proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
                        } else {
                            if (!proteinValidationMap.containsKey(proteinGroup.getValidation())) {
                                proteinValidationMap.put(proteinGroup.getValidation(), new LinkedHashSet<>());
                            }
                            proteinValidationMap.get(proteinGroup.getValidation()).add(proteinGroup.getProteinGroupKey());
                        }

                    }

                    if (proteinsMap.containsKey(proteinGroup.getProteinGroupKey())) {
                        System.out.println("at Error in proteins file key , key repeated : " + proteinGroup.getProteinGroupKey());
                    } else {
                        proteinsMap.put(proteinGroup.getProteinGroupKey(), proteinGroup);
                    }
                    proteinGroup.getProteinGroupSet().stream().map((acc) -> {
                        if (!protein_relatedProteins_Map.containsKey(acc)) {
                            Set<ProteinGroupObject> protenHashSet = new LinkedHashSet<>();
                            protein_relatedProteins_Map.put(acc, protenHashSet);
                        }

                        if (!protein_ProteinGroup_Map.containsKey(acc)) {
                            Set<String> protenHashSet = new LinkedHashSet<>();
                            protein_ProteinGroup_Map.put(acc, protenHashSet);
                        }
                        protein_ProteinGroup_Map.get(acc).add(proteinGroup.getProteinGroupKey());
                        return acc;
                    }).forEachOrdered((acc) -> {
                        protein_relatedProteins_Map.get(acc).add(proteinGroup);
                    });
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
            proteinsMap.keySet().forEach((String key) -> {
                String[] accs;
                accs = key.split("-_-");
                ProteinGroupObject pgo = proteinsMap.get(key);
                for (String acc : accs) {
                    accToGroupKeyMap.put(acc.trim(), pgo);
                }
            });
        }

        public Map<String, ProteinGroupObject> getAccToGroupKeyMap() {
            return accToGroupKeyMap;
        }

        /**
         * Get proteins map when the files are ready
         *
         * @return Proteins map (accession to proteins object)
         * @throws Exception
         */
        @Override
        public Map<String, ProteinGroupObject> call() throws Exception {
            return this.proteinsMap;
        }

        /**
         * Get Protein to related protein map
         *
         * @return accession to protein objects map
         */
        public Map<String, Set<ProteinGroupObject>> getProtein_relatedProteins_Map() {
            return protein_relatedProteins_Map;
        }

        /**
         * Get proteins map
         *
         * @return Proteins map (accession to proteins object)
         */
        public Map<String, ProteinGroupObject> getProteinsMap() {
            return proteinsMap;
        }

        /**
         * Get protein inference map.
         *
         * @return protein inference map
         */
        public Map<String, Set<Comparable>> getProteinInferenceMap() {
            return proteinInferenceMap;
        }

        /**
         * Get protein validation map.
         *
         * @return protein validation map
         */
        public Map<String, Set<Comparable>> getProteinValidationMap() {
            return proteinValidationMap;
        }

        /**
         * Get map of proteins to the chromosome index.
         *
         * @return proteins to the chromosome index map
         */
        public Map<Integer, Set<Comparable>> getChromosomeMap() {
            return chromosomeMap;
        }

        /**
         * Get protein to coverage value map.
         *
         * @return protein to coverage value map
         */
        public TreeMap<Comparable, Set<Comparable>> getProteinCoverageMap() {
            return proteinCoverageMap;
        }

        /**
         * Get protein to PSM numbers map.
         *
         * @return Protein to PSM numbers map
         */
        public TreeMap<Comparable, Set<Comparable>> getProteinPSMNumberMap() {
            return proteinPSMNumberMap;
        }

        /**
         * Get maximum molecular weight
         *
         * @return double value
         */
        public double getMaxMW() {
            return maxMW;
        }

        /**
         * Get maximum MS2 quantitative
         *
         * @return double value
         */
        public double getMaxMS2Quant() {
            return maxMS2Quant;
        }

        /**
         * Get maximum peptides number (highest number of peptides from a
         * protein)
         *
         * @return number of peptides
         */
        public int getMaxPeptideNumber() {
            return maxPeptideNumber;
        }

        /**
         * Get maximum PSM number (highest number of PSM from a protein)
         *
         * @return number of PSM
         */
        public int getMaxPsmNumber() {
            return maxPsmNumber;
        }

        public Set<String> getProtein_ProteinGroupkeys(String accession) {
            return protein_ProteinGroup_Map.get(accession);
        }

        public Map<String, Set<String>> getProtein_ProteinGroup_Map() {
            return protein_ProteinGroup_Map;
        }

    }

    /**
     * This class is used to create task that is used to process FASTA file.
     */
    private class ProcessFastaFileTask implements Callable<LinkedHashMap<String, ProteinSequence>> {

        /**
         * Map of protein accession mapped to protein object imported from FASTA
         * file.
         */
        private final Map<Object, ProteinGroupObject> fastaProteinMap;
        /**
         * Map of protein accession mapped to The representation of a
         * ProteinSequence.
         */
        private LinkedHashMap<String, ProteinSequence> fastaProteinSequenceMap;

        /**
         * Constructor to initialise the main variables.
         *
         * @param fasta_file input FASTA file
         */
        public ProcessFastaFileTask(File fasta_file) {
            this.fastaProteinMap = new LinkedHashMap<>();
            fastaProteinSequenceMap = null;
            FileInputStream inStream;
            try {
                inStream = new FileInputStream(fasta_file);
                FastaReader<ProteinSequence, AminoAcidCompound> fastaReader
                        = new FastaReader<>(
                                inStream,
                                new GenericFastaHeaderParser<>(),
                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
                fastaProteinSequenceMap = fastaReader.process();
            } catch (IOException | NumberFormatException ex) {

            }
        }

        /**
         * Get map of protein accession mapped to protein object imported from
         * FASTA file.
         *
         * @return protein objects map
         */
        public Map<Object, ProteinGroupObject> getFastaProteinMap() {
            return fastaProteinMap;
        }

        /**
         * get map of protein accession mapped to The representation of a
         * ProteinSequence.
         *
         * @return Protein sequence mapped to accessions
         */
        public LinkedHashMap<String, ProteinSequence> getFastaProteinSequenceMap() {
            return fastaProteinSequenceMap;
        }

        /**
         * Get protein sequences map when the files are ready
         *
         * @return protein sequence map (accession to protein sequence object)
         * @throws Exception
         */
        @Override
        public LinkedHashMap<String, ProteinSequence> call() throws Exception {
            return fastaProteinSequenceMap;
        }

    }

    /**
     * This class is used to create task that is used to process FASTA file.
     */
    private class ProcessPathwayMatcherFilesTask implements Callable<Map<String, Map<String, NetworkGraphNode>>> {

        /**
         * Map of protein accession mapped to list of protein/ for proteoform
         * node object imported from Pathway matcher files file.
         */
        private final Map<String, Map<String, NetworkGraphNode>> nodes;

        /**
         * Constructor to initialise the main variables.
         *
         * @param fasta_file input FASTA file
         */
        public ProcessPathwayMatcherFilesTask(GalaxyTransferableFile proteoform_file) {

            this.nodes = new HashMap<>();
            List<String> readerSet = new ArrayList<>();
            String line;
            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(proteoform_file.getFile()), "UTF-8");
                try ( // Always wrap FileReader in BufferedReader.
                        BufferedReader bufferedReader = new BufferedReader(is)) {
                    while ((line = bufferedReader.readLine()) != null) {
                        line = line.replace("\"", "");
                        readerSet.add(line);
                    }
                    int i = 0;
                    readerSet.stream().map((parsline) -> parsline.trim()).forEachOrdered((proteoformId) -> {

                        if (!proteoformId.endsWith(";")) {
                            proteoformId = proteoformId + ";";
                        }
                        String accession = proteoformId.split(";")[0].trim();
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
        }

        /**
         * Get map of protein accession to list of protein/ for proteoform node
         * object imported from Pathway matcher files file.
         *
         * @return Pathway interaction nodes (accession to list of protein and
         * its proteoforms)
         * @throws Exception
         */
        @Override
        public Map<String, Map<String, NetworkGraphNode>> call() throws Exception {
            return nodes;
        }

    }

    /**
     * Update the protein with Reactom pathway information
     *
     * @param proteinNodes nodes of protein to update
     * @return set of network graph edges
     */
    public Set<NetworkGraphEdge> updateProteinPathwayInformation(Map<String, ProteinGroupObject> proteinNodes) {

        proteinNodes.values().stream().filter((protein) -> !(protein.isProteoformUpdated())).forEachOrdered((protein) -> {
            Map<String, NetworkGraphNode> subNodes;
            try {
                if (uploadedProject) {
                    subNodes = null;
                } else {
                    subNodes = processPathwayMatcherFilesTask.call().get(protein.getAccession());
                }

                NetworkGraphNode parentNode = new NetworkGraphNode(protein.getAccession(), true, true) {
                    @Override
                    public void selected(String id) {
                        System.out.println("at selected parent node  id " + id);
                    }

                };
                if (subNodes == null) {
                    NetworkGraphNode singleNode = new NetworkGraphNode(protein.getAccession() + ";", true, false) {
                        @Override
                        public void selected(String id) {
                            System.out.println("at selected single id " + id);
                        }

                    };
                    singleNode.setType(3);
                    singleNode.setParentNode(parentNode);
                    protein.addProteoformNode(singleNode);
                } else {
                    subNodes.values().stream().map((n) -> {
                        n.setParentNode(parentNode);
                        return n;
                    }).forEachOrdered((n) -> {
                        protein.addProteoformNode(n);
                    });

                }

                protein.setParentNode(parentNode);
                protein.setProteoformUpdated(true);
            } catch (Exception ex) {
                System.out.println("Error : line 2941 " + ex);
            }
        });

        //get all edges
        Set<String[]> edgesData = getPathwayEdges(proteinNodes.keySet());
        Set<NetworkGraphEdge> edges = new HashSet<>();
        Map<String, NetworkGraphNode> tNodes = new HashMap<>();
        edgesData.stream().map((String[] arr) -> {
            ProteinGroupObject p1 = proteinNodes.get(arr[0].split(";")[0].split("-")[0]);
            ProteinGroupObject p2 = proteinNodes.get(arr[1].split(";")[0].split("-")[0]);
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
                    NetworkGraphNode parentNode = new NetworkGraphNode(n1.getAccession(), false, true) {
                        @Override
                        public void selected(String id) {
                            System.out.println("at selected parent node  id " + id);
                        }
                    };
                    n1.setParentNode(parentNode);
                    tNodes.put(n1.getAccession(), parentNode);
                }
            } else if (tNodes.containsKey(arr[0])) {
                n1 = tNodes.get(arr[0]);

            } else {//if (p1.getProteoformsNodes().containsKey(arr[0]))
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
                    NetworkGraphNode parentNode = new NetworkGraphNode(n2.getAccession(), false, true) {
                        @Override
                        public void selected(String id) {
                            System.out.println("at selected parent node  id " + id);
                        }

                    };
                    tNodes.put(n2.getAccession(), parentNode);

                }
                n2.setParentNode(tNodes.get(n2.getAccession()));
            } else if (tNodes.containsKey(arr[1])) {
                n2 = tNodes.get(arr[1]);

            } else if (p2 != null && p2.getProteoformsNodes() != null) {//if (p1.getProteoformsNodes().containsKey(arr[1])) 
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
        proteinNodes.values().forEach((protein) -> {
            edges.addAll(protein.getLocalEdges());
        });
        return edges;

    }

    /**
     * Get the pathway graph edges
     *
     * @param proteinAcc set of proteins accessions
     * @return the protein network
     */
    public abstract Set<String[]> getPathwayEdges(Set<String> proteinAcc);

}
