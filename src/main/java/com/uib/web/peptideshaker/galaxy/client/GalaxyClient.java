/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.galaxy.client;

import com.github.jmchilton.blend4j.galaxy.*;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import java.util.List;

/**
 * This class is made to handel exceptions for galaxy connections
 *
 * @author y-mok
 */
public class GalaxyClient {

    private final String galaxy_Server_Url;
    private final String user_API;
    private GalaxyInstance Galaxy_Instance;

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

    public boolean isJobDone(String jobid) {
        try {
            String jobStat = Galaxy_Instance.getJobsClient().showJob(jobid).getState();
            System.out.println("at job stat is " + jobStat);
            return jobStat.equalsIgnoreCase("ok") || jobStat.equalsIgnoreCase("error") || jobStat.equalsIgnoreCase("paused");

        } catch (Exception e) {
            System.out.println("at handel exception at check history ready");
            Galaxy_Instance = GalaxyInstanceFactory.get(galaxy_Server_Url, user_API);
        }
        String jobStat = Galaxy_Instance.getJobsClient().showJob(jobid).getState();
        return jobStat.equalsIgnoreCase("ok");

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

    private boolean checkHistoryReady() {

        return true;
    }

}
