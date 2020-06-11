package com.uib.web.peptideshaker.galaxy.utilities.history;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class represents FASTA utilities that allow using the UniProt
 * web-service to get protein information in case of some missing information in
 * proteins file
 *
 * @author Yehia Farag
 */
public class FastaFileWebService {
    /**
     * Protein evidence list.
     */
    private final String[] proteinEvidence = new String[]{"Not Available", "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};

    /**
     * Update protein information from Uniprot web service.
     *
     * @param protein protein object to store/update protein information
     * @param accession the protein accession;
     * @return updated protein object
     */
    public ProteinGroupObject updateProteinInformation(ProteinGroupObject protein, String accession) {
        if (protein == null) {
            protein = new ProteinGroupObject();
            protein.setAccession(accession);
            System.out.println("itr was null protein :( ");
        }
        if (protein.getSequence() == null) {
            try {
                URL website = new URL("http://www.uniprot.org/uniprot/" + protein.getAccession() + ".fasta");
                URLConnection conn = website.openConnection();
                InputStream in = conn.getInputStream();
                try (BufferedReader bin = new BufferedReader(new InputStreamReader(in))) {
                    String fastaHeader = bin.readLine();
                    String sequence = "";
                    String line;
                    while ((line = bin.readLine()) != null) {
                        sequence += line;
                    }
                    protein.setSequence(sequence);
                    protein.setProteinEvidence(proteinEvidence[Integer.parseInt(fastaHeader.split("PE=")[1].split(" ")[0])]);
                }

            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }
       
        return protein;

    }
}
