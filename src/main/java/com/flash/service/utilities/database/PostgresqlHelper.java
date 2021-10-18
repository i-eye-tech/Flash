package com.flash.service.utilities.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash.service.utilities.ReportHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class PostgresqlHelper implements DbHelper {
    ReportHelper reportHelper;
    private Optional<Connection> connection = Optional.empty();
    private static final Logger logger = LoggerFactory.getLogger(PostgresqlHelper.class);
    private static Map<String, PostgresqlHelper> instanceMap = new HashMap<>();
    public static String postgresUri;//="jdbc:postgresql://localhost:5432/";
    public static String username;//="postgres";
    public static String password;//="postgres";
    private String databaseName;


    private PostgresqlHelper(String databaseName, ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
        this.databaseName = databaseName;
        this.connection = getConnection(postgresUri, databaseName, username, password);
    }

    public static PostgresqlHelper getInstance(String databaseName, ReportHelper reportHelper) {
        try {
            if (!instanceMap.containsKey(databaseName) || instanceMap.get(databaseName).connection.isEmpty() || instanceMap.get(databaseName).connection.get().isClosed()) {
                instanceMap.remove(databaseName);
            }
        } catch (SQLException throwables) {
            logger.error("Exception verifying Postgre sql connection state");
        }


        if (!instanceMap.containsKey(databaseName))
            instanceMap.put(databaseName, new PostgresqlHelper(databaseName, reportHelper));

        return instanceMap.get(databaseName);
    }

    public Optional<Connection> getConnection(String postgresUri, String databaseName, String userName, String password) {
        if (connection.isEmpty()) {
            String url = postgresUri + databaseName + "?useSSL=false";
            try {
                Class.forName("org.postgresql.Driver");
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
        } catch (Exception throwables) {
            logger.error(throwables.getMessage());
        }
        return affectedrows;
    }


}
