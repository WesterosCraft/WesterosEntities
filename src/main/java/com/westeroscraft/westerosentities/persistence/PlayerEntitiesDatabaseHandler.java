package com.westeroscraft.westerosentities.persistence;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

/*
Server-side class for writing and reading player-bound entity data
Requires a table set up like so:
CREATE TABLE playerboundentities (PlayerUUID VARCHAR(36), DirewolfColor INT, DirewolfName TEXT, CONSTRAINT PRIMARY KEY (PlayerUUID));
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

    public PlayerEntitiesDatabaseHandler(Logger logger) {
        this.logger = logger;
    }

    /*
    This method will create a new row for a player if they are not already in the database
    Otherwise, it will simply update their direwolf data
    Returns true if successful, returns false otherwise
     */
    public boolean storeDirewolfData(DirewolfData direwolfDataIn) {
        // declare statement and connection variables for later
        Statement dbStatement = null;
        Connection dbConnection = null;
        // begin the process
        try {
            // load the JDBC driver
            Class.forName(JDBC_DRIVER);
            // connect to the database
            dbConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            // create the SQL statement
            dbStatement = dbConnection.createStatement();

            // create the SQL query
            // we attempt to insert the direwolf data into the table. if we get a duplicate key error, we update instead
            String sql = "INSERT INTO " + TABLE_NAME + " (PlayerUUID, DirewolfColor, DirewolfName) SELECT '"
                    + direwolfDataIn.ownerUUID.toString()
                    + "',"
                    + direwolfDataIn.coat
                    + ", '"
                    + direwolfDataIn.name
                    + "' ON DUPLICATE KEY UPDATE PlayerUUID = VALUES(PlayerUUID), DirewolfColor = VALUES(DirewolfColor), DirewolfName = VALUES(DirewolfName)";
            // attempt to execute our SQL
            dbStatement.execute(sql);

            // clean up to prevent resource leaks
            dbStatement.close();
            dbConnection.close();

            // return true if everything went alright
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL error while storing direwolf data");
            e.printStackTrace();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error loading JDBC driver while storing direwolf data");
            e.printStackTrace();
        } finally {
            try {
                if (dbStatement != null) dbStatement.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "SQL error while closing statement. This is very bad");
                e.printStackTrace();
            }
            try {
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "SQL error while closing connection. This is very bad");
            }
        }
        // we should only get here if something went wrong
        return false;
    }

    /*
    This method attempts to build a DirewolfData object from SQL
    It will return a null object if nothing is found
     */
    public DirewolfData getDirewolfData(UUID playerUUIDIn) {
        // declare variables for later
        Connection dbConnection = null;
        Statement dbStatement = null;
        ResultSet dbResultSet = null;
        // begin the process
        try {
            // load the JDBC driver
            Class.forName(JDBC_DRIVER);
            // connect to the database
            dbConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            // create a SQL statement
            dbStatement = dbConnection.createStatement();

            // create the SQL query
            // pretty self-explanatory - we grab direwolf data from the db using the UUID as a key
            // I store UUIDs as VARCHARs in MariaDB, thus we use toString()
            String sql = "SELECT DirewolfColor, DirewolfName FROM " + TABLE_NAME + " WHERE PlayerUUID='" + playerUUIDIn.toString() + "'";

            // get results
            dbResultSet = dbStatement.executeQuery(sql);
            // parse data
            int coat = 404;
            String name = "Something went wrong!";
            while(dbResultSet.next()) {
                coat = dbResultSet.getInt("DirewolfColor");
                name = dbResultSet.getString("DirewolfName");
            }
            // construct DirewolfData object
            DirewolfData returnDirewolfData = new DirewolfData(playerUUIDIn, coat, name);
            // set it to null if we have empty data for some reason
            if (coat == 404 && name.equals("Something went wrong!")) {
                returnDirewolfData = null;
            }

            // clean up after ourselves
            dbResultSet.close();
            dbStatement.close();
            dbConnection.close();

            return returnDirewolfData;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL error while getting direwolf data");
            e.printStackTrace();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error while loading JDBC driver while getting direwolf data");
            e.printStackTrace();
        } finally {
            try {
                if (dbResultSet != null) dbResultSet.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Error while closing SQL result set while getting direwolf data. This is very bad");
                e.printStackTrace();
            }
            try {
                if (dbStatement != null) dbStatement.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Error while closing SQL statement while getting direwolf data. This is very bad");
                e.printStackTrace();
            }
            try {
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Error while closing SQL connection while getting direwolf data. This is very bad");
                e.printStackTrace();
            }
        }
        // if we're here, something went very wrong
        return null;
    }
}
