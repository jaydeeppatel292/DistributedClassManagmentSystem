package com.concordia.dsd.client.corba;

import CenterServerApp.Center;
import CenterServerApp.CenterHelper;
import com.concordia.dsd.utils.ConfigManager;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class ClientManager {
    String[][] hostPortArray;
    Center centerobj;
    private static ClientManager clientManager;


    static ClientManager getInstace(){
        if(clientManager == null){
            clientManager = new ClientManager();
        }
        return clientManager;
    }

    private ClientManager() {
        hostPortArray = ConfigManager.getInstance().getHostPortArray();
    }

    public String[][] getHostPortArray() {
        return hostPortArray;
    }

    public Center getCenterobj() {
        return centerobj;
    }

    public boolean validateManager(String managerId) {
        if (managerId.length() != 7) {
            System.out.println("Invalid Manager Id length"); //log needs to be put
            return false;
        }

        for (int i = 0; i < hostPortArray.length; i++) {
            if (managerId.substring(0, 3).equalsIgnoreCase(hostPortArray[i][0])) {
                createConnection(hostPortArray, managerId.substring(0, 3));
                return true;
            }
        }
        System.out.println("Invalid Manager Id location"); //log needs to be put
        return false;
    }

    public  void createConnection(String[][] hostPortArray, String serverLoc) {
        String[] hostPortInfo = new String[4];
        for (int j = 0; j < 4; j++) {
            hostPortInfo[j] = hostPortArray[1][j + 1];
        }
        ORB orb = ORB.init(hostPortInfo, null);
        org.omg.CORBA.Object objRef = null;

        try {
            objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            centerobj = (Center) CenterHelper.narrow(ncRef.resolve_str(serverLoc));
        } catch (Exception e) {
            System.out.println("Issue in creating connection");
            e.printStackTrace();
        }
    }

}
