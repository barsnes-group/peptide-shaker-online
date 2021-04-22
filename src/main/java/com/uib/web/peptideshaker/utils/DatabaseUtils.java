package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.VaadinSession;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides utilities for accessing the database
 *
 * @author Yehia Mokhtar Farag
 */
public class DatabaseUtils {

    private Connection conn = null;
    private boolean dbEnabled = true;
    private final AppManagmentBean appManagmentBean;

    public DatabaseUtils() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        try {
            Class.forName(appManagmentBean.getAppConfig().getDbDriver()).newInstance();
            conn = DriverManager.getConnection(appManagmentBean.getAppConfig().getDbURL() + appManagmentBean.getAppConfig().getDbName() + CONSTANT.SERVER_TIMEZONE, appManagmentBean.getAppConfig().getDbUserName(), appManagmentBean.getAppConfig().getDbPassword());

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("at Error : database util " + ex);
            dbEnabled = false;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getDatasetSharingKey(String datasetKey) {
        try {
            if (!dbEnabled) {
                return -1;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(appManagmentBean.getAppConfig().getDbDriver()).newInstance();
                conn = DriverManager.getConnection(appManagmentBean.getAppConfig().getDbURL() + appManagmentBean.getAppConfig().getDbName() + CONSTANT.SERVER_TIMEZONE, appManagmentBean.getAppConfig().getDbUserName(), appManagmentBean.getAppConfig().getDbPassword());

            }
            String selectstatment = "SELECT * FROM `datasetsharing` where `ds_key` = (?);";
            PreparedStatement preparedStatment = conn.prepareStatement(selectstatment);
            preparedStatment.setString(1, datasetKey);
            ResultSet rs = preparedStatment.executeQuery();
            while (rs.next()) {
                return rs.getInt("ds_index");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println("at Error : database util " + ex);
        }
        return -1;
    }

    public int insertDatasetSharingData(String datasetKey, String datasetDetails) {
        try {
            if (!dbEnabled) {
                return -1;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(appManagmentBean.getAppConfig().getDbDriver()).newInstance();
                conn = DriverManager.getConnection(appManagmentBean.getAppConfig().getDbURL() + appManagmentBean.getAppConfig().getDbName() + CONSTANT.SERVER_TIMEZONE, appManagmentBean.getAppConfig().getDbUserName(), appManagmentBean.getAppConfig().getDbPassword());

            }
            String insertstatment = "INSERT INTO `datasetsharing` (`ds_details`, `ds_key`) VALUES (?,?);";
            PreparedStatement preparedStatment = conn.prepareStatement(insertstatment, Statement.RETURN_GENERATED_KEYS);
            preparedStatment.setString(1, datasetDetails);
            preparedStatment.setString(2, datasetKey);
            preparedStatment.executeUpdate();
            ResultSet rs = preparedStatment.getGeneratedKeys();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println("at Error : database util " + ex);
        }
        return -1;
    }

    /**
     * Get list of accessions available on csf-pr in order to allow mapping data
     * to csf-pr.
     *
     * @return set of Uni-prot protein accessions available on csf-pr
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
                Class.forName(appManagmentBean.getAppConfig().getDbDriver()).newInstance();
                conn = DriverManager.getConnection(appManagmentBean.getAppConfig().getDbURL() + appManagmentBean.getAppConfig().getDbName() + CONSTANT.SERVER_TIMEZONE, appManagmentBean.getAppConfig().getDbUserName(), appManagmentBean.getAppConfig().getDbPassword());

            }
            if (conn == null) {
                dbEnabled = false;
                return csf_pr_acc_list;
            }
            String selectStat = "SELECT * FROM  INFORMATION_SCHEMA.TABLES  WHERE `TABLE_SCHEMA` = '" + appManagmentBean.getAppConfig().getDbName() + "';";
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
        String csfprLink = appManagmentBean.getAppConfig().getCsfprLink();
        String updateFileName = "csf_pr_available_prot_accs.txt";
        try {
            if (appManagmentBean.getHttpClientUtil().testURL(csfprLink + updateFileName)) {

                BufferedReader br = appManagmentBean.getHttpClientUtil().streamFile(csfprLink + updateFileName);
                if (br == null) {
                    return csf_pr_acc_list;
                }
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
    public Set<String[]> getPathwayEdges(String[] proteinAcc) {
        Set<String[]> edges = new LinkedHashSet<>();
        try {

            if (!dbEnabled) {
                return edges;
            }
            if (conn == null || conn.isClosed()) {
                Class.forName(appManagmentBean.getAppConfig().getDbDriver()).newInstance();
                conn = DriverManager.getConnection(appManagmentBean.getAppConfig().getDbURL() + appManagmentBean.getAppConfig().getDbName() + CONSTANT.SERVER_TIMEZONE, appManagmentBean.getAppConfig().getDbUserName(), appManagmentBean.getAppConfig().getDbPassword());

            }
            String selectstatment = "SELECT * FROM `accession_json_index`  where ";
            String value = "";
            for (String accssion : proteinAcc) {
                value += " or `uniprot_acc`=?";
            }
            value = value.replaceFirst(" or ", "") + ";";
            PreparedStatement selectNameStat = conn.prepareStatement(selectstatment + value);
            int index = 1;
            for (String accssion : proteinAcc) {
                selectNameStat.setString(index++, accssion);
            }
            ResultSet rs = selectNameStat.executeQuery();
            JsonObject jsonMap = new JsonObject();
            while (rs.next()) {
                jsonMap.put(rs.getString("uniprot_acc"), new JsonArray(rs.getString("index_as_json")));
            }
            selectstatment = "SELECT * FROM `updated_edge`  where ";
            value = "";
            for (String acc : jsonMap.fieldNames()) {
                JsonArray arr = jsonMap.getJsonArray(acc);
                for (int i = 0; i < arr.size(); i++) {
                    value += " or `id`=?";
                }
            }
            value = value.replaceFirst(" or ", "") + ";";
            selectNameStat = conn.prepareStatement(selectstatment + value);
            index = 1;
            for (String acc : jsonMap.fieldNames()) {
                JsonArray arr = jsonMap.getJsonArray(acc);
                for (int i = 0; i < arr.size(); i++) {
                    selectNameStat.setInt(index++, arr.getInteger(i));
                }
            }
            rs = selectNameStat.executeQuery();
            while (rs.next()) {
                String compI = rs.getString("compI").trim();
                String compII = rs.getString("compII").trim();
                edges.add(new String[]{compI, compII});
            }
            return edges;

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            dbEnabled = false;
            System.out.println(e.getLocalizedMessage());
        }
        return edges;
    }
}
