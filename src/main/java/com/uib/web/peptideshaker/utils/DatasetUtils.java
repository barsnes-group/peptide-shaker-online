package com.uib.web.peptideshaker.utils;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.ServletContext;

/**
 * This class provides utils for visualization data-set
 *
 * @author Yehia Mokhtar Farag
 */
public class DatasetUtils implements Serializable{

    private final AppManagmentBean appManagmentBean;

    public DatasetUtils() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
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
//        String encodedLink = "encoded link";
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

//                    dsKey = insertDatsetLinkToShare(urlUtils.encrypt(link), urlUtils.encrypt(dataset.getName() + "-_-" + dataset.getPsZipFile().getId()));
//                    String appName = VaadinSession.getCurrent().getAttribute("appName") + "";
//                    String url = Page.getCurrent().getLocation().toString().split(appName)[0] + appName + "/";
//                    String encryptedDsKey = urlUtils.encrypt(dsKey + "");
//                    link = url + "toShare;" + encryptedDsKey;
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
        dataset.setUploadedDataset(false);
        dataset.setDatasetType(datasetType);
        dataset.setStatus(CONSTANT.RUNNING_STATUS);
        dataset.setSharingLink(CONSTANT.RUNNING_STATUS);
        return dataset;
    }

}
