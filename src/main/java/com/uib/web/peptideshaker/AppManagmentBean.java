package com.uib.web.peptideshaker;

import com.uib.web.peptideshaker.facades.GalaxyFacade;
import com.uib.web.peptideshaker.facades.NotificationsFacade;
import com.uib.web.peptideshaker.handler.UserHandler;
import com.uib.web.peptideshaker.utils.HttpClientUtils;
import java.io.Serializable;

/**
 * This class is used to encapsulate application main handler and facades to
 * avoid using static classes
 *
 * @author Yehia Mokhtar Farag
 */
public class AppManagmentBean implements Serializable {

    private Config appConfig;
    private GalaxyFacade galaxyFacad;
    private NotificationsFacade notificationFacade;
    private UserHandler userLoginHandler;
    private HttpClientUtils httpClientUtil;

    public NotificationsFacade getNotificationFacade() {
        if (notificationFacade == null) {
            notificationFacade = new NotificationsFacade();
        }
        return notificationFacade;
    }

    public HttpClientUtils getHttpClientUtil() {
        if (httpClientUtil == null) {
            httpClientUtil = new HttpClientUtils();
        }
        return httpClientUtil;
    }

    public GalaxyFacade getGalaxyFacad() {
        if (galaxyFacad == null) {
            galaxyFacad = new GalaxyFacade();
        }
        return galaxyFacad;
    }

    public UserHandler getUserLoginHandler() {
        if (userLoginHandler == null) {
            userLoginHandler = new UserHandler();
        }
        return userLoginHandler;
    }

    public AppManagmentBean() {

    }

    public Config getAppConfig() {
        if (appConfig == null) {
            appConfig = new Config();
        }
        return appConfig;
    }

}
