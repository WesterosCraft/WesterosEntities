package com.westeroscraft.westerosentities.persistence;

import net.minecraftforge.common.config.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

/*
Server-side class for writing and reading player-bound entity data
Requires a table set up like so:
CREATE TABLE playerboundentities (PlayerUUID VARCHAR(36), DirewolfColor INT, DirewolfName TEXT, DirewolfTimeCreated TIMESTAMP, CONSTRAINT PRIMARY KEY (PlayerUUID));
 */
public class PlayerEntitiesDatabaseHandler {

    // logger for debugging
    Logger logger;

    // basic information about the DB & where we will look for values
    final String DB_NAME;
    final String TABLE_NAME;
    final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    final String DB_URL; // jdbc:mariadb://localhost/dbName

    // credentials for the DB. This profile should only have access to the playerboundentities table
    final String USER;
    final String PASS;

    public PlayerEntitiesDatabaseHandler(
            Logger logger,
            String dbName,
            String dbTableName,
            String dbURL,
            String dbUser,
            String dbPass
    ) {
        this.logger = logger;
        DB_NAME = dbName;
        TABLE_NAME = dbTableName;
        DB_URL = dbURL;
        USER = dbUser;
        PASS = dbPass;
    }

    /*
    This method will create a new row for a player if they are not already in the database
    Otherwise, it will simply update their direwolf data
    Returns true if successful, returns false otherwise
     */
    public boolean storeDirewolfData(DirewolfData direwolfDataIn) {
        // declare statement and connection variables for later
        PreparedStatement dbStatement = null;
        Connection dbConnection = null;
        // begin the process
        try {
            // load the JDBC driver
            Class.forName(JDBC_DRIVER);
            // connect to the database
            dbConnection = DriverManager.getConnection(DB_URL, USER, PASS);

            // create the SQL query
            // we attempt to insert the direwolf data into the table. if we get a duplicate key error, we update instead
            String sql = "INSERT INTO " + TABLE_NAME + " (PlayerUUID, DirewolfColor, DirewolfName, DirewolfTimeCreated) SELECT ?, ?, ?, ?" +
                    " ON DUPLICATE KEY UPDATE PlayerUUID = VALUES(PlayerUUID), DirewolfColor = VALUES(DirewolfColor), DirewolfName = VALUES(DirewolfName), DirewolfTimeCreated = VALUES(DirewolfTimeCreated)";
            // get a sql date
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(direwolfDataIn.dateCreated.getTime());
            // create the SQL statement
            dbStatement = dbConnection.prepareStatement(sql);
            dbStatement.setString(1, direwolfDataIn.ownerUUID.toString());
            dbStatement.setInt(2, direwolfDataIn.coat);
            dbStatement.setString(3, direwolfDataIn.name);
            dbStatement.setTimestamp(4, sqlTimestamp);

            // execute the statement
            dbStatement.executeUpdate();

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
            String sql = "SELECT DirewolfColor, DirewolfName, DirewolfTimeCreated FROM " + TABLE_NAME + " WHERE PlayerUUID='" + playerUUIDIn.toString() + "'";

            // get results
            dbResultSet = dbStatement.executeQuery(sql);
            // parse data
            int coat = 404;
            String name = "Something went wrong!";
            Date timeCreated = null;
            while(dbResultSet.next()) {
                coat = dbResultSet.getInt("DirewolfColor");
                name = dbResultSet.getString("DirewolfName");
                timeCreated = new Date(dbResultSet.getTimestamp("DirewolfTimeCreated").getTime());
            }
            // construct DirewolfData object
            DirewolfData returnDirewolfData = new DirewolfData(playerUUIDIn, coat, name, timeCreated);
            // set it to null if we have empty data for some reason
            if (coat == 404 && name.equals("Something went wrong!") && timeCreated == null) {
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
