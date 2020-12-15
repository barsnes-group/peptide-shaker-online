package com.uib.web.peptideshaker.utils;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.VaadinSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class provides utils for visualization data-set
 *
 * @author Yehia Mokhtar Farag
 */
public class DatasetUtils {

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

}
