package org.mizhou.landd;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class LANAddressTool {

    private static final String OS_NAME
            = System.getProperties().getProperty("os.name").toUpperCase();

    public static InetAddress getLANAddressOnWindows() {
        InterfaceAddress iaddr = getLANInterfaceAddressOnWindows();
        return iaddr != null ? iaddr.getAddress() : null;
    }

    public static InetAddress getLANBroadcastAddressOnWindows() {
        InterfaceAddress iaddr = getLANInterfaceAddressOnWindows();
        return iaddr != null ? iaddr.getBroadcast() : null;
    }

    public static InterfaceAddress getLANInterfaceAddressOnWindows() {
        if (!OS_NAME.startsWith("WIN")) {
            return null;
        }
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                List<InterfaceAddress> iaddrs = nif.getInterfaceAddresses();
                for (InterfaceAddress iaddr : iaddrs) {
                    InetAddress addr = iaddr.getAddress();
                    if (!(addr instanceof Inet4Address)) {
                        continue;
                    }
                    if (nif.getName().startsWith("wlan")) {
                        return iaddr;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

}
