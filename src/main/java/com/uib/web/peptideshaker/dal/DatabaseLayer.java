package com.uib.web.peptideshaker.dal;

import com.vaadin.server.VaadinSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import static junit.framework.Assert.assertEquals;

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
    private String servertimezone = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

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
                conn = DriverManager.getConnection(dbURL + dbName + servertimezone, dbUserName, dbPassword);
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
            System.out.println("at Error at database connection 117: " + ex);
            return csf_pr_acc_list;
        }
        //check file on csfpr
        String csfprLink = VaadinSession.getCurrent().getAttribute("csfprLink") + "";
        String updateFileName = "csf_pr_available_prot_accs.txt";
        try {
            URL downloadableFile = new URL(csfprLink + updateFileName);
            if (testURL(downloadableFile)) {
                URLConnection urlConn = downloadableFile.openConnection();

                urlConn.setConnectTimeout(30000);
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
            } else {
                dbEnabled = false;
            }
        } catch (MalformedURLException ex) {
            dbEnabled = false;
            System.out.println("at Error at database connection 176: " + ex);
            return csf_pr_acc_list;
        } catch (IOException | SQLException ex) {
            dbEnabled = false;
            System.out.println("at Error at database connection 180: " + ex);
            return csf_pr_acc_list;
        } catch (Exception ex) {
            dbEnabled = false;
            System.out.println("at Error at database connection 185: " + ex);
            return csf_pr_acc_list;
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
                conn = DriverManager.getConnection(dbURL + dbName + servertimezone, dbUserName, dbPassword);
            }

            for (String acc : proteinAcc) {
                if (acc.trim().equalsIgnoreCase("")) {
                    continue;
                }
                String selectstatment = "SELECT `edge_index` FROM `accessionindex`  where `uniprot_acc`=? ;";
                PreparedStatement selectNameStat = conn.prepareStatement(selectstatment);
                selectNameStat.setString(1, acc);
                ResultSet rs = selectNameStat.executeQuery();
                Set<Integer> indexes = new HashSet<>();
                while (rs.next()) {
                    indexes.add(rs.getInt("edge_index"));
                }
                selectNameStat.close();
                rs.close();
                List<Integer> list = new ArrayList<>(indexes);
                int step = Math.min(15, list.size() - 1);
                int start = 0;
                while (true) {
                    Set<Integer> subSet = new LinkedHashSet<>(list.subList(start, step));
                    start = step + 1;
                    if (start == list.size()) {
                        break;
                    }
                    step = Math.min(step + 15, list.size() - 1);
                    selectstatment = "SELECT * FROM `edges`  where ";
                    for (Integer i : subSet) {
                        selectstatment = selectstatment + "`edge_index`=? or ";
                    }
                    selectstatment = selectstatment.substring(0, selectstatment.length() - 4) + ";";
                    selectNameStat = conn.prepareStatement(selectstatment);
                    int indexer = 1;
                    for (Integer i : subSet) {
                        selectNameStat.setInt(indexer++, i);
                    }
                    rs = selectNameStat.executeQuery();
                    while (rs.next()) {
                        String protI = rs.getString(2).trim();
                        String protII = rs.getString(3).trim();
                        edges.add(new String[]{protI, protII});
                    }

                }

            }
            edges.stream().map((arr) -> {
                if (!arr[0].contains(";")) {
                    arr[0] = arr[0] + ";";
                }
                return arr;
            }).filter((arr) -> (!arr[1].contains(";"))).forEachOrdered((arr) -> {
                arr[1] = arr[1] + ";";
            });
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
     * @param dsUniqueKey dataset unique key
     * @returndataset details public key
     */
    public int insertDatasetSharingLink(String dsDetails,String dsUniqueKey) {
        /**
         * select dsDetails
         */
        try {
            if (!dbEnabled) {
                return -1;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                conn = DriverManager.getConnection(dbURL + dbName + servertimezone, dbUserName, dbPassword);
            }
            /**
             * if nothing available insert and get the index
             */
            
            String insertstatment = "INSERT INTO `datasetsharing` (`ds_details`, `ds_name`) VALUES (?,?);";
            PreparedStatement preparedStatment = conn.prepareStatement(insertstatment, Statement.RETURN_GENERATED_KEYS);
            preparedStatment.setString(1, dsDetails);
             preparedStatment.setString(2, dsUniqueKey);
            preparedStatment.executeUpdate();
            ResultSet rs = preparedStatment.getGeneratedKeys();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            String message = e.getLocalizedMessage();
            return (getDatasetSharingLink(dsDetails));
        }
         System.out.println("at err else happend we return -1: ");
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
                conn = DriverManager.getConnection(dbURL + dbName + servertimezone, dbUserName, dbPassword);
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
                conn = DriverManager.getConnection(dbURL + dbName + servertimezone, dbUserName, dbPassword);
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

    private boolean testURL(URL url) {

        try {
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(10000);
            urlConn.connect();

            assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            return false;
        }
        return true;
    }
}
