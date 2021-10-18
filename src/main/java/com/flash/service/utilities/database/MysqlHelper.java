package com.flash.service.utilities.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash.service.utilities.ReportHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class MysqlHelper implements DbHelper {
    ReportHelper reportHelper;
    private static Optional<Connection> connection = Optional.empty();
    private static final Logger logger = LoggerFactory.getLogger(MysqlHelper.class);
    private static  Map<String,MysqlHelper> instanceMap = new HashMap<>();
    public static String mysqlUri;
    public static String username;
    public static String password;
    private String databaseName;


    private MysqlHelper(String databaseName, ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
        this.databaseName=databaseName;
        MysqlHelper.connection = getConnection(mysqlUri,databaseName,username,password);
    }

    public static MysqlHelper getInstance( String databaseName, ReportHelper reportHelper)
    {
        if (!instanceMap.containsKey(databaseName) )
            instanceMap.put(databaseName, new MysqlHelper( databaseName, reportHelper));
        return instanceMap.get(databaseName);
    }

    public static Optional<Connection> getConnection(String mysqlUri, String databaseName,String userName,
                                                     String password) {
        if (connection.isEmpty()) {
            String url = mysqlUri+databaseName+"?useSSL=false";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = Optional.ofNullable(
                        DriverManager.getConnection(url, userName, password));
            } catch (SQLException ex) {
                logger.error(ex.getSQLState());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


    public <T> T getDataAsListOfMap(String sql) {
        try (Statement statement = connection.get().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSetToListOfMap(resultSet);
        } catch (SQLException ex) {
            logger.error(ex.getSQLState());
        }
        return null;

    }

    private <T> T resultSetToListOfMap(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i)
                row.put(md.getColumnName(i), rs.getObject(i));
            rows.add(row);
        }
        return (T) rows;
    }

    private <T> List<T> resultSetToListOfObject(ResultSet rs, Class<T> classType) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<T> rows = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i)
                row.put(md.getColumnName(i), rs.getObject(i));
            rows.add(mapper.convertValue(row, classType));
        }
        return rows;
    }

    public <T> List<T> getDataAsListOfObject(String sql, Class<T> tClass) {
        try (Statement statement = connection.get().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSetToListOfObject(resultSet, tClass);
        } catch (SQLException ex) {
            logger.error(ex.getSQLState());
        }
        return null;

    }

    public int update(String sql) {
        int affectedrows = 0;
        try (PreparedStatement pstmt = connection.get().prepareStatement(sql)) {
            affectedrows = pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return affectedrows;
    }

}
