package com.uib.web.peptideshaker.galaxy.handlers;

import com.uib.web.peptideshaker.galaxy.utilities.history.GalaxyDatasetServingUtil;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.uib.web.peptideshaker.galaxy.client.GalaxyClient;
import com.uib.web.peptideshaker.galaxy.utilities.GalaxyAPIInteractiveLayer;
import com.uib.web.peptideshaker.model.core.LinkUtil;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

import java.io.File;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class represents Galaxy history (galaxy file system) in Peptide-Shaker
 * web application the class is responsible for handling files in galaxy history
 * and shares it cross the application
 *
 * @author Yehia Farag
 */
public abstract class GalaxyHistoryHandler {

    /**
     * The main Galaxy instance in the system.
     */
    private GalaxyClient Galaxy_Instance;
    /**
     * User data files folder.
     */
    private File user_folder;
    /**
     * Helping class to initialise and update dataset information.
     */
    private GalaxyDatasetServingUtil galaxyDatasetServingUtil;
    /**
     * Is system in progress.
     */
//    private boolean jobsInProgress = false;
    /**
     * Inner class responsible for create Task to tracking and update the galaxy
     * history.
     */
    private UpdateDatasetructureTask updateDatasetructureTask;
    /**
     * Future results from executing tasks to tracking and update the galaxy
     * history.
     */
    private Future updateDatasetructureFuture;

    /**
     * Refresher listener allow action on adding different actions for the
     * application refresher.
     */
//    private Refresher.RefreshListener refreshlistener;
    /**
     * Date formatter to allow reading the creation date of the datasets from
     * the dataset json file.
     */
    private final DateFormat df6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * push to update the file system presenter.
     */
    private boolean visualiseProjectOverviewPresenter;
    /**
     * The alternative class to deal with Galaxy server API through REST
     * services.
     */
    private final GalaxyAPIInteractiveLayer galaxyApiInteractiveLayer = new GalaxyAPIInteractiveLayer();

    /**
     * Get list of accessions available on csf-pr in order to allow mapping data
     * to csf-pr
     *
     * @return set of Uni-prot protein accessions available on csf-pr
     */
    public abstract Set<String> getCsf_pr_Accession_List();

    /**
     * System connected to Galaxy Server
     *
     * @param Galaxy_Instance the main Galaxy instance in the system
     * @param user_folder user folder to store users file temporarily
     */
    public void setGalaxyConnected(GalaxyClient Galaxy_Instance, File user_folder) {
        this.Galaxy_Instance = Galaxy_Instance;
        this.galaxyDatasetServingUtil = new GalaxyDatasetServingUtil(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());
        this.user_folder = user_folder;
        forceUpdateGalaxyFileSystem(false);
    }

    private String userDataFolderUrl;
    private String galaxyServerUrl;

    /**
     * Connect the system to Galaxy Server
     *
     * @param galaxyServerUrl the address of Galaxy Server
     * @param userDataFolderUrl main folder for storing users data
     */
    public void viewToShareDataset(String galaxyServerUrl, String userDataFolderUrl) {
        this.userDataFolderUrl = userDataFolderUrl;
        this.galaxyServerUrl = galaxyServerUrl;
        forceUpdateGalaxyFileSystem(false);

    }

    ;

    /**
     * Get map of available PeptideShaker visualisation datasets.
     *
     * @return map of available PS datasets
     */
    public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashMap<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getPeptideShakerVisualizationMap();
    }

    /**
     * Get the main Search settings .par files Map
     *
     * @return Search parameters files .par map
     */
    public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashMap<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getSearchSettingsFilesMap();
    }

    /**
     * Get the main FASTA files Map
     *
     * @return FASTA Files Map
     */
    public Map<String, GalaxyFileObject> getFastaFilesMap() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashMap<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getFastaFilesMap();
    }

    /**
     * Set of currently running job ids on galaxy.
     *
     * @return Set of currently ruuning jobs on galaxy
     */
    public Set<String> getRunningJobSet() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashSet<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getRunningJobSet();
    }

    /**
     * check if there is any job running on the server*
     */
    public boolean isJobRunning() {
        if (updateDatasetructureFuture == null) {
            return false;
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.isJobRunning();
    }

    /**
     * Get the main MGF files Map
     *
     * @return Map of available MGF files (file galaxy id mapped to datasets)
     */
    public Map<String, GalaxyFileObject> getMgfFilesMap() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashMap<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        try {
            return updateDatasetructureTask.call();
        } catch (Exception ex) {
            System.err.println("Error: " + ex);
            return null;
        }
    }

    /**
     * Get the main Raw files Map
     *
     * @return Map of available Raw files (file galaxy id mapped to datasets)
     */
    public Map<String, GalaxyFileObject> getRawFilesMap() {
        if (updateDatasetructureFuture == null) {
            return new LinkedHashMap<>();
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        try {
            return updateDatasetructureTask.getRawFilesMap();
        } catch (Exception ex) {
            System.err.println("Error: " + ex);
            return null;
        }
    }

    /**
     * Get map all available files (datasets)
     *
     * @return Map of all available files (file galaxy id mapped to datasets)
     */
    public Map<String, GalaxyFileObject> getHistoryFilesMap() {
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getHistoryFilesMap();
    }

    /**
     * Get the total memory usage on galaxy server in GB
     *
     * @return double value of user memory used in GB (deleted files not
     * included)
     */
    public String getMemoryUsage() {

        return updateDatasetructureTask.getUsedStorageSpace();
    }

    /**
     * Get the number of viewable files available for the user
     *
     * @return integer number of available files
     */
    public int getFilesNumber() {
        return updateDatasetructureTask.getFilesNumber();
    }

    /**
     * Get the number of viewable datasets available for the user
     *
     * @return integer number of available datasets
     */
    public int getDatasetsNumber() {
        if (updateDatasetructureFuture == null) {
            return -1;
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getDsNumbers();
    }

    /**
     * Get the main working history id on galaxy server where all new datasets
     * will be stored
     *
     * @return Working history id in Galaxy server
     */
    public String getWorkingHistoryId() {
        if (updateDatasetructureFuture == null) {
            return "offlinegalaxyid";
        }
        while (!updateDatasetructureFuture.isDone()) {
        }
        return updateDatasetructureTask.getWorkingHistoryId();
    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
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
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
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
     * Abstract method allows performing actions based on the jobs states on
     * Galaxy server
     *
     * @param historyFilesMap list of available files (datasets) on Galaxy
     * server
     * @param jobsInProgress the system currently in progress (jobs still
     * running)
     * @param visualiseProjectOverviewPresenter open file system view after
     * updating the file system
     * @param toDeleteMap datasets to delete after finishing work
     */
    public abstract void updatePresenterLayer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean visualiseProjectOverviewPresenter, Set<String> toDeleteMap);

    private ScheduledExecutorService scheduler;
    private ScheduledFuture schedulerFuture;

    public void forceUpdateGalaxyFileSystem(boolean keepfollow) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        updateDatasetructureTask = new UpdateDatasetructureTask(keepfollow);
        updateDatasetructureFuture = executorService.submit(updateDatasetructureTask);
        while (!updateDatasetructureFuture.isDone()) {
        }
        executorService.submit(() -> {
            updatePresenterLayer(updateDatasetructureTask.getHistoryFilesMap(), updateDatasetructureTask.isJobRunning(), visualiseProjectOverviewPresenter, updateDatasetructureTask.getToDeleteMap());
        });
        executorService.shutdown();
    }

    /**
     * Following jobs statues on Galaxy server until all jobs are done.
     *
     * @param visualiseProjectOverviewPresenter open file system view after
     * updating the file system
     */
    private void followGalaxyJobs() {

        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            Set<String> jobsToIgnoe = new HashSet<>();
            Runnable followUpGalaxy = new Runnable() {
                private boolean busy;
                private int counter = 0;

                @Override
                public void run() {
                    if ((!updateDatasetructureTask.isJobRunning() && !updateDatasetructureTask.isInvokeTracker()) || busy) {
                        return;
                    }
                    busy = true;
                    try {

                        Set<String> tempSet = new HashSet<>(updateDatasetructureTask.getRunningJobSet());
                        for (String jobId : tempSet) {
                            if (jobsToIgnoe.contains(jobId)) {
                                continue;

                            }
                            try {
                                if (galaxyApiInteractiveLayer.isJobDone(Galaxy_Instance.getGalaxyUrl(), jobId, Galaxy_Instance.getApiKey())) {//invoke update
                                    jobsToIgnoe.clear();
                                    oneJobISDone();
                                }
                            } catch (java.io.FileNotFoundException ex) {
                                jobsToIgnoe.add(jobId);
                                continue;
                            }
                        }
                        if (updateDatasetructureTask.isInvokeTracker()) {
                            updateDatasetructureTask.updateRunningJobSet();
                        }
                        if (jobsToIgnoe.size() == tempSet.size() && !jobsToIgnoe.isEmpty()) {
                            jobsToIgnoe.clear();
                            oneJobISDone();
                        }

                    } catch (Exception e) {
                        System.out.println("at Error : follow galaxy job " + e);
                    }
                    busy = false;
                    System.out.println("-----scedule is running-----" + (counter++));
                    if (counter >= 50) {
                        System.out.println("oldSchedulerFuture schedulerfuture termonated " + schedulerFuture.cancel(true));
                        scheduler.shutdown();
                        scheduler = null;
                        oneJobISDone();

                    }

                }
            };

            ScheduledFuture oldSchedulerFuture = (ScheduledFuture) VaadinSession.getCurrent().getAttribute("schedulerfuture");
            if (oldSchedulerFuture != null) {
                System.out.println("oldSchedulerFuture schedulerfuture termonated " + oldSchedulerFuture.cancel(true));
            }
            ScheduledExecutorService oldScheduler = (ScheduledExecutorService) VaadinSession.getCurrent().getAttribute("scheduler");
            if (oldScheduler != null) {
                oldScheduler.shutdown();
                System.out.println("at oldScheduler scheduler shoutdown ");
            }

            schedulerFuture = scheduler.scheduleAtFixedRate(followUpGalaxy, 0, 20, TimeUnit.SECONDS);
            VaadinSession.getCurrent().getSession().setAttribute("scheduler", scheduler);
            VaadinSession.getCurrent().getSession().setAttribute("schedulerfuture", schedulerFuture);
            VaadinSession.getCurrent().setAttribute("scheduler", scheduler);
            VaadinSession.getCurrent().setAttribute("schedulerfuture", schedulerFuture);
//            scheduler.shutdown();
        }
//        schedulerFuture = scheduler.scheduleAtFixedRate(() -> {
//            try {
//                Set<String> tempSet = new HashSet<>(runningJobSet);
//                for (String jobId : tempSet) {
//                    if (Galaxy_Instance.isJobDone(jobId)) {//invoke update
//
//                        oneJobISDone(jobId);
//                    }
//
//                }
//
//            } catch (Exception e) {
//                System.out.println("at Error : follow galaxy job " + e);
//
//            }
//            System.out.println("-----scedule is running-----2" + runningJobSet.isEmpty());
//            if (runningJobSet.isEmpty()) {
//                schedulerFuture.cancel(true);
//                scheduler.shutdown();
//            }
//        }, 0, 20, TimeUnit.SECONDS);
    }

    private void oneJobISDone() {
        forceUpdateGalaxyFileSystem(false);

    }

    /**
     * This class responsible for generating tasks that track the different jobs
     * process on Galaxy server
     *
     */
    private class UpdateDatasetructureTask implements Callable<Map<String, GalaxyFileObject>> {

        /**
         * The main MGF Files Map.
         */
        private final Map<String, GalaxyFileObject> mgfFilesMap;
        /**
         * The indexed MGF Files Map.
         */
//        private final Map<String, GalaxyFileObject> indexedMgfFilesMap;
        /**
         * The main Raw Files Map.
         */
        private final Map<String, GalaxyFileObject> rawFilesMap;
        /**
         * The Full historyFiles Map.
         */
        private final Map<String, GalaxyFileObject> historyFilesMap;
        /**
         * The main Search settings .par File Map.
         */
        private final Map<String, GalaxyTransferableFile> searchSettingsFilesMap;
        /**
         * The main MOFF quant data File Map.
         */
//        private final Map<String, GalaxyTransferableFile> moffFilesMap;
        /**
         * The main Indexed MGF FILE Map.
         */
        private final Map<String, Set<GalaxyFileObject>> indexedMgfFilesMap;
        /**
         * The main FASTA File Map.
         */
        private final Map<String, GalaxyFileObject> fastaFilesMap;
        /**
         * The main MGF and FASTA index Files Map.
         */
        private final Map<String, GalaxyTransferableFile> indexFilesMap;

        /**
         * The main SearchGUI Files Map.
         */
        private final Map<String, GalaxyFileObject> searchGUIFilesMap;
        /**
         * The main PeptideShaker Visualisation Map.
         */
        private final Map<String, PeptideShakerVisualizationDataset> peptideShakerVisualizationMap;
        /**
         * List of available galaxy histories IDs.
         */
        private final Set<String> historiesIds;

        /**
         * The Working galaxy history.
         */
        private History workingHistory;
        private boolean jobRunning;

        public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
            return searchSettingsFilesMap;
        }

        public Map<String, GalaxyFileObject> getMgfFilesMap() {
            return mgfFilesMap;
        }

        public Map<String, Set<GalaxyFileObject>> getIndexedMgfFilesMap() {
            return indexedMgfFilesMap;
        }

        public Map<String, GalaxyFileObject> getRawFilesMap() {
            return rawFilesMap;
        }

        public Set<String> getToDeleteMap() {
            return toDeleteMap;
        }

        public Map<String, GalaxyFileObject> getHistoryFilesMap() {
            return historyFilesMap;
        }

        public Map<String, GalaxyFileObject> getFastaFilesMap() {
            return fastaFilesMap;
        }

        public Map<String, GalaxyTransferableFile> getIndexFilesMap() {
            return indexFilesMap;
        }

        public Map<String, GalaxyFileObject> getSearchGUIFilesMap() {
            return searchGUIFilesMap;
        }

        public Map<String, PeptideShakerVisualizationDataset> getPeptideShakerVisualizationMap() {
            return peptideShakerVisualizationMap;
        }

        public Set<String> getHistoriesIds() {
            return historiesIds;
        }

        public String getWorkingHistoryId() {
            return workingHistory.getId();
        }

        private String usedStorageSpace = "Not Available";
        private int filesNumber;
        private int dsNumbers;
        private Set<String> toDeleteMap = new HashSet<>();
        /**
         * Set of currently running jobs on galaxy.
         */
        private Set<String> runningJobSet;

        public Set<String> getRunningJobSet() {
            return runningJobSet;
        }

        public void updateRunningJobSet() {
            if (runningJobSet.isEmpty()) {
                List<History> historiesList = Galaxy_Instance.getHistoriesClient().getHistories();
                for (History history : historiesList) {
                    if (history.isDeleted()) {
                        continue;
                    }
                    historiesIds.add(history.getId());
                    runningJobSet.addAll(galaxyApiInteractiveLayer.isHistoryReady(Galaxy_Instance.getGalaxyUrl(), history.getId(), Galaxy_Instance.getApiKey()));
                }

            }
        }
        /**
         * Set of currently running jobs on galaxy with error.
         */
        private final Map<String, Set<GalaxyTransferableFile>> cuiFilesMap;
        private final Map<String, Set<GalaxyTransferableFile>> MoffFilesMap;
        private final Map<String, String> cuiGalaxyIdsMap;
        private final Map<String, String> moffGalaxyIdsMap;
        private final Map<String, String> indexedMgfGalaxyIdsMap;
        private boolean invokeTracker;

        /**
         * error with java.util.ConcurrentModificationException**
         */
        private UpdateDatasetructureTask(boolean keepfollow) {
            this.mgfFilesMap = new LinkedHashMap<>();
            this.indexedMgfFilesMap = new LinkedHashMap<>();
            this.rawFilesMap = new LinkedHashMap<>();
            this.historyFilesMap = new LinkedHashMap<>();
            this.fastaFilesMap = new LinkedHashMap<>();
//            this.moffFilesMap = new LinkedHashMap<>();

            this.indexFilesMap = new LinkedHashMap<>();
            this.peptideShakerVisualizationMap = new LinkedHashMap<>();
            this.searchGUIFilesMap = new LinkedHashMap<>();
            this.historiesIds = new HashSet<>();
            this.searchSettingsFilesMap = new LinkedHashMap<>();
//            this.mgfIndexFilesMap = new LinkedHashMap<>();
            this.runningJobSet = new LinkedHashSet<>();
            this.cuiFilesMap = new LinkedHashMap<>();
            this.MoffFilesMap = new LinkedHashMap<>();
            this.cuiGalaxyIdsMap = new LinkedHashMap<>();
            this.moffGalaxyIdsMap = new LinkedHashMap<>();
            this.indexedMgfGalaxyIdsMap = new LinkedHashMap<>();
            this.invokeTracker = keepfollow;
            try {
                String requestToShare = Page.getCurrent().getLocation().toString();
                if (requestToShare.contains("toShare_-_")) {
                    int dsKey = -1;
                    final LinkUtil linkUtil = new LinkUtil();
                    if (requestToShare.split("toShare_-_").length > 1) {
                        try {
                            String dsKeyAsString = requestToShare.split("toShare_-_")[1];
                            dsKey = Integer.valueOf(linkUtil.decrypt(dsKeyAsString));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    String encodedDsDetails = getDatasetSharingLink(dsKey);
                    if (encodedDsDetails != null) {
                        String deCrypted = URLDecoder.decode(linkUtil.decrypt(encodedDsDetails), "UTF-8");
                        JSONParser parser = new JSONParser();
                        org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(deCrypted);
                        Map<String, Object> dsInformationMap = jsonToMap(json);

                        user_folder = new File(userDataFolderUrl, dsInformationMap.get("apiKey").toString());
                        user_folder.mkdir();
                        galaxyDatasetServingUtil = new GalaxyDatasetServingUtil(galaxyServerUrl, dsInformationMap.get("apiKey").toString());

                        PeptideShakerVisualizationDataset externaldataset = new PeptideShakerVisualizationDataset(dsInformationMap.get("dsName").toString(), user_folder, galaxyServerUrl, dsInformationMap.get("apiKey").toString(), galaxyDatasetServingUtil, getCsf_pr_Accession_List()) {
                            @Override
                            public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                                return GalaxyHistoryHandler.this.getPathwayEdges(proteinAcc);
                            }
                        };
                        externaldataset.setHistoryId(dsInformationMap.get("ps_history").toString());
                        externaldataset.setPeptideShakerResultsFileId(dsInformationMap.get("ps").toString(), true);
                        externaldataset.setGalaxyId(dsInformationMap.get("ps").toString());
                        externaldataset.setStatus("ok");
                        externaldataset.setDownloadUrl(galaxyServerUrl + "/api/histories/" + externaldataset.getHistoryId() + "/contents/" + externaldataset.getGalaxyId() + "/display?key=" + dsInformationMap.get("apiKey").toString());

                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setHistoryId(dsInformationMap.get("ps_history").toString());
                        ds.setGalaxyId(dsInformationMap.get("sqi").toString());
                        ds.setDownloadUrl(galaxyServerUrl + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + dsInformationMap.get("apiKey").toString());
                        ds.setStatus("ok");
                        ds.setType("SearchGUI");
                        ds.setOverview(dsInformationMap.get("overviewSGUI").toString());
                        externaldataset.setSearchGUIResultFile(ds);

                        Set<GalaxyTransferableFile> cuiSet = new LinkedHashSet<>();
                        Map<String, String> cuiFileIdMap = (Map<String, String>) dsInformationMap.get("cuiSet");
                        for (String galaxyId : cuiFileIdMap.keySet()) {
                            ds = new GalaxyFileObject();
                            ds.setName(cuiFileIdMap.get(galaxyId));
                            ds.setType("CUI");
                            ds.setHistoryId(externaldataset.getHistoryId());
                            ds.setGalaxyId(galaxyId);
                            ds.setDownloadUrl(galaxyServerUrl + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + dsInformationMap.get("apiKey").toString());
                            ds.setStatus("ok");
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            cuiSet.add(file);

                        }
                        externaldataset.setCuiFiles(dsInformationMap.get("cui").toString(), cuiSet);

                        Set<GalaxyFileObject> indexedMGFSet = new LinkedHashSet<>();
                        Map<String, String> indexedMGFFileIdMap = (Map<String, String>) dsInformationMap.get("cuiSet");
                        for (String galaxyId : indexedMGFFileIdMap.keySet()) {
                            ds = new GalaxyFileObject();
                            ds.setName(indexedMGFFileIdMap.get(galaxyId));
                            ds.setType("Indexed-MGF");
                            ds.setStatus("ok");
                            ds.setHistoryId(externaldataset.getHistoryId());
                            ds.setGalaxyId(galaxyId);
                            ds.setDownloadUrl(galaxyServerUrl + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + dsInformationMap.get("apiKey").toString());
                            indexedMGFSet.add(ds);
                        }

                        externaldataset.setIndexedMgfFiles(dsInformationMap.get("mgf").toString(), indexedMGFSet);

                        if (dsInformationMap.containsKey("moffid")) {

                            Set<GalaxyTransferableFile> moffSet = new LinkedHashSet<>();
                            Map<String, String> moffFileIdMap = (Map<String, String>) dsInformationMap.get("moffSet");
                            for (String galaxyId : moffFileIdMap.keySet()) {
                                ds = new GalaxyFileObject();
                                ds.setName(moffFileIdMap.get(galaxyId));
                                ds.setType("MOFF Quant");
                                ds.setHistoryId(externaldataset.getHistoryId());
                                ds.setGalaxyId(galaxyId);
                                ds.setDownloadUrl(galaxyServerUrl + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + dsInformationMap.get("apiKey").toString());
                                ds.setStatus("ok");
                                GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                                file.setDownloadUrl(ds.getDownloadUrl());
                                moffSet.add(file);

                            }

                            externaldataset.setMoff_quant_files(dsInformationMap.get("moffid").toString(), moffSet);
                        }
                        externaldataset.setToShareDataset(true);
                        historyFilesMap.put(externaldataset.getProjectName() + "_ExternalDS", externaldataset);
                        return;
                    }
                    Page.getCurrent().open(requestToShare.split("toShare_-_")[0] + ".error", "");
                    return;
                }

                HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
                List<History> historiesList = galaxyHistoriesClient.getHistories();
                if (historiesList.isEmpty()) {
                    galaxyHistoriesClient.create(new History("Online-PeptideShaker-History"));
                    workingHistory = galaxyHistoriesClient.create(new History("Online-PeptideShaker-Job-History"));
                    historiesList = galaxyHistoriesClient.getHistories();
                } else {
                    for (History h : historiesList) {
                        if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
                            workingHistory = h;
                        }
                    }
                    if (workingHistory == null) {
                        workingHistory = Galaxy_Instance.getHistoriesClient().create(new History("Online-PeptideShaker-Job-History"));
                        historiesList = galaxyHistoriesClient.getHistories();
                    }
                }
//                
                for (History history : historiesList) {
                    if (history.isDeleted()) {
                        continue;
                    }
                    historiesIds.add(history.getId());
                    runningJobSet.addAll(galaxyApiInteractiveLayer.isHistoryReady(Galaxy_Instance.getGalaxyUrl(), history.getId(), Galaxy_Instance.getApiKey()));
                }
                List<Map<String, Object>> results = galaxyApiInteractiveLayer.getDatasetIdList(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());//sresp.getResults();

                String userID = Galaxy_Instance.getUsersClient().getUsers().get(0).getId();
                usedStorageSpace = galaxyApiInteractiveLayer.getUserMemoryUsage(Galaxy_Instance.getGalaxyUrl(), userID, Galaxy_Instance.getApiKey());

                results.stream().filter((map) -> map != null && (!((map.get("purged") + "").equalsIgnoreCase("true") || (!historiesIds.contains(map.get("history_id") + "")) || (map.get("deleted") + "").equalsIgnoreCase("true")))).forEachOrdered((Map<String, Object> map) -> {

                    if (map.containsKey("collection_type")) {//                        
                        if (map.containsKey("elements")) {
                            List elements = ((List) map.get("elements"));
                            for (Object element : elements) {
                                Map<String, Object> toDeleteElement = (Map<String, Object>) element;
                                toDeleteElement = ((Map<String, Object>) toDeleteElement.get("object"));
                                if (toDeleteElement.get("file_ext").toString().equalsIgnoreCase("thermo.raw") || toDeleteElement.get("file_ext").toString().equalsIgnoreCase("mgf") || toDeleteElement.get("file_ext").toString().equalsIgnoreCase("mzml")) {
                                    toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString() + ";collection");
                                    break;
                                }
                            }
                        }

                    }
                    String name = map.get("name").toString();
                    if (!map.containsKey("collection_type") && ((name.endsWith("-original-input") || ((name.contains("moFF") && name.endsWith(": log")))))) {
                        toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString());
                    } else if (!map.containsKey("collection_type") && name.endsWith("-original-input-MGF")) {

                        if (!map.get("extension").toString().equalsIgnoreCase("cui")) {
                            toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString());
                        }
                    } else if (map.containsKey("collection_type") && (name.equals("Label-Flatten-MGF") || name.endsWith("-original-input-MGF"))) {
                        toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString() + ";collection");
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> toDeleteElement = (Map<String, Object>) element;
                            toDeleteElement = ((Map<String, Object>) toDeleteElement.get("object"));
                            toDeleteMap.add(map.get("history_id") + ";" + toDeleteElement.get("id").toString());
                        }

                    } else if (map.containsKey("collection_type") && (name.startsWith("Thermo on data ") && name.endsWith(": MGF"))) {
                        toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString() + ";collection");
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> toDeleteElement = (Map<String, Object>) element;
                            toDeleteElement = ((Map<String, Object>) toDeleteElement.get("object"));
                            toDeleteMap.add(map.get("history_id") + ";" + toDeleteElement.get("id").toString());
                        }

                    } else if (map.containsKey("collection_type") && (name.startsWith("moFF collection") && name.endsWith(": log"))) {
                        toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString() + ";collection");
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> toDeleteElement = (Map<String, Object>) element;
                            toDeleteElement = ((Map<String, Object>) toDeleteElement.get("object"));
                            toDeleteMap.add(map.get("history_id") + ";" + toDeleteElement.get("id").toString());
                        }

                    } else if (map.containsKey("collection_type") && (map.get("name").toString().endsWith("-Indexed-MGF-CUI"))) {//

                        String dsName = map.get("name").toString().replace("-Indexed-MGF-CUI", "");
                        if (!cuiFilesMap.containsKey(dsName)) {
                            cuiFilesMap.put(dsName, new HashSet<>());
                        } else {
                            cuiFilesMap.get(dsName).clear();
                            cuiGalaxyIdsMap.remove(dsName);
                        }
                        cuiGalaxyIdsMap.put(dsName, map.get("id").toString());
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> mgfIndexElement = (Map<String, Object>) element;
                            GalaxyFileObject ds = new GalaxyFileObject();
                            mgfIndexElement = ((Map<String, Object>) mgfIndexElement.get("object"));
                            ds.setName(mgfIndexElement.get("name").toString());
                            ds.setType("CUI");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(mgfIndexElement.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey());
                            ds.setStatus(mgfIndexElement.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            file.setCreate_time(ds.getCreate_time());
                            this.cuiFilesMap.get(dsName).add(file);

                        }

                    } else if (map.containsKey("collection_type") && (map.get("name").toString().endsWith("-Indexed-MGF"))) {
                        String dsName = map.get("name").toString().replace("-Indexed-MGF", "");
                        if (!indexedMgfFilesMap.containsKey(dsName)) {
                            indexedMgfFilesMap.put(dsName, new HashSet<>());
                            indexedMgfGalaxyIdsMap.put(dsName, map.get("id").toString());
                        }
                        //elements to add to indexed mgf files
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> mgfElement = (Map<String, Object>) element;
                            mgfElement = ((Map<String, Object>) mgfElement.get("object"));
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(mgfElement.get("name").toString());
                            ds.setType("Indexed-MGF");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(mgfElement.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey());

                            ds.setStatus(mgfElement.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            this.indexedMgfFilesMap.get(dsName).add(ds);

                        }

                    } else if (map.containsKey("collection_type") && (map.get("name").toString().endsWith("-MOFF"))) {
                        String dsName = map.get("name").toString().replace("-MOFF", "");
                        if (!MoffFilesMap.containsKey(dsName)) {
                            MoffFilesMap.put(dsName, new HashSet<>());

                        } else {
                            MoffFilesMap.get(dsName).clear();
                            moffGalaxyIdsMap.remove(dsName);
                        }
                        moffGalaxyIdsMap.put(dsName, map.get("id").toString());
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> moffElement = (Map<String, Object>) element;
                            GalaxyFileObject ds = new GalaxyFileObject();
                            moffElement = ((Map<String, Object>) moffElement.get("object"));
                            ds.setName(moffElement.get("name").toString());
                            ds.setType("MOFF Quant");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(moffElement.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/api/histories/" + ds.getHistoryId() + "/contents/" + ds.getGalaxyId() + "/display?key=" + Galaxy_Instance.getApiKey());
                            ds.setStatus(moffElement.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            file.setCreate_time(ds.getCreate_time());
                            this.MoffFilesMap.get(dsName).add(file);
                        }

                    } else if (map.containsKey("collection_type") && (map.get("name").toString().equalsIgnoreCase("collection"))) {
                    } else if (map.get("extension").toString().equalsIgnoreCase("tabular") && map.get("name").toString().endsWith("-MOFF")) {
                        String dsName = map.get("name").toString().replace("-MOFF", "");
                        if (!MoffFilesMap.containsKey(dsName)) {
                            MoffFilesMap.put(dsName, new HashSet<>());
                            moffGalaxyIdsMap.put(dsName, map.get("id").toString());
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(map.get("name").toString());
                            ds.setType("MOFF Quant");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setStatus(map.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            file.setCreate_time(ds.getCreate_time());
                            this.MoffFilesMap.get(dsName).add(file);
                        }
                    } else if ((map.get("extension") + "").equalsIgnoreCase("searchgui_archive")) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setStatus(map.get("state") + "");

                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "").replace("_-_", ":")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                        ds.setName(map.get("name").toString());
                        ds.setType("SearchGUI");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setOverview(map.get("misc_info") + "");
                        searchGUIFilesMap.put(map.get("name").toString().replace("-SearchGUI-Results", ""), ds);

                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                    } else if (map.get("extension").toString().equalsIgnoreCase("json") && (map.get("name").toString().endsWith(".par") || map.get("name").toString().endsWith("-PAR"))) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("Parameters File (JSON)");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                        ds.setStatus(map.get("state") + "");
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "").replace("_-_", ":")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                        GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                        file.setDownloadUrl(ds.getDownloadUrl());
                        this.searchSettingsFilesMap.put(ds.getGalaxyId(), file);
                        file.setCreate_time(ds.getCreate_time());
                    } else if (map.get("extension").toString().equalsIgnoreCase("mgf") || (map.get("extension").toString().equalsIgnoreCase("auto") && map.get("name").toString().endsWith(".mgf"))) {
                        if (map.get("visible").toString().equalsIgnoreCase("true")) {
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(map.get("name").toString());
                            ds.setType("MGF");
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setStatus(map.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            this.mgfFilesMap.put(ds.getGalaxyId(), ds);

                            //try to ignor mgf produce in the project
                        }
                    } else if (map.get("extension").toString().equalsIgnoreCase("mzml") || (map.get("extension").toString().equalsIgnoreCase("auto") && map.get("name").toString().endsWith(".mzml"))) {
                        if (map.get("visible").toString().equalsIgnoreCase("true")) {
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(map.get("name").toString());
                            ds.setType("mzML");
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setStatus(map.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                System.err.println("Error: " + ex);
                            }
                            this.mgfFilesMap.put(ds.getGalaxyId(), ds);

                            //try to ignor mgf produce in the project
                        }
                    } else if (map.get("extension").toString().equalsIgnoreCase("thermo.raw") || (map.get("extension").toString().equalsIgnoreCase("auto") && map.get("name").toString().endsWith(".thermo.raw"))) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("Thermo.raw");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        ds.setStatus(map.get("state") + "");
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                        this.rawFilesMap.put(ds.getGalaxyId(), ds);
                    } else if (map.get("extension").toString().equalsIgnoreCase("fasta") || (map.get("extension").toString().equalsIgnoreCase("auto") && map.get("name").toString().endsWith(".fasta"))) {
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setName(map.get("name").toString());
                        ds.setType("FASTA");
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                        ds.setHistoryId(map.get("history_id") + "");
                        ds.setGalaxyId(map.get("id").toString());
                        this.fastaFilesMap.put(ds.getGalaxyId(), ds);
                        ds.setStatus(map.get("state") + "");
                        try {
                            ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                    } else if ((map.get("name").toString().endsWith("-PS")) && (map.get("extension").toString().equalsIgnoreCase("zip"))) {
                        String projectId = map.get("name").toString().replace("-PS", "");
                        PeptideShakerVisualizationDataset vDs = new PeptideShakerVisualizationDataset(projectId, user_folder, Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey(), galaxyDatasetServingUtil, getCsf_pr_Accession_List()) {
                            @Override
                            public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                                return GalaxyHistoryHandler.this.getPathwayEdges(proteinAcc);
                            }

                        };
                        peptideShakerVisualizationMap.put(projectId, vDs);
                        vDs.setHistoryId(map.get("history_id") + "");
                        vDs.setType("Web Peptide Shaker Dataset");
                        vDs.setName(projectId);
                        vDs.setPeptideShakerResultsFileId(map.get("id").toString(), false);
                        vDs.setStatus(map.get("state") + "");
                        vDs.setGalaxyId(map.get("id").toString());
                        vDs.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                        try {
                            vDs.setCreateTime(df6.parse((map.get("create_time") + "")));
                        } catch (ParseException ex) {
                            System.err.println("Error: " + ex);
                        }
                    }

                }
                );

                /**
                 * Concurrent modification exception here.
                 *
                 */
                peptideShakerVisualizationMap.keySet().stream().map((key) -> peptideShakerVisualizationMap.get(key)).filter((vDs) -> !(!searchGUIFilesMap.containsKey(vDs.getProjectName()))).forEachOrdered((vDs) -> {
                    GalaxyFileObject ds = searchGUIFilesMap.get(vDs.getProjectName());
                    vDs.setCreateTime(ds.getCreate_time());
                    Dataset hcp = Galaxy_Instance.getHistoriesClient().showDataset(ds.getHistoryId(), ds.getGalaxyId());
                    ds.setOverview(hcp.getInfo());
                    vDs.setSearchGUIResultFile(ds);
                    if (indexedMgfFilesMap.containsKey(vDs.getProjectName())) {
                        vDs.setIndexedMgfFiles(indexedMgfGalaxyIdsMap.get(vDs.getProjectName()), indexedMgfFilesMap.get(vDs.getProjectName()));
                    }
                    if (MoffFilesMap.containsKey(vDs.getProjectName())) {
                        vDs.setMoff_quant_files(moffGalaxyIdsMap.get(vDs.getProjectName()), MoffFilesMap.get(vDs.getProjectName()));
                    }
                    if (cuiFilesMap.containsKey(vDs.getProjectName())) {
                        vDs.setCuiFiles(cuiGalaxyIdsMap.get(vDs.getProjectName()), cuiFilesMap.get(vDs.getProjectName()));
                    }
                });

                List<PeptideShakerVisualizationDataset> collection = new ArrayList<>(peptideShakerVisualizationMap.values());
                Collections.sort(collection);
                Collections.reverse(collection);
                peptideShakerVisualizationMap.clear();
                collection.forEach((ps) -> {
                    peptideShakerVisualizationMap.put(ps.getProjectName(), ps);
                });
                dsNumbers = peptideShakerVisualizationMap.size();
                filesNumber = mgfFilesMap.size() + fastaFilesMap.size() + searchSettingsFilesMap.size() + indexFilesMap.size() + rawFilesMap.size();
                historyFilesMap.putAll(peptideShakerVisualizationMap);
                historyFilesMap.putAll(mgfFilesMap);
                historyFilesMap.putAll(fastaFilesMap);
                historyFilesMap.putAll(searchSettingsFilesMap);
                historyFilesMap.putAll(indexFilesMap);
                historyFilesMap.putAll(rawFilesMap);
                jobRunning = (!runningJobSet.isEmpty());
                if (jobRunning) {
                    invokeTracker = false;
                }
                if (jobRunning || invokeTracker) {
                    followGalaxyJobs();
                }

            } catch (Exception e) {
                if (e.toString().contains("Service Temporarily Unavailable")) {
                    Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                } else {
                    System.err.println("Error:  galaxy history handler error " + e);
                    e.printStackTrace();
                }
            }

        }

        public History getWorkingHistory() {
            return workingHistory;
        }

        public String getUsedStorageSpace() {
            return usedStorageSpace;
        }

        public int getFilesNumber() {
            return filesNumber;
        }

        public int getDsNumbers() {
            return dsNumbers;
        }

        @Override
        public Map<String, GalaxyFileObject> call() throws Exception {
            return mgfFilesMap;
        }

        public boolean isJobRunning() {
            return jobRunning;
        }

        public boolean isInvokeTracker() {
            return invokeTracker;
        }

        public void setInvokeTracker(boolean invokeTracker) {
            this.invokeTracker = invokeTracker;
        }

    }

    /**
     * Get pathway information for accessions list
     *
     * @param proteinAcc protein accession list
     * @return edges data for the selected accessions
     *
     */
    public abstract Set<String[]> getPathwayEdges(Set<String> proteinAcc);

    /**
     * Retrieve dataset details from index to share in link
     *
     * @param dsKey dataset public key
     * @return encoded dataset details to visualise
     */
    public abstract String getDatasetSharingLink(int dsKey);

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private Map<String, Object> jsonToMap(org.json.simple.JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keySet().iterator();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof org.json.simple.JSONArray) {
                value = toList((org.json.simple.JSONArray) value);
            } else if (value instanceof org.json.simple.JSONObject) {
                value = jsonToMap((org.json.simple.JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private List<Object> toList(org.json.simple.JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof org.json.simple.JSONArray) {
                value = toList((org.json.simple.JSONArray) value);
            } else if (value instanceof org.json.simple.JSONObject) {
                value = jsonToMap((org.json.simple.JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

}