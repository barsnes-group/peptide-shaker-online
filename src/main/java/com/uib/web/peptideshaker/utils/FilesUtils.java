package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.model.CONSTANT;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import pl.exsio.plupload.PluploadFile;

/**
 * This class provides utilities for Files
 *
 * @author Yehia Mokhtar Farag
 */
public class FilesUtils implements Serializable {

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
            System.err.println("at Error: " + this.getClass().getName() + " " + ex);
        }

    }

    /**
     * Prepare visualisation dataset from uploaded uploaded files by checking
     * all uploaded files
     *
     * @param file file to validate
     * @param format file format     *
     * @return is valid file
     */
    public boolean validateUploadedFiles(File file, String format) {
        switch (format) {
            case CONSTANT.FASTA_FILE_EXTENSION:
                return checkFastaFile(file);
            case CONSTANT.PROTEIN_FILE_TYPE:
                return checkProteinsFile(file);
            case CONSTANT.PEPTIDE_FILE_TYPE:
                return checkPeptideFile(file);
        }

        return false;

    }

    /**
     * check peptides file is valid
     *
     * @param peptides_file File
     * @return file is valid
     */
    private boolean checkPeptideFile(File peptides_file) {
        try {
            File f = peptides_file;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f), 1024 * 100);
            HashSet<String> peptideFileHeaderIndexerMap = new HashSet<>();
            peptideFileHeaderIndexerMap.add("Protein Group(s)");
            peptideFileHeaderIndexerMap.add("Sequence");
            peptideFileHeaderIndexerMap.add("Modified Sequence");
            peptideFileHeaderIndexerMap.add("Variable Modifications");
            peptideFileHeaderIndexerMap.add("Fixed Modifications");
            peptideFileHeaderIndexerMap.add("#Validated PSMs");
            peptideFileHeaderIndexerMap.add("#PSMs");
            peptideFileHeaderIndexerMap.add("Confidence [%]");
            peptideFileHeaderIndexerMap.add("Validation");
            String line = bufferedReader.readLine();
            line = line.replace("\"", "");
            for (String str : line.split("\\t")) {
                str = str.trim();
                peptideFileHeaderIndexerMap.remove(str);
            }
            return peptideFileHeaderIndexerMap.isEmpty();
        } catch (IOException e) {

        }
        return false;
    }

    /**
     * check proteins file is valid
     *
     * @param proteins_file File
     * @return file is valid
     */
    private boolean checkProteinsFile(File proteins_file) {

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(proteins_file), 1024 * 100);
            String line;
            /**
             * escape header
             */
            line = bufferedReader.readLine();
            line = line.replace("\"", "");
            HashSet<String> proteinFileHeaderIndexerMap = new HashSet<>();
            proteinFileHeaderIndexerMap.add("Main Accession");
            proteinFileHeaderIndexerMap.add("Description");
            proteinFileHeaderIndexerMap.add("Chromosome");
            proteinFileHeaderIndexerMap.add("Protein Inference");
            proteinFileHeaderIndexerMap.add("Protein Group");
            proteinFileHeaderIndexerMap.add("#Validated Peptides");
            proteinFileHeaderIndexerMap.add("#Peptides");
            proteinFileHeaderIndexerMap.add("#Validated PSMs");
            proteinFileHeaderIndexerMap.add("#PSMs");
            proteinFileHeaderIndexerMap.add("Confidence [%]");
            proteinFileHeaderIndexerMap.add("Validation");
            for (String str : line.split("\\t")) {
                str = str.trim();
                proteinFileHeaderIndexerMap.remove(str);
            }
            return proteinFileHeaderIndexerMap.isEmpty();
        } catch (IOException e) {

        }
        return false;
    }

    /**
     * check fasta file is valid
     *
     * @param fastaFile File
     * @return file is valid
     */
    private boolean checkFastaFile(File fastaFile) {

//        LinkedHashMap fastaProteinMap = new LinkedHashMap<>();
        FileInputStream inStream;
        try {
            inStream = new FileInputStream(fastaFile);
            FastaReader<ProteinSequence, AminoAcidCompound> fastaReader
                    = new FastaReader<>(
                            inStream,
                            new GenericFastaHeaderParser<>(),
                            new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
            return fastaReader.process(1) != null;
//            LinkedHashMap<String, ProteinSequence> fastaProteinSequenceMap = fastaReader.process();

        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
