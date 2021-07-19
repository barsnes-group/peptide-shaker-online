package com.uib.web.peptideshaker.handler;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.vaadin.server.VaadinSession;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.httpclient.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * This class is responsible for handling workflow related actions
 *
 * @author Yehia Mokhtar Farag
 */
public class WorkFlowHandler {

    private final AppManagmentBean appManagmentBean;
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss");

    /**
     *
     */
    public WorkFlowHandler() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
    }

    /**
     * get default search parameter object
     *
     * @return IdentificationParameters
     */
    public IdentificationParameters getDefaultIdentificationParameters() {
        return this.getIdentificationParameters(appManagmentBean.getAppConfig().getDefaultSearchParamPath());
    }

    /**
     * get dataset search parameter object
     *
     * @param path
     * @return IdentificationParameters
     */
    public IdentificationParameters getIdentificationParameters(String path) {
        try {
            File file = new File(path);
            return IdentificationParameters.getIdentificationParameters(file);
        } catch (IOException ex) {
            System.err.println("at Error: 1" + this.getClass().getName() + " : " + ex);
        }
        return null;
    }

    /**
     * Download and load IdentificationParameters from galaxy
     *
     * @param galaxyfile
     * @return
     */
    public IdentificationParameters retriveIdentificationParametersFileFromGalaxy(GalaxyFileModel galaxyfile) {
        String fileName = galaxyfile.getId() + "_SEARCHGUI_IdentificationParameters.par";
        File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                file = appManagmentBean.getHttpClientUtil().downloadFile(galaxyfile.getDownloadUrl(), file);
            }
            if (!IdentificationParameters.supportedVersion(file)) {
                System.err.println("at Error: 2" + this.getClass().getName() + " : " + "not supported virsion");
            }
            return IdentificationParameters.getIdentificationParameters(file);
        } catch (IOException ex) {
            System.err.println("at Error: 3" + this.getClass().getName() + " : " + ex);
        }
        return null;
    }

    /**
     * save IdentificationParameters as file on galaxy server
     *
     * @param searchParameters
     * @return file information
     */
    public GalaxyFileModel saveSearchParametersFile(IdentificationParameters searchParameters) {

        try {
            File temp = File.createTempFile(searchParameters.getName(), ".par");
            IdentificationParameters.saveIdentificationParameters(searchParameters, temp);
            GalaxyFileModel galaxyFileModel = this.uploadFile(temp, searchParameters.getName() + ": PAR file", CONSTANT.JSON_FILE_EXTENSION);
            temp.delete();
            if (galaxyFileModel.getId().equals(CONSTANT.ERROR)) {
                return null;
            }
            String fileName = galaxyFileModel.getId() + "_SEARCHGUI_IdentificationParameters.par";
            File file = new File(appManagmentBean.getAppConfig().getUserFolderUri(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            IdentificationParameters.saveIdentificationParameters(searchParameters, file);
            return galaxyFileModel;
        } catch (IOException ex) {
            System.err.println("at Error: 4" + this.getClass().getName() + " : " + ex);
        }
        return null;
    }

    /**
     * Upload file to galaxy server
     *
     * @param file
     * @param name
     * @param extension
     * @return uploaded file information
     */
    public GalaxyFileModel uploadFile(File file, String name, String extension) {

        GalaxyFileModel tempGalaxyFile = new GalaxyFileModel();
        tempGalaxyFile.setName(name);
        tempGalaxyFile.setExtension(extension);
        tempGalaxyFile.setHistoryId(appManagmentBean.getAppConfig().getMainGalaxyHistoryId());
        FormDataMultiPart multiPart = new FormDataMultiPart();
        final FileDataBodyPart filePart = new FileDataBodyPart("files_0|file_data", file);
        filePart.setMediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(filePart);
        multiPart.field(CONSTANT.TOOL_ID, "upload1", MediaType.APPLICATION_JSON_TYPE);
        multiPart.field(CONSTANT.HISTORY_ID, tempGalaxyFile.getHistoryId(), MediaType.APPLICATION_JSON_TYPE);
        multiPart.field("files_0|NAME", tempGalaxyFile.getName(), MediaType.APPLICATION_JSON_TYPE);
        multiPart.field("dbkey", "?", MediaType.APPLICATION_JSON_TYPE);
        multiPart.field("file_type", "auto", MediaType.APPLICATION_JSON_TYPE);
        multiPart.field("type", "upload_dataset", MediaType.APPLICATION_JSON_TYPE);

        Response response = appManagmentBean.getGalaxyFacad().uploadFile(multiPart);
        if (response.getStatus() != HttpStatus.SC_OK) {
            tempGalaxyFile.setId(CONSTANT.ERROR);
            return tempGalaxyFile;
        }
        JsonObject object = new JsonObject(response.readEntity(String.class));
        JsonObject dataset = object.getJsonArray(CONSTANT.OUTPUTS).getJsonObject(0);
        tempGalaxyFile.setId(dataset.getString(CONSTANT.ID));
        tempGalaxyFile.setDeleted(dataset.getBoolean(CONSTANT.DELETED));
        tempGalaxyFile.setStatus(CONSTANT.OK_STATUS);
        try {
            tempGalaxyFile.setCreatedDate(format.parse(dataset.getString(CONSTANT.CREATE_TIME)));
        } catch (ParseException ex) {
            System.out.println("Error 5: GalaxyFacad - " + ex);
        }
        tempGalaxyFile.setDownloadUrl(appManagmentBean.getAppConfig().getGalaxyServerUrl() + dataset.getString(CONSTANT.URL) + "/display?key=" + appManagmentBean.getUserHandler().getUserAPIKey());

        return tempGalaxyFile;
    }

    /**
     * Run workflow
     *
     * @param projectName
     * @param fastaFile
     * @param searchParam
     * @param spectrumFileSet
     * @param searchEngineSet
     * @return
     */
    public VisualizationDatasetModel executeWorkFlow(String projectName, GalaxyFileModel fastaFile, GalaxyFileModel searchParam, Set<GalaxyFileModel> spectrumFileSet, Map<String, Boolean> searchEngineSet) {

        boolean quant = false;
        String datasetType = CONSTANT.ID_DATASET;
        if (spectrumFileSet.iterator().next().getExtension().equals(CONSTANT.THERMO_RAW_FILE_EXTENSION)) {
            quant = true;
            datasetType = CONSTANT.QUANT_DATASET;
        }

        String inputListId = buildList(spectrumFileSet);
        //upload the workflow
        String workflowId = prepareAndUploadWorkFlow(projectName, searchEngineSet, quant);
//        execute workflow
        JsonObject body = prepareWorkFlowInvoking(fastaFile, searchParam, inputListId, quant);
        Response response = appManagmentBean.getGalaxyFacad().invokeWorkFlow(workflowId, body);
        boolean status = response.getStatus() == HttpResponseStatus.OK.code();
        JsonArray jsonArrayResp = new JsonArray(response.readEntity(String.class));
//        delete workflow
        appManagmentBean.getGalaxyFacad().deleteWorkFlow(workflowId);
        if (status) {
            VisualizationDatasetModel tempDataset = appManagmentBean.getDatasetUtils().getOnProgressDataset(datasetType);
            tempDataset.setName(projectName);
            tempDataset.setId(jsonArrayResp.getJsonObject(0).getString(CONSTANT.ID));
            try {
                tempDataset.setCreatedTime(appManagmentBean.getCoreUtils().getDateFormater().parse(jsonArrayResp.getJsonObject(0).getString(CONSTANT.CREATE_TIME)));
            } catch (ParseException ex) {
                System.err.println("ar Errot 6-" + this.getClass().getName() + " - " + ex);
                tempDataset.setCreatedTime(java.util.Calendar.getInstance().getTime());
            }
            return tempDataset;
        }
        return null;
    }

    private String buildList(Set<GalaxyFileModel> fileSet) {
        JsonObject body = new JsonObject();
        body.put(CONSTANT.HISTORY_ID, appManagmentBean.getAppConfig().getMainGalaxyHistoryId());
        body.put(CONSTANT.TOOL_ID, appManagmentBean.getAppConfig().getBUILD_LIST_TOOL_ID());
        body.put(CONSTANT.TOOL_VERSION, appManagmentBean.getAppConfig().getBUILD_LIST_TOOL_ID());
        JsonObject inputs = new JsonObject();
        JsonObject outputs = new JsonObject();
        int counter = 0;
        for (GalaxyFileModel file : fileSet) {
            JsonArray values = new JsonArray();
            JsonObject _values = new JsonObject();
            _values.put(CONSTANT.ID, file.getId());
            _values.put(CONSTANT.NAME, file.getName());
            _values.put("src", "hda");
            _values.put("keep", Boolean.FALSE);
            _values.put(CONSTANT.HISTORY_ID, file.getHistoryId());
            values.add(_values);
            JsonObject input = new JsonObject();
            input.put("values", values);
            input.put("batch", Boolean.FALSE);
            inputs.put("datasets_" + counter + "|input", input);
            JsonArray values2 = new JsonArray();
            JsonObject _values2 = new JsonObject();
            _values2.put(CONSTANT.NAME, file.getName());
            values2.add(_values2);
            JsonObject output = new JsonObject();
            output.put("values", values2);
            output.put("batch", Boolean.FALSE);
            outputs.put("datasets_" + counter + "|output", output);
            counter++;
        }
        body.put(CONSTANT.INPUTS, inputs);
        body.put(CONSTANT.OUTPUTS, outputs);

        Response response = appManagmentBean.getGalaxyFacad().buildList(body);
        JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
        return jsonObject.getJsonArray("output_collections").getJsonObject(0).getString(CONSTANT.ID);

    }

    private String prepareAndUploadWorkFlow(String projectName, Map<String, Boolean> searchEngineSet, boolean quant) {
        File workflowFile;
        if (quant) {
            workflowFile = new File(appManagmentBean.getAppConfig().getQuant_workflow_file_path());
        } else {
            workflowFile = new File(appManagmentBean.getAppConfig().getId_workflow_file_path());
        }
        /**
         * prepare the workflow
         */
        JsonObject workflowAsJson = appManagmentBean.getFilesUtils().readJsonFile(workflowFile);
        JsonObject steps = workflowAsJson.getJsonObject("steps");
        /**
         * Thermo raw convertor
         */
        int step = 3;
        if (quant) {
            steps.getJsonObject(step + "").put("content_id", appManagmentBean.getAppConfig().getTHERMO_RAW_CONVERTOR_TOOL_ID());
            steps.getJsonObject(step + "").put("tool_id", appManagmentBean.getAppConfig().getTHERMO_RAW_CONVERTOR_TOOL_ID());
            steps.getJsonObject(step + "").put("tool_version", appManagmentBean.getAppConfig().getTHERMO_RAW_CONVERTOR_TOOL_VERSION());
            step++;
        }
        /**
         * SearchGUI
         */
        steps.getJsonObject(step + "").put("content_id", appManagmentBean.getAppConfig().getSEARCHGUI_TOOL_ID());
        steps.getJsonObject(step + "").put("tool_id", appManagmentBean.getAppConfig().getSEARCHGUI_TOOL_ID());
        steps.getJsonObject(step + "").put("tool_version", appManagmentBean.getAppConfig().getSEARCHGUI_TOOL_VERSION());
        steps.getJsonObject(step + "").getJsonObject("post_job_actions").getJsonObject("RenameDatasetActionsearchgui_results").getJsonObject("action_arguments").put("newname", projectName);
        steps.getJsonObject(step + "").getJsonArray("workflow_outputs").getJsonObject(0).put("label", projectName);
        JsonArray searchEnginesArray = new JsonArray();
        searchEngineSet.keySet().stream().filter((engine) -> (searchEngineSet.get(engine))).forEachOrdered((engine) -> {
            searchEnginesArray.add(engine);
        });
        steps.getJsonObject(step + "").getJsonObject("tool_state").getJsonObject("search_engines_options").put("engines", searchEnginesArray);
        step++;
        /**
         * PeptideShaker
         */
        steps.getJsonObject(step + "").put("content_id", appManagmentBean.getAppConfig().getPEPTIDESHAKER_TOOL_ID());
        steps.getJsonObject(step + "").put("tool_id", appManagmentBean.getAppConfig().getPEPTIDESHAKER_TOOL_ID());
        steps.getJsonObject(step + "").put("tool_version", appManagmentBean.getAppConfig().getPEPTIDESHAKER_TOOL_VERSION());
        steps.getJsonObject(step + "").getJsonObject("post_job_actions").getJsonObject("RenameDatasetActionoutput_zip").getJsonObject("action_arguments").put("newname", projectName);

        /**
         * moff tool
         */
        if (quant) {
            steps.getJsonObject("7").put("content_id", appManagmentBean.getAppConfig().getMOFF_TOOL_ID());
            steps.getJsonObject("7").put("tool_id", appManagmentBean.getAppConfig().getMOFF_TOOL_ID());
            steps.getJsonObject("7").put("tool_version", appManagmentBean.getAppConfig().getMOFF_TOOL_Version());
        }

        JsonObject body = new JsonObject();
        body.put("workflow", workflowAsJson);
        Response response = appManagmentBean.getGalaxyFacad().uploadWorkFlow(body);
        JsonObject jsonObject = new JsonObject(response.readEntity(String.class));
        return jsonObject.getString(CONSTANT.ID);
    }

    private JsonObject prepareWorkFlowInvoking(GalaxyFileModel fastaFile, GalaxyFileModel searchParam, String inputListId, boolean quant) {
        File workflowInvokingFile;
        if (quant) {
            workflowInvokingFile = new File(appManagmentBean.getAppConfig().getQuant_workflow_invoking_file_path());
        } else {
            workflowInvokingFile = new File(appManagmentBean.getAppConfig().getId_workflow_invoking_file_path());
        }

        /**
         * prepare the workflow
         */
        JsonObject workflowInvokingAsJson = appManagmentBean.getFilesUtils().readJsonFile(workflowInvokingFile);
        workflowInvokingAsJson.put("history_id", appManagmentBean.getAppConfig().getMainGalaxyHistoryId());
        JsonObject parameters = workflowInvokingAsJson.getJsonObject("parameters");
        parameters.getJsonObject("0").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.ID, searchParam.getId());
        parameters.getJsonObject("0").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.HISTORY_ID, searchParam.getHistoryId());
        parameters.getJsonObject("1").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.ID, fastaFile.getId());
        parameters.getJsonObject("1").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.HISTORY_ID, fastaFile.getHistoryId());
//
        parameters.getJsonObject("2").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.ID, inputListId);
        parameters.getJsonObject("2").getJsonObject("input").getJsonArray("values").getJsonObject(0).put(CONSTANT.HISTORY_ID, appManagmentBean.getAppConfig().getMainGalaxyHistoryId());

        return workflowInvokingAsJson;
    }

}
