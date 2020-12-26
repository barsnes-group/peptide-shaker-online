package com.uib.web.peptideshaker.utils;

import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides utilities for Files
 *
 * @author Yehia Mokhtar Farag
 */
public class FilesUtils implements Serializable{

    /**
     * Read and convert the work-flow file into string (JSON like string) so the
     * system can execute the work-flow
     *
     * @param file the input file
     * @return the JSON string of the file content
     */
    public JsonObject readJsonFile(File file) {
        String json = "";
        String line;
        try {
            FileReader fileReader = new FileReader(file);
            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    json += (line);
                }
                // Always close files.
                bufferedReader.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading file '" + "'");
        }
        return new JsonObject(json);
    }
    
     /**
     * Save an object to JSON.
     *
     * @param anObject the input object
     * @param jsonFile the target file to which the JSON will be saved.
     *
     * @throws IOException if the object cannot be successfully saved into a
     * JSON file
     */
    public void saveObjectToJson(JsonObject anObject, File jsonFile) {        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(jsonFile))) {
            out.append(anObject.encodePrettily()).flush();
        } catch (IOException ex) {
            System.err.println("at Error: "+this.getClass().getName()+" "+ex);
        }

    }
}
