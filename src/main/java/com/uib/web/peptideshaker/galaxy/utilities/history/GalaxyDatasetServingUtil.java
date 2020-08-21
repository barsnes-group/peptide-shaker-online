package com.uib.web.peptideshaker.galaxy.utilities.history;

import com.compomics.util.experiment.mass_spectrometry.spectra.Peak;
import com.compomics.util.experiment.mass_spectrometry.spectra.Precursor;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

/**
 * Utility class for galaxy files that helps in managing the integration and
 * data transfer between Galaxy Server and Online Peptide Shaker (managing
 * requests and responses)
 *
 * @author Yehia Farag
 */
public class GalaxyDatasetServingUtil {

    /**
     * The Galaxy server address (URL).
     */
    private final String galaxyLink;
    /**
     * Requests parameters values.
     */
    private final GalaxyDatasetServingUtil.ParameterNameValue[] params;

    /**
     * Update the file settings to be ready for reading the data.
     *
     * @param galaxyLink In use Galaxy server URL
     * @param userApiKey the required API key to access galaxy API
     */
    public GalaxyDatasetServingUtil(String galaxyLink, String userApiKey) {
        this.galaxyLink = galaxyLink;
        params = new GalaxyDatasetServingUtil.ParameterNameValue[]{
                new GalaxyDatasetServingUtil.ParameterNameValue("key", userApiKey), new GalaxyDatasetServingUtil.ParameterNameValue("offset", ""),};

    }

    /**
     * Get MSn spectrum object using HTML request to Galaxy server (byte serving
     * support).
     *
     * @param startIndex  the spectra index on the MGF file
     * @param historyId   the Galaxy Server History ID that contain the MGF file
     * @param MGFGalaxyID The ID of the MGF file on Galaxy Server
     * @param MGFFileName The MGF file name
     * @return MSnSpectrum spectrum object
     */
    public Spectrum getSpectrum(long startIndex, String historyId, String MGFGalaxyID, String MGFFileName, int charge) {
        try {
            StringBuilder locationBuilder = new StringBuilder(galaxyLink + "/api/histories/" + historyId + "/contents/" + MGFGalaxyID + "/display?");
            params[1].value = (startIndex - 11) + "";
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    locationBuilder.append('&');
                }
                locationBuilder.append(params[i].name).append('=').append(params[i].value);
            }
            String location = locationBuilder.toString();
            URL website = new URL(location);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            double precursorMz = 0, precursorIntensity = 0, rt = -1.0, rt1 = -1, rt2 = -1;
            int[] precursorCharges = null;
            String scanNumber = "", spectrumTitle = "";
            HashMap<Double, Peak> spectrum = new HashMap<>();
            String line;
            boolean insideSpectrum = false;
            ArrayList<Double> mzList = new ArrayList<>(0);
            ArrayList<Double> intensityList = new ArrayList<>(0);

            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                while ((line = bin.readLine()) != null) {
                    JSONObject jsonObject = new JSONObject(line);
                    if (jsonObject != JSONObject.NULL) {
                        Map<String, Object> map = jsonToMap(jsonObject);
                        line = map.get("ck_data").toString();
                    }
                    String[] spectrumData = line.split("\n");
                    for (String str : spectrumData) {
                        line = str;
                        // fix for lines ending with \r
                        if (line.endsWith("\r")) {
                            line = line.replace("\r", "");
                        }

                        if (line.startsWith("BEGIN")) {
                            insideSpectrum = true;
                            mzList = new ArrayList<>();
                            intensityList = new ArrayList<>();
                        } else if (line.startsWith("TITLE")) {
                            insideSpectrum = true;
                            spectrumTitle = line.substring(line.indexOf('=') + 1);
                            try {
                                spectrumTitle = URLDecoder.decode(spectrumTitle, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                System.out.println("Error: 113 :An exception was thrown when trying to decode an mgf title: " + spectrumTitle + "  " + e);
                            }
                        } else if (line.startsWith("CHARGE")) {
                            precursorCharges = parseCharges(line);
                        } else if (line.startsWith("PEPMASS")) {
                            String temp = line.substring(line.indexOf("=") + 1);
                            String[] values = temp.split("\\s");
                            precursorMz = Double.parseDouble(values[0]);
                            if (values.length > 1) {
                                precursorIntensity = Double.parseDouble(values[1]);
                            } else {
                                precursorIntensity = 0.0;
                            }
                        } else if (line.startsWith("RTINSECONDS")) {
                            try {
                                String rtInput = line.substring(line.indexOf('=') + 1);
                                String[] rtWindow = rtInput.split("-");
                                if (rtWindow.length == 1) {
                                    String tempRt = rtWindow[0];
                                    // possible fix for values like RTINSECONDS=PT121.250000S
                                    if (tempRt.startsWith("PT") && tempRt.endsWith("S")) {
                                        tempRt = tempRt.substring(2, tempRt.length() - 1);
                                    }
                                    rt = new Double(tempRt);
                                } else if (rtWindow.length == 2) {
                                    rt1 = new Double(rtWindow[0]);
                                    rt2 = new Double(rtWindow[1]);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("An exception was thrown when trying to decode the retention time: " + spectrumTitle);
                                e.printStackTrace();
                                // ignore exception, RT will not be parsed
                            }
                        } else if (line.startsWith("TOLU")) {
                            // peptide tolerance unit not implemented
                        } else if (line.startsWith("TOL")) {
                            // peptide tolerance not implemented
                        } else if (line.startsWith("SEQ")) {
                            // sequence qualifier not implemented
                        } else if (line.startsWith("COMP")) {
                            // composition qualifier not implemented
                        } else if (line.startsWith("ETAG")) {
                            // error tolerant search sequence tag not implemented
                        } else if (line.startsWith("TAG")) {
                            // sequence tag not implemented
                        } else if (line.startsWith("SCANS")) {
                            try {
                                scanNumber = line.substring(line.indexOf('=') + 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new IllegalArgumentException("Cannot parse scan number.");
                            }
                        } else if (line.startsWith("INSTRUMENT")) {
                            // ion series not implemented
                        } else if (line.startsWith("END")) {

                            if (precursorCharges == null) {
                                precursorCharges = new int[]{charge};
                            }
                            Precursor precursor;
                            if (rt1 != -1 && rt2 != -1) {
                                precursor = new Precursor(precursorMz, precursorIntensity, precursorCharges, rt1, rt2);
                            } else {
                                precursor = new Precursor(rt, precursorMz, precursorIntensity, precursorCharges);
                            }

                            double[] mzArray = mzList.stream()
                                    .mapToDouble(
                                            a -> a
                                    )
                                    .toArray();
                            double[] intensityArray = intensityList.stream()
                                    .mapToDouble(
                                            a -> a
                                    )
                                    .toArray();
                            return new Spectrum(precursor, mzArray, intensityArray);

                        } else if (insideSpectrum && !line.equals("")) {
                            try {
                                String values[] = line.split("\\s+");
                                double mz = Double.parseDouble(values[0]);
                                mzList.add(mz);
                                double intensity = Double.parseDouble(values[1]);
                                intensityList.add(intensity);
                            } catch (Exception e1) {
                                // ignore comments and all other lines
                            }
                        }

                    }

                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("we will return null spectrum");
        return null;
    }

    /**
     * Parses the charge line of an MGF files.
     *
     * @param chargeLine the charge line
     * @return the possible charges found
     * @throws IllegalArgumentException
     */
    private int[] parseCharges(String chargeLine) {

        ArrayList<Integer> result = new ArrayList<>(1);
        String tempLine = chargeLine.substring(chargeLine.indexOf("=") + 1);
        String[] chargesAnd = tempLine.split(" and ");
        ArrayList<String> chargesAsString = new ArrayList<>();

        for (String charge : chargesAnd) {
            for (String charge2 : charge.split(",")) {
                chargesAsString.add(charge2.trim());
            }
        }
        for (String chargeAsString : chargesAsString) {

            chargeAsString = chargeAsString.trim();

            if (!chargeAsString.isEmpty()) {
                try {
                    if (chargeAsString.endsWith("+")) {
                        int value = Integer.parseInt(chargeAsString.substring(0, chargeAsString.length() - 1));
                        result.add(value);
                    } else if (chargeAsString.endsWith("-")) {
                        int value = Integer.parseInt(chargeAsString.substring(0, chargeAsString.length() - 1));
                        result.add(value);
                    } else if (!chargeAsString.equalsIgnoreCase("Mr")) {
                        int value = Integer.parseInt(chargeAsString);
                        result.add(value);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("\'" + chargeAsString + "\' could not be processed as a valid precursor charge!");
                }
            }
        }
        // if empty, add a default charge of 1
        if (result.isEmpty()) {
            result.add(1);
        }

        return result.stream()
                .mapToInt(a -> a)
                .toArray();
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
     * Private inner class represents encoded name and value of parameters that
     * is used in HTML requests
     *
     * @author Yehia Farag
     */
    private class ParameterNameValue {

        /**
         * Parameter name
         */
        private String name;
        /**
         * Parameter value
         */
        private String value;

        /**
         * Initialise the parameters objects
         *
         * @param name  parameter name
         * @param value parameter value
         */
        public ParameterNameValue(String name, String value) {
            try {
                this.name = URLEncoder.encode(name, "UTF-8");
                this.value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }
}
