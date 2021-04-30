package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * This utilities is used to retrieve dataset using shared link
 *
 * @author Yehia Mokhtar Farag
 */
public class SharedDatasetUtils {

    private final AppManagmentBean appManagmentBean;

    public SharedDatasetUtils() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
    }

    public JsonObject retrieveDataset(String encodedDatasetId) {
        try {
            int decryptedDsIndex = Integer.valueOf(appManagmentBean.getURLUtils().decrypt(encodedDatasetId));
            String encryptedDsDetails = appManagmentBean.getDatabaseUtils().retrieveDataSet(decryptedDsIndex);
            String decryptedDsDetails = URLDecoder.decode(encryptedDsDetails, "UTF-8");
            JsonObject object = new JsonObject(decryptedDsDetails);
            return object;
        } catch (UnsupportedEncodingException | NumberFormatException ex) {
        }
        return null;
    }

}
