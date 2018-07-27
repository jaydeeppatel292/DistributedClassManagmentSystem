package com.concordia.dsd.server.corba;

import CenterServerApp.Center;
import CenterServerApp.CenterHelper;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.FrontEndServer;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.utils.ConfigManager;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;

public class StartServer {
    //    public static List<String> locationList = new ArrayList<>();
    public static String[][] hostPortArray;
    static ORB orb;

    public static void main(String args[]) {
        hostPortArray = ConfigManager.getInstance().getHostPortArray();

        for (int i = 0; i < hostPortArray.length; i++) {
            if(hostPortArray[i][0].equals("FE")) {
                String[] hostPortInfo = new String[4];
                for (int j = 0; j < 4; j++) {
                    hostPortInfo[j] = hostPortArray[i][j + 1];
                }
                orb = ORB.init(hostPortInfo, null);
                createServerBinding(hostPortArray[i][0], Integer.parseInt(hostPortArray[i][2]), i);
            }
            else{
                startUDPServer(hostPortArray[i][0], Integer.parseInt(hostPortArray[i][2]));
            }
        }

        orb.run();
        // wait for invocations from clients
    }

    private static void startUDPServer(String centerName,int port) {
        try {
            CenterServerImpl centerServer = new CenterServerImpl(Location.valueOf(centerName),port);
            ServerManager.getInstance().addServer(Location.valueOf(centerName),port,centerServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create server
     * @param centerName
     * @param serverNumber
     */
    public static void createServerBinding(String centerName,int port, int serverNumber) {
        try {
            // create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            FrontEndServer centerServer = new FrontEndServer(Location.valueOf(centerName));
            centerServer.setORB(orb);
            ServerManager.getInstance().setFrontEndServer(centerServer);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(centerServer);
            Center href = CenterHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name(centerName);
            ncRef.rebind(path, href);
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("Center Server ready and waiting ...");
        // wait for invocations from clients

    }
}