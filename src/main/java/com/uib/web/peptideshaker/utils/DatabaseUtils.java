package com.uib.web.peptideshaker.utils;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.server.VaadinSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
