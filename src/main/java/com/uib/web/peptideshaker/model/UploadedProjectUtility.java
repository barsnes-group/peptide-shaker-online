package com.uib.web.peptideshaker.model;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class responsible for process project uploaded files and generate
 * compatible visualisation Dataset
 *
 * @author Yehia Farag
 */
public abstract class UploadedProjectUtility {

    /**
     * Prepare visualisation dataset from uploaded uploaded files by checking
     * all uploaded files
     *
     * @param projectName
     * @param uploadedFileMap
     * @param csf_pr_Accession_List
     * @return array of checked valid files
     */
    public boolean[] processVisualizationDataset(String projectName, Map<String, PluploadFile> uploadedFileMap, Set<String> csf_pr_Accession_List) {

        File fastaFile = null;
        File proteinFile = null;
        File peptideFile = null;
        boolean[] checkFiles = new boolean[2];

        List<String> filesnames = new ArrayList<>(3);
        filesnames.add("");
        filesnames.add("");
        filesnames.add("");
        for (String key : uploadedFileMap.keySet()) {
            switch (key) {
                case "FASTA":
                    fastaFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    filesnames.set(0, uploadedFileMap.get(key).getName());
                    break;
                case "Protein":
                    proteinFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    checkFiles[0] = checkProteinsFile(proteinFile);
                    filesnames.set(1, uploadedFileMap.get(key).getName());
                    break;
                case "Peptide":
                    peptideFile = (File) uploadedFileMap.get(key).getUploadedFile();
                    checkFiles[1] = checkPeptideFile(peptideFile);
                    filesnames.set(2, uploadedFileMap.get(key).getName());
                    break;

            }
        }
        if (!checkFiles[0] || !checkFiles[1]) {
            return checkFiles;
        }
        projectName = projectName.replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Timestamp(System.currentTimeMillis())) + "___" + filesnames.toString().replace(" ", "").replace("[", "").replace("]", ""));

        PeptideShakerVisualizationDataset psDs = new PeptideShakerVisualizationDataset(projectName, fastaFile, proteinFile, peptideFile, csf_pr_Accession_List) {
            @Override
            public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
                return new HashSet<>();
            }
        };
        viewUploadedProjectDataset(psDs);
        return checkFiles;

    }

    /**
     * check peptides file is valid
     *
     * @param peptides_file File
     * @return file is valid
     *
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
     *
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
     * Visualise uploaded dataset
     *
     * @param psDs dataset to visualise
     */
    public abstract void viewUploadedProjectDataset(PeptideShakerVisualizationDataset psDs);
}
