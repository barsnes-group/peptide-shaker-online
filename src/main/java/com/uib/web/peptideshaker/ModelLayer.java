package com.uib.web.peptideshaker;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.dal.DatabaseLayer;
import com.uib.web.peptideshaker.galaxy.GalaxyInteractiveLayer;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.VaadinSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class responsible for handling the data on galaxy or on any other
 * resources including the database
 *
 * @author Yehia Farag
 */
public abstract class ModelLayer {

    /**
     * The Main Galaxy server layer that interact with Galaxy server.
     */
    private GalaxyInteractiveLayer Galaxy_Interactive_Layer;
    private Set<String> csf_pr_Accession_List;
    private final DatabaseLayer Database_Layer;

    /**
     * Initialise data access layer
     *
     * @param availableGalaxyServer galaxy server is available
     */
    public ModelLayer(boolean availableGalaxyServer) {
        this.Database_Layer = new DatabaseLayer();
        this.csf_pr_Accession_List = this.Database_Layer.getCsfprAccList();
        if (availableGalaxyServer) {
            this.Galaxy_Interactive_Layer = new GalaxyInteractiveLayer() {
                @Override
                public String getDatasetSharingLink(int dsKey) {
                    return Database_Layer.getDatasetSharingLink(dsKey);
                }
                @Override
                public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                    return Database_Layer.getPathwayEdges(proteinAcc);
                }

                @Override
                public Set<String> getCsf_pr_Accession_List() {
                    return csf_pr_Accession_List;
                }

                @Override
                public void updatePresenterLayer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView) {
                    if (historyFilesMap.size() == 1 && historyFilesMap.keySet().iterator().next().contains("_ExternalDS")) {
                       PeptideShakerVisualizationDataset psDs =  (PeptideShakerVisualizationDataset) historyFilesMap.values().iterator().next();
                        viewDataset(psDs);
                    } else {
                        Map<String, GalaxyFileObject> tempHistoryFilesMap = new LinkedHashMap<>();
                        tempHistoryFilesMap.putAll(((LinkedHashMap<String, GalaxyFileObject>) VaadinSession.getCurrent().getAttribute("uploaded_projects_" + this.getAPIKey())));
                        tempHistoryFilesMap.putAll(historyFilesMap);
                        updatePresenter(tempHistoryFilesMap, historyFilesMap, jobsInProgress);

                    }
                }
            };
        }

    }

    /**
     * View selected dataset
     *
     * @param peptideShakerVisualizationDataset web PS visualisation dataset
     * object
     */
    public abstract void viewDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset);

    /**
     * Update system presenter with current data including currently running
     * processes
     *
     * @param tempHistoryFilesMap list of all files including under processing
     * files or datasets
     * @param historyFilesMap list of already processed files and datasets
     * @param jobsInProgress check if there is still jobs in progress
     */
    public abstract void updatePresenter(Map<String, GalaxyFileObject> tempHistoryFilesMap, Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress);

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userAPI Galaxy user API key
     * @param userDataFolderUrl main folder for storing users data
     * @return System connected to Galaxy server or not
     */
    public boolean connectToGalaxyServer(String galaxyServerUrl, String userAPI, String userDataFolderUrl) {
        if (Galaxy_Interactive_Layer != null) {
            return this.Galaxy_Interactive_Layer.connectToGalaxyServer(galaxyServerUrl, userAPI, userDataFolderUrl);
        }
        return false;
    }

    /**
     * Get user overview information list for welcome page left panel
     *
     * @return list of user information username/#datasets/#files/memory used on
     * Galaxy Server
     */
    public List<String> getUserOverViewList() {
        return this.Galaxy_Interactive_Layer.getUserOverViewList();
    }

    /**
     * Save user input search parameters file into galaxy to be reused in future
     *
     * @param searchParameters search parameters file.
     * @param newFile is new or just edited file
     * @return updated get user Search Settings Files Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean newFile) {
        return this.Galaxy_Interactive_Layer.saveSearchGUIParameters(searchParameters, newFile);

    }

    /**
     * Upload Files (FASTA and MGF files to Galaxy Server)
     *
     * @param toUploadFiles list of the files to be uploaded to Galaxy Server
     * @return files are successfully uploaded to Galaxy Server
     */
    public boolean uploadDataFiles(PluploadFile[] toUploadFiles) {

        return this.Galaxy_Interactive_Layer.uploadToGalaxy(toUploadFiles);
    }

    /**
     * Delete action for files from Galaxy Server
     *
     * @param fileObject the file to be removed from Galaxy Server
     */
    public void deleteDataset(GalaxyFileObject fileObject) {
        this.Galaxy_Interactive_Layer.deleteDataset(fileObject);
    }

    /**
     * Get available csf-pr protein accession list
     *
     * @return protein accession list
     */
    public Set<String> getCsf_pr_Accession_List() {
        return csf_pr_Accession_List;
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
     * @param quant the dataset is quantification dataset
     */
    public void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant) {
        this.Galaxy_Interactive_Layer.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParameters, quant);
    }

    /**
     * Store and retrieve dataset details index to share in link
     *
     * @param dsDetails encoded dataset details to store in database
     * @param dsUniqueKey
     * @returndataset details public key
     */
    public int insertDatasetSharingLink(String dsDetails,String dsUniqueKey) {
        return Database_Layer.insertDatasetSharingLink(dsDetails,dsUniqueKey);
    }
}
