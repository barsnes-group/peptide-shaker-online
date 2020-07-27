package com.uib.web.peptideshaker.galaxy.utilities;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.OutputDataset;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import pl.exsio.plupload.PluploadFile;

/**
 * This class responsible for interaction with tools on Galaxy Server
 * (Search-GUI and Peptide Shaker)
 *
 * @author Yehia Farag
 */
public abstract class GalaxyToolsHandler {

    /**
     * Galaxy Server contains valid Search-GUI and Peptide Shaker tools.
     */
    private boolean validIDToolsAvailable;
    /**
     * Galaxy Server contains valid Search-GUI and Peptide Shaker tools.
     */
    private boolean validQuantToolsAvailable;

    /**
     * The main galaxy Work-Flow Client on Galaxy Server.
     */
    private final WorkflowsClient galaxyWorkFlowClient;
    /**
     * The main galaxy History Client on Galaxy Server.
     */
    private final HistoriesClient galaxyHistoriesClient;
    /**
     * The main galaxy Tools Client on Galaxy Server.
     */
    private final ToolsClient galaxyToolClient;
    /**
     * SearchGUI tool representation for the tool on Galaxy Server.
     */
    private Tool search_GUI_Tool = null;
    /**
     * Peptide Shaker tool representation for the tool on Galaxy Server.
     */
    private Tool peptideShaker_Tool = null;
    /**
     * Peptide Shaker tool representation for the tool on Galaxy Server.
     */
    private Tool moff_Tool = null;
    /**
     * The working history storage representation for the user on Galaxy Server.
     */
    private History galaxyWorkingHistory;

    /**
     * Get SearchGui tool object
     *
     * @return galaxy tool
     */
    public Tool getSearch_GUI_Tool() {
        return search_GUI_Tool;
    }

    /**
     * Get PeptideShaker tool object
     *
     * @return galaxy tool object
     */
    public Tool getPeptideShaker_Tool() {
        return peptideShaker_Tool;
    }

    /**
     * Get the release version of SearchGUI tool
     *
     * @return release version
     */
    public String getSearch_GUI_Tool_version() {
        if (search_GUI_Tool != null) {
            return search_GUI_Tool.getVersion();
        } else {
            return "Not available";
        }

    }

    /**
     * Get the release version of PeptideShaker tool
     *
     * @return release version
     */
    public String getPeptideShaker_Tool_Version() {
        if (peptideShaker_Tool != null) {
            return peptideShaker_Tool.getVersion();
        } else {
            return "Not available";
        }
    }

    /**
     * Constructor to initialise the main data structure and other variables.
     *
     * @param galaxyToolClient The main galaxy Tools Client on Galaxy Server.
     * @param galaxyWorkFlowClient The main galaxy Work-Flow Client on Galaxy
     * Server.
     * @param galaxyHistoriesClient The main galaxy History Client on Galaxy
     * Server
     */
    public GalaxyToolsHandler(ToolsClient galaxyToolClient, WorkflowsClient galaxyWorkFlowClient, HistoriesClient galaxyHistoriesClient) {

        this.galaxyWorkFlowClient = galaxyWorkFlowClient;
        this.galaxyHistoriesClient = galaxyHistoriesClient;
        List<History> availableHistories = galaxyHistoriesClient.getHistories();
        for (History h : availableHistories) {
            if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
                galaxyWorkingHistory = h;
                break;
            }
        }

        if (galaxyWorkingHistory == null) {
            galaxyWorkingHistory = galaxyHistoriesClient.getHistories().get(0);
        }
        this.galaxyToolClient = galaxyToolClient;
        try {
            List<ToolSection> toolSections = galaxyToolClient.getTools();
            String PSVersion = VaadinSession.getCurrent().getAttribute("psVersion") + "";
            String SGVersion = VaadinSession.getCurrent().getAttribute("searchGUIversion") + "";
            String moffVersion = VaadinSession.getCurrent().getAttribute("moffvirsion") + "";
            for (ToolSection secion : toolSections) {
                List<Tool> tools = secion.getElems();
                if (tools == null) {
                    continue;
                }
                for (Tool tool : tools) {
                    if (tool == null || tool.getName() == null) {
                        continue;
                    } else if (tool.getName().equalsIgnoreCase("Search GUI") && tool.getVersion().equalsIgnoreCase(SGVersion)) {
                        search_GUI_Tool = tool;
                    } else if (tool.getName().equalsIgnoreCase("Peptide Shaker") && tool.getVersion().equalsIgnoreCase(PSVersion)) {
                        peptideShaker_Tool = tool;
                    } else if (tool.getName().equalsIgnoreCase("moFF") && tool.getVersion().equalsIgnoreCase(moffVersion)) {
                        moff_Tool = tool;
                    }

                }

            }
            if (peptideShaker_Tool != null && search_GUI_Tool != null && moff_Tool != null) {
                validIDToolsAvailable = true;
                validQuantToolsAvailable = true;
            } else if (peptideShaker_Tool != null && search_GUI_Tool != null) {
                validIDToolsAvailable = true;
                validQuantToolsAvailable = false;
            } else {
                validIDToolsAvailable = false;
                validQuantToolsAvailable = false;
            }
        } catch (Exception e) {
            if (e.toString().contains("Service Temporarily Unavailable")) {
                Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                UI.getCurrent().getSession().close();
            } else {
                System.out.println("at tools are not available");
                e.printStackTrace();
            }
        }
    }

    /**
     * Re-Index the files MGF files (Convert the stored files in MGF file format
     * to Tab separated format to support byte serving on the server side)
     *
     * @param id file id on Galaxy Server
     * @param historyId the history id that the file belong to
     * @param workHistoryId the history id that the new re-indexed file will be
     * stored in
     *
     * @return new re-indexed file id on galaxy
     *
     */
    private String reIndexFile(String id, String peptideShakerViewID, String workHistoryId) {

        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/VAADIN/Galaxy-Workflow-convertMGF.ga");
        String json = readJsonFile(file).replace("updated_MGF", peptideShakerViewID);
        Workflow selectedWf = galaxyWorkFlowClient.importWorkflow(json);

        try {
            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(workHistoryId));
            WorkflowInputs.WorkflowInput input = new WorkflowInputs.WorkflowInput(id, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", input);
            final WorkflowOutputs output = galaxyWorkFlowClient.runWorkflow(workflowInputs);
            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            return output.getOutputIds().get(0);
        } catch (Exception e) {
            galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
        }
        return null;

    }

    /**
     * Save search settings file into galaxy
     *
     * @param galaxyURL Galaxy Server web address
     * @param user_folder Personal user folder where the user temporary files
     * are stored
     * @param searchParameters searchParameters .par file
     * @param workHistoryId The working History ID on Galaxy Server
     * @param searchParametersFilesMap The Search Parameters files (.par) Map
     * @param isNew the .par file is new
     * @return updated Search Parameters files (.par) Map
     */
    public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(String galaxyURL, File user_folder, Map<String, GalaxyTransferableFile> searchParametersFilesMap, String workHistoryId, IdentificationParameters searchParameters, boolean isNew) {

        String fileName = searchParameters.getName() + ".par";
        File file = new File(user_folder, fileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            IdentificationParameters.saveIdentificationParameters(searchParameters, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, file);
        request.setDatasetName(fileName);

        List<OutputDataset> excList = galaxyToolClient.upload(request).getOutputs();
        if (excList != null && !excList.isEmpty()) {
            OutputDataset oDs = excList.get(0);
            GalaxyFileObject ds = new GalaxyFileObject();
            ds.setName(oDs.getName());
            ds.setType("Search Parameters File (JSON)");
            ds.setHistoryId(workHistoryId);
            ds.setGalaxyId(oDs.getId());
            ds.setDownloadUrl(galaxyURL + "/datasets/" + ds.getGalaxyId() + "/display?to_ext=" + oDs.getDataTypeExt());
            GalaxyTransferableFile userFolderfile = new GalaxyTransferableFile(user_folder, ds, false);
            searchParametersFilesMap.put(ds.getGalaxyId(), userFolderfile);
            String temFileName = ds.getGalaxyId().replace("/", "_") + ds.getName();

            File updated = new File(user_folder, temFileName);
            try {
                updated.createNewFile();
                FileUtils.copyFile(file, updated);
                file.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return searchParametersFilesMap;
    }

    /**
     * Read and convert the work-flow file into string (JSON like string) so the
     * system can execute the work-flow
     *
     * @param file the input file
     * @return the JSON string of the file content
     */
    private String readJsonFile(File file) {
        String json = "";
        String line;
        try {
            FileReader fileReader = new FileReader(file);
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    json += (line);
                }
                // Always close files.
                bufferedReader.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading file '" + "'");
        }
        return json;
    }

    /**
     * Delete files (galaxy datasets) from Galaxy server
     *
     * @param galaxyURL Galaxy Server web address
     * @param historyId The Galaxy Server History ID where the file belong
     * @param dsId The file (galaxy dataset) ID on Galaxy Server
     */
    public void deleteDataset(String galaxyURL, String historyId, String dsId, boolean collection) {
        try {
            if (dsId == null || historyId == null || galaxyURL == null) {
                return;
            }
            String userAPIKey = VaadinSession.getCurrent().getAttribute("ApiKey") + "";
            String datatype;
            if (collection) {
                datatype = "dataset_collections";
            } else {
                datatype = "datasets";
            }

            URL url = new URL(galaxyURL + "/api/histories/" + historyId + "/contents/" + datatype + "/" + dsId + "?key=" + userAPIKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.addRequestProperty("Connection", "keep-alive");
            conn.addRequestProperty("DNT", "1");
            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.addRequestProperty("Pragma", "no-cache");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                final ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> payLoadParamMap = new LinkedHashMap<>();
                payLoadParamMap.put("deleted", Boolean.TRUE);
                payLoadParamMap.put("purged", Boolean.TRUE);
                String payload = mapper.writer().writeValueAsString(payLoadParamMap);
                writer.write(payload);
            }
            conn.connect();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
            }
            conn.disconnect();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Run Online Peptide-Shaker (Search-GUI -> Peptide Shaker) work-flow
     *
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputFileIdsList list of input MGF file dataset IDs on Galaxy
     * Server
     * @param searchEnginesList List of selected search engine names
     * @param historyId Galaxy history id that will store the results
     * @param searchParameters Search Parameter object
     * @param quant full quant pipe-line
     * @return invoking the workflow is successful
     */
    public boolean execute_SearchGUI_PeptideShaker_WorkFlow(String galaxyUrl, String projectName, String fastaFileId, String searchParameterFileId, Map<String, String> inputFileIdsList, Set<String> searchEnginesList, String historyId, IdentificationParameters searchParameters, boolean quant) {
        Workflow selectedWf = null;
        try {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            File workflowFile;
            WorkflowInputs.WorkflowInput inputDataFiles;

            if (quant && inputFileIdsList.size() == 1) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Quant-Single-Input.ga");//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
                inputDataFiles = new WorkflowInputs.WorkflowInput(inputFileIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);

            } else if (quant && inputFileIdsList.size() > 1) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Quant-Multiple-Input.ga");//Galaxy-Workflow-Web-Peptide-Shaker-Multi-MGF-2018.ga
                inputDataFiles = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, inputFileIdsList.keySet(), historyId);
            } else if (inputFileIdsList.size() == 1) {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Id-Single-Input.ga");
                inputDataFiles = new WorkflowInputs.WorkflowInput(inputFileIdsList.keySet().iterator().next(), WorkflowInputs.InputSourceType.HDA);
            } else {
                workflowFile = new File(basepath + "/VAADIN/Galaxy-Workflow-Full-Pipeline-Workflow-Id-Multiple-Input.ga");
                inputDataFiles = prepareWorkflowCollectionList(WorkflowInputs.InputSourceType.HDCA, inputFileIdsList.keySet(), historyId);
            }
            String jsonWorkflow = readJsonFile(workflowFile);
            jsonWorkflow = jsonWorkflow.replace("2.0.0_BETA.13", peptideShaker_Tool.getVersion()).replace("4.0.0_BETA.12", search_GUI_Tool.getVersion()).replace("2.0.3.0", moff_Tool.getVersion());
            jsonWorkflow = jsonWorkflow.replace("Label-SearchGUI-Results", projectName + "-SearchGUI-Results").replace("Label-PS", projectName + "-PS").replace("Label-MOFF", projectName + "-MOFF").replace("Label-Indexed-MGF", projectName + "-Indexed-MGF").replace("Label-MGF", projectName + "-MGF").replace("Label-Indexed-MGF-CUI", projectName + "-Indexed-MGF-CUI").replace("Label-original-input-MGF", projectName + "-original-input-MGF");
            /**
             * Search engines options.
             */
            Set<String> search_engines = new HashSet<>();
            searchEnginesList.forEach((searchEng) -> {
                search_engines.add(("\\\\\\\"" + searchEng + "\\\\\\\"").replace(" (Select for noncommercial use only)", "").replace("+", "").replace("-", ""));
            });
            jsonWorkflow = jsonWorkflow.replace("\\\"{\\\\\\\"engines\\\\\\\": [\\\\\\\"X!Tandem\\\\\\\"]}\\\"", "\\\"{\\\\\\\"engines\\\\\\\": " + search_engines.toString() + "}\\\"");
            selectedWf = galaxyWorkFlowClient.importWorkflow(jsonWorkflow);

            WorkflowInputs workflowInputs = new WorkflowInputs();
            workflowInputs.setWorkflowId(selectedWf.getId());
            workflowInputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));

            WorkflowInputs.WorkflowInput workflowInput0 = new WorkflowInputs.WorkflowInput(searchParameterFileId, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("0", workflowInput0);
            WorkflowInputs.WorkflowInput workflowInput1 = new WorkflowInputs.WorkflowInput(fastaFileId, WorkflowInputs.InputSourceType.HDA);
            workflowInputs.setInput("1", workflowInput1);
            workflowInputs.setInput("2", inputDataFiles);

            payLoad = "";
            if (quant) {
                if (inputFileIdsList.size() == 1) {
                    File file = new File(basepath + "/VAADIN/Single-Quant-Invoking.json");
                    payLoad = readJsonFile(file);
                    payLoad = payLoad.replace("History_ID", historyId).replace("Param_File_ID", workflowInputs.getInputs().get("0").getId()).replace("Fasta_File_ID", workflowInputs.getInputs().get("1").getId()).replace("Raw_File_ID", workflowInputs.getInputs().get("2").getId());
                } else {

                    File file = new File(basepath + "/VAADIN/Multi-Quant-Invoking.json");
                    payLoad = readJsonFile(file);
                    payLoad = payLoad.replace("\"History_ID\"", "\"" + historyId + "\"").replace("\"Param_File_ID\"", "\"" + workflowInputs.getInputs().get("0").getId() + "\"").replace("\"Fasta_File_ID\"", "\"" + workflowInputs.getInputs().get("1").getId() + "\"").replace("\"Input_List_ID\"", "\"" + workflowInputs.getInputs().get("2").getId() + "\"");
                }
            } else {
                if (inputFileIdsList.size() == 1) {
                    File file = new File(basepath + "/VAADIN/Single-id-Invoking.json");
                    payLoad = readJsonFile(file);
                    payLoad = payLoad.replace("History_ID", historyId).replace("Param_File_ID", workflowInputs.getInputs().get("0").getId()).replace("Fasta_File_ID", workflowInputs.getInputs().get("1").getId()).replace("MGF_MZML_File_ID", workflowInputs.getInputs().get("2").getId());
                } else {
                    File file = new File(basepath + "/VAADIN/Multi-id-Invoking.json");
                    payLoad = readJsonFile(file);
                    payLoad = payLoad.replace("\"History_ID\"", "\"" + historyId + "\"").replace("\"Param_File_ID\"", "\"" + workflowInputs.getInputs().get("0").getId() + "\"").replace("\"Fasta_File_ID\"", "\"" + workflowInputs.getInputs().get("1").getId() + "\"").replace("\"Input_List_ID\"", "\"" + workflowInputs.getInputs().get("2").getId() + "\"");
                }

            }

//            else if (quant && inputFileIdsList.size() > 1) {
//             workflowInputs.setInput("0",iputDataFiles );//fasta file
//                workflowInputs.setInput("2", workflowInput1);//search param
//                workflowInputs.setInput("1", workflowInput0);//spectra
//            } else {
//                workflowInputs.setInput("0", workflowInput0);
//                workflowInputs.setInput("1", workflowInput1);
//            }
//
            Thread t = new Thread(() -> {
                boolean check = invokeWorkflow(galaxyUrl, historyId, workflowInputs, inputFileIdsList.size() == 1, quant);
            });
            t.start();
            while (t.isAlive()) {

            }
            if (inputFileIdsList.size() != 1) {
                Notification.show(projectName.split("___")[0], "Progress will appear in projects overview when process start", Notification.Type.TRAY_NOTIFICATION);
            }
            final String workFlowId = selectedWf.getId();
            ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture f = exe.schedule(() -> {
                galaxyWorkFlowClient.deleteWorkflowRequest(workFlowId);
                jobIsExecuted((quant && (inputFileIdsList.size() != 1)));
            }, 10, TimeUnit.SECONDS);
            exe.shutdown();
            while (!f.isDone()) {
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: workflow invoking error " + e);
            if (selectedWf != null) {
                galaxyWorkFlowClient.deleteWorkflowRequest(selectedWf.getId());
            }
            jobIsExecuted(false);
            return false;
        }
    }

    /**
     * Prepares a work flow which takes as input a collection list.
     *
     * @param inputSource The type of input source for this work flow.
     * @param dsIds The set of files IDs on Galaxy Server that will be used to
     * generate the collection dataset
     * @param historyId The history ID on Galaxy Server where the collection
     * will be saved
     * @return A WorkflowInputs describing the work flow.
     */
    private WorkflowInputs.WorkflowInput prepareWorkflowCollectionList(WorkflowInputs.InputSourceType inputSource, Set<String> dsIds, String historyId) {

        CollectionResponse collectionResponse = constructFileCollectionList(historyId, dsIds);
        return new WorkflowInputs.WorkflowInput(collectionResponse.getId(),
                inputSource);
    }

    /**
     * Constructs a list collection from the given files within the given
     * history.
     *
     * @param historyId The history id on Galaxy Server to store the collection
     * in.
     * @param inputIds The IDs of the files (galaxy datasets) on Galaxy Server
     * that will be added to the collection.
     * @return A CollectionResponse object for the constructed collection.
     */
    private CollectionResponse constructFileCollectionList(String historyId, Set<String> inputIds) {
        CollectionDescription collectionDescription = new CollectionDescription();
        collectionDescription.setCollectionType("list");
        collectionDescription.setName("collection");
        inputIds.stream().map((inputId) -> {
            HistoryDatasetElement element = new HistoryDatasetElement();
            element.setId(inputId);
            element.setName(inputId);
            return element;
        }).forEachOrdered((element) -> {
            collectionDescription.addDatasetElement(element);
        });
        return galaxyHistoriesClient.createDatasetCollection(historyId, collectionDescription);
    }

    /**
     * Convert JSON object to readable java HashMap.
     *
     * @param object the JSON object to be converted
     * @return HashMap that contains the information
     * @throws JSONException
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
     * Convert JSON object to readable java List.
     *
     * @param object the JSON object to be converted
     * @return List that contains the information
     * @throws JSONException
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
     * Upload Files (FASTA and MGF files to Galaxy Server)
     *
     * @param workHistoryId The galaxy history ID where the files will be stored
     * @param toUploadFiles list of the files to be uploaded to Galaxy Server
     * @return files are successfully uploaded to Galaxy Server
     */
    public boolean uploadToGalaxy(String workHistoryId, PluploadFile[] toUploadFiles) {
        Thread t = new Thread(() -> {
            for (PluploadFile file : toUploadFiles) {
                File tFile = (File) file.getUploadedFile();
                final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(workHistoryId, tFile);
                if (file.getName().endsWith(".thermo.raw")) {
                    request.setFileType("thermo.raw");
                }

                request.setDatasetName(file.getName());
                galaxyToolClient.uploadRequest(request);
                tFile.delete();
            }
            ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
            exe.schedule(() -> {
                jobIsExecuted(false);
            }, 3, TimeUnit.SECONDS);
            exe.shutdown();
        });
        t.start();
        return true;
    }

    /**
     * Synchronise and update Online PEptideShaker file system with Galaxy
     * Server.
     *
     * @param keepfollow invoke history tracker
     */
    public abstract void jobIsExecuted(boolean keepfollow);
    String payLoad = "";

    /**
     * Delete files (galaxy datasets) from Galaxy server
     *
     * @param galaxyURL Galaxy Server web address
     * @param workflowId The Galaxy Server History ID where the file belong
     * @param dsId The file (galaxy dataset) ID on Galaxy Server
     */
    private boolean invokeWorkflow(String galaxyURL, String historyId, WorkflowInputs workflowInputs, boolean quant, boolean single) {
        try {

            String userAPIKey = VaadinSession.getCurrent().getAttribute("ApiKey") + "";
            URL url = new URL(galaxyURL + "/api/workflows/" + workflowInputs.getWorkflowId() + "/invocations" + "?key=" + userAPIKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
//            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.addRequestProperty("Host", galaxyURL.replace("http://", ""));
            conn.addRequestProperty("Content-Length", "2522");
//            conn.addRequestProperty("DNT", "1");
//            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
//            conn.addRequestProperty("Pragma", "no-cache");
//            conn.addRequestProperty("Referer", galaxyURL + "/workflows/run?id=" + workflowInputs.getWorkflowId());

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                writer.write(payLoad);
            }
            conn.connect();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
            }
            int stat = conn.getResponseCode();
            conn.disconnect();
            return stat == 200;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        return false;
    }
}
