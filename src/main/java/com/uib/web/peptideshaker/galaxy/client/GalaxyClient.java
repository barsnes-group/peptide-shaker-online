/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.galaxy.client;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import java.util.List;

/**
 * This class is made to handel exceptions for galaxy connections
 *
 * @author y-mok
 */
public class GalaxyClient {

    private GalaxyInstance Galaxy_Instance;
    private final String galaxy_Server_Url;
    private final String user_API;

    public GalaxyClient(String galaxyServerUrl, String userAPI) {
        Galaxy_Instance = GalaxyInstanceFactory.get(galaxyServerUrl, userAPI);
        this.galaxy_Server_Url = galaxyServerUrl;
        this.user_API = userAPI;
    }

    public HistoriesClient getHistoriesClient() {
        try {
            return Galaxy_Instance.getHistoriesClient();
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return Galaxy_Instance.getHistoriesClient();

    }

    public boolean isHistoryReady(String historyId) {
        try {
           return getHistoriesClient().showHistory(historyId).isReady();
           
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return getHistoriesClient().showHistory(historyId).isReady();

    }

    public String getApiKey() {
        return user_API;
    }

    public String getGalaxyUrl() {
        return Galaxy_Instance.getGalaxyUrl();
    }

    public UsersClient getUsersClient() {
        try {
            return Galaxy_Instance.getUsersClient();
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return Galaxy_Instance.getUsersClient();

    }

    public ToolsClient getToolsClient() {
        try {
            return Galaxy_Instance.getToolsClient();
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return Galaxy_Instance.getToolsClient();

    }

    public WorkflowsClient getWorkflowsClient() {
        try {
            return Galaxy_Instance.getWorkflowsClient();
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return Galaxy_Instance.getWorkflowsClient();

    }

    public List<History> getHistories() {
        try {
            return Galaxy_Instance.getHistoriesClient().getHistories();
        } catch (Exception e) {
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        return Galaxy_Instance.getHistoriesClient().getHistories();

    }

}
