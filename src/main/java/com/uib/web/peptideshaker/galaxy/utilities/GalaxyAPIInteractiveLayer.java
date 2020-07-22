package com.uib.web.peptideshaker.galaxy.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class will include required API connections that is no longer supported
 * in blind4J
 *
 * @author Yehia Farag
 */
public class GalaxyAPIInteractiveLayer {

    /**
     * Get list of datasets ids on galaxy server
     *
     * @param galaxyUrl web address to galaxy
     * @param apiKey user API key
     * @return map of available dataset objects on galaxy
     */
    public List<Map<String, Object>> getDatasetIdList(String galaxyUrl, String apiKey) {

        try {

            URL website = new URL(galaxyUrl + "/api/datasets/?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(stringToParse);
            List<Object> dataList = toList(json);

            return filterDatasets(dataList, galaxyUrl, apiKey);

        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Fill missing information for specific dataset
     *
     * @param galaxyUrl web address for galaxy
     * @param dsId dataset id
     * @param historyId history id
     * @param apiKey user API key
     * @return dataset object map after filling all the missing information
     */
    public Map<String, Object> getDatasetInformation(String galaxyUrl, String dsId, String historyId, String apiKey) {

        try {
            URL website = new URL(galaxyUrl + "/api/histories/" + historyId + "/contents/datasets/" + dsId + "?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            return jsonToMap(json);
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    /**
     * Get the user usage of memory on galaxy server
     *
     * @param galaxyUrl web address for galaxy
     * @param userId user id
     * @param apiKey user API key
     * @return the memory usage as string
     */
    public String getUserMemoryUsage(String galaxyUrl, String userId, String apiKey) {

        try {

            URL website = new URL(galaxyUrl + "/api/users/" + userId + "?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            Map<String, Object> dataList = jsonToMap(json);
            return dataList.get("nice_total_disk_usage") + "";

        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return "Not Available";
    }

    /**
     * filter the dataset list removing collections and deleted datasets
     *
     * @param dataList full dataset list
     * @param galaxyUrl url for galaxy server
     * @param apiKey user API key on galaxy server
     * @return filtered map of datasets
     *
     */
    private List<Map<String, Object>> filterDatasets(List<Object> dataList, String galaxyUrl, String apiKey) {
        List<Map<String, Object>> convertedList = new ArrayList<>();
        int dsSize = dataList.size();
        for (int i = 0; i < dsSize; i++) {
            Map<String, Object> datasetMap = (Map<String, Object>) dataList.get(i);
            if ((datasetMap.containsKey("collection_type") && datasetMap.get("deleted").toString().equalsIgnoreCase("false"))) {
//                if (datasetMap.get("name").toString().endsWith("-Indexed-MGF")) {
                Map<String, Object> collectionMap = getCollectionElements(datasetMap.get("url").toString(), galaxyUrl, apiKey);
                datasetMap.put("elements", ((List) collectionMap.get("elements")));
//                }
            }
            if ((datasetMap.containsKey("collection_type")) && (datasetMap.get("purged") + "").equalsIgnoreCase("true") || (datasetMap.get("deleted") + "").equalsIgnoreCase("true")) {
                continue;
            }
            if ((datasetMap.get("extension") + "").equalsIgnoreCase("searchgui_archive")) {
                datasetMap.put("misc_info", getDatasetInformation(galaxyUrl, (datasetMap.get("id") + ""), (datasetMap.get("history_id") + ""), apiKey).get("misc_info"));
            }
            convertedList.add(datasetMap);

        }

        return convertedList;

    }

    /**
     * Get data inside collections
     *
     * @param url the url of collection
     * @param galaxyUrl web address for galaxy server
     * @param apiKey user API key on galaxy server
     * @return filtered map of datasets
     *
     */
    private Map<String, Object> getCollectionElements(String url, String galaxyUrl, String apiKey) {
        try {
            URL website = new URL(galaxyUrl + url + "?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse = "";
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            } catch (IOException ex) {
                Logger.getLogger(GalaxyAPIInteractiveLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            return jsonToMap(json);

        } catch (MalformedURLException ex) {
            System.out.println("at exception 184 " + ex.getMessage());
        } catch (ParseException | JSONException | IOException ex) {
            System.out.println("at exception 184 " + ex.getMessage());
        }
        return new HashMap<>();
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

        Iterator<String> keysItr = object.keySet().iterator();
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
        for (int i = 0; i < array.size(); i++) {
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
     * Get the user usage of memory on galaxy server
     *
     * @param galaxyUrl web address for galaxy
     * @param userId user id
     * @param apiKey user API key
     * @return the memory usage as string
     */
    public boolean isJobDone(String galaxyUrl, String jobId, String apiKey) throws MalformedURLException, ParseException, JSONException, IOException ,FileNotFoundException{

   
            URL website = new URL(galaxyUrl + "/api/jobs/" + jobId + "?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            Map<String, Object> dataList = jsonToMap(json);
            System.out.println("at history ready " + dataList.get("state"));
            return dataList.get("state").toString().equalsIgnoreCase("ok") || dataList.get("state").toString().equalsIgnoreCase("error") || dataList.get("state").toString().equalsIgnoreCase("paused");

        
    }

    public Set<String> isHistoryReady(String galaxyUrl, String historyId, String apiKey) {
        Set<String> runningJobs = new HashSet<>();
        try {

            URL website = new URL(galaxyUrl + "/api/histories/" + historyId + "?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            
            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            Map<String, Object> dataList = jsonToMap(json);
            String stat = dataList.get("state").toString();
 for(String key:dataList.keySet())
                System.out.println("at map id "+ key+"  "+dataList.get(key));
            dataList = (Map<String, Object>) dataList.get("state_ids");
            System.out.println("     -----------------------            ");
            for(String key:dataList.keySet())
                System.out.println("at stat id "+ key+"  "+dataList.get(key));
            
            List<String> jobs = (List<String>) dataList.get("running");
            runningJobs.addAll(jobs);
            System.out.println("running "+jobs);
            jobs = (List<String>) dataList.get("new");
            System.out.println("new "+jobs);
            runningJobs.addAll(jobs);
            jobs = (List<String>) dataList.get("paused");
            System.out.println("paused "+jobs);
            runningJobs.addAll(jobs);
            jobs = (List<String>) dataList.get("queued");
            runningJobs.addAll(jobs);
            System.out.println("quwued "+jobs);
            jobs = (List<String>) dataList.get("upload");
            runningJobs.addAll(jobs);
            System.out.println("upload "+jobs);

            jobs = (List<String>) dataList.get("discarded");
            runningJobs.removeAll(jobs);
            System.out.println("discard "+jobs);
            jobs = (List<String>) dataList.get("error");
            runningJobs.removeAll(jobs);
            System.out.println("error "+jobs);
            jobs = (List<String>) dataList.get("ok");
            runningJobs.removeAll(jobs);
            System.out.println("ok "+jobs);
            
            
           
            System.out.println("----------------------------" + stat + "---------------------------------"+runningJobs);

        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return runningJobs;
    }

}
