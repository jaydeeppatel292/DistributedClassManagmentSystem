package com.concordia.dsd.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Singleton ConfigManager
 */
public class ConfigManager {

    private static ConfigManager instance;
    private ConfigManager() {
    }
    public static ConfigManager getInstance(){
        if(instance==null){
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Get Host Port Array
     * @return
     */
    public String[][] getHostPortArray() {

        org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader("config/location.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonArr = (JSONArray) jsonObject.get("centers");

        int sizeOfLoc = jsonArr.size();
        String[][] hostPortArray = new String[sizeOfLoc][5];

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = (JSONObject) jsonArr.get(i);
//            locationList.add(obj.get("names").toString());
            hostPortArray[i][0] = obj.get("names").toString();

        }

        JSONArray portArr = (JSONArray) jsonObject.get("port");
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = (JSONObject) jsonArr.get(i);
            hostPortArray[i][1] = "-ORBInitialPort";
            hostPortArray[i][2] = obj.get("port").toString();
            hostPortArray[i][3] = "-ORBInitialHost";
            hostPortArray[i][4] = obj.get("host").toString();
        }
        return hostPortArray;
    }
    /**
     * Get Host Port Array
     * @return
     */
    public String[][] getServersInfo() {
        org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader("config/location.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonArr = (JSONArray) jsonObject.get("centers");
        int sizeOfLoc = jsonArr.size();
        String[][] hostPortArray = new String[sizeOfLoc][3];

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject obj = (JSONObject) jsonArr.get(i);
            hostPortArray[i][0] = obj.get("names").toString();
            hostPortArray[i][1] = obj.get("port").toString();
            hostPortArray[i][2] = obj.get("host").toString();
        }
        return hostPortArray;
    }
}
