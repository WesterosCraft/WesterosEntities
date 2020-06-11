package com.westeroscraft.westerosdirewolves.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class DirewolfStorage {
    Gson gson;
    private FileReader readLocation;
    private FileWriter writeLocation;
    private File location;
    private Logger logger;

    /*
    Should be pretty self-explanatory. We pass a file in to represent a location to write direwolf json to.
     */
    public DirewolfStorage(File location, Logger logger) {
        this.logger = logger;
        this.location = location;
        gson = new Gson();
    }

    /*
    This method is called by CommandWCDirewolf when parameters have been provided. It overwrites any existing
    direwolf data objects in JSON with the newly provided DirewolfData.
     */
    public void writeToJson(DirewolfData data) {
        // pull HashMap from json
        Type type = new TypeToken<HashMap<UUID, DirewolfData>>(){}.getType();
        HashMap<UUID, DirewolfData> direwolfDataHashMap = gson.fromJson(readLocation, type);
        if(direwolfDataHashMap == null) {
            direwolfDataHashMap = new HashMap<UUID, DirewolfData>();
        }
        // remove an existing DirewolfData, if it exists
        if (direwolfDataHashMap.containsKey(data.ownerUUID)) {
            direwolfDataHashMap.remove(data.ownerUUID);
        }
        // stick the new data in
        direwolfDataHashMap.put(data.ownerUUID, data);
        // this is supposed to clear the file of contents, so we can rewrite the Hashmap into it
        try {
            PrintWriter pw = new PrintWriter(location);
            pw.close();
            // also reset the fileWriter
            writeLocation = new FileWriter(location);
        } catch (IOException e) {
            // uh oh
        }
        logger.log(Level.INFO, "Direwolf HashMap size: " + direwolfDataHashMap.size());
        gson.toJson(direwolfDataHashMap, writeLocation);
        try {
            writeLocation.flush();
            writeLocation.close();
            logger.log(Level.INFO, "Wrote direwolf HashMap to: " + location.getPath());
        } catch (IOException e) {
            logger.log(Level.ERROR, "IOException while writing direwolf HashMap to file.");
        }
    }

    /*
    When no parameters for your wolf have been provided to CommandWCDirewolf, this is called.
    It attempts to read DirewolfData from JSON and return it.
     */
    public DirewolfData readFromJson(UUID ownerUUID) {
        // reset the FileReader
        try {
            readLocation = new FileReader(location);
        } catch (Exception e) {
            // bruh
        }
        // this TypeToken business makes my head hurt
        Type type = new TypeToken<HashMap<UUID, DirewolfData>>(){}.getType();
        HashMap<UUID, DirewolfData> direwolfDataMap = gson.fromJson(readLocation, type);
        logger.log(Level.INFO, "Attempted to read HashMap from json. Length: " + direwolfDataMap.size());
        return direwolfDataMap.get(ownerUUID);
    }
}

/*
if params given:
- pull hashmap from file
- add to the hashmap
- clear the file
- write the hashmap to the file

if no params given:
- pull hashmap from file
- check if the hashmap has the wolf data
- if there is wolf data, send it back
 */