package com.concordia.dsd.server.webservice;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.utils.ConfigManager;

import javax.xml.ws.Endpoint;
import java.io.IOException;

public class StartServer {

    public static void main(String args[]) {
        startAllServer();
    }

    /**
     * Start all Server
     */
    public static void startAllServer() {
        String[][] hostPortArray = ConfigManager.getInstance().getServersInfo();
        for (int i = 0; i < hostPortArray.length; i++) {
            Endpoint endpoint = null;
            try {
                WSCenterServerImpl wsCenterServer = new WSCenterServerImpl(Location.valueOf(hostPortArray[i][0]));
                endpoint = Endpoint.publish("http://" + hostPortArray[i][2] + ":" + hostPortArray[i][1] + "/" + hostPortArray[i][0],wsCenterServer);
                ServerManager.getInstance().addServer(Location.valueOf(hostPortArray[i][0]),Integer.parseInt(hostPortArray[i][1]), wsCenterServer.getCenterServerCenterImpl());
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception ex){
                System.out.println("Server is already running or Something went wrong please try again!!");
            }
            if(endpoint.isPublished()){
                System.out.println(hostPortArray[i][0]+" server started");
            }
        }
    }
}
