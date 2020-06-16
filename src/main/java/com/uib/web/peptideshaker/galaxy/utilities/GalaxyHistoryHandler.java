package com.uib.web.peptideshaker.galaxy.utilities;

import com.uib.web.peptideshaker.galaxy.utilities.history.GalaxyDatasetServingUtil;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.wolfie.refresher.Refresher;
import com.uib.web.peptideshaker.PeptidShakerUI;
import com.uib.web.peptideshaker.model.core.LinkUtil;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

import com.vaadin.ui.UI;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.ServletContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
    private GalaxyInstance Galaxy_Instance;
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
    private boolean jobsInProgress = false;
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
     * Refresher to keep tracking history state in galaxy.
     */
    private final Refresher REFRESHER;
    /**
     * Refresher listener allow action on adding different actions for the
     * application refresher.
     */
    private Refresher.RefreshListener refreshlistener;
    /**
     * Date formatter to allow reading the creation date of the datasets from
     * the dataset json file.
     */
    private final DateFormat df6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * push to update the file system presenter.
     */
    private boolean updatePresenterView;
    /**
     * The alternative class to deal with Galaxy server API through REST
     * services.
     */
    private final GalaxyAPIInteractiveLayer galaxyApiInteractiveLayer = new GalaxyAPIInteractiveLayer();

    /**
     * Constructor to initialise the the galaxy history handler, and Connection
     * refresher to keep tracking running jobs on Galaxy Server.
     *
     */
    public GalaxyHistoryHandler() {
        REFRESHER = new Refresher();
        ((PeptidShakerUI) UI.getCurrent()).addExtension(REFRESHER);

        refreshlistener = (Refresher source) -> {
            HistoriesClient loopGalaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
            List<History> historiesList = Galaxy_Instance.getHistoriesClient().getHistories();
            boolean ready = false;
            for (History history : historiesList) {
                ready = (loopGalaxyHistoriesClient.showHistory(history.getId()).isReady());
                if (!ready) {
                    break;
                }
            }
            if (ready) {
                REFRESHER.removeListener(refreshlistener);
                updateHistory(updatePresenterView);
                REFRESHER.setRefreshInterval(1000);
                updatePresenterView = false;
                Notification.show("Data is ready to display", Notification.Type.ASSISTIVE_NOTIFICATION);
            }
        };

    }

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
    public void connectToGalaxy(GalaxyInstance Galaxy_Instance, File user_folder) {
        this.Galaxy_Instance = Galaxy_Instance;
        this.galaxyDatasetServingUtil = new GalaxyDatasetServingUtil(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());
        this.user_folder = user_folder;
        GalaxyHistoryHandler.this.updateHistory(true);
    }

    /**
     * Update galaxy layer file system datasets from Galaxy server.
     *
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    public void updateHistory(boolean updatePresenterView) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        updateDatasetructureTask = new UpdateDatasetructureTask(updatePresenterView);
        updateDatasetructureFuture = executorService.submit(updateDatasetructureTask);
        executorService.shutdown();
    }

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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getFastaFilesMap();
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return updateDatasetructureTask.call();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return updateDatasetructureTask.getRawFilesMap();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return updateDatasetructureTask.getUsedStorageSpace();
    }

    /**
     * Get the number of viewable files available for the user
     *
     * @return integer number of available files
     */
    public int getFilesNumber() {
        while (!updateDatasetructureFuture.isDone()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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
     * @param updatePresenterView open file system view after updating the file
     * system
     * @param toDeleteMap datasets to delete after finishing work
     */
    public abstract void synchronizeDataWithGalaxyServer(Map<String, GalaxyFileObject> historyFilesMap, boolean jobsInProgress, boolean updatePresenterView, Set<String> toDeleteMap);

    /**
     * Following jobs statues on Galaxy server until all jobs are done.
     *
     * @param updatePresenterView open file system view after updating the file
     * system
     */
    private void invokeRecheckDataProcessing(boolean updatePresenterView) {
        int mSecound = 20000;
        this.updatePresenterView = updatePresenterView;
        if (refreshlistener != null) {
            REFRESHER.removeListener(refreshlistener);

        }
        REFRESHER.setRefreshInterval(mSecound);
        REFRESHER.addListener(refreshlistener);

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
        private final Map<String, GalaxyFileObject> indexedMgfFilesMap;
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
        private final Map<String, GalaxyTransferableFile> moffFilesMap;
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

        public Map<String, GalaxyTransferableFile> getSearchSettingsFilesMap() {
            return searchSettingsFilesMap;
        }

        public Map<String, GalaxyFileObject> getMgfFilesMap() {
            return mgfFilesMap;
        }

        public Map<String, GalaxyFileObject> getIndexedMgfFilesMap() {
            return indexedMgfFilesMap;
        }

        public Map<String, GalaxyFileObject> getRawFilesMap() {
            return rawFilesMap;
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
         * error with java.util.ConcurrentModificationException**
         */
        public UpdateDatasetructureTask(boolean updatePresenterView) {
            jobsInProgress = false;
            this.mgfFilesMap = new LinkedHashMap<>();
            this.indexedMgfFilesMap = new LinkedHashMap<>();
            this.rawFilesMap = new LinkedHashMap<>();
            this.historyFilesMap = new LinkedHashMap<>();
            this.fastaFilesMap = new LinkedHashMap<>();
            this.moffFilesMap = new LinkedHashMap<>();

            this.indexFilesMap = new LinkedHashMap<>();
            this.peptideShakerVisualizationMap = new LinkedHashMap<>();
            this.searchGUIFilesMap = new LinkedHashMap<>();
            this.historiesIds = new HashSet<>();
            this.searchSettingsFilesMap = new LinkedHashMap<>();

            try {
                String requestSearching = Page.getCurrent().getLocation().toString();
                if (requestSearching.contains("toShare_-_")) {
                    int dsKey = -1;
                    final LinkUtil linkUtil = new LinkUtil();
                    if (requestSearching.split("toShare_-_").length > 1) {
                        try {
                            String dsKeyAsString = requestSearching.split("toShare_-_")[1];
                            dsKey = Integer.valueOf(linkUtil.decrypt(dsKeyAsString));
                        } catch (NumberFormatException e) {
                        }
                    }
                    String encodedDsDetails = getDatasetSharingLink(dsKey);
                    if (encodedDsDetails != null) {
                        String deCrypted = linkUtil.decrypt(encodedDsDetails);
                        String[] dsDetails = deCrypted.split("-_-");
                        String project_name = dsDetails[0];
                        PeptideShakerVisualizationDataset externaldataset = new PeptideShakerVisualizationDataset(project_name, user_folder, Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey(), galaxyDatasetServingUtil, getCsf_pr_Accession_List()) {
                            @Override
                            public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                                return GalaxyHistoryHandler.this.getPathwayEdges(proteinAcc);
                            }
                        };
                        dsDetails[3] = dsDetails[3].replace("mgf-_:_-\\", "");
                        for (String indexedmgf : dsDetails[3].split("\\+")) {
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(indexedmgf.split("\\(API:")[0]);
                            ds.setType("MGF");
                            ds.setGalaxyId(indexedmgf.split("\\(API:")[1].replace(")", ""));
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + ds.getGalaxyId() + "/display");
                            ds.setStatus("ok");
                            externaldataset.addMgfFiles(ds.getName(), ds);

                        }
                        externaldataset.setStatus("ok", true);
                        externaldataset.setType("Web Peptide Shaker Dataset");
                        externaldataset.setName(project_name);
                        externaldataset.setToShareDataset(true);
                        GalaxyFileObject ds = new GalaxyFileObject();
                        ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + dsDetails[1].split("-_:_-")[1] + "/display?");
                        ds.setStatus("ok");
                        ds.setType("SearchGUI");
                        ds.setOverview(dsDetails[4].split("-_:_-")[1]);
                        externaldataset.setSearchGUIResultFile(ds);
                        externaldataset.setPeptideShakerResultsFileId(dsDetails[2].split("-_:_-")[1], true);
                        externaldataset.setGalaxyId(dsDetails[2].split("-_:_-")[1]);
                        externaldataset.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + dsDetails[2].split("-_:_-")[1] + "/display?to_ext=zip");
                        if (deCrypted.contains("-_-quant-_:_-")) {
                            ds = new GalaxyFileObject();
                            ds.setName(project_name);
                            ds.setType("MOFF Quant");
                            ds.setGalaxyId(deCrypted.split("-_-quant-_:_-")[1]);
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + "/datasets/" + deCrypted.split("-_-quant-_:_-")[1] + "/display?");
                            ds.setStatus("ok");

                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            externaldataset.setMoff_quant_file(file);
                        }

                        historyFilesMap.put(project_name + "_ExternalDS", externaldataset);

                        if (jobsInProgress) {
                            invokeRecheckDataProcessing(updatePresenterView);
                        }
                        synchronizeDataWithGalaxyServer(historyFilesMap, jobsInProgress, updatePresenterView, toDeleteMap);
                        return;
                    }
                }

                HistoriesClient galaxyHistoriesClient = Galaxy_Instance.getHistoriesClient();
                List<History> historiesList = galaxyHistoriesClient.getHistories();
                if (historiesList.isEmpty()) {
                    galaxyHistoriesClient.create(new History("Online-PeptideShaker-History"));
                    workingHistory = galaxyHistoriesClient.create(new History("Online-PeptideShaker-Job-History"));
                } else {
                    for (History h : historiesList) {
                        if (h.getName().equalsIgnoreCase("Online-PeptideShaker-Job-History")) {
                            workingHistory = h;
                        }
                    }
                    if (workingHistory == null) {
                        workingHistory = Galaxy_Instance.getHistoriesClient().create(new History("Online-PeptideShaker-Job-History"));
                    }
                }
                historiesList = galaxyHistoriesClient.getHistories();
                for (History history : historiesList) {
                    historiesIds.add(history.getId());

                }
                List<Map<String, Object>> results = galaxyApiInteractiveLayer.getDatasetIdList(Galaxy_Instance.getGalaxyUrl(), Galaxy_Instance.getApiKey());//sresp.getResults();
                String userID = Galaxy_Instance.getUsersClient().getUsers().get(0).getId();
                usedStorageSpace = galaxyApiInteractiveLayer.getUserMemoryUsage(Galaxy_Instance.getGalaxyUrl(), userID, Galaxy_Instance.getApiKey());
                results.stream().filter((map) -> map != null && (!((map.get("purged") + "").equalsIgnoreCase("true") || (!historiesIds.contains(map.get("history_id") + "")) || (map.get("deleted") + "").equalsIgnoreCase("true")))).forEachOrdered((Map<String, Object> map) -> {
                    if (map.get("name").toString().contains("-original-input")) {
                        toDeleteMap.add(map.get("history_id") + ";" + map.get("id").toString());
                    } else if (map.containsKey("collection_type") && map.get("collection_type").toString().equalsIgnoreCase("list")) {
                        List elements = ((List) map.get("elements"));
                        for (Object element : elements) {
                            Map<String, Object> mgfElement = (Map<String, Object>) element;
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(map.get("name").toString().replace("-Indexed-MGF", "") + "-" + mgfElement.get("element_identifier") + "-Multi-Indexed-MGF");
                            ds.setType("Indexed-MGF");
                            mgfElement = ((Map<String, Object>) mgfElement.get("object"));
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(mgfElement.get("id").toString());
                            ds.setStatus(mgfElement.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            this.indexedMgfFilesMap.put(ds.getName(), ds);
                        }
                    } else {
                        if (map.get("state").toString().equalsIgnoreCase("new") || map.get("state").toString().equalsIgnoreCase("running") || map.get("state").toString().equalsIgnoreCase("queued")) {
                            jobsInProgress = true;
                        }
                        if (map.get("extension").toString().equalsIgnoreCase("tabular") && map.get("name").toString().endsWith("-MOFF")) {
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
                                ex.printStackTrace();
                            }
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());

                            this.moffFilesMap.put(map.get("name").toString().replace("-MOFF", ""), file);
                            file.setCreate_time(ds.getCreate_time());

                        }
                        if ((map.get("extension") + "").equalsIgnoreCase("searchgui_archive")) {
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setStatus(map.get("state") + "");
                            if (ds.getStatus().equalsIgnoreCase("new") || ds.getStatus().equalsIgnoreCase("running") || ds.getStatus().equalsIgnoreCase("queued")) {
                                jobsInProgress = true;
                            }
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "").replace( "_-_",":")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            ds.setName(map.get("name").toString());
                            ds.setType("SearchGUI");
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setOverview(map.get("misc_info") + "");
                            searchGUIFilesMap.put(map.get("name").toString().replace("-SearchGUI Results", ""), ds);

                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
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
                                ds.setCreate_time(df6.parse((map.get("create_time") + "").replace( "_-_",":")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            GalaxyTransferableFile file = new GalaxyTransferableFile(user_folder, ds, false);
                            file.setDownloadUrl(ds.getDownloadUrl());
                            this.searchSettingsFilesMap.put(ds.getGalaxyId(), file);
                            file.setCreate_time(ds.getCreate_time());
                        } else if (map.get("extension").toString().equalsIgnoreCase("mgf") || (map.get("extension").toString().equalsIgnoreCase("auto") && map.get("name").toString().endsWith(".mgf"))) {
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
                                ex.printStackTrace();
                            }
                            this.mgfFilesMap.put(ds.getGalaxyId(), ds);
                        } else if (map.get("extension").toString().equalsIgnoreCase("tabular") && map.get("name").toString().endsWith("Indexed-MGF")) {
                            GalaxyFileObject ds = new GalaxyFileObject();
                            ds.setName(map.get("name").toString());
                            ds.setType("Indexed-MGF");
                            ds.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            ds.setHistoryId(map.get("history_id") + "");
                            ds.setGalaxyId(map.get("id").toString());
                            ds.setStatus(map.get("state") + "");
                            try {
                                ds.setCreate_time(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            this.indexedMgfFilesMap.put(ds.getName(), ds);

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
                                ex.printStackTrace();
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
                                ex.printStackTrace();
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
                            vDs.setName(projectId);;
                            vDs.setPeptideShakerResultsFileId(map.get("id").toString(), false);
                            vDs.setStatus(map.get("state") + "");
                            vDs.setGalaxyId(map.get("id").toString());
                            vDs.setDownloadUrl(Galaxy_Instance.getGalaxyUrl() + map.get("url") + "/display?");
                            if (map.get("state").toString().equalsIgnoreCase("new") || map.get("state").toString().equalsIgnoreCase("running") || map.get("state").toString().equalsIgnoreCase("queued")) {
                                jobsInProgress = true;
                            }
                            try {
                                vDs.setCreateTime(df6.parse((map.get("create_time") + "")));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

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
                    indexedMgfFilesMap.keySet().stream().filter((key) -> (key.contains(vDs.getProjectName()))).forEachOrdered((key) -> {
                        vDs.addMgfFiles(key, indexedMgfFilesMap.get(key));
                    });
                    moffFilesMap.keySet().stream().filter((key) -> (key.contains(vDs.getProjectName()))).forEachOrdered((key) -> {
                        vDs.setMoff_quant_file(moffFilesMap.get(key));
                    });
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

                if (jobsInProgress) {

                    invokeRecheckDataProcessing(updatePresenterView);

                }
                synchronizeDataWithGalaxyServer(historyFilesMap, jobsInProgress, updatePresenterView, toDeleteMap);
            } catch (Exception e) {
                if (e.toString().contains("Service Temporarily Unavailable")) {
                    Notification.show("Service Temporarily Unavailable", Notification.Type.ERROR_MESSAGE);
                } else {
                    e.printStackTrace();
                    if (VaadinSession.getCurrent() != null && VaadinSession.getCurrent().getSession() != null) {
                        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(10);
                    }
                    Page.getCurrent().reload();

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

}
