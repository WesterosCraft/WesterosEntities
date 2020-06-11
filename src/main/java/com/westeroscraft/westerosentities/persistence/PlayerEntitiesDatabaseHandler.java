package com.westeroscraft.westerosentities.persistence;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

/*
Server-side class for writing and reading player-bound entity data
 */
public class PlayerEntitiesDatabaseHandler {

    // logger for debugging
    Logger logger;

    // basic information about the DB & where we will look for values
    static final String DB_NAME = "test";
    static final String TABLE_NAME = "playerboundentities";
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost/" + DB_NAME;

    // credentials for the DB. This profile should only have access to the playerboundentities table
    static final String USER = "PlayerBoundEntitiesManipulator";
    static final String PASS = "BARN44";

    private Connection dbConnection;

    public PlayerEntitiesDatabaseHandler(Logger logger) {
        this.logger = logger;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.log(Level.ERROR, "JDBC driver could not be found");
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.log(Level.INFO, "Connected successfully to the Entities database");
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not connect to Entities database");
            e.printStackTrace();
        }
    }

    /*
    This method will create a new row for a player if they are not already in the database
    Otherwise, it will simply update their direwolf data
    Returns true if successful, returns false otherwise
     */
    public boolean storeDirewolfData(DirewolfData direwolfDataIn) {

        // make the SQL statement
        Statement statement = null;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not create a SQL statement");
            return false;
        }

        // create the SQL query
        // we attempt to insert the direwolf data into the table. if we get a duplicate key error, we update instead
        String sql = "INSERT INTO " + TABLE_NAME + " (PlayerUUID, DirewolfColor, DirewolfName) SELECT '"
                + direwolfDataIn.ownerUUID.toString()
                + "',"
                + direwolfDataIn.coat
                + ", '"
                + direwolfDataIn.name
                + "' ON DUPLICATE KEY UPDATE PlayerUUID = VALUES(PlayerUUID), DirewolfColor = VALUES(DirewolfColor), DirewolfName = VALUES(DirewolfName)";
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Error executing SQL statement");
            return false;
        }

        // return true if no exceptions were thrown
        return true;

    }

    /*
    This method attempts to build a DirewolfData object from SQL
    It will return a null object if nothing is found
     */
    public DirewolfData getDirewolfData(UUID playerUUIDIn) {

        // make the SQL statement
        Statement statement = null;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not create a SQL statement");
            return null;
        }

        // create the SQL query
        // pretty self-explanatory - we grab direwolf data from the db using the UUID as a key
        // I store UUIDs as VARCHARs in MariaDB, thus we use toString()
        String sql = "SELECT DirewolfColor, DirewolfName FROM " + TABLE_NAME + " WHERE PlayerUUID='" + playerUUIDIn.toString() + "'";
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            // parse data from the ResultSet
            int coat = 404;
            String name = "Something went wrong!";
            while (resultSet.next()) {
                coat = resultSet.getInt("DirewolfColor");
                name = resultSet.getString("DirewolfName");
            }
            // construct the DirewolfData, return it
            if (coat == 404 && name.equals("Something went wrong!")) {
                return null;
            } else {
                return new DirewolfData(playerUUIDIn, coat, name);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Error while retrieving results from database");
            return null;
        }
    }

    /*
    Closes stuff, cleans up
     */
    public void cleanUp() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            // uh oh
        }
    }
}
