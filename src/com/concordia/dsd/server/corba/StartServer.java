package com.concordia.dsd.server.corba;

import CenterServerApp.Center;
import CenterServerApp.CenterHelper;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.utils.ConfigManager;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class StartServer {
    //    public static List<String> locationList = new ArrayList<>();
    public static String[][] hostPortArray;
    static ORB[] orb;

    public static void main(String args[]) {
        hostPortArray = ConfigManager.getInstance().getHostPortArray();
        String[] hostPortInfo = new String[4];

        orb = new ORB[hostPortArray.length];
        for (int i = 0; i < hostPortArray.length; i++) {
            for (int j = 0; j < 4; j++) {
                hostPortInfo[j] = hostPortArray[i][j + 1];
            }
            orb[i] = ORB.init(hostPortInfo, null);
            createServerBinding(hostPortArray[i][0], i);
        }

        for (; ; ) {
            for (int i = 0; i < hostPortArray.length; i++) {
                orb[i].run();
            }
        }
        // wait for invocations from clients
    }

    /**
     * Create server
     * @param centerName
     * @param serverNumber
     */
    public static void createServerBinding(String centerName, int serverNumber, int port) {
        try {
            // create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager

            POA rootpoa = POAHelper.narrow(orb[serverNumber].resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            CorbaCenterServerImpl centerServer = new CorbaCenterServerImpl(Location.valueOf(centerName), port);
            centerServer.setORB(orb[serverNumber]);
            ServerManager.getInstance().addServer(Location.valueOf(centerName),port ,centerServer.getCenterServerCenterImpl());

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(centerServer);
            Center href = CenterHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb[serverNumber].resolve_initial_references("NameService");
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