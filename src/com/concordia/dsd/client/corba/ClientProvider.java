package com.concordia.dsd.client.corba;

import CenterServerApp.Center;
import CenterServerApp.CenterHelper;
import com.concordia.dsd.utils.ConfigManager;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 * Singleton ClientProvider
 */
public class ClientProvider {
    String[][] hostPortArray;
    Center centerobj;
    private static ClientProvider clientProvider;


    static ClientProvider getInstace(){
        if(clientProvider == null){
            clientProvider = new ClientProvider();
        }
        return clientProvider;
    }

    private ClientProvider() {
        hostPortArray = ConfigManager.getInstance().getHostPortArray();
    }

    public String[][] getHostPortArray() {
        return hostPortArray;
    }

    public Center getCenterobj() {
        return centerobj;
    }

    /**
     * Validate Managerid by length and host
     * @param managerId
     * @return
     */
    public boolean validateManager(String managerId) {
        if (managerId.length() != 7) {
            System.out.println("Invalid Manager Id length"); //log needs to be put
            return false;
        }

        for (int i = 0; i < hostPortArray.length; i++) {
            if (managerId.substring(0, 3).equalsIgnoreCase(hostPortArray[i][0])) {
                createConnection(hostPortArray, "FE");
                return true;
            }
        }
        System.out.println("Invalid Manager Id location"); //log needs to be put
        return false;
    }

    /**
     * Create Connection to given server
     * @param hostPortArray
     * @param serverLoc
     */
    public  void createConnection(String[][] hostPortArray, String serverLoc) {
        String[] hostPortInfo = new String[4];
        for(int i=0;i<hostPortArray.length;i++){
            if(hostPortArray[i][0].equals(serverLoc)) {
                for (int j = 0; j < 4; j++) {
                    hostPortInfo[j] = hostPortArray[i][j + 1];
                }
                break;
            }
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
