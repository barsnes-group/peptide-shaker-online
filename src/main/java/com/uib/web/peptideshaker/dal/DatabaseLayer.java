package com.uib.web.peptideshaker.dal;

import com.vaadin.server.VaadinSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents database layer that interact with mySQL database that
 * have Reactome data
 *
 * @author Yehia Farag
 */
public class DatabaseLayer {

    /**
     * Database URL.
     */
    private String dbURL;
    /**
     * Database name.
     */
    private String dbName;
    /**
     * Database driver.
     */
    private String dbDriver;
    /**
     * Database username.
     */
    private String dbUserName;
    /**
     * Database password.
     */
    private String dbPassword;
    private Connection conn = null;
    private boolean dbEnabled = true;

    /**
     * Initialise the database layer.
     */
    public DatabaseLayer() {
        if (VaadinSession.getCurrent().getAttribute("dbUserName") == null) {
            dbEnabled = false;
            return;
        }
        this.dbURL = VaadinSession.getCurrent().getAttribute("dbURL").toString();
        this.dbDriver = VaadinSession.getCurrent().getAttribute("dbDriver").toString();
        this.dbUserName = VaadinSession.getCurrent().getAttribute("dbUserName").toString();
        this.dbPassword = VaadinSession.getCurrent().getAttribute("dbPassword").toString();
        this.dbName = VaadinSession.getCurrent().getAttribute("dbName").toString();

    }

    /**
     * Check if the database available
     *
     * @return database available
     */
    public boolean isDbEnabled() {
        return dbEnabled;
    }

    /**
     * Get list of accessions available on csf-pr in order to allow mapping data
     * to csf-pr.
     *
     * @return set of Uni-prot protein accessions available on csf-pr
     *
     */
    public Set<String> getCsfprAccList() {
        Set<String> csf_pr_acc_list = new LinkedHashSet<>();
        if (!dbEnabled) {
            return csf_pr_acc_list;
        }
        String version = "";
        String updateStatment = "";
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            if (conn == null) {
                dbEnabled = false;
                return csf_pr_acc_list;
            }
            String selectStat = "SELECT * FROM  INFORMATION_SCHEMA.TABLES  WHERE `TABLE_SCHEMA` = '" + dbName.replace(".tables", "") + "';";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(selectStat);

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.contains("csf_pr_accssion_list_")) {
                    version = tableName.replace("csf_pr_accssion_list_", "");
                    break;
                }
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            dbEnabled = false;
            Logger.getLogger(DatabaseLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //check file on csfpr
        String csfprLink = VaadinSession.getCurrent().getAttribute("csfprLink") + "";
        String updateFileName = "csf_pr_available_prot_accs.txt";
        try {
            URL downloadableFile = new URL(csfprLink + updateFileName);
            URLConnection urlConn = downloadableFile.openConnection();
            urlConn.addRequestProperty("Connection", "keep-alive");
            InputStream in = urlConn.getInputStream();
            InputStreamReader r = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(r);
            String line = br.readLine();
            if (version.equalsIgnoreCase("") || (line != null && !line.contains(version))) {
                version = line.replace("CSF-PR Protein Accession List (", "").replace(")", "");
                updateStatment = "INSERT INTO `csf_pr_accssion_list_" + version + "` (`Accssion`) VALUES ";//('lozt'), ('bozot');
                while ((line = br.readLine()) != null) {
                    csf_pr_acc_list.add(line);
                    updateStatment += ("(?),");
                }
                updateStatment = updateStatment.substring(0, updateStatment.length() - 1) + ";";
            }
            if (!csf_pr_acc_list.isEmpty()) {
                Statement st = conn.createStatement();
                String statment = "CREATE TABLE IF NOT EXISTS `csf_pr_accssion_list_" + version + "` (\n"
                        + "  `Accssion` varchar(300) NOT NULL\n"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                st.executeUpdate(statment);
                st = conn.createStatement();
                statment = "TRUNCATE `csf_pr_accssion_list_" + version + "`;";
                st.executeUpdate(statment);

                try (PreparedStatement preparedStatment = conn.prepareStatement(updateStatment)) {
                    int i = 0;
                    for (String str : csf_pr_acc_list) {
                        i++;
                        preparedStatment.setString(i, str);
                    }
                    preparedStatment.executeUpdate();
                }

            } else {
                String selectStat = "SELECT * FROM csf_pr_accssion_list_" + version + ";";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(selectStat);
                while (rs.next()) {
                    String acc = rs.getString("Accssion");
                    csf_pr_acc_list.add(acc);
                }
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException | SQLException ex) {
            dbEnabled = false;
            ex.printStackTrace();
        } catch (Exception ex) {
            dbEnabled = false;
            ex.printStackTrace();
        }

        return csf_pr_acc_list;
    }

    /**
     * Get the edges information for selected accession list
     *
     * @param proteinAcc protein accession list
     * @return set of edge data
     */
    public Set<String[]> getPathwayEdges(Set<String> proteinAcc) {
        Set<String[]> edges = new LinkedHashSet<>();
        try {

            if (!dbEnabled) {
                return edges;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            String selectstatment = "SELECT `edge_index` FROM `accessionindex`  where ";
            for (String str : proteinAcc) {
                if (str.trim().equalsIgnoreCase("")) {
                    continue;
                }
                selectstatment = selectstatment + "`uniprot_acc`=? or ";
            }
            if (selectstatment.endsWith("where ")) {
                return edges;
            }
            selectstatment = selectstatment.substring(0, selectstatment.length() - 4) + ";";
            PreparedStatement selectNameStat = conn.prepareStatement(selectstatment);
            int indexer = 1;
            for (String str : proteinAcc) {
                if (str.trim().equalsIgnoreCase("")) {
                    continue;
                }
                selectNameStat.setString(indexer++, str);
            }
            ResultSet rs = selectNameStat.executeQuery();
            Set<Integer> lines = new HashSet<>();
            while (rs.next()) {
                lines.add(rs.getInt("edge_index"));
            }
            selectNameStat.close();
            rs.close();
            Set<Integer> indexes = new HashSet<>();

            for (int index : lines) {
                indexes.add(index);

            }
//            System.out.println("at total indexe for "+proteinAcc+"  "+indexes+"" );
            selectstatment = "SELECT * FROM `edges`  where ";
            for (Integer i : indexes) {
                selectstatment = selectstatment + "`edge_index`=? or ";

            }
            selectstatment = selectstatment.substring(0, selectstatment.length() - 4) + ";";
            selectNameStat = conn.prepareStatement(selectstatment);
            indexer = 1;
            for (Integer i : indexes) {
                selectNameStat.setInt(indexer++, i);
            }
            rs = selectNameStat.executeQuery();
            while (rs.next()) {
                String protI = rs.getString(2).trim();
                String protII = rs.getString(3).trim();
                if (!protI.contains(";")) {
                    protI = protI + ";";
                }
                if (!protII.contains(";")) {
                    protII = protII + ";";
                }
                edges.add(new String[]{protI,protII});
            }
//            System.out.println(" ------------------------------------------");
//            System.out.println(" ----------------acc list--------------------------");
//            System.out.println(proteinAcc);
//            System.out.println(" ------------------------------------------");
//            int counter = 1;
//            for (String[] strArr : edges) {
//                for (String str : strArr) {
//                    System.out.print(" - " + str + "-");
//                }
//                System.out.println();
//                System.out.println("****************************" + "****************************");
//            }
//            System.out.println(" ------------------------------------------");
            return edges;

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            dbEnabled = false;
            System.out.println(e.getLocalizedMessage());
        }
        return edges;
    }

    /**
     * Store and retrieve dataset details index to share in link
     *
     * @param dsDetails encoded dataset details to store in database
     * @returndataset details public key
     */
    public int insertDatasetSharingLink(String dsDetails) {
        /**
         * select dsDetails
         */
        try {
            if (!dbEnabled) {
                return -1;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            /**
             * if nothing available insert and get the index
             */
            String insertstatment = "INSERT INTO `datasetsharing` (`ds_details`) VALUES (?);";
            PreparedStatement preparedStatment = conn.prepareStatement(insertstatment, Statement.RETURN_GENERATED_KEYS);
            preparedStatment.setString(1, dsDetails);
            preparedStatment.executeUpdate();
            ResultSet rs = preparedStatment.getGeneratedKeys();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            String message = e.getLocalizedMessage();
            return (getDatasetSharingLink(dsDetails));
        }
        return -1;
    }

    /**
     * Retrieve dataset index to share in link
     *
     * @param dsDetails encoded dataset details to store in database
     * @return dataset public key
     */
    private int getDatasetSharingLink(String dsDetails) {
        try {
            if (!dbEnabled) {
                return -1;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            String selectstatment = "SELECT * FROM `datasetsharing` where `ds_details` = (?);";
            PreparedStatement preparedStatment = conn.prepareStatement(selectstatment);
            preparedStatment.setString(1, dsDetails);
            ResultSet rs = preparedStatment.executeQuery();
            while (rs.next()) {
                return rs.getInt("ds_index");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
            dbEnabled = false;
        }
        return -1;
    }

    /**
     * Retrieve dataset details from index to share in link
     *
     * @param dsKey dataset public key
     * @return encoded dataset details to visualise
     */
    public String getDatasetSharingLink(int dsKey) {
        try {
            if (!dbEnabled) {
                return null;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
            }
            String selectstatment = "SELECT * FROM `datasetsharing` WHERE `ds_index` = ?";
            PreparedStatement preparedStatment = conn.prepareStatement(selectstatment);
            preparedStatment.setInt(1, dsKey);
            ResultSet rs = preparedStatment.executeQuery();
            while (rs.next()) {
                return rs.getString("ds_details");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
            dbEnabled = false;
        }
        return null;
    }
}
