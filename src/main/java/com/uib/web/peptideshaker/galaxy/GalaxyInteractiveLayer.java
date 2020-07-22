package com.uib.web.peptideshaker.galaxy;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyHistoryHandler;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyToolsHandler;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.uib.web.peptideshaker.galaxy.client.GalaxyClient;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.VaadinSession;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represents main galaxy interactive layer that interact with both
 * Galaxy server and Online PeptideShaker
 *
 * @author Yehia Farag
 */
public abstract class GalaxyInteractiveLayer {

    /**
     * Main Galaxy Server instance.
     */
    private GalaxyClient Galaxy_Instance;
    /**
     * Galaxy Server history management system
     */
    private final GalaxyHistoryHandler historyHandler;
    /**
     * Galaxy Server tools management handler
     */
    private GalaxyToolsHandler toolsHandler;
    /**
     * User data files folder
     */
    private File user_folder;
    /**
     * User overview information list for welcome page left panel
     */
    private final List<String> userOverViewList;
    /**
     * Decimal Format for memory usage
     */
    private final DecimalFormat dsFormater = new DecimalFormat("#.##");

    /**
     * Constructor to initialise the main Galaxy history handler.
     */
    public GalaxyInteractiveLayer() {
        this.userOverViewList = new ArrayList<>();
        this.historyHandler = new GalaxyHistoryHandler() {
            @Override
            public Set<String> getCsf_pr_Accession_List() {
                return GalaxyInteractiveLayer.this.getCsf_pr_Accession_List();
            }

            @Override
            public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                return GalaxyInteractiveLayer.this.getPathwayEdges(proteinAcc);
            }

            @Override
            public void updatePresenterLayer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView, Set<String> toDeleteMap) {
                //update history in the system                 
                System.out.println("at before update presenter is jobs still in progress " + jobsInProgress);
                GalaxyInteractiveLayer.this.updatePresenterLayer(historyFilesMap, jobsInProgress, updatePresenterView);
                if (!jobsInProgress && toDeleteMap != null && toolsHandler != null) {
                    toDeleteMap.forEach((galaxyId) -> {
                        toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), galaxyId.split(";")[0], galaxyId.split(";")[1], galaxyId.split(";").length == 3);
                    });
                    toDeleteMap.clear();
                }
                if (userOverViewList.size() == 3) {
                    userOverViewList.set(1, historyHandler.getDatasetsNumber() + "");
                    userOverViewList.set(2, historyHandler.getFilesNumber() + "");
                    try {
                        userOverViewList.set(3, dsFormater.format(historyHandler.getMemoryUsage()) + " GB");
                    } catch (IllegalArgumentException exp) {
                        userOverViewList.set(3, (historyHandler.getMemoryUsage()) + "");
                    }
                }
            }

            @Override
            public String getDatasetSharingLink(int dsKey) {
                return GalaxyInteractiveLayer.this.getDatasetSharingLink(dsKey);
            }
        };

    }

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userAPI Galaxy user API key
     * @param userDataFolderUrl main folder for storing users data
     * @return System connected to Galaxy server or not
     */
    public boolean connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
        try {
            userOverViewList.clear();
            Galaxy_Instance = new GalaxyClient(galaxyServerUrl, userAPI);
            user_folder = new File(userDataFolderUrl, Galaxy_Instance.getApiKey() + "");
            user_folder.mkdir();

            historyHandler.setGalaxyConnected(Galaxy_Instance, user_folder);
            //on connection update history

            toolsHandler = new GalaxyToolsHandler(Galaxy_Instance.getToolsClient(), Galaxy_Instance.getWorkflowsClient(), Galaxy_Instance.getHistoriesClient()) {
                @Override
                public void jobIsExecuted() {
                    historyHandler.forceUpdateGalaxyFileSystem();
                }
            };
            VaadinSession.getCurrent().setAttribute("ApiKey", Galaxy_Instance.getApiKey());
            UsersClient userClient = Galaxy_Instance.getUsersClient();
            User user = userClient.getUsers().get(0);
            userOverViewList.add(user.getUsername().replace("public_user", "Guest User <i style='font-size: 10px;position: relative;top: -23px;left: 101px;'>(public data)</i>"));
            userOverViewList.add(historyHandler.getDatasetsNumber() + "");
            userOverViewList.add(historyHandler.getFilesNumber() + "");
            userOverViewList.add(historyHandler.getMemoryUsage());
            userOverViewList.add(toolsHandler.getSearch_GUI_Tool_version());
            userOverViewList.add(toolsHandler.getPeptideShaker_Tool_Version());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("at Error:  galaxy connection  " + e);
            return false;
        }
        return true;
    }

    /**
     * Get user API key for galaxy server
     *
     * @return user API key
     */
    public String getAPIKey() {
        return Galaxy_Instance.getApiKey();
    }

    /**
     * Get user overview information list for welcome page left panel
     *
     * @return list of user information username/#datasets/#files/memory used on
     * Galaxy Server
     */
    public List<String> getUserOverViewList() {
        return userOverViewList;
    }

    /**
     * Run Online Peptide-Shaker search and analysis work-flow
     *
     * @param projectName new project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputFilesIdsList list of MGF or Raw file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParameters User input search parameters
     * @param quant execute work-flow for quantification dataset (raw files
     * included)
     */
    public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
        Map<String, String> inputFilesMap = new LinkedHashMap<>();
        inputFilesIdsList.forEach((inputFileId) -> {
            if (quant) {
                inputFilesMap.put(inputFileId, historyHandler.getRawFilesMap().get(inputFileId).getName());
            } else {
                inputFilesMap.put(inputFileId, historyHandler.getMgfFilesMap().get(inputFileId).getName());
            }
        });
        if (inputFilesMap.isEmpty()) {
            return;
        }
        toolsHandler.execute_SearchGUI_PeptideShaker_WorkFlow(Galaxy_Instance.getGalaxyUrl(), projectName, fastaFileId, searchParameterFileId, inputFilesMap, searchEnginesList, historyHandler.getWorkingHistoryId(), searchParameters, quant);

    }

    /**
     * Save user input search parameters file into galaxy to be reused in future
     *
     * @param searchParameters search parameters file.
     * @param newFile is new or just edited file
     * @return updated get user Search Settings Files Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean newFile) {
        if (toolsHandler != null) {
            return toolsHandler.saveSearchGUIParameters(Galaxy_Instance.getGalaxyUrl(), user_folder, historyHandler.getSearchSettingsFilesMap(), historyHandler.getWorkingHistoryId(), searchParameters, newFile);
        }
        return null;

    }

    /**
     * Get the main search settings .par files Map on Galaxy Server
     *
     * @return search Settings Files .par Map(Galaxy dataset id to galaxy file)
     */
    public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getSearchSettingsFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the available FASTA files Map on Galaxy Server
     *
     * @return FASTA Files Map (Galaxy dataset id to galaxy file)
     */
    public Map<String, GalaxyFileObject> getFastaFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getFastaFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the available MGF files Map on Galaxy Server
     *
     * @return mgfFilesMap
     *
     */
    public Map<String, GalaxyFileObject> getMgfFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getMgfFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Get the available Raw files Map on Galaxy Server
     *
     * @return rawfFilesMap
     *
     */
    public Map<String, GalaxyFileObject> getRawFilesMap() {
        if (historyHandler != null) {
            return historyHandler.getRawFilesMap();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Upload Files (FASTA and MGF files to Galaxy Server)
     *
     * @param toUploadFiles list of the files to be uploaded to Galaxy Server
     * @return files are successfully uploaded to Galaxy Server
     */
    public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
        /**
         * upload file to galaxy server
         */
        boolean check = toolsHandler.uploadToGalaxy(historyHandler.getWorkingHistoryId(), toUploadFiles);
        return check;
    }

    /**
     * Delete action for files from Galaxy Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
    public void deleteDataset(GalaxyFileObject fileObject) {
        if (fileObject.getType().equalsIgnoreCase("Web Peptide Shaker Dataset")) {
            PeptideShakerVisualizationDataset vDs = (PeptideShakerVisualizationDataset) fileObject;
            for (GalaxyTransferableFile moffId : vDs.getMoff_quant_file()) {
                toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), moffId.getGalaxyId(), false);
            }
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getMoffGalaxyId(), true);
            for (GalaxyTransferableFile moffId : vDs.getCuiFileSet()) {
                toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), moffId.getGalaxyId(), false);
            }
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getCuiListGalaxyId(), true);
            for (GalaxyFileObject moffId : vDs.getIndexedMGFFilesMap().values()) {
                toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), moffId.getGalaxyId(), false);
            }
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getIndexedMgfGalaxyId(), true);
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getMoffGalaxyId(), true);
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getGalaxyId(), false);
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), vDs.getHistoryId(), vDs.getSearchGUIFile().getGalaxyId(), false);

        } else {
            toolsHandler.deleteDataset(Galaxy_Instance.getGalaxyUrl(), fileObject.getHistoryId(), fileObject.getGalaxyId(), false);
        }
        historyHandler.forceUpdateGalaxyFileSystem();
//        historyHandler.jobIsExecuted(true);
    }

    /**
     * Update and synchronise the data on Galaxy Server with the local file
     * system on Online Peptide Shaker
     *
     * @param historyFilesMap List of available files(datasets) available on
     * galaxy
     * @param jobsInProgress there is currently jobs running on Galaxy Server
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    public abstract void updatePresenterLayer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView);

    /**
     * Get pathway information for accessions list
     *
     * @param proteinAcc protein accession list
     * @return edges data for the selected accessions
     *
     */
    public abstract Set<String[]> getPathwayEdges(Set<String> proteinAcc);

    /**
     * Get list of accessions available on csf-pr in order to allow mapping data
     * to csf-pr
     *
     * @return set of Uni-prot protein accessions available on csf-pr
     */
    public abstract Set<String> getCsf_pr_Accession_List();

    /**
     * Retrieve dataset details from index to share in link
     *
     * @param dsKey dataset public key
     * @return encoded dataset details to visualise
     */
    public abstract String getDatasetSharingLink(int dsKey);
}
