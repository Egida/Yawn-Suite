package pers.harry_deng.yawnsuite.beans.items;

public class Host {
    private final String hostIP;
    private final String hostMAC;

    public Host(String hostIP, String hostMAC) {
        this.hostIP = hostIP;
        this.hostMAC = hostMAC;
    }

    public String getHostIP() {
        return hostIP;
    }

    public String getHostMAC() {
        return hostMAC;
    }
}
